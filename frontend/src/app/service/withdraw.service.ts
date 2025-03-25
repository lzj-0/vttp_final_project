import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class WithdrawService {

  http = inject(HttpClient);
  authService = inject(AuthService);

  withdrawCredit(formValue : {selectedBank : string, bankAccount : string, credits : number}) {
    const url = "/api/user/withdraw";

    const jwt = this.authService.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);
      
    return this.http.put(url, formValue, {headers : headers});
  }
}
