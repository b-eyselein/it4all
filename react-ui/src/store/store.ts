import {createStore} from 'redux';
import {LoggedInUserWithTokenFragment} from '../graphql';
import {CHANGE_LANGUAGE, StoreAction, USER_LOGIN, USER_LOGOUT} from './actions';
import i18next from 'i18next';

const userField = 'user';
const languageField = 'language';

interface StoreState {
  currentUser?: LoggedInUserWithTokenFragment;
  chosenLanguage?: string;
}

function rootReducer(store: StoreState = {}, action: StoreAction): StoreState {
  switch (action.type) {
  case USER_LOGIN:
    localStorage.setItem(userField, JSON.stringify(action.user));
    return {...store, currentUser: action.user};
  case USER_LOGOUT:
    localStorage.removeItem(userField);
    return {...store, currentUser: undefined};
  case CHANGE_LANGUAGE:
    localStorage.setItem(languageField, action.languageCode);

    // noinspection JSIgnoredPromiseFromCall
    i18next.changeLanguage(action.languageCode);

    return {...store, chosenLanguage: action.languageCode};
  default:
    return store;
  }
}

function initialState(): StoreState {
  const currentUserString = localStorage.getItem(userField);
  const chosenLanguageString = localStorage.getItem(languageField);

  return {
    currentUser: currentUserString ? JSON.parse(currentUserString) : undefined,
    chosenLanguage: chosenLanguageString ? chosenLanguageString : undefined
  };
}

export const store = createStore(rootReducer, initialState());

export const currentUserSelector: (store: StoreState) => LoggedInUserWithTokenFragment | undefined = (store) => store.currentUser;
