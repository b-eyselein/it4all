#!/usr/bin/env python3

from generate_users import generate_user_names, generate_user_insert_stmt
from typing import List
from random import choice as rand_choice

user_names = generate_user_names(10)

tool_url: List[str] = ['question', 'sql', 'programming', 'uml', 'rose', 'web', 'xml', 'spread', 'bool', 'nary']
marks: List[str] = ['VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark']

inserted_values: List[str] = []

for user in user_names:
    for tool in tool_url:
        sense_mark = rand_choice(marks)
        used_mark = rand_choice(marks)
        usability_mark = rand_choice(marks)
        feedback_mark = rand_choice(marks)
        fairness_mark = rand_choice(marks)

        inserted_values.append(
            "('{}', '{}', '{}', '{}', '{}' , '{}', '{}', '')".format(user, tool, sense_mark, used_mark, usability_mark,
                                                                     feedback_mark, fairness_mark))

insert_stmt = """\
insert into feedback (username, tool_url, sense, used, usability, style_feedback, fairness_feedback, comment)
values {};""".format(",\n".join(inserted_values))

print(generate_user_insert_stmt(user_names))
print()
print(insert_stmt)
