import { ToolOverviewFragment, useCollectionToolOverviewQuery } from '../graphql';
import { BreadCrumbs } from '../helpers/BreadCrumbs';
import { ProficiencyCard } from '../helpers/ProficiencyCard';
import { WithQuery } from '../WithQuery';
import { ToolBaseParams } from '../ToolBase';
import { NewCard } from '../helpers/BulmaCard';
import { useTranslation } from 'react-i18next';
import { allExercisesUrlFragment, collectionsUrlFragment, toolsBreadCrumbs, toolsUrlFragment } from '../urls';

interface InnerProps {
  toolId: string;
  tool: ToolOverviewFragment;
}

function Inner({ toolId, tool }: InnerProps): JSX.Element {

  const { t } = useTranslation('common');

  const { name, collectionCount, exerciseCount, proficiencies } = tool;

  const collectionsLink = `/${toolsUrlFragment}/${toolId}/${collectionsUrlFragment}`;
  const allExercisesLink = `/${toolsUrlFragment}/${toolId}/${allExercisesUrlFragment}`;

  return (
    <>
      <h1 className="mb-4 font-bold text-2xl text-center">{t('tool')} {name}</h1>

      <BreadCrumbs parents={[toolsBreadCrumbs(t)]} current={tool.name} />

      <div className="grid grid-cols-2 gap-2">
        <NewCard title={t('collection_plural')} footerItems={[{ link: collectionsLink, title: t('toCollections') }]}>
          <span>{collectionCount} {t('collection_plural')} {t('with')} {exerciseCount} {t('exercise_plural')}</span>
        </NewCard>

        <NewCard title={'allExercises'} footerItems={[{ link: allExercisesLink, title: t('toAllExercises') }]}>
          <span>{exerciseCount} {t('exercise_plural')}</span>
        </NewCard>
      </div>

      <div className="my-3">
        <h2 className="text-2xl text-center">{t('myProficiencies')}</h2>

        {proficiencies.length > 0
          ? (
            <div className="columns is-multiline">
              {proficiencies.map((proficiency) =>
                <div key={proficiency.topic.abbreviation} className="column is-one-quarter-desktop">
                  <ProficiencyCard proficiency={proficiency} />
                </div>)}
            </div>
          ) : <div className="my-4 p-2 rounded bg-blue-500 text-white text-center">{t('noProficienciesYet')}</div>

        }
      </div>
    </>
  );
}

export function ToolOverview({ toolId }: ToolBaseParams): JSX.Element {

  const query = useCollectionToolOverviewQuery({ variables: { toolId } });

  return (
    <div className="container mx-auto">

      <WithQuery query={query}>
        {({ tool }) =>
          tool
            ? <Inner toolId={toolId} tool={tool} />
            : <div>TODO!</div>}
      </WithQuery>
    </div>
  );
}
