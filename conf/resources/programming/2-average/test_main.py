from typing import Any

# noinspection PyUnresolvedReferences
from solution import average

epsilon = 1e-3


def convert_input(input_json):
    return input_json[0]


def test(my_list, awaited_output) -> (Any, bool):
    gotten_output = average(my_list)

    if isinstance(awaited_output, str) or isinstance(gotten_output, str):
        correctness = gotten_output == awaited_output
    else:
        correctness = abs(gotten_output - awaited_output) < epsilon

    return gotten_output, correctness