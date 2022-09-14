import classNames, {Argument as ClassNamesArgument} from 'classnames';

interface IProps {
  children: string | JSX.Element;
  title?: string;
  otherClasses?: ClassNamesArgument;
}

export function Tag({children, title, otherClasses}: IProps): JSX.Element {
  return <span title={title} className={classNames('m-2', 'p-2', 'rounded', 'bg-slate-100', 'text-center', 'text-xs', otherClasses)}>{children}</span>;
}
