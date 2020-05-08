from typing import Tuple


def file_name_and_ending(filename: str) -> Tuple[str, str]:
    contents = filename.split('.')
    if len(contents) == 1:  # Files without ending
        return contents[0], ''
    elif contents[0] == '':  # 'Hidden' files
        return contents[1], ''
    elif len(contents) == 2:  # 'Normal' files
        return contents[0], contents[1]
    else:  # Files with more than one ending
        return '.'.join(contents[:-1]), contents[-1]
