import { useEffect, useState } from 'react';
import { ExerciseSolveFieldsFragment, ExerciseSolveFieldsToolFragment, FilesSolutionInput, useExerciseQuery, XmlSolutionInput } from '../graphql';
import { WithQuery } from '../WithQuery';
import { RegexExercise } from './tools/regex/RegexExercise';
import { SqlExercise } from './tools/sql/SqlExercise';
import { ProgrammingExercise } from './tools/programming/ProgrammingExercise';
import { WebExercise } from './tools/web/WebExercise';
import { XmlExercise } from './tools/xml/XmlExercise';
import { FlaskExercise } from './tools/flask/FlaskExercise';
import { database } from './DexieTable';
import { useTranslation } from 'react-i18next';
import { UmlExercise } from './tools/uml/UmlExercise';
import { neverRender } from '../helpers';
import { UmlDbClassDiagram } from './tools/uml/UmlDiagramDrawing';
import { Navigate } from 'react-router-dom';
import { homeUrl } from '../urls';

export interface ConcreteExerciseWithoutPartsProps<T, S> {
  exercise: ExerciseSolveFieldsFragment;
  content: T;
  oldSolution?: S;
}

export interface ConcreteExerciseWithPartsProps<T, S> extends ConcreteExerciseWithoutPartsProps<T, S> {
  partId: string;
}

interface IState<S> {
  oldSolutionLoaded: boolean;
  oldSolution?: S;
}

interface IProps {
  toolId: string;
  collectionId: number;
  exerciseId: number;
  partId?: string;
}

function Inner<SolutionType>({ toolId, collectionId, exerciseId, partId, tool }: IProps & { tool: ExerciseSolveFieldsToolFragment }): JSX.Element {

  const { t } = useTranslation('common');
  const [state, setState] = useState<IState<SolutionType>>({ oldSolutionLoaded: false });

  if (!tool.collection.exercise) {
    return <Navigate to={homeUrl} />;
  }

  useEffect(() => {
    const solutionPromise = partId
      ? database.getSolutionWithParts<SolutionType>([toolId, collectionId, exerciseId, partId])
      : database.getSolutionWithoutParts<SolutionType>([toolId, collectionId, exerciseId]);

    solutionPromise
      .then((oldSolution) => setState({ oldSolutionLoaded: true, oldSolution: oldSolution?.solution }))
      .catch(() => setState({ oldSolutionLoaded: true }));
  }, [partId]);

  const exercise = tool.collection.exercise;

  const content = exercise.content;

  if (!state.oldSolutionLoaded) {
    return <div className="notification is-primary has-text-centered">{t('loadingOldSolution')}...</div>;
  } else if (content.__typename === 'FlaskExerciseContent') {
    return <FlaskExercise exercise={exercise} content={content} oldSolution={state.oldSolution as FilesSolutionInput | undefined} />;
  } else if (content.__typename === 'ProgrammingExerciseContent') {
    return <ProgrammingExercise exercise={exercise} content={content} partId={partId as string}
      oldSolution={state.oldSolution as FilesSolutionInput | undefined} />;
  } else if (content.__typename === 'RegexExerciseContent') {
    return <RegexExercise exercise={exercise} content={content} oldSolution={state.oldSolution as string | undefined} />;
  } else if (content.__typename === 'SqlExerciseContent') {
    return <SqlExercise exercise={exercise} content={content} oldSolution={state.oldSolution as string | undefined} />;
  } else if (content.__typename === 'UmlExerciseContent') {
    return <UmlExercise exercise={exercise} content={content} partId={partId as string} oldSolution={state.oldSolution as UmlDbClassDiagram | undefined} />;
  } else if (content.__typename === 'WebExerciseContent') {
    return <WebExercise exercise={exercise} content={content} partId={partId as string} oldSolution={state.oldSolution as FilesSolutionInput | undefined} />;
  } else if (content.__typename === 'XmlExerciseContent') {
    return <XmlExercise exercise={exercise} content={content} partId={partId as string} oldSolution={state.oldSolution as XmlSolutionInput | undefined} />;
  } else {
    return neverRender(content);
  }
}

export function Exercise({ toolId, collectionId, exerciseId, partId }: IProps): JSX.Element {

  const exerciseQuery = useExerciseQuery({ variables: { toolId, collectionId, exerciseId } });

  return (
    <WithQuery query={exerciseQuery}>
      {({ tool }) =>
        tool
          ? <Inner toolId={toolId} collectionId={collectionId} exerciseId={exerciseId} partId={partId} tool={tool} />
          : <div>TODO!</div>}
    </WithQuery>
  );

}
