def three_chinese(line: str, target_vowel: str) -> str:
    to_replace = ['A', 'a', 'E', 'e', 'I', 'i', 'O', 'o', 'U', 'u', 'Ä', 'ä', 'Ö', 'ö', 'Ü', 'ü']
    result: str = line

    for char in to_replace:
        result = result.replace(char, '')

    result = result.replace(target_vowel + target_vowel, target_vowel)

    return result
