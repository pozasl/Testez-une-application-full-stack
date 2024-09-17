import { HttpClient, HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';
import { Session } from '../interfaces/session.interface';
import { Observable, Subscription } from 'rxjs';

describe('SessionsService', () => {
  let service: SessionApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[
        HttpClientModule
      ]
    });
    service = TestBed.inject(SessionApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

describe('SessionsService with mocked http', () => {
  let subs: Subscription[];
  let service: SessionApiService;
  let http: HttpClient;
  const now = new Date();
  const session1 = {
    id: 1,
    name: "Session 1",
    description: "Une session de test",
    date: now,
    teacher_id: 1,
    users: [1,2,3,4],
    createdAt: now,
    updatedAt:now
  };
  const session2 = {
    id: 2,
    name: "Session 2",
    description: "Une autre session de test",
    date: now,
    teacher_id: 2,
    users: [1,2,3],
    createdAt: now,
    updatedAt:now
  };

  beforeEach(() => {
    subs = [];
    TestBed.configureTestingModule({
      providers:[
        { provide: HttpClient,
          useValue: {get: jest.fn(), post: jest.fn(), put: jest.fn(), delete: jest.fn()}
        }
      ]
    });
    service = TestBed.inject(SessionApiService);
    http = TestBed.inject(HttpClient);
  });

  afterEach(() => {
    subs.forEach(sub=>sub.unsubscribe());
  });

  it('all should return an observable of a session collection', (done) => {
    http.get = jest.fn(()=> new Observable<any>((obs) => obs.next([session1,session2])));
    subs.push(service.all().subscribe((sessions) => {
      expect(sessions.length).toBe(2);
      expect(sessions[0]).toBe(session1);
      expect(sessions[1]).toBe(session2);
      done();
    }))
  });

  it('detail should return an observable of a session', (done) => {
    http.get = jest.fn(()=> new Observable<any>((obs) => obs.next(session1)));
    subs.push(service.detail("1").subscribe((session) => {
      expect(session).toBe(session1);
      done();
    }))
  });

  it('delete should return an observable of a session', (done) => {
    http.delete = jest.fn(()=> new Observable<any>((obs) => obs.next(session1)));
    subs.push(service.delete("1").subscribe((session) => {
      expect(session).toBe(session1);
      done();
    }))
  });

  it('create should return an observable of a session', (done) => {
    const newSession = Object({
      name: "Session 2",
      description: "Une autre session de test",
      date: now,
      teacher_id: 2,
      users: [1,2,3],
    }) as Session;
    http.post = jest.fn(()=> new Observable<any>((obs) => obs.next(session2)));
    subs.push(service.create(newSession).subscribe((session) => {
      expect(session).toBe(session2);
      done();
    }))
  });

  it('update should return an observable of a session', (done) => {
    http.put = jest.fn(()=> new Observable<any>((obs) => obs.next(session2)));
    const userUpdated = {...session2} as Session;
    subs.push(service.update("2", userUpdated).subscribe((session) => {
      expect(session).toBe(session2);
      done();
    }))
  });

  it('participate should subscribe user to a session', (done) => {
    http.post = jest.fn(()=> new Observable<any>((obs) => obs.next()));
    subs.push(service.participate("1", "2").subscribe(() => {
      done();
    }))
  });

  it('unparticipate should unsubscribe user to a session', (done) => {
    http.delete = jest.fn(()=> new Observable<any>((obs) => obs.next()));
    subs.push(service.unParticipate("1", "2").subscribe(() => {
      done();
    }))
  });

});
