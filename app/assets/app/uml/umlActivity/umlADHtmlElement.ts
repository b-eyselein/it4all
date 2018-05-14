joint.shapes.html = {};

joint.shapes.html.Element = joint.shapes.basic.Generic.extend({
    markup: '<rect/>',
    defaults: _.defaultsDeep({
        type: 'html.Element',
        attrs: {
            rect: {
                'ref-width': '100%',
                'ref-height': '100%',
                'stroke': 'dashed'
            },
            '.': {magnet: false}  // Force Port selection
        }
    }, joint.shapes.basic.Generic.prototype.defaults)
});

// Create a custom view for that element that displays an HTML div above it.
joint.shapes.html.ElementView = joint.dia.ElementView.extend({

    init: function () {
        this.listenTo(this.model, 'change', this.updateBox);
    },

    onBoxChange: function (evt) {
        const input = evt.target;
        const attribute = input.dataset.attribute;
        if (attribute) {
            this.model.set(attribute, input.value);
        }
    },

    onRender: function () {
        if (this.$box) this.$box.remove();
        const boxMarkup = joint.util.template(this.model.get('template'))();
        const $box = this.$box = $(boxMarkup);
        this.$attributes = $box.find('[data-attribute]');
        // React on all box changes. e.g. input change
        $box.on('change', _.bind(this.onBoxChange, this));
        // Update the box size and position whenever the paper transformation changes. Note: there is no paper yet on `init` method.
        this.listenTo(this.paper, 'scale', this.updateBox);
        this.$box.find('.delete').on('click', _.bind(this.model.remove, this.model));
        $box.appendTo(this.paper.el);
        this.updateBox();
        return this;
    },

    updateBox: function () {
        // Set the position and the size of the box so that it covers the JointJS element (taking the paper transformations into account).
        const bbox = this.getBBox({useModelGeometry: true});
        const scale = V(this.paper.viewport).scale();
        this.$box.css({
            transform: 'scale(' + scale.sx + ',' + scale.sy + ')',
            transformOrigin: '0 0',
            width: bbox.width / scale.sx,
            height: bbox.height / scale.sy,
            left: bbox.x,
            top: bbox.y
        });
        changeSize(this.model, this.$box, bbox);
        this.updateAttributes();
    },

    updateAttributes: function () {
        const model = this.model;
        this.$attributes.each(function () {
            const value = model.get(this.dataset.attribute);
            switch (this.tagName.toUpperCase()) {
                case 'LABEL':
                    this.textContent = value;
                    break;
                case 'INPUT':
                    this.value = value;
                    break;
                case 'TEXTAREA':
                    this.value = value;
                    break;
            }
        });
    },

    onRemove: function () {
        this.$box.find('.delete').on('click', _.bind(this.model.remove, this.model));
        this.model.on('remove', this.removeBox, this);
        this.$box.remove();
        removeIdFromArray(this.model.id);
        removeIdFromArray(this.model.id);
    }
});

function changeSize(model, box, bbox) {
    switch (model.attributes.name) {
        case 'actionInput':
            let newHeightActionInput = box['0'].children[1].style.height;

            newHeightActionInput = Number(newHeightActionInput.replace('px', ''));

            if (newHeightActionInput > 15) {
                model.resize(bbox.width, newHeightActionInput + 15);
                model.prop('ports/items/1/args/y', (newHeightActionInput + 15));
            }
            break;

        case 'forLoop':
            let newHeightForLoop = box['0'].children[2].style.height;

            newHeightForLoop = Number(newHeightForLoop.replace('px', ''));

            if (newHeightForLoop > 50) {
                model.resize(bbox.width, newHeightForLoop + 35);
                model.prop('ports/items/2/args/y', (newHeightForLoop + 35));
            }
            break;

        case 'doWhile':
            let newHeightDoWhile = box['0'].children[2].style.height;

            newHeightDoWhile = Number(newHeightDoWhile.replace('px', ''));

            if (newHeightDoWhile > 50) {
                model.resize(bbox.width, newHeightDoWhile + 50);
                model.prop('ports/items/1/args/y', (newHeightDoWhile + 50));
            }
            break;

        case 'whileDo' || model.attributes.name === "ifThen":
            let newHeightWhileDo = box['0'].children[4].style.height;

            newHeightWhileDo = Number(newHeightWhileDo.replace('px', ''));

            if (newHeightWhileDo > 50) {
                model.resize(bbox.width, newHeightWhileDo + 52);
                model.prop('ports/items/1/args/y', (newHeightWhileDo + 52));
            }
            break;

        case 'if':
            let newHeightIf = box['0'].children[2].children[1].style.height;
            let newHeightElse = box['0'].children[3].children[1].style.height;

            newHeightIf = Number(newHeightIf.replace('px', ''));
            newHeightElse = Number(newHeightElse.replace('px', ''));

            if (newHeightIf + newHeightElse > 100) {

                newHeightIf = Math.max(newHeightIf, 75);
                newHeightElse = Math.max(newHeightElse, 75);

                model.resize(bbox.width, newHeightIf + newHeightElse + 130);
                model.prop('ports/items/2/args/y', (newHeightIf));
                model.prop('ports/items/3/args/y', (newHeightIf + newHeightElse + 25));
                model.prop('ports/items/1/args/y', (newHeightIf + newHeightElse + 130));
            }
            break;

        default:
            // do nothing
            break;
    }
}