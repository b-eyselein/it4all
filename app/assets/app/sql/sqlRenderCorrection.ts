import {AnalysisResult, Match, MatchingResult} from "../matches";

export {ExecutionResultsObject, renderMatchingResult, renderExecution}

interface ExecutionResultsObject {
    success: 'COMPLETE' | 'PARTIALLY' | 'NONE' | 'ERROR'
    userResult: ExecutionTable | null
    sampleResult: ExecutionTable | null
}

interface ExecTableCell {
    content: string
    different: boolean
}

interface ExecTableRow {
    [name: string]: ExecTableCell
}

interface ExecutionTable {
    colNames: string[]
    content: ExecTableRow[]
}

function displayMatchResult(matchingRes: Match<any, any>, matchName: string) {
    let alertClass, glyphicon;

    switch (matchingRes.matchType) {
        case 'SUCCESSFUL_MATCH':
            alertClass = 'success';
            glyphicon = 'ok';
            break;
        case 'PARTIAL_MATCH':
            alertClass = 'warning';
            glyphicon = 'question-sign';
            break;
        case 'UNSUCCESSFUL_MATCH':
            alertClass = 'danger';
            glyphicon = 'remove';
            break;
        case 'ONLY_USER':
            alertClass = 'danger';
            glyphicon = 'remove';
            break;
        case 'ONLY_SAMPLE':
            alertClass = 'danger';
            glyphicon = 'remove';
            break;
        default:
            alertClass = 'info';
            glyphicon = 'remove';
    }

    let mainExplanation = explainMatch(matchingRes, matchName);

    return `<span class="text-${alertClass}">${mainExplanation}</span>`;
}


function renderMatchingResult(matchObj: MatchingResult<string, AnalysisResult>): string {
    const pointStr: string = `(${matchObj.points} / ${matchObj.maxPoints} P)`;

    if (matchObj.success) {
        return `<span class="text-success">${pointStr} Der Vergleich der ${matchObj.matchName} war erfolgreich.</span>`;
    } else {
        return `
<span class="text-danger">${pointStr} Der Vergleich der ${matchObj.matchName} war nicht komplett korrekt:</span>
<ul>
    ${matchObj.matches.map(s => `<li>${displayMatchResult(s, matchObj.matchSingularName)}</li>`).join('\n')}
</ul>`.trim();
    }
}

function renderExecutionResult(executionTable: ExecutionTable): string {
    if (executionTable == null) {
        return `<div class="alert alert-danger">Es gab einen Fehler beim Ausführen Ihrer Lösung!</div>`;
    } else {
        let tableBody: string = '';
        for (let row of executionTable.content) {
            let cells: string[] = [];

            for (const colName of executionTable.colNames) {
                const cell = row[colName];
                cells.push(`<td${cell.different ? '' : ` class="table-danger"`}>${cell.content}</td>`)
            }

            tableBody += `<tr>${cells.join('')}</tr>`;
        }

        return `
<table class="table table-striped table-bordered">
    <thead>
        <tr>${executionTable.colNames.map((cn) => `<td>${cn}</td>`).join('')}</tr>
    </thead>
    <tbody>${tableBody}</tbody>
</table>`;
    }
}

function renderExecution(executionResults: ExecutionResultsObject): string {
    return `
<h3 class="text-center">Vergleich der Ergebnistabellen</h3>
<div class="row">
    <div class="col-sm-6">
        <h4 class="text-center">Nutzer</h4>
        ${renderExecutionResult(executionResults.userResult)}
    </div>
    <div class="col-sm-6">
        <h4 class="text-center">Muster</h4>
        ${renderExecutionResult(executionResults.sampleResult)}
    </div>
</div>`.trim();
}

function explainMatch(aMatch: Match<any, any>, matchName: string): string {
    switch (aMatch.matchType) {
        case 'SUCCESSFUL_MATCH':
            return `Die Angabe ${matchName} <code>${aMatch.userArg}</code> war richtig.`;
        case 'PARTIAL_MATCH':
        case 'UNSUCCESSFUL_MATCH':
            return `Die Angabe ${matchName} <code>${aMatch.userArg}</code> war nicht komplett richtig. Erwartet wurde <code>${aMatch.sampleArg}</code>.`;
        case 'ONLY_SAMPLE':
            return `Die Angabe ${matchName} <code>${aMatch.sampleArg}</code> fehlt!`;
        case 'ONLY_USER':
            return `Die Angabe ${matchName} <code>${aMatch.userArg}</code> ist falsch!`;
        default:
            return 'Es gab einen Fehler!';
    }
}
