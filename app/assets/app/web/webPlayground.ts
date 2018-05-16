import * as $ from "jquery";
import {initEditor} from "../editorHelpers";
import * as CodeMirror from 'codemirror';
import 'codemirror/mode/htmlmixed/htmlmixed';

let editor: CodeMirror.Editor;

$(() => {
    editor = initEditor('htmlmixed');
});