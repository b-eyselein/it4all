import os
import copy
from typing import List, Any


def is_number(s: Any) -> bool:
    # implementation hidden
    pass


class Table:
    def __init__(self, name: str):
        # implementation hidden
        pass

    def load_from_csv(self, csv_file, delimiter=';'):
        # implementation hidden
        pass

    def copy(self, original_table: 'Table'):
        # implementation hidden
        pass

    def length(self) -> int:
        # implementation hidden
        pass

    def insert(self, row) -> bool:
        # implementation hidden
        pass
