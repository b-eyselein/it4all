# noinspection PyUnresolvedReferences
from solution import Patient, Station, Krankenhaus

epsilon = 1e-3


def convert_input(input_json):
    stations = []

    # Instantiate all stations
    for (stations_index, alter_liste_station) in enumerate(input_json[0]):
        patients_on_station = []

        # Instantiate all patients on station
        for (patient_index, patient_alter) in enumerate(alter_liste_station):
            patient_id = "{}_{}".format(stations_index, patient_index)
            patients_on_station.append(Patient(id=patient_id, name='', alter=patient_alter, rezepte=[]))

        stations.append(Station(stations_index, doktoren=[], krankenschwestern=[], patienten=patients_on_station))

    # Instantiate hospital, return
    return Krankenhaus(stations)


def test(hospital, awaited_output):
    gotten_output = hospital.durchschnitt_alter_patienten()

    correctness = abs(gotten_output - awaited_output) < epsilon

    return gotten_output, correctness
