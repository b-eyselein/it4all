import React from 'react';
import classNames from 'classnames';
import {ExerciseFileFragment} from '../graphql';
import CodeMirror from '@uiw/react-codemirror';
import {getDefaultCodeMirrorEditorOptions} from '../collectionTools/tools/codeMirrorOptions';

interface IProps {
  files: Workspace;
  activeFileName: keyof Workspace;
  setActiveFile: (filename: string) => void;
  updateActiveFileContent: (content: string) => void;
}

export interface Workspace {
  [name: string]: ExerciseFileFragment;
}

export function workspace(files: ExerciseFileFragment[]): Workspace {
  return Object.fromEntries(
    files.map((file) => [file.name, file])
  );
}

export function ExerciseFilesEditor({files, activeFileName, setActiveFile, updateActiveFileContent}: IProps): JSX.Element {

  const activeFile: ExerciseFileFragment = files[activeFileName];

  return (
    <>
      <div className="tabs is-centered">
        <ul>
          {Object.values(files).map((file) =>
            <li key={file.name} onClick={() => setActiveFile(file.name)} className={classNames({'is-active': file.name === activeFileName})}>
              <a className={classNames({'has-text-grey-light': file.editable})} title={file.editable ? '' : 'Nicht editierbar'}>{file.name}</a>
            </li>
          )}
        </ul>
      </div>
      <CodeMirror value={activeFile.content} height={'700px'} options={getDefaultCodeMirrorEditorOptions(activeFile.fileType)}
                  onChange={(ed) => updateActiveFileContent(ed.getValue())}/>
    </>
  );

}
