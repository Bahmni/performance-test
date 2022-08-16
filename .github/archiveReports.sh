#!/bin/bash
set -e

cd reports
count=$(ls -d report* | wc -l)
if [ "$count" -gt 10 ]
then
  echo "deleting reports older than 10 occurrences $(ls -d report* | head -10)"
  ls -d report* | head -10 | xargs rm -rf
fi
