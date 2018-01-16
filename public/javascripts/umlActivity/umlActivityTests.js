/**
 * TEST: path to end possible (beginnning at startIdToTest)
 */
function test_isEndnodeAccessible(graphToTest, startIdToTest, endIdToTest) {
    list_successors = graph.getSuccessors(graph.getCell(startIdToTest));
    if (typeof list_successors[list_successors.length - 1] !== 'undefined') {
        for (let i = list_successors.length - 1; i > -1; i--) {
            if (list_successors[i].attributes.id === endIdToTest) {
                return;
            }
        }
    }

    log.push("Der Endknoten kann nicht erreicht werden.");
    highlightedCells.push(endIdToTest);
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

//Test: outbound connections from endnode are restricted
function test_forbidOutboundConForEnd(endId) {
    if (graph.getConnectedLinks(graph.getCell(endId), {outbound: true}).length > 0) {
        log.push("Der Endknoten darf keine ausgehenden Verbindungen enthalten.");
        highlightedCells.push(endId);
    }
}

//TEST: graph doesnt contains unknown elements
function test_noUnknownElements(graphToTest) {
    for (i = 0; i < graphToTest.length; i++) {
        if (graphToTest[i].attributes.name === "unknown") {
            log.push("Der Graph enth\u00e4lt Verzweigungselemente, welche nicht korrekt verbunden sind.");
            highlightedCells.push(graphToTest[i].attributes.id);
            return;
        }
    }
}

//TEST: alternative Endpoints through not connected elements
function test_alternativeEnds(graphToTest, endId) {
    const sinks = graph.getSinks();
    let string = [];
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