import unittest

from vector2d import Vector2D


class VectorTest(unittest.TestCase):
    def test_init(self):
        vec1: Vector2D = Vector2D(1, 3)
        self.assertEqual(1, vec1.x)
        self.assertEqual(3, vec1.y)

        vec2: Vector2D = Vector2D(5, -7)
        self.assertEqual(5, vec2.x)
        self.assertEqual(-7, vec2.y)

    def test_repr(self):
        self.assertEqual('(1, 2)', repr(Vector2D(1, 2)))
        self.assertEqual('(13, 37)', repr(Vector2D(13, 37)))
        self.assertEqual('(-51, -27)', repr(Vector2D(-51, -27)))

    def test_equal(self):
        self.assertEqual(Vector2D(1, 1), Vector2D(1, 1))
        self.assertEqual(Vector2D(13, 37), Vector2D(13, 37))

        self.assertNotEqual(Vector2D(1, 1), Vector2D(2, 2))
        self.assertNotEqual(Vector2D(37, 8), Vector2D(8, 37))

        class VectorNot2D:
            def __init__(self, x: float, y: float):
                self.x: float = x
                self.y: float = y

        self.assertNotEqual(Vector2D(1, 1), VectorNot2D(1, 1))

    def test_abs(self):
        self.assertEqual(1.4142135623730951, Vector2D(1, 1).abs())
        self.assertEqual(5, Vector2D(3, 4).abs())
        self.assertEqual(5.0990195135927845, Vector2D(-1, 5).abs())
        self.assertEqual(3.605551275463989, Vector2D(-2, -3).abs())

    def test_add(self):
        self.assertEqual(Vector2D(2, 2), Vector2D(1, 1) + Vector2D(1, 1))
        self.assertEqual(Vector2D(11, 37), Vector2D(-21, 10) + Vector2D(32, 27))
        self.assertEqual(Vector2D(18, 5), Vector2D(-37, 48) + Vector2D(55, -43))

    def test_sub(self):
        self.assertEqual(Vector2D(1, 1), Vector2D(3, 5) - Vector2D(2, 4))
        self.assertEqual(Vector2D(-53, -17), Vector2D(-21, 10) - Vector2D(32, 27))
        self.assertEqual(Vector2D(18, 91), Vector2D(-37, 48) - Vector2D(-55, -43))

    def test_mul(self):
        self.assertEqual(Vector2D(2, 2), Vector2D(1, 1) * 2)
        self.assertEqual(Vector2D(3, -5), Vector2D(-3, 5) * -1)
        self.assertEqual(Vector2D(2.5, -77), Vector2D(5, -154) * 0.5)

    def test_dot(self):
        self.assertEqual(4, Vector2D(1, 2).dot(Vector2D(2, 1)))
        self.assertEqual(24, Vector2D(1, 3).dot(Vector2D(3, 7)))
        self.assertEqual(2747, Vector2D(17, 23).dot(Vector2D(52, 81)))


if __name__ == "__main__":
    unittest.main()  # pragma: no cover
