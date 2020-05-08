import unittest
from typing import List

from name_search import name_search


class NameSearchTest(unittest.TestCase):
    names: List[str] = [
        'anna', 'otto', 'jack', 'lena', 'tom', 'arthur', 'laura', 'james', 'lili', 'chris', 'neil', 'alex', 'michael',
        'john', 'helena', 'zoe', 'robert', 'lisa', 'joanne', 'julian', 'felix', 'sophie', 'lara', 'jonathan'
    ]

    def test_name_search(self):
        self.assertEqual([], name_search(self.names, 'jas'))
        self.assertEqual([('', 'a'), ('he', 'a')], name_search(self.names, 'len'))
        self.assertEqual([('', 'na'), ('jo', 'ne'), ('juli', ''), ('jonath', '')], name_search(self.names, 'an'))


if __name__ == '__main__':
    unittest.main()
