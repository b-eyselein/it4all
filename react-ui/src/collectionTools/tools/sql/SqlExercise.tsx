import {useState} from 'react';
import './SqlExercise.sass';
import {SqlExecutionResultFragment, SqlExerciseContentFragment, SqlExPart, useSqlCorrectionMutation} from '../../../graphql';
import {useTranslation} from 'react-i18next';
import CodeMirror, {Compartment} from '@uiw/react-codemirror';
import {BulmaTabs, Tabs} from '../../../helpers/BulmaTabs';
import {SqlTableContents} from './SqlTableContents';
import {SqlCorrection} from './SqlCorrection';
import {StringSampleSolution} from '../StringSampleSolution';
import {SqlExecutionResultDisplay} from './SqlExecutionResultDisplay';
import {ConcreteExerciseIProps} from '../../Exercise';
import {SampleSolutionTabContent} from '../../SampleSolutionTabContent';
import {ExerciseControlButtons} from '../../../helpers/ExerciseControlButtons';
import {database} from '../../DexieTable';
import {sql} from '@codemirror/lang-sql';

type IProps = ConcreteExerciseIProps<SqlExerciseContentFragment, string>;

export function SqlExercise({exercise, content, partId, oldSolution}: IProps): JSX.Element {

  const {t} = useTranslation('common');
  const [solution, setSolution] = useState('');
  const [correctExercise, correctionMutationResult] = useSqlCorrectionMutation();

  const correcting = correctionMutationResult.called && correctionMutationResult.loading;

  const executionResult: SqlExecutionResultFragment | undefined = correctionMutationResult.called && !!correctionMutationResult.data
    ? correctionMutationResult.data.sqlExercise?.correct.result.executionResult
    : undefined;

  const part = SqlExPart.SqlSingleExPart;

  function correct(): void {
    database.upsertSolution(exercise.toolId, exercise.collectionId, exercise.exerciseId, partId, solution);

    correctExercise({variables: {collectionId: exercise.collectionId, exerciseId: exercise.exerciseId, part, solution}})
      .then(() => setActiveTabId('correction'))
      .catch((error) => console.error(error));
  }

  const tabs: Tabs = {
    databaseContent: {name: t('databaseContent'), render: <SqlTableContents tables={content.sqlDbContents}/>},
    correction: {name: t('correction'), render: <SqlCorrection mutationResult={correctionMutationResult}/>},
    sampleSolution: {
      name: t('sampleSolution_plural'),
      render: <SampleSolutionTabContent>
        {() => content.sqlSampleSolutions.map((sample) =>
          <StringSampleSolution sample={sample} key={sample}/>
        )}
      </SampleSolutionTabContent>
    }
  };

  const [activeTabId, setActiveTabId] = useState<keyof Tabs>(Object.keys(tabs)[0]);

  return (
    <div className="container is-fluid">

      <div className="columns">
        <div className="column is-two-fifths-desktop">
          <h1 className="title is-3 has-text-centered">{t('exerciseText')}</h1>
          <div className="notification is-light-grey">{exercise.text}</div>

          <h1 className="title is-4 has-text-centered">{t('query')}</h1>

          {/* options={getDefaultCodeMirrorEditorOptions('sql')} */}
          <CodeMirror value={oldSolution ? oldSolution : solution} height={'200px'} onChange={(ed) => setSolution(ed)} extensions={[(new Compartment).of(sql())]}/>

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
