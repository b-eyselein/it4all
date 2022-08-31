import {useTranslation} from 'react-i18next';
import {object as yupObject, SchemaOf, string as yupString} from 'yup';
import {RegisterValues, useRegisterMutation} from './graphql';
import {Field, Form, Formik} from 'formik';
import {useSelector} from 'react-redux';
import {currentUserSelector} from './store/store';
import {Navigate} from 'react-router-dom';
import {homeUrl} from './urls';
import {BulmaInputField} from './BulmaFields';
import classNames from 'classnames';

const registerValuesSchema: SchemaOf<RegisterValues> = yupObject({
  username: yupString().required(),
  firstPassword: yupString().required(),
  secondPassword: yupString().required()
}).required();

const initialValues: RegisterValues = {username: '', firstPassword: '', secondPassword: ''};

export function RegisterForm(): JSX.Element {

  const {t} = useTranslation('common');
  const [register, {data, loading, error}] = useRegisterMutation();

  if (useSelector(currentUserSelector)) {
    return <Navigate to={homeUrl}/>;
  }

  function onSubmit(variables: RegisterValues): void {
    register({variables})
      .catch((error) => console.error(error));
  }

  return (
    <div className="container">
      <h1 className="title is-3 has-text-centered">{t('register')}</h1>

      <Formik initialValues={initialValues} validationSchema={registerValuesSchema} onSubmit={onSubmit}>
        {() => <Form>

          <Field name="username" id="username" label={t('username')} required autoFocus component={BulmaInputField}/>

          <Field type="password" name="firstPassword" id="firstPassword" label={t('firstPassword')} required component={BulmaInputField}/>

          <Field type="password" name="secondPassword" id="secondPassword" label={t('repeatPassword')} required component={BulmaInputField}/>

          {error && <div className="notification is-danger has-text-centered">{error.message}</div>}

          {data && <div className="notification is-success has-text-centered">{t('userRegistered_{{who}}', {who: data.register})}</div>}

          <button type="submit" className={classNames('button', 'is-link', 'is-fullwidth', {'is-loading': loading})} disabled={loading}>
            {t('register')}
          </button>
        </Form>}
      </Formik>

    </div>
  );
}
