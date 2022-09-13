import {Field, Form, Formik} from 'formik';
import {useTranslation} from 'react-i18next';
import {useState} from 'react';
import {LoginMutationVariables, useLoginMutation} from './graphql';
import {useDispatch, useSelector} from 'react-redux';
import {Navigate} from 'react-router-dom';
import classNames from 'classnames';
import {object as yupObject, SchemaOf, string as yupString} from 'yup';
import {homeUrl} from './urls';
import {loginUser, newCurrentUserSelector} from './newStore';

const loginValuesSchema: SchemaOf<LoginMutationVariables> = yupObject({
  username: yupString().required(),
  password: yupString().required()
}).required();

const initialValues: LoginMutationVariables = {username: '', password: ''};

export function LoginForm(): JSX.Element {

  const {t} = useTranslation('common');
  const dispatch = useDispatch();
  const [loginInvalid, setLoginInvalid] = useState<boolean>();
  const [loginMutation, {loading, error}] = useLoginMutation();

  if (useSelector(newCurrentUserSelector)) {
    return <Navigate to={homeUrl}/>;
  }

  function handleSubmit(variables: LoginMutationVariables): void {
    loginMutation({variables})
      .then(({data}) => {
        if (data) {
          setLoginInvalid(false);
          dispatch(loginUser(data.login));
        } else {
          setLoginInvalid(true);
        }
      })
      .catch((error) => console.error(error));
  }

  return (
    <div className="container mx-auto">
      <h1 className="font-bold text-2xl text-center">{t('login')}</h1>

      <Formik initialValues={initialValues} validationSchema={loginValuesSchema} onSubmit={handleSubmit}>
        {({touched, errors}) => <Form>

          <div className="my-2">
            <label htmlFor="username" className="font-bold">{t('username')}*:</label>
            <Field type="text" name="username" id="username" placeholder={t('username')} required autoFocus
                   className={classNames('mt-2', 'p-2', 'rounded', 'border', 'w-full',
                     touched.username && errors.username ? 'border-red-500' : 'border-slate-300')}/>
          </div>

          <div className="my-2">
            <label htmlFor="password" className="font-bold">{t('password')}*:</label>
            <Field type="password" name="password" id="password" placeholder={t('password')} required
                   className={classNames('mt-2', 'p-2', 'rounded', 'border', 'w-full',
                     touched.password && errors.password ? 'border-red-500' : 'border-slate-300')}/>
          </div>

          {loginInvalid && <div className="p-2">{t('invalidUsernamePasswordCombination')}</div>}

          {error && <div className="mt-4 p-2 rounded bg-red-500 text-white text-center w-full">{error.message}</div>}

          <button type="submit" className="mt-4 p-2 rounded bg-blue-500 text-white w-full" disabled={loading}>{t('login')}</button>
        </Form>}
      </Formik>
    </div>
  );
}
