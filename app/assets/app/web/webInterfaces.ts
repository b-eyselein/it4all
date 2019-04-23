import {ExerciseFile} from "../tools/ideExerciseHelpers";

export interface WebSampleSolution {
    id: number;
    sample: ExerciseFile[];
}

export interface WebCompleteResult {
    solutionSaved: boolean
    part: 'html' | 'js'

    htmlResults: HtmlResult[]
    jsResults: JsResult[]

    success: boolean
    points: number
    maxPoints: number
}

export interface WebResult {
    id: number

    success: boolean
    points: number
    maxPoints: number
}

export interface HtmlResult extends WebResult {
    elementFound: boolean
    textContentResult: TextResult | null
    attributeResults: TextResult[]
}

export interface TextResult {
    keyName: string
    awaitedContent: string
    maybeFoundContent: string | null
    isSuccessful: boolean
    points: number
    maxPoints: number
}

export interface JsResult extends WebResult {
    preResults: ConditionResult[]
    actionDescription: string
    actionPerformed: boolean
    postResults: ConditionResult[]
}

export interface ConditionResult {
    points: number
    maxPoints: number
    success: boolean
    description: string
    awaited: string
    gotten: string
}
