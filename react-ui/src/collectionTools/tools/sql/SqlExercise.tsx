import React, {useState} from "react";
import './SqlExercise.sass';
import {ExerciseSolveFieldsFragment, SqlExecutionResultFragment, SqlExerciseContentFragment, useSqlCorrectionMutation} from "../../../generated/graphql";
import classNames from "classnames";
import {useTranslation} from "react-i18next";
import CodeMirror from '@uiw/react-codemirror';
import {getDefaultCodeMirrorEditorOptions} from "../codeMirrorOptions";
import {BulmaTabs, TabConfig} from "../../../helpers/BulmaTabs";
import {SqlTableContents} from "./SqlTableContents";
import {SqlCorrection} from './SqlCorrection';
import {StringSampleSolution} from "../StringSampleSolution";
import {SqlExecutionResultDisplay} from './SqlExecutionResultDisplay';
import {Link} from "react-router-dom";

interface IProps {
  exerciseFragment: ExerciseSolveFieldsFragment;
  contentFragment: SqlExerciseContentFragment;
}

export function SqlExercise({exerciseFragment, contentFragment}: IProps): JSX.Element {

  const {t} = useTranslation('common');
  const [solution, setSolution] = useState('');
  const [correctExercise, correctionMutationResult] = useSqlCorrectionMutation();

  const correcting = correctionMutationResult.called && correctionMutationResult.loading;

  const executionResult: SqlExecutionResultFragment | undefined = correctionMutationResult.called && !!correctionMutationResult.data
    ? correctionMutationResult.data.me?.sqlExercise?.correct.result.executionResult
    : undefined;

  function correct(): void {
    correctExercise({
      variables: {
        collectionId: exerciseFragment.collectionId,
        exerciseId: exerciseFragment.exerciseId,
        part: contentFragment.sqlPart!!,
        solution
      }
    }).catch((error) => console.error(error));
  }

  const tabConfigs: TabConfig[] = [
    {id: 'databaseContent', name: t('databaseContent'), render: () => <SqlTableContents tables={contentFragment.sqlDbContents}/>},
    {id: 'correction', name: t('correction'), render: () => <SqlCorrection mutationResult={correctionMutationResult}/>},
    {
      id: 'sampleSolution', name: t('sampleSolution_plural'), render: () => <>
        {contentFragment.sqlSampleSolutions.map((sample) =>
          <StringSampleSolution sample={sample} key={sample}/>
        )}
      </>
    }
  ]

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
              {/* routerLink="../.." */}
              <Link to={`./../../${exerciseFragment.exerciseId}`} className="button is-dark is-fullwidth">Bearbeiten beenden</Link>
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
