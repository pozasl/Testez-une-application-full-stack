import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { TeacherService } from './teacher.service';
import { Observable, Subscription } from 'rxjs';

describe('TeacherService', () => {
  let service: TeacherService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientModule
      ]
    });
    service = TestBed.inject(TeacherService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

});

describe('TeacherService with mocked http', () => {
  let service: TeacherService;
  let http: HttpClient;
  const now = new Date();
  const teacher1 = {
    id:1,
    lastName: "LeBricoleur",
    firstName: "Bob",
    createdAt: now,
    updatedAt: now
  }
  const teacher2 = {
    id:2,
    lastName: "InChain",
    firstName: "Alice",
    createdAt: now,
    updatedAt: now
  }
  let subs:Subscription[];

  beforeEach(()=> {
    subs = [];
    TestBed.configureTestingModule({
      providers:[
        { provide: HttpClient, useValue: { get: jest.fn() } },
      ]
    })
    service = TestBed.inject(TeacherService);
    http = TestBed.inject(HttpClient);    

  });

  afterEach(() => {
    subs.forEach(sub=>sub.unsubscribe());
  });

  it('all should return an observable of Teacher collection', (done) => {
    http.get = jest.fn(() => new Observable<any>((obs)=> obs.next([teacher1, teacher2])));
    subs.push(service.all().subscribe((teachers)=> {
      expect(teachers.length).toBe(2);
      expect(teachers[0]).toBe(teacher1);
      expect(teachers[1]).toBe(teacher2);
      done();
    }))
  });

  it('detail should return an observable of Teacher', (done) => {
    http.get = jest.fn(() => new Observable<any>((obs) => obs.next(teacher1)));
    subs.push(service.detail("1").subscribe((teacher)=> {
      expect(teacher).toBe(teacher1);
      done();
    }))
  });

});
