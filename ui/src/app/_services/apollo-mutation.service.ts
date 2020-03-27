import gql from 'graphql-tag';
import { Injectable } from '@angular/core';
import * as Apollo from 'apollo-angular';
export type CollectionListQueryVariables = {
  toolId: Scalars['String'];
};


export type CollectionListQuery = (
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
      & { tags: Array<(
        { __typename?: 'ExTag' }
        & TagFragment
      )> }
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
      { __typename: 'ProgExerciseContent' }
      & ProgExerciseContentSolveFieldsFragment
    ) | (
      { __typename: 'RegexExerciseContent' }
      & RegexExerciseContentSolveFieldsFragment
    ) | { __typename: 'RoseExerciseContent' } | (
      { __typename: 'SqlExerciseContent' }
      & SqlExerciseContentSolveFieldsFragment
    ) | (
      { __typename: 'UmlExerciseContent' }
      & UmlExerciseContentSolveFieldsFragment
    ) | (
      { __typename: 'WebExerciseContent' }
      & WebExerciseContentSolveFieldsFragment
    ) | (
      { __typename: 'XmlExerciseContent' }
      & XmlExerciseContentSolveFieldsFragment
    )> }
  )> }
);

export type LessonsForToolQueryVariables = {
  toolId: Scalars['String'];
};


export type LessonsForToolQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & Pick<Tool, 'name'>
    & { lessons: Array<(
      { __typename?: 'Lesson' }
      & Pick<Lesson, 'id' | 'title' | 'description'>
    )> }
  )> }
);

export type LessonQueryVariables = {
  toolId: Scalars['String'];
  lessonId: Scalars['Int'];
};


export type LessonQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & Pick<Tool, 'name'>
    & { lesson?: Maybe<(
      { __typename?: 'Lesson' }
      & Pick<Lesson, 'id' | 'title' | 'description'>
    )> }
  )> }
);

export type CollectionToolAdminQueryVariables = {
  toolId: Scalars['String'];
};


export type CollectionToolAdminQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & Pick<Tool, 'name' | 'collectionCount' | 'lessonCount'>
  )> }
);

export type AdminLessonIndexQueryVariables = {
  toolId: Scalars['String'];
};


export type AdminLessonIndexQuery = (
  { __typename?: 'Query' }
  & { tool?: Maybe<(
    { __typename?: 'Tool' }
    & Pick<Tool, 'name'>
    & { lessons: Array<(
      { __typename?: 'Lesson' }
      & LessonFragmentFragment
    )> }
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

export type TagFragment = (
  { __typename?: 'ExTag' }
  & Pick<ExTag, 'abbreviation' | 'title'>
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
  & Pick<Exercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'text'>
);

export type ProgExerciseContentSolveFieldsFragment = (
  { __typename?: 'ProgExerciseContent' }
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
  ), progSampleSolutions: Array<(
    { __typename?: 'ProgSampleSolution' }
    & { sample: (
      { __typename?: 'ProgSolution' }
      & { files: Array<(
        { __typename?: 'ExerciseFile' }
        & ExFileAllFragment
      )> }
    ) }
  )> }
);

export type RegexExerciseContentSolveFieldsFragment = (
  { __typename?: 'RegexExerciseContent' }
  & { regexSampleSolutions: Array<(
    { __typename?: 'StringSampleSolution' }
    & Pick<StringSampleSolution, 'sample'>
  )> }
);

export type SqlExerciseContentSolveFieldsFragment = (
  { __typename?: 'SqlExerciseContent' }
  & Pick<SqlExerciseContent, 'hint'>
  & { sqlSampleSolutions: Array<(
    { __typename?: 'StringSampleSolution' }
    & Pick<StringSampleSolution, 'sample'>
  )> }
);

export type UmlExerciseContentSolveFieldsFragment = (
  { __typename?: 'UmlExerciseContent' }
  & Pick<UmlExerciseContent, 'toIgnore'>
  & { mappings: Array<(
    { __typename?: 'KeyValueObject' }
    & Pick<KeyValueObject, 'key' | 'value'>
  )>, umlSampleSolutions: Array<(
    { __typename?: 'UmlSampleSolution' }
    & { sample: (
      { __typename?: 'UmlClassDiagram' }
      & UmlClassDiagramFragment
    ) }
  )> }
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
  & Pick<UmlClass, 'classType' | 'name'>
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
  & Pick<UmlAttribute, 'isAbstract' | 'isDerived' | 'isStatic' | 'visibility' | 'memberName' | 'memberType'>
);

export type UmlMethodFragment = (
  { __typename?: 'UmlMethod' }
  & Pick<UmlMethod, 'isAbstract' | 'isStatic' | 'visibility' | 'memberName' | 'parameters' | 'memberType'>
);

export type UmlAssociationFragment = (
  { __typename?: 'UmlAssociation' }
  & Pick<UmlAssociation, 'assocType' | 'assocName' | 'firstEnd' | 'firstMult' | 'secondEnd' | 'secondMult'>
);

export type UmlImplementationFragment = (
  { __typename?: 'UmlImplementation' }
  & Pick<UmlImplementation, 'subClass' | 'superClass'>
);

export type WebExerciseContentSolveFieldsFragment = (
  { __typename?: 'WebExerciseContent' }
  & { files: Array<(
    { __typename?: 'ExerciseFile' }
    & ExFileAllFragment
  )>, siteSpec: (
    { __typename?: 'SiteSpec' }
    & Pick<SiteSpec, 'jsTaskCount'>
    & { htmlTasks: Array<(
      { __typename?: 'HtmlTask' }
      & Pick<HtmlTask, 'text'>
    )> }
  ), webSampleSolutions: Array<(
    { __typename?: 'WebSampleSolution' }
    & { sample: Array<(
      { __typename?: 'ExerciseFile' }
      & ExFileAllFragment
    )> }
  )> }
);

export type XmlExerciseContentSolveFieldsFragment = (
  { __typename?: 'XmlExerciseContent' }
  & Pick<XmlExerciseContent, 'rootNode' | 'grammarDescription'>
  & { xmlSampleSolutions: Array<(
    { __typename?: 'XmlSampleSolution' }
    & { sample: (
      { __typename?: 'XmlSolution' }
      & Pick<XmlSolution, 'document' | 'grammar'>
    ) }
  )> }
);

export type ExFileAllFragment = (
  { __typename?: 'ExerciseFile' }
  & Pick<ExerciseFile, 'name' | 'resourcePath' | 'fileType' | 'content' | 'editable'>
);

export type LessonFragmentFragment = (
  { __typename?: 'Lesson' }
  & Pick<Lesson, 'id' | 'title'>
);

export const TagFragmentDoc = gql`
    fragment Tag on ExTag {
  abbreviation
  title
}
    `;
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
  resourcePath
  fileType
  content
  editable
}
    `;
export const ProgExerciseContentSolveFieldsFragmentDoc = gql`
    fragment ProgExerciseContentSolveFields on ProgExerciseContent {
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
  progSampleSolutions: sampleSolutions {
    sample {
      files {
        ...ExFileAll
      }
    }
  }
}
    ${ExFileAllFragmentDoc}`;
export const RegexExerciseContentSolveFieldsFragmentDoc = gql`
    fragment RegexExerciseContentSolveFields on RegexExerciseContent {
  regexSampleSolutions: sampleSolutions {
    sample
  }
}
    `;
export const SqlExerciseContentSolveFieldsFragmentDoc = gql`
    fragment SqlExerciseContentSolveFields on SqlExerciseContent {
  hint
  sqlSampleSolutions: sampleSolutions {
    sample
  }
}
    `;
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
export const UmlExerciseContentSolveFieldsFragmentDoc = gql`
    fragment UmlExerciseContentSolveFields on UmlExerciseContent {
  toIgnore
  mappings {
    key
    value
  }
  umlSampleSolutions: sampleSolutions {
    sample {
      ...UmlClassDiagram
    }
  }
}
    ${UmlClassDiagramFragmentDoc}`;
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
  webSampleSolutions: sampleSolutions {
    sample {
      ...ExFileAll
    }
  }
}
    ${ExFileAllFragmentDoc}`;
export const XmlExerciseContentSolveFieldsFragmentDoc = gql`
    fragment XmlExerciseContentSolveFields on XmlExerciseContent {
  rootNode
  grammarDescription
  xmlSampleSolutions: sampleSolutions {
    sample {
      document
      grammar
    }
  }
}
    `;
export const LessonFragmentFragmentDoc = gql`
    fragment LessonFragment on Lesson {
  id
  title
}
    `;
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
      tags {
        ...Tag
      }
      ...fieldsForLink
    }
  }
}
    ${TagFragmentDoc}
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
      __typename
      ...ProgExerciseContentSolveFields
      ...RegexExerciseContentSolveFields
      ...SqlExerciseContentSolveFields
      ...UmlExerciseContentSolveFields
      ...WebExerciseContentSolveFields
      ...XmlExerciseContentSolveFields
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
      ...LessonFragment
    }
  }
}
    ${LessonFragmentFragmentDoc}`;

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