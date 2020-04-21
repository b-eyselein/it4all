
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
          },
          {
            "name": "UmlCompleteResult"
          },
          {
            "name": "WebCompleteResult"
          },
          {
            "name": "XmlCompleteResult"
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
          },
          {
            "name": "UmlClassMatchingResult"
          },
          {
            "name": "UmlAttributeMatchingResult"
          },
          {
            "name": "UmlMethodMatchingResult"
          },
          {
            "name": "UmlAssociationMatchingResult"
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
            "name": "RegexMatchMatch"
          },
          {
            "name": "SqlColumnMatch"
          },
          {
            "name": "SqlTableMatch"
          },
          {
            "name": "SqlBinaryExpressionMatch"
          },
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
            "name": "UmlClassMatch"
          },
          {
            "name": "UmlAttributeMatch"
          },
          {
            "name": "UmlMethodMatch"
          },
          {
            "name": "UmlAssociationMatch"
          },
          {
            "name": "UmlImplementationMatch"
          },
          {
            "name": "ElementLineMatch"
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
      }
    ]
  }
};
      export default result;
    