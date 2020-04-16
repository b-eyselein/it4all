import * as Types from '../_interfaces/graphql-types';

import gql from 'graphql-tag';
import { Injectable } from '@angular/core';
import * as Apollo from 'apollo-angular';

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

export type ExercisesQueryVariables = {
  toolId: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
};


export type ExercisesQuery = (
  { __typename?: 'Query' }
  & { tool?: Types.Maybe<(
    { __typename?: 'CollectionTol' }
    & { collection?: Types.Maybe<(
      { __typename?: 'ExerciseCollection' }
      & { exercises: Array<(
        { __typename?: 'ProgrammingExercise' }
        & Pick<Types.ProgrammingExercise, 'id' | 'title'>
      ) | (
        { __typename?: 'RegexExercise' }
        & Pick<Types.RegexExercise, 'id' | 'title'>
      ) | (
        { __typename?: 'SqlExercise' }
        & Pick<Types.SqlExercise, 'id' | 'title'>
      ) | (
        { __typename?: 'UmlExercise' }
        & Pick<Types.UmlExercise, 'id' | 'title'>
      ) | (
        { __typename?: 'WebExercise' }
        & Pick<Types.WebExercise, 'id' | 'title'>
      ) | (
        { __typename?: 'XmlExercise' }
        & Pick<Types.XmlExercise, 'id' | 'title'>
      )> }
    )> }
  )> }
);

export type CollectionToolOverviewQueryVariables = {
  toolId: Types.Scalars['String'];
};


export type CollectionToolOverviewQuery = (
  { __typename?: 'Query' }
  & { tool?: Types.Maybe<(
    { __typename?: 'CollectionTol' }
    & Pick<Types.CollectionTol, 'name' | 'exerciseCount' | 'collectionCount' | 'lessonCount'>
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
        { __typename?: 'ProgrammingExercise' }
        & FieldsForLink_ProgrammingExercise_Fragment
      ) | (
        { __typename?: 'RegexExercise' }
        & FieldsForLink_RegexExercise_Fragment
      ) | (
        { __typename?: 'SqlExercise' }
        & FieldsForLink_SqlExercise_Fragment
      ) | (
        { __typename?: 'UmlExercise' }
        & FieldsForLink_UmlExercise_Fragment
      ) | (
        { __typename?: 'WebExercise' }
        & FieldsForLink_WebExercise_Fragment
      ) | (
        { __typename?: 'XmlExercise' }
        & FieldsForLink_XmlExercise_Fragment
      )> }
    )> }
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
      { __typename?: 'ProgrammingExercise' }
      & { topics: Array<(
        { __typename?: 'Topic' }
        & TopicFragment
      )> }
      & FieldsForLink_ProgrammingExercise_Fragment
    ) | (
      { __typename?: 'RegexExercise' }
      & { topics: Array<(
        { __typename?: 'Topic' }
        & TopicFragment
      )> }
      & FieldsForLink_RegexExercise_Fragment
    ) | (
      { __typename?: 'SqlExercise' }
      & { topics: Array<(
        { __typename?: 'Topic' }
        & TopicFragment
      )> }
      & FieldsForLink_SqlExercise_Fragment
    ) | (
      { __typename?: 'UmlExercise' }
      & { topics: Array<(
        { __typename?: 'Topic' }
        & TopicFragment
      )> }
      & FieldsForLink_UmlExercise_Fragment
    ) | (
      { __typename?: 'WebExercise' }
      & { topics: Array<(
        { __typename?: 'Topic' }
        & TopicFragment
      )> }
      & FieldsForLink_WebExercise_Fragment
    ) | (
      { __typename?: 'XmlExercise' }
      & { topics: Array<(
        { __typename?: 'Topic' }
        & TopicFragment
      )> }
      & FieldsForLink_XmlExercise_Fragment
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
        { __typename?: 'ProgrammingExercise' }
        & Pick<Types.ProgrammingExercise, 'id' | 'title' | 'text'>
        & { unitTestPart: (
          { __typename?: 'UnitTestPart' }
          & Pick<Types.UnitTestPart, 'unitTestType'>
        ) }
      ) | (
        { __typename?: 'RegexExercise' }
        & Pick<Types.RegexExercise, 'id' | 'title' | 'text'>
      ) | (
        { __typename?: 'SqlExercise' }
        & Pick<Types.SqlExercise, 'id' | 'title' | 'text'>
      ) | (
        { __typename?: 'UmlExercise' }
        & Pick<Types.UmlExercise, 'id' | 'title' | 'text'>
      ) | (
        { __typename?: 'WebExercise' }
        & Pick<Types.WebExercise, 'id' | 'title' | 'text'>
        & { siteSpec: (
          { __typename?: 'SiteSpec' }
          & Pick<Types.SiteSpec, 'htmlTaskCount' | 'jsTaskCount'>
        ) }
      ) | (
        { __typename?: 'XmlExercise' }
        & Pick<Types.XmlExercise, 'id' | 'title' | 'text'>
      )> }
    )> }
  )> }
);

export type ExerciseQueryVariables = {
  toolId: Types.Scalars['String'];
  collId: Types.Scalars['Int'];
  exId: Types.Scalars['Int'];
};


export type ExerciseQuery = (
  { __typename?: 'Query' }
  & { tool?: Types.Maybe<(
    { __typename?: 'CollectionTol' }
    & { collection?: Types.Maybe<(
      { __typename?: 'ExerciseCollection' }
      & Pick<Types.ExerciseCollection, 'shortName'>
      & { exercise?: Types.Maybe<(
        { __typename?: 'ProgrammingExercise' }
        & ExerciseSolveFields_ProgrammingExercise_Fragment
        & ProgExerciseContentSolveFieldsFragment
      ) | (
        { __typename?: 'RegexExercise' }
        & ExerciseSolveFields_RegexExercise_Fragment
        & RegexExerciseContentSolveFieldsFragment
      ) | (
        { __typename?: 'SqlExercise' }
        & ExerciseSolveFields_SqlExercise_Fragment
        & SqlExerciseContentSolveFieldsFragment
      ) | (
        { __typename?: 'UmlExercise' }
        & ExerciseSolveFields_UmlExercise_Fragment
        & UmlExerciseContentSolveFieldsFragment
      ) | (
        { __typename?: 'WebExercise' }
        & ExerciseSolveFields_WebExercise_Fragment
        & WebExerciseContentSolveFieldsFragment
      ) | (
        { __typename?: 'XmlExercise' }
        & ExerciseSolveFields_XmlExercise_Fragment
        & XmlExerciseContentSolveFieldsFragment
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
      & LessonFragmentFragment
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
        { __typename?: 'ProgrammingExercise' }
        & FieldsForLink_ProgrammingExercise_Fragment
      ) | (
        { __typename?: 'RegexExercise' }
        & FieldsForLink_RegexExercise_Fragment
      ) | (
        { __typename?: 'SqlExercise' }
        & FieldsForLink_SqlExercise_Fragment
      ) | (
        { __typename?: 'UmlExercise' }
        & FieldsForLink_UmlExercise_Fragment
      ) | (
        { __typename?: 'WebExercise' }
        & FieldsForLink_WebExercise_Fragment
      ) | (
        { __typename?: 'XmlExercise' }
        & FieldsForLink_XmlExercise_Fragment
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
      & CompleteCollectionFragment
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

export type CompleteCollectionFragment = (
  { __typename?: 'ExerciseCollection' }
  & Pick<Types.ExerciseCollection, 'id' | 'toolId' | 'title' | 'authors' | 'text' | 'shortName'>
);

export type AdminUpsertCollectionMutationVariables = {
  toolId: Types.Scalars['String'];
  content: Types.Scalars['String'];
};


export type AdminUpsertCollectionMutation = (
  { __typename?: 'Mutation' }
  & Pick<Types.Mutation, 'upsertCollection'>
);

export type CompleteLessonFragment = { __typename: 'Lesson' };

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
      & CompleteLessonFragment
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
      & { exercise?: Types.Maybe<{ __typename: 'ProgrammingExercise' } | { __typename: 'RegexExercise' } | { __typename: 'SqlExercise' } | { __typename: 'UmlExercise' } | { __typename: 'WebExercise' } | { __typename: 'XmlExercise' }> }
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

type CompleteExercise_ProgrammingExercise_Fragment = (
  { __typename: 'ProgrammingExercise' }
  & Pick<Types.ProgrammingExercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'authors' | 'text'>
);

type CompleteExercise_RegexExercise_Fragment = (
  { __typename: 'RegexExercise' }
  & Pick<Types.RegexExercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'authors' | 'text'>
);

type CompleteExercise_SqlExercise_Fragment = (
  { __typename: 'SqlExercise' }
  & Pick<Types.SqlExercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'authors' | 'text'>
);

type CompleteExercise_UmlExercise_Fragment = (
  { __typename: 'UmlExercise' }
  & Pick<Types.UmlExercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'authors' | 'text'>
);

type CompleteExercise_WebExercise_Fragment = (
  { __typename: 'WebExercise' }
  & Pick<Types.WebExercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'authors' | 'text'>
);

type CompleteExercise_XmlExercise_Fragment = (
  { __typename: 'XmlExercise' }
  & Pick<Types.XmlExercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'authors' | 'text'>
);

export type CompleteExerciseFragment = CompleteExercise_ProgrammingExercise_Fragment | CompleteExercise_RegexExercise_Fragment | CompleteExercise_SqlExercise_Fragment | CompleteExercise_UmlExercise_Fragment | CompleteExercise_WebExercise_Fragment | CompleteExercise_XmlExercise_Fragment;

export type TopicFragment = (
  { __typename?: 'Topic' }
  & Pick<Types.Topic, 'abbreviation' | 'title'>
);

type FieldsForLink_ProgrammingExercise_Fragment = (
  { __typename?: 'ProgrammingExercise' }
  & Pick<Types.ProgrammingExercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'difficulty'>
  & { topics: Array<(
    { __typename?: 'Topic' }
    & Pick<Types.Topic, 'abbreviation' | 'title'>
  )> }
);

type FieldsForLink_RegexExercise_Fragment = (
  { __typename?: 'RegexExercise' }
  & Pick<Types.RegexExercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'difficulty'>
  & { topics: Array<(
    { __typename?: 'Topic' }
    & Pick<Types.Topic, 'abbreviation' | 'title'>
  )> }
);

type FieldsForLink_SqlExercise_Fragment = (
  { __typename?: 'SqlExercise' }
  & Pick<Types.SqlExercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'difficulty'>
  & { topics: Array<(
    { __typename?: 'Topic' }
    & Pick<Types.Topic, 'abbreviation' | 'title'>
  )> }
);

type FieldsForLink_UmlExercise_Fragment = (
  { __typename?: 'UmlExercise' }
  & Pick<Types.UmlExercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'difficulty'>
  & { topics: Array<(
    { __typename?: 'Topic' }
    & Pick<Types.Topic, 'abbreviation' | 'title'>
  )> }
);

type FieldsForLink_WebExercise_Fragment = (
  { __typename?: 'WebExercise' }
  & Pick<Types.WebExercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'difficulty'>
  & { topics: Array<(
    { __typename?: 'Topic' }
    & Pick<Types.Topic, 'abbreviation' | 'title'>
  )> }
);

type FieldsForLink_XmlExercise_Fragment = (
  { __typename?: 'XmlExercise' }
  & Pick<Types.XmlExercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'difficulty'>
  & { topics: Array<(
    { __typename?: 'Topic' }
    & Pick<Types.Topic, 'abbreviation' | 'title'>
  )> }
);

export type FieldsForLinkFragment = FieldsForLink_ProgrammingExercise_Fragment | FieldsForLink_RegexExercise_Fragment | FieldsForLink_SqlExercise_Fragment | FieldsForLink_UmlExercise_Fragment | FieldsForLink_WebExercise_Fragment | FieldsForLink_XmlExercise_Fragment;

type ExerciseSolveFields_ProgrammingExercise_Fragment = (
  { __typename?: 'ProgrammingExercise' }
  & Pick<Types.ProgrammingExercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'text'>
);

type ExerciseSolveFields_RegexExercise_Fragment = (
  { __typename?: 'RegexExercise' }
  & Pick<Types.RegexExercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'text'>
);

type ExerciseSolveFields_SqlExercise_Fragment = (
  { __typename?: 'SqlExercise' }
  & Pick<Types.SqlExercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'text'>
);

type ExerciseSolveFields_UmlExercise_Fragment = (
  { __typename?: 'UmlExercise' }
  & Pick<Types.UmlExercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'text'>
);

type ExerciseSolveFields_WebExercise_Fragment = (
  { __typename?: 'WebExercise' }
  & Pick<Types.WebExercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'text'>
);

type ExerciseSolveFields_XmlExercise_Fragment = (
  { __typename?: 'XmlExercise' }
  & Pick<Types.XmlExercise, 'id' | 'collectionId' | 'toolId' | 'title' | 'text'>
);

export type ExerciseSolveFieldsFragment = ExerciseSolveFields_ProgrammingExercise_Fragment | ExerciseSolveFields_RegexExercise_Fragment | ExerciseSolveFields_SqlExercise_Fragment | ExerciseSolveFields_UmlExercise_Fragment | ExerciseSolveFields_WebExercise_Fragment | ExerciseSolveFields_XmlExercise_Fragment;

export type ProgExerciseContentSolveFieldsFragment = (
  { __typename?: 'ProgrammingExercise' }
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
  { __typename?: 'RegexExercise' }
  & { regexSampleSolutions: Array<(
    { __typename?: 'StringSampleSolution' }
    & Pick<Types.StringSampleSolution, 'sample'>
  )> }
);

export type SqlExerciseContentSolveFieldsFragment = (
  { __typename?: 'SqlExercise' }
  & Pick<Types.SqlExercise, 'hint'>
  & { sqlSampleSolutions: Array<(
    { __typename?: 'StringSampleSolution' }
    & Pick<Types.StringSampleSolution, 'sample'>
  )> }
);

export type UmlExerciseContentSolveFieldsFragment = (
  { __typename?: 'UmlExercise' }
  & Pick<Types.UmlExercise, 'toIgnore'>
  & { mappings: Array<(
    { __typename?: 'KeyValueObject' }
    & Pick<Types.KeyValueObject, 'key' | 'value'>
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
  { __typename?: 'WebExercise' }
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
  ), webSampleSolutions: Array<(
    { __typename?: 'WebSampleSolution' }
    & { sample: (
      { __typename?: 'WebSolution' }
      & { files: Array<(
        { __typename?: 'ExerciseFile' }
        & ExFileAllFragment
      )> }
    ) }
  )> }
);

export type XmlExerciseContentSolveFieldsFragment = (
  { __typename?: 'XmlExercise' }
  & Pick<Types.XmlExercise, 'rootNode' | 'grammarDescription'>
  & { xmlSampleSolutions: Array<(
    { __typename?: 'XmlSampleSolution' }
    & { sample: (
      { __typename?: 'XmlSolution' }
      & Pick<Types.XmlSolution, 'document' | 'grammar'>
    ) }
  )> }
);

export type ExFileAllFragment = (
  { __typename?: 'ExerciseFile' }
  & Pick<Types.ExerciseFile, 'name' | 'fileType' | 'content' | 'editable'>
);

export type LessonFragmentFragment = (
  { __typename?: 'Lesson' }
  & Pick<Types.Lesson, 'id' | 'title'>
);

export const CompleteCollectionFragmentDoc = gql`
    fragment CompleteCollection on ExerciseCollection {
  id
  toolId
  title
  authors
  text
  shortName
}
    `;
export const CompleteLessonFragmentDoc = gql`
    fragment CompleteLesson on Lesson {
  __typename
}
    `;
export const CompleteExerciseFragmentDoc = gql`
    fragment CompleteExercise on ExerciseInterface {
  id
  collectionId
  toolId
  title
  authors
  text
  __typename
}
    `;
export const TopicFragmentDoc = gql`
    fragment Topic on Topic {
  abbreviation
  title
}
    `;
export const FieldsForLinkFragmentDoc = gql`
    fragment FieldsForLink on ExerciseInterface {
  id
  collectionId
  toolId
  title
  difficulty
  topics {
    abbreviation
    title
  }
}
    `;
export const ExerciseSolveFieldsFragmentDoc = gql`
    fragment ExerciseSolveFields on ExerciseInterface {
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
export const ProgExerciseContentSolveFieldsFragmentDoc = gql`
    fragment ProgExerciseContentSolveFields on ProgrammingExercise {
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
    fragment RegexExerciseContentSolveFields on RegexExercise {
  regexSampleSolutions: sampleSolutions {
    sample
  }
}
    `;
export const SqlExerciseContentSolveFieldsFragmentDoc = gql`
    fragment SqlExerciseContentSolveFields on SqlExercise {
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
    fragment UmlExerciseContentSolveFields on UmlExercise {
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
    fragment WebExerciseContentSolveFields on WebExercise {
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
      files {
        ...ExFileAll
      }
    }
  }
}
    ${ExFileAllFragmentDoc}`;
export const XmlExerciseContentSolveFieldsFragmentDoc = gql`
    fragment XmlExerciseContentSolveFields on XmlExercise {
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
export const ExerciseOverviewDocument = gql`
    query ExerciseOverview($toolId: String!, $collId: Int!, $exId: Int!) {
  tool(toolId: $toolId) {
    collection(collId: $collId) {
      exercise(exId: $exId) {
        id
        title
        text
        ... on ProgrammingExercise {
          unitTestPart {
            unitTestType
          }
        }
        ... on WebExercise {
          siteSpec {
            htmlTaskCount
            jsTaskCount
          }
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
      shortName
      exercise(exId: $exId) {
        ...ExerciseSolveFields
        ...ProgExerciseContentSolveFields
        ...RegexExerciseContentSolveFields
        ...SqlExerciseContentSolveFields
        ...UmlExerciseContentSolveFields
        ...WebExerciseContentSolveFields
        ...XmlExerciseContentSolveFields
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
      ...CompleteCollection
    }
  }
}
    ${CompleteCollectionFragmentDoc}`;

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
      ...CompleteLesson
    }
  }
}
    ${CompleteLessonFragmentDoc}`;

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
        __typename
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