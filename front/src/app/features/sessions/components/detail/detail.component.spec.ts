import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { RouterTestingModule, } from '@angular/router/testing';
import { expect } from '@jest/globals'; 
import { Observable } from 'rxjs';

import { SessionService } from '../../../../services/session.service';
import { DetailComponent } from './detail.component';
import { SessionApiService } from '../../services/session-api.service';
import { ActivatedRoute, Router } from '@angular/router';
import { TeacherService } from 'src/app/services/teacher.service';


class MatSnackBarStub{
  open(){
    return {
      // Can't wait 3 seconds for a test
      onAction: () => new Observable((obs) => obs.next())
    };
  }
}

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>; 
  let sessionApiService: SessionApiService;
  let teacherService: TeacherService;
  let router: Router;
  let snackBar: MatSnackBar;

  const date1 =  new Date(2024, 7, 30);
  const dateString1 =  "August 30, 2024";
  const date2 =  new Date(2024, 8, 6);
  const dateString2 =  "September 6, 2024";
  const date3 =  new Date(2024, 9, 15);
  const dateString3 =  "October 15, 2024";

  const teacher1 = {
    id:1,
    lastName: "LeBricoleur",
    firstName: "Bob",
    createdAt: date1,
    updatedAt: date1
  }

  describe('as admin', () => {
    const mockSessionService = {
      sessionInformation: {
        admin: true,
        id: 1
      }
    }

    const mockRoute = {
      snapshot: {
        paramMap: {
          get: jest.fn(() => '2')
        }
      }
    }

    const session2 = {
      id: 2,
      name: "Session 2",
      description: "Une session de test",
      date: date3,
      teacher_id: 1,
      users: [1,2,3,4],
      createdAt: date1,
      updatedAt: date2
    };

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [
          RouterTestingModule,
          HttpClientModule,
          MatIconModule,
          MatCardModule,
          MatSnackBarModule,
          ReactiveFormsModule,
          
        ],
        declarations: [DetailComponent], 
        providers: [{ provide: SessionService, useValue: mockSessionService },
          { provide: ActivatedRoute, useValue: mockRoute},
          { provide: MatSnackBar, useClass: MatSnackBarStub },
          { provide: Router, useValue: {navigate: jest.fn()} },
        ],
      })
        .compileComponents();
      sessionApiService = TestBed.inject(SessionApiService);
      sessionApiService.detail = jest.fn(()=>new Observable((obs) => obs.next(session2)))
      teacherService = TestBed.inject(TeacherService);
      teacherService.detail = jest.fn(()=>new Observable(obs=>obs.next(teacher1)))
      router = TestBed.inject(Router);
      snackBar = TestBed.inject(MatSnackBar);
      fixture = TestBed.createComponent(DetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
      
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should fetch the yoga session datas at initialization', () => {
      const sessionApiSpy = jest.spyOn(sessionApiService, "detail");
      component.ngOnInit();
      fixture.detectChanges();
      expect(sessionApiSpy).toBeCalledWith('2');
      expect(component.session).toBe(session2);
      expect(component.sessionId).toBe('2');
      expect(component.teacher).toBe(teacher1);
      expect(component.isParticipate).toBe(true);
    });

  });

  describe('as user', () => {

    const mockSessionService = {
      sessionInformation: {
        admin: false,
        id: 5
      }
    }

    const mockRoute = {
      snapshot: {
        paramMap: {
          get: jest.fn(() => '3')
        }
      }
    }

    const session3 = {
      id: 3,
      name: "Session 3",
      description: "Une session de test",
      date: date3,
      teacher_id: 1,
      users: [1,2,3,4],
      createdAt: date1,
      updatedAt: date2
    };

    const session4 = {
      id: 3,
      name: "Session 3",
      description: "Une session de test",
      date: date3,
      teacher_id: 1,
      users: [1,2,3,4,5],
      createdAt: date1,
      updatedAt: date2
    };

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [
          RouterTestingModule,
          HttpClientModule,
          MatIconModule,
          MatCardModule,
          MatSnackBarModule,
          ReactiveFormsModule,
          
        ],
        declarations: [DetailComponent], 
        providers: [{ provide: SessionService, useValue: mockSessionService },
          { provide: ActivatedRoute, useValue: mockRoute},
          { provide: MatSnackBar, useClass: MatSnackBarStub },
          { provide: Router, useValue: {navigate: jest.fn()} },
        ],
      })
        .compileComponents();
      sessionApiService = TestBed.inject(SessionApiService);
      sessionApiService.detail = jest.fn(()=>new Observable((obs) => obs.next(session3)))
      teacherService = TestBed.inject(TeacherService);
      teacherService.detail = jest.fn(()=>new Observable(obs=>obs.next(teacher1)))
      router = TestBed.inject(Router);
      snackBar = TestBed.inject(MatSnackBar);
      fixture = TestBed.createComponent(DetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
      
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should fetch the yoga session datas at initialization', () => {
      const sessionApiSpy = jest.spyOn(sessionApiService, "detail");
      component.ngOnInit();
      fixture.detectChanges();
      expect(sessionApiSpy).toBeCalledWith('3');
      expect(component.session).toBe(session3);
      expect(component.sessionId).toBe('3');
      expect(component.teacher).toBe(teacher1);
      expect(component.isParticipate).toBe(false);
    });

  });

});

