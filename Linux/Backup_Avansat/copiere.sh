#!/bin/bash

if [[ -z $path_to_logfile ]]; then
    echo "[ERROR] Calea spre fisierul de log nu a fost setata!" >&2
    exit 1
fi

COMPRESS="false"
RAW="false"
SOURCE=()
DEFAULT_BACKUP_DIR="../backups"
DESTINATION=""

if [[ ! -d "$DEFAULT_BACKUP_DIR" ]]; then
    mkdir -p "$DEFAULT_BACKUP_DIR"
    echo "[$(date '+%H:%M:%S')] Creare director implicit de backup: $DEFAULT_BACKUP_DIR" >> "$path_to_logfile"
fi

function navigate_and_select_sources() {
    local current_dir=$(pwd)
    echo -e "\033[1;36mNavigați și selectați fișierele sau directoarele pe care doriți să le includeți:\033[0m"
    echo "[$(date '+%H:%M:%S')] Incepere selectie surse" >> "$path_to_logfile"
    while true; do
        echo -e "\033[1;33mLocația curentă: $current_dir\033[0m"
        local options=(".. (Mergi înapoi)" $(ls -p) "Finalizează selecția" "Anulare")
        select opt in "${options[@]}"; do
            case $opt in
                ".. (Mergi înapoi)")
                    cd ..
                    current_dir=$(pwd)
                    echo "[$(date '+%H:%M:%S')] Navigare inapoi la: $current_dir" >> "$path_to_logfile"
                    break
                    ;;
                "Finalizează selecția")
                    echo -e "\033[1;32mSelecție finalizată:\033[0m ${SOURCE[@]}"
                    echo "[$(date '+%H:%M:%S')] Selectie surse finalizata: ${SOURCE[*]}" >> "$path_to_logfile"
                    return
                    ;;
                "Anulare")
                    echo -e "\033[1;31mOperațiunea a fost anulată.\033[0m"
                    echo "[$(date '+%H:%M:%S')] Selectie surse anulata" >> "$path_to_logfile"
                    exit 1
                    ;;
                *)
                    if [[ -d "$opt" ]]; then
                        cd "$opt"
                        current_dir=$(pwd)
                        echo "[$(date '+%H:%M:%S')] Navigare in directorul: $current_dir" >> "$path_to_logfile"
                        break
                    elif [[ -f "$opt" ]]; then
                        if [[ " ${SOURCE[@]} " =~ " $current_dir/$opt " ]]; then
                            echo -e "\033[1;31mFișierul '$current_dir/$opt' a fost deja selectat.\033[0m"
                            echo "[$(date '+%H:%M:%S')] Incercare selectie duplicat: $current_dir/$opt" >> "$path_to_logfile"
                        else
                            SOURCE+=("$current_dir/$opt")
                            echo -e "\033[1;35mAdăugat:\033[0m $current_dir/$opt"
                            echo "[$(date '+%H:%M:%S')] Fisier selectat: $current_dir/$opt" >> "$path_to_logfile"
                        fi
                    else
                        echo -e "\033[1;31mOpțiune invalidă.\033[0m"
                        echo "[$(date '+%H:%M:%S')] Selectie invalida" >> "$path_to_logfile"
                    fi
                    ;;
            esac
        done
    done
}

function navigate_and_select_destination() {
    local current_dir=$(pwd)
    echo -e "\033[1;36mSelectați directorul destinație:\033[0m"
    echo -e "\033[1;33mImplicit: $DEFAULT_BACKUP_DIR\033[0m"
    echo "[$(date '+%H:%M:%S')] Incepere selectie destinatie" >> "$path_to_logfile"
    
    PS3="Alegeți o opțiune: "
    options=("Folosește directorul implicit" "Selectează alt director" "Anulare")
    select opt in "${options[@]}"; do
        case $opt in
            "Folosește directorul implicit")
                DESTINATION="$DEFAULT_BACKUP_DIR"
                echo -e "\033[1;32mSe va folosi directorul implicit: $DESTINATION\033[0m"
                echo "[$(date '+%H:%M:%S')] Director destinatie implicit selectat: $DESTINATION" >> "$path_to_logfile"
                return
                ;;
            "Selectează alt director")
                while true; do
                    echo -e "\033[1;33mLocația curentă: $current_dir\033[0m"
                    local dir_options=(".. (Mergi înapoi)" $(ls -d */ 2>/dev/null) "Creează un director nou" "Finalizează selecția" "Anulare")
                    select dir_opt in "${dir_options[@]}"; do
                        case $dir_opt in
                            ".. (Mergi înapoi)")
                                cd ..
                                current_dir=$(pwd)
                                echo "[$(date '+%H:%M:%S')] Navigare inapoi la: $current_dir" >> "$path_to_logfile"
                                break
                                ;;
                            "Creează un director nou")
                                read -p "Introduceți numele noului director: " new_dir
                                mkdir -p "$new_dir"
                                DESTINATION="$current_dir/$new_dir"
                                echo -e "\033[1;32mDirectorul '$DESTINATION' a fost creat și selectat ca destinație.\033[0m"
                                echo "[$(date '+%H:%M:%S')] Director nou creat si selectat: $DESTINATION" >> "$path_to_logfile"
                                return
                                ;;
                            "Finalizează selecția")
                                DESTINATION="$current_dir"
                                echo -e "\033[1;32mAți selectat directorul destinație: $DESTINATION\033[0m"
                                echo "[$(date '+%H:%M:%S')] Director destinatie selectat: $DESTINATION" >> "$path_to_logfile"
                                return
                                ;;
                            "Anulare")
                                echo -e "\033[1;31mOperațiunea a fost anulată.\033[0m"
                                echo "[$(date '+%H:%M:%S')] Selectie destinatie anulata" >> "$path_to_logfile"
                                exit 1
                                ;;
                            *)
                                if [[ -d "$dir_opt" ]]; then
                                    cd "$dir_opt"
                                    current_dir=$(pwd)
                                    echo "[$(date '+%H:%M:%S')] Navigare in directorul: $current_dir" >> "$path_to_logfile"
                                    break
                                else
                                    echo -e "\033[1;31mOpțiune invalidă.\033[0m"
                                    echo "[$(date '+%H:%M:%S')] Selectie invalida" >> "$path_to_logfile"
                                fi
                                ;;
                        esac
                    done
                done
                ;;
            "Anulare")
                echo -e "\033[1;31mOperațiunea a fost anulată.\033[0m"
                echo "[$(date '+%H:%M:%S')] Selectie destinatie anulata" >> "$path_to_logfile"
                exit 1
                ;;
            *)
                echo -e "\033[1;31mOpțiune invalidă.\033[0m"
                echo "[$(date '+%H:%M:%S')] Selectie invalida" >> "$path_to_logfile"
                ;;
        esac
    done
}

function select_operation() {
    echo -e "\033[1;36mSelectați operațiunea dorită:\033[0m"
    echo "[$(date '+%H:%M:%S')] Incepere selectie operatiune" >> "$path_to_logfile"
    PS3="Alegeți o opțiune: "
    options=("Comprimare" "Copiere raw" "Anulare")
    select opt in "${options[@]}"; do
        case $opt in
            "Comprimare")
                COMPRESS="true"
                RAW="false"
                echo "[$(date '+%H:%M:%S')] Operatiune selectata: Comprimare" >> "$path_to_logfile"
                break
                ;;
            "Copiere raw")
                RAW="true"
                COMPRESS="false"
                echo "[$(date '+%H:%M:%S')] Operatiune selectata: Copiere raw" >> "$path_to_logfile"
                break
                ;;
            "Anulare")
                echo -e "\033[1;31mOperațiunea a fost anulată.\033[0m"
                echo "[$(date '+%H:%M:%S')] Selectie operatiune anulata" >> "$path_to_logfile"
                exit 1
                ;;
            *)
                echo -e "\033[1;31mOpțiune invalidă.\033[0m"
                echo "[$(date '+%H:%M:%S')] Selectie operatiune invalida" >> "$path_to_logfile"
                ;;
        esac
    done
}

function execute_backup() {
    if [ "$DEBUG" == "on" ]; then
        echo -e "\033[1;34m[DEBUG]\033[0m SOURCE: ${SOURCE[@]}"
        echo -e "\033[1;34m[DEBUG]\033[0m DESTINATION: $DESTINATION"
        echo "[$(date '+%H:%M:%S')] [DEBUG] SOURCE: ${SOURCE[*]}" >> "$path_to_logfile"
        echo "[$(date '+%H:%M:%S')] [DEBUG] DESTINATION: $DESTINATION" >> "$path_to_logfile"
    fi

    if [ "$COMPRESS" == "true" ]; then
        echo -e "\033[1;36m=== Comprimare fișiere/directoare ===\033[0m"
        echo "[$(date '+%H:%M:%S')] Incepere proces de comprimare" >> "$path_to_logfile"
        for src in "${SOURCE[@]}"; do
            tar -czf "$DESTINATION/$(basename "$src").tar.gz" -C "$(dirname "$src")" "$(basename "$src")"
            if [ $? -eq 0 ]; then
                echo -e "\033[1;32mFișierul '$src' a fost comprimat cu succes în $DESTINATION\033[0m"
                echo "[$(date '+%H:%M:%S')] Comprimare reusita: $src -> $DESTINATION/$(basename "$src").tar.gz" >> "$path_to_logfile"
            else
                echo -e "\033[1;31mEroare la comprimare pentru '$src'.\033[0m"
                echo "[$(date '+%H:%M:%S')] [ERROR] Eroare la comprimare: $src" >> "$path_to_logfile"
            fi
        done
    elif [ "$RAW" == "true" ]; then
        echo -e "\033[1;36m=== Copiere fișiere/directoare ===\033[0m"
        echo "[$(date '+%H:%M:%S')] Incepere proces de copiere raw" >> "$path_to_logfile"
        for src in "${SOURCE[@]}"; do
            cp -r "$src" "$DESTINATION"
            if [ $? -eq 0 ]; then
                echo -e "\033[1;32mFișierul '$src' a fost copiat cu succes în $DESTINATION\033[0m"
                echo "[$(date '+%H:%M:%S')] Copiere reusita: $src -> $DESTINATION" >> "$path_to_logfile"
            else
                echo -e "\033[1;31mEroare la copiere pentru '$src'.\033[0m"
                echo "[$(date '+%H:%M:%S')] [ERROR] Eroare la copiere: $src" >> "$path_to_logfile"
            fi
        done
    fi
}

navigate_and_select_sources
navigate_and_select_destination
select_operation
execute_backup
