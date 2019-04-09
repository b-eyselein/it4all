import * as CodeMirror from 'codemirror';
import {initEditor} from '../editorHelpers';

let activeFile: string;
let filenames: string[] = [];

let fileChangeBtns: HTMLButtonElement[] = [];

let files: Map<string, LoadFileSingleResult> = new Map<string, LoadFileSingleResult>();

let editor: CodeMirror.Editor;


export interface LoadFileSingleResult {
    path: string
    content: string
    fileType: string
    editable: boolean
}

export interface IdeWorkspace {
    filesNum: number
    files: LoadFileSingleResult[]
}

function onLoadFileSuccess(result: LoadFileSingleResult[]): Promise<CodeMirror.Editor> {
    for (const res of result) {
        // Fill file map
        files.set(res.path, res);
    }

    activeFile = result[0].path;

    // Read all CodeMirror modes from result array
    const allDistinctModes: string[] = Array.from(new Set(result.map(r => r.fileType)));

    // Load all file modes
    return Promise.all(allDistinctModes.map(mode => import(`codemirror/mode/${mode}/${mode}`)))
        .then(() => {
            // Init editor (all modes have already been loaded!)
            const firstFile: LoadFileSingleResult | undefined = files.get(activeFile);
            // FIXME: get => null!
            editor = initEditor(firstFile.fileType, 'myTextEditor');

            insertContentIntoEditor(firstFile);

            return editor;
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
    const clickedBtn: HTMLButtonElement = event.target as HTMLButtonElement;
    activeFile = clickedBtn.dataset['filename'] as string;

    const nextFile: LoadFileSingleResult = files.get(activeFile);

    insertContentIntoEditor(nextFile);

    // Update buttons
    fileChangeBtns.forEach((fileChangeBtn) => {
        fileChangeBtn.classList.remove('btn-primary');
        fileChangeBtn.classList.add('btn-outline-secondary');
    });

    // fileBtns.removeClass('btn-primary').addClass('btn-outline-secondary');
    clickedBtn.classList.remove('btn-outline-secondary');
    clickedBtn.classList.add('btn-primary');
}

export function getIdeWorkspace(): IdeWorkspace {
    // Save current changes
    saveEditorContent();

    return {
        files: [...files.values()],
        filesNum: files.size
    };
}


export function uploadFiles<ResultType>(testButton: HTMLButtonElement, onSuccess: (ResultType) => void, onError): void {

    const url: string = testButton.dataset['href'];

    const fileValues: IdeWorkspace = getIdeWorkspace();

    const token: string = document.querySelector<HTMLInputElement>('input[name="csrfToken"]').value as string;

    const headers: Headers = new Headers({
        "Content-Type": "application/json",
        "Accept": "application/json",
        "Csrf-Token": token
    });

    fetch(url, {method: 'PUT', body: JSON.stringify(fileValues), headers})
        .then(response => response.json())
        .then(onSuccess)
        .catch(onError);
}

export function setupEditor(): Promise<CodeMirror.Editor> {
    fileChangeBtns = Array.from(document.querySelectorAll<HTMLButtonElement>('.fileBtn'));

    fileChangeBtns.forEach((fileChangeBtn: HTMLButtonElement) => {
        filenames.push(fileChangeBtn.dataset['filename']);
        fileChangeBtn.onclick = changeEditorContent;
    });

    const loadFilesUrl: string = document.getElementById('theContainer').dataset['loadfilesurl'];

    return fetch(loadFilesUrl)
        .then(response => response.json())
        .then(onLoadFileSuccess)
        .catch(reason => console.error(reason));
}

// $(() => setupEditor());
