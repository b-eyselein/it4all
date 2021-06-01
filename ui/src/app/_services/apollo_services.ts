import { gql } from 'apollo-angular';
import { Injectable } from '@angular/core';
import * as Apollo from 'apollo-angular';
export type Maybe<T> = T | null;
export type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
export type MakeOptional<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]?: Maybe<T[SubKey]> };
export type MakeMaybe<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]: Maybe<T[SubKey]> };
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



export type AbstractCorrectionResult = {
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type AdditionalComparison = {
  __typename?: 'AdditionalComparison';
  selectComparisons?: Maybe<SelectAdditionalComparisons>;
  insertComparison?: Maybe<StringMatchingResult>;
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

export type DtdParseException = {
  __typename?: 'DTDParseException';
  msg: Scalars['String'];
  parsedLine: Scalars['String'];
};

export type EbnfCorrectionResult = {
  __typename?: 'EbnfCorrectionResult';
  solutionSaved: Scalars['Boolean'];
  resultSaved: Scalars['Boolean'];
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: EbnfResult;
};

export enum EbnfExPart {
  GrammarCreation = 'GrammarCreation'
}

export type EbnfExerciseMutations = {
  __typename?: 'EbnfExerciseMutations';
  correct: EbnfCorrectionResult;
};


export type EbnfExerciseMutationsCorrectArgs = {
  part: EbnfExPart;
  solution: EbnfGrammarInput;
};

export type EbnfGrammarInput = {
  startSymbol: Scalars['String'];
};

export type EbnfResult = {
  __typename?: 'EbnfResult';
  x: Scalars['String'];
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
  userArg: ElementLine;
  sampleArg: ElementLine;
  analysisResult: ElementLineAnalysisResult;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type ExerciseFileInput = {
  name: Scalars['String'];
  fileType: Scalars['String'];
  content: Scalars['String'];
  editable: Scalars['Boolean'];
};

export type FilesSolutionInput = {
  files: Array<ExerciseFileInput>;
};

export type FlaskCorrectionResult = {
  __typename?: 'FlaskCorrectionResult';
  solutionSaved: Scalars['Boolean'];
  resultSaved: Scalars['Boolean'];
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: FlaskResult;
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
  testResults: Array<FlaskTestResult>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type FlaskTestResult = {
  __typename?: 'FlaskTestResult';
  testId: Scalars['Int'];
  testName: Scalars['String'];
  successful: Scalars['Boolean'];
  stdout: Array<Scalars['String']>;
  stderr: Array<Scalars['String']>;
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

export type ImplementationCorrectionResult = ProgrammingTestCorrectionResult & {
  __typename?: 'ImplementationCorrectionResult';
  testSuccessful: Scalars['Boolean'];
  stdout: Array<Scalars['String']>;
  stderr: Array<Scalars['String']>;
  successful: Scalars['Boolean'];
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

export enum MatchType {
  SuccessfulMatch = 'SUCCESSFUL_MATCH',
  PartialMatch = 'PARTIAL_MATCH',
  UnsuccessfulMatch = 'UNSUCCESSFUL_MATCH'
}

export type MatchingResult = {
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
  allMatches: Array<NewMatch>;
  notMatchedForUserString: Array<Scalars['String']>;
  notMatchedForSampleString: Array<Scalars['String']>;
};

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

export type NewMatch = {
  matchType: MatchType;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type ProgrammingCorrectionResult = {
  __typename?: 'ProgrammingCorrectionResult';
  solutionSaved: Scalars['Boolean'];
  resultSaved: Scalars['Boolean'];
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: ProgrammingResult;
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
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  implementationCorrectionResult?: Maybe<ImplementationCorrectionResult>;
  unitTestResults: Array<UnitTestCorrectionResult>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type ProgrammingTestCorrectionResult = {
  successful: Scalars['Boolean'];
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

export type RegexExerciseMutations = {
  __typename?: 'RegexExerciseMutations';
  correct: RegexCorrectionResult;
};


export type RegexExerciseMutationsCorrectArgs = {
  part: RegexExPart;
  solution: Scalars['String'];
};

export type RegexExtractedValuesComparisonMatchingResult = MatchingResult & {
  __typename?: 'RegexExtractedValuesComparisonMatchingResult';
  allMatches: Array<RegexMatchMatch>;
  notMatchedForUser: Array<Scalars['String']>;
  notMatchedForSample: Array<Scalars['String']>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
  notMatchedForUserString: Array<Scalars['String']>;
  notMatchedForSampleString: Array<Scalars['String']>;
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

export type RegexMatchMatch = NewMatch & {
  __typename?: 'RegexMatchMatch';
  sampleArg?: Maybe<Scalars['String']>;
  userArg?: Maybe<Scalars['String']>;
  matchType: MatchType;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
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

export type RegisterValues = {
  username: Scalars['String'];
  firstPassword: Scalars['String'];
  secondPassword: Scalars['String'];
};

export type SelectAdditionalComparisons = {
  __typename?: 'SelectAdditionalComparisons';
  groupByComparison: StringMatchingResult;
  orderByComparison: StringMatchingResult;
  limitComparison: SqlLimitComparisonMatchingResult;
};

export type SqlBinaryExpressionComparisonMatchingResult = MatchingResult & {
  __typename?: 'SqlBinaryExpressionComparisonMatchingResult';
  allMatches: Array<SqlBinaryExpressionMatch>;
  notMatchedForUser: Array<Scalars['String']>;
  notMatchedForSample: Array<Scalars['String']>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
  notMatchedForUserString: Array<Scalars['String']>;
  notMatchedForSampleString: Array<Scalars['String']>;
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
  allMatches: Array<SqlColumnMatch>;
  notMatchedForUser: Array<Scalars['String']>;
  notMatchedForSample: Array<Scalars['String']>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
  notMatchedForUserString: Array<Scalars['String']>;
  notMatchedForSampleString: Array<Scalars['String']>;
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
  result: SqlResult;
};

export type SqlExecutionResult = {
  __typename?: 'SqlExecutionResult';
  userResult?: Maybe<SqlQueryResult>;
  sampleResult?: Maybe<SqlQueryResult>;
};

export type SqlExerciseMutations = {
  __typename?: 'SqlExerciseMutations';
  correct: SqlCorrectionResult;
};


export type SqlExerciseMutationsCorrectArgs = {
  part: SqlExPart;
  solution: Scalars['String'];
};

export type SqlLimitComparisonMatchingResult = MatchingResult & {
  __typename?: 'SqlLimitComparisonMatchingResult';
  allMatches: Array<SqlLimitMatch>;
  notMatchedForUser: Array<Scalars['String']>;
  notMatchedForSample: Array<Scalars['String']>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
  notMatchedForUserString: Array<Scalars['String']>;
  notMatchedForSampleString: Array<Scalars['String']>;
};

export type SqlLimitMatch = NewMatch & {
  __typename?: 'SqlLimitMatch';
  sampleArg?: Maybe<Scalars['String']>;
  userArg?: Maybe<Scalars['String']>;
  matchType: MatchType;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type SqlQueriesStaticComparison = {
  __typename?: 'SqlQueriesStaticComparison';
  columnComparison: SqlColumnComparisonMatchingResult;
  tableComparison: StringMatchingResult;
  joinExpressionComparison: SqlBinaryExpressionComparisonMatchingResult;
  whereComparison: SqlBinaryExpressionComparisonMatchingResult;
  additionalComparisons: AdditionalComparison;
};

export type SqlResult = {
  __typename?: 'SqlResult';
  staticComparison: SqlQueriesStaticComparison;
  executionResult: SqlExecutionResult;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type StringMatch = {
  __typename?: 'StringMatch';
  matchType: MatchType;
  userArg: Scalars['String'];
  sampleArg: Scalars['String'];
};

export type StringMatchingResult = {
  __typename?: 'StringMatchingResult';
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
  allMatches: Array<StringMatch>;
  notMatchedForUser: Array<Scalars['String']>;
  notMatchedForSample: Array<Scalars['String']>;
};

export enum SuccessType {
  Error = 'ERROR',
  None = 'NONE',
  Partially = 'PARTIALLY',
  Complete = 'COMPLETE'
}

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
  userArg: UmlAssociation;
  sampleArg: UmlAssociation;
  analysisResult: UmlAssociationAnalysisResult;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type UmlAssociationMatchingResult = MatchingResult & {
  __typename?: 'UmlAssociationMatchingResult';
  allMatches: Array<UmlAssociationMatch>;
  notMatchedForUser: Array<UmlAssociation>;
  notMatchedForSample: Array<UmlAssociation>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
  notMatchedForUserString: Array<Scalars['String']>;
  notMatchedForSampleString: Array<Scalars['String']>;
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
  userArg: UmlAttribute;
  sampleArg: UmlAttribute;
  maybeAnalysisResult: UmlAttributeAnalysisResult;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type UmlAttributeMatchingResult = MatchingResult & {
  __typename?: 'UmlAttributeMatchingResult';
  allMatches: Array<UmlAttributeMatch>;
  notMatchedForUser: Array<UmlAttribute>;
  notMatchedForSample: Array<UmlAttribute>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
  notMatchedForUserString: Array<Scalars['String']>;
  notMatchedForSampleString: Array<Scalars['String']>;
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
  userArg: UmlClass;
  sampleArg: UmlClass;
  compAM: Scalars['Boolean'];
  analysisResult: UmlClassMatchAnalysisResult;
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
  notMatchedForUser: Array<UmlClass>;
  notMatchedForSample: Array<UmlClass>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
  notMatchedForUserString: Array<Scalars['String']>;
  notMatchedForSampleString: Array<Scalars['String']>;
};

export type UmlCorrectionResult = {
  __typename?: 'UmlCorrectionResult';
  solutionSaved: Scalars['Boolean'];
  resultSaved: Scalars['Boolean'];
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: UmlResult;
};

export type UmlExerciseMutations = {
  __typename?: 'UmlExerciseMutations';
  correct: UmlCorrectionResult;
};


export type UmlExerciseMutationsCorrectArgs = {
  part: UmlExPart;
  solution: UmlClassDiagramInput;
};

export type UmlImplementationInput = {
  subClass: Scalars['String'];
  superClass: Scalars['String'];
};

export type UmlImplementationMatch = NewMatch & {
  __typename?: 'UmlImplementationMatch';
  matchType: MatchType;
  userArg: UmlImplementation;
  sampleArg: UmlImplementation;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type UmlImplementationMatchingResult = MatchingResult & {
  __typename?: 'UmlImplementationMatchingResult';
  allMatches: Array<UmlImplementationMatch>;
  notMatchedForUser: Array<UmlImplementation>;
  notMatchedForSample: Array<UmlImplementation>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
  notMatchedForUserString: Array<Scalars['String']>;
  notMatchedForSampleString: Array<Scalars['String']>;
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
  userArg: UmlMethod;
  sampleArg: UmlMethod;
  maybeAnalysisResult: UmlMethodAnalysisResult;
  userArgDescription?: Maybe<Scalars['String']>;
  sampleArgDescription?: Maybe<Scalars['String']>;
};

export type UmlMethodMatchingResult = MatchingResult & {
  __typename?: 'UmlMethodMatchingResult';
  allMatches: Array<UmlMethodMatch>;
  notMatchedForUser: Array<UmlMethod>;
  notMatchedForSample: Array<UmlMethod>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
  notMatchedForUserString: Array<Scalars['String']>;
  notMatchedForSampleString: Array<Scalars['String']>;
};

export type UmlResult = {
  __typename?: 'UmlResult';
  classResult?: Maybe<UmlClassMatchingResult>;
  assocResult?: Maybe<UmlAssociationMatchingResult>;
  implResult?: Maybe<UmlImplementationMatchingResult>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type UnitTestCorrectionResult = ProgrammingTestCorrectionResult & {
  __typename?: 'UnitTestCorrectionResult';
  testId: Scalars['Int'];
  description: Scalars['String'];
  shouldFail: Scalars['Boolean'];
  testSuccessful: Scalars['Boolean'];
  stdout: Array<Scalars['String']>;
  stderr: Array<Scalars['String']>;
  successful: Scalars['Boolean'];
};

export type UserCredentials = {
  username: Scalars['String'];
  password: Scalars['String'];
};

export type UserMutations = {
  __typename?: 'UserMutations';
  ebnfExercise?: Maybe<EbnfExerciseMutations>;
  flaskExercise?: Maybe<FlaskExerciseMutations>;
  programmingExercise?: Maybe<ProgrammingExerciseMutations>;
  regexExercise?: Maybe<RegexExerciseMutations>;
  sqlExercise?: Maybe<SqlExerciseMutations>;
  umlExercise?: Maybe<UmlExerciseMutations>;
  webExercise?: Maybe<WebExerciseMutations>;
  xmlExercise?: Maybe<XmlExerciseMutations>;
};


export type UserMutationsEbnfExerciseArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
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

export type WebCorrectionResult = {
  __typename?: 'WebCorrectionResult';
  solutionSaved: Scalars['Boolean'];
  resultSaved: Scalars['Boolean'];
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: WebResult;
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
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type XmlCorrectionResult = {
  __typename?: 'XmlCorrectionResult';
  solutionSaved: Scalars['Boolean'];
  resultSaved: Scalars['Boolean'];
  proficienciesUpdated?: Maybe<Scalars['Boolean']>;
  result: XmlResult;
};

export type XmlDocumentResult = {
  __typename?: 'XmlDocumentResult';
  errors: Array<XmlError>;
};

export type XmlElementLineComparisonMatchingResult = MatchingResult & {
  __typename?: 'XmlElementLineComparisonMatchingResult';
  allMatches: Array<ElementLineMatch>;
  notMatchedForUser: Array<ElementLine>;
  notMatchedForSample: Array<ElementLine>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
  notMatchedForUserString: Array<Scalars['String']>;
  notMatchedForSampleString: Array<Scalars['String']>;
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
  successType: SuccessType;
  documentResult?: Maybe<XmlDocumentResult>;
  grammarResult?: Maybe<XmlGrammarResult>;
  points: Scalars['Float'];
  maxPoints: Scalars['Float'];
};

export type XmlSolutionInput = {
  document: Scalars['String'];
  grammar: Scalars['String'];
};

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

export type EbnfExerciseContent = {
  __typename?: 'EbnfExerciseContent';
  sampleSolutions: Array<Scalars['String']>;
};

export type ExPart = {
  __typename?: 'ExPart';
  id: Scalars['String'];
  name: Scalars['String'];
  isEntryPart: Scalars['Boolean'];
  solved: Scalars['Boolean'];
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
  content: ExerciseContentUnionType;
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

export type ExerciseContentUnionType = EbnfExerciseContent | FlaskExerciseContent | ProgrammingExerciseContent | RegexExerciseContent | SqlExerciseContent | UmlExerciseContent | WebExerciseContent | XmlExerciseContent;

export type ExerciseFile = {
  __typename?: 'ExerciseFile';
  name: Scalars['String'];
  fileType: Scalars['String'];
  editable: Scalars['Boolean'];
  content: Scalars['String'];
};

export type FilesSolution = {
  __typename?: 'FilesSolution';
  files: Array<ExerciseFile>;
};

export type FlaskExerciseContent = {
  __typename?: 'FlaskExerciseContent';
  files: Array<ExerciseFile>;
  testFiles: Array<ExerciseFile>;
  testConfig: FlaskTestsConfig;
  sampleSolutions: Array<FilesSolution>;
};

export type FlaskSingleTestConfig = {
  __typename?: 'FlaskSingleTestConfig';
  id: Scalars['Int'];
  description: Scalars['String'];
  maxPoints: Scalars['Int'];
  testName: Scalars['String'];
  dependencies?: Maybe<Array<Scalars['String']>>;
};

export type FlaskTestsConfig = {
  __typename?: 'FlaskTestsConfig';
  testFileName: Scalars['String'];
  testClassName: Scalars['String'];
  tests: Array<FlaskSingleTestConfig>;
};

export type HtmlTask = {
  __typename?: 'HtmlTask';
  text: Scalars['String'];
};

export type ImplementationPart = {
  __typename?: 'ImplementationPart';
  files: Array<ExerciseFile>;
  implFileName: Scalars['String'];
};

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

export enum ProgExPart {
  TestCreation = 'TestCreation',
  Implementation = 'Implementation',
  ActivityDiagram = 'ActivityDiagram'
}

export type ProgrammingExerciseContent = {
  __typename?: 'ProgrammingExerciseContent';
  filename: Scalars['String'];
  unitTestPart: UnitTestPart;
  implementationPart: ImplementationPart;
  sampleSolutions: Array<FilesSolution>;
  part?: Maybe<ProgExPart>;
};


export type ProgrammingExerciseContentPartArgs = {
  partId: Scalars['String'];
};

export type Query = {
  __typename?: 'Query';
  me?: Maybe<User>;
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
  maxPoints: Scalars['Int'];
  correctionType: RegexCorrectionType;
  matchTestData: Array<RegexMatchTestData>;
  extractionTestData: Array<RegexExtractionTestData>;
  sampleSolutions: Array<Scalars['String']>;
  part?: Maybe<RegexExPart>;
};


export type RegexExerciseContentPartArgs = {
  partId: Scalars['String'];
};

export type RegexExtractionTestData = {
  __typename?: 'RegexExtractionTestData';
  id: Scalars['Int'];
  base: Scalars['String'];
};

export type RegexMatchTestData = {
  __typename?: 'RegexMatchTestData';
  id: Scalars['Int'];
  data: Scalars['String'];
  isIncluded: Scalars['Boolean'];
};

export type SiteSpec = {
  __typename?: 'SiteSpec';
  fileName: Scalars['String'];
  htmlTasks: Array<HtmlTask>;
  htmlTaskCount: Scalars['Int'];
  jsTaskCount: Scalars['Int'];
};

export type SqlCell = {
  __typename?: 'SqlCell';
  colName: Scalars['String'];
  content?: Maybe<Scalars['String']>;
  different: Scalars['Boolean'];
};

export enum SqlExPart {
  SqlSingleExPart = 'SqlSingleExPart'
}

export type SqlExerciseContent = {
  __typename?: 'SqlExerciseContent';
  exerciseType: SqlExerciseType;
  schemaName: Scalars['String'];
  sampleSolutions: Array<Scalars['String']>;
  hint?: Maybe<Scalars['String']>;
  part?: Maybe<SqlExPart>;
  sqlDbContents: Array<SqlQueryResult>;
};


export type SqlExerciseContentPartArgs = {
  partId: Scalars['String'];
};

export enum SqlExerciseType {
  Insert = 'INSERT',
  Update = 'UPDATE',
  Create = 'CREATE',
  Select = 'SELECT',
  Delete = 'DELETE'
}

export type SqlKeyCellValueObject = {
  __typename?: 'SqlKeyCellValueObject';
  key: Scalars['String'];
  value: SqlCell;
};

export type SqlQueryResult = {
  __typename?: 'SqlQueryResult';
  columnNames: Array<Scalars['String']>;
  rows: Array<SqlRow>;
  tableName: Scalars['String'];
};

export type SqlRow = {
  __typename?: 'SqlRow';
  cells: Array<SqlKeyCellValueObject>;
};

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

export type UmlAssociation = {
  __typename?: 'UmlAssociation';
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

export enum UmlClassType {
  Abstract = 'ABSTRACT',
  Class = 'CLASS',
  Interface = 'INTERFACE'
}

export enum UmlExPart {
  ClassSelection = 'ClassSelection',
  DiagramDrawingHelp = 'DiagramDrawingHelp',
  DiagramDrawing = 'DiagramDrawing',
  MemberAllocation = 'MemberAllocation'
}

export type UmlExerciseContent = {
  __typename?: 'UmlExerciseContent';
  toIgnore: Array<Scalars['String']>;
  sampleSolutions: Array<UmlClassDiagram>;
  part?: Maybe<UmlExPart>;
  mappings: Array<KeyValueObject>;
};


export type UmlExerciseContentPartArgs = {
  partId: Scalars['String'];
};

export type UmlImplementation = {
  __typename?: 'UmlImplementation';
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

export enum UmlMultiplicity {
  Single = 'SINGLE',
  Unbound = 'UNBOUND'
}

export enum UmlVisibility {
  Public = 'PUBLIC',
  Package = 'PACKAGE',
  Protected = 'PROTECTED',
  Private = 'PRIVATE'
}

export type UnitTestPart = {
  __typename?: 'UnitTestPart';
  unitTestsDescription: Scalars['String'];
  unitTestFiles: Array<ExerciseFile>;
  unitTestTestConfigs: Array<UnitTestTestConfig>;
  testFileName: Scalars['String'];
  folderName: Scalars['String'];
};

export type UnitTestTestConfig = {
  __typename?: 'UnitTestTestConfig';
  id: Scalars['Int'];
  description: Scalars['String'];
  file: ExerciseFile;
  shouldFail: Scalars['Boolean'];
};

export type User = {
  __typename?: 'User';
  tools: Array<CollectionTool>;
  tool?: Maybe<CollectionTool>;
};


export type UserToolArgs = {
  toolId: Scalars['String'];
};

export type UserProficiency = {
  __typename?: 'UserProficiency';
  username: Scalars['String'];
  topic: Topic;
  points: Scalars['Int'];
  pointsForNextLevel: Scalars['Int'];
  level: Level;
};

export enum WebExPart {
  HtmlPart = 'HtmlPart',
  JsPart = 'JsPart'
}

export type WebExerciseContent = {
  __typename?: 'WebExerciseContent';
  siteSpec: SiteSpec;
  files: Array<ExerciseFile>;
  sampleSolutions: Array<FilesSolution>;
  htmlText?: Maybe<Scalars['String']>;
  jsText?: Maybe<Scalars['String']>;
  part?: Maybe<WebExPart>;
};


export type WebExerciseContentPartArgs = {
  partId: Scalars['String'];
};

export enum XmlExPart {
  GrammarCreationXmlPart = 'GrammarCreationXmlPart',
  DocumentCreationXmlPart = 'DocumentCreationXmlPart'
}

export type XmlExerciseContent = {
  __typename?: 'XmlExerciseContent';
  grammarDescription: Scalars['String'];
  rootNode: Scalars['String'];
  sampleSolutions: Array<XmlSolution>;
  part?: Maybe<XmlExPart>;
};


export type XmlExerciseContentPartArgs = {
  partId: Scalars['String'];
};

export type XmlSolution = {
  __typename?: 'XmlSolution';
  document: Scalars['String'];
  grammar: Scalars['String'];
};


export type FlaskCorrectionMutationVariables = Exact<{
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: FlaskExercisePart;
  solution: FilesSolutionInput;
}>;


export type FlaskCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { me?: Maybe<(
    { __typename?: 'UserMutations' }
    & { flaskExercise?: Maybe<(
      { __typename?: 'FlaskExerciseMutations' }
      & { correct: (
        { __typename?: 'FlaskCorrectionResult' }
        & FlaskCorrectionResultFragment
      ) }
    )> }
  )> }
);

export type FlaskCorrectionResultFragment = (
  { __typename?: 'FlaskCorrectionResult' }
  & Pick<FlaskCorrectionResult, 'solutionSaved' | 'resultSaved' | 'proficienciesUpdated'>
  & { result: (
    { __typename?: 'FlaskResult' }
    & FlaskResultFragment
  ) }
);

export type FlaskResultFragment = (
  { __typename?: 'FlaskResult' }
  & Pick<FlaskResult, 'points' | 'maxPoints'>
  & { testResults: Array<(
    { __typename?: 'FlaskTestResult' }
    & FlaskTestResultFragment
  )> }
);

export type FlaskTestResultFragment = (
  { __typename?: 'FlaskTestResult' }
  & Pick<FlaskTestResult, 'testName' | 'successful' | 'stdout' | 'stderr'>
);

export type ProgrammingCorrectionMutationVariables = Exact<{
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: ProgExPart;
  solution: FilesSolutionInput;
}>;


export type ProgrammingCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { me?: Maybe<(
    { __typename?: 'UserMutations' }
    & { programmingExercise?: Maybe<(
      { __typename?: 'ProgrammingExerciseMutations' }
      & { correct: (
        { __typename?: 'ProgrammingCorrectionResult' }
        & ProgrammingCorrectionResultFragment
      ) }
    )> }
  )> }
);

export type ProgrammingCorrectionResultFragment = (
  { __typename: 'ProgrammingCorrectionResult' }
  & Pick<ProgrammingCorrectionResult, 'solutionSaved' | 'resultSaved' | 'proficienciesUpdated'>
  & { result: (
    { __typename?: 'ProgrammingResult' }
    & ProgrammingResultFragment
  ) }
);

export type ProgrammingResultFragment = (
  { __typename?: 'ProgrammingResult' }
  & Pick<ProgrammingResult, 'points' | 'maxPoints'>
  & { implementationCorrectionResult?: Maybe<(
    { __typename?: 'ImplementationCorrectionResult' }
    & ImplementationCorrectionResultFragment
  )>, unitTestResults: Array<(
    { __typename?: 'UnitTestCorrectionResult' }
    & UnitTestCorrectionResultFragment
  )> }
);

export type ImplementationCorrectionResultFragment = (
  { __typename?: 'ImplementationCorrectionResult' }
  & Pick<ImplementationCorrectionResult, 'successful' | 'stdout' | 'stderr'>
);

export type UnitTestCorrectionResultFragment = (
  { __typename?: 'UnitTestCorrectionResult' }
  & Pick<UnitTestCorrectionResult, 'testId' | 'successful' | 'shouldFail' | 'description' | 'stderr'>
);

export type RegexCorrectionMutationVariables = Exact<{
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: RegexExPart;
  solution: Scalars['String'];
}>;


export type RegexCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { me?: Maybe<(
    { __typename?: 'UserMutations' }
    & { regexExercise?: Maybe<(
      { __typename?: 'RegexExerciseMutations' }
      & { correct: (
        { __typename?: 'RegexCorrectionResult' }
        & RegexCorrectionResultFragment
      ) }
    )> }
  )> }
);

export type RegexCorrectionResultFragment = (
  { __typename?: 'RegexCorrectionResult' }
  & Pick<RegexCorrectionResult, 'solutionSaved' | 'resultSaved' | 'proficienciesUpdated'>
  & { result: (
    { __typename?: 'RegexExtractionResult' }
    & RegexAbstractResult_RegexExtractionResult_Fragment
    & RegexExtractionResultFragment
  ) | (
    { __typename?: 'RegexMatchingResult' }
    & RegexAbstractResult_RegexMatchingResult_Fragment
    & RegexMatchingResultFragment
  ) }
);

type RegexAbstractResult_RegexExtractionResult_Fragment = (
  { __typename: 'RegexExtractionResult' }
  & Pick<RegexExtractionResult, 'points' | 'maxPoints'>
);

type RegexAbstractResult_RegexMatchingResult_Fragment = (
  { __typename: 'RegexMatchingResult' }
  & Pick<RegexMatchingResult, 'points' | 'maxPoints'>
);

export type RegexAbstractResultFragment = RegexAbstractResult_RegexExtractionResult_Fragment | RegexAbstractResult_RegexMatchingResult_Fragment;

export type RegexMatchingSingleResultFragment = (
  { __typename?: 'RegexMatchingSingleResult' }
  & Pick<RegexMatchingSingleResult, 'resultType' | 'matchData'>
);

export type RegexMatchingResultFragment = (
  { __typename?: 'RegexMatchingResult' }
  & { matchingResults: Array<(
    { __typename?: 'RegexMatchingSingleResult' }
    & RegexMatchingSingleResultFragment
  )> }
);

export type RegexExtractionMatchFragment = (
  { __typename?: 'RegexMatchMatch' }
  & Pick<RegexMatchMatch, 'matchType' | 'userArg' | 'sampleArg'>
);

export type ExtractionMatchingResultFragment = (
  { __typename?: 'RegexExtractedValuesComparisonMatchingResult' }
  & Pick<RegexExtractedValuesComparisonMatchingResult, 'notMatchedForUser' | 'notMatchedForSample' | 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'RegexMatchMatch' }
    & RegexExtractionMatchFragment
  )> }
);

export type RegexExtractionSingleResultFragment = (
  { __typename?: 'RegexExtractionSingleResult' }
  & Pick<RegexExtractionSingleResult, 'base'>
  & { extractionMatchingResult: (
    { __typename?: 'RegexExtractedValuesComparisonMatchingResult' }
    & ExtractionMatchingResultFragment
  ) }
);

export type RegexExtractionResultFragment = (
  { __typename?: 'RegexExtractionResult' }
  & { extractionResults: Array<(
    { __typename?: 'RegexExtractionSingleResult' }
    & RegexExtractionSingleResultFragment
  )> }
);

export type SqlCorrectionMutationVariables = Exact<{
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: SqlExPart;
  solution: Scalars['String'];
}>;


export type SqlCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { me?: Maybe<(
    { __typename?: 'UserMutations' }
    & { sqlExercise?: Maybe<(
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
  & Pick<SqlCorrectionResult, 'solutionSaved' | 'resultSaved' | 'proficienciesUpdated'>
  & { result: (
    { __typename?: 'SqlResult' }
    & SqlResultFragment
  ) }
);

export type ColumnComparisonFragment = (
  { __typename?: 'SqlColumnComparisonMatchingResult' }
  & SqlMatchingResult_SqlColumnComparisonMatchingResult_Fragment
);

export type BinaryExpressionComparisonFragment = (
  { __typename?: 'SqlBinaryExpressionComparisonMatchingResult' }
  & SqlMatchingResult_SqlBinaryExpressionComparisonMatchingResult_Fragment
);

export type LimitComparisonFragment = (
  { __typename?: 'SqlLimitComparisonMatchingResult' }
  & SqlMatchingResult_SqlLimitComparisonMatchingResult_Fragment
);

export type SelectAdditionalComparisonFragment = (
  { __typename?: 'SelectAdditionalComparisons' }
  & { groupByComparison: (
    { __typename?: 'StringMatchingResult' }
    & StringMatchingResultFragment
  ), orderByComparison: (
    { __typename?: 'StringMatchingResult' }
    & StringMatchingResultFragment
  ), limitComparison: (
    { __typename?: 'SqlLimitComparisonMatchingResult' }
    & LimitComparisonFragment
  ) }
);

export type SqlResultFragment = (
  { __typename?: 'SqlResult' }
  & Pick<SqlResult, 'points' | 'maxPoints'>
  & { staticComparison: (
    { __typename?: 'SqlQueriesStaticComparison' }
    & { columnComparison: (
      { __typename?: 'SqlColumnComparisonMatchingResult' }
      & ColumnComparisonFragment
    ), tableComparison: (
      { __typename?: 'StringMatchingResult' }
      & StringMatchingResultFragment
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
        { __typename?: 'StringMatchingResult' }
        & StringMatchingResultFragment
      )> }
    ) }
  ), executionResult: (
    { __typename?: 'SqlExecutionResult' }
    & SqlExecutionResultFragment
  ) }
);

export type SqlExecutionResultFragment = (
  { __typename?: 'SqlExecutionResult' }
  & { userResult?: Maybe<(
    { __typename?: 'SqlQueryResult' }
    & SqlQueryResultFragment
  )>, sampleResult?: Maybe<(
    { __typename?: 'SqlQueryResult' }
    & SqlQueryResultFragment
  )> }
);

export type SqlQueryResultFragment = (
  { __typename?: 'SqlQueryResult' }
  & Pick<SqlQueryResult, 'tableName' | 'columnNames'>
  & { rows: Array<(
    { __typename?: 'SqlRow' }
    & SqlRowFragment
  )> }
);

export type SqlRowFragment = (
  { __typename?: 'SqlRow' }
  & { cells: Array<(
    { __typename?: 'SqlKeyCellValueObject' }
    & Pick<SqlKeyCellValueObject, 'key'>
    & { value: (
      { __typename?: 'SqlCell' }
      & SqlCellFragment
    ) }
  )> }
);

export type SqlCellFragment = (
  { __typename?: 'SqlCell' }
  & Pick<SqlCell, 'colName' | 'content' | 'different'>
);

export type StringMatchFragment = (
  { __typename?: 'StringMatch' }
  & Pick<StringMatch, 'matchType' | 'sampleArg' | 'userArg'>
);

export type StringMatchingResultFragment = (
  { __typename?: 'StringMatchingResult' }
  & Pick<StringMatchingResult, 'points' | 'maxPoints' | 'notMatchedForUser' | 'notMatchedForSample'>
  & { allMatches: Array<(
    { __typename?: 'StringMatch' }
    & StringMatchFragment
  )> }
);

type NewMatch_ElementLineMatch_Fragment = (
  { __typename?: 'ElementLineMatch' }
  & Pick<ElementLineMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_RegexMatchMatch_Fragment = (
  { __typename?: 'RegexMatchMatch' }
  & Pick<RegexMatchMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_SqlBinaryExpressionMatch_Fragment = (
  { __typename?: 'SqlBinaryExpressionMatch' }
  & Pick<SqlBinaryExpressionMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_SqlColumnMatch_Fragment = (
  { __typename?: 'SqlColumnMatch' }
  & Pick<SqlColumnMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_SqlLimitMatch_Fragment = (
  { __typename?: 'SqlLimitMatch' }
  & Pick<SqlLimitMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_UmlAssociationMatch_Fragment = (
  { __typename?: 'UmlAssociationMatch' }
  & Pick<UmlAssociationMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_UmlAttributeMatch_Fragment = (
  { __typename?: 'UmlAttributeMatch' }
  & Pick<UmlAttributeMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_UmlClassMatch_Fragment = (
  { __typename?: 'UmlClassMatch' }
  & Pick<UmlClassMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_UmlImplementationMatch_Fragment = (
  { __typename?: 'UmlImplementationMatch' }
  & Pick<UmlImplementationMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

type NewMatch_UmlMethodMatch_Fragment = (
  { __typename?: 'UmlMethodMatch' }
  & Pick<UmlMethodMatch, 'matchType' | 'sampleArgDescription' | 'userArgDescription'>
);

export type NewMatchFragment = NewMatch_ElementLineMatch_Fragment | NewMatch_RegexMatchMatch_Fragment | NewMatch_SqlBinaryExpressionMatch_Fragment | NewMatch_SqlColumnMatch_Fragment | NewMatch_SqlLimitMatch_Fragment | NewMatch_UmlAssociationMatch_Fragment | NewMatch_UmlAttributeMatch_Fragment | NewMatch_UmlClassMatch_Fragment | NewMatch_UmlImplementationMatch_Fragment | NewMatch_UmlMethodMatch_Fragment;

type SqlMatchingResult_RegexExtractedValuesComparisonMatchingResult_Fragment = (
  { __typename?: 'RegexExtractedValuesComparisonMatchingResult' }
  & Pick<RegexExtractedValuesComparisonMatchingResult, 'points' | 'maxPoints' | 'notMatchedForUserString' | 'notMatchedForSampleString'>
  & { allMatches: Array<(
    { __typename?: 'RegexMatchMatch' }
    & NewMatch_RegexMatchMatch_Fragment
  )> }
);

type SqlMatchingResult_SqlBinaryExpressionComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlBinaryExpressionComparisonMatchingResult' }
  & Pick<SqlBinaryExpressionComparisonMatchingResult, 'points' | 'maxPoints' | 'notMatchedForUserString' | 'notMatchedForSampleString'>
  & { allMatches: Array<(
    { __typename?: 'SqlBinaryExpressionMatch' }
    & NewMatch_SqlBinaryExpressionMatch_Fragment
  )> }
);

type SqlMatchingResult_SqlColumnComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlColumnComparisonMatchingResult' }
  & Pick<SqlColumnComparisonMatchingResult, 'points' | 'maxPoints' | 'notMatchedForUserString' | 'notMatchedForSampleString'>
  & { allMatches: Array<(
    { __typename?: 'SqlColumnMatch' }
    & NewMatch_SqlColumnMatch_Fragment
  )> }
);

type SqlMatchingResult_SqlLimitComparisonMatchingResult_Fragment = (
  { __typename?: 'SqlLimitComparisonMatchingResult' }
  & Pick<SqlLimitComparisonMatchingResult, 'points' | 'maxPoints' | 'notMatchedForUserString' | 'notMatchedForSampleString'>
  & { allMatches: Array<(
    { __typename?: 'SqlLimitMatch' }
    & NewMatch_SqlLimitMatch_Fragment
  )> }
);

type SqlMatchingResult_UmlAssociationMatchingResult_Fragment = (
  { __typename?: 'UmlAssociationMatchingResult' }
  & Pick<UmlAssociationMatchingResult, 'points' | 'maxPoints' | 'notMatchedForUserString' | 'notMatchedForSampleString'>
  & { allMatches: Array<(
    { __typename?: 'UmlAssociationMatch' }
    & NewMatch_UmlAssociationMatch_Fragment
  )> }
);

type SqlMatchingResult_UmlAttributeMatchingResult_Fragment = (
  { __typename?: 'UmlAttributeMatchingResult' }
  & Pick<UmlAttributeMatchingResult, 'points' | 'maxPoints' | 'notMatchedForUserString' | 'notMatchedForSampleString'>
  & { allMatches: Array<(
    { __typename?: 'UmlAttributeMatch' }
    & NewMatch_UmlAttributeMatch_Fragment
  )> }
);

type SqlMatchingResult_UmlClassMatchingResult_Fragment = (
  { __typename?: 'UmlClassMatchingResult' }
  & Pick<UmlClassMatchingResult, 'points' | 'maxPoints' | 'notMatchedForUserString' | 'notMatchedForSampleString'>
  & { allMatches: Array<(
    { __typename?: 'UmlClassMatch' }
    & NewMatch_UmlClassMatch_Fragment
  )> }
);

type SqlMatchingResult_UmlImplementationMatchingResult_Fragment = (
  { __typename?: 'UmlImplementationMatchingResult' }
  & Pick<UmlImplementationMatchingResult, 'points' | 'maxPoints' | 'notMatchedForUserString' | 'notMatchedForSampleString'>
  & { allMatches: Array<(
    { __typename?: 'UmlImplementationMatch' }
    & NewMatch_UmlImplementationMatch_Fragment
  )> }
);

type SqlMatchingResult_UmlMethodMatchingResult_Fragment = (
  { __typename?: 'UmlMethodMatchingResult' }
  & Pick<UmlMethodMatchingResult, 'points' | 'maxPoints' | 'notMatchedForUserString' | 'notMatchedForSampleString'>
  & { allMatches: Array<(
    { __typename?: 'UmlMethodMatch' }
    & NewMatch_UmlMethodMatch_Fragment
  )> }
);

type SqlMatchingResult_XmlElementLineComparisonMatchingResult_Fragment = (
  { __typename?: 'XmlElementLineComparisonMatchingResult' }
  & Pick<XmlElementLineComparisonMatchingResult, 'points' | 'maxPoints' | 'notMatchedForUserString' | 'notMatchedForSampleString'>
  & { allMatches: Array<(
    { __typename?: 'ElementLineMatch' }
    & NewMatch_ElementLineMatch_Fragment
  )> }
);

export type SqlMatchingResultFragment = SqlMatchingResult_RegexExtractedValuesComparisonMatchingResult_Fragment | SqlMatchingResult_SqlBinaryExpressionComparisonMatchingResult_Fragment | SqlMatchingResult_SqlColumnComparisonMatchingResult_Fragment | SqlMatchingResult_SqlLimitComparisonMatchingResult_Fragment | SqlMatchingResult_UmlAssociationMatchingResult_Fragment | SqlMatchingResult_UmlAttributeMatchingResult_Fragment | SqlMatchingResult_UmlClassMatchingResult_Fragment | SqlMatchingResult_UmlImplementationMatchingResult_Fragment | SqlMatchingResult_UmlMethodMatchingResult_Fragment | SqlMatchingResult_XmlElementLineComparisonMatchingResult_Fragment;

export type UmlCorrectionMutationVariables = Exact<{
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: UmlExPart;
  solution: UmlClassDiagramInput;
}>;


export type UmlCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { me?: Maybe<(
    { __typename?: 'UserMutations' }
    & { umlExercise?: Maybe<(
      { __typename?: 'UmlExerciseMutations' }
      & { correct: (
        { __typename?: 'UmlCorrectionResult' }
        & UmlCorrectionResultFragment
      ) }
    )> }
  )> }
);

export type UmlCorrectionResultFragment = (
  { __typename?: 'UmlCorrectionResult' }
  & Pick<UmlCorrectionResult, 'solutionSaved' | 'resultSaved' | 'proficienciesUpdated'>
  & { result: (
    { __typename?: 'UmlResult' }
    & UmlResultFragment
  ) }
);

export type UmlResultFragment = (
  { __typename?: 'UmlResult' }
  & Pick<UmlResult, 'points' | 'maxPoints'>
  & { classResult?: Maybe<(
    { __typename?: 'UmlClassMatchingResult' }
    & UmlClassMatchingResultFragment
  )>, assocResult?: Maybe<(
    { __typename?: 'UmlAssociationMatchingResult' }
    & UmlAssociationMatchingResultFragment
  )>, implResult?: Maybe<(
    { __typename?: 'UmlImplementationMatchingResult' }
    & UmlImplementationMatchingResultFragment
  )> }
);

export type UmlClassMatchingResultFragment = (
  { __typename?: 'UmlClassMatchingResult' }
  & Pick<UmlClassMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'UmlClassMatch' }
    & UmlClassMatchFragment
  )>, notMatchedForUser: Array<(
    { __typename?: 'UmlClass' }
    & UmlSolutionClassFragment
  )>, notMatchedForSample: Array<(
    { __typename?: 'UmlClass' }
    & UmlSolutionClassFragment
  )> }
);

export type UmlClassMatchFragment = (
  { __typename?: 'UmlClassMatch' }
  & Pick<UmlClassMatch, 'matchType'>
  & { userArg: (
    { __typename?: 'UmlClass' }
    & UmlSolutionClassFragment
  ), sampleArg: (
    { __typename?: 'UmlClass' }
    & UmlSolutionClassFragment
  ), analysisResult: { __typename: 'UmlClassMatchAnalysisResult' } }
);

export type UmlSolutionClassFragment = (
  { __typename?: 'UmlClass' }
  & Pick<UmlClass, 'classType' | 'name'>
  & { attributes: Array<{ __typename: 'UmlAttribute' }>, methods: Array<{ __typename: 'UmlMethod' }> }
);

export type UmlAssociationMatchingResultFragment = (
  { __typename?: 'UmlAssociationMatchingResult' }
  & Pick<UmlAssociationMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'UmlAssociationMatch' }
    & UmlAssociationMatchFragment
  )>, notMatchedForUser: Array<(
    { __typename?: 'UmlAssociation' }
    & UmlAssociationFragment
  )>, notMatchedForSample: Array<(
    { __typename?: 'UmlAssociation' }
    & UmlAssociationFragment
  )> }
);

export type UmlAssociationMatchFragment = (
  { __typename?: 'UmlAssociationMatch' }
  & Pick<UmlAssociationMatch, 'matchType'>
  & { userArg: (
    { __typename?: 'UmlAssociation' }
    & UmlAssociationFragment
  ), sampleArg: (
    { __typename?: 'UmlAssociation' }
    & UmlAssociationFragment
  ), analysisResult: (
    { __typename?: 'UmlAssociationAnalysisResult' }
    & Pick<UmlAssociationAnalysisResult, 'assocTypeEqual' | 'correctAssocType' | 'multiplicitiesEqual'>
  ) }
);

export type UmlAssociationFragment = (
  { __typename?: 'UmlAssociation' }
  & Pick<UmlAssociation, 'assocType' | 'assocName' | 'firstEnd' | 'firstMult' | 'secondEnd' | 'secondMult'>
);

export type UmlImplementationMatchingResultFragment = (
  { __typename?: 'UmlImplementationMatchingResult' }
  & Pick<UmlImplementationMatchingResult, 'points' | 'maxPoints'>
  & { allMatches: Array<(
    { __typename?: 'UmlImplementationMatch' }
    & UmlImplementationMatchFragment
  )>, notMatchedForUser: Array<(
    { __typename?: 'UmlImplementation' }
    & UmlImplementationFragment
  )>, notMatchedForSample: Array<(
    { __typename?: 'UmlImplementation' }
    & UmlImplementationFragment
  )> }
);

export type UmlImplementationMatchFragment = (
  { __typename?: 'UmlImplementationMatch' }
  & Pick<UmlImplementationMatch, 'matchType'>
  & { userArg: (
    { __typename?: 'UmlImplementation' }
    & UmlImplementationFragment
  ), sampleArg: (
    { __typename?: 'UmlImplementation' }
    & UmlImplementationFragment
  ) }
);

export type UmlImplementationFragment = (
  { __typename?: 'UmlImplementation' }
  & Pick<UmlImplementation, 'subClass' | 'superClass'>
);

export type WebCorrectionMutationVariables = Exact<{
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: WebExPart;
  solution: FilesSolutionInput;
}>;


export type WebCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { me?: Maybe<(
    { __typename?: 'UserMutations' }
    & { webExercise?: Maybe<(
      { __typename?: 'WebExerciseMutations' }
      & { correct: (
        { __typename?: 'WebCorrectionResult' }
        & WebCorrectionResultFragment
      ) }
    )> }
  )> }
);

export type WebCorrectionResultFragment = (
  { __typename?: 'WebCorrectionResult' }
  & Pick<WebCorrectionResult, 'solutionSaved' | 'resultSaved' | 'proficienciesUpdated'>
  & { result: (
    { __typename?: 'WebResult' }
    & WebResultFragment
  ) }
);

export type WebResultFragment = (
  { __typename?: 'WebResult' }
  & Pick<WebResult, 'points' | 'maxPoints'>
  & { gradedHtmlTaskResults: Array<(
    { __typename?: 'GradedHtmlTaskResult' }
    & GradedHtmlTaskResultFragment
  )>, gradedJsTaskResults: Array<(
    { __typename?: 'GradedJsTaskResult' }
    & GradedJsTaskResultFragment
  )> }
);

export type GradedHtmlTaskResultFragment = (
  { __typename?: 'GradedHtmlTaskResult' }
  & Pick<GradedHtmlTaskResult, 'id' | 'success' | 'elementFound' | 'isSuccessful' | 'points' | 'maxPoints'>
  & { textContentResult?: Maybe<(
    { __typename?: 'GradedTextResult' }
    & GradedTextContentResultFragment
  )>, attributeResults: Array<(
    { __typename?: 'GradedTextResult' }
    & GradedTextContentResultFragment
  )> }
);

export type GradedTextContentResultFragment = (
  { __typename?: 'GradedTextResult' }
  & Pick<GradedTextResult, 'keyName' | 'awaitedContent' | 'maybeFoundContent' | 'isSuccessful' | 'points' | 'maxPoints'>
);

export type GradedJsTaskResultFragment = (
  { __typename?: 'GradedJsTaskResult' }
  & Pick<GradedJsTaskResult, 'id' | 'success' | 'points' | 'maxPoints'>
  & { gradedPreResults: Array<(
    { __typename?: 'GradedJsHtmlElementSpecResult' }
    & GradedJsHtmlElementSpecResultFragment
  )>, gradedJsActionResult: (
    { __typename?: 'GradedJsActionResult' }
    & GradedJsActionResultFragment
  ), gradedPostResults: Array<(
    { __typename?: 'GradedJsHtmlElementSpecResult' }
    & GradedJsHtmlElementSpecResultFragment
  )> }
);

export type GradedJsHtmlElementSpecResultFragment = (
  { __typename?: 'GradedJsHtmlElementSpecResult' }
  & Pick<GradedJsHtmlElementSpecResult, 'id'>
);

export type GradedJsActionResultFragment = (
  { __typename?: 'GradedJsActionResult' }
  & Pick<GradedJsActionResult, 'actionPerformed' | 'points' | 'maxPoints'>
  & { jsAction: { __typename: 'JsAction' } }
);

export type XmlCorrectionMutationVariables = Exact<{
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  part: XmlExPart;
  solution: XmlSolutionInput;
}>;


export type XmlCorrectionMutation = (
  { __typename?: 'Mutation' }
  & { me?: Maybe<(
    { __typename?: 'UserMutations' }
    & { xmlExercise?: Maybe<(
      { __typename?: 'XmlExerciseMutations' }
      & { correct: (
        { __typename?: 'XmlCorrectionResult' }
        & XmlCorrectionResultFragment
      ) }
    )> }
  )> }
);

export type XmlCorrectionResultFragment = (
  { __typename?: 'XmlCorrectionResult' }
  & Pick<XmlCorrectionResult, 'solutionSaved' | 'resultSaved' | 'proficienciesUpdated'>
  & { result: (
    { __typename?: 'XmlResult' }
    & XmlResultFragment
  ) }
);

export type XmlResultFragment = (
  { __typename?: 'XmlResult' }
  & Pick<XmlResult, 'points' | 'maxPoints' | 'successType'>
  & { grammarResult?: Maybe<(
    { __typename?: 'XmlGrammarResult' }
    & XmlGrammarResultFragment
  )>, documentResult?: Maybe<(
    { __typename?: 'XmlDocumentResult' }
    & XmlDocumentResultFragment
  )> }
);

export type XmlGrammarResultFragment = (
  { __typename?: 'XmlGrammarResult' }
  & { parseErrors: Array<(
    { __typename?: 'DTDParseException' }
    & Pick<DtdParseException, 'msg' | 'parsedLine'>
  )>, results: (
    { __typename?: 'XmlElementLineComparisonMatchingResult' }
    & Pick<XmlElementLineComparisonMatchingResult, 'points' | 'maxPoints'>
    & XmlElementLineMatchingResultFragment
  ) }
);

export type XmlElementLineMatchingResultFragment = (
  { __typename?: 'XmlElementLineComparisonMatchingResult' }
  & { allMatches: Array<(
    { __typename?: 'ElementLineMatch' }
    & XmlElementLineMatchFragment
  )>, notMatchedForUser: Array<(
    { __typename?: 'ElementLine' }
    & ElementLineFragment
  )>, notMatchedForSample: Array<(
    { __typename?: 'ElementLine' }
    & ElementLineFragment
  )> }
);

export type XmlElementLineMatchFragment = (
  { __typename?: 'ElementLineMatch' }
  & Pick<ElementLineMatch, 'matchType'>
  & { userArg: (
    { __typename?: 'ElementLine' }
    & ElementLineFragment
  ), sampleArg: (
    { __typename?: 'ElementLine' }
    & ElementLineFragment
  ), analysisResult: (
    { __typename?: 'ElementLineAnalysisResult' }
    & XmlElementLineAnalysisResultFragment
  ) }
);

export type XmlElementLineAnalysisResultFragment = (
  { __typename?: 'ElementLineAnalysisResult' }
  & Pick<ElementLineAnalysisResult, 'attributesCorrect' | 'correctAttributes' | 'contentCorrect' | 'correctContent'>
);

export type ElementLineFragment = (
  { __typename?: 'ElementLine' }
  & Pick<ElementLine, 'elementName'>
  & { elementDefinition: (
    { __typename?: 'ElementDefinition' }
    & Pick<ElementDefinition, 'elementName' | 'content'>
  ), attributeLists: Array<(
    { __typename?: 'AttributeList' }
    & Pick<AttributeList, 'elementName' | 'attributeDefinitions'>
  )> }
);

export type XmlDocumentResultFragment = (
  { __typename?: 'XmlDocumentResult' }
  & { errors: Array<(
    { __typename?: 'XmlError' }
    & XmlErrorFragment
  )> }
);

export type XmlErrorFragment = (
  { __typename?: 'XmlError' }
  & Pick<XmlError, 'success' | 'line' | 'errorType' | 'errorMessage'>
);

export type LessonIdentifierFragment = (
  { __typename?: 'Lesson' }
  & Pick<Lesson, 'lessonId' | 'title' | 'description' | 'video'>
);

export type LessonsForToolQueryVariables = Exact<{
  toolId: Scalars['String'];
}>;


export type LessonsForToolQuery = (
  { __typename?: 'Query' }
  & { me?: Maybe<(
    { __typename?: 'User' }
    & { tool?: Maybe<(
      { __typename?: 'CollectionTool' }
      & Pick<CollectionTool, 'name'>
      & { lessons: Array<(
        { __typename?: 'Lesson' }
        & LessonIdentifierFragment
      )> }
    )> }
  )> }
);

export type LessonOverviewFragment = (
  { __typename?: 'Lesson' }
  & Pick<Lesson, 'title' | 'description' | 'video' | 'contentCount'>
);

export type LessonOverviewQueryVariables = Exact<{
  toolId: Scalars['String'];
  lessonId: Scalars['Int'];
}>;


export type LessonOverviewQuery = (
  { __typename?: 'Query' }
  & { me?: Maybe<(
    { __typename?: 'User' }
    & { tool?: Maybe<(
      { __typename?: 'CollectionTool' }
      & { lesson?: Maybe<(
        { __typename?: 'Lesson' }
        & LessonOverviewFragment
      )> }
    )> }
  )> }
);

export type LessonTextContentFragment = (
  { __typename: 'LessonTextContent' }
  & Pick<LessonTextContent, 'contentId' | 'content'>
);

export type LessonMultipleChoiceQuestionAnswerFragment = (
  { __typename?: 'LessonMultipleChoiceQuestionAnswer' }
  & Pick<LessonMultipleChoiceQuestionAnswer, 'id' | 'answer' | 'isCorrect'>
);

export type LessonMultipleChoiceQuestionFragment = (
  { __typename?: 'LessonMultipleChoiceQuestion' }
  & Pick<LessonMultipleChoiceQuestion, 'id' | 'questionText'>
  & { answers: Array<(
    { __typename?: 'LessonMultipleChoiceQuestionAnswer' }
    & LessonMultipleChoiceQuestionAnswerFragment
  )> }
);

export type LessonMultipleChoiceQuestionContentFragment = (
  { __typename: 'LessonMultipleChoiceQuestionsContent' }
  & Pick<LessonMultipleChoiceQuestionsContent, 'contentId'>
  & { questions: Array<(
    { __typename?: 'LessonMultipleChoiceQuestion' }
    & LessonMultipleChoiceQuestionFragment
  )> }
);

export type LessonAsTextFragment = (
  { __typename?: 'Lesson' }
  & Pick<Lesson, 'lessonId' | 'title' | 'description'>
  & { contents: Array<(
    { __typename?: 'LessonMultipleChoiceQuestionsContent' }
    & LessonMultipleChoiceQuestionContentFragment
  ) | (
    { __typename?: 'LessonTextContent' }
    & LessonTextContentFragment
  )> }
);

export type LessonAsTextQueryVariables = Exact<{
  toolId: Scalars['String'];
  lessonId: Scalars['Int'];
}>;


export type LessonAsTextQuery = (
  { __typename?: 'Query' }
  & { me?: Maybe<(
    { __typename?: 'User' }
    & { tool?: Maybe<(
      { __typename?: 'CollectionTool' }
      & { lesson?: Maybe<(
        { __typename?: 'Lesson' }
        & LessonAsTextFragment
      )> }
    )> }
  )> }
);

export type LessonAsVideoQueryVariables = Exact<{
  toolId: Scalars['String'];
  lessonId: Scalars['Int'];
}>;


export type LessonAsVideoQuery = (
  { __typename?: 'Query' }
  & { me?: Maybe<(
    { __typename?: 'User' }
    & { tool?: Maybe<(
      { __typename?: 'CollectionTool' }
      & { lesson?: Maybe<(
        { __typename?: 'Lesson' }
        & Pick<Lesson, 'title' | 'video'>
      )> }
    )> }
  )> }
);

export type CollectionToolFragment = (
  { __typename?: 'CollectionTool' }
  & Pick<CollectionTool, 'id' | 'name' | 'state' | 'collectionCount' | 'lessonCount' | 'exerciseCount'>
);

export type ToolOverviewQueryVariables = Exact<{ [key: string]: never; }>;


export type ToolOverviewQuery = (
  { __typename?: 'Query' }
  & { me?: Maybe<(
    { __typename?: 'User' }
    & { tools: Array<(
      { __typename?: 'CollectionTool' }
      & CollectionToolFragment
    )> }
  )> }
);

export type CollectionToolOverviewQueryVariables = Exact<{
  toolId: Scalars['String'];
}>;


export type CollectionToolOverviewQuery = (
  { __typename?: 'Query' }
  & { me?: Maybe<(
    { __typename?: 'User' }
    & { tool?: Maybe<(
      { __typename?: 'CollectionTool' }
      & Pick<CollectionTool, 'id' | 'name' | 'collectionCount' | 'exerciseCount' | 'lessonCount'>
      & { proficiencies: Array<(
        { __typename?: 'UserProficiency' }
        & UserProficiencyFragment
      )> }
    )> }
  )> }
);

export type UserProficiencyFragment = (
  { __typename?: 'UserProficiency' }
  & Pick<UserProficiency, 'points' | 'pointsForNextLevel'>
  & { topic: (
    { __typename?: 'Topic' }
    & TopicFragment
  ), level: (
    { __typename?: 'Level' }
    & LevelFragment
  ) }
);

export type AllExercisesOverviewQueryVariables = Exact<{
  toolId: Scalars['String'];
}>;


export type AllExercisesOverviewQuery = (
  { __typename?: 'Query' }
  & { me?: Maybe<(
    { __typename?: 'User' }
    & { tool?: Maybe<(
      { __typename?: 'CollectionTool' }
      & { allExercises: Array<(
        { __typename?: 'Exercise' }
        & { topicsWithLevels: Array<(
          { __typename?: 'TopicWithLevel' }
          & TopicWithLevelFragment
        )> }
        & FieldsForLinkFragment
      )> }
    )> }
  )> }
);

export type CollectionValuesFragment = (
  { __typename?: 'ExerciseCollection' }
  & Pick<ExerciseCollection, 'collectionId' | 'title' | 'exerciseCount'>
);

export type CollectionListQueryVariables = Exact<{
  toolId: Scalars['String'];
}>;


export type CollectionListQuery = (
  { __typename?: 'Query' }
  & { me?: Maybe<(
    { __typename?: 'User' }
    & { tool?: Maybe<(
      { __typename?: 'CollectionTool' }
      & Pick<CollectionTool, 'id' | 'name'>
      & { collections: Array<(
        { __typename?: 'ExerciseCollection' }
        & CollectionValuesFragment
      )> }
    )> }
  )> }
);

export type CollOverviewToolFragment = (
  { __typename?: 'CollectionTool' }
  & Pick<CollectionTool, 'id' | 'name'>
  & { collection?: Maybe<(
    { __typename?: 'ExerciseCollection' }
    & Pick<ExerciseCollection, 'collectionId' | 'title'>
    & { exercises: Array<(
      { __typename?: 'Exercise' }
      & FieldsForLinkFragment
    )> }
  )> }
);

export type CollectionOverviewQueryVariables = Exact<{
  toolId: Scalars['String'];
  collId: Scalars['Int'];
}>;


export type CollectionOverviewQuery = (
  { __typename?: 'Query' }
  & { me?: Maybe<(
    { __typename?: 'User' }
    & { tool?: Maybe<(
      { __typename?: 'CollectionTool' }
      & CollOverviewToolFragment
    )> }
  )> }
);

export type PartFragment = (
  { __typename?: 'ExPart' }
  & Pick<ExPart, 'id' | 'name' | 'isEntryPart' | 'solved'>
);

export type ExerciseOverviewFragment = (
  { __typename?: 'Exercise' }
  & Pick<Exercise, 'exerciseId' | 'title' | 'text'>
  & { parts: Array<(
    { __typename?: 'ExPart' }
    & PartFragment
  )> }
);

export type ExerciseOverviewQueryVariables = Exact<{
  toolId: Scalars['String'];
  collId: Scalars['Int'];
  exId: Scalars['Int'];
}>;


export type ExerciseOverviewQuery = (
  { __typename?: 'Query' }
  & { me?: Maybe<(
    { __typename?: 'User' }
    & { tool?: Maybe<(
      { __typename?: 'CollectionTool' }
      & Pick<CollectionTool, 'id' | 'name'>
      & { collection?: Maybe<(
        { __typename?: 'ExerciseCollection' }
        & Pick<ExerciseCollection, 'collectionId' | 'title'>
        & { exercise?: Maybe<(
          { __typename?: 'Exercise' }
          & ExerciseOverviewFragment
        )> }
      )> }
    )> }
  )> }
);

export type ExerciseSolveFieldsFragment = (
  { __typename?: 'Exercise' }
  & Pick<Exercise, 'exerciseId' | 'collectionId' | 'toolId' | 'title' | 'text'>
  & { content: (
    { __typename: 'EbnfExerciseContent' }
    & EbnfExerciseContentFragment
  ) | (
    { __typename: 'FlaskExerciseContent' }
    & FlaskExerciseContentFragment
  ) | (
    { __typename: 'ProgrammingExerciseContent' }
    & ProgrammingExerciseContentFragment
    & ProgrammingExerciseContentFragment
  ) | (
    { __typename: 'RegexExerciseContent' }
    & RegexExerciseContentFragment
  ) | (
    { __typename: 'SqlExerciseContent' }
    & SqlExerciseContentFragment
  ) | (
    { __typename: 'UmlExerciseContent' }
    & UmlExerciseContentFragment
  ) | (
    { __typename: 'WebExerciseContent' }
    & WebExerciseContentFragment
  ) | (
    { __typename: 'XmlExerciseContent' }
    & XmlExerciseContentFragment
  ) }
);

export type ExerciseQueryVariables = Exact<{
  toolId: Scalars['String'];
  collId: Scalars['Int'];
  exId: Scalars['Int'];
  partId: Scalars['String'];
}>;


export type ExerciseQuery = (
  { __typename?: 'Query' }
  & { me?: Maybe<(
    { __typename?: 'User' }
    & { tool?: Maybe<(
      { __typename?: 'CollectionTool' }
      & { collection?: Maybe<(
        { __typename?: 'ExerciseCollection' }
        & { exercise?: Maybe<(
          { __typename?: 'Exercise' }
          & ExerciseSolveFieldsFragment
        )> }
      )> }
    )> }
  )> }
);

export type ExerciseFileFragment = (
  { __typename?: 'ExerciseFile' }
  & Pick<ExerciseFile, 'name' | 'fileType' | 'content' | 'editable'>
);

export type FilesSolutionFragment = (
  { __typename: 'FilesSolution' }
  & { files: Array<(
    { __typename?: 'ExerciseFile' }
    & ExerciseFileFragment
  )> }
);

export type LevelFragment = (
  { __typename?: 'Level' }
  & Pick<Level, 'title' | 'levelIndex'>
);

export type TopicFragment = (
  { __typename?: 'Topic' }
  & Pick<Topic, 'abbreviation' | 'title'>
  & { maxLevel: (
    { __typename?: 'Level' }
    & LevelFragment
  ) }
);

export type TopicWithLevelFragment = (
  { __typename?: 'TopicWithLevel' }
  & { topic: (
    { __typename?: 'Topic' }
    & TopicFragment
  ), level: (
    { __typename?: 'Level' }
    & LevelFragment
  ) }
);

export type FieldsPartFragment = (
  { __typename?: 'ExPart' }
  & Pick<ExPart, 'id' | 'name' | 'solved'>
);

export type FieldsForLinkFragment = (
  { __typename?: 'Exercise' }
  & Pick<Exercise, 'exerciseId' | 'collectionId' | 'toolId' | 'title' | 'difficulty'>
  & { topicsWithLevels: Array<(
    { __typename?: 'TopicWithLevel' }
    & TopicWithLevelFragment
  )>, parts: Array<(
    { __typename?: 'ExPart' }
    & FieldsPartFragment
  )> }
);

export type EbnfExerciseContentFragment = (
  { __typename?: 'EbnfExerciseContent' }
  & Pick<EbnfExerciseContent, 'sampleSolutions'>
);

export type FlaskExerciseContentFragment = (
  { __typename: 'FlaskExerciseContent' }
  & { testConfig: (
    { __typename?: 'FlaskTestsConfig' }
    & { tests: Array<(
      { __typename?: 'FlaskSingleTestConfig' }
      & Pick<FlaskSingleTestConfig, 'id' | 'testName' | 'description'>
    )> }
  ), files: Array<(
    { __typename?: 'ExerciseFile' }
    & ExerciseFileFragment
  )>, flaskSampleSolutions: Array<(
    { __typename?: 'FilesSolution' }
    & FilesSolutionFragment
  )> }
);

export type UnitTestPartFragment = (
  { __typename?: 'UnitTestPart' }
  & { unitTestFiles: Array<(
    { __typename?: 'ExerciseFile' }
    & ExerciseFileFragment
  )> }
);

export type ProgrammingExerciseContentFragment = (
  { __typename?: 'ProgrammingExerciseContent' }
  & { programmingPart: ProgrammingExerciseContent['part'] }
  & { unitTestPart: (
    { __typename?: 'UnitTestPart' }
    & UnitTestPartFragment
  ), implementationPart: (
    { __typename?: 'ImplementationPart' }
    & { files: Array<(
      { __typename?: 'ExerciseFile' }
      & ExerciseFileFragment
    )> }
  ), programmingSampleSolutions: Array<(
    { __typename?: 'FilesSolution' }
    & FilesSolutionFragment
  )> }
);

export type RegexExerciseContentFragment = (
  { __typename?: 'RegexExerciseContent' }
  & { regexSampleSolutions: RegexExerciseContent['sampleSolutions'], regexPart: RegexExerciseContent['part'] }
);

export type SqlExerciseContentFragment = (
  { __typename?: 'SqlExerciseContent' }
  & Pick<SqlExerciseContent, 'hint'>
  & { sqlSampleSolutions: SqlExerciseContent['sampleSolutions'], sqlPart: SqlExerciseContent['part'] }
  & { sqlDbContents: Array<(
    { __typename?: 'SqlQueryResult' }
    & SqlQueryResultFragment
  )> }
);

export type UmlExerciseContentFragment = (
  { __typename?: 'UmlExerciseContent' }
  & Pick<UmlExerciseContent, 'toIgnore'>
  & { umlPart: UmlExerciseContent['part'] }
  & { mappings: Array<(
    { __typename?: 'KeyValueObject' }
    & Pick<KeyValueObject, 'key' | 'value'>
  )>, umlSampleSolutions: Array<(
    { __typename?: 'UmlClassDiagram' }
    & UmlClassDiagramFragment
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

export type WebExerciseContentFragment = (
  { __typename?: 'WebExerciseContent' }
  & { webPart: WebExerciseContent['part'] }
  & { files: Array<(
    { __typename?: 'ExerciseFile' }
    & ExerciseFileFragment
  )>, siteSpec: (
    { __typename?: 'SiteSpec' }
    & Pick<SiteSpec, 'fileName' | 'jsTaskCount'>
    & { htmlTasks: Array<(
      { __typename?: 'HtmlTask' }
      & Pick<HtmlTask, 'text'>
    )> }
  ), webSampleSolutions: Array<(
    { __typename?: 'FilesSolution' }
    & FilesSolutionFragment
  )> }
);

export type XmlExerciseContentFragment = (
  { __typename?: 'XmlExerciseContent' }
  & Pick<XmlExerciseContent, 'rootNode' | 'grammarDescription'>
  & { xmlPart: XmlExerciseContent['part'] }
  & { xmlSampleSolutions: Array<(
    { __typename?: 'XmlSolution' }
    & XmlSolutionFragment
  )> }
);

export type XmlSolutionFragment = (
  { __typename: 'XmlSolution' }
  & Pick<XmlSolution, 'document' | 'grammar'>
);

export type RegisterMutationVariables = Exact<{
  username: Scalars['String'];
  firstPassword: Scalars['String'];
  secondPassword: Scalars['String'];
}>;


export type RegisterMutation = (
  { __typename?: 'Mutation' }
  & Pick<Mutation, 'register'>
);

export type LoggedInUserWithTokenFragment = (
  { __typename?: 'LoggedInUserWithToken' }
  & Pick<LoggedInUserWithToken, 'jwt'>
  & { loggedInUser: (
    { __typename?: 'LoggedInUser' }
    & Pick<LoggedInUser, 'username' | 'isAdmin'>
  ) }
);

export type LoginMutationVariables = Exact<{
  username: Scalars['String'];
  password: Scalars['String'];
}>;


export type LoginMutation = (
  { __typename?: 'Mutation' }
  & { login?: Maybe<(
    { __typename?: 'LoggedInUserWithToken' }
    & LoggedInUserWithTokenFragment
  )> }
);

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
export const RegexAbstractResultFragmentDoc = gql`
    fragment RegexAbstractResult on RegexAbstractResult {
  __typename
  points
  maxPoints
}
    `;
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
    ...RegexAbstractResult
    ...RegexMatchingResult
    ...RegexExtractionResult
  }
}
    ${RegexAbstractResultFragmentDoc}
${RegexMatchingResultFragmentDoc}
${RegexExtractionResultFragmentDoc}`;
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
  notMatchedForUserString
  notMatchedForSampleString
}
    ${NewMatchFragmentDoc}`;
export const ColumnComparisonFragmentDoc = gql`
    fragment ColumnComparison on SqlColumnComparisonMatchingResult {
  ...SqlMatchingResult
}
    ${SqlMatchingResultFragmentDoc}`;
export const StringMatchFragmentDoc = gql`
    fragment StringMatch on StringMatch {
  matchType
  sampleArg
  userArg
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
export const BinaryExpressionComparisonFragmentDoc = gql`
    fragment BinaryExpressionComparison on SqlBinaryExpressionComparisonMatchingResult {
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
    ...StringMatchingResult
  }
  orderByComparison {
    ...StringMatchingResult
  }
  limitComparison {
    ...LimitComparison
  }
}
    ${StringMatchingResultFragmentDoc}
${LimitComparisonFragmentDoc}`;
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
    columnComparison {
      ...ColumnComparison
    }
    tableComparison {
      ...StringMatchingResult
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
        ...StringMatchingResult
      }
    }
  }
  executionResult {
    ...SqlExecutionResult
  }
}
    ${ColumnComparisonFragmentDoc}
${StringMatchingResultFragmentDoc}
${BinaryExpressionComparisonFragmentDoc}
${SelectAdditionalComparisonFragmentDoc}
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
export const LessonIdentifierFragmentDoc = gql`
    fragment LessonIdentifier on Lesson {
  lessonId
  title
  description
  video
}
    `;
export const LessonOverviewFragmentDoc = gql`
    fragment LessonOverview on Lesson {
  title
  description
  video
  contentCount
}
    `;
export const LessonTextContentFragmentDoc = gql`
    fragment LessonTextContent on LessonTextContent {
  __typename
  contentId
  content
}
    `;
export const LessonMultipleChoiceQuestionAnswerFragmentDoc = gql`
    fragment LessonMultipleChoiceQuestionAnswer on LessonMultipleChoiceQuestionAnswer {
  id
  answer
  isCorrect
}
    `;
export const LessonMultipleChoiceQuestionFragmentDoc = gql`
    fragment LessonMultipleChoiceQuestion on LessonMultipleChoiceQuestion {
  id
  questionText
  answers {
    ...LessonMultipleChoiceQuestionAnswer
  }
}
    ${LessonMultipleChoiceQuestionAnswerFragmentDoc}`;
export const LessonMultipleChoiceQuestionContentFragmentDoc = gql`
    fragment LessonMultipleChoiceQuestionContent on LessonMultipleChoiceQuestionsContent {
  __typename
  contentId
  questions {
    ...LessonMultipleChoiceQuestion
  }
}
    ${LessonMultipleChoiceQuestionFragmentDoc}`;
export const LessonAsTextFragmentDoc = gql`
    fragment LessonAsText on Lesson {
  lessonId
  title
  description
  contents {
    ... on LessonTextContent {
      ...LessonTextContent
    }
    ... on LessonMultipleChoiceQuestionsContent {
      ...LessonMultipleChoiceQuestionContent
    }
  }
}
    ${LessonTextContentFragmentDoc}
${LessonMultipleChoiceQuestionContentFragmentDoc}`;
export const CollectionToolFragmentDoc = gql`
    fragment CollectionTool on CollectionTool {
  id
  name
  state
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
  id
  name
  collection(collId: $collId) {
    collectionId
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
  sampleSolutions
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
  __typename
  files {
    ...ExerciseFile
  }
}
    ${ExerciseFileFragmentDoc}`;
export const FlaskExerciseContentFragmentDoc = gql`
    fragment FlaskExerciseContent on FlaskExerciseContent {
  __typename
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
  regexPart: part(partId: $partId)
}
    `;
export const SqlExerciseContentFragmentDoc = gql`
    fragment SqlExerciseContent on SqlExerciseContent {
  hint
  sqlSampleSolutions: sampleSolutions
  sqlPart: part(partId: $partId)
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
  __typename
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
export const FlaskCorrectionDocument = gql`
    mutation FlaskCorrection($collId: Int!, $exId: Int!, $part: FlaskExercisePart!, $solution: FilesSolutionInput!) {
  me {
    flaskExercise(collId: $collId, exId: $exId) {
      correct(part: $part, solution: $solution) {
        ...FlaskCorrectionResult
      }
    }
  }
}
    ${FlaskCorrectionResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class FlaskCorrectionGQL extends Apollo.Mutation<FlaskCorrectionMutation, FlaskCorrectionMutationVariables> {
    document = FlaskCorrectionDocument;
    
    constructor(apollo: Apollo.Apollo) {
      super(apollo);
    }
  }
export const ProgrammingCorrectionDocument = gql`
    mutation ProgrammingCorrection($collId: Int!, $exId: Int!, $part: ProgExPart!, $solution: FilesSolutionInput!) {
  me {
    programmingExercise(collId: $collId, exId: $exId) {
      correct(part: $part, solution: $solution) {
        ...ProgrammingCorrectionResult
      }
    }
  }
}
    ${ProgrammingCorrectionResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class ProgrammingCorrectionGQL extends Apollo.Mutation<ProgrammingCorrectionMutation, ProgrammingCorrectionMutationVariables> {
    document = ProgrammingCorrectionDocument;
    
    constructor(apollo: Apollo.Apollo) {
      super(apollo);
    }
  }
export const RegexCorrectionDocument = gql`
    mutation RegexCorrection($collId: Int!, $exId: Int!, $part: RegexExPart!, $solution: String!) {
  me {
    regexExercise(collId: $collId, exId: $exId) {
      correct(part: $part, solution: $solution) {
        ...RegexCorrectionResult
      }
    }
  }
}
    ${RegexCorrectionResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class RegexCorrectionGQL extends Apollo.Mutation<RegexCorrectionMutation, RegexCorrectionMutationVariables> {
    document = RegexCorrectionDocument;
    
    constructor(apollo: Apollo.Apollo) {
      super(apollo);
    }
  }
export const SqlCorrectionDocument = gql`
    mutation SqlCorrection($collId: Int!, $exId: Int!, $part: SqlExPart!, $solution: String!) {
  me {
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
    
    constructor(apollo: Apollo.Apollo) {
      super(apollo);
    }
  }
export const UmlCorrectionDocument = gql`
    mutation UmlCorrection($collId: Int!, $exId: Int!, $part: UmlExPart!, $solution: UmlClassDiagramInput!) {
  me {
    umlExercise(collId: $collId, exId: $exId) {
      correct(part: $part, solution: $solution) {
        ...UmlCorrectionResult
      }
    }
  }
}
    ${UmlCorrectionResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class UmlCorrectionGQL extends Apollo.Mutation<UmlCorrectionMutation, UmlCorrectionMutationVariables> {
    document = UmlCorrectionDocument;
    
    constructor(apollo: Apollo.Apollo) {
      super(apollo);
    }
  }
export const WebCorrectionDocument = gql`
    mutation WebCorrection($collId: Int!, $exId: Int!, $part: WebExPart!, $solution: FilesSolutionInput!) {
  me {
    webExercise(collId: $collId, exId: $exId) {
      correct(part: $part, solution: $solution) {
        ...WebCorrectionResult
      }
    }
  }
}
    ${WebCorrectionResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class WebCorrectionGQL extends Apollo.Mutation<WebCorrectionMutation, WebCorrectionMutationVariables> {
    document = WebCorrectionDocument;
    
    constructor(apollo: Apollo.Apollo) {
      super(apollo);
    }
  }
export const XmlCorrectionDocument = gql`
    mutation XmlCorrection($collId: Int!, $exId: Int!, $part: XmlExPart!, $solution: XmlSolutionInput!) {
  me {
    xmlExercise(collId: $collId, exId: $exId) {
      correct(part: $part, solution: $solution) {
        ...XmlCorrectionResult
      }
    }
  }
}
    ${XmlCorrectionResultFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class XmlCorrectionGQL extends Apollo.Mutation<XmlCorrectionMutation, XmlCorrectionMutationVariables> {
    document = XmlCorrectionDocument;
    
    constructor(apollo: Apollo.Apollo) {
      super(apollo);
    }
  }
export const LessonsForToolDocument = gql`
    query LessonsForTool($toolId: String!) {
  me {
    tool(toolId: $toolId) {
      name
      lessons {
        ...LessonIdentifier
      }
    }
  }
}
    ${LessonIdentifierFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class LessonsForToolGQL extends Apollo.Query<LessonsForToolQuery, LessonsForToolQueryVariables> {
    document = LessonsForToolDocument;
    
    constructor(apollo: Apollo.Apollo) {
      super(apollo);
    }
  }
export const LessonOverviewDocument = gql`
    query LessonOverview($toolId: String!, $lessonId: Int!) {
  me {
    tool(toolId: $toolId) {
      lesson(lessonId: $lessonId) {
        ...LessonOverview
      }
    }
  }
}
    ${LessonOverviewFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class LessonOverviewGQL extends Apollo.Query<LessonOverviewQuery, LessonOverviewQueryVariables> {
    document = LessonOverviewDocument;
    
    constructor(apollo: Apollo.Apollo) {
      super(apollo);
    }
  }
export const LessonAsTextDocument = gql`
    query LessonAsText($toolId: String!, $lessonId: Int!) {
  me {
    tool(toolId: $toolId) {
      lesson(lessonId: $lessonId) {
        ...LessonAsText
      }
    }
  }
}
    ${LessonAsTextFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class LessonAsTextGQL extends Apollo.Query<LessonAsTextQuery, LessonAsTextQueryVariables> {
    document = LessonAsTextDocument;
    
    constructor(apollo: Apollo.Apollo) {
      super(apollo);
    }
  }
export const LessonAsVideoDocument = gql`
    query LessonAsVideo($toolId: String!, $lessonId: Int!) {
  me {
    tool(toolId: $toolId) {
      lesson(lessonId: $lessonId) {
        title
        video
      }
    }
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class LessonAsVideoGQL extends Apollo.Query<LessonAsVideoQuery, LessonAsVideoQueryVariables> {
    document = LessonAsVideoDocument;
    
    constructor(apollo: Apollo.Apollo) {
      super(apollo);
    }
  }
export const ToolOverviewDocument = gql`
    query ToolOverview {
  me {
    tools {
      ...CollectionTool
    }
  }
}
    ${CollectionToolFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class ToolOverviewGQL extends Apollo.Query<ToolOverviewQuery, ToolOverviewQueryVariables> {
    document = ToolOverviewDocument;
    
    constructor(apollo: Apollo.Apollo) {
      super(apollo);
    }
  }
export const CollectionToolOverviewDocument = gql`
    query CollectionToolOverview($toolId: String!) {
  me {
    tool(toolId: $toolId) {
      id
      name
      collectionCount
      exerciseCount
      lessonCount
      proficiencies {
        ...UserProficiency
      }
    }
  }
}
    ${UserProficiencyFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class CollectionToolOverviewGQL extends Apollo.Query<CollectionToolOverviewQuery, CollectionToolOverviewQueryVariables> {
    document = CollectionToolOverviewDocument;
    
    constructor(apollo: Apollo.Apollo) {
      super(apollo);
    }
  }
export const AllExercisesOverviewDocument = gql`
    query AllExercisesOverview($toolId: String!) {
  me {
    tool(toolId: $toolId) {
      allExercises {
        topicsWithLevels {
          ...TopicWithLevel
        }
        ...FieldsForLink
      }
    }
  }
}
    ${TopicWithLevelFragmentDoc}
${FieldsForLinkFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class AllExercisesOverviewGQL extends Apollo.Query<AllExercisesOverviewQuery, AllExercisesOverviewQueryVariables> {
    document = AllExercisesOverviewDocument;
    
    constructor(apollo: Apollo.Apollo) {
      super(apollo);
    }
  }
export const CollectionListDocument = gql`
    query CollectionList($toolId: String!) {
  me {
    tool(toolId: $toolId) {
      id
      name
      collections {
        ...CollectionValues
      }
    }
  }
}
    ${CollectionValuesFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class CollectionListGQL extends Apollo.Query<CollectionListQuery, CollectionListQueryVariables> {
    document = CollectionListDocument;
    
    constructor(apollo: Apollo.Apollo) {
      super(apollo);
    }
  }
export const CollectionOverviewDocument = gql`
    query CollectionOverview($toolId: String!, $collId: Int!) {
  me {
    tool(toolId: $toolId) {
      ...CollOverviewTool
    }
  }
}
    ${CollOverviewToolFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class CollectionOverviewGQL extends Apollo.Query<CollectionOverviewQuery, CollectionOverviewQueryVariables> {
    document = CollectionOverviewDocument;
    
    constructor(apollo: Apollo.Apollo) {
      super(apollo);
    }
  }
export const ExerciseOverviewDocument = gql`
    query ExerciseOverview($toolId: String!, $collId: Int!, $exId: Int!) {
  me {
    tool(toolId: $toolId) {
      id
      name
      collection(collId: $collId) {
        collectionId
        title
        exercise(exId: $exId) {
          ...ExerciseOverview
        }
      }
    }
  }
}
    ${ExerciseOverviewFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class ExerciseOverviewGQL extends Apollo.Query<ExerciseOverviewQuery, ExerciseOverviewQueryVariables> {
    document = ExerciseOverviewDocument;
    
    constructor(apollo: Apollo.Apollo) {
      super(apollo);
    }
  }
export const ExerciseDocument = gql`
    query Exercise($toolId: String!, $collId: Int!, $exId: Int!, $partId: String!) {
  me {
    tool(toolId: $toolId) {
      collection(collId: $collId) {
        exercise(exId: $exId) {
          ...ExerciseSolveFields
        }
      }
    }
  }
}
    ${ExerciseSolveFieldsFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class ExerciseGQL extends Apollo.Query<ExerciseQuery, ExerciseQueryVariables> {
    document = ExerciseDocument;
    
    constructor(apollo: Apollo.Apollo) {
      super(apollo);
    }
  }
export const RegisterDocument = gql`
    mutation Register($username: String!, $firstPassword: String!, $secondPassword: String!) {
  register(
    registerValues: {username: $username, firstPassword: $firstPassword, secondPassword: $secondPassword}
  )
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class RegisterGQL extends Apollo.Mutation<RegisterMutation, RegisterMutationVariables> {
    document = RegisterDocument;
    
    constructor(apollo: Apollo.Apollo) {
      super(apollo);
    }
  }
export const LoginDocument = gql`
    mutation Login($username: String!, $password: String!) {
  login(credentials: {username: $username, password: $password}) {
    ...LoggedInUserWithToken
  }
}
    ${LoggedInUserWithTokenFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class LoginGQL extends Apollo.Mutation<LoginMutation, LoginMutationVariables> {
    document = LoginDocument;
    
    constructor(apollo: Apollo.Apollo) {
      super(apollo);
    }
  }