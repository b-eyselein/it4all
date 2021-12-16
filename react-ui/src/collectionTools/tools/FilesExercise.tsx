import {useState} from 'react';
import {ExerciseFilesEditor, Workspace, workspace} from '../../helpers/ExerciseFilesEditor';
import {ExerciseControlButtons} from '../../helpers/ExerciseControlButtons';
import {SampleSolutionTabContent} from '../SampleSolutionTabContent';
import {BulmaTabs, Tabs} from '../../helpers/BulmaTabs';
import {ExerciseFileFragment, ExerciseFileInput, FilesSolution} from '../../graphql';
import {useTranslation} from 'react-i18next';
import {ExerciseFileCard} from './ExerciseFileCard';
import update from 'immutability-helper';

interface IProps {
  exerciseDescription: JSX.Element;
  initialFiles: ExerciseFileFragment[];
  sampleSolutions: FilesSolution[],
  correctionTabRender: JSX.Element;
  correct: (files: ExerciseFileFragment[], onCorrected: () => void) => void;
  isCorrecting: boolean;
}

interface IState {
  workspace: Workspace;
  activeFile: keyof Workspace;
}

export function updateFileContents(oldSolution: ExerciseFileInput[], defaultSolution: ExerciseFileInput[]): ExerciseFileInput[] {
  return defaultSolution.map((defaultSolutionFile) => {

    const oldSolutionFile = oldSolution.find(({name}) => defaultSolutionFile.name === name);

    return oldSolutionFile
      ? {...defaultSolutionFile, content: oldSolutionFile.content}
      : defaultSolutionFile;
  });
}

export function FilesExercise({exerciseDescription, initialFiles, sampleSolutions, correctionTabRender, correct, isCorrecting,}: IProps): JSX.Element {

  const {t} = useTranslation('common');
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

  const exerciseDescriptionTabRender = <>
    <div className="notification is-light-grey">{exerciseDescription}</div>

    <ExerciseControlButtons isCorrecting={isCorrecting} correct={onCorrect} endLink={'./../..'}/>
  </>;

  const sampleSolutionTabRender = <SampleSolutionTabContent>
    {() => sampleSolutions.map(({files}, index) => <div className="mb-3" key={index}>
      {files.map((file) => <ExerciseFileCard exerciseFile={file} key={file.name}/>)}
    </div>)}
  </SampleSolutionTabContent>;

  const tabs: Tabs = {
    exerciseText: {name: t('exerciseDescription'), render: exerciseDescriptionTabRender},
    correction: {name: t('correction'), render: correctionTabRender},
    sampleSolutions: {name: t('sampleSolution_plural'), render: sampleSolutionTabRender}
  };

  const [activeTabId, setActiveTabId] = useState<keyof Tabs>(Object.keys(tabs)[0]);

  return (
    <div className="container is-fluid">
      <div className="columns">
        <div className="column is-half-desktop">
          <ExerciseFilesEditor files={state.workspace} activeFileName={state.activeFile}
                               setActiveFile={(activeFile) => setState(({workspace}) => ({workspace, activeFile}))}
                               updateActiveFileContent={updateActiveFileContent}/>
        </div>

        <div className="column is-half-desktop">
          <BulmaTabs tabs={tabs} activeTabId={activeTabId} setActiveTabId={setActiveTabId}/>
        </div>
      </div>
    </div>
  );
}
