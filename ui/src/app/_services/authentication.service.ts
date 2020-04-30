import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {map, tap} from 'rxjs/operators';
import {Router} from '@angular/router';
import {LoggedInUserWithTokenFragment, LoginGQL} from "./apollo_services";

@Injectable({providedIn: 'root'})
export class AuthenticationService {

  private currentUserSubject: BehaviorSubject<LoggedInUserWithTokenFragment>;
  public currentUser: Observable<LoggedInUserWithTokenFragment>;

  constructor(private http: HttpClient, private loginGQL: LoginGQL, private router: Router) {
    this.currentUserSubject = new BehaviorSubject<LoggedInUserWithTokenFragment>(JSON.parse(localStorage.getItem('currentUser')));
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): LoggedInUserWithTokenFragment {
    return this.currentUserSubject.value;
  }

  private activateLogin(user: LoggedInUserWithTokenFragment): void {
    localStorage.setItem('currentUser', JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  login(username: string, password: string): Observable<LoggedInUserWithTokenFragment | null> {
    return this.loginGQL
      .mutate({username, password})
      .pipe(map(({data}) => {
        if (data.login) {
          this.activateLogin(data.login);
        }

        return data.login;
      }));
  }

  claimJsonWebToken(uuid: string): Observable<LoggedInUserWithTokenFragment> {
    return this.http.get<LoggedInUserWithTokenFragment | undefined>(`/api/claimWebToken/${uuid}`)
      .pipe(tap((user) => this.activateLogin(user)));
  }

  logout() {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
    // noinspection JSIgnoredPromiseFromCall
    this.router.navigate(['/loginForm']);
  }
}
