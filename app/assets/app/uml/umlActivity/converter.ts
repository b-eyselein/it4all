import * as $ from 'jquery';
import * as joint from 'jointjs';

import {mainEndNode, mainStartNode, umlActivityGraph, umlActivityPaper} from "./umlActivityDrawing";
import {AbstractLanguageBuilder, Java, Python} from "./languageBuilders";
import {ActionInput, Edit, ForLoopText, IfElseText} from "./umlActivityElements";
import {ExerciseParameters} from "../umlInterfaces";

export {newGenerate};

interface ContentReadResult {
    success: boolean,
    contents: string[],
    logs: string[]
}

function readContentFromTo(languageBuilder: AbstractLanguageBuilder, startNode: joint.dia.Cell, endNode: joint.dia.Cell): ContentReadResult {
    let contents: string[] = [];
    let logs: string[] = [];

    let currentElement = startNode;

    let step = 0;
    let success = true;
    while (currentElement.id !== endNode.id && success && step < 100) {

        let cellView: joint.dia.CellView = currentElement.findView(umlActivityPaper);
        let model = cellView.model;

        cellView.highlight();

        // TODO: read content from element!
        if (model instanceof ActionInput) {
            contents.push(...model.getContent());
        } else if (model instanceof ForLoopText) {
            if (model.isOkay()) {
                let variable = model.getVariable();
                let collection = model.getCollection();
                let loopContent: string[] = languageBuilder.addIdentation(model.getLoopContent());

                contents.push(...languageBuilder.getFor(variable, collection, loopContent));
            } else {
                success = false;
                logs.push('For-Schleife hat keine Variable oder Collection!');
                return {success, contents, logs};
            }
        } else if (model instanceof IfElseText) {
            if (model.isOkay()) {
                let elseContent = model.getElseContent();
                contents.push(...languageBuilder.getIfElse(model.getCondition(), model.getIfContent(), elseContent));
            } else {
                success = false;
                logs.push('If-Else-Verzweigung hat keine Bedingung!');
                return {success, contents, logs};
            }
        } else if (model instanceof Edit) {
            // FIXME: ERROR!
            success = false;
            logs.push('Edit-Elemente können nicht so benutzt werden!');
            return {success, contents, logs};
        }

        let elementType = model.get('type');
        switch (elementType) {
            case 'uml.ActionInput':
            case 'uml.ForLoopText':
            case 'uml.CustomStartState':
            case 'uml.CustomEndState':
            case 'uml.IfElseText':
                break;
            case 'html.Element':
                console.info(cellView);
                break;
            default:
                console.error(elementType);
                break;
        }

        let allOutboundLinks: joint.dia.Link[] = umlActivityGraph.getConnectedLinks(currentElement, {outbound: true}).filter((l) => l.get('source').port === 'out');

        if (allOutboundLinks.length === 0) {
            logs.push('Element hat keinen Nachfolger!');
            success = false;
            break;
        } else if (allOutboundLinks.length > 1) {
            logs.push('Element ' + cellView.model.id + ' hat mehr als einen Nachfolger!');
            success = false;
            break;
        } else {
            currentElement = umlActivityGraph.getCell(allOutboundLinks[0].prop('target').id);
            cellView.unhighlight();
        }

        step++;
    }

    return {success, contents, logs};
}

function newGenerate(exerciseParameters: ExerciseParameters): void {
    let languageBuilder = getLangBuilder($('#langSelect').val() as string);

    let codeSection = $('#preCode');
    let messageSection = $('#generationMsgSection');
    let correctButton = $('#testBtn');
    let correctionSection = $('#correctionSection');

    codeSection.html('');
    messageSection.html('');

    let readResult = readContentFromTo(languageBuilder, mainStartNode, mainEndNode);

    $('#generatedCodeSection').prop('hidden', false);

    if (readResult.success) {
        let script = languageBuilder.getCore(exerciseParameters, readResult.contents);

        codeSection.html(script);
        codeSection.prop('hidden', false);

        messageSection.prop('hidden', true);

        correctButton.prop('disabled', false);
        correctButton.removeClass('btn-default').addClass('btn-primary');

        correctionSection.prop('hidden', false);

        $('#generationAlerts').html('');
    } else {
        codeSection.prop('hidden', true);

        messageSection.html(`<div class="alert alert-danger">${readResult.logs.join('\n')}</div>`);
        messageSection.prop('hidden', false);

        correctButton.prop('disabled', true);
        correctButton.removeClass('btn-primary').addClass('btn-default');

        correctionSection.prop('hidden', true);
    }
}

function getLangBuilder(language: string): AbstractLanguageBuilder {
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