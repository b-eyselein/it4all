
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
            "name": "RegexIllegalRegexResult"
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
            "name": "SqlIllegalQueryResult"
          },
          {
            "name": "SqlInternalErrorResult"
          },
          {
            "name": "SqlResult"
          },
          {
            "name": "SqlWrongQueryTypeResult"
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
      },
      {
        "kind": "INTERFACE",
        "name": "AbstractCorrectionResult",
        "possibleTypes": [
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
            "name": "RegexIllegalRegexResult"
          },
          {
            "name": "RegexInternalErrorResult"
          },
          {
            "name": "RegexMatchingResult"
          },
          {
            "name": "SqlIllegalQueryResult"
          },
          {
            "name": "SqlInternalErrorResult"
          },
          {
            "name": "SqlResult"
          },
          {
            "name": "SqlWrongQueryTypeResult"
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
        "name": "MatchingResult",
        "possibleTypes": [
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
          },
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
            "name": "SqlTableComparisonMatchingResult"
          },
          {
            "name": "UmlAssociationMatchingResult"
          },
          {
            "name": "UmlAttributeMatchingResult"
          },
          {
            "name": "UmlMethodMatchingResult"
          },
          {
            "name": "UmlClassMatchingResult"
          },
          {
            "name": "UmlImplementationMatchingResult"
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
            "name": "SqlGroupByMatch"
          },
          {
            "name": "SqlOrderByMatch"
          },
          {
            "name": "SqlLimitMatch"
          },
          {
            "name": "SqlInsertMatch"
          },
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
            "name": "UmlMethodMatch"
          },
          {
            "name": "UmlImplementationMatch"
          }
        ]
      }
    ]
  }
};
      export default result;
    