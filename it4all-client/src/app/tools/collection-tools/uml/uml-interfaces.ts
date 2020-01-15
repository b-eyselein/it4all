
export interface IUmlClassDiagram {
  classes: IUmlClass[];
  associations: IUmlAssociation[];
  implementations: IUmlImplementation[];
}


export interface IUmlAssociation {
  assocType: UmlAssociationType;
  assocName?: string;
  firstEnd: string;
  firstMult: UmlMultiplicity;
  secondEnd: string;
  secondMult: UmlMultiplicity;
}

export type UmlClassType = ("CLASS" | "INTERFACE" | "ABSTRACT");

export interface IUmlAttribute {
  visibility: UmlVisibility;
  memberName: string;
  memberType: string;
  isStatic: boolean;
  isDerived: boolean;
  isAbstract: boolean;
}


export interface IUmlClass {
  classType: UmlClassType;
  name: string;
  attributes: IUmlAttribute[];
  methods: IUmlMethod[];
}

export type UmlVisibility = ("PUBLIC" | "PACKAGE" | "PROTECTED" | "PRIVATE");

export interface IUmlMethod {
  visibility: UmlVisibility;
  memberName: string;
  memberType: string;
  parameters: string;
  isStatic: boolean;
  isAbstract: boolean;
}

export type KeyValueObjectMap = IKeyValueObject[];

export interface ISampleSolution {
  id: number;
  sample: object;
}


export interface IUmlImplementation {
  subClass: string;
  superClass: string;
}


export interface IKeyValueObject {
  key: string;
  value: string;
}

export type UmlMultiplicity = ("SINGLE" | "UNBOUND");

export interface IUmlExerciseContent {
  toIgnore: string[];
  mappings: KeyValueObjectMap;
  sampleSolutions: ISampleSolution[];
}

export type UmlAssociationType = ("ASSOCIATION" | "AGGREGATION" | "COMPOSITION");