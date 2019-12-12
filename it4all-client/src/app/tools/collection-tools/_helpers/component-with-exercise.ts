import {TabsComponent} from '../../../shared/tabs/tabs.component';
import {QueryList} from '@angular/core';
import {TabComponent} from '../../../shared/tab/tab.component';

export class ComponentWithExercise<ResultType> {

  isCorrecting = false;

  result: ResultType | undefined;

  displaySampleSolutions = false;

  readonly correctionTabTitle = 'Korrektur';
  readonly sampleSolutionsTabTitle = 'Musterlösungen';

  protected activateCorrectionTab(tabsComponent: TabsComponent, tabComponents: QueryList<TabComponent>) {
    const correctionTab = tabComponents.toArray().find((v) => v.title === this.correctionTabTitle);
    if (correctionTab) {
      tabsComponent.selectTab(correctionTab);
    }
  }

}
