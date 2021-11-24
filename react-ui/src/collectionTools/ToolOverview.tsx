import {useCollectionToolOverviewQuery} from '../graphql';
import {BreadCrumbs} from '../helpers/BreadCrumbs';
import {ProficiencyCard} from '../helpers/ProficiencyCard';
import {WithQuery} from '../WithQuery';
import {ToolBaseParams} from '../ToolBase';
import {BulmaCard} from '../helpers/BulmaCard';
import {useTranslation} from 'react-i18next';
import {allExercisesUrlFragment, collectionsUrlFragment, homeUrl, toolsUrlFragment} from '../urls';
import {WithNullableNavigate} from '../WithNullableNavigate';

export function ToolOverview({toolId}: ToolBaseParams): JSX.Element {

  const {t} = useTranslation('common');
  const query = useCollectionToolOverviewQuery({variables: {toolId}});

  return (
    <div className="container">
      <WithQuery query={query}>
        {({tool}) => <WithNullableNavigate t={tool}>
          {({name, collectionCount, exerciseCount, /*lessonCount,*/ proficiencies}) => <>
            <h1 className="title is-3 has-text-centered">{t('tool')} {name}</h1>

            <div className="mb-3">
              <BreadCrumbs parts={[
                {routerLinkPart: homeUrl, title: 'Tools'},
                {routerLinkPart: `${toolsUrlFragment}/${toolId}`, title: name}
              ]}/>
            </div>

            <div className="columns">
              <div className="column">
                <BulmaCard title={t('collection_plural')} footerItems={[
                  {link: `/${toolsUrlFragment}/${toolId}/${collectionsUrlFragment}`, title: t('toCollections')}
                ]}>
                  <span>{collectionCount} {t('collection_plural')} {t('with')} {exerciseCount} {t('exercise_plural')}</span>
                </BulmaCard>
              </div>

              <div className="column">
                <BulmaCard title={'allExercises'} footerItems={[
                  {link: `/${toolsUrlFragment}/${toolId}/${allExercisesUrlFragment}`, title: t('toAllExercises')}
                ]}>
                  <span>{exerciseCount} {t('exercise_plural')}</span>
                </BulmaCard>
              </div>

              {/*TODO: lessonCount > 0 && <div className="column">
            <BulmaCard title={t('lesson_plural')} footerItems={[{link: `./${toolId}/lessons`, title: t('toLessons')}]}>
              {() => <>{lessonCount} {t('lesson_plural')}</>}
            </BulmaCard>
          </div>*/}
            </div>

            <div className="my-3">
              <h2 className="subtitle is-3 has-text-centered">Meine Fertigkeiten</h2>

              {proficiencies.length > 0
                ? <div className="columns is-multiline">
                  {proficiencies.map((proficiency) =>
                    <div className="column is-one-quarter-desktop" key={proficiency.topic.abbreviation}>
                      <ProficiencyCard proficiency={proficiency}/>
                    </div>)}
                </div>
                : <div className="notification is-primary has-text-centered">
                  Sie haben bisher noch keine Fähigkeiten erworben.
                </div>
              }
            </div>
          </>}
        </WithNullableNavigate>}
      </WithQuery>
    </div>
  );
}
