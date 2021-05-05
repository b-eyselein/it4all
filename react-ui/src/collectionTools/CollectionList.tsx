import React from "react";
import {ToolBaseParams} from "../ToolBase";
import {CollectionListQuery, useCollectionListQuery} from "../generated/graphql";
import {WithQuery} from "../WithQuery";
import {Redirect} from "react-router-dom";
import {BreadCrumbPart, BreadCrumbs} from "../helpers/BreadCrumbs";
import {useTranslation} from "react-i18next";
import {BulmaCard} from "../helpers/BulmaCard";

export function CollectionList({toolId}: ToolBaseParams): JSX.Element {

  const {t} = useTranslation('common');
  const query = useCollectionListQuery({variables: {toolId}});

  function render({me}: CollectionListQuery): JSX.Element {
    if (!me?.tool?.collections) {
      return <Redirect to={''}/>;
    }

    const {name, collections} = me.tool;

    const breadCrumbs: BreadCrumbPart[] = [
      {routerLinkPart: '/', title: t('tool_plural')},
      {routerLinkPart: `tools/${toolId}`, title: name},
      {routerLinkPart: 'collections', title: t('collection_plural')},
    ];

    return (
      <div className="container">
        <h1 className="title is-3 has-text-centered">{t('tool')} {name}: {t('collection_plural')}</h1>

        <div className="mb-3">
          <BreadCrumbs parts={breadCrumbs}/>
        </div>

        <div className="columns is-multiline">
          {collections.map(({collectionId, title, exerciseCount}) =>
            <div className="column is-one-quarter" key={collectionId}>
              <BulmaCard title={`${collectionId}. ${title}`}
                         footerItems={[{link: `./collections/${collectionId}`, title: t('toCollection')}]}>
                {() => <>{exerciseCount} {t('exercise_plural')}</>}
              </BulmaCard>
            </div>
          )}
        </div>
      </div>
    );
  }

  return <WithQuery query={query} children={render}/>;
}
