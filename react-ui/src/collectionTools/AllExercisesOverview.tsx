import {useState} from 'react';
import {AllExesOverviewToolFragment, useAllExercisesOverviewQuery} from '../graphql';
import {WithQuery} from '../WithQuery';
import {useTranslation} from 'react-i18next';
import {distinctObjectArray} from './tools/uml/UmlMemberAllocation';
import {ExerciseLinkCard} from './ExerciseLinkCard';
import classNames from 'classnames';

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
      <h1 className="mb-4 font-bold text-2xl text-center">{t('tool')} {name}: {t('allExercises')}</h1>

      {distinctTopicWithLevels.length > 0 && <section>
        <h2 className="mb-4 font-bold text-2xl text-center">Filter</h2>

        <div className="grid grid-cols-5 gap-2">
          {distinctTopicWithLevels.map((topicWithLevel) =>
            <button key={topicWithLevel.topic.abbreviation} type="button" onClick={() => toggleFilter(topicWithLevel.topic.abbreviation)}
                    className={classNames('p-2', 'rounded', 'w-full', filterAbbreviationsActivated.includes(topicWithLevel.topic.abbreviation) ? ['bg-blue-500', 'text-white'] : ['border', 'border-slate-500'])}>
              {topicWithLevel.topic.title}
            </button>)}
        </div>
      </section>}

      <hr className="my-4"/>

      <div className="grid grid-cols-3 gap-2">
        {filteredExercises.map((exercise) => <ExerciseLinkCard key={exercise.title} showCollection={true} exercise={exercise}/>)}
      </div>
    </>
  );

}


export function AllExercisesOverview({toolId}: { toolId: string }): JSX.Element {

  const allExercisesOverviewQuery = useAllExercisesOverviewQuery({variables: {toolId}});

  return (
    <div className="container mx-auto">
      <WithQuery query={allExercisesOverviewQuery}>
        {({tool}) => <Inner tool={tool}/>}
      </WithQuery>
    </div>
  );
}
