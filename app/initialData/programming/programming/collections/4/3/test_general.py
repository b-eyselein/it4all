import unittest

from general import filter_greater, count_lower, bank_card_security_value, vector_length, vector_add_scalar, vector_add_vector, flatten_lists


class ListFilterTest(unittest.TestCase):
    def test_filter_greater(self):
        self.assertEqual([], filter_greater([], 0))
        self.assertEqual([4, 5, 6], filter_greater([i for i in range(7)], 3))
        self.assertEqual([77, 81, 66], filter_greater([53, 77, 11, 81, 66, 9], 55))

    def test_count_lower(self):
        self.assertEqual(0, count_lower([], 0))
        self.assertEqual(3, count_lower([i for i in range(8)], 3))
        self.assertEqual(4, count_lower([53, 77, 11, 81, 66, 9], 67))

    def test_bank_card_check(self):
        self.assertEqual(0, bank_card_security_value([]))
        self.assertEqual(55, bank_card_security_value([1] * 10))
        self.assertEqual(330, bank_card_security_value([i for i in range(10)]))
        self.assertEqual(135, bank_card_security_value([5, 7, 3, 1, 4, 8, 5]))
        self.assertEqual(792, bank_card_security_value([8, 4, 1, 6, 5, 78, 2, 8, 2, 8, 4, 3]))

    def test_vector_length(self):
        self.assertEqual(0, vector_length([]))
        self.assertEqual(1, vector_length([1]))
        self.assertEqual(5, vector_length([4, 3]))
        self.assertAlmostEqual(8.774_964_387_392_123, vector_length([3, 8, -2]))

    def test_vector_add_scalar(self):
        self.assertEqual([], vector_add_scalar([], 0))
        self.assertEqual([2], vector_add_scalar([1], 1))
        self.assertEqual([3, 5, -2], vector_add_scalar([1, 3, -4], 2))

    def test_vector_add_vector(self):
        self.assertEqual([], vector_add_vector([], []))
        self.assertEqual([2], vector_add_vector([1], [1]))
        self.assertEqual([3, 5, -2], vector_add_vector([1, -2, 1], [2, 7, -3]))
        self.assertEqual([], vector_add_vector([1], []))

    def test_flatten_lists(self):
        self.assertEqual([], flatten_lists([]))
        self.assertEqual([1, 2, 3], flatten_lists([[1], [2], [3]]))
        self.assertEqual([1, 77, 81, 83, 5, 66, 33, 44], flatten_lists([[1, 77], [81], [83, 5, 66], [33, 44]]))


if __name__ == "__main__":
    unittest.main() # pragma: no cover
