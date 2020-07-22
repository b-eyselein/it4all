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
    & { programmingExercise?: Types.Maybe<(
      { __typename?: 'ProgrammingExerciseMutations' }
      & { correct: (
        { __typename?: 'ProgrammingCorrectionResult' }
        & ProgrammingCorrectionResultFragment
      ) }
    )> }
  )> }
);

export type ProgrammingCorrectionResultFragment = (
  { __typename?: 'ProgrammingCorrectionResult' }
  & Pick<Types.ProgrammingCorrectionResult, 'solutionSaved' | 'proficienciesUpdated'>
  & { result: (
    { __typename?: 'ProgrammingInternalErrorResult' }
    & ProgrammingAbstractResult_ProgrammingInternalErrorResult_Fragment
    & ProgrammingInternalErrorResultFragment
  ) | (
    { __typename?: 'ProgrammingResult' }
    & ProgrammingAbstractResult_ProgrammingResult_Fragment
    & ProgrammingResultFragment
  ) }
);

type ProgrammingAbstractResult_ProgrammingInternalErrorResult_Fragment = (
  { __typename: 'ProgrammingInternalErrorResult' }
  & Pick<Types.ProgrammingInternalErrorResult, 'points' | 'maxPoints'>
);

type ProgrammingAbstractResult_ProgrammingResult_Fragment = (
  { __typename: 'ProgrammingResult' }
  & Pick<Types.ProgrammingResult, 'points' | 'maxPoints'>
);

export type ProgrammingAbstractResultFragment = ProgrammingAbstractResult_ProgrammingInternalErrorResult_Fragment | ProgrammingAbstractResult_ProgrammingResult_Fragment;

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

export const ProgrammingAbstractResultFragmentDoc = gql`
    fragment ProgrammingAbstractResult on ProgrammingAbstractResult {
  __typename
  points
  maxPoints
}
    `;
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
export const ProgrammingCorrectionResultFragmentDoc = gql`
    fragment ProgrammingCorrectionResult on ProgrammingCorrectionResult {
  solutionSaved
  proficienciesUpdated
  result {
    ...ProgrammingAbstractResult
    ...ProgrammingInternalErrorResult
    ...ProgrammingResult
  }
}
    ${ProgrammingAbstractResultFragmentDoc}
${ProgrammingInternalErrorResultFragmentDoc}
${ProgrammingResultFragmentDoc}`;
export const ProgrammingCorrectionDocument = gql`
    mutation ProgrammingCorrection($userJwt: String!, $collId: Int!, $exId: Int!, $part: ProgExPart!, $solution: ProgSolutionInput!) {
  me(userJwt: $userJwt) {
    programmingExercise(collId: $collId, exId: $exId) {
      correct(part: $part, solution: $solution) {
        ...ProgrammingCorrectionResult
      }
    }
  }
}
    ${ProgrammingCorrectionResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class ProgrammingCorrectionGQL extends Apollo.Mutation<ProgrammingCorrectionMutation, ProgrammingCorrectionMutationVariables> {
    document = ProgrammingCorrectionDocument;
    
  }