from typing import List, Optional


def average(my_list: List[int]) -> Optional[float]:
    length: int = len(my_list)

    if length == 0:
        return None

    element_sum: int = 0
    for element in my_list:
        element_sum += element

    return element_sum
