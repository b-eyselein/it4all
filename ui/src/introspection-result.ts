
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
        "kind": "INTERFACE",
        "name": "AbstractCorrectionResult",
        "possibleTypes": [
          {
            "name": "FlaskInternalErrorResult"
          },
          {
            "name": "FlaskResult"
          },
          {
            "name": "ProgrammingInternalErrorResult"
          },
          {
            "name": "ProgrammingResult"
          },
          {
            "name": "RegexExtractionResult"
          },
          {
            "name": "RegexInternalErrorResult"
          },
          {
            "name": "RegexMatchingResult"
          },
          {
            "name": "SqlInternalErrorResult"
          },
          {
            "name": "SqlResult"
          },
          {
            "name": "WebInternalErrorResult"
          },
          {
            "name": "WebResult"
          },
          {
            "name": "XmlInternalErrorResult"
          },
          {
            "name": "XmlResult"
          }
        ]
      },
      {
        "kind": "UNION",
        "name": "ExerciseContentUnionType",
        "possibleTypes": [
          {
            "name": "FlaskExerciseContent"
          },
          {
            "name": "ProgrammingExerciseContent"
          },
          {
            "name": "RegexExerciseContent"
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
        "kind": "INTERFACE",
        "name": "FlaskAbstractCorrectionResult",
        "possibleTypes": [
          {
            "name": "FlaskInternalErrorResult"
          },
          {
            "name": "FlaskResult"
          }
        ]
      },
      {
        "kind": "INTERFACE",
        "name": "LessonContent",
        "possibleTypes": [
          {
            "name": "LessonMultipleChoiceQuestionsContent"
          },
          {
            "name": "LessonTextContent"
          }
        ]
      },
      {
        "kind": "INTERFACE",
        "name": "MatchingResult",
        "possibleTypes": [
          {
            "name": "RegexExtractedValuesComparisonMatchingResult"
          },
          {
            "name": "SqlBinaryExpressionComparisonMatchingResult"
          },
          {
            "name": "SqlColumnComparisonMatchingResult"
          },
          {
            "name": "SqlLimitComparisonMatchingResult"
          },
          {
            "name": "UmlAssociationMatchingResult"
          },
          {
            "name": "UmlAttributeMatchingResult"
          },
          {
            "name": "UmlClassMatchingResult"
          },
          {
            "name": "UmlImplementationMatchingResult"
          },
          {
            "name": "UmlMethodMatchingResult"
          },
          {
            "name": "XmlElementLineComparisonMatchingResult"
          }
        ]
      },
      {
        "kind": "INTERFACE",
        "name": "NewMatch",
        "possibleTypes": [
          {
            "name": "ElementLineMatch"
          },
          {
            "name": "RegexMatchMatch"
          },
          {
            "name": "SqlBinaryExpressionMatch"
          },
          {
            "name": "SqlColumnMatch"
          },
          {
            "name": "SqlLimitMatch"
          },
          {
            "name": "UmlAssociationMatch"
          },
          {
            "name": "UmlAttributeMatch"
          },
          {
            "name": "UmlClassMatch"
          },
          {
            "name": "UmlImplementationMatch"
          },
          {
            "name": "UmlMethodMatch"
          }
        ]
      },
      {
        "kind": "INTERFACE",
        "name": "ProgrammingAbstractResult",
        "possibleTypes": [
          {
            "name": "ProgrammingInternalErrorResult"
          },
          {
            "name": "ProgrammingResult"
          }
        ]
      },
      {
        "kind": "INTERFACE",
        "name": "RegexAbstractResult",
        "possibleTypes": [
          {
            "name": "RegexExtractionResult"
          },
          {
            "name": "RegexInternalErrorResult"
          },
          {
            "name": "RegexMatchingResult"
          }
        ]
      },
      {
        "kind": "INTERFACE",
        "name": "SqlAbstractResult",
        "possibleTypes": [
          {
            "name": "SqlInternalErrorResult"
          },
          {
            "name": "SqlResult"
          }
        ]
      },
      {
        "kind": "INTERFACE",
        "name": "UmlAbstractResult",
        "possibleTypes": [
          {
            "name": "UmlInternalErrorResult"
          },
          {
            "name": "UmlResult"
          }
        ]
      },
      {
        "kind": "UNION",
        "name": "UnitTestPart",
        "possibleTypes": [
          {
            "name": "SimplifiedUnitTestPart"
          },
          {
            "name": "NormalUnitTestPart"
          }
        ]
      },
      {
        "kind": "INTERFACE",
        "name": "WebAbstractResult",
        "possibleTypes": [
          {
            "name": "WebInternalErrorResult"
          },
          {
            "name": "WebResult"
          }
        ]
      },
      {
        "kind": "INTERFACE",
        "name": "XmlAbstractResult",
        "possibleTypes": [
          {
            "name": "XmlInternalErrorResult"
          },
          {
            "name": "XmlResult"
          }
        ]
      }
    ]
  }
};
      export default result;
    