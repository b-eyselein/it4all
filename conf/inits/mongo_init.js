db.auth('root', '1234');

const newUserName = 'it4all';
const newUserPassword = '1234'

db.createUser({
    user: newUserName,
    pwd: newUserPassword,
    roles: [{role: 'readWrite', db: 'it4all'}]
});

// instantiate collection with validation and unique index for users
db.createCollection('users', {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            properties: {
                _id: {bsonType: "objectId"},
                username: {bsonType: "string"},
                pwHash: {bsonType: ["string", "null"]},
                isAdmin: {bsonType: "bool"}
            },
            required: ["_id", "username"],
            additionalProperties: false
        }
    }
});

const usersCollection = db.getCollection('users');

const usersIndex = {username: 1};
const usersUniqueIndexName = usersCollection.createIndex(usersIndex, {unique: true});

print(`Created unique index on ${usersCollection.name}: ${usersUniqueIndexName}`);

// TODO: instantiate unique index for lessons

db.createCollection('lessons', {
    validators: {
        $jsonSchema: {
            bsonType: "object",
            properties: {
                _id: {bsonType: "objectId"},
                lessonId: {bsonType: "int"},
                toolId: {bsonType: "string"},
                title: {bsonType: "string"},
                description: {bsonType: "string"}
            },
            required: ["_id", "lessonId", "toolId", "title", "description"],
            additionalProperties: true
        }
    }
});

const lessonsCollection = db.getCollection('lessons');

const lessonsIndex = {toolId: 1, lessonId: 1};
const lessonsUniqueIndexName = lessonsCollection.createIndex(lessonsIndex, {unique: true});

print(`Created unique index on ${lessonsCollection}: ${lessonsUniqueIndexName}`);

// instantiate unique index for exercise collections
db.createCollection('exerciseCollections', {
    validators: {
        $jsonSchema: {
            bsonType: "object",
            properties: {
                _id: {bsonType: "objectId"},
                collectionId: {bsonType: "int"},
                toolId: {bsonType: "string"},
                title: {bsonType: "string"},
                authors: {bsonType: "array", "items": {bsonType: "string"}},
                text: {bsonType: "string"}
            },
            required: ["_id", "collectionId", "toolId", "title", "authors", "text"],
            additionalProperties: false
        }
    }
});

const exCollectionsCollection = db.getCollection('exerciseCollections');

const exCollectionsIndex = {toolId: 1, collectionId: 1};
const exCollectionsUniqueIndexName = exCollectionsCollection.createIndex(exCollectionsIndex, {unique: true});

print(`Created unique index on ${exCollectionsCollection.name}: ${exCollectionsUniqueIndexName}`);

// instantiate unique index for exercises
db.createCollection('exercises', {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            properties: {
                _id: {bsonType: "objectId"},
                exerciseId: {bsonType: "int"},
                collectionId: {bsonType: "int"},
                toolId: {bsonType: "string"},
                title: {bsonType: "string"},
                authors: {bsonType: "array", "items": {bsonType: "string"}},
                text: {bsonType: "string"},
                topicsWithLevels: {
                    bsonType: "array",
                    items: {
                        bsonType: "object",
                        properties: {
                            topic: {
                                bsonType: "object",
                                properties: {
                                    abbreviation: {bsonType: "string"},
                                    toolId: {bsonType: "string"},
                                    title: {bsonType: "string"},
                                    maxLevel: {enum: ["Beginner", "Intermediate", "Advanced", "Expert"]}
                                },
                                required: ["abbrevation", "toolId", "title", "maxLevel"],
                                additionalProperties: false
                            },
                            level: {enum: ["Beginner", "Intermediate", "Advanced", "Expert"]}
                        },
                        required: ["topic", "level"],
                        additionalProperties: false
                    }
                },
                difficulty: {bsonType: "int"},
                content: {}
            },
            required: [],
            additionalProperties: false
        }
    }
});

const exercisesCollection = db.getCollection('exercises');

const exercisesIndex = {...exCollectionsIndex, exerciseId: 1};
const exercisesUniqueIndexName = exercisesCollection.createIndex(exercisesIndex, {unique: true});

print(`Created unique index on ${exercisesCollection.name}: ${exercisesUniqueIndexName}`);

// instantiate unique index for user solutions

const userSolutionsCollection = db.getCollection('userSolutions');

const userSolutionsIndex = {username: 1, ...exercisesIndex, solutionId: 1, part: 1}
const userSolutionsUniqueIndex = userSolutionsCollection.createIndex(userSolutionsIndex, {unique: true});

print(`Created unique index on ${userSolutionsCollection.name}: ${userSolutionsUniqueIndex}`);

// TODO: instantiate unique index for user proficiencies

const userProficienciesCollection = db.getCollection('userProficiencies');

const userProficienciesIndex = {username: 1, topic: 1};
const userProficienciesUniqueIndex = userProficienciesCollection.createIndex(userProficienciesIndex, {unique: true});

print(`Created unique index on ${userProficienciesCollection.name}: ${userProficienciesUniqueIndex}`);
