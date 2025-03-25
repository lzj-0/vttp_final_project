import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class RewardsService {

  http = inject(HttpClient);
  authService = inject(AuthService);

  getActionData() {
    const url = "/api/rewards/actiondata";

    const jwt = this.authService.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);
                    
    return this.http.get<{status : number, gatekeepData : any[], nonGatekeepData : any[]}>(url, {headers : headers});
  }
}
