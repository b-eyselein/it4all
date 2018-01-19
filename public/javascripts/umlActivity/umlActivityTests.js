const pattTypeDouble = / {0,}\d+.?\d* {0,}/;
const pattTypeString = / {0,}(".{0,}"|[a-zA-Z]) {0,}/;
const pattTypeBoolean = / {0,}(true|false){1} {0,}/;

// Test: path to end possible (beginnning at startIdToTest)
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


//TEST: test if manual_loopstart and manual_ifstart have at least one element
function test_atLeastOneElementinMerge() {
    const list_namesOfIncorrectElements = ["manual_ifstart", "manual_loopstart"];
    for (let i = 0; i < allElements.length; i++) {
        if (allElements[i].attributes.name === "manual_ifstart") {
            const startids = graph.getConnectedLinks(graph.getCell(allElements[i].attributes.id), {outbound: true});
            var stopwords = ["manual_ifend"];
            let cnr = 0;
            for (let j = 0; j < startids.length; j++) {
                var node = graph.getCell(startids[j].attributes.target.id);
                if (stopwords.includes(node.attributes.name)) {
                    cnr++;
                }
            }
            if (cnr === 2) {
                log.push("Ein Verzweigungselement enth\u00e4lt keine Elemente au\u00DFer das Ende des Verzweigungselements");
                highlightedCells.push(allElements[i].attributes.id);
            }
        }
        if (allElements[i].attributes.name === "manual_loopstart") {
            let cnr2 = 0;
            stopwords = ["manual_loopendcf", "manual_loopendct"];
            //Define: only 1 outgoing link on botside
            const startId_path = graph.getConnectedLinks(graph.getCell(allElements[i].attributes.id), {outbound: true})["0"].attributes.target.id;
            var node = graph.getCell(startId_path);
            if (stopwords.includes(node.attributes.name)) {
                cnr2++;
            }
            while (!stopwords.includes(node.attributes.name)) {
                if (list_namesOfIncorrectElements.includes(node.attributes.name)) {
                    if (!jsonMerge.hasOwnProperty(node.attributes.id)) {
                        return false;
                    } else {
                        node = graph.getCell(jsonMerge[node.attributes.id].targetId);
                    }
                } else {
                    node = graph.getSuccessors(graph.getCell(node.attributes.id))["0"];
                }
            }
            const loopid = node.attributes.id;
            const startIds_loop = graph.getConnectedLinks(graph.getCell(loopid), {outbound: true});
            //Define: Port Right or Left
            let startId_loop;
            if (startIds_loop["0"].attributes.source.port === "bot" || startIds_loop["0"].attributes.source.port === "top") {
                startId_loop = startIds_loop["1"].attributes.target.id;
            } else {
                startId_loop = startIds_loop["0"].attributes.target.id;
            }
            node = graph.getCell(startId_loop);
            stopwords = ["manual_loopstart"];
            if (stopwords.includes(node.attributes.name)) {
                cnr2++;
            }
            if (cnr2 === 2) {
                log.push("Ein Verzweigungselement enth\u00e4lt keine Elemente au\u00DFer das Ende des Verzweigungselements");
                highlightedCells.push(allElements[i].attributes.id);
            }
        }
    }
}

// Test: endnode has outbound connections
function test_EndNodeHasOutboundConnections(endId) {
    return graph.getConnectedLinks(graph.getCell(endId), {outbound: true}).length > 0;
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

// Test: alternative Endpoints through not connected elements
function test_alternativeEnds(graph, endId) {
    // FIXME: currently broken...
    const sinks = graph.getSinks();
    let string = [];

    console.log("Senkenanzahl: " + sinks.length);

    if (sinks.length > 1) {
        for (let i = 0; i < sinks.length; i++) {
            if (sinks[i].id.startsWith("Endknoten") || sinks[i].attributes.name === "edit") {
                delete sinks[i];
            } else {
                string.push(sinks[i].attributes.cleanname);
                highlightedCells.push(sinks[i].attributes.id);
            }
        }
        if (string.length > 1) {
            string = "Die Elemente " + string.toString() + " stellen Endpunkte bzw. Senke dar. (Endknoten exklusiv)";
        } else {
            string = "Das Element " + string.toString() + " stellt einen Endpunkt bzw. eine Senke dar. (Endknoten exklusiv)";
        }
        log.push(string);
    }
}

//TEST: Anzahl der unverbundenen Knoten vom Start aus ( -1 um den Startknoten zu ignorieren)
function test_disconnectedElements() {
    const allElements2 = allElements.slice();
    for (let i = allElements2.length - 1; i >= 0; i--) {
        switch (allElements2[i].attributes.cleanname) {
            case "Startknoten":
                if (graph.getNeighbors(allElements2[i], {inbound: true}).length > 0) {
                    log.push("Ein Startknoten besitzt eingehende Verbindungen");
                    highlightedCells.push(allElements2[i].attributes.id);
                }
                allElements2.splice(i, 1);
                break;
            case "Endknoten":
                if (graph.getNeighbors(allElements2[i], {outbound: true}).length > 0) {
                    log.push("Ein Endknoten besitzt ausgehende Verbindungen");
                    highlightedCells.push(allElements2[i].attributes.id);
                }
                allElements2.splice(i, 1);
                break;
            case "Externer Knoten":
                if (graph.getNeighbors(allElements2[i], {outbound: true}).length > 0) {
                    log.push("Ein Bearbeitungsknoten besitzt ausgehende Verbindungen");
                    highlightedCells.push(allElements2[i].attributes.id);
                }
                if (graph.getNeighbors(allElements2[i]).length === 0) {
                    log.push("Ein Bearbeitungsknoten ist nicht verbunden");
                    highlightedCells.push(allElements2[i].attributes.id);
                }
                allElements2.splice(i, 1);
                break;
            default:
                if (!(graph.getNeighbors(allElements2[i], {outbound: true}).length > 0 && graph.getNeighbors(allElements2[i], {inbound: true}).length)) {
                    log.push("Ein Knoten besitzt keine aus- bzw. eingehende Verbindung");
                    highlightedCells.push(allElements2[i].attributes.id);
                }
        }
    }
}

//manuell loop start darf keine Bedingung enthalten!
function test_conditionOfMergeelements() {
    for (let i = 0; i < allElements.length; i++) {
        if (allElements[i].attributes.name === "manual_ifstart" || allElements[i].attributes.name === "manual_loopendcf" || allElements[i].attributes.name === "manual_loopendct") {
            if (allElements[i].attributes.einput === "") {
                var desc = "";
                if (allElements[i].attributes.name === "manual_ifstart" || allElements[i].attributes.name === "manual_loopendcf" || allElements[i].attributes.name === "manual_loopendct") {
                    desc = "If-Verzweigung";
                } else {
                    desc = "Schleifen-Verzweigung";
                }
                log.push("Ein " + allElements[i].attributes.cleanname + " ent\hu00e4lt keine Bedingung, obwohl es als " + desc + " erkannt wurde.");
                highlightedCells.push(allElements[i].attributes.id);
            }
        } else {
            if (allElements[i].attributes.name === "manual_ifend" || allElements[i].attributes.name === "manual_loopstart") {
                if (allElements[i].attributes.einput !== "") {
                    var desc = "";
                    if (allElements[i].attributes.name === "manual_ifend") {
                        desc = "Ende einer If-Verzweigung";
                    } else {
                        desc = "Anfang einer Schleifen-Verzweigung";
                    }
                    log.push("Ein " + allElements[i].attributes.cleanname + " ent\u00e4lt enhält eine Bedingung, obwohl es als " + desc + " erkannt wurde.");
                    highlightedCells.push(allElements[i].attributes.id);
                }
            }
        }
    }
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
    for (let i = 0; i < graphToTest.length; i++) {
        if (!(graphToTest[i].attributes.id === startId || graphToTest[i].attributes.id === endId)) {
            switch (graphToTest[i].attributes.name) {
                case "actionInput":
                    //console.log(getDataFromElement(graphToTest[i].attributes).content);
                    if (getDataFromElement((graphToTest[i].attributes)).content.data.toString() === "") {
                        log.push("Das Element " + graphToTest[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen");
                        highlightedCells.push(graphToTest[i].attributes.id);
                    }
                    break;

                case "actionSelect":
                    //console.log(getDataFromElement(graphToTest[i].attributes).content);
                    if (getDataFromElement((graphToTest[i].attributes)).content.data.toString() === "") {
                        log.push("Das Element " + graphToTest[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen");
                        highlightedCells.push(graphToTest[i].attributes.id);
                    }
                    break;

                case "actionDeclare":
                    //console.log(getDataFromElement(graphToTest[i].attributes).content);
                    if (!(pattDeclaration.test(getDataFromElement((graphToTest[i].attributes)).content.data.toString()))) {
                        log.push("Das Element " + graphToTest[i].attributes.cleanname + " wurde unvollst\u00e4ndig initialsiert");
                        highlightedCells.push(graphToTest[i].attributes.id);
                    }
                    break;

                case "forin":
                    if (!(amountOfEditNodes(graphToTest[i]) > 0)) {
                        if (getDataFromElement((graphToTest[i].attributes)).content.area.toString() === "") {
                            log.push("Das Element " + graphToTest[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen");
                            highlightedCells.push(graphToTest[i].attributes.id);
                        }
                    }
                    if (getDataFromElement((graphToTest[i].attributes)).content.for.toString() === "") {
                        log.push("Das Element " + graphToTest[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"for\"");
                        highlightedCells.push(graphToTest[i].attributes.id);
                    }
                    if (getDataFromElement((graphToTest[i].attributes)).content.in.toString() === "") {
                        log.push("Das Element " + graphToTest[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"in\"");
                        highlightedCells.push(graphToTest[i].attributes.id);
                    }
                    break;

                case "dw":
                    if (getDataFromElement((graphToTest[i].attributes)).content.ewhile.toString() === "") {
                        log.push("Das Element " + graphToTest[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"while\"");
                        highlightedCells.push(graphToTest[i].attributes.id);
                    }
                    if (!(amountOfEditNodes(graphToTest[i]) > 0)) {
                        if (getDataFromElement((graphToTest[i].attributes)).content.edo.toString() === "") {
                            log.push("Das Element " + graphToTest[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen");
                            highlightedCells.push(graphToTest[i].attributes.id);
                        }
                    }
                    break;

                case "wd":
                    if (getDataFromElement((graphToTest[i].attributes)).content.ewhile.toString() === "") {
                        log.push("Das Element " + graphToTest[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"while\"");
                        highlightedCells.push(graphToTest[i].attributes.id);
                    }
                    if (!(amountOfEditNodes(graphToTest[i]) > 0)) {
                        if (getDataFromElement((graphToTest[i].attributes)).content.edo.toString() === "") {
                            log.push("Das Element " + graphToTest[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen");
                            highlightedCells.push(graphToTest[i].attributes.id);
                        }
                    }
                    break;

                case "if":
                    if (getDataFromElement((graphToTest[i].attributes)).content.eif.toString() === "") {
                        log.push("Das Element " + graphToTest[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"if\"");
                        highlightedCells.push(graphToTest[i].attributes.id);
                    }
                    if (!(amountOfEditNodes(graphToTest[i]) > 1)) {
                        if (getDataFromElement((graphToTest[i].attributes)).content.ethen.toString() === "") {
                            log.push("Das Element " + graphToTest[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"then\"");
                            highlightedCells.push(graphToTest[i].attributes.id);
                        }
                        if (getDataFromElement((graphToTest[i].attributes)).content.eelse.toString() === "") {
                            log.push("Das Element " + graphToTest[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"else\"");
                            highlightedCells.push(graphToTest[i].attributes.id);
                        }
                    }
                    break;
            }
        }
    }
}

//TEST: if a merge element is open --> must be close with end tag
function test_CnrStartnodeEqualsEndnode() {
    let eif = 0;
    let eloop = 0;
    for (let i = 0; i < allElements.length; i++) {
        switch (allElements[i].attributes.name) {
            case "manual_ifstart":
                eif++;
                break;
            case "manual_ifend":
                eif--;
                break;
            case "manual_loopstart":
                eloop++;
                break;
            case "manual_loopendct":
                eloop--;
                break;
            case "manual_loopendcf":
                eloop--;
                break;
        }
    }
    if (eloop > 0) {
        log.push("Das Verzweigungselement Schleife wurde nicht korrekt abgeschlossen");
    } else if (eloop < 0) {
        log.push("Das Verzweigungselement Schleife wurde nicht korrekt begonnen");
    }
    if (eif > 0) {
        log.push("Das Verzweigungselement Bedingung wurde nicht korrekt abgeschlossen");
    } else if (eif < 0) {
        log.push("Das Verzweigungselement Bedingung wurde nicht korrekt begonnen");
    }
}

function test_isExternPortConnectedWithEditNode() {
    for (let i = allElements.length - 1; i >= 0; i--) {
        switch (allElements[i].attributes.name) {
            case "wd":
            case "dw":
            case "forin":
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
