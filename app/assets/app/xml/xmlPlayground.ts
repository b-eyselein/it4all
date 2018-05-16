import * as $ from "jquery";
import {initEditor} from "../editorHelpers";
import * as CodeMirror from 'codemirror';
import 'codemirror/mode/xml/xml';

let editor: CodeMirror.Editor;

$(() => {
    editor = initEditor('xml');
});