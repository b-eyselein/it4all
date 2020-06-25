import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {AuthenticationService} from '../../_services/authentication.service';
import {first} from 'rxjs/operators';

@Component({templateUrl: './login-form.component.html'})
export class LoginFormComponent implements OnInit {

  loginForm: FormGroup;
  loading = false;
  submitted = false;
  inValid = false;
  returnUrl: string;

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authenticationService: AuthenticationService
  ) {

    if (this.authenticationService.currentUserValue) {
      // noinspection JSIgnoredPromiseFromCall
      this.router.navigate(['/']);
    }

  }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });

    this.returnUrl = this.route.snapshot.queryParams.returnUrl || '/';
  }

  get f() {
    return this.loginForm.controls;
  }

  onSubmit() {
    this.submitted = true;

    const username = this.f.username.value;
    const password = this.f.password.value;

    if (this.loginForm.invalid) {
      alert('Daten sind nicht valide!');
      return;
    }

    this.loading = true;

    this.authenticationService.login(username, password)
      .pipe(first())
      .subscribe(
        (data) => {
          if (data) {
            // noinspection JSIgnoredPromiseFromCall
            this.router.navigate([this.returnUrl]);
          } else {
            this.inValid = true;
          }
        },
        () => this.loading = false
      );
  }

}
