#!/bin/bash

# Get the current script directory
script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"

# Set the root directory path
root_dir="$script_dir/src"

output="orangebot.java"

if [[ -f "$output" ]]; then
    rm "$output"
    echo "// Bundle uploaded at $(date)" >> "$output"
    echo "import java.util.*;" >> "$output"
    echo "import java.util.stream.Collectors;" >> "$output"
fi

find "$root_dir" -name "*.java" -print0 | while IFS= read -r -d $'\0' f; do
    sed -e 's/public enum/enum/g' -e 's/public class/class/g' "$f" | \
    grep -vE 'package|import|^$' >> "$output"
done
