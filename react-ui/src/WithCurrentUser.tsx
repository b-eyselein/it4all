import {useSelector} from 'react-redux';
import {LoginResultFragment} from './graphql';
import {Navigate} from 'react-router-dom';
import {loginUrl} from './urls';
import {newCurrentUserSelector} from './newStore';

interface IProps {
  children: (currentUser: LoginResultFragment) => JSX.Element;
  noLoginOption?: JSX.Element;
}

// FIXME: use?
export function WithCurrentUser({children, noLoginOption}: IProps): JSX.Element {

  const currentUser = useSelector(newCurrentUserSelector);

  if (currentUser) {
    return children(currentUser);
  } else if (noLoginOption) {
    return noLoginOption;
  } else {
    return <Navigate to={loginUrl}/>;
  }
}
