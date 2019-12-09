import {CollectionTool, ExerciseTag, ToolPart} from '../../../_interfaces/tool';
import {IExercise, IWebExerciseContent} from '../../../_interfaces/models';


const HtmlPart: ToolPart = {id: 'html', name: 'Html-Teil'};

const JsPart: ToolPart = {id: 'js', name: 'JS-Teil'};

export const WebTool: CollectionTool = new class WebToolClass extends CollectionTool {
  constructor() {
    super('web', 'Web', [HtmlPart, JsPart], 'live', true);
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

  processTagString(tag: string): ExerciseTag {
    return undefined;
  }
}();
