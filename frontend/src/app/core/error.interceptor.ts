import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ApiError } from './models';
import { NotificationService } from './notification.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const notify = inject(NotificationService);

  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      const api = err.error as ApiError | undefined;
      let mensagem = 'Erro inesperado ao comunicar com a API';

      if (api?.mensagem) {
        mensagem = api.mensagem;
        if (api.campos?.length) {
          mensagem += ': ' + api.campos.map(c => c.mensagem).join('; ');
        }
      } else if (err.status === 0) {
        mensagem = 'API indisponivel. Verifique se o backend esta em http://localhost:8080';
      } else if (err.message) {
        mensagem = err.message;
      }

      notify.erro(mensagem);
      return throwError(() => err);
    })
  );
};
