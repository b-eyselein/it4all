import {QueryResult} from "@apollo/client";
import classNames from "classnames";
import React from "react";
import {useTranslation} from "react-i18next";

interface IProps<T> {
  query: QueryResult<T>;
  children: (t: T) => JSX.Element;
}

export function WithQuery<T>({query: {data, loading, error}, children}: IProps<T>): JSX.Element {

  const {t} = useTranslation('common');

  if (!data) {
    return <div className={classNames('notification', 'has-text-centered', {'is-info': loading, 'is-warning': !!error})}>
      {loading && <span>{t('loading_data')}...</span>}
      {error && error.message}
    </div>
  } else {
    return children(data);
  }

}
