import {DbSolution} from '../../../_interfaces/exercise';

export interface UmlClassSelectionTextPart {
  text: string;
  isSelectable: boolean;
  baseForm?: string;
}

export interface DbUmlSolution extends DbSolution<any> {

}
