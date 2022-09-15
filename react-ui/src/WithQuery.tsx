import {MutationResult, QueryResult} from '@apollo/client';
import classNames from 'classnames';
import {useTranslation} from 'react-i18next';

interface IProps<T, V> {
  query: QueryResult<T, V> | MutationResult<T>;
  children: (t: T) => JSX.Element;
  notCalledMessage?: JSX.Element;
}

export function WithQuery<T, V>({query: {data, loading, error, called}, children, notCalledMessage}: IProps<T, V>): JSX.Element {

  const {t} = useTranslation('common');

  if (!data) {
    return (
      <div className={classNames('my-2', 'p-2', 'rounded', 'text-center','text-white', {'is-info': loading, 'is-warning': !!error, 'bg-cyan-500': !called})}>
        {loading && <span>{t('loading_data')}...</span>}
        {error && error.message}
        {!called && (notCalledMessage || <p>{t('mutationNotYetCalled')}</p>)}
      </div>
    );
  } else {
    return children(data);
  }

}
