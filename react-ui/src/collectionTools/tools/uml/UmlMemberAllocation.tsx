import {useState} from 'react';
import {ExerciseSolveFieldsFragment, UmlAttribute, UmlClassDiagram, UmlExerciseContentFragment, UmlMethod, UmlVisibility} from '../../../graphql';
import {useTranslation} from 'react-i18next';
import classNames from 'classnames';
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
  switch (v) {
    case UmlVisibility.Package:
      return '~';
    case UmlVisibility.Protected:
      return '#';
    case UmlVisibility.Public:
      return '+';
    case UmlVisibility.Private:
      return '-';
  }
}

const printAttribute = (a: UmlAttribute): string => `${printVisibility(a.visibility)} ${a.memberName}: ${a.memberType}`;
const printMethod = (m: UmlMethod): string => `${printVisibility(m.visibility)} ${m.memberName}(${m.parameters}): ${m.memberType}`;

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

  function memberColor(m: MemberAllocMember): string | undefined {
    if (!state.corrected) {
      return undefined;
    }

    return (m.correct === m.selected) ? 'text-green-600' : 'text-red-600';
  }

  const correct = (): void => setState((state) => update(state, {corrected: {$set: true}}));

  const selectAttribute = (clazzIndex: number, index: number, targetState: boolean): void =>
    setState((state) => update(state, {data: {[clazzIndex]: {attributes: {[index]: {selected: {$set: targetState}}}}}}));

  const selectMethod = (clazzIndex: number, index: number, targetState: boolean): void =>
    setState((state) => update(state, {data: {[clazzIndex]: {methods: {[index]: {selected: {$set: targetState}}}}}}));

  return (
    <div className="px-2">
      <div className="grid grid-cols-4 gap-2">
        <div>
          <h2 className="font-bold text-xl text-center">{t('exerciseText')}</h2>

          <div className="p-2 rounded bg-gray-200">{exercise.text}</div>

          <ExerciseControlButtons isCorrecting={false} correct={correct}
                                  endLink={`/${toolsUrlFragment}/${exercise.toolId}/${collectionsUrlFragment}/${exercise.collectionId}`}/>
        </div>

        <div className="col-span-3 grid grid-cols-3 gap-2">
          {state.data.map(({name, attributes, methods}, clazzIndex) =>

            <div key={name} className="p-2 rounded border border-slate-500">
              <header className="p-2 font-bold text-center">{name}</header>

              <div className="p-2 border-t border-slate-500">
                {attributes.map((a, index) => <p key={index}>
                  <label className={classNames('checkbox', memberColor(a))}>
                    <input type="checkbox" onChange={(event) => selectAttribute(clazzIndex, index, event.target.checked)}/> {a.display}
                  </label>
                </p>)}
              </div>

              <div className="p-2 border-t border-slate-500">
                {methods.map((m, index) => <p key={index}>
                  <label className={classNames('checkbox', memberColor(m))}>
                    <input type="checkbox" onChange={(event) => selectMethod(clazzIndex, index, event.target.checked)}/> {m.display}
                  </label>
                </p>)}
              </div>

            </div>
          )}
        </div>
      </div>
    </div>
  );
}
