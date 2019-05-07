import {CorrectionResult} from "../matches";
import {SuccessType} from "../otherHelpers";

type XmlErrorType = 'WARNING' | 'ERROR' | 'FATAL';

interface XmlError {
    errorType: XmlErrorType;
    errorMessage: string;
    line: number;
    success: string;
}

export interface XmlDocumentCorrectionResult extends CorrectionResult<XmlError> {
    successType: SuccessType;
    results: XmlError[];
}

export function renderXmlDocumentCorrection(response: XmlDocumentCorrectionResult): string {
    let html: string = '';

    if (response.solutionSaved) {
        html += `<p class="text-success">Ihre Lösung wurde gespeichert.</p>`;
    } else {
        html += `<p class="text-danger">Ihre Lösung konnte nicht gespeichert werden!</p>`;
    }

    let successClazz = 'danger';
    let successWord = 'nicht';
    let errorWord = '';

    switch (response.successType) {
        case 'COMPLETE':
            successClazz = 'success';
            successWord = 'komplett';
            break;
        case 'PARTIALLY':
            successClazz = 'warning';
            successWord = 'teilweise';
            break;
        case 'NONE':
            successClazz = 'danger';
            successWord = 'nicht';
            break;
    }

    html += `<p class="text-${successClazz}">Die Korrektur war ${successWord} erfolgreich. Es wurden ${errorWord} Fehler gefunden.</p>`;

    if (response.results.length !== 0) {
        html += response.results
            .map((xmlError: XmlError) => {
                const cls = xmlError.errorType === 'WARNING' ? 'warning' : 'danger';
                return `<p class="text-${cls}"><b>Fehler in Zeile ${xmlError.line}</b>: ${xmlError.errorMessage}</p>`;
            })
            .join('\n')
    }

    return html;
}
