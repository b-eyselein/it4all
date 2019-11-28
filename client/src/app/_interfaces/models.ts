
export interface ISampleSolution {
  id: number
  sample: object
}
       

export interface IWebExerciseContent {
  htmlText?: string
  jsText?: string
  siteSpec: ISiteSpec
  files: IExerciseFile[]
  sampleSolutions: ISampleSolution[]
}
       

export interface IJsHtmlElementSpec {
  id: number
  xpathQuery: string
  awaitedTagName: string
  awaitedTextContent?: string
  attributes: { [ key: string ]: string }
}
       

export interface IRegexMatchTestData {
  id: number
  data: string
  isIncluded: boolean
}
       

export interface ISiteSpec {
  fileName: string
  htmlTasks: IHtmlTask[]
  jsTasks: IJsTask[]
}
       

export interface ISqlExerciseContent {
  exerciseType: SqlExerciseType
  tags: SqlExerciseTag[]
  hint?: string
  sampleSolutions: ISampleSolution[]
}
       

export interface IExerciseFile {
  name: string
  resourcePath: string
  fileType: string
  editable: boolean
  content: string
  active?: boolean
}
       

export interface IUmlExerciseContent {
  toIgnore: string[]
  mappings: { [ key: string ]: string }
  sampleSolutions: ISampleSolution[]
}
       
export type SqlExerciseTag = ("SQL_JOIN" | "SQL_DOUBLE_JOIN" | "SQL_TRIPLE_JOIN" | "SQL_ORDER_BY" | "SQL_GROUP_BY" | "SQL_FUNCTION" | "SQL_ALIAS" | "SQL_LIMIT" | "SQL_SUBSELECT")

export interface IHtmlTask {
  id: number
  text: string
  xpathQuery: string
  awaitedTagName: string
  awaitedTextContent?: string
  attributes: { [ key: string ]: string }
}
       

export interface IProgSolution {
  files: IExerciseFile[]
  testData: IProgTestData[]
}
       
export type SqlExerciseType = ("SELECT" | "CREATE" | "UPDATE" | "INSERT" | "DELETE")

export interface ISemanticVersion {
  major: number
  minor: number
  patch: number
}
       
export type JsActionType = ("Click" | "FillOut")

export interface IJsAction {
  xpathQuery: string
  actionType: JsActionType
  keysToSend?: string
}
       
export type RegexCorrectionTypes = ("MATCHING" | "EXTRACTION")

export interface IExerciseCollection {
  id: number
  toolId: string
  title: string
  author: string
  text: string
  shortName: string
  exercises: IExercise[]
}
       

export interface IExercise {
  id: number
  collectionId: number
  toolId: string
  semanticVersion: ISemanticVersion
  title: string
  author: string
  text: string
  content: any
}
       

export interface IJsTask {
  id: number
  text: string
  preConditions: IJsHtmlElementSpec[]
  action: IJsAction
  postConditions: IJsHtmlElementSpec[]
}
       

export interface IRegexExtractionTestData {
  id: number
  base: string
}
       

export interface IProgTestData {
  id: number
  input: any
  output: any
}
       

export interface IXmlExerciseContent {
  grammarDescription: ILongText
  rootNode: string
  sampleSolutions: ISampleSolution[]
}
       

export interface IRegexExerciseContent {
  maxPoints: number
  correctionType: RegexCorrectionTypes
  sampleSolutions: ISampleSolution[]
  matchTestData: IRegexMatchTestData[]
  extractionTestData: IRegexExtractionTestData[]
}
       

export interface ILongText {
  wrapped: string
}
       