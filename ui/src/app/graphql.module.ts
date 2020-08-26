import {NgModule} from '@angular/core';
import {APOLLO_OPTIONS, ApolloModule} from 'apollo-angular';
import {HttpLink, HttpLinkModule} from 'apollo-angular-link-http';
import {InMemoryCache, IntrospectionFragmentMatcher} from 'apollo-cache-inmemory';
import possibleTypes from '../introspection-result';
import {DefaultOptions} from 'apollo-client';

const uri = '/api/graphql'; // <-- add the URL of the GraphQL server here

const fragmentMatcher = new IntrospectionFragmentMatcher({
  introspectionQueryResultData: possibleTypes
});

const defaultOptions: DefaultOptions = {
  watchQuery: {
    fetchPolicy: 'no-cache'
  },
  query: {
    fetchPolicy: 'no-cache'
  }
};

function createApollo(httpLink: HttpLink) {
  return {
    link: httpLink.create({uri}),
    cache: new InMemoryCache({/*addTypename: false,*/ fragmentMatcher}),
    defaultOptions
  };
}

@NgModule({
  exports: [ApolloModule, HttpLinkModule],
  providers: [
    {
      provide: APOLLO_OPTIONS,
      useFactory: createApollo,
      deps: [HttpLink],
    },
  ],
})
export class GraphQLModule {
}
