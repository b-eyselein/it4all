import {ToolBaseParams} from '../ToolBase';
import {ToolCollectionOverviewFragment, useCollectionListQuery} from '../graphql';
import {WithQuery} from '../WithQuery';
import {BreadCrumbs} from '../helpers/BreadCrumbs';
import {useTranslation} from 'react-i18next';
import {toolBreadCrumbs} from '../urls';
import {NewCard} from '../helpers/BulmaCard';

interface InnerProps {
  toolId: string;
  tool: ToolCollectionOverviewFragment;
}

function Inner({toolId, tool}: InnerProps): JSX.Element {

  const {t} = useTranslation('common');

  return (
    <>
      <h1 className="mb-4 font-bold text-2xl text-center">{t('tool')} {tool.name}: {t('collection_plural')}</h1>

      <BreadCrumbs parents={toolBreadCrumbs(toolId, tool.name, t)} current={t('collection_plural')}/>

      <div className="grid grid-cols-4 gap-2">
        {tool.collections.map(({collectionId, title, exerciseCount}) =>
          <NewCard key={collectionId} title={`${collectionId}. ${title}`} footerItems={[{link: `./${collectionId}`, title: t('toCollection')}]}>
            <span>{exerciseCount} {t('exercise_plural')}</span>
          </NewCard>
        )}
      </div>
    </>
  );
}

export function CollectionList({toolId}: ToolBaseParams): JSX.Element {

  const query = useCollectionListQuery({variables: {toolId}});

  return (
    <div className="container mx-auto">
      <WithQuery query={query}>
        {({tool}) => <Inner toolId={toolId} tool={tool}/>}
      </WithQuery>
    </div>
  );
}
