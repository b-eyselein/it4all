import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ReadObjectComponent} from './read-object.component';

describe('ReadObjectComponent', () => {
  let component: ReadObjectComponent<any>;
  let fixture: ComponentFixture<ReadObjectComponent<any>>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ReadObjectComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReadObjectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
