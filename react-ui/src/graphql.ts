import { gql } from '@apollo/client';
import * as Apollo from '@apollo/client';
export type Maybe<T> = T | null;
export type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
export type MakeOptional<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]?: Maybe<T[SubKey]> };
export type MakeMaybe<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]: Maybe<T[SubKey]> };
const defaultOptions =  {}
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: string;
  String: string;
  Boolean: boolean;
  Int: number;
  Float: number;
  /** The `Long` scalar type represents non-fractional signed whole numeric values. Long can represent values between -(2^63) and 2^63 - 1. */
  Long: any;
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
  TruePositive = 'TruePositive',
  FalsePositive = 'FalsePositive',
  FalseNegative = 'FalseNegative',
  TrueNegative = 'TrueNegative'
}

export type CollectionTool = {
  __typename?: 'CollectionTool';
  allExercises: Array<Exercise>;
  collection?: Maybe<ExerciseCollection>;
  collectionCount: Scalars['Long'];
  collections: Array<ExerciseCollection>;
  exerciseCount: Scalars['Long'];
  id: Scalars['String'];
  isBeta: Scalars['Boolean'];
  lesson?: Maybe<Lesson>;
  lessonCount: Scalars['Long'];
  lessons: Array<Lesson>;
  name: Scalars['String'];
  proficiencies: Array<UserProficiency>;
};


export type CollectionToolCollectionArgs = {
  collId: Scalars['Int'];
};


export type CollectionToolLessonArgs = {
  lessonId: Scalars['Int'];
};

export type DtdParseException = {
  __typename?: 'DTDParseException';
  msg: Scalars['String'];
  parsedLine: Scalars['String'];
};

export type EbnfCorrectionResult = {
  __typename?: 'EbnfCorrectionResult';
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: EbnfResult;
  resultSaved: Scalars['Boolean'];
  solutionSaved: Scalars['Boolean'];
};

export enum EbnfExPart {
  GrammarCreation = 'GrammarCreation'
}

export type EbnfExerciseContent = {
  __typename?: 'EbnfExerciseContent';
  predefinedTerminals?: Maybe<Array<Scalars['String']>>;
  sampleSolutions: Array<EbnfGrammar>;
};

export type EbnfExerciseMutations = {
  __typename?: 'EbnfExerciseMutations';
  correct: EbnfCorrectionResult;
};


export type EbnfExerciseMutationsCorrectArgs = {
  part: EbnfExPart;
  solution: EbnfGrammarInput;
};

export type EbnfGrammar = {
  __typename?: 'EbnfGrammar';
  startSymbol: Scalars['String'];
  rules: Scalars['String'];
};

export type EbnfGrammarInput = {
  startSymbol: Scalars['String'];
  rules: Array<EbnfRuleInput>;
};

export type EbnfResult = {
  __typename?: 'EbnfResult';
  x: Scalars['String'];
};

export type EbnfRuleInput = {
  variable: Scalars['String'];
  rule: Scalars['String'];
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
  solved?: Maybe<Scalars['Boolean']>;
};

export type Exercise = {
  __typename?: 'Exercise';
  authors: Array<Scalars['String']>;
  collectionId: Scalars['Int'];
  content: ExerciseContentUnionType;
  difficulty: Scalars['Int'];
  exerciseId: Scalars['Int'];
  parts: Array<ExPart>;
  text: Scalars['String'];
  title: Scalars['String'];
  toolId: Scalars['String'];
  topicsWithLevels: Array<TopicWithLevel>;
};

export type ExerciseCollection = {
  __typename?: 'ExerciseCollection';
  authors: Array<Scalars['String']>;
  collectionId: Scalars['Int'];
  exercise?: Maybe<Exercise>;
  exerciseCount: Scalars['Long'];
  exercises: Array<Exercise>;
  title: Scalars['String'];
  toolId: Scalars['String'];
};


export type ExerciseCollectionExerciseArgs = {
  exId: Scalars['Int'];
};

export type ExerciseContentUnionType = EbnfExerciseContent | FlaskExerciseContent | ProgrammingExerciseContent | RegexExerciseContent | SqlExerciseContent | UmlExerciseContent | WebExerciseContent | XmlExerciseContent;

export type ExerciseFile = {
  __typename?: 'ExerciseFile';
  content: Scalars['String'];
  editable: Scalars['Boolean'];
  fileType: Scalars['String'];
  name: Scalars['String'];
};

export type ExerciseFileInput = {
  content: Scalars['String'];
  editable: Scalars['Boolean'];
  fileType: Scalars['String'];
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
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: FlaskResult;
  resultSaved: Scalars['Boolean'];
  solutionSaved: Scalars['Boolean'];
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
  part: FlaskExercisePart;
  solution: FilesSolutionInput;
};

export enum FlaskExercisePart {
  FlaskSingleExPart = 'FlaskSingleExPart'
}

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

export type GradedHtmlTaskResult = {
  __typename?: 'GradedHtmlTaskResult';
  attributeResults: Array<GradedTextResult>;
  elementFound: Scalars['Boolean'];
  id: Scalars['Int'];
  isSuccessful: Scalars['Boolean'];
  maxPoints: Scalars['Float'];
  points: Scalars['Float'];
  success: SuccessType;
  textContentResult?: Maybe<GradedTextResult>;
};

export type GradedJsActionResult = {
  __typename?: 'GradedJsActionResult';
  actionPerformed: Scalars['Boolean'];
  jsAction: JsAction;
  maxPoints: Scalars['Float'];
  points: Scalars['Float'];
};

export type GradedJsHtmlElementSpecResult = {
  __typename?: 'GradedJsHtmlElementSpecResult';
  attributeResults: Array<GradedTextResult>;
  elementFound: Scalars['Boolean'];
  id: Scalars['Int'];
  isSuccessful: Scalars['Boolean'];
  maxPoints: Scalars['Float'];
  points: Scalars['Float'];
  success: SuccessType;
  textContentResult?: Maybe<GradedTextResult>;
};

export type GradedJsTaskResult = {
  __typename?: 'GradedJsTaskResult';
  gradedJsActionResult: GradedJsActionResult;
  gradedPostResults: Array<GradedJsHtmlElementSpecResult>;
  gradedPreResults: Array<GradedJsHtmlElementSpecResult>;
  id: Scalars['Int'];
  maxPoints: Scalars['Float'];
  points: Scalars['Float'];
  success: SuccessType;
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

export type Lesson = {
  __typename?: 'Lesson';
  content?: Maybe<LessonContent>;
  contentCount: Scalars['Long'];
  contents: Array<LessonContent>;
  description: Scalars['String'];
  lessonId: Scalars['Int'];
  title: Scalars['String'];
  toolId: Scalars['String'];
  video?: Maybe<Scalars['String']>;
};


export type LessonContentArgs = {
  lessonId: Scalars['Int'];
};

export type LessonContent = {
  contentId: Scalars['Int'];
  lessonId: Scalars['Int'];
  toolId: Scalars['String'];
};

export type LessonMultipleChoiceQuestion = {
  __typename?: 'LessonMultipleChoiceQuestion';
  answers: Array<LessonMultipleChoiceQuestionAnswer>;
  id: Scalars['Int'];
  questionText: Scalars['String'];
};

export type LessonMultipleChoiceQuestionAnswer = {
  __typename?: 'LessonMultipleChoiceQuestionAnswer';
  answer: Scalars['String'];
  id: Scalars['Int'];
  isCorrect: Scalars['Boolean'];
};

export type LessonMultipleChoiceQuestionsContent = LessonContent & {
  __typename?: 'LessonMultipleChoiceQuestionsContent';
  contentId: Scalars['Int'];
  lessonId: Scalars['Int'];
  questions: Array<LessonMultipleChoiceQuestion>;
  toolId: Scalars['String'];
};

export type LessonTextContent = LessonContent & {
  __typename?: 'LessonTextContent';
  content: Scalars['String'];
  contentId: Scalars['Int'];
  lessonId: Scalars['Int'];
  toolId: Scalars['String'];
};

export type Level = {
  __typename?: 'Level';
  levelIndex: Scalars['Int'];
  title: Scalars['String'];
};

export type LoggedInUser = {
  __typename?: 'LoggedInUser';
  isAdmin: Scalars['Boolean'];
  username: Scalars['String'];
};

export type LoggedInUserWithToken = {
  __typename?: 'LoggedInUserWithToken';
  jwt: Scalars['String'];
  loggedInUser: LoggedInUser;
};

export enum MatchType {
  SuccessfulMatch = 'SUCCESSFUL_MATCH',
  PartialMatch = 'PARTIAL_MATCH',
  UnsuccessfulMatch = 'UNSUCCESSFUL_MATCH'
}

export type Mutation = {
  __typename?: 'Mutation';
  ebnfExercise?: Maybe<EbnfExerciseMutations>;
  flaskExercise?: Maybe<FlaskExerciseMutations>;
  login?: Maybe<LoggedInUserWithToken>;
  programmingExercise?: Maybe<ProgrammingExerciseMutations>;
  regexExercise?: Maybe<RegexExerciseMutations>;
  register?: Maybe<Scalars['String']>;
  sqlExercise?: Maybe<SqlExerciseMutations>;
  umlExercise?: Maybe<UmlExerciseMutations>;
  webExercise?: Maybe<WebExerciseMutations>;
  xmlExercise?: Maybe<XmlExerciseMutations>;
};


export type MutationEbnfExerciseArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
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
  ActivityDiagram = 'ActivityDiagram',
  Implementation = 'Implementation',
  TestCreation = 'TestCreation'
}

export type ProgrammingCorrectionResult = {
  __typename?: 'ProgrammingCorrectionResult';
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: ProgrammingResult;
  resultSaved: Scalars['Boolean'];
  solutionSaved: Scalars['Boolean'];
};

export type ProgrammingExerciseContent = {
  __typename?: 'ProgrammingExerciseContent';
  filename: Scalars['String'];
  implementationPart: ImplementationPart;
  part?: Maybe<ProgExPart>;
  sampleSolutions: Array<FilesSolution>;
  unitTestPart: UnitTestPart;
};


export type ProgrammingExerciseContentPartArgs = {
  partId: Scalars['String'];
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
  tool?: Maybe<CollectionTool>;
  tools: Array<CollectionTool>;
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
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: RegexAbstractResult;
  resultSaved: Scalars['Boolean'];
  solutionSaved: Scalars['Boolean'];
};

export enum RegexCorrectionType {
  Matching = 'MATCHING',
  Extraction = 'EXTRACTION'
}

export enum RegexExPart {
  RegexSingleExPart = 'RegexSingleExPart'
}

export type RegexExerciseContent = {
  __typename?: 'RegexExerciseContent';
  correctionType: RegexCorrectionType;
  extractionTestData: Array<RegexExtractionTestData>;
  matchTestData: Array<RegexMatchTestData>;
  maxPoints: Scalars['Int'];
  part?: Maybe<RegexExPart>;
  sampleSolutions: Array<Scalars['String']>;
};


export type RegexExerciseContentPartArgs = {
  partId: Scalars['String'];
};

export type RegexExerciseMutations = {
  __typename?: 'RegexExerciseMutations';
  correct: RegexCorrectionResult;
};


export type RegexExerciseMutationsCorrectArgs = {
  part: RegexExPart;
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
  firstPassword: Scalars['String'];
  secondPassword: Scalars['String'];
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
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: SqlResult;
  resultSaved: Scalars['Boolean'];
  solutionSaved: Scalars['Boolean'];
};

export enum SqlExPart {
  SqlSingleExPart = 'SqlSingleExPart'
}

export type SqlExecutionResult = {
  __typename?: 'SqlExecutionResult';
  sampleResult?: Maybe<SqlQueryResult>;
  userResult?: Maybe<SqlQueryResult>;
};

export type SqlExerciseContent = {
  __typename?: 'SqlExerciseContent';
  exerciseType: SqlExerciseType;
  hint?: Maybe<Scalars['String']>;
  part?: Maybe<SqlExPart>;
  sampleSolutions: Array<Scalars['String']>;
  schemaName: Scalars['String'];
  sqlDbContents: Array<SqlQueryResult>;
};


export type SqlExerciseContentPartArgs = {
  partId: Scalars['String'];
};

export type SqlExerciseMutations = {
  __typename?: 'SqlExerciseMutations';
  correct: SqlCorrectionResult;
};


export type SqlExerciseMutationsCorrectArgs = {
  part: SqlExPart;
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

export enum SuccessType {
  Error = 'ERROR',
  None = 'NONE',
  Partially = 'PARTIALLY'
}

export type Topic = {
  __typename?: 'Topic';
  abbreviation: Scalars['String'];
  maxLevel: Level;
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
  assocName?: Maybe<Scalars['String']>;
  assocType?: Maybe<UmlAssociationType>;
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
  isAbstract?: Maybe<Scalars['Boolean']>;
  isDerived?: Maybe<Scalars['Boolean']>;
  isStatic?: Maybe<Scalars['Boolean']>;
  memberName: Scalars['String'];
  memberType: Scalars['String'];
  visibility?: Maybe<UmlVisibility>;
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
  attributes?: Maybe<Array<UmlAttributeInput>>;
  classType?: Maybe<UmlClassType>;
  methods?: Maybe<Array<UmlMethodInput>>;
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
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: UmlResult;
  resultSaved: Scalars['Boolean'];
  solutionSaved: Scalars['Boolean'];
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
  part?: Maybe<UmlExPart>;
  sampleSolutions: Array<UmlClassDiagram>;
  toIgnore: Array<Scalars['String']>;
};


export type UmlExerciseContentPartArgs = {
  partId: Scalars['String'];
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
  isAbstract?: Maybe<Scalars['Boolean']>;
  isStatic?: Maybe<Scalars['Boolean']>;
  memberName: Scalars['String'];
  memberType: Scalars['String'];
  parameters: Scalars['String'];
  visibility?: Maybe<UmlVisibility>;
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
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: WebResult;
  resultSaved: Scalars['Boolean'];
  solutionSaved: Scalars['Boolean'];
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
  part?: Maybe<WebExPart>;
  sampleSolutions: Array<FilesSolution>;
  siteSpec: SiteSpec;
};


export type WebExerciseContentPartArgs = {
  partId: Scalars['String'];
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
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: XmlResult;
  resultSaved: Scalars['Boolean'];
  solutionSaved: Scalars['Boolean'];
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
  success: SuccessType;
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
  part?: Maybe<XmlExPart>;
  rootNode: Scalars['String'];
  sampleSolutions: Array<XmlSolution>;
};


export type XmlExerciseContentPartArgs = {
  partId: Scalars['String'];
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
  successType: SuccessType;
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

export type EbnfCorrectionMutationVariables = Exact<{
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  solution: EbnfGrammarInput;
}>;


export type EbnfCorrectionMutation = { __typename?: 'Mutation', ebnfExercise?: Maybe<{ __typename?: 'EbnfExerciseMutations', correct: { __typename?: 'EbnfCorrectionResult', solutionSaved: boolean, proficienciesUpdated?: Maybe<boolean>, resultSaved: boolean, result: { __typename?: 'EbnfResult', x: string } } }> };

export type FlaskCorrectionMutationVariables = Exact<{
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: FlaskExercisePart;
  solution: FilesSolutionInput;
}>;


export type FlaskCorrectionMutation = { __typename?: 'Mutation', flaskExercise?: Maybe<{ __typename?: 'FlaskExerciseMutations', correct: { __typename?: 'FlaskCorrectionResult', solutionSaved: boolean, resultSaved: boolean, proficienciesUpdated?: Maybe<boolean>, result: { __typename?: 'FlaskResult', points: number, maxPoints: number, testResults: Array<{ __typename?: 'FlaskTestResult', testName: string, successful: boolean, stdout: Array<string>, stderr: Array<string> }> } } }> };

export type FlaskCorrectionResultFragment = { __typename?: 'FlaskCorrectionResult', solutionSaved: boolean, resultSaved: boolean, proficienciesUpdated?: Maybe<boolean>, result: { __typename?: 'FlaskResult', points: number, maxPoints: number, testResults: Array<{ __typename?: 'FlaskTestResult', testName: string, successful: boolean, stdout: Array<string>, stderr: Array<string> }> } };

export type FlaskResultFragment = { __typename?: 'FlaskResult', points: number, maxPoints: number, testResults: Array<{ __typename?: 'FlaskTestResult', testName: string, successful: boolean, stdout: Array<string>, stderr: Array<string> }> };

export type FlaskTestResultFragment = { __typename?: 'FlaskTestResult', testName: string, successful: boolean, stdout: Array<string>, stderr: Array<string> };

export type ProgrammingCorrectionMutationVariables = Exact<{
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: ProgExPart;
  solution: FilesSolutionInput;
}>;


export type ProgrammingCorrectionMutation = { __typename?: 'Mutation', programmingExercise?: Maybe<{ __typename?: 'ProgrammingExerciseMutations', correct: { __typename: 'ProgrammingCorrectionResult', solutionSaved: boolean, resultSaved: boolean, proficienciesUpdated?: Maybe<boolean>, result: { __typename?: 'ProgrammingResult', points: number, maxPoints: number, implementationCorrectionResult?: Maybe<{ __typename?: 'ImplementationCorrectionResult', successful: boolean, stdout: Array<string>, stderr: Array<string> }>, unitTestResults: Array<{ __typename?: 'UnitTestCorrectionResult', testId: number, successful: boolean, shouldFail: boolean, description: string, stderr: Array<string> }> } } }> };

export type ProgrammingCorrectionResultFragment = { __typename: 'ProgrammingCorrectionResult', solutionSaved: boolean, resultSaved: boolean, proficienciesUpdated?: Maybe<boolean>, result: { __typename?: 'ProgrammingResult', points: number, maxPoints: number, implementationCorrectionResult?: Maybe<{ __typename?: 'ImplementationCorrectionResult', successful: boolean, stdout: Array<string>, stderr: Array<string> }>, unitTestResults: Array<{ __typename?: 'UnitTestCorrectionResult', testId: number, successful: boolean, shouldFail: boolean, description: string, stderr: Array<string> }> } };

export type ProgrammingResultFragment = { __typename?: 'ProgrammingResult', points: number, maxPoints: number, implementationCorrectionResult?: Maybe<{ __typename?: 'ImplementationCorrectionResult', successful: boolean, stdout: Array<string>, stderr: Array<string> }>, unitTestResults: Array<{ __typename?: 'UnitTestCorrectionResult', testId: number, successful: boolean, shouldFail: boolean, description: string, stderr: Array<string> }> };

export type ImplementationCorrectionResultFragment = { __typename?: 'ImplementationCorrectionResult', successful: boolean, stdout: Array<string>, stderr: Array<string> };

export type UnitTestCorrectionResultFragment = { __typename?: 'UnitTestCorrectionResult', testId: number, successful: boolean, shouldFail: boolean, description: string, stderr: Array<string> };

export type RegexCorrectionMutationVariables = Exact<{
  collectionId: Scalars['Int'];
  exerciseId: Scalars['Int'];
  part: RegexExPart;
  solution: Scalars['String'];
}>;


export type RegexCorrectionMutation = { __typename?: 'Mutation', regexExercise?: Maybe<{ __typename?: 'RegexExerciseMutations', correct: { __typename?: 'RegexCorrectionResult', solutionSaved: boolean, resultSaved: boolean, proficienciesUpdated?: Maybe<boolean>, result: { __typename: 'RegexExtractionResult', points: number, maxPoints: number, extractionResults: Array<{ __typename?: 'RegexExtractionSingleResult', base: string, extractionMatchingResult: { __typename?: 'RegexExtractedValuesComparisonMatchingResult', notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, points: number, maxPoints: number, allMatches: Array<{ __typename?: 'RegexMatchMatch', matchType: MatchType, userArg: string, sampleArg: string }> } }> } | { __typename: 'RegexMatchingResult', points: number, maxPoints: number, matchingResults: Array<{ __typename?: 'RegexMatchingSingleResult', resultType: BinaryClassificationResultType, matchData: string }> } } }> };

export type RegexCorrectionResultFragment = { __typename?: 'RegexCorrectionResult', solutionSaved: boolean, resultSaved: boolean, proficienciesUpdated?: Maybe<boolean>, result: { __typename: 'RegexExtractionResult', points: number, maxPoints: number, extractionResults: Array<{ __typename?: 'RegexExtractionSingleResult', base: string, extractionMatchingResult: { __typename?: 'RegexExtractedValuesComparisonMatchingResult', notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, points: number, maxPoints: number, allMatches: Array<{ __typename?: 'RegexMatchMatch', matchType: MatchType, userArg: string, sampleArg: string }> } }> } | { __typename: 'RegexMatchingResult', points: number, maxPoints: number, matchingResults: Array<{ __typename?: 'RegexMatchingSingleResult', resultType: BinaryClassificationResultType, matchData: string }> } };

export type RegexMatchingSingleResultFragment = { __typename?: 'RegexMatchingSingleResult', resultType: BinaryClassificationResultType, matchData: string };

export type RegexMatchingResultFragment = { __typename?: 'RegexMatchingResult', matchingResults: Array<{ __typename?: 'RegexMatchingSingleResult', resultType: BinaryClassificationResultType, matchData: string }> };

export type RegexExtractionMatchFragment = { __typename?: 'RegexMatchMatch', matchType: MatchType, userArg: string, sampleArg: string };

export type ExtractionMatchingResultFragment = { __typename?: 'RegexExtractedValuesComparisonMatchingResult', notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, points: number, maxPoints: number, allMatches: Array<{ __typename?: 'RegexMatchMatch', matchType: MatchType, userArg: string, sampleArg: string }> };

export type RegexExtractionSingleResultFragment = { __typename?: 'RegexExtractionSingleResult', base: string, extractionMatchingResult: { __typename?: 'RegexExtractedValuesComparisonMatchingResult', notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, points: number, maxPoints: number, allMatches: Array<{ __typename?: 'RegexMatchMatch', matchType: MatchType, userArg: string, sampleArg: string }> } };

export type RegexExtractionResultFragment = { __typename?: 'RegexExtractionResult', extractionResults: Array<{ __typename?: 'RegexExtractionSingleResult', base: string, extractionMatchingResult: { __typename?: 'RegexExtractedValuesComparisonMatchingResult', notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, points: number, maxPoints: number, allMatches: Array<{ __typename?: 'RegexMatchMatch', matchType: MatchType, userArg: string, sampleArg: string }> } }> };

export type SqlCorrectionMutationVariables = Exact<{
  collectionId: Scalars['Int'];
  exerciseId: Scalars['Int'];
  part: SqlExPart;
  solution: Scalars['String'];
}>;


export type SqlCorrectionMutation = { __typename?: 'Mutation', sqlExercise?: Maybe<{ __typename?: 'SqlExerciseMutations', correct: { __typename?: 'SqlCorrectionResult', solutionSaved: boolean, resultSaved: boolean, proficienciesUpdated?: Maybe<boolean>, result: { __typename?: 'SqlResult', points: number, maxPoints: number, staticComparison: { __typename?: 'SqlQueriesStaticComparison', columnComparison: { __typename?: 'SqlColumnComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlColumnMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, tableComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, joinExpressionComparison: { __typename?: 'SqlBinaryExpressionComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlBinaryExpressionMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, whereComparison: { __typename?: 'SqlBinaryExpressionComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlBinaryExpressionMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, additionalComparisons: { __typename?: 'AdditionalComparison', selectComparisons?: Maybe<{ __typename?: 'SelectAdditionalComparisons', groupByComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, orderByComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, limitComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> } }>, insertComparison?: Maybe<{ __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }> } }, executionResult: { __typename?: 'SqlExecutionResult', userResult?: Maybe<{ __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: Maybe<string>, different: boolean } }> }> }>, sampleResult?: Maybe<{ __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: Maybe<string>, different: boolean } }> }> }> } } } }> };

export type SqlCorrectionResultFragment = { __typename?: 'SqlCorrectionResult', solutionSaved: boolean, resultSaved: boolean, proficienciesUpdated?: Maybe<boolean>, result: { __typename?: 'SqlResult', points: number, maxPoints: number, staticComparison: { __typename?: 'SqlQueriesStaticComparison', columnComparison: { __typename?: 'SqlColumnComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlColumnMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, tableComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, joinExpressionComparison: { __typename?: 'SqlBinaryExpressionComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlBinaryExpressionMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, whereComparison: { __typename?: 'SqlBinaryExpressionComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlBinaryExpressionMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, additionalComparisons: { __typename?: 'AdditionalComparison', selectComparisons?: Maybe<{ __typename?: 'SelectAdditionalComparisons', groupByComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, orderByComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, limitComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> } }>, insertComparison?: Maybe<{ __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }> } }, executionResult: { __typename?: 'SqlExecutionResult', userResult?: Maybe<{ __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: Maybe<string>, different: boolean } }> }> }>, sampleResult?: Maybe<{ __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: Maybe<string>, different: boolean } }> }> }> } } };

export type SelectAdditionalComparisonFragment = { __typename?: 'SelectAdditionalComparisons', groupByComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, orderByComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, limitComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> } };

export type StaticComparisonFragment = { __typename?: 'SqlQueriesStaticComparison', columnComparison: { __typename?: 'SqlColumnComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlColumnMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, tableComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, joinExpressionComparison: { __typename?: 'SqlBinaryExpressionComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlBinaryExpressionMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, whereComparison: { __typename?: 'SqlBinaryExpressionComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlBinaryExpressionMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, additionalComparisons: { __typename?: 'AdditionalComparison', selectComparisons?: Maybe<{ __typename?: 'SelectAdditionalComparisons', groupByComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, orderByComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, limitComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> } }>, insertComparison?: Maybe<{ __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }> } };

export type SqlResultFragment = { __typename?: 'SqlResult', points: number, maxPoints: number, staticComparison: { __typename?: 'SqlQueriesStaticComparison', columnComparison: { __typename?: 'SqlColumnComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlColumnMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, tableComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, joinExpressionComparison: { __typename?: 'SqlBinaryExpressionComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlBinaryExpressionMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, whereComparison: { __typename?: 'SqlBinaryExpressionComparisonMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'SqlBinaryExpressionMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, additionalComparisons: { __typename?: 'AdditionalComparison', selectComparisons?: Maybe<{ __typename?: 'SelectAdditionalComparisons', groupByComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, orderByComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }, limitComparison: { __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> } }>, insertComparison?: Maybe<{ __typename?: 'StringMatchingResult', points: number, maxPoints: number, notMatchedForUser: Array<string>, notMatchedForSample: Array<string>, allMatches: Array<{ __typename?: 'StringMatch', matchType: MatchType, userArg: string, sampleArg: string }> }> } }, executionResult: { __typename?: 'SqlExecutionResult', userResult?: Maybe<{ __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: Maybe<string>, different: boolean } }> }> }>, sampleResult?: Maybe<{ __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: Maybe<string>, different: boolean } }> }> }> } };

export type SqlExecutionResultFragment = { __typename?: 'SqlExecutionResult', userResult?: Maybe<{ __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: Maybe<string>, different: boolean } }> }> }>, sampleResult?: Maybe<{ __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: Maybe<string>, different: boolean } }> }> }> };

export type SqlQueryResultFragment = { __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: Maybe<string>, different: boolean } }> }> };

export type SqlRowFragment = { __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: Maybe<string>, different: boolean } }> };

export type SqlCellFragment = { __typename?: 'SqlCell', colName: string, content?: Maybe<string>, different: boolean };

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


export type UmlCorrectionMutation = { __typename?: 'Mutation', umlExercise?: Maybe<{ __typename?: 'UmlExerciseMutations', correct: { __typename?: 'UmlCorrectionResult', solutionSaved: boolean, resultSaved: boolean, proficienciesUpdated?: Maybe<boolean>, result: { __typename?: 'UmlResult', points: number, maxPoints: number, classResult?: Maybe<{ __typename?: 'UmlClassMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlClassMatch', matchType: MatchType, userArg: { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }, sampleArg: { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }, analysisResult: { __typename: 'UmlClassMatchAnalysisResult' } }>, notMatchedForUser: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }>, notMatchedForSample: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }> }>, assocResult?: Maybe<{ __typename?: 'UmlAssociationMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlAssociationMatch', matchType: MatchType, userArg: { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }, sampleArg: { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }, analysisResult: { __typename?: 'UmlAssociationAnalysisResult', assocTypeEqual: boolean, correctAssocType: UmlAssociationType, multiplicitiesEqual: boolean } }>, notMatchedForUser: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }>, notMatchedForSample: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }> }>, implResult?: Maybe<{ __typename?: 'UmlImplementationMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlImplementationMatch', matchType: MatchType, userArg: { __typename?: 'UmlImplementation', subClass: string, superClass: string }, sampleArg: { __typename?: 'UmlImplementation', subClass: string, superClass: string } }>, notMatchedForUser: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }>, notMatchedForSample: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }> }> } } }> };

export type UmlCorrectionResultFragment = { __typename?: 'UmlCorrectionResult', solutionSaved: boolean, resultSaved: boolean, proficienciesUpdated?: Maybe<boolean>, result: { __typename?: 'UmlResult', points: number, maxPoints: number, classResult?: Maybe<{ __typename?: 'UmlClassMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlClassMatch', matchType: MatchType, userArg: { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }, sampleArg: { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }, analysisResult: { __typename: 'UmlClassMatchAnalysisResult' } }>, notMatchedForUser: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }>, notMatchedForSample: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }> }>, assocResult?: Maybe<{ __typename?: 'UmlAssociationMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlAssociationMatch', matchType: MatchType, userArg: { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }, sampleArg: { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }, analysisResult: { __typename?: 'UmlAssociationAnalysisResult', assocTypeEqual: boolean, correctAssocType: UmlAssociationType, multiplicitiesEqual: boolean } }>, notMatchedForUser: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }>, notMatchedForSample: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }> }>, implResult?: Maybe<{ __typename?: 'UmlImplementationMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlImplementationMatch', matchType: MatchType, userArg: { __typename?: 'UmlImplementation', subClass: string, superClass: string }, sampleArg: { __typename?: 'UmlImplementation', subClass: string, superClass: string } }>, notMatchedForUser: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }>, notMatchedForSample: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }> }> } };

export type UmlResultFragment = { __typename?: 'UmlResult', points: number, maxPoints: number, classResult?: Maybe<{ __typename?: 'UmlClassMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlClassMatch', matchType: MatchType, userArg: { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }, sampleArg: { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }, analysisResult: { __typename: 'UmlClassMatchAnalysisResult' } }>, notMatchedForUser: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }>, notMatchedForSample: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }> }>, assocResult?: Maybe<{ __typename?: 'UmlAssociationMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlAssociationMatch', matchType: MatchType, userArg: { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }, sampleArg: { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }, analysisResult: { __typename?: 'UmlAssociationAnalysisResult', assocTypeEqual: boolean, correctAssocType: UmlAssociationType, multiplicitiesEqual: boolean } }>, notMatchedForUser: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }>, notMatchedForSample: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }> }>, implResult?: Maybe<{ __typename?: 'UmlImplementationMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlImplementationMatch', matchType: MatchType, userArg: { __typename?: 'UmlImplementation', subClass: string, superClass: string }, sampleArg: { __typename?: 'UmlImplementation', subClass: string, superClass: string } }>, notMatchedForUser: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }>, notMatchedForSample: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }> }> };

export type UmlClassMatchingResultFragment = { __typename?: 'UmlClassMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlClassMatch', matchType: MatchType, userArg: { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }, sampleArg: { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }, analysisResult: { __typename: 'UmlClassMatchAnalysisResult' } }>, notMatchedForUser: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }>, notMatchedForSample: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }> };

export type UmlClassMatchFragment = { __typename?: 'UmlClassMatch', matchType: MatchType, userArg: { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }, sampleArg: { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }, analysisResult: { __typename: 'UmlClassMatchAnalysisResult' } };

export type UmlSolutionClassFragment = { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> };

export type UmlAssociationMatchingResultFragment = { __typename?: 'UmlAssociationMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlAssociationMatch', matchType: MatchType, userArg: { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }, sampleArg: { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }, analysisResult: { __typename?: 'UmlAssociationAnalysisResult', assocTypeEqual: boolean, correctAssocType: UmlAssociationType, multiplicitiesEqual: boolean } }>, notMatchedForUser: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }>, notMatchedForSample: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }> };

export type UmlAssociationMatchFragment = { __typename?: 'UmlAssociationMatch', matchType: MatchType, userArg: { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }, sampleArg: { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }, analysisResult: { __typename?: 'UmlAssociationAnalysisResult', assocTypeEqual: boolean, correctAssocType: UmlAssociationType, multiplicitiesEqual: boolean } };

export type UmlAssociationFragment = { __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity };

export type UmlImplementationMatchingResultFragment = { __typename?: 'UmlImplementationMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'UmlImplementationMatch', matchType: MatchType, userArg: { __typename?: 'UmlImplementation', subClass: string, superClass: string }, sampleArg: { __typename?: 'UmlImplementation', subClass: string, superClass: string } }>, notMatchedForUser: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }>, notMatchedForSample: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }> };

export type UmlImplementationMatchFragment = { __typename?: 'UmlImplementationMatch', matchType: MatchType, userArg: { __typename?: 'UmlImplementation', subClass: string, superClass: string }, sampleArg: { __typename?: 'UmlImplementation', subClass: string, superClass: string } };

export type UmlImplementationFragment = { __typename?: 'UmlImplementation', subClass: string, superClass: string };

export type WebCorrectionMutationVariables = Exact<{
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: WebExPart;
  solution: FilesSolutionInput;
}>;


export type WebCorrectionMutation = { __typename?: 'Mutation', webExercise?: Maybe<{ __typename?: 'WebExerciseMutations', correct: { __typename?: 'WebCorrectionResult', solutionSaved: boolean, resultSaved: boolean, proficienciesUpdated?: Maybe<boolean>, result: { __typename?: 'WebResult', points: number, maxPoints: number, gradedHtmlTaskResults: Array<{ __typename?: 'GradedHtmlTaskResult', id: number, success: SuccessType, elementFound: boolean, isSuccessful: boolean, points: number, maxPoints: number, textContentResult?: Maybe<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: Maybe<string>, isSuccessful: boolean, points: number, maxPoints: number }>, attributeResults: Array<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: Maybe<string>, isSuccessful: boolean, points: number, maxPoints: number }> }>, gradedJsTaskResults: Array<{ __typename?: 'GradedJsTaskResult', id: number, success: SuccessType, points: number, maxPoints: number, gradedPreResults: Array<{ __typename?: 'GradedJsHtmlElementSpecResult', id: number }>, gradedJsActionResult: { __typename?: 'GradedJsActionResult', actionPerformed: boolean, points: number, maxPoints: number, jsAction: { __typename: 'JsAction' } }, gradedPostResults: Array<{ __typename?: 'GradedJsHtmlElementSpecResult', id: number }> }> } } }> };

export type WebCorrectionResultFragment = { __typename?: 'WebCorrectionResult', solutionSaved: boolean, resultSaved: boolean, proficienciesUpdated?: Maybe<boolean>, result: { __typename?: 'WebResult', points: number, maxPoints: number, gradedHtmlTaskResults: Array<{ __typename?: 'GradedHtmlTaskResult', id: number, success: SuccessType, elementFound: boolean, isSuccessful: boolean, points: number, maxPoints: number, textContentResult?: Maybe<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: Maybe<string>, isSuccessful: boolean, points: number, maxPoints: number }>, attributeResults: Array<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: Maybe<string>, isSuccessful: boolean, points: number, maxPoints: number }> }>, gradedJsTaskResults: Array<{ __typename?: 'GradedJsTaskResult', id: number, success: SuccessType, points: number, maxPoints: number, gradedPreResults: Array<{ __typename?: 'GradedJsHtmlElementSpecResult', id: number }>, gradedJsActionResult: { __typename?: 'GradedJsActionResult', actionPerformed: boolean, points: number, maxPoints: number, jsAction: { __typename: 'JsAction' } }, gradedPostResults: Array<{ __typename?: 'GradedJsHtmlElementSpecResult', id: number }> }> } };

export type WebResultFragment = { __typename?: 'WebResult', points: number, maxPoints: number, gradedHtmlTaskResults: Array<{ __typename?: 'GradedHtmlTaskResult', id: number, success: SuccessType, elementFound: boolean, isSuccessful: boolean, points: number, maxPoints: number, textContentResult?: Maybe<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: Maybe<string>, isSuccessful: boolean, points: number, maxPoints: number }>, attributeResults: Array<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: Maybe<string>, isSuccessful: boolean, points: number, maxPoints: number }> }>, gradedJsTaskResults: Array<{ __typename?: 'GradedJsTaskResult', id: number, success: SuccessType, points: number, maxPoints: number, gradedPreResults: Array<{ __typename?: 'GradedJsHtmlElementSpecResult', id: number }>, gradedJsActionResult: { __typename?: 'GradedJsActionResult', actionPerformed: boolean, points: number, maxPoints: number, jsAction: { __typename: 'JsAction' } }, gradedPostResults: Array<{ __typename?: 'GradedJsHtmlElementSpecResult', id: number }> }> };

export type GradedHtmlTaskResultFragment = { __typename?: 'GradedHtmlTaskResult', id: number, success: SuccessType, elementFound: boolean, isSuccessful: boolean, points: number, maxPoints: number, textContentResult?: Maybe<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: Maybe<string>, isSuccessful: boolean, points: number, maxPoints: number }>, attributeResults: Array<{ __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: Maybe<string>, isSuccessful: boolean, points: number, maxPoints: number }> };

export type GradedTextContentResultFragment = { __typename?: 'GradedTextResult', keyName: string, awaitedContent: string, maybeFoundContent?: Maybe<string>, isSuccessful: boolean, points: number, maxPoints: number };

export type GradedJsTaskResultFragment = { __typename?: 'GradedJsTaskResult', id: number, success: SuccessType, points: number, maxPoints: number, gradedPreResults: Array<{ __typename?: 'GradedJsHtmlElementSpecResult', id: number }>, gradedJsActionResult: { __typename?: 'GradedJsActionResult', actionPerformed: boolean, points: number, maxPoints: number, jsAction: { __typename: 'JsAction' } }, gradedPostResults: Array<{ __typename?: 'GradedJsHtmlElementSpecResult', id: number }> };

export type GradedJsHtmlElementSpecResultFragment = { __typename?: 'GradedJsHtmlElementSpecResult', id: number };

export type GradedJsActionResultFragment = { __typename?: 'GradedJsActionResult', actionPerformed: boolean, points: number, maxPoints: number, jsAction: { __typename: 'JsAction' } };

export type XmlCorrectionMutationVariables = Exact<{
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: XmlExPart;
  solution: XmlSolutionInput;
}>;


export type XmlCorrectionMutation = { __typename?: 'Mutation', xmlExercise?: Maybe<{ __typename?: 'XmlExerciseMutations', correct: { __typename?: 'XmlCorrectionResult', solutionSaved: boolean, resultSaved: boolean, proficienciesUpdated?: Maybe<boolean>, result: { __typename?: 'XmlResult', points: number, maxPoints: number, successType: SuccessType, grammarResult?: Maybe<{ __typename?: 'XmlGrammarResult', parseErrors: Array<{ __typename?: 'DTDParseException', msg: string, parsedLine: string }>, results: { __typename?: 'XmlElementLineComparisonMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'ElementLineMatch', matchType: MatchType, userArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, sampleArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, analysisResult: { __typename?: 'ElementLineAnalysisResult', attributesCorrect: boolean, correctAttributes: string, contentCorrect: boolean, correctContent: string } }>, notMatchedForUser: Array<{ __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }>, notMatchedForSample: Array<{ __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }> } }>, documentResult?: Maybe<{ __typename?: 'XmlDocumentResult', errors: Array<{ __typename?: 'XmlError', success: SuccessType, line: number, errorType: XmlErrorType, errorMessage: string }> }> } } }> };

export type XmlCorrectionResultFragment = { __typename?: 'XmlCorrectionResult', solutionSaved: boolean, resultSaved: boolean, proficienciesUpdated?: Maybe<boolean>, result: { __typename?: 'XmlResult', points: number, maxPoints: number, successType: SuccessType, grammarResult?: Maybe<{ __typename?: 'XmlGrammarResult', parseErrors: Array<{ __typename?: 'DTDParseException', msg: string, parsedLine: string }>, results: { __typename?: 'XmlElementLineComparisonMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'ElementLineMatch', matchType: MatchType, userArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, sampleArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, analysisResult: { __typename?: 'ElementLineAnalysisResult', attributesCorrect: boolean, correctAttributes: string, contentCorrect: boolean, correctContent: string } }>, notMatchedForUser: Array<{ __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }>, notMatchedForSample: Array<{ __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }> } }>, documentResult?: Maybe<{ __typename?: 'XmlDocumentResult', errors: Array<{ __typename?: 'XmlError', success: SuccessType, line: number, errorType: XmlErrorType, errorMessage: string }> }> } };

export type XmlResultFragment = { __typename?: 'XmlResult', points: number, maxPoints: number, successType: SuccessType, grammarResult?: Maybe<{ __typename?: 'XmlGrammarResult', parseErrors: Array<{ __typename?: 'DTDParseException', msg: string, parsedLine: string }>, results: { __typename?: 'XmlElementLineComparisonMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'ElementLineMatch', matchType: MatchType, userArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, sampleArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, analysisResult: { __typename?: 'ElementLineAnalysisResult', attributesCorrect: boolean, correctAttributes: string, contentCorrect: boolean, correctContent: string } }>, notMatchedForUser: Array<{ __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }>, notMatchedForSample: Array<{ __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }> } }>, documentResult?: Maybe<{ __typename?: 'XmlDocumentResult', errors: Array<{ __typename?: 'XmlError', success: SuccessType, line: number, errorType: XmlErrorType, errorMessage: string }> }> };

export type XmlGrammarResultFragment = { __typename?: 'XmlGrammarResult', parseErrors: Array<{ __typename?: 'DTDParseException', msg: string, parsedLine: string }>, results: { __typename?: 'XmlElementLineComparisonMatchingResult', points: number, maxPoints: number, allMatches: Array<{ __typename?: 'ElementLineMatch', matchType: MatchType, userArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, sampleArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, analysisResult: { __typename?: 'ElementLineAnalysisResult', attributesCorrect: boolean, correctAttributes: string, contentCorrect: boolean, correctContent: string } }>, notMatchedForUser: Array<{ __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }>, notMatchedForSample: Array<{ __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }> } };

export type XmlElementLineMatchingResultFragment = { __typename?: 'XmlElementLineComparisonMatchingResult', allMatches: Array<{ __typename?: 'ElementLineMatch', matchType: MatchType, userArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, sampleArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, analysisResult: { __typename?: 'ElementLineAnalysisResult', attributesCorrect: boolean, correctAttributes: string, contentCorrect: boolean, correctContent: string } }>, notMatchedForUser: Array<{ __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }>, notMatchedForSample: Array<{ __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }> };

export type XmlElementLineMatchFragment = { __typename?: 'ElementLineMatch', matchType: MatchType, userArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, sampleArg: { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> }, analysisResult: { __typename?: 'ElementLineAnalysisResult', attributesCorrect: boolean, correctAttributes: string, contentCorrect: boolean, correctContent: string } };

export type XmlElementLineAnalysisResultFragment = { __typename?: 'ElementLineAnalysisResult', attributesCorrect: boolean, correctAttributes: string, contentCorrect: boolean, correctContent: string };

export type ElementLineFragment = { __typename?: 'ElementLine', elementName: string, elementDefinition: { __typename?: 'ElementDefinition', elementName: string, content: string }, attributeLists: Array<{ __typename?: 'AttributeList', elementName: string, attributeDefinitions: Array<string> }> };

export type XmlDocumentResultFragment = { __typename?: 'XmlDocumentResult', errors: Array<{ __typename?: 'XmlError', success: SuccessType, line: number, errorType: XmlErrorType, errorMessage: string }> };

export type XmlErrorFragment = { __typename?: 'XmlError', success: SuccessType, line: number, errorType: XmlErrorType, errorMessage: string };

export type CollectionToolFragment = { __typename?: 'CollectionTool', id: string, name: string, collectionCount: any, lessonCount: any, exerciseCount: any };

export type ToolOverviewQueryVariables = Exact<{ [key: string]: never; }>;


export type ToolOverviewQuery = { __typename?: 'Query', tools: Array<{ __typename?: 'CollectionTool', id: string, name: string, collectionCount: any, lessonCount: any, exerciseCount: any }> };

export type CollectionToolOverviewQueryVariables = Exact<{
  toolId: Scalars['String'];
}>;


export type CollectionToolOverviewQuery = { __typename?: 'Query', tool?: Maybe<{ __typename?: 'CollectionTool', name: string, collectionCount: any, exerciseCount: any, lessonCount: any, proficiencies: Array<{ __typename?: 'UserProficiency', points: number, pointsForNextLevel: number, topic: { __typename?: 'Topic', abbreviation: string, title: string, maxLevel: { __typename?: 'Level', title: string, levelIndex: number } }, level: { __typename?: 'Level', title: string, levelIndex: number } }> }> };

export type UserProficiencyFragment = { __typename?: 'UserProficiency', points: number, pointsForNextLevel: number, topic: { __typename?: 'Topic', abbreviation: string, title: string, maxLevel: { __typename?: 'Level', title: string, levelIndex: number } }, level: { __typename?: 'Level', title: string, levelIndex: number } };

export type AllExercisesOverviewQueryVariables = Exact<{
  toolId: Scalars['String'];
}>;


export type AllExercisesOverviewQuery = { __typename?: 'Query', tool?: Maybe<{ __typename?: 'CollectionTool', allExercises: Array<{ __typename?: 'Exercise', exerciseId: number, collectionId: number, toolId: string, title: string, difficulty: number, topicsWithLevels: Array<{ __typename?: 'TopicWithLevel', topic: { __typename?: 'Topic', abbreviation: string, title: string, maxLevel: { __typename?: 'Level', title: string, levelIndex: number } }, level: { __typename?: 'Level', title: string, levelIndex: number } }>, parts: Array<{ __typename?: 'ExPart', id: string, name: string, solved?: Maybe<boolean> }> }> }> };

export type CollectionValuesFragment = { __typename?: 'ExerciseCollection', collectionId: number, title: string, exerciseCount: any };

export type CollectionListQueryVariables = Exact<{
  toolId: Scalars['String'];
}>;


export type CollectionListQuery = { __typename?: 'Query', tool?: Maybe<{ __typename?: 'CollectionTool', name: string, collections: Array<{ __typename?: 'ExerciseCollection', collectionId: number, title: string, exerciseCount: any }> }> };

export type CollOverviewToolFragment = { __typename?: 'CollectionTool', name: string, collection?: Maybe<{ __typename?: 'ExerciseCollection', title: string, exercises: Array<{ __typename?: 'Exercise', exerciseId: number, collectionId: number, toolId: string, title: string, difficulty: number, topicsWithLevels: Array<{ __typename?: 'TopicWithLevel', topic: { __typename?: 'Topic', abbreviation: string, title: string, maxLevel: { __typename?: 'Level', title: string, levelIndex: number } }, level: { __typename?: 'Level', title: string, levelIndex: number } }>, parts: Array<{ __typename?: 'ExPart', id: string, name: string, solved?: Maybe<boolean> }> }> }> };

export type CollectionOverviewQueryVariables = Exact<{
  toolId: Scalars['String'];
  collId: Scalars['Int'];
}>;


export type CollectionOverviewQuery = { __typename?: 'Query', tool?: Maybe<{ __typename?: 'CollectionTool', name: string, collection?: Maybe<{ __typename?: 'ExerciseCollection', title: string, exercises: Array<{ __typename?: 'Exercise', exerciseId: number, collectionId: number, toolId: string, title: string, difficulty: number, topicsWithLevels: Array<{ __typename?: 'TopicWithLevel', topic: { __typename?: 'Topic', abbreviation: string, title: string, maxLevel: { __typename?: 'Level', title: string, levelIndex: number } }, level: { __typename?: 'Level', title: string, levelIndex: number } }>, parts: Array<{ __typename?: 'ExPart', id: string, name: string, solved?: Maybe<boolean> }> }> }> }> };

export type PartFragment = { __typename?: 'ExPart', id: string, name: string, isEntryPart: boolean, solved?: Maybe<boolean> };

export type ExerciseOverviewFragment = { __typename?: 'Exercise', exerciseId: number, title: string, text: string, parts: Array<{ __typename?: 'ExPart', id: string, name: string, isEntryPart: boolean, solved?: Maybe<boolean> }> };

export type ExerciseOverviewQueryVariables = Exact<{
  toolId: Scalars['String'];
  collectionId: Scalars['Int'];
  exerciseId: Scalars['Int'];
}>;


export type ExerciseOverviewQuery = { __typename?: 'Query', tool?: Maybe<{ __typename?: 'CollectionTool', id: string, name: string, collection?: Maybe<{ __typename?: 'ExerciseCollection', collectionId: number, title: string, exercise?: Maybe<{ __typename?: 'Exercise', exerciseId: number, title: string, text: string, parts: Array<{ __typename?: 'ExPart', id: string, name: string, isEntryPart: boolean, solved?: Maybe<boolean> }> }> }> }> };

export type ExerciseSolveFieldsFragment = { __typename?: 'Exercise', exerciseId: number, collectionId: number, toolId: string, title: string, text: string, content: { __typename: 'EbnfExerciseContent', predefinedTerminals?: Maybe<Array<string>>, sampleSolutions: Array<{ __typename?: 'EbnfGrammar', startSymbol: string }> } | { __typename: 'FlaskExerciseContent', testConfig: { __typename?: 'FlaskTestsConfig', tests: Array<{ __typename?: 'FlaskSingleTestConfig', id: number, testName: string, description: string }> }, files: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }>, flaskSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }> }> } | { __typename: 'ProgrammingExerciseContent', programmingPart?: Maybe<ProgExPart>, unitTestPart: { __typename?: 'UnitTestPart', unitTestFiles: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }> }, implementationPart: { __typename?: 'ImplementationPart', files: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }> }, programmingSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }> }> } | { __typename: 'RegexExerciseContent', regexSampleSolutions: Array<string> } | { __typename: 'SqlExerciseContent', hint?: Maybe<string>, sqlSampleSolutions: Array<string>, sqlDbContents: Array<{ __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: Maybe<string>, different: boolean } }> }> }> } | { __typename: 'UmlExerciseContent', toIgnore: Array<string>, umlPart?: Maybe<UmlExPart>, mappings: Array<{ __typename?: 'KeyValueObject', key: string, value: string }>, umlSampleSolutions: Array<{ __typename?: 'UmlClassDiagram', classes: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename?: 'UmlAttribute', isAbstract: boolean, isDerived: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, memberType: string }>, methods: Array<{ __typename?: 'UmlMethod', isAbstract: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, parameters: string, memberType: string }> }>, associations: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }>, implementations: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }> }> } | { __typename: 'WebExerciseContent', webPart?: Maybe<WebExPart>, files: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }>, siteSpec: { __typename?: 'SiteSpec', fileName: string, jsTaskCount: number, htmlTasks: Array<{ __typename?: 'HtmlTask', text: string }> }, webSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }> }> } | { __typename: 'XmlExerciseContent', rootNode: string, grammarDescription: string, xmlPart?: Maybe<XmlExPart>, xmlSampleSolutions: Array<{ __typename?: 'XmlSolution', document: string, grammar: string }> } };

export type ExerciseQueryVariables = Exact<{
  toolId: Scalars['String'];
  collectionId: Scalars['Int'];
  exerciseId: Scalars['Int'];
  partId: Scalars['String'];
}>;


export type ExerciseQuery = { __typename?: 'Query', tool?: Maybe<{ __typename?: 'CollectionTool', collection?: Maybe<{ __typename?: 'ExerciseCollection', exercise?: Maybe<{ __typename?: 'Exercise', exerciseId: number, collectionId: number, toolId: string, title: string, text: string, content: { __typename: 'EbnfExerciseContent', predefinedTerminals?: Maybe<Array<string>>, sampleSolutions: Array<{ __typename?: 'EbnfGrammar', startSymbol: string }> } | { __typename: 'FlaskExerciseContent', testConfig: { __typename?: 'FlaskTestsConfig', tests: Array<{ __typename?: 'FlaskSingleTestConfig', id: number, testName: string, description: string }> }, files: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }>, flaskSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }> }> } | { __typename: 'ProgrammingExerciseContent', programmingPart?: Maybe<ProgExPart>, unitTestPart: { __typename?: 'UnitTestPart', unitTestFiles: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }> }, implementationPart: { __typename?: 'ImplementationPart', files: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }> }, programmingSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }> }> } | { __typename: 'RegexExerciseContent', regexSampleSolutions: Array<string> } | { __typename: 'SqlExerciseContent', hint?: Maybe<string>, sqlSampleSolutions: Array<string>, sqlDbContents: Array<{ __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: Maybe<string>, different: boolean } }> }> }> } | { __typename: 'UmlExerciseContent', toIgnore: Array<string>, umlPart?: Maybe<UmlExPart>, mappings: Array<{ __typename?: 'KeyValueObject', key: string, value: string }>, umlSampleSolutions: Array<{ __typename?: 'UmlClassDiagram', classes: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename?: 'UmlAttribute', isAbstract: boolean, isDerived: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, memberType: string }>, methods: Array<{ __typename?: 'UmlMethod', isAbstract: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, parameters: string, memberType: string }> }>, associations: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }>, implementations: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }> }> } | { __typename: 'WebExerciseContent', webPart?: Maybe<WebExPart>, files: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }>, siteSpec: { __typename?: 'SiteSpec', fileName: string, jsTaskCount: number, htmlTasks: Array<{ __typename?: 'HtmlTask', text: string }> }, webSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }> }> } | { __typename: 'XmlExerciseContent', rootNode: string, grammarDescription: string, xmlPart?: Maybe<XmlExPart>, xmlSampleSolutions: Array<{ __typename?: 'XmlSolution', document: string, grammar: string }> } }> }> }> };

export type ExerciseFileFragment = { __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean };

export type FilesSolutionFragment = { __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }> };

export type LevelFragment = { __typename?: 'Level', title: string, levelIndex: number };

export type TopicFragment = { __typename?: 'Topic', abbreviation: string, title: string, maxLevel: { __typename?: 'Level', title: string, levelIndex: number } };

export type TopicWithLevelFragment = { __typename?: 'TopicWithLevel', topic: { __typename?: 'Topic', abbreviation: string, title: string, maxLevel: { __typename?: 'Level', title: string, levelIndex: number } }, level: { __typename?: 'Level', title: string, levelIndex: number } };

export type FieldsPartFragment = { __typename?: 'ExPart', id: string, name: string, solved?: Maybe<boolean> };

export type FieldsForLinkFragment = { __typename?: 'Exercise', exerciseId: number, collectionId: number, toolId: string, title: string, difficulty: number, topicsWithLevels: Array<{ __typename?: 'TopicWithLevel', topic: { __typename?: 'Topic', abbreviation: string, title: string, maxLevel: { __typename?: 'Level', title: string, levelIndex: number } }, level: { __typename?: 'Level', title: string, levelIndex: number } }>, parts: Array<{ __typename?: 'ExPart', id: string, name: string, solved?: Maybe<boolean> }> };

export type EbnfExerciseContentFragment = { __typename?: 'EbnfExerciseContent', predefinedTerminals?: Maybe<Array<string>>, sampleSolutions: Array<{ __typename?: 'EbnfGrammar', startSymbol: string }> };

export type FlaskExerciseContentFragment = { __typename?: 'FlaskExerciseContent', testConfig: { __typename?: 'FlaskTestsConfig', tests: Array<{ __typename?: 'FlaskSingleTestConfig', id: number, testName: string, description: string }> }, files: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }>, flaskSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }> }> };

export type UnitTestPartFragment = { __typename?: 'UnitTestPart', unitTestFiles: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }> };

export type ProgrammingExerciseContentFragment = { __typename?: 'ProgrammingExerciseContent', programmingPart?: Maybe<ProgExPart>, unitTestPart: { __typename?: 'UnitTestPart', unitTestFiles: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }> }, implementationPart: { __typename?: 'ImplementationPart', files: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }> }, programmingSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }> }> };

export type RegexExerciseContentFragment = { __typename?: 'RegexExerciseContent', regexSampleSolutions: Array<string> };

export type SqlExerciseContentFragment = { __typename?: 'SqlExerciseContent', hint?: Maybe<string>, sqlSampleSolutions: Array<string>, sqlDbContents: Array<{ __typename?: 'SqlQueryResult', tableName: string, columnNames: Array<string>, rows: Array<{ __typename?: 'SqlRow', cells: Array<{ __typename?: 'SqlKeyCellValueObject', key: string, value: { __typename?: 'SqlCell', colName: string, content?: Maybe<string>, different: boolean } }> }> }> };

export type UmlExerciseContentFragment = { __typename?: 'UmlExerciseContent', toIgnore: Array<string>, umlPart?: Maybe<UmlExPart>, mappings: Array<{ __typename?: 'KeyValueObject', key: string, value: string }>, umlSampleSolutions: Array<{ __typename?: 'UmlClassDiagram', classes: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename?: 'UmlAttribute', isAbstract: boolean, isDerived: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, memberType: string }>, methods: Array<{ __typename?: 'UmlMethod', isAbstract: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, parameters: string, memberType: string }> }>, associations: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }>, implementations: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }> }> };

export type UmlClassDiagramFragment = { __typename?: 'UmlClassDiagram', classes: Array<{ __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename?: 'UmlAttribute', isAbstract: boolean, isDerived: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, memberType: string }>, methods: Array<{ __typename?: 'UmlMethod', isAbstract: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, parameters: string, memberType: string }> }>, associations: Array<{ __typename?: 'UmlAssociation', assocType: UmlAssociationType, assocName?: Maybe<string>, firstEnd: string, firstMult: UmlMultiplicity, secondEnd: string, secondMult: UmlMultiplicity }>, implementations: Array<{ __typename?: 'UmlImplementation', subClass: string, superClass: string }> };

export type UmlClassFragment = { __typename?: 'UmlClass', classType: UmlClassType, name: string, attributes: Array<{ __typename?: 'UmlAttribute', isAbstract: boolean, isDerived: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, memberType: string }>, methods: Array<{ __typename?: 'UmlMethod', isAbstract: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, parameters: string, memberType: string }> };

export type UmlAttributeFragment = { __typename?: 'UmlAttribute', isAbstract: boolean, isDerived: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, memberType: string };

export type UmlMethodFragment = { __typename?: 'UmlMethod', isAbstract: boolean, isStatic: boolean, visibility: UmlVisibility, memberName: string, parameters: string, memberType: string };

export type WebExerciseContentFragment = { __typename?: 'WebExerciseContent', webPart?: Maybe<WebExPart>, files: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }>, siteSpec: { __typename?: 'SiteSpec', fileName: string, jsTaskCount: number, htmlTasks: Array<{ __typename?: 'HtmlTask', text: string }> }, webSampleSolutions: Array<{ __typename?: 'FilesSolution', files: Array<{ __typename?: 'ExerciseFile', name: string, fileType: string, content: string, editable: boolean }> }> };

export type XmlExerciseContentFragment = { __typename?: 'XmlExerciseContent', rootNode: string, grammarDescription: string, xmlPart?: Maybe<XmlExPart>, xmlSampleSolutions: Array<{ __typename?: 'XmlSolution', document: string, grammar: string }> };

export type XmlSolutionFragment = { __typename?: 'XmlSolution', document: string, grammar: string };

export type RegisterMutationVariables = Exact<{
  username: Scalars['String'];
  firstPassword: Scalars['String'];
  secondPassword: Scalars['String'];
}>;


export type RegisterMutation = { __typename?: 'Mutation', register?: Maybe<string> };

export type LoggedInUserWithTokenFragment = { __typename?: 'LoggedInUserWithToken', jwt: string, loggedInUser: { __typename?: 'LoggedInUser', username: string, isAdmin: boolean } };

export type LoginMutationVariables = Exact<{
  username: Scalars['String'];
  password: Scalars['String'];
}>;


export type LoginMutation = { __typename?: 'Mutation', login?: Maybe<{ __typename?: 'LoggedInUserWithToken', jwt: string, loggedInUser: { __typename?: 'LoggedInUser', username: string, isAdmin: boolean } }> };

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
  solutionSaved
  resultSaved
  proficienciesUpdated
  result {
    ...FlaskResult
  }
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
  __typename
  solutionSaved
  resultSaved
  proficienciesUpdated
  result {
    ...ProgrammingResult
  }
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
  solutionSaved
  resultSaved
  proficienciesUpdated
  result {
    __typename
    points
    maxPoints
    ...RegexMatchingResult
    ...RegexExtractionResult
  }
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
  solutionSaved
  resultSaved
  proficienciesUpdated
  result {
    ...SqlResult
  }
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
  solutionSaved
  resultSaved
  proficienciesUpdated
  result {
    ...UmlResult
  }
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
export const GradedJsHtmlElementSpecResultFragmentDoc = gql`
    fragment GradedJsHtmlElementSpecResult on GradedJsHtmlElementSpecResult {
  id
}
    `;
export const GradedJsActionResultFragmentDoc = gql`
    fragment GradedJsActionResult on GradedJsActionResult {
  jsAction {
    __typename
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
    ...GradedJsHtmlElementSpecResult
  }
  gradedJsActionResult {
    ...GradedJsActionResult
  }
  gradedPostResults {
    ...GradedJsHtmlElementSpecResult
  }
  success
  points
  maxPoints
}
    ${GradedJsHtmlElementSpecResultFragmentDoc}
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
  solutionSaved
  resultSaved
  proficienciesUpdated
  result {
    ...WebResult
  }
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
  points
  maxPoints
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
  resultSaved
  proficienciesUpdated
  result {
    ...XmlResult
  }
}
    ${XmlResultFragmentDoc}`;
export const CollectionToolFragmentDoc = gql`
    fragment CollectionTool on CollectionTool {
  id
  name
  collectionCount
  lessonCount
  exerciseCount
}
    `;
export const LevelFragmentDoc = gql`
    fragment Level on Level {
  title
  levelIndex
}
    `;
export const TopicFragmentDoc = gql`
    fragment Topic on Topic {
  abbreviation
  title
  maxLevel {
    ...Level
  }
}
    ${LevelFragmentDoc}`;
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
export const CollectionValuesFragmentDoc = gql`
    fragment CollectionValues on ExerciseCollection {
  collectionId
  title
  exerciseCount
}
    `;
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
export const FieldsPartFragmentDoc = gql`
    fragment FieldsPart on ExPart {
  id
  name
  solved
}
    `;
export const FieldsForLinkFragmentDoc = gql`
    fragment FieldsForLink on Exercise {
  exerciseId
  collectionId
  toolId
  title
  difficulty
  topicsWithLevels {
    ...TopicWithLevel
  }
  parts {
    ...FieldsPart
  }
}
    ${TopicWithLevelFragmentDoc}
${FieldsPartFragmentDoc}`;
export const CollOverviewToolFragmentDoc = gql`
    fragment CollOverviewTool on CollectionTool {
  name
  collection(collId: $collId) {
    title
    exercises {
      ...FieldsForLink
    }
  }
}
    ${FieldsForLinkFragmentDoc}`;
export const PartFragmentDoc = gql`
    fragment Part on ExPart {
  id
  name
  isEntryPart
  solved
}
    `;
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
export const EbnfExerciseContentFragmentDoc = gql`
    fragment EbnfExerciseContent on EbnfExerciseContent {
  predefinedTerminals
  sampleSolutions {
    startSymbol
  }
}
    `;
export const ExerciseFileFragmentDoc = gql`
    fragment ExerciseFile on ExerciseFile {
  name
  fileType
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
  programmingPart: part(partId: $partId)
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
  umlPart: part(partId: $partId)
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
  webPart: part(partId: $partId)
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
  xmlPart: part(partId: $partId)
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
    ...EbnfExerciseContent
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
    ${EbnfExerciseContentFragmentDoc}
${FlaskExerciseContentFragmentDoc}
${ProgrammingExerciseContentFragmentDoc}
${RegexExerciseContentFragmentDoc}
${SqlExerciseContentFragmentDoc}
${UmlExerciseContentFragmentDoc}
${WebExerciseContentFragmentDoc}
${XmlExerciseContentFragmentDoc}`;
export const LoggedInUserWithTokenFragmentDoc = gql`
    fragment LoggedInUserWithToken on LoggedInUserWithToken {
  loggedInUser {
    username
    isAdmin
  }
  jwt
}
    `;
export const EbnfCorrectionDocument = gql`
    mutation EbnfCorrection($collId: Int!, $exId: Int!, $solution: EbnfGrammarInput!) {
  ebnfExercise(collId: $collId, exId: $exId) {
    correct(solution: $solution, part: GrammarCreation) {
      solutionSaved
      proficienciesUpdated
      resultSaved
      result {
        x
      }
    }
  }
}
    `;
export type EbnfCorrectionMutationFn = Apollo.MutationFunction<EbnfCorrectionMutation, EbnfCorrectionMutationVariables>;

/**
 * __useEbnfCorrectionMutation__
 *
 * To run a mutation, you first call `useEbnfCorrectionMutation` within a React component and pass it any options that fit your needs.
 * When your component renders, `useEbnfCorrectionMutation` returns a tuple that includes:
 * - A mutate function that you can call at any time to execute the mutation
 * - An object with fields that represent the current status of the mutation's execution
 *
 * @param baseOptions options that will be passed into the mutation, supported options are listed on: https://www.apollographql.com/docs/react/api/react-hooks/#options-2;
 *
 * @example
 * const [ebnfCorrectionMutation, { data, loading, error }] = useEbnfCorrectionMutation({
 *   variables: {
 *      collId: // value for 'collId'
 *      exId: // value for 'exId'
 *      solution: // value for 'solution'
 *   },
 * });
 */
export function useEbnfCorrectionMutation(baseOptions?: Apollo.MutationHookOptions<EbnfCorrectionMutation, EbnfCorrectionMutationVariables>) {
        const options = {...defaultOptions, ...baseOptions}
        return Apollo.useMutation<EbnfCorrectionMutation, EbnfCorrectionMutationVariables>(EbnfCorrectionDocument, options);
      }
export type EbnfCorrectionMutationHookResult = ReturnType<typeof useEbnfCorrectionMutation>;
export type EbnfCorrectionMutationResult = Apollo.MutationResult<EbnfCorrectionMutation>;
export type EbnfCorrectionMutationOptions = Apollo.BaseMutationOptions<EbnfCorrectionMutation, EbnfCorrectionMutationVariables>;
export const FlaskCorrectionDocument = gql`
    mutation FlaskCorrection($collId: Int!, $exId: Int!, $part: FlaskExercisePart!, $solution: FilesSolutionInput!) {
  flaskExercise(collId: $collId, exId: $exId) {
    correct(part: $part, solution: $solution) {
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
 *      part: // value for 'part'
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
    mutation RegexCorrection($collectionId: Int!, $exerciseId: Int!, $part: RegexExPart!, $solution: String!) {
  regexExercise(collId: $collectionId, exId: $exerciseId) {
    correct(part: $part, solution: $solution) {
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
 *      part: // value for 'part'
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
    mutation SqlCorrection($collectionId: Int!, $exerciseId: Int!, $part: SqlExPart!, $solution: String!) {
  sqlExercise(collId: $collectionId, exId: $exerciseId) {
    correct(part: $part, solution: $solution) {
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
 *      part: // value for 'part'
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
    ...CollectionTool
  }
}
    ${CollectionToolFragmentDoc}`;

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
    name
    collectionCount
    exerciseCount
    lessonCount
    proficiencies {
      ...UserProficiency
    }
  }
}
    ${UserProficiencyFragmentDoc}`;

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
    allExercises {
      topicsWithLevels {
        ...TopicWithLevel
      }
      ...FieldsForLink
    }
  }
}
    ${TopicWithLevelFragmentDoc}
${FieldsForLinkFragmentDoc}`;

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
    name
    collections {
      ...CollectionValues
    }
  }
}
    ${CollectionValuesFragmentDoc}`;

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
}
    ${ExerciseOverviewFragmentDoc}`;

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
    query Exercise($toolId: String!, $collectionId: Int!, $exerciseId: Int!, $partId: String!) {
  tool(toolId: $toolId) {
    collection(collId: $collectionId) {
      exercise(exId: $exerciseId) {
        ...ExerciseSolveFields
      }
    }
  }
}
    ${ExerciseSolveFieldsFragmentDoc}`;

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
 *      partId: // value for 'partId'
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
    mutation Register($username: String!, $firstPassword: String!, $secondPassword: String!) {
  register(
    registerValues: {username: $username, firstPassword: $firstPassword, secondPassword: $secondPassword}
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
 *      firstPassword: // value for 'firstPassword'
 *      secondPassword: // value for 'secondPassword'
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
  login(credentials: {username: $username, password: $password}) {
    ...LoggedInUserWithToken
  }
}
    ${LoggedInUserWithTokenFragmentDoc}`;
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