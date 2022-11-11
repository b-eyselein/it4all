import {ApolloClient, ApolloLink, concat, HttpLink, InMemoryCache} from '@apollo/client';
import {store} from './store';
import {serverUrl} from './urls';

const apolloAuthMiddleware = new ApolloLink((operation, forward) => {
  const token = store.getState().user.user?.token;

  operation.setContext({
    headers: {
      Authorization: token ? `Bearer ${token}` : undefined,
    }
  });

  return forward(operation);
});

export const apolloClient = new ApolloClient({
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
