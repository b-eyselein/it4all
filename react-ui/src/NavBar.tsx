import {useTranslation} from 'react-i18next';
import {Link} from 'react-router-dom';
import {homeUrl} from './urls';
import {changeLanguageAction, userLogoutAction} from './store/actions';
import {useDispatch, useSelector} from 'react-redux';
import {currentUserSelector} from './store/store';

const languages = ['de', 'en'];

export function NavBar(): JSX.Element {

  const {t} = useTranslation('common');
  const currentUser = useSelector(currentUserSelector);
  const dispatch = useDispatch();

  function changeLanguage(language: string): void {
    dispatch(changeLanguageAction(language));
  }

  return (
    <nav className="navbar is-dark" role="navigation" aria-label="main navigation">
      <div className="navbar-brand">
        <Link className="navbar-item" to={homeUrl}>it4all - &beta;</Link>
      </div>

      <div className="navbar-menu">
        <div className="navbar-end">
          <div className="navbar-item has-dropdown is-hoverable">
            <div className="navbar-link">{t('language')}</div>
            <div className="navbar-dropdown">
              {languages.map((lang) => <div key={lang} className="navbar-item" onClick={() => changeLanguage(lang)}>{lang}</div>)}
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
              ? (
                <button onClick={() => dispatch(userLogoutAction)} className="button is-light">
                  {t('logout')}&nbsp;{currentUser.username}
                </button>
              ) : (
                <div className="buttons">
                  <Link to="/loginForm" className="button is-light">{t('login')}</Link>
                  <Link to="/registerForm" className="button is-light">{t('register')}</Link>
                </div>
              )
            }
          </div>
        </div>
      </div>
    </nav>

  );
}
