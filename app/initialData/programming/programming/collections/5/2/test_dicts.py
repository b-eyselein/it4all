import unittest

from dicts import count_char_occurrences, word_position_list, merge_dicts_with_add


class DictTest(unittest.TestCase):
    def test_count_char_occurrences(self):
        self.assertEqual({'o': 2, 't': 2}, count_char_occurrences('otto'))
        self.assertEqual({'a': 2, 'n': 2}, count_char_occurrences('Anna'))
        self.assertEqual({'a': 1, 'c': 1, 'f': 3, 'h': 2, 'i': 1, 'r': 1, 's': 1, 't': 1},
                         count_char_occurrences('schifffahrt'))
        self.assertEqual({}, count_char_occurrences(''))

    def test_word_position_list(self):
        self.assertEqual({"dies": [0]}, word_position_list("dies"))
        self.assertEqual({"dies": [0], "ist": [1], "ein": [2], "test": [3]}, word_position_list("dies ist ein test"))
        self.assertEqual({"dies": [0, 4], "ist": [1, 5], "ein": [2, 6], "test": [3, 7]},
                         word_position_list("dies ist ein test dies ist ein test"))
        self.assertEqual(
            {'auch': [7], 'dies': [0, 5], 'ein': [2, 9], 'ist': [1, 6], 'noch': [8], 'test': [3, 10], 'und': [4]},
            word_position_list("dies ist ein test und dies ist auch noch ein test"))

    def test_merge_dicts_with_add(self):
        self.assertEqual({}, merge_dicts_with_add({}, {}))
        self.assertEqual({'o': 1}, merge_dicts_with_add({'o': 1}, {}))
        self.assertEqual({'o': 1}, merge_dicts_with_add({}, {'o': 1}))
        self.assertEqual({'o': 8, 't': 2, 's': 7}, merge_dicts_with_add({'o': 7, 't': 2, 's': 5}, {'o': 1, 's': 2}))


if __name__ == "__main__":
    unittest.main()  # pragma: no cover
