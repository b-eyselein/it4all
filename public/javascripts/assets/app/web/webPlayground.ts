import * as CodeMirror from 'codemirror';
import 'codemirror/mode/htmlmixed/htmlmixed';
import {initEditor} from "../editorHelpers";

import {domReady} from "../otherHelpers";

domReady(() => {
    let editor = initEditor('htmlmixed', 'webEditor');

    editor.on('change', (cm: CodeMirror.Editor) => {
        let preview = document.getElementById('preview');

        if (preview instanceof HTMLIFrameElement) {
            preview.contentDocument.open();
            preview.contentDocument.write(cm.getValue());
            preview.contentDocument.close();
        }
    });

});
