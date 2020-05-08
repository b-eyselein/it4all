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
        pass

    def test_calculate_reindeer_bmis(self):
        pass


if __name__ == "__main__":
    unittest.main()  # pragma: no cover
