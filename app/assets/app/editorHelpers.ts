import * as $ from 'jquery';
import * as monaco from 'monaco-editor';
// import {Ace, edit} from 'ace';

export {initEditor};

function setFontSize(value: number): void {
    $('#editor').css({'fontSize': value + 'px'})
}

function changeFontSize(value: number): void {
    const fontSizeElement = $('#fontSize');
    const fontSize = parseInt(fontSizeElement.text()) + value;
    setFontSize(fontSize);
    fontSizeElement.text(fontSize);
}

function initEditor(language) {
    const domElement = document.getElementById('editor');
    let editor: monaco.editor.IStandaloneCodeEditor = monaco.editor.create(domElement, {
        language,
        value: 'test'
    });
    return editor;
}

// function initEditor(language: string, minLines: number, maxLines: number): Ace.Editor {
//
//     setFontSize(16);
//
//     let newEditor: Ace.Editor = edit('editor');
//
//     newEditor.setTheme('ace/theme/eclipse');
//
//     newEditor.getSession().setMode('ace/mode/' + language);
//
//     newEditor.getSession().setTabSize(2);
//
//     newEditor.getSession().setUseSoftTabs(true);
//     newEditor.getSession().setUseWrapMode(true);
//
//     newEditor.setOptions({minLines, maxLines});
//
//     return newEditor;
// }
