import unittest

from average import average


class AverageTest(unittest.TestCase):
    def test_average(self):
        self.assertIsNone(average([]))

        self.assertAlmostEqual(1.0, average([1, 1, 1]))
        self.assertAlmostEqual(2.0, average([1, 2, 3]))
        self.assertAlmostEqual(0.0, average([-5, 2, -1, 4]))