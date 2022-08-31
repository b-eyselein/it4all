import {FilledPoints} from '../helpers/FilledPoints';
import classNames from 'classnames';
import {BulmaCard} from '../helpers/BulmaCard';
import {FieldsForLinkFragment} from '../graphql';
import {collectionsUrlFragment, exercisesUrlFragment, toolsUrlFragment} from '../urls';
import {useTranslation} from 'react-i18next';

interface IProps {
  exercise: FieldsForLinkFragment;
}

export function ExerciseLinkCard({exercise}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  const {toolId, collectionId, exerciseId, title, parts, difficulty, topicsWithLevels} = exercise;

  const cardTitle = <>
    {exerciseId}. {title}
    &nbsp;
    <div className="tag" title="Schwierigkeit">
      <FilledPoints filledPoints={difficulty.levelIndex} maxPoints={/* TODO: */4}/>
    </div>
  </>;

  return (
    <BulmaCard title={cardTitle} footerItems={[{
      link: `/${toolsUrlFragment}/${toolId}/${collectionsUrlFragment}/${collectionId}/${exercisesUrlFragment}/${exerciseId}`,
      title: t('toExercise')
    }]}>
      <>

        <div className="tags">
          {parts.map(({id, name, solved}) =>
            <div key={id} className={classNames('tag', {'is-success': solved})}>{name}</div>
          )}
        </div>
        {topicsWithLevels.length > 0
          ? (
            <div className="tags">
              {topicsWithLevels.map(({topic, level}) =>
                <div className="tag" title={topic.title} key={topic.abbreviation}>
                  {topic.abbreviation}&nbsp; - &nbsp;
                  <FilledPoints filledPoints={level.levelIndex} maxPoints={/* TODO: topic.maxLevel.levelIndex */ 4}/>
                </div>)}
            </div>
          ) : (
            <div className="tag is-warning">Keine Tags vorhanden</div>
          )}
      </>
    </BulmaCard>
  );
}
