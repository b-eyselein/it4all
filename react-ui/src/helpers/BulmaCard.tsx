import React from "react";
import {Link} from "react-router-dom";

export interface FooterItem {
  title: string;
  link: string;
}

interface IProps {
  title: string | JSX.Element;
  children: () => JSX.Element;
  footerItems?: FooterItem[];
}

export function BulmaCard({title, children, footerItems}: IProps): JSX.Element {
  return <div className="card">
    <header className="card-header">
      <div className="card-header-title">{title}</div>
    </header>
    <div className="card-content">
      {children()}
    </div>
    {footerItems && <footer className="card-footer">
      {footerItems.map(({link, title}) => <Link key={link} className="card-footer-item" to={link}>{title}</Link>)}
    </footer>}
  </div>
}
