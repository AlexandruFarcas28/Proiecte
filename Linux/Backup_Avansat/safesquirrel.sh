#!/bin/bash

OPTIONS=$(getopt -o hud: -l help,usage,debug: -- "$@")
if [ $? -ne 0 ]; then
    echo "[ERROR] Invalid options provided. Use -h or --help for usage instructions."
    exit 1
fi

eval set -- "$OPTIONS"

DEBUG="off"
date=$(date '+%Y-%m-%d')
path_to_logfile="../logs/$date.log"
export path_to_logfile

if [[ ! -f ../logs/"$date.log" ]]; then
    echo "[$(date '+%H:%M:%S')] Fisier de log creat" > ../logs/"$date.log"
fi

parse_time() {
    local time=$1
    local days_pattern="^([0-9]+)d$"
    local weeks_pattern="^([0-9]+)w$"
    local months_pattern="^([0-9]+)m$"
    local years_pattern="^([0-9]+)y$"
    local crontab_pattern="^([0-9]+)\s+([0-9]+)\s+([0-9]+)\s+([0-9]+)$"

    if [[ $time =~ $days_pattern ]]; then
        echo $((${BASH_REMATCH[1]}))
    elif [[ $time =~ $weeks_pattern ]]; then
        echo $((${BASH_REMATCH[1]} * 7))
    elif [[ $time =~ $months_pattern ]]; then
        echo $((${BASH_REMATCH[1]} * 30))
    elif [[ $time =~ $years_pattern ]]; then
        echo $((${BASH_REMATCH[1]} * 365))
    elif [[ $time =~ $crontab_pattern ]]; then
        local days=${BASH_REMATCH[1]}
        local weeks=${BASH_REMATCH[2]}
        local months=${BASH_REMATCH[3]}
        local years=${BASH_REMATCH[4]}
        echo $((days + weeks * 7 + months * 30 + years * 365))
    else
        return 1
    fi
}

show_help() {
    less <<EOF
NAME
    safesquirrel - Un script pentru backup automat al fisierelor.

DESCRIPTION
    Acest script ofera o alternativa simpla la controlul fisierelor de pe sistem.
    El incorporeaza optiuni de backup impreuna cu compresie, verificare de integritate, criptare si functionalitati cloud.

OPTIONS
    -h, --help
        Arata meniul de help.

    -u, --usage
        Arata utliziari frecvente ale optiunilor meniului.

    -d, --debug [on|off]
        Activeaza sau dezactiveaza modul de debug.

LOGGING
    Toate actiunile executate de script vor fi logate intr-un fisier separat
    numit in functie de data curenta de pe sistem si gasit in directorul logs
    din cadrul aplicatiei.

AUTHORS
    Balcus Bogdan - Contact bogdan.balcus04@e-uvt.ro
    Csala Sebastian - Contact
    Foghis Adrian - Contact
    Maria - Contact
    Alex - Contact

DEBUG MODE
    Modul de debug va afisa in mod aditional informatie pe ecran la executia fiecarei comenzi

FUNCTIONALITIES
    1) Mutarea fisierelor pe sistem cu verificarea integritatii
    2) Copierea fisierelor de pe sistem cu posibilitatea arhivarii
    3) Realizarea de backup in paralel pentru directoare sau fisiere 

QUIT
    Apasati tasta q pentru a parasi meniul de help

SEE ALSO
    bash(1), cp(1), mv(1), rsync(1), whiptail(1), openssl(1)

EOF
}

show_usage() {
    less <<EOF
SET DEBUG MODE
    ./safesquirrel -d on
    ./safesquirrel --debug on
    ./safesquirrel -d off
    ./safesquirrel --debug off

SEE HELP
    ./safesquirrel -h
    ./safesquirrel --help

SEE USAGE
    ./safesquirrel -u
    ./safesquirrel --usage

EOF
}

while true; do
    case $1 in
        -h|--help)
            show_help
            exit 0
            ;;
        -u|--usage)
            show_usage
            exit 0
            ;;
        -d|--debug)
            DEBUG=$2
            echo "Debug mode set to: $DEBUG"
            shift 2
            ;;
        --)
            shift
            break
            ;;
        *)
            echo "[ERROR] Invalid option: $1"
            exit 1
            ;;
    esac
done

echo "[$(date '+%H:%M:%S')] Intrare in aplicatie" >> $path_to_logfile

if [[ ! -f userdata ]]; then
    whiptail --title "Error" --msgbox "[$(date '+%H:%M:%S')] Rulati mai intai scriptul de configurare!" 8 78
    echo "[$(date '+%H:%M:%S')] Rulati mai intai scriptul de configurare!" >> $path_to_logfile
    exit 1
fi

continue_use() {
    if whiptail --title "Continue" --yesno "Doriti sa continuati sa folositi aplicatia?" 8 78; then
        return 0
    else
        echo "[$(date '+%H:%M:%S')] Iesire din aplicatie" >> $path_to_logfile
        exit 0
    fi
}

exec 3< userdata
read -r USERNAME <&3
read -r TOKEN <&3
read -r REPO_LINK <&3
exec 3<&-

export USERNAME
export TOKEN
export REPO_LINK
export DEBUG

while true; do
    CHOICE=$(whiptail --title "SafeSquirrel" --menu "Selectati o optiune:" 20 78 9 \
        "1" "Mutare fisiere" \
        "2" "Copiere fisiere" \
        "3" "Backup paralel manual" \
        "4" "Stergere automata fisiere" \
        "5" "Backup automat" \
        "6" "Resetati crontab" \
        "7" "Salvare in cloud" \
        "8" "Criptare fisiere din backups" \
        "9" "Decriptare fisiere din backups" \
        "10" "Cautare fisiere" \
        "11" "Redenumire fisiere" \
        "12" "Help" \
        "13" "Exit" 3>&1 1>&2 2>&3)

    exitstatus=$?
    if [ $exitstatus != 0 ]; then
        echo "[$(date '+%H:%M:%S')] Iesire din aplicatie" >> $path_to_logfile
        exit 0
    fi

    case $CHOICE in
        1)
            echo "[$(date '+%H:%M:%S')] A fost aleasa optiunea de mutare a fisierelor" >> $path_to_logfile
            ./mutare.sh
            continue_use
            ;;
        2)
            echo "[$(date '+%H:%M:%S')] A fost aleasa optiunea de copiere a fisierelor" >> $path_to_logfile
            ./copiere.sh
            continue_use
            ;;
        3)
            echo "[$(date '+%H:%M:%S')] A fost aleasa optiunea de backup paralel manual" >> $path_to_logfile
            ./backup.sh
            continue_use
            ;;
        4)
            echo "[$(date '+%H:%M:%S')] A fost aleasa optiunea de stergere automata" >> $path_to_logfile
            ./stergere.sh
            continue_use
            ;;
        5)
            echo "[$(date '+%H:%M:%S')] A fost aleasa optiunea de backup automat" >> $path_to_logfile
            ./backup_automat.sh
            continue_use
            ;;
        6)
            crontab -r 2> /dev/null
            whiptail --title "Succes" --msgbox "Crontab resetat cu succes" 8 78
            echo "[$(date '+%H:%M:%S')] Crontab resretat cu succes" >> $path_to_logfile
            continue_use
            ;;
        7)
            echo "[$(date '+%H:%M:%S')] A fost aleasa optiunea de salvare in cloud" >> $path_to_logfile
            ./salvare_cloud.sh
            continue_use
            ;;
        8)
            echo "[$(date '+%H:%M:%S')] A fost aleasa optiunea de criptare a fisierelor din backups" >> $path_to_logfile
            PASSWD=$(whiptail --inputbox "Introduceti parola pentru encriptie:" 8 39 --title "Password" 3>&1 1>&2 2>&3)

            if [[ $? -ne 0 ]]; then
                echo "[ERROR] Eroare la setarea parolei pentru encriptie" | tee -a "$path_to_logfile"
                exit 1
            fi

            find ../backups/ -type f ! -path "../backups/.git/*" ! -name ".gitignore" | while read -r file; do
                openssl enc -aes-256-cbc -salt -in "$file" -out "${file}.enc" -pass pass:$PASSWD 2> /dev/null
                if [[ $? -eq 0 ]]; then
                    echo "[$(date '+%H:%M:%S')] Fișierul '$file' a fost criptat cu succes" >> "$path_to_logfile"
                    rm "$file"
                else
                    echo "[ERROR] Eroare la criptarea fișierului '$file'" >> "$path_to_logfile"
                fi

            done

            continue_use
            ;;
        9)
            echo "[$(date '+%H:%M:%S')] A fost aleasa optiunea de decriptare a fisierelor din backups" >> $path_to_logfile
            PASSWD=$(whiptail --inputbox "Introduceti parola pentru decriptie:" 8 39 --title "Password" 3>&1 1>&2 2>&3)

            if [[ $? -ne 0 ]]; then
                echo "[ERROR] Eroare la setarea parolei pentru decriptare" | tee -a "$path_to_logfile"
                exit 1
            fi

            find ../backups/ -type f -name "*.enc" ! -path "../backups/.git/*" ! -name ".gitignore" | while read -r file; do
                output_file="${file%.enc}"
                openssl enc -aes-256-cbc -d -salt -in "$file" -out "$output_file" -pass pass:$PASSWD 2>&1 1>> "$path_to_logfile"
                if [[ $? -eq 0 ]]; then
                    echo "[$(date '+%H:%M:%S')] Fișierul '$file' a fost decriptat cu succes" >> "$path_to_logfile"
                    rm "$file"
                else
                    echo "[ERROR] Eroare la decriptarea fișierului '$file'" >> "$path_to_logfile"
                fi
            done

            continue_use
            ;;
        10)
            echo "[$(date '+%H:%M:%S')] A fost aleasa optiunea de cautare a fisierelor" >> $path_to_logfile
            START=$(whiptail --inputbox "Introduceti calea din care sa se inceapa cautarea" 8 39 --title "Cautare" 3>&1 1>&2 2>&3)
            if [[ $? -ne 0 ]]; then
                echo "[ERROR] Eroare la citirea locatiei de inceput" | tee -a "$path_to_logfile"
                exit 1
            fi
            if [[ ! -d $START ]]; then
                echo "[ERROR] Locatia data nu constituie un director" | tee -a "$path_to_logfile"
                exit 1
            fi

            TIME=$(whiptail --inputbox "Introduceti pragul de timp (d - zile, w - saptamani, m - luni, y - ani) sau asemanator cu crontab-ul * * * * in ordinea zile saptamani luni ani" 8 100 --title "Cautare" 3>&1 1>&2 2>&3)
            if [[ $? -ne 0 ]]; then
                echo "[ERROR] Eroare la citirea timpului" | tee -a "$path_to_logfile"
                exit 1
            fi

            DAYS=$(parse_time "$TIME")
            if [[ $? -ne 0 ]]; then
                echo "[ERROR] Format timp invalid" | tee -a "$path_to_logfile"
               exit 1
            fi

            results_file="../logs/search_results.txt"
            echo "Fisiere mai vechi de $DAYS zile gasite in $START:" > "$results_file"

            find "$START" -type f -mtime +$DAYS 2>/dev/null | while read -r file; do
                echo "$file" >> "$results_file"
            done

            if [[ -s "$results_file" ]]; then
                echo "[$(date '+%H:%M:%S')] Rezultatele cautarii au fost salvate in $results_file" | tee -a "$path_to_logfile"
            else
                echo "[$(date '+%H:%M:%S')] Nu s-au gasit fisiere care sa corespunda criteriilor de cautare" | tee -a "$path_to_logfile"
            fi
            continue_use
            ;;
        11)
            echo "[$(date '+%H:%M:%S')] S-a ales optiunea de redenumire a fisierelor din backups" >> $path_to_logfile
            if whiptail --title "Redenumire" --yesno "Doriti redenumirea tuturor fisierelor din back-up astfel incat sa includa terminatia .old" 8 78; then
                renamed_count=0

                find "../backups" -type f ! -path "../backups/.git/*" ! -name "*.old" ! -name ".gitignore" | while read -r file; do
                    echo "###DEPRECATED###" >> "$file"
                    mv "$file" "${file}.old"
                    if [[ $? -eq 0 ]]; then
                        echo "[$(date '+%H:%M:%S')] Fisierul $(basename "$file") a fost redenumit in $(basename "${file}.old")" >> "$path_to_logfile"
                        ((renamed_count++))
                    else
                        echo "[ERROR] Eroare la redenumirea fisierului $file" | tee -a "$path_to_logfile"
                    fi
                done

                whiptail --title "Succes" --msgbox "Redenumire realizata cu succes!" 8 78
                echo "[$(date '+%H:%M:%S')] Redenumire completa. $renamed_count fisiere au fost procesate" >> "$path_to_logfile"
            else
                echo "[$(date '+%H:%M:%S')] Redenumire anulata de utilizator" >> "$path_to_logfile"
            fi

            continue_use
            ;;
        12)
            show_help
            continue_use
            ;;
        13)
            echo "[$(date '+%H:%M:%S')] Iesire din aplicatie" >> $path_to_logfile
            exit 0
            ;;
    esac
done
