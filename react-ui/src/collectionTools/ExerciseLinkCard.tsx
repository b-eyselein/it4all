import {FilledPoints} from '../helpers/FilledPoints';
import {NewCard} from '../helpers/BulmaCard';
import {FieldsForLinkFragment} from '../graphql';
import {collectionsUrlFragment, exercisesUrlFragment, toolsUrlFragment} from '../urls';
import {useTranslation} from 'react-i18next';
import {Tag} from '../helpers/Tag';
import {correctBgColor} from '../consts';

interface IProps {
  exercise: FieldsForLinkFragment;
  showCollection?: boolean;
}

export function ExerciseLinkCard({exercise, showCollection}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  const {toolId, collectionId, exerciseId, title, parts, difficulty, topicsWithLevels} = exercise;

  const cardTitle = (
    <>
      {showCollection ? collectionId + '.' : ''}{exerciseId} {title}&nbsp;<FilledPoints filledPoints={difficulty.levelIndex}
                                                                                   title={t('difficulty_{{title}}', {title: difficulty.title})}/>
    </>
  );

  const footerItems = [{
    link: `/${toolsUrlFragment}/${toolId}/${collectionsUrlFragment}/${collectionId}/${exercisesUrlFragment}/${exerciseId}`,
    title: t('toExercise')
  }];

  return (
    <NewCard title={cardTitle} footerItems={footerItems}>
      <>
        <div className="grid grid-cols-2 gap-2">
          {parts.length === 0
            ? <span>&nbsp;</span>
            : parts.map(({id, name, solved}) => <Tag key={id} otherClasses={{[correctBgColor]: solved}}>{name}</Tag>)}
        </div>

        {topicsWithLevels.length > 0 && <div className="grid grid-cols-3 gap-2">
          {topicsWithLevels.map(({topic, level}) => <Tag key={topic.abbreviation} title={topic.title}>
            <>{topic.abbreviation}&nbsp; - &nbsp;<FilledPoints filledPoints={level.levelIndex}/></>
          </Tag>)}
        </div>}
      </>
    </NewCard>
  );
}
