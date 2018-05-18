import * as $ from 'jquery';
import * as joint from 'jointjs';

import 'bootstrap';

import {UmlSolution} from '../uml/umlInterfaces';

const GRAPH_WIDTH = 700, GRAPH_HEIGHT = 700;

const classDiagGraph = new joint.dia.Graph();

let notGenerated = true;

let classDiag: UmlSolution;
let classDiagLoaded: boolean = false;

function createConnection(connection) {
    const input = {
        source: {id: connection.sourceId}, target: {id: connection.targetId},
        labels: [
            {position: 25, attrs: {text: {text: connection.sourceMultiplicity}}},
            {position: -25, attrs: {text: {text: connection.targetMultiplicity}}}
        ]
    };

    switch (connection.type) {
        case "Implementation":
            return new joint.shapes.uml.Implementation(input);
        case "Generalization":
            return new joint.shapes.uml.Generalization(input);

        case "Association":
            return new joint.shapes.uml.Association(input);
        case "Composition":
            return new joint.shapes.uml.Composition(input);
        case "Aggregation":
            return new joint.shapes.uml.Aggregation(input);

        default:
            return new joint.dia.Link(input);
    }
}

function createClass(class_attributes) {
    switch (class_attributes.classType) {
        case "ABSTRACT":
            return new joint.shapes.uml.Abstract(class_attributes);
        case "INTERFACE":
            return new joint.shapes.uml.Interface(class_attributes);
        case "CLASS":
            return new joint.shapes.uml.Class(class_attributes);
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
    console.warn(url);

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
                    console.warn("Klassendiagramm ist noch nicht geladen!");
                    return;
                }
                console.warn(classDiag);
                console.warn('Generating class Diag...');

                new joint.dia.Paper({
                    el: classDiagDiv,
                    width: classDiagDiv.width(), height: GRAPH_HEIGHT,
                    drawGrid: {name: 'dot'},
                    gridSize: 10,
                    model: classDiagGraph,
                    // setLinkVertices: true
                });


                // for (let clazzInput of classDiagClasses) {
                //     classDiagGraph.addCell(createClass(clazzInput));
                // }

                // for (let associationInput of classDiagAssociations) {
                //     classDiagGraph.addCell(createConnection(associationInput));
                // }

                // for (let implementationInput of classDiagImplementations) {
                //     classDiagGraph.addCell(createConnection(implementationInput));
                // }

                notGenerated = false;
            }
        });
    }

});
