import {Link} from 'react-router-dom';
import {RandomTool} from './randomTools';
import {useTranslation} from 'react-i18next';

interface IProps {
  tool: RandomTool;
}

export function RandomToolOverview({tool: {name, parts}}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  return (
    <div className="container">
      <h1 className="title is-3 has-text-centered">{t('tool')} {name}</h1>

      <div className="buttons">
        {parts.filter((p) => !p.disabled).map(({id, name}) =>
          <Link to={id} key={id} className="button is-link is-fullwidth">{name}</Link>
        )}
      </div>
    </div>
  );
}
