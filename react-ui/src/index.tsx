import {StrictMode} from 'react';
import {App} from './App';
import reportWebVitals from './reportWebVitals';
import {Provider as ReduxProvider} from 'react-redux';
import i18next from 'i18next';
import {I18nextProvider, initReactI18next} from 'react-i18next';
import {ApolloClient, ApolloLink, ApolloProvider, concat, HttpLink, InMemoryCache} from '@apollo/client';
import {BrowserRouter} from 'react-router-dom';
import common_de from './locales/de/common.json';
import common_en from './locales/en/common.json';
import {createRoot} from 'react-dom/client';
import {newStore} from './newStore';
import {serverUrl} from './urls';
import './index.css';

i18next
  .use(initReactI18next)
  .init({
    lng: 'de',
    fallbackLng: 'en',
    resources: {
      de: {common: common_de},
      en: {common: common_en}
    }
  })
  .catch((err) => console.error('could not init i18n' + err));

const apolloAuthMiddleware = new ApolloLink((operation, forward) => {

  const currentUser = newStore.getState().user;

  const Authorization = currentUser
    ? `Bearer ${currentUser.jwt}`
    : undefined;

  operation.setContext({
    headers: {
      Authorization,
      Language: 'de' //chosenLanguageCodeSelector(store.getState()),
    }
  });

  return forward(operation);
});

const client = new ApolloClient({
  link: concat(
    apolloAuthMiddleware,
    new HttpLink({uri: `${serverUrl}/graphql`})
  ),
  cache: new InMemoryCache(),
  defaultOptions: {
    query: {fetchPolicy: 'no-cache'},
    watchQuery: {fetchPolicy: 'no-cache'},
    mutate: {fetchPolicy: 'no-cache'}
  }
});

const root = createRoot(
  document.getElementById('root') as HTMLElement
);

root.render(
  <StrictMode>
    <I18nextProvider i18n={i18next}>
      <ReduxProvider store={newStore}>
        <ApolloProvider client={client}>
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
