import {Link} from 'react-router-dom';

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
    <nav className="my-3 breadcrumb" aria-label="breadcrumbs">
      <ul>
        {parents.map(({title, to}, index) => <li key={index}><Link to={to}>{title}</Link></li>)}
        <li className="is-active">
          <Link className="disabled" to="">{current}</Link>
        </li>
      </ul>
    </nav>
  );
}
