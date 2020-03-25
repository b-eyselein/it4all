
      export interface IntrospectionResultData {
        __schema: {
          types: {
            kind: string;
            name: string;
            possibleTypes: {
              name: string;
            }[];
          }[];
        };
      }
      const result: IntrospectionResultData = {
  "__schema": {
    "types": [
      {
        "kind": "UNION",
        "name": "ExContent",
        "possibleTypes": [
          {
            "name": "ProgExerciseContent"
          },
          {
            "name": "RegexExerciseContent"
          },
          {
            "name": "RoseExerciseContent"
          },
          {
            "name": "SqlExerciseContent"
          },
          {
            "name": "UmlExerciseContent"
          },
          {
            "name": "WebExerciseContent"
          },
          {
            "name": "XmlExerciseContent"
          }
        ]
      }
    ]
  }
};
      export default result;
    