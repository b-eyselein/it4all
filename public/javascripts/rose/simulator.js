const FIELD_SIZE_CELLS = {x: 10, y: 10};

let optimalCellSize;

const DIRECTIONS = {'UP': {x: 0, y: +1}, 'DOWN': {x: 0, y: -1}, 'LEFT': {x: -1, y: 0}, 'RIGHT': {x: 1, y: 0}};

const COLORS = {'BLUE': '#0000ff', 'PURPLE': '#800080', 'RED': '#ff0000', 'ORANGE': '#FFA500', 'YELLOW': '#ffff00', 'GREEN': '#008000', 'CYAN': '#00ffff'};

class SimulatorThings {

    /**
     * @param {joint.shapes.robot.Robot} robot
     * @param {joint.shapes.robot.Cell[][]} fieldCells
     * @param {string[]} steps
     */
    constructor(robot, fieldCells, steps) {
        this.robot = robot;
        this.fieldCells = fieldCells;
        this.steps = steps;
        this.stepCount = steps.length;
    }
}

let runResult;

let userThings;
let sampleThings;

let currentStep = -1;


function updateButtons() {
    // FIXME: split in 2 buttons...
    $('#stepBackBtn').prop('disabled', currentStep < 0);
    $('#stepOnBtn').prop('disabled', currentStep >= Math.max(userThings.stepCount, sampleThings.stepCount) - 1);
}

function stepOn() {
    currentStep++;
    performAction(userThings);
    performAction(sampleThings);
    updateButtons();
}

/**
 * @param {SimulatorThings} simulatorThings
 */
function performAction(simulatorThings) {
    if (currentStep >= simulatorThings.stepCount)
        return;

    const action = simulatorThings.steps[currentStep];

    if (Object.keys(DIRECTIONS).includes(action)) {
        moveRobot(simulatorThings.robot, DIRECTIONS[action]);
    } else if (Object.keys(COLORS).includes(action)) {
        let coordinates = Position.fromRobotPosition(simulatorThings.robot.position()).toCoordinates();
        changeCellColor(simulatorThings.fieldCells, coordinates, action);
    } else {
        console.error(action)
    }
}

function stepBack() {
    // FIXME: implement!
    currentStep--;
    updateButtons();
}


/**
 *
 * @param graph
 * @param {SimulatorCoordinates} coordinates
 */
function addCell(graph, coordinates) {
    let cellToAdd = new joint.shapes.robot.Cell({
        position: coordinates.toPosition(),
        size: {width: optimalCellSize, height: optimalCellSize}
    });
    graph.addCell(cellToAdd);
    return cellToAdd;
}

/**
 *
 * @param graph
 * @param {SimulatorCoordinates} coordinates
 *
 * @return {joint.shapes.robot.Robot}
 */
function addRobot(graph, coordinates) {
    let robot = new joint.shapes.robot.Robot({
        position: coordinates.toPosition(),
        size: {width: optimalCellSize, height: optimalCellSize}
    });
    graph.addCell(robot);
    return robot;
}

/**
 * @param {joint.shapes.robot.Cell[][]} fieldCells
 * @param {SimulatorCoordinates} coordinates
 * @param {string} colorName
 */
function changeCellColor(fieldCells, coordinates, colorName) {
    fieldCells[coordinates.x][coordinates.y].attr('rect/fill', COLORS[colorName]);
}

/**
 * @param {joint.shapes.robot.Robot} robot
 * @param {SimulatorCoordinates} direction
 */
function moveRobot(robot, direction) {
    let currentPosition = Position.fromRobotPosition(robot.position());
    let newPosition = SimulatorCoordinates.add(currentPosition.toCoordinates(), direction).toPosition();
    robot.prop('position', newPosition);
}

function instantiateField(element, width, height, isUser) {
    let graph = new joint.dia.Graph();

    /*let paper =*/
    new joint.dia.Paper({
        el: element,
        model: graph,

        width, height,

        // userFieldCells cannot be moved
        interactive: false
    });

    let cells = [];
    for (let w of _.range(FIELD_SIZE_CELLS.x)) {
        cells[w] = [];
        for (let h of _.range(FIELD_SIZE_CELLS.x)) {
            cells[w][h] = addCell(graph, new SimulatorCoordinates(w, h));
        }
    }

    let robot = addRobot(graph, new SimulatorCoordinates(runResult.start.x, runResult.start.y));

    if (isUser) {
        return new SimulatorThings(robot, cells, runResult.user.actions);
    } else {
        return new SimulatorThings(robot, cells, runResult.sample.actions);
    }
}

function instantiateAll(theRunResult) {
    runResult = theRunResult;

    let userField = $('#userField');
    let sampleField = $('#sampleField');

    // Round number to next 10
    optimalCellSize = Math.floor(Math.min(userField.width(), sampleField.width()) / FIELD_SIZE_CELLS.x / 10) * 10;

    const paperWidth = FIELD_SIZE_CELLS.x * optimalCellSize;
    const paperHeight = FIELD_SIZE_CELLS.y * optimalCellSize;

    userThings = instantiateField(userField, paperWidth, paperHeight, true);
    sampleThings = instantiateField(sampleField, paperWidth, paperHeight, false);

    updateButtons();
}