import * as $ from 'jquery';
// import {Ace, edit} from 'ace';
import * as CodeMirror from 'codemirror';

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

function initEditor(language): CodeMirror.Editor {
    let textArea = document.getElementById('myTextArea');

    if (textArea instanceof HTMLTextAreaElement) {
        return CodeMirror.fromTextArea(textArea, {
            mode: {name: language},
            lineNumbers: true,
            value: '<xml></xml>'
        });
    } else {
        console.warn(textArea);
    }
}