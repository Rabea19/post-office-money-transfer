jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { PostOfficeService } from '../service/post-office.service';

import { PostOfficeDeleteDialogComponent } from './post-office-delete-dialog.component';

describe('PostOffice Management Delete Component', () => {
  let comp: PostOfficeDeleteDialogComponent;
  let fixture: ComponentFixture<PostOfficeDeleteDialogComponent>;
  let service: PostOfficeService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, PostOfficeDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(PostOfficeDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PostOfficeDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(PostOfficeService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete(123);
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith(123);
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      }),
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
