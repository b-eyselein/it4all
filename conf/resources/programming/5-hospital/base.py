from typing import List


class Patient:
    def __init__(self, id: str, name: str, alter: int):
        self.id = id
        self.name = name
        self.alter = alter


class Station:
    def __init__(self, nummer: int, patienten: List[Patient]):
        self.nummer = nummer
        self.patienten = patienten

# {1}
class Krankenhaus:
    def __init__(self, stationen: List[Station]):
        self.stationen = stationen

# {2}
    def durchschnitt_alter_patienten(self):
        return -1
# {12}