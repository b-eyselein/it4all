
export interface IRegexMatchMatch {
  userArg?: string;
  sampleArg?: string;
  analysisResult?: IGenericAnalysisResult;
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


export interface IRegexCompleteResult {
  correctionType: RegexCorrectionTypes;
  matchingResults: IRegexMatchingEvaluationResult[];
  extractionResults: IRegexExtractionEvaluationResult[];
  points: IPoints;
  maxPoints: IPoints;
  solutionSaved: boolean;
}


export interface IRegexExtractionEvaluationResult {
  base: string;
  extractionMatchingResult: IRegexExtractionMatchingResult;
  correct: boolean;
}


export interface IPoints {
  quarters: number;
}


export interface IRegexMatchingEvaluationResult {
  matchData: string;
  isIncluded: boolean;
  resultType: BinaryClassificationResultTypes;
}


export interface ISampleSolution {
  id: number;
  sample: object;
}

export type RegexCorrectionTypes = ("MATCHING" | "EXTRACTION");

export interface IGenericAnalysisResult {
  matchType: MatchType;
}

export type BinaryClassificationResultTypes = ("TruePositive" | "FalsePositive" | "FalseNegative" | "TrueNegative");
export type MatchType = ("SUCCESSFUL_MATCH" | "PARTIAL_MATCH" | "UNSUCCESSFUL_MATCH" | "ONLY_USER" | "ONLY_SAMPLE");

export interface IRegexExerciseContent {
  maxPoints: number;
  correctionType: RegexCorrectionTypes;
  sampleSolutions: ISampleSolution[];
  matchTestData: IRegexMatchTestData[];
  extractionTestData: IRegexExtractionTestData[];
}


export interface IRegexExtractionMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IRegexMatchMatch[];
  points: number;
  maxPoints: number;
}
