import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IPostOffice, NewPostOffice } from '../post-office.model';

export type PartialUpdatePostOffice = Partial<IPostOffice> & Pick<IPostOffice, 'id'>;

export type EntityResponseType = HttpResponse<IPostOffice>;
export type EntityArrayResponseType = HttpResponse<IPostOffice[]>;

@Injectable({ providedIn: 'root' })
export class PostOfficeService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/post-offices');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/post-offices/_search');

  create(postOffice: NewPostOffice): Observable<EntityResponseType> {
    return this.http.post<IPostOffice>(this.resourceUrl, postOffice, { observe: 'response' });
  }

  update(postOffice: IPostOffice): Observable<EntityResponseType> {
    return this.http.put<IPostOffice>(`${this.resourceUrl}/${this.getPostOfficeIdentifier(postOffice)}`, postOffice, {
      observe: 'response',
    });
  }

  partialUpdate(postOffice: PartialUpdatePostOffice): Observable<EntityResponseType> {
    return this.http.patch<IPostOffice>(`${this.resourceUrl}/${this.getPostOfficeIdentifier(postOffice)}`, postOffice, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPostOffice>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPostOffice[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPostOffice[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IPostOffice[]>()], asapScheduler)));
  }

  getPostOfficeIdentifier(postOffice: Pick<IPostOffice, 'id'>): number {
    return postOffice.id;
  }

  comparePostOffice(o1: Pick<IPostOffice, 'id'> | null, o2: Pick<IPostOffice, 'id'> | null): boolean {
    return o1 && o2 ? this.getPostOfficeIdentifier(o1) === this.getPostOfficeIdentifier(o2) : o1 === o2;
  }

  addPostOfficeToCollectionIfMissing<Type extends Pick<IPostOffice, 'id'>>(
    postOfficeCollection: Type[],
    ...postOfficesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const postOffices: Type[] = postOfficesToCheck.filter(isPresent);
    if (postOffices.length > 0) {
      const postOfficeCollectionIdentifiers = postOfficeCollection.map(postOfficeItem => this.getPostOfficeIdentifier(postOfficeItem));
      const postOfficesToAdd = postOffices.filter(postOfficeItem => {
        const postOfficeIdentifier = this.getPostOfficeIdentifier(postOfficeItem);
        if (postOfficeCollectionIdentifiers.includes(postOfficeIdentifier)) {
          return false;
        }
        postOfficeCollectionIdentifiers.push(postOfficeIdentifier);
        return true;
      });
      return [...postOfficesToAdd, ...postOfficeCollection];
    }
    return postOfficeCollection;
  }
}
