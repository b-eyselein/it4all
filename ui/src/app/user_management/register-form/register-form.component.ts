import {Component, OnInit} from '@angular/core';
import {RegisterGQL} from '../../_services/apollo_services';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthenticationService} from '../../_services/authentication.service';
import {first} from 'rxjs/operators';

@Component({templateUrl: './register-form.component.html'})
export class RegisterFormComponent implements OnInit {

  registerForm: FormGroup;
  loading = false;
  submitted = false;

  registeredUsername: string | undefined;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private registerGQL: RegisterGQL,
    private authenticationService: AuthenticationService
  ) {

    if (this.authenticationService.currentUserValue) {
      // noinspection JSIgnoredPromiseFromCall
      this.router.navigate(['/']);
    }
  }

  ngOnInit(): void {
    this.registerForm = this.formBuilder.group({
      username: ['', Validators.required],
      firstPassword: ['', Validators.required],
      secondPassword: ['', Validators.required],
    });
  }

  private get f() {
    return this.registerForm.controls;
  }

  onSubmit() {
    this.submitted = true;

    const username = this.f.username.value;
    const firstPassword = this.f.firstPassword.value;
    const secondPassword = this.f.secondPassword.value;

    if (this.registerForm.invalid || firstPassword !== secondPassword) {
      alert('Daten sind nicht valide!');
      return;
    }

    this.loading = true;

    this.registerGQL
      .mutate({username, firstPassword, secondPassword})
      .pipe(first())
      .subscribe(
        ({data}) => {
          this.loading = false;
          this.registeredUsername = data.register;
        },
        (err) => this.loading = false);
  }

}
