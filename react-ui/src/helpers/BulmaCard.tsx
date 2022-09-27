import {Link} from 'react-router-dom';
import classNames from 'classnames';
import {linkColor} from '../consts';
import {useState} from 'react';

export interface FooterItem {
  title: string;
  link: string;
}

interface IProps {
  title: string | JSX.Element;
  reducible?: boolean;
  children: JSX.Element;
  footerItems?: FooterItem[];
}

export function NewCard({title, reducible, children, footerItems}: IProps): JSX.Element {

  const [reduced, setReduced] = useState(false);

  function toggleReduction() {
    if (reducible) {
      setReduced((value) => !value);
    }
  }

  return (
    <div>
      <header className="px-4 py-2 border border-slate-200 font-bold shadow-sm" onClick={reducible ? toggleReduction : undefined}>
        {reducible && (reduced ? <span>&#65310;&nbsp;</span> : <span>&#8744;&nbsp;</span>)}
        {title}
      </header>

      {!reduced && <div className={classNames('p-4', 'border-x', 'border-slate-200', {'border-b': !footerItems})}>
        {children}
      </div>}

      {footerItems && <footer className="px-4 py-2 border border-slate-200 text-center shadow-sm flex">
        {footerItems.map(({link, title}) => <Link key={link} to={link} className={classNames(linkColor, 'flex-grow')}>{title}</Link>)}
      </footer>}
    </div>
  );
}
