def is_palindrome(word: str) -> bool:
    lower: str = word.lower()
    return lower == lower[::-1]
