# noinspection PyUnresolvedReferences
from solution import is_palindrome

def convert_input(input_json):
    return input_json[0]

def test(my_list, awaited_output):
    gotten_output = is_palindrome(my_list)
    return gotten_output, gotten_output == awaited_output