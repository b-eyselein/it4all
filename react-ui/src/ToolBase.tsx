import {Navigate, Route, Routes, useParams} from 'react-router-dom';
import {CollectionList} from './collectionTools/CollectionList';
import {CollectionOverview} from './collectionTools/CollectionOverview';
import {ToolOverview} from './collectionTools/ToolOverview';
import {ExerciseOverview} from './collectionTools/ExerciseOverview';
import {Exercise} from './collectionTools/Exercise';
import {AllExercisesOverview} from './collectionTools/AllExercisesOverview';
import {allExercisesUrlFragment, collectionsUrlFragment, exercisesUrlFragment, homeUrl} from './urls';

export interface ToolBaseParams {
  toolId: string;
}

export function ToolBase(): JSX.Element {

  const {toolId} = useParams<'toolId'>();

  if (!toolId) {
    return <Navigate to={homeUrl}/>;
  }

  return (
    <Routes>
      <Route index element={<ToolOverview toolId={toolId}/>}/>

      <Route path={collectionsUrlFragment}>
        <Route index element={<CollectionList toolId={toolId}/>}/>
        <Route path={':collectionId/*'} element={<CollectionBase toolId={toolId}/>}/>
      </Route>

      <Route path={allExercisesUrlFragment} element={<AllExercisesOverview toolId={toolId}/>}/>
    </Routes>
  );
}

export interface CollectionBaseParams extends ToolBaseParams {
  collectionId: number;
}

function CollectionBase({toolId}: ToolBaseParams): JSX.Element {

  const params = useParams<'collectionId'>();

  if (!params.collectionId) {
    return <Navigate to={homeUrl}/>;
  }

  const collectionId = parseInt(params.collectionId);

  return (
    <Routes>
      <Route index element={<CollectionOverview toolId={toolId} collectionId={collectionId}/>}/>
      <Route path={exercisesUrlFragment}>
        <Route path={':exerciseId/*'} element={<ExerciseBase toolId={toolId} collectionId={collectionId}/>}/>
      </Route>
    </Routes>
  );
}

export interface ExerciseIProps extends CollectionBaseParams {
  exerciseId: number;
}

function ExerciseBase({toolId, collectionId}: CollectionBaseParams): JSX.Element {

  const params = useParams<'exerciseId'>();

  if (!params.exerciseId) {
    return <Navigate to={homeUrl}/>;
  }

  const exerciseId = parseInt(params.exerciseId);

  return (
    <Routes>
      <Route index element={<ExerciseOverview exerciseId={exerciseId} collectionId={collectionId} toolId={toolId}/>}/>
      <Route path={'/solve'} element={<ExerciseContainer toolId={toolId} collectionId={collectionId} exerciseId={exerciseId}/>}/>
      <Route path={'/parts/:partId'} element={<ExerciseContainer toolId={toolId} collectionId={collectionId} exerciseId={exerciseId}/>}/>
    </Routes>
  );
}


function ExerciseContainer({toolId, collectionId, exerciseId}: ExerciseIProps): JSX.Element {

  const {partId} = useParams<'partId'>();

  if (!partId) {
    return <Navigate to={homeUrl}/>;
  }

  return <Exercise toolId={toolId} collectionId={collectionId} exerciseId={exerciseId} partId={partId}/>;
}
