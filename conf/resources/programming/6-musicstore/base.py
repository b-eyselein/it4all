from datetime import date
from enum import Enum
from typing import List


class Customer:
    def __init__(self, number: int, first_name: str, family_name: str):
        self.number = number
        self.first_name = first_name
        self.family_name = family_name


class Genre(Enum):
    Rock = 1
    Pop = 2
    Metal = 3


class Artist:
    def __init__(self, id: int, pseudonym: str):
        self.id = id
        self.pseudonym = pseudonym


class Album:
    def __init__(self, id: int, title: str, artist: Artist):
        self.id = id
        self.title = title
        self.artist = artist


class Song:
    def __init__(self, id: int, title: str, length: int, genre: Genre, album: Album):
        self.id = id
        self.title = title
        self.length = length
        self.genre = genre
        self.album = album


class Purchase:
    def __init__(self, song: Song, customer: Customer, purchase_date: date):
        self.song = song
        self.customer = customer
        self.purchase_date = purchase_date

# {1}
class Recommender:
    def __init__(self, songs: List[Song], purchases: List[Purchase]):
        self.songs = songs
        self.purchases = purchases

    @staticmethod
    def similarity(song1: Song, song2: Song) -> int:
        score = 0
        if song1.genre == song2.genre:
            score += 10
        if song1.album == song2.album:
            score += 5
        if song1.album.artist == song2.album.artist:
            score += 3
        return score
# {2}
    def recommend(self, customer: Customer) -> Song:
        pass
# {12}