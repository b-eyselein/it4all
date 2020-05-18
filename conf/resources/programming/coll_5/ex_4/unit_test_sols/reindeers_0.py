from typing import Dict


def calculate_bmi(height_cm: int, weight_kg: int) -> float:
    return weight_kg / (height_cm / 100.0) ** 2


def calculate_reindeer_bmis(reindeers: Dict[str, Dict[str, int]]) -> Dict[str, float]:
    bmis: Dict[str, float] = {}

    for name, values in reindeers.items():
        bmis[name] = calculate_bmi(values["height_cm"], values["weight_kg"])

    return bmis
