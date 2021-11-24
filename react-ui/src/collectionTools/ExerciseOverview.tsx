import {useExerciseOverviewQuery} from '../graphql';
import {ExerciseIProps} from '../ToolBase';
import {Link} from 'react-router-dom';
import {BreadCrumbs} from '../helpers/BreadCrumbs';
import {WithQuery} from '../WithQuery';
import {useSelector} from 'react-redux';
import {currentUserSelector} from '../store/store';
import {useTranslation} from 'react-i18next';
import {WithNullableNavigate} from '../WithNullableNavigate';

export function ExerciseOverview({toolId, collectionId, exerciseId}: ExerciseIProps): JSX.Element {

  const {t} = useTranslation('common');
  const currentUser = useSelector(currentUserSelector);
  const exerciseOverviewQuery = useExerciseOverviewQuery({variables: {toolId, collectionId, exerciseId}});

  return (
    <div className="container">
      <WithQuery query={exerciseOverviewQuery}>
        {({tool: maybeTool}) => <WithNullableNavigate t={maybeTool}>
          {(tool) => <WithNullableNavigate t={tool.collection}>
            {(collection) => <WithNullableNavigate t={collection.exercise}>
              {(exercise) => <>

                <h1 className="title is-3 has-text-centered">Aufgabe &quot;{exercise.title}&quot;</h1>

                <div className="my-3">
                  <BreadCrumbs parts={[
                    {routerLinkPart: '/', title: 'Tools'},
                    {routerLinkPart: `tools/${toolId}`, title: tool.name},
                    {routerLinkPart: 'collections', title: 'Sammlungen'},
                    {routerLinkPart: collection.collectionId.toString(), title: collection.title},
                    {routerLinkPart: 'exercises', title: 'Aufgaben'},
                    {routerLinkPart: exercise.exerciseId.toString(), title: exercise.exerciseId.toString()}
                  ]}/>
                </div>

                <div className="notification is-light-grey" dangerouslySetInnerHTML={{__html: exercise.text}}/>

                {currentUser
                  ? <div className="columns">
                    {exercise.parts.filter((p) => p.isEntryPart).map((part) =>
                      <div className="column" key={part.id}>
                        {/* FIXME: check if url is working! */}
                        <Link className="button is-link is-fullwidth" to={`./parts/${part.id}`}>{part.name}</Link>
                      </div>)}
                  </div>
                  : <div className="notification is-primary has-text-centered">{t('pleaseLogin')}</div>
                }

              </>}
            </WithNullableNavigate>}
          </WithNullableNavigate>}
        </WithNullableNavigate>}
      </WithQuery>
    </div>
  );
}
