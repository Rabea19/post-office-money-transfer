import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPostOffice } from '../post-office.model';
import { PostOfficeService } from '../service/post-office.service';

const postOfficeResolve = (route: ActivatedRouteSnapshot): Observable<null | IPostOffice> => {
  const id = route.params['id'];
  if (id) {
    return inject(PostOfficeService)
      .find(id)
      .pipe(
        mergeMap((postOffice: HttpResponse<IPostOffice>) => {
          if (postOffice.body) {
            return of(postOffice.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default postOfficeResolve;
