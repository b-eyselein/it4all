import {CorrectionResult} from '../../basics';

export interface WebCompleteResult extends CorrectionResult<any> {
  htmlResults: HtmlResult[];
  jsResults: JsResult[];

  success: boolean;
}

export interface WebResult {
  id: number;

  success: boolean;
  points: number;
  maxPoints: number;
}

export interface HtmlResult extends WebResult {
  elementFound: boolean;
  textContentResult: TextResult | null;
  attributeResults: TextResult[];
}

export interface TextResult {
  keyName: string;
  awaitedContent: string;
  maybeFoundContent: string | null;
  isSuccessful: boolean;
  points: number;
  maxPoints: number;
}

export interface JsResult {
  id: number;

  preResults: HtmlResult[];
  actionDescription: string;
  actionPerformed: boolean;
  postResults: HtmlResult[];

  success: boolean;
  points: number;
  maxPoints: number;
}

export interface ConditionResult {
  points: number;
  maxPoints: number;
  success: boolean;
  description: string;
  awaited: string;
  gotten: string;
}

