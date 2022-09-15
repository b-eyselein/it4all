import {useState} from 'react';
import {useTranslation} from 'react-i18next';

interface IProps {
  children: () => JSX.Element[];
}

export function SampleSolutionTabContent({children}: IProps): JSX.Element {

  const {t} = useTranslation('common');
  const [showSampleSolutions, setShowSampleSolutions] = useState(false);

  return (
    <>
      <button className="mt-2 mb-4 p-2 rounded bg-cyan-500 w-full" onClick={() => setShowSampleSolutions((value) => !value)}>
        {showSampleSolutions ? t('hideSampleSolutions') : t('showSampleSolutions')}
      </button>

      {showSampleSolutions && children()}
    </>
  );
}
