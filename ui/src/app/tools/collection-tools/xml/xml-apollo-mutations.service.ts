import * as Types from '../../../_interfaces/graphql-types';

import gql from 'graphql-tag';
import { Injectable } from '@angular/core';
import * as Apollo from 'apollo-angular';

export type XmlCorrectionMutationVariables = Types.Exact<{
  userJwt: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
  part: Types.XmlExPart;
  solution: Types.XmlSolutionInput;
}>;


export type XmlCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { me?: Types.Maybe<(
    { __typename?: 'UserMutations' }
    & { xmlExercise?: Types.Maybe<(
      { __typename?: 'XmlExerciseMutations' }
      & { correct: (
        { __typename?: 'XmlCorrectionResult' }
        & XmlCorrectionResultFragment
      ) }
    )> }
  )> }
);

export type XmlCorrectionResultFragment = (
  { __typename?: 'XmlCorrectionResult' }
  & Pick<Types.XmlCorrectionResult, 'solutionSaved' | 'proficienciesUpdated'>
  & { result: (
    { __typename?: 'XmlInternalErrorResult' }
    & XmlAbstractResult_XmlInternalErrorResult_Fragment
    & XmlInternalErrorResultFragment
  ) | (
    { __typename?: 'XmlResult' }
    & XmlAbstractResult_XmlResult_Fragment
    & XmlResultFragment
  ) }
);

type XmlAbstractResult_XmlInternalErrorResult_Fragment = (
  { __typename: 'XmlInternalErrorResult' }
  & Pick<Types.XmlInternalErrorResult, 'points' | 'maxPoints'>
);

type XmlAbstractResult_XmlResult_Fragment = (
  { __typename: 'XmlResult' }
  & Pick<Types.XmlResult, 'points' | 'maxPoints'>
);

export type XmlAbstractResultFragment = XmlAbstractResult_XmlInternalErrorResult_Fragment | XmlAbstractResult_XmlResult_Fragment;

export type XmlInternalErrorResultFragment = (
  { __typename?: 'XmlInternalErrorResult' }
  & Pick<Types.XmlInternalErrorResult, 'msg'>
);

export type XmlResultFragment = (
  { __typename?: 'XmlResult' }
  & Pick<Types.XmlResult, 'successType'>
  & { grammarResult?: Types.Maybe<(
    { __typename?: 'XmlGrammarResult' }
    & XmlGrammarResultFragment
  )>, documentResult?: Types.Maybe<(
    { __typename?: 'XmlDocumentResult' }
    & XmlDocumentResultFragment
  )> }
);

export type XmlGrammarResultFragment = (
  { __typename?: 'XmlGrammarResult' }
  & { parseErrors: Array<(
    { __typename?: 'DTDParseException' }
    & Pick<Types.DtdParseException, 'msg' | 'parsedLine'>
  )>, results: (
    { __typename?: 'XmlElementLineComparisonMatchingResult' }
    & Pick<Types.XmlElementLineComparisonMatchingResult, 'points' | 'maxPoints'>
    & XmlElementLineMatchingResultFragment
  ) }
);

export type XmlElementLineMatchingResultFragment = (
  { __typename?: 'XmlElementLineComparisonMatchingResult' }
  & { allMatches: Array<(
    { __typename?: 'ElementLineMatch' }
    & XmlElementLineMatchFragment
  )> }
);

export type XmlElementLineMatchFragment = (
  { __typename?: 'ElementLineMatch' }
  & Pick<Types.ElementLineMatch, 'matchType'>
  & { userArg?: Types.Maybe<(
    { __typename?: 'ElementLine' }
    & ElementLineFragment
  )>, sampleArg?: Types.Maybe<(
    { __typename?: 'ElementLine' }
    & ElementLineFragment
  )>, maybeAnalysisResult?: Types.Maybe<(
    { __typename?: 'ElementLineAnalysisResult' }
    & XmlElementLineAnalysisResultFragment
  )> }
);

export type XmlElementLineAnalysisResultFragment = (
  { __typename?: 'ElementLineAnalysisResult' }
  & Pick<Types.ElementLineAnalysisResult, 'attributesCorrect' | 'correctAttributes' | 'contentCorrect' | 'correctContent'>
);

export type ElementLineFragment = (
  { __typename?: 'ElementLine' }
  & Pick<Types.ElementLine, 'elementName'>
  & { elementDefinition: (
    { __typename?: 'ElementDefinition' }
    & Pick<Types.ElementDefinition, 'elementName' | 'content'>
  ), attributeLists: Array<(
    { __typename?: 'AttributeList' }
    & Pick<Types.AttributeList, 'elementName' | 'attributeDefinitions'>
  )> }
);

export type XmlDocumentResultFragment = (
  { __typename?: 'XmlDocumentResult' }
  & { errors: Array<(
    { __typename?: 'XmlError' }
    & XmlErrorFragment
  )> }
);

export type XmlErrorFragment = (
  { __typename?: 'XmlError' }
  & Pick<Types.XmlError, 'success' | 'line' | 'errorType' | 'errorMessage'>
);

export const XmlAbstractResultFragmentDoc = gql`
    fragment XmlAbstractResult on XmlAbstractResult {
  __typename
  points
  maxPoints
}
    `;
export const XmlInternalErrorResultFragmentDoc = gql`
    fragment XmlInternalErrorResult on XmlInternalErrorResult {
  msg
}
    `;
export const ElementLineFragmentDoc = gql`
    fragment ElementLine on ElementLine {
  elementName
  elementDefinition {
    elementName
    content
  }
  attributeLists {
    elementName
    attributeDefinitions
  }
}
    `;
export const XmlElementLineAnalysisResultFragmentDoc = gql`
    fragment XmlElementLineAnalysisResult on ElementLineAnalysisResult {
  attributesCorrect
  correctAttributes
  contentCorrect
  correctContent
}
    `;
export const XmlElementLineMatchFragmentDoc = gql`
    fragment XmlElementLineMatch on ElementLineMatch {
  matchType
  userArg {
    ...ElementLine
  }
  sampleArg {
    ...ElementLine
  }
  maybeAnalysisResult {
    ...XmlElementLineAnalysisResult
  }
}
    ${ElementLineFragmentDoc}
${XmlElementLineAnalysisResultFragmentDoc}`;
export const XmlElementLineMatchingResultFragmentDoc = gql`
    fragment XmlElementLineMatchingResult on XmlElementLineComparisonMatchingResult {
  allMatches {
    ...XmlElementLineMatch
  }
}
    ${XmlElementLineMatchFragmentDoc}`;
export const XmlGrammarResultFragmentDoc = gql`
    fragment XmlGrammarResult on XmlGrammarResult {
  parseErrors {
    msg
    parsedLine
  }
  results {
    points
    maxPoints
    ...XmlElementLineMatchingResult
  }
}
    ${XmlElementLineMatchingResultFragmentDoc}`;
export const XmlErrorFragmentDoc = gql`
    fragment XmlError on XmlError {
  success
  line
  errorType
  errorMessage
}
    `;
export const XmlDocumentResultFragmentDoc = gql`
    fragment XmlDocumentResult on XmlDocumentResult {
  errors {
    ...XmlError
  }
}
    ${XmlErrorFragmentDoc}`;
export const XmlResultFragmentDoc = gql`
    fragment XmlResult on XmlResult {
  successType
  grammarResult {
    ...XmlGrammarResult
  }
  documentResult {
    ...XmlDocumentResult
  }
}
    ${XmlGrammarResultFragmentDoc}
${XmlDocumentResultFragmentDoc}`;
export const XmlCorrectionResultFragmentDoc = gql`
    fragment XmlCorrectionResult on XmlCorrectionResult {
  solutionSaved
  proficienciesUpdated
  result {
    ...XmlAbstractResult
    ...XmlInternalErrorResult
    ...XmlResult
  }
}
    ${XmlAbstractResultFragmentDoc}
${XmlInternalErrorResultFragmentDoc}
${XmlResultFragmentDoc}`;
export const XmlCorrectionDocument = gql`
    mutation XmlCorrection($userJwt: String!, $collId: Int!, $exId: Int!, $part: XmlExPart!, $solution: XmlSolutionInput!) {
  me(userJwt: $userJwt) {
    xmlExercise(collId: $collId, exId: $exId) {
      correct(part: $part, solution: $solution) {
        ...XmlCorrectionResult
      }
    }
  }
}
    ${XmlCorrectionResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class XmlCorrectionGQL extends Apollo.Mutation<XmlCorrectionMutation, XmlCorrectionMutationVariables> {
    document = XmlCorrectionDocument;
    
  }