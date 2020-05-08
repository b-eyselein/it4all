from typing import Optional


class CeasarCipher:
    point_a = ord("a")

    def __init__(self, rounds: int):
        self.rounds: int = rounds

    def crypt_letter(self, lower_letter: str) -> str:
        pass

    def decrypt_letter(self, lower_letter: str) -> str:
        pass

    def crypt_word(self, lower_word: str) -> str:
        pass

    def decrypt_word(self, lower_word: str) -> str:
        pass

    def crypt_text(self, text_lower: str) -> str:
        pass

    def decrypt_text(self, text_lower: str) -> str:
        pass


word_list = ["ich", "du", "er", "sie", "es", "wir", "ihr", "sie", "der", "die", "das"]


def crack_ceasar(encrypted_text: str) -> Optional[str]:
    pass
