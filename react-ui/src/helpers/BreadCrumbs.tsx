import {Link} from 'react-router-dom';
import {linkColor} from '../consts';
import classNames from 'classnames';

export interface BreadCrumb {
  title: string;
  to: string;
}

interface IProps {
  parents: BreadCrumb[];
  current: string;
}

export function BreadCrumbs({parents, current}: IProps): JSX.Element {
  return (
    <nav className="my-4" aria-label="breadcrumbs">
      {parents.map(({title, to}, index) =>
        <span className="mr-2" key={index}><Link to={to} className={classNames('mr-2', linkColor)}>{title}</Link> / </span>
      )}
      <span className="is-active">{current}</span>
    </nav>
  );
}
