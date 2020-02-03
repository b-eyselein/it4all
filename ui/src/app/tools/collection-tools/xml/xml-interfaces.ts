
export interface IXmlCompleteResult {
  successType: SuccessType;
  documentResult: IXmlError[];
  grammarResult?: IXmlGrammarResult;
  points: IPoints;
  maxPoints: IPoints;
  solutionSaved: boolean;
}


export interface IElementLineAnalysisResult {
  contentCorrect: boolean;
  correctContent: string;
  attributesCorrect: boolean;
  correctAttributes: string;
}


export interface IElementLineMatch {
  matchType: MatchType;
  userArg?: IElementLine;
  sampleArg?: IElementLine;
  maybeAnalysisResult?: IElementLineAnalysisResult;
}


export interface IDTDParseException {
  msg: string;
  parsedLine: string;
}


export interface IPoints {
  quarters: number;
}


export interface IXmlExerciseContent {
  grammarDescription: string;
  rootNode: string;
  sampleSolutions: IXmlSampleSolution[];
}


export interface IXmlError {
  errorType: XmlErrorType;
  errorMessage: string;
  line: number;
  success: SuccessType;
}


export interface IElementLineMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IElementLineMatch[];
  points: number;
  maxPoints: number;
}


export interface IXmlSolution {
  document: string;
  grammar: string;
}


export interface IXmlSampleSolution {
  id: number;
  sample: IXmlSolution;
}

export type XmlErrorType = ("FATAL" | "ERROR" | "WARNING");

export interface IXmlGrammarResult {
  parseErrors: IDTDParseException[];
  results: IElementLineMatchingResult;
}

export type SuccessType = ("ERROR" | "NONE" | "PARTIALLY" | "COMPLETE");

export interface IElementLine {
  elementName: string;
  elementDefinition: string;
  attributeLists: object[];
}

export type MatchType = ("SUCCESSFUL_MATCH" | "PARTIAL_MATCH" | "UNSUCCESSFUL_MATCH" | "ONLY_USER" | "ONLY_SAMPLE");