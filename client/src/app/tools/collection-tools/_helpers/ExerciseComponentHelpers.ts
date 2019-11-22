import {ActivatedRoute} from '@angular/router';
import {CollectionTool} from '../../../_interfaces/tool';
import {collectionTools} from '../collection-tools-list';
import {ExerciseContent} from '../../../_interfaces/exercise';

export abstract class ExerciseComponentHelpers<EC extends ExerciseContent> {

  readonly tool: CollectionTool<EC>;

  protected constructor(route: ActivatedRoute) {
    const toolId = route.snapshot.paramMap.get('toolId');

    this.tool = collectionTools.find((t) => t.id === toolId);
  }

}
