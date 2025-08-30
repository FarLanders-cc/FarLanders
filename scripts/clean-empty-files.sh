#!/usr/bin/env bash
set -euo pipefail

# clean-empty-files.sh
# Dry-run by default: lists zero-byte files under the repo directory, excluding common necessary files.
# To actually delete, pass --force or set FORCE=1 environment variable.

FORCE=0
if [ "${1:-}" = "--force" ] || [ "${FORCE:-0}" = "1" ]; then
  FORCE=1
fi

# Exclude patterns (files that may intentionally be empty or should not be deleted)
EXCLUDES=(
  "docs/.gitkeep"
  "public/.gitkeep"
  "src/main/resources/"
  "src/test/resources/"
)

# build a find command
REPO_ROOT="$(pwd)"

# find zero-byte files (portable loop)
FILES=()
while IFS= read -r -d $'\0' file; do
  file=${file#./}
  FILES+=("$file")
done < <(find . -type f -size 0c -print0)

if [ ${#FILES[@]} -eq 0 ]; then
  echo "No empty files found."
  exit 0
fi

# filter excludes
TO_DELETE=()
for f in "${FILES[@]}"; do
  skip=0
  for ex in "${EXCLUDES[@]}"; do
    case "$f" in
      $ex* ) skip=1; break;;
    esac
  done
  if [ $skip -eq 0 ]; then
    TO_DELETE+=("$f")
  fi
done

if [ ${#TO_DELETE[@]} -eq 0 ]; then
  echo "No deletable empty files found (excludes applied)."
  exit 0
fi

echo "Empty files found:" 
for f in "${TO_DELETE[@]}"; do
  echo "  $f"
done

if [ $FORCE -eq 1 ]; then
  echo "Deleting ${#TO_DELETE[@]} files..."
  for f in "${TO_DELETE[@]}"; do
    rm -f -- "$f" && echo "Deleted: $f"
  done
else
  echo "Dry run: set --force to actually delete these files."
fi

exit 0
