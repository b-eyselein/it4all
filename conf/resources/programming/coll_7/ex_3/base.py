from typing import Any


class Vector2D:
    def __init__(self, x: float, y: float):
        pass

    def __repr__(self) -> str:
        pass

    def __eq__(self, other: Any) -> bool:
        pass

    def abs(self) -> float:
        pass

    def __add__(self, other: 'Vector2D') -> 'Vector2D':
        pass

    def __sub__(self, other: 'Vector2D') -> 'Vector2D':
        pass

    def __mul__(self, scalar: float) -> 'Vector2D':
        pass

    def dot(self, other: 'Vector2D') -> float:
        pass
