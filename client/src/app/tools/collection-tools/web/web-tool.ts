import {CollectionTool, ExerciseTag, ToolPart} from '../../../_interfaces/tool';
import {WebExerciseContent} from './web-interfaces';


const HtmlPart: ToolPart = {id: 'html', name: 'Html-Teil'};

const JsPart: ToolPart = {id: 'js', name: 'JS-Teil'};

export const WebTool: CollectionTool<WebExerciseContent> = new (
  class WebToolClass extends CollectionTool<WebExerciseContent> {
    constructor() {
      super('web', 'Web', [HtmlPart, JsPart], 'live', true, true);
    }

    processTagString(tag: string): ExerciseTag {
      return undefined;
    }
  }
)();
