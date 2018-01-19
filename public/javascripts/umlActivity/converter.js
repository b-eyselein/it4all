//Basis
//group of possible elements for basic element
const list_nameOfCorrectChangingElements = [
    "manual_ifend", "manual_ifstart", "manual_loopstart", "manual_loopendct", "manual_loopendcf"
];


const pattDeclaration = /^((String|Double|Boolean)|(String|Double|Boolean) {0,}) {1,}[a-z]{1,}[a-z0-9]{0,} {0,}= {0,}[a-z0-9"]{1,}.{0,}/;
const pattUpdate = /^ {0,}[a-z]{1,}[a-z0-9]{0,} {0,}= {0,}[a-z0-9"]{1,}.{0,}/;

//for getDataFromElement split(const)
const newLine = /\n/;

let jsonMerge = {};  //merged elements
let jsongraph; // saving and loading graph

let log = []; // hints and feedback from testcases
let highlightedCells = []; //filled via tests
let currentVariables; //current used Variables in Diagramm

let endName;	//input for codegeneration ( mainpaper, editfields)

let changeField = "";
let allElements;
let isCodeGenerated; // needed for logpage to prevent rewrite previous errors from other papers

//TEST: ON,OFF
const testIfEndNodeIsAccessible = true;
const noUnknownElements = true;
const testIfEndnodeHasOutboundConnections = true;
const alternativeEnds = false; // broken, since editfields!
const disconnectedElements = true;
const conditionOfMergeelements = true;
const CnrStartnodeEqualsEndnode = true;
const atLeastOneElementinMerge = true;
const elementsMustHaveInputs = true;
const isExternPortConnectedWithEditNode = true;

const MultipleDeclarations = false;
const DeclarationAgainstProgress = false;
const TypeAgainstValue = false;

function mainGeneration() {
    isCodeGenerated = false;
    allElements = graph.getElements();

    highlightedCells = [];
    currentVariables = [];
    log = [];  // reset logfile before starting

    //generate code for the editfields first --> last one is main paper
    for (let i = parentChildNodes.length - 1; i > -1; i--) {
        generateCode(parentChildNodes[i].parentId, parentChildNodes[i].startId, parentChildNodes[i].endId);
    }
    //REGEX Vartest
    if (MultipleDeclarations) {
        test_MultipleDeclarations();
    }
    if (DeclarationAgainstProgress) {
        test_DeclarationAgainstProgress();
    }
    if (TypeAgainstValue) {
        test_TypeAgainstValue();
    }
    //if no errors found and code is set in HTML
    if (!(isCodeGenerated && log.length === 0)) {
        addLogtoPage();
        document.getElementById("sendToServer").className = "form-control";
        document.getElementById("mainGeneration").className = "form-control btn-primary";
    }
    updateHighlight();

    // set programmcode toggle
    $('#ExerciseText').collapse('hide');
    $('#Configuration').collapse('hide');
    $('#generatedCode').collapse('show');
    $('#resultDiv').collapse('hide');
}

//MAIN Function
function generateCode(parentN, start, end) {
    let generationAlertsJq = $('#generationAlerts');
    generationAlertsJq.html('');

    let startId = start;
    let endId = end;
    let parentId = parentN;

    let graphCopy;

    if (graph.getCell(parentId).getEmbeddedCells().length > 0) {
        graphCopy = graph.getCell(parentId).getEmbeddedCells();
    } else {
        graphCopy = removeUsedCells(graph.getElements());
    }

    //Testcases
    if (testIfEndNodeIsAccessible) {
        let endNodeAccessible = test_isEndNodeAccessible(graph, startId, endId);
        if (!endNodeAccessible) {
            log.push("Der Endknoten kann nicht erreicht werden.");
            highlightedCells.push(endId);
        }
    }

    if (testIfEndnodeHasOutboundConnections) {
        let endNodeHasOutBoundCons = test_EndNodeHasOutboundConnections(endId);
        if (endNodeHasOutBoundCons) {
            log.push("Der Endknoten darf keine ausgehenden Verbindungen enthalten.");
            highlightedCells.push(endId);
        }
    }

    if (noUnknownElements) {
        let unknownElements = test_SearchForUnknownElements(graphCopy);
        if (unknownElements.length !== 0) {
            log.push("Der Graph enthält Verzweigungselemente, welche nicht korrekt verbunden sind.");
            for (let ue of unknownElements) {
                highlightedCells.push(ue);
            }
        }
    }

    if (alternativeEnds) {
        test_alternativeEnds(graph, endId);
    }
    if (disconnectedElements) {
        test_disconnectedElements();
    }
    if (conditionOfMergeelements) {
        test_conditionOfMergeelements();
    }
    if (CnrStartnodeEqualsEndnode) {
        test_CnrStartnodeEqualsEndnode();
    }
    if (atLeastOneElementinMerge) {
        test_atLeastOneElementinMerge();
    }
    if (elementsMustHaveInputs) {
        test_elementsMustHaveInputs(graphCopy, startId, endId);
    }
    if (isExternPortConnectedWithEditNode) {
        test_isExternPortConnectedWithEditNode();
    }

    // Generating Code, if no errors occured
    if (log.length === 0) {
        try {
            document.getElementById("preCode").removeChild(document.getElementById("list_error")); //remove Children if previous work was a failure
        } catch (e) {
        }

        //Define: complete graph with Merged elements in a row
        const json_completeGraph = {};

        //Define: use mergecontent instead of regular element content
        const list_openingsMerge = ["manual_ifstart", "manual_loopstart"];

        //Build: Mergeelements --> create new element with startid = openmerge-element and endid = endmerge-element, contain all other elements within
        //graphCopy=graph.getElements();

        console.log(graph.getSuccessors(graph.getCell(startId)));

        mergeSplittingElements(graphCopy);

        let node = graph.getSuccessors(graph.getCell(startId))["0"]; // Define startId
        let cnr = 0; // set entries in json_completeGraph for inputlines
        while (node.attributes.id !== endId) {
            //Looking for Opening Merge elements and add the data
            if (list_openingsMerge.includes(node.attributes.name)) {
                // ADD: Data
                json_completeGraph[cnr] = jsonMerge[node.attributes.id];
                // DEFINE: next element
                node = graph.getCell(json_completeGraph[cnr].targetId);
            } else {
                if (node.attributes.name === "manual_loopendcf" || node.attributes.name === "manual_loopendct" || node.attributes.name === "manual_ifend") {
                    // get outbound links from the element
                    const startIds_loop = graph.getConnectedLinks(graph.getCell(node.attributes.id), {outbound: true});
                    let startId_loop;
                    /*
                        Define the next element through target id

                        two elements --> loop with condition false or true
                        one element --> manual if --> next element is on top or bot side
                    */
                    if (!(startIds_loop.length > 1)) {
                        startId_loop = startIds_loop["0"].attributes.target.id;
                    } else {
                        if (startIds_loop["0"].attributes.source.port === "bot") {
                            startId_loop = startIds_loop["0"].attributes.target.id;
                        } else {
                            startId_loop = startIds_loop["1"].attributes.target.id;
                        }
                    }
                    try {
                        node = graph.getCell(graph.getCell(startId_loop));
                    } catch (e) {
                        node = graph.getCell(graph.getCell(startId_loop).attributes.id);
                    }
                    cnr--;
                } else {
                    json_completeGraph[cnr] = getDataFromElement(graph.getCell(node.attributes.id).attributes);
                    if (node.attributes.name === "edit") {
                        const neighbourId = graph.getConnectedLinks(node)["0"].attributes.source.id;
                        const lastNodePorts = graph.getConnectedLinks(graph.getCell(neighbourId));
                        node = graph.getCell(getPortByName(lastNodePorts, "out"));
                    } else {
                        node = graph.getCell(graph.getSuccessors(node)["0"]);
                    }
                }
            }
            cnr++;
        }

        let selected_language = $("#selectedLanguage").val();

        convert_JsonToProgrammCode(json_completeGraph, selected_language, parentId);

        generationAlertsJq.html(
            `<div class="alert alert-success">Die Generierung des Codes war erfolgreich. Wechseln Sie zum Tab 
            &quot;<a type="button" onclick="$('#resultTabToggle').click();">Resultate</a>&quot;</div>`
        );

    } else {
        generationAlertsJq.html(
            '<div class="alert alert-danger">Die Generierung des Codes war nicht erfolgreich. Es traten folgende Fehler auf:<ul>'
            + (log.map((str) => '<li>' + str + '</li>').join("\n")) + '</ul>')
    }
}

function getPortByName(portarray, name) {
    for (let i = 0; i < portarray.length; i++) {
        if (portarray[i].attributes.source.port === name) {
            return portarray[i].attributes.target.id;
        }
    }
}

function addLogtoPage() {
    document.getElementById("preCode").innerHTML = "";
    try {
        document.getElementById("preCode").removeChild(document.getElementById("list_error"));
    } catch (e) {
    }
    const plog = preparelog(log);
    document.getElementById("preCode").appendChild(plog);
}

function removeUsedCells(array) {
    const ret = [];
    for (let i = 0; i < array.length; i++) {
        if (!(array[i].attributes.name === "edit" || array[i].attributes.hasOwnProperty('parent'))) {
            ret.push(array[i]);
        }
    }
    return ret;
}


//BOOLEAN: is variable in used
function isVariableActive(variable) {
    for (let i = 0; i < currentVariables.length; i++) {
        if (currentVariables[i].variable === variable) {
            return true;
        }
    }
    return false;
}


function detectVariable(string, elementid) {
    try {
        string = string.split("\n");
    } catch (e) {
    }
    for (var i = 0; i < string.length; i++) {
        let j = 0;
        while (string[i].charAt(j) === " ") {
            string[i] = string[i].substring(j + 1, string[i].length)
            j++;
        }
        //CASE: Declaration
        if (pattDeclaration.test(string[i])) {
            var s = string[i].split("=");
            const type = string[i].substr(0, string[i].indexOf(' '));
            var variable = s[0].substr(string[i].indexOf(' '), s[0].length - 1).replace(/ /g, "");
            var value = s[1];
            if (value.charAt(value.length - 1) === ";") {
                value = value.substr(0, value.length - 1);
            }
            if (isVariableActive(variable)) {
                for (var i = 0; i < currentVariables.length; i++) {
                    if (currentVariables[i].variable === variable) {
                        currentVariables[i].found.push({"id": elementid, "type": type, "value": value});
                    }
                }
            } else {
                currentVariables.push({
                    "variable": variable,
                    "lastValue": value,
                    "lastUpdateID": elementid,
                    "found": [{"id": elementid, "type": type, "value": value}]
                });
            }
        }
        //CASE: Update
        if (pattUpdate.test(string[i])) {
            var s = string[i].split("=");
            var variable = s[0].replace(/ /g, "");
            var value = s[1];
            if (value.charAt(value.length - 1) === ";") {
                value = value.substr(0, value.length - 1);
            }
            if (isVariableActive(variable)) {
                for (var i = 0; i < currentVariables.length; i++) {
                    if (currentVariables[i].variable === variable) {
                        currentVariables[i].lastValue = value;
                        currentVariables[i].lastUpdateID = elementid;
                    }
                }
            } else {
                //	highlightedCells.push(elementid);
                //	log.push("Die Variable "+variable+" wurde zuvor nicht deklariert");
            }
        }
    }
}


//Helper: elementsMustHaveInputs
function amountOfEditNodes(node) {
    let cnr = 0;
    const linkarray = graph.getConnectedLinks(node, {outbound: true});
    for (let i = 0; i < linkarray.length; i++) {
        if (linkarray[i].attributes.source.port === "extern" || linkarray[i].attributes.source.port === "extern-ethen" || linkarray[i].attributes.source.port === "extern-eelse") {
            cnr++;
        }
    }
    return cnr;
}

//Return: Array of all connected Links
function getLinksById(id) {
    return graph.getConnectedLinks(graph.getCell(id));
}

//return jSON with pseudocode   node = starting node,  type = Bezeichnung des mergeknotens
function connectElements(node_input, type) {
    /*
Define Stop ELEMENT:
manual_loopstart --> manual_loopendcf  || manual_loopendct
manual_ifstart --> manual_ifend
*/
    let stopwords = [];
    switch (type) {
        case "manual_loopstart":
            stopwords = ["manual_loopendcf", "manual_loopendct"];
            break;
        case "manual_ifstart":
            stopwords = ["manual_ifend"];
            break;
        case "manual_loopendcf":
            stopwords = ["manual_loopstart"];
            break;
        case "manual_loopendct":
            stopwords = ["manual_loopstart"];
            break;
        default:
            stopwords = [endName];
    }
    const jsonCode = {};
    let i = 0;
    let node = node_input;
    while (!stopwords.includes(node.attributes.name)) {
        let target;
        if (jsonMerge.hasOwnProperty(node.attributes.id)) {
            jsonCode[i] = jsonMerge[node.attributes.id];
            target = jsonCode[i].targetId;
            if (node.attributes.name === "manual_loopstart") {
                const startIds_loop = graph.getConnectedLinks(graph.getCell(jsonMerge[node.attributes.id].targetId), {outbound: true});
                if (startIds_loop["0"].attributes.source.port === "bot") {
                    startId_loop = startIds_loop["0"].attributes.target.id;
                } else {
                    startId_loop = startIds_loop["1"].attributes.target.id;
                }
                node = graph.getCell(startId_loop);
            } else {
                node = graph.getSuccessors(graph.getCell(target))["0"];
            }
        } else {
            //node is
            jsonCode[i] = getDataFromElement(node.attributes);
            target = node.attributes.id;
            node = graph.getSuccessors(graph.getCell(target))["0"];
        }
        i++;
    }
    //console.log(jsonCode);
    return jsonCode;

}

//Return: amount of mergedElements (startelements)
function getNumberOfMergeElements(graphToTest) {
    let cnr = 0;
    for (let i = 0; i < graphToTest.length; i++) {
        if (graphToTest[i].attributes.name === "manual_ifstart" || graphToTest[i].attributes.name === "manual_loopstart") {
            cnr++;
        }
    }
    return cnr;
}

function updateHighlight() {
    for (let i = 0; i < allElements.length; i++) {
        if (highlightedCells.includes(allElements[i].id)) {
            allElements[i].findView(paper).highlight();
        } else {
            allElements[i].findView(paper).unhighlight();
        }
    }
}

/*
Returns Boolean 
true: no other mergestart elements or already merged
*/
function isPathclean(id) {
    const list_namesOfIncorrectElements = ["manual_ifstart", "manual_loopstart"];
    var stopwords = [];
    switch (graph.getCell(id).attributes.name) {
        //Note: loopstart only needs bot port ( one outgoing port )
        case "manual_loopstart":
            stopwords = ["manual_loopendcf", "manual_loopendct"];
            //Define: only 1 outgoing link on botside
            const startId_path = graph.getConnectedLinks(graph.getCell(id), {outbound: true})["0"].attributes.target.id;
            var node = graph.getCell(startId_path);
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
            var startIds_loop = graph.getConnectedLinks(graph.getCell(loopid), {outbound: true});
            //Define: Port Right or Left
            let startId_loop;
            if (startIds_loop["0"].attributes.source.port === "bot" || startIds_loop["0"].attributes.source.port === "top") {
                startId_loop = startIds_loop["1"].attributes.target.id;
            } else {
                startId_loop = startIds_loop["0"].attributes.target.id;
            }
            node = graph.getCell(startId_loop);
            stopwords = ["manual_loopstart"];
            while (!stopwords.includes(node.attributes.name)) {
                if (list_namesOfIncorrectElements.includes(node.attributes.name)) {
                    if (!jsonMerge.hasOwnProperty(node.attributes.id)) {
                        return false;
                    } else {
                        if (node.attributes.name === "manual_loopstart") {
                            var startIds_loop = graph.getConnectedLinks(graph.getCell(jsonMerge[node.attributes.id].targetId), {outbound: true});
                            if (startIds_loop["0"].attributes.source.port === "bot" || startIds_loop["0"].attributes.source.port === "top") {
                                startId_loop = startIds_loop["0"].attributes.target.id;
                            } else {
                                startId_loop = startIds_loop["1"].attributes.target.id;
                            }
                            node = graph.getSuccessors(graph.getCell(startId_loop))["0"];
                        } else {
                            node = graph.getCell(jsonMerge[node.attributes.id].targetId);
                        }
                    }
                } else {
                    node = graph.getSuccessors(graph.getCell(node.attributes.id))["0"];
                }
            }
            return true;

        case "manual_ifstart":
            //Define: outgoing ports
            const startids = graph.getConnectedLinks(graph.getCell(id), {outbound: true});
            var stopwords = ["manual_ifend"];
            for (let i = 0; i < startids.length; i++) {
                var node = graph.getCell(startids[i].attributes.target.id);
                while (!stopwords.includes(node.attributes.name)) {
                    if (list_namesOfIncorrectElements.includes(node.attributes.name)) {
                        if (!jsonMerge.hasOwnProperty(node.attributes.id)) {
                            return false;
                        } else {
                            if (node.attributes.name === "manual_loopstart") {

                                var startIds_loop = graph.getConnectedLinks(graph.getCell(jsonMerge[node.attributes.id].targetId), {outbound: true});
                                if (startIds_loop["0"].attributes.source.port === "bot" || startIds_loop["0"].attributes.source.port === "top") {
                                    startId_loop = startIds_loop["0"].attributes.target.id;
                                } else {
                                    startId_loop = startIds_loop["1"].attributes.target.id;
                                }
                                node = graph.getCell(startId_loop);
                            } else {
                                node = graph.getSuccessors(graph.getCell(jsonMerge[node.attributes.id].targetId))["0"];
                            }
                        }
                    } else {
                        node = graph.getSuccessors(graph.getCell(node.attributes.id))["0"];
                    }
                }
            }
            return true;
        default:
        //console.log("pathclean nicht möglich");
        //console.log(graph.getCell(id).attributes.name);
    }
}

//Returns JsonFile with all merged elements and contents
function mergeSplittingElements(graphToTest) {
    /*
jsonMerge
{
sourceId:[]
}


json_element
{
"name": "",				Elementname of sourcepoint
 "sourceId":"",			ElementID of sourcepoint
"targetId":"",			ElementID of targetpoint
 "content": []			Content of specific mergeelement 
}

*/
    const list_namesOfIncorrectElements = ["manual_ifstart", "manual_loopstart"];
    //Define: Array of all Merging Elements
    jsonMerge = {};
    //Define: Counter for Mergeelements
    let cnr_mergeElements = getNumberOfMergeElements(graphToTest);
    //console.log(graphToTest);
    //While: any merged element isnt converted in json
    while (cnr_mergeElements > 0) {
        //For: each element of graph
        for (let i = 0; i < graphToTest.length; i++) {
            var startelement = graphToTest[i].attributes.id;
            //If: elementtype belongs to mergetype
            if (list_nameOfCorrectChangingElements.includes(graphToTest[i].attributes.name)) {
                if (jsonMerge.hasOwnProperty(graphToTest[i].attributes.id)) {
                    continue;
                }
                if (isPathclean(graphToTest[i].attributes.id)) {
                    //Define: all links of mergeelement
                    const list_links = getLinksById(startelement);
                    //Define: structure of an entry
                    const json_element = {
                        "name": "",
                        "sourceId": "",
                        "targetId": "",
                        "content": []
                    };
                    //Define: sourceId for jsonMerge
                    var startelement = graphToTest[i].attributes.id;
                    //Define: startpoint bye elementype for search
                    switch (graphToTest[i].attributes.name) {
                        //Note: loopstart only needs bot port ( one outgoing port )
                        case "manual_loopstart":
                            //Define: only 1 outgoing link on botside
                            const startId_path = graph.getConnectedLinks(graph.getCell(startelement), {outbound: true})["0"].attributes.target.id;
                            //Define: json content Path from mergesource to condition

                            //Define: Mergestart --> name and sourceId
                            json_element.name = "manual_loopstart";
                            json_element.sourceId = startelement;
                            json_element["content"].path = connectElements(graph.getCell(startId_path), "manual_loopstart");
                            //Test: No elements on path
                            if (Object.entries(json_element.content.path).length != 0) {
                                if (list_namesOfIncorrectElements.includes(json_element.content.path[Object.keys(json_element.content.path).length - 1].name)) {
                                    const node = graph.getCell(json_element.content.path[Object.keys(json_element.content.path).length - 1].targetId);
                                    json_element.targetId = graph.getSuccessors(node)["0"].attributes.id;
                                } else {
                                    json_element.targetId = graph.getSuccessors(graph.getCell(json_element.content.path[Object.keys(json_element.content.path).length - 1].id))["0"].attributes.id;
                                }
                            }
                            if (json_element.targetId === "") {
                                json_element.targetId = graph.getSuccessors(graph.getCell(json_element.sourceId))["0"].attributes.id;
                            }
                            json_element["content"].condition = graph.getCell(json_element.targetId).attributes.einput;
                            //Define: StartId of endelement
                            const startIds_loop = graph.getConnectedLinks(graph.getCell(json_element.targetId), {outbound: true});
                            //Define: Port Right or Left
                            let startId_loop;
                            //console.log(startIds_loop);
                            if (startIds_loop["0"].attributes.source.port === "bot" || startIds_loop["0"].attributes.source.port === "top") {
                                startId_loop = startIds_loop["1"].attributes.target.id;
                                json_element.targetId = startIds_loop["0"].attributes.source.id;
                            } else {
                                startId_loop = startIds_loop["0"].attributes.target.id;
                                json_element.targetId = startIds_loop["1"].attributes.source.id;
                            }
                            //ignoring side of port! condition true or false
                            json_element["content"].loop = connectElements(graph.getCell(startId_loop), "manual_loopendcf");
                            jsonMerge[json_element.sourceId] = json_element;
                            cnr_mergeElements--;
                            break;

                        case "manual_ifstart":

                            //Define: outgoing ports
                            const startids = graph.getConnectedLinks(graph.getCell(startelement), {outbound: true});
                            //Define: branches of true and false
                            let branch_true;
                            let branch_false;
                            if (startids["0"].attributes.source.port === "left") {
                                branch_true = startids["0"].attributes.target.id;
                                branch_false = startids[1].attributes.target.id;
                            } else {
                                branch_true = startids[1].attributes.target.id;
                                branch_false = startids["0"].attributes.target.id;
                            }
                            //tripleIF
                            const nodeTrue = graph.getCell(branch_true);
                            const nodeFalse = graph.getCell(branch_false);
                            //nodeTrue = checkIfBranchesMergeObjects(nodeTrue);
                            //nodeFalse = checkIfBranchesMergeObjects(nodeFalse);
                            //Define: Content with JSON element, if there are no Followers undefined
                            json_element["content"].true = connectElements(nodeTrue, "manual_ifstart");
                            json_element["content"].false = connectElements(nodeFalse, "manual_ifstart");
                            json_element["content"].condition = graphToTest[i].attributes.einput;
                            //Define: Mergestart --> name and sourceId
                            json_element.name = "manual_ifstart";
                            json_element.sourceId = startelement;
                            /*
                                Define: TargetId
                                Branches can be empty --> undefined
                                Test: if true and false have no entry, just add following element of sourceId
                            */
                            if (Object.entries(json_element.content.false).length != 0) {
                                if (list_namesOfIncorrectElements.includes(json_element.content.false[Object.keys(json_element.content.false).length - 1].name)) {
                                    json_element.targetId = json_element.content.false[Object.keys(json_element.content.false).length - 1].targetId;
                                } else {
                                    json_element.targetId = graph.getSuccessors(graph.getCell(json_element.content.false[Object.keys(json_element.content.false).length - 1].id))["0"].attributes.id;
                                }
                            }
                            if (Object.entries(json_element.content.true).length != 0) {
                                if (list_namesOfIncorrectElements.includes(json_element.content.true[Object.keys(json_element.content.true).length - 1].name)) {
                                    json_element.targetId = json_element.content.true[Object.keys(json_element.content.true).length - 1].targetId;
                                } else {
                                    json_element.targetId = graph.getSuccessors(graph.getCell(json_element.content.true[Object.keys(json_element.content.true).length - 1].id))["0"].attributes.id;
                                }
                            }
                            if (json_element.targetId === "") {
                                json_element.targetId = graph.getSuccessors(graph.getCell(json_element.sourceId))["0"].attributes.id;
                            }
                            //ADD: JSONmerge
                            jsonMerge[json_element.sourceId] = json_element;
                            cnr_mergeElements--;
                            break;
                    }
                }
            }
        }
    }
    //console.log("JsonMerge:");
    //console.log(jsonMerge);
    return jsonMerge;
}

//RETURN last point
function checkIfBranchesMergeObjects(node) {
    if (jsonMerge.hasOwnProperty(node.attributes.id)) {
        node = graph.getCell(jsonMerge[node.attributes.id].targetId);
        return node;
    } else {
        return node;
    }
}

// Ausgabe der Datenfelder als Liste mit ID des ELEMENTS 
function getDataFromElement(el) {
    data = {"name": el.name, "id": el.id, "content": []};
    switch (el.name) {
        case "action":
            data["content"].data = el.area.split(newLine);
            break;
        case "actionInput":
            data["content"].data = el.varContent.split(newLine);
            break;
        case "actionSelect":
            data["content"].data = el.varContent;
            break;

        case "actionDeclare":
            data["content"].data = el.varContent1 + " " + el.varContent2 + " = " + el.varContent3;
            break;

        case "actionExtended":
            const str = [];
            if (el.varType1.length > 0) {
                str.push(el.varType1 + " " + el.varContent1);
            }
            if (el.varType2.length > 0) {
                str.push(el.varType2 + " " + el.varContent2);
            }
            if (el.area.length > 0) {
                const sep = el.area.split(newLine);
                for (let i = 0; i < sep.length; i++) {
                    str.push(sep[i]);
                }
            }
            if (el.method1.length > 0) {
                str.push(el.method1);
            }
            if (el.method2.length > 0) {
                str.push(el.method2);
            }
            data["content"].data = str;
            break;
        case "forin":
            data["content"].for = el.efor.split(newLine);
            data["content"].in = el.ein.split(newLine);
            data["content"].area = el.area.split(newLine);
            break;
        case "dw":
            data["content"].ewhile = el.ewhile.split(newLine);
            data["content"].edo = el.edo.split(newLine);
            break;
        case "wd":
            data["content"].ewhile = el.ewhile.split(newLine);
            data["content"].edo = el.edo.split(newLine);
            break;
        case "if":
            data["content"].eif = el.eif.split(newLine);
            data["content"].ethen = el.ethen.split(newLine);
            data["content"].eelse = el.eelse.split(newLine);
            break;
        case "manual_loopendcf":
            data["content"].einput = el.einput.split(newLine);
            break;
        case "manual_loopendct":
            data["content"].einput = el.einput.split(newLine);
            break;
        case "manual_loopstart":
            if (jsonMerge.hasOwnProperty(el.id)) {
                data = jsonMerge[el.id];
                break;
            }
            break;
        case "manual_ifstart":
            if (jsonMerge.hasOwnProperty(el.id)) {
                data = jsonMerge[el.id];
                break;
            }
            data["content"].einput = el.einput.split(newLine);
            break;
        case endName:
            break;
        case "start":
            break;
        default:
            console.log(el);
            console.log("Daten konnten vom Element nicht ausgelesen werden");
    }
    return data;
}

function preparelog(log) {
    const list = document.createElement('ul');
    for (let i = 0; i < log.length; i++) {
        const item = document.createElement('li');
        item.appendChild(document.createTextNode(log[i]));
        list.appendChild(item);
    }
    if ($('#editDiagramModal').hasClass('in')) {
        list.id = "list_errorModal";
    } else {
        list.id = "list_error";
    }
    return list;
}

//Return: String,  Input: graph as json , programminglanguage as String
function convert_JsonToProgrammCode(json_graph, language, parentId) {

    let selectedLanguageBuilderType;
    switch (language) {
        case "java":
            selectedLanguageBuilderType = Java;
            break;
        case "python":
            selectedLanguageBuilderType = Python;
            break;
        default:
            selectedLanguageBuilderType = Python;
            log.push("Body f&uuml;r Sprache konnte nicht ermittelt werden: Nutze Python...");
    }

    const result = buildProgramm(json_graph, selectedLanguageBuilderType);

    let wrap = selectedLanguageBuilderType.get_core(EXERCISE_PARAMETERS.startNode.inputType, EXERCISE_PARAMETERS.startNode.input,
        EXERCISE_PARAMETERS.endNode.outputType, EXERCISE_PARAMETERS.endNode.output, EXERCISE_PARAMETERS.methodName, result);

    if (graph.getCell(parentId).getEmbeddedCells().length > 0) {
        fillContentinElement(parentId, result);
    } else {
        try {
            document.getElementById("preCode").removeChild(document.getElementById("list_error"));
        } catch (e) {
        }

        $('#preCode').html(wrap);

        let submitButton = $('#sendToServer');
        console.log(submitButton.length);
        submitButton.removeClass('btn-default').addClass('btn-primary').prop('disabled', false);
        submitButton.prop('title', '');
        document.getElementById("mainGeneration").className = "form-control";
        isCodeGenerated = true;
    }
}

//todo
function fillContentinElement(parentId, result) {
    const connectedLink = graph.getCell(graph.getConnectedLinks(graph.getCell(parentId))["0"]);
    const portLabel = connectedLink.attributes.source.port;
    const connectedCell = graph.getCell(connectedLink.attributes.source.id);
    let field;
    switch (connectedCell.attributes.name) {
        case "forin":
            field = 'area';
            break;
        case "if":
            switch (portLabel) {
                case "extern-ethen":
                    field = 'ethen';
                    break;
                case "extern-eelse":
                    field = 'eelse';
                    break;
            }
            break;
        case "dw":
            field = 'edo';
            break;
        case "wd":
            field = 'edo';
            break;
    }
    connectedCell.prop(field, result);

    const textareaHeight = (result.split("\n").length) * 25;
    const parentView = connectedCell.findView(paper);

    for (let i = 0; i < parentView.$box["0"].children.length; i++) {
        if (parentView.$box["0"].children[i].nodeName === "TEXTAREA") {
            parentView.$box["0"].children[i].style.height = textareaHeight + "px";
            refreshElement(connectedCell);
        }
        if (parentView.$box["0"].children[i].nodeName === "DIV") {
            for (let j = 0; j < parentView.$attributes.length; j++) {
                console.log(parentView.$attributes[j].nodeName);
                if (parentView.$attributes[j].nodeName === "TEXTAREA") {
                    // var oldTextAreaHeight = parentView.$attributes[j].style.height;
                    parentView.$attributes[j].style.height = textareaHeight + "px";
                    refreshElement(connectedCell);
                }

            }
        }
    }

}

/**
 *
 * @param json
 * @param sel_langClass
 *
 * @returns {string}
 */
function buildProgramm(json, sel_langClass) {
    let content = '';

    for (let i = 0; i < Object.keys(json).length; i++) {
        switch (json[i].name) {
            case 'manual_ifstart':
                sel_langClass.set_increaseDeep(1);
                //console.log(sel_langClass);
                const left = buildProgramm(json[i].content.true, sel_langClass);
                const right = buildProgramm(json[i].content.false, sel_langClass);
                const conditionIf = json[i].content.condition;
                content += sel_langClass.get_manualIf(conditionIf, left, right, sel_langClass.deep);
                sel_langClass.set_increaseDeep(-1);
                //console.log(sel_langClass);
                break;

            case 'manual_loopstart':
                sel_langClass.set_increaseDeep(1);
                const loop = ' '.repeat(sel_langClass.deep - 1) + buildProgramm(json[i].content.loop, sel_langClass);
                const path = buildProgramm(json[i].content.path, sel_langClass);
                const conditionLoop = json[i].content.condition;
                const comb = loop + ' '.repeat(sel_langClass.deep - 1) + path;
                const path2 = buildProgramm(json[i].content.path, sel_langClass.deep); //level of while
                content += sel_langClass.get_manualLoop(conditionLoop, path2, comb, sel_langClass.deep);
                sel_langClass.set_increaseDeep(-1);
                break;

            default:
                content += readDataFromLanguage(json[i], sel_langClass);
        }
    }
    return content;
}

//RETURN: code from a element combind with language , Input: jsonelement,language, deep
function readDataFromLanguage(graphelement, sel_langClass) {
    let str = "";
    const deep = sel_langClass.deep;
    switch (graphelement.name) {
        case "action":
            var content = graphelement.content.data;
            detectVariable(content, graphelement.id);
            if (content.length > 1) {
                str = createMoreLinesText(content, deep) + "\n";
            } else {
                str = " ".repeat(deep) + graphelement.content.data["0"] + "\n";
            }
            break;

        case "actionInput":
            var content = graphelement.content.data;
            detectVariable(content, graphelement.id);
            str = createMoreLinesText(content, deep) + "\n";
            break;

        case "actionSelect":
            var content = graphelement.content.data;
            detectVariable(content, graphelement.id);
            str = " ".repeat(deep) + content + "\n";
            break;

        case "actionDeclare":
            var content = graphelement.content.data;
            detectVariable(content, graphelement.id);
            str = " ".repeat(deep) + content + "\n";
            break;

        case "actionExtended":
            var content = graphelement.content.data;
            detectVariable(content, graphelement.id);
            if (content.length > 1) {
                str = createMoreLinesText(content, deep) + "\n";
            } else {
                str = " ".repeat(deep) + graphelement.content.data["0"] + "\n";
            }
            break;
        case "if":
            const eelse = createMoreLinesText(graphelement.content.eelse, deep + 1);
            const eif = createMoreLinesText(graphelement.content.eif, 0);
            const ethen = createMoreLinesText(graphelement.content.ethen, deep + 1);
            detectVariable(eelse, graphelement.id);
            detectVariable(eif, graphelement.id);
            detectVariable(ethen, graphelement.id);
            str = sel_langClass.get_if(eif, ethen, eelse, deep);
            break;
        case "forin":
            const efor = createMoreLinesText(graphelement.content.for, 0);
            const ein = createMoreLinesText(graphelement.content.in, 0);
            const earea = createMoreLinesText(graphelement.content.area, deep + 1);
            detectVariable(efor, graphelement.id);
            detectVariable(ein, graphelement.id);
            detectVariable(earea, graphelement.id);
            str = sel_langClass.get_efor(efor, ein, earea, deep);
            break;
        case "dw":
            var edo = createMoreLinesText(graphelement.content.edo, deep + 1);
            var ewhile = createMoreLinesText(graphelement.content.ewhile, 0);
            detectVariable(edo, graphelement.id);
            detectVariable(ewhile, graphelement.id);
            str = sel_langClass.get_edw(ewhile, edo, deep);
            break;
        case "wd":
            var edo = createMoreLinesText(graphelement.content.edo, deep + 1);
            var ewhile = createMoreLinesText(graphelement.content.ewhile, 0);
            detectVariable(edo, graphelement.id);
            detectVariable(ewhile, graphelement.id);
            str = sel_langClass.get_ewd(ewhile, edo, deep);
            break;
        default:
            console.log("Es konnte Daten aus dem Graphelement nicht lesen: " + graphelement.name);
    }
    return str;
}

//RETURN: STRING     INPUT: jsonnode of textlines
// Summs up more lines in contentfields for pre field
function createMoreLinesText(input, deep) {
    let text = "";
    if (Array.isArray(input)) {
        for (let i = 0; i < input.length; i++) {
            if (input[i].length > 0) {
                text += " ".repeat(deep) + input[i] + "\n";
            }
        }
        return text.substr(0, text.length - 1);
    } else {
        return " ".repeat(deep) + text;
    }
}

//SET: Saveing current Graph
function saveGraph() {
    jsongraph = graph.toJSON();
    console.log(JSON.stringify(jsongraph));
}

function transferCode() {
    changeField = wrap;
}