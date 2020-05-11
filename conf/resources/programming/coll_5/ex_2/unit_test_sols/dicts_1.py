from typing import Dict, List


def count_char_occurrences(my_str: str) -> Dict[str, int]:
    result: Dict[str, int] = {}

    for char in my_str:
        if char in result:
            result[char] += 1
        else:
            result[char] = 1

    return result


def word_position_list(my_str: str) -> Dict[str, List[int]]:
    result: Dict[str, List[int]] = {}

    for index, word in enumerate(my_str.split(' ')):
        if word in result:
            result[word].append(index)
        else:
            result[word] = [index]

    return result


def merge_dicts_with_add(dict1: Dict[str, int], dict2: Dict[str, int]) -> Dict[str, int]:
    result: Dict[str, int] = {}

    for key, value in dict1.items():
        result[key] = value

    for key, value in dict2.items():
        if key in result:
            result[key] += value
        else:
            result[key] = value

    return result
