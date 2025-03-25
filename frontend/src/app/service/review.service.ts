import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { Review } from '../model/Review';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {

  constructor() { }

  http = inject(HttpClient);
  authService = inject(AuthService);

  // baseUrl = "http://localhost:8080/api/review"

  addReview(review : Review) {
    const url = "/api/review/add";

    const jwt = this.authService.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);

    return this.http.post<{status : number, reviewId : string, 
            expActivity : {expAwarded : number, levelUp : boolean, tierUp : string}}>
            (url, review, {headers : headers});
  }
}
