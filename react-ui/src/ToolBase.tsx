import React from "react";
import {Route, Switch, useRouteMatch} from "react-router-dom";
import {CollectionList} from "./collectionTools/CollectionList";
import {CollectionOverview} from './collectionTools/CollectionOverview';
import {ToolOverview} from './collectionTools/ToolOverview';
import {ExerciseOverview} from "./collectionTools/ExerciseOverview";
import {Exercise} from './collectionTools/Exercise';

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

export interface CollectionBaseParams extends ToolBaseParams {
  collectionId: number;
}

function CollectionBase({toolId}: ToolBaseParams): JSX.Element {

  const {url, params} = useRouteMatch<{ collectionId: string; }>();
  const collectionId = parseInt(params.collectionId);

  return <Switch>
    <Route path={url} exact render={() => <CollectionOverview toolId={toolId} collectionId={collectionId}/>}/>
    <Route path={`${url}/exercises/:exerciseId`} render={() => <ExerciseBase toolId={toolId} collectionId={collectionId}/>}/>
  </Switch>
}


export interface ExerciseIProps extends CollectionBaseParams {
  exerciseId: number;
}

function ExerciseBase({toolId, collectionId}: CollectionBaseParams): JSX.Element {

  const {url, params} = useRouteMatch<{ exerciseId: string }>();
  const exerciseId = parseInt(params.exerciseId);

  return <Switch>
    <Route path={url} exact render={() => <ExerciseOverview exerciseId={exerciseId} collectionId={collectionId} toolId={toolId}/>}/>
    <Route path={`${url}/parts/:partId`} render={() => <Exercise toolId={toolId} collectionId={collectionId} exerciseId={exerciseId}/>}/>
  </Switch>
}
