export interface IMatch {
  start: number;
  end: number;
  content: string;
}


export interface IRegexExerciseContent {
  maxPoints: number;
  correctionType: RegexCorrectionTypes;
  sampleSolutions: ISampleSolution[];
  matchTestData: IRegexMatchTestData[];
  extractionTestData: IRegexExtractionTestData[];
}


export interface IRegexMatchMatch {
  userArg?: IMatch;
  sampleArg?: IMatch;
  analysisResult: IGenericAnalysisResult;
}

export type RegexCorrectionTypes = ('MATCHING' | 'EXTRACTION');

export interface IRegexCompleteResult {
  correctionType: RegexCorrectionTypes;
  matchingResults: IRegexMatchingEvaluationResult[];
  extractionResults: IRegexExtractionEvaluationResult[];
  points: IPoints;
  maxPoints: IPoints;
  solutionSaved: boolean;
}


export interface IGenericAnalysisResult {
  matchType: MatchType;
}


export interface IPoints {
  quarters: number;
}


export interface IRegexMatchTestData {
  id: number;
  data: string;
  isIncluded: boolean;
}


export interface IRegexExtractionTestData {
  id: number;
  base: string;
}

export type BinaryClassificationResultTypes = ('TruePositive' | 'FalsePositive' | 'FalseNegative' | 'TrueNegative');
export type MatchType = ('SUCCESSFUL_MATCH' | 'PARTIAL_MATCH' | 'UNSUCCESSFUL_MATCH' | 'ONLY_USER' | 'ONLY_SAMPLE');

export interface IRegexExtractionMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IRegexMatchMatch[];
  points: number;
  maxPoints: number;
}


export interface IRegexExtractionEvaluationResult {
  base: string;
  extractionMatchingResult: IRegexExtractionMatchingResult;
  correct: boolean;
}


export interface ISampleSolution {
  id: number;
  sample: object;
}


export interface IRegexMatchingEvaluationResult {
  matchData: string;
  isIncluded: boolean;
  resultType: BinaryClassificationResultTypes;
}