#!/bin/bash

source_dir=$(pwd)

if [ ! -f "$source_dir/pk2cmd" ]; then
    echo "pk2cmd not found in the current directory."
    exit 1
fi

if [ ! -f "$source_dir/PK2DeviceFile.dat" ]; then
    echo "PK2DeviceFile.dat not found in the current directory."
    exit 1
fi

destination_dir="/usr/local/bin"

sudo cp "$source_dir/pk2cmd" "$destination_dir/"

sudo cp "$source_dir/PK2DeviceFile.dat" "$destination_dir/"

sudo chmod u+s "$destination_dir/pk2cmd"

echo "pk2cmd and PK2DeviceFile.dat successfully copied to $destination_dir"
