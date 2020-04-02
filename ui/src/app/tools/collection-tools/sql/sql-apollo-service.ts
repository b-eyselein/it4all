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
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type AbstractRegexResult = RegexIllegalRegexResult | RegexMatchingResult | RegexExtractionResult;

export type AdditionalComparison = {
   __typename?: 'AdditionalComparison';
  selectComparisons?: Maybe<SelectAdditionalComparisons>;
  insertComparison?: Maybe<SqlInsertComparisonMatchingResult>;
};

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

export type ElementLine = {
   __typename?: 'ElementLine';
  todo?: Maybe<Scalars['Int']>;
};

export type ElementLineAnalysisResult = {
   __typename?: 'ElementLineAnalysisResult';
  contentCorrect: Scalars['Boolean'];
  correctContent: Scalars['String'];
  attributesCorrect: Scalars['Boolean'];
  correctAttributes: Scalars['String'];
};

export type ElementLineMatch = NewMatch & {
   __typename?: 'ElementLineMatch';
  matchType: MatchType;
  userArg?: Maybe<ElementLine>;
  sampleArg?: Maybe<ElementLine>;
  maybeAnalysisResult?: Maybe<ElementLineAnalysisResult>;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type ExContent = ProgExerciseContent | RegexExerciseContent | SqlExerciseContent | UmlExerciseContent | WebExerciseContent | XmlExerciseContent;

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

export type MatchingResult = {
  matchName: Scalars['String'];
  matchSingularName: Scalars['String'];
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
  allMatches: Array<NewMatch>;
};

export enum MatchType {
  OnlySample = 'ONLY_SAMPLE',
  PartialMatch = 'PARTIAL_MATCH',
  OnlyUser = 'ONLY_USER',
  SuccessfulMatch = 'SUCCESSFUL_MATCH',
  UnsuccessfulMatch = 'UNSUCCESSFUL_MATCH'
}

export type Mutation = {
   __typename?: 'Mutation';
  correctProgramming?: Maybe<ProgCompleteResult>;
  correctRegex?: Maybe<AbstractRegexResult>;
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

export type NewMatch = {
  matchType: MatchType;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type NormalExecutionResult = {
   __typename?: 'NormalExecutionResult';
  success: SuccessType;
  logs: Scalars['String'];
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

export type RegexExtractedValuesComparisonMatchingResult = MatchingResult & {
   __typename?: 'RegexExtractedValuesComparisonMatchingResult';
  /** @deprecated Will be deleted */
  matchName: Scalars['String'];
  /** @deprecated Will be deleted */
  matchSingularName: Scalars['String'];
  allMatches: Array<RegexMatchMatch>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type RegexExtractionResult = AbstractCorrectionResult & {
   __typename?: 'RegexExtractionResult';
  extractionResults: Array<RegexExtractionSingleResult>;
  solutionSaved: Scalars['Boolean'];
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type RegexExtractionSingleResult = {
   __typename?: 'RegexExtractionSingleResult';
  base: Scalars['String'];
  extractionMatchingResult: RegexExtractedValuesComparisonMatchingResult;
  correct: Scalars['Boolean'];
};

export type RegexExtractionTestData = {
   __typename?: 'RegexExtractionTestData';
  id: Scalars['Int'];
  base: Scalars['String'];
};

export type RegexIllegalRegexResult = AbstractCorrectionResult & {
   __typename?: 'RegexIllegalRegexResult';
  message: Scalars['String'];
  solutionSaved: Scalars['Boolean'];
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type RegexMatchingResult = AbstractCorrectionResult & {
   __typename?: 'RegexMatchingResult';
  matchingResults: Array<RegexMatchingSingleResult>;
  solutionSaved: Scalars['Boolean'];
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type RegexMatchingSingleResult = {
   __typename?: 'RegexMatchingSingleResult';
  matchData: Scalars['String'];
  isIncluded: Scalars['Boolean'];
  resultType: BinaryClassificationResultType;
};

export type RegexMatchMatch = NewMatch & {
   __typename?: 'RegexMatchMatch';
  sampleArg?: Maybe<Scalars['String']>;
  userArg?: Maybe<Scalars['String']>;
  matchType: MatchType;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type RegexMatchTestData = {
   __typename?: 'RegexMatchTestData';
  id: Scalars['Int'];
  data: Scalars['String'];
  isIncluded: Scalars['Boolean'];
};

export type SelectAdditionalComparisons = {
   __typename?: 'SelectAdditionalComparisons';
  groupByComparison: SqlGroupByComparisonMatchingResult;
  orderByComparison: SqlOrderByComparisonMatchingResult;
  limitComparison: SqlLimitComparisonMatchingResult;
};

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

export type SqlBinaryExpressionComparisonMatchingResult = MatchingResult & {
   __typename?: 'SqlBinaryExpressionComparisonMatchingResult';
  /** @deprecated Will be deleted */
  matchName: Scalars['String'];
  /** @deprecated Will be deleted */
  matchSingularName: Scalars['String'];
  allMatches: Array<SqlBinaryExpressionMatch>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type SqlBinaryExpressionMatch = NewMatch & {
   __typename?: 'SqlBinaryExpressionMatch';
  sampleArg?: Maybe<Scalars['String']>;
  userArg?: Maybe<Scalars['String']>;
  matchType: MatchType;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type SqlColumnComparisonMatchingResult = MatchingResult & {
   __typename?: 'SqlColumnComparisonMatchingResult';
  /** @deprecated Will be deleted */
  matchName: Scalars['String'];
  /** @deprecated Will be deleted */
  matchSingularName: Scalars['String'];
  allMatches: Array<SqlColumnMatch>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type SqlColumnMatch = NewMatch & {
   __typename?: 'SqlColumnMatch';
  sampleArg?: Maybe<Scalars['String']>;
  userArg?: Maybe<Scalars['String']>;
  matchType: MatchType;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
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
  Create = 'CREATE',
  Select = 'SELECT',
  Insert = 'INSERT',
  Delete = 'DELETE',
  Update = 'UPDATE'
}

export enum SqlExPart {
  SqlSingleExPart = 'SqlSingleExPart'
}

export type SqlGroupByComparisonMatchingResult = MatchingResult & {
   __typename?: 'SqlGroupByComparisonMatchingResult';
  /** @deprecated Will be deleted */
  matchName: Scalars['String'];
  /** @deprecated Will be deleted */
  matchSingularName: Scalars['String'];
  allMatches: Array<SqlGroupByMatch>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type SqlGroupByMatch = NewMatch & {
   __typename?: 'SqlGroupByMatch';
  sampleArg?: Maybe<Scalars['String']>;
  userArg?: Maybe<Scalars['String']>;
  matchType: MatchType;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type SqlIllegalQueryResult = AbstractCorrectionResult & {
   __typename?: 'SqlIllegalQueryResult';
  message: Scalars['String'];
  solutionSaved: Scalars['Boolean'];
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type SqlInsertComparisonMatchingResult = MatchingResult & {
   __typename?: 'SqlInsertComparisonMatchingResult';
  /** @deprecated Will be deleted */
  matchName: Scalars['String'];
  /** @deprecated Will be deleted */
  matchSingularName: Scalars['String'];
  allMatches: Array<SqlInsertMatch>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type SqlInsertMatch = NewMatch & {
   __typename?: 'SqlInsertMatch';
  sampleArg?: Maybe<Scalars['String']>;
  userArg?: Maybe<Scalars['String']>;
  matchType: MatchType;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type SqlLimitComparisonMatchingResult = MatchingResult & {
   __typename?: 'SqlLimitComparisonMatchingResult';
  /** @deprecated Will be deleted */
  matchName: Scalars['String'];
  /** @deprecated Will be deleted */
  matchSingularName: Scalars['String'];
  allMatches: Array<SqlLimitMatch>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type SqlLimitMatch = NewMatch & {
   __typename?: 'SqlLimitMatch';
  sampleArg?: Maybe<Scalars['String']>;
  userArg?: Maybe<Scalars['String']>;
  matchType: MatchType;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type SqlOrderByComparisonMatchingResult = MatchingResult & {
   __typename?: 'SqlOrderByComparisonMatchingResult';
  /** @deprecated Will be deleted */
  matchName: Scalars['String'];
  /** @deprecated Will be deleted */
  matchSingularName: Scalars['String'];
  allMatches: Array<SqlOrderByMatch>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type SqlOrderByMatch = NewMatch & {
   __typename?: 'SqlOrderByMatch';
  sampleArg?: Maybe<Scalars['String']>;
  userArg?: Maybe<Scalars['String']>;
  matchType: MatchType;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type SqlQueriesStaticComparison = {
   __typename?: 'SqlQueriesStaticComparison';
  columnComparison: SqlColumnComparisonMatchingResult;
  tableComparison: SqlTableComparisonMatchingResult;
  joinExpressionComparison: SqlBinaryExpressionComparisonMatchingResult;
  whereComparison: SqlBinaryExpressionComparisonMatchingResult;
  additionalComparisons: AdditionalComparison;
};

export type SqlResult = AbstractCorrectionResult & {
   __typename?: 'SqlResult';
  staticComparison: SqlQueriesStaticComparison;
  executionResult: SqlExecutionResult;
  solutionSaved: Scalars['Boolean'];
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type SqlTableComparisonMatchingResult = MatchingResult & {
   __typename?: 'SqlTableComparisonMatchingResult';
  /** @deprecated Will be deleted */
  matchName: Scalars['String'];
  /** @deprecated Will be deleted */
  matchSingularName: Scalars['String'];
  allMatches: Array<SqlTableMatch>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type SqlTableMatch = NewMatch & {
   __typename?: 'SqlTableMatch';
  sampleArg?: Maybe<Scalars['String']>;
  userArg?: Maybe<Scalars['String']>;
  matchType: MatchType;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type SqlWrongQueryTypeResult = AbstractCorrectionResult & {
   __typename?: 'SqlWrongQueryTypeResult';
  message: Scalars['String'];
  solutionSaved: Scalars['Boolean'];
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
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

export type UmlCompleteResult = AbstractCorrectionResult & {
   __typename?: 'UmlCompleteResult';
  solutionSaved: Scalars['Boolean'];
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
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

export type WebCompleteResult = AbstractCorrectionResult & {
   __typename?: 'WebCompleteResult';
  solutionSaved: Scalars['Boolean'];
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
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

export type XmlCompleteResult = AbstractCorrectionResult & {
   __typename?: 'XmlCompleteResult';
  successType: SuccessType;
  documentResult: Array<XmlError>;
  grammarResult?: Maybe<XmlGrammarResult>;
  solutionSaved: Scalars['Boolean'];
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type XmlElementLineComparisonMatchingResult = MatchingResult & {
   __typename?: 'XmlElementLineComparisonMatchingResult';
  /** @deprecated Will be deleted */
  matchName: Scalars['String'];
  /** @deprecated Will be deleted */
  matchSingularName: Scalars['String'];
  allMatches: Array<ElementLineMatch>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
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
  results: XmlElementLineComparisonMatchingResult;
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

type AbstractCorrectionResult_RegexIllegalRegexResult_Fragment = (
  { __typename?: 'RegexIllegalRegexResult' }
  & Pick<RegexIllegalRegexResult, 'solutionSaved' | 'points' | 'maxPoints'>
);

type AbstractCorrectionResult_RegexMatchingResult_Fragment = (
  { __typename?: 'RegexMatchingResult' }
  & Pick<RegexMatchingResult, 'solutionSaved' | 'points' | 'maxPoints'>
);

type AbstractCorrectionResult_RegexExtractionResult_Fragment = (
  { __typename?: 'RegexExtractionResult' }
  & Pick<RegexExtractionResult, 'solutionSaved' | 'points' | 'maxPoints'>
);

type AbstractCorrectionResult_SqlIllegalQueryResult_Fragment = (
  { __typename?: 'SqlIllegalQueryResult' }
  & Pick<SqlIllegalQueryResult, 'solutionSaved' | 'points' | 'maxPoints'>
);

type AbstractCorrectionResult_SqlWrongQueryTypeResult_Fragment = (
  { __typename?: 'SqlWrongQueryTypeResult' }
  & Pick<SqlWrongQueryTypeResult, 'solutionSaved' | 'points' | 'maxPoints'>
);

type AbstractCorrectionResult_SqlResult_Fragment = (
  { __typename?: 'SqlResult' }
  & Pick<SqlResult, 'solutionSaved' | 'points' | 'maxPoints'>
);

type AbstractCorrectionResult_UmlCompleteResult_Fragment = (
  { __typename?: 'UmlCompleteResult' }
  & Pick<UmlCompleteResult, 'solutionSaved' | 'points' | 'maxPoints'>
);

type AbstractCorrectionResult_WebCompleteResult_Fragment = (
  { __typename?: 'WebCompleteResult' }
  & Pick<WebCompleteResult, 'solutionSaved' | 'points' | 'maxPoints'>
);

type AbstractCorrectionResult_XmlCompleteResult_Fragment = (
  { __typename?: 'XmlCompleteResult' }
  & Pick<XmlCompleteResult, 'solutionSaved' | 'points' | 'maxPoints'>
);

export type AbstractCorrectionResultFragment = AbstractCorrectionResult_RegexIllegalRegexResult_Fragment | AbstractCorrectionResult_RegexMatchingResult_Fragment | AbstractCorrectionResult_RegexExtractionResult_Fragment | AbstractCorrectionResult_SqlIllegalQueryResult_Fragment | AbstractCorrectionResult_SqlWrongQueryTypeResult_Fragment | AbstractCorrectionResult_SqlResult_Fragment | AbstractCorrectionResult_UmlCompleteResult_Fragment | AbstractCorrectionResult_WebCompleteResult_Fragment | AbstractCorrectionResult_XmlCompleteResult_Fragment;

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
  & AbstractCorrectionResult_SqlIllegalQueryResult_Fragment
);

export type SqlWrongQueryTypeResultFragment = (
  { __typename?: 'SqlWrongQueryTypeResult' }
  & AbstractCorrectionResult_SqlWrongQueryTypeResult_Fragment
);

export type ColumnMatchFragment = (
  { __typename?: 'SqlColumnMatch' }
  & Pick<SqlColumnMatch, 'matchType' | 'userArg' | 'sampleArg'>
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
  & Pick<SqlTableMatch, 'matchType' | 'userArg' | 'sampleArg'>
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
  & Pick<SqlBinaryExpressionMatch, 'matchType' | 'userArg' | 'sampleArg'>
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
  & Pick<SqlInsertMatch, 'matchType' | 'userArg' | 'sampleArg'>
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
  & Pick<SqlGroupByMatch, 'matchType' | 'sampleArg' | 'userArg'>
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
  & Pick<SqlOrderByMatch, 'matchType' | 'sampleArg' | 'userArg'>
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
  & Pick<SqlLimitMatch, 'matchType' | 'sampleArg' | 'userArg'>
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
      & { selectComparisons?: Maybe<(
        { __typename?: 'SelectAdditionalComparisons' }
        & SelectAdditionalComparisonFragment
      )>, insertComparison?: Maybe<(
        { __typename?: 'SqlInsertComparisonMatchingResult' }
        & InsertComparisonFragment
      )> }
    ) }
  ), executionResult: { __typename: 'SqlExecutionResult' } }
  & AbstractCorrectionResult_SqlResult_Fragment
);

type Mr_RegexExtractedValuesComparisonMatchingResult_Fragment = (
  { __typename?: 'RegexExtractedValuesComparisonMatchingResult' }
  & Pick<RegexExtractedValuesComparisonMatchingResult, 'matchName' | 'matchSingularName' | 'points' | 'maxPoints'>
);

type Mr_SqlColumnComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlColumnComparisonMatchingResult' }
  & Pick<SqlColumnComparisonMatchingResult, 'matchName' | 'matchSingularName' | 'points' | 'maxPoints'>
);

type Mr_SqlTableComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlTableComparisonMatchingResult' }
  & Pick<SqlTableComparisonMatchingResult, 'matchName' | 'matchSingularName' | 'points' | 'maxPoints'>
);

type Mr_SqlBinaryExpressionComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlBinaryExpressionComparisonMatchingResult' }
  & Pick<SqlBinaryExpressionComparisonMatchingResult, 'matchName' | 'matchSingularName' | 'points' | 'maxPoints'>
);

type Mr_SqlGroupByComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlGroupByComparisonMatchingResult' }
  & Pick<SqlGroupByComparisonMatchingResult, 'matchName' | 'matchSingularName' | 'points' | 'maxPoints'>
);

type Mr_SqlOrderByComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlOrderByComparisonMatchingResult' }
  & Pick<SqlOrderByComparisonMatchingResult, 'matchName' | 'matchSingularName' | 'points' | 'maxPoints'>
);

type Mr_SqlLimitComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlLimitComparisonMatchingResult' }
  & Pick<SqlLimitComparisonMatchingResult, 'matchName' | 'matchSingularName' | 'points' | 'maxPoints'>
);

type Mr_SqlInsertComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlInsertComparisonMatchingResult' }
  & Pick<SqlInsertComparisonMatchingResult, 'matchName' | 'matchSingularName' | 'points' | 'maxPoints'>
);

type Mr_XmlElementLineComparisonMatchingResult_Fragment = (
  { __typename?: 'XmlElementLineComparisonMatchingResult' }
  & Pick<XmlElementLineComparisonMatchingResult, 'matchName' | 'matchSingularName' | 'points' | 'maxPoints'>
);

export type MrFragment = Mr_RegexExtractedValuesComparisonMatchingResult_Fragment | Mr_SqlColumnComparisonMatchingResult_Fragment | Mr_SqlTableComparisonMatchingResult_Fragment | Mr_SqlBinaryExpressionComparisonMatchingResult_Fragment | Mr_SqlGroupByComparisonMatchingResult_Fragment | Mr_SqlOrderByComparisonMatchingResult_Fragment | Mr_SqlLimitComparisonMatchingResult_Fragment | Mr_SqlInsertComparisonMatchingResult_Fragment | Mr_XmlElementLineComparisonMatchingResult_Fragment;

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
}
    ${AbstractCorrectionResultFragmentDoc}`;
export const SqlWrongQueryTypeResultFragmentDoc = gql`
    fragment SqlWrongQueryTypeResult on SqlWrongQueryTypeResult {
  ...AbstractCorrectionResult
}
    ${AbstractCorrectionResultFragmentDoc}`;
export const MrFragmentDoc = gql`
    fragment MR on MatchingResult {
  matchName
  matchSingularName
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
    __typename
  }
}
    ${AbstractCorrectionResultFragmentDoc}
${ColumnComparisonFragmentDoc}
${MrFragmentDoc}
${TableComparisonFragmentDoc}
${BinaryExpressionComparisonFragmentDoc}
${SelectAdditionalComparisonFragmentDoc}
${InsertComparisonFragmentDoc}`;
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