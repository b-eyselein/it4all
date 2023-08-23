import {useSelector} from 'react-redux';
import {Navigate} from 'react-router-dom';
import {loginUrl} from './urls';
import {newCurrentUserSelector, User} from './store';
import {ReactElement} from 'react';

interface IProps {
  children: (currentUser: User) => ReactElement;
  noLoginOption?: ReactElement;
}

// FIXME: use?
export function WithCurrentUser({children, noLoginOption}: IProps): ReactElement {

  const currentUser = useSelector(newCurrentUserSelector);

  if (currentUser) {
    return children(currentUser);
  } else if (noLoginOption) {
    return noLoginOption;
  } else {
    return <Navigate to={loginUrl}/>;
  }
}
