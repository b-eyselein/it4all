
export interface IGradedJsHtmlElementSpecResult {
  id: number;
  success: SuccessType;
  elementFound: boolean;
  textContentResult?: IGradedTextResult;
  attributeResults: IGradedTextResult[];
  isSuccessful: boolean;
  points: IPoints;
  maxPoints: IPoints;
}


export interface IGradedTextResult {
  keyName: string;
  awaitedContent: string;
  maybeFoundContent?: string;
  isSuccessful: boolean;
  points: IPoints;
  maxPoints: IPoints;
}


export interface IWebCompleteResult {
  gradedHtmlTaskResults: IGradedHtmlTaskResult[];
  gradedJsTaskResults: IGradedJsTaskResult[];
  points: IPoints;
  maxPoints: IPoints;
  solutionSaved: boolean;
}


export interface IGradedJsActionResult {
  actionPerformed: boolean;
  jsAction: IJsAction;
  points: IPoints;
  maxPoints: IPoints;
}


export interface IPoints {
  quarters: number;
}


export interface IGradedHtmlTaskResult {
  id: number;
  success: SuccessType;
  elementFound: boolean;
  textContentResult?: IGradedTextResult;
  attributeResults: IGradedTextResult[];
  isSuccessful: boolean;
  points: IPoints;
  maxPoints: IPoints;
}

export type JsActionType = ("Click" | "FillOut");

export interface IGradedJsTaskResult {
  id: number;
  gradedPreResults: IGradedJsHtmlElementSpecResult[];
  gradedJsActionResult: IGradedJsActionResult;
  gradedPostResults: IGradedJsHtmlElementSpecResult[];
  success: SuccessType;
  points: IPoints;
  maxPoints: IPoints;
}


export interface IJsAction {
  xpathQuery: string;
  actionType: JsActionType;
  keysToSend?: string;
}

export type SuccessType = ("ERROR" | "NONE" | "PARTIALLY" | "COMPLETE");