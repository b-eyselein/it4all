import * as Types from '../_interfaces/graphql-types';

import gql from 'graphql-tag';
import { Injectable } from '@angular/core';
import * as Apollo from 'apollo-angular';

export type RegisterMutationVariables = {
  username: Types.Scalars['String'];
  firstPassword: Types.Scalars['String'];
  secondPassword: Types.Scalars['String'];
};


export type RegisterMutation = (
  { __typename?: 'Mutation' }
  & Pick<Types.Mutation, 'register'>
);

export type LoggedInUserWithTokenFragment = (
  { __typename?: 'LoggedInUserWithToken' }
  & Pick<Types.LoggedInUserWithToken, 'jwt'>
  & { loggedInUser: (
    { __typename?: 'LoggedInUser' }
    & Pick<Types.LoggedInUser, 'username' | 'isAdmin'>
  ) }
);

export type LoginMutationVariables = {
  username: Types.Scalars['String'];
  password: Types.Scalars['String'];
};


export type LoginMutation = (
  { __typename?: 'Mutation' }
  & { login?: Types.Maybe<(
    { __typename?: 'LoggedInUserWithToken' }
    & LoggedInUserWithTokenFragment
  )> }
);

export type CollectionToolFragment = (
  { __typename?: 'CollectionTool' }
  & Pick<Types.CollectionTool, 'id' | 'name' | 'state' | 'collectionCount' | 'lessonCount' | 'exerciseCount'>
);

export type ToolOverviewQueryVariables = {
  userJwt: Types.Scalars['String'];
};


export type ToolOverviewQuery = (
  { __typename?: 'Query' }
  & { me?: Types.Maybe<(
    { __typename?: 'User' }
    & { tools: Array<(
      { __typename?: 'CollectionTool' }
      & CollectionToolFragment
    )> }
  )> }
);

export type CollectionToolOverviewQueryVariables = {
  userJwt: Types.Scalars['String'];
  toolId: Types.Scalars['String'];
};


export type CollectionToolOverviewQuery = (
  { __typename?: 'Query' }
  & { me?: Types.Maybe<(
    { __typename?: 'User' }
    & { tool?: Types.Maybe<(
      { __typename?: 'CollectionTool' }
      & Pick<Types.CollectionTool, 'name' | 'collectionCount' | 'exerciseCount' | 'lessonCount'>
      & { proficiencies: Array<(
        { __typename?: 'UserProficiency' }
        & UserProficiencyFragment
      )> }
    )> }
  )> }
);

export type UserProficiencyFragment = (
  { __typename?: 'UserProficiency' }
  & Pick<Types.UserProficiency, 'points' | 'pointsForNextLevel'>
  & { topic: (
    { __typename?: 'Topic' }
    & TopicFragment
  ), level: (
    { __typename?: 'Level' }
    & LevelFragment
  ) }
);

export type AllExercisesOverviewQueryVariables = {
  userJwt: Types.Scalars['String'];
  toolId: Types.Scalars['String'];
};


export type AllExercisesOverviewQuery = (
  { __typename?: 'Query' }
  & { me?: Types.Maybe<(
    { __typename?: 'User' }
    & { tool?: Types.Maybe<(
      { __typename?: 'CollectionTool' }
      & { allExercises: Array<(
        { __typename?: 'Exercise' }
        & { topicsWithLevels: Array<(
          { __typename?: 'TopicWithLevel' }
          & TopicWithLevelFragment
        )> }
        & FieldsForLinkFragment
      )> }
    )> }
  )> }
);

export type CollectionValuesFragment = (
  { __typename?: 'ExerciseCollection' }
  & Pick<Types.ExerciseCollection, 'collectionId' | 'title' | 'exerciseCount'>
);

export type CollectionListQueryVariables = {
  userJwt: Types.Scalars['String'];
  toolId: Types.Scalars['String'];
};


export type CollectionListQuery = (
  { __typename?: 'Query' }
  & { me?: Types.Maybe<(
    { __typename?: 'User' }
    & { tool?: Types.Maybe<(
      { __typename?: 'CollectionTool' }
      & Pick<Types.CollectionTool, 'name'>
      & { collections: Array<(
        { __typename?: 'ExerciseCollection' }
        & CollectionValuesFragment
      )> }
    )> }
  )> }
);

export type CollectionOverviewQueryVariables = {
  userJwt: Types.Scalars['String'];
  toolId: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
};


export type CollectionOverviewQuery = (
  { __typename?: 'Query' }
  & { me?: Types.Maybe<(
    { __typename?: 'User' }
    & { tool?: Types.Maybe<(
      { __typename?: 'CollectionTool' }
      & { collection?: Types.Maybe<(
        { __typename?: 'ExerciseCollection' }
        & Pick<Types.ExerciseCollection, 'title'>
        & { exercises: Array<(
          { __typename?: 'Exercise' }
          & FieldsForLinkFragment
        )> }
      )> }
    )> }
  )> }
);

export type ExerciseOverviewQueryVariables = {
  userJwt: Types.Scalars['String'];
  toolId: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
};


export type ExerciseOverviewQuery = (
  { __typename?: 'Query' }
  & { me?: Types.Maybe<(
    { __typename?: 'User' }
    & { tool?: Types.Maybe<(
      { __typename?: 'CollectionTool' }
      & { collection?: Types.Maybe<(
        { __typename?: 'ExerciseCollection' }
        & { exercise?: Types.Maybe<(
          { __typename?: 'Exercise' }
          & Pick<Types.Exercise, 'exerciseId' | 'title' | 'text'>
          & { parts: Array<(
            { __typename?: 'ExPart' }
            & PartFragment
          )> }
        )> }
      )> }
    )> }
  )> }
);

export type ExerciseSolveFieldsFragment = (
  { __typename?: 'Exercise' }
  & Pick<Types.Exercise, 'exerciseId' | 'collectionId' | 'toolId' | 'title' | 'text'>
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
);

export type ExerciseQueryVariables = {
  userJwt: Types.Scalars['String'];
  toolId: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
  partId: Types.Scalars['String'];
};


export type ExerciseQuery = (
  { __typename?: 'Query' }
  & { me?: Types.Maybe<(
    { __typename?: 'User' }
    & { tool?: Types.Maybe<(
      { __typename?: 'CollectionTool' }
      & { collection?: Types.Maybe<(
        { __typename?: 'ExerciseCollection' }
        & { exercise?: Types.Maybe<(
          { __typename?: 'Exercise' }
          & ExerciseSolveFieldsFragment
        )> }
      )> }
    )> }
  )> }
);

export type LessonIdentifierFragment = (
  { __typename?: 'Lesson' }
  & Pick<Types.Lesson, 'lessonId' | 'title' | 'description'>
);

export type LessonsForToolQueryVariables = {
  userJwt: Types.Scalars['String'];
  toolId: Types.Scalars['String'];
};


export type LessonsForToolQuery = (
  { __typename?: 'Query' }
  & { me?: Types.Maybe<(
    { __typename?: 'User' }
    & { tool?: Types.Maybe<(
      { __typename?: 'CollectionTool' }
      & Pick<Types.CollectionTool, 'name'>
      & { lessons: Array<(
        { __typename?: 'Lesson' }
        & LessonIdentifierFragment
      )> }
    )> }
  )> }
);

type LessonContent_LessonMultipleChoiceQuestionsContent_Fragment = (
  { __typename: 'LessonMultipleChoiceQuestionsContent' }
  & Pick<Types.LessonMultipleChoiceQuestionsContent, 'contentId'>
);

type LessonContent_LessonTextContent_Fragment = (
  { __typename: 'LessonTextContent' }
  & Pick<Types.LessonTextContent, 'contentId'>
);

export type LessonContentFragment = LessonContent_LessonMultipleChoiceQuestionsContent_Fragment | LessonContent_LessonTextContent_Fragment;

export type LessonFragment = (
  { __typename?: 'Lesson' }
  & Pick<Types.Lesson, 'lessonId' | 'title' | 'description'>
  & { contents: Array<(
    { __typename?: 'LessonMultipleChoiceQuestionsContent' }
    & LessonContent_LessonMultipleChoiceQuestionsContent_Fragment
  ) | (
    { __typename?: 'LessonTextContent' }
    & LessonContent_LessonTextContent_Fragment
  )> }
);

export type LessonQueryVariables = {
  userJwt: Types.Scalars['String'];
  toolId: Types.Scalars['String'];
  lessonId: Types.Scalars['Int'];
};


export type LessonQuery = (
  { __typename?: 'Query' }
  & { me?: Types.Maybe<(
    { __typename?: 'User' }
    & { tool?: Types.Maybe<(
      { __typename?: 'CollectionTool' }
      & Pick<Types.CollectionTool, 'name'>
      & { lesson?: Types.Maybe<(
        { __typename?: 'Lesson' }
        & LessonFragment
      )> }
    )> }
  )> }
);

export type LevelFragment = (
  { __typename?: 'Level' }
  & Pick<Types.Level, 'title' | 'levelIndex'>
);

export type TopicFragment = (
  { __typename?: 'Topic' }
  & Pick<Types.Topic, 'abbreviation' | 'title'>
  & { maxLevel: (
    { __typename?: 'Level' }
    & LevelFragment
  ) }
);

export type TopicWithLevelFragment = (
  { __typename?: 'TopicWithLevel' }
  & { topic: (
    { __typename?: 'Topic' }
    & TopicFragment
  ), level: (
    { __typename?: 'Level' }
    & LevelFragment
  ) }
);

export type PartFragment = (
  { __typename?: 'ExPart' }
  & Pick<Types.ExPart, 'id' | 'name' | 'isEntryPart'>
);

export type FieldsForLinkFragment = (
  { __typename?: 'Exercise' }
  & Pick<Types.Exercise, 'exerciseId' | 'collectionId' | 'toolId' | 'title' | 'difficulty'>
  & { topicsWithLevels: Array<(
    { __typename?: 'TopicWithLevel' }
    & TopicWithLevelFragment
  )> }
);

export type ProgExerciseContentSolveFieldsFragment = (
  { __typename?: 'ProgrammingExerciseContent' }
  & Pick<Types.ProgrammingExerciseContent, 'part'>
  & { unitTestPart: (
    { __typename?: 'UnitTestPart' }
    & Pick<Types.UnitTestPart, 'unitTestType'>
    & { unitTestFiles: Array<(
      { __typename?: 'ExerciseFile' }
      & ExerciseFileFragment
    )> }
  ), implementationPart: (
    { __typename?: 'ImplementationPart' }
    & { files: Array<(
      { __typename?: 'ExerciseFile' }
      & ExerciseFileFragment
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
      & ExerciseFileFragment
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
    & ExerciseFileFragment
  )>, siteSpec: (
    { __typename?: 'SiteSpec' }
    & Pick<Types.SiteSpec, 'fileName' | 'jsTaskCount'>
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
      & ExerciseFileFragment
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

export type ExerciseFileFragment = (
  { __typename?: 'ExerciseFile' }
  & Pick<Types.ExerciseFile, 'name' | 'fileType' | 'content' | 'editable'>
);

export const LoggedInUserWithTokenFragmentDoc = gql`
    fragment LoggedInUserWithToken on LoggedInUserWithToken {
  loggedInUser {
    username
    isAdmin
  }
  jwt
}
    `;
export const CollectionToolFragmentDoc = gql`
    fragment CollectionTool on CollectionTool {
  id
  name
  state
  collectionCount
  lessonCount
  exerciseCount
}
    `;
export const LevelFragmentDoc = gql`
    fragment Level on Level {
  title
  levelIndex
}
    `;
export const TopicFragmentDoc = gql`
    fragment Topic on Topic {
  abbreviation
  title
  maxLevel {
    ...Level
  }
}
    ${LevelFragmentDoc}`;
export const UserProficiencyFragmentDoc = gql`
    fragment UserProficiency on UserProficiency {
  topic {
    ...Topic
  }
  points
  pointsForNextLevel
  level {
    ...Level
  }
}
    ${TopicFragmentDoc}
${LevelFragmentDoc}`;
export const CollectionValuesFragmentDoc = gql`
    fragment CollectionValues on ExerciseCollection {
  collectionId
  title
  exerciseCount
}
    `;
export const ExerciseFileFragmentDoc = gql`
    fragment ExerciseFile on ExerciseFile {
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
      ...ExerciseFile
    }
  }
}
    ${ExerciseFileFragmentDoc}`;
export const ProgExerciseContentSolveFieldsFragmentDoc = gql`
    fragment ProgExerciseContentSolveFields on ProgrammingExerciseContent {
  unitTestPart {
    unitTestType
    unitTestFiles {
      ...ExerciseFile
    }
  }
  implementationPart {
    files {
      ...ExerciseFile
    }
  }
  sampleSolutions {
    ...ProgrammingSampleSolution
  }
  part(partId: $partId)
}
    ${ExerciseFileFragmentDoc}
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
      ...ExerciseFile
    }
  }
}
    ${ExerciseFileFragmentDoc}`;
export const WebExerciseContentSolveFieldsFragmentDoc = gql`
    fragment WebExerciseContentSolveFields on WebExerciseContent {
  files {
    ...ExerciseFile
  }
  siteSpec {
    fileName
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
    ${ExerciseFileFragmentDoc}
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
export const ExerciseSolveFieldsFragmentDoc = gql`
    fragment ExerciseSolveFields on Exercise {
  exerciseId
  collectionId
  toolId
  title
  text
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
    ${ProgExerciseContentSolveFieldsFragmentDoc}
${RegexExerciseContentSolveFieldsFragmentDoc}
${SqlExerciseContentSolveFieldsFragmentDoc}
${UmlExerciseContentSolveFieldsFragmentDoc}
${WebExerciseContentSolveFieldsFragmentDoc}
${XmlExerciseContentSolveFieldsFragmentDoc}`;
export const LessonIdentifierFragmentDoc = gql`
    fragment LessonIdentifier on Lesson {
  lessonId
  title
  description
}
    `;
export const LessonContentFragmentDoc = gql`
    fragment LessonContent on LessonContent {
  __typename
  contentId
}
    `;
export const LessonFragmentDoc = gql`
    fragment Lesson on Lesson {
  lessonId
  title
  description
  contents {
    ...LessonContent
  }
}
    ${LessonContentFragmentDoc}`;
export const PartFragmentDoc = gql`
    fragment Part on ExPart {
  id
  name
  isEntryPart
}
    `;
export const TopicWithLevelFragmentDoc = gql`
    fragment TopicWithLevel on TopicWithLevel {
  topic {
    ...Topic
  }
  level {
    ...Level
  }
}
    ${TopicFragmentDoc}
${LevelFragmentDoc}`;
export const FieldsForLinkFragmentDoc = gql`
    fragment FieldsForLink on Exercise {
  exerciseId
  collectionId
  toolId
  title
  difficulty
  topicsWithLevels {
    ...TopicWithLevel
  }
}
    ${TopicWithLevelFragmentDoc}`;
export const RegisterDocument = gql`
    mutation Register($username: String!, $firstPassword: String!, $secondPassword: String!) {
  register(registerValues: {username: $username, firstPassword: $firstPassword, secondPassword: $secondPassword})
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class RegisterGQL extends Apollo.Mutation<RegisterMutation, RegisterMutationVariables> {
    document = RegisterDocument;
    
  }
export const LoginDocument = gql`
    mutation Login($username: String!, $password: String!) {
  login(credentials: {username: $username, password: $password}) {
    ...LoggedInUserWithToken
  }
}
    ${LoggedInUserWithTokenFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class LoginGQL extends Apollo.Mutation<LoginMutation, LoginMutationVariables> {
    document = LoginDocument;
    
  }
export const ToolOverviewDocument = gql`
    query ToolOverview($userJwt: String!) {
  me(userJwt: $userJwt) {
    tools {
      ...CollectionTool
    }
  }
}
    ${CollectionToolFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class ToolOverviewGQL extends Apollo.Query<ToolOverviewQuery, ToolOverviewQueryVariables> {
    document = ToolOverviewDocument;
    
  }
export const CollectionToolOverviewDocument = gql`
    query CollectionToolOverview($userJwt: String!, $toolId: String!) {
  me(userJwt: $userJwt) {
    tool(toolId: $toolId) {
      name
      collectionCount
      exerciseCount
      lessonCount
      proficiencies {
        ...UserProficiency
      }
    }
  }
}
    ${UserProficiencyFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class CollectionToolOverviewGQL extends Apollo.Query<CollectionToolOverviewQuery, CollectionToolOverviewQueryVariables> {
    document = CollectionToolOverviewDocument;
    
  }
export const AllExercisesOverviewDocument = gql`
    query AllExercisesOverview($userJwt: String!, $toolId: String!) {
  me(userJwt: $userJwt) {
    tool(toolId: $toolId) {
      allExercises {
        topicsWithLevels {
          ...TopicWithLevel
        }
        ...FieldsForLink
      }
    }
  }
}
    ${TopicWithLevelFragmentDoc}
${FieldsForLinkFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class AllExercisesOverviewGQL extends Apollo.Query<AllExercisesOverviewQuery, AllExercisesOverviewQueryVariables> {
    document = AllExercisesOverviewDocument;
    
  }
export const CollectionListDocument = gql`
    query CollectionList($userJwt: String!, $toolId: String!) {
  me(userJwt: $userJwt) {
    tool(toolId: $toolId) {
      name
      collections {
        ...CollectionValues
      }
    }
  }
}
    ${CollectionValuesFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class CollectionListGQL extends Apollo.Query<CollectionListQuery, CollectionListQueryVariables> {
    document = CollectionListDocument;
    
  }
export const CollectionOverviewDocument = gql`
    query CollectionOverview($userJwt: String!, $toolId: String!, $collId: Int!) {
  me(userJwt: $userJwt) {
    tool(toolId: $toolId) {
      collection(collId: $collId) {
        title
        exercises {
          ...FieldsForLink
        }
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
    query ExerciseOverview($userJwt: String!, $toolId: String!, $collId: Int!, $exId: Int!) {
  me(userJwt: $userJwt) {
    tool(toolId: $toolId) {
      collection(collId: $collId) {
        exercise(exId: $exId) {
          exerciseId
          title
          text
          parts {
            ...Part
          }
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
    query Exercise($userJwt: String!, $toolId: String!, $collId: Int!, $exId: Int!, $partId: String!) {
  me(userJwt: $userJwt) {
    tool(toolId: $toolId) {
      collection(collId: $collId) {
        exercise(exId: $exId) {
          ...ExerciseSolveFields
        }
      }
    }
  }
}
    ${ExerciseSolveFieldsFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class ExerciseGQL extends Apollo.Query<ExerciseQuery, ExerciseQueryVariables> {
    document = ExerciseDocument;
    
  }
export const LessonsForToolDocument = gql`
    query LessonsForTool($userJwt: String!, $toolId: String!) {
  me(userJwt: $userJwt) {
    tool(toolId: $toolId) {
      name
      lessons {
        ...LessonIdentifier
      }
    }
  }
}
    ${LessonIdentifierFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class LessonsForToolGQL extends Apollo.Query<LessonsForToolQuery, LessonsForToolQueryVariables> {
    document = LessonsForToolDocument;
    
  }
export const LessonDocument = gql`
    query Lesson($userJwt: String!, $toolId: String!, $lessonId: Int!) {
  me(userJwt: $userJwt) {
    tool(toolId: $toolId) {
      name
      lesson(lessonId: $lessonId) {
        ...Lesson
      }
    }
  }
}
    ${LessonFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class LessonGQL extends Apollo.Query<LessonQuery, LessonQueryVariables> {
    document = LessonDocument;
    
  }