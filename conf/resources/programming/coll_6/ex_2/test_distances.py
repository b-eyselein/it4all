import unittest

from distances import yards_to_meters, meters_to_yards, miles_to_meters, meters_to_miles, seamiles_to_meters, \
    meters_to_seamiles, inches_to_meters, meters_to_inches


class DistancesTest(unittest.TestCase):
    def test_yards_to_meters(self):
        self.assertAlmostEqual(.9144, yards_to_meters(1))
        self.assertAlmostEqual(2.7432, yards_to_meters(3))
        self.assertAlmostEqual(1.1283696, yards_to_meters(1.234))
        self.assertAlmostEqual(57, yards_to_meters(62.335958005249346))

    def test_meters_to_yards(self):
        self.assertAlmostEqual(1, meters_to_yards(0.9144))
        self.assertAlmostEqual(1.0936132983377078, meters_to_yards(1))
        self.assertAlmostEqual(62.335958005249346, meters_to_yards(57))
        self.assertAlmostEqual(3, meters_to_yards(2.7432))

    def test_miles_to_meters(self):
        self.assertAlmostEqual(1609.344, miles_to_meters(1))
        self.assertAlmostEqual(4023.36, miles_to_meters(2.5))
        self.assertAlmostEqual(497.287296, miles_to_meters(0.309))
        self.assertAlmostEqual(100, miles_to_meters(0.06213711922373339))

    def test_meters_to_miles(self):
        self.assertAlmostEqual(0.006213711922373339, meters_to_miles(10))
        self.assertAlmostEqual(0.24792710570269624, meters_to_miles(399))
        self.assertAlmostEqual(2, meters_to_miles(3218.688))
        self.assertAlmostEqual(1.242742384474668, meters_to_miles(2000))

    def test_sea_miles_to_meters(self):
        self.assertAlmostEqual(1852, seamiles_to_meters(1))
        self.assertAlmostEqual(4444.8, seamiles_to_meters(2.4))
        self.assertAlmostEqual(3463.2400000000002, seamiles_to_meters(1.87))

    def test_meters_to_sea_miles(self):
        self.assertAlmostEqual(1, meters_to_seamiles(1852))
        self.assertAlmostEqual(2.4, meters_to_seamiles(4444.8))
        self.assertAlmostEqual(0.3596112311015119, meters_to_seamiles(666))

    def test_inches_to_meters(self):
        self.assertAlmostEqual(0.0254, inches_to_meters(1))
        self.assertAlmostEqual(16.9164, inches_to_meters(666))
        self.assertAlmostEqual(1.9558000000000002, inches_to_meters(77))

    def test_meters_to_inches(self):
        self.assertAlmostEqual(39.370078740157474, meters_to_inches(1))
        self.assertAlmostEqual(100, meters_to_inches(2.54))
        self.assertAlmostEqual(21850.3937007874, meters_to_inches(555))


if __name__ == "__main__":
    unittest.main()  # pragma: no cover
