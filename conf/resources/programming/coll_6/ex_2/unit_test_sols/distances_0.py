def yards_to_meters(length_yards: float) -> float:
    return length_yards * 0.9144


def meters_to_yards(length_meters: float) -> float:
    return length_meters / 0.9144


def miles_to_meters(length_miles: float) -> float:
    return length_miles * 1609.344


def meters_to_miles(length_meters: float) -> float:
    return length_meters / 1609.344


def seamiles_to_meters(length_sea_miles: float) -> float:
    return length_sea_miles * 1852


def meters_to_seamiles(length_meters: float) -> float:
    return length_meters / 1852


def inches_to_meters(length_inches: float) -> float:
    return length_inches * 2.54 / 100


def meters_to_inches(length_meters: float) -> float:
    return length_meters / 2.54 * 100
