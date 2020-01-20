export interface IXmlError {
  errorType: XmlErrorType;
  errorMessage: string;
  line: number;
  success: SuccessType;
}


export interface IElementLineMatch {
  userArg?: ElementLine;
  sampleArg?: ElementLine;
  maybeAnalysisResult?: IElementLineAnalysisResult;
}


export interface IXmlSolution {
  document: string;
  grammar: string;
}

export type ElementLine = object;
export type MatchType = ('SUCCESSFUL_MATCH' | 'PARTIAL_MATCH' | 'UNSUCCESSFUL_MATCH' | 'ONLY_USER' | 'ONLY_SAMPLE');

export interface IXmlExerciseContent {
  grammarDescription: string;
  rootNode: string;
  sampleSolutions: ISampleSolution[];
}


export interface IDTDParseException {
  msg: string;
  parsedLine: string;
}


export interface IXmlGrammarResult {
  parseErrors: IDTDParseException[];
  results: IElementLineMatch[];
}


export interface IElementLineAnalysisResult {
  matchType: MatchType;
  contentCorrect: boolean;
  correctContent: string;
  attributesCorrect: boolean;
  correctAttributes: string;
}


export interface ISampleSolution {
  id: number;
  sample: object;
}


export interface IPoints {
  quarters: number;
}


export interface IXmlCompleteResult {
  successType: SuccessType;
  documentResult: IXmlError[];
  grammarResult?: IXmlGrammarResult;
  points: IPoints;
  maxPoints: IPoints;
  solutionSaved: boolean;
}

export type XmlErrorType = ('FATAL' | 'ERROR' | 'WARNING');
export type SuccessType = ('ERROR' | 'NONE' | 'PARTIALLY' | 'COMPLETE');
