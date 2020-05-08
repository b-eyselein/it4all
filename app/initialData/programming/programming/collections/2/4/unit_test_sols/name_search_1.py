from typing import Tuple, List


def name_search(all_names: List[str], fragment: str) -> List[Tuple[str, str]]:
    result: List[Tuple[str, str]] = []

    for name in all_names:
        if fragment in name:
            index: int = name.index(fragment)
            result.append((name[:index - 1], name[index + len(fragment):]))

    return result
