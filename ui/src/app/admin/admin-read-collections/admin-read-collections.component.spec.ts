import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AdminReadCollectionsComponent} from './admin-read-collections.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterTestingModule} from '@angular/router/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRoute} from '@angular/router';
import {ReadObjectComponent} from '../_components/read-object/read-object.component';

describe('AdminReadCollectionsComponent', () => {
  let component: AdminReadCollectionsComponent;
  let fixture: ComponentFixture<AdminReadCollectionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, ReactiveFormsModule, RouterTestingModule, HttpClientTestingModule],
      declarations: [AdminReadCollectionsComponent, ReadObjectComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {snapshot: {paramMap: new Map<string, string>([['toolId', 'web']])}}
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminReadCollectionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
