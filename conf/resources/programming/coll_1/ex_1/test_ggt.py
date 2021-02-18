import unittest
from ggt import ggt

class GgtTest(unittest.TestCase):
    def test_ggt(self):
        self.assertEqual(4, ggt(12, 4))
        self.assertEqual(1, ggt(3, 7))
        self.assertEqual(2, ggt(46, 64))
        self.assertEqual(111, ggt(777, 111))
        self.assertEqual(5, ggt(15, 25))
        self.assertEqual(30, ggt(30, 60))


if __name__ == "__main__":
    unittest.main()
