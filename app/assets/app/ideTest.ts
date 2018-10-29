import * as $ from 'jquery';
import * as CodeMirror from 'codemirror';
import {initEditor} from './editorHelpers';

const modes = ["python", "htmlmixed", "jinja2"];

modes.forEach(mode => {
    import('codemirror/mode/' + mode + '/' + mode);
});

let filenames: string[] = [];
let fileBtns: JQuery;

let files: Map<string, LoadFileSingleResult> = new Map<string, LoadFileSingleResult>();

let editor: CodeMirror.Editor;

interface LoadFileSingleResult {
    name: string,
    content: string,
    filetype: string,
}


function onLoadFileSuccess(result: LoadFileSingleResult[]): void {
    for (const res of result) {
        files.set(res.name, res);
    }

    console.warn(files);
    const firstFile: LoadFileSingleResult = files.get("app.py");

    editor = initEditor(firstFile.filetype, "myTextEditor");


    editor.setValue(firstFile.content);
}

$(() => {
    fileBtns = $('.fileBtn');

    fileBtns.each((_: number, el: HTMLElement) => {
        filenames.push($(el).data('filename'));
    });

    fileBtns.on('click', (event: JQuery.Event) => {
        const clickedBtn: JQuery<HTMLButtonElement> = $(event.target as HTMLButtonElement);

        const nextFileName: string = clickedBtn.data('filename') as string;

        const nextFile: LoadFileSingleResult = files.get(nextFileName);

        editor.setValue(nextFile.content);
        editor.setOption('mode', nextFile.filetype);
    });

    $.ajax({
        url: "http://localhost:9000/ideFiles",
        data: JSON.stringify(filenames),
        error: jqXHR => {
            console.error(jqXHR)
        },
        success: onLoadFileSuccess
    });

});