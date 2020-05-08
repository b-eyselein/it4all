import unittest

from a1z26 import encrypt_letter, decrypt_letter, encrypt_word, decrypt_word


class A1Z26Test(unittest.TestCase):
    def test_encrypt_letter(self):
        self.assertEqual(1, encrypt_letter('a'))
        self.assertEqual(10, encrypt_letter('j'))
        self.assertEqual(25, encrypt_letter('y'))

    def test_decrypt_letter(self):
        self.assertEqual('o', decrypt_letter(15))
        self.assertEqual('t', decrypt_letter(20))
        self.assertEqual('b', decrypt_letter(2))

    def test_encrypt_word(self):
        self.assertEqual([], encrypt_word(''))
        self.assertEqual([20, 5, 19, 20], encrypt_word('test'))
        self.assertEqual([3, 9, 16, 8, 5, 18], encrypt_word('cipher'))

    def test_decrypt_word(self):
        self.assertEqual('', decrypt_word([]))
        self.assertEqual('test', decrypt_word([20, 5, 19, 20]))
        self.assertEqual('cipher', decrypt_word([3, 9, 16, 8, 5, 18]))


if __name__ == "__main__":
    unittest.main()
