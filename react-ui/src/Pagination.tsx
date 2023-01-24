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
    <nav className="my-4 flex justify-center">
      <button className="mx-1 px-4 py-2 rounded border border-slate-500" onClick={() => setCurrentPage(currentPage - 1)} disabled={currentPage < 1}>
        {t('previous')}
      </button>
      {pages.map((page) =>
        <button key={page} className={classNames('mx-1 px-4 py-2 rounded', page === currentPage ? 'bg-blue-600 text-white' : 'border border-slate-500')}
                onClick={() => setCurrentPage(page)}>
          {page}
        </button>)}
      <button className="mx-1 px-4 py-2 rounded border border-slate-500" onClick={() => setCurrentPage(currentPage + 1)} disabled={currentPage >= maxPage - 1}>
        {t('next')}
      </button>
    </nav>
  );
}
