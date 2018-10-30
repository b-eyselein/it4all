import * as $ from 'jquery';
import * as CodeMirror from 'codemirror';
import {initEditor} from './editorHelpers';

let filenames: string[] = [];
let fileBtns: JQuery;

let files: Map<string, LoadFileSingleResult> = new Map<string, LoadFileSingleResult>();

let editor: CodeMirror.Editor;

interface LoadFileSingleResult {
    name: string,
    content: string,
    filetype: string,
}


function onLoadFileSuccess(result: LoadFileSingleResult[], activeFile: string): void {
    // Fill file map
    for (const res of result) {
        files.set(res.name, res);
    }

    // Read all CodeMirror modes from result array
    const allModes: string[] = Array.from(new Set(result.map(r => r.filetype)));

    // Load all file modes
    Promise.all(allModes.map(mode => import('codemirror/mode/' + mode + '/' + mode)))
        .then(() => {
            // Init editor (all modes have already been loaded!)
            const firstFile: LoadFileSingleResult = files.get(activeFile);
            // FIXME: get => null!
            editor = initEditor(firstFile.filetype, "myTextEditor");
        });
}

$(() => {
    fileBtns = $('.fileBtn');

    fileBtns.each((_: number, el: HTMLElement) => {
        filenames.push($(el).data('filename'));
    });

    const activeFile: string = $('.btn-primary').data('filename');

    fileBtns.on('click', (event: JQuery.Event) => {
        const clickedBtn: JQuery<HTMLButtonElement> = $(event.target as HTMLButtonElement);

        const nextFileName: string = clickedBtn.data('filename') as string;

        const nextFile: LoadFileSingleResult = files.get(nextFileName);

        editor.setValue(nextFile.content);
        editor.setOption('mode', nextFile.filetype);

        fileBtns.removeClass('btn-primary').addClass('btn-outline-secondary');
        clickedBtn.removeClass('btn-outline-secondary').addClass('btn-primary');
    });

    $.ajax({
        url: "http://localhost:9000/ideFiles",
        data: JSON.stringify(filenames),
        error: jqXHR => {
            console.error(jqXHR)
        },
        success: (r) => onLoadFileSuccess(r, activeFile)
    });

});