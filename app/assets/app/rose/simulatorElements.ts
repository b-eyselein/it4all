import * as joint from 'jointjs';
import * as _ from 'lodash';
import {FIELD_SIZE_CELLS, optimalCellSize} from "./simulator";

export {Robot, RobotCell, MyPosition, SimulatorCoordinates, SimulatorThings}

class Robot extends joint.shapes.basic.Circle {
    defaults() {
        return _.defaultsDeep({
            type: 'robot.Cell',
            attrs: {
                circle: {fill: '#00bb00'}
            }
        }, joint.shapes.basic.Circle.prototype.defaults);
    }
}

class RobotCell extends joint.shapes.basic.Rect {
    defaults() {
        return _.defaultsDeep({
            type: 'robot.Cell',
            attrs: {
                rect: {stroke: '#000000', strokeWidth: 2}
            }
        }, joint.shapes.basic.Rect.prototype.defaults);
    }
}

class SimulatorThings {

    stepCount: number;

    constructor(public robot: Robot, public  fieldCells: RobotCell[][], public steps: string[]) {
        this.fieldCells = fieldCells;
        this.steps = steps;
        this.stepCount = steps.length;
    }
}

class MyPosition {

    constructor(public x: number, public y: number) {
    }

    static fromRobotPosition(robotPos: joint.g.Point): MyPosition {
        return new MyPosition(robotPos.x, robotPos.y);
    }

    toCoordinates(): SimulatorCoordinates {
        return new SimulatorCoordinates(this.x / optimalCellSize, FIELD_SIZE_CELLS.y - this.y / optimalCellSize - 1);
    }

}

class SimulatorCoordinates {

    constructor(public x: number, public  y: number) {
    }

    toPosition(): MyPosition {
        return new MyPosition((this.x * optimalCellSize), (FIELD_SIZE_CELLS.y - this.y - 1) * optimalCellSize);
    }

    static add(coordinate1: SimulatorCoordinates, coordinate2: SimulatorCoordinates): SimulatorCoordinates {
        return new SimulatorCoordinates(coordinate1.x + coordinate2.x, coordinate1.y + coordinate2.y);
    }

}
