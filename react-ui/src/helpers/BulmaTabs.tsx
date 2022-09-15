import classNames from 'classnames';

export interface Tabs {
  [key: string]: {
    name: string;
    render: JSX.Element | (() => JSX.Element);
    disabled?: boolean;
  };
}

interface IProps {
  tabs: Tabs;
  activeTabId: keyof Tabs;
  setActiveTabId: (id: keyof Tabs) => void;
}

/**
 * @deprecated
 *
 * @param tabs
 * @param activeTabId
 * @param setActiveTabId
 * @constructor
 */
export function BulmaTabs({tabs, activeTabId, setActiveTabId}: IProps): JSX.Element {

  const activeTab = tabs[activeTabId].render;

  return (
    <div>
      <div className="tabs is-centered">
        <ul>
          {Object.entries(tabs).map(([id, {name}]) =>
            <li className={classNames({'is-active': activeTabId === id})} key={id}>
              <a onClick={() => setActiveTabId(id)}>{name}</a>
            </li>
          )}
        </ul>
      </div>
      {typeof activeTab === 'function' ? activeTab() : activeTab}
    </div>
  );
}

interface TabPillProps {
  pills: {
    id: string;
    name: string;
    disabled?: boolean;
  }[];
  activePillId: string;
  onClick: (id: string) => void;
}

export function TabPills({pills, activePillId, onClick}: TabPillProps): JSX.Element {
  return (
    <div className="flex">
      {pills.map(({id, name/*, disabled*/}) =>
        <button key={id} onClick={() => onClick(id)}
                className={classNames('p-2', 'flex-grow', 'rounded-t', 'text-center',
                  id === activePillId
                    ? ['bg-blue-500', 'text-white']
                    : ['border', 'border-slate-300'])}>
          {name}
        </button>
      )}
    </div>
  );
}

interface NewTabsProps {
  children: Tabs;
  activeTabId: keyof Tabs;
  setActiveTabId: (id: keyof Tabs) => void;
}

export function NewTabs({children, activeTabId, setActiveTabId}: NewTabsProps): JSX.Element {

  const activeTab = children[activeTabId].render;

  const pills = Object.entries(children).map(([id, {name, disabled}]) => ({id, name, disabled}));

  return (
    <div>
      <TabPills pills={pills} activePillId={activeTabId as string} onClick={setActiveTabId}/>

      {typeof activeTab === 'function' ? activeTab() : activeTab}
    </div>
  );
}
