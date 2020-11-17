import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ProgrammingNormalResultComponent } from './programming-normal-result.component';

describe('ProgrammingNormalResultComponent', () => {
  let component: ProgrammingNormalResultComponent;
  let fixture: ComponentFixture<ProgrammingNormalResultComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ProgrammingNormalResultComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProgrammingNormalResultComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
