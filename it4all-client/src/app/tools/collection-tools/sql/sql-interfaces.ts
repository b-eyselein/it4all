
export interface ISampleSolution {
  id: number;
  sample: object;
}


export interface IGroupByMatch {
  userArg?: string;
  sampleArg?: string;
  analysisResult?: IGenericAnalysisResult;
}


export interface ILimitMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: ILimitMatch[];
  points: number;
  maxPoints: number;
}


export interface ISqlResult {
  columnComparison: IColumnMatchingResult;
  tableComparison: ITableMatchingResult;
  joinExpressionComparison: IBinaryExpressionMatchingResult;
  whereComparison: IBinaryExpressionMatchingResult;
  additionalComparisons: IAdditionalComparison;
  executionResult: ISqlExecutionResult;
  solutionSaved: boolean;
}


export interface ISqlExecutionResult {
  userResultTry?: ISqlQueryResult;
  sampleResultTry?: ISqlQueryResult;
}


export interface IGenericAnalysisResult {
  matchType: MatchType;
}


export interface IExpressionListMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IExpressionListMatch[];
  points: number;
  maxPoints: number;
}


export interface ISqlRow {
  cells: { [ key: string ]: ISqlCell };
}


export interface IBinaryExpressionMatch {
  userArg?: string;
  sampleArg?: string;
  analysisResult?: IGenericAnalysisResult;
}


export interface ISqlExerciseContent {
  exerciseType: SqlExerciseType;
  hint?: string;
  sampleSolutions: ISampleSolution[];
}


export interface IGroupByMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IGroupByMatch[];
  points: number;
  maxPoints: number;
}


export interface IBinaryExpressionMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IBinaryExpressionMatch[];
  points: number;
  maxPoints: number;
}


export interface IColumnMatch {
  userArg?: string;
  sampleArg?: string;
  analysisResult?: IGenericAnalysisResult;
}


export interface IOrderByMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IOrderByMatch[];
  points: number;
  maxPoints: number;
}


export interface ISqlCell {
  colName: string;
  content: string;
}


export interface ISqlQueryResult {
  columnNames: string[];
  rows: ISqlRow[];
  tableName: string;
}

export type MatchType = ("SUCCESSFUL_MATCH" | "PARTIAL_MATCH" | "UNSUCCESSFUL_MATCH" | "ONLY_USER" | "ONLY_SAMPLE");

export interface ITableMatch {
  userArg?: string;
  sampleArg?: string;
  analysisResult?: IGenericAnalysisResult;
}


export interface IExpressionListMatch {
  userArg?: string;
  sampleArg?: string;
  analysisResult?: IGenericAnalysisResult;
}


export interface ITableMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: ITableMatch[];
  points: number;
  maxPoints: number;
}


export interface IAdditionalComparison {
  selectComparisons?: ISelectAdditionalComparisons;
  insertComparison?: IExpressionListMatchingResult;
}


export interface IOrderByMatch {
  userArg?: string;
  sampleArg?: string;
  analysisResult?: IGenericAnalysisResult;
}


export interface ISelectAdditionalComparisons {
  groupByComparison: IGroupByMatchingResult;
  orderByComparison: IOrderByMatchingResult;
  limitComparison: ILimitMatchingResult;
}

export type SqlExerciseType = ("SELECT" | "CREATE" | "UPDATE" | "INSERT" | "DELETE");

export interface ILimitMatch {
  userArg?: string;
  sampleArg?: string;
  analysisResult?: IGenericAnalysisResult;
}


export interface IColumnMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IColumnMatch[];
  points: number;
  maxPoints: number;
}
