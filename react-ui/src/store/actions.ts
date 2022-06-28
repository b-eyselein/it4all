import {Action} from 'redux';
import {LoginResultFragment} from '../graphql';

// User login

export const USER_LOGIN = 'USER_LOGIN';

interface UserLoginAction extends Action<typeof USER_LOGIN> {
  user: LoginResultFragment;
}

export function userLoginAction(user: LoginResultFragment): UserLoginAction {
  return {type: USER_LOGIN, user};
}

// User logout

export const USER_LOGOUT = 'USER_LOGOUT';

type UserLogoutAction = Action<typeof USER_LOGOUT>;

export const userLogoutAction: UserLogoutAction = {type: USER_LOGOUT};

// Change language

export const CHANGE_LANGUAGE = 'CHANGE_LANGUAGE';

interface ChangeLanguageAction extends Action<typeof CHANGE_LANGUAGE> {
  languageCode: string;
}

export function changeLanguageAction(languageCode: string): ChangeLanguageAction {
  return {type: CHANGE_LANGUAGE, languageCode};
}

// Actions

export type StoreAction = UserLoginAction | UserLogoutAction | ChangeLanguageAction;
