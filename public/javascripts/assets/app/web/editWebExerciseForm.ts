import * as $ from 'jquery';

import * as CodeMirror from 'codemirror';
import {initEditor} from "../editorHelpers";
import 'codemirror/mode/htmlmixed/htmlmixed';
import {domReady} from "../otherHelpers";

let addHtmlTaskBtn: JQuery<HTMLButtonElement>;
let htmlTasksDiv: JQuery<HTMLDivElement>;

let editor: CodeMirror.Editor;

domReady(() => {
    editor = initEditor('htmlmixed', 'sampleSolution_editor');
    editor.on('change', () => {
        console.info(editor.getValue());
        $('#sampleSolution').val(editor.getValue());
    });

    htmlTasksDiv = $('#htmlTasksDiv');
    addHtmlTaskBtn = $('#addHtmlTaskBtn');
    addHtmlTaskBtn.on('click', () => {
        const newId = htmlTasksDiv.children().length;

        htmlTasksDiv.append(`
<div class="form-group">
    <div class="card">
        <div class="card-body">
            <div class="form-group row">
                <label class="col-form-label col-md-2" for="htmlTasks_${newId}_id">ID:</label>
                <div class="col-md-2">
                    <input type="number" class="form-control" name="htmlTasks[${newId}].id" id="htmlTasks_${newId}_id" value="${newId + 1}" required="" readonly="">
                </div>
                
                <label class="col-form-label col-md-1" for="htmlTasks_${newId}_xpathQuery">XPath:</label>
                <div class="col-md-7">
                    <input type="text" class="form-control" name="htmlTasks[${newId}].xpathQuery" id="htmlTasks_${newId}_xpathQuery" value="" required="" placeholder="XPath - Query">
                </div>
            </div>
        
            <div class="form-group row">
                <label class="col-form-label col-md-2" for="htmlTasks_${newId}_text">Text:</label>
                <div class="col-md-10">
                    <textarea class="form-control" name="htmlTasks[${newId}].text" id="htmlTasks_${newId}_text" rows="3" required="" placeholder="Teilaufgabentext"></textarea>
                </div>
            </div>
        
            <div class="form-group row">
                <label class="col-form-label col-md-2" for="htmlTasks_${newId}_textContent">Textinhalt:</label>
                <div class="col-md-10">
                    <input type="text" class="form-control" name="htmlTasks[${newId}].textContent" id="htmlTasks_${newId}_textContent" placeholder="Textinhalt des Elements, optional">
                </div>
            </div>
        </div>
    </div>
</div>`.trim());
    });

});
