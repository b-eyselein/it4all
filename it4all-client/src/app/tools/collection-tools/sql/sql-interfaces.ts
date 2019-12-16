import {AnalysisResult, CorrectionResult, MatchingResult} from '../../basics';
import {SuccessType} from '../../../_interfaces/tool';

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