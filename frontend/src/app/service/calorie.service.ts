import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { CalorieLog } from '../model/CalorieLog';

@Injectable({
  providedIn: 'root'
})
export class CalorieService {

  http = inject(HttpClient);
  authService = inject(AuthService);

  // baseUrl = "http://localhost:8080/api/calorie";

  uploadMeal(calorieLog : CalorieLog) {
    const url = "/api/calorie/addmeal";

    const jwt = this.authService.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);

    return this.http.post<{status : string, logId : string}>(url, calorieLog, {headers : headers});
  }

  getLogs() {
    const url = "/api/calorie/getlog";

    const jwt = this.authService.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);

    return this.http.get<{logs : CalorieLog[], email : string}>(url, {headers : headers});
  }

  deleteLog(id: string) {
    const url = "/api/calorie/deletelog/" + id;

    const jwt = this.authService.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);

    return this.http.delete<Response>(url, {headers : headers});
  }

}
