import * as $ from 'jquery';
import {UmlClassAttribute, UmlClassMethod, UmlSolution} from "../umlInterfaces";

function readAttributeElement(elem: HTMLInputElement): UmlClassAttribute {
    if (elem.checked) {
        const jElement = $(elem);
        return {
            visibility: jElement.data('visibility'), name: jElement.data('value'), type: jElement.data('type'),
            isDerived: false, isAbstract: false, isStatic: false
        };
    } else {
        return null;
    }
}


function readMemberElement(elem: HTMLInputElement): UmlClassMethod {
    if (elem.checked) {
        return {
            visibility: 'public', name: elem.dataset['value'], type: elem.dataset['type'],
            parameters: '', isAbstract: false, isStatic: false
        };
    } else {
        return null;
    }
}

function readAllocation(): boolean {
    try {
        let solution: UmlSolution = {
            classes: [], associations: [], implementations: []
        };

        $('.panel.panel-default').map((index: number, elem: Element) => {
            if (elem instanceof HTMLDivElement) {

                let attrCheckBoxes: HTMLInputElement[] = Array.from(elem.querySelector('section.attributeList').getElementsByTagName('input')) as HTMLInputElement[];
                let attributes: UmlClassAttribute[] = attrCheckBoxes.map(readAttributeElement).filter((c) => c != null);

                let methodCheckBoxes: HTMLInputElement[] = Array.from(elem.querySelector('section.methodList').getElementsByTagName('input')) as HTMLInputElement[];
                let methods: UmlClassMethod[] = methodCheckBoxes.map(readMemberElement).filter((c) => c != null);

                solution.classes.push({classType: 'CLASS', name: $(elem).data('classname'), attributes, methods});
            } else {
                console.error('Class panel is no div!');
            }
        });

        console.warn(JSON.stringify(solution, null, 2));

        $('#learnerSolution').val(JSON.stringify(solution));

        return true;
    } catch (err) {
        console.error(err);
        return false;
    }
}

$(() => {
    $('#allocationForm').on('submit', readAllocation);
});