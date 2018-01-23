const pattTypeDouble = /\s*\d+.?\d*\s*/;
const pattTypeString = /\s*(".*"|[a-zA-Z])\s*/;
const pattTypeBoolean = /\s*(true|false)\s*/;

/**
 * Test: path to end possible (beginning at startIdToTest)
 *
 * @param {object[]} graphToTest
 * @param {string} startIdToTest
 * @param {string} endIdToTest
 *
 * @return {boolean}
 */
function test_isEndNodeAccessible(graphToTest, startIdToTest, endIdToTest) {
    let list_successors = graphToTest.getSuccessors(graphToTest.getCell(startIdToTest));

    // Search for end node
    if (typeof list_successors[list_successors.length - 1] !== 'undefined') {
        for (let i = list_successors.length - 1; i > -1; i--) {
            if (list_successors[i].attributes.id === endIdToTest) {
                // end node found
                return true;
            }
        }
    }

    // end node not found
    return false;
}

// Test: search for unknown elements in graph
function test_SearchForUnknownElements(graphToTest) {
    let unknownElements = [];
    for (let element of  graphToTest) {
        if (element.attributes.name === "unknown") {
            unknownElements.push(element.attributes.id);
        }
    }
    return unknownElements;
}

/**
 * Test: Anzahl der unverbundenen Knoten vom Start aus ( -1 um den Startknoten zu ignorieren)
 *
 * @param {object[]} allElements
 *
 * @return { {id: string, msg: string }[] }
 */
function test_disconnectedElements(allElements) {
    let disconnectedElements = [];

    for (let currentNode of allElements) {
        let currentNodeOk;
        let logToPush = "";

        switch (currentNode.attributes.cleanname) {
            case "Startknoten":
                currentNodeOk = graph.getNeighbors(currentNode, {inbound: true}).length === 0 && graph.getNeighbors(currentNode, {outbound: true}).length === 1;
                logToPush = "Ein Startknoten besitzt ein- oder mehr als eine ausgehende Verbindungen!";
                break;

            case "Endknoten":
                currentNodeOk = graph.getNeighbors(currentNode, {outbound: true}).length === 0 && graph.getNeighbors(currentNode, {inbound: true}).length > 0;
                logToPush = "Ein Endknoten besitzt aus- oder keine eingehenden Verbindungen!";
                break;

            case "Externer Knoten":
                currentNodeOk = graph.getNeighbors(currentNode, {outbound: true}).length === 0 && graph.getNeighbors(currentNode, {inbound: true}).length > 0;
                logToPush = "Ein Bearbeitungsknoten besitzt aus- oder keine eigehenden Verbindungen";
                break;

            default:
                currentNodeOk = graph.getNeighbors(currentNode, {outbound: true}).length > 0 && graph.getNeighbors(currentNode, {inbound: true}).length > 0;
                logToPush = "Ein Knoten besitzt keine aus- bzw. eingehende Verbindung";
                break;
        }

        if (!currentNodeOk) {
            disconnectedElements.push({
                id: currentNode.attributes.id,
                msg: logToPush
            });
        }
    }

    return disconnectedElements;
}

function highlightCellsFromRegex(i) {
    for (let j = 0; j < currentVariables[i].found.length; j++) {
        highlightedCells.push(currentVariables[i].found[j].id);
    }
}

function test_MultipleDeclarations() {
    for (let i = 0; i < currentVariables.length; i++) {
        if (currentVariables[i].found.length > 1) {
            log.push("Die Variable " + currentVariables[i].variable + " wurde " + currentVariables[i].found.length + "-mal deklariert");
            highlightCellsFromRegex(i);
        }
    }
}

function test_DeclarationAgainstProgress() {
    for (let i = 0; i < currentVariables.length; i++) {
        if (currentVariables[i].found.length > 1) {
            for (let j = 0; j < currentVariables[i].found.length - 1; j++) {
                if (currentVariables[i].found[j].type !== currentVariables[i].found[j + 1].type) {
                    log.push("Die Variable " + currentVariables[i].variable + " wechselte den Datentyp von " + currentVariables[i].found[j].type + " zu " + currentVariables[i].found[j + 1].type + " durch mehrfache Deklaration");
                    highlightedCells.push(currentVariables[i].found[j].id);
                    highlightedCells.push(currentVariables[i].found[j + 1].id);
                }
            }
        }
    }
}

function test_TypeAgainstValue() {
    for (let i = 0; i < currentVariables.length; i++) {
        switch (currentVariables[i].found[0].type) {
            case "String":
                if (!(pattTypeString.test(currentVariables[i].found[0].value) | pattTypeString.test(currentVariables[i].lastValue))) {
                    highlightedCells.push(currentVariables[i].found[0].id);
                    log.push("Die Variable " + currentVariables[i].variable + " wurde als " + currentVariables[i].found[0].type + " erkannt und mit Wert " + currentVariables[i].found[0].value + " initialisiert.");
                    log.push("Die Variable " + currentVariables[i].variable + " wurde zuletzt mit dem Wert " + currentVariables[i].lastValue + " erkannt.");
                }

                break;

            case "Boolean":
                console.log("pattDouble with " + currentVariables[i].lastValue + " :" + pattTypeBoolean.test(pattTypeBoolean.test(currentVariables[i].lastValue)));
                if (!(pattTypeBoolean.test(currentVariables[i].found[0].value) | pattTypeBoolean.test(currentVariables[i].lastValue))) {
                    highlightedCells.push(currentVariables[i].found[0].id);
                    log.push("Die Variable " + currentVariables[i].variable + " wurde als " + currentVariables[i].found[0].type + " erkannt und mit Wert " + currentVariables[i].found[0].value + " initialisiert.");
                    log.push("Die Variable " + currentVariables[i].variable + " wurde zuletzt mit dem Wert " + currentVariables[i].lastValue + " erkannt.");
                }
                break;

            case "Double":
                //console.log("pattDouble with "+currentVariables[i].found[0].value+" :"+pattTypeDouble.test(currentVariables[i].found[0].value));
                if (!(pattTypeDouble.test(currentVariables[i].found[0].value) | pattTypeBoolean.test(currentVariables[i].lastValue))) {
                    highlightedCells.push(currentVariables[i].found[0].id);
                    log.push("Die Variable " + currentVariables[i].variable + " wurde als " + currentVariables[i].found[0].type + " erkannt und mit Wert " + currentVariables[i].found[0].value + " initialisiert.");
                    log.push("Die Variable " + currentVariables[i].variable + " wurde zuletzt mit dem Wert " + currentVariables[i].lastValue + " erkannt.");
                }
                break;
        }
    }
}

//TEST: Elements must have an Input
function test_elementsMustHaveInputs(graphToTest, startId, endId) {
    for (let elementToTest of graphToTest) {

        switch (elementToTest.attributes.name) {
            case "actionInput":
                if (getDataFromElement((elementToTest.attributes)).content.data.toString() === "") {
                    log.push("Das Element " + elementToTest.attributes.cleanname + " enth\u00e4lt keine Anweisungen");
                    highlightedCells.push(elementToTest.attributes.id);
                }
                break;

            case "actionSelect":
                //console.log(getDataFromElement(elementToTest.attributes).content);
                if (getDataFromElement((elementToTest.attributes)).content.data.toString() === "") {
                    log.push("Das Element " + elementToTest.attributes.cleanname + " enth\u00e4lt keine Anweisungen");
                    highlightedCells.push(elementToTest.attributes.id);
                }
                break;

            case "actionDeclare":
                //console.log(getDataFromElement(elementToTest.attributes).content);
                if (!(pattDeclaration.test(getDataFromElement((elementToTest.attributes)).content.data.toString()))) {
                    log.push("Das Element " + elementToTest.attributes.cleanname + " wurde unvollst\u00e4ndig initialsiert");
                    highlightedCells.push(elementToTest.attributes.id);
                }
                break;

            case "forLoop":
                if (!(amountOfEditNodes(elementToTest) > 0)) {
                    if (getDataFromElement((elementToTest.attributes)).content.area.toString() === "") {
                        log.push("Das Element " + elementToTest.attributes.cleanname + " enth\u00e4lt keine Anweisungen");
                        highlightedCells.push(elementToTest.attributes.id);
                    }
                }
                if (getDataFromElement((elementToTest.attributes)).content.for.toString() === "") {
                    log.push("Das Element " + elementToTest.attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"for\"");
                    highlightedCells.push(elementToTest.attributes.id);
                }
                if (getDataFromElement((elementToTest.attributes)).content.in.toString() === "") {
                    log.push("Das Element " + elementToTest.attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"in\"");
                    highlightedCells.push(elementToTest.attributes.id);
                }
                break;

            case "doWhile":
                if (getDataFromElement((elementToTest.attributes)).content.ewhile.toString() === "") {
                    log.push("Das Element " + elementToTest.attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"while\"");
                    highlightedCells.push(elementToTest.attributes.id);
                }
                if (!(amountOfEditNodes(elementToTest) > 0)) {
                    if (getDataFromElement((elementToTest.attributes)).content.edo.toString() === "") {
                        log.push("Das Element " + elementToTest.attributes.cleanname + " enth\u00e4lt keine Anweisungen");
                        highlightedCells.push(elementToTest.attributes.id);
                    }
                }
                break;

            case "whileDo":
                if (getDataFromElement((elementToTest.attributes)).content.ewhile.toString() === "") {
                    log.push("Das Element " + elementToTest.attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"while\"");
                    highlightedCells.push(elementToTest.attributes.id);
                }
                if (!(amountOfEditNodes(elementToTest) > 0)) {
                    if (getDataFromElement((elementToTest.attributes)).content.edo.toString() === "") {
                        log.push("Das Element " + elementToTest.attributes.cleanname + " enth\u00e4lt keine Anweisungen");
                        highlightedCells.push(elementToTest.attributes.id);
                    }
                }
                break;

            case "if":
                if (getDataFromElement((elementToTest.attributes)).content.eif.toString() === "") {
                    log.push("Das Element " + elementToTest.attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"if\"");
                    highlightedCells.push(elementToTest.attributes.id);
                }
                if (!(amountOfEditNodes(elementToTest) > 1)) {
                    if (getDataFromElement((elementToTest.attributes)).content.ethen.toString() === "") {
                        log.push("Das Element " + elementToTest.attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"then\"");
                        highlightedCells.push(elementToTest.attributes.id);
                    }
                    if (getDataFromElement((elementToTest.attributes)).content.eelse.toString() === "") {
                        log.push("Das Element " + elementToTest.attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"else\"");
                        highlightedCells.push(elementToTest.attributes.id);
                    }
                }
                break;

            case "ifThen":
                if (getDataFromElement((elementToTest.attributes)).content.eif.toString() === "") {
                    log.push("Das Element " + elementToTest.attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"if\"");
                    highlightedCells.push(elementToTest.attributes.id);
                }
                if (!(amountOfEditNodes(elementToTest) === 1)) {
                    if (getDataFromElement((elementToTest.attributes)).content.ethen.toString() === "") {
                        log.push("Das Element " + elementToTest.attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"then\"");
                        highlightedCells.push(elementToTest.attributes.id);
                    }
                }
                break;


            default:
                // Start- or end node, do nothing...
                break;
        }

    }
}


/**
 *
 * @param {object[]} allElements
 */
function test_isExternPortConnectedWithEditNode(allElements) {
    for (let i = allElements.length - 1; i >= 0; i--) {
        switch (allElements[i].attributes.name) {
            case "whileDo":
            case "doWhile":
            case "forLoop":
            case "ifThen":
                if (graph.getConnectedLinks(allElements[i], {outbound: true}).length > 0) {
                    const targetId = getPortByName(graph.getConnectedLinks(allElements[i]), "extern");
                    if (targetId !== undefined) {
                        if (graph.getCell(targetId).attributes.name !== "edit") {
                            log.push("Ein " + allElements[i].attributes.cleanname + " ist nicht mit einem Bearbeitungsknoten verbunden");
                            highlightedCells.push(allElements[i].attributes.id);
                        }
                    }
                }
                break;
            case "if":
                if (graph.getConnectedLinks(allElements[i], {outbound: true}).length > 1) {

                    const targetIdethen = getPortByName(graph.getConnectedLinks(allElements[i]), "extern-ethen");
                    const targetIdeelse = getPortByName(graph.getConnectedLinks(allElements[i]), "extern-eelse");

                    if (targetIdethen !== undefined) {
                        if (graph.getCell(targetIdethen).attributes.name !== "edit") {
                            log.push("Ein " + allElements[i].attributes.cleanname + " ist nicht mit einem Bearbeitungsknoten verbunden");
                            highlightedCells.push(allElements[i].attributes.id);
                        }
                    }
                    if (targetIdeelse !== undefined) {
                        if (graph.getCell(targetIdeelse).attributes.name !== "edit") {
                            log.push("Ein " + allElements[i].attributes.cleanname + " ist nicht mit einem Bearbeitungsknoten verbunden");
                            highlightedCells.push(allElements[i].attributes.id);
                        }
                    }
                    break;
                }
        }
    }
}
