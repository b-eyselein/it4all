import * as $ from 'jquery';
import {CLASS_TYPES, UmlAssociation, UmlClass, UmlImplementation, UmlSolution} from '../umlInterfaces';

let chosenClasses: string[] = [];

function readClass(className): UmlClass {
    return new UmlClass(className, CLASS_TYPES.CLASS, [], []);
}

function prepareFormForSubmitting(): void {
    let solutionToSend: UmlSolution = {
        classes: chosenClasses.map(readClass),
        associations: <UmlAssociation[]> [],
        implementations: <UmlImplementation[]> []
    };

    $('#learnerSolution').val(JSON.stringify(solutionToSend));
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

    $('#classesList').html(asList(chosenClasses));

    let otherSpans: HTMLElement[] = $('#exercisetext').find('span').get() as HTMLElement[];

    for (let otherSpan of otherSpans) {
        if (chosenClasses.indexOf(otherSpan.dataset.baseform) < 0) {
            otherSpan.className = 'non-marked';
        } else {
            otherSpan.className = 'marked bg-info';
        }
    }
}

$(document).ready(() => {
    $('span.non-marked').each((index: number, span: Element) => {
        $(span).click(() => select(span))
    });

    $('form').submit(prepareFormForSubmitting);
});