import {CollectionTool, ToolPart} from '../../../_interfaces/tool';
import {IExercise} from '../../../_interfaces/models';
import {IWebExerciseContent} from './web-interfaces';


export const HtmlPart: ToolPart = {id: 'html', name: 'Html-Teil'};

const JsPart: ToolPart = {id: 'js', name: 'JS-Teil'};

export const WebTool: CollectionTool = new class WebToolClass extends CollectionTool {
  constructor() {
    super('web', 'Web', [HtmlPart, JsPart], 'live', false);
  }

  exerciseHasPart(exercise: IExercise, part: ToolPart): boolean {
    const exerciseContent = exercise.content as IWebExerciseContent;

    if (part === HtmlPart) {
      return exerciseContent.siteSpec.htmlTasks.length > 0;
    }
    if (part === JsPart) {
      return exerciseContent.siteSpec.jsTasks.length > 0;
    }

    return super.exerciseHasPart(exercise, part);
  }

  hasPlayground(): boolean {
    return true;
  }

}();
