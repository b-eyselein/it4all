import * as $ from 'jquery';
import * as joint from 'jointjs';
import * as _ from 'lodash';

import {MyPosition, Robot, RobotCell, SimulatorCoordinates, SimulatorThings} from "./simulatorElements";
import {domReady} from "../otherHelpers";

export {FIELD_SIZE_CELLS, optimalCellSize, instantiateAll, CompleteRunResult}

const FIELD_SIZE_CELLS = {x: 10, y: 10};

let optimalCellSize: number;

let firstInitiation = true;

const DIRECTIONS = {'UP': {x: 0, y: +1}, 'DOWN': {x: 0, y: -1}, 'LEFT': {x: -1, y: 0}, 'RIGHT': {x: 1, y: 0}};

const COLORS = {
    BLUE: '#0000ff',
    PURPLE: '#800080',
    RED: '#ff0000',
    ORANGE: '#FFA500',
    YELLOW: '#ffff00',
    GREEN: '#008000',
    CYAN: '#00ffff',
    WHITE: '#ffffff'
};

const DELAY_IN_MS = 200;

let userThings: SimulatorThings;
let sampleThings: SimulatorThings;

let maxStep: number;
let currentStep: number = 0;

let stepBackBtn: JQuery, stepOnBtn: JQuery, playBtn: JQuery;

function updateHtml(): void {
    $('#userCurStepSpan').html('' + currentStep);
    $('#sampleCurStepSpan').html('' + currentStep);

    stepBackBtn.prop('disabled', currentStep <= 0);
    stepOnBtn.prop('disabled', currentStep >= maxStep);
    playBtn.prop('disabled', currentStep >= maxStep);
}

function stepOn(): void {
    performAction(userThings, currentStep);
    performAction(sampleThings, currentStep);

    currentStep++;

    updateHtml();
}

function stepBack(): void {
    currentStep--;

    reverseAction(userThings, currentStep);
    reverseAction(sampleThings, currentStep);

    updateHtml();
}

function play(): void {
    if (currentStep <= maxStep) {
        let interval = setInterval(function () {
            stepOn();
            if (currentStep > maxStep) clearInterval(interval);
        }, 2 * DELAY_IN_MS);
    }
}

function performAction(simulatorThings: SimulatorThings, currentStep: number): void {
    const action = simulatorThings.steps[currentStep] || null;

    if (Object.keys(DIRECTIONS).indexOf(action) > -1) {
        moveRobot(simulatorThings.robot, DIRECTIONS[action]);
    } else if (Object.keys(COLORS).indexOf(action) > -1) {
        let coordinates = MyPosition.fromRobotPosition(simulatorThings.robot.position()).toCoordinates();
        changeCellColor(simulatorThings.fieldCells, coordinates, action);
    } else if (action === null) {
        // Do nothing
    } else {
        console.error(action);
    }
}

function reverse(direction: string): string {
    switch (direction) {
        case 'UP':
            return 'DOWN';
        case 'DOWN':
            return 'UP';
        case 'LEFT':
            return 'RIGHT';
        case 'RIGHT':
            return 'LEFT';
        default:
            return direction;
    }
}

function reverseAction(simulatorThings: SimulatorThings, currentStep: number): void {
    const action = simulatorThings.steps[currentStep];

    if (Object.keys(DIRECTIONS).indexOf(action) > -1) {
        moveRobot(simulatorThings.robot, DIRECTIONS[reverse(action)]);
    } else if (Object.keys(COLORS).indexOf(action) > -1) {
        let coordinates = MyPosition.fromRobotPosition(simulatorThings.robot.position()).toCoordinates();
        changeCellColor(simulatorThings.fieldCells, coordinates, 'WHITE');
    } else {
        console.error(action);
    }

}

function addCell(graph: joint.dia.Graph, coordinates: SimulatorCoordinates): RobotCell {
    let cellToAdd = new RobotCell({
        position: coordinates.toPosition(),
        size: {width: optimalCellSize, height: optimalCellSize}
    });
    graph.addCell(cellToAdd);
    return cellToAdd;
}

function addRobot(graph: joint.dia.Graph, coordinates: SimulatorCoordinates): Robot {
    let robot = new Robot({
        position: coordinates.toPosition(),
        size: {width: optimalCellSize, height: optimalCellSize}
    });
    graph.addCell(robot);
    return robot;
}

function changeCellColor(fieldCells: RobotCell[][], coordinates: SimulatorCoordinates, colorName: string): void {
    fieldCells[coordinates.x][coordinates.y].attr('rect/fill', COLORS[colorName]);
}

function moveRobot(robot: Robot, direction: SimulatorCoordinates): void {
    let currentPosition = MyPosition.fromRobotPosition(robot.position());
    let newPosition = SimulatorCoordinates.add(currentPosition.toCoordinates(), direction).toPosition();
    robot.prop('position', newPosition);
}

function instantiateField(element: JQuery, start: MyPosition, width: number, height: number, actions: string[]): SimulatorThings {
    let graph = new joint.dia.Graph();

    new joint.dia.Paper({el: element, model: graph, width, height, interactive: false});

    let cells = [];
    for (let w of _.range(FIELD_SIZE_CELLS.x)) {
        cells[w] = [];
        for (let h of _.range(FIELD_SIZE_CELLS.x)) {
            cells[w][h] = addCell(graph, new SimulatorCoordinates(w, h));
        }
    }

    let robot = addRobot(graph, new SimulatorCoordinates(start.x, start.y));

    return new SimulatorThings(robot, cells, actions);
}

interface RunResult {
    actions: string[]
}

interface CompleteRunResult {
    correct: boolean
    start: MyPosition
    user: RunResult
    sample: RunResult
}

function instantiateAll(runResult: CompleteRunResult): void {

    let userField = $('#userField');
    let sampleField = $('#sampleField');

    if (firstInitiation) {
        $('#fieldDiv').prop('hidden', false);

        // Round number to next 10
        optimalCellSize = Math.floor(Math.min(userField.width(), sampleField.width()) / FIELD_SIZE_CELLS.x / 10) * 10;

        firstInitiation = false;
    }

    currentStep = 0;

    const paperWidth: number = FIELD_SIZE_CELLS.x * optimalCellSize;
    const paperHeight: number = FIELD_SIZE_CELLS.y * optimalCellSize;

    userThings = instantiateField(userField, runResult.start, paperWidth, paperHeight, runResult.user.actions);
    sampleThings = instantiateField(sampleField, runResult.start, paperWidth, paperHeight, runResult.sample.actions);

    maxStep = Math.max(userThings.stepCount, sampleThings.stepCount) - 1;

    $('#userMaxStepSpan').html(userThings.stepCount + '');
    $('#sampleMaxStepSpan').html(sampleThings.stepCount + '');

    stepBackBtn.on('click', stepBack);
    stepOnBtn.on('click', stepOn);
    playBtn.on('click', play);

    updateHtml();
}

domReady(() => {
    stepBackBtn = $('#stepBackBtn');
    stepOnBtn = $('#stepOnBtn');
    playBtn = $('#playBtn');
});
