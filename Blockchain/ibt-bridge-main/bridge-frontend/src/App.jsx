import { useState, useEffect } from 'react'
import axios from 'axios'
import './App.css'

// Hardcoded accounts pentru localnet demo
const ACCOUNTS = {
  ethereum: {
    address: '0xf39Fd6e51aad88F6F4ce6aB8827279cffFb92266',
    name: 'Anvil Account #0',
    network: 'Anvil Local'
  },
  sui: {
    address: '0x6a2d504b05a202c749cd7ccb06fa92ced0becf55b3b7b909d8257f9566a604bc',
    name: 'Sui Local Account',
    network: 'Sui Localnet'
  }
}

function App() {
  const [bridgeDirection, setBridgeDirection] = useState('eth-to-sui')
  const [amount, setAmount] = useState('')
  const [ethBalance, setEthBalance] = useState('Loading...')
  const [isLoading, setIsLoading] = useState(false)
  const [status, setStatus] = useState(null)
  const [txHistory, setTxHistory] = useState([])

  useEffect(() => {
    fetchEthBalance()
  }, [])

  const fetchEthBalance = async () => {
    try {
      const res = await axios.get(`http://localhost:3001/balance/ethereum/${ACCOUNTS.ethereum.address}`)
      setEthBalance(parseFloat(res.data.balance).toFixed(2))
    } catch (error) {
      console.error('Error fetching balance:', error)
      setEthBalance('Error')
    }
  }

  const handleBridge = async () => {
    if (!amount || parseFloat(amount) <= 0) {
      setStatus({ type: 'error', message: '⚠️ Please enter a valid amount' })
      return
    }

    const sourceChain = bridgeDirection === 'eth-to-sui' ? 'ethereum' : 'sui'
    const destinationChain = bridgeDirection === 'eth-to-sui' ? 'sui' : 'ethereum'
    const userAddress = bridgeDirection === 'eth-to-sui' 
      ? ACCOUNTS.ethereum.address 
      : ACCOUNTS.sui.address

    setIsLoading(true)
    setStatus({ 
      type: 'info', 
      message: `🔄 Bridging ${amount} IBT from ${sourceChain.toUpperCase()} to ${destinationChain.toUpperCase()}...` 
    })

    try {
      const response = await axios.post('http://localhost:3001/bridge', {
        sourceChain,
        destinationChain,
        amount: parseFloat(amount),
        userAddress,
        ethAddress: ACCOUNTS.ethereum.address,
        suiAddress: ACCOUNTS.sui.address
      })

      const newTx = {
        id: Date.now(),
        direction: bridgeDirection,
        amount: amount,
        timestamp: new Date().toLocaleTimeString(),
        ethTx: response.data.burn?.txHash,
        suiTx: response.data.mint?.digest
      }

      setTxHistory(prev => [newTx, ...prev].slice(0, 5)) // Keep last 5 transactions

      setStatus({ 
        type: 'success', 
        message: `✅ Successfully bridged ${amount} IBT!`,
        txHash: response.data.burn?.txHash,
        digest: response.data.mint?.digest
      })
      
      setAmount('')
      
      // Refresh balance
      setTimeout(() => {
        fetchEthBalance()
      }, 1000)
      
    } catch (error) {
      console.error('Bridge error:', error)
      setStatus({ 
        type: 'error', 
        message: '❌ ' + (error.response?.data?.error || 'Bridge failed. Check backend logs for details.') 
      })
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="app-container">
      <div className="bridge-card">
        {/* Header */}
        <div style={{ textAlign: 'center', marginBottom: '30px' }}>
          <h1 className="title">🌉 IBT Bridge</h1>
          <p className="subtitle">Cross-chain bridge between Ethereum and Sui</p>
          <div style={{ 
            display: 'inline-block',
            padding: '6px 12px',
            background: '#f0fdf4',
            border: '1px solid #86efac',
            borderRadius: '6px',
            fontSize: '12px',
            color: '#166534',
            fontWeight: '600'
          }}>
            🟢 LOCALNET MODE
          </div>
        </div>

        {/* Accounts Grid */}
        <div className="wallets-grid">
          <div className="wallet-card">
            <h3>💎 Ethereum</h3>
            <p style={{ fontSize: '11px', color: '#999', marginBottom: '8px' }}>
              {ACCOUNTS.ethereum.name}
            </p>
            <p className="wallet-address">{ACCOUNTS.ethereum.address}</p>
            <p className="wallet-balance">{ethBalance} IBT</p>
            <div style={{ 
              marginTop: '10px',
              padding: '6px',
              background: '#f0fdf4',
              borderRadius: '4px'
            }}>
              <p style={{ fontSize: '11px', color: '#16a34a', margin: 0 }}>
                ✓ {ACCOUNTS.ethereum.network}
              </p>
            </div>
          </div>

          <div className="wallet-card">
            <h3>🔷 Sui</h3>
            <p style={{ fontSize: '11px', color: '#999', marginBottom: '8px' }}>
              {ACCOUNTS.sui.name}
            </p>
            <p className="wallet-address">{ACCOUNTS.sui.address}</p>
            <div style={{ 
              marginTop: '10px',
              padding: '6px',
              background: '#f0fdf4',
              borderRadius: '4px'
            }}>
              <p style={{ fontSize: '11px', color: '#16a34a', margin: 0 }}>
                ✓ {ACCOUNTS.sui.network}
              </p>
            </div>
          </div>
        </div>

        {/* Bridge Direction */}
        <div className="form-section">
          <label className="form-label">🔀 Bridge Direction</label>
          <div className="direction-buttons">
            <button
              onClick={() => setBridgeDirection('eth-to-sui')}
              className={`direction-button ${bridgeDirection === 'eth-to-sui' ? 'active-eth' : ''}`}
            >
              <span style={{ fontSize: '20px' }}>💎</span>
              <br />
              <span style={{ fontSize: '14px' }}>Ethereum → Sui</span>
            </button>
            <button
              onClick={() => setBridgeDirection('sui-to-eth')}
              className={`direction-button ${bridgeDirection === 'sui-to-eth' ? 'active-sui' : ''}`}
            >
              <span style={{ fontSize: '20px' }}>🔷</span>
              <br />
              <span style={{ fontSize: '14px' }}>Sui → Ethereum</span>
            </button>
          </div>
        </div>

        {/* Amount Input */}
        <div className="form-section">
          <label className="form-label">💰 Amount (IBT)</label>
          <input
            type="number"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            placeholder="Enter amount (e.g., 100)"
            className="amount-input"
            min="0"
            step="1"
          />
          <div style={{ 
            display: 'flex', 
            justifyContent: 'space-between',
            marginTop: '8px',
            fontSize: '11px',
            color: '#666'
          }}>
            <span>Available: {ethBalance} IBT</span>
            <button
              onClick={() => setAmount(Math.floor(parseFloat(ethBalance) / 2).toString())}
              style={{
                background: 'none',
                border: 'none',
                color: '#2563eb',
                cursor: 'pointer',
                fontSize: '11px',
                textDecoration: 'underline'
              }}
            >
              Use 50%
            </button>
          </div>
        </div>

        {/* Bridge Button */}
        <button
          onClick={handleBridge}
          disabled={isLoading || !amount}
          className="bridge-submit-button"
        >
          {isLoading ? (
            <>⏳ Processing...</>
          ) : (
            <>🚀 Bridge {amount || '___'} IBT</>
          )}
        </button>

        {/* Status Message */}
        {status && (
          <div className={`status-message status-${status.type}`}>
            <p className="status-title">{status.message}</p>
            {status.txHash && (
              <div className="status-details">
                <strong>🔗 Ethereum TX:</strong><br/>
                <code style={{ fontSize: '10px', wordBreak: 'break-all' }}>
                  {status.txHash}
                </code>
              </div>
            )}
            {status.digest && (
              <div className="status-details" style={{ marginTop: '8px' }}>
                <strong>🔗 Sui TX:</strong><br/>
                <code style={{ fontSize: '10px', wordBreak: 'break-all' }}>
                  {status.digest}
                </code>
              </div>
            )}
          </div>
        )}

        {/* Transaction History */}
        {txHistory.length > 0 && (
          <div style={{ marginTop: '20px' }}>
            <h3 style={{ fontSize: '14px', fontWeight: '600', marginBottom: '10px', color: '#374151' }}>
              📜 Recent Transactions
            </h3>
            <div style={{ 
              border: '1px solid #e5e7eb',
              borderRadius: '6px',
              overflow: 'hidden'
            }}>
              {txHistory.map((tx, index) => (
                <div 
                  key={tx.id}
                  style={{
                    padding: '10px',
                    background: index % 2 === 0 ? '#f9fafb' : 'white',
                    borderBottom: index < txHistory.length - 1 ? '1px solid #e5e7eb' : 'none',
                    fontSize: '11px'
                  }}
                >
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px' }}>
                    <span style={{ fontWeight: '600', color: '#1f2937' }}>
                      {tx.direction === 'eth-to-sui' ? '💎→🔷' : '🔷→💎'} {tx.amount} IBT
                    </span>
                    <span style={{ color: '#6b7280' }}>{tx.timestamp}</span>
                  </div>
                  <div style={{ fontSize: '10px', color: '#6b7280', fontFamily: 'monospace' }}>
                    {tx.ethTx?.slice(0, 20)}...
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Info Box */}
        <div style={{ 
          marginTop: '20px', 
          padding: '12px', 
          background: 'linear-gradient(135deg, #667eea15, #764ba215)',
          borderRadius: '8px',
          border: '1px solid #818cf8'
        }}>
          <p style={{ fontSize: '12px', color: '#4338ca', margin: 0, lineHeight: '1.6' }}>
            💡 <strong>Demo Mode:</strong> Using pre-configured local accounts from Anvil and Sui localnet. 
            No wallet extensions required! Perfect for development and testing.
          </p>
        </div>
      </div>

      {/* Footer */}
      <div className="footer-info">
        <p style={{ fontWeight: '600' }}>🔗 Localnet Environment</p>
        <p>Ethereum RPC: 127.0.0.1:8545</p>
        <p>Sui RPC: 127.0.0.1:9000</p>
        <p>Bridge API: localhost:3001</p>
        <p style={{ marginTop: '12px', fontSize: '11px', opacity: 0.8 }}>
          Built with React + Vite | Backend: Node.js + Express
        </p>
      </div>
    </div>
  )
}

export default App
