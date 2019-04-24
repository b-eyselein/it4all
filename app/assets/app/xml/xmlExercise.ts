import * as CodeMirror from 'codemirror';
import 'codemirror/mode/xml/xml';
import 'codemirror/mode/dtd/dtd';
import {initEditor} from '../editorHelpers';

import {renderXmlGrammarCorrectionSuccess, XmlGrammarCorrectionResult} from './xmlGrammarCorrection';
import {renderXmlDocumentCorrection, XmlDocumentCorrectionResult} from './xmlDocumentCorrection';

import {focusOnCorrection, testTextExerciseSolution} from '../textExercise';
import {domReady, escapeHtml, initShowSampleSolBtn} from "../otherHelpers";

let editor: CodeMirror.Editor;

let testBtn: HTMLButtonElement;

let solutionChanged: boolean;
let isDocumentPart: boolean;


interface XmlSampleSolution {
    id: number;
    sample: {
        document: string;
        grammar: string;
    };
}

function onXmlCorrectionSuccess(isDocumentPart: boolean, response: (XmlGrammarCorrectionResult | XmlDocumentCorrectionResult)): void {
    solutionChanged = false;
    testBtn.disabled = false;

    let html: string;
    if (isDocumentPart) {
        html = renderXmlDocumentCorrection(response as XmlDocumentCorrectionResult);
    } else {
        html = renderXmlGrammarCorrectionSuccess(response as XmlGrammarCorrectionResult);
    }

    document.querySelector<HTMLDivElement>('#correction').innerHTML = html;
    focusOnCorrection();
}

function testSol(): void {
    testBtn.disabled = true;

    const solution: string = editor.getValue();

    testTextExerciseSolution<string, XmlGrammarCorrectionResult | XmlDocumentCorrectionResult>(
        testBtn, solution, reponse => onXmlCorrectionSuccess(isDocumentPart, reponse));
}

function endSolve(): boolean {
    return !solutionChanged || confirm("Ihre Lösung hat sich seit dem letzten Speichern (Korrektur) geändert. Wollen Sie die Bearbeitung beenden?");
}

function renderXmlSample(xmlSample: XmlSampleSolution): string {
    if (isDocumentPart) {
        return escapeHtml(xmlSample.sample.document);
    } else {
        return escapeHtml(xmlSample.sample.grammar);
    }
}

function showXmlSampleSolution(xmlSampleSolutions: XmlSampleSolution[]): string {
    // console.warn(JSON.stringify(xmlSampleSolutions, null, 2));

    return xmlSampleSolutions.map(xmlSampleSol => `
<div class="card">
    <div class="card-body bg-light">
        <pre>${renderXmlSample(xmlSampleSol)}</pre>
    </div>
</div>`).join('\n');
}

domReady(() => {
    isDocumentPart = document.querySelector<HTMLInputElement>('#exercisePart').value === 'document';
    const language: string = isDocumentPart ? 'xml' : 'application/xml-dtd';

    editor = initEditor(language, 'textEditor');
    editor.on('change', () => {
        solutionChanged = true;
    });

    initShowSampleSolBtn<XmlSampleSolution[]>(showXmlSampleSolution);

    testBtn = document.querySelector<HTMLButtonElement>('#testBtn');
    testBtn.onclick = testSol;

    document.querySelector<HTMLAnchorElement>('#endSolveAnchor').onclick = endSolve;
});
