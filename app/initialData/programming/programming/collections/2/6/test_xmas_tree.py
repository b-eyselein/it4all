import unittest

easy_tree: str = """\
#############
#     *     #
#    ***    #
#   *****   #
#  *******  #
# ********* #
#     I     #
#     I     #
#############"""

hard_tree: str = """\
#############
#     *     #
#    *o*    #
#   *J*J*   #
#  *o*o*o*  #
# *J*J*J*J* #
#     I     #
#     I     #
#############"""

from xmas_tree import xmas_tree_top_simple, xmas_tree_top_design, xmas_tree_stub, xmas_tree_simple, xmas_tree_design


class XmasTreeTest(unittest.TestCase):
    def test_xmas_tree_top_simple(self):
        self.assertEqual('#   *   #', xmas_tree_top_simple(0, 3))
        self.assertEqual('#  ***  #', xmas_tree_top_simple(1, 3))
        self.assertEqual('# ***** #', xmas_tree_top_simple(2, 3))

        self.assertEqual('#    *    #', xmas_tree_top_simple(0, 4))
        self.assertEqual('#   ***   #', xmas_tree_top_simple(1, 4))
        self.assertEqual('#  *****  #', xmas_tree_top_simple(2, 4))
        self.assertEqual('# ******* #', xmas_tree_top_simple(3, 4))

        self.assertEqual('#     *     #', xmas_tree_top_simple(0, 5))
        self.assertEqual('#    ***    #', xmas_tree_top_simple(1, 5))
        self.assertEqual('#   *****   #', xmas_tree_top_simple(2, 5))
        self.assertEqual('#  *******  #', xmas_tree_top_simple(3, 5))
        self.assertEqual('# ********* #', xmas_tree_top_simple(4, 5))

    def test_xml_tree_stop_design(self):
        self.assertEqual('#   *   #', xmas_tree_top_design(0, 3))
        self.assertEqual('#  *o*  #', xmas_tree_top_design(1, 3))
        self.assertEqual('# *J*J* #', xmas_tree_top_design(2, 3))

        self.assertEqual('#    *    #', xmas_tree_top_design(0, 4))
        self.assertEqual('#   *o*   #', xmas_tree_top_design(1, 4))
        self.assertEqual('#  *J*J*  #', xmas_tree_top_design(2, 4))
        self.assertEqual('# *o*o*o* #', xmas_tree_top_design(3, 4))

        self.assertEqual('#     *     #', xmas_tree_top_design(0, 5))
        self.assertEqual('#    *o*    #', xmas_tree_top_design(1, 5))
        self.assertEqual('#   *J*J*   #', xmas_tree_top_design(2, 5))
        self.assertEqual('#  *o*o*o*  #', xmas_tree_top_design(3, 5))
        self.assertEqual('# *J*J*J*J* #', xmas_tree_top_design(4, 5))

    def test_xml_tree_stub(self):
        self.assertEqual('#   I   #', xmas_tree_stub(3))
        self.assertEqual('#    I    #', xmas_tree_stub(4))
        self.assertEqual('#     I     #', xmas_tree_stub(5))

    def test_xmas_tree_simple(self):
        self.assertEqual("""\
#######
#  *  #
# *** #
#  I  #
#######""", xmas_tree_simple(2, 1))
        self.assertEqual(easy_tree, xmas_tree_simple(5, 2))

    def test_xmas_tree_design(self):
        self.assertEqual(hard_tree, xmas_tree_design(5, 2))


if __name__ == "__main__":
    unittest.main()
