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

export type FlaskCorrectionResultFragment = { __typename: 'FlaskCorrectionResult' };

export const FlaskCorrectionResultFragmentDoc = gql`
    fragment FlaskCorrectionResult on FlaskCorrectionResult {
  __typename
}
    `;
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