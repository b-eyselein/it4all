import {checkNever} from '../../../helpers';

type EmptyWordType = { _type: 'EmptyWord' };

export const EmptyWord: EmptyWordType = {
  _type: 'EmptyWord'
};


export interface Terminal {
  _type: 'Terminal';
  value: string;
}

export function terminal(value: string): Terminal {
  return {_type: 'Terminal', value};
}


export interface Variable {
  _type: 'Variable';
  value: string;
}

export function variable(value: string): Variable {
  return {_type: 'Variable', value};
}


export type UnaryOperator = '?' | '*' | '+';


export interface Optional {
  _type: 'Optional';
  child: ExtendedBackusNaurFormGrammarElement;
}

export function optional(child: ExtendedBackusNaurFormGrammarElement): Optional {
  return {_type: 'Optional', child};
}


export interface RepetitionAny {
  _type: 'RepetitionAny';
  child: ExtendedBackusNaurFormGrammarElement;
}

export function repetitionAny(child: ExtendedBackusNaurFormGrammarElement): RepetitionAny {
  return {_type: 'RepetitionAny', child};
}


export interface RepetitionOne {
  _type: 'RepetitionOne';
  child: ExtendedBackusNaurFormGrammarElement;
}

export function repetitionOne(child: ExtendedBackusNaurFormGrammarElement): RepetitionOne {
  return {_type: 'RepetitionOne', child};
}


export interface Alternative<ElType> {
  _type: 'Alternative';
  children: ElType[];
}

export function alternative<ElType>(...children: ElType[]): Alternative<ElType> {
  return {_type: 'Alternative', children};
}


export interface Sequence<ElType> {
  _type: 'Sequence';
  children: ElType[];
}

export function sequence<ElType>(...children: ElType[]): Sequence<ElType> {
  return {_type: 'Sequence', children};
}


export type BasicGrammarElement = EmptyWordType | Variable | Terminal;

export type BaseNormalizedSequence = Sequence<BasicGrammarElement>;

export type BaseNormalizedFormGrammarElement = Alternative<BaseNormalizedSequence>;

export type BackusNaurFormElement = BasicGrammarElement | Alternative<BackusNaurFormElement> | Sequence<BackusNaurFormElement>;

export type ExtendedBackusNaurFormGrammarElement =
  BasicGrammarElement
  | Alternative<ExtendedBackusNaurFormGrammarElement>
  | Sequence<ExtendedBackusNaurFormGrammarElement>
  | Optional
  | RepetitionAny
  | RepetitionOne;

// Helper functions

export function stringifyGrammarElement(ge: ExtendedBackusNaurFormGrammarElement): string {
  if (ge._type === 'Sequence') {
    return ge.children.map(stringifyGrammarElement).join(' ');
  } else if (ge._type === 'Alternative') {
    return ge.children.map(stringifyGrammarElement).join(' | ');
  } else if (ge._type === 'RepetitionOne') {
    return stringifyGrammarElement(ge.child) + '+';
  } else if (ge._type === 'RepetitionAny') {
    return stringifyGrammarElement(ge.child) + '*';
  } else if (ge._type === 'Optional') {
    return stringifyGrammarElement(ge.child) + '?';
  } else if (ge._type === 'Variable') {
    return ge.value;
  } else if (ge._type === 'Terminal') {
    return `'${ge.value}'`;
  } else if (ge._type === 'EmptyWord') {
    // Empty word...
    return 'eps';
  } else {
    return checkNever(ge, '');
  }
}

export function getVariablesFromBasicGrammarElement(ge: BasicGrammarElement): Variable[] {
  return ge._type === 'Variable' ? [ge] : [];
}

export function getVariablesFromBaseNormalizedFormGrammarElement(ge: BaseNormalizedFormGrammarElement): Variable[] {
  return ge.children.flatMap(({children}) => children.flatMap(getVariablesFromBasicGrammarElement));
}

export function getVariablesFromBackusNaurFormGrammarElement(ge: BackusNaurFormElement): Variable[] {
  return ge._type === 'Sequence' || ge._type === 'Alternative'
    ? ge.children.flatMap(getVariablesFromGrammarElement)
    : getVariablesFromBasicGrammarElement(ge);
}

export function getVariablesFromGrammarElement(ge: ExtendedBackusNaurFormGrammarElement): Variable[] {
  if (ge._type === 'RepetitionOne' || ge._type === 'RepetitionAny' || ge._type === 'Optional') {
    return getVariablesFromGrammarElement(ge.child);
  } else if (ge._type === 'Alternative' || ge._type === 'Sequence') {
    return ge.children.flatMap(getVariablesFromGrammarElement);
  } else {
    return getVariablesFromBasicGrammarElement(ge);
  }
}
