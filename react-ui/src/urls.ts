import {BreadCrumb} from './helpers/BreadCrumbs';
import {TFunction} from 'i18next';

export const serverUrl = process.env.NODE_ENV === 'development'
  ? 'http://localhost:9000'
  : '';

export const homeUrl = '/';

export const loginUrl = '/loginForm';

export const registerUrl = '/registerForm';

export function toolUrl(toolId: string): string {
  return `/${toolsUrlFragment}/${toolId}`;
}

export function toolCollectionsUrl(toolId: string): string {
  return `${toolUrl(toolId)}/${collectionsUrlFragment}`;
}

export function collectionUrl(toolId: string, collectionId: number): string {
  return `${toolCollectionsUrl(toolId)}/${collectionId}`;
}

// fragments

export const randomToolsUrlFragment = 'randomTools';


export const toolsUrlFragment = 'tools';

export const collectionsUrlFragment = 'collections';

export const exercisesUrlFragment = 'exercises';

export const allExercisesUrlFragment = 'allExercises';

export const partsUrlFragment = 'parts';

// Breadcrumbs

export function toolsBreadCrumbs(t: TFunction): BreadCrumb {
  return {title: t('tool_plural'), to: homeUrl};
}

export function toolBreadCrumbs(toolId: string, toolName: string, t: TFunction): BreadCrumb[] {
  return [
    toolsBreadCrumbs(t),
    {title: toolName, to: toolUrl(toolId)}
  ];
}

export function collectionsBreadCrumbs(toolId: string, toolName: string, t: TFunction): BreadCrumb[] {
  return [
    ...toolBreadCrumbs(toolId, toolName, t),
    {title: t('collection_plural'), to: toolCollectionsUrl(toolId)}
  ];
}

export function collectionBreadCrumbs(toolId: string, toolName: string, collectionId: number, collectionTitle: string, t: TFunction): BreadCrumb[] {
  return [
    ...collectionsBreadCrumbs(toolId, toolName, t),
    {title: collectionTitle, to: collectionUrl(toolId, collectionId)}
  ];
}

export function exercisesBreadCrumbs(toolId: string, toolName: string, collectionId: number, collectionTitle: string, t: TFunction): BreadCrumb[] {
  return [
    ...collectionBreadCrumbs(toolId, toolName, collectionId, collectionTitle, t),
    {title: t('exercise_plural'), to: collectionUrl(toolId, collectionId)}
  ];
}
