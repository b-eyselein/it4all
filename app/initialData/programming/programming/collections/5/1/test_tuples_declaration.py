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
        pass

    def test_account_value(self):
        pass

    def test_stock_value(self):
        pass


if __name__ == "__main__":
    unittest.main()  # pragma: no cover
