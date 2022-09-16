import classNames from 'classnames';

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
  children: {
    [key: string]: {
      name: string;
      render: JSX.Element | (() => JSX.Element);
      disabled?: boolean;
    };
  };
  activeTabId: string;
  setActiveTabId: (id: string) => void;
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
