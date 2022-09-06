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

  const cardTitle = (
    <>
      {exerciseId}. {title}
      &nbsp;
      <div className="tag" title={t('difficulty_{{title}}', {title: difficulty.title})}>
        <FilledPoints filledPoints={difficulty.levelIndex}/>
      </div>
    </>
  );

  const footerItems = [{
    link: `/${toolsUrlFragment}/${toolId}/${collectionsUrlFragment}/${collectionId}/${exercisesUrlFragment}/${exerciseId}`,
    title: t('toExercise')
  }];

  return (
    <BulmaCard title={cardTitle} footerItems={footerItems}>
      <>
        <div className="tags">
          {parts.length === 0
            ? <div>TODO!</div>
            : parts.map(({id, name, solved}) => <div key={id} className={classNames('tag', {'is-success': solved})}>{name}</div>)}
        </div>

        {topicsWithLevels.length > 0 && <div className="tags">
          {topicsWithLevels.map(({topic, level}) =>
            <div className="tag" title={topic.title} key={topic.abbreviation}>
              {topic.abbreviation}&nbsp; - &nbsp;
              <FilledPoints filledPoints={level.levelIndex}/>
            </div>)}
        </div>}
      </>
    </BulmaCard>
  );
}
