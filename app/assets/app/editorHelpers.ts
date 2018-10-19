import * as $ from 'jquery';
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

function initEditor(language: string, elementId: string): CodeMirror.Editor {
    let textArea = document.getElementById(elementId);

    if (textArea instanceof HTMLTextAreaElement) {
        return CodeMirror.fromTextArea(textArea, {
            mode: {name: language},
            lineNumbers: true,
            value: '<xml></xml>',
            indentUnit: 4
        });
    } else {
        console.warn(textArea);
    }
}