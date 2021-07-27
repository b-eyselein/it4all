import React, {useState} from 'react';
import {CollectionBaseParams} from '../ToolBase';
import {useTranslation} from 'react-i18next';
import {CollectionOverviewQuery, FieldsForLinkFragment, useCollectionOverviewQuery} from '../graphql';
import {BreadCrumbPart, BreadCrumbs} from '../helpers/BreadCrumbs';
import {Redirect} from 'react-router-dom';
import {WithQuery} from '../WithQuery';
import classNames from 'classnames';
import {FilledPoints} from '../helpers/FilledPoints';
import {BulmaCard} from '../helpers/BulmaCard';

const SLICE_COUNT = 12;

export function CollectionOverview({toolId, collectionId}: CollectionBaseParams): JSX.Element {

  const {t} = useTranslation('common');
  const query = useCollectionOverviewQuery({variables: {toolId, collId: collectionId}});
  const [currentPage, setCurrentPage] = useState(0);

  function render({me}: CollectionOverviewQuery): JSX.Element {
    if (!me?.tool?.collection) {
      return <Redirect to={''}/>;
    }

    const {name: toolName, collection: {title, exercises}} = me.tool;

    const paginationNeeded = exercises.length > SLICE_COUNT;
    const maxPage = Math.ceil(exercises.length / SLICE_COUNT);
    const pages: number[] = Array(maxPage).fill(0).map((value, index) => index);

    function getExercisesPaginated(): FieldsForLinkFragment[] {
      return exercises.slice(currentPage * SLICE_COUNT, (currentPage + 1) * SLICE_COUNT);
    }

    const breadCrumbs: BreadCrumbPart[] = [
      {routerLinkPart: '/', title: 'Tools'},
      {routerLinkPart: `tools/${toolId}`, title: toolName},
      {routerLinkPart: 'collections', title: 'Sammlungen'},
      {routerLinkPart: collectionId.toString(), title: title}
    ];

    return <div className="container">
      <h1 className="title is-3 has-text-centered">{t('collection')} &quot;{title}&quot; - {t('exercise_plural')}</h1>

      <div className="mb-3">
        <BreadCrumbs parts={breadCrumbs}/>
      </div>

      {paginationNeeded && <nav className="pagination is-centered" role="navigation" aria-label="pagination">
        <button className="button pagination-previous" onClick={() => setCurrentPage(currentPage - 1)} disabled={currentPage < 1}>
          {t('previous')}
        </button>
        <ul className="pagination-list">
          {pages.map((page) => <li key={page}>
            <button className={classNames('button', 'pagination-link', {'is-current': page === currentPage})} onClick={() => setCurrentPage(page)}>
              {page}
            </button>
          </li>)}
        </ul>
        <button className="button pagination-next" onClick={() => setCurrentPage(currentPage + 1)} disabled={currentPage >= maxPage - 1}>
          {t('next')}
        </button>
      </nav>}

      {exercises.length > 0
        ? <div className="columns is-multiline">
          {getExercisesPaginated().map((exercise) =>
            <div className="column is-one-third-desktop is-half" key={exercise.exerciseId}>
              <BulmaCard
                title={<>
                  {exercise.exerciseId}. {exercise.title}
                  &nbsp;
                  <div className="tag" title="Schwierigkeit">
                    <FilledPoints filledPoints={exercise.difficulty} maxPoints={5}/>
                  </div>
                </>} footerItems={[{link: `./${collectionId}/exercises/${exercise.exerciseId}`, title: t('toExercise')}]}>
                {() => <>

                  <div className="tags">
                    {exercise.parts.map(({id, name, solved}) =>
                      <div key={id} className={classNames('tag', {'is-success': solved})}>{name}</div>
                    )}
                  </div>
                  {exercise.topicsWithLevels.length > 0
                    ? <div className="tags">
                      {exercise.topicsWithLevels.map(({topic, level}) =>
                        <div className="tag" title={topic.title} key={topic.abbreviation}>
                          {topic.abbreviation}&nbsp; - &nbsp;
                          <FilledPoints filledPoints={level.levelIndex} maxPoints={topic.maxLevel.levelIndex}/>
                        </div>)}
                    </div>
                    : <div className="tag is-warning">Keine Tags vorhanden</div>}
                </>}
              </BulmaCard>
            </div>
          )}
        </div>
        : <div className="notification is-danger has-text-centered">{t('noExercisesFound')}</div>}

    </div>;


  }

  return <WithQuery query={query} render={render}/>;
}
