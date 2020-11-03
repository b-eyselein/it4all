import {Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthenticationService} from '../_services/authentication.service';

@Component({template: ''})
export class LtiComponent {

  // TODO: eventually render template?

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authenticationService: AuthenticationService
  ) {
    const uuid: string = this.route.snapshot.paramMap.get('uuid');

    this.authenticationService.claimJsonWebToken(uuid)
      .subscribe(() => this.router.navigate(['/']));
  }

}
