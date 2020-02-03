
export interface IOrderByMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IOrderByMatch[];
  points: number;
  maxPoints: number;
}


export interface IAdditionalComparison {
  selectComparisons?: ISelectAdditionalComparisons;
  insertComparison?: IExpressionListMatchingResult;
}


export interface IExpressionListMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IExpressionListMatch[];
  points: number;
  maxPoints: number;
}


export interface ISqlQueriesStaticComparison {
  columnComparison: IColumnMatchingResult;
  tableComparison: ITableMatchingResult;
  joinExpressionComparison: IBinaryExpressionMatchingResult;
  whereComparison: IBinaryExpressionMatchingResult;
  additionalComparisons: IAdditionalComparison;
}


export interface IGroupByMatch {
  matchType: MatchType;
  userArg?: string;
  sampleArg?: string;
}


export interface IBinaryExpressionMatch extends IMatch {
  matchType: MatchType;
  userArg?: string;
  sampleArg?: string;
}


export interface ILimitMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: ILimitMatch[];
  points: number;
  maxPoints: number;
}


export interface ITableMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: ITableMatch[];
  points: number;
  maxPoints: number;
}


export interface IColumnMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IColumnMatch[];
  points: number;
  maxPoints: number;
}

export type SqlExerciseType = ("SELECT" | "CREATE" | "UPDATE" | "INSERT" | "DELETE");

export interface ISelectAdditionalComparisons {
  groupByComparison: IGroupByMatchingResult;
  orderByComparison: IOrderByMatchingResult;
  limitComparison: ILimitMatchingResult;
}


export interface ISqlExerciseContent {
  exerciseType: SqlExerciseType;
  hint?: string;
  sampleSolutions: ISqlSampleSolution[];
}


export interface IColumnMatch {
  matchType: MatchType;
  userArg?: string;
  sampleArg?: string;
}


export interface ISqlResult {
  staticComparison: ISqlQueriesStaticComparison;
  executionResult: ISqlExecutionResult;
  solutionSaved: boolean;
}


export interface IBinaryExpressionMatchingResult extends IMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IBinaryExpressionMatch[];
  points: number;
  maxPoints: number;
}


export interface IOrderByMatch {
  matchType: MatchType;
  userArg?: string;
  sampleArg?: string;
}


export interface ISqlRow {
  cells: { [ key: string ]: ISqlCell };
}


export interface IExpressionListMatch {
  matchType: MatchType;
  userArg?: string;
  sampleArg?: string;
}


export interface ITableMatch {
  matchType: MatchType;
  userArg?: string;
  sampleArg?: string;
}

export type MatchType = ("SUCCESSFUL_MATCH" | "PARTIAL_MATCH" | "UNSUCCESSFUL_MATCH" | "ONLY_USER" | "ONLY_SAMPLE");

export interface ISqlQueryResult {
  columnNames: string[];
  rows: ISqlRow[];
  tableName: string;
}


export interface ISqlCell {
  colName: string;
  content: string;
}


export interface ILimitMatch {
  matchType: MatchType;
  userArg?: string;
  sampleArg?: string;
}


export interface IMatch {
  matchType: MatchType;
  userArg?: any;
  sampleArg?: any;
}


export interface IMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IMatch[];
  points: number;
  maxPoints: number;
}


export interface ISqlSampleSolution {
  id: number;
  sample: string;
}


export interface ISqlExecutionResult {
  userResultTry?: ISqlQueryResult;
  sampleResultTry?: ISqlQueryResult;
}


export interface IGroupByMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IGroupByMatch[];
  points: number;
  maxPoints: number;
}
