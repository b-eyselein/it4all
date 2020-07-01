import * as Types from '../../../_interfaces/graphql-types';

import gql from 'graphql-tag';
import { Injectable } from '@angular/core';
import * as Apollo from 'apollo-angular';

export type UmlCorrectionMutationVariables = Types.Exact<{
  userJwt: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
  part: Types.UmlExPart;
  solution: Types.UmlClassDiagramInput;
}>;


export type UmlCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { me?: Types.Maybe<(
    { __typename?: 'UserMutations' }
    & { correctUml: (
      { __typename: 'UmlInternalErrorResult' }
      & Pick<Types.UmlInternalErrorResult, 'solutionSaved' | 'points' | 'maxPoints'>
      & UmlInternalErrorResultFragment
    ) | (
      { __typename: 'UmlResult' }
      & Pick<Types.UmlResult, 'solutionSaved' | 'points' | 'maxPoints'>
      & UmlResultFragment
    ) }
  )> }
);

export type UmlInternalErrorResultFragment = (
  { __typename?: 'UmlInternalErrorResult' }
  & Pick<Types.UmlInternalErrorResult, 'msg'>
);

export type UmlResultFragment = (
  { __typename?: 'UmlResult' }
  & { classResult?: Types.Maybe<(
    { __typename?: 'UmlClassMatchingResult' }
    & UmlClassMatchingResultFragment
  )>, assocResult?: Types.Maybe<(
    { __typename?: 'UmlAssociationMatchingResult' }
    & UmlAssociationMatchingResultFragment
  )>, implResult?: Types.Maybe<(
    { __typename?: 'UmlImplementationMatchingResult' }
    & UmlImplementationMatchingResultFragment
  )> }
);

export type UmlClassMatchingResultFragment = (
  { __typename?: 'UmlClassMatchingResult' }
  & Pick<Types.UmlClassMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'UmlClassMatch' }
    & UmlClassMatchFragment
  )> }
);

export type UmlClassMatchFragment = (
  { __typename?: 'UmlClassMatch' }
  & Pick<Types.UmlClassMatch, 'matchType'>
  & { userArg?: Types.Maybe<(
    { __typename?: 'UmlClass' }
    & UmlSolutionClassFragment
  )>, sampleArg?: Types.Maybe<(
    { __typename?: 'UmlClass' }
    & UmlSolutionClassFragment
  )>, analysisResult?: Types.Maybe<{ __typename: 'UmlClassMatchAnalysisResult' }> }
);

export type UmlSolutionClassFragment = (
  { __typename?: 'UmlClass' }
  & Pick<Types.UmlClass, 'classType' | 'name'>
  & { attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }
);

export type UmlAssociationMatchingResultFragment = (
  { __typename?: 'UmlAssociationMatchingResult' }
  & Pick<Types.UmlAssociationMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'UmlAssociationMatch' }
    & UmlAssociationMatchFragment
  )> }
);

export type UmlAssociationMatchFragment = (
  { __typename?: 'UmlAssociationMatch' }
  & Pick<Types.UmlAssociationMatch, 'matchType'>
  & { userArg?: Types.Maybe<(
    { __typename?: 'UmlAssociation' }
    & UmlAssociationFragment
  )>, sampleArg?: Types.Maybe<(
    { __typename?: 'UmlAssociation' }
    & UmlAssociationFragment
  )>, maybeAnalysisResult?: Types.Maybe<{ __typename: 'UmlAssociationAnalysisResult' }> }
);

export type UmlAssociationFragment = (
  { __typename?: 'UmlAssociation' }
  & Pick<Types.UmlAssociation, 'assocType' | 'assocName' | 'firstEnd' | 'firstMult' | 'secondEnd' | 'secondMult'>
);

export type UmlImplementationMatchingResultFragment = (
  { __typename?: 'UmlImplementationMatchingResult' }
  & Pick<Types.UmlImplementationMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'UmlImplementationMatch' }
    & UmlImplementationMatchFragment
  )> }
);

export type UmlImplementationMatchFragment = (
  { __typename?: 'UmlImplementationMatch' }
  & Pick<Types.UmlImplementationMatch, 'matchType'>
  & { userArg?: Types.Maybe<(
    { __typename?: 'UmlImplementation' }
    & UmlImplementationFragment
  )>, sampleArg?: Types.Maybe<(
    { __typename?: 'UmlImplementation' }
    & UmlImplementationFragment
  )> }
);

export type UmlImplementationFragment = (
  { __typename?: 'UmlImplementation' }
  & Pick<Types.UmlImplementation, 'subClass' | 'superClass'>
);

export const UmlInternalErrorResultFragmentDoc = gql`
    fragment UmlInternalErrorResult on UmlInternalErrorResult {
  msg
}
    `;
export const UmlSolutionClassFragmentDoc = gql`
    fragment UmlSolutionClass on UmlClass {
  classType
  name
  attributes {
    __typename
  }
  methods {
    __typename
  }
}
    `;
export const UmlClassMatchFragmentDoc = gql`
    fragment UmlClassMatch on UmlClassMatch {
  matchType
  userArg {
    ...UmlSolutionClass
  }
  sampleArg {
    ...UmlSolutionClass
  }
  analysisResult {
    __typename
  }
}
    ${UmlSolutionClassFragmentDoc}`;
export const UmlClassMatchingResultFragmentDoc = gql`
    fragment UmlClassMatchingResult on UmlClassMatchingResult {
  allMatches {
    ...UmlClassMatch
  }
  points
  maxPoints
}
    ${UmlClassMatchFragmentDoc}`;
export const UmlAssociationFragmentDoc = gql`
    fragment UmlAssociation on UmlAssociation {
  assocType
  assocName
  firstEnd
  firstMult
  secondEnd
  secondMult
}
    `;
export const UmlAssociationMatchFragmentDoc = gql`
    fragment UmlAssociationMatch on UmlAssociationMatch {
  matchType
  userArg {
    ...UmlAssociation
  }
  sampleArg {
    ...UmlAssociation
  }
  maybeAnalysisResult {
    __typename
  }
}
    ${UmlAssociationFragmentDoc}`;
export const UmlAssociationMatchingResultFragmentDoc = gql`
    fragment UmlAssociationMatchingResult on UmlAssociationMatchingResult {
  allMatches {
    ...UmlAssociationMatch
  }
  points
  maxPoints
}
    ${UmlAssociationMatchFragmentDoc}`;
export const UmlImplementationFragmentDoc = gql`
    fragment UmlImplementation on UmlImplementation {
  subClass
  superClass
}
    `;
export const UmlImplementationMatchFragmentDoc = gql`
    fragment UmlImplementationMatch on UmlImplementationMatch {
  matchType
  userArg {
    ...UmlImplementation
  }
  sampleArg {
    ...UmlImplementation
  }
}
    ${UmlImplementationFragmentDoc}`;
export const UmlImplementationMatchingResultFragmentDoc = gql`
    fragment UmlImplementationMatchingResult on UmlImplementationMatchingResult {
  allMatches {
    ...UmlImplementationMatch
  }
  points
  maxPoints
}
    ${UmlImplementationMatchFragmentDoc}`;
export const UmlResultFragmentDoc = gql`
    fragment UmlResult on UmlResult {
  classResult {
    ...UmlClassMatchingResult
  }
  assocResult {
    ...UmlAssociationMatchingResult
  }
  implResult {
    ...UmlImplementationMatchingResult
  }
}
    ${UmlClassMatchingResultFragmentDoc}
${UmlAssociationMatchingResultFragmentDoc}
${UmlImplementationMatchingResultFragmentDoc}`;
export const UmlCorrectionDocument = gql`
    mutation UmlCorrection($userJwt: String!, $collId: Int!, $exId: Int!, $part: UmlExPart!, $solution: UmlClassDiagramInput!) {
  me(userJwt: $userJwt) {
    correctUml(collId: $collId, exId: $exId, part: $part, solution: $solution) {
      __typename
      solutionSaved
      points
      maxPoints
      ...UmlInternalErrorResult
      ...UmlResult
    }
  }
}
    ${UmlInternalErrorResultFragmentDoc}
${UmlResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class UmlCorrectionGQL extends Apollo.Mutation<UmlCorrectionMutation, UmlCorrectionMutationVariables> {
    document = UmlCorrectionDocument;
    
  }