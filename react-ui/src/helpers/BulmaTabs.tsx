import classNames from 'classnames';

export interface Tabs {
  [key: string]: {
    name: string;
    render: JSX.Element | (() => JSX.Element);
  };
}

interface IProps {
  tabs: Tabs;
  activeTabId: keyof Tabs;
  setActiveTabId: (id: keyof Tabs) => void;
}

export function BulmaTabs({tabs, activeTabId, setActiveTabId}: IProps): JSX.Element {

  const activeTab = tabs[activeTabId].render;

  return (
    <>
      <div className="tabs is-centered">
        <ul>
          {Object.entries(tabs).map(([id, {name}]) =>
            <li className={classNames({'is-active': activeTabId === id})} key={id}>
              <a onClick={() => setActiveTabId(id)}>{name}</a>
            </li>
          )}
        </ul>
      </div>
      {typeof activeTab === 'function' ? activeTab : activeTab}
    </>
  );
}
