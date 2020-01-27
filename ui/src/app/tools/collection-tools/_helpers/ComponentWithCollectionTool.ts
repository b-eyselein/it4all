import {ActivatedRoute} from '@angular/router';
import {CollectionTool} from '../../../_interfaces/tool';
import {collectionTools} from '../collection-tools-list';

export abstract class ComponentWithCollectionTool {

  readonly tool: CollectionTool;

  protected constructor(route: ActivatedRoute) {
    const toolId = route.snapshot.paramMap.get('toolId');

    this.tool = collectionTools.find((t) => t.id === toolId);
  }

}
