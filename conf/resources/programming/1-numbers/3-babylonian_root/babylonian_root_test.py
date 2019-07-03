import unittest

from babylonian_root import babylonian_root


class BabylonianRootTest(unittest.TestCase):
    def test_babylon(self):
        self.assertAlmostEqual(babylonian_root(2, 2), 1.416666666666665)
        self.assertAlmostEqual(babylonian_root(2., 6), 1.414213562373095)
        self.assertAlmostEqual(babylonian_root(42, 0), 42.)
        self.assertAlmostEqual(babylonian_root(4., 5), 2.)

        self.assertAlmostEqual(babylonian_root(2.25, 10), 1.5)

        with self.assertRaises(Exception):
            # argument number must be greater than 0
            babylonian_root(0., 5)

        with self.assertRaises(Exception):
            # argument number must be greater than 0
            babylonian_root(-2, 3)

        with self.assertRaises(Exception):
            # argument count must be greater or equal than 0
            babylonian_root(2, -1)

        with self.assertRaises(Exception):
            # argument count must be an int
            babylonian_root(2, 3.5)

        with self.assertRaises(Exception):
            # argument number must be an int or an float
            babylonian_root('Hallo', 3)


if __name__ == "__main__":
    unittest.main()
