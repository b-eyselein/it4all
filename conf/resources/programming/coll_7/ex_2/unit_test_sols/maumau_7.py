from typing import List, Any


class Card:
    def __init__(self, suit: int, rank: int):
        self.suit: int = suit
        self.rank: int = rank

    def __eq__(self, other: Any) -> bool:
        return isinstance(other, Card) and other.suit == self.suit and other.rank == self.rank  # pragma: no cover

    def __repr__(self) -> str:
        return "Card({}, {})".format(self.suit, self.rank)  # pragma: no cover


def can_be_played_on(first_card: Card, second_card: Card) -> bool:
    return first_card.suit == second_card.suit or first_card.rank == second_card.rank


def playable_cards(card: Card, hand: List[Card]) -> List[Card]:
    can_be_played: List[Card] = []

    for hand_card in hand:
        if not can_be_played_on(hand_card, card):
            can_be_played = [hand_card]

    return can_be_played
