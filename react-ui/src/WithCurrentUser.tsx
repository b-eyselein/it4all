import {useSelector} from 'react-redux';
import {Navigate} from 'react-router-dom';
import {loginUrl} from './urls';
import {newCurrentUserSelector, User} from './store';

interface IProps {
  children: (currentUser: User) => JSX.Element;
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
