import React, {useState} from 'react';
import './SqlExercise.sass';
import {SqlExecutionResultFragment, SqlExerciseContentFragment, useSqlCorrectionMutation} from '../../../graphql';
import {useTranslation} from 'react-i18next';
import CodeMirror from '@uiw/react-codemirror';
import {getDefaultCodeMirrorEditorOptions} from '../codeMirrorOptions';
import {BulmaTabs, Tabs} from '../../../helpers/BulmaTabs';
import {SqlTableContents} from './SqlTableContents';
import {SqlCorrection} from './SqlCorrection';
import {StringSampleSolution} from '../StringSampleSolution';
import {SqlExecutionResultDisplay} from './SqlExecutionResultDisplay';
import {ConcreteExerciseIProps} from '../../Exercise';
import {SampleSolutionTabContent} from '../../SampleSolutionTabContent';
import {ExerciseControlButtons} from '../../../helpers/ExerciseControlButtons';

type IProps = ConcreteExerciseIProps<SqlExerciseContentFragment, string>;

export function SqlExercise({exercise, content, partId, oldSolution}: IProps): JSX.Element {

  const {t} = useTranslation('common');
  const [solution, setSolution] = useState('');
  const [correctExercise, correctionMutationResult] = useSqlCorrectionMutation();

  const correcting = correctionMutationResult.called && correctionMutationResult.loading;

  const executionResult: SqlExecutionResultFragment | undefined = correctionMutationResult.called && !!correctionMutationResult.data
    ? correctionMutationResult.data.sqlExercise?.correct.result.executionResult
    : undefined;

  if (!content.sqlPart) {
    throw new Error('TODO!');
  }

  const part = content.sqlPart;

  function correct(): void {
    correctExercise({variables: {collectionId: exercise.collectionId, exerciseId: exercise.exerciseId, part, solution}})
      .then(() => setActiveTabId('correction'))
      .catch((error) => console.error(error));
  }

  const databaseContentRender = () => <SqlTableContents tables={content.sqlDbContents}/>;
  databaseContentRender.displayName = 'SqlDatabaseContentRender';

  const correctionTabRender = () => <SqlCorrection mutationResult={correctionMutationResult}/>;
  correctionTabRender.display = 'SqlCorrectionTabRender';

  const sampleSolutionTabRender = () => <SampleSolutionTabContent>
    {() => content.sqlSampleSolutions.map((sample) =>
      <StringSampleSolution sample={sample} key={sample}/>
    )}
  </SampleSolutionTabContent>;
  sampleSolutionTabRender.displayName = 'SqlSampleSolutionTabRender';


  const tabs: Tabs = {
    databaseContent: {name: t('databaseContent'), render: databaseContentRender},
    correction: {name: t('correction'), render: correctionTabRender},
    sampleSolution: {name: t('sampleSolution_plural'), render: sampleSolutionTabRender}
  };

  const [activeTabId, setActiveTabId] = useState<keyof Tabs>(Object.keys(tabs)[0]);

  return (
    <div className="container is-fluid">

      <div className="columns">
        <div className="column is-two-fifths-desktop">
          <h1 className="title is-3 has-text-centered">{t('exerciseText')}</h1>
          <div className="notification is-light-grey">{exercise.text}</div>

          <h1 className="title is-4 has-text-centered">{t('query')}</h1>

          <CodeMirror value={solution} height={'200px'} options={getDefaultCodeMirrorEditorOptions('sql')} onChange={(ed) => setSolution(ed.getValue())}/>

          <ExerciseControlButtons isCorrecting={correcting} correct={correct} endLink={`./../../${exercise.exerciseId}`}/>
        </div>

        <div className="column">
          <BulmaTabs tabs={tabs} activeTabId={activeTabId} setActiveTabId={setActiveTabId}/>
        </div>
      </div>

      {executionResult && <SqlExecutionResultDisplay queryResult={executionResult}/>}

    </div>
  );
}
