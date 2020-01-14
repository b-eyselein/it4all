
export interface ISampleSolution {
  id: number;
  sample: object;
}

export type ElementLine = object;
export type MatchType = ("SUCCESSFUL_MATCH" | "PARTIAL_MATCH" | "UNSUCCESSFUL_MATCH" | "ONLY_USER" | "ONLY_SAMPLE");

export interface IXmlCompleteResult {
  successType: SuccessType;
  documentResult: IXmlError[];
  grammarResult?: IXmlGrammarResult;
  points: IPoints;
  maxPoints: IPoints;
  solutionSaved: boolean;
}


export interface IDTDParseException {
  msg: string;
  parsedLine: string;
}


export interface IXmlGrammarResult {
  parseErrors: IDTDParseException[];
  results: IElementLineMatch[];
}


export interface IXmlError {
  errorType: XmlErrorType;
  errorMessage: string;
  line: number;
  success: SuccessType;
}


export interface IPoints {
  quarters: number;
}


export interface IXmlSolution {
  document: string;
  grammar: string;
}


export interface IElementLineAnalysisResult {
  matchType: MatchType;
  contentCorrect: boolean;
  correctContent: string;
  attributesCorrect: boolean;
  correctAttributes: string;
}

export type XmlErrorType = ("FATAL" | "ERROR" | "WARNING");
export type SuccessType = ("ERROR" | "NONE" | "PARTIALLY" | "COMPLETE");

export interface IXmlExerciseContent {
  grammarDescription: string;
  rootNode: string;
  sampleSolutions: ISampleSolution[];
}


export interface IElementLineMatch {
  userArg?: ElementLine;
  sampleArg?: ElementLine;
  analysisResult?: IElementLineAnalysisResult;
}
