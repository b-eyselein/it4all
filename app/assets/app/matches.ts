export type MatchType = 'SUCCESSFUL_MATCH' | 'PARTIAL_MATCH' | 'UNSUCCESSFUL_MATCH' | 'ONLY_USER' | 'ONLY_SAMPLE';

export interface AnalysisResult {
    success: string
}

export interface MatchingResult<T, AR> {
    matchName: string
    matchSingularName: string

    success: boolean
    matches: Match<T, AR>[]

    points: number
    maxPoints: number
}

export interface Match<T, AR> {
    matchType: MatchType;
    analysisResult: AR | null;
    userArg: T | null
    sampleArg: T | null
}

export interface CorrectionResult<R> {
    solutionSaved: boolean
    success: boolean
    results: R[]

    points: number
    maxPoints: number
}
