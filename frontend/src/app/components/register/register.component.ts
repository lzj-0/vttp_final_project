import { Component, ElementRef, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AuthService } from '../../service/auth.service';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit, OnDestroy {

  fb = inject(FormBuilder);
  authService = inject(AuthService);
  route = inject(ActivatedRoute);
  router = inject(Router);

  @ViewChild('file') fileInput!: ElementRef;

  form!: FormGroup;
  selectedFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null;
  signUpStatus!: string;

  sub$!: Subscription;
  registerSub$!: Subscription;


  hasUpperCase = (control: AbstractControl) => {
    if (!/[A-Z]/.test(control.value)) {
      return { hasUpperCase: true } as ValidationErrors;
    }
    return null;
  }


  ngOnInit(): void {
    this.form = this.fb.group({
      name: this.fb.control<string>('', [Validators.required, Validators.minLength(3)]),
      email: this.fb.control<string>('', [Validators.required, Validators.email]),
      password: this.fb.control<string>('', [Validators.required, Validators.minLength(8), this.hasUpperCase]),
      confirmPassword: this.fb.control<string>('')
    })

    this.sub$ = this.route.queryParams.subscribe({
      next: (params) => {
        this.form.patchValue({
          name: params['name'],
          email: params['email'],
        })
      }
    })
  }

  passwordClass = (field: string) => {
    const passwordControl = this.form.get('password');
    const isDirty = passwordControl?.dirty;
    const isEmpty = !passwordControl?.value;

    if (field === 'minlength' && isEmpty) {
      return { 'text-red-600': isDirty, 'text-gray-400': !isDirty };
    }

    return {
      'text-gray-400': !isDirty,
      'text-red-600': isDirty && passwordControl?.hasError(field),
      'text-green-500': isDirty && !passwordControl?.hasError(field)
    };
  }

  passwordMatches() {
    return this.form.get('password')?.value === this.form.get('confirmPassword')?.value;
  }

  isInvalidField(control: string): boolean {
    return !!this.form.get(control)?.invalid && !!this.form.get(control)?.touched;
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.generateImagePreview(this.selectedFile);
    }
  }

  generateImagePreview(file: File): void {
    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result;
    };
    reader.readAsDataURL(file);
  }

  clearFile(): void {
    this.selectedFile = null;
    this.imagePreview = null;
    this.resetFileInput();
  }

  resetFileInput(): void {
    if (this.fileInput && this.fileInput.nativeElement) {
      this.fileInput.nativeElement.value = '';
    }
  }

  registerUser() {
    console.log(this.form.value);

    const formData = new FormData();
    formData.set('userStr', JSON.stringify(this.form.value));
    const file = this.fileInput.nativeElement.files[0];
    console.log(file);
    if (file) {
      formData.set("file", file);
    }

    this.registerSub$ = this.authService.registerUser(formData).subscribe({
      next: (data) => {
        console.log(data);
        this.router.navigate(['/login'], { queryParams: { registerSuccess: true } });
      },
      error: (error) => {
        console.log(error);
        this.signUpStatus = error.error.message;
      },
      complete: () => this.registerSub$.unsubscribe()
    })
  }

  ngOnDestroy(): void {
    this.sub$.unsubscribe();
  }
}
