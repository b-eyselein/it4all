import {Link} from 'react-router-dom';
import classNames from 'classnames';

export interface BreadCrumbPart {
  routerLinkPart: string;
  title: string;
}

interface BreadCrumb {
  routerLink: string;
  title: string;
}

interface IProps {
  parts: BreadCrumbPart[];
}

export function BreadCrumbs({parts}: IProps): JSX.Element {

  function breadCrumbs(): BreadCrumb[] {
    const partAggregator: string[] = [];

    return parts.map(({routerLinkPart, title}) => {
      partAggregator.push(routerLinkPart);

      return {routerLink: partAggregator.join('/'), title};
    });
  }

  return (
    <nav className="my-3 breadcrumb" aria-label="breadcrumbs">
      <ul>
        {breadCrumbs().map((part, index, arr) =>
          <li className={classNames({'is-active': index === arr.length - 1})} key={part.routerLink}>
            <Link to={part.routerLink}>{part.title}</Link>
          </li>
        )}
      </ul>
    </nav>
  );
}
