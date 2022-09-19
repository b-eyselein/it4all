import {configureStore, createSlice, EnhancedStore, PayloadAction} from '@reduxjs/toolkit';
import i18next from 'i18next';

function readJsonFromLocalStorage<T>(key: string, defaultValue: T): T {
  const readValue = localStorage.getItem(key);

  return readValue ? JSON.parse(readValue) : defaultValue;
}

// User slice

export interface User {
  sub: string;
  token: string;
}

function userFromToken(token: string): Omit<User, 'token'> {
  return JSON.parse(
    atob(
      token.split('.')[1]
        .replace(/-/g, '+')
        .replace(/_/g, '/')
    )
  );
}

const userField = 'user';

const userSlice = createSlice({
  name: 'user',
  initialState: () => ({user: readJsonFromLocalStorage<User | null>(userField, null)}),
  reducers: {
    login(state, {payload}: PayloadAction<string>) {
      const user = {...userFromToken(payload), token: payload};
      localStorage.setItem(userField, JSON.stringify(user));
      state.user = user;
    },
    logout(state) {
      localStorage.removeItem(userField);
      state.user = null;
    }
  }
});

export const {login, logout} = userSlice.actions;

// Language slice

const languageField = 'language';

export type Language = 'en' | 'de';
export const languages: Language[] = ['de', 'en'];

const languageSlice = createSlice({
  name: 'language',
  initialState: (): { language: Language } => ({language: localStorage.getItem(languageField) as Language || 'en'}),
  reducers: {
    changeLanguage(state, {payload}: PayloadAction<Language>) {
      localStorage.setItem(languageField, payload);

      i18next.changeLanguage(payload)
        .catch((err) => console.error(err));

      state.language = payload;
    }
  }
});

export const {changeLanguage} = languageSlice.actions;

// Store

export interface NewStoreState {
  user: { user: User | null };
  language: { language: Language };
}

export const store: EnhancedStore<NewStoreState> = configureStore({
  reducer: {
    user: userSlice.reducer,
    language: languageSlice.reducer
  }
});

export const newCurrentUserSelector: (s: NewStoreState) => User | null = ({user}) => user.user;

export const newLanguageSelector: (s: NewStoreState) => Language = ({language}) => language.language;
