import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { StringSampleSolComponent } from './string-sample-sol.component';

describe('StringSampleSolComponent', () => {
  let component: StringSampleSolComponent;
  let fixture: ComponentFixture<StringSampleSolComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ StringSampleSolComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StringSampleSolComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
