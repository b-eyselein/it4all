import React from "react";

interface IProps {
  showSampleSolutions: boolean;
  toggleSampleSolutions: () => void;
  renderSampleSolutions: () => JSX.Element[];
}

export function SampleSolutionTabContent({showSampleSolutions, toggleSampleSolutions, renderSampleSolutions}: IProps): JSX.Element {
  return (
    <div>
      <div className="buttons">
        <button className="button is-primary is-fullwidth" onClick={toggleSampleSolutions}>
          Musterlösungen {showSampleSolutions ? 'ausblenden' : 'anzeigen'}
        </button>
      </div>

      {showSampleSolutions && renderSampleSolutions()}
    </div>
  );
}
