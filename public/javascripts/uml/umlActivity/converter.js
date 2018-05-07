function readContentFromTo(languageBuilder, startNode, endNode) {
    let contents = [];
    let logs = [];
    let currentElement = startNode;
    let step = 0;
    let success = true;
    while (currentElement.id !== endNode.id && success && step < 100) {
        let cellView = currentElement.findView(paper);
        let model = cellView.model;
        cellView.highlight();
        if (model instanceof joint.shapes.uml.ActionInput) {
            contents.push(...model.getContent());
        }
        else if (model instanceof joint.shapes.uml.ForLoopText) {
            let loopHeader = model.getLoopHeader();
            let loopContent = languageBuilder.addIdentation(model.get('loopContent'));
            contents.push(...[loopHeader, ...loopContent]);
        }
        let elementType = model.get('type');
        switch (elementType) {
            case 'uml.ActionInput':
            case 'uml.ForLoopText':
                break;
            case 'html.Element':
                console.info(cellView);
                break;
            default:
                console.error(elementType);
                break;
        }
        let allOutboundLinks = graph.getConnectedLinks(currentElement, { outbound: true });
        if (allOutboundLinks.length === 0) {
            logs.push('Element hat keinen Nachfolger!');
            success = false;
            break;
        }
        else if (allOutboundLinks.length > 1) {
            logs.push('Element ' + cellView.model.id + ' hat mehr als einen Nachfolger!');
            success = false;
            break;
        }
        else {
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
    let readResult = readContentFromTo(languageBuilder, mainStartNode, mainEndNode);
    if (readResult.success) {
        let script = languageBuilder.getCore(EXERCISE_PARAMETERS, readResult.contents);
        console.info(script);
        $('#generatedCodeSection').prop('hidden', false);
        $('#preCode').html(script);
    }
    else {
        console.error(readResult.logs.join('\n'));
    }
}
function updateHighlight(allElements, cellsToHighLight) {
    for (let i = 0; i < allElements.length; i++) {
        if (cellsToHighLight.includes(allElements[i].id)) {
            allElements[i].findView(paper).highlight();
        }
        else {
            allElements[i].findView(paper).unhighlight();
        }
    }
}
function getLangBuilder(language) {
    switch (language) {
        case 'JAVA_8':
            return Java;
        case 'PYTHON_3':
            return Python;
        default:
            console.warn('Body für Sprache konnte nicht ermittelt werden: Nutze Python...');
            return Python;
    }
}
//# sourceMappingURL=converter.js.map