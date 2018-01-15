#!/usr/bin/env python3

from typing import List

# Load learner solution
from solution import *

# Load other libraries
import json, sys, traceback


# Helper defs
def typeToFunction(aType):
    if aType == "int":
        return lambda s: int(s)
    elif aType == "boolean":
        return lambda s: s in ['true', '1', 't', 'y', 'yes']
    elif aType == "float":
        return lambda s: float(s)
    elif aType == "string":
        return lambda s: str(s)
    else:
        return lambda s: str(s)


def mapVariables(variableTypes: List[str]) -> List[str]:
    variables = {}
    for index, variableType in enumerate(variableTypes):
        variables[chr(startIndex + index)] = typeToFunction(variableType)
    return variables


def toJson(variable, allocation):
    return {
        "variable": "{}".format(variable),
        "value": allocation[ord(variable) - startIndex]
    }


# Constants
startIndex = ord("a")
resultFilename = "result"
errorFilename = "error"

# Main
if __name__ == "__main__":

    # Load testconfig.json
    testconfigFile = open("testconfig.json", 'r')
    testconfig = json.loads(testconfigFile.read())
    testconfigFile.close()

    functionname = testconfig['functionname']
    variableTypes = testconfig['variableTypes']
    outputTypeFunc = typeToFunction(testconfig['outputType'])

    variables = mapVariables(variableTypes)

    results = []

    for testdata in testconfig['testdata']:
        id = int(testdata['id'])

        allocationArray = testdata['inputs']
        awaited = outputTypeFunc(testdata['awaited'])

        if len(variables) > len(allocationArray):
            print("Fehler bei Testdaten mit Id {}: Es gab {} Variablen und {} Testdaten".format(id, len(variables), len(allocationArray)), file = sys.stderr)
            continue

        allocation = []
        for i in range(0, len(variables)):
            func = variables[chr(i + startIndex)]
            allocation.append(func(allocationArray[i]))

        toWrite = {
            'id': id,
            'inputs': list(map(lambda ip: toJson(ip, allocation), variables)),
            'functionName': functionname,
            'awaited': awaited
        }

        # Redirect print into output.txt
        newStdOut = open("output{}.txt".format(id), 'w')
        sys.stdout = newStdOut

        try:
            result = getattr(sys.modules[__name__], functionname)(*allocation)
            toWrite['result'] = result
            toWrite['success'] = "COMPLETE" if result == awaited else "NONE"
        except Exception as err:
            toWrite['result'] = str(err)
            toWrite['success'] = "ERROR"

        sys.stdout = sys.__stdout__
        newStdOut.close()

        results.append(toWrite)

    with open("{}.json".format(resultFilename), 'w') as file:
        file.write(json.dumps(results, indent=2))
