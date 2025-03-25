import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { User } from '../model/User';

@Injectable({
  providedIn: 'root'
})
export class LeaderboardService {

  http = inject(HttpClient);
  authService = inject(AuthService);

  // baseUrl = "http://localhost:8080/api/leaderboard";

  getTopUsers() {
    const url = "/api/leaderboard/get";

    const jwt = this.authService.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);

    return this.http.get<{leaderboard : { user : User, expEarned : number}[], email : string}>(url, {headers : headers});
  }

}
