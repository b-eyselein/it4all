import {useState} from 'react';
import {ExerciseFilesEditor, Workspace, workspace} from '../../helpers/ExerciseFilesEditor';
import {ExerciseControlButtons} from '../../helpers/ExerciseControlButtons';
import {SampleSolutionTabContent} from '../SampleSolutionTabContent';
import {NewTabs} from '../../helpers/BulmaTabs';
import {ExerciseFileFragment, FilesSolution} from '../../graphql';
import {useTranslation} from 'react-i18next';
import update from 'immutability-helper';
import {IExerciseFile, IFilesSolution} from '../exerciseFile';
import {NewCard} from '../../helpers/BulmaCard';

interface IProps {
  exerciseDescription: JSX.Element;
  defaultFiles: IExerciseFile[];
  oldSolution: IFilesSolution | undefined;
  sampleSolutions: FilesSolution[],
  correctionTabRender: JSX.Element;
  correct: (files: IExerciseFile[], onCorrected: () => void) => void;
  isCorrecting: boolean;
}

export function ExerciseFileCard({exerciseFile}: { exerciseFile: ExerciseFileFragment }): JSX.Element {
  return (
    <NewCard title={exerciseFile.name}>
      <pre>{exerciseFile.content}</pre>
    </NewCard>
  );
}

interface IState {
  workspace: Workspace;
  activeFile: keyof Workspace;
}

export function updateFileContents(oldSolution: IExerciseFile[], defaultSolution: IExerciseFile[]): IExerciseFile[] {
  return defaultSolution.map((defaultSolutionFile) => {

    const oldSolutionFile = oldSolution.find(({name}) => defaultSolutionFile.name === name);

    return oldSolutionFile
      ? {...defaultSolutionFile, content: oldSolutionFile.content}
      : defaultSolutionFile;
  });
}

export function FilesExercise({
  exerciseDescription,
  defaultFiles,
  oldSolution,
  sampleSolutions,
  correctionTabRender,
  correct,
  isCorrecting
}: IProps): JSX.Element {

  const initialFiles = oldSolution
    ? updateFileContents(oldSolution.files, defaultFiles)
    : defaultFiles;

  const {t} = useTranslation('common');
  const [activeTabId, setActiveTabId] = useState('exerciseText');
  const [state, setState] = useState<IState>({
    workspace: workspace(initialFiles),
    activeFile: initialFiles[0].name
  });

  function updateActiveFileContent(content: string): void {
    setState((state) => update(state, {
      workspace: {[state.activeFile]: {content: {$set: content}}}
    }));
  }

  function onCorrect(): void {
    correct(Object.values(state.workspace), () => setActiveTabId('correction'));
  }

  return (
    <div className="p-4 grid grid-cols-2 gap-2">
      <ExerciseFilesEditor files={state.workspace} activeFileName={state.activeFile}
                           setActiveFile={(activeFile) => setState(({workspace}) => ({workspace, activeFile}))}
                           updateActiveFileContent={updateActiveFileContent}/>

      <NewTabs activeTabId={activeTabId} setActiveTabId={setActiveTabId}>
        {{
          exerciseText: {
            name: t('exerciseDescription'),
            render: () => (
              <>
                <div className="my-2 p-2 rounded bg-gray-200">{exerciseDescription}</div>

                <ExerciseControlButtons isCorrecting={isCorrecting} correct={onCorrect} endLink={'./../..'}/>
              </>
            )
          },
          correction: {name: t('correction'), render: correctionTabRender/*, disabled: !corrected*/},
          sampleSolutions: {
            name: t('sampleSolution_plural'),
            render: () => (
              <SampleSolutionTabContent>
                {() => sampleSolutions.map(({files}, index) => <div className="mb-3" key={index}>
                  {files.map((file) => <ExerciseFileCard exerciseFile={file} key={file.name}/>)}
                </div>)}
              </SampleSolutionTabContent>
            )
          }
        }}
      </NewTabs>
    </div>
  );
}
