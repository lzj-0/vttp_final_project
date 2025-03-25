import { Component, ElementRef, inject, Input, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AIService } from '../../service/ai.service';
import { UserService } from '../../service/user.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-cooking-image',
  standalone: false,
  templateUrl: './cooking-image.component.html',
  styleUrl: './cooking-image.component.css'
})
export class CookingImageComponent implements OnInit {

  @ViewChild('file') fileInput!: ElementRef;

  fb = inject(FormBuilder);
  aiService = inject(AIService);
  userService = inject(UserService);

  cookingImageForm!: FormGroup;
  selectedFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null;
  uploadSuccess: boolean = false;
  uploadError: boolean = false;
  message!: string;
  expDetail!: { expAwarded: number, levelUp: boolean, tierUp: string };
  expAlertMessage: string | null = null;

  sub$!: Subscription;

  @Input()
  recipeId!: string | null;

  ngOnInit(): void {
    this.cookingImageForm = this.fb.group({
      image: [null, Validators.required]
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.generateImagePreview(this.selectedFile);
      this.uploadSuccess = false;
      this.uploadError = false;
      this.message = '';
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

  onSubmit() {
    if (this.selectedFile) {
      const formData = new FormData();
      formData.append('file', this.selectedFile);

      if (this.recipeId) {
        formData.append("recipeId", this.recipeId);
      }

      this.sub$ = this.aiService.uploadCookedImage(formData)
        .subscribe({
          next: (data) => {
            this.message = data.message;
            this.uploadSuccess = true;
            this.uploadError = false;
            // console.log(data);
            this.expDetail = data.expActivity;
            this.handleExpActivityResponse();
            this.userService.fetchUserProfile();
            this.clearFile();
          },
          error: (error) => {
            this.message = error.message;
            this.uploadError = true;
            this.uploadSuccess = false;
          },
          complete: () => this.sub$.unsubscribe()
        });
    }
  }

  handleExpActivityResponse() {

    this.expAlertMessage = `You have earned ${this.expDetail.expAwarded} EXP!`;

    setTimeout(() => {
      this.clearExpAlert();
    }, 5000);
  }

  clearExpAlert() {
    this.expAlertMessage = null;
  }
}
