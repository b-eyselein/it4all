import unittest
from typing import Dict

from reindeers import calculate_bmi, calculate_reindeer_bmis


class ReindeerTest(unittest.TestCase):
    all_reindeers: Dict[str, Dict[str, int]] = {
        "Rudolph": {"age_years": 2, "height_cm": 200, "weight_kg": 120},
        "Comet": {"age_years": 1, "height_cm": 180, "weight_kg": 100},
        "Doner": {"age_years": 3, "height_cm": 210, "weight_kg": 90},
        "Blizzen": {"age_years": 4, "height_cm": 190, "weight_kg": 200},
        "Cupid": {"age_years": 2, "height_cm": 192, "weight_kg": 121},
        "Prancer": {"age_years": 4, "height_cm": 215, "weight_kg": 134},
        "Vixen": {"age_years": 6, "height_cm": 230, "weight_kg": 143},
        "Dancer": {"age_years": 1, "height_cm": 176, "weight_kg": 82},
        "Dasher": {"age_years": 5, "height_cm": 197, "weight_kg": 101}
    }

    def test_calculate_bmi(self):
        self.assertAlmostEqual(30.0, calculate_bmi(200, 120))
        self.assertAlmostEqual(25.0, calculate_bmi(200, 100))
        self.assertAlmostEqual(21.877551020408163, calculate_bmi(175, 67))
        self.assertAlmostEqual(18.359374999999996, calculate_bmi(160, 47))

    def test_calculate_reindeer_bmis(self):
        bmis: Dict[str, float] = calculate_reindeer_bmis(self.all_reindeers)

        self.assertAlmostEqual(30.0, bmis["Rudolph"])
        self.assertAlmostEqual(30.864197530864196, bmis["Comet"])
        self.assertAlmostEqual(20.408163265306122, bmis["Doner"])

        self.assertAlmostEqual(55.4016620498615, bmis["Blizzen"])
        self.assertAlmostEqual(32.82335069444444, bmis["Cupid"])
        self.assertAlmostEqual(28.98864250946458, bmis["Prancer"])

        self.assertAlmostEqual(27.03213610586012, bmis["Vixen"])
        self.assertAlmostEqual(26.47210743801653, bmis["Dancer"])
        self.assertAlmostEqual(26.02489113349996, bmis["Dasher"])

        self.assertEqual({}, calculate_reindeer_bmis({}))


if __name__ == "__main__":
    unittest.main()  # pragma: no cover
