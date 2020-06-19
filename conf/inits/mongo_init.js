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
            required: ["username"],
            additionalProperties: false
        }
    }
});

const usersCollection = db.getCollection('users');

const usersIndex = {username: 1};
const usersUniqueIndexName = usersCollection.createIndex(usersIndex, {unique: true});

print(`Created unique index on ${usersCollection.name}: ${usersUniqueIndexName}`);

// TODO: instantiate unique index for lessons

// instantiate unique index for exercise collections
const exCollectionsCollection = db.getCollection('exerciseCollections');

const exCollectionsIndex = {toolId: 1, collectionId: 1};
const exCollectionsUniqueIndexName = exCollectionsCollection.createIndex(exCollectionsIndex, {unique: true});

print(`Created unique index on ${exCollectionsCollection.name}: ${exCollectionsUniqueIndexName}`);

// instantiate unique index for exercises
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
