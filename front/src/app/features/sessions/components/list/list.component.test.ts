import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { expect } from '@jest/globals';

import { SessionService } from 'src/app/services/session.service';
import { ListComponent } from './list.component';
import { SessionApiService } from '../../services/session-api.service';
import { Observable } from 'rxjs';
import { Router, Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

class Dummy {};

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;
  let sessionApiService: SessionApiService;
  let router: Router;

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

    it("Create Button should be visible", () => {
      const createBtn = fixture.nativeElement.querySelector('button[ng-reflect-router-link="create"]');
      expect(createBtn).not.toBeNull();
      expect(createBtn?.textContent).toContain("Create");
    });
  
    it('should display the sessions\' list with detail and edit buttons', () => {
      const matCardItemElements: HTMLElement[] = fixture.nativeElement.querySelectorAll('mat-card.item');
      expect(matCardItemElements.length).toBe(2);
      expect(matCardItemElements[0].querySelector("mat-card-title")?.textContent).toContain(sessions[0].name);
      expect(matCardItemElements[0].querySelector("mat-card-subtitle")?.textContent).toContain("Session on " + date3Str);
      expect(matCardItemElements[0].querySelector("mat-card-subtitle")?.textContent).toContain("Session on " + date3Str);
      expect(matCardItemElements[0].querySelector('button[ng-reflect-router-link="detail,' + sessions[0].id + '"]')?.textContent).toContain("Detail");
      expect(matCardItemElements[0].querySelector('button[ng-reflect-router-link="update,' + sessions[0].id + '"]')?.textContent).toContain("Edit");
    });

  });

});
