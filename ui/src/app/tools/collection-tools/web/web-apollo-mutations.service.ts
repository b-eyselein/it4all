import * as Types from '../../../_interfaces/graphql-types';

import gql from 'graphql-tag';
import { Injectable } from '@angular/core';
import * as Apollo from 'apollo-angular';

export type WebCorrectionMutationVariables = Types.Exact<{
  userJwt: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
  part: Types.WebExPart;
  solution: Types.WebSolutionInput;
}>;


export type WebCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { me?: Types.Maybe<(
    { __typename?: 'UserMutations' }
    & { webExercise?: Types.Maybe<(
      { __typename?: 'WebExerciseMutations' }
      & { correct: (
        { __typename?: 'WebCorrectionResult' }
        & WebCorrectionResultFragment
      ) }
    )> }
  )> }
);

export type WebCorrectionResultFragment = (
  { __typename?: 'WebCorrectionResult' }
  & Pick<Types.WebCorrectionResult, 'solutionSaved' | 'proficienciesUpdated'>
  & { result: (
    { __typename?: 'WebInternalErrorResult' }
    & WebAbstractResult_WebInternalErrorResult_Fragment
    & WebInternalErrorResultFragment
  ) | (
    { __typename?: 'WebResult' }
    & WebAbstractResult_WebResult_Fragment
    & WebResultFragment
  ) }
);

type WebAbstractResult_WebInternalErrorResult_Fragment = (
  { __typename: 'WebInternalErrorResult' }
  & Pick<Types.WebInternalErrorResult, 'points' | 'maxPoints'>
);

type WebAbstractResult_WebResult_Fragment = (
  { __typename: 'WebResult' }
  & Pick<Types.WebResult, 'points' | 'maxPoints'>
);

export type WebAbstractResultFragment = WebAbstractResult_WebInternalErrorResult_Fragment | WebAbstractResult_WebResult_Fragment;

export type WebInternalErrorResultFragment = (
  { __typename?: 'WebInternalErrorResult' }
  & Pick<Types.WebInternalErrorResult, 'msg'>
);

export type WebResultFragment = (
  { __typename?: 'WebResult' }
  & { gradedHtmlTaskResults: Array<(
    { __typename?: 'GradedHtmlTaskResult' }
    & GradedHtmlTaskResultFragment
  )>, gradedJsTaskResults: Array<(
    { __typename?: 'GradedJsTaskResult' }
    & GradedJsTaskResultFragment
  )> }
);

export type GradedHtmlTaskResultFragment = (
  { __typename?: 'GradedHtmlTaskResult' }
  & Pick<Types.GradedHtmlTaskResult, 'id' | 'success' | 'elementFound' | 'isSuccessful' | 'points' | 'maxPoints'>
  & { textContentResult?: Types.Maybe<(
    { __typename?: 'GradedTextResult' }
    & GradedTextContentResultFragment
  )>, attributeResults: Array<(
    { __typename?: 'GradedTextResult' }
    & GradedTextContentResultFragment
  )> }
);

export type GradedTextContentResultFragment = (
  { __typename?: 'GradedTextResult' }
  & Pick<Types.GradedTextResult, 'keyName' | 'awaitedContent' | 'maybeFoundContent' | 'isSuccessful' | 'points' | 'maxPoints'>
);

export type GradedJsTaskResultFragment = (
  { __typename?: 'GradedJsTaskResult' }
  & Pick<Types.GradedJsTaskResult, 'id' | 'success' | 'points' | 'maxPoints'>
  & { gradedPreResults: Array<(
    { __typename?: 'GradedJsHtmlElementSpecResult' }
    & GradedJsHtmlElementSpecResultFragment
  )>, gradedJsActionResult: (
    { __typename?: 'GradedJsActionResult' }
    & GradedJsActionResultFragment
  ), gradedPostResults: Array<(
    { __typename?: 'GradedJsHtmlElementSpecResult' }
    & GradedJsHtmlElementSpecResultFragment
  )> }
);

export type GradedJsHtmlElementSpecResultFragment = (
  { __typename?: 'GradedJsHtmlElementSpecResult' }
  & Pick<Types.GradedJsHtmlElementSpecResult, 'id'>
);

export type GradedJsActionResultFragment = (
  { __typename?: 'GradedJsActionResult' }
  & Pick<Types.GradedJsActionResult, 'actionPerformed' | 'points' | 'maxPoints'>
  & { jsAction: { __typename: 'JsAction' } }
);

export const WebAbstractResultFragmentDoc = gql`
    fragment WebAbstractResult on WebAbstractResult {
  __typename
  points
  maxPoints
}
    `;
export const GradedTextContentResultFragmentDoc = gql`
    fragment GradedTextContentResult on GradedTextResult {
  keyName
  awaitedContent
  maybeFoundContent
  isSuccessful
  points
  maxPoints
}
    `;
export const GradedHtmlTaskResultFragmentDoc = gql`
    fragment GradedHtmlTaskResult on GradedHtmlTaskResult {
  id
  success
  elementFound
  textContentResult {
    ...GradedTextContentResult
  }
  attributeResults {
    ...GradedTextContentResult
  }
  isSuccessful
  points
  maxPoints
}
    ${GradedTextContentResultFragmentDoc}`;
export const GradedJsHtmlElementSpecResultFragmentDoc = gql`
    fragment GradedJsHtmlElementSpecResult on GradedJsHtmlElementSpecResult {
  id
}
    `;
export const GradedJsActionResultFragmentDoc = gql`
    fragment GradedJsActionResult on GradedJsActionResult {
  jsAction {
    __typename
  }
  actionPerformed
  points
  maxPoints
}
    `;
export const GradedJsTaskResultFragmentDoc = gql`
    fragment GradedJsTaskResult on GradedJsTaskResult {
  id
  gradedPreResults {
    ...GradedJsHtmlElementSpecResult
  }
  gradedJsActionResult {
    ...GradedJsActionResult
  }
  gradedPostResults {
    ...GradedJsHtmlElementSpecResult
  }
  success
  points
  maxPoints
}
    ${GradedJsHtmlElementSpecResultFragmentDoc}
${GradedJsActionResultFragmentDoc}`;
export const WebResultFragmentDoc = gql`
    fragment WebResult on WebResult {
  gradedHtmlTaskResults {
    ...GradedHtmlTaskResult
  }
  gradedJsTaskResults {
    ...GradedJsTaskResult
  }
}
    ${GradedHtmlTaskResultFragmentDoc}
${GradedJsTaskResultFragmentDoc}`;
export const WebInternalErrorResultFragmentDoc = gql`
    fragment WebInternalErrorResult on WebInternalErrorResult {
  msg
}
    `;
export const WebCorrectionResultFragmentDoc = gql`
    fragment WebCorrectionResult on WebCorrectionResult {
  solutionSaved
  proficienciesUpdated
  result {
    ...WebAbstractResult
    ...WebResult
    ...WebInternalErrorResult
  }
}
    ${WebAbstractResultFragmentDoc}
${WebResultFragmentDoc}
${WebInternalErrorResultFragmentDoc}`;
export const WebCorrectionDocument = gql`
    mutation WebCorrection($userJwt: String!, $collId: Int!, $exId: Int!, $part: WebExPart!, $solution: WebSolutionInput!) {
  me(userJwt: $userJwt) {
    webExercise(collId: $collId, exId: $exId) {
      correct(part: $part, solution: $solution) {
        ...WebCorrectionResult
      }
    }
  }
}
    ${WebCorrectionResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class WebCorrectionGQL extends Apollo.Mutation<WebCorrectionMutation, WebCorrectionMutationVariables> {
    document = WebCorrectionDocument;
    
  }