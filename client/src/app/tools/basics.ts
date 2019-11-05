export interface SampleSolution<SolutionType> {
  id: number;
  sample: SolutionType;
}

export interface StringSampleSolution extends SampleSolution<string> {
}

export type BinaryClassificationResultType = 'TruePositive' | 'FalsePositive' | 'FalseNegative' | 'TrueNegative';

// Matches

export type MatchType = 'SUCCESSFUL_MATCH' | 'PARTIAL_MATCH' | 'UNSUCCESSFUL_MATCH' | 'ONLY_USER' | 'ONLY_SAMPLE';

export interface AnalysisResult {
  success: MatchType;
}

export interface MatchingResult<T, AR extends AnalysisResult> {
  matchName: string;
  matchSingularName: string;

  success: boolean;
  matches: Match<T, AR>[];

  points: number;
  maxPoints: number;
}

export interface Match<T, AR extends AnalysisResult> {
  matchType: MatchType;
  analysisResult: AR | null;
  userArg: T | null;
  sampleArg: T | null;
}

export interface CorrectionResult<R> {
  solutionSaved: boolean;
  isSuccessful: boolean;
  // results: R[];

  points: number;
  maxPoints: number;
}

export interface ExerciseFile {
  name: string;
  content: string;
  fileType: string;
  editable: boolean;
  active?: boolean;
}

export interface IdeWorkspace {
  filesNum: number;
  files: ExerciseFile[];
}
