#!/usr/bin/env python3

# Load learner solution
# noinspection PyUnresolvedReferences
from solution import *

# Load other libraries
import json
import sys
import re
import traceback

list_type_regex = "list<(.*?)>"

# Helper defs
def type_to_function(a_type):
    if a_type == "int":
        return lambda s: int(s)
    elif a_type == "boolean":
        return lambda s: s in ['true', '1', 't', 'y', 'yes']
    elif a_type == "float":
        return lambda s: float(s)
    elif a_type == "string":
        return lambda s: str(s)
    elif re.match(list_type_regex, a_type):
        sub_type_function = type_to_function(re.match(list_type_regex, a_type).group(1))
        return lambda s: list(map(sub_type_function, s))
    else:
        print("Matches: {}".format(re.match("list<(.*?>", a_type)))
        print("There has been an error!")
        return lambda s: str(s)


def map_variables(variable_types):
    current_vars = {}
    for index, variable_type in enumerate(variable_types):
        current_vars[chr(start_index + index)] = type_to_function(variable_type)
    return current_vars


def to_json(variable, variable_allocation):
    return {
        "variable": "{}".format(variable),
        "value": variable_allocation[ord(variable) - start_index]
    }


# Constants
start_index = ord("a")
result_filename = "result"
error_filename = "error"

# Main
if __name__ == "__main__":

    # Load testconfig.json
    testconfig_file = open("testconfig.json", 'r')
    testconfig = json.loads(testconfig_file.read())
    testconfig_file.close()

    functionname = testconfig['functionname']
    variableTypes = testconfig['variableTypes']
    outputTypeFunc = type_to_function(testconfig['outputType'])

    variables = map_variables(variableTypes)

    results = []

    for testdata in testconfig['testdata']:
        testdataId = int(testdata['id'])

        allocationArray = testdata['inputs']
        awaited = outputTypeFunc(testdata['awaited'])

        if len(variables) > len(allocationArray):
            print("Fehler bei Testdaten mit Id {}: Es gab {} Variablen und {} Testdaten".format(
                testdataId, len(variables), len(allocationArray)), file=sys.stderr)
            continue

        allocation = []
        for i in range(0, len(variables)):
            func = variables[chr(i + start_index)]
            allocation.append(func(allocationArray[i]))

        toWrite = {
            'id': testdataId,
            'inputs': list(map(lambda ip: to_json(ip, allocation), variables)),
            'functionName': functionname,
            'awaited': awaited
        }

        # Redirect print into output.txt
        newStdOut = open("output{}.txt".format(testdataId), 'w')
        sys.stdout = newStdOut

        try:
            result = getattr(sys.modules[__name__], functionname)(*allocation)
            toWrite['result'] = result
            toWrite['success'] = "COMPLETE" if result == awaited else "NONE"
        except Exception:
            toWrite['result'] = str(traceback.format_exc())
            toWrite['success'] = "ERROR"

        sys.stdout = sys.__stdout__
        newStdOut.close()

        results.append(toWrite)

    with open("{}.json".format(result_filename), 'w') as file:
        file.write(json.dumps(results, indent=2))
