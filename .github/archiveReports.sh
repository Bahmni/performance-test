#!/bin/bash
set -e

cd reports
count=$(ls -d report* | wc -l)
if [ "$count" -gt 20 ]
then
  echo "deleting reports older than 5 occurrences $(ls -d report* | head -20)"
  ls -d report* | head -20 | xargs rm -rf
fi