export {AnalysisResult, CorrectionResult, MatchingResult, Match};

interface AnalysisResult {
    success: string
}

interface MatchingResult<T, AR> {
    matchName: string
    matchSingularName: string
    success: boolean
    matches: Match<T, AR>[]
}

interface Match<T, AR> {
    matchType: 'SUCCESSFUL_MATCH' | 'PARTIAL_MATCH' | 'UNSUCCESSFUL_MATCH' | 'ONLY_USER' | 'ONLY_SAMPLE'
    analysisResult: AR
    userArg: T
    sampleArg: T
}

interface CorrectionResult<R> {
    solutionSaved: boolean
    success: boolean
    results: R[]

    points: number
    maxPoints: number
}