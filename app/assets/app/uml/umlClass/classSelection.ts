import * as $ from 'jquery';
import {UmlClass} from '../umlInterfaces';

let chosenClasses: string[] = [];

function readClass(name): UmlClass {
    return {name, classType: 'CLASS', attributes: [], methods: []};
}

function prepareFormForSubmitting(): void {
    $('#learnerSolution').val(JSON.stringify({
        classes: chosenClasses.map(readClass), associations: [], implementations: []
    }));
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

$(() => {
    $('span.non-marked').each((index: number, span: Element) => {
        $(span).on('click', () => select(span))
    });

    $('form').on('submit', prepareFormForSubmitting);
});