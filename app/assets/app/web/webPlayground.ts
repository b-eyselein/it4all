import * as $ from "jquery";
import {initEditor} from "../editorHelpers";
import * as CodeMirror from 'codemirror';
import 'codemirror/mode/htmlmixed/htmlmixed';

$(() => {
    let editor = initEditor('htmlmixed');
    editor.on('change', (cm: CodeMirror.Editor) => {
        let preview: HTMLIFrameElement = document.getElementById('preview') as HTMLIFrameElement;
        preview.contentDocument.open();
        preview.contentDocument.write(cm.getValue());
        preview.contentDocument.close();
    })
});