import { TestBed } from '@angular/core/testing';

import { LessonApiService } from './lesson-api.service';

describe('LessonApiService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: LessonApiService = TestBed.get(LessonApiService);
    expect(service).toBeTruthy();
  });
});
