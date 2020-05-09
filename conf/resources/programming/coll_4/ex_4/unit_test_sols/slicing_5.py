from typing import List


def even_indexes(my_list: List[int]) -> List[int]:
    return my_list[::2]


def reversed_special(my_list: List[int]) -> List[int]:
    return my_list[-2::-4]


def first_half(my_list: List[int]) -> List[int]:
    return my_list[:len(my_list) // 2]


def rotate_right(my_list: List[int], count: int) -> List[int]:
    return my_list[-count:] + my_list[:-count]
