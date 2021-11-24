import { useEffect, useState } from 'react';
import {ExerciseIProps} from '../ToolBase';
import {useParams} from 'react-router-dom';
import {ExerciseSolveFieldsFragment, FilesSolutionInput, useExerciseQuery, XmlSolutionInput} from '../graphql';
import {WithQuery} from '../WithQuery';
import {EbnfExercise} from './tools/ebnf/EbnfExercise';
import {RegexExercise} from './tools/regex/RegexExercise';
import {SqlExercise} from './tools/sql/SqlExercise';
import {ProgrammingExercise} from './tools/programming/ProgrammingExercise';
import {WebExercise} from './tools/web/WebExercise';
import {XmlExercise} from './tools/xml/XmlExercise';
import {FlaskExercise} from './tools/flask/FlaskExercise';
import {database} from './DexieTable';
import {useTranslation} from 'react-i18next';
import {UmlExercise} from './tools/uml/UmlExercise';
import {neverRender} from '../helpers';
import {UmlDbClassDiagram} from './tools/uml/UmlDiagramDrawing';
import {WithNullableNavigate} from '../WithNullableNavigate';

export interface ConcreteExerciseIProps<T, S> {
  exercise: ExerciseSolveFieldsFragment;
  content: T;
  partId: string;
  oldSolution?: S;
}

interface IState<S> {
  oldSolutionLoaded: boolean;
  oldSolution?: S;
}

export function Exercise<SolutionType>({toolId, collectionId, exerciseId}: ExerciseIProps): JSX.Element {

  const {t} = useTranslation('common');
  const partId = useParams<'partId'>().partId!;
  const [state, setState] = useState<IState<SolutionType>>({oldSolutionLoaded: false});
  const exerciseQuery = useExerciseQuery({variables: {toolId, collectionId, exerciseId, partId}});

  useEffect(() => {
    database.getSolution<SolutionType>(toolId, collectionId, exerciseId, partId)
      .then((oldSolution) => setState({oldSolutionLoaded: true, oldSolution: oldSolution?.solution}))
      .catch((/*error*/) => setState({oldSolutionLoaded: true}));
  }, [partId]);

  return (
    <WithQuery query={exerciseQuery}>
      {({tool: maybeTool}) => <WithNullableNavigate t={maybeTool}>
        {({collection}) => <WithNullableNavigate t={collection}>
          {({exercise: maybeExercise}) => <WithNullableNavigate t={maybeExercise}>
            {(exercise) => {

              const content = exercise.content;

              if (!state.oldSolutionLoaded) {
                return <div className="notification is-primary has-text-centered">{t('loadingOldSolution')}...</div>;
              }

              if (content.__typename === 'EbnfExerciseContent') {
                return <EbnfExercise exercise={exercise} content={content} partId={partId} oldSolution={state.oldSolution as string | undefined}/>;
              } else if (content.__typename === 'FlaskExerciseContent') {
                return <FlaskExercise exercise={exercise} content={content} partId={partId} oldSolution={state.oldSolution as FilesSolutionInput | undefined}/>;
              } else if (content.__typename === 'ProgrammingExerciseContent') {
                return <ProgrammingExercise exercise={exercise} content={content} partId={partId}
                                            oldSolution={state.oldSolution as FilesSolutionInput | undefined}/>;
              } else if (content.__typename === 'RegexExerciseContent') {
                return <RegexExercise exercise={exercise} content={content} partId={partId} oldSolution={state.oldSolution as string | undefined}/>;
              } else if (content.__typename === 'SqlExerciseContent') {
                return <SqlExercise exercise={exercise} content={content} partId={partId} oldSolution={state.oldSolution as string | undefined}/>;
              } else if (content.__typename === 'UmlExerciseContent') {
                return <UmlExercise exercise={exercise} content={content} partId={partId} oldSolution={state.oldSolution as UmlDbClassDiagram | undefined}/>;
              } else if (content.__typename === 'WebExerciseContent') {
                return <WebExercise exercise={exercise} content={content} partId={partId} oldSolution={state.oldSolution as FilesSolutionInput | undefined}/>;
              } else if (content.__typename === 'XmlExerciseContent') {
                return <XmlExercise exercise={exercise} content={content} partId={partId} oldSolution={state.oldSolution as XmlSolutionInput | undefined}/>;
              } else {
                return neverRender(content);
              }
            }}
          </WithNullableNavigate>}
        </WithNullableNavigate>}
      </WithNullableNavigate>}
    </WithQuery>
  );

}
