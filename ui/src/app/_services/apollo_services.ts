import gql from 'graphql-tag';
import { Injectable } from '@angular/core';
import * as Apollo from 'apollo-angular';
export type Maybe<T> = T | null;
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: string;
  String: string;
  Boolean: boolean;
  Int: number;
  Float: number;
};



export type Collection = {
   __typename?: 'Collection';
  id: Scalars['Int'];
  title: Scalars['String'];
  authors: Array<Scalars['String']>;
  text: Scalars['String'];
  shortName: Scalars['String'];
  exerciseCount: Scalars['Int'];
  exercises: Array<Exercise>;
  exercise?: Maybe<Exercise>;
};


export type CollectionExerciseArgs = {
  exId: Scalars['Int'];
};

export type ExContent = ProgExerciseContent | RegexExerciseContent | RoseExerciseContent | SqlExerciseContent | UmlExerciseContent | WebExerciseContent | XmlExerciseContent;

export type Exercise = {
   __typename?: 'Exercise';
  id: Scalars['Int'];
  collectionId: Scalars['Int'];
  toolId: Scalars['String'];
  semanticVersion: SemanticVersion;
  title: Scalars['String'];
  authors: Array<Scalars['String']>;
  text: Scalars['String'];
  tags: Array<ExTag>;
  difficulty?: Maybe<Scalars['Int']>;
};

export type ExerciseFile = {
   __typename?: 'ExerciseFile';
  name: Scalars['String'];
  resourcePath: Scalars['String'];
  fileType: Scalars['String'];
  editable: Scalars['Boolean'];
  content: Scalars['String'];
};

export type ExTag = {
   __typename?: 'ExTag';
  abbreviation: Scalars['String'];
  title: Scalars['String'];
};

export type ImplementationPart = {
   __typename?: 'ImplementationPart';
  base: Scalars['String'];
  files: Array<ExerciseFile>;
  implFileName: Scalars['String'];
  sampleSolFileNames: Array<Scalars['String']>;
};

export type Lesson = {
   __typename?: 'Lesson';
  id: Scalars['Int'];
  toolId: Scalars['String'];
  title: Scalars['String'];
};

export type ProgExerciseContent = {
   __typename?: 'ProgExerciseContent';
  functionName: Scalars['String'];
  foldername: Scalars['String'];
  filename: Scalars['String'];
  unitTestPart: UnitTestPart;
  implementationPart: ImplementationPart;
  sampleSolutions: Array<SampleSolution>;
};

export type ProgSolution = {
   __typename?: 'ProgSolution';
  files: Array<ExerciseFile>;
};

export type Query = {
   __typename?: 'Query';
  tools: Array<Tool>;
  tool?: Maybe<Tool>;
};


export type QueryToolArgs = {
  toolId: Scalars['String'];
};

export enum RegexCorrectionType {
  Extraction = 'EXTRACTION',
  Matching = 'MATCHING'
}

export type RegexExerciseContent = {
   __typename?: 'RegexExerciseContent';
  maxPoints: Scalars['Int'];
  correctionType: RegexCorrectionType;
  sampleSolutions: Array<SampleSolution>;
  matchTestData: Array<RegexMatchTestData>;
  extractionTestData: Array<RegexExtractionTestData>;
};

export type RegexExtractionTestData = {
   __typename?: 'RegexExtractionTestData';
  id: Scalars['Int'];
  base: Scalars['String'];
};

export type RegexMatchTestData = {
   __typename?: 'RegexMatchTestData';
  id: Scalars['Int'];
  data: Scalars['String'];
  isIncluded: Scalars['Boolean'];
};

export type RoseExerciseContent = {
   __typename?: 'RoseExerciseContent';
  fieldWidth: Scalars['Int'];
  fieldHeight: Scalars['Int'];
  isMultiplayer: Scalars['Boolean'];
  sampleSolutions: Array<SampleSolution>;
};

export type SampleSolution = {
   __typename?: 'SampleSolution';
  id: Scalars['Int'];
  sample: ProgSolution;
};

export type SemanticVersion = {
   __typename?: 'SemanticVersion';
  major: Scalars['Int'];
  minor: Scalars['Int'];
  patch: Scalars['Int'];
};

export type SiteSpec = {
   __typename?: 'SiteSpec';
  fileName: Scalars['String'];
  htmlTaskCount: Scalars['Int'];
  jsTaskCount: Scalars['Int'];
};

export type SqlExerciseContent = {
   __typename?: 'SqlExerciseContent';
  exerciseType: SqlExerciseType;
  hint?: Maybe<Scalars['String']>;
  sampleSolutions: Array<SampleSolution>;
};

export enum SqlExerciseType {
  Create = 'CREATE',
  Insert = 'INSERT',
  Select = 'SELECT',
  Delete = 'DELETE',
  Update = 'UPDATE'
}

export type Tool = {
   __typename?: 'Tool';
  id: Scalars['String'];
  name: Scalars['String'];
  state: ToolState;
  lessonCount: Scalars['Int'];
  lessons: Array<Lesson>;
  collectionCount: Scalars['Int'];
  collections: Array<Collection>;
  collection?: Maybe<Collection>;
  exerciseCount: Scalars['Int'];
  allExerciseMetaData: Array<Exercise>;
  exerciseContent?: Maybe<ExContent>;
};


export type ToolCollectionArgs = {
  collId: Scalars['Int'];
};


export type ToolExerciseContentArgs = {
  collId: Scalars['Int'];
  exId: Scalars['Int'];
};

export enum ToolState {
  Alpha = 'ALPHA',
  Beta = 'BETA',
  Live = 'LIVE'
}

export type UmlExerciseContent = {
   __typename?: 'UmlExerciseContent';
  sampleSolutions: Array<SampleSolution>;
};

export type UnitTestPart = {
   __typename?: 'UnitTestPart';
  unitTestType: UnitTestType;
  unitTestsDescription: Scalars['String'];
  unitTestFiles: Array<ExerciseFile>;
  unitTestTestConfigs: Array<UnitTestTestConfig>;
  simplifiedTestMainFile?: Maybe<ExerciseFile>;
  testFileName: Scalars['String'];
  sampleSolFileNames: Array<Scalars['String']>;
};

export type UnitTestTestConfig = {
   __typename?: 'UnitTestTestConfig';
  id: Scalars['Int'];
  shouldFail: Scalars['Boolean'];
  description: Scalars['String'];
  file: ExerciseFile;
};

export enum UnitTestType {
  Normal = 'Normal',
  Simplified = 'Simplified'
}

export type WebExerciseContent = {
   __typename?: 'WebExerciseContent';
  htmlText?: Maybe<Scalars['String']>;
  jsText?: Maybe<Scalars['String']>;
  siteSpec: SiteSpec;
  files: Array<ExerciseFile>;
  sampleSolutions: Array<SampleSolution>;
};

export type XmlExerciseContent = {
   __typename?: 'XmlExerciseContent';
  grammarDescription: Scalars['String'];
  rootNode: Scalars['String'];
  sampleSolutions: Array<SampleSolution>;
};

export type CollectionsQueryVariables = {
  toolId: Scalars['String'];
};


export type CollectionsQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & { collections: Array<(
      { __typename?: 'Collection' }
      & Pick<Collection, 'id' | 'title'>
    )> }
  )> }
);

export type ExercisesQueryVariables = {
  toolId: Scalars['String'];
  collId: Scalars['Int'];
};


export type ExercisesQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & { collection?: Maybe<(
      { __typename?: 'Collection' }
      & { exercises: Array<(
        { __typename?: 'Exercise' }
        & Pick<Exercise, 'id' | 'title'>
      )> }
    )> }
  )> }
);

export type CollectionToolOverviewQueryVariables = {
  toolId: Scalars['String'];
};


export type CollectionToolOverviewQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & Pick<Tool, 'name' | 'exerciseCount' | 'collectionCount' | 'lessonCount'>
  )> }
);

export type CollectionOverviewQueryVariables = {
  toolId: Scalars['String'];
  collId: Scalars['Int'];
};


export type CollectionOverviewQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & { collection?: Maybe<(
      { __typename?: 'Collection' }
      & Pick<Collection, 'title'>
      & { exercises: Array<(
        { __typename?: 'Exercise' }
        & FieldsForLinkFragment
      )> }
    )> }
  )> }
);

export type AllExercisesOverviewQueryVariables = {
  toolId: Scalars['String'];
};


export type AllExercisesOverviewQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & { allExerciseMetaData: Array<(
      { __typename?: 'Exercise' }
      & FieldsForLinkFragment
    )> }
  )> }
);

export type ExerciseOverviewQueryVariables = {
  toolId: Scalars['String'];
  collId: Scalars['Int'];
  exId: Scalars['Int'];
};


export type ExerciseOverviewQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & { collection?: Maybe<(
      { __typename?: 'Collection' }
      & { exercise?: Maybe<(
        { __typename?: 'Exercise' }
        & Pick<Exercise, 'id' | 'title' | 'text'>
      )> }
    )>, exerciseContent?: Maybe<(
      { __typename: 'ProgExerciseContent' }
      & { unitTestPart: (
        { __typename?: 'UnitTestPart' }
        & Pick<UnitTestPart, 'unitTestType'>
      ) }
    ) | { __typename: 'RegexExerciseContent' } | { __typename: 'RoseExerciseContent' } | { __typename: 'SqlExerciseContent' } | { __typename: 'UmlExerciseContent' } | (
      { __typename: 'WebExerciseContent' }
      & { siteSpec: (
        { __typename?: 'SiteSpec' }
        & Pick<SiteSpec, 'htmlTaskCount' | 'jsTaskCount'>
      ) }
    ) | { __typename: 'XmlExerciseContent' }> }
  )> }
);

export type ExerciseQueryVariables = {
  toolId: Scalars['String'];
  collId: Scalars['Int'];
  exId: Scalars['Int'];
};


export type ExerciseQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & { collection?: Maybe<(
      { __typename?: 'Collection' }
      & { exercise?: Maybe<(
        { __typename?: 'Exercise' }
        & ExerciseSolveFieldsFragment
      )> }
    )>, exerciseContent?: Maybe<(
      { __typename?: 'ProgExerciseContent' }
      & ProgExerciseContentSolveFieldsFragment
    ) | { __typename?: 'RegexExerciseContent' } | { __typename?: 'RoseExerciseContent' } | { __typename?: 'SqlExerciseContent' } | { __typename?: 'UmlExerciseContent' } | (
      { __typename?: 'WebExerciseContent' }
      & WebExerciseContentSolveFieldsFragment
    ) | { __typename?: 'XmlExerciseContent' }> }
  )> }
);

export type CollectionToolAdminQueryVariables = {
  toolId: Scalars['String'];
};


export type CollectionToolAdminQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & Pick<Tool, 'collectionCount' | 'lessonCount'>
  )> }
);

export type CollectionAdminQueryVariables = {
  toolId: Scalars['String'];
  collId: Scalars['Int'];
};


export type CollectionAdminQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & { collection?: Maybe<(
      { __typename?: 'Collection' }
      & Pick<Collection, 'title'>
      & { exercises: Array<(
        { __typename?: 'Exercise' }
        & Pick<Exercise, 'id' | 'title'>
      )> }
    )> }
  )> }
);

export type AdminCollectionsIndexQueryVariables = {
  toolId: Scalars['String'];
};


export type AdminCollectionsIndexQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & Pick<Tool, 'name'>
    & { collections: Array<(
      { __typename?: 'Collection' }
      & Pick<Collection, 'id' | 'title' | 'exerciseCount'>
    )> }
  )> }
);

export type AdminEditCollectionQueryVariables = {
  toolId: Scalars['String'];
  collId: Scalars['Int'];
};


export type AdminEditCollectionQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & { collection?: Maybe<(
      { __typename?: 'Collection' }
      & Pick<Collection, 'id' | 'title'>
    )> }
  )> }
);

export type FieldsForLinkFragment = (
  { __typename?: 'Exercise' }
  & Pick<Exercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'difficulty'>
  & { tags: Array<(
    { __typename?: 'ExTag' }
    & Pick<ExTag, 'abbreviation' | 'title'>
  )> }
);

export type ExerciseSolveFieldsFragment = (
  { __typename?: 'Exercise' }
  & Pick<Exercise, 'title' | 'text'>
);

export type WebExerciseContentSolveFieldsFragment = (
  { __typename?: 'WebExerciseContent' }
  & { siteSpec: { __typename: 'SiteSpec' } }
);

export type ProgExerciseContentSolveFieldsFragment = (
  { __typename: 'ProgExerciseContent' }
  & { unitTestPart: (
    { __typename?: 'UnitTestPart' }
    & { unitTestFiles: Array<(
      { __typename?: 'ExerciseFile' }
      & ExFileAllFragment
    )> }
  ), implementationPart: (
    { __typename?: 'ImplementationPart' }
    & { files: Array<(
      { __typename?: 'ExerciseFile' }
      & ExFileAllFragment
    )> }
  ), sampleSolutions: Array<(
    { __typename?: 'SampleSolution' }
    & { sample: (
      { __typename?: 'ProgSolution' }
      & { files: Array<(
        { __typename?: 'ExerciseFile' }
        & ExFileAllFragment
      )> }
    ) }
  )> }
);

export type ExFileAllFragment = (
  { __typename?: 'ExerciseFile' }
  & Pick<ExerciseFile, 'name' | 'resourcePath' | 'fileType' | 'content' | 'editable'>
);

export const FieldsForLinkFragmentDoc = gql`
    fragment fieldsForLink on Exercise {
  id
  collectionId
  toolId
  title
  difficulty
  tags {
    abbreviation
    title
  }
}
    `;
export const ExerciseSolveFieldsFragmentDoc = gql`
    fragment ExerciseSolveFields on Exercise {
  title
  text
}
    `;
export const WebExerciseContentSolveFieldsFragmentDoc = gql`
    fragment WebExerciseContentSolveFields on WebExerciseContent {
  siteSpec {
    __typename
  }
}
    `;
export const ExFileAllFragmentDoc = gql`
    fragment ExFileAll on ExerciseFile {
  name
  resourcePath
  fileType
  content
  editable
}
    `;
export const ProgExerciseContentSolveFieldsFragmentDoc = gql`
    fragment ProgExerciseContentSolveFields on ProgExerciseContent {
  __typename
  unitTestPart {
    unitTestFiles {
      ...ExFileAll
    }
  }
  implementationPart {
    files {
      ...ExFileAll
    }
  }
  sampleSolutions {
    sample {
      files {
        ...ExFileAll
      }
    }
  }
}
    ${ExFileAllFragmentDoc}`;
export const CollectionsDocument = gql`
    query Collections($toolId: String!) {
  tool(toolId: $toolId) {
    collections {
      id
      title
    }
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class CollectionsGQL extends Apollo.Query<CollectionsQuery, CollectionsQueryVariables> {
    document = CollectionsDocument;
    
  }
export const ExercisesDocument = gql`
    query Exercises($toolId: String!, $collId: Int!) {
  tool(toolId: $toolId) {
    collection(collId: $collId) {
      exercises {
        id
        title
      }
    }
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class ExercisesGQL extends Apollo.Query<ExercisesQuery, ExercisesQueryVariables> {
    document = ExercisesDocument;
    
  }
export const CollectionToolOverviewDocument = gql`
    query CollectionToolOverview($toolId: String!) {
  tool(toolId: $toolId) {
    name
    exerciseCount
    collectionCount
    lessonCount
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class CollectionToolOverviewGQL extends Apollo.Query<CollectionToolOverviewQuery, CollectionToolOverviewQueryVariables> {
    document = CollectionToolOverviewDocument;
    
  }
export const CollectionOverviewDocument = gql`
    query CollectionOverview($toolId: String!, $collId: Int!) {
  tool(toolId: $toolId) {
    collection(collId: $collId) {
      title
      exercises {
        ...fieldsForLink
      }
    }
  }
}
    ${FieldsForLinkFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class CollectionOverviewGQL extends Apollo.Query<CollectionOverviewQuery, CollectionOverviewQueryVariables> {
    document = CollectionOverviewDocument;
    
  }
export const AllExercisesOverviewDocument = gql`
    query AllExercisesOverview($toolId: String!) {
  tool(toolId: $toolId) {
    allExerciseMetaData {
      ...fieldsForLink
    }
  }
}
    ${FieldsForLinkFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class AllExercisesOverviewGQL extends Apollo.Query<AllExercisesOverviewQuery, AllExercisesOverviewQueryVariables> {
    document = AllExercisesOverviewDocument;
    
  }
export const ExerciseOverviewDocument = gql`
    query ExerciseOverview($toolId: String!, $collId: Int!, $exId: Int!) {
  tool(toolId: $toolId) {
    collection(collId: $collId) {
      exercise(exId: $exId) {
        id
        title
        text
      }
    }
    exerciseContent(collId: $collId, exId: $exId) {
      __typename
      ... on ProgExerciseContent {
        unitTestPart {
          unitTestType
        }
      }
      ... on WebExerciseContent {
        siteSpec {
          htmlTaskCount
          jsTaskCount
        }
      }
    }
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class ExerciseOverviewGQL extends Apollo.Query<ExerciseOverviewQuery, ExerciseOverviewQueryVariables> {
    document = ExerciseOverviewDocument;
    
  }
export const ExerciseDocument = gql`
    query Exercise($toolId: String!, $collId: Int!, $exId: Int!) {
  tool(toolId: $toolId) {
    collection(collId: $collId) {
      exercise(exId: $exId) {
        ...ExerciseSolveFields
      }
    }
    exerciseContent(collId: $collId, exId: $exId) {
      ...WebExerciseContentSolveFields
      ...ProgExerciseContentSolveFields
    }
  }
}
    ${ExerciseSolveFieldsFragmentDoc}
${WebExerciseContentSolveFieldsFragmentDoc}
${ProgExerciseContentSolveFieldsFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class ExerciseGQL extends Apollo.Query<ExerciseQuery, ExerciseQueryVariables> {
    document = ExerciseDocument;
    
  }
export const CollectionToolAdminDocument = gql`
    query CollectionToolAdmin($toolId: String!) {
  tool(toolId: $toolId) {
    collectionCount
    lessonCount
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class CollectionToolAdminGQL extends Apollo.Query<CollectionToolAdminQuery, CollectionToolAdminQueryVariables> {
    document = CollectionToolAdminDocument;
    
  }
export const CollectionAdminDocument = gql`
    query CollectionAdmin($toolId: String!, $collId: Int!) {
  tool(toolId: $toolId) {
    collection(collId: $collId) {
      title
      exercises {
        id
        title
      }
    }
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class CollectionAdminGQL extends Apollo.Query<CollectionAdminQuery, CollectionAdminQueryVariables> {
    document = CollectionAdminDocument;
    
  }
export const AdminCollectionsIndexDocument = gql`
    query AdminCollectionsIndex($toolId: String!) {
  tool(toolId: $toolId) {
    name
    collections {
      id
      title
      exerciseCount
    }
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class AdminCollectionsIndexGQL extends Apollo.Query<AdminCollectionsIndexQuery, AdminCollectionsIndexQueryVariables> {
    document = AdminCollectionsIndexDocument;
    
  }
export const AdminEditCollectionDocument = gql`
    query AdminEditCollection($toolId: String!, $collId: Int!) {
  tool(toolId: $toolId) {
    collection(collId: $collId) {
      id
      title
    }
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class AdminEditCollectionGQL extends Apollo.Query<AdminEditCollectionQuery, AdminEditCollectionQueryVariables> {
    document = AdminEditCollectionDocument;
    
  }