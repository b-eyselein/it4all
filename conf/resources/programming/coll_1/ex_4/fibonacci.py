def fibonacci(number: int) -> int:
    if not isinstance(number, int):
        raise Exception('Das Argument number muss eine Ganzzahl sein!')

    if number < 0:
        raise Exception('Das Argument number muss größer oder gleich 0 sein!')

    if number == 0 or number == 1:
        return 1

    return fibonacci(number - 1) + fibonacci(number - 2)
