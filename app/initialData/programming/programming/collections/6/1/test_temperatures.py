import unittest

from temperatures import celsius_to_kelvin, celsius_to_fahrenheit, fahrenheit_to_celsius, fahrenheit_to_kelvin, kelvin_to_celsius, kelvin_to_fahrenheit


class TemperatureUnitsTest(unittest.TestCase):
    def test_celsius_to_kelvin(self):
        self.assertAlmostEqual(273.15, celsius_to_kelvin(0))
        self.assertAlmostEqual(100, celsius_to_kelvin(-173.15))
        self.assertAlmostEqual(310.83, celsius_to_kelvin(37.68))
        self.assertAlmostEqual(485.15, celsius_to_kelvin(212))

    def test_celsius_to_fahrenheit(self):
        self.assertAlmostEqual(32, celsius_to_fahrenheit(0))
        self.assertAlmostEqual(-279.67, celsius_to_fahrenheit(-173.15))
        self.assertAlmostEqual(99.824, celsius_to_fahrenheit(37.68))
        self.assertAlmostEqual(212, celsius_to_fahrenheit(100))

    def test_fahrenheit_to_celsius(self):
        self.assertAlmostEqual(0, fahrenheit_to_celsius(32))
        self.assertAlmostEqual(-173.15, fahrenheit_to_celsius(-279.67))
        self.assertAlmostEqual(37.68, fahrenheit_to_celsius(99.824))
        self.assertAlmostEqual(100, fahrenheit_to_celsius(212))

    def test_fahrenheit_to_kelvin(self):
        self.assertAlmostEqual(273.15, fahrenheit_to_kelvin(32))
        self.assertAlmostEqual(309.816_666_666, fahrenheit_to_kelvin(98))
        self.assertAlmostEqual(227.594_444_444, fahrenheit_to_kelvin(-50))
        self.assertAlmostEqual(373.15, fahrenheit_to_kelvin(212))

    def test_kelvin_to_celsius(self):
        self.assertAlmostEqual(-273.15, kelvin_to_celsius(0))
        self.assertAlmostEqual(-173.15, kelvin_to_celsius(100))
        self.assertAlmostEqual(37.68, kelvin_to_celsius(310.83))
        self.assertAlmostEqual(-61.15, kelvin_to_celsius(212))

    def test_kelvin_to_fahrenheit(self):
        self.assertAlmostEqual(32, kelvin_to_fahrenheit(273.15))
        self.assertAlmostEqual(96.53, kelvin_to_fahrenheit(309))
        self.assertAlmostEqual(-51.069_999_999, kelvin_to_fahrenheit(227))
        self.assertAlmostEqual(-78.07, kelvin_to_fahrenheit(212))


if __name__ == "__main__":
    unittest.main()  # pragma: no cover
