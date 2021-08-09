import React from 'react';
import {ExerciseSolveFieldsFragment, UmlAttribute, UmlClassDiagram, UmlExerciseContentFragment, UmlMethod, UmlVisibility} from '../../../graphql';
import {useTranslation} from 'react-i18next';
import {Link} from 'react-router-dom';
import classNames from 'classnames';

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
  if (v === 'PUBLIC') {
    return '+';
  } else if (v === 'PACKAGE') {
    return '~';
  } else if (v === 'PROTECTED') {
    return '#';
  } else if (v === 'PRIVATE') {
    return '-';
  } else {
    return '';
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


export function UmlMemberAllocation({exercise, content}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  const corrected = false;


  const sample: UmlClassDiagram = content.umlSampleSolutions[0];

  const allAttributes: UmlAttribute[] = distinctObjectArray(
    sample.classes.flatMap((clazz) => clazz.attributes), (a) => a.memberName
  );

  const allMethods: UmlMethod[] = distinctObjectArray(
    sample.classes.flatMap((clazz) => clazz.methods), (m) => m.memberName
  );


  const data: MemberAllocClass[] = sample.classes.map((clazz) => {
    return {
      name: clazz.name,
      attributes: allAttributes.map((attr) => {
        return {
          display: printAttribute(attr),
          correct: clazz.attributes.includes(attr),
          selected: false
        };
      }),
      methods: allMethods.map((met) => {
        return {
          display: printMethod(met),
          correct: clazz.methods.includes(met),
          selected: false
        };
      })
    };
  });


  function memberColor(m: MemberAllocMember): string {
    if (corrected) {
      return (m.correct === m.selected) ? 'has-text-dark-success' : 'has-text-danger';
    } else {
      return '';
    }
  }

  function correct(): void {
    console.error('TODO: correct!');
  }


  return (

    <div className="container is-fluid">
      <div className="columns">
        <div className="column is-one-quarter-desktop">
          <h2 className="subtitle is-4 has-text-centered">{t('exerciseText')}</h2>

          <div className="notification is-light-grey">
            {exercise.text}
          </div>

          <div className="columns">
            <div className="column">
              <button className="button is-link is-fullwidth" onClick={correct} disabled={corrected}>Korrektur</button>
            </div>
            <div className="column">
              <Link className="button is-dark is-fullwidth" to={'/'}>Bearbeiten beenden</Link>
            </div>
          </div>

        </div>
        <div className="column">

          <div className="columns is-multiline">
            {data.map((clazz) =>
              <div className="column is-one-third-desktop" key={clazz.name}>

                <div className="card">
                  <header className="card-header">
                    <p className="card-header-title">{clazz.name}</p>
                  </header>

                  <div className="card-content">
                    {clazz.attributes.map((a, index) => <p key={index}>
                      <label className={classNames('checkbox', memberColor(a))}><input type="checkbox"/> {a.display}</label>
                    </p>)}

                    <hr/>

                    {clazz.methods.map((m, index) => <p key={index}>
                      <label className={classNames('checkbox', memberColor(m))}><input type="checkbox"/> {m.display}</label>
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
