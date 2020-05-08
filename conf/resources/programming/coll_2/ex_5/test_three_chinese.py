import unittest

from three_chinese import three_chinese


class ThreeChineseTest(unittest.TestCase):
    def test_three_chinese(self):
        self.assertEqual('Dri Chinisin mit nim Kintribiss', three_chinese('Drei Chinesen mit nem Kontrabass', 'i'))
        self.assertEqual('Faschars Fratz fascht frascha Fascha',
                         three_chinese('Fischers Fritz fischt frische Fische', 'a'))
        self.assertEqual('Bretkled blebt Bretkled end Blekret blebt Blekret',
                         three_chinese('Brautkleid bleibt Brautkleid und Blaukraut bleibt Blaukraut', 'e'))
        self.assertEqual('Wunn Flugun huntur Flugun flugun, flugun Flugun huntur Flugun',
                         three_chinese('Wenn Fliegen hinter Fliegen fliegen, fliegen Fliegen hinter Fliegen', 'u'))
        self.assertEqual('Zwo ostronoton koton ond koton wohrond so blogrono Mondstono klobton', three_chinese(
            'Zwei Astronauten kauten und kauten während sie blaugrüne Mondsteine klaubten', 'o'))
        self.assertEqual('Dar Mandschan schan schan schan', three_chinese('Der Mondschein schien schon schön', 'a'))


if __name__ == '__main__':
    unittest.main()
