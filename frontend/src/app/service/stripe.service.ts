import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { loadStripe, Stripe } from '@stripe/stripe-js';
import { environment } from '../../environments/environment';
import { lastValueFrom } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class StripeService {

  http = inject(HttpClient);
  authService = inject(AuthService);

  private stripePromise!: Promise<Stripe | null>;

  constructor() { 
    this.stripePromise = loadStripe(environment.stripePublishableKey);
  }


  // baseUrl = "http://localhost:8080";

  async createCheckout() {
    const url = "/api/payment/checkout";
    const jwt = this.authService.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);
    console.log(jwt);
    const userId = this.authService.getUserId();
    
    if (userId != null) {
      const queryParams = new HttpParams().set("userId", userId);
      console.log(queryParams);
      return lastValueFrom(this.http.post<{sessionId: string}>(url, {}, {headers : headers, params : queryParams}));
    }

    return lastValueFrom(this.http.post<{sessionId: string}>(url, {}, {headers : headers}));

  }

  async redirectCheckout(sessionId: string) {
    console.log("redirect");

    const stripe = await this.stripePromise;
    if (!stripe) {
      throw new Error('Stripe.js failed to load.');
    }

    console.log("before stripe redirect");  

    const { error } = await stripe.redirectToCheckout({ sessionId });
    if (error) {
      console.error('Error redirecting to Stripe Checkout:', error);
      alert(error.message);
    }
  }

}
