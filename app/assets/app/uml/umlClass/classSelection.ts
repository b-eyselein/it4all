import * as $ from 'jquery';
import {UmlClass, UmlSolution} from '../umlInterfaces';
import {displayMatchingResultList, UmlClassDiagCorrectionResult, UmlClassMatch} from "./classDiagCorrection";

let chosenClasses: string[] = [];

let testBtn: JQuery, correctionDiv: JQuery, correction: JQuery, classesList: JQuery;

const notChosenClass = 'text-muted', chosenClass = 'text-primary';

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

function readClass(name): UmlClass {
    return {name, classType: 'CLASS', attributes: [], methods: []};
}

function asList(array: string[]): string {
    return array.length === 0 ? '<li>--</li>' : '<li>' + array.join('</li><li>') + '</li>';
}

function select(span: Element): void {
    let baseform: string = $(span).data('baseform');

    if (chosenClasses.indexOf(baseform) < 0) {
        chosenClasses.push(baseform);
    } else {
        chosenClasses.splice(chosenClasses.indexOf(baseform), 1);
    }

    classesList.html(asList(chosenClasses));

    $('#exercisetext').find('span').each((index, element: HTMLElement) => {
        const jElement = $(element);
        if (chosenClasses.indexOf(jElement.data('baseform')) > -1) {
            jElement.removeClass(notChosenClass).addClass(chosenClass);
        } else {
            jElement.removeClass(chosenClass).addClass(notChosenClass);
        }
    });
}

function onClassSelectionCorrectionSuccess(response: UmlClassDiagCorrectionResult): void {
    testBtn.prop('disabled', false);

    correctionDiv.prop('hidden', false);
    correction.html(displayMatchingResultList(response.classResult, "Klassen", explainClassResult));
}

function onClassSelectionCorrectionError(jqXHR): void {
    testBtn.prop('disabled', false);
    console.error(jqXHR);
}

function testSol(): void {
    testBtn.prop('disabled', true);

    let solution: UmlSolution = {
        classes: chosenClasses.map(readClass), associations: [], implementations: []
    };

    $.ajax({
        type: 'PUT',
        dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url: $('#testBtn').data('url'),
        data: JSON.stringify(solution),
        async: true,
        beforeSend: (xhr) => {
            const token = $('input[name="csrfToken"]').val() as string;
            xhr.setRequestHeader("Csrf-Token", token);
        },
        success: onClassSelectionCorrectionSuccess,
        error: onClassSelectionCorrectionError
    });
}

$(() => {
    testBtn = $('#testBtn');
    testBtn.on('click', testSol);

    $('span.' + notChosenClass).each((index: number, span: Element) => {
        $(span).on('click', () => select(span))
    });

    classesList = $('#classesList');

    correction = $('#correction');
    correctionDiv = $('#correctionDiv');
});