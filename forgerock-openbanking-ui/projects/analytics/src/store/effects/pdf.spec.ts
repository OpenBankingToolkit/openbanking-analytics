import { TestBed, async } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { HttpErrorResponse } from '@angular/common/http';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { CommonModule } from '@angular/common';
import { CookieModule } from 'ngx-cookie';
import { StoreModule, Store } from '@ngrx/store';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Observable } from 'rxjs';
import { ReplaySubject } from 'rxjs';
import { of } from 'rxjs';
import { throwError } from 'rxjs';
import * as fileSaver from 'file-saver';
import { DeviceDetectorModule } from 'ngx-device-detector';

import { GetPdfRequestAction, GetPdfSuccessAction, GetPdfErrorAction } from 'analytics/src/store/reducers/pdf';
import { PdfEffects } from './pdf';
import { MetricsService } from 'analytics/src/app/services/metrics.service';
import { ForgerockMessagesService } from '@forgerock/openbanking-ngx-common/services/forgerock-messages';
import rootReducer from 'analytics/src/store';
import { IState } from 'analytics/src/models';
import { first } from 'rxjs/operators';

jest.mock('file-saver');

describe('PdfEffects', () => {
  let effects: PdfEffects;
  let actions: Observable<any>;
  let messsageService: ForgerockMessagesService;
  let metricsService: MetricsService;
  let store: Store<IState>;
  let metricsServiceSpy;
  let messsageServiceSpy;
  let fromDate: string;
  let toDate: string;

  beforeAll(() => {
    fileSaver.mockImplementation(() => {
      return {
        saveAs: () => {
          throw new Error('Test error');
        }
      };
    });
  });

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        CookieModule.forRoot(),
        MatSnackBarModule,
        RouterTestingModule.withRoutes([]),
        NoopAnimationsModule,
        CommonModule,
        StoreModule.forRoot(rootReducer),
        DeviceDetectorModule.forRoot()
      ],
      providers: [PdfEffects, MetricsService, ForgerockMessagesService, provideMockActions(() => actions)]
    });

    store = TestBed.get(Store);
    metricsService = TestBed.get(MetricsService);
    effects = TestBed.get(PdfEffects);
    messsageService = TestBed.get(ForgerockMessagesService);

    store.pipe(first()).subscribe(state => {
      fromDate = state.dates.from;
      toDate = state.dates.to;
    });
  });

  it('should return Success action', async(() => {
    const pdfId = 'tpp';
    metricsServiceSpy = spyOn(metricsService, 'getPdf').and.returnValue(of(true));

    actions = new ReplaySubject(1);
    actions.next(new GetPdfRequestAction({ id: pdfId }));

    effects.request$.subscribe(result => {
      expect(metricsServiceSpy).toHaveBeenCalledWith({ id: pdfId, fromDate, toDate });
      expect(result).toEqual(new GetPdfSuccessAction({ id: pdfId }));
    });
  }));

  it('should return Error action', async(() => {
    const pdfId = 'tpp';
    metricsServiceSpy = spyOn(metricsService, 'getPdf').and.returnValue(
      throwError(
        new HttpErrorResponse({
          error: {
            message: 'hehe this is an error'
          }
        })
      )
    );
    messsageServiceSpy = spyOn(messsageService, 'error');

    actions = new ReplaySubject(1);
    actions.next(new GetPdfRequestAction({ id: pdfId }));

    effects.request$.subscribe(result => {
      expect(metricsServiceSpy).toHaveBeenCalledWith({ id: pdfId, fromDate, toDate });
      expect(messsageServiceSpy).toHaveBeenCalled();
      expect(result).toEqual(new GetPdfErrorAction({ id: pdfId }));
    });
  }));
});
