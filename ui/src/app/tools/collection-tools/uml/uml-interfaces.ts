
export interface IUmlClassDiagram {
  classes: IUmlClass[];
  associations: IUmlAssociation[];
  implementations: IUmlImplementation[];
}


export interface IAssociationMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IUmlAssociationMatch[];
  points: number;
  maxPoints: number;
}


export interface IUmlImplementationMatch {
  matchType: MatchType;
  userArg?: IUmlImplementation;
  sampleArg?: IUmlImplementation;
}


export interface IUmlAssociation {
  assocType: UmlAssociationType;
  assocName?: string;
  firstEnd: string;
  firstMult: UmlMultiplicity;
  secondEnd: string;
  secondMult: UmlMultiplicity;
}

export type UmlMultiplicity = ("SINGLE" | "UNBOUND");

export interface IUmlAssociationMatch {
  matchType: MatchType;
  userArg?: IUmlAssociation;
  sampleArg?: IUmlAssociation;
  maybeAnalysisResult?: IUmlAssociationAnalysisResult;
}


export interface IUmlMethod {
  visibility: UmlVisibility;
  memberName: string;
  memberType: string;
  parameters: string;
  isStatic: boolean;
  isAbstract: boolean;
}


export interface IUmlAssociationAnalysisResult {
  endsParallel: boolean;
  assocTypeEqual: boolean;
  correctAssocType: UmlAssociationType;
  multiplicitiesEqual: boolean;
}


export interface IUmlAttributeAnalysisResult {
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


export interface IUmlMethodAnalysisResult {
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


export interface IUmlCompleteResult {
  classResult?: IClassMatchingResult;
  assocResult?: IAssociationMatchingResult;
  implResult?: IImplementationMatchingResult;
  points: IPoints;
  maxPoints: IPoints;
  solutionSaved: boolean;
}

export type UmlClassType = ("CLASS" | "INTERFACE" | "ABSTRACT");

export interface IAttributeMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IUmlAttributeMatch[];
  points: number;
  maxPoints: number;
}


export interface IUmlMethodMatch {
  matchType: MatchType;
  userArg?: IUmlMethod;
  sampleArg?: IUmlMethod;
  maybeAnalysisResult?: IUmlMethodAnalysisResult;
}


export interface IUmlClassMatch {
  matchType: MatchType;
  userArg?: IUmlClass;
  sampleArg?: IUmlClass;
  compAM: boolean;
  analysisResult?: IUmlClassMatchAnalysisResult;
}


export interface IImplementationMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IUmlImplementationMatch[];
  points: number;
  maxPoints: number;
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


export interface IUmlAttributeMatch {
  matchType: MatchType;
  userArg?: IUmlAttribute;
  sampleArg?: IUmlAttribute;
  maybeAnalysisResult?: IUmlAttributeAnalysisResult;
}

export type UmlVisibility = ("PUBLIC" | "PACKAGE" | "PROTECTED" | "PRIVATE");
export type MatchType = ("SUCCESSFUL_MATCH" | "PARTIAL_MATCH" | "UNSUCCESSFUL_MATCH" | "ONLY_USER" | "ONLY_SAMPLE");

export interface IUmlClassMatchAnalysisResult {
  classTypeCorrect: boolean;
  correctClassType: UmlClassType;
  maybeAttributeMatchingResult?: IAttributeMatchingResult;
  maybeMethodMatchingResult?: IMethodMatchingResult;
}


export interface IPoints {
  quarters: number;
}


export interface IClassMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IUmlClassMatch[];
  points: number;
  maxPoints: number;
}


export interface IUmlImplementation {
  subClass: string;
  superClass: string;
}

export type UmlAssociationType = ("ASSOCIATION" | "AGGREGATION" | "COMPOSITION");

export interface IMethodMatchingResult {
  matchName: string;
  matchSingularName: string;
  allMatches: IUmlMethodMatch[];
  points: number;
  maxPoints: number;
}
