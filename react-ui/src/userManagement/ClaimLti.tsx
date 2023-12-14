import { useEffect } from 'react';
import { Navigate, useParams } from 'react-router-dom';
import { useClaimLtiWebTokenMutation } from '../graphql';
import { useDispatch, useSelector } from 'react-redux';
import { homeUrl } from '../urls';
import { useTranslation } from 'react-i18next';
import { login, newCurrentUserSelector } from '../store';

export function ClaimLti(): JSX.Element {

  const { t } = useTranslation('common');
  const { ltiUuid } = useParams<'ltiUuid'>();
  const [claimLtiWebToken, { error }] = useClaimLtiWebTokenMutation();
  const dispatch = useDispatch();
  const currentUser = useSelector(newCurrentUserSelector);

  if (!ltiUuid) {
    return <Navigate to={homeUrl} />;
  }

  useEffect(() => {
    claimLtiWebToken({ variables: { ltiUuid } })
      .then(({ data }) => {
        if (data && data.claimLtiWebToken) {
          dispatch(login(data.claimLtiWebToken));
        } else {
          console.info('ERROR!');
        }
      })
      .catch((error) => console.error(error));
  }, []);

  if (currentUser) {
    return <Navigate to={homeUrl} />;
  }

  return (
    <div className="container mx-auto">
      <div className="notification is-primary has-text-centered">{t('performingLogin')}...</div>

      {error && <div className="notification is-danger has-text-centered">{error.message}</div>}
    </div>
  );

}
