import unittest

from discount import calculate_discount


class DiscountTest(unittest.TestCase):
    def test_calculate_discount(self):
        self.assertEqual(0, calculate_discount(False, False, False))
        self.assertEqual(2, calculate_discount(False, False, True))
        self.assertEqual(5, calculate_discount(False, True, False))
        self.assertEqual(7, calculate_discount(False, True, True))

        self.assertEqual(5, calculate_discount(True, False, False))
        self.assertEqual(7, calculate_discount(True, False, True))
        self.assertEqual(8, calculate_discount(True, True, False))
        self.assertEqual(10, calculate_discount(True, True, True))


if __name__ == "__main__":
    unittest.main()  # pragma: no cover
