import React from 'react';
import {Link, Route, Switch, useRouteMatch} from 'react-router-dom';
import {RandomTool, randomTools} from './randomTools';
import {useTranslation} from 'react-i18next';
import {randomToolsUrlFragment} from '../urls';

export function RandomToolsBase(): JSX.Element {

  const {url} = useRouteMatch();

  return (
    <Switch>
      {randomTools.map((tool) =>
        <Route key={tool.id} path={`${url}/${tool.id}`} render={() => <RandomToolBase tool={tool}/>}/>
      )}
    </Switch>
  );
}

function RandomToolBase({tool}: { tool: RandomTool }): JSX.Element {

  const {url} = useRouteMatch();

  return (
    <Switch>
      <Route path={`${url}/`} exact render={() => <RandomToolOverview tool={tool}/>}/>
      {tool.parts.map(({id, component}) =>
        <Route key={id} path={`${url}/${id}`} component={component}/>
      )}
    </Switch>
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
