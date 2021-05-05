import React from "react";
import {Route, Switch, useRouteMatch} from "react-router-dom";
import {CollectionList} from "./collectionTools/CollectionList";
import {CollectionOverview} from './collectionTools/CollectionOverview';
import {ToolOverview} from './collectionTools/ToolOverview';

interface ToolRouteProps {
  toolId: string;
}

export interface ToolBaseParams {
  toolId: string;
}

export function ToolBase(): JSX.Element {

  const {url, params: {toolId}} = useRouteMatch<ToolRouteProps>();

  return <Switch>
    <Route path={url} exact render={() => <ToolOverview toolId={toolId}/>}/>
    <Route path={`${url}/collections`} exact render={() => <CollectionList toolId={toolId}/>}/>
    <Route path={`${url}/collections/:collectionId`} render={() => <CollectionBase toolId={toolId}/>}/>
  </Switch>;
}

interface CollectionRouteProps {
  collectionId: string;
}

export interface CollectionBaseParams extends ToolBaseParams {
  collectionId: number;
}

function CollectionBase({toolId}: ToolBaseParams): JSX.Element {

  const {url, params} = useRouteMatch<CollectionRouteProps>();
  const collectionId = parseInt(params.collectionId);

  return <Switch>
    <Route path={url} exact render={() => <CollectionOverview toolId={toolId} collectionId={collectionId}/>}/>
  </Switch>
}
