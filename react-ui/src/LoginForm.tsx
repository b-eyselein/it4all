import {Field, Form, Formik} from 'formik';
import {useTranslation} from 'react-i18next';
import {useState} from 'react';
import {LoginMutationVariables, useLoginMutation} from './graphql';
import {useDispatch, useSelector} from 'react-redux';
import {userLoginAction} from './store/actions';
import {currentUserSelector} from './store/store';
import {Navigate} from 'react-router-dom';
import {BulmaInputField} from './BulmaFields';
import classNames from 'classnames';
import * as yup from 'yup';
import {object as yupObject} from 'yup';
import {homeUrl} from './urls';

const loginValuesSchema: yup.SchemaOf<LoginMutationVariables> = yupObject({
  username: yup.string().required(),
  password: yup.string().required()
}).required();

const initialValues: LoginMutationVariables = {username: '', password: ''};

export function LoginForm(): JSX.Element {

  const {t} = useTranslation('common');
  const dispatch = useDispatch();
  const [loginInvalid, setLoginInvalid] = useState<boolean>();
  const [loginMutation, {loading, error}] = useLoginMutation();

  if (useSelector(currentUserSelector)) {
    return <Navigate to={homeUrl}/>;
  }

  function handleSubmit(variables: LoginMutationVariables): void {
    loginMutation({variables})
      .then(({data}) => {
        if (data && data.login) {
          setLoginInvalid(false);
          dispatch(userLoginAction(data.login));
        } else {
          setLoginInvalid(true);
        }
      })
      .catch((error) => console.error(error));
  }

  return (
    <div className="container">
      <h1 className="title is-3 has-text-centered">{t('login')}</h1>

      <Formik initialValues={initialValues} validationSchema={loginValuesSchema} onSubmit={handleSubmit}>
        {() => <Form>

          <Field name="username" id="username" label={t('username')} required autoFocus component={BulmaInputField}/>

          <Field type="password" name="password" id="password" label={t('password')} required component={BulmaInputField}/>

          {loginInvalid && <div className="notification is-warning has-text-centered">{t('invalidUsernamePasswordCombination')}</div>}

          {error && <div className="notification is-danger has-text-centered">{error.message}</div>}

          <button type="submit" className={classNames('button', 'is-link', 'is-fullwidth', {'is-loading': loading})} disabled={loading}>
            {t('login')}
          </button>
        </Form>}
      </Formik>
    </div>
  );
}
