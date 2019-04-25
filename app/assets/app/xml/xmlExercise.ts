import * as CodeMirror from 'codemirror';
import 'codemirror/mode/xml/xml';
import 'codemirror/mode/dtd/dtd';
import {initEditor} from '../editorHelpers';

import {focusOnCorrection, testTextExerciseSolution} from '../textExercise';
import {domReady, escapeHtml, initShowSampleSolBtn, SampleSolution} from "../otherHelpers";

import {renderXmlGrammarCorrectionSuccess, XmlGrammarCorrectionResult} from './xmlGrammarCorrection';
import {renderXmlDocumentCorrection, XmlDocumentCorrectionResult} from './xmlDocumentCorrection';

let editor: CodeMirror.Editor;

let testBtn: HTMLButtonElement;

let solutionChanged: boolean;
let isDocumentPart: boolean;


interface XmlSampleSolution extends SampleSolution<{ document: string, grammar: string }> {
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
    const learnerSolution: string = editor.getValue();

    if (learnerSolution.length === 0) {
        alert("Sie können keine leere Query abgeben!");
        return;
    }

    testBtn.disabled = true;

    testTextExerciseSolution<string, XmlGrammarCorrectionResult | XmlDocumentCorrectionResult>(
        testBtn, learnerSolution, reponse => onXmlCorrectionSuccess(isDocumentPart, reponse));
}

function renderXmlSample(xmlSample: XmlSampleSolution): string {
    if (isDocumentPart) {
        return escapeHtml(xmlSample.sample.document);
    } else {
        return escapeHtml(xmlSample.sample.grammar);
    }
}

function displayXmlSampleSolution(xmlSampleSolution: XmlSampleSolution): string {
    return `
<div class="card my-3">
    <div class="card-body bg-light">
        <pre>${renderXmlSample(xmlSampleSolution)}</pre>
    </div>
</div>`.trim();
}

domReady(() => {
    initShowSampleSolBtn<XmlSampleSolution[]>(xmlSamples =>
        xmlSamples.map(displayXmlSampleSolution).join('\n')
    );

    isDocumentPart = document.querySelector<HTMLInputElement>('#exercisePart').value === 'document';
    const language: string = isDocumentPart ? 'xml' : 'application/xml-dtd';

    editor = initEditor(language, 'textEditor');
    editor.on('change', () => {
        solutionChanged = true;
    });

    document.querySelector<HTMLAnchorElement>('#endSolveAnchor').onclick = () => {
        return !solutionChanged || confirm("Ihre Lösung hat sich seit dem letzten Speichern (Korrektur) geändert. Wollen Sie die Bearbeitung beenden?");
    };

    testBtn = document.querySelector<HTMLButtonElement>('#testBtn');
    testBtn.onclick = testSol;
});
