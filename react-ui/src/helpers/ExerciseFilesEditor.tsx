import classNames from 'classnames';
import {ExerciseFileFragment} from '../graphql';
import CodeMirror, {Extension} from '@uiw/react-codemirror';
import {python} from '@codemirror/lang-python';
import {css} from '@codemirror/lang-css';
import {html} from '@codemirror/lang-html';
import {xml} from '@codemirror/lang-xml';
import {sql} from '@codemirror/lang-sql';
import {javascript} from '@codemirror/lang-javascript';

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

function getFileExtension(filename: string): string | undefined {
  const fileNameLastPointIndex = filename.lastIndexOf('.');

  if (fileNameLastPointIndex !== -1) {
    return filename.substring(fileNameLastPointIndex + 1);
  }
}

function getExtensionForFileExtension(fileExtension: string): Extension | undefined {
  switch (fileExtension) {
    case 'py':
      return python();
    case 'css':
      return css();
    case 'html':
      return html();
    case 'xml':
      return xml();
    case 'sql':
      return sql();
    case 'js':
      return javascript();
    default:
      return undefined;
  }
}

export function ExerciseFilesEditor({files, activeFileName, setActiveFile, updateActiveFileContent}: IProps): JSX.Element {

  const activeFile: ExerciseFileFragment = files[activeFileName];

  const fileExtension = getFileExtension(activeFile.name);

  const newExtension = fileExtension
    ? getExtensionForFileExtension(fileExtension)
    : undefined;

  const extensions = newExtension ? [newExtension] : [];

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

      {/* options={getDefaultCodeMirrorEditorOptions(mode)} */}
      <CodeMirror value={activeFile.content} height={'700px'} onChange={(ed) => updateActiveFileContent(ed)} extensions={extensions}/>
    </>
  );

}
