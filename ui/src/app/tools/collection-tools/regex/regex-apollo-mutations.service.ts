import * as Types from '../../../_interfaces/graphql-types';

import gql from 'graphql-tag';
import { Injectable } from '@angular/core';
import * as Apollo from 'apollo-angular';

export type RegexCorrectionMutationVariables = Types.Exact<{
  userJwt: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
  part: Types.RegexExPart;
  solution: Types.Scalars['String'];
}>;


export type RegexCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { me?: Types.Maybe<(
    { __typename?: 'UserMutations' }
    & { regexExercise?: Types.Maybe<(
      { __typename?: 'RegexExerciseMutations' }
      & { correct: (
        { __typename?: 'RegexCorrectionResult' }
        & RegexCorrectionResultFragment
      ) }
    )> }
  )> }
);

export type RegexCorrectionResultFragment = (
  { __typename?: 'RegexCorrectionResult' }
  & Pick<Types.RegexCorrectionResult, 'solutionSaved' | 'proficienciesUpdated'>
  & { result: (
    { __typename?: 'RegexExtractionResult' }
    & RegexAbstractResult_RegexExtractionResult_Fragment
    & RegexExtractionResultFragment
  ) | (
    { __typename?: 'RegexInternalErrorResult' }
    & RegexAbstractResult_RegexInternalErrorResult_Fragment
    & RegexInternalErrorResultFragment
  ) | (
    { __typename?: 'RegexMatchingResult' }
    & RegexAbstractResult_RegexMatchingResult_Fragment
    & RegexMatchingResultFragment
  ) }
);

type RegexAbstractResult_RegexExtractionResult_Fragment = (
  { __typename: 'RegexExtractionResult' }
  & Pick<Types.RegexExtractionResult, 'points' | 'maxPoints'>
);

type RegexAbstractResult_RegexInternalErrorResult_Fragment = (
  { __typename: 'RegexInternalErrorResult' }
  & Pick<Types.RegexInternalErrorResult, 'points' | 'maxPoints'>
);

type RegexAbstractResult_RegexMatchingResult_Fragment = (
  { __typename: 'RegexMatchingResult' }
  & Pick<Types.RegexMatchingResult, 'points' | 'maxPoints'>
);

export type RegexAbstractResultFragment = RegexAbstractResult_RegexExtractionResult_Fragment | RegexAbstractResult_RegexInternalErrorResult_Fragment | RegexAbstractResult_RegexMatchingResult_Fragment;

export type RegexInternalErrorResultFragment = (
  { __typename?: 'RegexInternalErrorResult' }
  & Pick<Types.RegexInternalErrorResult, 'msg'>
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
);

export const RegexAbstractResultFragmentDoc = gql`
    fragment RegexAbstractResult on RegexAbstractResult {
  __typename
  points
  maxPoints
}
    `;
export const RegexInternalErrorResultFragmentDoc = gql`
    fragment RegexInternalErrorResult on RegexInternalErrorResult {
  msg
}
    `;
export const RegexMatchingSingleResultFragmentDoc = gql`
    fragment RegexMatchingSingleResult on RegexMatchingSingleResult {
  resultType
  matchData
}
    `;
export const RegexMatchingResultFragmentDoc = gql`
    fragment RegexMatchingResult on RegexMatchingResult {
  matchingResults {
    ...RegexMatchingSingleResult
  }
}
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
  extractionResults {
    ...RegexExtractionSingleResult
  }
}
    ${RegexExtractionSingleResultFragmentDoc}`;
export const RegexCorrectionResultFragmentDoc = gql`
    fragment RegexCorrectionResult on RegexCorrectionResult {
  solutionSaved
  proficienciesUpdated
  result {
    ...RegexAbstractResult
    ...RegexInternalErrorResult
    ...RegexMatchingResult
    ...RegexExtractionResult
  }
}
    ${RegexAbstractResultFragmentDoc}
${RegexInternalErrorResultFragmentDoc}
${RegexMatchingResultFragmentDoc}
${RegexExtractionResultFragmentDoc}`;
export const RegexCorrectionDocument = gql`
    mutation RegexCorrection($userJwt: String!, $collId: Int!, $exId: Int!, $part: RegexExPart!, $solution: String!) {
  me(userJwt: $userJwt) {
    regexExercise(collId: $collId, exId: $exId) {
      correct(part: $part, solution: $solution) {
        ...RegexCorrectionResult
      }
    }
  }
}
    ${RegexCorrectionResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class RegexCorrectionGQL extends Apollo.Mutation<RegexCorrectionMutation, RegexCorrectionMutationVariables> {
    document = RegexCorrectionDocument;
    
  }