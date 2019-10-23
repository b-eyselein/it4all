import {createToken, EmbeddedActionsParser, ILexingResult, IToken, Lexer, TokenType} from 'chevrotain';
import {
  BooleanAnd,
  BooleanBinaryNode,
  BooleanConstant,
  BooleanEquivalency,
  BooleanFalse,
  BooleanImplication,
  BooleanNAnd,
  BooleanNode,
  BooleanNOr,
  BooleanNot,
  BooleanOr,
  BooleanTrue,
  BooleanVariable,
  BooleanXOr
} from './bool-node';

type RuleType<T> = (idxInCallingRule?: number, ...args: any[]) => T;

type RuleRight = { operator: string, right: BooleanNode } | undefined;

// Tokens

const whiteSpace = createToken({name: 'WhiteSpace', pattern: /\s+/, group: Lexer.SKIPPED});

const variable = createToken({name: 'Variable', pattern: /[a-z]/});
const TRUE = createToken({name: 'TRUE', pattern: /1|TRUE/i});
const FALSE = createToken({name: 'FALSE', pattern: /0|FALSE/i});

const leftBracket = createToken({name: 'leftBracket', pattern: /\(/});
const rightBracket = createToken({name: 'rightBracket', pattern: /\)/});

const notOperator = createToken({name: 'notOperator', pattern: /not/});
const andOperator = createToken({name: 'AndOperator', pattern: /and/});
const orOperator = createToken({name: 'OrOperator', pattern: /or/});
const otherOperators = createToken({name: 'Operators', pattern: /[x|n]or|nand|equiv|impl/});


const allTokens: TokenType[] = [
//  andOperator, orOperator, xOrOperator, nOrOerator, nAndOperator, equivOperator, implOperator,
  leftBracket, rightBracket,
  notOperator, andOperator, orOperator, otherOperators,
  TRUE, FALSE, variable, whiteSpace
];

const BooleanFormulaLexer: Lexer = new Lexer(allTokens);


function instantiateOperator(leftOp: BooleanNode, opString: string, rightOp: BooleanNode): BooleanBinaryNode {
  switch (opString) {
    case 'and':
      return new BooleanAnd(leftOp, rightOp);
    case 'or':
      return new BooleanOr(leftOp, rightOp);
    case 'xor':
      return new BooleanXOr(leftOp, rightOp);
    case 'nor':
      return new BooleanNOr(leftOp, rightOp);
    case 'nand':
      return new BooleanNAnd(leftOp, rightOp);
    case 'equiv':
      return new BooleanEquivalency(leftOp, rightOp);
    case 'impl':
      return new BooleanImplication(leftOp, rightOp);
  }
}

export class BooleanFormulaParser extends EmbeddedActionsParser {

  constructor() {
    super(allTokens);
    this.performSelfAnalysis();
  }

  private trueRule: RuleType<BooleanConstant> = this.RULE('trueRule', () => {
    this.CONSUME(TRUE);
    return BooleanTrue;
  });

  private falseRule: RuleType<BooleanConstant> = this.RULE('falseRule', () => {
    this.CONSUME(FALSE);
    return BooleanFalse;
  });

  private variableRule: RuleType<BooleanVariable> = this.RULE('variableRule', () => {
    const variableToken: IToken = this.CONSUME(variable);
    return new BooleanVariable(variableToken.image[0]);
  });

  private factor: RuleType<BooleanNode> = this.RULE('factor', () => {
    return this.OR([
      {ALT: () => this.SUBRULE(this.variableRule)},
      {ALT: () => this.SUBRULE<BooleanNode>(this.trueRule)},
      {ALT: () => this.SUBRULE<BooleanNode>(this.falseRule)},
      {
        ALT: () => {
          this.CONSUME(leftBracket);
          const child = this.SUBRULE(this.booleanFormula);
          this.CONSUME(rightBracket);
          return child;
        }
      }
    ]);
  });

  private notFactor: RuleType<BooleanNode> = this.RULE('notFactor', () => {
    const negated: boolean = this.OPTION(() => {
      return this.CONSUME(notOperator);
    }) !== undefined;

    const child = this.SUBRULE(this.factor);

    return negated ? new BooleanNot(child) : child;
  });

  private andTermComponent: RuleType<BooleanNode> = this.RULE('otherTerm', () => {
    const left: BooleanNode = this.SUBRULE(this.notFactor);
    const right: RuleRight = this.OPTION<RuleRight>(() => {
      const operator: IToken = this.CONSUME(otherOperators);
      const factor: BooleanNode = this.SUBRULE2(this.notFactor);
      return {operator: operator.image, right: factor};
    });

    return right ? instantiateOperator(left, right.operator, right.right) : left;
  });

  private orTermComponent: RuleType<BooleanNode> = this.RULE('orTerm', () => {
    const left: BooleanNode = this.SUBRULE(this.andTermComponent);
    const right: BooleanNode | undefined = this.OPTION(() => {
      this.CONSUME(andOperator);
      return this.SUBRULE2(this.andTermComponent);
    });

    return right ? new BooleanAnd(left, right) : left;
  });

  public booleanFormula: RuleType<BooleanNode> = this.RULE('booleanFormula', () => {
    const left: BooleanNode = this.SUBRULE(this.orTermComponent);
    const right: BooleanNode | undefined = this.OPTION(() => {
      this.CONSUME(orOperator);
      return this.SUBRULE2(this.orTermComponent);
    });

    return right ? new BooleanOr(left, right) : left;
  });

}

export function parseBooleanFormula(formulaString: string): BooleanNode | undefined {
  const lexResult: ILexingResult = BooleanFormulaLexer.tokenize(formulaString);

  const parser: BooleanFormulaParser = new BooleanFormulaParser();

  parser.input = lexResult.tokens;

  return parser.booleanFormula();
}
