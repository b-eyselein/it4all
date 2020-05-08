from typing import Tuple, List


def name_search(all_names: List[str], fragment: str) -> List[Tuple[str, str]]:
    result: List[Tuple[str, str]] = []
    temp: Tuple[str, str] = ('', '')

    for name in all_names:
        if fragment in name:
            index: int = name.index(fragment)
            temp = (name[:index], name[index + len(fragment):])

    result.append(temp)

    return result
