import gql from 'graphql-tag';
import {Injectable} from '@angular/core';
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
  exercises: Array<ExerciseMetaData>;
  exercise?: Maybe<Exercise>;
};


export type CollectionExerciseArgs = {
  id: Scalars['Int'];
};

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

export type ExerciseMetaData = {
  __typename?: 'ExerciseMetaData';
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

export type ExTag = {
  __typename?: 'ExTag';
  abbreviation: Scalars['String'];
  title: Scalars['String'];
};

export type Query = {
  __typename?: 'Query';
  tools: Array<Tool>;
  tool?: Maybe<Tool>;
};


export type QueryToolArgs = {
  toolId: Scalars['String'];
};

export type SemanticVersion = {
  __typename?: 'SemanticVersion';
  major: Scalars['Int'];
  minor: Scalars['Int'];
  patch: Scalars['Int'];
};

export type Tool = {
  __typename?: 'Tool';
  id: Scalars['String'];
  name: Scalars['String'];
  state: ToolState;
  collections: Array<Collection>;
  collection?: Maybe<Collection>;
  allExerciseMetaData: Array<ExerciseMetaData>;
};


export type ToolCollectionArgs = {
  id: Scalars['Int'];
};

export enum ToolState {
  Alpha = 'ALPHA',
  Beta = 'BETA',
  Live = 'LIVE'
}

export type CollectionsQueryVariables = {
  toolId: Scalars['String'];
};


export type CollectionsQuery = (
  { __typename?: 'Query' }
  & {
  tool?: Maybe<(
    { __typename?: 'Tool' }
    & {
    collections: Array<(
      { __typename?: 'Collection' }
      & Pick<Collection, 'id' | 'title'>
      )>
  }
    )>
}
  );

export type ExercisesQueryVariables = {
  toolId: Scalars['String'];
  collId: Scalars['Int'];
};


export type ExercisesQuery = (
  { __typename?: 'Query' }
  & {
  tool?: Maybe<(
    { __typename?: 'Tool' }
    & {
    collection?: Maybe<(
      { __typename?: 'Collection' }
      & {
      exercises: Array<(
        { __typename?: 'ExerciseMetaData' }
        & Pick<ExerciseMetaData, 'id' | 'title'>
        )>
    }
      )>
  }
    )>
}
  );

export type CollectionOverviewQueryVariables = {
  toolId: Scalars['String'];
  collId: Scalars['Int'];
};


export type CollectionOverviewQuery = (
  { __typename?: 'Query' }
  & {
  tool?: Maybe<(
    { __typename?: 'Tool' }
    & {
    collection?: Maybe<(
      { __typename?: 'Collection' }
      & Pick<Collection, 'title'>
      & {
      exercises: Array<(
        { __typename?: 'ExerciseMetaData' }
        & FieldsForLinkFragment
        )>
    }
      )>
  }
    )>
}
  );

export type AllExercisesOverviewQueryVariables = {
  toolId: Scalars['String'];
};


export type AllExercisesOverviewQuery = (
  { __typename?: 'Query' }
  & {
  tool?: Maybe<(
    { __typename?: 'Tool' }
    & {
    allExerciseMetaData: Array<(
      { __typename?: 'ExerciseMetaData' }
      & FieldsForLinkFragment
      )>
  }
    )>
}
  );

export type FieldsForLinkFragment = (
  { __typename?: 'ExerciseMetaData' }
  & Pick<ExerciseMetaData, 'id' | 'title' | 'difficulty'>
  & {
  tags: Array<(
    { __typename?: 'ExTag' }
    & Pick<ExTag, 'abbreviation' | 'title'>
    )>
}
  );

export const FieldsForLinkFragmentDoc = gql`
  fragment fieldsForLink on ExerciseMetaData {
    id
    title
    difficulty
    tags {
      abbreviation
      title
    }
  }
`;
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
      collection(id: $collId) {
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

export const CollectionOverviewDocument = gql`
  query CollectionOverview($toolId: String!, $collId: Int!) {
    tool(toolId: $toolId) {
      collection(id: $collId) {
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
