import {Link} from 'react-router-dom';
import {RandomTool} from './randomTools';
import {useTranslation} from 'react-i18next';

interface IProps {
  tool: RandomTool;
}

export function RandomToolOverview({tool: {name, parts}}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  return (
    <div className="container mx-auto">
      <h1 className="mb-4 font-bold text-2xl text-center">{t('tool')} {name}</h1>

      {parts.filter((p) => !p.disabled).map(({id, name}) =>
        <Link to={id} key={id} className="mt-2 p-2 block rounded bg-blue-500 text-white text-center w-full">{name}</Link>
      )}
    </div>
  );
}
