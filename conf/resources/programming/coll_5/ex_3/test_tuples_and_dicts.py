import unittest

from tuples_and_dicts import tuple_list_to_dict, intersect_dicts


class TupleDictTest(unittest.TestCase):
    def test_tuple_list_to_dict(self):
        self.assertEqual({}, tuple_list_to_dict([]))
        self.assertEqual({'o': 2, 't': 5}, tuple_list_to_dict([('o', 2), ('t', 5)]))
        self.assertEqual({'o': 2, 't': 5}, tuple_list_to_dict([('o', 2), ('t', 5), ('o', 7)]))

    def test_intersect_dicts(self):
        self.assertEqual({}, intersect_dicts({}, {}))
        self.assertEqual({'o': (1, 2)}, intersect_dicts({'o': 1}, {'o': 2, 't': 1}))
        self.assertEqual({}, intersect_dicts({'o': 1, 'p': 2}, {'s': 2, 't': 1}))
        self.assertEqual({'p': (2, 3), 's': (5, 2)},
                         intersect_dicts({'o': 1, 'p': 2, 's': 5}, {'s': 2, 'p': 3, 't': 1}))


if __name__ == "__main__":
    unittest.main()  # pragma: no cover
