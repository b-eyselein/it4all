import unittest

from ceasar import CeasarCipher, crack_ceasar


class CeasarCipherTest(unittest.TestCase):
    ceasar_three = CeasarCipher(3)
    ceasar_seven = CeasarCipher(7)
    ceasar_symmetric = CeasarCipher(13)

    def test_crypt_decrypt_letter(self):
        self.assertEqual("d", self.ceasar_three.crypt_letter("a"))
        self.assertEqual("b", self.ceasar_three.crypt_letter("y"))
        self.assertEqual("o", self.ceasar_three.crypt_letter("l"))
        self.assertEqual("a", self.ceasar_three.decrypt_letter("d"))
        self.assertEqual("y", self.ceasar_three.decrypt_letter("b"))
        self.assertEqual("l", self.ceasar_three.decrypt_letter("o"))

        self.assertEqual("k", self.ceasar_seven.crypt_letter("d"))
        self.assertEqual("b", self.ceasar_seven.crypt_letter("u"))
        self.assertEqual("d", self.ceasar_seven.decrypt_letter("k"))
        self.assertEqual("u", self.ceasar_seven.decrypt_letter("b"))

        self.assertEqual("a", self.ceasar_symmetric.crypt_letter(self.ceasar_symmetric.crypt_letter("a")))
        self.assertEqual("o", self.ceasar_symmetric.crypt_letter(self.ceasar_symmetric.crypt_letter("o")))
        self.assertEqual("z", self.ceasar_symmetric.crypt_letter(self.ceasar_symmetric.crypt_letter("z")))

    def test_crypt_decrypt_word(self):
        self.assertEqual("whvw", self.ceasar_three.crypt_word("test"))
        self.assertEqual("sbwkrq", self.ceasar_three.crypt_word("python"))
        self.assertEqual("lqirkdi", self.ceasar_three.crypt_word("infohaf"))

        self.assertEqual("test", self.ceasar_three.decrypt_word("whvw"))
        self.assertEqual("python", self.ceasar_three.decrypt_word("sbwkrq"))
        self.assertEqual("infohaf", self.ceasar_three.decrypt_word("lqirkdi"))

        self.assertEqual("test", self.ceasar_symmetric.crypt_word(self.ceasar_symmetric.crypt_word("test")))
        self.assertEqual("python", self.ceasar_symmetric.crypt_word(self.ceasar_symmetric.crypt_word("python")))
        self.assertEqual("infohaf", self.ceasar_symmetric.crypt_word(self.ceasar_symmetric.crypt_word("infohaf")))

    def test_crypt_text(self):
        self.assertEqual("glhv lvw hlq whvw", self.ceasar_three.crypt_text("dies ist ein test"))
        self.assertEqual("erqjr erqjr erqjr", self.ceasar_three.crypt_text("bongo bongo bongo"))
        self.assertEqual("the ceasar ciphre with thirteen rounds is symmetric", self.ceasar_symmetric.crypt_text(
            self.ceasar_symmetric.crypt_text("the ceasar ciphre with thirteen rounds is symmetric")))

        self.assertEqual("kplz pza lpu alza tpa gdlp zhlaglu. hbjo kplzly zvssal mburapvuplylu",
                         self.ceasar_seven.crypt_text(
                             "dies ist ein test mit zwei saetzen. auch dieser sollte funktionieren"))

        self.assertEqual("dies ist ein test", self.ceasar_three.decrypt_text("glhv lvw hlq whvw"))
        self.assertEqual("bongo bongo bongo", self.ceasar_three.decrypt_text("erqjr erqjr erqjr"))
        self.assertEqual("the ceasar ciphre with thirteen rounds is symmetric", self.ceasar_symmetric.decrypt_text(
            self.ceasar_symmetric.decrypt_text("the ceasar ciphre with thirteen rounds is symmetric")))

        self.assertEqual("dies ist ein test mit zwei saetzen. auch dieser sollte funktionieren",
                         self.ceasar_seven.decrypt_text(
                             "kplz pza lpu alza tpa gdlp zhlaglu. hbjo kplzly zvssal mburapvuplylu"))

    def test_crack_ceasar(self):
        self.assertEqual(crack_ceasar("zpl ohilu klu jvkl nlruhjra"), "sie haben den code geknackt")
        self.assertEqual(crack_ceasar("glh fhdvdu fkliiuh lvw qlfkw zlunolfk vlfkhu"),
                         "die ceasar chiffre ist nicht wirklich sicher")
        self.assertEqual("qnf xanpxra qre prnfne puvsser vfg rvaqrhgvt mh rvasnpu",
                         self.ceasar_symmetric.crypt_text("das knacken der ceasar chiffre ist eindeutig zu einfach"))
        self.assertEqual(crack_ceasar("wnzrf obaq"), None)


if __name__ == "__main__":
    unittest.main()  # pragma: no cover
