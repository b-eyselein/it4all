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

export type Mutation = {
   __typename?: 'Mutation';
  correctProgramming?: Maybe<ProgCompleteResult>;
  correctRegex?: Maybe<AbstractRegexResult>;
  correctRose?: Maybe<RoseCompleteResult>;
  correctSql?: Maybe<SqlResult>;
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
  Insert = 'INSERT',
  Update = 'UPDATE',
  Create = 'CREATE',
  Select = 'SELECT',
  Delete = 'DELETE'
}

export enum SqlExPart {
  SqlSingleExPart = 'SqlSingleExPart'
}

export type SqlQueriesStaticComparison = {
   __typename?: 'SqlQueriesStaticComparison';
  X?: Maybe<Scalars['Int']>;
};

export type SqlResult = {
   __typename?: 'SqlResult';
  staticComparison: SqlQueriesStaticComparison;
  executionResult: SqlExecutionResult;
  solutionSaved: Scalars['Boolean'];
};

export type StringSampleSolution = {
   __typename?: 'StringSampleSolution';
  id: Scalars['Int'];
  sample: Scalars['String'];
};

export enum SuccessType {
  Error = 'ERROR',
  None = 'NONE',
  Partially = 'PARTIALLY',
  Complete = 'COMPLETE'
}

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
  Live = 'LIVE',
  Alpha = 'ALPHA',
  Beta = 'BETA'
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
  Association = 'ASSOCIATION',
  Aggregation = 'AGGREGATION',
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
  Class = 'CLASS',
  Interface = 'INTERFACE',
  Abstract = 'ABSTRACT'
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
  Public = 'PUBLIC',
  Package = 'PACKAGE',
  Protected = 'PROTECTED',
  Private = 'PRIVATE'
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
  Simplified = 'Simplified',
  Normal = 'Normal'
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

export type ProgCorrectionMutationVariables = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: ProgExPart;
  solution: ProgSolutionInput;
};


export type ProgCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { correctProgramming?: Maybe<(
    { __typename?: 'ProgCompleteResult' }
    & ProgCompleteResultFragment
  )> }
);

export type ProgCompleteResultFragment = (
  { __typename?: 'ProgCompleteResult' }
  & Pick<ProgCompleteResult, 'solutionSaved'>
  & { normalResult?: Maybe<{ __typename: 'NormalExecutionResult' }>, unitTestResults: Array<{ __typename: 'UnitTestCorrectionResult' }> }
);

export type RegexCorrectionMutationVariables = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: RegexExPart;
  solution: Scalars['String'];
};


export type RegexCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { correctRegex?: Maybe<(
    { __typename?: 'RegexIllegalRegexResult' }
    & RegexIllegalRegexResultFragment
  ) | (
    { __typename?: 'RegexMatchingResult' }
    & RegexMatchingResultFragment
  ) | (
    { __typename?: 'RegexExtractionResult' }
    & RegexExtractionResultFragment
  )> }
);

export type RegexIllegalRegexResultFragment = (
  { __typename?: 'RegexIllegalRegexResult' }
  & Pick<RegexIllegalRegexResult, 'solutionSaved' | 'message'>
  & { points: (
    { __typename?: 'Points' }
    & PointsFragment
  ), maxPoints: (
    { __typename?: 'Points' }
    & PointsFragment
  ) }
);

export type RegexMatchingSingleResultFragment = (
  { __typename?: 'RegexMatchingSingleResult' }
  & Pick<RegexMatchingSingleResult, 'resultType' | 'matchData'>
);

export type RegexMatchingResultFragment = (
  { __typename?: 'RegexMatchingResult' }
  & Pick<RegexMatchingResult, 'solutionSaved'>
  & { matchingResults: Array<(
    { __typename?: 'RegexMatchingSingleResult' }
    & RegexMatchingSingleResultFragment
  )>, points: (
    { __typename?: 'Points' }
    & PointsFragment
  ), maxPoints: (
    { __typename?: 'Points' }
    & PointsFragment
  ) }
);

export type RegexExtractionSingleResultFragment = (
  { __typename?: 'RegexExtractionSingleResult' }
  & Pick<RegexExtractionSingleResult, 'base' | 'correct'>
);

export type RegexExtractionResultFragment = (
  { __typename?: 'RegexExtractionResult' }
  & Pick<RegexExtractionResult, 'solutionSaved'>
  & { extractionResults: Array<(
    { __typename?: 'RegexExtractionSingleResult' }
    & RegexExtractionSingleResultFragment
  )>, points: (
    { __typename?: 'Points' }
    & PointsFragment
  ), maxPoints: (
    { __typename?: 'Points' }
    & PointsFragment
  ) }
);

export type SqlCorrectionMutationVariables = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: SqlExPart;
  solution: Scalars['String'];
};


export type SqlCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { correctSql?: Maybe<(
    { __typename?: 'SqlResult' }
    & SqlResultFragment
  )> }
);

export type SqlResultFragment = (
  { __typename?: 'SqlResult' }
  & Pick<SqlResult, 'solutionSaved'>
  & { staticComparison: { __typename: 'SqlQueriesStaticComparison' }, executionResult: { __typename: 'SqlExecutionResult' } }
);

export type UmlCorrectionMutationVariables = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: UmlExPart;
  solution: UmlClassDiagramInput;
};


export type UmlCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { correctUml?: Maybe<(
    { __typename?: 'UmlCompleteResult' }
    & UmlCompleteResultFragment
  )> }
);

export type UmlCompleteResultFragment = (
  { __typename?: 'UmlCompleteResult' }
  & Pick<UmlCompleteResult, 'solutionSaved'>
  & { points: (
    { __typename?: 'Points' }
    & PointsFragment
  ), maxPoints: (
    { __typename?: 'Points' }
    & PointsFragment
  ) }
);

export type WebCorrectionMutationVariables = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: WebExPart;
  solution: Array<ExerciseFileInput>;
};


export type WebCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { correctWeb?: Maybe<(
    { __typename?: 'WebCompleteResult' }
    & WebCompleteResultFragment
  )> }
);

export type WebCompleteResultFragment = (
  { __typename?: 'WebCompleteResult' }
  & Pick<WebCompleteResult, 'solutionSaved'>
  & { points: (
    { __typename?: 'Points' }
    & PointsFragment
  ), maxPoints: (
    { __typename?: 'Points' }
    & PointsFragment
  ) }
);

export type XmlCorrectionMutationVariables = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: XmlExPart;
  solution: XmlSolutionInput;
};


export type XmlCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { correctXml?: Maybe<(
    { __typename?: 'XmlCompleteResult' }
    & XmlCompleteResultFragment
  )> }
);

export type XmlCompleteResultFragment = (
  { __typename?: 'XmlCompleteResult' }
  & Pick<XmlCompleteResult, 'solutionSaved' | 'successType'>
  & { points: (
    { __typename?: 'Points' }
    & PointsFragment
  ), maxPoints: (
    { __typename?: 'Points' }
    & PointsFragment
  ), grammarResult?: Maybe<{ __typename: 'XmlGrammarResult' }>, documentResult: Array<{ __typename: 'XmlError' }> }
);

export type PointsFragment = (
  { __typename?: 'Points' }
  & Pick<Points, 'quarters'>
);

export const ProgCompleteResultFragmentDoc = gql`
    fragment ProgCompleteResult on ProgCompleteResult {
  solutionSaved
  normalResult {
    __typename
  }
  unitTestResults {
    __typename
  }
}
    `;
export const PointsFragmentDoc = gql`
    fragment Points on Points {
  quarters
}
    `;
export const RegexIllegalRegexResultFragmentDoc = gql`
    fragment RegexIllegalRegexResult on RegexIllegalRegexResult {
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
export const RegexMatchingSingleResultFragmentDoc = gql`
    fragment RegexMatchingSingleResult on RegexMatchingSingleResult {
  resultType
  matchData
}
    `;
export const RegexMatchingResultFragmentDoc = gql`
    fragment RegexMatchingResult on RegexMatchingResult {
  solutionSaved
  matchingResults {
    ...RegexMatchingSingleResult
  }
  points {
    ...Points
  }
  maxPoints {
    ...Points
  }
}
    ${RegexMatchingSingleResultFragmentDoc}
${PointsFragmentDoc}`;
export const RegexExtractionSingleResultFragmentDoc = gql`
    fragment RegexExtractionSingleResult on RegexExtractionSingleResult {
  base
  correct
}
    `;
export const RegexExtractionResultFragmentDoc = gql`
    fragment RegexExtractionResult on RegexExtractionResult {
  solutionSaved
  extractionResults {
    ...RegexExtractionSingleResult
  }
  points {
    ...Points
  }
  maxPoints {
    ...Points
  }
}
    ${RegexExtractionSingleResultFragmentDoc}
${PointsFragmentDoc}`;
export const SqlResultFragmentDoc = gql`
    fragment SqlResult on SqlResult {
  solutionSaved
  staticComparison {
    __typename
  }
  executionResult {
    __typename
  }
}
    `;
export const UmlCompleteResultFragmentDoc = gql`
    fragment UmlCompleteResult on UmlCompleteResult {
  solutionSaved
  points {
    ...Points
  }
  maxPoints {
    ...Points
  }
}
    ${PointsFragmentDoc}`;
export const WebCompleteResultFragmentDoc = gql`
    fragment WebCompleteResult on WebCompleteResult {
  solutionSaved
  points {
    ...Points
  }
  maxPoints {
    ...Points
  }
}
    ${PointsFragmentDoc}`;
export const XmlCompleteResultFragmentDoc = gql`
    fragment XmlCompleteResult on XmlCompleteResult {
  solutionSaved
  successType
  points {
    ...Points
  }
  maxPoints {
    ...Points
  }
  grammarResult {
    __typename
  }
  documentResult {
    __typename
  }
}
    ${PointsFragmentDoc}`;
export const ProgCorrectionDocument = gql`
    mutation ProgCorrection($collId: Int!, $exId: Int!, $part: ProgExPart!, $solution: ProgSolutionInput!) {
  correctProgramming(collId: $collId, exId: $exId, part: $part, solution: $solution) {
    ...ProgCompleteResult
  }
}
    ${ProgCompleteResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class ProgCorrectionGQL extends Apollo.Mutation<ProgCorrectionMutation, ProgCorrectionMutationVariables> {
    document = ProgCorrectionDocument;
    
  }
export const RegexCorrectionDocument = gql`
    mutation RegexCorrection($collId: Int!, $exId: Int!, $part: RegexExPart!, $solution: String!) {
  correctRegex(collId: $collId, exId: $exId, part: $part, solution: $solution) {
    ...RegexIllegalRegexResult
    ...RegexMatchingResult
    ...RegexExtractionResult
  }
}
    ${RegexIllegalRegexResultFragmentDoc}
${RegexMatchingResultFragmentDoc}
${RegexExtractionResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class RegexCorrectionGQL extends Apollo.Mutation<RegexCorrectionMutation, RegexCorrectionMutationVariables> {
    document = RegexCorrectionDocument;
    
  }
export const SqlCorrectionDocument = gql`
    mutation SqlCorrection($collId: Int!, $exId: Int!, $part: SqlExPart!, $solution: String!) {
  correctSql(collId: $collId, exId: $exId, part: $part, solution: $solution) {
    ...SqlResult
  }
}
    ${SqlResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class SqlCorrectionGQL extends Apollo.Mutation<SqlCorrectionMutation, SqlCorrectionMutationVariables> {
    document = SqlCorrectionDocument;
    
  }
export const UmlCorrectionDocument = gql`
    mutation UmlCorrection($collId: Int!, $exId: Int!, $part: UmlExPart!, $solution: UmlClassDiagramInput!) {
  correctUml(collId: $collId, exId: $exId, part: $part, solution: $solution) {
    ...UmlCompleteResult
  }
}
    ${UmlCompleteResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class UmlCorrectionGQL extends Apollo.Mutation<UmlCorrectionMutation, UmlCorrectionMutationVariables> {
    document = UmlCorrectionDocument;
    
  }
export const WebCorrectionDocument = gql`
    mutation WebCorrection($collId: Int!, $exId: Int!, $part: WebExPart!, $solution: [ExerciseFileInput!]!) {
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
export const XmlCorrectionDocument = gql`
    mutation XmlCorrection($collId: Int!, $exId: Int!, $part: XmlExPart!, $solution: XmlSolutionInput!) {
  correctXml(collId: $collId, exId: $exId, part: $part, solution: $solution) {
    ...XmlCompleteResult
  }
}
    ${XmlCompleteResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class XmlCorrectionGQL extends Apollo.Mutation<XmlCorrectionMutation, XmlCorrectionMutationVariables> {
    document = XmlCorrectionDocument;
    
  }