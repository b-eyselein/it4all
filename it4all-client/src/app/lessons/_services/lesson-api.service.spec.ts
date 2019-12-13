import {TestBed} from '@angular/core/testing';

import {LessonApiService} from './lesson-api.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('LessonApiService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [HttpClientTestingModule],
    providers: [LessonApiService]
  }));

  it('should be created', () => {
    const service: LessonApiService = TestBed.get(LessonApiService);
    expect(service).toBeTruthy();
  });
});
