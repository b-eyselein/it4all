import unittest
from typing import List, Tuple

from tuples import min_max, account_value, stock_value


class TupleTest(unittest.TestCase):
    stock_values_1: List[Tuple[str, int]] = [
        ("iag", 100_00), ("dag", 120_00)
    ]
    stock_values_2: List[Tuple[str, int]] = [
        ("eag", 87_99), ("sag", 117_52)
    ]

    def test_min_max(self):
        self.assertEqual((5, 5), min_max([5]))
        self.assertEqual((5, 7), min_max([5, 6, 7]))
        self.assertEqual((5, 7), min_max([7, 6, 5]))

    def test_account_value(self):
        self.assertAlmostEqual(0.0, account_value([]))
        self.assertAlmostEqual(220.0, account_value(self.stock_values_1))
        self.assertAlmostEqual(205.51, account_value(self.stock_values_2))
        self.assertAlmostEqual(425.51, account_value(self.stock_values_1 + self.stock_values_2))

    def test_stock_value(self):
        self.assertEqual(100_00, stock_value(self.stock_values_1, "iag"))
        self.assertEqual(120_00, stock_value(self.stock_values_1, "dag"))
        self.assertEqual(-1, stock_value(self.stock_values_1, "eag"))

        self.assertEqual(87_99, stock_value(self.stock_values_2, "eag"))
        self.assertEqual(117_52, stock_value(self.stock_values_2, "sag"))
        self.assertEqual(-1, stock_value(self.stock_values_2, "iag"))

        self.assertEqual(-1, stock_value([], ""))


if __name__ == "__main__":
    unittest.main()  # pragma: no cover
