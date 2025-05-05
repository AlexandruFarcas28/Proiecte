#!/bin/bash

DIRECTOR_BACKUP=$(realpath ../backups/)

while true; do
    CHOICE=$(whiptail --title "Ștergere Automată" --menu "Selectați modul de ștergere pe care doriți să îl utilizați:" 15 78 3 \
        "1" "Pornire stergere automată a fișierelor mai vechi de 60 zile" \
        "2" "Oprire ștergere automată" \
        "3" "Back" 3>&1 1>&2 2>&3)

    if [ $? -ne 0 ]; then
        echo "[$(date '+%H:%M:%S')] Operațiune anulată" >> "$path_to_logfile"
        exit 0
    fi

    case $CHOICE in
        "1")
            (crontab -l 2>/dev/null; echo "0 20 * * 1 find $DIRECTOR_BACKUP -type f -mtime +60 -delete") | crontab -
            whiptail --title "Success" --msgbox "Ștergerea automată a fost configurată." 8 78
            echo "[$(date '+%H:%M:%S')] Ștergerea automată a fost configurată." >> "$path_to_logfile"
            break
            ;;
        "2")
            crontab -l | grep -v "find $DIRECTOR_BACKUP -type f -mtime +60 -delete" | crontab -
            whiptail --title "Success" --msgbox "Ștergerea automată a fost dezactivată." 8 78
            echo "[$(date '+%H:%M:%S')] Ștergerea automată a fost dezactivată." >> "$path_to_logfile"
            break
            ;;
        "3")
            exit 0
            ;;
    esac
done
