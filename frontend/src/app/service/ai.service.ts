import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { Response } from '../model/Response';

@Injectable({
  providedIn: 'root'
})
export class AIService {

  http = inject(HttpClient);
  authService = inject(AuthService);

  // baseUrl = "http://localhost:8080/api/ai";

  uploadCookedImage(formData : FormData) {

    const url = "/api/ai/cookedimage";

    const jwt = this.authService.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);

    return this.http.post<Response>(url, formData, {headers : headers});
  }

  getCalories(payload : any) {

    const url = "/api/ai/getcalories";

    const jwt = this.authService.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);

    return this.http.post(url, payload, {headers : headers});
  }

  guessCalories(formData : FormData) {
    const url = "/api/ai/guesscalories";

    const jwt = this.authService.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);

    return this.http.post(url, formData, {headers : headers});

  }

}
