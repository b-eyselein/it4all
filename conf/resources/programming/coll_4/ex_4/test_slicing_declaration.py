import unittest
from typing import List

from slicing import even_indexes, reversed_special, first_half, rotate_right


class SlicingTest(unittest.TestCase):
    evens: List[int] = [2 * i for i in range(15)]
    un_evens: List[int] = [2 * i + 1 for i in range(15)]
    primes: List[int] = [2, 3, 5, 7, 11, 13, 17, 19, 23, 29]

    def test_even_indexes(self):
        pass

    def test_reversed_special(self):
        pass

    def test_first_half(self):
        pass

    def test_rotate_right(self):
        pass


if __name__ == "__main__":
    unittest.main() # pragma: no cover
