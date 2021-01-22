import * as Types from '../_interfaces/graphql-types';

import gql from 'graphql-tag';
import { Injectable } from '@angular/core';
import * as Apollo from 'apollo-angular';
export type LessonIdentifierFragment = (
  { __typename?: 'Lesson' }
  & Pick<Types.Lesson, 'lessonId' | 'title' | 'description' | 'video'>
);

export type LessonsForToolQueryVariables = Types.Exact<{
  userJwt: Types.Scalars['String'];
  toolId: Types.Scalars['String'];
}>;


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

export type LessonOverviewFragment = (
  { __typename?: 'Lesson' }
  & Pick<Types.Lesson, 'title' | 'description' | 'video' | 'contentCount'>
);

export type LessonOverviewQueryVariables = Types.Exact<{
  userJwt: Types.Scalars['String'];
  toolId: Types.Scalars['String'];
  lessonId: Types.Scalars['Int'];
}>;


export type LessonOverviewQuery = (
  { __typename?: 'Query' }
  & { me?: Types.Maybe<(
    { __typename?: 'User' }
    & { tool?: Types.Maybe<(
      { __typename?: 'CollectionTool' }
      & { lesson?: Types.Maybe<(
        { __typename?: 'Lesson' }
        & LessonOverviewFragment
      )> }
    )> }
  )> }
);

export type LessonTextContentFragment = (
  { __typename: 'LessonTextContent' }
  & Pick<Types.LessonTextContent, 'contentId' | 'content'>
);

export type LessonMultipleChoiceQuestionAnswerFragment = (
  { __typename?: 'LessonMultipleChoiceQuestionAnswer' }
  & Pick<Types.LessonMultipleChoiceQuestionAnswer, 'id' | 'answer' | 'isCorrect'>
);

export type LessonMultipleChoiceQuestionFragment = (
  { __typename?: 'LessonMultipleChoiceQuestion' }
  & Pick<Types.LessonMultipleChoiceQuestion, 'id' | 'questionText'>
  & { answers: Array<(
    { __typename?: 'LessonMultipleChoiceQuestionAnswer' }
    & LessonMultipleChoiceQuestionAnswerFragment
  )> }
);

export type LessonMultipleChoiceQuestionContentFragment = (
  { __typename: 'LessonMultipleChoiceQuestionsContent' }
  & Pick<Types.LessonMultipleChoiceQuestionsContent, 'contentId'>
  & { questions: Array<(
    { __typename?: 'LessonMultipleChoiceQuestion' }
    & LessonMultipleChoiceQuestionFragment
  )> }
);

export type LessonAsTextFragment = (
  { __typename?: 'Lesson' }
  & Pick<Types.Lesson, 'lessonId' | 'title' | 'description'>
  & { contents: Array<(
    { __typename?: 'LessonMultipleChoiceQuestionsContent' }
    & LessonMultipleChoiceQuestionContentFragment
  ) | (
    { __typename?: 'LessonTextContent' }
    & LessonTextContentFragment
  )> }
);

export type LessonAsTextQueryVariables = Types.Exact<{
  userJwt: Types.Scalars['String'];
  toolId: Types.Scalars['String'];
  lessonId: Types.Scalars['Int'];
}>;


export type LessonAsTextQuery = (
  { __typename?: 'Query' }
  & { me?: Types.Maybe<(
    { __typename?: 'User' }
    & { tool?: Types.Maybe<(
      { __typename?: 'CollectionTool' }
      & { lesson?: Types.Maybe<(
        { __typename?: 'Lesson' }
        & LessonAsTextFragment
      )> }
    )> }
  )> }
);

export type LessonAsVideoQueryVariables = Types.Exact<{
  userJwt: Types.Scalars['String'];
  toolId: Types.Scalars['String'];
  lessonId: Types.Scalars['Int'];
}>;


export type LessonAsVideoQuery = (
  { __typename?: 'Query' }
  & { me?: Types.Maybe<(
    { __typename?: 'User' }
    & { tool?: Types.Maybe<(
      { __typename?: 'CollectionTool' }
      & { lesson?: Types.Maybe<(
        { __typename?: 'Lesson' }
        & Pick<Types.Lesson, 'title' | 'video'>
      )> }
    )> }
  )> }
);

export type CollectionToolFragment = (
  { __typename?: 'CollectionTool' }
  & Pick<Types.CollectionTool, 'id' | 'name' | 'state' | 'collectionCount' | 'lessonCount' | 'exerciseCount'>
);

export type ToolOverviewQueryVariables = Types.Exact<{
  userJwt: Types.Scalars['String'];
}>;


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

export type CollectionToolOverviewQueryVariables = Types.Exact<{
  userJwt: Types.Scalars['String'];
  toolId: Types.Scalars['String'];
}>;


export type CollectionToolOverviewQuery = (
  { __typename?: 'Query' }
  & { me?: Types.Maybe<(
    { __typename?: 'User' }
    & { tool?: Types.Maybe<(
      { __typename?: 'CollectionTool' }
      & Pick<Types.CollectionTool, 'id' | 'name' | 'collectionCount' | 'exerciseCount' | 'lessonCount'>
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

export type AllExercisesOverviewQueryVariables = Types.Exact<{
  userJwt: Types.Scalars['String'];
  toolId: Types.Scalars['String'];
}>;


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

export type CollectionListQueryVariables = Types.Exact<{
  userJwt: Types.Scalars['String'];
  toolId: Types.Scalars['String'];
}>;


export type CollectionListQuery = (
  { __typename?: 'Query' }
  & { me?: Types.Maybe<(
    { __typename?: 'User' }
    & { tool?: Types.Maybe<(
      { __typename?: 'CollectionTool' }
      & Pick<Types.CollectionTool, 'id' | 'name'>
      & { collections: Array<(
        { __typename?: 'ExerciseCollection' }
        & CollectionValuesFragment
      )> }
    )> }
  )> }
);

export type CollOverviewToolFragment = (
  { __typename?: 'CollectionTool' }
  & Pick<Types.CollectionTool, 'id' | 'name'>
  & { collection?: Types.Maybe<(
    { __typename?: 'ExerciseCollection' }
    & Pick<Types.ExerciseCollection, 'collectionId' | 'title'>
    & { exercises: Array<(
      { __typename?: 'Exercise' }
      & FieldsForLinkFragment
    )> }
  )> }
);

export type CollectionOverviewQueryVariables = Types.Exact<{
  userJwt: Types.Scalars['String'];
  toolId: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
}>;


export type CollectionOverviewQuery = (
  { __typename?: 'Query' }
  & { me?: Types.Maybe<(
    { __typename?: 'User' }
    & { tool?: Types.Maybe<(
      { __typename?: 'CollectionTool' }
      & CollOverviewToolFragment
    )> }
  )> }
);

export type PartFragment = (
  { __typename?: 'ExPart' }
  & Pick<Types.ExPart, 'id' | 'name' | 'isEntryPart' | 'solved'>
);

export type ExerciseOverviewFragment = (
  { __typename?: 'Exercise' }
  & Pick<Types.Exercise, 'exerciseId' | 'title' | 'text'>
  & { parts: Array<(
    { __typename?: 'ExPart' }
    & PartFragment
  )> }
);

export type ExerciseOverviewQueryVariables = Types.Exact<{
  userJwt: Types.Scalars['String'];
  toolId: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
}>;


export type ExerciseOverviewQuery = (
  { __typename?: 'Query' }
  & { me?: Types.Maybe<(
    { __typename?: 'User' }
    & { tool?: Types.Maybe<(
      { __typename?: 'CollectionTool' }
      & Pick<Types.CollectionTool, 'id' | 'name'>
      & { collection?: Types.Maybe<(
        { __typename?: 'ExerciseCollection' }
        & Pick<Types.ExerciseCollection, 'collectionId' | 'title'>
        & { exercise?: Types.Maybe<(
          { __typename?: 'Exercise' }
          & ExerciseOverviewFragment
        )> }
      )> }
    )> }
  )> }
);

export type ExerciseSolveFieldsFragment = (
  { __typename?: 'Exercise' }
  & Pick<Types.Exercise, 'exerciseId' | 'collectionId' | 'toolId' | 'title' | 'text'>
  & { content: (
    { __typename: 'FlaskExerciseContent' }
    & FlaskExerciseContentFragment
  ) | (
    { __typename: 'ProgrammingExerciseContent' }
    & ProgrammingExerciseContentFragment
    & ProgrammingExerciseContentFragment
  ) | (
    { __typename: 'RegexExerciseContent' }
    & RegexExerciseContentFragment
  ) | (
    { __typename: 'SqlExerciseContent' }
    & SqlExerciseContentFragment
  ) | (
    { __typename: 'UmlExerciseContent' }
    & UmlExerciseContentFragment
  ) | (
    { __typename: 'WebExerciseContent' }
    & WebExerciseContentFragment
  ) | (
    { __typename: 'XmlExerciseContent' }
    & XmlExerciseContentFragment
  ) }
);

export type ExerciseQueryVariables = Types.Exact<{
  userJwt: Types.Scalars['String'];
  toolId: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
  partId: Types.Scalars['String'];
}>;


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

export type ExerciseFileFragment = (
  { __typename?: 'ExerciseFile' }
  & Pick<Types.ExerciseFile, 'name' | 'fileType' | 'content' | 'editable'>
);

export type FilesSolutionFragment = (
  { __typename: 'FilesSolution' }
  & { files: Array<(
    { __typename?: 'ExerciseFile' }
    & ExerciseFileFragment
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

export type FieldsPartFragment = (
  { __typename?: 'ExPart' }
  & Pick<Types.ExPart, 'id' | 'name' | 'solved'>
);

export type FieldsForLinkFragment = (
  { __typename?: 'Exercise' }
  & Pick<Types.Exercise, 'exerciseId' | 'collectionId' | 'toolId' | 'title' | 'difficulty'>
  & { topicsWithLevels: Array<(
    { __typename?: 'TopicWithLevel' }
    & TopicWithLevelFragment
  )>, parts: Array<(
    { __typename?: 'ExPart' }
    & FieldsPartFragment
  )> }
);

export type FlaskExerciseContentFragment = (
  { __typename: 'FlaskExerciseContent' }
  & { testConfig: (
    { __typename?: 'FlaskTestsConfig' }
    & { tests: Array<(
      { __typename?: 'FlaskSingleTestConfig' }
      & Pick<Types.FlaskSingleTestConfig, 'id' | 'testName' | 'description'>
    )> }
  ), files: Array<(
    { __typename?: 'ExerciseFile' }
    & ExerciseFileFragment
  )>, flaskSampleSolutions: Array<(
    { __typename?: 'FilesSolution' }
    & FilesSolutionFragment
  )> }
);

export type SimplifiedUnitTestPartFragment = (
  { __typename?: 'SimplifiedUnitTestPart' }
  & { simplifiedTestMainFile: (
    { __typename?: 'ExerciseFile' }
    & ExerciseFileFragment
  ) }
);

export type NormalUnitTestPartFragment = (
  { __typename?: 'NormalUnitTestPart' }
  & { unitTestFiles: Array<(
    { __typename?: 'ExerciseFile' }
    & ExerciseFileFragment
  )> }
);

export type ProgrammingExerciseContentFragment = (
  { __typename?: 'ProgrammingExerciseContent' }
  & { programmingPart: Types.ProgrammingExerciseContent['part'] }
  & { unitTestPart: (
    { __typename: 'SimplifiedUnitTestPart' }
    & SimplifiedUnitTestPartFragment
  ) | (
    { __typename: 'NormalUnitTestPart' }
    & NormalUnitTestPartFragment
  ), implementationPart: (
    { __typename?: 'ImplementationPart' }
    & { files: Array<(
      { __typename?: 'ExerciseFile' }
      & ExerciseFileFragment
    )> }
  ), programmingSampleSolutions: Array<(
    { __typename?: 'FilesSolution' }
    & FilesSolutionFragment
  )> }
);

export type RegexExerciseContentFragment = (
  { __typename?: 'RegexExerciseContent' }
  & { regexSampleSolutions: Types.RegexExerciseContent['sampleSolutions'], regexPart: Types.RegexExerciseContent['part'] }
);

export type SqlExerciseContentFragment = (
  { __typename?: 'SqlExerciseContent' }
  & Pick<Types.SqlExerciseContent, 'hint'>
  & { sqlSampleSolutions: Types.SqlExerciseContent['sampleSolutions'], sqlPart: Types.SqlExerciseContent['part'] }
  & { sqlDbContents: Array<(
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

export type UmlExerciseContentFragment = (
  { __typename?: 'UmlExerciseContent' }
  & Pick<Types.UmlExerciseContent, 'toIgnore'>
  & { umlPart: Types.UmlExerciseContent['part'] }
  & { mappings: Array<(
    { __typename?: 'KeyValueObject' }
    & Pick<Types.KeyValueObject, 'key' | 'value'>
  )>, umlSampleSolutions: Array<(
    { __typename?: 'UmlClassDiagram' }
    & UmlClassDiagramFragment
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

export type WebExerciseContentFragment = (
  { __typename?: 'WebExerciseContent' }
  & { webPart: Types.WebExerciseContent['part'] }
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
  ), webSampleSolutions: Array<(
    { __typename?: 'FilesSolution' }
    & FilesSolutionFragment
  )> }
);

export type XmlExerciseContentFragment = (
  { __typename?: 'XmlExerciseContent' }
  & Pick<Types.XmlExerciseContent, 'rootNode' | 'grammarDescription'>
  & { xmlPart: Types.XmlExerciseContent['part'] }
  & { xmlSampleSolutions: Array<(
    { __typename?: 'XmlSolution' }
    & XmlSolutionFragment
  )> }
);

export type XmlSolutionFragment = (
  { __typename: 'XmlSolution' }
  & Pick<Types.XmlSolution, 'document' | 'grammar'>
);

export type RegisterMutationVariables = Types.Exact<{
  username: Types.Scalars['String'];
  firstPassword: Types.Scalars['String'];
  secondPassword: Types.Scalars['String'];
}>;


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

export type LoginMutationVariables = Types.Exact<{
  username: Types.Scalars['String'];
  password: Types.Scalars['String'];
}>;


export type LoginMutation = (
  { __typename?: 'Mutation' }
  & { login?: Types.Maybe<(
    { __typename?: 'LoggedInUserWithToken' }
    & LoggedInUserWithTokenFragment
  )> }
);

export const LessonIdentifierFragmentDoc = gql`
    fragment LessonIdentifier on Lesson {
  lessonId
  title
  description
  video
}
    `;
export const LessonOverviewFragmentDoc = gql`
    fragment LessonOverview on Lesson {
  title
  description
  video
  contentCount
}
    `;
export const LessonTextContentFragmentDoc = gql`
    fragment LessonTextContent on LessonTextContent {
  __typename
  contentId
  content
}
    `;
export const LessonMultipleChoiceQuestionAnswerFragmentDoc = gql`
    fragment LessonMultipleChoiceQuestionAnswer on LessonMultipleChoiceQuestionAnswer {
  id
  answer
  isCorrect
}
    `;
export const LessonMultipleChoiceQuestionFragmentDoc = gql`
    fragment LessonMultipleChoiceQuestion on LessonMultipleChoiceQuestion {
  id
  questionText
  answers {
    ...LessonMultipleChoiceQuestionAnswer
  }
}
    ${LessonMultipleChoiceQuestionAnswerFragmentDoc}`;
export const LessonMultipleChoiceQuestionContentFragmentDoc = gql`
    fragment LessonMultipleChoiceQuestionContent on LessonMultipleChoiceQuestionsContent {
  __typename
  contentId
  questions {
    ...LessonMultipleChoiceQuestion
  }
}
    ${LessonMultipleChoiceQuestionFragmentDoc}`;
export const LessonAsTextFragmentDoc = gql`
    fragment LessonAsText on Lesson {
  lessonId
  title
  description
  contents {
    ... on LessonTextContent {
      ...LessonTextContent
    }
    ... on LessonMultipleChoiceQuestionsContent {
      ...LessonMultipleChoiceQuestionContent
    }
  }
}
    ${LessonTextContentFragmentDoc}
${LessonMultipleChoiceQuestionContentFragmentDoc}`;
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
export const FieldsPartFragmentDoc = gql`
    fragment FieldsPart on ExPart {
  id
  name
  solved
}
    `;
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
  parts {
    ...FieldsPart
  }
}
    ${TopicWithLevelFragmentDoc}
${FieldsPartFragmentDoc}`;
export const CollOverviewToolFragmentDoc = gql`
    fragment CollOverviewTool on CollectionTool {
  id
  name
  collection(collId: $collId) {
    collectionId
    title
    exercises {
      ...FieldsForLink
    }
  }
}
    ${FieldsForLinkFragmentDoc}`;
export const PartFragmentDoc = gql`
    fragment Part on ExPart {
  id
  name
  isEntryPart
  solved
}
    `;
export const ExerciseOverviewFragmentDoc = gql`
    fragment ExerciseOverview on Exercise {
  exerciseId
  title
  text
  parts {
    ...Part
  }
}
    ${PartFragmentDoc}`;
export const ExerciseFileFragmentDoc = gql`
    fragment ExerciseFile on ExerciseFile {
  name
  fileType
  content
  editable
}
    `;
export const FilesSolutionFragmentDoc = gql`
    fragment FilesSolution on FilesSolution {
  __typename
  files {
    ...ExerciseFile
  }
}
    ${ExerciseFileFragmentDoc}`;
export const FlaskExerciseContentFragmentDoc = gql`
    fragment FlaskExerciseContent on FlaskExerciseContent {
  __typename
  testConfig {
    tests {
      id
      testName
      description
    }
  }
  files {
    ...ExerciseFile
  }
  flaskSampleSolutions: sampleSolutions {
    ...FilesSolution
  }
}
    ${ExerciseFileFragmentDoc}
${FilesSolutionFragmentDoc}`;
export const NormalUnitTestPartFragmentDoc = gql`
    fragment NormalUnitTestPart on NormalUnitTestPart {
  unitTestFiles {
    ...ExerciseFile
  }
}
    ${ExerciseFileFragmentDoc}`;
export const SimplifiedUnitTestPartFragmentDoc = gql`
    fragment SimplifiedUnitTestPart on SimplifiedUnitTestPart {
  simplifiedTestMainFile {
    ...ExerciseFile
  }
}
    ${ExerciseFileFragmentDoc}`;
export const ProgrammingExerciseContentFragmentDoc = gql`
    fragment ProgrammingExerciseContent on ProgrammingExerciseContent {
  unitTestPart {
    __typename
    ...NormalUnitTestPart
    ...SimplifiedUnitTestPart
  }
  implementationPart {
    files {
      ...ExerciseFile
    }
  }
  programmingSampleSolutions: sampleSolutions {
    ...FilesSolution
  }
  programmingPart: part(partId: $partId)
}
    ${NormalUnitTestPartFragmentDoc}
${SimplifiedUnitTestPartFragmentDoc}
${ExerciseFileFragmentDoc}
${FilesSolutionFragmentDoc}`;
export const RegexExerciseContentFragmentDoc = gql`
    fragment RegexExerciseContent on RegexExerciseContent {
  regexSampleSolutions: sampleSolutions
  regexPart: part(partId: $partId)
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
export const SqlExerciseContentFragmentDoc = gql`
    fragment SqlExerciseContent on SqlExerciseContent {
  hint
  sqlSampleSolutions: sampleSolutions
  sqlPart: part(partId: $partId)
  sqlDbContents {
    ...SqlQueryResult
  }
}
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
export const UmlExerciseContentFragmentDoc = gql`
    fragment UmlExerciseContent on UmlExerciseContent {
  toIgnore
  mappings {
    key
    value
  }
  umlSampleSolutions: sampleSolutions {
    ...UmlClassDiagram
  }
  umlPart: part(partId: $partId)
}
    ${UmlClassDiagramFragmentDoc}`;
export const WebExerciseContentFragmentDoc = gql`
    fragment WebExerciseContent on WebExerciseContent {
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
  webSampleSolutions: sampleSolutions {
    ...FilesSolution
  }
  webPart: part(partId: $partId)
}
    ${ExerciseFileFragmentDoc}
${FilesSolutionFragmentDoc}`;
export const XmlSolutionFragmentDoc = gql`
    fragment XmlSolution on XmlSolution {
  __typename
  document
  grammar
}
    `;
export const XmlExerciseContentFragmentDoc = gql`
    fragment XmlExerciseContent on XmlExerciseContent {
  rootNode
  grammarDescription
  xmlSampleSolutions: sampleSolutions {
    ...XmlSolution
  }
  xmlPart: part(partId: $partId)
}
    ${XmlSolutionFragmentDoc}`;
export const ExerciseSolveFieldsFragmentDoc = gql`
    fragment ExerciseSolveFields on Exercise {
  exerciseId
  collectionId
  toolId
  title
  text
  content {
    __typename
    ...FlaskExerciseContent
    ...ProgrammingExerciseContent
    ...ProgrammingExerciseContent
    ...RegexExerciseContent
    ...SqlExerciseContent
    ...UmlExerciseContent
    ...WebExerciseContent
    ...XmlExerciseContent
  }
}
    ${FlaskExerciseContentFragmentDoc}
${ProgrammingExerciseContentFragmentDoc}
${RegexExerciseContentFragmentDoc}
${SqlExerciseContentFragmentDoc}
${UmlExerciseContentFragmentDoc}
${WebExerciseContentFragmentDoc}
${XmlExerciseContentFragmentDoc}`;
export const LoggedInUserWithTokenFragmentDoc = gql`
    fragment LoggedInUserWithToken on LoggedInUserWithToken {
  loggedInUser {
    username
    isAdmin
  }
  jwt
}
    `;
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
export const LessonOverviewDocument = gql`
    query LessonOverview($userJwt: String!, $toolId: String!, $lessonId: Int!) {
  me(userJwt: $userJwt) {
    tool(toolId: $toolId) {
      lesson(lessonId: $lessonId) {
        ...LessonOverview
      }
    }
  }
}
    ${LessonOverviewFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class LessonOverviewGQL extends Apollo.Query<LessonOverviewQuery, LessonOverviewQueryVariables> {
    document = LessonOverviewDocument;
    
  }
export const LessonAsTextDocument = gql`
    query LessonAsText($userJwt: String!, $toolId: String!, $lessonId: Int!) {
  me(userJwt: $userJwt) {
    tool(toolId: $toolId) {
      lesson(lessonId: $lessonId) {
        ...LessonAsText
      }
    }
  }
}
    ${LessonAsTextFragmentDoc}`;

  @Injectable({
    providedIn: 'root'
  })
  export class LessonAsTextGQL extends Apollo.Query<LessonAsTextQuery, LessonAsTextQueryVariables> {
    document = LessonAsTextDocument;
    
  }
export const LessonAsVideoDocument = gql`
    query LessonAsVideo($userJwt: String!, $toolId: String!, $lessonId: Int!) {
  me(userJwt: $userJwt) {
    tool(toolId: $toolId) {
      lesson(lessonId: $lessonId) {
        title
        video
      }
    }
  }
}
    `;

  @Injectable({
    providedIn: 'root'
  })
  export class LessonAsVideoGQL extends Apollo.Query<LessonAsVideoQuery, LessonAsVideoQueryVariables> {
    document = LessonAsVideoDocument;
    
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
      id
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
      id
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
      ...CollOverviewTool
    }
  }
}
    ${CollOverviewToolFragmentDoc}`;

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
      id
      name
      collection(collId: $collId) {
        collectionId
        title
        exercise(exId: $exId) {
          ...ExerciseOverview
        }
      }
    }
  }
}
    ${ExerciseOverviewFragmentDoc}`;

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
export const RegisterDocument = gql`
    mutation Register($username: String!, $firstPassword: String!, $secondPassword: String!) {
  register(
    registerValues: {username: $username, firstPassword: $firstPassword, secondPassword: $secondPassword}
  )
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