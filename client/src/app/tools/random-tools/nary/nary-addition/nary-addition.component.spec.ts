import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NaryAdditionComponent } from './nary-addition.component';

describe('NaryAdditionComponent', () => {
  let component: NaryAdditionComponent;
  let fixture: ComponentFixture<NaryAdditionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NaryAdditionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NaryAdditionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});