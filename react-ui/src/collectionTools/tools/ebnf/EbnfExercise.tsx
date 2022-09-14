import {useState} from 'react';
import {ConcreteExerciseWithoutPartsProps} from '../../Exercise';
import {EbnfExerciseContentFragment, EbnfGrammarInput, EbnfRuleInput} from '../../../graphql';
import {useTranslation} from 'react-i18next';
import {BulmaTabs, Tabs} from '../../../helpers/BulmaTabs';
import {EbnfCorrection} from './EbnfCorrection';
import {SampleSolutionTabContent} from '../../SampleSolutionTabContent';
import {ExerciseControlButtons} from '../../../helpers/ExerciseControlButtons';
import {parseEbnfGrammarRight} from './ebnfParser';
import {Result, Success} from 'parsimmon';
import {ExtendedBackusNaurFormGrammarElement, getVariablesFromGrammarElement} from './ebnfElements';
import classNames from 'classnames';
import {correctEbnfExercise} from './ebnfCorrector';

type IProps = ConcreteExerciseWithoutPartsProps<EbnfExerciseContentFragment, string>;

interface FormEbnfRule extends EbnfRuleInput {
  parsed?: Result<ExtendedBackusNaurFormGrammarElement>;
}

interface FormEbnfGrammar extends EbnfGrammarInput {
  rules: FormEbnfRule[];
}

interface IState {
  solution: FormEbnfGrammar;
  terminals: string[];
  variables: string[];
}

const minimalGrammar: EbnfGrammarInput = {
  startSymbol: 'S',
  rules: [
    {variable: 'S', rule: ''}
  ]
};

export function EbnfExercise({exercise, content, /*oldSolution*/}: IProps): JSX.Element {

  const {t} = useTranslation('common');

  const [isCorrecting, setIsCorrecting] = useState(false);

  const [state, setState] = useState<IState>({solution: minimalGrammar, variables: [minimalGrammar.startSymbol], terminals: content.predefinedTerminals || []});

  const tabs: Tabs = {
    correction: {name: t('correction'), render: <EbnfCorrection/>},
    sampleSolution: {
      name: t('sampleSolution_plural'),
      render: (
        <SampleSolutionTabContent>
          {() => content.sampleSolutions.map((s, i) => <div key={i}>TODO!</div>)}
        </SampleSolutionTabContent>
      )
    }
  };

  const [activeTabId, setActiveTabId] = useState<keyof Tabs>(Object.keys(tabs)[0]);

  function correct(): void {

    const {startSymbol, rules} = state.solution;

    const solution: EbnfGrammarInput = {
      startSymbol,
      rules: rules.map(({variable, rule}) => {
        return {variable, rule};
      })
    };

    console.info(JSON.stringify(solution, null, 2));

    // FIXME: correct offline?

    setIsCorrecting(true);

    correctEbnfExercise(content, solution);

    setIsCorrecting(false);

    /*
    correctExercise({variables: {collId: exercise.collectionId, exId: exercise.exerciseId, solution}})
      .catch((error) => console.error(error));
     */
  }

  function updateStartState(newStartState: string): void {
    setState(({solution, ...rest}) => ({solution: {...solution, startSymbol: newStartState}, ...rest}));
  }

  function updateRule(ruleIndex: number, newRule: string): void {
    setState(({solution: {startSymbol, rules}, terminals}) => {

      const newRules = rules.map((rule, index) =>
        index === ruleIndex
          ? {variable: rule.variable, rule: newRule, parsed: parseEbnfGrammarRight(newRule)}
          : rule
      );

      const variables = Array.from(
        new Set(
          newRules
            .map(({parsed}) => parsed)
            .filter((result): result is Success<ExtendedBackusNaurFormGrammarElement> => !!result && result.status)
            .flatMap(({value}) => getVariablesFromGrammarElement(value))
            .map(({value}) => value)
        )
      );

      // FIXME: add new rules for new variables!

      const variablesWithRules = newRules.map(({variable}) => variable);

      const variablesWithoutRules = variables
        .filter((variable) => !variablesWithRules.includes(variable));

      if (variablesWithoutRules.length > 0) {
        newRules.push(...variablesWithoutRules.map((variable) => ({variable, rule: ''})));
      }

      return {
        solution: {startSymbol, rules: newRules},
        variables,
        terminals
      };
    });
  }

  return (
    <div className="container">

      <h1 className="title is-3 has-text-centered">{t('exercise')} &quot;{exercise.title}&quot;</h1>

      <div className="notification is-light-grey">{exercise.text}</div>

      <div className="field has-addons">
        <div className="control">
          <button className="button is-static">T = &#123;</button>
        </div>
        <div className="control is-expanded">
          <input type="text" className="input" value={state.terminals.map((t) => `'${t}'`).join(', ')} disabled={!!content.predefinedTerminals}/>
        </div>
        <div className="control">
          <button className="button is-static">&#125;</button>
        </div>
      </div>

      <div className="field has-addons">
        <div className="control">
          <button className="button is-static">V = &#123;</button>
        </div>
        <div className="control is-expanded">
          <input type="text" className="input" readOnly value={state.variables.join(', ')}/>
        </div>
        <div className="control">
          <button className="button is-static">&#125;</button>
        </div>
      </div>

      <div className="field has-addons">
        <div className="control">
          <button className="button is-static">S =</button>
        </div>
        <div className="control is-expanded">
          <div className="select is-fullwidth">
            <select onChange={(event) => updateStartState(event.target.value)} defaultValue={state.solution.startSymbol}>
              {state.variables.map((variable) => <option key={variable}>{variable}</option>)}
            </select>
          </div>
        </div>
      </div>

      <div className="field">
        <button className="button is-static">R = &#123;</button>
      </div>

      <div className="field ml-2">
        {state.solution.rules.map(({variable, rule, parsed}, index) =>
          <div className="field has-addons" key={variable}>
            <div className="control">
              <button className="button is-static">{variable} =</button>
            </div>
            <div className="control is-expanded">
              <input type="text"
                     className={classNames('input', {'is-danger': parsed && !parsed.status, 'is-success': parsed && parsed.status})}
                     defaultValue={rule} onChange={(event) => updateRule(index, event.target.value)}/>
            </div>
          </div>
        )}
      </div>

      <div className="field">
        <button className="button is-static">&#125;</button>
      </div>


      <ExerciseControlButtons isCorrecting={isCorrecting} correct={correct} endLink={`./../../${exercise.exerciseId}`}/>

      <BulmaTabs tabs={tabs} activeTabId={activeTabId} setActiveTabId={setActiveTabId}/>

    </div>
  );
}
