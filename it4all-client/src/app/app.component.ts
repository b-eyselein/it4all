import {Component} from '@angular/core';
import {User} from './_interfaces/user';
import {AuthenticationService} from './_services/authentication.service';

@Component({selector: 'it4all-root', templateUrl: './app.component.html'})
export class AppComponent {
  currentUser: User;

  constructor(private authenticationService: AuthenticationService) {
    this.authenticationService.currentUser.subscribe((u: User) => this.currentUser = u);
  }

  logout() {
    this.authenticationService.logout();
  }

}
