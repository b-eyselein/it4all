def celsius_to_fahrenheit(degrees_celsius: float) -> float:
    return (degrees_celsius * 1.8) + 32


def celsius_to_kelvin(degrees_celsius: float) -> float:
    return degrees_celsius + 273.15


def fahrenheit_to_celsius(degrees_fahrenheit: float) -> float:
    return (degrees_fahrenheit + 32) / 1.8


def fahrenheit_to_kelvin(degrees_fahrenheit: float) -> float:
    return (degrees_fahrenheit + 459.67) / 1.8


def kelvin_to_celsius(degrees_kelvin: float) -> float:
    return degrees_kelvin - 273.15


def kelvin_to_fahrenheit(degrees_kelvin: float) -> float:
    return (degrees_kelvin * 1.8) - 459.67
