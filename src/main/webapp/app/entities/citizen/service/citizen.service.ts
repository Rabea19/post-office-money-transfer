import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ICitizen, NewCitizen } from '../citizen.model';

export type PartialUpdateCitizen = Partial<ICitizen> & Pick<ICitizen, 'id'>;

export type EntityResponseType = HttpResponse<ICitizen>;
export type EntityArrayResponseType = HttpResponse<ICitizen[]>;

@Injectable({ providedIn: 'root' })
export class CitizenService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/citizens');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/citizens/_search');

  create(citizen: NewCitizen): Observable<EntityResponseType> {
    return this.http.post<ICitizen>(this.resourceUrl, citizen, { observe: 'response' });
  }

  update(citizen: ICitizen): Observable<EntityResponseType> {
    return this.http.put<ICitizen>(`${this.resourceUrl}/${this.getCitizenIdentifier(citizen)}`, citizen, { observe: 'response' });
  }

  partialUpdate(citizen: PartialUpdateCitizen): Observable<EntityResponseType> {
    return this.http.patch<ICitizen>(`${this.resourceUrl}/${this.getCitizenIdentifier(citizen)}`, citizen, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICitizen>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICitizen[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICitizen[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<ICitizen[]>()], asapScheduler)));
  }

  getCitizenIdentifier(citizen: Pick<ICitizen, 'id'>): number {
    return citizen.id;
  }

  compareCitizen(o1: Pick<ICitizen, 'id'> | null, o2: Pick<ICitizen, 'id'> | null): boolean {
    return o1 && o2 ? this.getCitizenIdentifier(o1) === this.getCitizenIdentifier(o2) : o1 === o2;
  }

  addCitizenToCollectionIfMissing<Type extends Pick<ICitizen, 'id'>>(
    citizenCollection: Type[],
    ...citizensToCheck: (Type | null | undefined)[]
  ): Type[] {
    const citizens: Type[] = citizensToCheck.filter(isPresent);
    if (citizens.length > 0) {
      const citizenCollectionIdentifiers = citizenCollection.map(citizenItem => this.getCitizenIdentifier(citizenItem));
      const citizensToAdd = citizens.filter(citizenItem => {
        const citizenIdentifier = this.getCitizenIdentifier(citizenItem);
        if (citizenCollectionIdentifiers.includes(citizenIdentifier)) {
          return false;
        }
        citizenCollectionIdentifiers.push(citizenIdentifier);
        return true;
      });
      return [...citizensToAdd, ...citizenCollection];
    }
    return citizenCollection;
  }
}
