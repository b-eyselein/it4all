db.auth('root', '1234');

db.createUser({
    user: 'it4all',
    pwd: '1234',
    roles: [{role: 'readWrite', db: 'it4all'}]
});

// instantiate collection with validation and unique index for users, create default user

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

usersCollection.createIndex({username: 1}, {unique: true});

usersCollection.insertOne({
    username: "developer",
    pwHash: "$2a$10$v92Jgbo0hB.2fOmhcrjgzO8SO.xp6Q7i38lVF/ko0ag4.evDpJt4u",
    isAdmin: true
});

// instantiate collection with validation and unique index for lessons

db.createCollection('lessons', {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            properties: {
                _id: {bsonType: "objectId"},
                lessonId: {bsonType: "int"},
                toolId: {bsonType: "string"},
                title: {bsonType: "string"},
                description: {bsonType: "string"},
                video: {bsonType: "string"}
            },
            required: ["_id", "lessonId", "toolId", "title", "description"],
            additionalProperties: false
        }
    }
});

const lessonsIndex = {toolId: 1, lessonId: 1};

db.getCollection('lessons')
    .createIndex(lessonsIndex, {unique: true});

// instantiate collection with validation and unique index for lesson contents

db.createCollection('lessonContents', {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            properties: {
                _id: {bsonType: "objectId"},
                contentId: {bsonType: "int"},
                lessonId: {bsonType: "int"},
                toolId: {bsonType: "string"}
            },
            required: ["_id", "contentId", "lessonId", "toolId"],
            additionalProperties: true
        }
    }
});

db.getCollection('lessonContents')
    .createIndex({...lessonsIndex, contentId: 1}, {unique: true});

// instantiate collection with validation and unique index for exercise collections

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

const exCollectionsIndex = {toolId: 1, collectionId: 1};

db.getCollection('exerciseCollections')
    .createIndex(exCollectionsIndex, {unique: true});

// instantiate collection with validation and unique index for exercises

const topicsWithLevelType = {
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
                required: ["abbreviation", "toolId", "title", "maxLevel"],
                additionalProperties: false
            },
            level: {enum: ["Beginner", "Intermediate", "Advanced", "Expert"]}
        },
        required: ["topic", "level"],
        additionalProperties: false
    }
};

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
                topicsWithLevels: topicsWithLevelType,
                difficulty: {bsonType: "int"},
                content: {}
            },
            required: ["exerciseId", "collectionId", "toolId", "title", "authors", "text", "topicsWithLevels", "difficulty", "content"],
            additionalProperties: false
        }
    }
});

const exercisesIndex = {...exCollectionsIndex, exerciseId: 1};

db.getCollection('exercises')
    .createIndex(exercisesIndex, {unique: true});

// instantiate collection and unique index for user solutions (TODO: with validation)

db.getCollection('userSolutions')
    .createIndex({username: 1, ...exercisesIndex, solutionId: 1, part: 1}, {unique: true});

// instantiate collection and unique index for user proficiencies (TODO: with validation)

db.getCollection('userProficiencies')
    .createIndex({username: 1, topic: 1}, {unique: true});

// instantiate collection and unique index for user results (TODO: validation?)

db.getCollection('exerciseResults')
    .createIndex({username: 1, ...exercisesIndex, partId: 1}, {unique: true});

