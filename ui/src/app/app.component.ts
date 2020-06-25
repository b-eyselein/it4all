import {Component} from '@angular/core';
import {AuthenticationService} from './_services/authentication.service';
import {LoggedInUserWithTokenFragment} from './_services/apollo_services';

@Component({
  selector: 'it4all-root',
  templateUrl: './app.component.html'
})
export class AppComponent {

  currentUser: LoggedInUserWithTokenFragment;

  constructor(private authenticationService: AuthenticationService) {
    this.authenticationService.currentUser.subscribe((u) => this.currentUser = u);
  }

  logout(): void {
    this.authenticationService.logout();
  }

}
