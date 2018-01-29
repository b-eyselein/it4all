// Create new namespace
joint.shapes.robot = {};

// Model and view for userRobot

joint.shapes.robot.Robot = joint.shapes.basic.Circle.extend({
    defaults: joint.util.deepSupplement({
        type: 'robot.Cell',
        attrs: {
            circle: {fill: '#00bb00'}
        }
    }, joint.shapes.basic.Circle.prototype.defaults)
});

joint.shapes.robot.RobotView = joint.dia.ElementView.extend({

    initialize: function () {
        _.bindAll(this, 'updateBox');
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.$box = $(_.template(this.template)());
        // Prevent paper from handling pointerdown.
        this.$box.find('input,select').on('mousedown click', function (evt) {
            evt.stopPropagation();
        });
        // Update the box position whenever the underlying model changes.
        this.model.on('change', this.updateBox, this);
        // Remove the box when the model gets removed from the graph.
        this.model.on('remove', this.removeBox, this);

        this.updateBox();
    },

    render: function () {
        joint.dia.ElementView.prototype.render.apply(this, arguments);
        this.paper.$el.prepend(this.$box);
        this.updateBox();
        return this;
    },

    updateBox: function () {
        // Set the position and dimension of the box so that it covers the JointJS element.
        const bbox = this.model.getBBox();
        this.$box.css({
            width: bbox.width,
            height: bbox.height,
            left: bbox.x,
            top: bbox.y,
            transform: 'rotate(' + (this.model.get('angle') || 0) + 'deg)'
        });
    },

    removeBox: function (evt) {
        this.$box.remove();
    },

    coordinates: function () {
        return {x: -1, y: -1};
    }
});


// Model and view for cell and

joint.shapes.robot.Cell = joint.shapes.basic.Rect.extend({
    defaults: joint.util.deepSupplement({
        type: 'robot.Cell',
        attrs: {
            rect: {stroke: '#000000', 'stroke-width': 2}
        }
    }, joint.shapes.basic.Rect.prototype.defaults)
});

joint.shapes.robot.CellView = joint.dia.ElementView.extend({

    initialize: function () {
        _.bindAll(this, 'updateBox');
        joint.dia.ElementView.prototype.initialize.apply(this, arguments);

        this.$box = $(_.template(this.template)());
        // Prevent paper from handling pointerdown.
        this.$box.find('input,select').on('mousedown click', function (evt) {
            evt.stopPropagation();
        });
        // Update the box position whenever the underlying model changes.
        this.model.on('change', this.updateBox, this);
        // Remove the box when the model gets removed from the graph.
        this.model.on('remove', this.removeBox, this);

        this.updateBox();
    },
    render: function () {
        joint.dia.ElementView.prototype.render.apply(this, arguments);
        this.paper.$el.prepend(this.$box);
        this.updateBox();
        return this;
    },

    updateBox: function () {
        // Set the position and dimension of the box so that it covers the JointJS element.
        const bbox = this.model.getBBox();
        this.$box.css({
            width: bbox.width,
            height: bbox.height,
            left: bbox.x,
            top: bbox.y,
            transform: 'rotate(' + (this.model.get('angle') || 0) + 'deg)'
        });
    },
    removeBox: function (evt) {
        this.$box.remove();
    }
});
