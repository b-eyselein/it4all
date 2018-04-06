from datetime import date
from typing import Any

# noinspection PyUnresolvedReferences
from solution import Customer, Artist, Album, Song, Genre, Purchase, Recommender


def convert_base_data(json_base_data):
    gd = date(2017, 1, 1)

    print(json_base_data)

    customers = [Customer(c['number'], c['first_name'], c['family_name']) for c in json_base_data['customers']]

    artists = [Artist(a['number'], a['pseudonym']) for a in json_base_data['artists']]

    albums = [Album(a['number'], a['title'], artists[a['artist_id']]) for a in json_base_data['albums']]

    # TODO: genre!
    songs = [Song(s['number'], s['title'], s['length'], s['genre'], albums[s['album_id']]) for s in
             json_base_data['songs']]

    purchases = [Purchase(songs[x], customers[y], gd) for (x, y) in json_base_data['purchases']]

    return {'customers': customers, 'artists': artists, 'albums': albums, 'songs': songs, 'purchases': purchases}


def convert_test_input(base_data, test_input):
    return test_input


def test(base_data, test_input, awaited_output) -> (Any, bool):
    recommender = Recommender(base_data['songs'], base_data['purchases'])

    customer = base_data['customers'][test_input]
    recommended_song: Song = recommender.recommend(customer)

    correctness = recommended_song.id == awaited_output

    return recommended_song.id, correctness