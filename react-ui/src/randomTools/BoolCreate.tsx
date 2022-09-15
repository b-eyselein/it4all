import {ChangeEvent, useState} from 'react';
import {useTranslation} from 'react-i18next';
import {BooleanNode, evaluate, getVariables, stringifyNode} from './boolModel/boolNode';
import {Assignment, calculateAssignments, displayAssignmentValue, isCorrect, learnerVariable, sampleVariable} from './boolModel/assignment';
import classNames from 'classnames';
import {RandomSolveButtons} from './RandomSolveButtons';
import {BoolCreateInstructions} from './BoolCreateInstructions';
import {parseBooleanFormulaFromLanguage} from './boolModel/boolFormulaParser';
import {Result} from 'parsimmon';
import update from 'immutability-helper';
import {generateBooleanFormula} from './boolModel/booleanFormulaGenerator';
import {BoolTable, BoolTableColumn} from './BoolTable';
import {bgColors} from '../consts';
import {NewCard} from '../helpers/BulmaCard';

interface IState {
  formula: BooleanNode;
  assignments: Assignment[];
  corrected: boolean;
  showSampleSolutions: boolean;
  solution: string;
  userSolutionFormula?: BooleanNode;
}

function initState(): IState {
  const formula = generateBooleanFormula();

  const variables = getVariables(formula);

  const assignments = calculateAssignments(variables);

  assignments.forEach((assignment) => {
    assignment[sampleVariable.variable] = evaluate(formula, assignment);
  });

  return {formula, assignments, corrected: false, showSampleSolutions: false, solution: ''};
}

export function BoolCreate(): JSX.Element {

  const {t} = useTranslation('common');
  const [state, setState] = useState(initState());
  const [showInstructions, setShowInstructions] = useState(false);

  function correct(): void {
    const parsed: Result<BooleanNode> = parseBooleanFormulaFromLanguage(state.solution);

    if (parsed.status) {
      const userSolutionFormula = parsed.value;

      setState(({assignments, ...rest}) => {
        assignments.forEach((a) => a[learnerVariable.variable] = evaluate(userSolutionFormula, a));

        return {...rest, userSolutionFormula, assignments, corrected: true};
      });
    } else {
      setState((state) => update(state, {userSolutionFormula: {$set: undefined}, corrected: {$set: true}}));
    }
  }

  const completelyCorrect = state.corrected && state.assignments.every(isCorrect);
  const variables = getVariables(state.formula);

  const columns: BoolTableColumn[] = [
    ...variables.map((node) => ({node})),
    {node: sampleVariable},
    {
      node: learnerVariable,
      trClasses: (node, assignment) => ({
        [bgColors.inCorrect]: state.corrected && !isCorrect(assignment),
        [bgColors.correct]: state.corrected && isCorrect(assignment),
        'text-white': state.corrected
      }),
      children: (node, assignment) => {
        return (
          <>
            {displayAssignmentValue(assignment, node)}
            &nbsp;
            {state.corrected && (isCorrect(assignment) ? <span>&#10004;</span> : <span>&#10008;</span>)}
          </>);
      }
    }
  ];

  function updateSolution(event: ChangeEvent<HTMLInputElement>): void {
    setState((state) => update(state, {solution: {$set: event.target.value}}));
  }

  return (
    <div className="container mx-auto">
      <h1 className="mb-4 font-bold text-2xl text-center">{t('createBooleanFormula')}</h1>

      <BoolTable columns={columns} assignments={state.assignments}/>

      <hr/>

      <div className="my-4 flex">
        <label htmlFor="solution" className="p-2 font-bold">{sampleVariable.variable} = </label>
        <input type="text" value={state.solution} onChange={updateSolution} id="solution" autoComplete="off" placeholder={t('booleanFormula')} autoFocus
               className="p-2 flex-grow rounded border border-slate-500"/>
      </div>

      {state.corrected && <div className={classNames('my-4', 'p-2', 'rounded', 'text-center', 'text-white', completelyCorrect ? 'bg-green-500' : 'bg-red-500')}>
        {completelyCorrect
          ? <>&#10004; {t('solutionCorrect')}.</>
          : <>&#10008; {t('solutionNotCorrect')}.</>}
      </div>}

      <RandomSolveButtons toolId={'bool'} correct={correct} nextExercise={() => setState(initState())}/>

      <div className="my-4 grid grid-cols-2 gap-2">
        <button className="p-2 rounded bg-blue-500 text-white w-full" onClick={() => setState((state) => update(state, {$toggle: ['showSampleSolutions']}))}>
          {t('showSampleSolution')}
        </button>
        <button className="p-2 rounded bg-cyan-400 text-white w-full" onClick={() => setShowInstructions((showInstructions) => !showInstructions)}>
          {t('showHelp')}
        </button>
      </div>

      {state.showSampleSolutions && <NewCard title={t('sampleSolution_plural')}>
        <code>{sampleVariable.variable} = {stringifyNode(state.formula)}</code>
      </NewCard>}

      {showInstructions && <BoolCreateInstructions/>}

    </div>
  );
}
