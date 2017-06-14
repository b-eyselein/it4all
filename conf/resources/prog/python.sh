#!/usr/bin/env bash

index=0

cat testdata.txt |
while read testdata; do
	
  # delete old result file if existing
  filename=res$index.txt;
  if [ -e $filename ]
  then
	rm $filename;
  fi
 
  # execute script with testdata
  echo $testdata | python sol.py > $filename;
  
  (( index++ ));
done
