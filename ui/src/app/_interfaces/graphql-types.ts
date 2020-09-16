export type Maybe<T> = T | null;
export type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: string;
  String: string;
  Boolean: boolean;
  Int: number;
  Float: number;
  /**
   * The `Long` scalar type represents non-fractional signed whole numeric values.
   * Long can represent values between -(2^63) and 2^63 - 1.
   */
  Long: any;
};

export type AbstractCorrectionResult = {
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

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
  TruePositive = 'TruePositive',
  FalsePositive = 'FalsePositive',
  FalseNegative = 'FalseNegative',
  TrueNegative = 'TrueNegative'
}

export type CollectionTool = {
  __typename?: 'CollectionTool';
  id: Scalars['ID'];
  name: Scalars['String'];
  state: ToolState;
  lessonCount: Scalars['Long'];
  lessons: Array<Lesson>;
  lesson?: Maybe<Lesson>;
  collectionCount: Scalars['Long'];
  collections: Array<ExerciseCollection>;
  collection?: Maybe<ExerciseCollection>;
  exerciseCount: Scalars['Long'];
  allExercises: Array<Exercise>;
  proficiencies: Array<UserProficiency>;
};


export type CollectionToolLessonArgs = {
  lessonId: Scalars['Int'];
};


export type CollectionToolCollectionArgs = {
  collId: Scalars['Int'];
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

export type Exercise = {
  __typename?: 'Exercise';
  exerciseId: Scalars['Int'];
  collectionId: Scalars['Int'];
  toolId: Scalars['String'];
  title: Scalars['String'];
  authors: Array<Scalars['String']>;
  text: Scalars['String'];
  topicsWithLevels: Array<TopicWithLevel>;
  difficulty: Scalars['Int'];
  flaskContent?: Maybe<FlaskExerciseContent>;
  programmingContent?: Maybe<ProgrammingExerciseContent>;
  regexContent?: Maybe<RegexExerciseContent>;
  sqlContent?: Maybe<SqlExerciseContent>;
  umlContent?: Maybe<UmlExerciseContent>;
  webContent?: Maybe<WebExerciseContent>;
  xmlContent?: Maybe<XmlExerciseContent>;
  parts: Array<ExPart>;
};

export type ExerciseCollection = {
  __typename?: 'ExerciseCollection';
  collectionId: Scalars['Int'];
  title: Scalars['String'];
  authors: Array<Scalars['String']>;
  exerciseCount: Scalars['Long'];
  exercises: Array<Exercise>;
  exercise?: Maybe<Exercise>;
};


export type ExerciseCollectionExerciseArgs = {
  exId: Scalars['Int'];
};

export type ExerciseFile = {
  __typename?: 'ExerciseFile';
  name: Scalars['String'];
  fileType: Scalars['String'];
  editable: Scalars['Boolean'];
  content: Scalars['String'];
};

export type ExerciseFileInput = {
  name: Scalars['String'];
  fileType: Scalars['String'];
  editable: Scalars['Boolean'];
  content: Scalars['String'];
};

export type ExPart = {
  __typename?: 'ExPart';
  id: Scalars['String'];
  name: Scalars['String'];
  isEntryPart: Scalars['Boolean'];
  solved: Scalars['Boolean'];
};

export type FilesSampleSolution = {
  __typename?: 'FilesSampleSolution';
  id: Scalars['Int'];
  sample: FilesSolution;
};

export type FilesSolution = {
  __typename?: 'FilesSolution';
  files: Array<ExerciseFile>;
};

export type FilesSolutionInput = {
  files: Array<ExerciseFileInput>;
};

export type FlaskAbstractCorrectionResult = {
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type FlaskCorrectionResult = {
  __typename?: 'FlaskCorrectionResult';
  solutionSaved: Scalars['Boolean'];
  resultSaved: Scalars['Boolean'];
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: FlaskAbstractCorrectionResult;
};

export type FlaskExerciseContent = {
  __typename?: 'FlaskExerciseContent';
  files: Array<ExerciseFile>;
  testFiles: Array<ExerciseFile>;
  testConfig: FlaskTestsConfig;
  sampleSolutions: Array<FilesSampleSolution>;
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

export type FlaskInternalErrorResult = FlaskAbstractCorrectionResult & AbstractCorrectionResult & {
  __typename?: 'FlaskInternalErrorResult';
  msg: Scalars['String'];
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type FlaskResult = FlaskAbstractCorrectionResult & AbstractCorrectionResult & {
  __typename?: 'FlaskResult';
  testResults: Array<FlaskTestResult>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type FlaskSingleTestConfig = {
  __typename?: 'FlaskSingleTestConfig';
  maxPoints: Scalars['Int'];
  testName: Scalars['String'];
  testFunctionName: Scalars['String'];
  dependencies?: Maybe<Array<Scalars['String']>>;
};

export type FlaskTestResult = {
  __typename?: 'FlaskTestResult';
  maxPoints: Scalars['Int'];
  testName: Scalars['String'];
  successful: Scalars['Boolean'];
  stdout: Array<Scalars['String']>;
  stderr: Array<Scalars['String']>;
};

export type FlaskTestsConfig = {
  __typename?: 'FlaskTestsConfig';
  testFileName: Scalars['String'];
  testClassName: Scalars['String'];
  tests: Array<FlaskSingleTestConfig>;
};

export type GradedHtmlTaskResult = {
  __typename?: 'GradedHtmlTaskResult';
  id: Scalars['Int'];
  success: SuccessType;
  elementFound: Scalars['Boolean'];
  textContentResult?: Maybe<GradedTextResult>;
  attributeResults: Array<GradedTextResult>;
  isSuccessful: Scalars['Boolean'];
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type GradedJsActionResult = {
  __typename?: 'GradedJsActionResult';
  actionPerformed: Scalars['Boolean'];
  jsAction: JsAction;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type GradedJsHtmlElementSpecResult = {
  __typename?: 'GradedJsHtmlElementSpecResult';
  id: Scalars['Int'];
  success: SuccessType;
  elementFound: Scalars['Boolean'];
  textContentResult?: Maybe<GradedTextResult>;
  attributeResults: Array<GradedTextResult>;
  isSuccessful: Scalars['Boolean'];
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type GradedJsTaskResult = {
  __typename?: 'GradedJsTaskResult';
  id: Scalars['Int'];
  gradedPreResults: Array<GradedJsHtmlElementSpecResult>;
  gradedPostResults: Array<GradedJsHtmlElementSpecResult>;
  success: SuccessType;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
  gradedJsActionResult: GradedJsActionResult;
};

export type GradedTextResult = {
  __typename?: 'GradedTextResult';
  keyName: Scalars['String'];
  awaitedContent: Scalars['String'];
  maybeFoundContent?: Maybe<Scalars['String']>;
  isSuccessful: Scalars['Boolean'];
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
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

export type JsAction = {
  __typename?: 'JsAction';
  xpathQuery: Scalars['String'];
  actionType: JsActionType;
  keysToSend?: Maybe<Scalars['String']>;
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
  lessonId: Scalars['Int'];
  toolId: Scalars['String'];
  title: Scalars['String'];
  description: Scalars['String'];
  video?: Maybe<Scalars['String']>;
  contentCount: Scalars['Long'];
  contents: Array<LessonContent>;
  content?: Maybe<LessonContent>;
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
  id: Scalars['Int'];
  questionText: Scalars['String'];
  answers: Array<LessonMultipleChoiceQuestionAnswer>;
};

export type LessonMultipleChoiceQuestionAnswer = {
  __typename?: 'LessonMultipleChoiceQuestionAnswer';
  id: Scalars['Int'];
  answer: Scalars['String'];
  isCorrect: Scalars['Boolean'];
};

export type LessonMultipleChoiceQuestionsContent = LessonContent & {
  __typename?: 'LessonMultipleChoiceQuestionsContent';
  contentId: Scalars['Int'];
  lessonId: Scalars['Int'];
  toolId: Scalars['String'];
  questions: Array<LessonMultipleChoiceQuestion>;
};

export type LessonTextContent = LessonContent & {
  __typename?: 'LessonTextContent';
  contentId: Scalars['Int'];
  lessonId: Scalars['Int'];
  toolId: Scalars['String'];
  content: Scalars['String'];
};

export type Level = {
  __typename?: 'Level';
  title: Scalars['String'];
  levelIndex: Scalars['Int'];
};

export type LoggedInUser = {
  __typename?: 'LoggedInUser';
  username: Scalars['String'];
  isAdmin: Scalars['Boolean'];
};

export type LoggedInUserWithToken = {
  __typename?: 'LoggedInUserWithToken';
  loggedInUser: LoggedInUser;
  jwt: Scalars['String'];
};


export type MatchingResult = {
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
  allMatches: Array<NewMatch>;
};

export enum MatchType {
  UnsuccessfulMatch = 'UNSUCCESSFUL_MATCH',
  SuccessfulMatch = 'SUCCESSFUL_MATCH',
  PartialMatch = 'PARTIAL_MATCH',
  OnlySample = 'ONLY_SAMPLE',
  OnlyUser = 'ONLY_USER'
}

export type Mutation = {
  __typename?: 'Mutation';
  register?: Maybe<Scalars['String']>;
  login?: Maybe<LoggedInUserWithToken>;
  me?: Maybe<UserMutations>;
};


export type MutationRegisterArgs = {
  registerValues: RegisterValues;
};


export type MutationLoginArgs = {
  credentials: UserCredentials;
};


export type MutationMeArgs = {
  userJwt: Scalars['String'];
};

export type NewMatch = {
  matchType: MatchType;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type NormalExecutionResult = {
  __typename?: 'NormalExecutionResult';
  successful: Scalars['Boolean'];
  logs: Scalars['String'];
};

export type NormalUnitTestPart = {
  __typename?: 'NormalUnitTestPart';
  unitTestsDescription: Scalars['String'];
  unitTestFiles: Array<ExerciseFile>;
  unitTestTestConfigs: Array<UnitTestTestConfig>;
  testFileName: Scalars['String'];
  folderName: Scalars['String'];
  sampleSolFileNames: Array<Scalars['String']>;
  simplifiedTestMainFile?: Maybe<ExerciseFile>;
};

export enum ProgExPart {
  TestCreation = 'TestCreation',
  Implementation = 'Implementation',
  ActivityDiagram = 'ActivityDiagram'
}

export type ProgrammingAbstractResult = {
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type ProgrammingCorrectionResult = {
  __typename?: 'ProgrammingCorrectionResult';
  solutionSaved: Scalars['Boolean'];
  resultSaved: Scalars['Boolean'];
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: ProgrammingAbstractResult;
};

export type ProgrammingExerciseContent = {
  __typename?: 'ProgrammingExerciseContent';
  filename: Scalars['String'];
  implementationPart: ImplementationPart;
  sampleSolutions: Array<FilesSampleSolution>;
  unitTestPart: UnitTestPart;
  part?: Maybe<ProgExPart>;
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

export type ProgrammingInternalErrorResult = ProgrammingAbstractResult & AbstractCorrectionResult & {
  __typename?: 'ProgrammingInternalErrorResult';
  msg: Scalars['String'];
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type ProgrammingResult = ProgrammingAbstractResult & AbstractCorrectionResult & {
  __typename?: 'ProgrammingResult';
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  simplifiedResults: Array<SimplifiedExecutionResult>;
  normalResult?: Maybe<NormalExecutionResult>;
  unitTestResults: Array<UnitTestCorrectionResult>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type Query = {
  __typename?: 'Query';
  me?: Maybe<User>;
};


export type QueryMeArgs = {
  userJwt: Scalars['String'];
};

export type RegexAbstractResult = {
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type RegexCorrectionResult = {
  __typename?: 'RegexCorrectionResult';
  solutionSaved: Scalars['Boolean'];
  resultSaved: Scalars['Boolean'];
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: RegexAbstractResult;
};

export enum RegexCorrectionType {
  Matching = 'MATCHING',
  Extraction = 'EXTRACTION'
}

export type RegexExerciseContent = {
  __typename?: 'RegexExerciseContent';
  maxPoints: Scalars['Int'];
  correctionType: RegexCorrectionType;
  matchTestData: Array<RegexMatchTestData>;
  extractionTestData: Array<RegexExtractionTestData>;
  sampleSolutions: Array<RegexSampleSolution>;
  part?: Maybe<RegexExPart>;
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

export enum RegexExPart {
  RegexSingleExPart = 'RegexSingleExPart'
}

export type RegexExtractedValuesComparisonMatchingResult = MatchingResult & {
  __typename?: 'RegexExtractedValuesComparisonMatchingResult';
  allMatches: Array<RegexMatchMatch>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type RegexExtractionResult = RegexAbstractResult & AbstractCorrectionResult & {
  __typename?: 'RegexExtractionResult';
  extractionResults: Array<RegexExtractionSingleResult>;
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

export type RegexInternalErrorResult = RegexAbstractResult & AbstractCorrectionResult & {
  __typename?: 'RegexInternalErrorResult';
  msg: Scalars['String'];
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type RegexMatchingResult = RegexAbstractResult & AbstractCorrectionResult & {
  __typename?: 'RegexMatchingResult';
  matchingResults: Array<RegexMatchingSingleResult>;
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

export type RegexSampleSolution = {
  __typename?: 'RegexSampleSolution';
  id: Scalars['Int'];
  sample: Scalars['String'];
};

export type RegisterValues = {
  username: Scalars['String'];
  firstPassword: Scalars['String'];
  secondPassword: Scalars['String'];
};

export type SelectAdditionalComparisons = {
  __typename?: 'SelectAdditionalComparisons';
  groupByComparison: SqlGroupByComparisonMatchingResult;
  orderByComparison: SqlOrderByComparisonMatchingResult;
  limitComparison: SqlLimitComparisonMatchingResult;
};

export type SimplifiedExecutionResult = {
  __typename?: 'SimplifiedExecutionResult';
  testId: Scalars['Int'];
  success: SuccessType;
  stdout?: Maybe<Scalars['String']>;
  testInput: Scalars['String'];
  awaited: Scalars['String'];
  gotten: Scalars['String'];
};

export type SimplifiedUnitTestPart = {
  __typename?: 'SimplifiedUnitTestPart';
  simplifiedTestMainFile: ExerciseFile;
};

export type SiteSpec = {
  __typename?: 'SiteSpec';
  fileName: Scalars['String'];
  htmlTasks: Array<HtmlTask>;
  htmlTaskCount: Scalars['Int'];
  jsTaskCount: Scalars['Int'];
};

export type SqlAbstractResult = {
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type SqlBinaryExpressionComparisonMatchingResult = MatchingResult & {
  __typename?: 'SqlBinaryExpressionComparisonMatchingResult';
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
  content?: Maybe<Scalars['String']>;
  different: Scalars['Boolean'];
};

export type SqlColumnComparisonMatchingResult = MatchingResult & {
  __typename?: 'SqlColumnComparisonMatchingResult';
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

export type SqlCorrectionResult = {
  __typename?: 'SqlCorrectionResult';
  solutionSaved: Scalars['Boolean'];
  resultSaved: Scalars['Boolean'];
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: SqlAbstractResult;
};

export type SqlExecutionResult = {
  __typename?: 'SqlExecutionResult';
  userResult?: Maybe<SqlQueryResult>;
  sampleResult?: Maybe<SqlQueryResult>;
};

export type SqlExerciseContent = {
  __typename?: 'SqlExerciseContent';
  exerciseType: SqlExerciseType;
  schemaName: Scalars['String'];
  sampleSolutions: Array<SqlSampleSolution>;
  hint?: Maybe<Scalars['String']>;
  part?: Maybe<SqlExPart>;
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
  Update = 'UPDATE',
  Delete = 'DELETE',
  Select = 'SELECT',
  Insert = 'INSERT',
  Create = 'CREATE'
}

export enum SqlExPart {
  SqlSingleExPart = 'SqlSingleExPart'
}

export type SqlGroupByComparisonMatchingResult = MatchingResult & {
  __typename?: 'SqlGroupByComparisonMatchingResult';
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

export type SqlInsertComparisonMatchingResult = MatchingResult & {
  __typename?: 'SqlInsertComparisonMatchingResult';
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

export type SqlInternalErrorResult = SqlAbstractResult & AbstractCorrectionResult & {
  __typename?: 'SqlInternalErrorResult';
  msg: Scalars['String'];
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type SqlKeyCellValueObject = {
  __typename?: 'SqlKeyCellValueObject';
  key: Scalars['String'];
  value: SqlCell;
};

export type SqlLimitComparisonMatchingResult = MatchingResult & {
  __typename?: 'SqlLimitComparisonMatchingResult';
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

export type SqlResult = SqlAbstractResult & AbstractCorrectionResult & {
  __typename?: 'SqlResult';
  staticComparison: SqlQueriesStaticComparison;
  executionResult: SqlExecutionResult;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type SqlRow = {
  __typename?: 'SqlRow';
  cells: Array<SqlKeyCellValueObject>;
};

export type SqlSampleSolution = {
  __typename?: 'SqlSampleSolution';
  id: Scalars['Int'];
  sample: Scalars['String'];
};

export type SqlTableComparisonMatchingResult = MatchingResult & {
  __typename?: 'SqlTableComparisonMatchingResult';
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

export enum SuccessType {
  Error = 'ERROR',
  None = 'NONE',
  Partially = 'PARTIALLY',
  Complete = 'COMPLETE'
}

export enum ToolState {
  PreAlpha = 'PRE_ALPHA',
  Alpha = 'ALPHA',
  Beta = 'BETA',
  Live = 'LIVE'
}

export type Topic = {
  __typename?: 'Topic';
  abbreviation: Scalars['String'];
  toolId: Scalars['String'];
  title: Scalars['String'];
  maxLevel: Level;
};

export type TopicWithLevel = {
  __typename?: 'TopicWithLevel';
  topic: Topic;
  level: Level;
};

export type UmlAbstractResult = {
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

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
  assocType?: Maybe<UmlAssociationType>;
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
  allMatches: Array<UmlAssociationMatch>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
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
  visibility?: Maybe<UmlVisibility>;
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
  classType?: Maybe<UmlClassType>;
  name: Scalars['String'];
  attributes?: Maybe<Array<UmlAttributeInput>>;
  methods?: Maybe<Array<UmlMethodInput>>;
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
  allMatches: Array<UmlClassMatch>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export enum UmlClassType {
  Abstract = 'ABSTRACT',
  Class = 'CLASS',
  Interface = 'INTERFACE'
}

export type UmlCorrectionResult = {
  __typename?: 'UmlCorrectionResult';
  solutionSaved: Scalars['Boolean'];
  resultSaved: Scalars['Boolean'];
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: UmlAbstractResult;
};

export type UmlExerciseContent = {
  __typename?: 'UmlExerciseContent';
  toIgnore: Array<Scalars['String']>;
  sampleSolutions: Array<UmlSampleSolution>;
  part?: Maybe<UmlExPart>;
  mappings: Array<KeyValueObject>;
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
  allMatches: Array<UmlImplementationMatch>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type UmlInternalErrorResult = UmlAbstractResult & {
  __typename?: 'UmlInternalErrorResult';
  msg: Scalars['String'];
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
  visibility?: Maybe<UmlVisibility>;
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
  allMatches: Array<UmlMethodMatch>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export enum UmlMultiplicity {
  Single = 'SINGLE',
  Unbound = 'UNBOUND'
}

export type UmlResult = UmlAbstractResult & {
  __typename?: 'UmlResult';
  classResult?: Maybe<UmlClassMatchingResult>;
  assocResult?: Maybe<UmlAssociationMatchingResult>;
  implResult?: Maybe<UmlImplementationMatchingResult>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

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
  testId: Scalars['Int'];
  description: Scalars['String'];
  successful: Scalars['Boolean'];
  stdout: Array<Scalars['String']>;
  stderr: Array<Scalars['String']>;
};

export type UnitTestPart = SimplifiedUnitTestPart | NormalUnitTestPart;

export type UnitTestTestConfig = {
  __typename?: 'UnitTestTestConfig';
  id: Scalars['Int'];
  shouldFail: Scalars['Boolean'];
  description: Scalars['String'];
  file: ExerciseFile;
};

export type User = {
  __typename?: 'User';
  tools: Array<CollectionTool>;
  tool?: Maybe<CollectionTool>;
};


export type UserToolArgs = {
  toolId: Scalars['String'];
};

export type UserCredentials = {
  username: Scalars['String'];
  password: Scalars['String'];
};

export type UserMutations = {
  __typename?: 'UserMutations';
  flaskExercise?: Maybe<FlaskExerciseMutations>;
  programmingExercise?: Maybe<ProgrammingExerciseMutations>;
  regexExercise?: Maybe<RegexExerciseMutations>;
  sqlExercise?: Maybe<SqlExerciseMutations>;
  umlExercise?: Maybe<UmlExerciseMutations>;
  webExercise?: Maybe<WebExerciseMutations>;
  xmlExercise?: Maybe<XmlExerciseMutations>;
};


export type UserMutationsFlaskExerciseArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
};


export type UserMutationsProgrammingExerciseArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
};


export type UserMutationsRegexExerciseArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
};


export type UserMutationsSqlExerciseArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
};


export type UserMutationsUmlExerciseArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
};


export type UserMutationsWebExerciseArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
};


export type UserMutationsXmlExerciseArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
};

export type UserProficiency = {
  __typename?: 'UserProficiency';
  username: Scalars['String'];
  topic: Topic;
  points: Scalars['Int'];
  pointsForNextLevel: Scalars['Int'];
  level: Level;
};

export type WebAbstractResult = {
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type WebCorrectionResult = {
  __typename?: 'WebCorrectionResult';
  solutionSaved: Scalars['Boolean'];
  resultSaved: Scalars['Boolean'];
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: WebAbstractResult;
};

export type WebExerciseContent = {
  __typename?: 'WebExerciseContent';
  htmlText?: Maybe<Scalars['String']>;
  jsText?: Maybe<Scalars['String']>;
  siteSpec: SiteSpec;
  files: Array<ExerciseFile>;
  sampleSolutions: Array<FilesSampleSolution>;
  part?: Maybe<WebExPart>;
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

export enum WebExPart {
  HtmlPart = 'HtmlPart',
  JsPart = 'JsPart'
}

export type WebInternalErrorResult = WebAbstractResult & AbstractCorrectionResult & {
  __typename?: 'WebInternalErrorResult';
  msg: Scalars['String'];
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type WebResult = WebAbstractResult & AbstractCorrectionResult & {
  __typename?: 'WebResult';
  gradedHtmlTaskResults: Array<GradedHtmlTaskResult>;
  gradedJsTaskResults: Array<GradedJsTaskResult>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type XmlAbstractResult = {
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type XmlCorrectionResult = {
  __typename?: 'XmlCorrectionResult';
  solutionSaved: Scalars['Boolean'];
  resultSaved: Scalars['Boolean'];
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: XmlAbstractResult;
};

export type XmlDocumentResult = {
  __typename?: 'XmlDocumentResult';
  errors: Array<XmlError>;
};

export type XmlElementLineComparisonMatchingResult = MatchingResult & {
  __typename?: 'XmlElementLineComparisonMatchingResult';
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
  part?: Maybe<XmlExPart>;
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

export enum XmlExPart {
  GrammarCreationXmlPart = 'GrammarCreationXmlPart',
  DocumentCreationXmlPart = 'DocumentCreationXmlPart'
}

export type XmlGrammarResult = {
  __typename?: 'XmlGrammarResult';
  parseErrors: Array<DtdParseException>;
  results: XmlElementLineComparisonMatchingResult;
};

export type XmlInternalErrorResult = XmlAbstractResult & AbstractCorrectionResult & {
  __typename?: 'XmlInternalErrorResult';
  msg: Scalars['String'];
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type XmlResult = XmlAbstractResult & AbstractCorrectionResult & {
  __typename?: 'XmlResult';
  successType: SuccessType;
  documentResult?: Maybe<XmlDocumentResult>;
  grammarResult?: Maybe<XmlGrammarResult>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
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
