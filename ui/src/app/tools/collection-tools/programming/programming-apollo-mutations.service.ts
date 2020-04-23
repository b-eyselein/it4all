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

export type ProgrammingCorrectionMutationVariables = {
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
  part: Types.ProgExPart;
  solution: Types.ProgSolutionInput;
};


export type ProgrammingCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { correctProgramming?: Types.Maybe<(
    { __typename?: 'ProgCompleteResult' }
    & ProgrammingCompleteResultFragment
  )> }
);

export type ProgrammingCompleteResultFragment = (
  { __typename?: 'ProgCompleteResult' }
  & Pick<Types.ProgCompleteResult, 'solutionSaved'>
  & { normalResult?: Types.Maybe<{ __typename: 'NormalExecutionResult' }>, unitTestResults: Array<{ __typename: 'UnitTestCorrectionResult' }> }
);

export const AbstractCorrectionResultFragmentDoc = gql`
    fragment AbstractCorrectionResult on AbstractCorrectionResult {
  solutionSaved
  points
  maxPoints
}
    `;
export const ProgrammingCompleteResultFragmentDoc = gql`
    fragment ProgrammingCompleteResult on ProgCompleteResult {
  solutionSaved
  normalResult {
    __typename
  }
  unitTestResults {
    __typename
  }
}
    `;
export const ProgrammingCorrectionDocument = gql`
    mutation ProgrammingCorrection($collId: Int!, $exId: Int!, $part: ProgExPart!, $solution: ProgSolutionInput!) {
  correctProgramming(collId: $collId, exId: $exId, part: $part, solution: $solution) {
    ...ProgrammingCompleteResult
  }
}
    ${ProgrammingCompleteResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class ProgrammingCorrectionGQL extends Apollo.Mutation<ProgrammingCorrectionMutation, ProgrammingCorrectionMutationVariables> {
    document = ProgrammingCorrectionDocument;
    
  }