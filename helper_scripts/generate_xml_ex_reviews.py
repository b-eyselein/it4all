import random
from generate_users import *
from typing import List, Tuple

difficulties: List[str] = ['NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD']

ex_ids_and_vers: List[Tuple[int, str]] = [(1, '1.0.0'), (2, '1.0.0'), (3, '1.0.0'), (4, '1.0.0')]
exerciseParts = ['GrammarCreationXmlPart', 'DocumentCreationXmlPart']


def generate_reviews(users: List[str]) -> str:
    parts = []
    for user in users:
        for (ex_id, ex_sem_ver) in ex_ids_and_vers:
            for ex_part in exerciseParts:
                diff = random.choice(difficulties)
                dur = random.randint(2, 5)
                parts.append("('{}', {}, '1.0.0', '{}', '{}', {})".format(user, ex_id, ex_part, diff, dur))

    return """\
insert into xml_exercise_reviews(username, exercise_id, ex_sem_ver, exercise_part, difficulty, maybe_duration)
values {};""".format(
        ",\n".join(parts)
    )


if __name__ == "__main__":
    usernames = generate_user_names(10)
    print(generate_reviews(usernames))
