import { useState } from 'react';
import {ExerciseSolveFieldsFragment, UmlAttribute, UmlClassDiagram, UmlExerciseContentFragment, UmlMethod, UmlVisibility} from '../../../graphql';
import {useTranslation} from 'react-i18next';
import classNames from 'classnames';
import {checkNever} from '../../../helpers';
import {ExerciseControlButtons} from '../../../helpers/ExerciseControlButtons';
import {collectionsUrlFragment, toolsUrlFragment} from '../../../urls';
import update from 'immutability-helper';

interface IProps {
  exercise: ExerciseSolveFieldsFragment;
  content: UmlExerciseContentFragment;
}

export function distinctObjectArray<T, K>(ts: T[], key: (t: T) => K): T[] {
  const helperMap: Map<K, T> = new Map<K, T>();

  for (const t of ts) {
    const tKey = key(t);
    if (!helperMap.has(tKey)) {
      helperMap.set(tKey, t);
    }
  }

  return Array.from(helperMap.values());
}

function printVisibility(v: UmlVisibility): string {
  if (v === UmlVisibility.Public) {
    return '+';
  } else if (v === UmlVisibility.Package) {
    return '~';
  } else if (v === UmlVisibility.Protected) {
    return '#';
  } else if (v === UmlVisibility.Private) {
    return '-';
  } else {
    return checkNever(v, '');
  }
}

function printAttribute(a: UmlAttribute): string {
  return `${printVisibility(a.visibility)} ${a.memberName}: ${a.memberType}`;
}

function printMethod(m: UmlMethod): string {
  return `${printVisibility(m.visibility)} ${m.memberName}(${m.parameters}): ${m.memberType}`;
}

interface MemberAllocMember {
  display: string;
  correct: boolean;
  selected: boolean;
}

interface MemberAllocClass {
  name: string;
  attributes: MemberAllocMember[];
  methods: MemberAllocMember[];
}

interface IState {
  corrected: boolean;
  data: MemberAllocClass [];
}

export function UmlMemberAllocation({exercise, content}: IProps): JSX.Element {


  const sample: UmlClassDiagram = content.umlSampleSolutions[0];

  const allAttributes: UmlAttribute[] = distinctObjectArray(sample.classes.flatMap((clazz) => clazz.attributes), (a) => a.memberName);

  const allMethods: UmlMethod[] = distinctObjectArray(sample.classes.flatMap((clazz) => clazz.methods), (m) => m.memberName);

  const data: MemberAllocClass[] = sample.classes.map(({name, attributes, methods}) => ({
      name,
      attributes: allAttributes.map((attr) => ({display: printAttribute(attr), correct: attributes.includes(attr), selected: false})),
      methods: allMethods.map((met) => ({display: printMethod(met), correct: methods.includes(met), selected: false}))
    })
  );

  const {t} = useTranslation('common');
  const [state, setState] = useState<IState>({corrected: false, data});

  function memberColor(m: MemberAllocMember): string {
    if (state.corrected) {
      return (m.correct === m.selected) ? 'has-text-dark-success' : 'has-text-danger';
    } else {
      return '';
    }
  }

  function correct(): void {
    setState((state) => ({...state, corrected: true}));
  }

  function selectAttribute(clazzIndex: number, index: number, targetState: boolean): void {
    setState((state) => update(state, {data: {[clazzIndex]: {attributes: {[index]: {selected: {$set: targetState}}}}}}));
  }

  function selectMethod(clazzIndex: number, index: number, targetState: boolean): void {
    setState((state) => update(state, {data: {[clazzIndex]: {methods: {[index]: {selected: {$set: targetState}}}}}}));
  }

  return (
    <div className="container is-fluid">
      <div className="columns">
        <div className="column is-one-quarter-desktop">
          <h2 className="subtitle is-4 has-text-centered">{t('exerciseText')}</h2>

          <div className="notification is-light-grey">{exercise.text}</div>

          <ExerciseControlButtons isCorrecting={false} correct={correct}
                                  endLink={`/${toolsUrlFragment}/${exercise.toolId}/${collectionsUrlFragment}/${exercise.collectionId}`}/>
        </div>

        <div className="column">

          <div className="columns is-multiline">
            {state.data.map(({name, attributes, methods}, clazzIndex) => <div className="column is-one-third-desktop" key={name}>
                <div className="card">
                  <header className="card-header">
                    <p className="card-header-title">{name}</p>
                  </header>

                  <div className="card-content">
                    {attributes.map((a, index) => <p key={index}>
                      <label className={classNames('checkbox', memberColor(a))}>
                        <input type="checkbox" onChange={(event) => selectAttribute(clazzIndex, index, event.target.checked)}/> {a.display}
                      </label>
                    </p>)}

                    <hr/>

                    {methods.map((m, index) => <p key={index}>
                      <label className={classNames('checkbox', memberColor(m))}>
                        <input type="checkbox" onChange={(event) => selectMethod(clazzIndex, index, event.target.checked)}/> {m.display}
                      </label>
                    </p>)}
                  </div>
                </div>
              </div>
            )}
          </div>

        </div>
      </div>
    </div>
  );
}
