
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


export interface IRegexMatchingEvaluationResult {
  matchData: string;
  isIncluded: boolean;
  resultType: BinaryClassificationResultTypes;
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

export type BinaryClassificationResultTypes = ("TruePositive" | "FalsePositive" | "FalseNegative" | "TrueNegative");
export type MatchType = ("SUCCESSFUL_MATCH" | "PARTIAL_MATCH" | "UNSUCCESSFUL_MATCH" | "ONLY_USER" | "ONLY_SAMPLE");

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
