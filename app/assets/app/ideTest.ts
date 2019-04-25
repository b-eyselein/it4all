import * as $ from 'jquery';
import * as CodeMirror from 'codemirror';
import {initEditor} from './editorHelpers';
import {domReady} from "./otherHelpers";

let filenames: string[] = [];
let activeFile: string;

let fileBtns: JQuery;

let uploadBtn: JQuery<HTMLButtonElement>;

let files: Map<string, LoadFileSingleResult> = new Map<string, LoadFileSingleResult>();

let editor: CodeMirror.Editor;

interface LoadFileSingleResult {
    name: string
    orig_content: string
    filetype: string
    new_content?: string
}

interface IdeWorkspace {
    filesNum: number
    files: LoadFileSingleResult[]
}

function onLoadFileSuccess(result: LoadFileSingleResult[]): void {
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
            editor = initEditor(firstFile.filetype, 'myTextEditor');
        });
}

function changeEditorContent(event: Event): void {
    // save current editor content for activeFile!
    files.get(activeFile).new_content = editor.getValue();

    // TODO: mark current file btn as changed?

    // Get name and content of next file
    const clickedBtn: JQuery<HTMLButtonElement> = $(event.target as HTMLButtonElement);
    activeFile = clickedBtn.data('filename') as string;
    const nextFile: LoadFileSingleResult = files.get(activeFile);

    // Update editor content and mode (language!)
    editor.setValue(nextFile.new_content ? nextFile.new_content : nextFile.orig_content);
    editor.setOption('mode', nextFile.filetype);

    // Update buttons
    fileBtns.removeClass('btn-primary').addClass('btn-outline-secondary');
    clickedBtn.removeClass('btn-outline-secondary').addClass('btn-primary');
}

function uploadFiles(event: Event): void {
    // TODO: implement!
    console.warn('TODO: Upload!');

    const url = $(event.target).data('href');
    console.info(url);


    const fileValues: IdeWorkspace = {
        files: [...files.values()],
        filesNum: files.size
    };

    console.warn(fileValues);

    $.ajax({
        method: 'PUT',
        url,
        data: JSON.stringify(fileValues),
        dataType: 'json',
        contentType: 'application/json',
        beforeSend: (xhr) => {
            const token = $('input[name="csrfToken"]').val() as string;
            xhr.setRequestHeader('Csrf-Token', token);
        },
        error: (jqXHR) => {
            console.error(jqXHR.responseText);
        }
    });
}

domReady(() => {
    activeFile = $('.btn-primary').data('filename');

    fileBtns = $('.fileBtn');

    fileBtns.each((_: number, el: HTMLElement) => {
        filenames.push($(el).data('filename'));
    });

    fileBtns.on('click', changeEditorContent);

    $.ajax({
        method: 'POST',
        url: 'http://localhost:9000/ideFiles',
        data: JSON.stringify(filenames),
        beforeSend: (xhr) => {
            const token = $('input[name="csrfToken"]').val() as string;
            xhr.setRequestHeader('Csrf-Token', token);
        },
        error: jqXHR => {
            console.error(jqXHR)
        },
        success: onLoadFileSuccess
    });

    uploadBtn = $('#uploadBtn');
    uploadBtn.on('click', uploadFiles);
});
