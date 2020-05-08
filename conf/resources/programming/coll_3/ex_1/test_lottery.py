import unittest

from lottery import calculate_lottery_win


class LotteryTest(unittest.TestCase):
    def test_calculate_lottery_win(self):
        self.assertEqual(0, calculate_lottery_win(0, 0))
        self.assertEqual(0, calculate_lottery_win(1093, 0))

        self.assertEqual(0, calculate_lottery_win(0, -1))
        self.assertEqual(0, calculate_lottery_win(9348, 6))

        self.assertAlmostEqual(0.001, calculate_lottery_win(1, 1))
        self.assertAlmostEqual(0.5, calculate_lottery_win(500, 1))

        self.assertAlmostEqual(0.005, calculate_lottery_win(1, 2))
        self.assertAlmostEqual(2, calculate_lottery_win(400, 2))

        self.assertAlmostEqual(0.02, calculate_lottery_win(1, 3))
        self.assertAlmostEqual(11.46, calculate_lottery_win(573, 3))

        self.assertAlmostEqual(0.125, calculate_lottery_win(1, 4))
        self.assertAlmostEqual(97.125, calculate_lottery_win(777, 4))

        self.assertAlmostEqual(0.5, calculate_lottery_win(1, 5))
        self.assertAlmostEqual(666, calculate_lottery_win(1332, 5))


if __name__ == "__main__":
    unittest.main()  # pragma: no cover
