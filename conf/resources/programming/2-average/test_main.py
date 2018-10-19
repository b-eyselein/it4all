from typing import Any, List

# noinspection PyUnresolvedReferences
from solution import average

epsilon = 1e-3


def convert_base_data(json_base_data):
    return None


def convert_test_input(base_data, input_json: List[int]) -> List[int]:
    return input_json


def test(base_data, my_list: List[int], awaited_output: float) -> (Any, bool):
    gotten_output = average(my_list)

    if isinstance(awaited_output, str) or isinstance(gotten_output, str):
        correctness = gotten_output == awaited_output
    else:
        correctness = abs(gotten_output - awaited_output) < epsilon

    return gotten_output, correctness
