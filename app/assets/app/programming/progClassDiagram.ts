import * as $ from 'jquery';
import * as joint from 'jointjs';

import {
    buildAttributeString,
    buildMethodString,
    UmlAssociation,
    UmlClass,
    UmlImplementation,
    UmlSolution
} from '../uml/umlInterfaces';

import {COLORS, PAPER_HEIGHT} from '../uml/umlConsts';

const classDiagGraph = new joint.dia.Graph();

let notGenerated = true;

let classDiag: UmlSolution;
let classDiagLoaded: boolean = false;

const STD_CLASS_SIZE = 150;

function createImplementation(implementation: UmlImplementation): joint.shapes.uml.Implementation {
    return new joint.shapes.uml.Implementation(implementation);
}

function createAssociation(connection: UmlAssociation): joint.shapes.uml.Association {
    let sourceId: string, targetId: string;

    let graphElements = classDiagGraph.getElements();

    for (let classElem of graphElements) {
        if (classElem instanceof joint.shapes.uml.Class) {
            let completeClassName = classElem.getClassName();
            let className = completeClassName[completeClassName.length - 1];
            if (className === connection.firstEnd) {
                sourceId = classElem.id as string;
            } else if (className === connection.secondEnd) {
                targetId = classElem.id as string;
            }
        }
    }

    if (sourceId == null || targetId == null) {
        console.error('Could not find targets for association...');
    } else {
        const input = {
            source: {id: sourceId}, target: {id: targetId},
            labels: [
                {position: 25, attrs: {text: {text: connection.firstMult === 'UNBOUND' ? '*' : '1'}}},
                {position: -25, attrs: {text: {text: connection.secondMult === 'UNBOUND' ? '*' : '1'}}}
            ]
        };

        switch (connection.assocType) {
            case 'ASSOCIATION':
                return new joint.shapes.uml.Association(input);
            case 'COMPOSITION':
                return new joint.shapes.uml.Composition(input);
            case 'AGGREGATION':
                return new joint.shapes.uml.Aggregation(input);
            default:
                console.error('No such assoc type: ' + connection.assocType);
        }
    }
}

function createClass(class_attributes: UmlClass, position: joint.g.PlainPoint) {
    let options = {
        name: [class_attributes.name],
        attributes: class_attributes.attributes.map(buildAttributeString),
        methods: class_attributes.methods.map(buildMethodString),
        size: {width: STD_CLASS_SIZE, height: STD_CLASS_SIZE},
        position,
        attrs: {
            '.uml-class-name-rect': {fill: COLORS.White},
            '.uml-class-attrs-rect': {fill: COLORS.White},
            '.uml-class-methods-rect': {fill: COLORS.White}
        }
    };

    switch (class_attributes.classType) {
        case 'ABSTRACT':
            return new joint.shapes.uml.Abstract(options);
        case 'INTERFACE':
            return new joint.shapes.uml.Interface(options);
        case 'CLASS':
            return new joint.shapes.uml.Class(options);
        default:
            console.error(class_attributes.classType);
    }
}

function onLoadClassDiagramSuccess(response: UmlSolution): void {
    classDiag = response;
    classDiagLoaded = true;
}

function onLoadClassDiagramError(jqXHR): void {
    console.error(jqXHR.responseText);
}

function loadClassDiagram(url: string): void {
    $.ajax({
        method: 'GET',
        url,
        dataType: 'json',
        success: onLoadClassDiagramSuccess,
        error: onLoadClassDiagramError
    });
}

$(() => {
    let classDiagDiv = $('#classdiagram');

    if (classDiagDiv.length === 1) {
        loadClassDiagram(classDiagDiv.data('url'));

        $('#classDiagramModal').on('shown.bs.modal', () => {

            if (notGenerated) {

                if (!classDiagLoaded) {
                    console.error('Klassendiagramm ist noch nicht geladen!');
                    return;
                }

                new joint.dia.Paper({
                    el: classDiagDiv, model: classDiagGraph,
                    width: classDiagDiv.width(), height: PAPER_HEIGHT,
                    drawGrid: {name: 'dot'}, gridSize: 10
                });

                classDiag.classes.forEach((classInput, index) => {
                    let x = (index % 3) * 200 + 100;
                    let y = Math.floor(index / 3) * 200 + 100;
                    classDiagGraph.addCell(createClass(classInput, {x, y}));
                });

                for (let associationInput of classDiag.associations) {
                    classDiagGraph.addCell(createAssociation(associationInput));
                }

                for (let implementationInput of classDiag.implementations) {
                    classDiagGraph.addCell(createImplementation(implementationInput));
                }

                notGenerated = false;
            }
        });
    }

});
