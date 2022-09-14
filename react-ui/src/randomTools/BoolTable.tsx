import {BooleanNode, stringifyNode} from './boolModel/boolNode';
import classNames, {Argument as ClassNameArgument} from 'classnames';
import {BooleanNodeDisplay} from './BoolFormulaDisplay';
import {Assignment, displayAssignmentValue} from './boolModel/assignment';

const cellClasses = ['p-2 text-center border border-slate-200'];

const cellClassName = classNames(cellClasses);

export interface BoolTableColumn {
  node: BooleanNode;
  trClasses?: (node: BooleanNode, assignment: Assignment) => ClassNameArgument;
  children?: (node: BooleanNode, assignment: Assignment, assignmentIndex: number) => JSX.Element;
}

interface AssignmentRowProps {
  columns: BoolTableColumn[];
  assignment: Assignment;
  assignmentIndex: number;
}

function AssignmentRow({columns, assignment, assignmentIndex}: AssignmentRowProps): JSX.Element {
  return (
    <tr>
      {columns.map(({node, trClasses, children}) =>
        <td key={stringifyNode(node)} className={classNames(trClasses ? trClasses(node, assignment) : [], cellClasses)}>
          {children
            ? children(node, assignment, assignmentIndex)
            : displayAssignmentValue(assignment, node)}
        </td>
      )}
    </tr>
  );
}


interface IProps {
  columns: BoolTableColumn[];
  assignments: Assignment[];
}

export function BoolTable({columns, assignments}: IProps): JSX.Element {
  return (
    <table className="w-full">
      <thead>
        <tr>
          {columns.map(({node}) =>
            <th key={stringifyNode(node)} className={cellClassName}>
              <BooleanNodeDisplay node={node}/>
            </th>
          )}
        </tr>
      </thead>
      <tbody>
        {assignments.map((assignment, index) => <AssignmentRow key={index} columns={columns} assignment={assignment} assignmentIndex={index}/>)}
      </tbody>
    </table>
  );
}
