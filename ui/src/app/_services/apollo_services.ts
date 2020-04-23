import * as Types from '../_interfaces/graphql-types';

import gql from 'graphql-tag';
import { Injectable } from '@angular/core';
import * as Apollo from 'apollo-angular';

export type ToolOverviewQueryVariables = {};


export type ToolOverviewQuery = (
  { __typename?: 'Query' }
  & { tools: Array<(
    { __typename?: 'CollectionTol' }
    & Pick<Types.CollectionTol, 'id' | 'name' | 'state' | 'collectionCount' | 'lessonCount' | 'exerciseCount'>
  )> }
);

export type CollectionToolOverviewQueryVariables = {
  toolId: Types.Scalars['String'];
};


export type CollectionToolOverviewQuery = (
  { __typename?: 'Query' }
  & { tool?: Types.Maybe<(
    { __typename?: 'CollectionTol' }
    & Pick<Types.CollectionTol, 'name' | 'collectionCount' | 'exerciseCount' | 'lessonCount'>
  )> }
);

export type AllExercisesOverviewQueryVariables = {
  toolId: Types.Scalars['String'];
};


export type AllExercisesOverviewQuery = (
  { __typename?: 'Query' }
  & { tool?: Types.Maybe<(
    { __typename?: 'CollectionTol' }
    & { allExercises: Array<(
      { __typename?: 'Exercise' }
      & { topics: Array<(
        { __typename?: 'Topic' }
        & TopicFragment
      )> }
      & FieldsForLinkFragment
    )> }
  )> }
);

export type CollectionListQueryVariables = {
  toolId: Types.Scalars['String'];
};


export type CollectionListQuery = (
  { __typename?: 'Query' }
  & { tool?: Types.Maybe<(
    { __typename?: 'CollectionTol' }
    & Pick<Types.CollectionTol, 'name'>
    & { collections: Array<(
      { __typename?: 'ExerciseCollection' }
      & Pick<Types.ExerciseCollection, 'id' | 'title' | 'exerciseCount'>
    )> }
  )> }
);

export type CollectionOverviewQueryVariables = {
  toolId: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
};


export type CollectionOverviewQuery = (
  { __typename?: 'Query' }
  & { tool?: Types.Maybe<(
    { __typename?: 'CollectionTol' }
    & { collection?: Types.Maybe<(
      { __typename?: 'ExerciseCollection' }
      & Pick<Types.ExerciseCollection, 'title'>
      & { exercises: Array<(
        { __typename?: 'Exercise' }
        & FieldsForLinkFragment
      )> }
    )> }
  )> }
);

export type ExerciseOverviewQueryVariables = {
  toolId: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
};


export type ExerciseOverviewQuery = (
  { __typename?: 'Query' }
  & { tool?: Types.Maybe<(
    { __typename?: 'CollectionTol' }
    & { collection?: Types.Maybe<(
      { __typename?: 'ExerciseCollection' }
      & { exercise?: Types.Maybe<(
        { __typename?: 'Exercise' }
        & Pick<Types.Exercise, 'id' | 'title' | 'text'>
        & { parts: Array<(
          { __typename?: 'ExPart' }
          & PartFragment
        )> }
      )> }
    )> }
  )> }
);

export type ExerciseQueryVariables = {
  toolId: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
  partId: Types.Scalars['String'];
};


export type ExerciseQuery = (
  { __typename?: 'Query' }
  & { tool?: Types.Maybe<(
    { __typename?: 'CollectionTol' }
    & { collection?: Types.Maybe<(
      { __typename?: 'ExerciseCollection' }
      & { exercise?: Types.Maybe<(
        { __typename?: 'Exercise' }
        & { programmingContent?: Types.Maybe<(
          { __typename?: 'ProgrammingExerciseContent' }
          & ProgExerciseContentSolveFieldsFragment
        )>, regexContent?: Types.Maybe<(
          { __typename?: 'RegexExerciseContent' }
          & RegexExerciseContentSolveFieldsFragment
        )>, sqlContent?: Types.Maybe<(
          { __typename?: 'SqlExerciseContent' }
          & SqlExerciseContentSolveFieldsFragment
        )>, umlContent?: Types.Maybe<(
          { __typename?: 'UmlExerciseContent' }
          & UmlExerciseContentSolveFieldsFragment
        )>, webContent?: Types.Maybe<(
          { __typename?: 'WebExerciseContent' }
          & WebExerciseContentSolveFieldsFragment
        )>, xmlContent?: Types.Maybe<(
          { __typename?: 'XmlExerciseContent' }
          & XmlExerciseContentSolveFieldsFragment
        )> }
        & ExerciseSolveFieldsFragment
      )> }
    )> }
  )> }
);

export type LessonsForToolQueryVariables = {
  toolId: Types.Scalars['String'];
};


export type LessonsForToolQuery = (
  { __typename?: 'Query' }
  & { tool?: Types.Maybe<(
    { __typename?: 'CollectionTol' }
    & Pick<Types.CollectionTol, 'name'>
    & { lessons: Array<(
      { __typename?: 'Lesson' }
      & Pick<Types.Lesson, 'id' | 'title' | 'description'>
    )> }
  )> }
);

export type LessonQueryVariables = {
  toolId: Types.Scalars['String'];
  lessonId: Types.Scalars['Int'];
};


export type LessonQuery = (
  { __typename?: 'Query' }
  & { tool?: Types.Maybe<(
    { __typename?: 'CollectionTol' }
    & Pick<Types.CollectionTol, 'name'>
    & { lesson?: Types.Maybe<(
      { __typename?: 'Lesson' }
      & Pick<Types.Lesson, 'id' | 'title' | 'description'>
    )> }
  )> }
);

export type AdminIndexQueryVariables = {};


export type AdminIndexQuery = (
  { __typename?: 'Query' }
  & { tools: Array<(
    { __typename?: 'CollectionTol' }
    & Pick<Types.CollectionTol, 'id' | 'name' | 'collectionCount' | 'exerciseCount' | 'lessonCount'>
  )> }
);

export type CollectionToolAdminQueryVariables = {
  toolId: Types.Scalars['String'];
};


export type CollectionToolAdminQuery = (
  { __typename?: 'Query' }
  & { tool?: Types.Maybe<(
    { __typename?: 'CollectionTol' }
    & Pick<Types.CollectionTol, 'name' | 'collectionCount' | 'lessonCount'>
  )> }
);

export type AdminLessonIndexQueryVariables = {
  toolId: Types.Scalars['String'];
};


export type AdminLessonIndexQuery = (
  { __typename?: 'Query' }
  & { tool?: Types.Maybe<(
    { __typename?: 'CollectionTol' }
    & Pick<Types.CollectionTol, 'name'>
    & { lessons: Array<(
      { __typename?: 'Lesson' }
      & LessonFragment
    )> }
  )> }
);

export type CollectionAdminQueryVariables = {
  toolId: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
};


export type CollectionAdminQuery = (
  { __typename?: 'Query' }
  & { tool?: Types.Maybe<(
    { __typename?: 'CollectionTol' }
    & { collection?: Types.Maybe<(
      { __typename?: 'ExerciseCollection' }
      & Pick<Types.ExerciseCollection, 'title'>
      & { exercises: Array<(
        { __typename?: 'Exercise' }
        & FieldsForLinkFragment
      )> }
    )> }
  )> }
);

export type AdminCollectionsIndexQueryVariables = {
  toolId: Types.Scalars['String'];
};


export type AdminCollectionsIndexQuery = (
  { __typename?: 'Query' }
  & { tool?: Types.Maybe<(
    { __typename?: 'CollectionTol' }
    & Pick<Types.CollectionTol, 'name'>
    & { collections: Array<(
      { __typename?: 'ExerciseCollection' }
      & Pick<Types.ExerciseCollection, 'id' | 'title' | 'exerciseCount'>
    )> }
  )> }
);

export type AdminEditCollectionQueryVariables = {
  toolId: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
};


export type AdminEditCollectionQuery = (
  { __typename?: 'Query' }
  & { tool?: Types.Maybe<(
    { __typename?: 'CollectionTol' }
    & { collection?: Types.Maybe<(
      { __typename?: 'ExerciseCollection' }
      & Pick<Types.ExerciseCollection, 'asJsonString'>
    )> }
  )> }
);

export type AdminReadCollectionsQueryVariables = {
  toolId: Types.Scalars['String'];
};


export type AdminReadCollectionsQuery = (
  { __typename?: 'Query' }
  & { tool?: Types.Maybe<(
    { __typename?: 'CollectionTol' }
    & Pick<Types.CollectionTol, 'name' | 'readCollections'>
  )> }
);

export type AdminUpsertCollectionMutationVariables = {
  toolId: Types.Scalars['String'];
  content: Types.Scalars['String'];
};


export type AdminUpsertCollectionMutation = (
  { __typename?: 'Mutation' }
  & Pick<Types.Mutation, 'upsertCollection'>
);

export type AdminReadLessonsQueryVariables = {
  toolId: Types.Scalars['String'];
};


export type AdminReadLessonsQuery = (
  { __typename?: 'Query' }
  & { tool?: Types.Maybe<(
    { __typename?: 'CollectionTol' }
    & Pick<Types.CollectionTol, 'name'>
    & { readLessons: Array<(
      { __typename?: 'Lesson' }
      & LessonFragment
    )> }
  )> }
);

export type AdminEditExerciseQueryVariables = {
  toolId: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
};


export type AdminEditExerciseQuery = (
  { __typename?: 'Query' }
  & { tool?: Types.Maybe<(
    { __typename?: 'CollectionTol' }
    & { collection?: Types.Maybe<(
      { __typename?: 'ExerciseCollection' }
      & { exercise?: Types.Maybe<(
        { __typename?: 'Exercise' }
        & Pick<Types.Exercise, 'asJsonString'>
      )> }
    )> }
  )> }
);

export type AdminReadExercisesQueryVariables = {
  toolId: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
};


export type AdminReadExercisesQuery = (
  { __typename?: 'Query' }
  & { tool?: Types.Maybe<(
    { __typename?: 'CollectionTol' }
    & { collection?: Types.Maybe<(
      { __typename?: 'ExerciseCollection' }
      & Pick<Types.ExerciseCollection, 'readExercises'>
    )> }
  )> }
);

export type AdminUpsertExerciseMutationVariables = {
  toolId: Types.Scalars['String'];
  content: Types.Scalars['String'];
};


export type AdminUpsertExerciseMutation = (
  { __typename?: 'Mutation' }
  & Pick<Types.Mutation, 'upsertExercise'>
);

export type TopicFragment = (
  { __typename?: 'Topic' }
  & Pick<Types.Topic, 'abbreviation' | 'title'>
);

export type PartFragment = (
  { __typename?: 'ExPart' }
  & Pick<Types.ExPart, 'id' | 'name'>
);

export type FieldsForLinkFragment = (
  { __typename?: 'Exercise' }
  & Pick<Types.Exercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'difficulty'>
  & { topics: Array<(
    { __typename?: 'Topic' }
    & TopicFragment
  )> }
);

export type ExerciseSolveFieldsFragment = (
  { __typename?: 'Exercise' }
  & Pick<Types.Exercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'text'>
);

export type ProgExerciseContentSolveFieldsFragment = (
  { __typename?: 'ProgrammingExerciseContent' }
  & Pick<Types.ProgrammingExerciseContent, 'part'>
  & { unitTestPart: (
    { __typename?: 'UnitTestPart' }
    & Pick<Types.UnitTestPart, 'unitTestType'>
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
    { __typename?: 'ProgrammingSampleSolution' }
    & ProgrammingSampleSolutionFragment
  )> }
);

export type ProgrammingSampleSolutionFragment = (
  { __typename: 'ProgrammingSampleSolution' }
  & { sample: (
    { __typename?: 'ProgSolution' }
    & { files: Array<(
      { __typename?: 'ExerciseFile' }
      & ExFileAllFragment
    )> }
  ) }
);

export type RegexExerciseContentSolveFieldsFragment = (
  { __typename?: 'RegexExerciseContent' }
  & Pick<Types.RegexExerciseContent, 'part'>
  & { sampleSolutions: Array<(
    { __typename?: 'RegexSampleSolution' }
    & RegexSampleSolutionFragment
  )> }
);

export type RegexSampleSolutionFragment = (
  { __typename: 'RegexSampleSolution' }
  & Pick<Types.RegexSampleSolution, 'sample'>
);

export type SqlExerciseContentSolveFieldsFragment = (
  { __typename?: 'SqlExerciseContent' }
  & Pick<Types.SqlExerciseContent, 'hint' | 'part'>
  & { sampleSolutions: Array<(
    { __typename?: 'SqlSampleSolution' }
    & SqlSampleSolutionFragment
  )>, sqlDbContents: Array<(
    { __typename?: 'SqlQueryResult' }
    & SqlQueryResultFragment
  )> }
);

export type SqlQueryResultFragment = (
  { __typename?: 'SqlQueryResult' }
  & Pick<Types.SqlQueryResult, 'tableName' | 'columnNames'>
  & { rows: Array<(
    { __typename?: 'SqlRow' }
    & SqlRowFragment
  )> }
);

export type SqlRowFragment = (
  { __typename?: 'SqlRow' }
  & { cells: Array<(
    { __typename?: 'SqlKeyCellValueObject' }
    & Pick<Types.SqlKeyCellValueObject, 'key'>
    & { value: (
      { __typename?: 'SqlCell' }
      & SqlCellFragment
    ) }
  )> }
);

export type SqlCellFragment = (
  { __typename?: 'SqlCell' }
  & Pick<Types.SqlCell, 'colName' | 'content' | 'different'>
);

export type SqlSampleSolutionFragment = (
  { __typename: 'SqlSampleSolution' }
  & Pick<Types.SqlSampleSolution, 'sample'>
);

export type UmlExerciseContentSolveFieldsFragment = (
  { __typename?: 'UmlExerciseContent' }
  & Pick<Types.UmlExerciseContent, 'toIgnore' | 'part'>
  & { mappings: Array<(
    { __typename?: 'KeyValueObject' }
    & Pick<Types.KeyValueObject, 'key' | 'value'>
  )>, sampleSolutions: Array<(
    { __typename?: 'UmlSampleSolution' }
    & UmlSampleSolutionFragment
  )> }
);

export type UmlSampleSolutionFragment = (
  { __typename: 'UmlSampleSolution' }
  & { sample: (
    { __typename?: 'UmlClassDiagram' }
    & UmlClassDiagramFragment
  ) }
);

export type UmlClassDiagramFragment = (
  { __typename?: 'UmlClassDiagram' }
  & { classes: Array<(
    { __typename?: 'UmlClass' }
    & UmlClassFragment
  )>, associations: Array<(
    { __typename?: 'UmlAssociation' }
    & UmlAssociationFragment
  )>, implementations: Array<(
    { __typename?: 'UmlImplementation' }
    & UmlImplementationFragment
  )> }
);

export type UmlClassFragment = (
  { __typename?: 'UmlClass' }
  & Pick<Types.UmlClass, 'classType' | 'name'>
  & { attributes: Array<(
    { __typename?: 'UmlAttribute' }
    & UmlAttributeFragment
  )>, methods: Array<(
    { __typename?: 'UmlMethod' }
    & UmlMethodFragment
  )> }
);

export type UmlAttributeFragment = (
  { __typename?: 'UmlAttribute' }
  & Pick<Types.UmlAttribute, 'isAbstract' | 'isDerived' | 'isStatic' | 'visibility' | 'memberName' | 'memberType'>
);

export type UmlMethodFragment = (
  { __typename?: 'UmlMethod' }
  & Pick<Types.UmlMethod, 'isAbstract' | 'isStatic' | 'visibility' | 'memberName' | 'parameters' | 'memberType'>
);

export type UmlAssociationFragment = (
  { __typename?: 'UmlAssociation' }
  & Pick<Types.UmlAssociation, 'assocType' | 'assocName' | 'firstEnd' | 'firstMult' | 'secondEnd' | 'secondMult'>
);

export type UmlImplementationFragment = (
  { __typename?: 'UmlImplementation' }
  & Pick<Types.UmlImplementation, 'subClass' | 'superClass'>
);

export type WebExerciseContentSolveFieldsFragment = (
  { __typename?: 'WebExerciseContent' }
  & Pick<Types.WebExerciseContent, 'part'>
  & { files: Array<(
    { __typename?: 'ExerciseFile' }
    & ExFileAllFragment
  )>, siteSpec: (
    { __typename?: 'SiteSpec' }
    & Pick<Types.SiteSpec, 'jsTaskCount'>
    & { htmlTasks: Array<(
      { __typename?: 'HtmlTask' }
      & Pick<Types.HtmlTask, 'text'>
    )> }
  ), sampleSolutions: Array<(
    { __typename?: 'WebSampleSolution' }
    & WebSampleSolutionFragment
  )> }
);

export type WebSampleSolutionFragment = (
  { __typename: 'WebSampleSolution' }
  & { sample: (
    { __typename?: 'WebSolution' }
    & { files: Array<(
      { __typename?: 'ExerciseFile' }
      & ExFileAllFragment
    )> }
  ) }
);

export type XmlExerciseContentSolveFieldsFragment = (
  { __typename?: 'XmlExerciseContent' }
  & Pick<Types.XmlExerciseContent, 'rootNode' | 'grammarDescription' | 'part'>
  & { sampleSolutions: Array<(
    { __typename?: 'XmlSampleSolution' }
    & XmlSampleSolutionFragment
  )> }
);

export type XmlSampleSolutionFragment = (
  { __typename: 'XmlSampleSolution' }
  & { sample: (
    { __typename?: 'XmlSolution' }
    & Pick<Types.XmlSolution, 'document' | 'grammar'>
  ) }
);

export type ExFileAllFragment = (
  { __typename?: 'ExerciseFile' }
  & Pick<Types.ExerciseFile, 'name' | 'fileType' | 'content' | 'editable'>
);

export type LessonFragment = (
  { __typename?: 'Lesson' }
  & Pick<Types.Lesson, 'id' | 'title'>
);

export const PartFragmentDoc = gql`
    fragment Part on ExPart {
  id
  name
}
    `;
export const TopicFragmentDoc = gql`
    fragment Topic on Topic {
  abbreviation
  title
}
    `;
export const FieldsForLinkFragmentDoc = gql`
    fragment FieldsForLink on Exercise {
  id
  collectionId
  toolId
  title
  difficulty
  topics {
    ...Topic
  }
}
    ${TopicFragmentDoc}`;
export const ExerciseSolveFieldsFragmentDoc = gql`
    fragment ExerciseSolveFields on Exercise {
  id
  collectionId
  toolId
  title
  text
}
    `;
export const ExFileAllFragmentDoc = gql`
    fragment ExFileAll on ExerciseFile {
  name
  fileType
  content
  editable
}
    `;
export const ProgrammingSampleSolutionFragmentDoc = gql`
    fragment ProgrammingSampleSolution on ProgrammingSampleSolution {
  __typename
  sample {
    files {
      ...ExFileAll
    }
  }
}
    ${ExFileAllFragmentDoc}`;
export const ProgExerciseContentSolveFieldsFragmentDoc = gql`
    fragment ProgExerciseContentSolveFields on ProgrammingExerciseContent {
  unitTestPart {
    unitTestType
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
    ...ProgrammingSampleSolution
  }
  part(partId: $partId)
}
    ${ExFileAllFragmentDoc}
${ProgrammingSampleSolutionFragmentDoc}`;
export const RegexSampleSolutionFragmentDoc = gql`
    fragment RegexSampleSolution on RegexSampleSolution {
  __typename
  sample
}
    `;
export const RegexExerciseContentSolveFieldsFragmentDoc = gql`
    fragment RegexExerciseContentSolveFields on RegexExerciseContent {
  sampleSolutions {
    ...RegexSampleSolution
  }
  part(partId: $partId)
}
    ${RegexSampleSolutionFragmentDoc}`;
export const SqlSampleSolutionFragmentDoc = gql`
    fragment SqlSampleSolution on SqlSampleSolution {
  __typename
  sample
}
    `;
export const SqlCellFragmentDoc = gql`
    fragment SqlCell on SqlCell {
  colName
  content
  different
}
    `;
export const SqlRowFragmentDoc = gql`
    fragment SqlRow on SqlRow {
  cells {
    key
    value {
      ...SqlCell
    }
  }
}
    ${SqlCellFragmentDoc}`;
export const SqlQueryResultFragmentDoc = gql`
    fragment SqlQueryResult on SqlQueryResult {
  tableName
  columnNames
  rows {
    ...SqlRow
  }
}
    ${SqlRowFragmentDoc}`;
export const SqlExerciseContentSolveFieldsFragmentDoc = gql`
    fragment SqlExerciseContentSolveFields on SqlExerciseContent {
  hint
  sampleSolutions {
    ...SqlSampleSolution
  }
  part(partId: $partId)
  sqlDbContents {
    ...SqlQueryResult
  }
}
    ${SqlSampleSolutionFragmentDoc}
${SqlQueryResultFragmentDoc}`;
export const UmlAttributeFragmentDoc = gql`
    fragment UmlAttribute on UmlAttribute {
  isAbstract
  isDerived
  isStatic
  visibility
  memberName
  memberType
}
    `;
export const UmlMethodFragmentDoc = gql`
    fragment UmlMethod on UmlMethod {
  isAbstract
  isStatic
  visibility
  memberName
  parameters
  memberType
}
    `;
export const UmlClassFragmentDoc = gql`
    fragment UmlClass on UmlClass {
  classType
  name
  attributes {
    ...UmlAttribute
  }
  methods {
    ...UmlMethod
  }
}
    ${UmlAttributeFragmentDoc}
${UmlMethodFragmentDoc}`;
export const UmlAssociationFragmentDoc = gql`
    fragment UmlAssociation on UmlAssociation {
  assocType
  assocName
  firstEnd
  firstMult
  secondEnd
  secondMult
}
    `;
export const UmlImplementationFragmentDoc = gql`
    fragment UmlImplementation on UmlImplementation {
  subClass
  superClass
}
    `;
export const UmlClassDiagramFragmentDoc = gql`
    fragment UmlClassDiagram on UmlClassDiagram {
  classes {
    ...UmlClass
  }
  associations {
    ...UmlAssociation
  }
  implementations {
    ...UmlImplementation
  }
}
    ${UmlClassFragmentDoc}
${UmlAssociationFragmentDoc}
${UmlImplementationFragmentDoc}`;
export const UmlSampleSolutionFragmentDoc = gql`
    fragment UmlSampleSolution on UmlSampleSolution {
  __typename
  sample {
    ...UmlClassDiagram
  }
}
    ${UmlClassDiagramFragmentDoc}`;
export const UmlExerciseContentSolveFieldsFragmentDoc = gql`
    fragment UmlExerciseContentSolveFields on UmlExerciseContent {
  toIgnore
  mappings {
    key
    value
  }
  sampleSolutions {
    ...UmlSampleSolution
  }
  part(partId: $partId)
}
    ${UmlSampleSolutionFragmentDoc}`;
export const WebSampleSolutionFragmentDoc = gql`
    fragment WebSampleSolution on WebSampleSolution {
  __typename
  sample {
    files {
      ...ExFileAll
    }
  }
}
    ${ExFileAllFragmentDoc}`;
export const WebExerciseContentSolveFieldsFragmentDoc = gql`
    fragment WebExerciseContentSolveFields on WebExerciseContent {
  files {
    ...ExFileAll
  }
  siteSpec {
    htmlTasks {
      text
    }
    jsTaskCount
  }
  sampleSolutions {
    ...WebSampleSolution
  }
  part(partId: $partId)
}
    ${ExFileAllFragmentDoc}
${WebSampleSolutionFragmentDoc}`;
export const XmlSampleSolutionFragmentDoc = gql`
    fragment XmlSampleSolution on XmlSampleSolution {
  __typename
  sample {
    document
    grammar
  }
}
    `;
export const XmlExerciseContentSolveFieldsFragmentDoc = gql`
    fragment XmlExerciseContentSolveFields on XmlExerciseContent {
  rootNode
  grammarDescription
  sampleSolutions {
    ...XmlSampleSolution
  }
  part(partId: $partId)
}
    ${XmlSampleSolutionFragmentDoc}`;
export const LessonFragmentDoc = gql`
    fragment Lesson on Lesson {
  id
  title
}
    `;
export const ToolOverviewDocument = gql`
    query ToolOverview {
  tools {
    id
    name
    state
    collectionCount
    lessonCount
    exerciseCount
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class ToolOverviewGQL extends Apollo.Query<ToolOverviewQuery, ToolOverviewQueryVariables> {
    document = ToolOverviewDocument;
    
  }
export const CollectionToolOverviewDocument = gql`
    query CollectionToolOverview($toolId: String!) {
  tool(toolId: $toolId) {
    name
    collectionCount
    exerciseCount
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
export const AllExercisesOverviewDocument = gql`
    query AllExercisesOverview($toolId: String!) {
  tool(toolId: $toolId) {
    allExercises {
      topics {
        ...Topic
      }
      ...FieldsForLink
    }
  }
}
    ${TopicFragmentDoc}
${FieldsForLinkFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class AllExercisesOverviewGQL extends Apollo.Query<AllExercisesOverviewQuery, AllExercisesOverviewQueryVariables> {
    document = AllExercisesOverviewDocument;
    
  }
export const CollectionListDocument = gql`
    query CollectionList($toolId: String!) {
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
  export class CollectionListGQL extends Apollo.Query<CollectionListQuery, CollectionListQueryVariables> {
    document = CollectionListDocument;
    
  }
export const CollectionOverviewDocument = gql`
    query CollectionOverview($toolId: String!, $collId: Int!) {
  tool(toolId: $toolId) {
    collection(collId: $collId) {
      title
      exercises {
        ...FieldsForLink
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
export const ExerciseOverviewDocument = gql`
    query ExerciseOverview($toolId: String!, $collId: Int!, $exId: Int!) {
  tool(toolId: $toolId) {
    collection(collId: $collId) {
      exercise(exId: $exId) {
        id
        title
        text
        parts {
          ...Part
        }
      }
    }
  }
}
    ${PartFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class ExerciseOverviewGQL extends Apollo.Query<ExerciseOverviewQuery, ExerciseOverviewQueryVariables> {
    document = ExerciseOverviewDocument;
    
  }
export const ExerciseDocument = gql`
    query Exercise($toolId: String!, $collId: Int!, $exId: Int!, $partId: String!) {
  tool(toolId: $toolId) {
    collection(collId: $collId) {
      exercise(exId: $exId) {
        ...ExerciseSolveFields
        programmingContent {
          ...ProgExerciseContentSolveFields
        }
        regexContent {
          ...RegexExerciseContentSolveFields
        }
        sqlContent {
          ...SqlExerciseContentSolveFields
        }
        umlContent {
          ...UmlExerciseContentSolveFields
        }
        webContent {
          ...WebExerciseContentSolveFields
        }
        xmlContent {
          ...XmlExerciseContentSolveFields
        }
      }
    }
  }
}
    ${ExerciseSolveFieldsFragmentDoc}
${ProgExerciseContentSolveFieldsFragmentDoc}
${RegexExerciseContentSolveFieldsFragmentDoc}
${SqlExerciseContentSolveFieldsFragmentDoc}
${UmlExerciseContentSolveFieldsFragmentDoc}
${WebExerciseContentSolveFieldsFragmentDoc}
${XmlExerciseContentSolveFieldsFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class ExerciseGQL extends Apollo.Query<ExerciseQuery, ExerciseQueryVariables> {
    document = ExerciseDocument;
    
  }
export const LessonsForToolDocument = gql`
    query LessonsForTool($toolId: String!) {
  tool(toolId: $toolId) {
    name
    lessons {
      id
      title
      description
    }
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class LessonsForToolGQL extends Apollo.Query<LessonsForToolQuery, LessonsForToolQueryVariables> {
    document = LessonsForToolDocument;
    
  }
export const LessonDocument = gql`
    query Lesson($toolId: String!, $lessonId: Int!) {
  tool(toolId: $toolId) {
    name
    lesson(lessonId: $lessonId) {
      id
      title
      description
    }
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class LessonGQL extends Apollo.Query<LessonQuery, LessonQueryVariables> {
    document = LessonDocument;
    
  }
export const AdminIndexDocument = gql`
    query AdminIndex {
  tools {
    id
    name
    collectionCount
    exerciseCount
    lessonCount
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class AdminIndexGQL extends Apollo.Query<AdminIndexQuery, AdminIndexQueryVariables> {
    document = AdminIndexDocument;
    
  }
export const CollectionToolAdminDocument = gql`
    query CollectionToolAdmin($toolId: String!) {
  tool(toolId: $toolId) {
    name
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
export const AdminLessonIndexDocument = gql`
    query AdminLessonIndex($toolId: String!) {
  tool(toolId: $toolId) {
    name
    lessons {
      ...Lesson
    }
  }
}
    ${LessonFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class AdminLessonIndexGQL extends Apollo.Query<AdminLessonIndexQuery, AdminLessonIndexQueryVariables> {
    document = AdminLessonIndexDocument;
    
  }
export const CollectionAdminDocument = gql`
    query CollectionAdmin($toolId: String!, $collId: Int!) {
  tool(toolId: $toolId) {
    collection(collId: $collId) {
      title
      exercises {
        ...FieldsForLink
      }
    }
  }
}
    ${FieldsForLinkFragmentDoc}`;

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
      asJsonString
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
export const AdminReadCollectionsDocument = gql`
    query AdminReadCollections($toolId: String!) {
  tool(toolId: $toolId) {
    name
    readCollections
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class AdminReadCollectionsGQL extends Apollo.Query<AdminReadCollectionsQuery, AdminReadCollectionsQueryVariables> {
    document = AdminReadCollectionsDocument;
    
  }
export const AdminUpsertCollectionDocument = gql`
    mutation AdminUpsertCollection($toolId: String!, $content: String!) {
  upsertCollection(toolId: $toolId, content: $content)
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class AdminUpsertCollectionGQL extends Apollo.Mutation<AdminUpsertCollectionMutation, AdminUpsertCollectionMutationVariables> {
    document = AdminUpsertCollectionDocument;
    
  }
export const AdminReadLessonsDocument = gql`
    query AdminReadLessons($toolId: String!) {
  tool(toolId: $toolId) {
    name
    readLessons {
      ...Lesson
    }
  }
}
    ${LessonFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class AdminReadLessonsGQL extends Apollo.Query<AdminReadLessonsQuery, AdminReadLessonsQueryVariables> {
    document = AdminReadLessonsDocument;
    
  }
export const AdminEditExerciseDocument = gql`
    query AdminEditExercise($toolId: String!, $collId: Int!, $exId: Int!) {
  tool(toolId: $toolId) {
    collection(collId: $collId) {
      exercise(exId: $exId) {
        asJsonString
      }
    }
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class AdminEditExerciseGQL extends Apollo.Query<AdminEditExerciseQuery, AdminEditExerciseQueryVariables> {
    document = AdminEditExerciseDocument;
    
  }
export const AdminReadExercisesDocument = gql`
    query AdminReadExercises($toolId: String!, $collId: Int!) {
  tool(toolId: $toolId) {
    collection(collId: $collId) {
      readExercises
    }
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class AdminReadExercisesGQL extends Apollo.Query<AdminReadExercisesQuery, AdminReadExercisesQueryVariables> {
    document = AdminReadExercisesDocument;
    
  }
export const AdminUpsertExerciseDocument = gql`
    mutation AdminUpsertExercise($toolId: String!, $content: String!) {
  upsertExercise(toolId: $toolId, content: $content)
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class AdminUpsertExerciseGQL extends Apollo.Mutation<AdminUpsertExerciseMutation, AdminUpsertExerciseMutationVariables> {
    document = AdminUpsertExerciseDocument;
    
  }