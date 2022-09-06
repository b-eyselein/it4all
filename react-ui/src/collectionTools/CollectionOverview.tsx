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

function Inner({toolId, collectionId, tool}: InnerProps): JSX.Element {

  const {t} = useTranslation('common');
  const [currentPage, setCurrentPage] = useState(0);

  const {title, exercises} = tool.collection;

  const paginationNeeded = exercises.length > SLICE_COUNT;
  const maxPage = Math.ceil(exercises.length / SLICE_COUNT);
  const pages: number[] = Array(maxPage).fill(0).map((value, index) => index);

  const breadCrumbParts = [
    {routerLinkPart: '/', title: 'Tools'},
    {routerLinkPart: `tools/${toolId}`, title: tool.name},
    {routerLinkPart: 'collections', title: 'Sammlungen'},
    {routerLinkPart: collectionId.toString(), title: title}
  ];

  const exercisesPaginated = exercises.slice(currentPage * SLICE_COUNT, (currentPage + 1) * SLICE_COUNT);

  return (
    <>
      <h1 className="title is-3 has-text-centered">{t('collection')} &quot;{title}&quot; - {t('exercise_plural')}</h1>

      <BreadCrumbs parents={collectionsBreadCrumbs(toolId, tool.name, t)} current={tool.collection.title}/>

      {paginationNeeded && <Pagination pages={pages} maxPage={maxPage} currentPage={currentPage} setCurrentPage={setCurrentPage}/>}

      {exercises.length > 0
        ? (
          <div className="columns is-multiline">
            {exercisesPaginated.map((exercise) =>
              <div className="column is-one-third-desktop is-half" key={exercise.exerciseId}>
                <ExerciseLinkCard exercise={exercise}/>
              </div>
            )}
          </div>
        ) : (
          <div className="notification is-danger has-text-centered">{t('noExercisesFound')}</div>
        )
      }
    </>
  );
}

export function CollectionOverview({toolId, collectionId}: CollectionBaseParams): JSX.Element {

  const query = useCollectionOverviewQuery({variables: {toolId, collId: collectionId}});

  return (
    <div className="container">
      <WithQuery query={query}>
        {({tool}) => <Inner toolId={toolId} collectionId={collectionId} tool={tool}/>}
      </WithQuery>
    </div>
  );
}
