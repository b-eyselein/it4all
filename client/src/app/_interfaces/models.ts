export type UmlClassType = ("CLASS" | "INTERFACE" | "ABSTRACT")
export type UmlMultiplicity = ("SINGLE" | "UNBOUND")

export interface IUmlClass {
  classType: UmlClassType
  name: string
  attributes: IUmlAttribute[]
  methods: IUmlMethod[]
}
       

export interface IUmlAssociation {
  assocType: UmlAssociationType
  assocName?: string
  firstEnd: string
  firstMult: UmlMultiplicity
  secondEnd: string
  secondMult: UmlMultiplicity
}
       
export type UmlVisibility = ("PUBLIC" | "PACKAGE" | "PROTECTED" | "PRIVATE")
export type SqlExerciseTag = ("SQL_JOIN" | "SQL_DOUBLE_JOIN" | "SQL_TRIPLE_JOIN" | "SQL_ORDER_BY" | "SQL_GROUP_BY" | "SQL_FUNCTION" | "SQL_ALIAS" | "SQL_LIMIT" | "SQL_SUBSELECT")

export interface IRegexExtractionTestData {
  id: number
  base: string
}
       

export interface ILongText {
  wrapped: string
}
       
export type SqlExerciseType = ("SELECT" | "CREATE" | "UPDATE" | "INSERT" | "DELETE")
export type UmlAssociationType = ("ASSOCIATION" | "AGGREGATION" | "COMPOSITION")

export interface IUmlMethod {
  visibility: UmlVisibility
  memberName: string
  memberType: string
  parameters: string
  isStatic: boolean
  isAbstract: boolean
}
       

export interface IUmlExerciseContent {
  toIgnore: string[]
  mappings: { [ key: string ]: string }
  sampleSolutions: IUmlSampleSolution[]
}
       

export interface IRegexMatchTestData {
  id: number
  data: string
  isIncluded: boolean
}
       

export interface IXmlSolution {
  document: string
  grammar: string
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
       

export interface IXmlSampleSolution {
  id: number
  sample: IXmlSolution
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
       

export interface IRegexExerciseContent {
  maxPoints: number
  correctionType: RegexCorrectionTypes
  sampleSolutions: IStringSampleSolution[]
  matchTestData: IRegexMatchTestData[]
  extractionTestData: IRegexExtractionTestData[]
}
       

export interface IStringSampleSolution {
  id: number
  sample: string
}
       

export interface ISqlExerciseContent {
  exerciseType: SqlExerciseType
  tags: SqlExerciseTag[]
  hint?: string
  sampleSolutions: IStringSampleSolution[]
}
       

export interface IUmlSampleSolution {
  id: number
  sample: IUmlClassDiagram
}
       

export interface IUmlAttribute {
  visibility: UmlVisibility
  memberName: string
  memberType: string
  isStatic: boolean
  isDerived: boolean
  isAbstract: boolean
}
       

export interface ISemanticVersion {
  major: number
  minor: number
  patch: number
}
       

export interface IUmlClassDiagram {
  classes: IUmlClass[]
  associations: IUmlAssociation[]
  implementations: IUmlImplementation[]
}
       

export interface IXmlExerciseContent {
  grammarDescription: ILongText
  rootNode: string
  sampleSolutions: IXmlSampleSolution[]
}
       

export interface IUmlImplementation {
  subClass: string
  superClass: string
}
       