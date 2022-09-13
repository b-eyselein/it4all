import {configureStore, createSlice, EnhancedStore, PayloadAction} from '@reduxjs/toolkit';
import {LoginResultFragment} from './graphql';
import i18next from 'i18next';

// Login

const userField = 'user';

function initialUserState(): LoginResultFragment | null {
  const currentUserString = localStorage.getItem(userField);

  return currentUserString
    ? JSON.parse(currentUserString) as LoginResultFragment
    : null;
}

const userSlice = createSlice({
  name: 'user',
  initialState: initialUserState,
  reducers: {
    loginUser(state, {payload}: PayloadAction<LoginResultFragment>) {
      localStorage.setItem(userField, JSON.stringify(payload));
      state = payload;
    },
    logoutUser(state) {
      localStorage.removeItem(userField);
      // eslint-disable-next-line
      state = null;
    }
  }
});

export const {loginUser, logoutUser} = userSlice.actions;

// Language

const languageField = 'language';

export type Language = 'en' | 'de';

export const languages: Language[] = ['de', 'en'];

function initialLanguageState(): Language {
  const currentLanguage = localStorage.getItem(languageField);

  return currentLanguage ? currentLanguage as Language : 'en';
}

const languageSlice = createSlice({
  name: 'language',
  initialState: initialLanguageState,
  reducers: {
    changeLanguage(state, {payload}: PayloadAction<Language>) {
      localStorage.setItem(languageField, payload);

      i18next.changeLanguage(payload)
        .catch((err) => console.error(err));

      state = payload;
    }
  }
});

export const {changeLanguage} = languageSlice.actions;

export interface NewStoreState {
  user: LoginResultFragment | null;
  language: Language;
}

export const newStore: EnhancedStore<NewStoreState> = configureStore({
  reducer: {
    user: userSlice.reducer,
    language: languageSlice.reducer
  }
});

export const newCurrentUserSelector: (s: NewStoreState) => LoginResultFragment | null = ({user}) => user;

export const newLanguageSelector: (s: NewStoreState) => Language = ({language}) => language;
