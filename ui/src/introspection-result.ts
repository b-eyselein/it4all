
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
            "name": "SqlGroupByComparisonMatchingResult"
          },
          {
            "name": "SqlInsertComparisonMatchingResult"
          },
          {
            "name": "SqlLimitComparisonMatchingResult"
          },
          {
            "name": "SqlOrderByComparisonMatchingResult"
          },
          {
            "name": "SqlTableComparisonMatchingResult"
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
            "name": "SqlGroupByMatch"
          },
          {
            "name": "SqlInsertMatch"
          },
          {
            "name": "SqlLimitMatch"
          },
          {
            "name": "SqlOrderByMatch"
          },
          {
            "name": "SqlTableMatch"
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
    