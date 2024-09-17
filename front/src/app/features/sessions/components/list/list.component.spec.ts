import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { expect } from '@jest/globals';

import { SessionService } from 'src/app/services/session.service';
import { ListComponent } from './list.component';
import { SessionApiService } from '../../services/session-api.service';
import { Observable, Subscription } from 'rxjs';
import { Router, Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

class Dummy {};

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;
  let sessionApiService: SessionApiService;
  let router: Router;
  let subs: Subscription[];

  const date1 = new Date(2024,7,10);
  const date2 = new Date(2024,7,23);
  const date3 = new Date(2024,8,21);
  const date3Str = "September 21, 2024";
  const sessions = [
    {
      id: 1,
      name: "Session 1",
      description: "Une session de test",
      date: date3,
      teacher_id: 1,
      users: [1,2,3,4],
      createdAt: date1,
      updatedAt: date2
    },
    {
      id: 2,
      name: "Session 2",
      description: "Une autre session de test",
      date: date3,
      teacher_id: 2,
      users: [1,2,3],
      createdAt: date1,
      updatedAt: date2
    }
  ]
  const routes: Routes = [
    {path: 'sessions/create', component: Dummy},
    {path: 'sessions/update/:id', component: Dummy},
  ];

  describe('as Admin', () => {
    const mockSessionService = {
      sessionInformation: {
        admin: true
      }
    };

    beforeEach(async () => {
      subs = [];
      await TestBed.configureTestingModule({
        declarations: [ListComponent],
        imports: [HttpClientModule, MatCardModule, MatIconModule,
          RouterTestingModule.withRoutes(routes)],
        providers: [
          { provide: SessionService, useValue: mockSessionService },
        ]
      })
        .compileComponents();
      router = TestBed.inject(Router);
      sessionApiService = TestBed.inject(SessionApiService);
      sessionApiService.all = jest.fn(()=> new Observable(obs => obs.next(sessions)))
      fixture = TestBed.createComponent(ListComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    afterEach(() => {
      subs.forEach(sub=>sub.unsubscribe());
    });
  
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it("should fetch sessions at initialization", () => {
      const allSessionSpy = jest.spyOn(sessionApiService, "all")
      expect(allSessionSpy).toBeCalled();
    });
  
    it('sessions should return an Observable of sessions ', (done) => {
      subs.push(component.sessions$.subscribe((sessionList) => {
        expect(sessionList).toBe(sessions);
        done();
      }))
    });

    it("get user should return sessionInformation", () => {
      const allSessionSpy = jest.spyOn(sessionApiService, "all")
      expect(component.user).toBe(mockSessionService.sessionInformation);
    });

  });

});
