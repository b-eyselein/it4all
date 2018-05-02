const STD_PADDING = 10;
const fontSize = 15;
joint.shapes.customUml = {};
joint.shapes.basic.Generic.define('customUml.Class', {
    attrs: {
        rect: { 'width': 200, stroke: 'black', strokeWidth: 2 },
        text: { fill: 'black', fontSize, fontFamily: 'Times New Roman' },
        '.uml-class-name-rect': {},
        '.uml-class-attrs-rect': {},
        '.uml-class-methods-rect': {},
        '.uml-class-name-text': {
            ref: '.uml-class-name-rect',
            refY: .5,
            refX: .5,
            textAnchor: 'middle',
            yAlignment: 'middle',
            fontWeight: 'bold',
        },
        '.uml-class-attrs-text': { ref: '.uml-class-attrs-rect', refY: STD_PADDING, refX: STD_PADDING, },
        '.uml-class-methods-text': { ref: '.uml-class-methods-rect', refY: STD_PADDING, refX: STD_PADDING, }
    },
    name: [],
    classType: '',
    attributes: [],
    methods: []
}, {
    markup: `
<g class="rotatable">
  <g class="scalable">
    <rect class="uml-class-name-rect"/><rect class="uml-class-attrs-rect"/><rect class="uml-class-methods-rect"/>
  </g>
  <text class="uml-class-name-text"/><text class="uml-class-attrs-text"/><text class="uml-class-methods-text"/>
</g>`.trim(),
    initialize() {
        this.on('change:name change:classType change:attributes change:methods', function () {
            this.updateRectangles();
            this.trigger('uml-update');
        }, this);
        this.updateRectangles();
        joint.shapes.basic.Generic.prototype.initialize.apply(this, arguments);
    },
    getClassTypeRepresentant() {
        switch (this.get('classType')) {
            case 'ABSTRACT':
                return '<<abstract>>';
            case 'INTERFACE':
                return '<<interface>>';
            case 'CLASS':
            default:
                return '';
        }
    },
    getAsUmlClass() {
        return new UmlClass(this.get('name'), this.get('classType'), this.get('attributes'), this.get('methods'), this.get('position'));
    },
    getClassRectText() {
        let classType = this.getClassTypeRepresentant();
        let className = this.get('name');
        if (classType.length > 0) {
            return [classType, className];
        }
        else {
            return [className];
        }
    },
    getAttributes() {
        let attributes = this.get('attributes');
        return attributes.map((a) => a.buildString());
    },
    getMethods() {
        let methods = this.get('methods');
        return methods.map((m) => m.buildString());
    },
    updateRectangles() {
        const attrs = this.get('attrs');
        const rects = [
            { type: 'name', text: this.getClassRectText().filter((str) => str.length !== 0) },
            { type: 'attrs', text: this.getAttributes().filter((str) => str.length !== 0) },
            { type: 'methods', text: this.getMethods().filter((str) => str.length !== 0) }
        ];
        let offsetY = 0;
        rects.forEach(function (rect) {
            const lines = Array.isArray(rect.text) ? rect.text : [rect.text];
            const rectHeight = Math.max((lines.length * fontSize) + (2 * STD_PADDING), fontSize);
            attrs['.uml-class-' + rect.type + '-text'].text = lines.join('\n');
            attrs['.uml-class-' + rect.type + '-rect'].height = rectHeight;
            attrs['.uml-class-' + rect.type + '-rect'].transform = 'translate(0,' + offsetY + ')';
            offsetY += rectHeight;
        });
        this.resize(200, offsetY);
    }
});
joint.shapes.customUml.ClassView = joint.dia.ElementView.extend({}, {
    initialize: function () {
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);
        this.listenTo(this.model, 'uml-update', function () {
            this.update();
            this.resize();
        });
    }
});
joint.dia.Link.define('customUml.Implementation', {
    attrs: {
        '.marker-target': { d: 'M 20 0 L 0 10 L 20 20 z', fill: 'white' },
        '.connection': { strokeDasharray: '3,3' }
    }
});
joint.dia.Link.define('customUml.Aggregation', {
    attrs: { '.marker-target': { d: 'M 40 10 L 20 20 L 0 10 L 20 0 z', fill: 'white' } }
});
joint.dia.Link.define('customUml.Composition', {
    attrs: { '.marker-target': { d: 'M 40 10 L 20 20 L 0 10 L 20 0 z', fill: 'black' } }
});
joint.dia.Link.define('customUml.Association');
//# sourceMappingURL=umlViews.js.map