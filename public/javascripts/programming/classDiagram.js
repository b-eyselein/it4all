const GRAPH_WIDTH = 1000, GRAPH_HEIGHT = 700;
const CLASS_WIDTH = 250, CLASS_HEIGHT = 200;

const graph = new joint.dia.Graph();

const classes = [
    {
        id: 1, position: {x: 100, y: 20}, size: {width: CLASS_WIDTH, height: CLASS_HEIGHT},
        classType: "ABSTRACT", name: "Fahrzeug",
        attributes: ["Besitzer: String", "ID: Integer", "Preis: Double"],
        methods: ["fahren(): void", "getType(): String", "getPrice: Double"]
    },
    {
        id: 2, position: {x: 550, y: 20}, size: {width: CLASS_WIDTH, height: CLASS_HEIGHT},
        classType: "CLASS", name: "Haendler",
        attributes: ["Adresse: String", "Name: String", "AnzahlAutosimBesitz: Integer"],
        methods: ["calculateCostsPkws(): Integer", "getNutzlastLkw(Typ: String): Integer", "getAnzahlPkw(): Integer"]
    },
    {
        id: 3, position: {x: 25, y: 350}, size: {width: CLASS_WIDTH, height: CLASS_HEIGHT},
        classType: "CLASS", name: "LKW",
        attributes: ["Nutzlast: Double"],
        methods: ["getNutzlast(): Double"]

    },
    {
        id: 4, position: {x: 300, y: 350}, size: {width: CLASS_WIDTH, height: CLASS_HEIGHT},
        classType: "CLASS", name: "PKW",
        attributes: ["isCabrio: Boolean"],
        methods: ["getNutzlast(): Double"]
    }
];

// Kind of types:Composition,Implementation,Aggregation,Generalization,Link
const connections = [
    {
        sourceId: "3", targetId: "1",
        type: "Implementation",
        sourceMultiplicity: "",
        targetMultiplicity: "",
    },
    {
        sourceId: "4", targetId: "1",
        type: "Implementation",
        sourceMultiplicity: "",
        targetMultiplicity: "",
    },
    {
        sourceId: "1", targetId: "2",
        type: "Implementation",
        sourceMultiplicity: "*",
        targetMultiplicity: "1",
    }
];

$(document).ready(function () {
    $('#classDiagramModal').on('shown.bs.modal', function () {
        const uml = joint.shapes.uml;

        new joint.dia.Paper({
            el: $('#classdiagram'),
            width: GRAPH_WIDTH, height: GRAPH_HEIGHT,
            drawGrid: 'dot',
            gridSize: 10,
            model: graph,
            setLinkVertices: true
        });

        for (let clazzInput of classes) {
            graph.addCell(createClass(clazzInput));
        }

        for (let connectionInput of connections) {
            graph.addCell(createConnection(connectionInput));
        }

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
                    return new uml.Implementation(input);
                case "Generalization":
                    return new uml.Generalization(input);

                case "Association":
                    return new uml.Association(input);
                case "Composition":
                    return new uml.Composition(input);
                case "Aggregation":
                    return new uml.Aggregation(input);

                default:
                    return new joint.dia.Link(input);
            }
        }

        function createClass(class_attributes) {
            switch (class_attributes.classType) {
                case "ABSTRACT":
                    return new uml.Abstract(class_attributes);
                case "INTERFACE":
                    return new uml.Interface(class_attributes);
                case "CLASS":
                    return new uml.Class(class_attributes);
            }
        }
    });
});
