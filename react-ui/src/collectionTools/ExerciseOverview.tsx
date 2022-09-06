import {ExOverviewToolFragment, useExerciseOverviewQuery} from '../graphql';
import {Link, Navigate} from 'react-router-dom';
import {BreadCrumbs} from '../helpers/BreadCrumbs';
import {WithQuery} from '../WithQuery';
import {useSelector} from 'react-redux';
import {currentUserSelector} from '../store/store';
import {useTranslation} from 'react-i18next';
import {homeUrl} from '../urls';

interface InnerProps {
  toolId: string;
  tool: ExOverviewToolFragment;
}

function Inner({toolId, tool}: InnerProps): JSX.Element {

  const {t} = useTranslation('common');
  const currentUser = useSelector(currentUserSelector);

  if (!tool.collection.exercise) {
    return <Navigate to={homeUrl}/>;
  }

  const {title: collectionTitle, collectionId, exercise} = tool.collection;

  const breadCrumbParts = [
    {routerLinkPart: '', title: 'Tools'},
    {routerLinkPart: `tools/${toolId}`, title: tool.name},
    {routerLinkPart: 'collections', title: 'Sammlungen'},
    {routerLinkPart: collectionId.toString(), title: collectionTitle},
    {routerLinkPart: 'exercises', title: 'Aufgaben'},
    {routerLinkPart: exercise.exerciseId.toString(), title: exercise.exerciseId.toString()}
  ];

  const exerciseEntryParts = exercise.parts.length === 0
    ? [/* TODO: */]
    : exercise.parts.filter((p) => p.isEntryPart);

  return (
    <>
      <h1 className="title is-3 has-text-centered">{t('exercise_{{title}}', {title: exercise.title})}</h1>

      <BreadCrumbs parts={breadCrumbParts}/>

      <div className="notification is-light-grey" dangerouslySetInnerHTML={{__html: exercise.text}}/>

      {currentUser
        ? (
          <div className="columns">
            {exerciseEntryParts.map((part) => <div key={part.id} className="column">
              {/* FIXME: check if url is working! */}
              <Link className="button is-link is-fullwidth" to={`./parts/${part.id}`}>{part.name}</Link>
            </div>)}
          </div>
        ) : (
          <div className="notification is-primary has-text-centered">
            <Link to={'/loginForm'}>{t('pleaseLogin')}</Link>
          </div>
        )
      }

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
        {({tool}) => <Inner toolId={toolId} tool={tool}/>}
      </WithQuery>
    </div>
  );
}
