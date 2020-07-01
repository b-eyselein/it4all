import * as Types from '../../../_interfaces/graphql-types';

import gql from 'graphql-tag';
import { Injectable } from '@angular/core';
import * as Apollo from 'apollo-angular';

export type ProgrammingCorrectionMutationVariables = Types.Exact<{
  userJwt: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
  part: Types.ProgExPart;
  solution: Types.ProgSolutionInput;
}>;


export type ProgrammingCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { me?: Types.Maybe<(
    { __typename?: 'UserMutations' }
    & { correctProgramming: (
      { __typename: 'ProgrammingInternalErrorResult' }
      & Pick<Types.ProgrammingInternalErrorResult, 'solutionSaved' | 'points' | 'maxPoints'>
      & ProgrammingInternalErrorResultFragment
    ) | (
      { __typename: 'ProgrammingResult' }
      & Pick<Types.ProgrammingResult, 'solutionSaved' | 'points' | 'maxPoints'>
      & ProgrammingResultFragment
    ) }
  )> }
);

export type ProgrammingInternalErrorResultFragment = (
  { __typename?: 'ProgrammingInternalErrorResult' }
  & Pick<Types.ProgrammingInternalErrorResult, 'msg'>
);

export type ProgrammingResultFragment = (
  { __typename?: 'ProgrammingResult' }
  & { simplifiedResults: Array<(
    { __typename?: 'SimplifiedExecutionResult' }
    & SimplifiedExecutionResultFragment
  )>, normalResult?: Types.Maybe<(
    { __typename?: 'NormalExecutionResult' }
    & NormalExecutionResultFragment
  )>, unitTestResults: Array<(
    { __typename?: 'UnitTestCorrectionResult' }
    & UnitTestCorrectionResultFragment
  )> }
);

export type SimplifiedExecutionResultFragment = (
  { __typename?: 'SimplifiedExecutionResult' }
  & Pick<Types.SimplifiedExecutionResult, 'id' | 'success' | 'input' | 'awaited' | 'gotten'>
);

export type NormalExecutionResultFragment = (
  { __typename?: 'NormalExecutionResult' }
  & Pick<Types.NormalExecutionResult, 'success' | 'logs'>
);

export type UnitTestCorrectionResultFragment = (
  { __typename?: 'UnitTestCorrectionResult' }
  & Pick<Types.UnitTestCorrectionResult, 'successful' | 'file' | 'status' | 'stderr' | 'stdout'>
  & { testConfig: (
    { __typename?: 'UnitTestTestConfig' }
    & Pick<Types.UnitTestTestConfig, 'description' | 'id' | 'shouldFail'>
  ) }
);

export const ProgrammingInternalErrorResultFragmentDoc = gql`
    fragment ProgrammingInternalErrorResult on ProgrammingInternalErrorResult {
  msg
}
    `;
export const SimplifiedExecutionResultFragmentDoc = gql`
    fragment SimplifiedExecutionResult on SimplifiedExecutionResult {
  id
  success
  input
  awaited
  gotten
}
    `;
export const NormalExecutionResultFragmentDoc = gql`
    fragment NormalExecutionResult on NormalExecutionResult {
  success
  logs
}
    `;
export const UnitTestCorrectionResultFragmentDoc = gql`
    fragment UnitTestCorrectionResult on UnitTestCorrectionResult {
  successful
  file
  status
  stderr
  stdout
  testConfig {
    description
    id
    shouldFail
  }
}
    `;
export const ProgrammingResultFragmentDoc = gql`
    fragment ProgrammingResult on ProgrammingResult {
  simplifiedResults {
    ...SimplifiedExecutionResult
  }
  normalResult {
    ...NormalExecutionResult
  }
  unitTestResults {
    ...UnitTestCorrectionResult
  }
}
    ${SimplifiedExecutionResultFragmentDoc}
${NormalExecutionResultFragmentDoc}
${UnitTestCorrectionResultFragmentDoc}`;
export const ProgrammingCorrectionDocument = gql`
    mutation ProgrammingCorrection($userJwt: String!, $collId: Int!, $exId: Int!, $part: ProgExPart!, $solution: ProgSolutionInput!) {
  me(userJwt: $userJwt) {
    correctProgramming(collId: $collId, exId: $exId, part: $part, solution: $solution) {
      __typename
      solutionSaved
      points
      maxPoints
      ...ProgrammingInternalErrorResult
      ...ProgrammingResult
    }
  }
}
    ${ProgrammingInternalErrorResultFragmentDoc}
${ProgrammingResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class ProgrammingCorrectionGQL extends Apollo.Mutation<ProgrammingCorrectionMutation, ProgrammingCorrectionMutationVariables> {
    document = ProgrammingCorrectionDocument;
    
  }