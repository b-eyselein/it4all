import {useTranslation} from 'react-i18next';
import {useToolOverviewQuery} from './graphql';
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

  return (
    <div className="container">
      <h1 className="title is-3 has-text-centered">{t('tool_plural')}&nbsp; <code>it4all</code></h1>

      <div className="columns is-multiline">
        {randomTools.map(({id, name}) =>
          <div className="column is-one-quarter-desktop is-half-tablet" key={id}>
            <BulmaCard title={name} footerItems={randomToolsRoutes(id)}>
              <p>&nbsp;</p>
            </BulmaCard>
          </div>
        )}

        <WithQuery query={query}>
          {({tools}) => <>
            {tools.map(({id, name, /*state,*/ collectionCount, exerciseCount}) =>
              <div className="column is-one-quarter-desktop is-half-tablet" key={id}>
                <BulmaCard title={name} footerItems={[{link: `/tools/${id}`, title: t('toTool')}]}>
                  <p>{collectionCount} Sammlungen mit {exerciseCount} Aufgaben</p>
                </BulmaCard>
              </div>
            )}
          </>}
        </WithQuery>
      </div>
    </div>
  );
}
