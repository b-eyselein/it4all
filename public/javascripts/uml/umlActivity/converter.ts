const pattDeclaration = /^((String|Double|Boolean)|(String|Double|Boolean)\s*)\s+[a-z]+[a-z0-9]*\s*=\s*[a-z0-9"]+.*/;
const pattUpdate = /^\s*[a-z]+[a-z0-9]*\s*=\s*[a-z0-9"]+.*/;

//for getDataFromElement split(const)
const newLine = /\n/;

let log = []; // hints and feedback from testcases
let highlightedCells = []; //filled via tests
let currentVariables; //current used Variables in Diagramm

let isCodeGenerated; // needed for logpage to prevent rewrite previous errors from other papers

//TEST: ON,OFF
const testIfEndNodeIsAccessible = true;
const noUnknownElements = true;
const testIfEndnodeHasOutboundConnections = true;
const alternativeEnds = false; // broken, since editfields!
const disconnectedElements = true;
const elementsMustHaveInputs = true;
const isExternPortConnectedWithEditNode = true;

const MultipleDeclarations = false;
const DeclarationAgainstProgress = false;
const TypeAgainstValue = false;

interface ExerciseParameters {
    methodDisplay: string,
    methodDeclaration: string,
    methodName: string,
    methodParameters: string,
    output: {
        outputType: string,
        output: string,
        defaultValue: string
    }
}

interface ContentReadResult {
    success: boolean,
    contents: string[],
    logs: string[]
}

function readContentsFromGraph(languageBuilder: AbstractLanguageBuilder): ContentReadResult {
    let contents: string[] = [];
    let logs: string[] = [];

    let startNode = graph.getCell('Startknoten-startId');
    let endNode = graph.getCell('Endknoten-endId');

    let currentElement = startNode;

    let step = 0;
    let success = true;
    while (currentElement.id !== endNode.id && success && step < 100) {

        let cellView: joint.dia.CellView = currentElement.findView(paper);
        let model = cellView.model;

        cellView.highlight();

        // TODO: read content from element!
        if (model instanceof joint.shapes.uml.ActionInput) {
            contents.push(...model.getContent());
        } else if (model instanceof joint.shapes.uml.ForLoop) {
            let loopHeader = model.getLoopHeader();
            let loopContent: string[] = languageBuilder.addIdentation(model.get('loopContent'));
            contents.push(...[loopHeader, ...loopContent]);
        }

        let elementType = model.get('type');
        switch (elementType) {
            case 'uml.ActionInput':
                break;
            case 'uml.ForLoop':
                break;
            case 'html.Element':
                console.info(cellView);
                break;
            default:
                console.error(elementType);
                break;
        }

        let allOutboundLinks: joint.dia.Link[] = graph.getConnectedLinks(currentElement, {outbound: true});

        if (allOutboundLinks.length === 0) {
            logs.push('Element hat keinen Nachfolger!');
            success = false;
            break;
        } else if (allOutboundLinks.length > 1) {
            logs.push('Element ' + cellView.model.id + ' hat mehr als einen Nachfolger!');
            success = false;
            break;
        } else {
            currentElement = graph.getCell(allOutboundLinks[0].prop('target').id);
            cellView.unhighlight();
        }

        step++;
    }

    return {
        success,
        contents,
        logs
    };
}

function newGenerate() {
    let languageBuilder = getLangBuilder($('#langSelect').val());

    let readResult = readContentsFromGraph(languageBuilder);


    if (readResult.success) {
        let script = languageBuilder.getCore(EXERCISE_PARAMETERS, readResult.contents);
        console.info(script);

        $('#generatedCodeSection').prop('hidden', false);
        $('#preCode').html(script);
    } else {
        console.error(readResult.logs.join('\n'));
    }
}

function mainGeneration() {
    isCodeGenerated = false;

    let allElements: Element[] = graph.getElements();

    $('#preCode').html('');

    highlightedCells = [];
    currentVariables = [];
    log = [];  // reset logfile before starting

    // Generate code for the edit fields first --> last one is main paper
    for (let i = parentChildNodes.length - 1; i > -1; i--) {
        generateCodeForElement(parentChildNodes[i].parentId, parentChildNodes[i].startId, parentChildNodes[i].endId, allElements);
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

    // if no errors found and code is set in HTML
    if (!(isCodeGenerated && log.length === 0)) {
        document.getElementById('testButton').className = 'form-control';
        document.getElementById('mainGeneration').className = 'form-control btn-primary';
    }

    updateHighlight(allElements, highlightedCells);

    // set programmcode toggle
    $('#ExerciseText').collapse('hide');
    $('#Configuration').collapse('hide');
    $('#generatedCode').collapse('show');
    $('#resultDiv').collapse('hide');
}

function unique(list) {
    const res = [];
    $.each(list, function (i, e) {
        if ($.inArray(e, res) === -1) res.push(e);
    });
    return res;
}

interface GenerateCodeForElementResult {
    success: boolean,
    log: string[],
    result: string
}

//MAIN Function
function generateCodeForElement(parentId: string, startId: string, endId: string, allElements: Element[]): GenerateCodeForElementResult {
    let generationAlertsJq = $('#generationAlerts');
    generationAlertsJq.html('');

    let graphCopy;
    let graphIsCorrect = true;

    if (graph.getCell(parentId).getEmbeddedCells().length > 0) {
        graphCopy = graph.getCell(parentId).getEmbeddedCells();
    } else {
        graphCopy = removeUsedCells(graph.getElements());
    }

    // Testcases
    if (testIfEndNodeIsAccessible) {
        let endNodeAccessible = test_isEndNodeAccessible(graphCopy, startId, endId);
        if (!endNodeAccessible) {
            graphIsCorrect = false;
            log.push('Der Endknoten kann nicht erreicht werden.');
            highlightedCells.push(endId);
        }
    }

    if (testIfEndnodeHasOutboundConnections) {
        if (graph.getConnectedLinks(graph.getCell(endId), {outbound: true}).length > 0) {
            graphIsCorrect = false;
            log.push('Der Endknoten darf keine ausgehenden Verbindungen enthalten.');
            highlightedCells.push(endId);
        }
    }

    if (noUnknownElements) {
        let unknownElements = test_SearchForUnknownElements(graphCopy);
        if (unknownElements.length !== 0) {
            graphIsCorrect = false;
            highlightedCells.push(...unknownElements);
            log.push('Der Graph enthält Verzweigungselemente, welche nicht korrekt verbunden sind.');
        }
    }

    if (alternativeEnds) {
        let sinks = graph.getSinks().filter((sink) => !sink.id.startsWith('Endknoten') && sink.attributes.name !== 'edit');

        if (sinks.length !== 0) {
            graphIsCorrect = false;
            highlightedCells.push(...sinks);
            log.push((sinks.length > 1 ? 'Die Elemente ' + sinks.join(', ') + ' stellen jeweils' : 'Das Element ' + sinks[0] + ' stellt')
                + ' einen Endpunkt bzw. eine Senke (Endknoten exklusiv) dar.');
        }
    }

    if (disconnectedElements) {
        let disconnectedElements = test_disconnectedElements(allElements);
        if (disconnectedElements.length > 0) {
            graphIsCorrect = false;
            for (let disconnectedElement of disconnectedElements) {
                highlightedCells.push(disconnectedElement.id);
                log.push(disconnectedElement.msg);
            }
        }
    }

    if (elementsMustHaveInputs) {
        test_elementsMustHaveInputs(graphCopy, startId, endId);
    }

    if (isExternPortConnectedWithEditNode) {
        test_isExternPortConnectedWithEditNode(allElements);
    }

    // Generating Code, if no errors occured
    if (graphIsCorrect) {
        $('#preCode').html('');

        //Define: complete graph with Merged elements in a row
        const json_completeGraph = {};

        let node = graph.getSuccessors(graph.getCell(startId))['0']; // Define startId

        let cnr = 0; // set entries in json_completeGraph for inputlines
        while (node.attributes.id !== endId) {

            json_completeGraph[cnr] = getDataFromElement(graph.getCell(node.attributes.id).attributes);
            if (node.attributes.name === 'edit') {
                const neighbourId = graph.getConnectedLinks(node)['0'].attributes.source.id;
                const lastNodePorts = graph.getConnectedLinks(graph.getCell(neighbourId));
                node = graph.getCell(getPortByName(lastNodePorts, 'out'));
            } else {
                node = graph.getCell(graph.getSuccessors(node)['0']);
            }

            cnr++;
        }

        let selected_language = $('#langSelect').val();

        let result: string = convertJsonToProgrammCode(json_completeGraph, selected_language, parentId);

        generationAlertsJq.html(
            `<div class="alert alert-success">Die Generierung des Codes war erfolgreich.</div>`
        );

        return {
            success: true,
            log,
            result
        };

    } else {
        log = unique(log);
        generationAlertsJq.html(
            '<div class="alert alert-danger">Die Generierung des Codes war nicht erfolgreich. Es traten folgende Fehler auf:<ul>'
            + (log.map((str) => '<li>' + str + '</li>').join('\n')) + '</ul>');

        return {
            success: false,
            log: unique(log),
            result: ''
        }
    }
}

function getPortByName(portarray, name) {
    for (let i = 0; i < portarray.length; i++) {
        if (portarray[i].attributes.source.port === name) {
            return portarray[i].attributes.target.id;
        }
    }
}

function removeUsedCells(array) {
    const ret = [];
    for (let i = 0; i < array.length; i++) {
        if (!(array[i].attributes.name === 'edit' || array[i].attributes.hasOwnProperty('parent'))) {
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


function detectVariableInStringList(stringList, elementid) {
    for (let i = 0; i < stringList.length; i++) {
        detectVariableInString(stringList[i], elementid);
    }
}

function detectVariableInString(str, elementid) {

    let stringToTest = str.trim();

    //CASE: Declaration
    if (pattDeclaration.test(stringToTest)) {
        let strings = stringToTest.split('=');

        const type = stringToTest.substr(0, stringToTest.indexOf(' '));

        let variable = strings[0].substr(stringToTest.indexOf(' '), strings[0].length - 1).replace(/ /g, '');

        let value = strings[1];

        if (value.charAt(value.length - 1) === ';') {
            value = value.substr(0, value.length - 1);
        }

        if (isVariableActive(variable)) {
            for (let i = 0; i < currentVariables.length; i++) {
                if (currentVariables[i].variable === variable) {
                    currentVariables[i].found.push({'id': elementid, 'type': type, 'value': value});
                }
            }
        } else {
            currentVariables.push({
                variable: variable,
                lastValue: value,
                lastUpdateID: elementid,
                found: [{id: elementid, type, value}]
            });
        }
    }
    //CASE: Update
    if (pattUpdate.test(stringToTest)) {
        let s = stringToTest.split('=');
        let variable = s[0].replace(/ /g, '');
        let value = s[1];
        if (value.charAt(value.length - 1) === ';') {
            value = value.substr(0, value.length - 1);
        }
        if (isVariableActive(variable)) {
            for (let i = 0; i < currentVariables.length; i++) {
                if (currentVariables[i].variable === variable) {
                    currentVariables[i].lastValue = value;
                    currentVariables[i].lastUpdateID = elementid;
                }
            }
        }
    }
}


//Helper: elementsMustHaveInputs
function amountOfEditNodes(node) {
    let cnr = 0;
    const linkarray = graph.getConnectedLinks(node, {outbound: true});
    for (let i = 0; i < linkarray.length; i++) {
        if (linkarray[i].attributes.source.port === 'extern' || linkarray[i].attributes.source.port === 'extern-ethen' || linkarray[i].attributes.source.port === 'extern-eelse') {
            cnr++;
        }
    }
    return cnr;
}

/**
 * @param {object[]} allElements
 * @param {string[]} cellsToHighLight
 */
function updateHighlight(allElements, cellsToHighLight) {
    for (let i = 0; i < allElements.length; i++) {
        if (cellsToHighLight.includes(allElements[i].id)) {
            allElements[i].findView(paper).highlight();
        } else {
            allElements[i].findView(paper).unhighlight();
        }
    }
}

/**
 * Ausgabe der Datenfelder als Liste mit ID des ELEMENTS
 *
 * @param el
 *
 */
function getDataFromElement(el) {
    // FIXME: überarbeiten?

    let data = {
        name: el.name,
        id: el.id,
        content: []
    };

    switch (el.name) {

        case 'actionInput':
            data['content'].data = el.actionElementContent.split(newLine);
            break;
        case 'actionSelect':
            data['content'].data = el.actionElementContent;
            break;
        case 'actionDeclare':
            data['content'].data = el.varContent1 + ' ' + el.varContent2 + ' = ' + el.varContent3;
            break;

        case 'forLoop':
            data['content'].for = el.efor.split(newLine);
            data['content'].in = el.collectionName.split(newLine);
            data['content'].area = el.area.split(newLine);
            break;
        case 'doWhile':
            data['content'].ewhile = el.ewhile.split(newLine);
            data['content'].edo = el.edo.split(newLine);
            break;
        case 'whileDo':
            data['content'].ewhile = el.ewhile.split(newLine);
            data['content'].edo = el.edo.split(newLine);
            break;

        case 'ifThen':
            data['content'].eif = el.eif.split(newLine);
            data['content'].ethen = el.ethen.split(newLine);
            break;
        case 'if':
            data['content'].eif = el.eif.split(newLine);
            data['content'].ethen = el.ethen.split(newLine);
            data['content'].eelse = el.eelse.split(newLine);
            break;

        case 'edit':
            break;

        case 'end':
            break;
        case 'start':
            break;

        default:
            console.log('Daten vom Element mit Typ ' + el.name + ' konnten nicht ausgelesen werden');
    }
    return data;
}

function getLangBuilder(language: string): AbstractLanguageBuilder {
    switch (language) {
        case 'JAVA_8':
            return Java;
        case 'PYTHON_3':
            return Python;
        default:
            log.push('Body für Sprache konnte nicht ermittelt werden: Nutze Python...');
            return Python;
    }
}

/**
 * @param {object} json_graph
 * @param {string} language
 * @param {string} parentId
 *
 * @return: {string},
 */
function convertJsonToProgrammCode(json_graph, language: string, parentId: string): string {

    let selectedLanguageBuilderType = getLangBuilder(language);

    const programContent: string[] = buildProgramm(json_graph, selectedLanguageBuilderType);

    // FIXME: get_core!

    let wrap = selectedLanguageBuilderType.getCore(EXERCISE_PARAMETERS, programContent);

    if (graph.getCell(parentId).getEmbeddedCells().length > 0) {
        fillContentInElement(parentId, programContent);
    } else {
        try {
            document.getElementById('preCode').removeChild(document.getElementById('list_error'));
        } catch (e) {
        }

        $('#preCode').html(wrap);

        let submitButton = $('#testButton');
        submitButton.removeClass('btn-default').addClass('btn-primary').prop('disabled', false);
        submitButton.prop('title', '');
        document.getElementById('mainGeneration').className = 'form-control';
        isCodeGenerated = true;
    }

    return wrap;
}

//todo
/**
 *
 * @param parentId
 * @param {string[]} result
 */
function fillContentInElement(parentId, result) {
    const connectedLink = graph.getCell(graph.getConnectedLinks(graph.getCell(parentId))['0']);
    const portLabel = connectedLink.attributes.source.port;
    const connectedCell = graph.getCell(connectedLink.attributes.source.id);
    let field;

    switch (connectedCell.attributes.name) {
        case 'forLoop':
            field = 'area';
            break;
        case 'ifThen':
            field = 'ethen';
            break;
        case 'if':
            switch (portLabel) {
                case 'extern-ethen':
                    field = 'ethen';
                    break;
                case 'extern-eelse':
                    field = 'eelse';
                    break;
            }
            break;
        case 'doWhile':
            field = 'edo';
            break;
        case 'whileDo':
            field = 'edo';
            break;
    }
    connectedCell.prop(field, result.join('\n'));

    const textareaHeight = (result.length) * 25;
    const parentView = connectedCell.findView(paper);

    for (let i = 0; i < parentView.$box['0'].children.length; i++) {
        if (parentView.$box['0'].children[i].nodeName === 'TEXTAREA') {
            parentView.$box['0'].children[i].style.CLASS_HEIGHT = textareaHeight + 'px';
            refreshElement(connectedCell);
        }
        if (parentView.$box['0'].children[i].nodeName === 'DIV') {
            for (let j = 0; j < parentView.$attributes.length; j++) {
                // console.log(parentView.$attributes[j].nodeName);
                if (parentView.$attributes[j].nodeName === 'TEXTAREA') {
                    // var oldTextAreaHeight = parentView.$attributes[j].style.CLASS_HEIGHT;
                    parentView.$attributes[j].style.CLASS_HEIGHT = textareaHeight + 'px';
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
 * @returns {string[]}
 */
function buildProgramm(json, sel_langClass): string[] {
    let theContent = [];

    for (let i = 0; i < Object.keys(json).length; i++) {
        let allToAdd = readDataFromLanguage(json[i], sel_langClass);
        theContent.push(...allToAdd);
    }
    return theContent;
}

/**
 * code from a graphelement as string array, built for selected language
 *
 * @param graphelement
 * @param selectedLangBuilder
 * @returns {string[]}
 */
function readDataFromLanguage(graphelement, selectedLangBuilder: AbstractLanguageBuilder): any {
    switch (graphelement.name) {

        case 'actionInput':
            let actionInputContent /* string[] */ = graphelement.content.data;
            detectVariableInStringList(actionInputContent, graphelement.id);
            return actionInputContent;

        case 'actionSelect':
            let actionSelectContent /* string */ = graphelement.content.data;
            detectVariableInString(actionSelectContent, graphelement.id);
            return [actionSelectContent];

        case 'actionDeclare':
            let actionDeclareContent /* string */ = graphelement.content.data;
            detectVariableInStringList(actionDeclareContent, graphelement.id);
            return [actionDeclareContent];

        case 'if':
            const ifConditionContent /* string */ = graphelement.content.eif.join('');
            const thenContent  /* string[] */ = graphelement.content.ethen;
            const elseContent /* string[] */ = graphelement.content.eelse;

            detectVariableInString(ifConditionContent, graphelement.id);
            detectVariableInStringList(thenContent, graphelement.id);
            detectVariableInStringList(elseContent, graphelement.id);

            return selectedLangBuilder.getIfElse(ifConditionContent, thenContent, elseContent);

        case 'forLoop':
            const variable: string = graphelement.content.for.join('');
            const collection: string = graphelement.content.in.join('');
            const forContent: string[] = graphelement.content.area;

            detectVariableInString(variable, graphelement.id);
            detectVariableInString(collection, graphelement.id);
            detectVariableInStringList(forContent, graphelement.id);

            return selectedLangBuilder.getFor(variable, collection, forContent);

        case 'doWhile':
            let doContent /* string[] */ = graphelement.content.edo;
            let doWhileConditionContent /* string */ = graphelement.content.ewhile.join('');

            detectVariableInStringList(doContent, graphelement.id);
            detectVariableInString(doWhileConditionContent, graphelement.id);

            return selectedLangBuilder.getDoWhile(doWhileConditionContent, doContent);

        case 'whileDo':
            let whileDoContent /* string[] */ = graphelement.content.edo;
            let whileDoConditionContent /* string */ = graphelement.content.ewhile.join('');

            detectVariableInStringList(whileDoContent, graphelement.id);
            detectVariableInString(whileDoConditionContent, graphelement.id);

            return selectedLangBuilder.getWhileDo(whileDoConditionContent, whileDoContent);

        case 'ifThen':
            let ifThenContent /* string[] */ = graphelement.content.ethen;
            let ifThenConditionContent /* string */ = graphelement.content.eif.join('');

            detectVariableInStringList(ifThenContent, graphelement.id);
            detectVariableInString(ifThenConditionContent, graphelement.id);

            return selectedLangBuilder.getIfThen(ifThenConditionContent, ifThenContent);

        case 'edit':
            // FIXME called when?
            console.log('Edit element: ');
            console.log(graphelement);
            return [];

        default:
            console.warn(graphelement);
            console.error('Es konnte Daten aus dem Graphelement nicht lesen: ' + graphelement.name);
            return [];
    }
}
