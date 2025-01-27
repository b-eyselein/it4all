import {StrictMode} from 'react';
import {App} from './App';
import reportWebVitals from './reportWebVitals';
import {Provider as ReduxProvider} from 'react-redux';
import i18next from 'i18next';
import {I18nextProvider, initReactI18next} from 'react-i18next';
import {ApolloProvider} from '@apollo/client';
import {BrowserRouter} from 'react-router-dom';
import {createRoot} from 'react-dom/client';
import {loadLanguageFromLocalStorage, store} from './store';
import {apolloClient} from './apolloClient';
import common_de from './locales/common_de.json';
import common_en from './locales/common_en.json';
import './index.css';

export type MyFunComponent<P = Record<string, never>> = (p: P) => JSX.Element;

i18next
  .use(initReactI18next)
  .init({
    lng: loadLanguageFromLocalStorage(),
    fallbackLng: 'en',
    resources: {
      de: {common: common_de},
      en: {common: common_en}
    }
  })
  .catch((err) => console.error('could not init i18n' + err));

const root = createRoot(
  document.getElementById('root') as HTMLElement
);

root.render(
  <StrictMode>
    <I18nextProvider i18n={i18next}>
      <ReduxProvider store={store}>
        <ApolloProvider client={apolloClient}>
          <BrowserRouter>
            <App/>
          </BrowserRouter>
        </ApolloProvider>
      </ReduxProvider>
    </I18nextProvider>
  </StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
