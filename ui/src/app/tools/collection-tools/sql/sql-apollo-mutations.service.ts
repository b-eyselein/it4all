import * as Types from '../../../_interfaces/graphql-types';

import gql from 'graphql-tag';
import { Injectable } from '@angular/core';
import * as Apollo from 'apollo-angular';

export type SqlCorrectionMutationVariables = Types.Exact<{
  userJwt: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
  part: Types.SqlExPart;
  solution: Types.Scalars['String'];
}>;


export type SqlCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { me?: Types.Maybe<(
    { __typename?: 'UserMutations' }
    & { sqlExercise?: Types.Maybe<(
      { __typename?: 'SqlExerciseMutations' }
      & { correct: (
        { __typename?: 'SqlCorrectionResult' }
        & SqlCorrectionResultFragment
      ) }
    )> }
  )> }
);

export type SqlCorrectionResultFragment = (
  { __typename?: 'SqlCorrectionResult' }
  & Pick<Types.SqlCorrectionResult, 'solutionSaved' | 'resultSaved' | 'proficienciesUpdated'>
  & { result: (
    { __typename?: 'SqlInternalErrorResult' }
    & SqlAbstractResult_SqlInternalErrorResult_Fragment
    & SqlInternalErrorResultFragment
  ) | (
    { __typename?: 'SqlResult' }
    & SqlAbstractResult_SqlResult_Fragment
    & SqlResultFragment
  ) }
);

type SqlAbstractResult_SqlInternalErrorResult_Fragment = (
  { __typename: 'SqlInternalErrorResult' }
  & Pick<Types.SqlInternalErrorResult, 'points' | 'maxPoints'>
);

type SqlAbstractResult_SqlResult_Fragment = (
  { __typename: 'SqlResult' }
  & Pick<Types.SqlResult, 'points' | 'maxPoints'>
);

export type SqlAbstractResultFragment = SqlAbstractResult_SqlInternalErrorResult_Fragment | SqlAbstractResult_SqlResult_Fragment;

export type SqlInternalErrorResultFragment = (
  { __typename?: 'SqlInternalErrorResult' }
  & Pick<Types.SqlInternalErrorResult, 'msg'>
);

export type ColumnComparisonFragment = (
  { __typename?: 'SqlColumnComparisonMatchingResult' }
  & SqlMatchingResult_SqlColumnComparisonMatchingResult_Fragment
);

export type TableComparisonFragment = (
  { __typename?: 'SqlTableComparisonMatchingResult' }
  & SqlMatchingResult_SqlTableComparisonMatchingResult_Fragment
);

export type BinaryExpressionComparisonFragment = (
  { __typename?: 'SqlBinaryExpressionComparisonMatchingResult' }
  & SqlMatchingResult_SqlBinaryExpressionComparisonMatchingResult_Fragment
);

export type InsertComparisonFragment = (
  { __typename?: 'SqlInsertComparisonMatchingResult' }
  & SqlMatchingResult_SqlInsertComparisonMatchingResult_Fragment
);

export type GroupByComparisonFragment = (
  { __typename?: 'SqlGroupByComparisonMatchingResult' }
  & SqlMatchingResult_SqlGroupByComparisonMatchingResult_Fragment
);

export type OrderByComparisonFragment = (
  { __typename?: 'SqlOrderByComparisonMatchingResult' }
  & SqlMatchingResult_SqlOrderByComparisonMatchingResult_Fragment
);

export type LimitComparisonFragment = (
  { __typename?: 'SqlLimitComparisonMatchingResult' }
  & SqlMatchingResult_SqlLimitComparisonMatchingResult_Fragment
);

export type SelectAdditionalComparisonFragment = (
  { __typename?: 'SelectAdditionalComparisons' }
  & { groupByComparison: (
    { __typename?: 'SqlGroupByComparisonMatchingResult' }
    & GroupByComparisonFragment
  ), orderByComparison: (
    { __typename?: 'SqlOrderByComparisonMatchingResult' }
    & OrderByComparisonFragment
  ), limitComparison: (
    { __typename?: 'SqlLimitComparisonMatchingResult' }
    & LimitComparisonFragment
  ) }
);

export type SqlResultFragment = (
  { __typename?: 'SqlResult' }
  & { staticComparison: (
    { __typename?: 'SqlQueriesStaticComparison' }
    & { columnComparison: (
      { __typename?: 'SqlColumnComparisonMatchingResult' }
      & ColumnComparisonFragment
    ), tableComparison: (
      { __typename?: 'SqlTableComparisonMatchingResult' }
      & SqlMatchingResult_SqlTableComparisonMatchingResult_Fragment
      & TableComparisonFragment
    ), joinExpressionComparison: (
      { __typename?: 'SqlBinaryExpressionComparisonMatchingResult' }
      & BinaryExpressionComparisonFragment
    ), whereComparison: (
      { __typename?: 'SqlBinaryExpressionComparisonMatchingResult' }
      & BinaryExpressionComparisonFragment
    ), additionalComparisons: (
      { __typename?: 'AdditionalComparison' }
      & { selectComparisons?: Types.Maybe<(
        { __typename?: 'SelectAdditionalComparisons' }
        & SelectAdditionalComparisonFragment
      )>, insertComparison?: Types.Maybe<(
        { __typename?: 'SqlInsertComparisonMatchingResult' }
        & InsertComparisonFragment
      )> }
    ) }
  ), executionResult: (
    { __typename?: 'SqlExecutionResult' }
    & SqlExecutionResultFragment
  ) }
);

export type SqlExecutionResultFragment = (
  { __typename?: 'SqlExecutionResult' }
  & { userResult?: Types.Maybe<(
    { __typename?: 'SqlQueryResult' }
    & SqlQueryResultFragment
  )>, sampleResult?: Types.Maybe<(
    { __typename?: 'SqlQueryResult' }
    & SqlQueryResultFragment
  )> }
);

export type SqlQueryResultFragment = (
  { __typename?: 'SqlQueryResult' }
  & Pick<Types.SqlQueryResult, 'tableName' | 'columnNames'>
  & { rows: Array<(
    { __typename?: 'SqlRow' }
    & SqlRowFragment
  )> }
);

export type SqlRowFragment = (
  { __typename?: 'SqlRow' }
  & { cells: Array<(
    { __typename?: 'SqlKeyCellValueObject' }
    & Pick<Types.SqlKeyCellValueObject, 'key'>
    & { value: (
      { __typename?: 'SqlCell' }
      & SqlCellFragment
    ) }
  )> }
);

export type SqlCellFragment = (
  { __typename?: 'SqlCell' }
  & Pick<Types.SqlCell, 'colName' | 'content' | 'different'>
);

type NewMatch_ElementLineMatch_Fragment = (
  { __typename?: 'ElementLineMatch' }
  & Pick<Types.ElementLineMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_RegexMatchMatch_Fragment = (
  { __typename?: 'RegexMatchMatch' }
  & Pick<Types.RegexMatchMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_SqlBinaryExpressionMatch_Fragment = (
  { __typename?: 'SqlBinaryExpressionMatch' }
  & Pick<Types.SqlBinaryExpressionMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_SqlColumnMatch_Fragment = (
  { __typename?: 'SqlColumnMatch' }
  & Pick<Types.SqlColumnMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_SqlGroupByMatch_Fragment = (
  { __typename?: 'SqlGroupByMatch' }
  & Pick<Types.SqlGroupByMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_SqlInsertMatch_Fragment = (
  { __typename?: 'SqlInsertMatch' }
  & Pick<Types.SqlInsertMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_SqlLimitMatch_Fragment = (
  { __typename?: 'SqlLimitMatch' }
  & Pick<Types.SqlLimitMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_SqlOrderByMatch_Fragment = (
  { __typename?: 'SqlOrderByMatch' }
  & Pick<Types.SqlOrderByMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_SqlTableMatch_Fragment = (
  { __typename?: 'SqlTableMatch' }
  & Pick<Types.SqlTableMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_UmlAssociationMatch_Fragment = (
  { __typename?: 'UmlAssociationMatch' }
  & Pick<Types.UmlAssociationMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_UmlAttributeMatch_Fragment = (
  { __typename?: 'UmlAttributeMatch' }
  & Pick<Types.UmlAttributeMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_UmlClassMatch_Fragment = (
  { __typename?: 'UmlClassMatch' }
  & Pick<Types.UmlClassMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_UmlImplementationMatch_Fragment = (
  { __typename?: 'UmlImplementationMatch' }
  & Pick<Types.UmlImplementationMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_UmlMethodMatch_Fragment = (
  { __typename?: 'UmlMethodMatch' }
  & Pick<Types.UmlMethodMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

export type NewMatchFragment = NewMatch_ElementLineMatch_Fragment | NewMatch_RegexMatchMatch_Fragment | NewMatch_SqlBinaryExpressionMatch_Fragment | NewMatch_SqlColumnMatch_Fragment | NewMatch_SqlGroupByMatch_Fragment | NewMatch_SqlInsertMatch_Fragment | NewMatch_SqlLimitMatch_Fragment | NewMatch_SqlOrderByMatch_Fragment | NewMatch_SqlTableMatch_Fragment | NewMatch_UmlAssociationMatch_Fragment | NewMatch_UmlAttributeMatch_Fragment | NewMatch_UmlClassMatch_Fragment | NewMatch_UmlImplementationMatch_Fragment | NewMatch_UmlMethodMatch_Fragment;

type SqlMatchingResult_RegexExtractedValuesComparisonMatchingResult_Fragment = (
  { __typename?: 'RegexExtractedValuesComparisonMatchingResult' }
  & Pick<Types.RegexExtractedValuesComparisonMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'RegexMatchMatch' }
    & NewMatch_RegexMatchMatch_Fragment
  )> }
);

type SqlMatchingResult_SqlBinaryExpressionComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlBinaryExpressionComparisonMatchingResult' }
  & Pick<Types.SqlBinaryExpressionComparisonMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'SqlBinaryExpressionMatch' }
    & NewMatch_SqlBinaryExpressionMatch_Fragment
  )> }
);

type SqlMatchingResult_SqlColumnComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlColumnComparisonMatchingResult' }
  & Pick<Types.SqlColumnComparisonMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'SqlColumnMatch' }
    & NewMatch_SqlColumnMatch_Fragment
  )> }
);

type SqlMatchingResult_SqlGroupByComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlGroupByComparisonMatchingResult' }
  & Pick<Types.SqlGroupByComparisonMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'SqlGroupByMatch' }
    & NewMatch_SqlGroupByMatch_Fragment
  )> }
);

type SqlMatchingResult_SqlInsertComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlInsertComparisonMatchingResult' }
  & Pick<Types.SqlInsertComparisonMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'SqlInsertMatch' }
    & NewMatch_SqlInsertMatch_Fragment
  )> }
);

type SqlMatchingResult_SqlLimitComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlLimitComparisonMatchingResult' }
  & Pick<Types.SqlLimitComparisonMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'SqlLimitMatch' }
    & NewMatch_SqlLimitMatch_Fragment
  )> }
);

type SqlMatchingResult_SqlOrderByComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlOrderByComparisonMatchingResult' }
  & Pick<Types.SqlOrderByComparisonMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'SqlOrderByMatch' }
    & NewMatch_SqlOrderByMatch_Fragment
  )> }
);

type SqlMatchingResult_SqlTableComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlTableComparisonMatchingResult' }
  & Pick<Types.SqlTableComparisonMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'SqlTableMatch' }
    & NewMatch_SqlTableMatch_Fragment
  )> }
);

type SqlMatchingResult_UmlAssociationMatchingResult_Fragment = (
  { __typename?: 'UmlAssociationMatchingResult' }
  & Pick<Types.UmlAssociationMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'UmlAssociationMatch' }
    & NewMatch_UmlAssociationMatch_Fragment
  )> }
);

type SqlMatchingResult_UmlAttributeMatchingResult_Fragment = (
  { __typename?: 'UmlAttributeMatchingResult' }
  & Pick<Types.UmlAttributeMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'UmlAttributeMatch' }
    & NewMatch_UmlAttributeMatch_Fragment
  )> }
);

type SqlMatchingResult_UmlClassMatchingResult_Fragment = (
  { __typename?: 'UmlClassMatchingResult' }
  & Pick<Types.UmlClassMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'UmlClassMatch' }
    & NewMatch_UmlClassMatch_Fragment
  )> }
);

type SqlMatchingResult_UmlImplementationMatchingResult_Fragment = (
  { __typename?: 'UmlImplementationMatchingResult' }
  & Pick<Types.UmlImplementationMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'UmlImplementationMatch' }
    & NewMatch_UmlImplementationMatch_Fragment
  )> }
);

type SqlMatchingResult_UmlMethodMatchingResult_Fragment = (
  { __typename?: 'UmlMethodMatchingResult' }
  & Pick<Types.UmlMethodMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'UmlMethodMatch' }
    & NewMatch_UmlMethodMatch_Fragment
  )> }
);

type SqlMatchingResult_XmlElementLineComparisonMatchingResult_Fragment = (
  { __typename?: 'XmlElementLineComparisonMatchingResult' }
  & Pick<Types.XmlElementLineComparisonMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'ElementLineMatch' }
    & NewMatch_ElementLineMatch_Fragment
  )> }
);

export type SqlMatchingResultFragment = SqlMatchingResult_RegexExtractedValuesComparisonMatchingResult_Fragment | SqlMatchingResult_SqlBinaryExpressionComparisonMatchingResult_Fragment | SqlMatchingResult_SqlColumnComparisonMatchingResult_Fragment | SqlMatchingResult_SqlGroupByComparisonMatchingResult_Fragment | SqlMatchingResult_SqlInsertComparisonMatchingResult_Fragment | SqlMatchingResult_SqlLimitComparisonMatchingResult_Fragment | SqlMatchingResult_SqlOrderByComparisonMatchingResult_Fragment | SqlMatchingResult_SqlTableComparisonMatchingResult_Fragment | SqlMatchingResult_UmlAssociationMatchingResult_Fragment | SqlMatchingResult_UmlAttributeMatchingResult_Fragment | SqlMatchingResult_UmlClassMatchingResult_Fragment | SqlMatchingResult_UmlImplementationMatchingResult_Fragment | SqlMatchingResult_UmlMethodMatchingResult_Fragment | SqlMatchingResult_XmlElementLineComparisonMatchingResult_Fragment;

export const SqlAbstractResultFragmentDoc = gql`
    fragment SqlAbstractResult on SqlAbstractResult {
  __typename
  points
  maxPoints
}
    `;
export const SqlInternalErrorResultFragmentDoc = gql`
    fragment SqlInternalErrorResult on SqlInternalErrorResult {
  msg
}
    `;
export const NewMatchFragmentDoc = gql`
    fragment NewMatch on NewMatch {
  matchType
  sampleArgDescription
  userArgDescription
}
    `;
export const SqlMatchingResultFragmentDoc = gql`
    fragment SqlMatchingResult on MatchingResult {
  points
  maxPoints
  allMatches {
    ...NewMatch
  }
}
    ${NewMatchFragmentDoc}`;
export const ColumnComparisonFragmentDoc = gql`
    fragment ColumnComparison on SqlColumnComparisonMatchingResult {
  ...SqlMatchingResult
}
    ${SqlMatchingResultFragmentDoc}`;
export const TableComparisonFragmentDoc = gql`
    fragment TableComparison on SqlTableComparisonMatchingResult {
  ...SqlMatchingResult
}
    ${SqlMatchingResultFragmentDoc}`;
export const BinaryExpressionComparisonFragmentDoc = gql`
    fragment BinaryExpressionComparison on SqlBinaryExpressionComparisonMatchingResult {
  ...SqlMatchingResult
}
    ${SqlMatchingResultFragmentDoc}`;
export const GroupByComparisonFragmentDoc = gql`
    fragment GroupByComparison on SqlGroupByComparisonMatchingResult {
  ...SqlMatchingResult
}
    ${SqlMatchingResultFragmentDoc}`;
export const OrderByComparisonFragmentDoc = gql`
    fragment OrderByComparison on SqlOrderByComparisonMatchingResult {
  ...SqlMatchingResult
}
    ${SqlMatchingResultFragmentDoc}`;
export const LimitComparisonFragmentDoc = gql`
    fragment LimitComparison on SqlLimitComparisonMatchingResult {
  ...SqlMatchingResult
}
    ${SqlMatchingResultFragmentDoc}`;
export const SelectAdditionalComparisonFragmentDoc = gql`
    fragment SelectAdditionalComparison on SelectAdditionalComparisons {
  groupByComparison {
    ...GroupByComparison
  }
  orderByComparison {
    ...OrderByComparison
  }
  limitComparison {
    ...LimitComparison
  }
}
    ${GroupByComparisonFragmentDoc}
${OrderByComparisonFragmentDoc}
${LimitComparisonFragmentDoc}`;
export const InsertComparisonFragmentDoc = gql`
    fragment InsertComparison on SqlInsertComparisonMatchingResult {
  ...SqlMatchingResult
}
    ${SqlMatchingResultFragmentDoc}`;
export const SqlCellFragmentDoc = gql`
    fragment SqlCell on SqlCell {
  colName
  content
  different
}
    `;
export const SqlRowFragmentDoc = gql`
    fragment SqlRow on SqlRow {
  cells {
    key
    value {
      ...SqlCell
    }
  }
}
    ${SqlCellFragmentDoc}`;
export const SqlQueryResultFragmentDoc = gql`
    fragment SqlQueryResult on SqlQueryResult {
  tableName
  columnNames
  rows {
    ...SqlRow
  }
}
    ${SqlRowFragmentDoc}`;
export const SqlExecutionResultFragmentDoc = gql`
    fragment SqlExecutionResult on SqlExecutionResult {
  userResult {
    ...SqlQueryResult
  }
  sampleResult {
    ...SqlQueryResult
  }
}
    ${SqlQueryResultFragmentDoc}`;
export const SqlResultFragmentDoc = gql`
    fragment SqlResult on SqlResult {
  staticComparison {
    columnComparison {
      ...ColumnComparison
    }
    tableComparison {
      ...SqlMatchingResult
      ...TableComparison
    }
    joinExpressionComparison {
      ...BinaryExpressionComparison
    }
    whereComparison {
      ...BinaryExpressionComparison
    }
    additionalComparisons {
      selectComparisons {
        ...SelectAdditionalComparison
      }
      insertComparison {
        ...InsertComparison
      }
    }
  }
  executionResult {
    ...SqlExecutionResult
  }
}
    ${ColumnComparisonFragmentDoc}
${SqlMatchingResultFragmentDoc}
${TableComparisonFragmentDoc}
${BinaryExpressionComparisonFragmentDoc}
${SelectAdditionalComparisonFragmentDoc}
${InsertComparisonFragmentDoc}
${SqlExecutionResultFragmentDoc}`;
export const SqlCorrectionResultFragmentDoc = gql`
    fragment SqlCorrectionResult on SqlCorrectionResult {
  solutionSaved
  resultSaved
  proficienciesUpdated
  result {
    ...SqlAbstractResult
    ...SqlInternalErrorResult
    ...SqlResult
  }
}
    ${SqlAbstractResultFragmentDoc}
${SqlInternalErrorResultFragmentDoc}
${SqlResultFragmentDoc}`;
export const SqlCorrectionDocument = gql`
    mutation SqlCorrection($userJwt: String!, $collId: Int!, $exId: Int!, $part: SqlExPart!, $solution: String!) {
  me(userJwt: $userJwt) {
    sqlExercise(collId: $collId, exId: $exId) {
      correct(part: $part, solution: $solution) {
        ...SqlCorrectionResult
      }
    }
  }
}
    ${SqlCorrectionResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class SqlCorrectionGQL extends Apollo.Mutation<SqlCorrectionMutation, SqlCorrectionMutationVariables> {
    document = SqlCorrectionDocument;
    
  }