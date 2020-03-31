
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
      },
      {
        "kind": "UNION",
        "name": "AbstractRegexResult",
        "possibleTypes": [
          {
            "name": "RegexIllegalRegexResult"
          },
          {
            "name": "RegexMatchingResult"
          },
          {
            "name": "RegexExtractionResult"
          }
        ]
      },
      {
        "kind": "INTERFACE",
        "name": "AbstractCorrectionResult",
        "possibleTypes": [
          {
            "name": "RegexIllegalRegexResult"
          },
          {
            "name": "RegexMatchingResult"
          },
          {
            "name": "RegexExtractionResult"
          },
          {
            "name": "SqlIllegalQueryResult"
          },
          {
            "name": "SqlWrongQueryTypeResult"
          },
          {
            "name": "SqlResult"
          }
        ]
      },
      {
        "kind": "UNION",
        "name": "SqlAbstractResult",
        "possibleTypes": [
          {
            "name": "SqlIllegalQueryResult"
          },
          {
            "name": "SqlWrongQueryTypeResult"
          },
          {
            "name": "SqlResult"
          }
        ]
      },
      {
        "kind": "INTERFACE",
        "name": "MatchingResult",
        "possibleTypes": [
          {
            "name": "SqlColumnComparisonMatchingResult"
          },
          {
            "name": "SqlTableComparisonMatchingResult"
          },
          {
            "name": "SqlBinaryExpressionComparisonMatchingResult"
          },
          {
            "name": "SqlGroupByComparisonMatchingResult"
          },
          {
            "name": "SqlOrderByComparisonMatchingResult"
          },
          {
            "name": "SqlLimitComparisonMatchingResult"
          },
          {
            "name": "SqlInsertComparisonMatchingResult"
          }
        ]
      }
    ]
  }
};
      export default result;
    