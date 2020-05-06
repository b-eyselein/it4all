db.auth('root', 'example');

const newUserName = 'it4all';
const newUserPassword = '1234'


db.createUser({
    user: newUserName,
    pwd: newUserPassword,
    roles: [
        {role: 'readWrite', db: 'it4all'}
    ]
});

// instantiate unique index for users
const usersCollection = db.getCollection('users');

const usersIndex = {username: 1};
const usersUniqueIndexName = usersCollection.createIndex(usersIndex, {unique: true});

print(`Created unique index on ${usersCollection.name}: ${usersUniqueIndexName}`)

// TODO: instantiate unique index for lessons

// instantiate unique index for exercise collections
const exCollectionsCollection = db.getCollection('exerciseCollections');

const exCollectionsIndex = {toolId: 1, collectionId: 1};
const exCollectionsUniqueIndexName = exCollectionsCollection.createIndex(exCollectionsIndex, {unique: true});

print(`Created unique index on ${exCollectionsCollection.name}: ${exCollectionsUniqueIndexName}`);

// instantiate unique index for exercises
const exercisesCollection = db.getCollection('exercises');

const exercisesIndex = {toolId: 1, collectionId: 1, exerciseId: 1};
const exercisesUniqueIndexName = exercisesCollection.createIndex(exercisesIndex, {unique: true});

print(`Created unique index on ${exercisesCollection.name}: ${exercisesUniqueIndexName}`);

// TODO: instantiate unique index for user solutions
