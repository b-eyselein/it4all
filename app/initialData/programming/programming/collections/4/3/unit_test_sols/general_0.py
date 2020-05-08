from typing import List


def filter_greater(vector: List[int], x: int) -> List[int]:
    # return [y for y in vector if y > x]

    filtered: List[int] = []

    for element in vector:
        if element > x:
            filtered.append(element)

    return filtered


def count_lower(vector: List[int], x: int) -> int:
    lower: int = 0

    for element in vector:
        if element < x:
            lower += 1

    return lower


def bank_card_security_value(digits: List[int]) -> int:
    security_value: int = 0

    for (index, element) in enumerate(digits):
        security_value += (index + 1) * element

    return security_value


def vector_length(vector: List[int]) -> float:
    squared_vec_sum = 0

    for element in vector:
        squared_vec_sum += element * element

    return squared_vec_sum ** .5


def vector_add_scalar(vector: List[int], scalar: int) -> List[int]:
    # return [x + scalar for x in vector]

    new_vec: List[int] = []

    for element in vector:
        new_vec.append(element + scalar)

    return new_vec


def vector_add_vector(vector1: List[int], vector2: List[int]) -> List[int]:
    if len(vector1) != len(vector2):
        return []

    # return [vector1[i] + vector2[i] for i in range(len(vector1))]

    vector3: List[int] = []

    for i in range(len(vector1)):
        vector3.append(vector1[i] + vector2[i])

    return vector3


def flatten_lists(list_of_lists: List[List[int]]) -> List[int]:
    flat_list: List[int] = []

    for inner_list in list_of_lists:
        # Füge komplette Liste hinzu
        flat_list += inner_list

        # Oder: Füge jedes Element einzeln hinzu
        # for element in inner_list:
        #     flat_list.append(element)

    return flat_list
