import React, {useState} from 'react';
import './SqlExercise.sass';
import {SqlExecutionResultFragment, SqlExerciseContentFragment, useSqlCorrectionMutation} from '../../../graphql';
import classNames from 'classnames';
import {useTranslation} from 'react-i18next';
import CodeMirror from '@uiw/react-codemirror';
import {getDefaultCodeMirrorEditorOptions} from '../codeMirrorOptions';
import {BulmaTabs, Tabs} from '../../../helpers/BulmaTabs';
import {SqlTableContents} from './SqlTableContents';
import {SqlCorrection} from './SqlCorrection';
import {StringSampleSolution} from '../StringSampleSolution';
import {SqlExecutionResultDisplay} from './SqlExecutionResultDisplay';
import {Link} from 'react-router-dom';
import {ConcreteExerciseIProps} from '../../Exercise';
import {SampleSolutionTabContent} from '../../SampleSolutionTabContent';

type IProps = ConcreteExerciseIProps<SqlExerciseContentFragment>

export function SqlExercise({exerciseFragment, contentFragment, showSampleSolutions, toggleSampleSolutions}: IProps): JSX.Element {

  const {t} = useTranslation('common');
  const [solution, setSolution] = useState('');
  const [correctExercise, correctionMutationResult] = useSqlCorrectionMutation();

  const correcting = correctionMutationResult.called && correctionMutationResult.loading;

  const executionResult: SqlExecutionResultFragment | undefined = correctionMutationResult.called && !!correctionMutationResult.data
    ? correctionMutationResult.data.me?.sqlExercise?.correct.result.executionResult
    : undefined;

  if (!contentFragment.sqlPart) {
    throw new Error('TODO!');
  }

  const part = contentFragment.sqlPart;

  function correct(): void {
    correctExercise({
      variables: {
        collectionId: exerciseFragment.collectionId,
        exerciseId: exerciseFragment.exerciseId,
        part,
        solution
      }
    }).catch((error) => console.error(error));
  }

  const databaseContentRender = () => <SqlTableContents tables={contentFragment.sqlDbContents}/>;
  databaseContentRender.displayName = 'SqlDatabaseContentRender';

  const correctionTabRender = () => <SqlCorrection mutationResult={correctionMutationResult}/>;
  correctionTabRender.display = 'SqlCorrectionTabRender';

  const sampleSolutionTabRender = () =>
    <SampleSolutionTabContent showSampleSolutions={showSampleSolutions} toggleSampleSolutions={toggleSampleSolutions}
                              renderSampleSolutions={() => contentFragment.sqlSampleSolutions.map((sample) =>
                                <StringSampleSolution sample={sample} key={sample}/>
                              )}/>;
  sampleSolutionTabRender.displayName = 'SqlSampleSolutionTabRender';

  const tabConfigs: Tabs = {
    databaseContent: {name: t('databaseContent'), render: databaseContentRender},
    correction: {name: t('correction'), render: correctionTabRender},
    sampleSolution: {name: t('sampleSolution_plural'), render: sampleSolutionTabRender}
  };

  return (
    <div className="container is-fluid">

      <div className="columns">
        <div className="column is-two-fifths-desktop">
          <h1 className="title is-3 has-text-centered">{t('exerciseText')}</h1>
          <div className="notification is-light-grey">{exerciseFragment.text}</div>

          <h1 className="title is-4 has-text-centered">Anfrage</h1>

          <CodeMirror value={solution} height={'200px'} options={getDefaultCodeMirrorEditorOptions('sql')}
                      onChange={(editor) => setSolution(editor.getValue())}/>

          <div className="columns my-3">
            <div className="column">
              <button className={classNames('button', 'is-link', 'is-fullwidth', {'is-loading': correcting})} onClick={correct}>
                {t('correct')}
              </button>
            </div>
            <div className="column">
              <Link to={`./../../${exerciseFragment.exerciseId}`} className="button is-dark is-fullwidth">{t('endSolve')}</Link>
            </div>
          </div>
        </div>

        <div className="column">
          <BulmaTabs tabs={tabConfigs}/>
        </div>
      </div>

      {executionResult && <SqlExecutionResultDisplay queryResult={executionResult}/>}

    </div>
  );
}
