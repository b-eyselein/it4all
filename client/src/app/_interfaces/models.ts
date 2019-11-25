export interface IRegexMatchTestData {
  id: number
  data: string
  isIncluded: boolean
}

export type SqlExerciseTag = ('SQL_JOIN' | 'SQL_DOUBLE_JOIN' | 'SQL_TRIPLE_JOIN' | 'SQL_ORDER_BY' | 'SQL_GROUP_BY' | 'SQL_FUNCTION' | 'SQL_ALIAS' | 'SQL_LIMIT' | 'SQL_SUBSELECT')

export interface IRegexExtractionTestData {
  id: number
  base: string
}

export type SqlExerciseType = ('SELECT' | 'CREATE' | 'UPDATE' | 'INSERT' | 'DELETE')

export interface ISemanticVersion {
  major: number
  minor: number
  patch: number;
}

export type RegexCorrectionTypes = ('MATCHING' | 'EXTRACTION')

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
