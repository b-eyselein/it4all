# noinspection PyUnresolvedReferences
from solution import ggt


def convert_base_data(json_base_data):
    return None


def convert_test_input(base_data, input_json):
    return input_json


def test(base_data, test_input, awaited_output):
    gotten_output = ggt(test_input[0], test_input[1])

    return gotten_output, gotten_output == awaited_output