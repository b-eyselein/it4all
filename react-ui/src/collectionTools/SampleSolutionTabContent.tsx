import { useState } from 'react';

interface IProps {
  children: () => JSX.Element[];
}

export function SampleSolutionTabContent({children}: IProps): JSX.Element {

  const [showSampleSolutions, setShowSampleSolutions] = useState(false);

  return (
    <div>
      <div className="buttons">
        <button className="button is-primary is-fullwidth" onClick={() => setShowSampleSolutions((value) => !value)}>
          Musterlösungen {showSampleSolutions ? 'ausblenden' : 'anzeigen'}
        </button>
      </div>

      {showSampleSolutions && children()}
    </div>
  );
}
