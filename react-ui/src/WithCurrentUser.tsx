import {useSelector} from 'react-redux';
import {currentUserSelector} from './store/store';
import {LoginResultFragment} from './graphql';
import {Navigate} from 'react-router-dom';
import {loginUrl} from './urls';

interface IProps {
  children: (currentUser: LoginResultFragment) => JSX.Element;
  noLoginOption?: JSX.Element;
}

// FIXME: use?
export function WithCurrentUser({children, noLoginOption}: IProps): JSX.Element {

  const currentUser = useSelector(currentUserSelector);

  if (currentUser) {
    return children(currentUser);
  } else if (noLoginOption) {
    return noLoginOption;
  } else {
    return <Navigate to={loginUrl}/>;
  }
}
