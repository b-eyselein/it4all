@(exercise: model.programming.ProgCompleteEx)

console.error('@exercise.classDiagram');

const targetModal = 'classDiagModal';

$(document).ready(function () {
    $('#' + targetModal).on('shown.bs.modal', function () {
        const uml = joint.shapes.uml;

        const classes = [
            {
                x: 160, y: 50,
                name: 'fahrzeug',
                cname: 'Fahrzeug',
                isAbstract: false,
                attributes: ['Besitzer: String', 'ID: Integer', 'Preis: Double'],
                methodes: ['fahren(): void', 'getType(): String', 'getPrice: Double'],
                ownId: 1
            },
            {
                x: 550, y: 50,
                name: 'haendler',
                cname: 'H\u00e4ndler',
                isAbstract: false,
                attributes: ['Adresse: String', 'Name: String', 'AnzahlAutosimBesitz: Integer'],
                methodes: ['calculateCostsPkws(): Integer', 'getNutzlastLkw(Typ: String): Integer', 'getAnzahlPkw(): Integer'],
                ownId: 2
            },
            {
                x: 25, y: 350,
                name: 'lkw',
                cname: 'LKW',
                isAbstract: false,
                attributes: ['Nutzlast: Double'],
                methodes: ['getNutzlast(): Double'],
                ownId: 3
            },
            {
                x: 300, y: 350,
                name: 'pkw',
                cname: 'PKW',
                isAbstract: false,
                attributes: ['isCabrio: Boolean'],
                methodes: ['getNutzlast(): Double'],
                ownId: 4
            }
        ];

        //Kind of types:Composition,Implementation,Aggregation,Generalization,Link
        const connections = [
            {
                sourceId: '3',
                targetId: '1',
                type: 'Generalization',
                sourceMultiplicity: '',
                targetMultiplicity: '',
            },
            {
                sourceId: '4',
                targetId: '1',
                type: 'Generalization',
                sourceMultiplicity: '',
                targetMultiplicity: '',
            },
            {
                sourceId: '1',
                targetId: '2',
                type: 'Link',
                sourceMultiplicity: '*',
                targetMultiplicity: '1',
            }
        ];

        const graphClass = new joint.dia.Graph();

        new joint.dia.Paper({
            el: $('#classdiagram'),
            width: 900,
            height: 600,
            drawGrid: 'dot',
            gridSize: 10,
            model: graphClass,
            setLinkVertices: true
        });

        loadDiagramm(classes, connections);
        classdia = graphClass.toJSON();

        function loadDiagramm(classes, connections) {
            for (let i = 0; i < classes.length; i++) {
                graphClass.addCell(get_class(classes[i].x, classes[i].y, classes[i].name, classes[i].cname, classes[i].isAbstract, classes[i].attributes, classes[i].methodes, classes[i].ownId));
            }
            for (let j = 0; j < connections.length; j++) {
                graphClass.addCell(getConnection(connections[j]));
            }
        }

        function getConnection(connection) {
            const input = {
                source: {id: connection.sourceId}, target: {id: connection.targetId},
                labels: [
                    {position: 25, attrs: {text: {text: connection.sourceMultiplicity}}},
                    {position: -25, attrs: {text: {text: connection.targetMultiplicity}}}
                ]
            };
            switch (connection.type) {
                case 'Composition':
                    return new uml.Composition(input);
                case 'Implementation':
                    return new uml.Implementation(input);
                case 'Aggregation':
                    return new uml.Aggregation(input);
                case 'Generalization':
                    return new uml.Generalization(input);
                case 'Link':
                    return new joint.dia.Link(input);
            }
        }

        function get_class(xCoord, yCoord, cname, clname, isAbstract, attributesArr, methodsArr, ownId) {
            if (isAbstract) {
                clname = clname + '\n' + '{abstract}';
            }

            return new joint.shapes.html.Element({
                position: {x: xCoord, y: yCoord},
                size: {width: 250, height: 200},
                template: [
                    '<div class="classdiaEl">',
                    '<div class="solid-bot tcenter">',
                    '<textarea class="cname tcenter" data-attribute="clname"></textarea>',
                    '</div>',
                    '<div class="solid-bot">',
                    '<textarea class="values" data-attribute="attributes"></textarea>',
                    '</div>',
                    '<div>',
                    '<textarea class="values" data-attribute="methods"></textarea>',
                    '</div>',
                    '</div>'
                ].join(''),
                attributes: attributesArr,
                methods: methodsArr,
                clname: clname,
                name: cname,
                id: ownId,
                initialize: function () {
                    this.on('change:name change:attributes change:methods', function () {
                        this.updateRectangles();
                        this.trigger('uml-update');
                    }, this);

                    this.updateRectangles();

                    joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
                },
                getClassName: function () {
                    return this.get('name');
                },
                updateRectangles: function () {
                    const attrs = this.get('attrs');
                    const rects = [
                        {type: 'name', text: this.getClassName()},
                        {type: 'attrs', text: this.get('attributes')},
                        {type: 'methods', text: this.get('methods')}
                    ];
                    let offsetY = 0;
                    _.each(rects, function (rect) {
                        const lines = _.isArray(rect.text) ? rect.text : [rect.text];
                        const rectHeight = lines.length * 20 + 20;
                        attrs['.textar-class' + rect.type + '-text'].text = lines.join('\n');
                        attrs['.textar-class' + rect.type + '-rect'].height = rectHeight;
                        attrs['.textar-class' + rect.type + '-rect'].transform = 'translate(0,' + offsetY + ')';
                        offsetY += rectHeight;
                    });
                }
            });
        }
    });
});