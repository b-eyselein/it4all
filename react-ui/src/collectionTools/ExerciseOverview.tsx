import {ExOverviewToolFragment, useExerciseOverviewQuery} from '../graphql';
import {Link, Navigate} from 'react-router-dom';
import {BreadCrumbs} from '../helpers/BreadCrumbs';
import {WithQuery} from '../WithQuery';
import {useTranslation} from 'react-i18next';
import {exercisesBreadCrumbs, homeUrl, loginUrl} from '../urls';
import {WithCurrentUser} from '../WithCurrentUser';

interface InnerProps {
  toolId: string;
  collectionId: number;
  exerciseId: number;
  tool: ExOverviewToolFragment;
}

function Inner({toolId, collectionId, exerciseId, tool}: InnerProps): JSX.Element {

  const {t} = useTranslation('common');

  if (!tool.collection.exercise) {
    return <Navigate to={homeUrl}/>;
  }

  const {title, text, parts} = tool.collection.exercise;

  const noLoginOption = (
    <div className="notification is-primary has-text-centered">
      <Link to={loginUrl}>{t('pleaseLogin')}</Link>
    </div>
  );

  return (
    <>
      <h1 className="title is-3 has-text-centered">{t('exercise_{{title}}', {title})}</h1>

      <BreadCrumbs parents={exercisesBreadCrumbs(toolId, tool.name, collectionId, tool.collection.title, t)} current={exerciseId.toString()}/>

      <div className="notification is-light-grey" dangerouslySetInnerHTML={{__html: text}}/>

      <WithCurrentUser noLoginOption={noLoginOption}>
        {() => parts.length === 0
          ? <Link className="button is-link is-fullwidth" to="./solve">{t('solve')}</Link>
          : (
            <div className="columns">
              {parts.filter(({isEntryPart}) => isEntryPart).map((part) =>
                <div key={part.id} className="column">
                  <Link className="button is-link is-fullwidth" to={`./parts/${part.id}`}>
                    {/* FIXME: check if url is working! */}
                    {part.name}
                  </Link>
                </div>
              )}
            </div>
          )}
      </WithCurrentUser>
    </>
  );
}

interface IProps {
  toolId: string;
  collectionId: number;
  exerciseId: number;
}

export function ExerciseOverview({toolId, collectionId, exerciseId}: IProps): JSX.Element {

  const exerciseOverviewQuery = useExerciseOverviewQuery({variables: {toolId, collectionId, exerciseId}});

  return (
    <div className="container">
      <WithQuery query={exerciseOverviewQuery}>
        {({tool}) => <Inner toolId={toolId} collectionId={collectionId} exerciseId={exerciseId} tool={tool}/>}
      </WithQuery>
    </div>
  );
}
