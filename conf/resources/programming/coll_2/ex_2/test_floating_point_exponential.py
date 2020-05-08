import unittest

from floating_point_exponential import format_floating_point_exponential


class FloatingPointExponentialTest(unittest.TestCase):
    def test_format_floating_point_exponential(self):
        self.assertEqual('1.0e0', format_floating_point_exponential(1))
        self.assertEqual('1.0e2', format_floating_point_exponential(100))
        self.assertEqual('7.753e1', format_floating_point_exponential(77.53))
        self.assertEqual('2.99792458e8', format_floating_point_exponential(299_792_458))
        self.assertEqual('3.141592653589793e0', format_floating_point_exponential(3.141592653589793))


if __name__ == '__main__':
    unittest.main()
