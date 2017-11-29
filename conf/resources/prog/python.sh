#!/usr/bin/env bash

index=0

while read testdata < testdata.txt; do

  # delete old result file if existing
  filename=res$index.txt;
  if [ -e $filename ]
  then
    rm $filename;
  fi

  # execute script with testdata
  solFilename=solution.py;
  if [ -e $solFilename ]
  then
    echo $testdata | python $solFilename > $filename;
  fi

  (( index++ ));
done
