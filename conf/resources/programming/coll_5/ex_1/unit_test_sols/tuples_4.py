from typing import Tuple, List


def min_max(my_list: List[int]) -> Tuple[int, int]:
    list_min = my_list[0]
    list_max = my_list[0]

    for x in my_list:
        if x < list_min:
            list_min = x
        elif x > list_max:
            list_max = x

    return list_min, list_max


def account_value(stocks: List[Tuple[str, int]]) -> float:
    price_sum = 0.0
    for name, price in stocks:
        price_sum += price

    return price_sum


def stock_value(stocks: List[Tuple[str, int]], name: str) -> int:
    for stock_name, price in stocks:
        if stock_name == name:
            return price
    return -1
