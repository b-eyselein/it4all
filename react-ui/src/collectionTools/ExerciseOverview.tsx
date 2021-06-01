import React from "react";
import {ExerciseOverviewQuery, PartFragment, useExerciseOverviewQuery} from "../generated/graphql";
import {ExerciseIProps} from "../ToolBase";
import {Link, Redirect, useRouteMatch} from "react-router-dom";
import {BreadCrumbPart, BreadCrumbs} from "../helpers/BreadCrumbs";
import {WithQuery} from "../WithQuery";

export function ExerciseOverview({toolId, collectionId, exerciseId}: ExerciseIProps): JSX.Element {

  const {url} = useRouteMatch();

  const exerciseOverviewQuery = useExerciseOverviewQuery({variables: {toolId, collectionId, exerciseId}});

  function render({me}: ExerciseOverviewQuery): JSX.Element {
    if (!me || !me.tool || !me.tool.collection || !me.tool.collection.exercise) {
      return <Redirect to={'/'}/>
    }

    const tool = me.tool;
    const collection = me.tool.collection;
    const exercise = me.tool.collection.exercise;

    const entryParts: PartFragment[] = exercise.parts.filter((p) => p.isEntryPart);

    const breadCrumbs: BreadCrumbPart[] = [
      {routerLinkPart: '/', title: 'Tools'},
      {routerLinkPart: `tools/${toolId}`, title: tool.name},
      {routerLinkPart: 'collections', title: 'Sammlungen'},
      {routerLinkPart: collection.collectionId.toString(), title: collection.title},
      {routerLinkPart: 'exercises', title: 'Aufgaben'},
      {routerLinkPart: exercise.exerciseId.toString(), title: exercise.exerciseId.toString()}
    ];

    return (
      <div className="container">

        <h1 className="title is-3 has-text-centered">Aufgabe &quot;{exercise.title}&quot;</h1>

        <div className="my-3">
          <BreadCrumbs parts={breadCrumbs}/>
        </div>

        <div className="notification is-light-grey" dangerouslySetInnerHTML={{__html: exercise.text}}/>

        <div className="columns">
          {entryParts.map((part) =>
            <div className="column" key={part.id}>
              <Link className=" button is-link is-fullwidth" to={`${url}/parts/${part.id}`}>{part.name}</Link>
            </div>
          )}
        </div>

      </div>
    );

  }

  return <WithQuery query={exerciseOverviewQuery} children={render}/>;
}
