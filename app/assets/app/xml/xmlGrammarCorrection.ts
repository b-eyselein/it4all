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
            return `<li><span class="text-danger">Die Definition des Elements <code>${em.sampleArg.name}</code> fehlt!</span></li>`;
        case 'ONLY_USER':
            return `<li><span class="text-danger">Die Definition des Elements <code>${em.userArg.name}</code> ist falsch!</span></li>`;
        case 'UNSUCCESSFUL_MATCH':
        case 'PARTIAL_MATCH':
            return `
<li>
    <span class="text-danger">Der Vergleich des Arguments <code>${em.userArg.name}</code> war nicht erfolgreich:</span>
    <ul>${renderContentComparison(em)}</ul>
</li>`.trim();
        case 'SUCCESSFUL_MATCH':
            return `<li><span class="text-success">Das Element <code>${em.userArg.name}</code> ist komplett korrekt.</span></li>`;
        default:
            return `<li><span class="text-info">TODO: ${em.matchType}!</span></li>`;
    }
}

function renderXmlGrammarCorrectionSuccess(response: XmlGrammarCorrectionResult): string {
    console.log(JSON.stringify(response, null, 2));

    let html: string = '';

    if (response.solutionSaved) {
        html += `<div class="alert alert-success">Ihre Lösung wurde gespeichert.</div>`;
    } else {
        html += `<div class="alert alert-danger">Ihre Lösung konnte nicht gespeichert werden!</div>`;
    }

    if (response.success) {
        html += `<div class="alert alert-success">Die Korrektur war komplett erfolgreich.</div>`;
    } else {
        html += `
<div class="panel panel-danger">
    <div class="panel-heading">Korrekturergebnisse</div>
    <div class="panel-body">
        <ul>${response.results.map(renderElementMatch).join('\n')}</ul>
    </div>
</div>`.trim();
    }

    return html;
}
