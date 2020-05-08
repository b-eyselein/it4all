import unittest
from typing import List

from slicing import even_indexes, reversed_special, first_half, rotate_right


class SlicingTest(unittest.TestCase):
    evens: List[int] = [2 * i for i in range(15)]
    un_evens: List[int] = [2 * i + 1 for i in range(15)]
    primes: List[int] = [2, 3, 5, 7, 11, 13, 17, 19, 23, 29]

    def test_even_indexes(self):
        self.assertEqual([0, 4, 8, 12, 16, 20, 24, 28], even_indexes(self.evens))
        self.assertEqual([1, 5, 9, 13, 17, 21, 25, 29], even_indexes(self.un_evens))
        self.assertEqual([2, 5, 11, 17, 23], even_indexes(self.primes))

    def test_reversed_special(self):
        self.assertEqual([26, 20, 14, 8, 2], reversed_special(self.evens))
        self.assertEqual([27, 21, 15, 9, 3], reversed_special(self.un_evens))
        self.assertEqual([23, 13, 5], reversed_special(self.primes))

    def test_first_half(self):
        self.assertEqual([0, 2, 4, 6, 8, 10, 12], first_half(self.evens))
        self.assertEqual([1, 3, 5, 7, 9, 11, 13], first_half(self.un_evens))
        self.assertEqual([2, 3, 5, 7, 11], first_half(self.primes))

    def test_rotate_right(self):
        self.assertEqual([24, 26, 28, 0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22], rotate_right(self.evens, 3))
        self.assertEqual([21, 23, 25, 27, 29, 1, 3, 5, 7, 9, 11, 13, 15, 17, 19], rotate_right(self.un_evens, 5))
        self.assertEqual([7, 11, 13, 17, 19, 23, 29, 2, 3, 5], rotate_right(self.primes, 7))


if __name__ == "__main__":
    unittest.main() # pragma: no cover
