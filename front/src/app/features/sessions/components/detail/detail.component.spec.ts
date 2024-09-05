import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule, } from '@angular/router/testing';
import { expect } from '@jest/globals'; 
import { SessionService } from '../../../../services/session.service';

import { DetailComponent } from './detail.component';
import { SessionApiService } from '../../services/session-api.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { Teacher } from 'src/app/interfaces/teacher.interface';
import { TeacherService } from 'src/app/services/teacher.service';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

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
  let service: SessionService;
  let sessionApiService: SessionApiService;
  let teacherService: TeacherService;
  let router: Router;
  let snackBar: MatSnackBar;
  const now = new Date();

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

  const session1 = {
    id: 2,
    name: "Session 2",
    description: "Une session de test",
    date: now,
    teacher_id: 1,
    users: [1,2,3,4],
    createdAt: now,
    updatedAt:now
  };

  const teacher1 = {
    id:1,
    lastName: "LeBricoleur",
    firstName: "Bob",
    createdAt: now,
    updatedAt: now
  }

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
    service = TestBed.inject(SessionService);
    sessionApiService = TestBed.inject(SessionApiService);
    teacherService = TestBed.inject(TeacherService);
    router = TestBed.inject(Router);
    snackBar = TestBed.inject(MatSnackBar);
    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    teacherService.detail = jest.fn(()=>new Observable(obs=>obs.next(teacher1)))
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch the yoga session datas at initialization', () => {
    sessionApiService.detail = jest.fn(()=>new Observable((obs) => obs.next(session1)))
    const sessionApiSpy = jest.spyOn(sessionApiService, "detail");
    component.ngOnInit();
    fixture.detectChanges();
    expect(sessionApiSpy).toBeCalledWith('2');
    expect(component.session).toBe(session1);
    expect(component.sessionId).toBe('2');
    expect(component.teacher).toBe(teacher1);
    expect(component.isParticipate).toBe(true);
  });

  it('back should go back in history', () => {
    const historySpy = jest.spyOn(window.history, "back");
    component.back();
    expect(historySpy).toBeCalled();
  });

  it('delete should delete the current yoga session, notify the deletion and navigate to /sessions', () => {
    sessionApiService.delete = jest.fn(()=> new Observable(obs=>obs.next(session1)));
    const sessionApiSpy = jest.spyOn(sessionApiService, "delete");
    const snackBarSpy = jest.spyOn(snackBar, "open");
    const routerSpy = jest.spyOn(router, "navigate");
    component.delete();
    expect(sessionApiSpy).toBeCalledWith('2');
    expect(snackBarSpy).toBeCalled();
    expect(routerSpy).toBeCalledWith(['sessions']);
  });

  it('participate should add the user to the yoga session', () => {
    sessionApiService.participate = jest.fn(()=> new Observable(obs=>obs.next()));
    const sessionApiSpy = jest.spyOn(sessionApiService, "participate");
    component.participate();
    expect(sessionApiSpy).toBeCalledWith("2","1");
  });

  it('unParticipate should remove the user from the yoga session', () => {
    sessionApiService.unParticipate = jest.fn(()=> new Observable(obs=>obs.next()));
    const sessionApiSpy = jest.spyOn(sessionApiService, "unParticipate");
    component.unParticipate();
    expect(sessionApiSpy).toBeCalledWith("2","1");
  });

});

