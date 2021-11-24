import React from 'react';
import {Link, Route, Routes} from 'react-router-dom';
import {RandomTool, randomTools} from './randomTools';
import {useTranslation} from 'react-i18next';
import {randomToolsUrlFragment} from '../urls';

export function RandomToolsBase(): JSX.Element {

  return (
    <Routes>
      {randomTools.map((tool) =>
        <Route key={tool.id} path={`/${tool.id}`}>
          <Route path={'/'} element={<RandomToolOverview tool={tool}/>}/>
          {tool.parts.map(({id, component}) =>
            <Route key={id} path={`/${id}`} element={component()}/>
          )}
        </Route>
      )}
    </Routes>
  );
}

interface IProps {
  tool: RandomTool;
}

function RandomToolOverview({tool: {id: toolId, name, parts}}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  return (
    <div className="container">
      <h1 className="title is-3 has-text-centered">{t('tool')} {name}</h1>

      <div className="buttons">
        {parts.filter((p) => !p.disabled).map(({id, name}) =>
          <Link to={`/${randomToolsUrlFragment}/${toolId}/${id}`} key={id} className="button is-link is-fullwidth">{name}</Link>
        )}
      </div>
    </div>
  );
}
