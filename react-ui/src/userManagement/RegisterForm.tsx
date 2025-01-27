import { useTranslation } from 'react-i18next';
import * as yup from 'yup';
import { RegisterValues, useRegisterMutation } from '../graphql';
import { Field, Form, Formik } from 'formik';
import { useSelector } from 'react-redux';
import { Navigate } from 'react-router-dom';
import { homeUrl } from '../urls';
import classNames from 'classnames';
import { newCurrentUserSelector } from '../store';

const registerValuesSchema: yup.ObjectSchema<RegisterValues> = yup.object({
  username: yup.string().required(),
  password: yup.string().required(),
  passwordRepeat: yup.string().required()
}).required();

const initialValues: RegisterValues = { username: '', password: '', passwordRepeat: '' };

export function RegisterForm(): JSX.Element {

  const { t } = useTranslation('common');
  const [register, { data, loading, error }] = useRegisterMutation();

  if (useSelector(newCurrentUserSelector)) {
    return <Navigate to={homeUrl} />;
  }

  function onSubmit(variables: RegisterValues): void {
    register({ variables })
      .catch((error) => console.error(error));
  }

  return (
    <div className="container mx-auto">
      <h1 className="font-bold text-2xl text-center">{t('register')}</h1>

      <Formik initialValues={initialValues} validationSchema={registerValuesSchema} onSubmit={onSubmit}>
        {({ touched, errors }) => <Form>

          <div className="my-2">
            <label htmlFor="username" className="font-bold">{t('username')}*:</label>
            <Field type="text" name="username" id="username" placeholder={t('username')} required autoFocus
              className={classNames('mt-2', 'p-2', 'rounded', 'border', 'w-full',
                touched.username && errors.username ? 'border-red-500' : 'border-slate-300')} />
          </div>

          <div className="my-2">
            <label htmlFor="password" className="font-bold">{t('password')}*:</label>
            <Field type="password" name="password" id="password" placeholder={t('password')} required
              className={classNames('mt-2', 'p-2', 'rounded', 'border', 'w-full',
                touched.password && errors.password ? 'border-red-500' : 'border-slate-300')} />
          </div>

          <div className="my-2">
            <label htmlFor="passwordRepeat" className="font-bold">{t('repeatPassword')}*:</label>
            <Field type="password" name="passwordRepeat" id="passwordRepeat" placeholder={t('repeatPassword')} required
              className={classNames('mt-2', 'p-2', 'rounded', 'border', 'w-full',
                touched.passwordRepeat && errors.passwordRepeat ? 'border-red-500' : 'border-slate-300')} />
          </div>

          {error && <div className="mt-4 p-2 rounded bg-red-500 text-white text-center w-full">{error.message}</div>}

          {data && <div className="mt-4 p-2 rounded bg-green-500 text-white text-center w-full">{t('userRegistered_{{who}}', { who: data.register })}</div>}

          <button type="submit" className="mt-4 p-2 rounded bg-blue-500 text-white w-full" disabled={loading}>{t('register')}</button>
        </Form>}
      </Formik>

    </div>
  );
}
