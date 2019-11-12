import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {TagComponent} from './tag.component';

describe('TagComponent', () => {
  let component: TagComponent;
  let fixture: ComponentFixture<TagComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TagComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TagComponent);
    component = fixture.componentInstance;
    component.tag = {title: 'Title', label: 'L'};
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
