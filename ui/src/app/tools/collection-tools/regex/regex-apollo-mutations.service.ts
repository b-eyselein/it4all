import * as Types from '../../../_interfaces/graphql-types';

import gql from 'graphql-tag';
import { Injectable } from '@angular/core';
import * as Apollo from 'apollo-angular';

export type RegexCorrectionMutationVariables = {
  userJwt: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
  part: Types.RegexExPart;
  solution: Types.Scalars['String'];
};


export type RegexCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { me?: Types.Maybe<(
    { __typename?: 'UserMutations' }
    & { correctRegex: (
      { __typename: 'RegexExtractionResult' }
      & Pick<Types.RegexExtractionResult, 'solutionSaved' | 'points' | 'maxPoints'>
      & RegexExtractionResultFragment
    ) | (
      { __typename: 'RegexInternalErrorResult' }
      & Pick<Types.RegexInternalErrorResult, 'solutionSaved' | 'points' | 'maxPoints'>
      & RegexInternalErrorResultFragment
    ) | (
      { __typename: 'RegexMatchingResult' }
      & Pick<Types.RegexMatchingResult, 'solutionSaved' | 'points' | 'maxPoints'>
      & RegexMatchingResultFragment
    ) }
  )> }
);

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
export const RegexCorrectionDocument = gql`
    mutation RegexCorrection($userJwt: String!, $collId: Int!, $exId: Int!, $part: RegexExPart!, $solution: String!) {
  me(userJwt: $userJwt) {
    correctRegex(collId: $collId, exId: $exId, part: $part, solution: $solution) {
      __typename
      solutionSaved
      points
      maxPoints
      ...RegexInternalErrorResult
      ...RegexMatchingResult
      ...RegexExtractionResult
    }
  }
}
    ${RegexInternalErrorResultFragmentDoc}
${RegexMatchingResultFragmentDoc}
${RegexExtractionResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class RegexCorrectionGQL extends Apollo.Mutation<RegexCorrectionMutation, RegexCorrectionMutationVariables> {
    document = RegexCorrectionDocument;
    
  }