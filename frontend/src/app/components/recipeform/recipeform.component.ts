import { moveItemInArray } from '@angular/cdk/drag-drop';
import { HttpClient } from '@angular/common/http';
import { Component, ElementRef, inject, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { RecipeService } from '../../service/recipe.service';
import { Router } from '@angular/router';
import { AIService } from '../../service/ai.service';
import { UserService } from '../../service/user.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-recipeform',
  standalone: false,
  templateUrl: './recipeform.component.html',
  styleUrl: './recipeform.component.css',
  encapsulation: ViewEncapsulation.Emulated
})
export class RecipeformComponent implements OnInit {

  @ViewChild("file") fileInput!: ElementRef;

  fb = inject(FormBuilder);
  http = inject(HttpClient);
  recipeService = inject(RecipeService);
  router = inject(Router);
  aiService = inject(AIService);
  userService = inject(UserService);

  form!: FormGroup;
  ingredients!: FormArray;
  instructions!: FormArray;

  cuisines!: string[];

  isLoading: boolean = false;
  coolDownActive: boolean = false;
  coolDownTime = 0;
  coolDownInterval: any;
  errorMessage = '';

  fileName: string = 'No file chosen';

  aiSub$! : Subscription;
  addRecipeSub$!: Subscription;

  ngOnInit(): void {
    this.ingredients = this.fb.array([], [Validators.required]);
    this.instructions = this.fb.array([], [Validators.required]);
    this.addIngField();
    this.addInsField();
    this.loadCuisines();
    this.form = this.fb.group({
      title: this.fb.control<string>('', [Validators.required]),
      cuisine: this.fb.control<string>('', [Validators.required]),
      meal: this.fb.control<string>('', [Validators.required]),
      servingSize: this.fb.control<number>(1, [Validators.required]),
      ingredients: this.ingredients,
      preparationTime: this.fb.control<number>(0, [Validators.required]),
      summary: this.fb.control<string>('', [Validators.required]),
      instructions: this.instructions,
      calories: this.fb.control<number>(0, [Validators.required]),
      consent: this.fb.control<boolean>(false, [Validators.requiredTrue])
    })
  }

  loadCuisines(): void {
    this.http.get("cuisines.txt", { responseType: 'text' }).subscribe(data => this.cuisines = data.split("\n"),
      error => console.error("Error loading cuisines", error));
  }

  addIngField() {
    this.ingredients.push(
      this.fb.control<string>('')
    )
  }

  removeIngField(ind: number) {
    this.ingredients.removeAt(ind);
  }

  addInsField() {
    this.instructions.push(
      this.fb.control<string>('')
    )
  }

  removeInsField(ind: number) {
    this.instructions.removeAt(ind);
  }

  drop(event: { previousIndex: number; currentIndex: number; }) {
    moveItemInArray(this.instructions.controls, event.previousIndex, event.currentIndex);
    this.instructions.updateValueAndValidity();
  }

  askAI() {
    this.isLoading = true;
    const formData = this.form;
    this.coolDownTime = 60;
    this.errorMessage = '';

    const aiPayload = {
      title: formData.get('title')?.value,
      ingredients: formData.get('ingredients')?.value,
      instructions: formData.get('instructions')?.value,
      servingSize: formData.get('servingSize')?.value
    }

    console.log(aiPayload);

    this.aiSub$ = this.aiService.getCalories(aiPayload)
      .subscribe({
        next: (response: any) => {
          console.log(response);
          this.form.get('calories')?.setValue(response.estimatedCalories);
        },
        error: (error) => {
          console.error('Error querying Gemini AI', error);
          this.coolDownActive = true;
          this.isLoading = false;

          this.coolDownInterval = setInterval(() => {
            if (this.coolDownTime > 0) {
              this.coolDownTime--;
            } else {
              this.coolDownActive = false;
              clearInterval(this.coolDownInterval);
            }
          }, 1000);

          this.errorMessage = error.error?.message || 'An unknown error occurred.';

        },
        complete: () => {
          this.coolDownActive = true;
          this.isLoading = false;

          this.coolDownInterval = setInterval(() => {
            if (this.coolDownTime > 0) {
              this.coolDownTime--;
            } else {
              this.coolDownActive = false;
              clearInterval(this.coolDownInterval);
            }
          }, 1000);
          this.aiSub$.unsubscribe()
        }
      });

  }

  formCtrlInvalid(field: string): boolean {
    return !!this.form.get(field)?.invalid && !!this.form.get(field)?.dirty;
  }

  disableSubmit() {
    return this.form.invalid || this.ingInvalid() || this.insInvalid();
  }

  ingInvalid() {
    return this.ingredients.controls.some(ctrl => ctrl.value.trim() === "") && this.form.get('ingredients')?.touched;
  }

  insInvalid() {
    return this.instructions.controls.some(ctrl => ctrl.value.trim() === "") && this.form.get('instructions')?.touched;
  }

  onFileChange(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.fileName = file.name;
    } else {
      this.fileName = 'No file chosen';
    }
  }

  processForm() {
    console.log(this.form.value);
    const formData = new FormData();

    formData.set('recipeStr', JSON.stringify(this.form.value));
    const file = this.fileInput.nativeElement.files[0];
    console.log(file);
    if (file) {
      formData.set("file", file);
    }

    this.addRecipeSub$ = this.recipeService.addRecipe(formData).subscribe({
      next: (data) => {
        console.log(data.recipeId);
        console.log(data.expActivity);
        this.userService.fetchUserProfile();
        this.router.navigate(['/recipe', data.recipeId], { state: { expActivity: data.expActivity } });
      },
      error: (error) => console.log(error),
      complete: () => this.addRecipeSub$.unsubscribe()
    })
  }

}
