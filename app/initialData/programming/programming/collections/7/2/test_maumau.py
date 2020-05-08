import unittest

from maumau import Card, can_be_played_on, playable_cards


class MaumauTest(unittest.TestCase):
    def test_can_be_played(self):
        self.assertTrue(can_be_played_on(Card(1, 2), (Card(1, 2))))
        self.assertTrue(can_be_played_on(Card(3, 9), (Card(3, 11))))
        self.assertTrue(can_be_played_on(Card(1, 4), (Card(3, 4))))

        self.assertFalse(can_be_played_on(Card(1, 3), (Card(4, 4))))
        self.assertFalse(can_be_played_on(Card(3, 3), (Card(2, 7))))
        self.assertFalse(can_be_played_on(Card(1, 13), (Card(2, 4))))

    def test_playable_cards(self):
        self.assertEqual([], playable_cards(Card(1, 2), []))
        self.assertEqual(
            [Card(3, 12), Card(1, 13)], playable_cards(Card(3, 13), [Card(3, 12), Card(1, 13)])
        )
        self.assertEqual(
            [Card(2, 7), Card(3, 9)],
            playable_cards(Card(2, 9), [Card(3, 2), Card(1, 5), Card(2, 7), Card(1, 14), Card(3, 9)])
        )


if __name__ == "__main__":
    unittest.main()  # pragma: no cover
