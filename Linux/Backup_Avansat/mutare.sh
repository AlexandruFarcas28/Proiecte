#!/bin/bash

if [[ -z $path_to_logfile ]]; then
    echo "[ERROR] Calea spre fisierul de log nu a fost setata! "  | tee -a "$path_to_logfile" >&2
    exit 1
fi

echo "Introduceti calea spre directorul sursa: "
read -r src

if [[ ! -d $src ]]; then
    echo "[ERROR] Directorul sursa nu exista" | tee -a "$path_to_logfile" >&2
    exit 1
fi

echo "[$(date '+%H:%M:%S')] Director sursa setat la $src" >> $path_to_logfile

PS3="Alegeti destinatia: "
select opt in "default" "specificati director" "cloud"; do
    case $opt in
        "default")
            dest="../backups/"
            break
            ;;
        "specificati director")
            echo "Introduceti directorul destinatie:"
            read -r dest
            mkdir -p "$dest"
            break
            ;;
        "cloud")
            # does not work yet
            exit 0
            ;;
    esac
done

echo "[$(date '+%H:%M:%S')] Fisiere vor fi mutate din $src in: $dest" | tee -a "$path_to_logfile"
mkdir -p "$dest"
for file in "$src"/*; do
    if [[ -f $file ]]; then
        hash_before=$(md5sum "$file" | awk '{print $1}')
        if ! mv "$file" "$dest/"; then
            echo "[ERROR] Nu s-a reusit mutarea fisierului: $file" | tee -a "$path_to_logfile" >&2
            continue
        fi
        hash_after=$(md5sum "$dest/$(basename "$file")" | awk '{print $1}')
        if [[ $hash_before != $hash_after ]]; then
            echo "[ERROR] Integritatea fisierelor a fost compromisa: $file" | tee -a "$path_to_logfile" >&2
            continue
        fi
    fi
done
echo "[$(date '+%H:%M:%S')] Mutarea fisierelor a fost realizata cu succes !" | tee -a "$path_to_logfile"



