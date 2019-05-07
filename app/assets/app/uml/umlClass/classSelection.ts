import {UmlClass, UmlSolution} from '../umlInterfaces';
import {displayMatchingResultList, UmlClassDiagCorrectionResult, UmlClassMatch} from "./classDiagCorrection";

import {domReady, testExerciseSolution} from "../../otherHelpers";

let chosenClasses: string[] = [];

let testBtn: HTMLButtonElement;
let classesList: HTMLUListElement;

const notChosenAsClassClassName = 'text-muted';
const chosenAsClassClassName = 'text-primary';

function explainClassResult(classResult: UmlClassMatch, alertClass: string, glyphicon: string): string {
    let className = classResult.userArg != null ? classResult.userArg.name : classResult.sampleArg.name;

    switch (classResult.matchType) {

        case 'SUCCESSFUL_MATCH':
            return `
<p class="text-${alertClass}">
    <span class="glyphicon glyphicon-${glyphicon}"></span> Die Klasse <code>${className}</code> war korrekt.
</p>`.trim();

        case 'ONLY_USER':
            return `
<p class="text-${alertClass}">
    <span class="glyphicon glyphicon-${glyphicon}"></span> Die Klasse <code>${className}</code> ist falsch!
</p>`.trim();

        case 'ONLY_SAMPLE':
            return `
<p class="text-${alertClass}">
    <span class="glyphicon glyphicon-${glyphicon}"></span> Die Klasse <code>${className}</code> konnte nicht gefunden werden!
</p>`.trim();

        default:
            return `
<p class="text-${alertClass}">
    <span class="glyphicon glyphicon-${glyphicon}"></span> Die Klasse <code>${className}</code> ...
</p>`.trim();
    }
}

function readClass(name: string): UmlClass {
    return {name, classType: 'CLASS', attributes: [], methods: []};
}

function asList(array: string[]): string {
    return array.length === 0 ? '<li>--</li>' : '<li>' + array.join('</li><li>') + '</li>';
}

function select(span: HTMLSpanElement): void {
    let baseform: string = span.dataset['baseform'];

    if (chosenClasses.indexOf(baseform) < 0) {
        chosenClasses.push(baseform);
    } else {
        chosenClasses.splice(chosenClasses.indexOf(baseform), 1);
    }

    classesList.innerHTML = asList(chosenClasses);

    document.getElementById('exercisetext')
        .querySelectorAll<HTMLSpanElement>('span')
        .forEach((element: HTMLSpanElement) => {
            if (chosenClasses.indexOf(element.dataset['baseform']) > -1) {
                element.classList.remove(notChosenAsClassClassName);
                element.classList.add(chosenAsClassClassName);
            } else {
                element.classList.remove(chosenAsClassClassName);
                element.classList.add(notChosenAsClassClassName);
            }
        });
}

function onClassSelectionCorrectionSuccess(response: UmlClassDiagCorrectionResult): void {
    document.querySelector<HTMLDivElement>('#correctionDiv').innerHTML =
        displayMatchingResultList(response.classResult, "Klassen", explainClassResult);
}

function testSol(): void {
    testBtn.disabled = true;

    let solution: UmlSolution = {
        classes: chosenClasses.map(readClass), associations: [], implementations: []
    };

    testExerciseSolution<UmlSolution, UmlClassDiagCorrectionResult>(testBtn, solution, onClassSelectionCorrectionSuccess);
}

domReady(() => {
    testBtn = document.querySelector<HTMLButtonElement>('#testBtn');
    testBtn.onclick = testSol;

    document.querySelectorAll<HTMLSpanElement>('span.' + notChosenAsClassClassName)
        .forEach((span: HTMLSpanElement) => span.onclick = () => select(span));

    classesList = document.querySelector<HTMLUListElement>('#classesList');
});
