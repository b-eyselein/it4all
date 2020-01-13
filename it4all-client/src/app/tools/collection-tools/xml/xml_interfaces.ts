export interface ISampleSolution {
  id: number;
  sample: object;
}

export type ElementLine = any;

export interface IXmlSolution {
  document: string;
  grammar: string;
}


export interface IElementLineMatch {
  userArg?: ElementLine;
  sampleArg?: ElementLine;
}


export interface IXmlGrammarResult {
  parseErrors: IDTDParseException[];
  results: IElementLineMatch[];
}


export interface IXmlExerciseContent {
  grammarDescription: string;
  rootNode: string;
  sampleSolutions: ISampleSolution[];
}


export interface IDTDParseException {
  msg: string;
  parsedLine: string;
}


export interface IXmlCompleteResult {
  successType: SuccessType;
  documentResult: IXmlError[];
  grammarResult?: IXmlGrammarResult;
  points: IPoints;
  maxPoints: IPoints;
  solutionSaved: boolean;
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

export type XmlErrorType = ('FATAL' | 'ERROR' | 'WARNING');
export type SuccessType = ('ERROR' | 'NONE' | 'PARTIALLY' | 'COMPLETE');
