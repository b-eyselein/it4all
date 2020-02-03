
export interface IRegexSampleSolution {
  id: number;
  sample: string;
}


export interface IRegexExtractionEvaluationResult {
  base: string;
  extractionMatchingResult: IRegexExtractionMatchingResult;
  correct: boolean;
}

export type RegexCorrectionTypes = ("MATCHING" | "EXTRACTION");

export interface IRegexMatchMatch {
  matchType: MatchType;
  userArg?: IMatch;
  sampleArg?: IMatch;
}


export interface IMatch {
  start: number;
  end: number;
  content: string;
}


export interface IRegexExtractionMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IRegexMatchMatch[];
  points: number;
  maxPoints: number;
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

export type BinaryClassificationResultTypes = ("TruePositive" | "FalsePositive" | "FalseNegative" | "TrueNegative");
export type MatchType = ("SUCCESSFUL_MATCH" | "PARTIAL_MATCH" | "UNSUCCESSFUL_MATCH" | "ONLY_USER" | "ONLY_SAMPLE");

export interface IRegexExerciseContent {
  maxPoints: number;
  correctionType: RegexCorrectionTypes;
  sampleSolutions: IRegexSampleSolution[];
  matchTestData: IRegexMatchTestData[];
  extractionTestData: IRegexExtractionTestData[];
}


export interface IRegexMatchingEvaluationResult {
  matchData: string;
  isIncluded: boolean;
  resultType: BinaryClassificationResultTypes;
}


export interface IRegexCompleteResult {
  correctionType: RegexCorrectionTypes;
  matchingResults: IRegexMatchingEvaluationResult[];
  extractionResults: IRegexExtractionEvaluationResult[];
  points: IPoints;
  maxPoints: IPoints;
  solutionSaved: boolean;
}


export interface IPoints {
  quarters: number;
}
