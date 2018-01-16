//Basis
var list_nameOfCorrectChangingElements = ["manual_ifend", "manual_ifstart", "manual_loopstart", "manual_loopendct", "manual_loopendcf"]; //group of possible elements for basic element
var graphcopy;  //all Elements (no links)
var jsonMerge = {};  //merged elements
var jsongraph; // saving and loading graph
var list_successors; // startnode successors
const newLine = /\n/; //for getDataFromElement split(const)
var log = []; // hints and feedback from testcases
var highlightedCells = []; //filled via tests
var currentVariables; //current used Variables in Diagramm
var startId;	//input for codegeneration ( mainpaper, editfields)
var endId;		//input for codegeneration ( mainpaper, editfields)
var endName;	//input for codegeneration ( mainpaper, editfields)
var parentId;	//input for codegeneration ( mainpaper, editfields)
var changeField = "";
var selected_language;   // For Set modal language like main paper
var allElements;
var isCodeGenerated; // needed for logpage too prevent rewrite previous errors from other papers

//TEST: ON,OFF
var isEndnodeAccessible = true;
var noUnknownElements = true;
var forbidOutboundConForEnd = true;
var alternativeEnds = false; // broken, since editfields!
var disconnectedElements = true;
var conditionOfMergeelements = true;
var CnrStartnodeEqualsEndnode = true;
var atLeastOneElementinMerge = true;
var elementsMustHaveInputs = true;
var isExternPortConnectedWithEditNode = true;
var MultipleDeclarations = false;
var DeclarationAgainstProgress = false;
var TypeAgainstValue = false;

//python contents
class Python {

    constructor(deep) {
        this.deep = parseInt(deep);// deep is the amount of spaces before the text starts
    }

    get_core(startnode_inputtype, startnode_input, endnode_outputtype, endnode_output, methodname, content) {
        if ($('#editDiagramModal').hasClass('in')) {
            return content;
        } else {
            return "def " + methodname + "(" + startnode_input + "):\n" + content + " return " + endnode_output;
        }
    }

    get_if(econdition, ethen, eelse, deep) {
        return " ".repeat(deep) + "if " + econdition + ":\n" + ethen + "\n" + " ".repeat(deep) + "else:\n" + eelse + "\n";
    }

    get_loop(econdition, path, content, deep) {
        return path + "\n" + " ".repeat(deep) + "while" + econdition + ":\n" + content;
    }

    get_efor(eelement, collection, content, deep) {
        return " ".repeat(deep) + "for " + eelement + " in " + collection + ":\n" + content + "\n";
    }

    get_edw(econdition, content, deep) {
        return " ".repeat(deep) + "while True:\n" + content + "\n" + " ".repeat(deep) + "if " + econdition + ":\n" + " ".repeat(deep) + "break\n";
    }

    get_ewd(econdition, content, deep) {
        return " ".repeat(deep) + "while " + econdition + ":\n" + content + "\n";
    }

    get_manualIf(econdition, left, right, deep) {
        return " ".repeat(deep - 1) + "if " + econdition + ":\n" + " ".repeat(deep - 2) + left + " ".repeat(deep - 1) + "else:\n" + " ".repeat(deep - 2) + right;
    }

    get_manualLoop(econdition, path2, comb, deep) {
        return path2 + " ".repeat(deep - 1) + "while " + econdition + ":\n" + comb;
    }

    set_increaseDeep(value) {
        this.deep = this.deep + parseInt(value);
    }
}

//java contents
class Java {

    constructor(deep) {
        this.deep = parseInt(deep);
    }

    get_core(startnode_inputtype, startnode_input, endnode_outputtype, endnode_output, methodname, content) {
        if ($('#editDiagramModal').hasClass('in')) {
            return content;
        } else {
            return "public " + endnode_outputtype + " " + methodname + "(" + startnode_inputtype + " " + startnode_input + ")\n{\n" + content + "return " + endnode_output + ";\n}";
        }
    }

    get_if(econdition, ethen, eelse, deep) {
        return " ".repeat(deep) + "if(" + econdition + "){\n" + ethen + "\n" + " ".repeat(deep) + "}else{\n" + eelse + "\n" + " ".repeat(deep) + "}\n";
    }

    get_loop(econdition, path, content, deep) {
        return path + " ".repeat(deep) + "while(!(" + econdition + ")){\n" + content + "}";
    }

    get_efor(eelement, collection, content, deep) {
        return " ".repeat(deep) + "for(" + eelement + ":" + collection + "){\n" + content + "\n" + " ".repeat(deep) + "}\n";
    }

    get_edw(econdition, content, deep) {
        return " ".repeat(deep) + "do{\n" + content + "\n" + " ".repeat(deep) + "}\n" + " ".repeat(deep) + "while(" + econdition + ");\n";
    }

    get_ewd(econdition, content, deep) {
        return " ".repeat(deep) + "while(" + econdition + "){\n" + content + "\n" + " ".repeat(deep) + "}\n";
    }

    get_manualIf(econdition, left, right, deep) {
        return " ".repeat(deep - 1) + "if(" + econdition + "){\n" + left + " ".repeat(deep - 1) + "}else{\n" + right + " ".repeat(deep - 1) + "}\n";
    }

    get_manualLoop(econdition, path2, comb, deep) {
        return path2 + " ".repeat(deep - 1) + "while(!(" + econdition + ")){\n" + comb + "}\n";
    }

    set_increaseDeep(value) {
        this.deep = this.deep + parseInt(value);
    }
}

function mainGeneration() {
    isCodeGenerated = false;
    allElements = graph.getElements();
    highlightedCells = [];
    currentVariables = [];
    log = [];  // reset logfile before starting
    //generate code for the editfields first --> last one is main paper
    for (var i = parentChildNodes.length - 1; i > -1; i--) {
        generateCode(parentChildNodes[i].parentId, parentChildNodes[i].startId, parentChildNodes[i].endId, parentChildNodes[i].endName);
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
    $("#ExerciseText").collapse('hide');
    $("#Configuration").collapse('hide');
    $("#ProgrammCode").collapse('show');
}

//MAIN Function
function generateCode(parentN, start, end, endN) {
    startId = start;
    endId = end;
    endName = endN;
    parentId = parentN;
    if (graph.getCell(parentId).getEmbeddedCells().length > 0) {
        graphcopy = graph.getCell(parentId).getEmbeddedCells();
    } else {
        graphcopy = removeUsedCells(graph.getElements());
    }
    //console.log(graph.getCell(parentId).getEmbeddedCells());
    //console.log(parentChildNodes);
    //Testcases
    if (isEndnodeAccessible) {
        test_isEndnodeAccessible(graphcopy, startId, endId);
    }
    if (forbidOutboundConForEnd) {
        test_forbidOutboundConForEnd(endId);
    }
    if (noUnknownElements) {
        test_noUnknownElements(graphcopy);
    }
    if (alternativeEnds) {
        test_alternativeEnds(graphcopy, endId);
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
        test_elementsMustHaveInputs(graphcopy);
    }
    if (isExternPortConnectedWithEditNode) {
        test_isExternPortConnectedWithEditNode();
    }
    //Generating Code, if no errors occured
    if (log.length === 0) {
        try {
            document.getElementById("preCode").removeChild(document.getElementById("list_error")); //remove Children if previous work was a failure
        } catch (e) {
        }
        //Define: complete graph with Merged elements in a row
        var json_completeGraph = {};
        //Define: use mergecontent instead of regular element content
        var list_openingsMerge = ["manual_ifstart", "manual_loopstart"];
        //Build: Mergeelements --> create new element with startid = openmerge-element and endid = endmerge-element, contain all other elements within
        //graphcopy=graph.getElements();
        console.log(graph.getSuccessors(graph.getCell(startId)));
        mergeSplittingElements(graphcopy);
        var node = graph.getSuccessors(graph.getCell(startId))["0"]; // Define startId
        var cnr = 0; // set entries in json_completeGraph for inputlines
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
                    var startIds_loop = graph.getConnectedLinks(graph.getCell(node.attributes.id), {outbound: true});
                    var startId_loop;
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
                        var neighbourId = graph.getConnectedLinks(node)["0"].attributes.source.id;
                        var lastNodePorts = graph.getConnectedLinks(graph.getCell(neighbourId));
                        node = graph.getCell(getPortByName(lastNodePorts, "out"));
                    } else {
                        node = graph.getCell(graph.getSuccessors(node)["0"]);
                    }
                }
            }
            cnr++;
        }
        selected_language = $("#sel_lang").val();
        convert_JsonToProgrammCode(json_completeGraph, selected_language);
    }
}

function getPortByName(portarray, name) {
    for (var i = 0; i < portarray.length; i++) {
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
    var plog = preparelog(log);
    document.getElementById("preCode").appendChild(plog);
}

function removeUsedCells(array) {
    var ret = [];
    for (var i = 0; i < array.length; i++) {
        if (!(array[i].attributes.name === "edit" || array[i].attributes.hasOwnProperty('parent'))) {
            ret.push(array[i]);
        }
    }
    return ret;
}


//BOOLEAN: is variable in used
function isVariableActive(variable) {
    for (var i = 0; i < currentVariables.length; i++) {
        if (currentVariables[i].variable === variable) {
            return true;
        }
    }
    return false;
}

const pattDeclaration = /^((String|Double|Boolean)|(String|Double|Boolean) {0,}) {1,}[a-z]{1,}[a-z0-9]{0,} {0,}= {0,}[a-z0-9"]{1,}.{0,}/;
const pattUpdate = /^ {0,}[a-z]{1,}[a-z0-9]{0,} {0,}= {0,}[a-z0-9"]{1,}.{0,}/;

function detectVariable(string, elementid) {
    try {
        string = string.split("\n");
    } catch (e) {
    }
    for (var i = 0; i < string.length; i++) {
        var j = 0;
        while (string[i].charAt(j) === " ") {
            string[i] = string[i].substring(j + 1, string[i].length)
            j++;
        }
        //CASE: Declaration
        if (pattDeclaration.test(string[i])) {
            var s = string[i].split("=");
            var type = string[i].substr(0, string[i].indexOf(' '));
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


function highlightCellsFromRegex(i) {
    for (var j = 0; j < currentVariables[i].found.length; j++) {
        highlightedCells.push(currentVariables[i].found[j].id);
    }
}

function test_MultipleDeclarations() {
    for (var i = 0; i < currentVariables.length; i++) {
        if (currentVariables[i].found.length > 1) {
            log.push("Die Variable " + currentVariables[i].variable + " wurde " + currentVariables[i].found.length + "-mal deklariert");
            highlightCellsFromRegex(i);
        }
    }
}

function test_DeclarationAgainstProgress() {
    for (var i = 0; i < currentVariables.length; i++) {
        if (currentVariables[i].found.length > 1) {
            for (var j = 0; j < currentVariables[i].found.length - 1; j++) {
                if (currentVariables[i].found[j].type !== currentVariables[i].found[j + 1].type) {
                    log.push("Die Variable " + currentVariables[i].variable + " wechselte den Datentyp von " + currentVariables[i].found[j].type + " zu " + currentVariables[i].found[j + 1].type + " durch mehrfache Deklaration");
                    highlightedCells.push(currentVariables[i].found[j].id);
                    highlightedCells.push(currentVariables[i].found[j + 1].id);
                }
            }
        }
    }
}


const pattTypeDouble = / {0,}\d+.?\d* {0,}/;
const pattTypeString = / {0,}(\".{0,}\"|[a-zA-Z]) {0,}/;
const pattTypeBoolean = / {0,}(true|false){1} {0,}/;

function test_TypeAgainstValue() {
    for (var i = 0; i < currentVariables.length; i++) {
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
function test_elementsMustHaveInputs(graphcopy) {
    for (var i = 0; i < graphcopy.length; i++) {
        if (!(graphcopy[i].attributes.id === startId || graphcopy[i].attributes.id === endId)) {
            switch (graphcopy[i].attributes.name) {
                case "actionInput":
                    //console.log(getDataFromElement(graphcopy[i].attributes).content);
                    if (getDataFromElement((graphcopy[i].attributes)).content.data.toString() === "") {
                        log.push("Das Element " + graphcopy[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen");
                        highlightedCells.push(graphcopy[i].attributes.id);
                    }
                    break;

                case "actionSelect":
                    //console.log(getDataFromElement(graphcopy[i].attributes).content);
                    if (getDataFromElement((graphcopy[i].attributes)).content.data.toString() === "") {
                        log.push("Das Element " + graphcopy[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen");
                        highlightedCells.push(graphcopy[i].attributes.id);
                    }
                    break;

                case "actionDeclare":
                    //console.log(getDataFromElement(graphcopy[i].attributes).content);
                    if (!(pattDeclaration.test(getDataFromElement((graphcopy[i].attributes)).content.data.toString()))) {
                        log.push("Das Element " + graphcopy[i].attributes.cleanname + " wurde unvollst\u00e4ndig initialsiert");
                        highlightedCells.push(graphcopy[i].attributes.id);
                    }
                    break;

                case "forin":
                    if (!(amountOfEditNodes(graphcopy[i]) > 0)) {
                        if (getDataFromElement((graphcopy[i].attributes)).content.area.toString() === "") {
                            log.push("Das Element " + graphcopy[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen");
                            highlightedCells.push(graphcopy[i].attributes.id);
                        }
                    }
                    if (getDataFromElement((graphcopy[i].attributes)).content.for.toString() === "") {
                        log.push("Das Element " + graphcopy[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"for\"");
                        highlightedCells.push(graphcopy[i].attributes.id);
                    }
                    if (getDataFromElement((graphcopy[i].attributes)).content.in.toString() === "") {
                        log.push("Das Element " + graphcopy[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"in\"");
                        highlightedCells.push(graphcopy[i].attributes.id);
                    }
                    break;

                case "dw":
                    if (getDataFromElement((graphcopy[i].attributes)).content.ewhile.toString() === "") {
                        log.push("Das Element " + graphcopy[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"while\"");
                        highlightedCells.push(graphcopy[i].attributes.id);
                    }
                    if (!(amountOfEditNodes(graphcopy[i]) > 0)) {
                        if (getDataFromElement((graphcopy[i].attributes)).content.edo.toString() === "") {
                            log.push("Das Element " + graphcopy[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen");
                            highlightedCells.push(graphcopy[i].attributes.id);
                        }
                    }
                    break;

                case "wd":
                    if (getDataFromElement((graphcopy[i].attributes)).content.ewhile.toString() === "") {
                        log.push("Das Element " + graphcopy[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"while\"");
                        highlightedCells.push(graphcopy[i].attributes.id);
                    }
                    if (!(amountOfEditNodes(graphcopy[i]) > 0)) {
                        if (getDataFromElement((graphcopy[i].attributes)).content.edo.toString() === "") {
                            log.push("Das Element " + graphcopy[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen");
                            highlightedCells.push(graphcopy[i].attributes.id);
                        }
                    }
                    break;

                case "if":
                    if (getDataFromElement((graphcopy[i].attributes)).content.eif.toString() === "") {
                        log.push("Das Element " + graphcopy[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"if\"");
                        highlightedCells.push(graphcopy[i].attributes.id);
                    }
                    if (!(amountOfEditNodes(graphcopy[i]) > 1)) {
                        if (getDataFromElement((graphcopy[i].attributes)).content.ethen.toString() === "") {
                            log.push("Das Element " + graphcopy[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"then\"");
                            highlightedCells.push(graphcopy[i].attributes.id);
                        }
                        if (getDataFromElement((graphcopy[i].attributes)).content.eelse.toString() === "") {
                            log.push("Das Element " + graphcopy[i].attributes.cleanname + " enth\u00e4lt keine Anweisungen im Bereich \"else\"");
                            highlightedCells.push(graphcopy[i].attributes.id);
                        }
                    }
                    break;
            }
        }
    }
}

//Helper: elementsMustHaveInputs
function amountOfEditNodes(node) {
    var cnr = 0;
    var linkarray = graph.getConnectedLinks(node, {outbound: true});
    for (var i = 0; i < linkarray.length; i++) {
        if (linkarray[i].attributes.source.port === "extern" || linkarray[i].attributes.source.port === "extern-ethen" || linkarray[i].attributes.source.port === "extern-eelse") {
            cnr++;
        }
    }
    return cnr;
}

//SUPPORT
function removeFromArr(array, value) {
    var index = array.indexOf(value);
    if (index > -1) {
        array.splice(index, 1);
    }
    return array;
}

//TEST: if a merge element is open --> must be close with end tag
function test_CnrStartnodeEqualsEndnode() {
    var eif = 0;
    var eloop = 0;
    for (var i = 0; i < allElements.length; i++) {
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
    for (var i = allElements.length - 1; i >= 0; i--) {
        switch (allElements[i].attributes.name) {
            case "wd":
            case "dw":
            case "forin":
                if (graph.getConnectedLinks(allElements[i], {outbound: true}).length > 0) {
                    var targetId = getPortByName(graph.getConnectedLinks(allElements[i]), "extern");
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
                    var targetIdethen = getPortByName(graph.getConnectedLinks(allElements[i]), "extern-ethen");
                    var targetIdeelse = getPortByName(graph.getConnectedLinks(allElements[i]), "extern-eelse");
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

//TEST: path to end possible   (beginnning via startpoint)
function test_isEndnodeAccessible(graphcopy, startId, endId) {
    //	console.log(graphcopy.getSuccessors(graph.getCell(startId)));
    list_successors = graph.getSuccessors(graph.getCell(startId));
    if (typeof list_successors[list_successors.length - 1] !== 'undefined') {
        for (var i = list_successors.length - 1; i > -1; i--) {
            if (list_successors[i].attributes.id === endId) {
                return;
            }
        }
    }
    log.push("Der Endknoten kann nicht erreicht werden.");
    highlightedCells.push(endId);
}

//TEST: test if manual_loopstart and manual_ifstart have at least one element
function test_atLeastOneElementinMerge() {
    var list_namesOfIncorrectElements = ["manual_ifstart", "manual_loopstart"];
    for (var i = 0; i < allElements.length; i++) {
        if (allElements[i].attributes.name === "manual_ifstart") {
            var startids = graph.getConnectedLinks(graph.getCell(allElements[i].attributes.id), {outbound: true});
            var stopwords = ["manual_ifend"];
            var cnr = 0;
            for (var j = 0; j < startids.length; j++) {
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
            var cnr2 = 0;
            stopwords = ["manual_loopendcf", "manual_loopendct"];
            //Define: only 1 outgoing link on botside
            var startId_path = graph.getConnectedLinks(graph.getCell(allElements[i].attributes.id), {outbound: true})["0"].attributes.target.id;
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
            var loopid = node.attributes.id;
            var startIds_loop = graph.getConnectedLinks(graph.getCell(loopid), {outbound: true});
            //Define: Port Right or Left
            var startId_loop;
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
function test_noUnknownElements(graphcopy) {
    for (i = 0; i < graphcopy.length; i++) {
        if (graphcopy[i].attributes.name === "unknown") {
            log.push("Der Graph enth\u00e4lt Verzweigungselemente, welche nicht korrekt verbunden sind.");
            highlightedCells.push(graphcopy[i].attributes.id);
            return;
        }
    }
}

//TEST: alternative Endpoints through not connected elements
function test_alternativeEnds(graphcopy, endId) {
    var sinks = graph.getSinks();
    var string = [];
    if (sinks.length > 1) {
        for (var i = 0; i < sinks.length; i++) {
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
    var allElements2 = allElements.slice();
    for (var i = allElements2.length - 1; i >= 0; i--) {
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
    for (var i = 0; i < allElements.length; i++) {
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
    var stopwords = [];
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
    var jsonCode = {};
    var i = 0;
    var node = node_input;
    while (!stopwords.includes(node.attributes.name)) {
        var target;
        if (jsonMerge.hasOwnProperty(node.attributes.id)) {
            jsonCode[i] = jsonMerge[node.attributes.id];
            target = jsonCode[i].targetId;
            if (node.attributes.name === "manual_loopstart") {
                var startIds_loop = graph.getConnectedLinks(graph.getCell(jsonMerge[node.attributes.id].targetId), {outbound: true});
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
function getNumberOfMergeElements(graphcopy) {
    var cnr = 0;
    for (var i = 0; i < graphcopy.length; i++) {
        if (graphcopy[i].attributes.name === "manual_ifstart" || graphcopy[i].attributes.name === "manual_loopstart") {
            cnr++;
        }
    }
    return cnr;
}

function updateHighlight() {
    for (var i = 0; i < allElements.length; i++) {
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
    var list_namesOfIncorrectElements = ["manual_ifstart", "manual_loopstart"];
    var stopwords = [];
    switch (graph.getCell(id).attributes.name) {
        //Note: loopstart only needs bot port ( one outgoing port )
        case "manual_loopstart":
            stopwords = ["manual_loopendcf", "manual_loopendct"];
            //Define: only 1 outgoing link on botside
            var startId_path = graph.getConnectedLinks(graph.getCell(id), {outbound: true})["0"].attributes.target.id;
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
            var loopid = node.attributes.id;
            var startIds_loop = graph.getConnectedLinks(graph.getCell(loopid), {outbound: true});
            //Define: Port Right or Left
            var startId_loop;
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
            var startids = graph.getConnectedLinks(graph.getCell(id), {outbound: true});
            var stopwords = ["manual_ifend"];
            for (var i = 0; i < startids.length; i++) {
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
function mergeSplittingElements(graphcopy) {
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
    var list_namesOfIncorrectElements = ["manual_ifstart", "manual_loopstart"];
    //Define: Array of all Merging Elements
    jsonMerge = {};
    //Define: Counter for Mergeelements
    var cnr_mergeElements = getNumberOfMergeElements(graphcopy);
    //console.log(graphcopy);
    //While: any merged element isnt converted in json
    while (cnr_mergeElements > 0) {
        //For: each element of graph
        for (var i = 0; i < graphcopy.length; i++) {
            var startelement = graphcopy[i].attributes.id;
            //If: elementtype belongs to mergetype
            if (list_nameOfCorrectChangingElements.includes(graphcopy[i].attributes.name)) {
                if (jsonMerge.hasOwnProperty(graphcopy[i].attributes.id)) {
                    continue;
                }
                if (isPathclean(graphcopy[i].attributes.id)) {
                    //Define: all links of mergeelement
                    var list_links = getLinksById(startelement);
                    //Define: structure of an entry
                    var json_element = {
                        "name": "",
                        "sourceId": "",
                        "targetId": "",
                        "content": []
                    }
                    //Define: sourceId for jsonMerge
                    var startelement = graphcopy[i].attributes.id;
                    //Define: startpoint bye elementype for search
                    switch (graphcopy[i].attributes.name) {
                        //Note: loopstart only needs bot port ( one outgoing port )
                        case "manual_loopstart":
                            //Define: only 1 outgoing link on botside
                            var startId_path = graph.getConnectedLinks(graph.getCell(startelement), {outbound: true})["0"].attributes.target.id;
                            //Define: json content Path from mergesource to condition

                            //Define: Mergestart --> name and sourceId
                            json_element.name = "manual_loopstart";
                            json_element.sourceId = startelement;
                            json_element["content"].path = connectElements(graph.getCell(startId_path), "manual_loopstart");
                            //Test: No elements on path
                            if (Object.entries(json_element.content.path).length != 0) {
                                if (list_namesOfIncorrectElements.includes(json_element.content.path[Object.keys(json_element.content.path).length - 1].name)) {
                                    var node = graph.getCell(json_element.content.path[Object.keys(json_element.content.path).length - 1].targetId)
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
                            var startIds_loop = graph.getConnectedLinks(graph.getCell(json_element.targetId), {outbound: true});
                            //Define: Port Right or Left
                            var startId_loop;
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
                            var startids = graph.getConnectedLinks(graph.getCell(startelement), {outbound: true});
                            //Define: branches of true and false
                            var branch_true;
                            var branch_false;
                            if (startids["0"].attributes.source.port === "left") {
                                branch_true = startids["0"].attributes.target.id;
                                branch_false = startids[1].attributes.target.id;
                            } else {
                                branch_true = startids[1].attributes.target.id;
                                branch_false = startids["0"].attributes.target.id;
                            }
                            //tripleIF
                            var nodeTrue = graph.getCell(branch_true);
                            var nodeFalse = graph.getCell(branch_false);
                            //nodeTrue = checkIfBranchesMergeObjects(nodeTrue);
                            //nodeFalse = checkIfBranchesMergeObjects(nodeFalse);
                            //Define: Content with JSON element, if there are no Followers undefined
                            json_element["content"].true = connectElements(nodeTrue, "manual_ifstart");
                            json_element["content"].false = connectElements(nodeFalse, "manual_ifstart");
                            json_element["content"].condition = graphcopy[i].attributes.einput;
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
            var str = [];
            if (el.varType1.length > 0) {
                str.push(el.varType1 + " " + el.varContent1);
            }
            if (el.varType2.length > 0) {
                str.push(el.varType2 + " " + el.varContent2);
            }
            if (el.area.length > 0) {
                var sep = el.area.split(newLine);
                for (var i = 0; i < sep.length; i++) {
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
            break
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
    var list = document.createElement('ul');
    for (var i = 0; i < log.length; i++) {
        var item = document.createElement('li');
        item.appendChild(document.createTextNode(log[i]));
        list.appendChild(item);
    }
    if ($('#editDiagramModal').hasClass('in')) {
        list.id = "list_errorModal";
        ;
    } else {
        list.id = "list_error";
    }
    return list;
}

/*Converting JSON Code in Programming Code
1.Define programming language as ojbect



*/

//Return: String,  Input: graph as json , programminglanguage as String
function convert_JsonToProgrammCode(json_graph, language) {
    var sel_langClass;
    switch (language) {
        case "java":
            sel_langClass = new Java(0);
            break;
        case "python":
            sel_langClass = new Python(1);
            break;
        default:
            log.push("Body f&uuml;r Sprache konnte nicht ermittelt werden.");
    }
    var result = buildProgramm(json_graph, sel_langClass);
    wrap = sel_langClass.get_core(startnode_inputtype, startnode_input, endnode_outputtype, endnode_output, methodName, result);
    if (graph.getCell(parentId).getEmbeddedCells().length > 0) {
        fillContentinElement(parentId, result);
    } else {
        try {
            document.getElementById("preCode").removeChild(document.getElementById("list_error"));
        } catch (e) {
        }
        document.getElementById("preCode").innerHTML = wrap;
        document.getElementById("sendToServer").className = "form-control btn-primary";
        document.getElementById("mainGeneration").className = "form-control";
        isCodeGenerated = true;
    }
//document.getElementById("code").innerHTML = "Es wurden keine Fehler bei der Konvertierung von der Zeichnung zum Programmcode erkannt. Sollten Sie der Meinung sein, dass Ihr Code nicht korrekt ermittelt wurde, senden Sie bitte den gespeicherten Graphen per Email an asdf@asdf.de";
}

//todo
function fillContentinElement(parentId, result) {
    var connectedLink = graph.getCell(graph.getConnectedLinks(graph.getCell(parentId))["0"]);
    var portLabel = connectedLink.attributes.source.port;
    var connectedCell = graph.getCell(connectedLink.attributes.source.id);
    var field;
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
    var textareaHeight = (result.split("\n").length) * 25;
    var parentView = connectedCell.findView(paper);
    for (var i = 0; i < parentView.$box["0"].children.length; i++) {
        if (parentView.$box["0"].children[i].nodeName === "TEXTAREA") {
            var oldTextAreaHeight = parentView.$box["0"].style.height;
            parentView.$box["0"].children[i].style.height = textareaHeight + "px";
            refreshElement(connectedCell);
        }
        if (parentView.$box["0"].children[i].nodeName === "DIV") {
            for (var j = 0; j < parentView.$attributes.length; j++) {
                console.log(parentView.$attributes[j].nodeName);
                if (parentView.$attributes[j].nodeName === "TEXTAREA") {
                    var oldTextAreaHeight = parentView.$attributes[j].style.height;
                    parentView.$attributes[j].style.height = textareaHeight + "px";
                    refreshElement(connectedCell);
                }

            }
        }
    }

}

function buildProgramm(json, sel_langClass) {
    var content = "";
    for (var i = 0; i < Object.keys(json).length; i++) {
        switch (json[i].name) {
            case "manual_ifstart":
                sel_langClass.set_increaseDeep(1);
                //console.log(sel_langClass);
                var left = buildProgramm(json[i].content.true, sel_langClass);
                var right = buildProgramm(json[i].content.false, sel_langClass);
                var condition = json[i].content.condition;
                content += sel_langClass.get_manualIf(condition, left, right, sel_langClass.deep);
                sel_langClass.set_increaseDeep(-1);
                //console.log(sel_langClass);
                break;
            case "manual_loopstart":
                sel_langClass.set_increaseDeep(1);
                var loop = " ".repeat(sel_langClass.deep - 1) + buildProgramm(json[i].content.loop, sel_langClass);
                var path = buildProgramm(json[i].content.path, sel_langClass);
                var condition = json[i].content.condition;
                var comb = loop + " ".repeat(sel_langClass.deep - 1) + path;
                var path2 = buildProgramm(json[i].content.path, sel_langClass.deep); //level of while
                content += sel_langClass.get_manualLoop(condition, path2, comb, sel_langClass.deep);
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
    var str = "";
    var deep = sel_langClass.deep;
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
            var eelse = createMoreLinesText(graphelement.content.eelse, deep + 1);
            var eif = createMoreLinesText(graphelement.content.eif, 0);
            var ethen = createMoreLinesText(graphelement.content.ethen, deep + 1);
            detectVariable(eelse, graphelement.id);
            detectVariable(eif, graphelement.id);
            detectVariable(ethen, graphelement.id);
            str = sel_langClass.get_if(eif, ethen, eelse, deep);
            break;
        case "forin":
            var efor = createMoreLinesText(graphelement.content.for, 0);
            var ein = createMoreLinesText(graphelement.content.in, 0);
            var earea = createMoreLinesText(graphelement.content.area, deep + 1);
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
    var text = "";
    if (Array.isArray(input)) {
        for (var i = 0; i < input.length; i++) {
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