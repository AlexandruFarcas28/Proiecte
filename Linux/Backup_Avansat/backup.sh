#!/bin/bash
set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[1;35m'
CYAN='\033[0;36m'
BOLD='\033[1m'
RESET='\033[0m'

log_file="../logs/backup.log"
default_backup_dir=$(realpath -m "../backups")
max_parallel_jobs=4

log_message() {
    local level="$1"
    local message="$2"
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] [$level] $message" | tee -a "$log_file"
}

exec > >(tee -a "$log_file") 2>&1

debug_log() {
    if [[ "$DEBUG" == "on" ]]; then
        log_message "DEBUG" "$1"
    fi
}

configure_parallel_jobs() {
    while true; do
        read -p "Introduceți numărul de procese paralele (implicit 4, maxim 15): " jobs
        if [[ -z "$jobs" ]]; then
            max_parallel_jobs=4
            log_message "INFO" "Numărul de procese paralele setat implicit: $max_parallel_jobs"
            break
        elif [[ "$jobs" =~ ^[0-9]+$ && "$jobs" -gt 0 && "$jobs" -le 15 ]]; then
            max_parallel_jobs="$jobs"
            log_message "INFO" "Numărul de procese paralele setat la: $max_parallel_jobs"
            break
        elif [[ "$jobs" =~ ^[0-9]+$ && "$jobs" -gt 15 ]]; then
            log_message "ERROR" "Nu prăjim procesoare aici! Alegeți un număr de procese mai mic sau egal cu 15."
        else
            log_message "ERROR" "Introduceți un număr valid mai mare de 0 și mai mic sau egal cu 15."
        fi
    done
}

select_backup_dest() {
    log_message "INFO" "Începerea selecției directorului destinație"
    echo -e "${CYAN}Selectați locația destinației backup-ului:${RESET}"
    options=("Director implicit (../backups)" "Navigare" "Creează un director nou" "Ieșire")

    mkdir -p "$default_backup_dir"
    current_dir="$HOME"

    while true; do
        echo "======================"
        echo -e "${CYAN}Director curent: $current_dir${RESET}"
        echo -e "${YELLOW}Director implicit disponibil: $default_backup_dir${RESET}"
        log_message "INFO" "Navigare în directorul: $current_dir"
        echo "======================"

        select opt in "${options[@]}"; do
            case $opt in
                "Director implicit (../backups)")
                    backup_dest="$default_backup_dir"
                    log_message "INFO" "Director implicit selectat: $backup_dest"
                    return 0
                    ;;
                "Navigare")
                    debug_log "Navigare în $current_dir"
                    entries=($(ls -1 "$current_dir"))
                    entries+=("Înapoi")
                    entries+=("Selectează directorul curent")
                    entries+=("Înapoi la meniu")
                    select entry in "${entries[@]}"; do
                        if [[ "$entry" == "Înapoi" ]]; then
                            if [[ "$current_dir" != "/" ]]; then
                                current_dir=$(dirname "$current_dir")
                                log_message "INFO" "Navigare înapoi la: $current_dir"
                            else
                                log_message "WARNING" "Deja în directorul rădăcină (/)"
                            fi
                            break
                        elif [[ "$entry" == "Selectează directorul curent" ]]; then
                            backup_dest="$current_dir"
                            log_message "INFO" "Director destinație selectat: $backup_dest"
                            return 0
                        elif [[ "$entry" == "Înapoi la meniu" ]]; then
                            break
                        elif [[ -d "$current_dir/$entry" ]]; then
                            current_dir="$current_dir/$entry"
                            log_message "INFO" "Director nou selectat: $current_dir"
                            break
                        else
                            log_message "ERROR" "Director invalid selectat: $entry"
                        fi
                    done
                    break
                    ;;
                "Creează un director nou")
                    read -p "Introduceți numele directorului nou: " new_dir
                    if [[ -z "$new_dir" ]]; then
                        log_message "ERROR" "Numele directorului nu poate fi gol"
                    else
                        new_path="$current_dir/$new_dir"
                        if [[ -e "$new_path" ]]; then
                            log_message "ERROR" "Directorul '$new_path' deja există"
                        else
                            mkdir -p "$new_path"
                            if [[ $? -eq 0 ]]; then
                                log_message "INFO" "Director nou creat: $new_path"
                                current_dir="$new_path"
                            else
                                log_message "ERROR" "Nu s-a putut crea directorul: $new_path"
                            fi
                        fi
                    fi
                    break
                    ;;
                "Ieșire")
                    log_message "INFO" "Script oprit de utilizator"
                    exit 0
                    ;;
                *)
                    log_message "ERROR" "Opțiune invalidă selectată"
                    ;;
            esac
        done
    done
}

check_space() {
    local dest="$1"
    local min_space=100000

    log_message "INFO" "Verificare spațiu disponibil pentru: $dest"
    available_space=$(df -k "$dest" | awk 'NR==2 {print $4}')
    if (( available_space < min_space )); then
        log_message "ERROR" "Spațiu insuficient în destinația '$dest'. Disponibil: $available_space KB, necesar: $min_space KB"
        exit 1
    fi

    log_message "INFO" "Spațiu suficient în destinația '$dest'. Disponibil: $available_space KB"
}

select_items() {
    echo -e "${CYAN}Navigați și selectați fișierele sau directoarele pentru backup:${RESET}"
    current_dir="$HOME"
    options=("Navigare" "Adaugă locație" "Finalizare selecție" "Ieșire")

    while true; do
        echo "======================"
        echo -e "${CYAN}Director curent: $current_dir${RESET}"
        echo -e "${YELLOW}Fișiere selectate până acum: ${selected_items[*]:-(nimic selectat)}${RESET}"
        echo "======================"

        select opt in "${options[@]}"; do
            case $opt in
                "Navigare")
                    debug_log "Navigare în $current_dir"
                    entries=($(ls -1 "$current_dir"))
                    entries+=("Înapoi")
                    entries+=("Înapoi la meniu")
                    select entry in "${entries[@]}"; do
                        if [[ "$entry" == "Înapoi" ]]; then
                            if [[ "$current_dir" != "/" ]]; then
                                current_dir=$(dirname "$current_dir")
                                debug_log "Navigat în sus la $current_dir"
                            else
                                log_message "WARNING" "Deja în directorul rădăcină (/)"
                            fi
                            break
                        elif [[ "$entry" == "Înapoi la meniu" ]]; then
                            break
                        elif [[ -d "$current_dir/$entry" ]]; then
                            current_dir="$current_dir/$entry"
                            debug_log "Navigat în $current_dir"
                            break
                        else
                            log_message "ERROR" "Director invalid"
                        fi
                    done
                    break
                    ;;
                "Adaugă locație")
                    echo -e "${CYAN}Selectare fișier sau director pentru backup:${RESET}"
                    entries=($(ls -1 "$current_dir"))
                    entries+=("Înapoi")
                    select entry in "${entries[@]}"; do
                        if [[ "$entry" == "Înapoi" ]]; then
                            break
                        elif [[ -e "$current_dir/$entry" ]]; then
                            full_path="$current_dir/$entry"
                            if [[ " ${selected_items[*]} " == *" $full_path "* ]]; then
                                log_message "WARNING" "Locația '$full_path' este deja selectată"
                            else
                                selected_items+=("$full_path")
                                log_message "INFO" "Adăugat pentru backup: $full_path"
                            fi
                        else
                            log_message "ERROR" "Locație invalidă"
                        fi
                    done
                    break
                    ;;
                "Finalizare selecție")
                    if [[ ${#selected_items[@]} -eq 0 ]]; then
                        log_message "ERROR" "Nu ați selectat nimic"
                        break
                    fi
                    log_message "INFO" "Selecție finalizată: ${selected_items[*]}"
                    return 0
                    ;;
                "Ieșire")
                    log_message "INFO" "Script oprit de utilizator"
                    exit 0
                    ;;
                *)
                    log_message "ERROR" "Opțiune invalidă"
                    ;;
            esac
        done
    done
}

log_message "INFO" "Script backup pornit"
configure_parallel_jobs
select_backup_dest

if [[ -n "$backup_dest" ]]; then
    backup_dest=$(realpath -m "$backup_dest")
    log_message "INFO" "Cale absolută pentru destinație: $backup_dest"
fi

check_space "$backup_dest"

selected_items=()
select_items

while true; do
    read -p "Doriți să efectuați un test (dry-run) înainte de backup? (y/n): " dry_run
    if [[ "$dry_run" == "y" ]]; then
        echo -e "${CYAN}Testare dry-run în curs...${RESET}"
        echo "=============================="
        echo -e "${YELLOW}Rezultate dry-run:${RESET}"
        for item in "${selected_items[@]}"; do
            if [[ -e "$item" ]]; then
                rsync -av --dry-run "$item" "$backup_dest" >/dev/null 2>&1
                if [[ $? -eq 0 ]]; then
                    log_message "INFO" "[SUCCES] Test dry-run pentru: $item"
                else
                    log_message "ERROR" "[EROARE] Test dry-run pentru: $item"
                fi
            else
                log_message "ERROR" "[EROARE] Fișier sau director inexistent: $item"
            fi
        done
        echo "=============================="
        read -p "Continuați cu backup-ul real? (y/n): " real_backup
        if [[ "$real_backup" == "y" ]]; then
            break
        elif [[ "$real_backup" == "n" ]]; then
            log_message "INFO" "Backup anulat de utilizator"
            exit 0
        else
            log_message "ERROR" "Opțiune invalidă"
        fi
    elif [[ "$dry_run" == "n" ]]; then
        log_message "INFO" "Testare dry-run anulată"
        read -p "Continuați cu backup-ul real? (y/n): " real_backup
        if [[ "$real_backup" == "y" ]]; then
            break
        elif [[ "$real_backup" == "n" ]]; then
            log_message "INFO" "Backup anulat de utilizator"
            exit 0
        else
            log_message "ERROR" "Opțiune invalidă"
        fi
    else
        log_message "ERROR" "Opțiune invalidă"
    fi
done

log_message "INFO" "Începerea backup-ului real cu paralelism"
pids=()
for item in "${selected_items[@]}"; do
    if [[ -e "$item" ]]; then
        debug_log "Backup pentru: $item"
        rsync -a "$item" "$backup_dest" &
        pids+=($!)
    else
        log_message "ERROR" "Fișier sau director inexistent: $item"
    fi

    if [[ ${#pids[@]} -ge $max_parallel_jobs ]]; then
        wait -n
        for pid in "${pids[@]}"; do
            if ! kill -0 "$pid" 2>/dev/null; then
                pids=("${pids[@]/$pid/}")
            fi
        done
    fi
done

for pid in "${pids[@]}"; do
    wait "$pid"
    if [[ $? -eq 0 ]]; then
        log_message "INFO" "Proces finalizat cu succes pentru PID: $pid"
    else
        log_message "ERROR" "Eroare la procesul cu PID: $pid"
    fi
done

log_message "INFO" "Backup complet"
echo -e "${BOLD}${GREEN}Backup complet cu succes!${RESET}"
echo -e "${BOLD}${BLUE}Backup închiat la $(date)!${RESET}"
