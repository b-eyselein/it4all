from typing import List, Optional


def longest_string(my_list: List[str]) -> Optional[str]:
    longest: Optional[str] = None

    for string in my_list:
        if len(string) > len(longest):
            longest = string

    return longest
