import unittest

from greet import greet


class GreetTest(unittest.TestCase):
    def test_greet(self):
        self.assertEqual("Good night", greet(3))
        self.assertEqual("Good morning", greet(6))
        self.assertEqual("Good afternoon", greet(13))
        self.assertEqual("Good evening", greet(19))
        self.assertEqual("Good night", greet(23))
        self.assertEqual("I do not know this time.", greet(-1))
        self.assertEqual("I do not know this time.", greet(25))


if __name__ == "__main__":
    unittest.main()  # pragma: no cover
