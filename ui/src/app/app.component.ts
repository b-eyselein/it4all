import {Component} from '@angular/core';
import {AuthenticationService} from './_services/authentication.service';
import {LoggedInUserWithTokenFragment} from './_services/apollo_services';
import {Router} from "@angular/router";

@Component({
  selector: 'it4all-root',
  templateUrl: './app.component.html'
})
export class AppComponent {

  readonly langs: string[] = ["de", "en"];

  currentUser: LoggedInUserWithTokenFragment;

  constructor(private router: Router, private authenticationService: AuthenticationService) {
    this.authenticationService.currentUser.subscribe((u) => this.currentUser = u);
  }

  logout(): void {
    this.authenticationService.logout();
  }

  getCurrentUrl(): string {
    return this.router.url;
  }

}
