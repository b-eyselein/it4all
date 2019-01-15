import * as $ from "jquery";
import * as CodeMirror from 'codemirror';

import {ActionInput, ForLoopText, IfElseText, WhileLoopText} from "./umlActivityElements";
import {umlActivityGraph} from "./umlActivityDrawing";
import {initEditor} from "../../editorHelpers";

export {editActionInput, editForLoopText, editIfElseText, editWhileLoop};


const NEWLINE = '\n';

function getCell(button: HTMLElement): joint.dia.Cell {
    return umlActivityGraph.getCell($(button).data('cellId'));
}

// Action Input

let actionInputEditor: CodeMirror.Editor = null;

let actionInputEditSection: JQuery, actionInputSubmitBtn: JQuery, actionInputResetBtn: JQuery;

function actionInputInstantiate(): void {
    actionInputEditSection = $('#actionInputEditSection');

    actionInputSubmitBtn = $('#actionInputButton');
    actionInputSubmitBtn.on('click', updateActionInput);

    actionInputResetBtn = $('#actionInputResetBtn');
    actionInputResetBtn.on('click', resetActionInput);
}

function resetActionInput(): void {
    actionInputSubmitBtn.data('cellId', '');

    actionInputEditor.setValue('');

    actionInputEditSection.prop('hidden', true);
}


function updateActionInput(event: Event): void {
    const cell = getCell(event.target as HTMLElement);

    if (cell instanceof ActionInput) {
        cell.setContent(actionInputEditor.getValue().split(NEWLINE).filter((l) => l.trim().length !== 0));
    } else {
        console.error('Cell ' + cell + ' had wrong type...');
    }

    resetActionInput();
}

function editActionInput(actionInput: ActionInput): void {
    actionInputSubmitBtn.data('cellId', actionInput.id);

    actionInputEditSection.prop('hidden', false);

    if (actionInputEditor == null) {
        actionInputEditor = initEditor('python', 'actionInputEditor');
    }

    actionInputEditor.setValue(actionInput.getContent().join(NEWLINE));
    actionInputEditor.focus();
}

// For Loop Text

let forLoopEditor: CodeMirror.Editor = null;

let forLoopTextEditSection: JQuery, forLoopVariableInput: JQuery, forLoopCollectionInput: JQuery,
    forLoopTextSubmitBtn: JQuery, forLoopTextResetBtn: JQuery;

function forLoopInstantiate(): void {
    forLoopTextEditSection = $('#forLoopEditSection');

    forLoopVariableInput = $('#forLoopVariableInput');
    forLoopCollectionInput = $('#forLoopCollectionInput');

    forLoopTextSubmitBtn = $('#forLoopTextSubmitBtn');
    forLoopTextSubmitBtn.on('click', updateForLoopText);

    forLoopTextResetBtn = $('#forLoopTextResetBtn');
    forLoopTextResetBtn.on('click', resetForLoopText);
}

function resetForLoopText(): void {
    forLoopTextSubmitBtn.data('cellId', '');

    forLoopVariableInput.val('');
    forLoopCollectionInput.val('');

    forLoopEditor.setValue('');

    forLoopTextEditSection.prop('hidden', true);
}

function updateForLoopText(event: Event) {
    const cell = getCell(event.target as HTMLElement);

    if (cell instanceof ForLoopText) {

        cell.setVariable(forLoopVariableInput.val() as string);
        cell.setCollection(forLoopCollectionInput.val() as string);

        cell.setLoopContent(forLoopEditor.getValue().split(NEWLINE).filter((l) => l.trim().length !== 0));

    } else {
        console.error('Cell ' + cell + ' had wrong type...');
    }

    resetForLoopText();
}

function editForLoopText(forLoopText: ForLoopText): void {
    forLoopTextSubmitBtn.data('cellId', forLoopText.id);

    forLoopVariableInput.val(forLoopText.get('variable'));
    forLoopCollectionInput.val(forLoopText.get('collection'));

    forLoopTextEditSection.prop('hidden', false);

    if (forLoopEditor == null) {
        forLoopEditor = initEditor('python', 'forLoopTextEditor');
    }

    forLoopEditor.setValue(forLoopText.getLoopContent().join(NEWLINE));
    forLoopEditor.focus();
}

// If-Else Text

let ifEditor: CodeMirror.Editor = null, elseEditor: CodeMirror.Editor = null;

let ifElseTextEditSection: JQuery, ifElseTextConditionInput: JQuery, ifElseTextSubmitBtn: JQuery,
    ifElseTextResetBtn: JQuery;

function ifElseInstantiate(): void {
    ifElseTextEditSection = $('#ifElseTextEditSection');

    ifElseTextConditionInput = $('#ifElseTextConditionInput');

    ifElseTextSubmitBtn = $('#ifElseTextSubmitBtn');
    ifElseTextSubmitBtn.on('click', updateIfElseText);

    ifElseTextResetBtn = $('#ifElseTextResetBtn');
    ifElseTextResetBtn.on('click', resetIfElseText);
}

function resetIfElseText(): void {
    ifElseTextSubmitBtn.data('cellId', '');

    ifElseTextConditionInput.val('');

    ifEditor.setValue('');
    elseEditor.setValue('');

    ifElseTextEditSection.prop('hidden', true);
}

function updateIfElseText(event: Event): void {
    const cell = getCell(event.target as HTMLElement);

    if (cell instanceof IfElseText) {
        cell.setCondition(ifElseTextConditionInput.val() as string);

        cell.setIfContent(ifEditor.getValue().split(NEWLINE).filter((l) => l.trim().length !== 0));
        cell.setElseContent(elseEditor.getValue().split(NEWLINE).filter((l) => l.trim().length !== 0));
    } else {
        console.error('Cell ' + cell + ' had wrong type...');
    }

    resetIfElseText();
}

function editIfElseText(ifElseText: IfElseText): void {
    ifElseTextSubmitBtn.data('cellId', ifElseText.id);

    ifElseTextEditSection.prop('hidden', false);

    ifElseTextConditionInput.val(ifElseText.getCondition());

    if (ifEditor == null || elseEditor == null) {
        ifEditor = initEditor('python', 'ifTextEditor');
        elseEditor = initEditor('python', 'elseTextEditor');
    }

    ifEditor.setValue(ifElseText.getIfContent().join(NEWLINE));
    elseEditor.setValue(ifElseText.getElseContent().join(NEWLINE));
}

// While Text

let whileEditor: CodeMirror.Editor;

let whileTextEditSection: JQuery, whileTextEditConditionInput: JQuery, whileTextEditSubmitBtn: JQuery,
    whileTextEditResetBtn: JQuery;

function whileInstantiate(): void {
    whileTextEditSection = $('#whileTextEditSection');

    whileTextEditConditionInput = $('#whileTextConditionInput');

    whileTextEditSubmitBtn = $('#whileTextSubmitBtn');
    whileTextEditSubmitBtn.on('click', updateWhileText);

    whileTextEditResetBtn = $('#whileTextResetBtn');
    whileTextEditResetBtn.on('click', resetWhileText);

}

function updateWhileText(event: Event): void {
    const cell = getCell(event.target as HTMLElement);

    if (cell instanceof WhileLoopText) {
        cell.setCondition(whileTextEditConditionInput.val() as string);
        cell.setLoopContent(whileEditor.getValue().split(NEWLINE).filter((l) => l.trim().length !== 0));
    } else {
        console.error('Cell ' + cell + ' had wrong type...');
    }

    resetWhileText();
}

function resetWhileText(): void {
    whileTextEditSubmitBtn.data('cellId', '');

    whileTextEditConditionInput.val('');

    whileEditor.setValue('');

    whileTextEditSection.prop('hidden', true);
}

function editWhileLoop(whileLoopText: WhileLoopText): void {
    whileTextEditSubmitBtn.data('cellId', whileLoopText.id);

    whileTextEditSection.prop('hidden', false);

    whileTextEditConditionInput.val(whileLoopText.getCondition());

    if (whileEditor == null) {
        whileEditor = initEditor('python', 'whileLoopTextEditor');
    }

    whileEditor.setValue(whileLoopText.getLoopContent().join(NEWLINE));
}

$(() => {

    actionInputInstantiate();

    forLoopInstantiate();

    ifElseInstantiate();

    whileInstantiate();

});