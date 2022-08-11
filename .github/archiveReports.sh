#!/bin/bash
set -e

cd reports
count=$(ls -d report* | wc -l)
if [ "$count" -gt 5 ]
then
  echo "deleting reports older than 5 occurrences $(ls -d report* | head -5)"
  ls -d report* | head -5 | xargs rm -rf
fi