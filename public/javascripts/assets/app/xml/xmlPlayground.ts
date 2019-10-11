import * as CodeMirror from 'codemirror';
import 'codemirror/mode/xml/xml';
import {initEditor} from "../editorHelpers";
import {domReady} from "../otherHelpers";

let editor: CodeMirror.Editor;

domReady(() => {
    editor = initEditor('xml', 'xmlEditor');
});
