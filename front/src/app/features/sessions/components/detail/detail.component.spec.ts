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

describe('DetailComponent as admin', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>; 
  let service: SessionService;
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

  const teacher1 = {
    id:1,
    lastName: "LeBricoleur",
    firstName: "Bob",
    createdAt: date1,
    updatedAt: date1
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

  it('should display the yoga session informations at initialization', () => {
    const detailElement: HTMLElement = fixture.nativeElement;
    expect(detailElement.querySelector('h1')?.textContent).toContain('Session 2');
    expect(detailElement.querySelector('div.description')?.textContent).toContain(session2.description);
    expect(detailElement.querySelector('div.created')?.textContent).toContain(dateString1);
    expect(detailElement.querySelector('div.updated')?.textContent).toContain(dateString2);
    expect(detailElement.textContent).toContain(dateString3);
    expect(detailElement.textContent).toContain(session2.users.length +' attendees');
    expect(detailElement.textContent).toContain(teacher1.firstName + ' ' + teacher1.lastName.toUpperCase());
  });

  it('Click on back button should go back in history', () => {
    const historySpy = jest.spyOn(window.history, "back");
    const backBtn: HTMLButtonElement | null = fixture.nativeElement.querySelector('button');
    backBtn?.click();
    expect(historySpy).toBeCalled();
  });

  it('Click on delete button should delete the session, notify the deletion and navigate to /sessions', () => {
    sessionApiService.delete = jest.fn(()=> new Observable(obs=>obs.next(session2)));
    const sessionApiSpy = jest.spyOn(sessionApiService, "delete");
    const snackBarSpy = jest.spyOn(snackBar, "open");
    const routerSpy = jest.spyOn(router, "navigate");
    const deleteBtn: HTMLButtonElement = fixture.nativeElement.querySelectorAll('button')[1]
    deleteBtn?.click();
    expect(sessionApiSpy).toBeCalledWith('2');
    expect(snackBarSpy).toBeCalled();
    expect(routerSpy).toBeCalledWith(['sessions']);
  });

});

describe('DetailComponent as user', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>; 
  let service: SessionService;
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

  const teacher1 = {
    id:1,
    lastName: "Le Bricoleur",
    firstName: "Bob",
    createdAt: date1,
    updatedAt: date1
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

  it('should display the yoga session informations at initialization', () => {
    const detailElement: HTMLElement = fixture.nativeElement;
    expect(detailElement.querySelector('h1')?.textContent).toContain('Session 3');
    expect(detailElement.querySelector('div.description')?.textContent).toContain(session3.description);
    expect(detailElement.querySelector('div.created')?.textContent).toContain(dateString1);
    expect(detailElement.querySelector('div.updated')?.textContent).toContain(dateString2);
    expect(detailElement.textContent).toContain(dateString3);
    expect(detailElement.textContent).toContain(session3.users.length +' attendees');
    expect(detailElement.textContent).toContain(teacher1.firstName + ' ' + teacher1.lastName.toUpperCase());
  });

  it('Click on back button should go back in history', () => {
    const historySpy = jest.spyOn(window.history, "back");
    const backBtn: HTMLButtonElement | null = fixture.nativeElement.querySelector('button');
    backBtn?.click();
    expect(historySpy).toBeCalled();
  });

  it('Click on participate button should add the user to the yoga session', () => {
    sessionApiService.participate = jest.fn(()=> new Observable(obs=>obs.next()));
    const sessionApiSpy = jest.spyOn(sessionApiService, "participate");
    const participateBtn: HTMLButtonElement = fixture.nativeElement.querySelectorAll('button')[1]
    participateBtn?.click();
    expect(sessionApiSpy).toBeCalledWith("3","5");
  });

  it('Click on participate button should remove the user from the yoga session', () => {
    sessionApiService.detail = jest.fn(()=>new Observable((obs) => obs.next(session4)))
    component.ngOnInit();
    fixture.detectChanges();
    sessionApiService.unParticipate = jest.fn(()=> new Observable(obs=>obs.next()));
    const sessionApiSpy = jest.spyOn(sessionApiService, "unParticipate");
    const unparticipateBtn: HTMLButtonElement = fixture.nativeElement.querySelectorAll('button')[1]
    unparticipateBtn?.click();
    expect(sessionApiSpy).toBeCalledWith("3","5");
  });

});

