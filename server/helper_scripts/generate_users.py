#!/usr/bin/env python3

import random
from typing import List


def generate_user_names(user_count: int) -> List[str]:
    # Generate usernames starting with s3 and 5 digits
    return ["s3{}".format(random.randint(10000, 100000)) for _oc in range(user_count)]


def generate_user_insert_stmt(user_names: List[str]) -> str:
    return """\
insert into users (user_type, username, std_role, showHideAgg) values
{}
    on duplicate key update username = values(username);""".format(
        ",\n".join(map(lambda username: "    (0, '{}', 'RoleUser', 'AGGREGATE')".format(username), user_names))
    )


if __name__ == "__main__":
    default_user_count: int = 10
    generated_user_names = generate_user_names(default_user_count)
    insert_stmt = generate_user_insert_stmt(generated_user_names)
    print(insert_stmt)
