# --- !Ups

create table if not exists users (
    user_type int,
    username  varchar(30) primary key,
    std_role  enum ('RoleUser', 'RoleAdmin', 'RoleSuperAdmin') default 'RoleUser'
);

create table if not exists pw_hashes (
    username varchar(30) primary key,
    pw_hash  varchar(60),

    foreign key (username)
        references users (username)
        on update cascade
        on delete cascade
);

create table if not exists courses (
    id          varchar(30) primary key,
    course_name varchar(100)
);

create table if not exists users_in_courses (
    username  varchar(30),
    course_id varchar(30),
    role      enum ('RoleUser', 'RoleAdmin', 'RoleSuperAdmin') default 'RoleUser',

    primary key (username, course_id),
    foreign key (username) references users (username)
        on update cascade on delete cascade,
    foreign key (course_id) references courses (id)
        on update cascade on delete cascade
);

# Feedback

create table if not exists feedback (
    username          varchar(30),
    tool_url          varchar(30),
    marks_json        longtext not null, # TODO: json
    # -- TODO: remove following columns!
    sense             enum ('VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark') default 'NoMark',
    used              enum ('VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark') default 'NoMark',
    usability         enum ('VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark') default 'NoMark',
    style_feedback    enum ('VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark') default 'NoMark',
    fairness_feedback enum ('VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark') default 'NoMark',
    comment           text,

    primary key (username, tool_url),
    foreign key (username) references users (username)
        on update cascade on delete cascade
);

# Learning paths

create table if not exists learning_paths (
    tool_url varchar(10),
    id       int,
    title    varchar(50),

    primary key (tool_url, id)
);

create table if not exists learning_path_sections (
    id           int,
    tool_url     varchar(10),
    path_id      int,
    section_type varchar(30),
    title        varchar(60),
    content      text,

    primary key (id, tool_url, path_id),
    foreign key (tool_url, path_id) references learning_paths (tool_url, id)
        on update cascade on delete cascade
);

# Programming

create table if not exists prog_collections (
    id         int,
    tool_id    varchar(20),

    title      varchar(50),
    author     varchar(50),
    ex_text    text,
    ex_state   enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',
    short_name varchar(50),

    primary key (id, tool_id)
);

create table if not exists prog_exercises (
    id                       int,
    collection_id            int,
    tool_id                  varchar(20),
    semantic_version         varchar(10),

    title                    varchar(50),
    author                   varchar(50),
    ex_text                  text,
    ex_state                 enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

    function_name            varchar(30) not null,
    foldername               varchar(50) not null,
    filename                 varchar(50) not null,

    inputs_json              longtext    not null, # TODO: json!
    output_type_json         longtext    not null, # TODO: json!
    base_data_json           text,

    unit_test_part_json      longtext    not null, # TODO: json!
    implementation_part_json longtext    not null, # TODO: json!

    tags_json                longtext    not null, # TODO: json!

    sample_solutions_json    longtext    not null, # TODO: json!
    sample_test_data_json    longtext    not null, # TODO: json!

    class_diagram_json       longtext,             # TODO: json!

    primary key (id, collection_id, tool_id, semantic_version),
    foreign key (collection_id, tool_id) references prog_collections (id, tool_id)
        on update cascade on delete cascade
);

create table if not exists prog_impl_files (
    name             varchar(100),
    exercise_id      int,
    collection_id    int,
    tool_id          varchar(20),
    semantic_version varchar(10),

    content          text        not null,
    file_type        varchar(20) not null,
    editable         boolean     not null,

    primary key (name, exercise_id, collection_id, tool_id, semantic_version),
    foreign key (exercise_id, collection_id, tool_id, semantic_version)
        references prog_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

create table if not exists prog_commited_testdata (
    id               int,
    exercise_id      int,
    collection_id    int,
    tool_id          varchar(20),
    semantic_version varchar(10),
    username         varchar(50),

    input_json       text,
    output           varchar(50),


    approval_state   enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

    primary key (id, exercise_id, collection_id, tool_id, semantic_version, username),
    foreign key (exercise_id, collection_id, tool_id, semantic_version)
        references prog_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade,
    foreign key (username)
        references users (username)
        on update cascade on delete cascade
);

create table if not exists prog_sample_solutions (
    id               int,
    exercise_id      int,
    collection_id    int,
    tool_id          varchar(20),
    semantic_version varchar(10),

    primary key (id, exercise_id, collection_id, tool_id, semantic_version),
    foreign key (exercise_id, collection_id, tool_id, semantic_version)
        references prog_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

create table if not exists prog_sample_solution_files (
    name             varchar(100),
    sol_id           int,
    exercise_id      int,
    collection_id    int,
    tool_id          varchar(20),
    semantic_version varchar(10),

    content          text,
    file_type        varchar(10),
    editable         boolean default false,

    primary key (name, sol_id, exercise_id, collection_id, tool_id, semantic_version),
    foreign key (sol_id, exercise_id, collection_id, tool_id, semantic_version)
        references prog_sample_solutions (id, exercise_id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

create table if not exists prog_user_solutions (
    id                  int,
    exercise_id         int,
    collection_id       int,
    tool_id             varchar(20),
    semantic_version    varchar(10),
    part                varchar(50),
    username            varchar(50),

    extended_unit_tests boolean default false,
    test_data           text,
    points              double,
    max_points          double,

    primary key (id, exercise_id, collection_id, tool_id, semantic_version, part, username),
    foreign key (exercise_id, collection_id, tool_id, semantic_version)
        references prog_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade,
    foreign key (username)
        references users (username)
        on update cascade on delete cascade
);

create table if not exists prog_user_solution_files (
    name             varchar(100),
    sol_id           int,
    exercise_id      int,
    collection_id    int,
    tool_id          varchar(20),
    semantic_version varchar(10),
    part             varchar(50),
    username         varchar(50),

    content          text,
    file_type        varchar(10),
    editable         boolean default false,

    primary key (name, sol_id, exercise_id, collection_id, tool_id, semantic_version, part, username),
    foreign key (sol_id, exercise_id, collection_id, tool_id, semantic_version, part, username)
        references prog_user_solutions (id, exercise_id, collection_id, tool_id, semantic_version, part, username)
        on update cascade on delete cascade
);

create table if not exists prog_exercise_reviews (
    username         varchar(50),
    exercise_id      int,
    collection_id    int,
    tool_id          varchar(20),
    semantic_version varchar(10),
    part             varchar(50),

    difficulty       enum ('NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'),
    maybe_duration   int,

    primary key (username, exercise_id, collection_id, tool_id, semantic_version, part),
    foreign key (exercise_id, collection_id, tool_id, semantic_version)
        references prog_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade,
    foreign key (username)
        references users (username)
        on update cascade on delete cascade
);

# Regex

create table if not exists regex_collections (
    id         int,
    tool_id    varchar(20),
    title      varchar(50),
    author     varchar(50),
    ex_text    text,
    ex_state   enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',
    short_name varchar(50),

    primary key (id, tool_id)
);

create table if not exists regex_exercises (
    id                        int,
    collection_id             int,
    tool_id                   varchar(20),
    semantic_version          varchar(10),

    title                     varchar(50),
    author                    varchar(50),
    ex_text                   text,
    ex_state                  enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',
    max_points                int,
    correction_type           enum ('MATCHING', 'EXTRACTION')                      default 'MATCHING',
    samples_json              longtext not null, # TODO: json
    match_test_data_json      longtext not null, # TODO: json
    extraction_test_data_json longtext not null, # TODO: json

    primary key (id, collection_id, tool_id, semantic_version),
    foreign key (collection_id, tool_id)
        references regex_collections (id, tool_id)
        on update cascade on delete cascade
);

create table if not exists regex_user_solutions (
    id            int,
    exercise_id   int,
    collection_id int,
    tool_id       varchar(20),
    ex_sem_ver    varchar(10),
    part          varchar(50),
    username      varchar(50),

    points        double,
    max_points    double,
    solution      varchar(100),

    primary key (id, exercise_id, collection_id, tool_id, ex_sem_ver, part, username),
    foreign key (username)
        references users (username)
        on update cascade on delete cascade,
    foreign key (exercise_id, collection_id, tool_id, ex_sem_ver)
        references regex_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

create table if not exists regex_exercise_reviews (
    username       varchar(50),
    exercise_id    int,
    collection_id  int,
    tool_id        varchar(20),
    ex_sem_ver     varchar(10),
    part           varchar(50),
    difficulty     enum ('NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'),
    maybe_duration int,

    primary key (username, exercise_id, collection_id, tool_id, ex_sem_ver, part),
    foreign key (exercise_id, collection_id, tool_id, ex_sem_ver)
        references regex_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

# Rose

create table if not exists rose_collections (
    id         int,
    tool_id    varchar(50),
    title      varchar(50),
    author     varchar(50),
    ex_text    text,
    ex_state   enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',
    short_name varchar(50),

    primary key (id, tool_id)
);

create table if not exists rose_exercises (
    id                    int,
    collection_id         int,
    tool_id               varchar(20),
    semantic_version      varchar(10),

    title                 varchar(50),
    author                varchar(50),
    ex_text               text,
    ex_state              enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

    field_width           int,
    field_height          int,
    is_mp                 boolean,

    input_types_json      longtext not null, # TODO: json!
    sample_solutions_json longtext not null, # TODO: json!

    primary key (id, collection_id, tool_id, semantic_version),
    foreign key (collection_id, tool_id)
        references rose_collections (id, tool_id)
        on update cascade on delete cascade
);

create table if not exists rose_inputs (
    id            int,
    exercise_id   int,
    collection_id int,
    tool_id       varchar(20),
    ex_sem_ver    varchar(10),

    input_name    varchar(20),
    input_type    varchar(20),

    primary key (id, exercise_id, collection_id, tool_id, ex_sem_ver),
    foreign key (exercise_id, collection_id, tool_id, ex_sem_ver)
        references rose_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

create table if not exists rose_sample_solutions (
    id            int,
    exercise_id   int,
    collection_id int,
    tool_id       varchar(20),
    ex_sem_ver    varchar(10),

    language      enum ('PYTHON_3', 'JAVA_8') default 'PYTHON_3',
    sample        text,

    primary key (id, exercise_id, collection_id, tool_id, ex_sem_ver, language),
    foreign key (exercise_id, collection_id, tool_id, ex_sem_ver)
        references rose_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

create table if not exists rose_user_solutions (
    id            int,
    exercise_id   int,
    collection_id int,
    tool_id       varchar(20),
    ex_sem_ver    varchar(10),
    part          varchar(50),
    username      varchar(50),

    language      enum ('PYTHON_3', 'JAVA_8') default 'PYTHON_3',

    solution      text,
    points        double,
    max_points    double,

    primary key (id, exercise_id, collection_id, tool_id, ex_sem_ver, part, username, language),
    foreign key (username)
        references users (username)
        on update cascade on delete cascade,
    foreign key (exercise_id, collection_id, tool_id, ex_sem_ver)
        references rose_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

create table if not exists rose_exercise_reviews (
    username       varchar(50),
    exercise_id    int,
    collection_id  int,
    tool_id        varchar(20),
    ex_sem_ver     varchar(10),
    part           varchar(50),

    difficulty     enum ('NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'),
    maybe_duration int,

    primary key (username, exercise_id, collection_id, tool_id, ex_sem_ver, part),
    foreign key (exercise_id, collection_id, tool_id, ex_sem_ver)
        references rose_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

# Sql

create table if not exists sql_collections (
    id         int,
    tool_id    varchar(20),

    title      varchar(50),
    author     varchar(50),
    ex_text    text,
    ex_state   enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',
    short_name varchar(50),

    primary key (id, tool_id)
);

create table if not exists sql_exercises (
    id                    int,
    collection_id         int,
    tool_id               varchar(20),
    semantic_version      varchar(10),

    title                 varchar(50),
    author                varchar(50),
    ex_text               text,
    ex_state              enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',


    tags_json             longtext not null, # TODO: json!
    exercise_type_json    longtext not null, # TODO: json!
    hint                  text,
    sample_solutions_json longtext not null, # TODO: sjon!

    primary key (id, collection_id, tool_id, semantic_version),
    foreign key (collection_id, tool_id)
        references sql_collections (id, tool_id)
        on update cascade on delete cascade
);

create table if not exists sql_sample_solutions (
    id            int,
    exercise_id   int,
    collection_id int,
    tool_id       varchar(20),
    ex_sem_ver    varchar(10),

    sample        text,

    primary key (id, exercise_id, collection_id, tool_id, ex_sem_ver),
    foreign key (exercise_id, collection_id, tool_id, ex_sem_ver)
        references sql_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

create table if not exists sql_user_solutions (
    id            int,
    exercise_id   int,
    collection_id int,
    tool_id       varchar(20),
    ex_sem_ver    varchar(10),
    part          varchar(50),
    username      varchar(50),

    points        double,
    max_points    double,
    solution      text,

    primary key (id, exercise_id, collection_id, tool_id, ex_sem_ver, part, username),
    foreign key (username)
        references users (username)
        on update cascade on delete cascade,
    foreign key (exercise_id, collection_id, tool_id, ex_sem_ver)
        references sql_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

# Uml

create table if not exists uml_collections (
    id         int,
    tool_id    varchar(20),

    title      varchar(50),
    author     varchar(50),
    ex_text    text,
    ex_state   enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',
    short_name varchar(50),

    primary key (id, tool_id)
);

create table if not exists uml_exercises (
    id               int,
    collection_id    int,
    tool_id          varchar(20),
    semantic_version varchar(10),

    title            varchar(50),
    author           varchar(50),
    ex_text          text,
    ex_state         enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

    to_ignore_json   longtext not null, # TODO: json
    mappings_json    longtext not null, # TODO: json

    primary key (id, collection_id, tool_id, semantic_version),
    foreign key (collection_id, tool_id)
        references uml_collections (id, tool_id)
        on update cascade on delete cascade
);

create table if not exists uml_sample_solutions (
    id            int,
    exercise_id   int,
    collection_id int,
    tool_id       varchar(20),
    ex_sem_ver    varchar(10),

    sample        text,

    primary key (id, exercise_id, collection_id, tool_id, ex_sem_ver),
    foreign key (exercise_id, collection_id, tool_id, ex_sem_ver)
        references uml_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

create table if not exists uml_user_solutions (
    id            int,
    exercise_id   int,
    collection_id int,
    tool_id       varchar(20),
    ex_sem_ver    varchar(10),
    part          varchar(50),
    username      varchar(30),

    points        double,
    max_points    double,
    solution      text,

    primary key (id, exercise_id, collection_id, tool_id, ex_sem_ver, part, username),
    foreign key (username)
        references users (username)
        on update cascade on delete cascade,
    foreign key (exercise_id, collection_id, tool_id, ex_sem_ver)
        references uml_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

create table if not exists uml_exercise_reviews (
    username       varchar(50),
    exercise_id    int,
    collection_id  int,
    tool_id        varchar(20),
    ex_sem_ver     varchar(10),
    part           varchar(50),

    difficulty     enum ('NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'),
    maybe_duration int,

    primary key (username, exercise_id, collection_id, tool_id, ex_sem_ver, part),
    foreign key (username)
        references users (username)
        on update cascade on delete cascade,
    foreign key (exercise_id, collection_id, tool_id, ex_sem_ver)
        references uml_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

# Web

create table if not exists web_collections (
    id         int,
    tool_id    varchar(20),

    title      varchar(50),
    author     varchar(50),
    ex_text    varchar(50),
    ex_state   enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',
    short_name varchar(50),

    primary key (id, tool_id)
);

create table if not exists web_exercises (
    id                    int,
    collection_id         int,
    tool_id               varchar(20),
    semantic_version      varchar(10),

    title                 varchar(50)  not null,
    author                varchar(50)  not null,
    ex_text               text,
    ex_state              enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

    html_text             text,
    js_text               text,
    filename              varchar(100) not null,
    sample_solutions_json longtext     not null, # TODO: json!

    primary key (id, collection_id, tool_id, semantic_version),
    foreign key (collection_id, tool_id)
        references web_collections (id, tool_id)
        on update cascade on delete cascade
);

create table if not exists web_html_tasks (
    task_id         int,
    exercise_id     int,
    collection_id   int,
    tool_id         varchar(20),
    ex_sem_ver      varchar(10),

    text            text        not null,
    xpath_query     varchar(50) not null,
    awaited_tagname varchar(50) not null,
    text_content    varchar(100),
    attributes_json longtext    not null, # TODO: json

    primary key (task_id, exercise_id, collection_id, tool_id, ex_sem_ver),
    foreign key (exercise_id, collection_id, tool_id, ex_sem_ver)
        references web_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

create table if not exists web_js_tasks (
    task_id              int,
    exercise_id          int,
    collection_id        int,
    tool_id              varchar(20),
    ex_sem_ver           varchar(10),

    text                 text        not null,
    xpath_query          varchar(50) not null,

    action_type          enum ('CLICK', 'FILLOUT') default 'CLICK',
    keys_to_send         varchar(100),

    pre_conditions_json  longtext    not null, # TODO: json
    post_conditions_json longtext    not null, # TODO: json

    primary key (task_id, exercise_id, collection_id, tool_id, ex_sem_ver),
    foreign key (exercise_id, collection_id, tool_id, ex_sem_ver)
        references web_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

create table if not exists web_files (
    name          varchar(100),
    exercise_id   int,
    collection_id int,
    tool_id       varchar(20),
    ex_sem_ver    varchar(10),

    content       text        not null,
    file_type     varchar(20) not null,
    editable      boolean     not null,

    primary key (name, exercise_id, collection_id, tool_id, ex_sem_ver),
    foreign key (exercise_id, collection_id, tool_id, ex_sem_ver)
        references web_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

create table if not exists web_sample_solutions (
    id            int,
    exercise_id   int,
    collection_id int,
    tool_id       varchar(20),
    ex_sem_ver    varchar(10),

    html_sample   text,
    js_sample     text,

    primary key (id, exercise_id, collection_id, tool_id, ex_sem_ver),
    foreign key (exercise_id, collection_id, tool_id, ex_sem_ver)
        references web_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

create table if not exists web_sample_solution_files (
    name          varchar(100),
    sample_id     int,
    exercise_id   int,
    collection_id int,
    tool_id       varchar(20),
    ex_sem_ver    varchar(10),

    content       text        not null,
    file_type     varchar(50) not null,
    editable      boolean     not null,

    primary key (name, sample_id, exercise_id, collection_id, tool_id, ex_sem_ver),
    foreign key (sample_id, exercise_id, collection_id, tool_id, ex_sem_ver)
        references web_sample_solutions (id, exercise_id, collection_id, tool_id, ex_sem_ver)
        on update cascade on delete cascade
);

create table if not exists web_user_solutions (
    id            int,
    exercise_id   int,
    collection_id int,
    tool_id       varchar(20),
    ex_sem_ver    varchar(10),
    part          varchar(50),
    username      varchar(30),

    points        double,
    max_points    double,

    primary key (id, exercise_id, collection_id, tool_id, ex_sem_ver, part, username),
    foreign key (exercise_id, collection_id, tool_id, ex_sem_ver)
        references web_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade,
    foreign key (username)
        references users (username)
        on update cascade on delete cascade
);

create table if not exists web_user_solution_files (
    name          varchar(100),
    solution_id   int,
    exercise_id   int,
    collection_id int,
    tool_id       varchar(20),
    ex_sem_ver    varchar(10),
    part          varchar(50),
    username      varchar(30),

    content       text        not null,
    file_type     varchar(50) not null,
    editable      boolean     not null,

    primary key (name, solution_id, exercise_id, collection_id, tool_id, ex_sem_ver, part, username),
    foreign key (solution_id, exercise_id, collection_id, tool_id, ex_sem_ver, part, username)
        references web_user_solutions (id, exercise_id, collection_id, tool_id, ex_sem_ver, part, username)
        on update cascade on delete cascade
);

create table if not exists web_exercise_reviews (
    username       varchar(50),
    exercise_id    int,
    collection_id  int,
    tool_id        varchar(20),
    ex_sem_ver     varchar(10),
    part           varchar(50),

    difficulty     enum ('NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'),
    maybe_duration int,

    primary key (username, exercise_id, collection_id, tool_id, ex_sem_ver, part),
    foreign key (username)
        references users (username)
        on update cascade on delete cascade,
    foreign key (exercise_id, collection_id, tool_id, ex_sem_ver)
        references web_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

# Xml

create table if not exists xml_collections (
    id         int,
    tool_id    varchar(50),
    title      varchar(50)                                                             not null,
    author     varchar(50)                                                             not null,
    ex_text    text                                                                    not null,
    ex_state   enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED' not null,
    short_name varchar(50)                                                             not null,

    primary key (id, tool_id)
);

create table if not exists xml_exercises (
    id                  int,
    collection_id       int,
    tool_id             varchar(20),
    semantic_version    varchar(10),

    title               varchar(50)                                                             not null,
    author              varchar(50)                                                             not null,
    ex_text             text                                                                    not null,
    ex_state            enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED' not null,

    grammar_description text                                                                    not null,
    root_node           varchar(30)                                                             not null,

    primary key (id, collection_id, tool_id, semantic_version),
    foreign key (collection_id, tool_id)
        references xml_collections (id, tool_id)
        on update cascade on delete cascade
);

create table if not exists xml_sample_solutions (
    id            int,
    exercise_id   int,
    collection_id int,
    tool_id       varchar(20),
    ex_sem_ver    varchar(10),

    grammar       text not null,
    document      text not null,

    primary key (id, exercise_id, collection_id, tool_id, ex_sem_ver),
    foreign key (exercise_id, collection_id, tool_id, ex_sem_ver)
        references xml_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade
);

create table if not exists xml_user_solutions (
    id            int,
    exercise_id   int,
    collection_id int,
    tool_id       varchar(20),
    ex_sem_ver    varchar(10),
    part          varchar(50),
    username      varchar(50),

    points        double,
    max_points    double,
    grammar       text,
    document      text,

    primary key (id, exercise_id, collection_id, tool_id, ex_sem_ver, part, username),
    foreign key (exercise_id, collection_id, tool_id, ex_sem_ver)
        references xml_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade,
    foreign key (username)
        references users (username)
        on update cascade on delete cascade
);

create table if not exists xml_exercise_reviews (
    username       varchar(50),
    exercise_id    int,
    collection_id  int,
    tool_id        varchar(20),
    ex_sem_ver     varchar(10),
    part           varchar(50),

    difficulty     enum ('NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'),
    maybe_duration int,

    primary key (username, exercise_id, collection_id, tool_id, ex_sem_ver, part),
    foreign key (exercise_id, collection_id, tool_id, ex_sem_ver)
        references xml_exercises (id, collection_id, tool_id, semantic_version)
        on update cascade on delete cascade,
    foreign key (username) references users (username)
        on update cascade on delete cascade
);

# --- !Downs

# Xml

drop table if exists xml_exercise_reviews;

drop table if exists xml_user_solutions;

drop table if exists xml_sample_solutions;

drop table if exists xml_exercises;

drop table if exists xml_collections;

# Web

drop table if exists web_exercise_reviews;

drop table if exists web_user_solution_files;

drop table if exists web_user_solutions;

drop table if exists web_sample_solution_files;

drop table if exists web_sample_solutions;

drop table if exists web_files;

drop table if exists web_js_tasks;

drop table if exists web_html_tasks;

drop table if exists web_exercises;

drop table if exists web_collections;

# Uml

drop table if exists uml_exercise_reviews;

drop table if exists uml_user_solutions;

drop table if exists uml_sample_solutions;

drop table if exists uml_exercises;

drop table if exists uml_collections;

# Sql

drop table if exists sql_user_solutions;

drop table if exists sql_sample_solutions;

drop table if exists sql_exercises;

drop table if exists sql_collections;

# Rose

drop table if exists rose_exercise_reviews;

drop table if exists rose_user_solutions;

drop table if exists rose_sample_solutions;

drop table if exists rose_inputs;

drop table if exists rose_exercises;

drop table if exists rose_collections;

# Regex

drop table if exists regex_exercise_reviews;

drop table if exists regex_user_solutions;

drop table if exists regex_exercises;

drop table if exists regex_collections;

# Programming

drop table if exists prog_exercise_reviews;

drop table if exists prog_user_solution_files;

drop table if exists prog_user_solutions;

drop table if exists prog_sample_solution_files;

drop table if exists prog_sample_solutions;

drop table if exists prog_commited_testdata;

drop table if exists prog_impl_files;

drop table if exists prog_exercises;

drop table if exists prog_collections;

# General

drop table if exists learning_path_sections;

drop table if exists learning_paths;

drop table if exists feedback;

drop table if exists users_in_courses;

drop table if exists courses;

drop table if exists pw_hashes;

drop table if exists users;
