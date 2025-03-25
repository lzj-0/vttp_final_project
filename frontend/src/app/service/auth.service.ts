import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../model/User';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Response } from '../model/Response';
import { Recipe } from '../model/Recipe';
import { Review } from '../model/Review';
import { AuthStore } from '../store/AuthStore';
import { tap } from 'rxjs';
import { LevelDetail } from '../model/LevelDetail';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  // baseUrl = "http://localhost:8080/api/user";

  router = inject(Router);
  http = inject(HttpClient);
  authStore = inject(AuthStore)

  isLoggedIn(): boolean {
    return this.authStore.getState().isLoggedIn;
  }

  getToken(): string | null {
    return this.authStore.getState().jwtToken;
  }

  getUserId(): string | null {
    return this.authStore.getState().userId;
  }

  addTokenToHeader() {
    const jwt = this.getToken();
    const headers = new HttpHeaders().set('Authorization', `Bearer ${jwt}`);
    return {headers : headers};
  }

  logout(): void {
    this.authStore.logout();
    this.router.navigate(['/login']);
  }

  isTokenExpired(token: string): boolean {
    const payload = JSON.parse(atob(token.split('.')[1])); // decode the token
    const expiry = payload.exp;
    return expiry < Date.now() / 1000;
  }

  checkTokenExpiry(): void {
    const token = this.getToken();
    if (token && this.isTokenExpired(token)) {
      this.logout();
    }
  }

  verifyLogin(user: User) {
    const url = '/api/user/customlogin';
    return this.http.post<Response>(url, user).pipe(
      tap((response) => {
        this.authStore.setToken(response.token);
        this.authStore.setUserId(response.userId);
      })
    );
  }

  registerUser(formData : FormData) {
    const url = "/api/user/register";
    return this.http.post<Response>(url, formData);

  }

  getUserProfile(id : string | null) {
    const url = `/api/user/${id}`;

    const jwt = this.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);

    return this.http.get<{user : User, email : string, publicRecipes : Recipe[],
                       privateRecipes : Recipe[], reviews : Review[], levelStats : LevelDetail}>
              (url, {headers : headers});
  }

  updateWalletAddress(walletAddress : string) {
    const url = "/api/user/updatewallet";
    const jwt = this.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);

    return this.http.put<{status : number, walletAddress : string, message : string}>(url, { "walletAddress" : walletAddress}, {headers : headers});
  }

}
