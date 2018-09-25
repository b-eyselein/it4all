import * as $ from 'jquery';
import {UmlClassAttribute, UmlClassMethod, UmlSolution} from "../umlInterfaces";
import {UmlClassDiagCorrectionResult} from "./classDiagCorrection";

let testBtn: JQuery;

function readAttributeElement(elem: HTMLInputElement): UmlClassAttribute | null {
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


function readMethodElement(elem: HTMLInputElement): UmlClassMethod | null {
    if (elem.checked) {
        const jElement = $(elem);
        return {
            visibility: jElement.data('visibility'), name: jElement.data('value'), type: jElement.data('type'),
            parameters: '', isAbstract: false, isStatic: false
        };
    } else {
        return null;
    }
}

function readAllocation(): UmlSolution {
    let solution: UmlSolution = {
        classes: [], associations: [], implementations: []
    };

    try {
        $('.card[data-classname]').map((index: number, elem: HTMLElement) => {
            const jElement: JQuery = $(elem);

            let attributes: UmlClassAttribute[] = [];
            jElement.find('section.attributeList').find('input').each((index, element: HTMLInputElement) => {
                const readAttribute: UmlClassAttribute | null = readAttributeElement(element);
                if (readAttribute != null) {
                    attributes.push(readAttribute);
                }
            });

            let methods: UmlClassMethod[] = [];
            jElement.find('section.methodList').find('input').each((index, element: HTMLInputElement) => {
                const readMethod: UmlClassMethod | null = readMethodElement(element);
                if (readMethod != null) {
                    methods.push(readMethod);
                }
            });

            solution.classes.push({
                classType: jElement.data('classtype'),
                name: jElement.data('classname'),
                attributes,
                methods
            });
        });
    } catch (err) {
        console.error(err);
    }

    return solution;
}

function onMemberAllocationCorrectionSuccess(response: UmlClassDiagCorrectionResult): void {
    testBtn.prop('disabled', false);

    // FIXME: implement!

    console.warn(JSON.stringify(response, null, 2));
}

function onMemberAllocationCorrectionError(jqXHR): void {
    testBtn.prop('disabled', false);
    console.error(jqXHR);
}

function testSol() {
    testBtn.prop('disabled', true);

    let solution: UmlSolution = readAllocation();

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
        success: onMemberAllocationCorrectionSuccess,
        error: onMemberAllocationCorrectionError
    });
}

$(() => {
    testBtn = $('#testBtn');
    testBtn.on('click', testSol);

    $('#allocationForm').on('submit', readAllocation);

    let solutionChanged = false; // TODO!

    $('#endSolveBtn').on('click', () => {
        return !solutionChanged || confirm("Ihre Lösung hat sich seit dem letzten Speichern (Korrektur) geändert. Wollen Sie die Bearbeitung beenden?");
    });
});