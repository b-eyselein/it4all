import {ToolBaseParams} from '../ToolBase';
import {ToolCollectionOverviewFragment, useCollectionListQuery} from '../graphql';
import {WithQuery} from '../WithQuery';
import {BreadCrumbs} from '../helpers/BreadCrumbs';
import {useTranslation} from 'react-i18next';
import {BulmaCard} from '../helpers/BulmaCard';

interface InnerProps {
  toolId: string;
  tool: ToolCollectionOverviewFragment;
}

function Inner({toolId, tool}: InnerProps): JSX.Element {

  const {t} = useTranslation('common');

  const {name, collections} = tool;

  const breadCrumbParts = [
    {routerLinkPart: '/', title: t('tool_plural')},
    {routerLinkPart: `tools/${toolId}`, title: name},
    {routerLinkPart: 'collections', title: t('collection_plural')},
  ];

  return (
    <>
      <h1 className="title is-3 has-text-centered">{t('tool')} {name}: {t('collection_plural')}</h1>

      <BreadCrumbs parts={breadCrumbParts}/>

      <div className="columns is-multiline">
        {collections.map(({collectionId, title, exerciseCount}) =>
          <div className="column is-one-quarter" key={collectionId}>
            <BulmaCard title={`${collectionId}. ${title}`} footerItems={[{link: `./${collectionId}`, title: t('toCollection')}]}>
              <span>{exerciseCount} {t('exercise_plural')}</span>
            </BulmaCard>
          </div>
        )}
      </div>
    </>
  );
}

export function CollectionList({toolId}: ToolBaseParams): JSX.Element {

  const query = useCollectionListQuery({variables: {toolId}});

  return (
    <div className="container">
      <WithQuery query={query}>
        {({tool}) => <Inner toolId={toolId} tool={tool}/>}
      </WithQuery>
    </div>
  );
}
