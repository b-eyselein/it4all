from typing import List, Any
# noinspection PyUnresolvedReferences
from solution import Patient, Station, Krankenhaus

epsilon = 1e-3


def convert_base_data(json_base_data):
    return None


def convert_test_input(base_data, input_json: List[List[int]]) -> Krankenhaus:
    stations: List[Station] = []

    # Instantiate all stations
    for (stations_index, alter_liste_station) in enumerate(input_json):
        patients_on_station = [Patient('', '', patient_alter) for patient_alter in alter_liste_station]
        stations.append(Station(stations_index, patienten=patients_on_station))

    # Instantiate hospital, return
    return Krankenhaus(stations)


def test(base_data, hospital: Krankenhaus, awaited_output: float) -> (Any, bool):
    gotten_output = hospital.durchschnitt_alter_patienten()

    correctness = abs(gotten_output - awaited_output) < epsilon

    return gotten_output, correctness
