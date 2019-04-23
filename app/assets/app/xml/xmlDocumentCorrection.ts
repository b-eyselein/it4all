import {CorrectionResult} from "../matches";

interface XmlError {
    errorType: 'WARNING' | 'ERROR' | 'FATAL',
    errorMessage: string,
    line: number,
    success: string
}

export interface XmlDocumentCorrectionResult extends CorrectionResult<XmlError> {
    results: XmlError[]
}

export function renderXmlDocumentCorrection(response: XmlDocumentCorrectionResult): string {
    let html: string = '';

    if (response.solutionSaved) {
        html += `<div class="alert alert-success">Ihre Lösung wurde gespeichert.</div>`;
    } else {
        html += `<div class="alert alert-danger">Ihre Lösung konnte nicht gespeichert werden!</div>`;
    }

    if (response.success) {
        html += `<div class="alert alert-success">Die Korrektur war erfolgreich. Es wurden keine Fehler gefunden.</div>`;
    } else {
        html += response.results.map((xmlError: XmlError) => {
            const cls = xmlError.errorType === 'WARNING' ? 'warning' : 'danger';
            return `<div class="alert alert-${cls}"><b>Fehler in Zeile ${xmlError.line}</b>: ${xmlError.errorMessage}</div>`;
        }).join('\n');
    }

    return html;
}
