import {
    BinaryClassificationResultType,
    displayStringSampleSolution,
    domReady,
    focusOnCorrection,
    initShowSampleSolBtn,
    StringSampleSolution,
    testExerciseSolution
} from "../otherHelpers";

import {AnalysisResult, Match, MatchingResult} from "../matches";

let solutionInput: HTMLInputElement;

let testBtn: HTMLButtonElement;

let solutionChanged: boolean = false;

type RegexCorrectionType = 'MATCHING' | 'EXTRACTION';


interface RegexMatchingResult {
    matchData: string;
    isIncluded: boolean;
    resultType: BinaryClassificationResultType;
}

interface RegexExtractionMatch {
    start: number;
    end: number;
    content: string;
}

interface RegexExtractionMatchingResult extends MatchingResult<RegexExtractionMatch, AnalysisResult> {
    allMatches: Match<RegexExtractionMatch, AnalysisResult>[];
}

interface RegexExtractionResult {
    base: string;
    extractionMatchingResult: RegexExtractionMatchingResult;
    correct: boolean;
}

interface RegexCorrectionResult {
    correctionType: RegexCorrectionType;
    solutionSaved: boolean;
    solution: string;
    points: number;
    maxPoints: number;
    matchingResults: RegexMatchingResult[];
    extractionResults: RegexExtractionResult[];
}

function onRegexCorrectionSuccess(correctionResult: RegexCorrectionResult): void {
    testBtn.disabled = false;

    //  console.warn(JSON.stringify(correctionResult, null, 2));

    solutionChanged = false;

    let html: string = '';

    html += `
<p class="${correctionResult.solutionSaved ? 'text-success' : 'text-danger'}">
    Ihre Lösung wurde ${correctionResult.solutionSaved ? '' : 'nicht '}gespeichert.
</p>
<p>
    Sie haben ${correctionResult.points} von ${correctionResult.maxPoints} Punkten erreicht.
</p>`;

    // Single results
    for (const result of correctionResult.matchingResults) {
        let toAdd: string;
        let clazz: string = 'text-warning';

        switch (result.resultType) {
            case 'TruePositive':
                toAdd = 'wurde korrekt erkannt.';
                clazz = 'text-success';
                break;
            case 'FalsePositive':
                toAdd = 'wurde fälschlicherweise erkannt.';
                clazz = 'text-danger';
                break;
            case 'FalseNegative':
                toAdd = 'wurde fälschlicherweise <b>nicht</b> erkannt.';
                clazz = 'text-danger';
                break;
            case 'TrueNegative':
                toAdd = 'wurde korrekt <b>nicht</b> erkannt.';
                clazz = 'text-success';
                break;
        }
        html += `<p class="${clazz}"><code>${result.matchData}</code> ${toAdd}</p>`;
    }

    for (const result of correctionResult.extractionResults) {

        console.info(JSON.stringify(result.extractionMatchingResult.allMatches, null, 2));

        const matches = '<ul>' + result.extractionMatchingResult.allMatches.map(m => {
            const clazz = (m.analysisResult && m.analysisResult.success == 'SUCCESSFUL_MATCH') ? 'success' : 'danger';

            return `
<li>
    <b>Erwartet:</b> &quot;<span>${m.sampleArg ? m.sampleArg.content : ''}</span>&quot;,
    <b>Gefunden:</b> &quot;<span class="text-${clazz}">${m.userArg ? m.userArg.content : ''}</span>&quot;
</li>`.trim()
        }).join('\n') + '</ul>';

        html += `
<div class="card my-3">
    <div class="card-body">
        <p>${result.base}</p>
        ${matches}
    </div>
</div>`;

    }

    document.querySelector<HTMLDivElement>('#correctionDiv').innerHTML = html;
    focusOnCorrection();
}

function testSol(): void {
    const learnerSolution: string = solutionInput.value.trim();

    if (learnerSolution.length === 0) {
        alert('Sie können keine leere Lösung abgeben!');
        return;
    }

    testExerciseSolution<string, RegexCorrectionResult>(testBtn, learnerSolution, onRegexCorrectionSuccess);
}

domReady(() => {
    initShowSampleSolBtn<StringSampleSolution[]>(regexSampleSolutions =>
        regexSampleSolutions.map(displayStringSampleSolution).join('\n')
    );

    solutionInput = document.querySelector<HTMLInputElement>('#solutionInput');
    solutionInput.onchange = () => {
        solutionChanged = true;
    };

    document.querySelector<HTMLAnchorElement>('#endSolveAnchor').onclick = () => {
        return !solutionChanged || confirm('Ihre Lösung hat sich seit dem letzten Speichern (Korrektur) geändert. Wollen Sie die Bearbeitung beenden?');
    };

    testBtn = document.querySelector<HTMLButtonElement>('#testBtn');
    testBtn.onclick = testSol;
});
