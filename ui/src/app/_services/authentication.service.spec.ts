import {TestBed} from '@angular/core/testing';

import {AuthenticationService} from './authentication.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {AppRoutingModule, routingComponents} from '../app-routing.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

describe('AuthenticationService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [HttpClientTestingModule, FormsModule, ReactiveFormsModule, AppRoutingModule],
    declarations: [...routingComponents]
  }));

  it('should be created', () => {
    const service: AuthenticationService = TestBed.inject(AuthenticationService);
    expect(service).toBeTruthy();
  });
});
