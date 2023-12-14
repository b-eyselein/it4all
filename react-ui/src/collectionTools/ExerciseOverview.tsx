import { ExOverviewToolFragment, useExerciseOverviewQuery } from '../graphql';
import { Link, Navigate } from 'react-router-dom';
import { BreadCrumbs } from '../helpers/BreadCrumbs';
import { WithQuery } from '../WithQuery';
import { useTranslation } from 'react-i18next';
import { exercisesBreadCrumbs, homeUrl, loginUrl } from '../urls';
import { WithCurrentUser } from '../WithCurrentUser';
import { bgColors } from '../consts';
import classNames from 'classnames';

interface InnerProps {
  toolId: string;
  collectionId: number;
  exerciseId: number;
  tool: ExOverviewToolFragment;
}

function Inner({ toolId, collectionId, exerciseId, tool }: InnerProps): JSX.Element {

  const { t } = useTranslation('common');

  if (!tool.collection.exercise) {
    return <Navigate to={homeUrl} />;
  }

  const { title, text, parts } = tool.collection.exercise;

  const noLoginOption = (
    <div className="p-2 rounded bg-cyan-500 text-white text-center">
      <Link to={loginUrl}>{t('pleaseLogin')}</Link>
    </div>
  );

  return (
    <>
      <h1 className="mb-4 font-bold text-2xl text-center">{t('exercise_{{title}}', { title })}</h1>

      <BreadCrumbs parents={exercisesBreadCrumbs(toolId, tool.name, collectionId, tool.collection.title, t)} current={exerciseId.toString()} />

      <div className="my-4 p-4 rounded bg-slate-200" dangerouslySetInnerHTML={{ __html: text }} />

      <WithCurrentUser noLoginOption={noLoginOption}>
        {() => <div className="text-center">
          {parts.length === 0
            ? (
              <Link className={classNames('mx-2', 'p-2', 'rounded', /*solved ? bgColors.correct :*/ 'bg-blue-500', 'text-white', 'text-center')} to="./solve">
                {t('solve')}
              </Link>
            ) : (
              <>
                {parts.filter(({ isEntryPart }) => isEntryPart).map(({ id, name, solved }) =>
                  <Link key={id} to={`./parts/${id}`} title={solved ? t('partAlreadySolved') : undefined}
                    className={classNames('mx-2', 'p-2', 'rounded', solved ? bgColors.correct : 'bg-blue-500', 'text-white', 'text-center')}>
                    {name}{solved && <span> &#10004;</span>}
                  </Link>
                )}
              </>
            )}
        </div>}
      </WithCurrentUser>
    </>
  );
}

interface IProps {
  toolId: string;
  collectionId: number;
  exerciseId: number;
}

export function ExerciseOverview({ toolId, collectionId, exerciseId }: IProps): JSX.Element {

  const exerciseOverviewQuery = useExerciseOverviewQuery({ variables: { toolId, collectionId, exerciseId } });

  return (
    <div className="container mx-auto">
      <WithQuery query={exerciseOverviewQuery}>
        {({ tool }) =>
          tool
            ? <Inner toolId={toolId} collectionId={collectionId} exerciseId={exerciseId} tool={tool} />
            : <div>TODO!</div>}
      </WithQuery>
    </div>
  );
}
