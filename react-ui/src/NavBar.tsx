import {useTranslation} from 'react-i18next';
import {NavLink} from 'react-router-dom';
import {homeUrl, loginUrl, registerUrl} from './urls';
import {useDispatch, useSelector} from 'react-redux';
import {changeLanguage, languages, logout, newCurrentUserSelector, newLanguageSelector} from './store';
import classNames from 'classnames';

export function NavBar(): JSX.Element {

  const {t} = useTranslation('common');
  const dispatch = useDispatch();
  const currentUser = useSelector(newCurrentUserSelector);
  const currentLang = useSelector(newLanguageSelector);

  return (
    <nav className="mb-4 flex bg-slate-600 text-white" role="navigation" aria-label="main navigation">
      <NavLink className="p-4 font-bold hover:bg-slate-400" to={homeUrl}>it4all - &beta;</NavLink>

      <div className="flex-grow"/>

      <div className="py-4 pr-2">{t('language')}:</div>
      {languages.map((lang) => <button key={lang} className={classNames('py-4', 'px-2', 'hover:bg-slate-400', {'font-bold': currentLang === lang})}
                                       onClick={() => dispatch(changeLanguage(lang))}>{lang}</button>)}

      <a className="p-4 hover:bg-slate-400" target="_blank" href="https://www.uni-wuerzburg.de/sonstiges/impressum/" rel="noreferrer">
        {t('imprint')}
      </a>
      <a className="p-4 hover:bg-slate-400" target="_blank" href="https://www.uni-wuerzburg.de/sonstiges/datenschutz/" rel="noreferrer">
        {t('dataProtection')}
      </a>

      {currentUser
        ? <button className="p-4 hover:bg-slate-400" onClick={() => dispatch(logout())}>{t('logout')}&nbsp;{currentUser.sub}</button>
        : (
          <>
            <NavLink to={loginUrl} className="p-4 hover:bg-slate-400">{t('login')}</NavLink>
            <NavLink to={registerUrl} className="p-4 hover:bg-slate-400">{t('register')}</NavLink>
          </>
        )
      }
    </nav>
  );
}
