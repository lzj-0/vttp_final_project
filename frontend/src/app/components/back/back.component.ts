import { Component, inject, Input } from '@angular/core';
import { Router } from '@angular/router';
import { Location } from '@angular/common';

@Component({
  selector: 'app-back',
  standalone: false,
  templateUrl: './back.component.html',
  styleUrl: './back.component.css'
})
export class BackComponent {

  router = inject(Router);
  location = inject(Location);

  @Input()
  path : string = '';

  @Input()
  label : string = 'Back';

  goBack() {
    if (this.location.getState() && window.history.length > 1) {
      this.location.back(); // go back to the previous route
    } else {
      this.location.go(this.path || '/');
    }
  }
}
