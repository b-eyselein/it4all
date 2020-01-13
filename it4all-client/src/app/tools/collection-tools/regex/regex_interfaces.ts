
export interface ISampleSolution {
  id: number;
  sample: object;
}


export interface IRegexMatchTestData {
  id: number;
  data: string;
  isIncluded: boolean;
}

export type RegexCorrectionTypes = ("MATCHING" | "EXTRACTION");

export interface IRegexExtractionTestData {
  id: number;
  base: string;
}


export interface IRegexExerciseContent {
  maxPoints: number;
  correctionType: RegexCorrectionTypes;
  sampleSolutions: ISampleSolution[];
  matchTestData: IRegexMatchTestData[];
  extractionTestData: IRegexExtractionTestData[];
}
