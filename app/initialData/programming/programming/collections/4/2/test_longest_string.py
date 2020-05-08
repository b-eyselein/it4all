import unittest

from longest_string import longest_string


class LongestStringTest(unittest.TestCase):
    def test_longest_string(self):
        self.assertIsNone(longest_string([]))

        self.assertEqual('str', longest_string(['str']))
        self.assertEqual('1', longest_string(['', '1', '']))

        self.assertEqual('121', longest_string(['0', '1', '121', '12', '21', '01', '2']))
