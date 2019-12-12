import {Component} from '@angular/core';
import {User} from './_interfaces/user';
import {AuthenticationService} from './_services/authentication.service';
import {Title} from '@angular/platform-browser';

@Component({selector: 'it4all-root', templateUrl: './app.component.html'})
export class AppComponent {
  currentUser: User;

  constructor(private authenticationService: AuthenticationService, private  titleService: Title) {
    this.authenticationService.currentUser.subscribe((u: User) => this.currentUser = u);
  }

  public setTitle(newTitle: string): void {
    this.titleService.setTitle(newTitle);
  }

  logout() {
    this.authenticationService.logout();
  }

}
