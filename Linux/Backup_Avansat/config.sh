#!/bin/bash

if [[ -s userdata ]]; then
    whiptail --title "Configuration" --yesno \
        "User data file already exists. Wipe and reconfigure?" 8 78 || exit 0
fi

if whiptail --title "Configuration" --yesno "Do you want to move further with the configuration process ?" 8 78; then

    USERNAME=$(whiptail --inputbox "Enter your GitHub username:" 8 39 --title "Configuration" 3>&1 1>&2 2>&3)

    if [[ $? -ne 0 ]]; then
        echo "An error has occured while setting the username" >&2
        exit 1
    fi

    TOKEN=$(whiptail --inputbox "Enter your GitHub token:" 8 39 --title "Configuration" 3>&1 1>&2 2>&3)
    if [[ $? -ne 0 ]]; then
        echo "An error has occured while setting the token" >&2
        exit 1
    fi

    REPO_LINK=$(whiptail --inputbox "Enter the name of the repo you want the changes to be saved:" 8 39 --title "Configuration" 3>&1 1>&2 2>&3)
    if [[ $? -ne 0 ]]; then
        echo "An error has occured while setting the repo link" >&2
        exit 1
    fi

    whiptail --title "Configuration" --msgbox "Your data has been saved. Configuration completed!" 8 78

    echo $USERNAME > userdata
    echo $TOKEN >> userdata
    echo $REPO_LINK >> userdata

else
    echo "Configuration cancelled"
fi
