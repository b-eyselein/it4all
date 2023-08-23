import {useTranslation} from 'react-i18next';
import {ToolFragment, useToolOverviewQuery} from './graphql';
import {WithQuery} from './WithQuery';
import {FooterItem, NewCard} from './helpers/BulmaCard';
import {randomTools} from './randomTools/randomTools';
import {randomToolsUrlFragment} from './urls';
import {ReactElement} from 'react';

interface IProps {
  tools: ToolFragment[];
}

function Inner({tools}: IProps): ReactElement {

  const {t} = useTranslation('common');

  return (
    <>
      {tools.map(({id, name, /*state,*/ collectionCount, exerciseCount}) =>
        <NewCard key={id} title={name} footerItems={[{link: `/tools/${id}`, title: t('toTool')}]}>
          <>{collectionCount} Sammlungen mit {exerciseCount} Aufgaben</>
        </NewCard>
      )}
    </>
  );
}

export function Home(): ReactElement {

  const {t} = useTranslation('common');
  const query = useToolOverviewQuery();

  function randomToolsRoutes(id: string): FooterItem[] {
    return [{link: `/${randomToolsUrlFragment}/${id}`, title: t('toTool')}];
  }

  return (
    <div className="container mx-auto">
      <h1 className="font-bold text-2xl text-center">{t('tool_plural')}&nbsp; <code>it4all</code></h1>

      <div className="mt-4 grid grid-cols-4 gap-x-4 gap-y-8">
        {randomTools.map(({id, name}) =>
          <NewCard key={id} title={name} footerItems={randomToolsRoutes(id)}>
            <>&nbsp;</>
          </NewCard>)}

        <WithQuery query={query}>
          {({tools}) => <Inner tools={tools}/>}
        </WithQuery>
      </div>
    </div>
  );
}
