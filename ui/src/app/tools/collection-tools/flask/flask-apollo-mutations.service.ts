import * as Types from '../../../_interfaces/graphql-types';

import gql from 'graphql-tag';
import { Injectable } from '@angular/core';
import * as Apollo from 'apollo-angular';
export type FlaskCorrectionMutationVariables = Types.Exact<{
  userJwt: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
  part: Types.FlaskExercisePart;
  solution: Types.FilesSolutionInput;
}>;


export type FlaskCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { me?: Types.Maybe<(
    { __typename?: 'UserMutations' }
    & { flaskExercise?: Types.Maybe<(
      { __typename?: 'FlaskExerciseMutations' }
      & { correct: (
        { __typename?: 'FlaskCorrectionResult' }
        & FlaskCorrectionResultFragment
      ) }
    )> }
  )> }
);

export type FlaskCorrectionResultFragment = (
  { __typename?: 'FlaskCorrectionResult' }
  & Pick<Types.FlaskCorrectionResult, 'solutionSaved' | 'resultSaved' | 'proficienciesUpdated'>
  & { result: (
    { __typename?: 'FlaskInternalErrorResult' }
    & FlaskAbstractCorrectionResult_FlaskInternalErrorResult_Fragment
  ) | (
    { __typename?: 'FlaskResult' }
    & FlaskAbstractCorrectionResult_FlaskResult_Fragment
  ) }
);

type FlaskAbstractCorrectionResult_FlaskInternalErrorResult_Fragment = (
  { __typename: 'FlaskInternalErrorResult' }
  & Pick<Types.FlaskInternalErrorResult, 'points' | 'maxPoints'>
  & FlaskInternalErrorResultFragment
);

type FlaskAbstractCorrectionResult_FlaskResult_Fragment = (
  { __typename: 'FlaskResult' }
  & Pick<Types.FlaskResult, 'points' | 'maxPoints'>
  & FlaskResultFragment
);

export type FlaskAbstractCorrectionResultFragment = FlaskAbstractCorrectionResult_FlaskInternalErrorResult_Fragment | FlaskAbstractCorrectionResult_FlaskResult_Fragment;

export type FlaskInternalErrorResultFragment = (
  { __typename?: 'FlaskInternalErrorResult' }
  & Pick<Types.FlaskInternalErrorResult, 'msg'>
);

export type FlaskResultFragment = (
  { __typename?: 'FlaskResult' }
  & { testResults: Array<(
    { __typename?: 'FlaskTestResult' }
    & FlaskTestResultFragment
  )> }
);

export type FlaskTestResultFragment = (
  { __typename?: 'FlaskTestResult' }
  & Pick<Types.FlaskTestResult, 'testName' | 'successful' | 'stdout' | 'stderr'>
);

export const FlaskInternalErrorResultFragmentDoc = gql`
    fragment FlaskInternalErrorResult on FlaskInternalErrorResult {
  msg
}
    `;
export const FlaskTestResultFragmentDoc = gql`
    fragment FlaskTestResult on FlaskTestResult {
  testName
  successful
  stdout
  stderr
}
    `;
export const FlaskResultFragmentDoc = gql`
    fragment FlaskResult on FlaskResult {
  testResults {
    ...FlaskTestResult
  }
}
    ${FlaskTestResultFragmentDoc}`;
export const FlaskAbstractCorrectionResultFragmentDoc = gql`
    fragment FlaskAbstractCorrectionResult on FlaskAbstractCorrectionResult {
  __typename
  points
  maxPoints
  ...FlaskInternalErrorResult
  ...FlaskResult
}
    ${FlaskInternalErrorResultFragmentDoc}
${FlaskResultFragmentDoc}`;
export const FlaskCorrectionResultFragmentDoc = gql`
    fragment FlaskCorrectionResult on FlaskCorrectionResult {
  solutionSaved
  resultSaved
  proficienciesUpdated
  result {
    ...FlaskAbstractCorrectionResult
  }
}
    ${FlaskAbstractCorrectionResultFragmentDoc}`;
export const FlaskCorrectionDocument = gql`
    mutation FlaskCorrection($userJwt: String!, $collId: Int!, $exId: Int!, $part: FlaskExercisePart!, $solution: FilesSolutionInput!) {
  me(userJwt: $userJwt) {
    flaskExercise(collId: $collId, exId: $exId) {
      correct(part: $part, solution: $solution) {
        ...FlaskCorrectionResult
      }
    }
  }
}
    ${FlaskCorrectionResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class FlaskCorrectionGQL extends Apollo.Mutation<FlaskCorrectionMutation, FlaskCorrectionMutationVariables> {
    document = FlaskCorrectionDocument;
    
  }