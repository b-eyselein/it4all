import {useState} from 'react';
import {SqlExecutionResultFragment, SqlExerciseContentFragment, useSqlCorrectionMutation} from '../../../graphql';
import {useTranslation} from 'react-i18next';
import CodeMirror from '@uiw/react-codemirror';
import {NewTabs} from '../../../helpers/BulmaTabs';
import {SqlTableContents} from './SqlTableContents';
import {SqlCorrection} from './SqlCorrection';
import {StringSampleSolution} from '../StringSampleSolution';
import {SqlExecutionResultDisplay} from './SqlExecutionResultDisplay';
import {ConcreteExerciseWithoutPartsProps} from '../../Exercise';
import {SampleSolutionTabContent} from '../../SampleSolutionTabContent';
import {ExerciseControlButtons} from '../../../helpers/ExerciseControlButtons';
import {database} from '../../DexieTable';
import {sql} from '@codemirror/lang-sql';
import './SqlExercise.css';

type IProps = ConcreteExerciseWithoutPartsProps<SqlExerciseContentFragment, string>;

export function SqlExercise({exercise, content, oldSolution}: IProps): JSX.Element {

  const {t} = useTranslation('common');
  const [solution, setSolution] = useState('');
  const [activeTabId, setActiveTabId] = useState('databaseContent');
  const [correctExercise, correctionMutationResult] = useSqlCorrectionMutation();

  const correcting = correctionMutationResult.called && correctionMutationResult.loading;

  const executionResult: SqlExecutionResultFragment | undefined = correctionMutationResult.called && !!correctionMutationResult.data
    ? correctionMutationResult.data.sqlExercise?.correct.result.executionResult
    : undefined;

  function correct(): void {
    database.upsertSolutionWithoutParts(exercise.toolId, exercise.collectionId, exercise.exerciseId, solution);

    correctExercise({variables: {collectionId: exercise.collectionId, exerciseId: exercise.exerciseId, solution}})
      .then(() => setActiveTabId('correction'))
      .catch((error) => console.error(error));
  }

  return (
    <div className="p-4">

      <div className="grid grid-cols-5 gap-2">

        <div className="col-span-2">
          <h1 className="mb-4 font-bold text-xl text-center">{t('exerciseText')}</h1>
          <div className="p-4 rounded bg-slate-200">{exercise.text}</div>

          <h1 className="my-4 font-bold text-xl text-center">{t('query')}</h1>

          <CodeMirror value={oldSolution || solution} height={'200px'} onChange={setSolution} extensions={[sql()]}/>

          <ExerciseControlButtons isCorrecting={correcting} correct={correct} endLink={`./../../${exercise.exerciseId}`}/>
        </div>

        <div className="col-span-3">
          <NewTabs activeTabId={activeTabId} setActiveTabId={setActiveTabId}>
            {{
              databaseContent: {
                name: t('databaseContent'),
                render: <SqlTableContents tables={content.sqlDbContents}/>
              },
              correction: {
                name: t('correction'),
                render: <SqlCorrection mutationResult={correctionMutationResult}/>
              },
              sampleSolution: {
                name: t('sampleSolution_plural'),
                render: (
                  <SampleSolutionTabContent>
                    {() => content.sqlSampleSolutions.map((sample) => <StringSampleSolution sample={sample} key={sample}/>)}
                  </SampleSolutionTabContent>
                )
              }
            }}
          </NewTabs>
        </div>

      </div>

      {executionResult && <SqlExecutionResultDisplay queryResult={executionResult}/>}

    </div>
  );
}
