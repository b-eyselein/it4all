from typing import List

ord_a: int = ord('a')


def encrypt_letter(letter: str) -> int:
    return ord(letter) - ord_a + 1


def decrypt_letter(letter: int) -> str:
    return chr(ord_a + letter - 1)


def encrypt_word(word: str) -> List[int]:
    result: List[int] = []
    temp: int = 0

    for letter in word:
        temp = encrypt_letter(letter)

    result.append(temp)

    return result


def decrypt_word(word: List[int]) -> str:
    result: str = ''

    for letter in word:
        result += decrypt_letter(letter)

    return result
