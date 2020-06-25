import {BrowserModule, Title} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {AppRoutingModule, routingComponents} from './app-routing.module';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ErrorInterceptor} from './_helpers/error.interceptor';
import {CodemirrorModule} from '@ctrl/ngx-codemirror';
import {RandomToolsModule} from './tools/random-tools/random-tools.module';
import {SharedModule} from './shared/shared.module';
import {CollectionToolsModule} from './tools/collection-tools/collection-tools.module';
import {GraphQLModule} from './graphql.module';


@NgModule({
  declarations: [
    AppComponent,
    routingComponents,
  ],
  imports: [
    BrowserModule, HttpClientModule, AppRoutingModule, FormsModule, ReactiveFormsModule,
    CodemirrorModule,

    // Own Modules
    SharedModule,
    CollectionToolsModule,
    RandomToolsModule,
    GraphQLModule
  ],
  providers: [
    Title,
    {provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
