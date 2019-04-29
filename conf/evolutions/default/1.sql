# --- !Ups

create table if not exists users (
    user_type   int,
    username    varchar(30) primary key,
    std_role    enum ('RoleUser', 'RoleAdmin', 'RoleSuperAdmin') default 'RoleUser',
    showHideAgg enum ('SHOW', 'HIDE', 'AGGREGATE')               default 'SHOW'
);

create table if not exists pw_hashes (
    username varchar(30) primary key,
    pw_hash  varchar(60),

    foreign key (username) references users (username)
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
        on update cascade
        on delete cascade,
    foreign key (course_id) references courses (id)
        on update cascade
        on delete cascade
);

# Feedback

create table if not exists feedback (
    username          varchar(30),
    tool_url          varchar(30),
    sense             enum ('VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark') default 'NoMark',
    used              enum ('VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark') default 'NoMark',
    usability         enum ('VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark') default 'NoMark',
    style_feedback    enum ('VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark') default 'NoMark',
    fairness_feedback enum ('VeryGood', 'Good', 'Neutral', 'Bad', 'VeryBad', 'NoMark') default 'NoMark',
    comment           text,

    primary key (username, tool_url),
    foreign key (username) references users (username)
        on update cascade
        on delete cascade
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
        on update cascade
        on delete cascade
);

# Programming

create table if not exists prog_collections (
    id         int primary key,
    title      varchar(50),
    author     varchar(50),
    ex_text    text,
    ex_state   enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',
    short_name varchar(50)
);

create table if not exists prog_exercises (
    id                int,
    semantic_version  varchar(10),
    collection_id     int,
    title             varchar(50),
    author            varchar(50),
    ex_text           text,
    ex_state          enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

    folder_identifier varchar(30),
    function_name     varchar(30),
    indent_level      int,
    output_type       varchar(30),
    base_data_json    text,

    primary key (id, semantic_version, collection_id),
    foreign key (collection_id) references prog_collections (id)
        on update cascade on delete cascade
);

create table if not exists prog_input_types (
    id            int,
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    input_name    varchar(20),
    input_type    varchar(20),

    primary key (id, exercise_id, ex_sem_ver, collection_id),
    foreign key (exercise_id, ex_sem_ver, collection_id) references prog_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

create table if not exists prog_sample_solutions (
    id             int,
    exercise_id    int,
    ex_sem_ver     varchar(10),
    collection_id  int,
    base           text,
    implementation text,

    primary key (id, exercise_id, ex_sem_ver, collection_id),
    foreign key (exercise_id, ex_sem_ver, collection_id) references prog_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

create table if not exists prog_sample_testdata (
    id            int,
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    input_json    text,
    output        varchar(50),

    primary key (id, exercise_id, ex_sem_ver, collection_id),
    foreign key (exercise_id, ex_sem_ver, collection_id) references prog_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

create table if not exists prog_commited_testdata (
    id             int,
    exercise_id    int,
    ex_sem_ver     varchar(10),
    collection_id  int,
    input_json     text,
    output         varchar(50),

    username       varchar(50),
    approval_state enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

    primary key (id, exercise_id, ex_sem_ver, collection_id, username),
    foreign key (exercise_id, ex_sem_ver, collection_id) references prog_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade,
    foreign key (username) references users (username)
        on update cascade on delete cascade
);

create table if not exists prog_uml_cd_parts (
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    class_name    varchar(30),
    class_diagram text,

    primary key (exercise_id, ex_sem_ver, collection_id),
    foreign key (exercise_id, ex_sem_ver, collection_id) references prog_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

create table if not exists prog_user_solutions (
    id                  int,
    username            varchar(50),
    exercise_id         int,
    ex_sem_ver          varchar(10),
    collection_id       int,
    part                varchar(50),

    implementation      text,
    extended_unit_tests boolean default false,
    test_data           text,
    points              double,
    max_points          double,

    primary key (id, username, exercise_id, collection_id, part),
    foreign key (exercise_id, ex_sem_ver, collection_id) references prog_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade,
    foreign key (username) references users (username)
        on update cascade on delete cascade
);

create table if not exists prog_exercise_reviews (
    username       varchar(50),
    exercise_id    int,
    ex_sem_ver     varchar(10),
    collection_id  int,
    part           varchar(50),
    difficulty     enum ('NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'),
    maybe_duration int,

    primary key (username, exercise_id, ex_sem_ver, collection_id, part),
    foreign key (exercise_id, ex_sem_ver, collection_id) references prog_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade,
    foreign key (username) references users (username)
        on update cascade on delete cascade
);

# Regex

create table if not exists regex_collections (
    id         int primary key,
    title      varchar(50),
    author     varchar(50),
    ex_text    text,
    ex_state   enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',
    short_name varchar(50)
);

create table if not exists regex_exercises (
    id               int,
    semantic_version varchar(10),
    collection_id    int,
    title            varchar(50),
    author           varchar(50),
    ex_text          text,
    ex_state         enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',
    max_points       int,

    primary key (id, semantic_version, collection_id),
    foreign key (collection_id) references regex_collections (id)
        on update cascade on delete cascade
);

create table if not exists regex_sample_solutions (
    id            int,
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,

    sample        varchar(100),
    primary key (id, exercise_id, ex_sem_ver, collection_id),
    foreign key (exercise_id, ex_sem_ver, collection_id) references regex_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

create table if not exists regex_test_data (
    id            int,
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    data          varchar(100),
    is_included   boolean,

    primary key (id, exercise_id, ex_sem_ver, collection_id),
    foreign key (exercise_id, ex_sem_ver, collection_id) references regex_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

create table if not exists regex_user_solutions (
    id            int,
    username      varchar(50),
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    part          varchar(50),

    points        double,
    max_points    double,
    solution      varchar(100),

    primary key (id, username, exercise_id, collection_id, part),
    foreign key (username) references users (username)
        on update cascade on delete cascade,
    foreign key (exercise_id, ex_sem_ver, collection_id) references regex_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

create table if not exists regex_exercise_reviews (
    username       varchar(50),
    exercise_id    int,
    ex_sem_ver     varchar(10),
    collection_id  int,
    part           varchar(50),
    difficulty     enum ('NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'),
    maybe_duration int,

    primary key (username, exercise_id, ex_sem_ver, collection_id, part),
    foreign key (exercise_id, ex_sem_ver, collection_id) references regex_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

# Rose

create table if not exists rose_collections (
    id         int primary key,
    title      varchar(50),
    author     varchar(50),
    ex_text    text,
    ex_state   enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',
    short_name varchar(50)
);

create table if not exists rose_exercises (
    id               int,
    semantic_version varchar(10),
    collection_id    int,
    title            varchar(50),
    author           varchar(50),
    ex_text          text,
    ex_state         enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

    field_width      int,
    field_height     int,
    is_mp            boolean,

    primary key (id, semantic_version, collection_id),
    foreign key (collection_id) references rose_collections (id)
        on update cascade on delete cascade
);

create table if not exists rose_inputs (
    id            int,
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    input_name    varchar(20),
    input_type    varchar(20),

    primary key (id, exercise_id, ex_sem_ver, collection_id),
    foreign key (exercise_id, ex_sem_ver, collection_id) references rose_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

create table if not exists rose_sample_solutions (
    id            int,
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    language      enum ('PYTHON_3', 'JAVA_8') default 'PYTHON_3',
    sample        text,

    primary key (id, exercise_id, ex_sem_ver, collection_id, language),
    foreign key (exercise_id, ex_sem_ver, collection_id) references rose_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

create table if not exists rose_user_solutions (
    id            int,
    username      varchar(50),
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    part          varchar(50),

    language      enum ('PYTHON_3', 'JAVA_8') default 'PYTHON_3',

    solution      text,
    points        double,
    max_points    double,

    primary key (id, username, exercise_id, collection_id, part, language),
    foreign key (username) references users (username)
        on update cascade on delete cascade,
    foreign key (exercise_id, ex_sem_ver, collection_id) references rose_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

create table if not exists rose_exercise_reviews (
    username       varchar(50),
    exercise_id    int,
    ex_sem_ver     varchar(10),
    collection_id  int,
    part           varchar(50),
    difficulty     enum ('NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'),
    maybe_duration int,

    primary key (username, exercise_id, ex_sem_ver, collection_id, part),
    foreign key (exercise_id, ex_sem_ver, collection_id) references rose_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

# Sql

create table if not exists sql_scenarioes (
    id         int primary key,
    title      varchar(50),
    author     varchar(50),
    ex_text    text,
    ex_state   enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

    short_name varchar(50),
    scriptFile varchar(50)
);

create table if not exists sql_exercises (
    id               int,
    semantic_version varchar(10),
    title            varchar(50),
    author           varchar(50),
    ex_text          text,
    ex_state         enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED')    default 'RESERVED',

    collection_id    int,
    tags             text,
    exercise_type    enum ('SELECT', 'CREATE', 'UPDATE', 'INSERT', 'DELETE') default 'SELECT',
    hint             text,

    primary key (id, semantic_version, collection_id),
    foreign key (collection_id) references sql_scenarioes (id)
        on update cascade on delete cascade
);

create table if not exists sql_sample_solutions (
    id            int,
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    sample        text,

    primary key (id, exercise_id, ex_sem_ver, collection_id),
    foreign key (exercise_id, ex_sem_ver, collection_id) references sql_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

create table if not exists sql_user_solutions (
    id            int,
    username      varchar(50),
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    part          varchar(50),

    points        double,
    max_points    double,
    solution      text,

    primary key (id, username, exercise_id, collection_id, part),
    foreign key (username) references users (username)
        on update cascade on delete cascade,
    foreign key (exercise_id, ex_sem_ver, collection_id) references sql_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

# Uml

create table if not exists uml_collections (
    id         int primary key,
    title      varchar(50),
    author     varchar(50),
    ex_text    text,
    ex_state   enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',
    short_name varchar(50)
);

create table if not exists uml_exercises (
    id               int,
    semantic_version varchar(10),
    collection_id    int,
    title            varchar(50),
    author           varchar(50),
    ex_text          text,
    ex_state         enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

    marked_text      text,

    primary key (id, semantic_version, collection_id),
    foreign key (collection_id) references uml_collections (id)
        on update cascade on delete cascade
);

create table if not exists uml_to_ignore (
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    to_ignore     varchar(50),

    primary key (exercise_id, ex_sem_ver, collection_id, to_ignore),
    foreign key (exercise_id, ex_sem_ver, collection_id) references uml_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

create table if not exists uml_mappings (
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    mapping_key   varchar(50),
    mapping_value varchar(50),

    primary key (exercise_id, ex_sem_ver, collection_id, mapping_key),
    foreign key (exercise_id, ex_sem_ver, collection_id) references uml_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

create table if not exists uml_sample_solutions (
    id            int,
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    sample        text,

    primary key (id, exercise_id, ex_sem_ver, collection_id),
    foreign key (exercise_id, ex_sem_ver, collection_id) references uml_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

create table if not exists uml_user_solutions (
    id            int,
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    username      varchar(30),
    part          varchar(50),

    points        double,
    max_points    double,
    solution      text,

    primary key (id, exercise_id, collection_id, username, part),
    foreign key (username) references users (username)
        on update cascade on delete cascade,
    foreign key (exercise_id, ex_sem_ver, collection_id) references uml_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

create table if not exists uml_exercise_reviews (
    username       varchar(50),
    exercise_id    int,
    ex_sem_ver     varchar(10),
    collection_id  int,
    part           varchar(50),
    difficulty     enum ('NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'),
    maybe_duration int,

    primary key (username, exercise_id, ex_sem_ver, collection_id, part),
    foreign key (username) references users (username)
        on update cascade on delete cascade,
    foreign key (exercise_id, ex_sem_ver, collection_id) references uml_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

# Web

create table if not exists web_collections (
    id         int primary key,
    title      varchar(50),
    author     varchar(50),
    ex_text    varchar(50),
    ex_state   enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',
    short_name varchar(50)
);

create table if not exists web_exercises (
    id               int,
    semantic_version varchar(10),
    collection_id    int,
    title            varchar(50)  not null,
    author           varchar(50)  not null,
    ex_text          text,
    ex_state         enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

    html_text        text,
    js_text          text,
    file_name        varchar(100) not null,

    primary key (id, semantic_version, collection_id),
    foreign key (collection_id) references web_collections (id)
        on update cascade on delete cascade
);

create table if not exists html_tasks (
    task_id         int,
    exercise_id     int,
    ex_sem_ver      varchar(10),
    collection_id   int,
    text            text,
    xpath_query     varchar(50),
    awaited_tagname varchar(50),
    text_content    varchar(100),

    primary key (task_id, exercise_id, ex_sem_ver, collection_id),
    foreign key (exercise_id, ex_sem_ver, collection_id) references web_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

create table if not exists html_attributes (
    attr_key      varchar(30),
    task_id       int,
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    attr_value    varchar(150),

    primary key (attr_key, task_id, exercise_id, ex_sem_ver, collection_id),
    foreign key (task_id, exercise_id, ex_sem_ver, collection_id) references html_tasks (task_id, exercise_id, ex_sem_ver, collection_id)
        on update cascade on delete cascade
);

create table if not exists js_tasks (
    task_id            int,
    exercise_id        int,
    ex_sem_ver         varchar(10),
    collection_id      int,
    text               text,
    xpath_query        varchar(50),

    action_type        enum ('CLICK', 'FILLOUT') default 'CLICK',
    action_xpath_query varchar(50),
    keys_to_send       varchar(100),

    primary key (task_id, exercise_id, ex_sem_ver, collection_id),
    foreign key (exercise_id, ex_sem_ver, collection_id) references web_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

create table if not exists js_conditions (
    condition_id    int,
    task_id         int,
    exercise_id     int,
    ex_sem_ver      varchar(10),
    collection_id   int,
    is_precondition boolean default true,

    xpath_query     varchar(50),
    awaited_tagname varchar(50),
    awaited_value   varchar(50),

    primary key (condition_id, task_id, exercise_id, ex_sem_ver, collection_id, is_precondition),
    foreign key (task_id, exercise_id, ex_sem_ver, collection_id) references js_tasks (task_id, exercise_id, ex_sem_ver, collection_id)
        on update cascade on delete cascade
);

create table if not exists web_js_condition_attributes (
    attr_key        varchar(30),
    cond_id         int,
    task_id         int,
    exercise_id     int,
    ex_sem_ver      varchar(10),
    collection_id   int,
    is_precondition boolean      not null,
    attr_value      varchar(150) not null,

    primary key (attr_key, cond_id, task_id, exercise_id, ex_sem_ver, collection_id, is_precondition),
    foreign key (cond_id, task_id, exercise_id, ex_sem_ver, collection_id, is_precondition)
        references js_conditions (condition_id, task_id, exercise_id, ex_sem_ver, collection_id, is_precondition)
        on update cascade on delete cascade
);

create table if not exists web_files (
    path          varchar(100),
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    resource_path varchar(200) not null,
    file_type     varchar(20)  not null,
    editable      boolean      not null,

    primary key (path, exercise_id, ex_sem_ver, collection_id),
    foreign key (exercise_id, ex_sem_ver, collection_id)
        references web_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

create table if not exists web_sample_solutions (
    id            int,
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,

    html_sample   text,
    js_sample     text,

    primary key (id, exercise_id, ex_sem_ver, collection_id),
    foreign key (exercise_id, ex_sem_ver, collection_id)
        references web_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

create table if not exists web_sample_solution_files (
    name          varchar(50),
    sample_id     int,
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    content       text        not null,
    file_type     varchar(50) not null,
    editable      boolean     not null,

    primary key (name, sample_id, exercise_id, ex_sem_ver, collection_id),
    foreign key (sample_id, exercise_id, ex_sem_ver, collection_id)
        references web_sample_solutions (id, exercise_id, ex_sem_ver, collection_id)
        on update cascade on delete cascade
);

create table if not exists web_user_solutions (
    id            int,
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    username      varchar(30),
    part          varchar(50),

    points        double,
    max_points    double,

    primary key (id, exercise_id, collection_id, username, part),
    foreign key (exercise_id, ex_sem_ver, collection_id) references web_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade,
    foreign key (username) references users (username)
        on update cascade on delete cascade
);

create table if not exists web_user_solution_files (
    name          varchar(50),
    solution_id   int,
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    username      varchar(30),
    part          varchar(50),

    content       text    not null,
    file_type     varchar(50),
    editable      boolean not null,

    primary key (name, solution_id, exercise_id, collection_id, username, part),
    foreign key (solution_id, exercise_id, collection_id, username, part)
        references web_user_solutions (id, exercise_id, collection_id, username, part)
        on update cascade on delete cascade
);

create table if not exists web_exercise_reviews (
    username       varchar(50),
    exercise_id    int,
    ex_sem_ver     varchar(10),
    collection_id  int,
    part           varchar(50),
    difficulty     enum ('NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'),
    maybe_duration int,

    primary key (username, exercise_id, ex_sem_ver, part, collection_id),
    foreign key (exercise_id, ex_sem_ver, collection_id) references web_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

# Xml

create table if not exists xml_collections (
    id         int primary key,
    title      varchar(50),
    author     varchar(50),
    ex_text    text,
    ex_state   enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',
    short_name varchar(50)
);

create table if not exists xml_exercises (
    id                  int,
    semantic_version    varchar(10),
    collection_id       int,
    title               varchar(50),
    author              varchar(50),
    ex_text             text,
    ex_state            enum ('RESERVED', 'CREATED', 'ACCEPTED', 'APPROVED') default 'RESERVED',

    grammar_description text,
    root_node           varchar(30),

    primary key (id, semantic_version, collection_id),
    foreign key (collection_id) references xml_collections (id)
        on update cascade on delete cascade
);

create table if not exists xml_sample_solutions (
    id            int,
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    grammar       text,
    document      text,

    primary key (id, exercise_id, ex_sem_ver, collection_id),
    foreign key (exercise_id, ex_sem_ver, collection_id) references xml_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade
);

create table if not exists xml_user_solutions (
    id            int,
    exercise_id   int,
    ex_sem_ver    varchar(10),
    collection_id int,
    username      varchar(50),
    part          varchar(50),

    points        double,
    max_points    double,
    grammar       text,
    document      text,


    primary key (id, exercise_id, collection_id, username, part),
    foreign key (exercise_id, ex_sem_ver, collection_id) references xml_exercises (id, semantic_version, collection_id)
        on update cascade on delete cascade,
    foreign key (username) references users (username)
        on update cascade on delete cascade
);

create table if not exists xml_exercise_reviews (
    username       varchar(50),
    exercise_id    int,
    ex_sem_ver     varchar(10),
    collection_id  int,
    part           varchar(50),
    difficulty     enum ('NOT_SPECIFIED', 'VERY_EASY', 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'),
    maybe_duration int,

    primary key (username, exercise_id, ex_sem_ver, collection_id, part),
    foreign key (exercise_id, ex_sem_ver, collection_id) references xml_exercises (id, semantic_version, collection_id)
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

drop table if exists web_js_condition_attributes;

drop table if exists js_conditions;

drop table if exists js_tasks;

drop table if exists html_attributes;

drop table if exists html_tasks;

drop table if exists web_exercises;

drop table if exists web_collections;

# Uml

drop table if exists uml_exercise_reviews;

drop table if exists uml_user_solutions;

drop table if exists uml_sample_solutions;

drop table if exists uml_mappings;

drop table if exists uml_to_ignore;

drop table if exists uml_exercises;

# Sql

drop table if exists sql_user_solutions;

drop table if exists sql_sample_solutions;

drop table if exists sql_exercises;

drop table if exists sql_scenarioes;

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

drop table if exists regex_test_data;

drop table if exists regex_sample_solutions;

drop table if exists regex_exercises;

drop table if exists regex_collections;

# Programming

drop table if exists prog_exercise_reviews;

drop table if exists prog_user_solutions;

drop table if exists prog_uml_cd_parts;

drop table if exists prog_commited_testdata;

drop table if exists prog_sample_testdata;

drop table if exists prog_sample_solutions;

drop table if exists prog_input_types;

drop table if exists prog_exercises;

# General

drop table if exists learning_path_sections;

drop table if exists learning_paths;

drop table if exists feedback;

drop table if exists users_in_courses;

drop table if exists courses;

drop table if exists pw_hashes;

drop table if exists users;

