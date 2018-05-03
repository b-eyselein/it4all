const GRAPH_WIDTH = 700, GRAPH_HEIGHT = 700;

const classDiagGraph = new joint.dia.Graph();

let notGenerated = true;

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

$(document).ready(function () {

    $('#classDiagramModal').on('shown.bs.modal', function () {

        if (notGenerated) {
            let selector = $('#classdiagram');

            new joint.dia.Paper({
                el: selector,
                width: selector.width(), height: GRAPH_HEIGHT,
                drawGrid: 'dot',
                gridSize: 10,
                model: classDiagGraph,
                setLinkVertices: true
            });

            for (let clazzInput of classDiagClasses) {
                classDiagGraph.addCell(createClass(clazzInput));
            }

            for (let associationInput of classDiagAssociations) {
                classDiagGraph.addCell(createConnection(associationInput));
            }

            for (let implementationInput of classDiagImplementations) {
                classDiagGraph.addCell(createConnection(implementationInput));
            }

            notGenerated = false;
        }

    });

});
