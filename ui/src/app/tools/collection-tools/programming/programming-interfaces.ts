
export interface IUmlClassDiagram {
  classes: IUmlClass[];
  associations: IUmlAssociation[];
  implementations: IUmlImplementation[];
}


export interface IProgTestData {
  id: number;
  input: any;
  output: any;
}


export interface IUmlAssociation {
  assocType: UmlAssociationType;
  assocName?: string;
  firstEnd: string;
  firstMult: UmlMultiplicity;
  secondEnd: string;
  secondMult: UmlMultiplicity;
}


export interface IProgInput {
  id: number;
  inputName: string;
  inputType: any;
}

export type UmlClassType = ("CLASS" | "INTERFACE" | "ABSTRACT");

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


export interface IExerciseFile {
  name: string;
  resourcePath: string;
  fileType: string;
  editable: boolean;
  content: string;
  active?: boolean;
}


export interface IImplementationPart {
  base: string;
  files: IExerciseFile[];
  implFileName: string;
  sampleSolFileNames: string[];
}

export type UnitTestTypes = ("Simplified" | "Normal");

export interface IProgSolution {
  files: IExerciseFile[];
  testData: IProgTestData[];
}

export type UmlAssociationType = ("ASSOCIATION" | "AGGREGATION" | "COMPOSITION");

export interface IUnitTestTestConfig {
  id: number;
  shouldFail: boolean;
  description: string;
  file: IExerciseFile;
}

export type UmlMultiplicity = ("SINGLE" | "UNBOUND");

export interface IUnitTestPart {
  unitTestType: UnitTestTypes;
  unitTestsDescription: string;
  unitTestFiles: IExerciseFile[];
  unitTestTestConfigs: IUnitTestTestConfig[];
  simplifiedTestMainFile?: IExerciseFile;
  testFileName: string;
  sampleSolFileNames: string[];
}


export interface IUmlAttribute {
  visibility: UmlVisibility;
  memberName: string;
  memberType: string;
  isStatic: boolean;
  isDerived: boolean;
  isAbstract: boolean;
}


export interface IProgSampleSolution {
  id: number;
  sample: IProgSolution;
}

export type UmlVisibility = ("PUBLIC" | "PACKAGE" | "PROTECTED" | "PRIVATE");

export interface IUmlImplementation {
  subClass: string;
  superClass: string;
}


export interface IProgExerciseContent {
  functionName: string;
  foldername: string;
  filename: string;
  inputTypes: IProgInput[];
  outputType: any;
  baseData?: any;
  unitTestPart: IUnitTestPart;
  implementationPart: IImplementationPart;
  sampleSolutions: IProgSampleSolution[];
  sampleTestData: IProgTestData[];
  maybeClassDiagramPart?: IUmlClassDiagram;
}
