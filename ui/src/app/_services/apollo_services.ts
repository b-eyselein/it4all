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

export type AttributeList = {
   __typename?: 'AttributeList';
  elementName: Scalars['String'];
  attributeDefinitions: Array<Scalars['String']>;
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

export type ElementDefinition = {
   __typename?: 'ElementDefinition';
  elementName: Scalars['String'];
  content: Scalars['String'];
};

export type ElementLine = {
   __typename?: 'ElementLine';
  elementName: Scalars['String'];
  elementDefinition: ElementDefinition;
  attributeLists: Array<AttributeList>;
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
  solution: WebSolutionInput;
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

export type SqlCell = {
   __typename?: 'SqlCell';
  colName: Scalars['String'];
  content: Scalars['String'];
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
  userResultTry?: Maybe<SqlQueryResult>;
  sampleResultTry?: Maybe<SqlQueryResult>;
};

export type SqlExerciseContent = {
   __typename?: 'SqlExerciseContent';
  exerciseType: SqlExerciseType;
  hint?: Maybe<Scalars['String']>;
  sampleSolutions: Array<StringSampleSolution>;
};

export enum SqlExerciseType {
  Update = 'UPDATE',
  Create = 'CREATE',
  Select = 'SELECT',
  Delete = 'DELETE',
  Insert = 'INSERT'
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

export type SqlKeyCellValueObject = {
   __typename?: 'SqlKeyCellValueObject';
  key: Scalars['String'];
  value: SqlCell;
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

export type SqlQueryResult = {
   __typename?: 'SqlQueryResult';
  columnNames: Array<Scalars['String']>;
  rows: Array<SqlRow>;
  tableName: Scalars['String'];
};

export type SqlResult = AbstractCorrectionResult & {
   __typename?: 'SqlResult';
  staticComparison: SqlQueriesStaticComparison;
  executionResult: SqlExecutionResult;
  solutionSaved: Scalars['Boolean'];
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type SqlRow = {
   __typename?: 'SqlRow';
  cells: Array<SqlKeyCellValueObject>;
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

export type UmlAssociationAnalysisResult = {
   __typename?: 'UmlAssociationAnalysisResult';
  endsParallel: Scalars['Boolean'];
  assocTypeEqual: Scalars['Boolean'];
  correctAssocType: UmlAssociationType;
  multiplicitiesEqual: Scalars['Boolean'];
};

export type UmlAssociationInput = {
  assocType: UmlAssociationType;
  assocName?: Maybe<Scalars['String']>;
  firstEnd: Scalars['String'];
  firstMult: UmlMultiplicity;
  secondEnd: Scalars['String'];
  secondMult: UmlMultiplicity;
};

export type UmlAssociationMatch = NewMatch & {
   __typename?: 'UmlAssociationMatch';
  matchType: MatchType;
  userArg?: Maybe<UmlAssociation>;
  sampleArg?: Maybe<UmlAssociation>;
  maybeAnalysisResult?: Maybe<UmlAssociationAnalysisResult>;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type UmlAssociationMatchingResult = MatchingResult & {
   __typename?: 'UmlAssociationMatchingResult';
  /** @deprecated Will be deleted */
  matchName: Scalars['String'];
  /** @deprecated Will be deleted */
  matchSingularName: Scalars['String'];
  allMatches: Array<UmlAssociationMatch>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
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

export type UmlAttributeAnalysisResult = {
   __typename?: 'UmlAttributeAnalysisResult';
  visibilityComparison: Scalars['Boolean'];
  correctVisibility: UmlVisibility;
  typeComparison: Scalars['Boolean'];
  correctType: Scalars['String'];
  staticCorrect: Scalars['Boolean'];
  correctStatic: Scalars['Boolean'];
  derivedCorrect: Scalars['Boolean'];
  correctDerived: Scalars['Boolean'];
  abstractCorrect: Scalars['Boolean'];
  correctAbstract: Scalars['Boolean'];
};

export type UmlAttributeInput = {
  visibility: UmlVisibility;
  memberName: Scalars['String'];
  memberType: Scalars['String'];
  isStatic?: Maybe<Scalars['Boolean']>;
  isDerived?: Maybe<Scalars['Boolean']>;
  isAbstract?: Maybe<Scalars['Boolean']>;
};

export type UmlAttributeMatch = NewMatch & {
   __typename?: 'UmlAttributeMatch';
  matchType: MatchType;
  userArg?: Maybe<UmlAttribute>;
  sampleArg?: Maybe<UmlAttribute>;
  maybeAnalysisResult?: Maybe<UmlAttributeAnalysisResult>;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type UmlAttributeMatchingResult = MatchingResult & {
   __typename?: 'UmlAttributeMatchingResult';
  /** @deprecated Will be deleted */
  matchName: Scalars['String'];
  /** @deprecated Will be deleted */
  matchSingularName: Scalars['String'];
  allMatches: Array<UmlAttributeMatch>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
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

export type UmlClassMatch = NewMatch & {
   __typename?: 'UmlClassMatch';
  matchType: MatchType;
  userArg?: Maybe<UmlClass>;
  sampleArg?: Maybe<UmlClass>;
  compAM: Scalars['Boolean'];
  analysisResult?: Maybe<UmlClassMatchAnalysisResult>;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type UmlClassMatchAnalysisResult = {
   __typename?: 'UmlClassMatchAnalysisResult';
  classTypeCorrect: Scalars['Boolean'];
  correctClassType: UmlClassType;
  maybeAttributeMatchingResult?: Maybe<UmlAttributeMatchingResult>;
  maybeMethodMatchingResult?: Maybe<UmlMethodMatchingResult>;
};

export type UmlClassMatchingResult = MatchingResult & {
   __typename?: 'UmlClassMatchingResult';
  /** @deprecated Will be deleted */
  matchName: Scalars['String'];
  /** @deprecated Will be deleted */
  matchSingularName: Scalars['String'];
  allMatches: Array<UmlClassMatch>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export enum UmlClassType {
  Abstract = 'ABSTRACT',
  Class = 'CLASS',
  Interface = 'INTERFACE'
}

export type UmlCompleteResult = AbstractCorrectionResult & {
   __typename?: 'UmlCompleteResult';
  classResult?: Maybe<UmlClassMatchingResult>;
  assocResult?: Maybe<UmlAssociationMatchingResult>;
  implResult?: Maybe<UmlImplementationMatchingResult>;
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

export type UmlImplementationMatch = NewMatch & {
   __typename?: 'UmlImplementationMatch';
  matchType: MatchType;
  userArg?: Maybe<UmlImplementation>;
  sampleArg?: Maybe<UmlImplementation>;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type UmlImplementationMatchingResult = MatchingResult & {
   __typename?: 'UmlImplementationMatchingResult';
  /** @deprecated Will be deleted */
  matchName: Scalars['String'];
  /** @deprecated Will be deleted */
  matchSingularName: Scalars['String'];
  allMatches: Array<UmlImplementationMatch>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
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

export type UmlMethodAnalysisResult = {
   __typename?: 'UmlMethodAnalysisResult';
  visibilityComparison: Scalars['Boolean'];
  correctVisibility: UmlVisibility;
  typeComparison: Scalars['Boolean'];
  correctType: Scalars['String'];
  parameterComparison: Scalars['Boolean'];
  correctParameters: Scalars['String'];
  staticCorrect: Scalars['Boolean'];
  correctStatic: Scalars['Boolean'];
  abstractCorrect: Scalars['Boolean'];
  correctAbstract: Scalars['Boolean'];
};

export type UmlMethodInput = {
  visibility: UmlVisibility;
  memberName: Scalars['String'];
  memberType: Scalars['String'];
  parameters: Scalars['String'];
  isStatic?: Maybe<Scalars['Boolean']>;
  isAbstract?: Maybe<Scalars['Boolean']>;
};

export type UmlMethodMatch = NewMatch & {
   __typename?: 'UmlMethodMatch';
  matchType: MatchType;
  userArg?: Maybe<UmlMethod>;
  sampleArg?: Maybe<UmlMethod>;
  maybeAnalysisResult?: Maybe<UmlMethodAnalysisResult>;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type UmlMethodMatchingResult = MatchingResult & {
   __typename?: 'UmlMethodMatchingResult';
  /** @deprecated Will be deleted */
  matchName: Scalars['String'];
  /** @deprecated Will be deleted */
  matchSingularName: Scalars['String'];
  allMatches: Array<UmlMethodMatch>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
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
  sample: WebSolution;
};

export type WebSolution = {
   __typename?: 'WebSolution';
  files: Array<ExerciseFile>;
};

export type WebSolutionInput = {
  files: Array<ExerciseFileInput>;
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

export type CollectionListQueryVariables = {
  toolId: Scalars['String'];
};


export type CollectionListQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & Pick<Tool, 'name'>
    & { collections: Array<(
      { __typename?: 'Collection' }
      & Pick<Collection, 'id' | 'title' | 'exerciseCount'>
    )> }
  )> }
);

export type ExercisesQueryVariables = {
  toolId: Scalars['String'];
  collId: Scalars['Int'];
};


export type ExercisesQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & { collection?: Maybe<(
      { __typename?: 'Collection' }
      & { exercises: Array<(
        { __typename?: 'Exercise' }
        & Pick<Exercise, 'id' | 'title'>
      )> }
    )> }
  )> }
);

export type CollectionToolOverviewQueryVariables = {
  toolId: Scalars['String'];
};


export type CollectionToolOverviewQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & Pick<Tool, 'name' | 'exerciseCount' | 'collectionCount' | 'lessonCount'>
  )> }
);

export type CollectionOverviewQueryVariables = {
  toolId: Scalars['String'];
  collId: Scalars['Int'];
};


export type CollectionOverviewQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & { collection?: Maybe<(
      { __typename?: 'Collection' }
      & Pick<Collection, 'title'>
      & { exercises: Array<(
        { __typename?: 'Exercise' }
        & FieldsForLinkFragment
      )> }
    )> }
  )> }
);

export type AllExercisesOverviewQueryVariables = {
  toolId: Scalars['String'];
};


export type AllExercisesOverviewQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & { allExerciseMetaData: Array<(
      { __typename?: 'Exercise' }
      & { tags: Array<(
        { __typename?: 'ExTag' }
        & TagFragment
      )> }
      & FieldsForLinkFragment
    )> }
  )> }
);

export type ExerciseOverviewQueryVariables = {
  toolId: Scalars['String'];
  collId: Scalars['Int'];
  exId: Scalars['Int'];
};


export type ExerciseOverviewQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & { collection?: Maybe<(
      { __typename?: 'Collection' }
      & { exercise?: Maybe<(
        { __typename?: 'Exercise' }
        & Pick<Exercise, 'id' | 'title' | 'text'>
      )> }
    )>, exerciseContent?: Maybe<(
      { __typename: 'ProgExerciseContent' }
      & { unitTestPart: (
        { __typename?: 'UnitTestPart' }
        & Pick<UnitTestPart, 'unitTestType'>
      ) }
    ) | { __typename: 'RegexExerciseContent' } | { __typename: 'SqlExerciseContent' } | { __typename: 'UmlExerciseContent' } | (
      { __typename: 'WebExerciseContent' }
      & { siteSpec: (
        { __typename?: 'SiteSpec' }
        & Pick<SiteSpec, 'htmlTaskCount' | 'jsTaskCount'>
      ) }
    ) | { __typename: 'XmlExerciseContent' }> }
  )> }
);

export type ExerciseQueryVariables = {
  toolId: Scalars['String'];
  collId: Scalars['Int'];
  exId: Scalars['Int'];
};


export type ExerciseQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & { collection?: Maybe<(
      { __typename?: 'Collection' }
      & { exercise?: Maybe<(
        { __typename?: 'Exercise' }
        & ExerciseSolveFieldsFragment
      )> }
    )>, exerciseContent?: Maybe<(
      { __typename: 'ProgExerciseContent' }
      & ProgExerciseContentSolveFieldsFragment
    ) | (
      { __typename: 'RegexExerciseContent' }
      & RegexExerciseContentSolveFieldsFragment
    ) | (
      { __typename: 'SqlExerciseContent' }
      & SqlExerciseContentSolveFieldsFragment
    ) | (
      { __typename: 'UmlExerciseContent' }
      & UmlExerciseContentSolveFieldsFragment
    ) | (
      { __typename: 'WebExerciseContent' }
      & WebExerciseContentSolveFieldsFragment
    ) | (
      { __typename: 'XmlExerciseContent' }
      & XmlExerciseContentSolveFieldsFragment
    )> }
  )> }
);

export type LessonsForToolQueryVariables = {
  toolId: Scalars['String'];
};


export type LessonsForToolQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & Pick<Tool, 'name'>
    & { lessons: Array<(
      { __typename?: 'Lesson' }
      & Pick<Lesson, 'id' | 'title' | 'description'>
    )> }
  )> }
);

export type LessonQueryVariables = {
  toolId: Scalars['String'];
  lessonId: Scalars['Int'];
};


export type LessonQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & Pick<Tool, 'name'>
    & { lesson?: Maybe<(
      { __typename?: 'Lesson' }
      & Pick<Lesson, 'id' | 'title' | 'description'>
    )> }
  )> }
);

export type CollectionToolAdminQueryVariables = {
  toolId: Scalars['String'];
};


export type CollectionToolAdminQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & Pick<Tool, 'name' | 'collectionCount' | 'lessonCount'>
  )> }
);

export type AdminLessonIndexQueryVariables = {
  toolId: Scalars['String'];
};


export type AdminLessonIndexQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & Pick<Tool, 'name'>
    & { lessons: Array<(
      { __typename?: 'Lesson' }
      & LessonFragmentFragment
    )> }
  )> }
);

export type CollectionAdminQueryVariables = {
  toolId: Scalars['String'];
  collId: Scalars['Int'];
};


export type CollectionAdminQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & { collection?: Maybe<(
      { __typename?: 'Collection' }
      & Pick<Collection, 'title'>
      & { exercises: Array<(
        { __typename?: 'Exercise' }
        & Pick<Exercise, 'id' | 'title'>
      )> }
    )> }
  )> }
);

export type AdminCollectionsIndexQueryVariables = {
  toolId: Scalars['String'];
};


export type AdminCollectionsIndexQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & Pick<Tool, 'name'>
    & { collections: Array<(
      { __typename?: 'Collection' }
      & Pick<Collection, 'id' | 'title' | 'exerciseCount'>
    )> }
  )> }
);

export type AdminEditCollectionQueryVariables = {
  toolId: Scalars['String'];
  collId: Scalars['Int'];
};


export type AdminEditCollectionQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & { collection?: Maybe<(
      { __typename?: 'Collection' }
      & Pick<Collection, 'id' | 'title'>
    )> }
  )> }
);

export type TagFragment = (
  { __typename?: 'ExTag' }
  & Pick<ExTag, 'abbreviation' | 'title'>
);

export type FieldsForLinkFragment = (
  { __typename?: 'Exercise' }
  & Pick<Exercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'difficulty'>
  & { tags: Array<(
    { __typename?: 'ExTag' }
    & Pick<ExTag, 'abbreviation' | 'title'>
  )> }
);

export type ExerciseSolveFieldsFragment = (
  { __typename?: 'Exercise' }
  & Pick<Exercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'text'>
);

export type ProgExerciseContentSolveFieldsFragment = (
  { __typename?: 'ProgExerciseContent' }
  & { unitTestPart: (
    { __typename?: 'UnitTestPart' }
    & { unitTestFiles: Array<(
      { __typename?: 'ExerciseFile' }
      & ExFileAllFragment
    )> }
  ), implementationPart: (
    { __typename?: 'ImplementationPart' }
    & { files: Array<(
      { __typename?: 'ExerciseFile' }
      & ExFileAllFragment
    )> }
  ), progSampleSolutions: Array<(
    { __typename?: 'ProgSampleSolution' }
    & { sample: (
      { __typename?: 'ProgSolution' }
      & { files: Array<(
        { __typename?: 'ExerciseFile' }
        & ExFileAllFragment
      )> }
    ) }
  )> }
);

export type RegexExerciseContentSolveFieldsFragment = (
  { __typename?: 'RegexExerciseContent' }
  & { regexSampleSolutions: Array<(
    { __typename?: 'StringSampleSolution' }
    & Pick<StringSampleSolution, 'sample'>
  )> }
);

export type SqlExerciseContentSolveFieldsFragment = (
  { __typename?: 'SqlExerciseContent' }
  & Pick<SqlExerciseContent, 'hint'>
  & { sqlSampleSolutions: Array<(
    { __typename?: 'StringSampleSolution' }
    & Pick<StringSampleSolution, 'sample'>
  )> }
);

export type UmlExerciseContentSolveFieldsFragment = (
  { __typename?: 'UmlExerciseContent' }
  & Pick<UmlExerciseContent, 'toIgnore'>
  & { mappings: Array<(
    { __typename?: 'KeyValueObject' }
    & Pick<KeyValueObject, 'key' | 'value'>
  )>, umlSampleSolutions: Array<(
    { __typename?: 'UmlSampleSolution' }
    & { sample: (
      { __typename?: 'UmlClassDiagram' }
      & UmlClassDiagramFragment
    ) }
  )> }
);

export type UmlClassDiagramFragment = (
  { __typename?: 'UmlClassDiagram' }
  & { classes: Array<(
    { __typename?: 'UmlClass' }
    & UmlClassFragment
  )>, associations: Array<(
    { __typename?: 'UmlAssociation' }
    & UmlAssociationFragment
  )>, implementations: Array<(
    { __typename?: 'UmlImplementation' }
    & UmlImplementationFragment
  )> }
);

export type UmlClassFragment = (
  { __typename?: 'UmlClass' }
  & Pick<UmlClass, 'classType' | 'name'>
  & { attributes: Array<(
    { __typename?: 'UmlAttribute' }
    & UmlAttributeFragment
  )>, methods: Array<(
    { __typename?: 'UmlMethod' }
    & UmlMethodFragment
  )> }
);

export type UmlAttributeFragment = (
  { __typename?: 'UmlAttribute' }
  & Pick<UmlAttribute, 'isAbstract' | 'isDerived' | 'isStatic' | 'visibility' | 'memberName' | 'memberType'>
);

export type UmlMethodFragment = (
  { __typename?: 'UmlMethod' }
  & Pick<UmlMethod, 'isAbstract' | 'isStatic' | 'visibility' | 'memberName' | 'parameters' | 'memberType'>
);

export type UmlAssociationFragment = (
  { __typename?: 'UmlAssociation' }
  & Pick<UmlAssociation, 'assocType' | 'assocName' | 'firstEnd' | 'firstMult' | 'secondEnd' | 'secondMult'>
);

export type UmlImplementationFragment = (
  { __typename?: 'UmlImplementation' }
  & Pick<UmlImplementation, 'subClass' | 'superClass'>
);

export type WebExerciseContentSolveFieldsFragment = (
  { __typename?: 'WebExerciseContent' }
  & { files: Array<(
    { __typename?: 'ExerciseFile' }
    & ExFileAllFragment
  )>, siteSpec: (
    { __typename?: 'SiteSpec' }
    & Pick<SiteSpec, 'jsTaskCount'>
    & { htmlTasks: Array<(
      { __typename?: 'HtmlTask' }
      & Pick<HtmlTask, 'text'>
    )> }
  ), webSampleSolutions: Array<(
    { __typename?: 'WebSampleSolution' }
    & { sample: (
      { __typename?: 'WebSolution' }
      & { files: Array<(
        { __typename?: 'ExerciseFile' }
        & ExFileAllFragment
      )> }
    ) }
  )> }
);

export type XmlExerciseContentSolveFieldsFragment = (
  { __typename?: 'XmlExerciseContent' }
  & Pick<XmlExerciseContent, 'rootNode' | 'grammarDescription'>
  & { xmlSampleSolutions: Array<(
    { __typename?: 'XmlSampleSolution' }
    & { sample: (
      { __typename?: 'XmlSolution' }
      & Pick<XmlSolution, 'document' | 'grammar'>
    ) }
  )> }
);

export type ExFileAllFragment = (
  { __typename?: 'ExerciseFile' }
  & Pick<ExerciseFile, 'name' | 'resourcePath' | 'fileType' | 'content' | 'editable'>
);

export type LessonFragmentFragment = (
  { __typename?: 'Lesson' }
  & Pick<Lesson, 'id' | 'title'>
);

export const TagFragmentDoc = gql`
    fragment Tag on ExTag {
  abbreviation
  title
}
    `;
export const FieldsForLinkFragmentDoc = gql`
    fragment fieldsForLink on Exercise {
  id
  collectionId
  toolId
  title
  difficulty
  tags {
    abbreviation
    title
  }
}
    `;
export const ExerciseSolveFieldsFragmentDoc = gql`
    fragment ExerciseSolveFields on Exercise {
  id
  collectionId
  toolId
  title
  text
}
    `;
export const ExFileAllFragmentDoc = gql`
    fragment ExFileAll on ExerciseFile {
  name
  resourcePath
  fileType
  content
  editable
}
    `;
export const ProgExerciseContentSolveFieldsFragmentDoc = gql`
    fragment ProgExerciseContentSolveFields on ProgExerciseContent {
  unitTestPart {
    unitTestFiles {
      ...ExFileAll
    }
  }
  implementationPart {
    files {
      ...ExFileAll
    }
  }
  progSampleSolutions: sampleSolutions {
    sample {
      files {
        ...ExFileAll
      }
    }
  }
}
    ${ExFileAllFragmentDoc}`;
export const RegexExerciseContentSolveFieldsFragmentDoc = gql`
    fragment RegexExerciseContentSolveFields on RegexExerciseContent {
  regexSampleSolutions: sampleSolutions {
    sample
  }
}
    `;
export const SqlExerciseContentSolveFieldsFragmentDoc = gql`
    fragment SqlExerciseContentSolveFields on SqlExerciseContent {
  hint
  sqlSampleSolutions: sampleSolutions {
    sample
  }
}
    `;
export const UmlAttributeFragmentDoc = gql`
    fragment UmlAttribute on UmlAttribute {
  isAbstract
  isDerived
  isStatic
  visibility
  memberName
  memberType
}
    `;
export const UmlMethodFragmentDoc = gql`
    fragment UmlMethod on UmlMethod {
  isAbstract
  isStatic
  visibility
  memberName
  parameters
  memberType
}
    `;
export const UmlClassFragmentDoc = gql`
    fragment UmlClass on UmlClass {
  classType
  name
  attributes {
    ...UmlAttribute
  }
  methods {
    ...UmlMethod
  }
}
    ${UmlAttributeFragmentDoc}
${UmlMethodFragmentDoc}`;
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
export const UmlImplementationFragmentDoc = gql`
    fragment UmlImplementation on UmlImplementation {
  subClass
  superClass
}
    `;
export const UmlClassDiagramFragmentDoc = gql`
    fragment UmlClassDiagram on UmlClassDiagram {
  classes {
    ...UmlClass
  }
  associations {
    ...UmlAssociation
  }
  implementations {
    ...UmlImplementation
  }
}
    ${UmlClassFragmentDoc}
${UmlAssociationFragmentDoc}
${UmlImplementationFragmentDoc}`;
export const UmlExerciseContentSolveFieldsFragmentDoc = gql`
    fragment UmlExerciseContentSolveFields on UmlExerciseContent {
  toIgnore
  mappings {
    key
    value
  }
  umlSampleSolutions: sampleSolutions {
    sample {
      ...UmlClassDiagram
    }
  }
}
    ${UmlClassDiagramFragmentDoc}`;
export const WebExerciseContentSolveFieldsFragmentDoc = gql`
    fragment WebExerciseContentSolveFields on WebExerciseContent {
  files {
    ...ExFileAll
  }
  siteSpec {
    htmlTasks {
      text
    }
    jsTaskCount
  }
  webSampleSolutions: sampleSolutions {
    sample {
      files {
        ...ExFileAll
      }
    }
  }
}
    ${ExFileAllFragmentDoc}`;
export const XmlExerciseContentSolveFieldsFragmentDoc = gql`
    fragment XmlExerciseContentSolveFields on XmlExerciseContent {
  rootNode
  grammarDescription
  xmlSampleSolutions: sampleSolutions {
    sample {
      document
      grammar
    }
  }
}
    `;
export const LessonFragmentFragmentDoc = gql`
    fragment LessonFragment on Lesson {
  id
  title
}
    `;
export const CollectionListDocument = gql`
    query CollectionList($toolId: String!) {
  tool(toolId: $toolId) {
    name
    collections {
      id
      title
      exerciseCount
    }
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class CollectionListGQL extends Apollo.Query<CollectionListQuery, CollectionListQueryVariables> {
    document = CollectionListDocument;
    
  }
export const ExercisesDocument = gql`
    query Exercises($toolId: String!, $collId: Int!) {
  tool(toolId: $toolId) {
    collection(collId: $collId) {
      exercises {
        id
        title
      }
    }
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class ExercisesGQL extends Apollo.Query<ExercisesQuery, ExercisesQueryVariables> {
    document = ExercisesDocument;
    
  }
export const CollectionToolOverviewDocument = gql`
    query CollectionToolOverview($toolId: String!) {
  tool(toolId: $toolId) {
    name
    exerciseCount
    collectionCount
    lessonCount
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class CollectionToolOverviewGQL extends Apollo.Query<CollectionToolOverviewQuery, CollectionToolOverviewQueryVariables> {
    document = CollectionToolOverviewDocument;
    
  }
export const CollectionOverviewDocument = gql`
    query CollectionOverview($toolId: String!, $collId: Int!) {
  tool(toolId: $toolId) {
    collection(collId: $collId) {
      title
      exercises {
        ...fieldsForLink
      }
    }
  }
}
    ${FieldsForLinkFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class CollectionOverviewGQL extends Apollo.Query<CollectionOverviewQuery, CollectionOverviewQueryVariables> {
    document = CollectionOverviewDocument;
    
  }
export const AllExercisesOverviewDocument = gql`
    query AllExercisesOverview($toolId: String!) {
  tool(toolId: $toolId) {
    allExerciseMetaData {
      tags {
        ...Tag
      }
      ...fieldsForLink
    }
  }
}
    ${TagFragmentDoc}
${FieldsForLinkFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class AllExercisesOverviewGQL extends Apollo.Query<AllExercisesOverviewQuery, AllExercisesOverviewQueryVariables> {
    document = AllExercisesOverviewDocument;
    
  }
export const ExerciseOverviewDocument = gql`
    query ExerciseOverview($toolId: String!, $collId: Int!, $exId: Int!) {
  tool(toolId: $toolId) {
    collection(collId: $collId) {
      exercise(exId: $exId) {
        id
        title
        text
      }
    }
    exerciseContent(collId: $collId, exId: $exId) {
      __typename
      ... on ProgExerciseContent {
        unitTestPart {
          unitTestType
        }
      }
      ... on WebExerciseContent {
        siteSpec {
          htmlTaskCount
          jsTaskCount
        }
      }
    }
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class ExerciseOverviewGQL extends Apollo.Query<ExerciseOverviewQuery, ExerciseOverviewQueryVariables> {
    document = ExerciseOverviewDocument;
    
  }
export const ExerciseDocument = gql`
    query Exercise($toolId: String!, $collId: Int!, $exId: Int!) {
  tool(toolId: $toolId) {
    collection(collId: $collId) {
      exercise(exId: $exId) {
        ...ExerciseSolveFields
      }
    }
    exerciseContent(collId: $collId, exId: $exId) {
      __typename
      ...ProgExerciseContentSolveFields
      ...RegexExerciseContentSolveFields
      ...SqlExerciseContentSolveFields
      ...UmlExerciseContentSolveFields
      ...WebExerciseContentSolveFields
      ...XmlExerciseContentSolveFields
    }
  }
}
    ${ExerciseSolveFieldsFragmentDoc}
${ProgExerciseContentSolveFieldsFragmentDoc}
${RegexExerciseContentSolveFieldsFragmentDoc}
${SqlExerciseContentSolveFieldsFragmentDoc}
${UmlExerciseContentSolveFieldsFragmentDoc}
${WebExerciseContentSolveFieldsFragmentDoc}
${XmlExerciseContentSolveFieldsFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class ExerciseGQL extends Apollo.Query<ExerciseQuery, ExerciseQueryVariables> {
    document = ExerciseDocument;
    
  }
export const LessonsForToolDocument = gql`
    query LessonsForTool($toolId: String!) {
  tool(toolId: $toolId) {
    name
    lessons {
      id
      title
      description
    }
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class LessonsForToolGQL extends Apollo.Query<LessonsForToolQuery, LessonsForToolQueryVariables> {
    document = LessonsForToolDocument;
    
  }
export const LessonDocument = gql`
    query Lesson($toolId: String!, $lessonId: Int!) {
  tool(toolId: $toolId) {
    name
    lesson(lessonId: $lessonId) {
      id
      title
      description
    }
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class LessonGQL extends Apollo.Query<LessonQuery, LessonQueryVariables> {
    document = LessonDocument;
    
  }
export const CollectionToolAdminDocument = gql`
    query CollectionToolAdmin($toolId: String!) {
  tool(toolId: $toolId) {
    name
    collectionCount
    lessonCount
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class CollectionToolAdminGQL extends Apollo.Query<CollectionToolAdminQuery, CollectionToolAdminQueryVariables> {
    document = CollectionToolAdminDocument;
    
  }
export const AdminLessonIndexDocument = gql`
    query AdminLessonIndex($toolId: String!) {
  tool(toolId: $toolId) {
    name
    lessons {
      ...LessonFragment
    }
  }
}
    ${LessonFragmentFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class AdminLessonIndexGQL extends Apollo.Query<AdminLessonIndexQuery, AdminLessonIndexQueryVariables> {
    document = AdminLessonIndexDocument;
    
  }
export const CollectionAdminDocument = gql`
    query CollectionAdmin($toolId: String!, $collId: Int!) {
  tool(toolId: $toolId) {
    collection(collId: $collId) {
      title
      exercises {
        id
        title
      }
    }
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class CollectionAdminGQL extends Apollo.Query<CollectionAdminQuery, CollectionAdminQueryVariables> {
    document = CollectionAdminDocument;
    
  }
export const AdminCollectionsIndexDocument = gql`
    query AdminCollectionsIndex($toolId: String!) {
  tool(toolId: $toolId) {
    name
    collections {
      id
      title
      exerciseCount
    }
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class AdminCollectionsIndexGQL extends Apollo.Query<AdminCollectionsIndexQuery, AdminCollectionsIndexQueryVariables> {
    document = AdminCollectionsIndexDocument;
    
  }
export const AdminEditCollectionDocument = gql`
    query AdminEditCollection($toolId: String!, $collId: Int!) {
  tool(toolId: $toolId) {
    collection(collId: $collId) {
      id
      title
    }
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class AdminEditCollectionGQL extends Apollo.Query<AdminEditCollectionQuery, AdminEditCollectionQueryVariables> {
    document = AdminEditCollectionDocument;
    
  }