import { gql } from '@apollo/client';
import * as Apollo from '@apollo/client';
export type Maybe<T> = T | null;
export type InputMaybe<T> = Maybe<T>;
export type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
export type MakeOptional<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]?: Maybe<T[SubKey]> };
export type MakeMaybe<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]: Maybe<T[SubKey]> };
const defaultOptions = {} as const;
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: string;
  String: string;
  Boolean: boolean;
  Int: number;
  Float: number;
};

export type AdditionalComparison = {
  __typename?: 'AdditionalComparison';
  insertComparison?: Maybe<StringMatchingResult>;
  selectComparisons?: Maybe<SelectAdditionalComparisons>;
};

export type AttributeList = {
  __typename?: 'AttributeList';
  attributeDefinitions: Array<Scalars['String']>;
  elementName: Scalars['String'];
};

export enum BinaryClassificationResultType {
  FalseNegative = 'FalseNegative',
  FalsePositive = 'FalsePositive',
  TrueNegative = 'TrueNegative',
  TruePositive = 'TruePositive'
}

export type DtdParseException = {
  __typename?: 'DTDParseException';
  msg: Scalars['String'];
  parsedLine: Scalars['String'];
};

export type ElementDefinition = {
  __typename?: 'ElementDefinition';
  content: Scalars['String'];
  elementName: Scalars['String'];
};

export type ElementLine = {
  __typename?: 'ElementLine';
  attributeLists: Array<AttributeList>;
  elementDefinition: ElementDefinition;
  elementName: Scalars['String'];
};

export type ElementLineAnalysisResult = {
  __typename?: 'ElementLineAnalysisResult';
  attributesCorrect: Scalars['Boolean'];
  contentCorrect: Scalars['Boolean'];
  correctAttributes: Scalars['String'];
  correctContent: Scalars['String'];
};

export type ElementLineMatch = {
  __typename?: 'ElementLineMatch';
  analysisResult: ElementLineAnalysisResult;
  matchType: MatchType;
  sampleArg: ElementLine;
  userArg: ElementLine;
};

export type ExPart = {
  __typename?: 'ExPart';
  id: Scalars['String'];
  isEntryPart: Scalars['Boolean'];
  name: Scalars['String'];
  solved: Scalars['Boolean'];
};

export type Exercise = {
  __typename?: 'Exercise';
  collectionId: Scalars['Int'];
  content: ExerciseContentUnionType;
  difficulty: Level;
  exerciseId: Scalars['Int'];
  parts: Array<ExPart>;
  text: Scalars['String'];
  title: Scalars['String'];
  toolId: Scalars['String'];
  topicsWithLevels: Array<TopicWithLevel>;
};

export type ExerciseCollection = {
  __typename?: 'ExerciseCollection';
  collectionId: Scalars['Int'];
  exercise?: Maybe<Exercise>;
  exerciseCount: Scalars['Int'];
  exercises: Array<Exercise>;
  title: Scalars['String'];
  toolId: Scalars['String'];
};


export type ExerciseCollectionExerciseArgs = {
  exId: Scalars['Int'];
};

export type ExerciseContentUnionType = FlaskExerciseContent | ProgrammingExerciseContent | RegexExerciseContent | SqlExerciseContent | UmlExerciseContent | WebExerciseContent | XmlExerciseContent;

export type ExerciseFile = {
  __typename?: 'ExerciseFile';
  content: Scalars['String'];
  editable: Scalars['Boolean'];
  name: Scalars['String'];
};

export type ExerciseFileInput = {
  content: Scalars['String'];
  editable: Scalars['Boolean'];
  name: Scalars['String'];
};

export type FilesSolution = {
  __typename?: 'FilesSolution';
  files: Array<ExerciseFile>;
};

export type FilesSolutionInput = {
  files: Array<ExerciseFileInput>;
};

export type FlaskCorrectionResult = {
  __typename?: 'FlaskCorrectionResult';
  result: FlaskResult;
  solutionId: Scalars['Int'];
};

export type FlaskExerciseContent = {
  __typename?: 'FlaskExerciseContent';
  files: Array<ExerciseFile>;
  sampleSolutions: Array<FilesSolution>;
  testConfig: FlaskTestsConfig;
  testFiles: Array<ExerciseFile>;
};

export type FlaskExerciseMutations = {
  __typename?: 'FlaskExerciseMutations';
  correct: FlaskCorrectionResult;
};


export type FlaskExerciseMutationsCorrectArgs = {
  solution: FilesSolutionInput;
};

export type FlaskResult = {
  __typename?: 'FlaskResult';
  maxPoints: Scalars['Float'];
  points: Scalars['Float'];
  testResults: Array<FlaskTestResult>;
};

export type FlaskSingleTestConfig = {
  __typename?: 'FlaskSingleTestConfig';
  dependencies?: Maybe<Array<Scalars['String']>>;
  description: Scalars['String'];
  id: Scalars['Int'];
  maxPoints: Scalars['Int'];
  testName: Scalars['String'];
};

export type FlaskTestResult = {
  __typename?: 'FlaskTestResult';
  stderr: Array<Scalars['String']>;
  stdout: Array<Scalars['String']>;
  successful: Scalars['Boolean'];
  testId: Scalars['Int'];
  testName: Scalars['String'];
};

export type FlaskTestsConfig = {
  __typename?: 'FlaskTestsConfig';
  testClassName: Scalars['String'];
  testFileName: Scalars['String'];
  tests: Array<FlaskSingleTestConfig>;
};

export type GradedElementSpecResult = {
  __typename?: 'GradedElementSpecResult';
  attributeResults: Array<GradedTextResult>;
  elementFound: Scalars['Boolean'];
  isCorrect: Scalars['Boolean'];
  maxPoints: Scalars['Float'];
  points: Scalars['Float'];
  textContentResult?: Maybe<GradedTextResult>;
};

export type GradedHtmlTaskResult = {
  __typename?: 'GradedHtmlTaskResult';
  elementSpecResult: GradedElementSpecResult;
  id: Scalars['Int'];
};

export type GradedJsActionResult = {
  __typename?: 'GradedJsActionResult';
  actionPerformed: Scalars['Boolean'];
  jsAction: JsAction;
  maxPoints: Scalars['Float'];
  points: Scalars['Float'];
};

export type GradedJsTaskResult = {
  __typename?: 'GradedJsTaskResult';
  gradedJsActionResult: GradedJsActionResult;
  gradedPostResults: Array<GradedElementSpecResult>;
  gradedPreResults: Array<GradedElementSpecResult>;
  id: Scalars['Int'];
  maxPoints: Scalars['Float'];
  points: Scalars['Float'];
};

export type GradedTextResult = {
  __typename?: 'GradedTextResult';
  awaitedContent: Scalars['String'];
  isSuccessful: Scalars['Boolean'];
  keyName: Scalars['String'];
  maxPoints: Scalars['Float'];
  maybeFoundContent?: Maybe<Scalars['String']>;
  points: Scalars['Float'];
};

export type HtmlTask = {
  __typename?: 'HtmlTask';
  text: Scalars['String'];
};

export type ImplementationCorrectionResult = ProgrammingTestCorrectionResult & {
  __typename?: 'ImplementationCorrectionResult';
  stderr: Array<Scalars['String']>;
  stdout: Array<Scalars['String']>;
  successful: Scalars['Boolean'];
  testSuccessful: Scalars['Boolean'];
};

export type ImplementationPart = {
  __typename?: 'ImplementationPart';
  files: Array<ExerciseFile>;
  implFileName: Scalars['String'];
};

export type JsAction = {
  __typename?: 'JsAction';
  actionType: JsActionType;
  keysToSend?: Maybe<Scalars['String']>;
  xpathQuery: Scalars['String'];
};

export enum JsActionType {
  Click = 'Click',
  FillOut = 'FillOut'
}

export type KeyValueObject = {
  __typename?: 'KeyValueObject';
  key: Scalars['String'];
  value: Scalars['String'];
};

export type Level = {
  __typename?: 'Level';
  levelIndex: Scalars['Int'];
  title: Scalars['String'];
};

export enum MatchType {
  PartialMatch = 'PARTIAL_MATCH',
  SuccessfulMatch = 'SUCCESSFUL_MATCH',
  UnsuccessfulMatch = 'UNSUCCESSFUL_MATCH'
}

export type Mutation = {
  __typename?: 'Mutation';
  claimLtiWebToken?: Maybe<Scalars['String']>;
  flaskExercise?: Maybe<FlaskExerciseMutations>;
  login: Scalars['String'];
  programmingExercise?: Maybe<ProgrammingExerciseMutations>;
  regexExercise?: Maybe<RegexExerciseMutations>;
  register?: Maybe<Scalars['String']>;
  sqlExercise?: Maybe<SqlExerciseMutations>;
  umlExercise?: Maybe<UmlExerciseMutations>;
  webExercise?: Maybe<WebExerciseMutations>;
  xmlExercise?: Maybe<XmlExerciseMutations>;
};


export type MutationClaimLtiWebTokenArgs = {
  ltiUuid: Scalars['String'];
};


export type MutationFlaskExerciseArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
};


export type MutationLoginArgs = {
  credentials: UserCredentials;
};


export type MutationProgrammingExerciseArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
};


export type MutationRegexExerciseArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
};


export type MutationRegisterArgs = {
  registerValues: RegisterValues;
};


export type MutationSqlExerciseArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
};


export type MutationUmlExerciseArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
};


export type MutationWebExerciseArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
};


export type MutationXmlExerciseArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
};

export enum ProgExPart {
  Implementation = 'Implementation',
  TestCreation = 'TestCreation'
}

export type ProgrammingCorrectionResult = {
  __typename?: 'ProgrammingCorrectionResult';
  result: ProgrammingResult;
  solutionId: Scalars['Int'];
};

export type ProgrammingExerciseContent = {
  __typename?: 'ProgrammingExerciseContent';
  filename: Scalars['String'];
  implementationPart: ImplementationPart;
  sampleSolutions: Array<FilesSolution>;
  unitTestPart: UnitTestPart;
};

export type ProgrammingExerciseMutations = {
  __typename?: 'ProgrammingExerciseMutations';
  correct: ProgrammingCorrectionResult;
};


export type ProgrammingExerciseMutationsCorrectArgs = {
  part: ProgExPart;
  solution: FilesSolutionInput;
};

export type ProgrammingResult = {
  __typename?: 'ProgrammingResult';
  implementationCorrectionResult?: Maybe<ImplementationCorrectionResult>;
  maxPoints: Scalars['Float'];
  points: Scalars['Float'];
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  unitTestResults: Array<UnitTestCorrectionResult>;
};

export type ProgrammingTestCorrectionResult = {
  successful: Scalars['Boolean'];
};

export type Query = {
  __typename?: 'Query';
  tool: Tool;
  tools: Array<Tool>;
};


export type QueryToolArgs = {
  toolId: Scalars['String'];
};

export type RegexAbstractResult = {
  maxPoints: Scalars['Float'];
  points: Scalars['Float'];
};

export type RegexCorrectionResult = {
  __typename?: 'RegexCorrectionResult';
  result: RegexAbstractResult;
  solutionId: Scalars['Int'];
};

export enum RegexCorrectionType {
  Extraction = 'EXTRACTION',
  Matching = 'MATCHING'
}

export type RegexExerciseContent = {
  __typename?: 'RegexExerciseContent';
  correctionType: RegexCorrectionType;
  extractionTestData: Array<RegexExtractionTestData>;
  matchTestData: Array<RegexMatchTestData>;
  maxPoints: Scalars['Int'];
  sampleSolutions: Array<Scalars['String']>;
};

export type RegexExerciseMutations = {
  __typename?: 'RegexExerciseMutations';
  correct: RegexCorrectionResult;
};


export type RegexExerciseMutationsCorrectArgs = {
  solution: Scalars['String'];
};

export type RegexExtractedValuesComparisonMatchingResult = {
  __typename?: 'RegexExtractedValuesComparisonMatchingResult';
  allMatches: Array<RegexMatchMatch>;
  maxPoints: Scalars['Float'];
  notMatchedForSample: Array<Scalars['String']>;
  notMatchedForUser: Array<Scalars['String']>;
  points: Scalars['Float'];
};

export type RegexExtractionResult = RegexAbstractResult & {
  __typename?: 'RegexExtractionResult';
  extractionResults: Array<RegexExtractionSingleResult>;
  maxPoints: Scalars['Float'];
  points: Scalars['Float'];
};

export type RegexExtractionSingleResult = {
  __typename?: 'RegexExtractionSingleResult';
  base: Scalars['String'];
  correct: Scalars['Boolean'];
  extractionMatchingResult: RegexExtractedValuesComparisonMatchingResult;
};

export type RegexExtractionTestData = {
  __typename?: 'RegexExtractionTestData';
  base: Scalars['String'];
  id: Scalars['Int'];
};

export type RegexMatchMatch = {
  __typename?: 'RegexMatchMatch';
  matchType: MatchType;
  sampleArg: Scalars['String'];
  userArg: Scalars['String'];
};

export type RegexMatchTestData = {
  __typename?: 'RegexMatchTestData';
  data: Scalars['String'];
  id: Scalars['Int'];
  isIncluded: Scalars['Boolean'];
};

export type RegexMatchingResult = RegexAbstractResult & {
  __typename?: 'RegexMatchingResult';
  matchingResults: Array<RegexMatchingSingleResult>;
  maxPoints: Scalars['Float'];
  points: Scalars['Float'];
};

export type RegexMatchingSingleResult = {
  __typename?: 'RegexMatchingSingleResult';
  isIncluded: Scalars['Boolean'];
  matchData: Scalars['String'];
  resultType: BinaryClassificationResultType;
};

export type RegisterValues = {
  password: Scalars['String'];
  passwordRepeat: Scalars['String'];
  username: Scalars['String'];
};

export type SelectAdditionalComparisons = {
  __typename?: 'SelectAdditionalComparisons';
  groupByComparison: StringMatchingResult;
  limitComparison: StringMatchingResult;
  orderByComparison: StringMatchingResult;
};

export type SiteSpec = {
  __typename?: 'SiteSpec';
  fileName: Scalars['String'];
  htmlTaskCount: Scalars['Int'];
  htmlTasks: Array<HtmlTask>;
  jsTaskCount: Scalars['Int'];
};

export type SqlBinaryExpressionComparisonMatchingResult = {
  __typename?: 'SqlBinaryExpressionComparisonMatchingResult';
  allMatches: Array<SqlBinaryExpressionMatch>;
  maxPoints: Scalars['Float'];
  notMatchedForSample: Array<Scalars['String']>;
  notMatchedForUser: Array<Scalars['String']>;
  points: Scalars['Float'];
};

export type SqlBinaryExpressionMatch = {
  __typename?: 'SqlBinaryExpressionMatch';
  matchType: MatchType;
  sampleArg: Scalars['String'];
  userArg: Scalars['String'];
};

export type SqlCell = {
  __typename?: 'SqlCell';
  colName: Scalars['String'];
  content?: Maybe<Scalars['String']>;
  different: Scalars['Boolean'];
};

export type SqlColumnComparisonMatchingResult = {
  __typename?: 'SqlColumnComparisonMatchingResult';
  allMatches: Array<SqlColumnMatch>;
  maxPoints: Scalars['Float'];
  notMatchedForSample: Array<Scalars['String']>;
  notMatchedForUser: Array<Scalars['String']>;
  points: Scalars['Float'];
};

export type SqlColumnMatch = {
  __typename?: 'SqlColumnMatch';
  matchType: MatchType;
  sampleArg: Scalars['String'];
  userArg: Scalars['String'];
};

export type SqlCorrectionResult = {
  __typename?: 'SqlCorrectionResult';
  result: SqlResult;
  solutionId: Scalars['Int'];
};

export type SqlExecutionResult = {
  __typename?: 'SqlExecutionResult';
  sampleResult?: Maybe<SqlQueryResult>;
  userResult?: Maybe<SqlQueryResult>;
};

export type SqlExerciseContent = {
  __typename?: 'SqlExerciseContent';
  exerciseType: SqlExerciseType;
  hint?: Maybe<Scalars['String']>;
  sampleSolutions: Array<Scalars['String']>;
  schemaName: Scalars['String'];
  sqlDbContents: Array<SqlQueryResult>;
};

export type SqlExerciseMutations = {
  __typename?: 'SqlExerciseMutations';
  correct: SqlCorrectionResult;
};


export type SqlExerciseMutationsCorrectArgs = {
  solution: Scalars['String'];
};

export enum SqlExerciseType {
  Create = 'CREATE',
  Delete = 'DELETE',
  Insert = 'INSERT',
  Select = 'SELECT',
  Update = 'UPDATE'
}

export type SqlKeyCellValueObject = {
  __typename?: 'SqlKeyCellValueObject';
  key: Scalars['String'];
  value: SqlCell;
};

export type SqlQueriesStaticComparison = {
  __typename?: 'SqlQueriesStaticComparison';
  additionalComparisons: AdditionalComparison;
  columnComparison: SqlColumnComparisonMatchingResult;
  joinExpressionComparison: SqlBinaryExpressionComparisonMatchingResult;
  tableComparison: StringMatchingResult;
  whereComparison: SqlBinaryExpressionComparisonMatchingResult;
};

export type SqlQueryResult = {
  __typename?: 'SqlQueryResult';
  columnNames: Array<Scalars['String']>;
  rows: Array<SqlRow>;
  tableName: Scalars['String'];
};

export type SqlResult = {
  __typename?: 'SqlResult';
  executionResult: SqlExecutionResult;
  maxPoints: Scalars['Float'];
  points: Scalars['Float'];
  staticComparison: SqlQueriesStaticComparison;
};

export type SqlRow = {
  __typename?: 'SqlRow';
  cells: Array<SqlKeyCellValueObject>;
};

export type StringMatch = {
  __typename?: 'StringMatch';
  matchType: MatchType;
  sampleArg: Scalars['String'];
  userArg: Scalars['String'];
};

export type StringMatchingResult = {
  __typename?: 'StringMatchingResult';
  allMatches: Array<StringMatch>;
  maxPoints: Scalars['Float'];
  notMatchedForSample: Array<Scalars['String']>;
  notMatchedForUser: Array<Scalars['String']>;
  points: Scalars['Float'];
};

export type Tool = {
  __typename?: 'Tool';
  allExercises: Array<Exercise>;
  collection: ExerciseCollection;
  collectionCount: Scalars['Int'];
  collections: Array<ExerciseCollection>;
  exerciseCount: Scalars['Int'];
  id: Scalars['String'];
  isBeta: Scalars['Boolean'];
  name: Scalars['String'];
  proficiencies: Array<UserProficiency>;
};


export type ToolCollectionArgs = {
  collId: Scalars['Int'];
};

export type Topic = {
  __typename?: 'Topic';
  abbreviation: Scalars['String'];
  title: Scalars['String'];
  toolId: Scalars['String'];
};

export type TopicWithLevel = {
  __typename?: 'TopicWithLevel';
  level: Level;
  topic: Topic;
};

export type UmlAssociation = {
  __typename?: 'UmlAssociation';
  assocName?: Maybe<Scalars['String']>;
  assocType: UmlAssociationType;
  firstEnd: Scalars['String'];
  firstMult: UmlMultiplicity;
  secondEnd: Scalars['String'];
  secondMult: UmlMultiplicity;
};

export type UmlAssociationAnalysisResult = {
  __typename?: 'UmlAssociationAnalysisResult';
  assocTypeEqual: Scalars['Boolean'];
  correctAssocType: UmlAssociationType;
  endsParallel: Scalars['Boolean'];
  multiplicitiesEqual: Scalars['Boolean'];
};

export type UmlAssociationInput = {
  assocName?: InputMaybe<Scalars['String']>;
  assocType?: InputMaybe<UmlAssociationType>;
  firstEnd: Scalars['String'];
  firstMult: UmlMultiplicity;
  secondEnd: Scalars['String'];
  secondMult: UmlMultiplicity;
};

export type UmlAssociationMatch = {
  __typename?: 'UmlAssociationMatch';
  analysisResult: UmlAssociationAnalysisResult;
  matchType: MatchType;
  sampleArg: UmlAssociation;
  userArg: UmlAssociation;
};

export type UmlAssociationMatchingResult = {
  __typename?: 'UmlAssociationMatchingResult';
  allMatches: Array<UmlAssociationMatch>;
  maxPoints: Scalars['Float'];
  notMatchedForSample: Array<UmlAssociation>;
  notMatchedForUser: Array<UmlAssociation>;
  points: Scalars['Float'];
};

export enum UmlAssociationType {
  Aggregation = 'AGGREGATION',
  Association = 'ASSOCIATION',
  Composition = 'COMPOSITION'
}

export type UmlAttribute = {
  __typename?: 'UmlAttribute';
  isAbstract: Scalars['Boolean'];
  isDerived: Scalars['Boolean'];
  isStatic: Scalars['Boolean'];
  memberName: Scalars['String'];
  memberType: Scalars['String'];
  visibility: UmlVisibility;
};

export type UmlAttributeAnalysisResult = {
  __typename?: 'UmlAttributeAnalysisResult';
  abstractCorrect: Scalars['Boolean'];
  correctAbstract: Scalars['Boolean'];
  correctDerived: Scalars['Boolean'];
  correctStatic: Scalars['Boolean'];
  correctType: Scalars['String'];
  correctVisibility: UmlVisibility;
  derivedCorrect: Scalars['Boolean'];
  staticCorrect: Scalars['Boolean'];
  typeComparison: Scalars['Boolean'];
  visibilityComparison: Scalars['Boolean'];
};

export type UmlAttributeInput = {
  isAbstract?: InputMaybe<Scalars['Boolean']>;
  isDerived?: InputMaybe<Scalars['Boolean']>;
  isStatic?: InputMaybe<Scalars['Boolean']>;
  memberName: Scalars['String'];
  memberType: Scalars['String'];
  visibility?: InputMaybe<UmlVisibility>;
};

export type UmlAttributeMatch = {
  __typename?: 'UmlAttributeMatch';
  matchType: MatchType;
  maybeAnalysisResult: UmlAttributeAnalysisResult;
  sampleArg: UmlAttribute;
  userArg: UmlAttribute;
};

export type UmlAttributeMatchingResult = {
  __typename?: 'UmlAttributeMatchingResult';
  allMatches: Array<UmlAttributeMatch>;
  maxPoints: Scalars['Float'];
  notMatchedForSample: Array<UmlAttribute>;
  notMatchedForUser: Array<UmlAttribute>;
  points: Scalars['Float'];
};

export type UmlClass = {
  __typename?: 'UmlClass';
  attributes: Array<UmlAttribute>;
  classType: UmlClassType;
  methods: Array<UmlMethod>;
  name: Scalars['String'];
};

export type UmlClassDiagram = {
  __typename?: 'UmlClassDiagram';
  associations: Array<UmlAssociation>;
  classes: Array<UmlClass>;
  implementations: Array<UmlImplementation>;
};

export type UmlClassDiagramInput = {
  associations: Array<UmlAssociationInput>;
  classes: Array<UmlClassInput>;
  implementations: Array<UmlImplementationInput>;
};

export type UmlClassInput = {
  attributes?: InputMaybe<Array<UmlAttributeInput>>;
  classType?: InputMaybe<UmlClassType>;
  methods?: InputMaybe<Array<UmlMethodInput>>;
  name: Scalars['String'];
};

export type UmlClassMatch = {
  __typename?: 'UmlClassMatch';
  analysisResult: UmlClassMatchAnalysisResult;
  compAM: Scalars['Boolean'];
  matchType: MatchType;
  sampleArg: UmlClass;
  userArg: UmlClass;
};

export type UmlClassMatchAnalysisResult = {
  __typename?: 'UmlClassMatchAnalysisResult';
  classTypeCorrect: Scalars['Boolean'];
  correctClassType: UmlClassType;
  maybeAttributeMatchingResult?: Maybe<UmlAttributeMatchingResult>;
  maybeMethodMatchingResult?: Maybe<UmlMethodMatchingResult>;
};

export type UmlClassMatchingResult = {
  __typename?: 'UmlClassMatchingResult';
  allMatches: Array<UmlClassMatch>;
  maxPoints: Scalars['Float'];
  notMatchedForSample: Array<UmlClass>;
  notMatchedForUser: Array<UmlClass>;
  points: Scalars['Float'];
};

export enum UmlClassType {
  Abstract = 'ABSTRACT',
  Class = 'CLASS',
  Interface = 'INTERFACE'
}

export type UmlCorrectionResult = {
  __typename?: 'UmlCorrectionResult';
  result: UmlResult;
  solutionId: Scalars['Int'];
};

export enum UmlExPart {
  ClassSelection = 'ClassSelection',
  DiagramDrawing = 'DiagramDrawing',
  DiagramDrawingHelp = 'DiagramDrawingHelp',
  MemberAllocation = 'MemberAllocation'
}

export type UmlExerciseContent = {
  __typename?: 'UmlExerciseContent';
  mappings: Array<KeyValueObject>;
  sampleSolutions: Array<UmlClassDiagram>;
  toIgnore: Array<Scalars['String']>;
};

export type UmlExerciseMutations = {
  __typename?: 'UmlExerciseMutations';
  correct: UmlCorrectionResult;
};


export type UmlExerciseMutationsCorrectArgs = {
  part: UmlExPart;
  solution: UmlClassDiagramInput;
};

export type UmlImplementation = {
  __typename?: 'UmlImplementation';
  subClass: Scalars['String'];
  superClass: Scalars['String'];
};

export type UmlImplementationInput = {
  subClass: Scalars['String'];
  superClass: Scalars['String'];
};

export type UmlImplementationMatch = {
  __typename?: 'UmlImplementationMatch';
  matchType: MatchType;
  sampleArg: UmlImplementation;
  userArg: UmlImplementation;
};

export type UmlImplementationMatchingResult = {
  __typename?: 'UmlImplementationMatchingResult';
  allMatches: Array<UmlImplementationMatch>;
  maxPoints: Scalars['Float'];
  notMatchedForSample: Array<UmlImplementation>;
  notMatchedForUser: Array<UmlImplementation>;
  points: Scalars['Float'];
};

export type UmlMethod = {
  __typename?: 'UmlMethod';
  isAbstract: Scalars['Boolean'];
  isStatic: Scalars['Boolean'];
  memberName: Scalars['String'];
  memberType: Scalars['String'];
  parameters: Scalars['String'];
  visibility: UmlVisibility;
};

export type UmlMethodAnalysisResult = {
  __typename?: 'UmlMethodAnalysisResult';
  abstractCorrect: Scalars['Boolean'];
  correctAbstract: Scalars['Boolean'];
  correctParameters: Scalars['String'];
  correctStatic: Scalars['Boolean'];
  correctType: Scalars['String'];
  correctVisibility: UmlVisibility;
  parameterComparison: Scalars['Boolean'];
  staticCorrect: Scalars['Boolean'];
  typeComparison: Scalars['Boolean'];
  visibilityComparison: Scalars['Boolean'];
};

export type UmlMethodInput = {
  isAbstract?: InputMaybe<Scalars['Boolean']>;
  isStatic?: InputMaybe<Scalars['Boolean']>;
  memberName: Scalars['String'];
  memberType: Scalars['String'];
  parameters: Scalars['String'];
  visibility?: InputMaybe<UmlVisibility>;
};

export type UmlMethodMatch = {
  __typename?: 'UmlMethodMatch';
  matchType: MatchType;
  maybeAnalysisResult: UmlMethodAnalysisResult;
  sampleArg: UmlMethod;
  userArg: UmlMethod;
};

export type UmlMethodMatchingResult = {
  __typename?: 'UmlMethodMatchingResult';
  allMatches: Array<UmlMethodMatch>;
  maxPoints: Scalars['Float'];
  notMatchedForSample: Array<UmlMethod>;
  notMatchedForUser: Array<UmlMethod>;
  points: Scalars['Float'];
};

export enum UmlMultiplicity {
  Single = 'SINGLE',
  Unbound = 'UNBOUND'
}

export type UmlResult = {
  __typename?: 'UmlResult';
  assocResult?: Maybe<UmlAssociationMatchingResult>;
  classResult?: Maybe<UmlClassMatchingResult>;
  implResult?: Maybe<UmlImplementationMatchingResult>;
  maxPoints: Scalars['Float'];
  points: Scalars['Float'];
};

export enum UmlVisibility {
  Package = 'PACKAGE',
  Private = 'PRIVATE',
  Protected = 'PROTECTED',
  Public = 'PUBLIC'
}

export type UnitTestCorrectionResult = ProgrammingTestCorrectionResult & {
  __typename?: 'UnitTestCorrectionResult';
  description: Scalars['String'];
  shouldFail: Scalars['Boolean'];
  stderr: Array<Scalars['String']>;
  stdout: Array<Scalars['String']>;
  successful: Scalars['Boolean'];
  testId: Scalars['Int'];
  testSuccessful: Scalars['Boolean'];
};

export type UnitTestPart = {
  __typename?: 'UnitTestPart';
  folderName: Scalars['String'];
  testFileName: Scalars['String'];
  unitTestFiles: Array<ExerciseFile>;
  unitTestTestConfigs: Array<UnitTestTestConfig>;
  unitTestsDescription: Scalars['String'];
};

export type UnitTestTestConfig = {
  __typename?: 'UnitTestTestConfig';
  description: Scalars['String'];
  file: ExerciseFile;
  id: Scalars['Int'];
  shouldFail: Scalars['Boolean'];
};

export type UserCredentials = {
  password: Scalars['String'];
  username: Scalars['String'];
};

export type UserProficiency = {
  __typename?: 'UserProficiency';
  level: Level;
  points: Scalars['Int'];
  pointsForNextLevel: Scalars['Int'];
  topic: Topic;
  username: Scalars['String'];
};

export type WebCorrectionResult = {
  __typename?: 'WebCorrectionResult';
  result: WebResult;
  solutionId: Scalars['Int'];
};

export enum WebExPart {
  HtmlPart = 'HtmlPart',
  JsPart = 'JsPart'
}

export type WebExerciseContent = {
  __typename?: 'WebExerciseContent';
  files: Array<ExerciseFile>;
  htmlText?: Maybe<Scalars['String']>;
  jsText?: Maybe<Scalars['String']>;
  sampleSolutions: Array<FilesSolution>;
  siteSpec: SiteSpec;
};

export type WebExerciseMutations = {
  __typename?: 'WebExerciseMutations';
  correct: WebCorrectionResult;
};


export type WebExerciseMutationsCorrectArgs = {
  part: WebExPart;
  solution: FilesSolutionInput;
};

export type WebResult = {
  __typename?: 'WebResult';
  gradedHtmlTaskResults: Array<GradedHtmlTaskResult>;
  gradedJsTaskResults: Array<GradedJsTaskResult>;
  maxPoints: Scalars['Float'];
  points: Scalars['Float'];
};

export type XmlCorrectionResult = {
  __typename?: 'XmlCorrectionResult';
  result: XmlResult;
  solutionId: Scalars['Int'];
};

export type XmlDocumentResult = {
  __typename?: 'XmlDocumentResult';
  errors: Array<XmlError>;
};

export type XmlElementLineComparisonMatchingResult = {
  __typename?: 'XmlElementLineComparisonMatchingResult';
  allMatches: Array<ElementLineMatch>;
  maxPoints: Scalars['Float'];
  notMatchedForSample: Array<ElementLine>;
  notMatchedForUser: Array<ElementLine>;
  points: Scalars['Float'];
};

export type XmlError = {
  __typename?: 'XmlError';
  errorMessage: Scalars['String'];
  errorType: XmlErrorType;
  line: Scalars['Int'];
};

export enum XmlErrorType {
  Error = 'ERROR',
  Fatal = 'FATAL',
  Warning = 'WARNING'
}

export enum XmlExPart {
  DocumentCreationXmlPart = 'DocumentCreationXmlPart',
  GrammarCreationXmlPart = 'GrammarCreationXmlPart'
}

export type XmlExerciseContent = {
  __typename?: 'XmlExerciseContent';
  grammarDescription: Scalars['String'];
  rootNode: Scalars['String'];
  sampleSolutions: Array<XmlSolution>;
};

export type XmlExerciseMutations = {
  __typename?: 'XmlExerciseMutations';
  correct: XmlCorrectionResult;
};


export type XmlExerciseMutationsCorrectArgs = {
  part: XmlExPart;
  solution: XmlSolutionInput;
};

export type XmlGrammarResult = {
  __typename?: 'XmlGrammarResult';
  parseErrors: Array<DtdParseException>;
  results: XmlElementLineComparisonMatchingResult;
};

export type XmlResult = {
  __typename?: 'XmlResult';
  documentResult?: Maybe<XmlDocumentResult>;
  grammarResult?: Maybe<XmlGrammarResult>;
  maxPoints: Scalars['Float'];
  points: Scalars['Float'];
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

export type FlaskCorrectionMutationVariables = Exact<{
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  solution: FilesSolutionInput;
}>;


export type FlaskCorrectionMutation = { __typename?: 'Mutation', flaskExercise?: { __typename?: 'FlaskExerciseMutations', correct: { __typename?: 'FlaskCorrectionResult', solutionId: number, result: { __typename?: 'FlaskResult', points: number, maxPoints: number, testResults: Array<{ __typename?: 'FlaskTestResult', testName: string, successful: boolean, stdout: Array<string>, stderr: Array<string> }> } } } | null };

export type FlaskCorrectionResultFragment = { __typename?: 'FlaskCorrectionResult', solutionId: number, result: { __typename?: 'FlaskResult', points: number, maxPoints: number, testResults: Array<{ __typename?: 'FlaskTestResult', testName: string, successful: boolean, stdout: Array<string>, stderr: Array<string> }> } };

export type FlaskResultFragment = { __typename?: 'FlaskResult', points: number, maxPoints: number, testResults: Array<{ __typename?: 'FlaskTestResult', testName: string, successful: boolean, stdout: Array<string>, stderr: Array<string> }> };

export type FlaskTestResultFragment = { __typename?: 'FlaskTestResult', testName: string, successful: boolean, stdout: Array<string>, stderr: Array<string> };

export type ProgrammingCorrectionMutationVariables = Exact<{
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: ProgExPart;
  solution: FilesSolutionInput;
}>;


export type ProgrammingCorrectionMutation = { __typename?: 'Mutation', programmingExercise?: { __typename?: 'ProgrammingExerciseMutations', correct: { __typename?: 'ProgrammingCorrectionResult', solutionId: number, result: { __typename?: 'ProgrammingResult', points: number, maxPoints: number, implementationCorrectionResult?: { __typename?: 'ImplementationCorrectionResult', successful: boolean, stdout: Array<string>, stderr: Array<string> } | null, unitTestResults: Array<{ __typename?: 'UnitTestCorrectionResult', testId: number, successful: boolean, shouldFail: boolean, description: string, stderr: Array<string> }> } } } | null };

export type ProgrammingCorrectionResultFragment = { __typename?: 'ProgrammingCorrectionResult', solutionId: number, result: { __typename?: 'ProgrammingResult', points: number, maxPoints: number, implementationCorrectionResult?: { __typename?: 'ImplementationCorrectionResult', successful: boolean, stdout: Array<string>, stderr: Array<string> } | null, unitTestResults: Array<{ __typename?: 'UnitTestCorrectionResult', testId: number, successful: boolean, shouldFail: boolean, description: string, stderr: Array<string> }> } };

export type ProgrammingResultFragment = { __typename?: 'ProgrammingResult', points: number, maxPoints: number, implementationCorrectionResult?: { __typename?: 'ImplementationCorrectionResult', successful: boolean, stdout: Array<string>, stderr: Array<string> } | null, unitTestResults: Array<{ __typename?: 'UnitTestCorrectionResult', testId: number, successful: boolean, shouldFail: boolean, description: string, stderr: Array<string> }> };

export type ImplementationCorrectionResultFragment = { __typename?: 'ImplementationCorrectionResult', successful: boolean, stdout: Array<string>, stderr: Array<string> };

export type UnitTestCorrectionResultFragment = { __typename?: 'UnitTestCorrectionResult', testId: number, successful: boolean, shouldFail: boolean, description: string, stderr: Array<string> };

export type RegexCorrectionMutationVariables = Exact<{
  collectionId: Scalars['Int'];
  exerciseId: Scalars['Int'];
  solution: Scalars['String'];
}>;


export type RegexCorrectionMutation = { __typename?: 'Mutation', regexExercise?: { __typename?: 'RegexExerciseMutations', correct: { __typename?: 'RegexCorrectionResult', solutionId: number, result: { __typename: 'RegexExtractionResult', points: number, maxPoints: number, extractionResults: Array<{ __typename?: 'RegexExtractionSingleResult', base: string, extractionMatchingResult: { __typename?: 'RegexExtractedValuesComparisonMatchingResult', notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, points: number, maxPoints: number, allMatches: Array<{ __typename?: 'RegexMatchMatch', matchType: MatchType, userArg: string, sampleArg: string }> } }> } | { __typename: 'RegexMatchingResult', points: number, maxPoints: number, matchingResults: Array<{ __typename?: 'RegexMatchingSingleResult', resultType: BinaryClassificationResultType, matchData: string }> } } } | null };

export type RegexCorrectionResultFragment = { __typename?: 'RegexCorrectionResult', solutionId: number, result: { __typename: 'RegexExtractionResult', points: number, maxPoints: number, extractionResults: Array<{ __typename?: 'RegexExtractionSingleResult', base: string, extractionMatchingResult: { __typename?: 'RegexExtractedValuesComparisonMatchingResult', notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, points: number, maxPoints: number, allMatches: Array<{ __typename?: 'RegexMatchMatch', matchType: MatchType, userArg: string, sampleArg: string }> } }> } | { __typename: 'RegexMatchingResult', points: number, maxPoints: number, matchingResults: Array<{ __typename?: 'RegexMatchingSingleResult', resultType: BinaryClassificationResultType, matchData: string }> } };

export type RegexMatchingSingleResultFragment = { __typename?: 'RegexMatchingSingleResult', resultType: BinaryClassificationResultType, matchData: string };

export type RegexMatchingResultFragment = { __typename?: 'RegexMatchingResult', matchingResults: Array<{ __typename?: 'RegexMatchingSingleResult', resultType: BinaryClassificationResultType, matchData: string }> };

export type RegexExtractionMatchFragment = { __typename?: 'RegexMatchMatch', matchType: MatchType, userArg: string, sampleArg: string };

export type ExtractionMatchingResultFragment = { __typename?: 'RegexExtractedValuesComparisonMatchingResult', notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, points: number, maxPoints: number, allMatches: Array<{ __typename?: 'RegexMatchMatch', matchType: MatchType, userArg: string, sampleArg: string }> };

export type RegexExtractionSingleResultFragment = { __typename?: 'RegexExtractionSingleResult', base: string, extractionMatchingResult: { __typename?: 'RegexExtractedValuesComparisonMatchingResult', notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, points: number, maxPoints: number, allMatches: Array<{ __typename?: 'RegexMatchMatch', matchType: MatchType, userArg: string, sampleArg: string }> } };

export type RegexExtractionResultFragment = { __typename?: 'RegexExtractionResult', extractionResults: Array<{ __typename?: 'RegexExtractionSingleResult', base: string, extractionMatchingResult: { __typename?: 'RegexExtractedValuesComparisonMatchingResult', notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, points: number, maxPoints: number, allMatches: Array<{ __typename?: 'RegexMatchMatch', matchType: MatchType, userArg: string, sampleArg: string }> } }> };

export type SqlCorrectionMutationVariables = Exact<{
  collectionId: Scalars['Int'];
  exerciseId: Scalars['Int'];
  solution: Scalars['String'];
}>;


export type SqlCorrectionMutation = { __typename?: 'Mutation', sqlExercise?: { __typename?: 'SqlExerciseMutations', correct: { __typename?: 'SqlCorrectionResult', solutionId: number, result: { __typename?: 'SqlResult', points: number, maxPoints: number, staticComparison: { __typename?: 'SqlQueriesStaticComparison', columnComparison: { __typename?: 'SqlColumnComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlColumnMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, tableComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, joinExpressionComparison: { __typename?: 'SqlBinaryExpressionComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlBinaryExpressionMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, whereComparison: { __typename?: 'SqlBinaryExpressionComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlBinaryExpressionMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, additionalComparisons: { __typename?: 'AdditionalComparison', selectComparisons?: { __typename?: 'SelectAdditionalComparisons', groupByComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, orderByComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, limitComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> } } | null, insertComparison?: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> } | null } }, executionResult: { __typename?: 'SqlExecutionResult', userResult?: { __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: string | null, different: boolean } }> }> } | null, sampleResult?: { __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: string | null, different: boolean } }> }> } | null } } } } | null };

export type SqlCorrectionResultFragment = { __typename?: 'SqlCorrectionResult', solutionId: number, result: { __typename?: 'SqlResult', points: number, maxPoints: number, staticComparison: { __typename?: 'SqlQueriesStaticComparison', columnComparison: { __typename?: 'SqlColumnComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlColumnMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, tableComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, joinExpressionComparison: { __typename?: 'SqlBinaryExpressionComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlBinaryExpressionMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, whereComparison: { __typename?: 'SqlBinaryExpressionComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlBinaryExpressionMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, additionalComparisons: { __typename?: 'AdditionalComparison', selectComparisons?: { __typename?: 'SelectAdditionalComparisons', groupByComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, orderByComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, limitComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> } } | null, insertComparison?: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> } | null } }, executionResult: { __typename?: 'SqlExecutionResult', userResult?: { __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: string | null, different: boolean } }> }> } | null, sampleResult?: { __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: string | null, different: boolean } }> }> } | null } } };

export type SelectAdditionalComparisonFragment = { __typename?: 'SelectAdditionalComparisons', groupByComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, orderByComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, limitComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> } };

export type StaticComparisonFragment = { __typename?: 'SqlQueriesStaticComparison', columnComparison: { __typename?: 'SqlColumnComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlColumnMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, tableComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, joinExpressionComparison: { __typename?: 'SqlBinaryExpressionComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlBinaryExpressionMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, whereComparison: { __typename?: 'SqlBinaryExpressionComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlBinaryExpressionMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, additionalComparisons: { __typename?: 'AdditionalComparison', selectComparisons?: { __typename?: 'SelectAdditionalComparisons', groupByComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, orderByComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, limitComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> } } | null, insertComparison?: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> } | null } };

export type SqlResultFragment = { __typename?: 'SqlResult', points: number, maxPoints: number, staticComparison: { __typename?: 'SqlQueriesStaticComparison', columnComparison: { __typename?: 'SqlColumnComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlColumnMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, tableComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, joinExpressionComparison: { __typename?: 'SqlBinaryExpressionComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlBinaryExpressionMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, whereComparison: { __typename?: 'SqlBinaryExpressionComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlBinaryExpressionMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, additionalComparisons: { __typename?: 'AdditionalComparison', selectComparisons?: { __typename?: 'SelectAdditionalComparisons', groupByComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, orderByComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, limitComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> } } | null, insertComparison?: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> } | null } }, executionResult: { __typename?: 'SqlExecutionResult', userResult?: { __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: string | null, different: boolean } }> }> } | null, sampleResult?: { __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: string | null, different: boolean } }> }> } | null } };

export type SqlExecutionResultFragment = { __typename?: 'SqlExecutionResult', userResult?: { __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: string | null, different: boolean } }> }> } | null, sampleResult?: { __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: string | null, different: boolean } }> }> } | null };

export type SqlQueryResultFragment = { __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: string | null, different: boolean } }> }> };

export type SqlRowFragment = { __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: string | null, different: boolean } }> };

export type SqlCellFragment = { __typename?: 'SqlCell', colName: string, content?: string | null, different: boolean };

export type StringMatchFragment = { __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string };

export type StringMatchingResultFragment = { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> };

export type SqlColumnComparisonMatchFragment = { __typename?: 'SqlColumnMatch', matchType: MatchType, userArg: string, sampleArg: string };

export type SqlColumnComparisonMatchingResultFragment = { __typename?: 'SqlColumnComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlColumnMatch', matchType: MatchType, userArg: string, sampleArg: string }> };

export type SqlBinaryExpressionMatchFragment = { __typename?: 'SqlBinaryExpressionMatch', matchType: MatchType, userArg: string, sampleArg: string };

export type SqlBinaryExpressionComparisonMatchingResultFragment = { __typename?: 'SqlBinaryExpressionComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlBinaryExpressionMatch', matchType: MatchType, userArg: string, sampleArg: string }> };

export type UmlCorrectionMutationVariables = Exact<{
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: UmlExPart;
  solution: UmlClassDiagramInput;
}>;


export type UmlCorrectionMutation = { __typename?: 'Mutation', umlExercise?: { __typename?: 'UmlExerciseMutations', correct: { __typename?: 'UmlCorrectionResult', solutionId: number, result: { __typename?: 'UmlResult', points: number, maxPoints: number, classResult?: { __typename?: 'UmlClassMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlClassMatch', matchType: MatchType, userArg: { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }, sampleArg: { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }, analysisResult: { __typename: 'UmlClassMatchAnalysisResult' } }>, notMatchedForUser: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }>, notMatchedForSample: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }> } | null, assocResult?: { __typename?: 'UmlAssociationMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlAssociationMatch', matchType: MatchType, userArg: { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }, sampleArg: { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }, analysisResult: { __typename?: 'UmlAssociationAnalysisResult', assocTypeEqual: boolean, correctAssocType: UmlAssociationType, multiplicitiesEqual: boolean } }>, notMatchedForUser: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }>, notMatchedForSample: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }> } | null, implResult?: { __typename?: 'UmlImplementationMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlImplementationMatch', matchType: MatchType, userArg: { __typename?: 'UmlImplementation', subClass: string, superClass: string }, sampleArg: { __typename?: 'UmlImplementation', subClass: string, superClass: string } }>, notMatchedForUser: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }>, notMatchedForSample: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }> } | null } } } | null };

export type UmlCorrectionResultFragment = { __typename?: 'UmlCorrectionResult', solutionId: number, result: { __typename?: 'UmlResult', points: number, maxPoints: number, classResult?: { __typename?: 'UmlClassMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlClassMatch', matchType: MatchType, userArg: { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }, sampleArg: { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }, analysisResult: { __typename: 'UmlClassMatchAnalysisResult' } }>, notMatchedForUser: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }>, notMatchedForSample: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }> } | null, assocResult?: { __typename?: 'UmlAssociationMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlAssociationMatch', matchType: MatchType, userArg: { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }, sampleArg: { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }, analysisResult: { __typename?: 'UmlAssociationAnalysisResult', assocTypeEqual: boolean, correctAssocType: UmlAssociationType, multiplicitiesEqual: boolean } }>, notMatchedForUser: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }>, notMatchedForSample: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }> } | null, implResult?: { __typename?: 'UmlImplementationMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlImplementationMatch', matchType: MatchType, userArg: { __typename?: 'UmlImplementation', subClass: string, superClass: string }, sampleArg: { __typename?: 'UmlImplementation', subClass: string, superClass: string } }>, notMatchedForUser: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }>, notMatchedForSample: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }> } | null } };

export type UmlResultFragment = { __typename?: 'UmlResult', points: number, maxPoints: number, classResult?: { __typename?: 'UmlClassMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlClassMatch', matchType: MatchType, userArg: { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }, sampleArg: { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }, analysisResult: { __typename: 'UmlClassMatchAnalysisResult' } }>, notMatchedForUser: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }>, notMatchedForSample: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }> } | null, assocResult?: { __typename?: 'UmlAssociationMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlAssociationMatch', matchType: MatchType, userArg: { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }, sampleArg: { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }, analysisResult: { __typename?: 'UmlAssociationAnalysisResult', assocTypeEqual: boolean, correctAssocType: UmlAssociationType, multiplicitiesEqual: boolean } }>, notMatchedForUser: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }>, notMatchedForSample: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }> } | null, implResult?: { __typename?: 'UmlImplementationMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlImplementationMatch', matchType: MatchType, userArg: { __typename?: 'UmlImplementation', subClass: string, superClass: string }, sampleArg: { __typename?: 'UmlImplementation', subClass: string, superClass: string } }>, notMatchedForUser: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }>, notMatchedForSample: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }> } | null };

export type UmlClassMatchingResultFragment = { __typename?: 'UmlClassMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlClassMatch', matchType: MatchType, userArg: { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }, sampleArg: { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }, analysisResult: { __typename: 'UmlClassMatchAnalysisResult' } }>, notMatchedForUser: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }>, notMatchedForSample: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }> };

export type UmlClassMatchFragment = { __typename?: 'UmlClassMatch', matchType: MatchType, userArg: { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }, sampleArg: { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }, analysisResult: { __typename: 'UmlClassMatchAnalysisResult' } };

export type UmlSolutionClassFragment = { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> };

export type UmlAssociationMatchingResultFragment = { __typename?: 'UmlAssociationMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlAssociationMatch', matchType: MatchType, userArg: { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }, sampleArg: { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }, analysisResult: { __typename?: 'UmlAssociationAnalysisResult', assocTypeEqual: boolean, correctAssocType: UmlAssociationType, multiplicitiesEqual: boolean } }>, notMatchedForUser: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }>, notMatchedForSample: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }> };

export type UmlAssociationMatchFragment = { __typename?: 'UmlAssociationMatch', matchType: MatchType, userArg: { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }, sampleArg: { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }, analysisResult: { __typename?: 'UmlAssociationAnalysisResult', assocTypeEqual: boolean, correctAssocType: UmlAssociationType, multiplicitiesEqual: boolean } };

export type UmlAssociationFragment = { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity };

export type UmlImplementationMatchingResultFragment = { __typename?: 'UmlImplementationMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlImplementationMatch', matchType: MatchType, userArg: { __typename?: 'UmlImplementation', subClass: string, superClass: string }, sampleArg: { __typename?: 'UmlImplementation', subClass: string, superClass: string } }>, notMatchedForUser: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }>, notMatchedForSample: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }> };

export type UmlImplementationMatchFragment = { __typename?: 'UmlImplementationMatch', matchType: MatchType, userArg: { __typename?: 'UmlImplementation', subClass: string, superClass: string }, sampleArg: { __typename?: 'UmlImplementation', subClass: string, superClass: string } };

export type UmlImplementationFragment = { __typename?: 'UmlImplementation', subClass: string, superClass: string };

export type WebCorrectionMutationVariables = Exact<{
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: WebExPart;
  solution: FilesSolutionInput;
}>;


export type WebCorrectionMutation = { __typename?: 'Mutation', webExercise?: { __typename?: 'WebExerciseMutations', correct: { __typename?: 'WebCorrectionResult', solutionId: number, result: { __typename?: 'WebResult', points: number, maxPoints: number, gradedHtmlTaskResults: Array<{ __typename?: 'GradedHtmlTaskResult', id: number, elementSpecResult: { __typename?: 'GradedElementSpecResult', isCorrect: boolean, elementFound: boolean, points: number, maxPoints: number, textContentResult?: { __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number } | null, attributeResults: Array<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number }> } }>, gradedJsTaskResults: Array<{ __typename?: 'GradedJsTaskResult', id: number, points: number, maxPoints: number, gradedPreResults: Array<{ __typename?: 'GradedElementSpecResult', isCorrect: boolean, elementFound: boolean, points: number, maxPoints: number, textContentResult?: { __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number } | null, attributeResults: Array<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number }> }>, gradedJsActionResult: { __typename?: 'GradedJsActionResult', actionPerformed: boolean, points: number, maxPoints: number, jsAction: { __typename?: 'JsAction', actionType: JsActionType, keysToSend?: string | null, xpathQuery: string } }, gradedPostResults: Array<{ __typename?: 'GradedElementSpecResult', isCorrect: boolean, elementFound: boolean, points: number, maxPoints: number, textContentResult?: { __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number } | null, attributeResults: Array<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number }> }> }> } } } | null };

export type WebCorrectionResultFragment = { __typename?: 'WebCorrectionResult', solutionId: number, result: { __typename?: 'WebResult', points: number, maxPoints: number, gradedHtmlTaskResults: Array<{ __typename?: 'GradedHtmlTaskResult', id: number, elementSpecResult: { __typename?: 'GradedElementSpecResult', isCorrect: boolean, elementFound: boolean, points: number, maxPoints: number, textContentResult?: { __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number } | null, attributeResults: Array<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number }> } }>, gradedJsTaskResults: Array<{ __typename?: 'GradedJsTaskResult', id: number, points: number, maxPoints: number, gradedPreResults: Array<{ __typename?: 'GradedElementSpecResult', isCorrect: boolean, elementFound: boolean, points: number, maxPoints: number, textContentResult?: { __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number } | null, attributeResults: Array<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number }> }>, gradedJsActionResult: { __typename?: 'GradedJsActionResult', actionPerformed: boolean, points: number, maxPoints: number, jsAction: { __typename?: 'JsAction', actionType: JsActionType, keysToSend?: string | null, xpathQuery: string } }, gradedPostResults: Array<{ __typename?: 'GradedElementSpecResult', isCorrect: boolean, elementFound: boolean, points: number, maxPoints: number, textContentResult?: { __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number } | null, attributeResults: Array<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number }> }> }> } };

export type WebResultFragment = { __typename?: 'WebResult', points: number, maxPoints: number, gradedHtmlTaskResults: Array<{ __typename?: 'GradedHtmlTaskResult', id: number, elementSpecResult: { __typename?: 'GradedElementSpecResult', isCorrect: boolean, elementFound: boolean, points: number, maxPoints: number, textContentResult?: { __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number } | null, attributeResults: Array<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number }> } }>, gradedJsTaskResults: Array<{ __typename?: 'GradedJsTaskResult', id: number, points: number, maxPoints: number, gradedPreResults: Array<{ __typename?: 'GradedElementSpecResult', isCorrect: boolean, elementFound: boolean, points: number, maxPoints: number, textContentResult?: { __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number } | null, attributeResults: Array<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number }> }>, gradedJsActionResult: { __typename?: 'GradedJsActionResult', actionPerformed: boolean, points: number, maxPoints: number, jsAction: { __typename?: 'JsAction', actionType: JsActionType, keysToSend?: string | null, xpathQuery: string } }, gradedPostResults: Array<{ __typename?: 'GradedElementSpecResult', isCorrect: boolean, elementFound: boolean, points: number, maxPoints: number, textContentResult?: { __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number } | null, attributeResults: Array<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number }> }> }> };

export type GradedElementSpecResultFragment = { __typename?: 'GradedElementSpecResult', isCorrect: boolean, elementFound: boolean, points: number, maxPoints: number, textContentResult?: { __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number } | null, attributeResults: Array<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number }> };

export type GradedTextContentResultFragment = { __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number };

export type GradedHtmlTaskResultFragment = { __typename?: 'GradedHtmlTaskResult', id: number, elementSpecResult: { __typename?: 'GradedElementSpecResult', isCorrect: boolean, elementFound: boolean, points: number, maxPoints: number, textContentResult?: { __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number } | null, attributeResults: Array<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number }> } };

export type GradedJsTaskResultFragment = { __typename?: 'GradedJsTaskResult', id: number, points: number, maxPoints: number, gradedPreResults: Array<{ __typename?: 'GradedElementSpecResult', isCorrect: boolean, elementFound: boolean, points: number, maxPoints: number, textContentResult?: { __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number } | null, attributeResults: Array<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number }> }>, gradedJsActionResult: { __typename?: 'GradedJsActionResult', actionPerformed: boolean, points: number, maxPoints: number, jsAction: { __typename?: 'JsAction', actionType: JsActionType, keysToSend?: string | null, xpathQuery: string } }, gradedPostResults: Array<{ __typename?: 'GradedElementSpecResult', isCorrect: boolean, elementFound: boolean, points: number, maxPoints: number, textContentResult?: { __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number } | null, attributeResults: Array<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: string | null, isSuccessful: boolean, points: number, maxPoints: number }> }> };

export type GradedJsActionResultFragment = { __typename?: 'GradedJsActionResult', actionPerformed: boolean, points: number, maxPoints: number, jsAction: { __typename?: 'JsAction', actionType: JsActionType, keysToSend?: string | null, xpathQuery: string } };

export type XmlCorrectionMutationVariables = Exact<{
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: XmlExPart;
  solution: XmlSolutionInput;
}>;


export type XmlCorrectionMutation = { __typename?: 'Mutation', xmlExercise?: { __typename?: 'XmlExerciseMutations', correct: { __typename?: 'XmlCorrectionResult', solutionId: number, result: { __typename?: 'XmlResult', points: number, maxPoints: number, grammarResult?: { __typename?: 'XmlGrammarResult', parseErrors: Array<{ __typename?: 'DTDParseException', msg: string, parsedLine: string }>, results: { __typename?: 'XmlElementLineComparisonMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'ElementLineMatch', matchType: MatchType, userArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, sampleArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, analysisResult: { __typename?: 'ElementLineAnalysisResult', attributesCorrect: boolean, correctAttributes: string, contentCorrect: boolean, correctContent: string } }>, notMatchedForUser: Array<{ __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }>, notMatchedForSample: Array<{ __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }> } } | null, documentResult?: { __typename?: 'XmlDocumentResult', errors: Array<{ __typename?: 'XmlError', line: number, errorType: XmlErrorType, errorMessage: string }> } | null } } } | null };

export type XmlCorrectionResultFragment = { __typename?: 'XmlCorrectionResult', solutionId: number, result: { __typename?: 'XmlResult', points: number, maxPoints: number, grammarResult?: { __typename?: 'XmlGrammarResult', parseErrors: Array<{ __typename?: 'DTDParseException', msg: string, parsedLine: string }>, results: { __typename?: 'XmlElementLineComparisonMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'ElementLineMatch', matchType: MatchType, userArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, sampleArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, analysisResult: { __typename?: 'ElementLineAnalysisResult', attributesCorrect: boolean, correctAttributes: string, contentCorrect: boolean, correctContent: string } }>, notMatchedForUser: Array<{ __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }>, notMatchedForSample: Array<{ __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }> } } | null, documentResult?: { __typename?: 'XmlDocumentResult', errors: Array<{ __typename?: 'XmlError', line: number, errorType: XmlErrorType, errorMessage: string }> } | null } };

export type XmlResultFragment = { __typename?: 'XmlResult', points: number, maxPoints: number, grammarResult?: { __typename?: 'XmlGrammarResult', parseErrors: Array<{ __typename?: 'DTDParseException', msg: string, parsedLine: string }>, results: { __typename?: 'XmlElementLineComparisonMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'ElementLineMatch', matchType: MatchType, userArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, sampleArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, analysisResult: { __typename?: 'ElementLineAnalysisResult', attributesCorrect: boolean, correctAttributes: string, contentCorrect: boolean, correctContent: string } }>, notMatchedForUser: Array<{ __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }>, notMatchedForSample: Array<{ __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }> } } | null, documentResult?: { __typename?: 'XmlDocumentResult', errors: Array<{ __typename?: 'XmlError', line: number, errorType: XmlErrorType, errorMessage: string }> } | null };

export type XmlGrammarResultFragment = { __typename?: 'XmlGrammarResult', parseErrors: Array<{ __typename?: 'DTDParseException', msg: string, parsedLine: string }>, results: { __typename?: 'XmlElementLineComparisonMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'ElementLineMatch', matchType: MatchType, userArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, sampleArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, analysisResult: { __typename?: 'ElementLineAnalysisResult', attributesCorrect: boolean, correctAttributes: string, contentCorrect: boolean, correctContent: string } }>, notMatchedForUser: Array<{ __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }>, notMatchedForSample: Array<{ __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }> } };

export type XmlElementLineMatchingResultFragment = { __typename?: 'XmlElementLineComparisonMatchingResult', allMatches: Array<{ __typename?: 'ElementLineMatch', matchType: MatchType, userArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, sampleArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, analysisResult: { __typename?: 'ElementLineAnalysisResult', attributesCorrect: boolean, correctAttributes: string, contentCorrect: boolean, correctContent: string } }>, notMatchedForUser: Array<{ __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }>, notMatchedForSample: Array<{ __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }> };

export type XmlElementLineMatchFragment = { __typename?: 'ElementLineMatch', matchType: MatchType, userArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, sampleArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, analysisResult: { __typename?: 'ElementLineAnalysisResult', attributesCorrect: boolean, correctAttributes: string, contentCorrect: boolean, correctContent: string } };

export type XmlElementLineAnalysisResultFragment = { __typename?: 'ElementLineAnalysisResult', attributesCorrect: boolean, correctAttributes: string, contentCorrect: boolean, correctContent: string };

export type ElementLineFragment = { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> };

export type XmlDocumentResultFragment = { __typename?: 'XmlDocumentResult', errors: Array<{ __typename?: 'XmlError', line: number, errorType: XmlErrorType, errorMessage: string }> };

export type XmlErrorFragment = { __typename?: 'XmlError', line: number, errorType: XmlErrorType, errorMessage: string };

export type ExerciseFileFragment = { __typename?: 'ExerciseFile', name: string, content: string, editable: boolean };

export type FilesSolutionFragment = { __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> };

export type LevelFragment = { __typename?: 'Level', title: string, levelIndex: number };

export type TopicFragment = { __typename?: 'Topic', abbreviation: string, title: string };

export type TopicWithLevelFragment = { __typename?: 'TopicWithLevel', topic: { __typename?: 'Topic', abbreviation: string, title: string }, level: { __typename?: 'Level', title: string, levelIndex: number } };

export type PartFragment = { __typename?: 'ExPart', id: string, name: string, isEntryPart: boolean, solved: boolean };

export type FieldsForLinkFragment = { __typename?: 'Exercise', exerciseId: number, collectionId: number, toolId: string, title: string, difficulty: { __typename?: 'Level', title: string, levelIndex: number }, topicsWithLevels: Array<{ __typename?: 'TopicWithLevel', topic: { __typename?: 'Topic', abbreviation: string, title: string }, level: { __typename?: 'Level', title: string, levelIndex: number } }>, parts: Array<{ __typename?: 'ExPart', id: string, name: string, isEntryPart: boolean, solved: boolean }> };

export type ToolFragment = { __typename?: 'Tool', id: string, name: string, collectionCount: number, exerciseCount: number };

export type ToolOverviewQueryVariables = Exact<{ [key: string]: never; }>;


export type ToolOverviewQuery = { __typename?: 'Query', tools: Array<{ __typename?: 'Tool', id: string, name: string, collectionCount: number, exerciseCount: number }> };

export type UserProficiencyFragment = { __typename?: 'UserProficiency', points: number, pointsForNextLevel: number, topic: { __typename?: 'Topic', abbreviation: string, title: string }, level: { __typename?: 'Level', title: string, levelIndex: number } };

export type ToolOverviewFragment = { __typename?: 'Tool', name: string, collectionCount: number, exerciseCount: number, proficiencies: Array<{ __typename?: 'UserProficiency', points: number, pointsForNextLevel: number, topic: { __typename?: 'Topic', abbreviation: string, title: string }, level: { __typename?: 'Level', title: string, levelIndex: number } }> };

export type CollectionToolOverviewQueryVariables = Exact<{
  toolId: Scalars['String'];
}>;


export type CollectionToolOverviewQuery = { __typename?: 'Query', tool: { __typename?: 'Tool', name: string, collectionCount: number, exerciseCount: number, proficiencies: Array<{ __typename?: 'UserProficiency', points: number, pointsForNextLevel: number, topic: { __typename?: 'Topic', abbreviation: string, title: string }, level: { __typename?: 'Level', title: string, levelIndex: number } }> } };

export type AllExercisesOverviewExerciseFragment = { __typename?: 'Exercise', exerciseId: number, collectionId: number, toolId: string, title: string, topicsWithLevels: Array<{ __typename?: 'TopicWithLevel', topic: { __typename?: 'Topic', abbreviation: string, title: string }, level: { __typename?: 'Level', title: string, levelIndex: number } }>, difficulty: { __typename?: 'Level', title: string, levelIndex: number }, parts: Array<{ __typename?: 'ExPart', id: string, name: string, isEntryPart: boolean, solved: boolean }> };

export type AllExesOverviewToolFragment = { __typename?: 'Tool', name: string, allExercises: Array<{ __typename?: 'Exercise', exerciseId: number, collectionId: number, toolId: string, title: string, topicsWithLevels: Array<{ __typename?: 'TopicWithLevel', topic: { __typename?: 'Topic', abbreviation: string, title: string }, level: { __typename?: 'Level', title: string, levelIndex: number } }>, difficulty: { __typename?: 'Level', title: string, levelIndex: number }, parts: Array<{ __typename?: 'ExPart', id: string, name: string, isEntryPart: boolean, solved: boolean }> }> };

export type AllExercisesOverviewQueryVariables = Exact<{
  toolId: Scalars['String'];
}>;


export type AllExercisesOverviewQuery = { __typename?: 'Query', tool: { __typename?: 'Tool', name: string, allExercises: Array<{ __typename?: 'Exercise', exerciseId: number, collectionId: number, toolId: string, title: string, topicsWithLevels: Array<{ __typename?: 'TopicWithLevel', topic: { __typename?: 'Topic', abbreviation: string, title: string }, level: { __typename?: 'Level', title: string, levelIndex: number } }>, difficulty: { __typename?: 'Level', title: string, levelIndex: number }, parts: Array<{ __typename?: 'ExPart', id: string, name: string, isEntryPart: boolean, solved: boolean }> }> } };

export type CollectionValuesFragment = { __typename?: 'ExerciseCollection', collectionId: number, title: string, exerciseCount: number };

export type ToolCollectionOverviewFragment = { __typename?: 'Tool', name: string, collections: Array<{ __typename?: 'ExerciseCollection', collectionId: number, title: string, exerciseCount: number }> };

export type CollectionListQueryVariables = Exact<{
  toolId: Scalars['String'];
}>;


export type CollectionListQuery = { __typename?: 'Query', tool: { __typename?: 'Tool', name: string, collections: Array<{ __typename?: 'ExerciseCollection', collectionId: number, title: string, exerciseCount: number }> } };

export type CollOverviewToolFragment = { __typename?: 'Tool', name: string, collection: { __typename?: 'ExerciseCollection', title: string, exercises: Array<{ __typename?: 'Exercise', exerciseId: number, collectionId: number, toolId: string, title: string, difficulty: { __typename?: 'Level', title: string, levelIndex: number }, topicsWithLevels: Array<{ __typename?: 'TopicWithLevel', topic: { __typename?: 'Topic', abbreviation: string, title: string }, level: { __typename?: 'Level', title: string, levelIndex: number } }>, parts: Array<{ __typename?: 'ExPart', id: string, name: string, isEntryPart: boolean, solved: boolean }> }> } };

export type CollectionOverviewQueryVariables = Exact<{
  toolId: Scalars['String'];
  collId: Scalars['Int'];
}>;


export type CollectionOverviewQuery = { __typename?: 'Query', tool: { __typename?: 'Tool', name: string, collection: { __typename?: 'ExerciseCollection', title: string, exercises: Array<{ __typename?: 'Exercise', exerciseId: number, collectionId: number, toolId: string, title: string, difficulty: { __typename?: 'Level', title: string, levelIndex: number }, topicsWithLevels: Array<{ __typename?: 'TopicWithLevel', topic: { __typename?: 'Topic', abbreviation: string, title: string }, level: { __typename?: 'Level', title: string, levelIndex: number } }>, parts: Array<{ __typename?: 'ExPart', id: string, name: string, isEntryPart: boolean, solved: boolean }> }> } } };

export type ExerciseOverviewFragment = { __typename?: 'Exercise', exerciseId: number, title: string, text: string, parts: Array<{ __typename?: 'ExPart', id: string, name: string, isEntryPart: boolean, solved: boolean }> };

export type ExOverviewToolFragment = { __typename?: 'Tool', id: string, name: string, collection: { __typename?: 'ExerciseCollection', collectionId: number, title: string, exercise?: { __typename?: 'Exercise', exerciseId: number, title: string, text: string, parts: Array<{ __typename?: 'ExPart', id: string, name: string, isEntryPart: boolean, solved: boolean }> } | null } };

export type ExerciseOverviewQueryVariables = Exact<{
  toolId: Scalars['String'];
  collectionId: Scalars['Int'];
  exerciseId: Scalars['Int'];
}>;


export type ExerciseOverviewQuery = { __typename?: 'Query', tool: { __typename?: 'Tool', id: string, name: string, collection: { __typename?: 'ExerciseCollection', collectionId: number, title: string, exercise?: { __typename?: 'Exercise', exerciseId: number, title: string, text: string, parts: Array<{ __typename?: 'ExPart', id: string, name: string, isEntryPart: boolean, solved: boolean }> } | null } } };

export type ExerciseSolveFieldsFragment = { __typename?: 'Exercise', exerciseId: number, collectionId: number, toolId: string, title: string, text: string, content: { __typename: 'FlaskExerciseContent', testConfig: { __typename?: 'FlaskTestsConfig', tests: Array<{ __typename?: 'FlaskSingleTestConfig', id: number, testName: string, description: string }> }, files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }>, flaskSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> }> } | { __typename: 'ProgrammingExerciseContent', unitTestPart: { __typename?: 'UnitTestPart', unitTestFiles: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> }, implementationPart: { __typename?: 'ImplementationPart', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> }, programmingSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> }> } | { __typename: 'RegexExerciseContent', regexSampleSolutions: Array<string> } | { __typename: 'SqlExerciseContent', hint?: string | null, sqlSampleSolutions: Array<string>, sqlDbContents: Array<{ __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: string | null, different: boolean } }> }> }> } | { __typename: 'UmlExerciseContent', toIgnore: Array<string>, mappings: Array<{ __typename?: 'KeyValueObject', key: string, value: string }>, umlSampleSolutions: Array<{ __typename?: 'UmlClassDiagram', classes: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename?: 'UmlAttribute', isAbstract: boolean, isDerived: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, memberType: string }>, methods: Array<{ __typename?: 'UmlMethod', isAbstract: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, parameters: string, memberType: string }> }>, associations: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }>, implementations: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }> }> } | { __typename: 'WebExerciseContent', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }>, siteSpec: { __typename?: 'SiteSpec', fileName: string, jsTaskCount: number, htmlTasks: Array<{ __typename?: 'HtmlTask', text: string }> }, webSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> }> } | { __typename: 'XmlExerciseContent', rootNode: string, grammarDescription: string, xmlSampleSolutions: Array<{ __typename?: 'XmlSolution', document: string, grammar: string }> } };

export type ExerciseSolveFieldsToolFragment = { __typename?: 'Tool', collection: { __typename?: 'ExerciseCollection', exercise?: { __typename?: 'Exercise', exerciseId: number, collectionId: number, toolId: string, title: string, text: string, content: { __typename: 'FlaskExerciseContent', testConfig: { __typename?: 'FlaskTestsConfig', tests: Array<{ __typename?: 'FlaskSingleTestConfig', id: number, testName: string, description: string }> }, files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }>, flaskSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> }> } | { __typename: 'ProgrammingExerciseContent', unitTestPart: { __typename?: 'UnitTestPart', unitTestFiles: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> }, implementationPart: { __typename?: 'ImplementationPart', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> }, programmingSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> }> } | { __typename: 'RegexExerciseContent', regexSampleSolutions: Array<string> } | { __typename: 'SqlExerciseContent', hint?: string | null, sqlSampleSolutions: Array<string>, sqlDbContents: Array<{ __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: string | null, different: boolean } }> }> }> } | { __typename: 'UmlExerciseContent', toIgnore: Array<string>, mappings: Array<{ __typename?: 'KeyValueObject', key: string, value: string }>, umlSampleSolutions: Array<{ __typename?: 'UmlClassDiagram', classes: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename?: 'UmlAttribute', isAbstract: boolean, isDerived: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, memberType: string }>, methods: Array<{ __typename?: 'UmlMethod', isAbstract: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, parameters: string, memberType: string }> }>, associations: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }>, implementations: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }> }> } | { __typename: 'WebExerciseContent', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }>, siteSpec: { __typename?: 'SiteSpec', fileName: string, jsTaskCount: number, htmlTasks: Array<{ __typename?: 'HtmlTask', text: string }> }, webSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> }> } | { __typename: 'XmlExerciseContent', rootNode: string, grammarDescription: string, xmlSampleSolutions: Array<{ __typename?: 'XmlSolution', document: string, grammar: string }> } } | null } };

export type ExerciseQueryVariables = Exact<{
  toolId: Scalars['String'];
  collectionId: Scalars['Int'];
  exerciseId: Scalars['Int'];
}>;


export type ExerciseQuery = { __typename?: 'Query', tool: { __typename?: 'Tool', collection: { __typename?: 'ExerciseCollection', exercise?: { __typename?: 'Exercise', exerciseId: number, collectionId: number, toolId: string, title: string, text: string, content: { __typename: 'FlaskExerciseContent', testConfig: { __typename?: 'FlaskTestsConfig', tests: Array<{ __typename?: 'FlaskSingleTestConfig', id: number, testName: string, description: string }> }, files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }>, flaskSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> }> } | { __typename: 'ProgrammingExerciseContent', unitTestPart: { __typename?: 'UnitTestPart', unitTestFiles: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> }, implementationPart: { __typename?: 'ImplementationPart', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> }, programmingSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> }> } | { __typename: 'RegexExerciseContent', regexSampleSolutions: Array<string> } | { __typename: 'SqlExerciseContent', hint?: string | null, sqlSampleSolutions: Array<string>, sqlDbContents: Array<{ __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: string | null, different: boolean } }> }> }> } | { __typename: 'UmlExerciseContent', toIgnore: Array<string>, mappings: Array<{ __typename?: 'KeyValueObject', key: string, value: string }>, umlSampleSolutions: Array<{ __typename?: 'UmlClassDiagram', classes: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename?: 'UmlAttribute', isAbstract: boolean, isDerived: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, memberType: string }>, methods: Array<{ __typename?: 'UmlMethod', isAbstract: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, parameters: string, memberType: string }> }>, associations: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }>, implementations: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }> }> } | { __typename: 'WebExerciseContent', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }>, siteSpec: { __typename?: 'SiteSpec', fileName: string, jsTaskCount: number, htmlTasks: Array<{ __typename?: 'HtmlTask', text: string }> }, webSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> }> } | { __typename: 'XmlExerciseContent', rootNode: string, grammarDescription: string, xmlSampleSolutions: Array<{ __typename?: 'XmlSolution', document: string, grammar: string }> } } | null } } };

export type FlaskExerciseContentFragment = { __typename?: 'FlaskExerciseContent', testConfig: { __typename?: 'FlaskTestsConfig', tests: Array<{ __typename?: 'FlaskSingleTestConfig', id: number, testName: string, description: string }> }, files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }>, flaskSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> }> };

export type UnitTestPartFragment = { __typename?: 'UnitTestPart', unitTestFiles: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> };

export type ProgrammingExerciseContentFragment = { __typename?: 'ProgrammingExerciseContent', unitTestPart: { __typename?: 'UnitTestPart', unitTestFiles: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> }, implementationPart: { __typename?: 'ImplementationPart', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> }, programmingSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> }> };

export type RegexExerciseContentFragment = { __typename?: 'RegexExerciseContent', regexSampleSolutions: Array<string> };

export type SqlExerciseContentFragment = { __typename?: 'SqlExerciseContent', hint?: string | null, sqlSampleSolutions: Array<string>, sqlDbContents: Array<{ __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: string | null, different: boolean } }> }> }> };

export type UmlExerciseContentFragment = { __typename?: 'UmlExerciseContent', toIgnore: Array<string>, mappings: Array<{ __typename?: 'KeyValueObject', key: string, value: string }>, umlSampleSolutions: Array<{ __typename?: 'UmlClassDiagram', classes: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename?: 'UmlAttribute', isAbstract: boolean, isDerived: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, memberType: string }>, methods: Array<{ __typename?: 'UmlMethod', isAbstract: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, parameters: string, memberType: string }> }>, associations: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }>, implementations: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }> }> };

export type UmlClassDiagramFragment = { __typename?: 'UmlClassDiagram', classes: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename?: 'UmlAttribute', isAbstract: boolean, isDerived: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, memberType: string }>, methods: Array<{ __typename?: 'UmlMethod', isAbstract: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, parameters: string, memberType: string }> }>, associations: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: string | null, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }>, implementations: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }> };

export type UmlClassFragment = { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename?: 'UmlAttribute', isAbstract: boolean, isDerived: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, memberType: string }>, methods: Array<{ __typename?: 'UmlMethod', isAbstract: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, parameters: string, memberType: string }> };

export type UmlAttributeFragment = { __typename?: 'UmlAttribute', isAbstract: boolean, isDerived: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, memberType: string };

export type UmlMethodFragment = { __typename?: 'UmlMethod', isAbstract: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, parameters: string, memberType: string };

export type WebExerciseContentFragment = { __typename?: 'WebExerciseContent', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }>, siteSpec: { __typename?: 'SiteSpec', fileName: string, jsTaskCount: number, htmlTasks: Array<{ __typename?: 'HtmlTask', text: string }> }, webSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, content: string, editable: boolean }> }> };

export type XmlExerciseContentFragment = { __typename?: 'XmlExerciseContent', rootNode: string, grammarDescription: string, xmlSampleSolutions: Array<{ __typename?: 'XmlSolution', document: string, grammar: string }> };

export type XmlSolutionFragment = { __typename?: 'XmlSolution', document: string, grammar: string };

export type RegisterMutationVariables = Exact<{
  username: Scalars['String'];
  password: Scalars['String'];
  passwordRepeat: Scalars['String'];
}>;


export type RegisterMutation = { __typename?: 'Mutation', register?: string | null };

export type LoginMutationVariables = Exact<{
  username: Scalars['String'];
  password: Scalars['String'];
}>;


export type LoginMutation = { __typename?: 'Mutation', login: string };

export type ClaimLtiWebTokenMutationVariables = Exact<{
  ltiUuid: Scalars['String'];
}>;


export type ClaimLtiWebTokenMutation = { __typename?: 'Mutation', claimLtiWebToken?: string | null };

export const FlaskTestResultFragmentDoc = gql`
    fragment FlaskTestResult on FlaskTestResult {
  testName
  successful
  stdout
  stderr
}
    `;
export const FlaskResultFragmentDoc = gql`
    fragment FlaskResult on FlaskResult {
  points
  maxPoints
  testResults {
    ...FlaskTestResult
  }
}
    ${FlaskTestResultFragmentDoc}`;
export const FlaskCorrectionResultFragmentDoc = gql`
    fragment FlaskCorrectionResult on FlaskCorrectionResult {
  result {
    ...FlaskResult
  }
  solutionId
}
    ${FlaskResultFragmentDoc}`;
export const ImplementationCorrectionResultFragmentDoc = gql`
    fragment ImplementationCorrectionResult on ImplementationCorrectionResult {
  successful
  stdout
  stderr
}
    `;
export const UnitTestCorrectionResultFragmentDoc = gql`
    fragment UnitTestCorrectionResult on UnitTestCorrectionResult {
  testId
  successful
  shouldFail
  description
  stderr
}
    `;
export const ProgrammingResultFragmentDoc = gql`
    fragment ProgrammingResult on ProgrammingResult {
  points
  maxPoints
  implementationCorrectionResult {
    ...ImplementationCorrectionResult
  }
  unitTestResults {
    ...UnitTestCorrectionResult
  }
}
    ${ImplementationCorrectionResultFragmentDoc}
${UnitTestCorrectionResultFragmentDoc}`;
export const ProgrammingCorrectionResultFragmentDoc = gql`
    fragment ProgrammingCorrectionResult on ProgrammingCorrectionResult {
  result {
    ...ProgrammingResult
  }
  solutionId
}
    ${ProgrammingResultFragmentDoc}`;
export const RegexMatchingSingleResultFragmentDoc = gql`
    fragment RegexMatchingSingleResult on RegexMatchingSingleResult {
  resultType
  matchData
}
    `;
export const RegexMatchingResultFragmentDoc = gql`
    fragment RegexMatchingResult on RegexMatchingResult {
  matchingResults {
    ...RegexMatchingSingleResult
  }
}
    ${RegexMatchingSingleResultFragmentDoc}`;
export const RegexExtractionMatchFragmentDoc = gql`
    fragment RegexExtractionMatch on RegexMatchMatch {
  matchType
  userArg
  sampleArg
}
    `;
export const ExtractionMatchingResultFragmentDoc = gql`
    fragment ExtractionMatchingResult on RegexExtractedValuesComparisonMatchingResult {
  allMatches {
    ...RegexExtractionMatch
  }
  notMatchedForUser
  notMatchedForSample
  points
  maxPoints
}
    ${RegexExtractionMatchFragmentDoc}`;
export const RegexExtractionSingleResultFragmentDoc = gql`
    fragment RegexExtractionSingleResult on RegexExtractionSingleResult {
  base
  extractionMatchingResult {
    ...ExtractionMatchingResult
  }
}
    ${ExtractionMatchingResultFragmentDoc}`;
export const RegexExtractionResultFragmentDoc = gql`
    fragment RegexExtractionResult on RegexExtractionResult {
  extractionResults {
    ...RegexExtractionSingleResult
  }
}
    ${RegexExtractionSingleResultFragmentDoc}`;
export const RegexCorrectionResultFragmentDoc = gql`
    fragment RegexCorrectionResult on RegexCorrectionResult {
  result {
    __typename
    points
    maxPoints
    ...RegexMatchingResult
    ...RegexExtractionResult
  }
  solutionId
}
    ${RegexMatchingResultFragmentDoc}
${RegexExtractionResultFragmentDoc}`;
export const SqlColumnComparisonMatchFragmentDoc = gql`
    fragment SqlColumnComparisonMatch on SqlColumnMatch {
  matchType
  userArg
  sampleArg
}
    `;
export const SqlColumnComparisonMatchingResultFragmentDoc = gql`
    fragment SqlColumnComparisonMatchingResult on SqlColumnComparisonMatchingResult {
  points
  maxPoints
  allMatches {
    ...SqlColumnComparisonMatch
  }
  notMatchedForUser
  notMatchedForSample
}
    ${SqlColumnComparisonMatchFragmentDoc}`;
export const StringMatchFragmentDoc = gql`
    fragment StringMatch on StringMatch {
  matchType
  userArg
  sampleArg
}
    `;
export const StringMatchingResultFragmentDoc = gql`
    fragment StringMatchingResult on StringMatchingResult {
  points
  maxPoints
  allMatches {
    ...StringMatch
  }
  notMatchedForUser
  notMatchedForSample
}
    ${StringMatchFragmentDoc}`;
export const SqlBinaryExpressionMatchFragmentDoc = gql`
    fragment SqlBinaryExpressionMatch on SqlBinaryExpressionMatch {
  matchType
  userArg
  sampleArg
}
    `;
export const SqlBinaryExpressionComparisonMatchingResultFragmentDoc = gql`
    fragment SqlBinaryExpressionComparisonMatchingResult on SqlBinaryExpressionComparisonMatchingResult {
  points
  maxPoints
  allMatches {
    ...SqlBinaryExpressionMatch
  }
  notMatchedForUser
  notMatchedForSample
}
    ${SqlBinaryExpressionMatchFragmentDoc}`;
export const SelectAdditionalComparisonFragmentDoc = gql`
    fragment SelectAdditionalComparison on SelectAdditionalComparisons {
  groupByComparison {
    ...StringMatchingResult
  }
  orderByComparison {
    ...StringMatchingResult
  }
  limitComparison {
    ...StringMatchingResult
  }
}
    ${StringMatchingResultFragmentDoc}`;
export const StaticComparisonFragmentDoc = gql`
    fragment StaticComparison on SqlQueriesStaticComparison {
  columnComparison {
    ...SqlColumnComparisonMatchingResult
  }
  tableComparison {
    ...StringMatchingResult
  }
  joinExpressionComparison {
    ...SqlBinaryExpressionComparisonMatchingResult
  }
  whereComparison {
    ...SqlBinaryExpressionComparisonMatchingResult
  }
  additionalComparisons {
    selectComparisons {
      ...SelectAdditionalComparison
    }
    insertComparison {
      ...StringMatchingResult
    }
  }
}
    ${SqlColumnComparisonMatchingResultFragmentDoc}
${StringMatchingResultFragmentDoc}
${SqlBinaryExpressionComparisonMatchingResultFragmentDoc}
${SelectAdditionalComparisonFragmentDoc}`;
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
  points
  maxPoints
  staticComparison {
    ...StaticComparison
  }
  executionResult {
    ...SqlExecutionResult
  }
}
    ${StaticComparisonFragmentDoc}
${SqlExecutionResultFragmentDoc}`;
export const SqlCorrectionResultFragmentDoc = gql`
    fragment SqlCorrectionResult on SqlCorrectionResult {
  result {
    ...SqlResult
  }
  solutionId
}
    ${SqlResultFragmentDoc}`;
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
  notMatchedForUser {
    ...UmlSolutionClass
  }
  notMatchedForSample {
    ...UmlSolutionClass
  }
  points
  maxPoints
}
    ${UmlClassMatchFragmentDoc}
${UmlSolutionClassFragmentDoc}`;
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
  analysisResult {
    assocTypeEqual
    correctAssocType
    multiplicitiesEqual
  }
}
    ${UmlAssociationFragmentDoc}`;
export const UmlAssociationMatchingResultFragmentDoc = gql`
    fragment UmlAssociationMatchingResult on UmlAssociationMatchingResult {
  allMatches {
    ...UmlAssociationMatch
  }
  notMatchedForUser {
    ...UmlAssociation
  }
  notMatchedForSample {
    ...UmlAssociation
  }
  points
  maxPoints
}
    ${UmlAssociationMatchFragmentDoc}
${UmlAssociationFragmentDoc}`;
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
  notMatchedForUser {
    ...UmlImplementation
  }
  notMatchedForSample {
    ...UmlImplementation
  }
  points
  maxPoints
}
    ${UmlImplementationMatchFragmentDoc}
${UmlImplementationFragmentDoc}`;
export const UmlResultFragmentDoc = gql`
    fragment UmlResult on UmlResult {
  points
  maxPoints
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
export const UmlCorrectionResultFragmentDoc = gql`
    fragment UmlCorrectionResult on UmlCorrectionResult {
  result {
    ...UmlResult
  }
  solutionId
}
    ${UmlResultFragmentDoc}`;
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
export const GradedElementSpecResultFragmentDoc = gql`
    fragment GradedElementSpecResult on GradedElementSpecResult {
  isCorrect
  elementFound
  points
  maxPoints
  textContentResult {
    ...GradedTextContentResult
  }
  attributeResults {
    ...GradedTextContentResult
  }
}
    ${GradedTextContentResultFragmentDoc}`;
export const GradedHtmlTaskResultFragmentDoc = gql`
    fragment GradedHtmlTaskResult on GradedHtmlTaskResult {
  id
  elementSpecResult {
    ...GradedElementSpecResult
  }
}
    ${GradedElementSpecResultFragmentDoc}`;
export const GradedJsActionResultFragmentDoc = gql`
    fragment GradedJsActionResult on GradedJsActionResult {
  jsAction {
    actionType
    keysToSend
    xpathQuery
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
    ...GradedElementSpecResult
  }
  gradedJsActionResult {
    ...GradedJsActionResult
  }
  gradedPostResults {
    ...GradedElementSpecResult
  }
  points
  maxPoints
}
    ${GradedElementSpecResultFragmentDoc}
${GradedJsActionResultFragmentDoc}`;
export const WebResultFragmentDoc = gql`
    fragment WebResult on WebResult {
  points
  maxPoints
  gradedHtmlTaskResults {
    ...GradedHtmlTaskResult
  }
  gradedJsTaskResults {
    ...GradedJsTaskResult
  }
}
    ${GradedHtmlTaskResultFragmentDoc}
${GradedJsTaskResultFragmentDoc}`;
export const WebCorrectionResultFragmentDoc = gql`
    fragment WebCorrectionResult on WebCorrectionResult {
  result {
    ...WebResult
  }
  solutionId
}
    ${WebResultFragmentDoc}`;
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
  analysisResult {
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
  notMatchedForUser {
    ...ElementLine
  }
  notMatchedForSample {
    ...ElementLine
  }
}
    ${XmlElementLineMatchFragmentDoc}
${ElementLineFragmentDoc}`;
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
  points
  maxPoints
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
  result {
    ...XmlResult
  }
  solutionId
}
    ${XmlResultFragmentDoc}`;
export const ToolFragmentDoc = gql`
    fragment Tool on Tool {
  id
  name
  collectionCount
  exerciseCount
}
    `;
export const TopicFragmentDoc = gql`
    fragment Topic on Topic {
  abbreviation
  title
}
    `;
export const LevelFragmentDoc = gql`
    fragment Level on Level {
  title
  levelIndex
}
    `;
export const UserProficiencyFragmentDoc = gql`
    fragment UserProficiency on UserProficiency {
  topic {
    ...Topic
  }
  points
  pointsForNextLevel
  level {
    ...Level
  }
}
    ${TopicFragmentDoc}
${LevelFragmentDoc}`;
export const ToolOverviewFragmentDoc = gql`
    fragment ToolOverview on Tool {
  name
  collectionCount
  exerciseCount
  proficiencies {
    ...UserProficiency
  }
}
    ${UserProficiencyFragmentDoc}`;
export const TopicWithLevelFragmentDoc = gql`
    fragment TopicWithLevel on TopicWithLevel {
  topic {
    ...Topic
  }
  level {
    ...Level
  }
}
    ${TopicFragmentDoc}
${LevelFragmentDoc}`;
export const PartFragmentDoc = gql`
    fragment Part on ExPart {
  id
  name
  isEntryPart
  solved
}
    `;
export const FieldsForLinkFragmentDoc = gql`
    fragment FieldsForLink on Exercise {
  exerciseId
  collectionId
  toolId
  title
  difficulty {
    ...Level
  }
  topicsWithLevels {
    ...TopicWithLevel
  }
  parts {
    ...Part
  }
}
    ${LevelFragmentDoc}
${TopicWithLevelFragmentDoc}
${PartFragmentDoc}`;
export const AllExercisesOverviewExerciseFragmentDoc = gql`
    fragment AllExercisesOverviewExercise on Exercise {
  topicsWithLevels {
    ...TopicWithLevel
  }
  ...FieldsForLink
}
    ${TopicWithLevelFragmentDoc}
${FieldsForLinkFragmentDoc}`;
export const AllExesOverviewToolFragmentDoc = gql`
    fragment AllExesOverviewTool on Tool {
  name
  allExercises {
    ...AllExercisesOverviewExercise
  }
}
    ${AllExercisesOverviewExerciseFragmentDoc}`;
export const CollectionValuesFragmentDoc = gql`
    fragment CollectionValues on ExerciseCollection {
  collectionId
  title
  exerciseCount
}
    `;
export const ToolCollectionOverviewFragmentDoc = gql`
    fragment ToolCollectionOverview on Tool {
  name
  collections {
    ...CollectionValues
  }
}
    ${CollectionValuesFragmentDoc}`;
export const CollOverviewToolFragmentDoc = gql`
    fragment CollOverviewTool on Tool {
  name
  collection(collId: $collId) {
    title
    exercises {
      ...FieldsForLink
    }
  }
}
    ${FieldsForLinkFragmentDoc}`;
export const ExerciseOverviewFragmentDoc = gql`
    fragment ExerciseOverview on Exercise {
  exerciseId
  title
  text
  parts {
    ...Part
  }
}
    ${PartFragmentDoc}`;
export const ExOverviewToolFragmentDoc = gql`
    fragment ExOverviewTool on Tool {
  id
  name
  collection(collId: $collectionId) {
    collectionId
    title
    exercise(exId: $exerciseId) {
      ...ExerciseOverview
    }
  }
}
    ${ExerciseOverviewFragmentDoc}`;
export const ExerciseFileFragmentDoc = gql`
    fragment ExerciseFile on ExerciseFile {
  name
  content
  editable
}
    `;
export const FilesSolutionFragmentDoc = gql`
    fragment FilesSolution on FilesSolution {
  files {
    ...ExerciseFile
  }
}
    ${ExerciseFileFragmentDoc}`;
export const FlaskExerciseContentFragmentDoc = gql`
    fragment FlaskExerciseContent on FlaskExerciseContent {
  testConfig {
    tests {
      id
      testName
      description
    }
  }
  files {
    ...ExerciseFile
  }
  flaskSampleSolutions: sampleSolutions {
    ...FilesSolution
  }
}
    ${ExerciseFileFragmentDoc}
${FilesSolutionFragmentDoc}`;
export const UnitTestPartFragmentDoc = gql`
    fragment UnitTestPart on UnitTestPart {
  unitTestFiles {
    ...ExerciseFile
  }
}
    ${ExerciseFileFragmentDoc}`;
export const ProgrammingExerciseContentFragmentDoc = gql`
    fragment ProgrammingExerciseContent on ProgrammingExerciseContent {
  unitTestPart {
    ...UnitTestPart
  }
  implementationPart {
    files {
      ...ExerciseFile
    }
  }
  programmingSampleSolutions: sampleSolutions {
    ...FilesSolution
  }
}
    ${UnitTestPartFragmentDoc}
${ExerciseFileFragmentDoc}
${FilesSolutionFragmentDoc}`;
export const RegexExerciseContentFragmentDoc = gql`
    fragment RegexExerciseContent on RegexExerciseContent {
  regexSampleSolutions: sampleSolutions
}
    `;
export const SqlExerciseContentFragmentDoc = gql`
    fragment SqlExerciseContent on SqlExerciseContent {
  hint
  sqlSampleSolutions: sampleSolutions
  sqlDbContents {
    ...SqlQueryResult
  }
}
    ${SqlQueryResultFragmentDoc}`;
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
export const UmlExerciseContentFragmentDoc = gql`
    fragment UmlExerciseContent on UmlExerciseContent {
  toIgnore
  mappings {
    key
    value
  }
  umlSampleSolutions: sampleSolutions {
    ...UmlClassDiagram
  }
}
    ${UmlClassDiagramFragmentDoc}`;
export const WebExerciseContentFragmentDoc = gql`
    fragment WebExerciseContent on WebExerciseContent {
  files {
    ...ExerciseFile
  }
  siteSpec {
    fileName
    htmlTasks {
      text
    }
    jsTaskCount
  }
  webSampleSolutions: sampleSolutions {
    ...FilesSolution
  }
}
    ${ExerciseFileFragmentDoc}
${FilesSolutionFragmentDoc}`;
export const XmlSolutionFragmentDoc = gql`
    fragment XmlSolution on XmlSolution {
  document
  grammar
}
    `;
export const XmlExerciseContentFragmentDoc = gql`
    fragment XmlExerciseContent on XmlExerciseContent {
  rootNode
  grammarDescription
  xmlSampleSolutions: sampleSolutions {
    ...XmlSolution
  }
}
    ${XmlSolutionFragmentDoc}`;
export const ExerciseSolveFieldsFragmentDoc = gql`
    fragment ExerciseSolveFields on Exercise {
  exerciseId
  collectionId
  toolId
  title
  text
  content {
    __typename
    ...FlaskExerciseContent
    ...ProgrammingExerciseContent
    ...ProgrammingExerciseContent
    ...RegexExerciseContent
    ...SqlExerciseContent
    ...UmlExerciseContent
    ...WebExerciseContent
    ...XmlExerciseContent
  }
}
    ${FlaskExerciseContentFragmentDoc}
${ProgrammingExerciseContentFragmentDoc}
${RegexExerciseContentFragmentDoc}
${SqlExerciseContentFragmentDoc}
${UmlExerciseContentFragmentDoc}
${WebExerciseContentFragmentDoc}
${XmlExerciseContentFragmentDoc}`;
export const ExerciseSolveFieldsToolFragmentDoc = gql`
    fragment ExerciseSolveFieldsTool on Tool {
  collection(collId: $collectionId) {
    exercise(exId: $exerciseId) {
      ...ExerciseSolveFields
    }
  }
}
    ${ExerciseSolveFieldsFragmentDoc}`;
export const FlaskCorrectionDocument = gql`
    mutation FlaskCorrection($collId: Int!, $exId: Int!, $solution: FilesSolutionInput!) {
  flaskExercise(collId: $collId, exId: $exId) {
    correct(solution: $solution) {
      ...FlaskCorrectionResult
    }
  }
}
    ${FlaskCorrectionResultFragmentDoc}`;
export type FlaskCorrectionMutationFn = Apollo.MutationFunction<FlaskCorrectionMutation, FlaskCorrectionMutationVariables>;

/**
 * __useFlaskCorrectionMutation__
 *
 * To run a mutation, you first call `useFlaskCorrectionMutation` within a React component and pass it any options that fit your needs.
 * When your component renders, `useFlaskCorrectionMutation` returns a tuple that includes:
 * - A mutate function that you can call at any time to execute the mutation
 * - An object with fields that represent the current status of the mutation's execution
 *
 * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
 *
 * @example
 * const [flaskCorrectionMutation, { data, loading, error }] = useFlaskCorrectionMutation({
 *   variables: {
 *      collId: // value for 'collId'
 *      exId: // value for 'exId'
 *      solution: // value for 'solution'
 *   },
 * });
 */
export function useFlaskCorrectionMutation(baseOptions?: Apollo.MutationHookOptions<FlaskCorrectionMutation, FlaskCorrectionMutationVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useMutation<FlaskCorrectionMutation, FlaskCorrectionMutationVariables>(FlaskCorrectionDocument, options);
      }
export type FlaskCorrectionMutationHookResult = ReturnType<typeof useFlaskCorrectionMutation>;
export type FlaskCorrectionMutationResult = Apollo.MutationResult<FlaskCorrectionMutation>;
export type FlaskCorrectionMutationOptions = Apollo.BaseMutationOptions<FlaskCorrectionMutation, FlaskCorrectionMutationVariables>;
export const ProgrammingCorrectionDocument = gql`
    mutation ProgrammingCorrection($collId: Int!, $exId: Int!, $part: ProgExPart!, $solution: FilesSolutionInput!) {
  programmingExercise(collId: $collId, exId: $exId) {
    correct(part: $part, solution: $solution) {
      ...ProgrammingCorrectionResult
    }
  }
}
    ${ProgrammingCorrectionResultFragmentDoc}`;
export type ProgrammingCorrectionMutationFn = Apollo.MutationFunction<ProgrammingCorrectionMutation, ProgrammingCorrectionMutationVariables>;

/**
 * __useProgrammingCorrectionMutation__
 *
 * To run a mutation, you first call `useProgrammingCorrectionMutation` within a React component and pass it any options that fit your needs.
 * When your component renders, `useProgrammingCorrectionMutation` returns a tuple that includes:
 * - A mutate function that you can call at any time to execute the mutation
 * - An object with fields that represent the current status of the mutation's execution
 *
 * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
 *
 * @example
 * const [programmingCorrectionMutation, { data, loading, error }] = useProgrammingCorrectionMutation({
 *   variables: {
 *      collId: // value for 'collId'
 *      exId: // value for 'exId'
 *      part: // value for 'part'
 *      solution: // value for 'solution'
 *   },
 * });
 */
export function useProgrammingCorrectionMutation(baseOptions?: Apollo.MutationHookOptions<ProgrammingCorrectionMutation, ProgrammingCorrectionMutationVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useMutation<ProgrammingCorrectionMutation, ProgrammingCorrectionMutationVariables>(ProgrammingCorrectionDocument, options);
      }
export type ProgrammingCorrectionMutationHookResult = ReturnType<typeof useProgrammingCorrectionMutation>;
export type ProgrammingCorrectionMutationResult = Apollo.MutationResult<ProgrammingCorrectionMutation>;
export type ProgrammingCorrectionMutationOptions = Apollo.BaseMutationOptions<ProgrammingCorrectionMutation, ProgrammingCorrectionMutationVariables>;
export const RegexCorrectionDocument = gql`
    mutation RegexCorrection($collectionId: Int!, $exerciseId: Int!, $solution: String!) {
  regexExercise(collId: $collectionId, exId: $exerciseId) {
    correct(solution: $solution) {
      ...RegexCorrectionResult
    }
  }
}
    ${RegexCorrectionResultFragmentDoc}`;
export type RegexCorrectionMutationFn = Apollo.MutationFunction<RegexCorrectionMutation, RegexCorrectionMutationVariables>;

/**
 * __useRegexCorrectionMutation__
 *
 * To run a mutation, you first call `useRegexCorrectionMutation` within a React component and pass it any options that fit your needs.
 * When your component renders, `useRegexCorrectionMutation` returns a tuple that includes:
 * - A mutate function that you can call at any time to execute the mutation
 * - An object with fields that represent the current status of the mutation's execution
 *
 * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
 *
 * @example
 * const [regexCorrectionMutation, { data, loading, error }] = useRegexCorrectionMutation({
 *   variables: {
 *      collectionId: // value for 'collectionId'
 *      exerciseId: // value for 'exerciseId'
 *      solution: // value for 'solution'
 *   },
 * });
 */
export function useRegexCorrectionMutation(baseOptions?: Apollo.MutationHookOptions<RegexCorrectionMutation, RegexCorrectionMutationVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useMutation<RegexCorrectionMutation, RegexCorrectionMutationVariables>(RegexCorrectionDocument, options);
      }
export type RegexCorrectionMutationHookResult = ReturnType<typeof useRegexCorrectionMutation>;
export type RegexCorrectionMutationResult = Apollo.MutationResult<RegexCorrectionMutation>;
export type RegexCorrectionMutationOptions = Apollo.BaseMutationOptions<RegexCorrectionMutation, RegexCorrectionMutationVariables>;
export const SqlCorrectionDocument = gql`
    mutation SqlCorrection($collectionId: Int!, $exerciseId: Int!, $solution: String!) {
  sqlExercise(collId: $collectionId, exId: $exerciseId) {
    correct(solution: $solution) {
      ...SqlCorrectionResult
    }
  }
}
    ${SqlCorrectionResultFragmentDoc}`;
export type SqlCorrectionMutationFn = Apollo.MutationFunction<SqlCorrectionMutation, SqlCorrectionMutationVariables>;

/**
 * __useSqlCorrectionMutation__
 *
 * To run a mutation, you first call `useSqlCorrectionMutation` within a React component and pass it any options that fit your needs.
 * When your component renders, `useSqlCorrectionMutation` returns a tuple that includes:
 * - A mutate function that you can call at any time to execute the mutation
 * - An object with fields that represent the current status of the mutation's execution
 *
 * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
 *
 * @example
 * const [sqlCorrectionMutation, { data, loading, error }] = useSqlCorrectionMutation({
 *   variables: {
 *      collectionId: // value for 'collectionId'
 *      exerciseId: // value for 'exerciseId'
 *      solution: // value for 'solution'
 *   },
 * });
 */
export function useSqlCorrectionMutation(baseOptions?: Apollo.MutationHookOptions<SqlCorrectionMutation, SqlCorrectionMutationVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useMutation<SqlCorrectionMutation, SqlCorrectionMutationVariables>(SqlCorrectionDocument, options);
      }
export type SqlCorrectionMutationHookResult = ReturnType<typeof useSqlCorrectionMutation>;
export type SqlCorrectionMutationResult = Apollo.MutationResult<SqlCorrectionMutation>;
export type SqlCorrectionMutationOptions = Apollo.BaseMutationOptions<SqlCorrectionMutation, SqlCorrectionMutationVariables>;
export const UmlCorrectionDocument = gql`
    mutation UmlCorrection($collId: Int!, $exId: Int!, $part: UmlExPart!, $solution: UmlClassDiagramInput!) {
  umlExercise(collId: $collId, exId: $exId) {
    correct(part: $part, solution: $solution) {
      ...UmlCorrectionResult
    }
  }
}
    ${UmlCorrectionResultFragmentDoc}`;
export type UmlCorrectionMutationFn = Apollo.MutationFunction<UmlCorrectionMutation, UmlCorrectionMutationVariables>;

/**
 * __useUmlCorrectionMutation__
 *
 * To run a mutation, you first call `useUmlCorrectionMutation` within a React component and pass it any options that fit your needs.
 * When your component renders, `useUmlCorrectionMutation` returns a tuple that includes:
 * - A mutate function that you can call at any time to execute the mutation
 * - An object with fields that represent the current status of the mutation's execution
 *
 * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
 *
 * @example
 * const [umlCorrectionMutation, { data, loading, error }] = useUmlCorrectionMutation({
 *   variables: {
 *      collId: // value for 'collId'
 *      exId: // value for 'exId'
 *      part: // value for 'part'
 *      solution: // value for 'solution'
 *   },
 * });
 */
export function useUmlCorrectionMutation(baseOptions?: Apollo.MutationHookOptions<UmlCorrectionMutation, UmlCorrectionMutationVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useMutation<UmlCorrectionMutation, UmlCorrectionMutationVariables>(UmlCorrectionDocument, options);
      }
export type UmlCorrectionMutationHookResult = ReturnType<typeof useUmlCorrectionMutation>;
export type UmlCorrectionMutationResult = Apollo.MutationResult<UmlCorrectionMutation>;
export type UmlCorrectionMutationOptions = Apollo.BaseMutationOptions<UmlCorrectionMutation, UmlCorrectionMutationVariables>;
export const WebCorrectionDocument = gql`
    mutation WebCorrection($collId: Int!, $exId: Int!, $part: WebExPart!, $solution: FilesSolutionInput!) {
  webExercise(collId: $collId, exId: $exId) {
    correct(part: $part, solution: $solution) {
      ...WebCorrectionResult
    }
  }
}
    ${WebCorrectionResultFragmentDoc}`;
export type WebCorrectionMutationFn = Apollo.MutationFunction<WebCorrectionMutation, WebCorrectionMutationVariables>;

/**
 * __useWebCorrectionMutation__
 *
 * To run a mutation, you first call `useWebCorrectionMutation` within a React component and pass it any options that fit your needs.
 * When your component renders, `useWebCorrectionMutation` returns a tuple that includes:
 * - A mutate function that you can call at any time to execute the mutation
 * - An object with fields that represent the current status of the mutation's execution
 *
 * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
 *
 * @example
 * const [webCorrectionMutation, { data, loading, error }] = useWebCorrectionMutation({
 *   variables: {
 *      collId: // value for 'collId'
 *      exId: // value for 'exId'
 *      part: // value for 'part'
 *      solution: // value for 'solution'
 *   },
 * });
 */
export function useWebCorrectionMutation(baseOptions?: Apollo.MutationHookOptions<WebCorrectionMutation, WebCorrectionMutationVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useMutation<WebCorrectionMutation, WebCorrectionMutationVariables>(WebCorrectionDocument, options);
      }
export type WebCorrectionMutationHookResult = ReturnType<typeof useWebCorrectionMutation>;
export type WebCorrectionMutationResult = Apollo.MutationResult<WebCorrectionMutation>;
export type WebCorrectionMutationOptions = Apollo.BaseMutationOptions<WebCorrectionMutation, WebCorrectionMutationVariables>;
export const XmlCorrectionDocument = gql`
    mutation XmlCorrection($collId: Int!, $exId: Int!, $part: XmlExPart!, $solution: XmlSolutionInput!) {
  xmlExercise(collId: $collId, exId: $exId) {
    correct(part: $part, solution: $solution) {
      ...XmlCorrectionResult
    }
  }
}
    ${XmlCorrectionResultFragmentDoc}`;
export type XmlCorrectionMutationFn = Apollo.MutationFunction<XmlCorrectionMutation, XmlCorrectionMutationVariables>;

/**
 * __useXmlCorrectionMutation__
 *
 * To run a mutation, you first call `useXmlCorrectionMutation` within a React component and pass it any options that fit your needs.
 * When your component renders, `useXmlCorrectionMutation` returns a tuple that includes:
 * - A mutate function that you can call at any time to execute the mutation
 * - An object with fields that represent the current status of the mutation's execution
 *
 * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
 *
 * @example
 * const [xmlCorrectionMutation, { data, loading, error }] = useXmlCorrectionMutation({
 *   variables: {
 *      collId: // value for 'collId'
 *      exId: // value for 'exId'
 *      part: // value for 'part'
 *      solution: // value for 'solution'
 *   },
 * });
 */
export function useXmlCorrectionMutation(baseOptions?: Apollo.MutationHookOptions<XmlCorrectionMutation, XmlCorrectionMutationVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useMutation<XmlCorrectionMutation, XmlCorrectionMutationVariables>(XmlCorrectionDocument, options);
      }
export type XmlCorrectionMutationHookResult = ReturnType<typeof useXmlCorrectionMutation>;
export type XmlCorrectionMutationResult = Apollo.MutationResult<XmlCorrectionMutation>;
export type XmlCorrectionMutationOptions = Apollo.BaseMutationOptions<XmlCorrectionMutation, XmlCorrectionMutationVariables>;
export const ToolOverviewDocument = gql`
    query ToolOverview {
  tools {
    ...Tool
  }
}
    ${ToolFragmentDoc}`;

/**
 * __useToolOverviewQuery__
 *
 * To run a query within a React component, call `useToolOverviewQuery` and pass it any options that fit your needs.
 * When your component renders, `useToolOverviewQuery` returns an object from Apollo Client that contains loading, error, and data properties
 * you can use to render your UI.
 *
 * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
 *
 * @example
 * const { data, loading, error } = useToolOverviewQuery({
 *   variables: {
 *   },
 * });
 */
export function useToolOverviewQuery(baseOptions?: Apollo.QueryHookOptions<ToolOverviewQuery, ToolOverviewQueryVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useQuery<ToolOverviewQuery, ToolOverviewQueryVariables>(ToolOverviewDocument, options);
      }
export function useToolOverviewLazyQuery(baseOptions?: Apollo.LazyQueryHookOptions<ToolOverviewQuery, ToolOverviewQueryVariables>) {
          const options = {...defaultOptions, ...baseOptions}
          return Apollo.useLazyQuery<ToolOverviewQuery, ToolOverviewQueryVariables>(ToolOverviewDocument, options);
        }
export type ToolOverviewQueryHookResult = ReturnType<typeof useToolOverviewQuery>;
export type ToolOverviewLazyQueryHookResult = ReturnType<typeof useToolOverviewLazyQuery>;
export type ToolOverviewQueryResult = Apollo.QueryResult<ToolOverviewQuery, ToolOverviewQueryVariables>;
export const CollectionToolOverviewDocument = gql`
    query CollectionToolOverview($toolId: String!) {
  tool(toolId: $toolId) {
    ...ToolOverview
  }
}
    ${ToolOverviewFragmentDoc}`;

/**
 * __useCollectionToolOverviewQuery__
 *
 * To run a query within a React component, call `useCollectionToolOverviewQuery` and pass it any options that fit your needs.
 * When your component renders, `useCollectionToolOverviewQuery` returns an object from Apollo Client that contains loading, error, and data properties
 * you can use to render your UI.
 *
 * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
 *
 * @example
 * const { data, loading, error } = useCollectionToolOverviewQuery({
 *   variables: {
 *      toolId: // value for 'toolId'
 *   },
 * });
 */
export function useCollectionToolOverviewQuery(baseOptions: Apollo.QueryHookOptions<CollectionToolOverviewQuery, CollectionToolOverviewQueryVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useQuery<CollectionToolOverviewQuery, CollectionToolOverviewQueryVariables>(CollectionToolOverviewDocument, options);
      }
export function useCollectionToolOverviewLazyQuery(baseOptions?: Apollo.LazyQueryHookOptions<CollectionToolOverviewQuery, CollectionToolOverviewQueryVariables>) {
          const options = {...defaultOptions, ...baseOptions}
          return Apollo.useLazyQuery<CollectionToolOverviewQuery, CollectionToolOverviewQueryVariables>(CollectionToolOverviewDocument, options);
        }
export type CollectionToolOverviewQueryHookResult = ReturnType<typeof useCollectionToolOverviewQuery>;
export type CollectionToolOverviewLazyQueryHookResult = ReturnType<typeof useCollectionToolOverviewLazyQuery>;
export type CollectionToolOverviewQueryResult = Apollo.QueryResult<CollectionToolOverviewQuery, CollectionToolOverviewQueryVariables>;
export const AllExercisesOverviewDocument = gql`
    query AllExercisesOverview($toolId: String!) {
  tool(toolId: $toolId) {
    ...AllExesOverviewTool
  }
}
    ${AllExesOverviewToolFragmentDoc}`;

/**
 * __useAllExercisesOverviewQuery__
 *
 * To run a query within a React component, call `useAllExercisesOverviewQuery` and pass it any options that fit your needs.
 * When your component renders, `useAllExercisesOverviewQuery` returns an object from Apollo Client that contains loading, error, and data properties
 * you can use to render your UI.
 *
 * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
 *
 * @example
 * const { data, loading, error } = useAllExercisesOverviewQuery({
 *   variables: {
 *      toolId: // value for 'toolId'
 *   },
 * });
 */
export function useAllExercisesOverviewQuery(baseOptions: Apollo.QueryHookOptions<AllExercisesOverviewQuery, AllExercisesOverviewQueryVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useQuery<AllExercisesOverviewQuery, AllExercisesOverviewQueryVariables>(AllExercisesOverviewDocument, options);
      }
export function useAllExercisesOverviewLazyQuery(baseOptions?: Apollo.LazyQueryHookOptions<AllExercisesOverviewQuery, AllExercisesOverviewQueryVariables>) {
          const options = {...defaultOptions, ...baseOptions}
          return Apollo.useLazyQuery<AllExercisesOverviewQuery, AllExercisesOverviewQueryVariables>(AllExercisesOverviewDocument, options);
        }
export type AllExercisesOverviewQueryHookResult = ReturnType<typeof useAllExercisesOverviewQuery>;
export type AllExercisesOverviewLazyQueryHookResult = ReturnType<typeof useAllExercisesOverviewLazyQuery>;
export type AllExercisesOverviewQueryResult = Apollo.QueryResult<AllExercisesOverviewQuery, AllExercisesOverviewQueryVariables>;
export const CollectionListDocument = gql`
    query CollectionList($toolId: String!) {
  tool(toolId: $toolId) {
    ...ToolCollectionOverview
  }
}
    ${ToolCollectionOverviewFragmentDoc}`;

/**
 * __useCollectionListQuery__
 *
 * To run a query within a React component, call `useCollectionListQuery` and pass it any options that fit your needs.
 * When your component renders, `useCollectionListQuery` returns an object from Apollo Client that contains loading, error, and data properties
 * you can use to render your UI.
 *
 * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
 *
 * @example
 * const { data, loading, error } = useCollectionListQuery({
 *   variables: {
 *      toolId: // value for 'toolId'
 *   },
 * });
 */
export function useCollectionListQuery(baseOptions: Apollo.QueryHookOptions<CollectionListQuery, CollectionListQueryVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useQuery<CollectionListQuery, CollectionListQueryVariables>(CollectionListDocument, options);
      }
export function useCollectionListLazyQuery(baseOptions?: Apollo.LazyQueryHookOptions<CollectionListQuery, CollectionListQueryVariables>) {
          const options = {...defaultOptions, ...baseOptions}
          return Apollo.useLazyQuery<CollectionListQuery, CollectionListQueryVariables>(CollectionListDocument, options);
        }
export type CollectionListQueryHookResult = ReturnType<typeof useCollectionListQuery>;
export type CollectionListLazyQueryHookResult = ReturnType<typeof useCollectionListLazyQuery>;
export type CollectionListQueryResult = Apollo.QueryResult<CollectionListQuery, CollectionListQueryVariables>;
export const CollectionOverviewDocument = gql`
    query CollectionOverview($toolId: String!, $collId: Int!) {
  tool(toolId: $toolId) {
    ...CollOverviewTool
  }
}
    ${CollOverviewToolFragmentDoc}`;

/**
 * __useCollectionOverviewQuery__
 *
 * To run a query within a React component, call `useCollectionOverviewQuery` and pass it any options that fit your needs.
 * When your component renders, `useCollectionOverviewQuery` returns an object from Apollo Client that contains loading, error, and data properties
 * you can use to render your UI.
 *
 * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
 *
 * @example
 * const { data, loading, error } = useCollectionOverviewQuery({
 *   variables: {
 *      toolId: // value for 'toolId'
 *      collId: // value for 'collId'
 *   },
 * });
 */
export function useCollectionOverviewQuery(baseOptions: Apollo.QueryHookOptions<CollectionOverviewQuery, CollectionOverviewQueryVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useQuery<CollectionOverviewQuery, CollectionOverviewQueryVariables>(CollectionOverviewDocument, options);
      }
export function useCollectionOverviewLazyQuery(baseOptions?: Apollo.LazyQueryHookOptions<CollectionOverviewQuery, CollectionOverviewQueryVariables>) {
          const options = {...defaultOptions, ...baseOptions}
          return Apollo.useLazyQuery<CollectionOverviewQuery, CollectionOverviewQueryVariables>(CollectionOverviewDocument, options);
        }
export type CollectionOverviewQueryHookResult = ReturnType<typeof useCollectionOverviewQuery>;
export type CollectionOverviewLazyQueryHookResult = ReturnType<typeof useCollectionOverviewLazyQuery>;
export type CollectionOverviewQueryResult = Apollo.QueryResult<CollectionOverviewQuery, CollectionOverviewQueryVariables>;
export const ExerciseOverviewDocument = gql`
    query ExerciseOverview($toolId: String!, $collectionId: Int!, $exerciseId: Int!) {
  tool(toolId: $toolId) {
    ...ExOverviewTool
  }
}
    ${ExOverviewToolFragmentDoc}`;

/**
 * __useExerciseOverviewQuery__
 *
 * To run a query within a React component, call `useExerciseOverviewQuery` and pass it any options that fit your needs.
 * When your component renders, `useExerciseOverviewQuery` returns an object from Apollo Client that contains loading, error, and data properties
 * you can use to render your UI.
 *
 * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
 *
 * @example
 * const { data, loading, error } = useExerciseOverviewQuery({
 *   variables: {
 *      toolId: // value for 'toolId'
 *      collectionId: // value for 'collectionId'
 *      exerciseId: // value for 'exerciseId'
 *   },
 * });
 */
export function useExerciseOverviewQuery(baseOptions: Apollo.QueryHookOptions<ExerciseOverviewQuery, ExerciseOverviewQueryVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useQuery<ExerciseOverviewQuery, ExerciseOverviewQueryVariables>(ExerciseOverviewDocument, options);
      }
export function useExerciseOverviewLazyQuery(baseOptions?: Apollo.LazyQueryHookOptions<ExerciseOverviewQuery, ExerciseOverviewQueryVariables>) {
          const options = {...defaultOptions, ...baseOptions}
          return Apollo.useLazyQuery<ExerciseOverviewQuery, ExerciseOverviewQueryVariables>(ExerciseOverviewDocument, options);
        }
export type ExerciseOverviewQueryHookResult = ReturnType<typeof useExerciseOverviewQuery>;
export type ExerciseOverviewLazyQueryHookResult = ReturnType<typeof useExerciseOverviewLazyQuery>;
export type ExerciseOverviewQueryResult = Apollo.QueryResult<ExerciseOverviewQuery, ExerciseOverviewQueryVariables>;
export const ExerciseDocument = gql`
    query Exercise($toolId: String!, $collectionId: Int!, $exerciseId: Int!) {
  tool(toolId: $toolId) {
    ...ExerciseSolveFieldsTool
  }
}
    ${ExerciseSolveFieldsToolFragmentDoc}`;

/**
 * __useExerciseQuery__
 *
 * To run a query within a React component, call `useExerciseQuery` and pass it any options that fit your needs.
 * When your component renders, `useExerciseQuery` returns an object from Apollo Client that contains loading, error, and data properties
 * you can use to render your UI.
 *
 * @param baseOptions options that will be passed into the query, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options;
 *
 * @example
 * const { data, loading, error } = useExerciseQuery({
 *   variables: {
 *      toolId: // value for 'toolId'
 *      collectionId: // value for 'collectionId'
 *      exerciseId: // value for 'exerciseId'
 *   },
 * });
 */
export function useExerciseQuery(baseOptions: Apollo.QueryHookOptions<ExerciseQuery, ExerciseQueryVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useQuery<ExerciseQuery, ExerciseQueryVariables>(ExerciseDocument, options);
      }
export function useExerciseLazyQuery(baseOptions?: Apollo.LazyQueryHookOptions<ExerciseQuery, ExerciseQueryVariables>) {
          const options = {...defaultOptions, ...baseOptions}
          return Apollo.useLazyQuery<ExerciseQuery, ExerciseQueryVariables>(ExerciseDocument, options);
        }
export type ExerciseQueryHookResult = ReturnType<typeof useExerciseQuery>;
export type ExerciseLazyQueryHookResult = ReturnType<typeof useExerciseLazyQuery>;
export type ExerciseQueryResult = Apollo.QueryResult<ExerciseQuery, ExerciseQueryVariables>;
export const RegisterDocument = gql`
    mutation Register($username: String!, $password: String!, $passwordRepeat: String!) {
  register(
    registerValues: {username: $username, password: $password, passwordRepeat: $passwordRepeat}
  )
}
    `;
export type RegisterMutationFn = Apollo.MutationFunction<RegisterMutation, RegisterMutationVariables>;

/**
 * __useRegisterMutation__
 *
 * To run a mutation, you first call `useRegisterMutation` within a React component and pass it any options that fit your needs.
 * When your component renders, `useRegisterMutation` returns a tuple that includes:
 * - A mutate function that you can call at any time to execute the mutation
 * - An object with fields that represent the current status of the mutation's execution
 *
 * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
 *
 * @example
 * const [registerMutation, { data, loading, error }] = useRegisterMutation({
 *   variables: {
 *      username: // value for 'username'
 *      password: // value for 'password'
 *      passwordRepeat: // value for 'passwordRepeat'
 *   },
 * });
 */
export function useRegisterMutation(baseOptions?: Apollo.MutationHookOptions<RegisterMutation, RegisterMutationVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useMutation<RegisterMutation, RegisterMutationVariables>(RegisterDocument, options);
      }
export type RegisterMutationHookResult = ReturnType<typeof useRegisterMutation>;
export type RegisterMutationResult = Apollo.MutationResult<RegisterMutation>;
export type RegisterMutationOptions = Apollo.BaseMutationOptions<RegisterMutation, RegisterMutationVariables>;
export const LoginDocument = gql`
    mutation Login($username: String!, $password: String!) {
  login(credentials: {username: $username, password: $password})
}
    `;
export type LoginMutationFn = Apollo.MutationFunction<LoginMutation, LoginMutationVariables>;

/**
 * __useLoginMutation__
 *
 * To run a mutation, you first call `useLoginMutation` within a React component and pass it any options that fit your needs.
 * When your component renders, `useLoginMutation` returns a tuple that includes:
 * - A mutate function that you can call at any time to execute the mutation
 * - An object with fields that represent the current status of the mutation's execution
 *
 * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
 *
 * @example
 * const [loginMutation, { data, loading, error }] = useLoginMutation({
 *   variables: {
 *      username: // value for 'username'
 *      password: // value for 'password'
 *   },
 * });
 */
export function useLoginMutation(baseOptions?: Apollo.MutationHookOptions<LoginMutation, LoginMutationVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useMutation<LoginMutation, LoginMutationVariables>(LoginDocument, options);
      }
export type LoginMutationHookResult = ReturnType<typeof useLoginMutation>;
export type LoginMutationResult = Apollo.MutationResult<LoginMutation>;
export type LoginMutationOptions = Apollo.BaseMutationOptions<LoginMutation, LoginMutationVariables>;
export const ClaimLtiWebTokenDocument = gql`
    mutation ClaimLtiWebToken($ltiUuid: String!) {
  claimLtiWebToken(ltiUuid: $ltiUuid)
}
    `;
export type ClaimLtiWebTokenMutationFn = Apollo.MutationFunction<ClaimLtiWebTokenMutation, ClaimLtiWebTokenMutationVariables>;

/**
 * __useClaimLtiWebTokenMutation__
 *
 * To run a mutation, you first call `useClaimLtiWebTokenMutation` within a React component and pass it any options that fit your needs.
 * When your component renders, `useClaimLtiWebTokenMutation` returns a tuple that includes:
 * - A mutate function that you can call at any time to execute the mutation
 * - An object with fields that represent the current status of the mutation's execution
 *
 * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
 *
 * @example
 * const [claimLtiWebTokenMutation, { data, loading, error }] = useClaimLtiWebTokenMutation({
 *   variables: {
 *      ltiUuid: // value for 'ltiUuid'
 *   },
 * });
 */
export function useClaimLtiWebTokenMutation(baseOptions?: Apollo.MutationHookOptions<ClaimLtiWebTokenMutation, ClaimLtiWebTokenMutationVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useMutation<ClaimLtiWebTokenMutation, ClaimLtiWebTokenMutationVariables>(ClaimLtiWebTokenDocument, options);
      }
export type ClaimLtiWebTokenMutationHookResult = ReturnType<typeof useClaimLtiWebTokenMutation>;
export type ClaimLtiWebTokenMutationResult = Apollo.MutationResult<ClaimLtiWebTokenMutation>;
export type ClaimLtiWebTokenMutationOptions = Apollo.BaseMutationOptions<ClaimLtiWebTokenMutation, ClaimLtiWebTokenMutationVariables>;