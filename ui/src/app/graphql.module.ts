import {APOLLO_OPTIONS} from 'apollo-angular';
import {HttpLink} from 'apollo-angular/http';
import {ApolloClientOptions, ApolloLink, InMemoryCache, NormalizedCacheObject} from '@apollo/client/core';
import {NgModule} from '@angular/core';
import {HttpClientModule} from "@angular/common/http";
import {BrowserModule} from "@angular/platform-browser";
import {setContext} from "@apollo/client/link/context";
import {getCurrentUser} from "./_services/authentication.service";
import {LoggedInUserWithTokenFragment} from "./_services/apollo_services";

function createApollo(httpLink: HttpLink): ApolloClientOptions<NormalizedCacheObject> {

  const auth = setContext(() => {
    const loggedInUser: LoggedInUserWithTokenFragment | null = getCurrentUser();

    if (loggedInUser === null) {
      return {};
    } else {
      return {
        headers: {
          Authorization: loggedInUser.jwt
        }
      }
    }
  });

  return {
    link: ApolloLink.from([auth, httpLink.create({uri: '/api/graphql'})]),
    cache: new InMemoryCache(),
    defaultOptions: {
      watchQuery: {fetchPolicy: 'no-cache'},
      query: {fetchPolicy: 'no-cache'},
      mutate: {fetchPolicy: 'no-cache'}
    }
  };
}

@NgModule({
  imports: [BrowserModule, HttpClientModule],
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
