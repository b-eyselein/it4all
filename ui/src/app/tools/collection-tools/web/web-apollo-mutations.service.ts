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
  & { gradedHtmlTaskResults: Array<(
    { __typename?: 'GradedHtmlTaskResult' }
    & GradedHtmlTaskResultFragment
  )> }
  & AbstractCorrectionResult_WebCompleteResult_Fragment
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

export const AbstractCorrectionResultFragmentDoc = gql`
    fragment AbstractCorrectionResult on AbstractCorrectionResult {
  solutionSaved
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
export const WebCompleteResultFragmentDoc = gql`
    fragment WebCompleteResult on WebCompleteResult {
  ...AbstractCorrectionResult
  gradedHtmlTaskResults {
    ...GradedHtmlTaskResult
  }
}
    ${AbstractCorrectionResultFragmentDoc}
${GradedHtmlTaskResultFragmentDoc}`;
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