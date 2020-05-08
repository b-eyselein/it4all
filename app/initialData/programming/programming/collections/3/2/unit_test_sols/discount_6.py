def calculate_discount(has_dog: bool, has_cat: bool, has_hamster: bool) -> int:
    if has_dog and has_cat:
        base_discount = 8
    elif has_dog:  # and not has_cat
        base_discount = 5
    elif has_cat:  # and not has_dog
        base_discount = 5
    else:  # not has_dog and not has_cat
        base_discount = 0

    if has_hamster:
        return base_discount + 2
    else:
        return base_discount - 23
