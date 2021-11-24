import React, {Dispatch} from 'react';
import {Link, Route, Routes} from 'react-router-dom';
import {useTranslation} from 'react-i18next';
import {Home} from './Home';
import {LoginForm} from './LoginForm';
import {ToolBase} from './ToolBase';
import {RandomToolsBase} from './randomTools/RandomToolBase';
import {currentUserSelector} from './store/store';
import {useDispatch, useSelector} from 'react-redux';
import {changeLanguageAction, StoreAction, userLogoutAction} from './store/actions';
import {homeUrl, randomToolsUrlFragment, toolsUrlFragment} from './urls';
import {ClaimLti} from './ClaimLti';

export function App(): JSX.Element {

  const {t} = useTranslation('common');
  const currentUser = useSelector(currentUserSelector);
  const dispatch = useDispatch<Dispatch<StoreAction>>();

  const langs = ['de', 'en'];

  function changeLanguage(language: string): void {
    // console.error('TODO: change language to ' + language);
    // TODO: check if working...
    dispatch(changeLanguageAction(language));
  }

  return (
    <>
      <nav className="navbar is-dark" role="navigation" aria-label="main navigation">
        <div className="navbar-brand">
          <Link className="navbar-item" to={homeUrl}>it4all - &beta;</Link>
        </div>

        <div className="navbar-menu">
          <div className="navbar-end">
            <div className="navbar-item has-dropdown is-hoverable">
              <div className="navbar-link">{t('language')}</div>
              <div className="navbar-dropdown">
                {langs.map((lang) => <div key={lang} className="navbar-item" onClick={() => changeLanguage(lang)}>{lang}</div>)}
              </div>
            </div>

            <a className="navbar-item" target="_blank" href="https://www.uni-wuerzburg.de/sonstiges/impressum/" rel="noreferrer">
              {t('imprint')}
            </a>
            <a className="navbar-item" target="_blank" href="https://www.uni-wuerzburg.de/sonstiges/datenschutz/" rel="noreferrer">
              {t('dataProtection')}
            </a>

            <div className="navbar-item">
              {currentUser
                ? <button onClick={() => dispatch(userLogoutAction)} className="button is-light">
                  {t('logout')}&nbsp;{currentUser.loggedInUser.username}
                </button>
                : <div className="buttons">
                  <Link to="/loginForm" className="button is-light">{t('login')}</Link>
                  <Link to="/registerForm" className="button is-light">{t('register')}</Link>
                </div>
              }
            </div>
          </div>
        </div>
      </nav>

      <Routes>
        <Route path={homeUrl} element={<Home/>}/>
        <Route path={'/loginForm'} element={<LoginForm/>}/>
        <Route path={`/${toolsUrlFragment}/:toolId`} element={<ToolBase/>}/>
        <Route path={`/${randomToolsUrlFragment}`} element={<RandomToolsBase/>}/>
        <Route path={'/lti/:ltiUuid'} element={<ClaimLti/>}/>
      </Routes>
    </>
  );
}

