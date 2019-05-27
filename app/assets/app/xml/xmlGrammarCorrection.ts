import {AnalysisResult, CorrectionResult, Match} from "../matches";
import {SuccessType} from "../otherHelpers";

export {renderXmlGrammarCorrectionSuccess, XmlGrammarCorrectionResult}

interface XmlElementAnalysisResult extends AnalysisResult {
    contentCorrect: boolean
    correctContent: string

    attributesCorrect: boolean
    correctAttributes: string
}

interface XmlElement {
    name: string
    content: string
}

interface XmlElementMatch extends Match<XmlElement, XmlElementAnalysisResult> {
}

interface ParseError {
    parsedLine: string
    msg: string
}

interface XmlGrammarCorrectionResult extends CorrectionResult<XmlElementMatch> {
    successType: SuccessType;
    parseErrors: ParseError[];
}

function escapeXML(unescapedXML: string): string {
    return unescapedXML.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

function renderContentComparison(em: XmlElementMatch): string {
    let html = '';

    if (em.analysisResult.contentCorrect) {
        html += `<li><span class="text-success">Der Inhalt des Elements war korrekt.</span></li>`;
    } else {
        html += `<li><span class="text-danger">Der Inhalt des Elements war nicht korrekt. Erwartet wurde <code>${em.analysisResult.correctContent}</code>.</span></li>`;
    }

    if (em.analysisResult.attributesCorrect) {
        html += `<li><span class="text-success">Der Vergleich der Attribute war korrekt.</span></li>`;
    } else {
        html += `<li><span class="text-danger">Der Vergleich der Attribute war nicht korrekt. Erwartet wurde <code>${escapeXML(em.analysisResult.correctAttributes)}</code></span></li>`;
    }

    return html;
}

function renderElementMatch(em: XmlElementMatch): string {
    switch (em.matchType) {
        case 'ONLY_SAMPLE':
            return `<p class="text-danger">Die Definition des Elements <code>${em.sampleArg.name}</code> fehlt!</p>`;
        case 'ONLY_USER':
            return `<p class="text-danger">Die Definition des Elements <code>${em.userArg.name}</code> ist falsch!</p>`;
        case 'UNSUCCESSFUL_MATCH':
        case 'PARTIAL_MATCH':
            return `
<p class="text-danger">Der Vergleich des Arguments <code>${em.userArg.name}</code> war nicht erfolgreich:</p>
<ul>${renderContentComparison(em)}</ul>`.trim();
        case 'SUCCESSFUL_MATCH':
            return `<p class="text-success">Das Element <code>${em.userArg.name}</code> ist komplett korrekt.</p>`;
        default:
            return `<p class="text-info">TODO: ${em.matchType}!</p>`;
    }
}

function renderParseErrors(parseErrors: ParseError[]): string {
    const renderedErrors = parseErrors.map((x) => {
        const escapedParsed = x.parsedLine.replace(/</g, '&lt;').replace(/>/g, '&gt;');
        return `<p><div>Fehler beim Parsen von &quot;<code>${escapedParsed}</code>&quot;:</div><div class="text-danger">${x.msg}</div></p>`;
    }).join("\n");

    return `
<div>
    <h2>Parsingfehler</h2>
    ${renderedErrors}
</div>`.trim();
}

function renderXmlGrammarCorrectionSuccess(response: XmlGrammarCorrectionResult): string {
    let html: string = '';

    console.info(JSON.stringify(response, null, 2));

    if (response.solutionSaved) {
        html += `<p class="text-success">Ihre Lösung wurde gespeichert.</p>`;
    } else {
        html += `<p class="text-danger">Ihre Lösung konnte nicht gespeichert werden!</p>`;
    }

    let successClazz: string = 'danger';
    let successWord: string = 'nicht';

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
        default:
            successClazz = 'danger';
            successWord = 'nicht';
            break;
    }

    const progress: number = Math.round(response.points / response.maxPoints * 100);

    html += `
<p class="text-${successClazz}">Die Korrektur war ${successWord} erfolgreich. Sie haben ${response.points} von ${response.maxPoints} erreicht.</p>
<div class="progress">
    <div class="progress-bar bg-${successClazz}" style="width: ${progress}%">${progress}%</div>
</div>`.trim();

    if (response.parseErrors.length !== 0) {
        html += '<hr>' + renderParseErrors(response.parseErrors);
    }

    if (response.successType !== 'COMPLETE') {
        html += '<hr>' + response.results.map(renderElementMatch).join('\n')
    }

    return html;
}
