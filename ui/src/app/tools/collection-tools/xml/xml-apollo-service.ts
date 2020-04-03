import * as Types from '../../../_interfaces/graphql-types';

import gql from 'graphql-tag';
import { Injectable } from '@angular/core';
import * as Apollo from 'apollo-angular';

type AbstractCorrectionResult_RegexIllegalRegexResult_Fragment = (
  { __typename?: 'RegexIllegalRegexResult' }
  & Pick<Types.RegexIllegalRegexResult, 'solutionSaved' | 'points' | 'maxPoints'>
);

type AbstractCorrectionResult_RegexMatchingResult_Fragment = (
  { __typename?: 'RegexMatchingResult' }
  & Pick<Types.RegexMatchingResult, 'solutionSaved' | 'points' | 'maxPoints'>
);

type AbstractCorrectionResult_RegexExtractionResult_Fragment = (
  { __typename?: 'RegexExtractionResult' }
  & Pick<Types.RegexExtractionResult, 'solutionSaved' | 'points' | 'maxPoints'>
);

type AbstractCorrectionResult_SqlIllegalQueryResult_Fragment = (
  { __typename?: 'SqlIllegalQueryResult' }
  & Pick<Types.SqlIllegalQueryResult, 'solutionSaved' | 'points' | 'maxPoints'>
);

type AbstractCorrectionResult_SqlWrongQueryTypeResult_Fragment = (
  { __typename?: 'SqlWrongQueryTypeResult' }
  & Pick<Types.SqlWrongQueryTypeResult, 'solutionSaved' | 'points' | 'maxPoints'>
);

type AbstractCorrectionResult_SqlResult_Fragment = (
  { __typename?: 'SqlResult' }
  & Pick<Types.SqlResult, 'solutionSaved' | 'points' | 'maxPoints'>
);

type AbstractCorrectionResult_UmlCompleteResult_Fragment = (
  { __typename?: 'UmlCompleteResult' }
  & Pick<Types.UmlCompleteResult, 'solutionSaved' | 'points' | 'maxPoints'>
);

type AbstractCorrectionResult_WebCompleteResult_Fragment = (
  { __typename?: 'WebCompleteResult' }
  & Pick<Types.WebCompleteResult, 'solutionSaved' | 'points' | 'maxPoints'>
);

type AbstractCorrectionResult_XmlCompleteResult_Fragment = (
  { __typename?: 'XmlCompleteResult' }
  & Pick<Types.XmlCompleteResult, 'solutionSaved' | 'points' | 'maxPoints'>
);

export type AbstractCorrectionResultFragment = AbstractCorrectionResult_RegexIllegalRegexResult_Fragment | AbstractCorrectionResult_RegexMatchingResult_Fragment | AbstractCorrectionResult_RegexExtractionResult_Fragment | AbstractCorrectionResult_SqlIllegalQueryResult_Fragment | AbstractCorrectionResult_SqlWrongQueryTypeResult_Fragment | AbstractCorrectionResult_SqlResult_Fragment | AbstractCorrectionResult_UmlCompleteResult_Fragment | AbstractCorrectionResult_WebCompleteResult_Fragment | AbstractCorrectionResult_XmlCompleteResult_Fragment;

export type XmlCorrectionMutationVariables = {
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
  part: Types.XmlExPart;
  solution: Types.XmlSolutionInput;
};


export type XmlCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { correctXml?: Types.Maybe<(
    { __typename?: 'XmlCompleteResult' }
    & XmlCompleteResultFragment
  )> }
);

export type XmlCompleteResultFragment = (
  { __typename?: 'XmlCompleteResult' }
  & Pick<Types.XmlCompleteResult, 'successType'>
  & { grammarResult?: Types.Maybe<(
    { __typename?: 'XmlGrammarResult' }
    & XmlGrammarResultFragment
  )>, documentResult: Array<(
    { __typename?: 'XmlError' }
    & XmlErrorFragment
  )> }
  & AbstractCorrectionResult_XmlCompleteResult_Fragment
);

export type XmlGrammarResultFragment = (
  { __typename?: 'XmlGrammarResult' }
  & { parseErrors: Array<(
    { __typename?: 'DTDParseException' }
    & Pick<Types.DtdParseException, 'msg' | 'parsedLine'>
  )>, results: (
    { __typename?: 'XmlElementLineComparisonMatchingResult' }
    & Mr_XmlElementLineComparisonMatchingResult_Fragment
    & XmlElementLineMatchingResultFragment
  ) }
);

export type XmlElementLineMatchingResultFragment = (
  { __typename?: 'XmlElementLineComparisonMatchingResult' }
  & { allMatches: Array<(
    { __typename?: 'ElementLineMatch' }
    & XmlElementLineMatchFragment
  )> }
);

export type XmlElementLineMatchFragment = (
  { __typename?: 'ElementLineMatch' }
  & Pick<Types.ElementLineMatch, 'matchType'>
  & { userArg?: Types.Maybe<(
    { __typename?: 'ElementLine' }
    & ElementLineFragment
  )>, sampleArg?: Types.Maybe<(
    { __typename?: 'ElementLine' }
    & ElementLineFragment
  )> }
);

export type ElementLineFragment = (
  { __typename?: 'ElementLine' }
  & Pick<Types.ElementLine, 'elementName'>
  & { elementDefinition: (
    { __typename?: 'ElementDefinition' }
    & Pick<Types.ElementDefinition, 'elementName' | 'content'>
  ), attributeLists: Array<(
    { __typename?: 'AttributeList' }
    & Pick<Types.AttributeList, 'elementName' | 'attributeDefinitions'>
  )> }
);

export type XmlErrorFragment = (
  { __typename?: 'XmlError' }
  & Pick<Types.XmlError, 'success' | 'line' | 'errorType' | 'errorMessage'>
);

type Mr_RegexExtractedValuesComparisonMatchingResult_Fragment = (
  { __typename?: 'RegexExtractedValuesComparisonMatchingResult' }
  & Pick<Types.RegexExtractedValuesComparisonMatchingResult, 'points' | 'maxPoints'>
);

type Mr_SqlColumnComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlColumnComparisonMatchingResult' }
  & Pick<Types.SqlColumnComparisonMatchingResult, 'points' | 'maxPoints'>
);

type Mr_SqlTableComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlTableComparisonMatchingResult' }
  & Pick<Types.SqlTableComparisonMatchingResult, 'points' | 'maxPoints'>
);

type Mr_SqlBinaryExpressionComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlBinaryExpressionComparisonMatchingResult' }
  & Pick<Types.SqlBinaryExpressionComparisonMatchingResult, 'points' | 'maxPoints'>
);

type Mr_SqlGroupByComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlGroupByComparisonMatchingResult' }
  & Pick<Types.SqlGroupByComparisonMatchingResult, 'points' | 'maxPoints'>
);

type Mr_SqlOrderByComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlOrderByComparisonMatchingResult' }
  & Pick<Types.SqlOrderByComparisonMatchingResult, 'points' | 'maxPoints'>
);

type Mr_SqlLimitComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlLimitComparisonMatchingResult' }
  & Pick<Types.SqlLimitComparisonMatchingResult, 'points' | 'maxPoints'>
);

type Mr_SqlInsertComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlInsertComparisonMatchingResult' }
  & Pick<Types.SqlInsertComparisonMatchingResult, 'points' | 'maxPoints'>
);

type Mr_UmlClassMatchingResult_Fragment = (
  { __typename?: 'UmlClassMatchingResult' }
  & Pick<Types.UmlClassMatchingResult, 'points' | 'maxPoints'>
);

type Mr_UmlAttributeMatchingResult_Fragment = (
  { __typename?: 'UmlAttributeMatchingResult' }
  & Pick<Types.UmlAttributeMatchingResult, 'points' | 'maxPoints'>
);

type Mr_UmlMethodMatchingResult_Fragment = (
  { __typename?: 'UmlMethodMatchingResult' }
  & Pick<Types.UmlMethodMatchingResult, 'points' | 'maxPoints'>
);

type Mr_UmlAssociationMatchingResult_Fragment = (
  { __typename?: 'UmlAssociationMatchingResult' }
  & Pick<Types.UmlAssociationMatchingResult, 'points' | 'maxPoints'>
);

type Mr_UmlImplementationMatchingResult_Fragment = (
  { __typename?: 'UmlImplementationMatchingResult' }
  & Pick<Types.UmlImplementationMatchingResult, 'points' | 'maxPoints'>
);

type Mr_XmlElementLineComparisonMatchingResult_Fragment = (
  { __typename?: 'XmlElementLineComparisonMatchingResult' }
  & Pick<Types.XmlElementLineComparisonMatchingResult, 'points' | 'maxPoints'>
);

export type MrFragment = Mr_RegexExtractedValuesComparisonMatchingResult_Fragment | Mr_SqlColumnComparisonMatchingResult_Fragment | Mr_SqlTableComparisonMatchingResult_Fragment | Mr_SqlBinaryExpressionComparisonMatchingResult_Fragment | Mr_SqlGroupByComparisonMatchingResult_Fragment | Mr_SqlOrderByComparisonMatchingResult_Fragment | Mr_SqlLimitComparisonMatchingResult_Fragment | Mr_SqlInsertComparisonMatchingResult_Fragment | Mr_UmlClassMatchingResult_Fragment | Mr_UmlAttributeMatchingResult_Fragment | Mr_UmlMethodMatchingResult_Fragment | Mr_UmlAssociationMatchingResult_Fragment | Mr_UmlImplementationMatchingResult_Fragment | Mr_XmlElementLineComparisonMatchingResult_Fragment;

export const AbstractCorrectionResultFragmentDoc = gql`
    fragment AbstractCorrectionResult on AbstractCorrectionResult {
  solutionSaved
  points
  maxPoints
}
    `;
export const MrFragmentDoc = gql`
    fragment MR on MatchingResult {
  points
  maxPoints
}
    `;
export const ElementLineFragmentDoc = gql`
    fragment ElementLine on ElementLine {
  elementName
  elementDefinition {
    elementName
    content
  }
  attributeLists {
    elementName
    attributeDefinitions
  }
}
    `;
export const XmlElementLineMatchFragmentDoc = gql`
    fragment XmlElementLineMatch on ElementLineMatch {
  matchType
  userArg {
    ...ElementLine
  }
  sampleArg {
    ...ElementLine
  }
}
    ${ElementLineFragmentDoc}`;
export const XmlElementLineMatchingResultFragmentDoc = gql`
    fragment XmlElementLineMatchingResult on XmlElementLineComparisonMatchingResult {
  allMatches {
    ...XmlElementLineMatch
  }
}
    ${XmlElementLineMatchFragmentDoc}`;
export const XmlGrammarResultFragmentDoc = gql`
    fragment XmlGrammarResult on XmlGrammarResult {
  parseErrors {
    msg
    parsedLine
  }
  results {
    ...MR
    ...XmlElementLineMatchingResult
  }
}
    ${MrFragmentDoc}
${XmlElementLineMatchingResultFragmentDoc}`;
export const XmlErrorFragmentDoc = gql`
    fragment XmlError on XmlError {
  success
  line
  errorType
  errorMessage
}
    `;
export const XmlCompleteResultFragmentDoc = gql`
    fragment XmlCompleteResult on XmlCompleteResult {
  ...AbstractCorrectionResult
  successType
  grammarResult {
    ...XmlGrammarResult
  }
  documentResult {
    ...XmlError
  }
}
    ${AbstractCorrectionResultFragmentDoc}
${XmlGrammarResultFragmentDoc}
${XmlErrorFragmentDoc}`;
export const XmlCorrectionDocument = gql`
    mutation XmlCorrection($collId: Int!, $exId: Int!, $part: XmlExPart!, $solution: XmlSolutionInput!) {
  correctXml(collId: $collId, exId: $exId, part: $part, solution: $solution) {
    ...XmlCompleteResult
  }
}
    ${XmlCompleteResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class XmlCorrectionGQL extends Apollo.Mutation<XmlCorrectionMutation, XmlCorrectionMutationVariables> {
    document = XmlCorrectionDocument;
    
  }