def is_palindrome(word: str) -> bool:
    # convert all to lowercase
    # lower: str = word.lower()

    # solution with slicing
    # return lower == lower[::-1]

    for c in range(len(lower) // 2):
        if lower[c] != lower[-c - 1]:
            return False

    return True
