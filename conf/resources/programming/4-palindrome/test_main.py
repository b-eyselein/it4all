# noinspection PyUnresolvedReferences
from solution import is_palindrome


def convert_base_data(json_base_data):
    return None


def convert_test_input(base_data, input_json):
    return input_json


def test(base_data, my_list, awaited_output):
    gotten_output = is_palindrome(my_list)
    return gotten_output, gotten_output == awaited_output