
export interface ISiteSpec {
  fileName: string;
  htmlTasks: IHtmlTask[];
  jsTasks: IJsTask[];
}


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


export interface IJsTask {
  id: number;
  text: string;
  preConditions: IJsHtmlElementSpec[];
  action: IJsAction;
  postConditions: IJsHtmlElementSpec[];
}


export interface IJsHtmlElementSpec {
  id: number;
  xpathQuery: string;
  awaitedTagName: string;
  awaitedTextContent?: string;
  attributes: { [ key: string ]: string };
}


export interface IWebCompleteResult {
  gradedHtmlTaskResults: IGradedHtmlTaskResult[];
  gradedJsTaskResults: IGradedJsTaskResult[];
  points: IPoints;
  maxPoints: IPoints;
  solutionSaved: boolean;
}


export interface IHtmlTask {
  id: number;
  text: string;
  xpathQuery: string;
  awaitedTagName: string;
  awaitedTextContent?: string;
  attributes: { [ key: string ]: string };
}


export interface IExerciseFile {
  name: string;
  resourcePath: string;
  fileType: string;
  editable: boolean;
  content: string;
  active?: boolean;
}


export interface IGradedJsActionResult {
  actionPerformed: boolean;
  jsAction: IJsAction;
  points: IPoints;
  maxPoints: IPoints;
}


export interface ISampleSolution {
  id: number;
  sample: object;
}


export interface IPoints {
  quarters: number;
}


export interface IWebExerciseContent {
  htmlText?: string;
  jsText?: string;
  siteSpec: ISiteSpec;
  files: IExerciseFile[];
  sampleSolutions: ISampleSolution[];
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