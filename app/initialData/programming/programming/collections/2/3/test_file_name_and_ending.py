import unittest

from file_name_and_ending import file_name_and_ending


class FileNameAndEndingTest(unittest.TestCase):
    def test_file_name_and_ending(self):
        self.assertEqual(('config', ''), file_name_and_ending('config'))
        self.assertEqual(('.gitignore', ''), file_name_and_ending('.gitignore'))
        self.assertEqual(('text', 'txt'), file_name_and_ending('text.txt'))
        self.assertEqual(('application.conf', 'json'), file_name_and_ending('application.conf.json'))


if __name__ == '__main__':
    unittest.main()
