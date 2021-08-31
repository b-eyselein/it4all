export interface Terminal {
  type: 'Terminal';
  value: string;
}

export function terminal(value: string): Terminal {
  return {type: 'Terminal', value};
}


export interface Variable {
  type: 'Variable';
  value: string;
}

export function variable(value: string): Variable {
  return {type: 'Variable', value};
}


export type UnaryOperator = '?' | '*' | '+';


export interface Optional {
  type: 'Optional';
  child: GrammarElement;
}

export function optional(child: GrammarElement): Optional {
  return {type: 'Optional', child};
}


export interface RepetitionAny {
  type: 'RepetitionAny';
  child: GrammarElement;
}

export function repetitionAny(child: GrammarElement): RepetitionAny {
  return {type: 'RepetitionAny', child};
}


export interface RepetitionOne {
  type: 'RepetitionOne';
  child: GrammarElement;
}

export function repetitionOne(child: GrammarElement): RepetitionOne {
  return {type: 'RepetitionOne', child};
}


export interface Alternative {
  type: 'Alternative';
  children: GrammarElement[];
}

export function alternative(children: GrammarElement[]): Alternative {
  return {type: 'Alternative', children};
}


export interface Sequence {
  type: 'Sequence';
  children: GrammarElement[];
}

export function sequence(children: GrammarElement[]): Sequence {
  return {type: 'Sequence', children};
}

export type GrammarElement = Terminal | Variable | Optional | RepetitionAny | RepetitionOne | Alternative | Sequence;

export interface GrammarRule {
  variable: string;
  right: GrammarElement;
}

export function stringifyGrammarElement(ge: GrammarElement): string {
  if (ge.type === 'Sequence') {
    return ge.children.map(stringifyGrammarElement).join(' ');
  } else if (ge.type === 'Alternative') {
    return ge.children.map(stringifyGrammarElement).join(' | ');
  } else if (ge.type === 'RepetitionOne') {
    return stringifyGrammarElement(ge.child) + '+';
  } else if (ge.type === 'RepetitionAny') {
    return stringifyGrammarElement(ge.child) + '*';
  } else if (ge.type === 'Optional') {
    return stringifyGrammarElement(ge.child) + '?';
  } else if (ge.type === 'Variable') {
    return ge.value;
  } else /* if(ge.type === 'Terminal') */ {
    return `'${ge.value}'`;
  }
}

export function getVariablesFromGrammarElement(ge: GrammarElement): Variable[] {
  if (ge.type === 'Sequence') {
    return ge.children.flatMap(getVariablesFromGrammarElement);
  } else if (ge.type === 'Alternative') {
    return ge.children.flatMap(getVariablesFromGrammarElement);
  } else if (ge.type === 'RepetitionOne') {
    return getVariablesFromGrammarElement(ge.child);
  } else if (ge.type === 'RepetitionAny') {
    return getVariablesFromGrammarElement(ge.child);
  } else if (ge.type === 'Optional') {
    return getVariablesFromGrammarElement(ge.child);
  } else if (ge.type === 'Variable') {
    return [ge];
  } else /* if(ge.type === 'Terminal') */ {
    return [];
  }
}
