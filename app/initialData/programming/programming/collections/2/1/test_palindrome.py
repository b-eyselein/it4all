import unittest
from palindrome import is_palindrome


class PalindromeTest(unittest.TestCase):
    def test_is_palindrome(self):
        self.assertTrue(is_palindrome(''))

        self.assertTrue(is_palindrome('otto'))
        self.assertTrue(is_palindrome('lagerregal'))

        self.assertTrue(is_palindrome('eke'))
        self.assertTrue(is_palindrome('TaCoCaT'))
        self.assertTrue(is_palindrome('AnNa'))

        self.assertFalse(is_palindrome('toto'))
        self.assertFalse(is_palindrome('master'))


if __name__ == "__main__":
    unittest.main()
