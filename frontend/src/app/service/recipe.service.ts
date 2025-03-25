import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Recipe } from '../model/Recipe';
import { AuthService } from './auth.service';
import { RecipeDetail } from '../model/RecipeDetail';
import { User } from '../model/User';

@Injectable({
  providedIn: 'root'
})
export class RecipeService {

  constructor() { }

  http = inject(HttpClient);
  authService = inject(AuthService);

  // baseUrl = "http://localhost:8080/api/recipe"
  
  getRecipes() {
    const url = "/api/recipe/getrecipes";

    if (this.authService.isLoggedIn()) {
      const jwt = this.authService.getToken();
      const headers = new HttpHeaders()
                      .set('Authorization', `Bearer ${jwt}`);
      return this.http.get<{recipes : Recipe[], email : string | null}>(url, {headers : headers});
    }

    return this.http.get<{recipes : Recipe[], email : string | null}>(url);

  }

  getRecipeById(id : string | null) {
    const url = "/api/recipe/getrecipe/" + id;

    const jwt = this.authService.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);
    return this.http.get<{recipe : RecipeDetail, owner : User, email : string}>(url, {headers : headers});
  }

  addRecipe(formData : FormData) {
    const url = "/api/recipe/addrecipe";

    const jwt = this.authService.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);

    return this.http.post<{status : number, recipeId : string, 
                expActivity : {expAwarded : number, levelUp : boolean, tierUp : string}}>
                (url, formData, {headers : headers});
  }

  addRecipeView(recipeId : string | null) {
    const url = "/api/recipe/addrecipeview/" + recipeId;

    const jwt = this.authService.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);

    return this.http.put(url, {}, {headers : headers});
  }

  getTrendingRecipes() {
    const url = "/api/recipe/getrecipes/trending";

    const jwt = this.authService.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);

    return this.http.get<{recipes : RecipeDetail[], email : string}>(url, {headers : headers});
  }
  
  getNewRecipes() {
    const url = "/api/recipe/getrecipes/new";

    const jwt = this.authService.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);

    return this.http.get<{recipes : RecipeDetail[], email : string}>(url, {headers : headers});
  }

  toggleGatekeep(recipeId : string) {
    const url = "/api/recipe/togglegatekeep/" + recipeId;

    const jwt = this.authService.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);

    return this.http.put<{status : number, recipeId : string, gatekeep : boolean, message : string}>
              (url, {}, {headers : headers}); 
  }

  togglePrivate(recipeId : string) {
    const url = "/api/recipe/toggleprivate/" + recipeId;

    const jwt = this.authService.getToken();
    const headers = new HttpHeaders()
                    .set('Authorization', `Bearer ${jwt}`);

    return this.http.put<{status : number, recipeId : string, private : boolean, message : string}>
                    (url, {}, {headers : headers}); 
  }

}
