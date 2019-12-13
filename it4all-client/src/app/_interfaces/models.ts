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

export type SuccessType = ('ERROR' | 'NONE' | 'PARTIALLY' | 'COMPLETE');

export interface IImplementationPart {
  base: string;
  files: IExerciseFile[];
  implFileName: string;
  sampleSolFileNames: string[];
}


export interface IUmlClass {
  classType: UmlClassType;
  name: string;
  attributes: IUmlAttribute[];
  methods: IUmlMethod[];
}

export type UmlVisibility = ('PUBLIC' | 'PACKAGE' | 'PROTECTED' | 'PRIVATE');

export interface ISqlExerciseContent {
  exerciseType: SqlExerciseType;
  hint?: string;
  sampleSolutions: ISampleSolution[];
}


export interface IUmlAttribute {
  visibility: UmlVisibility;
  memberName: string;
  memberType: string;
  isStatic: boolean;
  isDerived: boolean;
  isAbstract: boolean;
}

export type UmlAssociationType = ('ASSOCIATION' | 'AGGREGATION' | 'COMPOSITION');

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


export interface IWebExerciseContent {
  htmlText?: string;
  jsText?: string;
  siteSpec: ISiteSpec;
  files: IExerciseFile[];
  sampleSolutions: ISampleSolution[];
}


export interface IUmlMethod {
  visibility: UmlVisibility;
  memberName: string;
  memberType: string;
  parameters: string;
  isStatic: boolean;
  isAbstract: boolean;
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


export interface IExerciseCollection {
  id: number;
  toolId: string;
  title: string;
  authors: string[];
  text: string;
  shortName: string;
  exercises: IExerciseMetaData[];
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

export type UmlClassType = ('CLASS' | 'INTERFACE' | 'ABSTRACT');

export interface IExTag {
  abbreviation: string;
  title: string;
}


export interface ISiteSpec {
  fileName: string;
  htmlTasks: IHtmlTask[];
  jsTasks: IJsTask[];
}

export type UmlMultiplicity = ('SINGLE' | 'UNBOUND');

export interface IWebCompleteResult {
  gradedHtmlTaskResults: IGradedHtmlTaskResult[];
  gradedJsTaskResults: IGradedJsTaskResult[];
  points: IPoints;
  maxPoints: IPoints;
  solutionSaved: boolean;
}

export type RegexCorrectionTypes = ('MATCHING' | 'EXTRACTION');

export interface IUmlAssociation {
  assocType: UmlAssociationType;
  assocName?: string;
  firstEnd: string;
  firstMult: UmlMultiplicity;
  secondEnd: string;
  secondMult: UmlMultiplicity;
}


export interface IExerciseFile {
  name: string;
  resourcePath: string;
  fileType: string;
  editable: boolean;
  content: string;
  active?: boolean;
}


export interface IProgExerciseContent {
  functionName: string;
  foldername: string;
  filename: string;
  inputTypes: IProgInput[];
  outputType: any;
  baseData?: any;
  unitTestPart: IUnitTestPart;
  implementationPart: IImplementationPart;
  sampleSolutions: ISampleSolution[];
  sampleTestData: IProgTestData[];
  maybeClassDiagramPart?: IUmlClassDiagram;
}


export interface IUnitTestPart {
  unitTestType: UnitTestTypes;
  unitTestsDescription: string;
  unitTestFiles: IExerciseFile[];
  unitTestTestConfigs: IUnitTestTestConfig[];
  simplifiedTestMainFile?: IExerciseFile;
  testFileName: string;
  sampleSolFileNames: string[];
}


export interface IUmlExerciseContent {
  toIgnore: string[];
  mappings: { [key: string]: string };
  sampleSolutions: ISampleSolution[];
}


export interface IUnitTestTestConfig {
  id: number;
  shouldFail: boolean;
  description: string;
  file: IExerciseFile;
}


export interface IJsTask {
  id: number;
  text: string;
  preConditions: IJsHtmlElementSpec[];
  action: IJsAction;
  postConditions: IJsHtmlElementSpec[];
}


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

export type UnitTestTypes = ('Simplified' | 'Normal');

export interface IProgSolution {
  files: IExerciseFile[];
  testData: IProgTestData[];
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

export interface IProgInput {
  id: number;
  inputName: string;
  inputType: any;
}


export interface ISemanticVersion {
  major: number;
  minor: number;
  patch: number;
}


export interface IUmlClassDiagram {
  classes: IUmlClass[];
  associations: IUmlAssociation[];
  implementations: IUmlImplementation[];
}


export interface IUmlImplementation {
  subClass: string;
  superClass: string;
}


export interface IPoints {
  quarters: number;
}


export interface IExerciseMetaData {
  id: number;
  collectionId: number;
  toolId: string;
  semanticVersion: ISemanticVersion;
  title: string;
  authors: string[];
  text: string;
  tags: IExTag[];
}

