import React, {useState} from 'react';
import {AllExercisesOverviewQuery, useAllExercisesOverviewQuery} from '../graphql';
import {Redirect} from 'react-router-dom';
import {homeUrl} from '../urls';
import {WithQuery} from '../WithQuery';
import {useTranslation} from 'react-i18next';
import {distinctObjectArray} from './tools/uml/UmlMemberAllocation';
import classNames from 'classnames';
import {ExerciseLinkCard} from './ExerciseLinkCard';

interface IProps {
  toolId: string;
}

export function AllExercisesOverview({toolId}: IProps): JSX.Element {

  const {t} = useTranslation('common');
  const allExercisesOverviewQuery = useAllExercisesOverviewQuery({variables: {toolId}});
  const [filterAbbreviationsActivated, setFilterAbbreviationsActivated] = useState<string[]>([]);

  function render({tool}: AllExercisesOverviewQuery): JSX.Element {
    if (!tool) {
      return <Redirect to={homeUrl}/>;
    }

    const {name, allExercises} = tool;

    const distinctTopicWithLevels = distinctObjectArray(
      allExercises.flatMap((exercises) => exercises.topicsWithLevels),
      (t) => t.topic.abbreviation
    );

    const filteredExercises = filterAbbreviationsActivated.length > 0
      ? allExercises.filter((exercise) => filterAbbreviationsActivated.every((topicAbb) => exercise.topicsWithLevels.map(({topic}) => topic.abbreviation).includes(topicAbb)))
      : allExercises;

    function toggleFilter(topicWithLevelAbbreviation: string): void {
      setFilterAbbreviationsActivated((filters) => filters.includes(topicWithLevelAbbreviation)
        ? filters.filter((s) => s !== topicWithLevelAbbreviation)
        : [...filters, topicWithLevelAbbreviation]
      );
    }

    return (
      <>
        <h1 className="title is-3 has-text-centered">{t('tool')} {name}: {t('allExercises')}</h1>

        <section>
          <h2 className="title is-3 has-text-centered">Filter</h2>

          <div className="columns is-multiline">
            {distinctTopicWithLevels.map((topicWithLevel) =>
              <div className="column is-one-fifth" key={topicWithLevel.topic.abbreviation}>
                <button className={classNames('button', 'is-fullwidth', {'is-link': filterAbbreviationsActivated.includes(topicWithLevel.topic.abbreviation)})}
                        onClick={() => toggleFilter(topicWithLevel.topic.abbreviation)}>
                  {topicWithLevel.topic.title}
                </button>
              </div>)}
          </div>
        </section>

        <hr/>

        <h1 className="title is-3 has-text-centered">Alle Aufgaben</h1>

        <div className="columns is-multiline">
          {filteredExercises.map((exercise) => <div className="column is-one-third-desktop" key={exercise.title}>
            <ExerciseLinkCard exercise={exercise}/>
          </div>)}
        </div>
      </>
    );
  }

  return (
    <div className="container">
      <WithQuery query={allExercisesOverviewQuery} render={render}/>
    </div>
  );
}
