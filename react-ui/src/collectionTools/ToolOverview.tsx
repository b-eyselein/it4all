import {ToolOverviewFragment, useCollectionToolOverviewQuery} from '../graphql';
import {BreadCrumbs} from '../helpers/BreadCrumbs';
import {ProficiencyCard} from '../helpers/ProficiencyCard';
import {WithQuery} from '../WithQuery';
import {ToolBaseParams} from '../ToolBase';
import {BulmaCard} from '../helpers/BulmaCard';
import {useTranslation} from 'react-i18next';
import {allExercisesUrlFragment, collectionsUrlFragment, homeUrl, toolsUrlFragment} from '../urls';

interface InnerProps {
  toolId: string;
  tool: ToolOverviewFragment;
}

function Inner({toolId, tool}: InnerProps): JSX.Element {

  const {t} = useTranslation('common');

  const {name, collectionCount, exerciseCount, proficiencies} = tool;

  const breadCrumbParts = [
    {routerLinkPart: homeUrl, title: 'Tools'},
    {routerLinkPart: `${toolsUrlFragment}/${toolId}`, title: name}
  ];

  const collectionsLink = `/${toolsUrlFragment}/${toolId}/${collectionsUrlFragment}`;
  const allExercisesLink = `/${toolsUrlFragment}/${toolId}/${allExercisesUrlFragment}`;

  return (
    <>
      <h1 className="title is-3 has-text-centered">{t('tool')} {name}</h1>

      <BreadCrumbs parts={breadCrumbParts}/>

      <div className="columns">
        <div className="column">
          <BulmaCard title={t('collection_plural')} footerItems={[{link: collectionsLink, title: t('toCollections')}]}>
            <span>{collectionCount} {t('collection_plural')} {t('with')} {exerciseCount} {t('exercise_plural')}</span>
          </BulmaCard>
        </div>

        <div className="column">
          <BulmaCard title={'allExercises'} footerItems={[{link: allExercisesLink, title: t('toAllExercises')}]}>
            <span>{exerciseCount} {t('exercise_plural')}</span>
          </BulmaCard>
        </div>
      </div>

      <div className="my-3">
        <h2 className="subtitle is-3 has-text-centered">{t('myProficiencies')}</h2>

        {proficiencies.length > 0
          ? (
            <div className="columns is-multiline">
              {proficiencies.map((proficiency) =>
                <div key={proficiency.topic.abbreviation} className="column is-one-quarter-desktop">
                  <ProficiencyCard proficiency={proficiency}/>
                </div>)}
            </div>
          ) : (
            <div className="notification is-primary has-text-centered">{t('noProficienciesYet')}</div>
          )
        }
      </div>
    </>
  );
}

export function ToolOverview({toolId}: ToolBaseParams): JSX.Element {

  const query = useCollectionToolOverviewQuery({variables: {toolId}});

  return (
    <div className="container">

      <WithQuery query={query}>
        {({tool}) => <Inner toolId={toolId} tool={tool}/>}
      </WithQuery>

    </div>
  );
}
