#!/bin/bash

path=$(whiptail --title "Backup Directory" --inputbox "Va rog introduceti calea spre directorul pe care se va face backup-ul automat:" 8 78 3>&1 1>&2 2>&3)

if [ $? -ne 0 ]; then
    echo "[$(date '+%H:%M:%S')] Operatiune anulata" >> "$path_to_logfile"
    exit 1
fi

if [ ! -d "$path" ]; then
    whiptail --title "Error" --msgbox "[ERROR] Directorul specificat nu exista!" 8 78
    echo "[ERROR] Directorul specificat nu exista!" >> "$path_to_logfile"
    exit 1
fi

DEST=$(realpath ../backups/)
SRC=$(basename "$path")

FREQUENCY=$(whiptail --title "Frecventa Backup" --menu "Selectati frecventa la care se va face backup-ul automat:" 15 78 4 \
    "1" "In fiecare zi la ora 00:00" \
    "2" "Odata pe saptamana, lunea" \
    "3" "Odata pe luna" \
    "4" "Odata pe an" 3>&1 1>&2 2>&3)

if [ $? -ne 0 ]; then
    echo "[$(date '+%H:%M:%S')] Operatiune anulata" >> "$path_to_logfile"
    exit 1
fi

case $FREQUENCY in
    "1")
        FR="0 0 * * *"
        MESSAGE="Backup automat pentru directorul ${path} in fiecare zi la ora 00:00"
        ;;
    "2")
        FR="0 0 * * 1"
        MESSAGE="Backup automat pentru directorul ${path} odata pe saptamana, lunea"
        ;;
    "3")
        FR="0 0 1 * *"
        MESSAGE="Backup automat pentru directorul ${path} odata pe luna"
        ;;
    "4")
        FR="0 0 1 1 *"
        MESSAGE="Backup automat pentru directorul ${path} odata pe an"
        ;;
esac

whiptail --title "Success" --msgbox "$MESSAGE" 8 78
echo "[$(date '+%H:%M:%S')] $MESSAGE" >> "$path_to_logfile"

if [[ -n $FR ]]; then
    REALSRC=$(realpath "$path")
    CMD="tar -czf ${DEST}/${SRC}.tar.gz ${REALSRC}"
    (crontab -l 2>/dev/null; echo "$FR $CMD") | crontab -
    
    whiptail --title "Success" --msgbox "Backup automat configurat cu succes!" 8 78
fi
