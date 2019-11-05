import {DbSolution, Exercise, SuccessType} from '../../../_interfaces/tool';
import {AnalysisResult, CorrectionResult, MatchingResult} from '../../basics';

// tslint:disable-next-line:no-empty-interface
export interface SqlExercise extends Exercise {
}

interface ExecutionResultsObject {
  success: SuccessType;
  userResult: ExecutionTable | null;
  sampleResult: ExecutionTable | null;
}

interface ExecTableCell {
  content: string;
  different: boolean;
}

interface ExecTableRow {
  [name: string]: ExecTableCell;
}

interface ExecutionTable {
  colNames: string[];
  content: ExecTableRow[];
}


interface SqlCorrectionResult {
  message: string;

  columnComparisons: MatchingResult<string, AnalysisResult>;
  tableComparisons: MatchingResult<string, AnalysisResult>;
  joinExpressionComparisons: MatchingResult<string, AnalysisResult>;
  whereComparisons: MatchingResult<string, AnalysisResult>;

  additionalComparisons: MatchingResult<string, AnalysisResult>[];

  executionResults: ExecutionResultsObject;
}


export interface SqlResult extends CorrectionResult<SqlCorrectionResult> {
  success: string;
  results: SqlCorrectionResult;
}

export interface DbSqlSolution extends DbSolution<string> {
}
