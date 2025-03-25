import { Component, inject } from '@angular/core';
import { AuthService } from '../../service/auth.service';
import { StripeService } from '../../service/stripe.service';

@Component({
  selector: 'app-subscription',
  standalone: false,
  templateUrl: './subscription.component.html',
  styleUrl: './subscription.component.css'
})
export class SubscriptionComponent {

  authService = inject(AuthService);
  stripeService = inject(StripeService);

  async processCheckout() {
    try {

      const session = await this.stripeService.createCheckout();
      console.log(session);
      if (session?.sessionId) {

        console.log(session.sessionId);
        await this.stripeService.redirectCheckout(session.sessionId);
      } else {
        console.error('Failed to create Stripe Checkout session.');
      }
    } catch (error) {
      console.error('Error during checkout:', error);
    }
  }

}
