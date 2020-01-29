
export interface IAssociationMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IUmlAssociationMatch[];
  points: number;
  maxPoints: number;
}


export interface IImplementationMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IUmlImplementationMatch[];
  points: number;
  maxPoints: number;
}

export type UmlClassType = ("CLASS" | "INTERFACE" | "ABSTRACT");

export interface IMethodMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IUmlMethodMatch[];
  points: number;
  maxPoints: number;
}


export interface IUmlAttributeAnalysisResult {
  matchType: MatchType;
  visibilityComparison: boolean;
  correctVisibility: UmlVisibility;
  typeComparison: boolean;
  correctType: string;
  staticCorrect: boolean;
  correctStatic: boolean;
  derivedCorrect: boolean;
  correctDerived: boolean;
  abstractCorrect: boolean;
  correctAbstract: boolean;
}


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


export interface IUmlMethod {
  visibility: UmlVisibility;
  memberName: string;
  memberType: string;
  parameters: string;
  isStatic: boolean;
  isAbstract: boolean;
}


export interface IUmlMethodMatch {
  userArg?: IUmlMethod;
  sampleArg?: IUmlMethod;
  maybeAnalysisResult?: IUmlMethodAnalysisResult;
}

export type KeyValueObjectMap = IKeyValueObject[];
export type MatchType = (IONLY_SAMPLE | IUNSUCCESSFUL_MATCH | IONLY_USER | IPARTIAL_MATCH | ISUCCESSFUL_MATCH);

export interface IGenericAnalysisResult {
  matchType: MatchType;
}


export interface ISampleSolution {
  id: number;
  sample: object;
}


export interface IPoints {
  quarters: number;
}


export interface IUmlCompleteResult {
  classResult?: IClassMatchingResult;
  assocResult?: IAssociationMatchingResult;
  implResult?: IImplementationMatchingResult;
  points: IPoints;
  maxPoints: IPoints;
  solutionSaved: boolean;
}



export interface IUmlClassDiagram {
  classes: IUmlClass[];
  associations: IUmlAssociation[];
  implementations: IUmlImplementation[];
}


export interface IAttributeMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IUmlAttributeMatch[];
  points: number;
  maxPoints: number;
}


export interface IUmlAssociation {
  assocType: UmlAssociationType;
  assocName?: string;
  firstEnd: string;
  firstMult: UmlMultiplicity;
  secondEnd: string;
  secondMult: UmlMultiplicity;
}


export interface IUmlClassMatch {
  userArg?: IUmlClass;
  sampleArg?: IUmlClass;
  compAM: boolean;
  analysisResult?: IUmlClassMatchAnalysisResult;
}



export interface IUmlAssociationMatch {
  userArg?: IUmlAssociation;
  sampleArg?: IUmlAssociation;
  maybeAnalysisResult?: IUmlAssociationAnalysisResult;
}


export interface IKeyValueObject {
  key: string;
  value: string;
}

export type UmlMultiplicity = ("SINGLE" | "UNBOUND");
export type UmlVisibility = ("PUBLIC" | "PACKAGE" | "PROTECTED" | "PRIVATE");
export type MatchType = ("SUCCESSFUL_MATCH" | "PARTIAL_MATCH" | "UNSUCCESSFUL_MATCH" | "ONLY_USER" | "ONLY_SAMPLE");

export interface IUmlClassMatchAnalysisResult {
  matchType: MatchType;
  classTypeCorrect: boolean;
  correctClassType: UmlClassType;
  maybeAttributeMatchingResult?: IAttributeMatchingResult;
  maybeMethodMatchingResult?: IMethodMatchingResult;
}



export interface IUmlAttributeMatch {
  userArg?: IUmlAttribute;
  sampleArg?: IUmlAttribute;
  maybeAnalysisResult?: IUmlAttributeAnalysisResult;
}


export interface IClassMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IUmlClassMatch[];
  points: number;
  maxPoints: number;
}


export interface IUmlMethodAnalysisResult {
  matchType: MatchType;
  visibilityComparison: boolean;
  correctVisibility: UmlVisibility;
  typeComparison: boolean;
  correctType: string;
  parameterComparison: boolean;
  correctParameters: string;
  staticCorrect: boolean;
  correctStatic: boolean;
  abstractCorrect: boolean;
  correctAbstract: boolean;
}


export interface IUmlAssociationAnalysisResult {
  matchType: MatchType;
  endsParallel: boolean;
  assocTypeEqual: boolean;
  correctAssocType: UmlAssociationType;
  multiplicitiesEqual: boolean;
}


export interface IUmlImplementation {
  subClass: string;
  superClass: string;
}



export interface IUmlImplementationMatch {
  userArg?: IUmlImplementation;
  sampleArg?: IUmlImplementation;
  analysisResult: IGenericAnalysisResult;
}


export interface IUmlExerciseContent {
  toIgnore: string[];
  mappings: KeyValueObjectMap;
  sampleSolutions: ISampleSolution[];
}

export type UmlAssociationType = ("ASSOCIATION" | "AGGREGATION" | "COMPOSITION");
