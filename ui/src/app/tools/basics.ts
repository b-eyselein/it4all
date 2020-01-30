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
