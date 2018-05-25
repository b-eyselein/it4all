import {AnalysisResult, CorrectionResult, Match} from "../matches";

export {renderXmlGrammarCorrectionSuccess}

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

interface XmlGrammarCorrectionResult extends CorrectionResult<XmlElementMatch> {
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

function renderXmlGrammarCorrectionSuccess(response: XmlGrammarCorrectionResult): string {
    let html: string = '';

    if (response.solutionSaved) {
        html += `<span class="text-success">Ihre Lösung wurde gespeichert.</span>`;
    } else {
        html += `<span class="text-danger">Ihre Lösung konnte nicht gespeichert werden!</span>`;
    }

    if (response.success) {
        html += `<span class="text-success"> Die Korrektur war komplett erfolgreich.</span>`;
    } else {
        html += response.results.map(renderElementMatch).join('\n');
    }

    return html;
}
