import * as $ from 'jquery';
import * as CodeMirror from 'codemirror';
import {initEditor} from '../editorHelpers';

let filenames: string[] = [];
let activeFile: string;

let fileBtns: JQuery;

let uploadBtn: JQuery<HTMLButtonElement>;

let files: Map<string, LoadFileSingleResult> = new Map<string, LoadFileSingleResult>();

export let editor: CodeMirror.Editor;

interface LoadFileSingleResult {
    path: string
    content: string
    fileType: string
    editable: boolean
}

interface IdeWorkspace {
    filesNum: number
    files: LoadFileSingleResult[]
}

function onLoadFileSuccess(result: LoadFileSingleResult[]): void {
    console.warn(JSON.stringify(result, null, 2));

    // Fill file map
    for (const res of result) {
        files.set(res.path, res);
    }

    activeFile = result[0].path;

    // Read all CodeMirror modes from result array
    const allModes: string[] = Array.from(new Set(result.map(r => r.fileType)));

    // Load all file modes
    Promise.all(allModes.map(mode => import(`codemirror/mode/${mode}/${mode}`)))
        .then(() => {
            console.warn('Initing editor with ' + activeFile);
            // Init editor (all modes have already been loaded!)
            const firstFile: LoadFileSingleResult | undefined = files.get(activeFile);
            // FIXME: get => null!
            editor = initEditor(firstFile.fileType, 'myTextEditor');

            insertContentIntoEditor(firstFile);
        });
}

function insertContentIntoEditor(nextFile: LoadFileSingleResult) {
// Update editor content and mode (language!)
    editor.setValue(nextFile.content);

    editor.setOption('mode', nextFile.fileType);
    editor.setOption('readOnly', !nextFile.editable);
}

function saveEditorContent(): void {
    // save current editor content for activeFile!
    files.get(activeFile).content = editor.getValue();
}

function changeEditorContent(event: Event): void {
    saveEditorContent();

    // TODO: mark current file btn as changed?

    // Get name and content of next file
    const clickedBtn: JQuery<HTMLButtonElement> = $(event.target as HTMLButtonElement);
    activeFile = clickedBtn.data('filename') as string;

    const nextFile: LoadFileSingleResult = files.get(activeFile);

    insertContentIntoEditor(nextFile);

    // Update buttons
    fileBtns.removeClass('btn-primary').addClass('btn-outline-secondary');
    clickedBtn.removeClass('btn-outline-secondary').addClass('btn-primary');
}


export function uploadFiles<ResultType>(testButton: HTMLButtonElement, success: (ResultType) => void, error): void {
    // Save current changes
    saveEditorContent();

    // TODO: implement!

    const url = testButton.dataset['href'];

    const fileValues: IdeWorkspace = {
        files: [...files.values()],
        filesNum: files.size
    };

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
        success,
        error
    });
}

$(() => {
    activeFile = $('.btn-primary').data('filename');

    fileBtns = $('.fileBtn');

    fileBtns.each((_: number, el: HTMLElement) => {
        filenames.push(el.dataset['filename']);
    });

    fileBtns.on('click', changeEditorContent);

    const loadFilesUrl: string = document.getElementById('theContainer').dataset['loadfilesurl']; // 'http://localhost:9000/ideFiles'

    $.ajax({
        url: loadFilesUrl,
        error: jqXHR => {
            console.error(jqXHR)
        },
        success: onLoadFileSuccess
    });

});
