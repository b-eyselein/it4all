export {AnalysisResult, CorrectionResult, Match};

interface AnalysisResult {
    success: string
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