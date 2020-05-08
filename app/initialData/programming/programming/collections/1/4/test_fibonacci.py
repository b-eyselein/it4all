import unittest
from fibonacci import fibonacci


class FibonacciTest(unittest.TestCase):
    def test_fibonacci(self):
        self.assertEqual(1, fibonacci(0))
        self.assertEqual(1, fibonacci(1))

        self.assertEqual(8, fibonacci(5))

        with self.assertRaises(Exception):
            fibonacci('str')

        with self.assertRaises(Exception):
            fibonacci(-1)


if __name__ == "__main__":
    unittest.main()
