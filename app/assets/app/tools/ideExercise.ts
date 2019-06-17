import * as CodeMirror from 'codemirror';
import {initEditor} from '../editorHelpers';


export interface ExerciseFile {
    name: string
    content: string
    fileType: string
    editable: boolean
}

export interface IdeWorkspace {
    filesNum: number
    files: ExerciseFile[]
}

interface LoadFilesMessage {
    files: ExerciseFile[];
    activeFileName: undefined | string;
}


let activeFile: string;
let filenames: string[] = [];

let fileChangeBtns: HTMLButtonElement[] = [];

let files: Map<string, ExerciseFile> = new Map<string, ExerciseFile>();

let editor: CodeMirror.Editor;


function onLoadFileSuccess(result: LoadFilesMessage): Promise<CodeMirror.Editor> {
    // console.info(JSON.stringify(result, null, 2));

    for (const res of result.files) {
        // Fill file map
        files.set(res.name, res);
    }

    if (result.activeFileName) {
        activeFile = result.activeFileName;
    } else {
        activeFile = result.files[0].name;
    }

    // Read all CodeMirror modes from result array
    const allDistinctModes: string[] = Array.from(new Set(result.files.map(r => r.fileType)));

    // Load all file modes
    return Promise.all(allDistinctModes.map(mode => import(`codemirror/mode/${mode}/${mode}`)))
        .then(() => {
            // Init editor (all modes have already been loaded!)
            const firstFile: ExerciseFile | undefined = files.get(activeFile);

            // FIXME: get => null!
            editor = initEditor(firstFile.fileType, 'myTextEditor');

            insertContentIntoEditor(firstFile);

            // Update buttons
            fileChangeBtns.forEach((fileChangeBtn) => {
                fileChangeBtn.classList.remove('btn-primary');
                fileChangeBtn.classList.add('btn-outline-secondary');
            });

            fileChangeBtns[0].classList.remove('btn-outline-secondary');
            fileChangeBtns[0].classList.add('btn-primary');

            if (result.activeFileName) {
                document.querySelector<HTMLButtonElement>(`button[data-filename="${result.activeFileName}"]`).click();
            }

            return editor;
        });
}

function insertContentIntoEditor(nextFile: ExerciseFile) {
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

    const nextFile: ExerciseFile = files.get(activeFile);

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

export function setupEditor(): Promise<void | CodeMirror.Editor> {
    fileChangeBtns = Array.from(document.querySelectorAll<HTMLButtonElement>('.fileBtn'));

    fileChangeBtns.forEach((fileChangeBtn: HTMLButtonElement) => {
        filenames.push(fileChangeBtn.dataset['filename']);
        fileChangeBtn.onclick = changeEditorContent;
    });

    const loadFilesUrl: string = document.getElementById('theContainer').dataset['loadfilesurl'];

    return fetch(loadFilesUrl)
        .then(response => {
            if (response.status === 200) {
                return response.json();
            } else {
                console.error('There has been an error loading files: ' + response.statusText)
            }
        })
        .then(onLoadFileSuccess)
        .catch(reason => console.error(reason));

}
