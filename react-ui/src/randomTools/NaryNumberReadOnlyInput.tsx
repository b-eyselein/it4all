import {NumberingSystem} from './nary';

export interface NaryReadOnlyNumberInput {
  decimalNumber: number;
  numberingSystem: NumberingSystem;
  maxValueForDigits: number;
}


interface IProps {
  labelContent: string;
  naryNumberInput: NaryReadOnlyNumberInput;
}

function chunkSubstr(str: string, size: number): string[] {
  const numChunks = Math.ceil(str.length / size);
  const chunks = new Array(numChunks);

  for (let i = 0, o = 0; i < numChunks; ++i, o += size) {
    chunks[i] = str.substr(o, size);
  }

  return chunks;
}

export function NaryNumberReadOnlyInputComponent({labelContent, naryNumberInput}: IProps): JSX.Element {

  const {maxValueForDigits, numberingSystem, decimalNumber} = naryNumberInput;

  function getSummandNary(): string {
    const radix = numberingSystem.radix;
    const blockSize = radix === 2 ? 4 : 2;

    const minimalDigits: number = Math.log2(maxValueForDigits) / Math.log2(radix);
    const newDigitCount: number = Math.ceil(minimalDigits / blockSize) * blockSize;

    return chunkSubstr(decimalNumber.toString(radix).padStart(newDigitCount, '0'), 4).join(' ');
  }

  return (
    <div className="field has-addons">
      <div className="control">
        <div className="button is-static">{labelContent}</div>
      </div>
      <div className="control is-expanded">
        <input className="input has-text-right numberInput" value={getSummandNary()} placeholder={labelContent} readOnly/>
      </div>
      <div className="control">
        <div className="button is-static">
          <sub>{numberingSystem.radix}</sub>
        </div>
      </div>
    </div>
  );
}
