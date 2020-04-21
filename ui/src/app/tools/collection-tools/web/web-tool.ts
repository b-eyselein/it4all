import {CollectionTool, ToolPart} from '../../../_interfaces/tool';


export const HtmlPart: ToolPart = {id: 'html', name: 'Html-Teil'};

export const JsPart: ToolPart = {id: 'js', name: 'JS-Teil'};

export const WebTool: CollectionTool = new class WebToolClass extends CollectionTool {
  constructor() {
    super('web', 'Web', [HtmlPart, JsPart], 'live', false);
  }

  exerciseHasPart(exerciseContent: any, part: ToolPart): boolean {
    if (part === HtmlPart) {
      return exerciseContent.siteSpec.htmlTaskCount > 0;
    }
    if (part === JsPart) {
      return exerciseContent.siteSpec.jsTaskCount > 0;
    }

    return super.exerciseHasPart(exerciseContent, part);
  }

  hasPlayground(): boolean {
    return true;
  }

}();
