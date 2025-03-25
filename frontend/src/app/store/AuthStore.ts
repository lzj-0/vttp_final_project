import { inject, Injectable } from "@angular/core";
import { ComponentStore } from '@ngrx/component-store';
import { User } from "../model/User";
import { take } from "rxjs";
import { AuthService } from "../service/auth.service";
import { LevelDetail } from "../model/LevelDetail";

export interface AuthState {
    jwtToken: string | null;
    userId: string | null;
    user: User | null;
    levelStats: LevelDetail | null;
    isLoggedIn: boolean;
  }

  @Injectable({
    providedIn: 'root',
  })
  export class AuthStore extends ComponentStore<AuthState> {
    
    constructor() {
        super({
            jwtToken: localStorage.getItem('jwtToken'),
            userId: localStorage.getItem('userId'),
            user: null,
            levelStats: null,
            isLoggedIn: !!localStorage.getItem('jwtToken'),
          });
    }

    readonly jwtToken$ = this.select((state) => state.jwtToken);
    readonly userId$ = this.select((state) => state.userId);
    readonly user$ = this.select((state) => state.user);
    readonly levelStats$ = this.select((state) => state.levelStats);
    readonly isLoggedIn$ = this.select((state) => state.isLoggedIn);

    // readonly setToken = this.updater((state, jwtToken: string | null) => ({
    //     ...state,
    //     jwtToken,
    //     isLoggedIn: !!jwtToken,
    //   }));
    
    // readonly setUserId = this.updater((state, userId: string | null) => ({
    // ...state,
    // userId,
    // }));

    // readonly setUser = this.updater((state, user: User | null) => ({
    // ...state,
    // user,
    // }));

    readonly setToken = this.updater((state, jwtToken: string | null) => {
        if (jwtToken) {
          localStorage.setItem('jwtToken', jwtToken); // Persist to localStorage
        } else {
          localStorage.removeItem('jwtToken'); // Remove from localStorage
        }
        return { ...state, jwtToken, isLoggedIn: !!jwtToken };
      });
    
      readonly setUserId = this.updater((state, userId: string | null) => {
        if (userId) {
          localStorage.setItem('userId', userId); // Persist to localStorage
        } else {
          localStorage.removeItem('userId'); // Remove from localStorage
        }
        return { ...state, userId };
      });
    
      readonly setUser = this.updater((state, user: User | null) => ({
        ...state,
        user,
      }));

      readonly setLevelStats = this.updater((state, levelStats: LevelDetail | null) => ({
        ...state,
        levelStats,
      }));

    // readonly logout = this.updater((state) => ({
    // ...state,
    // jwtToken: null,
    // userId: null,
    // user: null,
    // isLoggedIn: false,
    // }));

    readonly logout = this.updater((state) => {
        localStorage.removeItem('jwtToken'); // Clear localStorage
        localStorage.removeItem('userId');
        return {
          ...state,
          jwtToken: null,
          userId: null,
          user: null,
          levelStats : null,
          isLoggedIn: false,
        };
      });

    getState(): AuthState {
        let state: AuthState;
        this.state$.pipe(take(1)).subscribe((currentState) => (state = currentState));
        return state!;
      }

}

