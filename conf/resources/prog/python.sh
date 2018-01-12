#!/usr/bin/env bash

index=0

function delete {
    if [[ -f $1 ]]
    then
        rm $1
    fi
}

outputBase="output"
errorBase="error"
resultBase="result"

testdatafile=testdata.txt
solFilename=solution.py;

if [[ -f $solFilename ]] && [[ -f $testdatafile ]]
then
    while read testdata
    do

        outputFile=$outputBase$index.txt
        errorFile=$errorBase$index.txt
        resultFile=$resultBase$index.txt

        # delete old files
        delete $outputFile
        delete $errorFile
        delete $resultFile

        # if testdata not empty
        if [[ ! -z $testdata ]]
        then
            # execute script with testdata and record console output and errors
            echo $index $testdata | python $solFilename > $outputFile 2> $errorFile;
        fi

        # delete empty files
        [[ -s $outputFile ]] || delete $outputFile
        [[ -s $errorFile ]] || delete $errorFile
        [[ -s $resultFile ]] || delete $resultFile

        (( index++ ));

    done < $testdatafile
fi
