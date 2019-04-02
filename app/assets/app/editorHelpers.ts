import * as CodeMirror from 'codemirror';

export function initEditor(language: string, elementId: string): CodeMirror.Editor {
    // FIXME: dynamically import mode (language...) with import(): Promise!

    let textArea: HTMLElement | null = document.getElementById(elementId);

    if (textArea instanceof HTMLTextAreaElement) {
        return CodeMirror.fromTextArea(textArea, {
            mode: {name: language},
            lineNumbers: true,
            indentUnit: 4
        });
    } else if (textArea === null) {
        console.error("TextArea could not be found!");
    } else {
        console.warn(textArea);
    }
}
