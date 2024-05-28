import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPostOffice } from '../post-office.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../post-office.test-samples';

import { PostOfficeService } from './post-office.service';

const requireRestSample: IPostOffice = {
  ...sampleWithRequiredData,
};

describe('PostOffice Service', () => {
  let service: PostOfficeService;
  let httpMock: HttpTestingController;
  let expectedResult: IPostOffice | IPostOffice[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PostOfficeService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a PostOffice', () => {
      const postOffice = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(postOffice).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PostOffice', () => {
      const postOffice = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(postOffice).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PostOffice', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PostOffice', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PostOffice', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a PostOffice', () => {
      const queryObject: any = {
        page: 0,
        size: 20,
        query: '',
        sort: [],
      };
      service.search(queryObject).subscribe(() => expectedResult);

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(null, { status: 500, statusText: 'Internal Server Error' });
      expect(expectedResult).toBe(null);
    });

    describe('addPostOfficeToCollectionIfMissing', () => {
      it('should add a PostOffice to an empty array', () => {
        const postOffice: IPostOffice = sampleWithRequiredData;
        expectedResult = service.addPostOfficeToCollectionIfMissing([], postOffice);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(postOffice);
      });

      it('should not add a PostOffice to an array that contains it', () => {
        const postOffice: IPostOffice = sampleWithRequiredData;
        const postOfficeCollection: IPostOffice[] = [
          {
            ...postOffice,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPostOfficeToCollectionIfMissing(postOfficeCollection, postOffice);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PostOffice to an array that doesn't contain it", () => {
        const postOffice: IPostOffice = sampleWithRequiredData;
        const postOfficeCollection: IPostOffice[] = [sampleWithPartialData];
        expectedResult = service.addPostOfficeToCollectionIfMissing(postOfficeCollection, postOffice);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(postOffice);
      });

      it('should add only unique PostOffice to an array', () => {
        const postOfficeArray: IPostOffice[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const postOfficeCollection: IPostOffice[] = [sampleWithRequiredData];
        expectedResult = service.addPostOfficeToCollectionIfMissing(postOfficeCollection, ...postOfficeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const postOffice: IPostOffice = sampleWithRequiredData;
        const postOffice2: IPostOffice = sampleWithPartialData;
        expectedResult = service.addPostOfficeToCollectionIfMissing([], postOffice, postOffice2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(postOffice);
        expect(expectedResult).toContain(postOffice2);
      });

      it('should accept null and undefined values', () => {
        const postOffice: IPostOffice = sampleWithRequiredData;
        expectedResult = service.addPostOfficeToCollectionIfMissing([], null, postOffice, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(postOffice);
      });

      it('should return initial array if no PostOffice is added', () => {
        const postOfficeCollection: IPostOffice[] = [sampleWithRequiredData];
        expectedResult = service.addPostOfficeToCollectionIfMissing(postOfficeCollection, undefined, null);
        expect(expectedResult).toEqual(postOfficeCollection);
      });
    });

    describe('comparePostOffice', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePostOffice(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePostOffice(entity1, entity2);
        const compareResult2 = service.comparePostOffice(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePostOffice(entity1, entity2);
        const compareResult2 = service.comparePostOffice(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePostOffice(entity1, entity2);
        const compareResult2 = service.comparePostOffice(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
