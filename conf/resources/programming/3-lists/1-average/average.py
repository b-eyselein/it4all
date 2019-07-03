from typing import List, Optional


def average(my_list: List[int]) -> Optional[float]:
    length = len(my_list)
    if length == 0:
        return None
    else:
        return sum(my_list) / length
