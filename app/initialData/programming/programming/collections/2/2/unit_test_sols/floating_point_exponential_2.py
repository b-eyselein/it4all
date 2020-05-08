from math import log10


def format_floating_point_exponential(number: float) -> str:
    exponent: int = int(log10(number))
    new_num = str(number / (10 * exponent))
    return '{}e{}'.format(new_num, exponent)
