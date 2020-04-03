import * as Types from '../_interfaces/graphql-types';

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

export type ProgCorrectionMutationVariables = {
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
  part: Types.ProgExPart;
  solution: Types.ProgSolutionInput;
};


export type ProgCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { correctProgramming?: Types.Maybe<(
    { __typename?: 'ProgCompleteResult' }
    & ProgCompleteResultFragment
  )> }
);

export type ProgCompleteResultFragment = (
  { __typename?: 'ProgCompleteResult' }
  & Pick<Types.ProgCompleteResult, 'solutionSaved'>
  & { normalResult?: Types.Maybe<{ __typename: 'NormalExecutionResult' }>, unitTestResults: Array<{ __typename: 'UnitTestCorrectionResult' }> }
);

export type RegexCorrectionMutationVariables = {
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
  part: Types.RegexExPart;
  solution: Types.Scalars['String'];
};


export type RegexCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { correctRegex?: Types.Maybe<(
    { __typename?: 'RegexIllegalRegexResult' }
    & RegexIllegalRegexResultFragment
  ) | (
    { __typename?: 'RegexMatchingResult' }
    & RegexMatchingResultFragment
  ) | (
    { __typename?: 'RegexExtractionResult' }
    & RegexExtractionResultFragment
  )> }
);

export type RegexIllegalRegexResultFragment = (
  { __typename?: 'RegexIllegalRegexResult' }
  & Pick<Types.RegexIllegalRegexResult, 'message'>
  & AbstractCorrectionResult_RegexIllegalRegexResult_Fragment
);

export type RegexMatchingSingleResultFragment = (
  { __typename?: 'RegexMatchingSingleResult' }
  & Pick<Types.RegexMatchingSingleResult, 'resultType' | 'matchData'>
);

export type RegexMatchingResultFragment = (
  { __typename?: 'RegexMatchingResult' }
  & { matchingResults: Array<(
    { __typename?: 'RegexMatchingSingleResult' }
    & RegexMatchingSingleResultFragment
  )> }
  & AbstractCorrectionResult_RegexMatchingResult_Fragment
);

export type RegexExtractionMatchFragment = (
  { __typename?: 'RegexMatchMatch' }
  & Pick<Types.RegexMatchMatch, 'matchType' | 'userArg' | 'sampleArg'>
);

export type ExtractionMatchingResultFragment = (
  { __typename?: 'RegexExtractedValuesComparisonMatchingResult' }
  & Pick<Types.RegexExtractedValuesComparisonMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'RegexMatchMatch' }
    & RegexExtractionMatchFragment
  )> }
);

export type RegexExtractionSingleResultFragment = (
  { __typename?: 'RegexExtractionSingleResult' }
  & Pick<Types.RegexExtractionSingleResult, 'base' | 'correct'>
  & { extractionMatchingResult: (
    { __typename?: 'RegexExtractedValuesComparisonMatchingResult' }
    & ExtractionMatchingResultFragment
  ) }
);

export type RegexExtractionResultFragment = (
  { __typename?: 'RegexExtractionResult' }
  & { extractionResults: Array<(
    { __typename?: 'RegexExtractionSingleResult' }
    & RegexExtractionSingleResultFragment
  )> }
  & AbstractCorrectionResult_RegexExtractionResult_Fragment
);

export type WebCorrectionMutationVariables = {
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
  part: Types.WebExPart;
  solution: Types.WebSolutionInput;
};


export type WebCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { correctWeb?: Types.Maybe<(
    { __typename?: 'WebCompleteResult' }
    & WebCompleteResultFragment
  )> }
);

export type WebCompleteResultFragment = (
  { __typename?: 'WebCompleteResult' }
  & AbstractCorrectionResult_WebCompleteResult_Fragment
);

export const ProgCompleteResultFragmentDoc = gql`
    fragment ProgCompleteResult on ProgCompleteResult {
  solutionSaved
  normalResult {
    __typename
  }
  unitTestResults {
    __typename
  }
}
    `;
export const AbstractCorrectionResultFragmentDoc = gql`
    fragment AbstractCorrectionResult on AbstractCorrectionResult {
  solutionSaved
  points
  maxPoints
}
    `;
export const RegexIllegalRegexResultFragmentDoc = gql`
    fragment RegexIllegalRegexResult on RegexIllegalRegexResult {
  ...AbstractCorrectionResult
  message
}
    ${AbstractCorrectionResultFragmentDoc}`;
export const RegexMatchingSingleResultFragmentDoc = gql`
    fragment RegexMatchingSingleResult on RegexMatchingSingleResult {
  resultType
  matchData
}
    `;
export const RegexMatchingResultFragmentDoc = gql`
    fragment RegexMatchingResult on RegexMatchingResult {
  ...AbstractCorrectionResult
  matchingResults {
    ...RegexMatchingSingleResult
  }
}
    ${AbstractCorrectionResultFragmentDoc}
${RegexMatchingSingleResultFragmentDoc}`;
export const RegexExtractionMatchFragmentDoc = gql`
    fragment RegexExtractionMatch on RegexMatchMatch {
  matchType
  userArg
  sampleArg
}
    `;
export const ExtractionMatchingResultFragmentDoc = gql`
    fragment ExtractionMatchingResult on RegexExtractedValuesComparisonMatchingResult {
  allMatches {
    ...RegexExtractionMatch
  }
  points
  maxPoints
}
    ${RegexExtractionMatchFragmentDoc}`;
export const RegexExtractionSingleResultFragmentDoc = gql`
    fragment RegexExtractionSingleResult on RegexExtractionSingleResult {
  base
  correct
  extractionMatchingResult {
    ...ExtractionMatchingResult
  }
}
    ${ExtractionMatchingResultFragmentDoc}`;
export const RegexExtractionResultFragmentDoc = gql`
    fragment RegexExtractionResult on RegexExtractionResult {
  ...AbstractCorrectionResult
  extractionResults {
    ...RegexExtractionSingleResult
  }
}
    ${AbstractCorrectionResultFragmentDoc}
${RegexExtractionSingleResultFragmentDoc}`;
export const WebCompleteResultFragmentDoc = gql`
    fragment WebCompleteResult on WebCompleteResult {
  ...AbstractCorrectionResult
}
    ${AbstractCorrectionResultFragmentDoc}`;
export const ProgCorrectionDocument = gql`
    mutation ProgCorrection($collId: Int!, $exId: Int!, $part: ProgExPart!, $solution: ProgSolutionInput!) {
  correctProgramming(collId: $collId, exId: $exId, part: $part, solution: $solution) {
    ...ProgCompleteResult
  }
}
    ${ProgCompleteResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class ProgCorrectionGQL extends Apollo.Mutation<ProgCorrectionMutation, ProgCorrectionMutationVariables> {
    document = ProgCorrectionDocument;
    
  }
export const RegexCorrectionDocument = gql`
    mutation RegexCorrection($collId: Int!, $exId: Int!, $part: RegexExPart!, $solution: String!) {
  correctRegex(collId: $collId, exId: $exId, part: $part, solution: $solution) {
    ...RegexIllegalRegexResult
    ...RegexMatchingResult
    ...RegexExtractionResult
  }
}
    ${RegexIllegalRegexResultFragmentDoc}
${RegexMatchingResultFragmentDoc}
${RegexExtractionResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class RegexCorrectionGQL extends Apollo.Mutation<RegexCorrectionMutation, RegexCorrectionMutationVariables> {
    document = RegexCorrectionDocument;
    
  }
export const WebCorrectionDocument = gql`
    mutation WebCorrection($collId: Int!, $exId: Int!, $part: WebExPart!, $solution: WebSolutionInput!) {
  correctWeb(collId: $collId, exId: $exId, part: $part, solution: $solution) {
    ...WebCompleteResult
  }
}
    ${WebCompleteResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class WebCorrectionGQL extends Apollo.Mutation<WebCorrectionMutation, WebCorrectionMutationVariables> {
    document = WebCorrectionDocument;
    
  }