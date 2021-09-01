import {EbnfExerciseContentFragment, EbnfGrammarInput} from '../../../graphql';

export function correctEbnfExercise(
  content: EbnfExerciseContentFragment,
  solution: EbnfGrammarInput
): void {
  console.info(JSON.stringify(solution, null, 2));

}
