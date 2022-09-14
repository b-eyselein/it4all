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
    chunks[i] = str.substring(o, o + size);
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
    <div className="flex">
      <div className="p-2 rounded-l border-l border-y border-slate-400 bg-slate-100">{labelContent}</div>
      <input className="flex-grow p-2 border border-slate-400 text-right font-mono" value={getSummandNary()} placeholder={labelContent} readOnly/>
      <div className="p-2 rounded-r border-r border-y border-slate-400 bg-slate-100">
        <sub>{numberingSystem.radix}</sub>
      </div>
    </div>
  );
}
