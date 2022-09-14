import {useState} from 'react';
import {CollectionBaseParams} from '../ToolBase';
import {useTranslation} from 'react-i18next';
import {CollOverviewToolFragment, useCollectionOverviewQuery} from '../graphql';
import {BreadCrumbs} from '../helpers/BreadCrumbs';
import {WithQuery} from '../WithQuery';
import {ExerciseLinkCard} from './ExerciseLinkCard';
import {Pagination} from '../Pagination';
import {collectionsBreadCrumbs} from '../urls';

const SLICE_COUNT = 12;

interface InnerProps {
  toolId: string;
  collectionId: number;
  tool: CollOverviewToolFragment;
}

function Inner({toolId, tool}: InnerProps): JSX.Element {

  const {t} = useTranslation('common');
  const [currentPage, setCurrentPage] = useState(0);

  const {title, exercises} = tool.collection;

  const paginationNeeded = exercises.length > SLICE_COUNT;
  const maxPage = Math.ceil(exercises.length / SLICE_COUNT);
  const pages: number[] = Array(maxPage).fill(0).map((value, index) => index);

  const exercisesPaginated = exercises.slice(currentPage * SLICE_COUNT, (currentPage + 1) * SLICE_COUNT);

  return (
    <>
      <h1 className="mb-4 font-bold text-2xl text-center">{t('collection')} &quot;{title}&quot; - {t('exercise_plural')}</h1>

      <BreadCrumbs parents={collectionsBreadCrumbs(toolId, tool.name, t)} current={tool.collection.title}/>

      {paginationNeeded && <Pagination pages={pages} maxPage={maxPage} currentPage={currentPage} setCurrentPage={setCurrentPage}/>}

      {exercises.length > 0
        ? (
          <div className="grid grid-cols-3 gap-2">
            {exercisesPaginated.map((exercise) => <ExerciseLinkCard key={exercise.exerciseId} exercise={exercise}/>)}
          </div>
        ) : <div className="notification is-danger has-text-centered">{t('noExercisesFound')}</div>}
    </>
  );
}

export function CollectionOverview({toolId, collectionId}: CollectionBaseParams): JSX.Element {

  const query = useCollectionOverviewQuery({variables: {toolId, collId: collectionId}});

  return (
    <div className="container mx-auto">
      <WithQuery query={query}>
        {({tool}) => <Inner toolId={toolId} collectionId={collectionId} tool={tool}/>}
      </WithQuery>
    </div>
  );
}
