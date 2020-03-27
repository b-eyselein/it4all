export interface CorrectionResult<R> {
  solutionSaved: boolean;
  isSuccessful: boolean;
  // results: R[];

  points: number;
  maxPoints: number;
}

