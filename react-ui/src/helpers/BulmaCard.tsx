import {Link} from 'react-router-dom';

export interface FooterItem {
  title: string;
  link: string;
}

interface IProps {
  title: string | JSX.Element;
  children: JSX.Element;
  footerItems?: FooterItem[];
}

/**
 * @deprecated
 *
 * @param title
 * @param children
 * @param footerItems
 * @constructor
 */
export function BulmaCard({title, children, footerItems}: IProps): JSX.Element {
  return (
    <div className="card">
      <header className="card-header">
        <div className="card-header-title">{title}</div>
      </header>
      <div className="card-content">
        {children}
      </div>
      {footerItems && <footer className="card-footer">
        {footerItems.map(({link, title}) => <Link key={link} className="card-footer-item" to={link}>{title}</Link>)}
      </footer>}
    </div>
  );
}

export function NewCard({title, children, footerItems}: IProps): JSX.Element {
  return (
    <div className="card">

      <header className="px-4 py-2 border border-slate-200 font-bold shadow-sm">{title}</header>

      <div className="p-4 border-x border-slate-200">
        {children}
      </div>

      {footerItems && <footer className="px-4 py-2 border border-slate-200 text-center shadow-sm">
        {footerItems.map(({link, title}) => <Link key={link} to={link} className="text-blue-700">{title}</Link>)}
      </footer>}
    </div>
  );
}
