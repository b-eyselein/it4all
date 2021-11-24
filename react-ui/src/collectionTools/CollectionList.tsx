import React from 'react';
import {ToolBaseParams} from '../ToolBase';
import {useCollectionListQuery} from '../graphql';
import {WithQuery} from '../WithQuery';
import {BreadCrumbs} from '../helpers/BreadCrumbs';
import {useTranslation} from 'react-i18next';
import {BulmaCard} from '../helpers/BulmaCard';
import {WithNullableNavigate} from '../WithNullableNavigate';

export function CollectionList({toolId}: ToolBaseParams): JSX.Element {

  const {t} = useTranslation('common');
  const query = useCollectionListQuery({variables: {toolId}});

  return (
    <div className="container">
      <WithQuery query={query}>
        {({tool}) => <WithNullableNavigate t={tool}>
          {({name, collections}) => <>
            <h1 className="title is-3 has-text-centered">{t('tool')} {name}: {t('collection_plural')}</h1>

            <div className="mb-3">
              <BreadCrumbs parts={[
                {routerLinkPart: '/', title: t('tool_plural')},
                {routerLinkPart: `tools/${toolId}`, title: name},
                {routerLinkPart: 'collections', title: t('collection_plural')},
              ]}/>
            </div>

            <div className="columns is-multiline">
              {collections.map(({collectionId, title, exerciseCount}) =>
                <div className="column is-one-quarter" key={collectionId}>
                  <BulmaCard title={`${collectionId}. ${title}`} footerItems={[{link: `./collections/${collectionId}`, title: t('toCollection')}]}>
                    <span>{exerciseCount} {t('exercise_plural')}</span>
                  </BulmaCard>
                </div>
              )}

            </div>
          </>}
        </WithNullableNavigate>}
      </WithQuery>
    </div>
  );
}
