import {ProgCorrectionResult, UnitTestCorrectionResult} from "./progImplementationCorrectionHandler";
import {focusOnCorrection} from "../otherHelpers";

export function renderUnitTestCorrectionResult(result: ProgCorrectionResult): string {

    let html = '';

    if (result.solutionSaved) {
        html += `<p class="text-success">Ihre Lösung wurde gespeichert.</p>`;
    } else {
        html += `<p class="text-danger">Ihre Lösung konnte nicht gespeichert werden.</p>`;
    }

    const numOfSuccessfulTests: number = result.unitTestResults.filter(r => r.successful).length;
    const progressPercentage: number = Math.round(numOfSuccessfulTests * 100 / result.unitTestResults.length);

    let progressBarColor: string = 'bg-danger';
    if (50 <= progressPercentage && progressPercentage < 100) {
        progressBarColor = 'bg-warning'
    } else if (progressPercentage == 100) {
        progressBarColor = 'bg-success';
    }

    html += `
<p class="text-${progressBarColor}">Sie haben ${numOfSuccessfulTests} von ${result.unitTestResults.length} Tests bestanden.</p>
<div class="progress">
    <div class="progress-bar ${progressBarColor}" style="width: ${progressPercentage}%;">${progressPercentage}%</div>
</div>
`.trim();

    html += '<hr>';

    // FIXME: points === undefinded, maxPoints = undefined!
    console.info(result.points, result.maxPoints);

    html += '<ul>' + result.unitTestResults
        .map((unitTestCorrectionResult: UnitTestCorrectionResult) => {
            const successClass = unitTestCorrectionResult.successful ? 'success' : 'danger';
            const successWord = unitTestCorrectionResult.successful ? '' : 'nicht '

            let description = '';
            if (!unitTestCorrectionResult.successful) {
                description += `<p>${unitTestCorrectionResult.testConfig.description}</p>`
            }

            return `
<li>
    <p class="text-${successClass}">Der ${unitTestCorrectionResult.testConfig.id}. Test war ${successWord} erfolgreich.</p>
    ${description}
</li>`.trim();
        })
        .join("\n") + '</ul>';

    return html;

}

