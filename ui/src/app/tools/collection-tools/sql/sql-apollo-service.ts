import gql from 'graphql-tag';
import { Injectable } from '@angular/core';
import * as Apollo from 'apollo-angular';
export type Maybe<T> = T | null;
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: string;
  String: string;
  Boolean: boolean;
  Int: number;
  Float: number;
};



export type AbstractCorrectionResult = {
  solutionSaved: Scalars['Boolean'];
  points: Points;
  maxPoints: Points;
};

export type AbstractRegexResult = RegexIllegalRegexResult | RegexMatchingResult | RegexExtractionResult;

export enum BinaryClassificationResultType {
  FalseNegative = 'FalseNegative',
  FalsePositive = 'FalsePositive',
  TrueNegative = 'TrueNegative',
  TruePositive = 'TruePositive'
}

export type Collection = {
   __typename?: 'Collection';
  id: Scalars['Int'];
  title: Scalars['String'];
  authors: Array<Scalars['String']>;
  text: Scalars['String'];
  shortName: Scalars['String'];
  exerciseCount: Scalars['Int'];
  exercises: Array<Exercise>;
  exercise?: Maybe<Exercise>;
};


export type CollectionExerciseArgs = {
  exId: Scalars['Int'];
};

export type ColumnMatch = {
   __typename?: 'ColumnMatch';
  matchType: MatchType;
  userArg?: Maybe<ColumnWrapper>;
  sampleArg?: Maybe<ColumnWrapper>;
};

export type ColumnWrapper = {
   __typename?: 'ColumnWrapper';
  name: Scalars['String'];
};

export type DtdParseException = {
   __typename?: 'DTDParseException';
  msg: Scalars['String'];
  parsedLine: Scalars['String'];
};

export type ExContent = ProgExerciseContent | RegexExerciseContent | RoseExerciseContent | SqlExerciseContent | UmlExerciseContent | WebExerciseContent | XmlExerciseContent;

export type Exercise = {
   __typename?: 'Exercise';
  id: Scalars['Int'];
  collectionId: Scalars['Int'];
  toolId: Scalars['String'];
  semanticVersion: SemanticVersion;
  title: Scalars['String'];
  authors: Array<Scalars['String']>;
  text: Scalars['String'];
  tags: Array<ExTag>;
  difficulty?: Maybe<Scalars['Int']>;
};

export type ExerciseFile = {
   __typename?: 'ExerciseFile';
  name: Scalars['String'];
  resourcePath: Scalars['String'];
  fileType: Scalars['String'];
  editable: Scalars['Boolean'];
  content: Scalars['String'];
};

export type ExerciseFileInput = {
  name: Scalars['String'];
  resourcePath: Scalars['String'];
  fileType: Scalars['String'];
  editable: Scalars['Boolean'];
  content: Scalars['String'];
};

export type ExTag = {
   __typename?: 'ExTag';
  abbreviation: Scalars['String'];
  title: Scalars['String'];
};

export type HtmlTask = {
   __typename?: 'HtmlTask';
  text: Scalars['String'];
};

export type ImplementationPart = {
   __typename?: 'ImplementationPart';
  base: Scalars['String'];
  files: Array<ExerciseFile>;
  implFileName: Scalars['String'];
  sampleSolFileNames: Array<Scalars['String']>;
};

export type KeyValueObject = {
   __typename?: 'KeyValueObject';
  key: Scalars['String'];
  value: Scalars['String'];
};

export type Lesson = {
   __typename?: 'Lesson';
  id: Scalars['Int'];
  toolId: Scalars['String'];
  title: Scalars['String'];
  description: Scalars['String'];
};

export enum MatchType {
  SuccessfulMatch = 'SUCCESSFUL_MATCH',
  PartialMatch = 'PARTIAL_MATCH',
  OnlyUser = 'ONLY_USER',
  UnsuccessfulMatch = 'UNSUCCESSFUL_MATCH',
  OnlySample = 'ONLY_SAMPLE'
}

export type Mutation = {
   __typename?: 'Mutation';
  correctProgramming?: Maybe<ProgCompleteResult>;
  correctRegex?: Maybe<AbstractRegexResult>;
  correctRose?: Maybe<RoseCompleteResult>;
  correctSql?: Maybe<SqlAbstractResult>;
  correctUml?: Maybe<UmlCompleteResult>;
  correctWeb?: Maybe<WebCompleteResult>;
  correctXml?: Maybe<XmlCompleteResult>;
};


export type MutationCorrectProgrammingArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: ProgExPart;
  solution: ProgSolutionInput;
};


export type MutationCorrectRegexArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: RegexExPart;
  solution: Scalars['String'];
};


export type MutationCorrectRoseArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: RoseExPart;
  solution: Scalars['String'];
};


export type MutationCorrectSqlArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: SqlExPart;
  solution: Scalars['String'];
};


export type MutationCorrectUmlArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: UmlExPart;
  solution: UmlClassDiagramInput;
};


export type MutationCorrectWebArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: WebExPart;
  solution: Array<ExerciseFileInput>;
};


export type MutationCorrectXmlArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: XmlExPart;
  solution: XmlSolutionInput;
};

export type NormalExecutionResult = {
   __typename?: 'NormalExecutionResult';
  success: SuccessType;
  logs: Scalars['String'];
};

export type Points = {
   __typename?: 'Points';
  quarters: Scalars['Int'];
};

export type ProgCompleteResult = {
   __typename?: 'ProgCompleteResult';
  solutionSaved: Scalars['Boolean'];
  normalResult?: Maybe<NormalExecutionResult>;
  unitTestResults: Array<UnitTestCorrectionResult>;
};

export type ProgExerciseContent = {
   __typename?: 'ProgExerciseContent';
  functionName: Scalars['String'];
  foldername: Scalars['String'];
  filename: Scalars['String'];
  unitTestPart: UnitTestPart;
  implementationPart: ImplementationPart;
  sampleSolutions: Array<ProgSampleSolution>;
};

export enum ProgExPart {
  TestCreation = 'TestCreation',
  Implementation = 'Implementation',
  ActivityDiagram = 'ActivityDiagram'
}

export type ProgSampleSolution = {
   __typename?: 'ProgSampleSolution';
  id: Scalars['Int'];
  sample: ProgSolution;
};

export type ProgSolution = {
   __typename?: 'ProgSolution';
  files: Array<ExerciseFile>;
};

export type ProgSolutionInput = {
  files: Array<ExerciseFileInput>;
};

export type Query = {
   __typename?: 'Query';
  tools: Array<Tool>;
  tool?: Maybe<Tool>;
};


export type QueryToolArgs = {
  toolId: Scalars['String'];
};

export enum RegexCorrectionType {
  Extraction = 'EXTRACTION',
  Matching = 'MATCHING'
}

export type RegexExerciseContent = {
   __typename?: 'RegexExerciseContent';
  maxPoints: Scalars['Int'];
  correctionType: RegexCorrectionType;
  sampleSolutions: Array<StringSampleSolution>;
  matchTestData: Array<RegexMatchTestData>;
  extractionTestData: Array<RegexExtractionTestData>;
};

export enum RegexExPart {
  RegexSingleExPart = 'RegexSingleExPart'
}

export type RegexExtractionResult = AbstractCorrectionResult & {
   __typename?: 'RegexExtractionResult';
  solutionSaved: Scalars['Boolean'];
  extractionResults: Array<RegexExtractionSingleResult>;
  points: Points;
  maxPoints: Points;
};

export type RegexExtractionSingleResult = {
   __typename?: 'RegexExtractionSingleResult';
  base: Scalars['String'];
  correct: Scalars['Boolean'];
};

export type RegexExtractionTestData = {
   __typename?: 'RegexExtractionTestData';
  id: Scalars['Int'];
  base: Scalars['String'];
};

export type RegexIllegalRegexResult = AbstractCorrectionResult & {
   __typename?: 'RegexIllegalRegexResult';
  solutionSaved: Scalars['Boolean'];
  message: Scalars['String'];
  maxPoints: Points;
  points: Points;
};

export type RegexMatchingResult = AbstractCorrectionResult & {
   __typename?: 'RegexMatchingResult';
  solutionSaved: Scalars['Boolean'];
  matchingResults: Array<RegexMatchingSingleResult>;
  points: Points;
  maxPoints: Points;
};

export type RegexMatchingSingleResult = {
   __typename?: 'RegexMatchingSingleResult';
  matchData: Scalars['String'];
  isIncluded: Scalars['Boolean'];
  resultType: BinaryClassificationResultType;
};

export type RegexMatchTestData = {
   __typename?: 'RegexMatchTestData';
  id: Scalars['Int'];
  data: Scalars['String'];
  isIncluded: Scalars['Boolean'];
};

export type RoseCompleteResult = {
   __typename?: 'RoseCompleteResult';
  points: Points;
  maxPoints: Points;
  solutionSaved: Scalars['Boolean'];
};

export type RoseExerciseContent = {
   __typename?: 'RoseExerciseContent';
  fieldWidth: Scalars['Int'];
  fieldHeight: Scalars['Int'];
  isMultiplayer: Scalars['Boolean'];
  sampleSolutions: Array<StringSampleSolution>;
};

export enum RoseExPart {
  RoseSingleExPart = 'RoseSingleExPart'
}

export type SemanticVersion = {
   __typename?: 'SemanticVersion';
  major: Scalars['Int'];
  minor: Scalars['Int'];
  patch: Scalars['Int'];
};

export type SiteSpec = {
   __typename?: 'SiteSpec';
  fileName: Scalars['String'];
  htmlTasks: Array<HtmlTask>;
  htmlTaskCount: Scalars['Int'];
  jsTaskCount: Scalars['Int'];
};

export type SqlAbstractResult = SqlIllegalQueryResult | SqlWrongQueryTypeResult | SqlResult;

export type SqlColumnComparisonMatchingResult = {
   __typename?: 'SqlColumnComparisonMatchingResult';
  matchName: Scalars['String'];
  matchSingularName: Scalars['String'];
  points: Points;
  maxPoints: Points;
  allMatches: Array<ColumnMatch>;
};

export type SqlExecutionResult = {
   __typename?: 'SqlExecutionResult';
  X?: Maybe<Scalars['Int']>;
};

export type SqlExerciseContent = {
   __typename?: 'SqlExerciseContent';
  exerciseType: SqlExerciseType;
  hint?: Maybe<Scalars['String']>;
  sampleSolutions: Array<StringSampleSolution>;
};

export enum SqlExerciseType {
  Select = 'SELECT',
  Delete = 'DELETE',
  Insert = 'INSERT',
  Update = 'UPDATE',
  Create = 'CREATE'
}

export enum SqlExPart {
  SqlSingleExPart = 'SqlSingleExPart'
}

export type SqlIllegalQueryResult = AbstractCorrectionResult & {
   __typename?: 'SqlIllegalQueryResult';
  solutionSaved: Scalars['Boolean'];
  message: Scalars['String'];
  maxPoints: Points;
  points: Points;
};

export type SqlQueriesStaticComparison = {
   __typename?: 'SqlQueriesStaticComparison';
  columnComparison: SqlColumnComparisonMatchingResult;
  tableComparison: SqlTableComparisonMatchingResult;
};

export type SqlResult = AbstractCorrectionResult & {
   __typename?: 'SqlResult';
  staticComparison: SqlQueriesStaticComparison;
  executionResult: SqlExecutionResult;
  solutionSaved: Scalars['Boolean'];
  points: Points;
  maxPoints: Points;
};

export type SqlTableComparisonMatchingResult = {
   __typename?: 'SqlTableComparisonMatchingResult';
  matchName: Scalars['String'];
  matchSingularName: Scalars['String'];
  points: Points;
  maxPoints: Points;
  allMatches: Array<TableMatch>;
};

export type SqlWrongQueryTypeResult = AbstractCorrectionResult & {
   __typename?: 'SqlWrongQueryTypeResult';
  solutionSaved: Scalars['Boolean'];
  message: Scalars['String'];
  maxPoints: Points;
  points: Points;
};

export type StringSampleSolution = {
   __typename?: 'StringSampleSolution';
  id: Scalars['Int'];
  sample: Scalars['String'];
};

export enum SuccessType {
  Complete = 'COMPLETE',
  Error = 'ERROR',
  None = 'NONE',
  Partially = 'PARTIALLY'
}

export type Table = {
   __typename?: 'Table';
  name: Scalars['String'];
};

export type TableMatch = {
   __typename?: 'TableMatch';
  matchType: MatchType;
  userArg?: Maybe<Table>;
  sampleArg?: Maybe<Table>;
};

export type Tool = {
   __typename?: 'Tool';
  id: Scalars['String'];
  name: Scalars['String'];
  state: ToolState;
  lessonCount: Scalars['Int'];
  lessons: Array<Lesson>;
  lesson?: Maybe<Lesson>;
  collectionCount: Scalars['Int'];
  collections: Array<Collection>;
  collection?: Maybe<Collection>;
  exerciseCount: Scalars['Int'];
  allExerciseMetaData: Array<Exercise>;
  exerciseContent?: Maybe<ExContent>;
};


export type ToolLessonArgs = {
  lessonId: Scalars['Int'];
};


export type ToolCollectionArgs = {
  collId: Scalars['Int'];
};


export type ToolExerciseContentArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
};

export enum ToolState {
  Alpha = 'ALPHA',
  Beta = 'BETA',
  Live = 'LIVE'
}

export type UmlAssociation = {
   __typename?: 'UmlAssociation';
  assocType: UmlAssociationType;
  assocName?: Maybe<Scalars['String']>;
  firstEnd: Scalars['String'];
  firstMult: UmlMultiplicity;
  secondEnd: Scalars['String'];
  secondMult: UmlMultiplicity;
};

export type UmlAssociationInput = {
  assocType: UmlAssociationType;
  assocName?: Maybe<Scalars['String']>;
  firstEnd: Scalars['String'];
  firstMult: UmlMultiplicity;
  secondEnd: Scalars['String'];
  secondMult: UmlMultiplicity;
};

export enum UmlAssociationType {
  Aggregation = 'AGGREGATION',
  Association = 'ASSOCIATION',
  Composition = 'COMPOSITION'
}

export type UmlAttribute = {
   __typename?: 'UmlAttribute';
  visibility: UmlVisibility;
  memberName: Scalars['String'];
  memberType: Scalars['String'];
  isStatic: Scalars['Boolean'];
  isDerived: Scalars['Boolean'];
  isAbstract: Scalars['Boolean'];
};

export type UmlAttributeInput = {
  visibility: UmlVisibility;
  memberName: Scalars['String'];
  memberType: Scalars['String'];
  isStatic?: Maybe<Scalars['Boolean']>;
  isDerived?: Maybe<Scalars['Boolean']>;
  isAbstract?: Maybe<Scalars['Boolean']>;
};

export type UmlClass = {
   __typename?: 'UmlClass';
  classType: UmlClassType;
  name: Scalars['String'];
  attributes: Array<UmlAttribute>;
  methods: Array<UmlMethod>;
};

export type UmlClassDiagram = {
   __typename?: 'UmlClassDiagram';
  classes: Array<UmlClass>;
  associations: Array<UmlAssociation>;
  implementations: Array<UmlImplementation>;
};

export type UmlClassDiagramInput = {
  classes: Array<UmlClassInput>;
  associations: Array<UmlAssociationInput>;
  implementations: Array<UmlImplementationInput>;
};

export type UmlClassInput = {
  classType: UmlClassType;
  name: Scalars['String'];
  attributes: Array<UmlAttributeInput>;
  methods: Array<UmlMethodInput>;
};

export enum UmlClassType {
  Abstract = 'ABSTRACT',
  Class = 'CLASS',
  Interface = 'INTERFACE'
}

export type UmlCompleteResult = {
   __typename?: 'UmlCompleteResult';
  points: Points;
  maxPoints: Points;
  solutionSaved: Scalars['Boolean'];
};

export type UmlExerciseContent = {
   __typename?: 'UmlExerciseContent';
  toIgnore: Array<Scalars['String']>;
  sampleSolutions: Array<UmlSampleSolution>;
  mappings: Array<KeyValueObject>;
};

export enum UmlExPart {
  ClassSelection = 'ClassSelection',
  DiagramDrawingHelp = 'DiagramDrawingHelp',
  DiagramDrawing = 'DiagramDrawing',
  MemberAllocation = 'MemberAllocation'
}

export type UmlImplementation = {
   __typename?: 'UmlImplementation';
  subClass: Scalars['String'];
  superClass: Scalars['String'];
};

export type UmlImplementationInput = {
  subClass: Scalars['String'];
  superClass: Scalars['String'];
};

export type UmlMethod = {
   __typename?: 'UmlMethod';
  visibility: UmlVisibility;
  memberName: Scalars['String'];
  memberType: Scalars['String'];
  parameters: Scalars['String'];
  isStatic: Scalars['Boolean'];
  isAbstract: Scalars['Boolean'];
};

export type UmlMethodInput = {
  visibility: UmlVisibility;
  memberName: Scalars['String'];
  memberType: Scalars['String'];
  parameters: Scalars['String'];
  isStatic?: Maybe<Scalars['Boolean']>;
  isAbstract?: Maybe<Scalars['Boolean']>;
};

export enum UmlMultiplicity {
  Single = 'SINGLE',
  Unbound = 'UNBOUND'
}

export type UmlSampleSolution = {
   __typename?: 'UmlSampleSolution';
  id: Scalars['Int'];
  sample: UmlClassDiagram;
};

export enum UmlVisibility {
  Package = 'PACKAGE',
  Private = 'PRIVATE',
  Protected = 'PROTECTED',
  Public = 'PUBLIC'
}

export type UnitTestCorrectionResult = {
   __typename?: 'UnitTestCorrectionResult';
  testConfig: UnitTestTestConfig;
  successful: Scalars['Boolean'];
  file: Scalars['String'];
  status: Scalars['Int'];
  stdout: Array<Scalars['String']>;
  stderr: Array<Scalars['String']>;
};

export type UnitTestPart = {
   __typename?: 'UnitTestPart';
  unitTestType: UnitTestType;
  unitTestsDescription: Scalars['String'];
  unitTestFiles: Array<ExerciseFile>;
  unitTestTestConfigs: Array<UnitTestTestConfig>;
  simplifiedTestMainFile?: Maybe<ExerciseFile>;
  testFileName: Scalars['String'];
  sampleSolFileNames: Array<Scalars['String']>;
};

export type UnitTestTestConfig = {
   __typename?: 'UnitTestTestConfig';
  id: Scalars['Int'];
  shouldFail: Scalars['Boolean'];
  description: Scalars['String'];
  file: ExerciseFile;
};

export enum UnitTestType {
  Normal = 'Normal',
  Simplified = 'Simplified'
}

export type WebCompleteResult = {
   __typename?: 'WebCompleteResult';
  points: Points;
  maxPoints: Points;
  solutionSaved: Scalars['Boolean'];
};

export type WebExerciseContent = {
   __typename?: 'WebExerciseContent';
  htmlText?: Maybe<Scalars['String']>;
  jsText?: Maybe<Scalars['String']>;
  siteSpec: SiteSpec;
  files: Array<ExerciseFile>;
  sampleSolutions: Array<WebSampleSolution>;
};

export enum WebExPart {
  HtmlPart = 'HtmlPart',
  JsPart = 'JsPart'
}

export type WebSampleSolution = {
   __typename?: 'WebSampleSolution';
  id: Scalars['Int'];
  sample: Array<ExerciseFile>;
};

export type XmlCompleteResult = {
   __typename?: 'XmlCompleteResult';
  successType: SuccessType;
  documentResult: Array<XmlError>;
  grammarResult?: Maybe<XmlGrammarResult>;
  points: Points;
  maxPoints: Points;
  solutionSaved: Scalars['Boolean'];
};

export type XmlError = {
   __typename?: 'XmlError';
  errorType: XmlErrorType;
  errorMessage: Scalars['String'];
  line: Scalars['Int'];
  success: SuccessType;
};

export enum XmlErrorType {
  Error = 'ERROR',
  Fatal = 'FATAL',
  Warning = 'WARNING'
}

export type XmlExerciseContent = {
   __typename?: 'XmlExerciseContent';
  grammarDescription: Scalars['String'];
  rootNode: Scalars['String'];
  sampleSolutions: Array<XmlSampleSolution>;
};

export enum XmlExPart {
  GrammarCreationXmlPart = 'GrammarCreationXmlPart',
  DocumentCreationXmlPart = 'DocumentCreationXmlPart'
}

export type XmlGrammarResult = {
   __typename?: 'XmlGrammarResult';
  parseErrors: Array<DtdParseException>;
};

export type XmlSampleSolution = {
   __typename?: 'XmlSampleSolution';
  id: Scalars['Int'];
  sample: XmlSolution;
};

export type XmlSolution = {
   __typename?: 'XmlSolution';
  document: Scalars['String'];
  grammar: Scalars['String'];
};

export type XmlSolutionInput = {
  document: Scalars['String'];
  grammar: Scalars['String'];
};

export type SqlCorrectionMutationVariables = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: SqlExPart;
  solution: Scalars['String'];
};


export type SqlCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { correctSql?: Maybe<(
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
  & Pick<SqlIllegalQueryResult, 'solutionSaved' | 'message'>
  & { points: (
    { __typename?: 'Points' }
    & PointsFragment
  ), maxPoints: (
    { __typename?: 'Points' }
    & PointsFragment
  ) }
);

export type SqlWrongQueryTypeResultFragment = (
  { __typename?: 'SqlWrongQueryTypeResult' }
  & Pick<SqlWrongQueryTypeResult, 'solutionSaved' | 'message'>
  & { points: (
    { __typename?: 'Points' }
    & PointsFragment
  ), maxPoints: (
    { __typename?: 'Points' }
    & PointsFragment
  ) }
);

export type ColumnMatchFragment = (
  { __typename?: 'ColumnMatch' }
  & Pick<ColumnMatch, 'matchType'>
  & { userArg?: Maybe<(
    { __typename?: 'ColumnWrapper' }
    & Pick<ColumnWrapper, 'name'>
  )>, sampleArg?: Maybe<(
    { __typename?: 'ColumnWrapper' }
    & Pick<ColumnWrapper, 'name'>
  )> }
);

export type ColumnComparisonFragment = (
  { __typename?: 'SqlColumnComparisonMatchingResult' }
  & Pick<SqlColumnComparisonMatchingResult, 'matchSingularName' | 'matchName'>
  & { allMatches: Array<(
    { __typename?: 'ColumnMatch' }
    & ColumnMatchFragment
  )>, points: (
    { __typename?: 'Points' }
    & PointsFragment
  ), maxPoints: (
    { __typename?: 'Points' }
    & PointsFragment
  ) }
);

export type TableMatchFragment = (
  { __typename?: 'TableMatch' }
  & Pick<TableMatch, 'matchType'>
  & { userArg?: Maybe<(
    { __typename?: 'Table' }
    & Pick<Table, 'name'>
  )>, sampleArg?: Maybe<(
    { __typename?: 'Table' }
    & Pick<Table, 'name'>
  )> }
);

export type TableComparisonFragment = (
  { __typename?: 'SqlTableComparisonMatchingResult' }
  & Pick<SqlTableComparisonMatchingResult, 'matchSingularName' | 'matchName'>
  & { allMatches: Array<(
    { __typename?: 'TableMatch' }
    & TableMatchFragment
  )>, points: (
    { __typename?: 'Points' }
    & PointsFragment
  ), maxPoints: (
    { __typename?: 'Points' }
    & PointsFragment
  ) }
);

export type SqlResultFragment = (
  { __typename?: 'SqlResult' }
  & Pick<SqlResult, 'solutionSaved'>
  & { staticComparison: (
    { __typename?: 'SqlQueriesStaticComparison' }
    & { columnComparison: (
      { __typename?: 'SqlColumnComparisonMatchingResult' }
      & ColumnComparisonFragment
    ), tableComparison: (
      { __typename?: 'SqlTableComparisonMatchingResult' }
      & TableComparisonFragment
    ) }
  ), executionResult: { __typename: 'SqlExecutionResult' }, points: (
    { __typename?: 'Points' }
    & PointsFragment
  ), maxPoints: (
    { __typename?: 'Points' }
    & PointsFragment
  ) }
);

export type PointsFragment = (
  { __typename?: 'Points' }
  & Pick<Points, 'quarters'>
);

export const PointsFragmentDoc = gql`
    fragment Points on Points {
  quarters
}
    `;
export const SqlIllegalQueryResultFragmentDoc = gql`
    fragment SqlIllegalQueryResult on SqlIllegalQueryResult {
  solutionSaved
  message
  points {
    ...Points
  }
  maxPoints {
    ...Points
  }
}
    ${PointsFragmentDoc}`;
export const SqlWrongQueryTypeResultFragmentDoc = gql`
    fragment SqlWrongQueryTypeResult on SqlWrongQueryTypeResult {
  solutionSaved
  message
  points {
    ...Points
  }
  maxPoints {
    ...Points
  }
}
    ${PointsFragmentDoc}`;
export const ColumnMatchFragmentDoc = gql`
    fragment ColumnMatch on ColumnMatch {
  matchType
  userArg {
    name
  }
  sampleArg {
    name
  }
}
    `;
export const ColumnComparisonFragmentDoc = gql`
    fragment ColumnComparison on SqlColumnComparisonMatchingResult {
  matchSingularName
  matchName
  allMatches {
    ...ColumnMatch
  }
  points {
    ...Points
  }
  maxPoints {
    ...Points
  }
}
    ${ColumnMatchFragmentDoc}
${PointsFragmentDoc}`;
export const TableMatchFragmentDoc = gql`
    fragment TableMatch on TableMatch {
  matchType
  userArg {
    name
  }
  sampleArg {
    name
  }
}
    `;
export const TableComparisonFragmentDoc = gql`
    fragment TableComparison on SqlTableComparisonMatchingResult {
  matchSingularName
  matchName
  allMatches {
    ...TableMatch
  }
  points {
    ...Points
  }
  maxPoints {
    ...Points
  }
}
    ${TableMatchFragmentDoc}
${PointsFragmentDoc}`;
export const SqlResultFragmentDoc = gql`
    fragment SqlResult on SqlResult {
  solutionSaved
  staticComparison {
    columnComparison {
      ...ColumnComparison
    }
    tableComparison {
      ...TableComparison
    }
  }
  executionResult {
    __typename
  }
  points {
    ...Points
  }
  maxPoints {
    ...Points
  }
}
    ${ColumnComparisonFragmentDoc}
${TableComparisonFragmentDoc}
${PointsFragmentDoc}`;
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