import React from 'react';
import {Link, Route, Switch, useRouteMatch} from 'react-router-dom';
import {boolRandomTool, naryRandomTool, RandomTool} from './randomTools';
import {useTranslation} from 'react-i18next';
import {BoolFillOut} from './BoolFillOut';
import {NaryAddition} from './NaryAddition';
import {NaryConversion} from './NaryConversion';
import {BoolCreate} from './BoolCreate';
import {NaryTwoConversion} from './NaryTwoConversion';

interface RouteParams {
  toolId: string;
}

export function RandomToolBase(): JSX.Element {

  const {url} = useRouteMatch<RouteParams>();

  return <Switch>
    <Route path={`${url}/bool`} exact render={() => <RandomToolOverview tool={boolRandomTool}/>}/>
    <Route path={`${url}/bool/fillOut`} component={BoolFillOut}/>
    <Route path={`${url}/bool/create`} component={BoolCreate}/>
    <Route path={`${url}/nary`} exact render={() => <RandomToolOverview tool={naryRandomTool}/>}/>
    <Route path={`${url}/nary/addition`} exact component={NaryAddition}/>
    <Route path={`${url}/nary/conversion`} exact component={NaryConversion}/>
    <Route path={`${url}/nary/twoConversion`} component={NaryTwoConversion}/>
  </Switch>;
}


interface IProps {
  tool: RandomTool;
}

function RandomToolOverview({tool: {id: toolId, name, parts}}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  return <div className="container">
    <h1 className="title is-3 has-text-centered">{t('tool')} {name}</h1>

    <div className="buttons">
      <Link className="button is-primary is-fullwidth" to={'/'}>{t('backToHome')}</Link>
    </div>

    <div className="my-3">
      <h2 className="subtitle is-3 has-text-centered">Übungsaufgaben</h2>

      <div className="buttons">
        {parts
          .filter((p) => !p.disabled)
          .map(({id, name}) =>
            <Link to={`${toolId}/${id}`} key={id} className="button is-link is-fullwidth">{name}</Link>
          )}
      </div>
    </div>

  </div>;
}
