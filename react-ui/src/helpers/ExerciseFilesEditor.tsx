import {ExerciseFileFragment} from '../graphql';
import CodeMirror from '@uiw/react-codemirror';
import {python} from '@codemirror/lang-python';
import {css} from '@codemirror/lang-css';
import {html} from '@codemirror/lang-html';
import {xml} from '@codemirror/lang-xml';
import {sql} from '@codemirror/lang-sql';
import {javascript} from '@codemirror/lang-javascript';
import {LanguageSupport} from '@codemirror/language';
import {TabPills} from './BulmaTabs';

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

function getExtensionForFileExtension(fileExtension: string): LanguageSupport[] {
  switch (fileExtension) {
    case 'py':
      return [python()];
    case 'css':
      return [css()];
    case 'html':
      return [html()];
    case 'xml':
      return [xml()];
    case 'sql':
      return [sql()];
    case 'js':
      return [javascript()];
    default:
      return [];
  }
}

export function ExerciseFilesEditor({files, activeFileName, setActiveFile, updateActiveFileContent}: IProps): JSX.Element {

  const activeFile: ExerciseFileFragment = files[activeFileName];
  const fileExtension = getFileExtension(activeFile.name);

  const extensions = fileExtension
    ? getExtensionForFileExtension(fileExtension)
    : [];

  const pills = Object.entries(files).map(([id, {name}]) => ({id, name}));

  return (
    <div>
      <TabPills pills={pills} activePillId={activeFile.name} onClick={setActiveFile}/>

      <CodeMirror style={{fontSize: '16px'}} value={activeFile.content} height={'700px'} onChange={(ed) => updateActiveFileContent(ed)}
                  extensions={extensions}/>
    </div>
  );

}
