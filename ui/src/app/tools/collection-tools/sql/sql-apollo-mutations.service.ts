import * as Types from '../../../_interfaces/graphql-types';

import gql from 'graphql-tag';
import { Injectable } from '@angular/core';
import * as Apollo from 'apollo-angular';

export type DbContentsQueryVariables = {
  schemaName: Types.Scalars['String'];
};


export type DbContentsQuery = (
  { __typename?: 'Query' }
  & { sqlDbContents: Array<(
    { __typename?: 'SqlQueryResult' }
    & SqlQueryResultFragment
  )> }
);

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

export type SqlCorrectionMutationVariables = {
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
  part: Types.SqlExPart;
  solution: Types.Scalars['String'];
};


export type SqlCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { correctSql?: Types.Maybe<(
    { __typename?: 'SqlIllegalQueryResult' }
    & SqlIllegalQueryResultFragment
  ) | (
    { __typename?: 'SqlWrongQueryTypeResult' }
    & SqlWrongQueryTypeResultFragment
  ) | (
    { __typename?: 'SqlResult' }
    & SqlResultFragment
  )> }
);

export type SqlIllegalQueryResultFragment = (
  { __typename?: 'SqlIllegalQueryResult' }
  & Pick<Types.SqlIllegalQueryResult, 'message'>
  & AbstractCorrectionResult_SqlIllegalQueryResult_Fragment
);

export type SqlWrongQueryTypeResultFragment = (
  { __typename?: 'SqlWrongQueryTypeResult' }
  & Pick<Types.SqlWrongQueryTypeResult, 'message'>
  & AbstractCorrectionResult_SqlWrongQueryTypeResult_Fragment
);

export type ColumnMatchFragment = (
  { __typename?: 'SqlColumnMatch' }
  & Pick<Types.SqlColumnMatch, 'matchType' | 'userArg' | 'sampleArg'>
);

export type ColumnComparisonFragment = (
  { __typename?: 'SqlColumnComparisonMatchingResult' }
  & { allMatches: Array<(
    { __typename?: 'SqlColumnMatch' }
    & ColumnMatchFragment
  )> }
  & Mr_SqlColumnComparisonMatchingResult_Fragment
);

export type TableMatchFragment = (
  { __typename?: 'SqlTableMatch' }
  & Pick<Types.SqlTableMatch, 'matchType' | 'userArg' | 'sampleArg'>
);

export type TableComparisonFragment = (
  { __typename?: 'SqlTableComparisonMatchingResult' }
  & { allMatches: Array<(
    { __typename?: 'SqlTableMatch' }
    & TableMatchFragment
  )> }
  & Mr_SqlTableComparisonMatchingResult_Fragment
);

export type BinaryExpressionMatchFragment = (
  { __typename?: 'SqlBinaryExpressionMatch' }
  & Pick<Types.SqlBinaryExpressionMatch, 'matchType' | 'userArg' | 'sampleArg'>
);

export type BinaryExpressionComparisonFragment = (
  { __typename?: 'SqlBinaryExpressionComparisonMatchingResult' }
  & { allMatches: Array<(
    { __typename?: 'SqlBinaryExpressionMatch' }
    & BinaryExpressionMatchFragment
  )> }
  & Mr_SqlBinaryExpressionComparisonMatchingResult_Fragment
);

export type InsertMatchFragment = (
  { __typename?: 'SqlInsertMatch' }
  & Pick<Types.SqlInsertMatch, 'matchType' | 'userArg' | 'sampleArg'>
);

export type InsertComparisonFragment = (
  { __typename?: 'SqlInsertComparisonMatchingResult' }
  & { allMatches: Array<(
    { __typename?: 'SqlInsertMatch' }
    & InsertMatchFragment
  )> }
  & Mr_SqlInsertComparisonMatchingResult_Fragment
);

export type GroupByMatchFragment = (
  { __typename?: 'SqlGroupByMatch' }
  & Pick<Types.SqlGroupByMatch, 'matchType' | 'sampleArg' | 'userArg'>
);

export type GroupByComparisonFragment = (
  { __typename?: 'SqlGroupByComparisonMatchingResult' }
  & { allMatches: Array<(
    { __typename?: 'SqlGroupByMatch' }
    & GroupByMatchFragment
  )> }
  & Mr_SqlGroupByComparisonMatchingResult_Fragment
);

export type OrderByMatchFragment = (
  { __typename?: 'SqlOrderByMatch' }
  & Pick<Types.SqlOrderByMatch, 'matchType' | 'sampleArg' | 'userArg'>
);

export type OrderByComparisonFragment = (
  { __typename?: 'SqlOrderByComparisonMatchingResult' }
  & { allMatches: Array<(
    { __typename?: 'SqlOrderByMatch' }
    & OrderByMatchFragment
  )> }
  & Mr_SqlOrderByComparisonMatchingResult_Fragment
);

export type LimitMatchFragment = (
  { __typename?: 'SqlLimitMatch' }
  & Pick<Types.SqlLimitMatch, 'matchType' | 'sampleArg' | 'userArg'>
);

export type LimitComparisonFragment = (
  { __typename?: 'SqlLimitComparisonMatchingResult' }
  & { allMatches: Array<(
    { __typename?: 'SqlLimitMatch' }
    & LimitMatchFragment
  )> }
  & Mr_SqlLimitComparisonMatchingResult_Fragment
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
      & Mr_SqlTableComparisonMatchingResult_Fragment
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
  & AbstractCorrectionResult_SqlResult_Fragment
);

export type SqlExecutionResultFragment = (
  { __typename?: 'SqlExecutionResult' }
  & { userResultTry?: Types.Maybe<(
    { __typename?: 'SqlQueryResult' }
    & SqlQueryResultFragment
  )>, sampleResultTry?: Types.Maybe<(
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

type Mr_RegexExtractedValuesComparisonMatchingResult_Fragment = (
  { __typename?: 'RegexExtractedValuesComparisonMatchingResult' }
  & Pick<Types.RegexExtractedValuesComparisonMatchingResult, 'points' | 'maxPoints'>
);

type Mr_SqlColumnComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlColumnComparisonMatchingResult' }
  & Pick<Types.SqlColumnComparisonMatchingResult, 'points' | 'maxPoints'>
);

type Mr_SqlTableComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlTableComparisonMatchingResult' }
  & Pick<Types.SqlTableComparisonMatchingResult, 'points' | 'maxPoints'>
);

type Mr_SqlBinaryExpressionComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlBinaryExpressionComparisonMatchingResult' }
  & Pick<Types.SqlBinaryExpressionComparisonMatchingResult, 'points' | 'maxPoints'>
);

type Mr_SqlGroupByComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlGroupByComparisonMatchingResult' }
  & Pick<Types.SqlGroupByComparisonMatchingResult, 'points' | 'maxPoints'>
);

type Mr_SqlOrderByComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlOrderByComparisonMatchingResult' }
  & Pick<Types.SqlOrderByComparisonMatchingResult, 'points' | 'maxPoints'>
);

type Mr_SqlLimitComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlLimitComparisonMatchingResult' }
  & Pick<Types.SqlLimitComparisonMatchingResult, 'points' | 'maxPoints'>
);

type Mr_SqlInsertComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlInsertComparisonMatchingResult' }
  & Pick<Types.SqlInsertComparisonMatchingResult, 'points' | 'maxPoints'>
);

type Mr_UmlClassMatchingResult_Fragment = (
  { __typename?: 'UmlClassMatchingResult' }
  & Pick<Types.UmlClassMatchingResult, 'points' | 'maxPoints'>
);

type Mr_UmlAttributeMatchingResult_Fragment = (
  { __typename?: 'UmlAttributeMatchingResult' }
  & Pick<Types.UmlAttributeMatchingResult, 'points' | 'maxPoints'>
);

type Mr_UmlMethodMatchingResult_Fragment = (
  { __typename?: 'UmlMethodMatchingResult' }
  & Pick<Types.UmlMethodMatchingResult, 'points' | 'maxPoints'>
);

type Mr_UmlAssociationMatchingResult_Fragment = (
  { __typename?: 'UmlAssociationMatchingResult' }
  & Pick<Types.UmlAssociationMatchingResult, 'points' | 'maxPoints'>
);

type Mr_UmlImplementationMatchingResult_Fragment = (
  { __typename?: 'UmlImplementationMatchingResult' }
  & Pick<Types.UmlImplementationMatchingResult, 'points' | 'maxPoints'>
);

type Mr_XmlElementLineComparisonMatchingResult_Fragment = (
  { __typename?: 'XmlElementLineComparisonMatchingResult' }
  & Pick<Types.XmlElementLineComparisonMatchingResult, 'points' | 'maxPoints'>
);

export type MrFragment = Mr_RegexExtractedValuesComparisonMatchingResult_Fragment | Mr_SqlColumnComparisonMatchingResult_Fragment | Mr_SqlTableComparisonMatchingResult_Fragment | Mr_SqlBinaryExpressionComparisonMatchingResult_Fragment | Mr_SqlGroupByComparisonMatchingResult_Fragment | Mr_SqlOrderByComparisonMatchingResult_Fragment | Mr_SqlLimitComparisonMatchingResult_Fragment | Mr_SqlInsertComparisonMatchingResult_Fragment | Mr_UmlClassMatchingResult_Fragment | Mr_UmlAttributeMatchingResult_Fragment | Mr_UmlMethodMatchingResult_Fragment | Mr_UmlAssociationMatchingResult_Fragment | Mr_UmlImplementationMatchingResult_Fragment | Mr_XmlElementLineComparisonMatchingResult_Fragment;

export const AbstractCorrectionResultFragmentDoc = gql`
    fragment AbstractCorrectionResult on AbstractCorrectionResult {
  solutionSaved
  points
  maxPoints
}
    `;
export const SqlIllegalQueryResultFragmentDoc = gql`
    fragment SqlIllegalQueryResult on SqlIllegalQueryResult {
  ...AbstractCorrectionResult
  message
}
    ${AbstractCorrectionResultFragmentDoc}`;
export const SqlWrongQueryTypeResultFragmentDoc = gql`
    fragment SqlWrongQueryTypeResult on SqlWrongQueryTypeResult {
  ...AbstractCorrectionResult
  message
}
    ${AbstractCorrectionResultFragmentDoc}`;
export const MrFragmentDoc = gql`
    fragment MR on MatchingResult {
  points
  maxPoints
}
    `;
export const ColumnMatchFragmentDoc = gql`
    fragment ColumnMatch on SqlColumnMatch {
  matchType
  userArg
  sampleArg
}
    `;
export const ColumnComparisonFragmentDoc = gql`
    fragment ColumnComparison on SqlColumnComparisonMatchingResult {
  ...MR
  allMatches {
    ...ColumnMatch
  }
}
    ${MrFragmentDoc}
${ColumnMatchFragmentDoc}`;
export const TableMatchFragmentDoc = gql`
    fragment TableMatch on SqlTableMatch {
  matchType
  userArg
  sampleArg
}
    `;
export const TableComparisonFragmentDoc = gql`
    fragment TableComparison on SqlTableComparisonMatchingResult {
  ...MR
  allMatches {
    ...TableMatch
  }
}
    ${MrFragmentDoc}
${TableMatchFragmentDoc}`;
export const BinaryExpressionMatchFragmentDoc = gql`
    fragment BinaryExpressionMatch on SqlBinaryExpressionMatch {
  matchType
  userArg
  sampleArg
}
    `;
export const BinaryExpressionComparisonFragmentDoc = gql`
    fragment BinaryExpressionComparison on SqlBinaryExpressionComparisonMatchingResult {
  ...MR
  allMatches {
    ...BinaryExpressionMatch
  }
}
    ${MrFragmentDoc}
${BinaryExpressionMatchFragmentDoc}`;
export const GroupByMatchFragmentDoc = gql`
    fragment GroupByMatch on SqlGroupByMatch {
  matchType
  sampleArg
  userArg
}
    `;
export const GroupByComparisonFragmentDoc = gql`
    fragment GroupByComparison on SqlGroupByComparisonMatchingResult {
  ...MR
  allMatches {
    ...GroupByMatch
  }
}
    ${MrFragmentDoc}
${GroupByMatchFragmentDoc}`;
export const OrderByMatchFragmentDoc = gql`
    fragment OrderByMatch on SqlOrderByMatch {
  matchType
  sampleArg
  userArg
}
    `;
export const OrderByComparisonFragmentDoc = gql`
    fragment OrderByComparison on SqlOrderByComparisonMatchingResult {
  ...MR
  allMatches {
    ...OrderByMatch
  }
}
    ${MrFragmentDoc}
${OrderByMatchFragmentDoc}`;
export const LimitMatchFragmentDoc = gql`
    fragment LimitMatch on SqlLimitMatch {
  matchType
  sampleArg
  userArg
}
    `;
export const LimitComparisonFragmentDoc = gql`
    fragment LimitComparison on SqlLimitComparisonMatchingResult {
  ...MR
  allMatches {
    ...LimitMatch
  }
}
    ${MrFragmentDoc}
${LimitMatchFragmentDoc}`;
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
export const InsertMatchFragmentDoc = gql`
    fragment InsertMatch on SqlInsertMatch {
  matchType
  userArg
  sampleArg
}
    `;
export const InsertComparisonFragmentDoc = gql`
    fragment InsertComparison on SqlInsertComparisonMatchingResult {
  ...MR
  allMatches {
    ...InsertMatch
  }
}
    ${MrFragmentDoc}
${InsertMatchFragmentDoc}`;
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
  userResultTry {
    ...SqlQueryResult
  }
  sampleResultTry {
    ...SqlQueryResult
  }
}
    ${SqlQueryResultFragmentDoc}`;
export const SqlResultFragmentDoc = gql`
    fragment SqlResult on SqlResult {
  ...AbstractCorrectionResult
  staticComparison {
    columnComparison {
      ...ColumnComparison
    }
    tableComparison {
      ...MR
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
    ${AbstractCorrectionResultFragmentDoc}
${ColumnComparisonFragmentDoc}
${MrFragmentDoc}
${TableComparisonFragmentDoc}
${BinaryExpressionComparisonFragmentDoc}
${SelectAdditionalComparisonFragmentDoc}
${InsertComparisonFragmentDoc}
${SqlExecutionResultFragmentDoc}`;
export const DbContentsDocument = gql`
    query DbContents($schemaName: String!) {
  sqlDbContents(schemaName: $schemaName) {
    ...SqlQueryResult
  }
}
    ${SqlQueryResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class DbContentsGQL extends Apollo.Query<DbContentsQuery, DbContentsQueryVariables> {
    document = DbContentsDocument;
    
  }
export const SqlCorrectionDocument = gql`
    mutation SqlCorrection($collId: Int!, $exId: Int!, $part: SqlExPart!, $solution: String!) {
  correctSql(collId: $collId, exId: $exId, part: $part, solution: $solution) {
    ...SqlIllegalQueryResult
    ...SqlWrongQueryTypeResult
    ...SqlResult
  }
}
    ${SqlIllegalQueryResultFragmentDoc}
${SqlWrongQueryTypeResultFragmentDoc}
${SqlResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class SqlCorrectionGQL extends Apollo.Mutation<SqlCorrectionMutation, SqlCorrectionMutationVariables> {
    document = SqlCorrectionDocument;
    
  }