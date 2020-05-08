def babylonian_root(number: float, count: int) -> float:
    if not isinstance(count, int):
        # raise Exception('The argument count must be an int!')
        count = 0

    if count < 0:
        raise Exception('The argument count must be greater or 0!')

    if not isinstance(number, int) and not isinstance(number, float):
        raise Exception('The argument number must be of the type int or float!')

    if number <= 0:
        raise Exception('The argument number has to be greater than 0!')

    sqr: float = number

    for _i in range(count):
        sqr = 1 / 2 * (sqr + (number / sqr))

    return sqr
