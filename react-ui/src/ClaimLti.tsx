import React, {useEffect} from 'react';
import {Navigate, useParams} from 'react-router-dom';
import {userLoginAction} from './store/actions';
import {useClaimLtiWebTokenMutation} from './graphql';
import {useDispatch, useSelector} from 'react-redux';
import {currentUserSelector} from './store/store';
import {homeUrl} from './urls';
import {useTranslation} from 'react-i18next';

export function ClaimLti(): JSX.Element {

  const {ltiUuid} = useParams<'ltiUuid'>();
  const dispatch = useDispatch();
  const [claimLtiWebToken, {error}] = useClaimLtiWebTokenMutation();
  const {t} = useTranslation('common');
  const currentUser = useSelector(currentUserSelector);

  if (!ltiUuid) {
    return <Navigate to={homeUrl}/>;
  }

  useEffect(() => {
    claimLtiWebToken({variables: {ltiUuid}})
      .then(({data}) => {
        if (data && data.claimLtiWebToken) {
          dispatch(userLoginAction(data.claimLtiWebToken));
        } else {
          console.info('ERROR!');
        }
      })
      .catch((error) => console.error(error));
  }, []);

  if (currentUser) {
    return <Navigate to={homeUrl}/>;
  }

  return (
    <div className="container">
      <div className="notification is-primary has-text-centered">{t('performingLogin')}...</div>

      {error && <div className="notification is-danger has-text-centered">{error.message}</div>}
    </div>
  );

}
