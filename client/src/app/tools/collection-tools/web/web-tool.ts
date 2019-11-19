import {ExerciseTag, Tool, ToolPart} from '../../../_interfaces/tool';


const HtmlPart: ToolPart = {id: 'html', name: 'Html-Teil'};

const JsPart: ToolPart = {id: 'js', name: 'JS-Teil'};

export const WebTool: Tool = new (
  class WebToolClass extends Tool {
    constructor() {
      super('web', 'Web', [HtmlPart, JsPart], 'live', true, true);
    }

    processTagString(tag: string): ExerciseTag {
      return undefined;
    }
  }
)();
