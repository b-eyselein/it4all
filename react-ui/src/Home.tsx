import React from 'react';
import {useTranslation} from 'react-i18next';
import {ToolOverviewQuery, useToolOverviewQuery} from './graphql';
import {WithQuery} from './WithQuery';
import {BulmaCard, FooterItem} from './helpers/BulmaCard';
import {randomTools} from './randomTools/randomTools';
import {randomToolsUrlFragment} from './urls';

export function Home(): JSX.Element {

  const {t} = useTranslation('common');
  const query = useToolOverviewQuery();

  function randomToolsRoutes(id: string): FooterItem[] {
    return [
      {link: `/${randomToolsUrlFragment}/${id}`, title: t('toTool')}
    ];
  }

  function render({tools: collectionTools}: ToolOverviewQuery): JSX.Element {
    return <>
      {collectionTools.map(({id, name, /*state,*/ collectionCount, lessonCount, exerciseCount}) =>
        <div className="column is-one-quarter-desktop is-half-tablet" key={id}>
          {/* TODO: use state!*/}
          <BulmaCard title={name} footerItems={[{link: `/tools/${id}`, title: t('toTool')}]}>
            {() => <>
              <p>{collectionCount} Sammlungen mit {exerciseCount} Aufgaben</p>
              <p>{lessonCount === 0 ? 'Keine' : lessonCount} Lektionen</p>
            </>}
          </BulmaCard>
        </div>
      )}
    </>;
  }

  return (
    <div className="container">
      <h1 className="title is-3 has-text-centered">
        {t('tool_plural')}&nbsp;<code>it4all</code>
      </h1>

      <div className="columns is-multiline">
        {randomTools.map(({id, name}) =>
          <div className="column is-one-quarter-desktop is-half-tablet" key={id}>
            <BulmaCard title={name} footerItems={randomToolsRoutes(id)}>
              {() => <><p>&nbsp;</p><p>&nbsp;</p></>}
            </BulmaCard>
          </div>
        )}

        <WithQuery query={query} render={render}/>
      </div>
    </div>
  );
}
