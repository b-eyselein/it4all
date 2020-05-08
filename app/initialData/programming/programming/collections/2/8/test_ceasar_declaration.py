import unittest

from ceasar import CeasarCipher, crack_ceasar


class CeasarCipherTest(unittest.TestCase):
    ceasar_three = CeasarCipher(3)
    ceasar_seven = CeasarCipher(7)
    ceasar_symmetric = CeasarCipher(13)

    def test_crypt_decrypt_letter(self):
        pass

    def test_crypt_decrypt_word(self):
        pass

    def test_crypt_text(self):
        pass

    def test_crack_ceasar(self):
        pass


if __name__ == "__main__":
    unittest.main()  # pragma: no cover
