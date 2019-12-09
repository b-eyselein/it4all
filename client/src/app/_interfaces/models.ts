export interface IWebExerciseContent {
  htmlText?: string;
  jsText?: string;
  siteSpec: ISiteSpec;
  files: IExerciseFile[];
  sampleSolutions: ISampleSolution[];
}


export interface IJsHtmlElementSpec {
  id: number;
  xpathQuery: string;
  awaitedTagName: string;
  awaitedTextContent?: string;
  attributes: { [key: string]: string };
}


export interface IRegexMatchTestData {
  id: number;
  data: string;
  isIncluded: boolean;
}


export interface IGradedHtmlTaskResult {
  id: number;
  success: SuccessType;
  elementFound: boolean;
  textContentResult?: IGradedTextResult;
  attributeResults: IGradedTextResult[];
  isSuccessful: boolean;
  points: IPoints;
  maxPoints: IPoints;
}

export type SuccessType = ('ERROR' | 'NONE' | 'PARTIALLY' | 'COMPLETE');

export interface IExerciseFile {
  name: string;
  resourcePath: string;
  fileType: string;
  editable: boolean;
  content: string;
  active?: boolean;
}


export interface IUmlExerciseContent {
  toIgnore: string[];
  mappings: { [key: string]: string };
  sampleSolutions: ISampleSolution[];
}


export interface IJsTask {
  id: number;
  text: string;
  preConditions: IJsHtmlElementSpec[];
  action: IJsAction;
  postConditions: IJsHtmlElementSpec[];
}


export interface IProgSolution {
  files: IExerciseFile[];
  testData: IProgTestData[];
}


export interface ISqlExerciseContent {
  exerciseType: SqlExerciseType;
  hint?: string;
  sampleSolutions: ISampleSolution[];
}


export interface IExerciseCollection {
  id: number;
  toolId: string;
  title: string;
  authors: string[];
  text: string;
  shortName: string;
  exercises: IExercise[];
}


export interface ISemanticVersion {
  major: number;
  minor: number;
  patch: number;
}


export interface IPoints {
  quarters: number;
}


export interface IGradedTextResult {
  keyName: string;
  awaitedContent: string;
  maybeFoundContent?: string;
  isSuccessful: boolean;
  points: IPoints;
  maxPoints: IPoints;
}


export interface ISampleSolution {
  id: number;
  sample: object;
}


export interface IGradedJsActionResult {
  actionPerformed: boolean;
  jsAction: IJsAction;
  points: IPoints;
  maxPoints: IPoints;
}

export type JsActionType = ('Click' | 'FillOut');

export interface IExercise {
  id: number;
  collectionId: number;
  toolId: string;
  semanticVersion: ISemanticVersion;
  title: string;
  authors: string[];
  text: string;
  tags: IExTag[];
  content: any;
}


export interface IJsAction {
  xpathQuery: string;
  actionType: JsActionType;
  keysToSend?: string;
}


export interface IExTag {
  abbreviation: string;
  title: string;
}


export interface ISiteSpec {
  fileName: string;
  htmlTasks: IHtmlTask[];
  jsTasks: IJsTask[];
}


export interface IWebCompleteResult {
  gradedHtmlTaskResults: IGradedHtmlTaskResult[];
  gradedJsTaskResults: IGradedJsTaskResult[];
  points: IPoints;
  maxPoints: IPoints;
  solutionSaved: boolean;
}

export type RegexCorrectionTypes = ('MATCHING' | 'EXTRACTION');

export interface IRegexExtractionTestData {
  id: number;
  base: string;
}


export interface IProgTestData {
  id: number;
  input: any;
  output: any;
}


export interface IRegexExerciseContent {
  maxPoints: number;
  correctionType: RegexCorrectionTypes;
  sampleSolutions: ISampleSolution[];
  matchTestData: IRegexMatchTestData[];
  extractionTestData: IRegexExtractionTestData[];
}


export interface IXmlExerciseContent {
  grammarDescription: string;
  rootNode: string;
  sampleSolutions: ISampleSolution[];
}


export interface IGradedJsTaskResult {
  id: number;
  gradedPreResults: IGradedJsHtmlElementSpecResult[];
  gradedJsActionResult: IGradedJsActionResult;
  gradedPostResults: IGradedJsHtmlElementSpecResult[];
  success: SuccessType;
  points: IPoints;
  maxPoints: IPoints;
}


export interface IHtmlTask {
  id: number;
  text: string;
  xpathQuery: string;
  awaitedTagName: string;
  awaitedTextContent?: string;
  attributes: { [key: string]: string };
}


export interface IGradedJsHtmlElementSpecResult {
  id: number;
  success: SuccessType;
  elementFound: boolean;
  textContentResult?: IGradedTextResult;
  attributeResults: IGradedTextResult[];
  isSuccessful: boolean;
  points: IPoints;
  maxPoints: IPoints;
}

export type SqlExerciseType = ('SELECT' | 'CREATE' | 'UPDATE' | 'INSERT' | 'DELETE');
