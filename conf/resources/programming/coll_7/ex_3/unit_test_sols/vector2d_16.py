from typing import Any


class Vector2D:
    def __init__(self, x: float, y: float):
        self.x: float = x
        self.y: float = y

    def __repr__(self) -> str:
        return "({}, {})".format(self.x, self.y)

    def __eq__(self, other: Any) -> bool:
        return isinstance(other, Vector2D) and self.x == other.x and self.y == other.y

    def abs(self) -> float:
        return (self.x ** 2 + self.y ** 2) ** 0.5

    def __add__(self, other: 'Vector2D') -> 'Vector2D':
        return Vector2D(self.x + other.x, self.y + other.y)

    def __sub__(self, other: 'Vector2D') -> 'Vector2D':
        return Vector2D(self.x - other.x, self.y - other.y)

    def __mul__(self, scalar: float) -> 'Vector2D':
        return Vector2D(self.x * scalar, self.y * scalar)

    def dot(self, other: 'Vector2D') -> float:
        return self.x * other.y + self.y * other.x
