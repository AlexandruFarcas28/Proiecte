#!/bin/bash

if whiptail --title "Push to cloud" --yesno "Doriti ca fisierele din interiorul folderului de backups sa fie salvate si in cloud?." 8 78; then

    cd ../backups/
    git init .
    git add .
    git commit -m "$(date)"
    git push "$REPO_LINK" --force 

    echo "[$(date '+%H:%M:%S')] Upload in cloud executat cu succes" | tee -a $path_to_logfile

else
    echo "[$(date '+%H:%M:%S')] Nu s-a dorit upload-ul in cloud" | tee -a $path_to_logfile  
fi
