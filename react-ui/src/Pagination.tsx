import {useTranslation} from 'react-i18next';
import classNames from 'classnames';

interface IProps {
  pages: number[];
  maxPage: number;
  currentPage: number;
  setCurrentPage: (value: number) => void;
}

export function Pagination({pages, maxPage, currentPage, setCurrentPage}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  return (
    <nav className="pagination is-centered" role="navigation" aria-label="pagination">
      <button className="button pagination-previous" onClick={() => setCurrentPage(currentPage - 1)} disabled={currentPage < 1}>
        {t('previous')}
      </button>
      <ul className="pagination-list">
        {pages.map((page) => <li key={page}>
          <button className={classNames('button', 'pagination-link', {'is-current': page === currentPage})} onClick={() => setCurrentPage(page)}>
            {page}
          </button>
        </li>)}
      </ul>
      <button className="button pagination-next" onClick={() => setCurrentPage(currentPage + 1)} disabled={currentPage >= maxPage - 1}>
        {t('next')}
      </button>
    </nav>
  );
}
