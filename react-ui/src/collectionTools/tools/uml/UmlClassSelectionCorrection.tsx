import {UmlClassSelectionCorrectionResult} from './UmlClassSelection';

export function UmlClassSelectionCorrection({correctClasses, missingClasses, wrongClasses}: UmlClassSelectionCorrectionResult): JSX.Element {

  return (
    <>
      <h2 className="mb-2 font-bold text-center">Korrektur</h2>

      <div className="grid grid-cols-3">
        <div>
          <h3 className="font-bold text-center">Korrekte Klassen:</h3>
          <ul>
            {correctClasses.map((name) => <li key={name}>&#10004;&nbsp;<code>{name}</code></li>)}
          </ul>
        </div>

        <div>
          <h3 className="font-bold text-center">Fehlende Klassen:</h3>
          <ul>
            {missingClasses.map((name) => <li key={name}>&#10008;&nbsp;<code>{name}</code></li>)}
          </ul>
        </div>

        <div>
          <h3 className="font-bold text-center">Falsche Klassen</h3>
          <ul>
            {wrongClasses.map((name) => <li key={name}>&#10067;&nbsp;<code>{name}</code></li>)}
          </ul>
        </div>
      </div>
    </>
  );
}
