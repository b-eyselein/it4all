db.auth('root', '1234');

db.createUser({
  user: 'it4all',
  pwd: '1234',
  roles: [{role: 'readWrite', db: 'it4all'}]
});

const exercisesIndex = {toolId: 1, collectionId: 1, exerciseId: 1};

// instantiate collection and unique index for user proficiencies (TODO: with validation)

db.getCollection('userProficiencies')
  .createIndex({username: 1, topic: 1}, {unique: true});

// instantiate collection and unique index for user results (TODO: validation?)

db.getCollection('exerciseResults')
  .createIndex({username: 1, ...exercisesIndex, partId: 1}, {unique: true});

