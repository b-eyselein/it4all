from typing import Dict, List, Tuple


def tuple_list_to_dict(my_list: List[Tuple[str, int]]) -> Dict[str, int]:
    result: Dict[str, int] = {}

    for key, value in my_list:
        if key not in result:
            result[key] = value

    return result


def intersect_dicts(dict1: Dict[str, int], dict2: Dict[str, int]) -> Dict[str, Tuple[int, int]]:
    result: Dict[str, Tuple[int, int]] = {}

    for key, value in dict1.items():
        if key in dict2:
            result[key] = (value, dict2[key])
        else:
            result[key] = (value, 0)

    for key, value in dict2.items():
        if key not in result:
            result[key] = (0, value)

    return result