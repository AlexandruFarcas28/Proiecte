#!/usr/bin/env python3
"""
Smart Tip Calculator Pro - Backend API (PostgreSQL version)
Updated with better exchange rate sources
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
import psycopg2
from psycopg2.extras import RealDictCursor
import requests
import xml.etree.ElementTree as ET
from datetime import datetime, timedelta
import logging
from functools import wraps
from contextlib import contextmanager
import json

app = Flask(__name__)
CORS(app)
app.config['SECRET_KEY'] = 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx' #enter your secret key

# Logging setup
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# PostgreSQL Config
DB_CONFIG = {
    'host': 'localhost',
    'database': 'tip_calculator',
    'user': 'tip_user',
    'password': '12345',  
    'port': 5432
}

@contextmanager
def get_db():
    """Context manager pentru conexiuni PostgreSQL"""
    conn = psycopg2.connect(**DB_CONFIG)
    try:
        yield conn
        conn.commit()
    except Exception:
        conn.rollback()
        raise
    finally:
        conn.close()

def init_db():
    """Inițializează tabelele în baza de date"""
    with get_db() as conn:
        cursor = conn.cursor()
        
        # Tabel pentru cursuri valutare
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS exchange_rates (
                id SERIAL PRIMARY KEY,
                base_currency VARCHAR(10) NOT NULL,
                target_currency VARCHAR(10) NOT NULL,
                rate DECIMAL(20,6) NOT NULL,
                source VARCHAR(50),
                last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                UNIQUE(base_currency, target_currency)
            )
        ''')
        
        # Tabel pentru istoricul calculelor
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS tip_history (
                id SERIAL PRIMARY KEY,
                user_id VARCHAR(255),
                bill_amount DECIMAL(10,2) NOT NULL,
                tip_percent DECIMAL(5,2) NOT NULL,
                total_amount DECIMAL(10,2) NOT NULL,
                base_currency VARCHAR(10) NOT NULL,
                target_currency VARCHAR(10) NOT NULL,
                split_count INTEGER DEFAULT 1,
                note TEXT,
                exchange_rate DECIMAL(20,6),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        ''')
        
        # Tabel pentru monitorizare API
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS api_usage (
                id SERIAL PRIMARY KEY,
                endpoint VARCHAR(255) NOT NULL,
                user_id VARCHAR(255),
                ip_address VARCHAR(45),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        ''')
        
        # Index pentru performanță
        cursor.execute('''
            CREATE INDEX IF NOT EXISTS idx_exchange_rates_currencies 
            ON exchange_rates(base_currency, target_currency)
        ''')
        
        cursor.execute('''
            CREATE INDEX IF NOT EXISTS idx_tip_history_user 
            ON tip_history(user_id, created_at DESC)
        ''')

def track_usage(endpoint):
    """Decorator pentru tracking usage"""
    def decorator(f):
        @wraps(f)
        def wrapper(*args, **kwargs):
            try:
                with get_db() as conn:
                    cursor = conn.cursor()
                    cursor.execute('''
                        INSERT INTO api_usage (endpoint, ip_address)
                        VALUES (%s, %s)
                    ''', (endpoint, request.remote_addr))
            except Exception as e:
                logger.error(f"Tracking failed: {e}")
            return f(*args, **kwargs)
        return wrapper
    return decorator

class ExchangeRateService:
    def __init__(self):
        self.cache_duration = timedelta(minutes=30)  # Reduced to 30 minutes for more frequent updates

    def get_bnr_rates(self):
        """Preia cursurile de la BNR pentru RON - sursa oficială"""
        try:
            # BNR publică cursul zilnic
            response = requests.get('https://www.bnr.ro/nbrfxrates.xml', timeout=10)
            response.encoding = 'utf-8'
            root = ET.fromstring(response.content)
            
            rates = {}
            # Find the Cube with rates
            for cube in root.findall('.//{http://www.bnr.ro/xsd}Cube'):
                date = cube.get('date')
                if date:  # This is the date cube
                    for rate_elem in cube.findall('.//{http://www.bnr.ro/xsd}Rate'):
                        currency = rate_elem.get('currency')
                        multiplier = int(rate_elem.get('multiplier', '1'))
                        try:
                            value = float(rate_elem.text)
                            rate_ron_per_unit = value / multiplier
                            rates[f'{currency}_RON'] = rate_ron_per_unit  # 1 EUR = 5.03 RON
                            rates[f'RON_{currency}'] = 1 / rate_ron_per_unit  # 1 RON = 0.199 EUR
                        except (ValueError, TypeError):
                            continue
            
            rates['RON_RON'] = 1.0
            logger.info(f"BNR rates loaded: {len(rates)} rates")
            return rates, 'BNR'
        except Exception as e:
            logger.error(f"BNR error: {e}")
            return {}, None

    def get_ecb_rates(self):
        """Preia cursurile de la European Central Bank pentru EUR"""
        try:
            response = requests.get('https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml', timeout=10)
            root = ET.fromstring(response.content)
            
            rates = {}
            # ECB provides rates relative to EUR
            namespaces = {'ns': 'http://www.ecb.int/vocabulary/2002-08-01/eurofxref'}
            
            for cube in root.findall('.//ns:Cube[@currency]', namespaces):
                currency = cube.get('currency')
                rate = float(cube.get('rate'))
                rates[f'EUR_{currency}'] = rate
                rates[f'{currency}_EUR'] = 1 / rate
            
            rates['EUR_EUR'] = 1.0
            logger.info(f"ECB rates loaded: {len(rates)} rates")
            return rates, 'ECB'
        except Exception as e:
            logger.error(f"ECB error: {e}")
            return {}, None

    def get_fixer_rates(self):
        """Preia cursuri de la Fixer.io (free tier) pentru backup"""
        try:
            # Free API key for Fixer.io - you should get your own at https://fixer.io
            API_KEY = 'xxxxxxxxxxxxxxxxxxx'  # Replace with your key
            url = f'http://api.fixer.io/api/latest?access_key={API_KEY}'
            response = requests.get(url, timeout=10)
            data = response.json()
            
            if data.get('success'):
                rates = {}
                base = data['base']  # Usually EUR
                for currency, rate in data['rates'].items():
                    rates[f'{base}_{currency}'] = rate
                    if rate != 0:
                        rates[f'{currency}_{base}'] = 1 / rate
                return rates, 'Fixer.io'
            return {}, None
        except Exception as e:
            logger.error(f"Fixer.io error: {e}")
            return {}, None

    def get_exchangerate_api_rates(self, base='USD'):
        """Preia cursuri de la exchangerate-api.com ca fallback"""
        try:
            url = f'https://api.exchangerate-api.com/v4/latest/{base}'
            response = requests.get(url, timeout=10)
            data = response.json()
            rates = {}
            for currency, rate in data['rates'].items():
                rates[f'{base}_{currency}'] = rate
                if rate != 0:
                    rates[f'{currency}_{base}'] = 1 / rate
            return rates, 'ExchangeRate-API'
        except Exception as e:
            logger.error(f"ExchangeRate-API error: {e}")
            return {}, None

    def update_exchange_rates(self):
        """Actualizează toate cursurile valutare din multiple surse"""
        with get_db() as conn:
            cursor = conn.cursor()
            
            try:
                all_rates = {}
                sources = {}
                
                # Priority order: Official sources first
                # 1. BNR for RON (official Romanian rates)
                bnr_rates, bnr_source = self.get_bnr_rates()
                for key, rate in bnr_rates.items():
                    all_rates[key] = rate
                    sources[key] = bnr_source
                
                # 2. ECB for EUR (official European rates)
                ecb_rates, ecb_source = self.get_ecb_rates()
                for key, rate in ecb_rates.items():
                    if key not in all_rates:  # Don't override BNR rates
                        all_rates[key] = rate
                        sources[key] = ecb_source
                
                # 3. Fallback to free APIs for other currencies
                for base in ['USD', 'EUR', 'GBP']:
                    api_rates, api_source = self.get_exchangerate_api_rates(base)
                    for key, rate in api_rates.items():
                        if key not in all_rates:
                            all_rates[key] = rate
                            sources[key] = api_source

                # Calculate cross rates for RON if needed
                if 'EUR_RON' in all_rates and 'EUR_USD' in all_rates:
                    if 'USD_RON' not in all_rates:
                        all_rates['USD_RON'] = all_rates['EUR_RON'] / all_rates['EUR_USD']
                        all_rates['RON_USD'] = 1 / all_rates['USD_RON']
                        sources['USD_RON'] = 'Calculated'
                        sources['RON_USD'] = 'Calculated'

                # Save all rates to database
                updated_count = 0
                for key, rate in all_rates.items():
                    base, target = key.split('_')
                    source = sources.get(key, 'Unknown')
                    cursor.execute('''
                        INSERT INTO exchange_rates (base_currency, target_currency, rate, source)
                        VALUES (%(base)s, %(target)s, %(rate)s, %(source)s)
                        ON CONFLICT (base_currency, target_currency) 
                        DO UPDATE SET 
                            rate = %(rate)s,
                            source = %(source)s,
                            last_updated = CURRENT_TIMESTAMP
                    ''', {'base': base, 'target': target, 'rate': rate, 'source': source})
                    updated_count += 1

                logger.info(f"Updated {updated_count} exchange rates")
                return True
            except Exception as e:
                logger.error(f"Failed to update rates: {e}")
                return False

    def get_cached_rate(self, base_currency, target_currency):
        """Obține cursul din cache sau actualizează"""
        if base_currency == target_currency:
            return 1.0

        with get_db() as conn:
            cursor = conn.cursor()
            try:
                # Try direct rate first
                cursor.execute('''
                    SELECT rate, last_updated, source FROM exchange_rates
                    WHERE base_currency = %s AND target_currency = %s
                ''', (base_currency, target_currency))
                
                result = cursor.fetchone()
                if result:
                    rate, last_updated, source = result
                    if datetime.now() - last_updated < self.cache_duration:
                        logger.info(f"Using cached rate {base_currency}/{target_currency}: {rate} from {source}")
                        return float(rate)
                
                # Try to calculate cross rate
                rate = self.calculate_cross_rate(base_currency, target_currency)
                if rate:
                    return rate
                
                # If no rate found or expired, trigger update
                logger.info(f"Rate not found or expired for {base_currency}/{target_currency}, updating...")
                self.update_exchange_rates()
                
                # Try again after update
                cursor.execute('''
                    SELECT rate FROM exchange_rates
                    WHERE base_currency = %s AND target_currency = %s
                ''', (base_currency, target_currency))
                
                result = cursor.fetchone()
                if result:
                    return float(result[0])
                
                # Last resort: calculate cross rate
                rate = self.calculate_cross_rate(base_currency, target_currency)
                return rate if rate else 1.0
                
            except Exception as e:
                logger.error(f"Rate lookup error: {e}")
                return None

    def calculate_cross_rate(self, base, target):
        """Calculate cross rate through common currencies"""
        with get_db() as conn:
            cursor = conn.cursor()
            
            # Common currencies to use as intermediaries
            common_currencies = ['USD', 'EUR', 'RON']
            
            for common in common_currencies:
                if common == base or common == target:
                    continue
                    
                try:
                    # Get base to common rate
                    cursor.execute('''
                        SELECT rate FROM exchange_rates
                        WHERE base_currency = %s AND target_currency = %s
                        AND last_updated > %s
                    ''', (base, common, datetime.now() - self.cache_duration))
                    base_to_common = cursor.fetchone()
                    
                    # Get common to target rate
                    cursor.execute('''
                        SELECT rate FROM exchange_rates
                        WHERE base_currency = %s AND target_currency = %s
                        AND last_updated > %s
                    ''', (common, target, datetime.now() - self.cache_duration))
                    common_to_target = cursor.fetchone()
                    
                    if base_to_common and common_to_target:
                        cross_rate = float(base_to_common[0]) * float(common_to_target[0])
                        logger.info(f"Calculated cross rate {base}/{target} via {common}: {cross_rate}")
                        
                        # Save the calculated cross rate
                        cursor.execute('''
                            INSERT INTO exchange_rates (base_currency, target_currency, rate, source)
                            VALUES (%s, %s, %s, %s)
                            ON CONFLICT (base_currency, target_currency) 
                            DO UPDATE SET 
                                rate = %s,
                                source = %s,
                                last_updated = CURRENT_TIMESTAMP
                        ''', (base, target, cross_rate, f'Cross via {common}', 
                              cross_rate, f'Cross via {common}'))
                        conn.commit()
                        
                        return cross_rate
                except Exception as e:
                    logger.error(f"Cross rate calculation error: {e}")
                    continue
            
            return None

# Initialize
exchange_service = ExchangeRateService()

@app.before_request
def startup():
    """Inițializare la pornirea aplicației"""
    init_db()
    exchange_service.update_exchange_rates()

@app.route('/')
def home():
    return jsonify({
        'message': 'Smart Tip Calculator API is running',
        'version': '2.0',
        'database': 'PostgreSQL',
        'features': ['BNR rates', 'ECB rates', 'Cross rates', 'Multi-currency support']
    })

@app.route('/api/exchange-rates')
@track_usage('/api/exchange-rates')
def get_exchange_rates():
    """Obține toate cursurile valutare"""
    try:
        # Force update if requested
        if request.args.get('force_update') == 'true':
            exchange_service.update_exchange_rates()
        
        with get_db() as conn:
            cursor = conn.cursor(cursor_factory=RealDictCursor)
            cursor.execute('''
                SELECT base_currency, target_currency, rate, source, last_updated 
                FROM exchange_rates
                ORDER BY base_currency, target_currency
            ''')
            
            rates = {}
            for row in cursor.fetchall():
                key = f"{row['base_currency']}_{row['target_currency']}"
                rates[key] = {
                    'rate': float(row['rate']),
                    'source': row['source'],
                    'last_updated': row['last_updated'].isoformat()
                }
            
            return jsonify({'success': True, 'rates': rates, 'count': len(rates)})
    except Exception as e:
        logger.error(f"Exchange rates error: {e}")
        return jsonify({'success': False, 'error': str(e)}), 500

@app.route('/api/exchange-rate/<base>/<target>')
@track_usage('/api/exchange-rate')
def get_specific_rate(base, target):
    """Obține un curs specific"""
    base, target = base.upper(), target.upper()
    try:
        rate = exchange_service.get_cached_rate(base, target)
        if rate is None:
            return jsonify({'success': False, 'error': 'Rate not found'}), 404
        
        # Get source info
        with get_db() as conn:
            cursor = conn.cursor()
            cursor.execute('''
                SELECT source, last_updated FROM exchange_rates
                WHERE base_currency = %s AND target_currency = %s
            ''', (base, target))
            result = cursor.fetchone()
            source = result[0] if result else 'Unknown'
            last_updated = result[1] if result else datetime.now()
        
        return jsonify({
            'success': True,
            'base_currency': base,
            'target_currency': target,
            'rate': rate,
            'source': source,
            'last_updated': last_updated.isoformat(),
            'timestamp': datetime.now().isoformat()
        })
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500

@app.route('/api/calculate-tip', methods=['POST'])
@track_usage('/api/calculate-tip')
def calculate_tip():
    """Calculează bacșișul"""
    try:
        data = request.get_json()
        
        # Validare input
        required = ['bill_amount', 'tip_percent']
        for field in required:
            if field not in data:
                return jsonify({'success': False, 'error': f'Missing {field}'}), 400

        # Extrage datele
        bill = float(data['bill_amount'])
        tip_percent = float(data['tip_percent'])
        base = data.get('base_currency', 'USD').upper()
        target = data.get('target_currency', base).upper()
        split = int(data.get('split_count', 1))
        note = data.get('note', '')
        user_id = data.get('user_id')

        # Calculează
        tip = bill * (tip_percent / 100)
        total = bill + tip

        # Obține cursul
        rate = exchange_service.get_cached_rate(base, target)
        if rate is None:
            rate = 1.0

        # Conversie
        conv_bill = bill * rate
        conv_tip = tip * rate
        conv_total = total * rate

        # Salvează în istoric
        with get_db() as conn:
            cursor = conn.cursor()
            cursor.execute('''
                INSERT INTO tip_history (
                    user_id, bill_amount, tip_percent, total_amount,
                    base_currency, target_currency, split_count, note, exchange_rate
                ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
            ''', (user_id, bill, tip_percent, total, base, target, split, note, rate))

        return jsonify({
            'success': True,
            'original': {
                'bill': round(bill, 2),
                'tip': round(tip, 2),
                'total': round(total, 2),
                'currency': base
            },
            'converted': {
                'bill': round(conv_bill, 2),
                'tip': round(conv_tip, 2),
                'total': round(conv_total, 2),
                'currency': target
            },
            'per_person': {
                'bill': round(conv_bill / split, 2),
                'tip': round(conv_tip / split, 2),
                'total': round(conv_total / split, 2)
            },
            'exchange_rate': rate,
            'split_count': split
        })

    except ValueError as e:
        return jsonify({'success': False, 'error': 'Invalid input values'}), 400
    except Exception as e:
        logger.error(f"Tip calc error: {e}")
        return jsonify({'success': False, 'error': str(e)}), 500

@app.route('/api/tip-history', methods=['GET'])
@track_usage('/api/tip-history')
def get_tip_history():
    """Obține istoricul calculelor"""
    user_id = request.args.get('user_id')
    limit = int(request.args.get('limit', 100))
    
    try:
        with get_db() as conn:
            cursor = conn.cursor(cursor_factory=RealDictCursor)
            
            if user_id:
                cursor.execute('''
                    SELECT * FROM tip_history 
                    WHERE user_id = %s 
                    ORDER BY created_at DESC 
                    LIMIT %s
                ''', (user_id, limit))
            else:
                cursor.execute('''
                    SELECT * FROM tip_history 
                    ORDER BY created_at DESC 
                    LIMIT %s
                ''', (limit,))
            
            history = []
            for row in cursor.fetchall():
                history.append({
                    'id': row['id'],
                    'user_id': row['user_id'],
                    'bill_amount': float(row['bill_amount']),
                    'tip_percent': float(row['tip_percent']),
                    'total_amount': float(row['total_amount']),
                    'base_currency': row['base_currency'],
                    'target_currency': row['target_currency'],
                    'split_count': row['split_count'],
                    'note': row['note'],
                    'exchange_rate': float(row['exchange_rate']) if row['exchange_rate'] else None,
                    'created_at': row['created_at'].isoformat()
                })
            
            return jsonify({'success': True, 'history': history})
    except Exception as e:
        logger.error(f"History error: {e}")
        return jsonify({'success': False, 'error': str(e)}), 500

@app.route('/api/statistics', methods=['GET'])
@track_usage('/api/statistics')
def get_statistics():
    """Obține statistici despre utilizare"""
    user_id = request.args.get('user_id')
    
    try:
        with get_db() as conn:
            cursor = conn.cursor(cursor_factory=RealDictCursor)
            
            if user_id:
                cursor.execute('''
                    SELECT 
                        COUNT(*) as total_calculations,
                        AVG(tip_percent) as avg_tip_percent,
                        SUM(total_amount) as total_spent,
                        MAX(created_at) as last_calculation
                    FROM tip_history
                    WHERE user_id = %s
                ''', (user_id,))
            else:
                cursor.execute('''
                    SELECT 
                        COUNT(*) as total_calculations,
                        AVG(tip_percent) as avg_tip_percent,
                        COUNT(DISTINCT user_id) as unique_users,
                        MAX(created_at) as last_calculation
                    FROM tip_history
                ''')
            
            stats = cursor.fetchone()
            
            if stats:
                stats = dict(stats)
                for key, value in stats.items():
                    if value is None:
                        stats[key] = 0
                    elif key == 'avg_tip_percent' and value:
                        stats[key] = float(value)
                    elif key == 'last_calculation' and value:
                        stats[key] = value.isoformat()
            
            return jsonify({'success': True, 'statistics': stats})
    except Exception as e:
        logger.error(f"Statistics error: {e}")
        return jsonify({'success': False, 'error': str(e)}), 500

@app.route('/api/force-update-rates', methods=['POST'])
@track_usage('/api/force-update-rates')
def force_update_rates():
    """Force update all exchange rates"""
    try:
        success = exchange_service.update_exchange_rates()
        if success:
            return jsonify({'success': True, 'message': 'Rates updated successfully'})
        else:
            return jsonify({'success': False, 'error': 'Failed to update rates'}), 500
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)