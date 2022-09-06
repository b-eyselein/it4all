import {useState} from 'react';
import {AllExesOverviewToolFragment, useAllExercisesOverviewQuery} from '../graphql';
import {WithQuery} from '../WithQuery';
import {useTranslation} from 'react-i18next';
import {distinctObjectArray} from './tools/uml/UmlMemberAllocation';
import classNames from 'classnames';
import {ExerciseLinkCard} from './ExerciseLinkCard';

interface InnerProps {
  tool: AllExesOverviewToolFragment;
}

function Inner({tool}: InnerProps): JSX.Element {

  const {name, allExercises} = tool;

  const {t} = useTranslation('common');
  const [filterAbbreviationsActivated, setFilterAbbreviationsActivated] = useState<string[]>([]);

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

      {distinctTopicWithLevels.length > 0 && <section>
        <h2 className="title is-3 has-text-centered">Filter</h2>

        <div className="columns is-multiline">
          {distinctTopicWithLevels.map((topicWithLevel) =>
            <div className="column is-one-fifth" key={topicWithLevel.topic.abbreviation}>
              <button type="button" onClick={() => toggleFilter(topicWithLevel.topic.abbreviation)}
                      className={classNames('button', 'is-fullwidth', {'is-link': filterAbbreviationsActivated.includes(topicWithLevel.topic.abbreviation)})}>
                {topicWithLevel.topic.title}
              </button>
            </div>)}
        </div>
      </section>}

      <hr/>

      <div className="columns is-multiline">
        {filteredExercises.map((exercise) => <div className="column is-one-third-desktop" key={exercise.title}>
          <ExerciseLinkCard exercise={exercise}/>
        </div>)}
      </div>
    </>
  );

}


export function AllExercisesOverview({toolId}: { toolId: string }): JSX.Element {

  const allExercisesOverviewQuery = useAllExercisesOverviewQuery({variables: {toolId}});

  return (
    <div className="container">
      <WithQuery query={allExercisesOverviewQuery}>
        {({tool}) => <Inner tool={tool}/>}
      </WithQuery>
    </div>
  );
}
