class Position {

    constructor(x, y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @param {g.Point} robotPos
     */
    static fromRobotPosition(robotPos) {
        return new Position(robotPos.x, robotPos.y);
    }

    /**
     * @returns {SimulatorCoordinates}
     */
    toCoordinates() {
        return new SimulatorCoordinates(this.x / optimalCellSize, FIELD_SIZE_CELLS.y - this.y / optimalCellSize - 1);
    }

    /**
     * @param {Position} that
     * @returns {Position}
     */
    add(that) {
        return Position.add(this, that);
    }

    /**
     * @param {Position} position1
     * @param {Position} position2
     *
     * @return {Position}
     */
    static add(position1, position2) {
        return position1;
    }

    toString() {
        return "Pos(" + this.x + ", " + this.y + ")";
    }

}

class SimulatorCoordinates {

    constructor(x, y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @returns {Position}
     */
    toPosition() {
        return new Position((this.x * optimalCellSize), (FIELD_SIZE_CELLS.y - this.y - 1) * optimalCellSize);
    }

    /**
     * @param {SimulatorCoordinates} coordinate1
     * @param {SimulatorCoordinates} coordinate2
     *
     * @return {SimulatorCoordinates}
     */
    static add(coordinate1, coordinate2) {
        return new SimulatorCoordinates(coordinate1.x + coordinate2.x, coordinate1.y + coordinate2.y);
    }

    toString() {
        return "Coord(" + this.x + ", " + this.y + ")";
    }

}
