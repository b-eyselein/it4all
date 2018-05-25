import {ExerciseParameters} from "../umlInterfaces";

export {AbstractLanguageBuilder, Java, Python}

function contentIsEmpty(content: string[]): boolean {
    return content.length === 0 || (content.length === 1 && content[0].trim().length === 0);
}

abstract class AbstractLanguageBuilder {

    protected standardIndent: number;

    protected constructor(standardIndent: number) {
        this.standardIndent = standardIndent;
    }

    abstract getCore(exerciseParameters: ExerciseParameters, content: string[]): string;

    abstract getIfElse(condition: string, contentThen: string[], contentElse: string[]): string[];

    abstract getIfThen(condition: string, content: string[]): string[];

    abstract getFor(variable: string, collection: string, content: string[]): string[];

    abstract getDoWhile(condition: string, content: string[]): string[];

    abstract getWhileDo(condition: string, content: string[]): string[];

    addIdentation(stringList: string[]): string[] {
        if (Array.isArray(stringList)) {
            return stringList.map((str) => ' '.repeat(this.standardIndent) + str)
        } else {
            console.error("No array: " + typeof stringList);
            console.error(stringList);
        }
    }

}

class PythonBuilder extends AbstractLanguageBuilder {

    constructor() {
        super(4);
    }

    getCore(exerciseParameters: ExerciseParameters, content: string[]): string {
        let contentToAdd = contentIsEmpty(content) ? '  ' : this.addIdentation(content);
        return [exerciseParameters.methodDeclaration, ...contentToAdd].join('\n');
    }

    getDoWhile(condition: string, content: string[]): string[] {
        let contentToAdd = contentIsEmpty(content) ? [] : this.addIdentation(content);
        return ['while True:', ...contentToAdd, '  if ' + condition + ':', '    break'];
    }

    getFor(variable: string, collection: string, content: string[]): string[] {
        let contentToAdd = contentIsEmpty(content) ? ['  pass'] : this.addIdentation(content);
        return ['for ' + variable + ' in ' + collection + ':', ...contentToAdd];
    }

    getIfElse(condition: string, contentThen: string[], contentElse: string[]): string[] {
        let contentThenToAdd = contentIsEmpty(contentThen) ? ['  pass'] : this.addIdentation(contentThen);

        // Omit else if content is empty
        let contentElseToAdd = contentIsEmpty(contentElse) ? [] : ['else:', ...this.addIdentation(contentElse)];

        return ['if ' + condition + ':', ...contentThenToAdd, ...contentElseToAdd];
    }

    getIfThen(condition: string, content: string[]): string[] {
        let contentToAdd = contentIsEmpty(content) ? ['  pass'] : this.addIdentation(content);
        return ['if ' + condition + ':', ...contentToAdd];
    }

    getWhileDo(condition: string, content: string[]): string[] {
        let contentToAdd = contentIsEmpty(content) ? ['  pass'] : this.addIdentation(content);
        return ['while ' + condition + ':', ...contentToAdd];
    }

}

class JavaBuilder extends AbstractLanguageBuilder {

    constructor() {
        super(4);
    }

    /*
    * @param {object} exerciseParameters
    * @param {string} exerciseParameters.methodName
    * @param {string} exerciseParameters.methodParameters
    * @param {object} exerciseParameters.output
    * @param {string} exerciseParameters.output.outputType
    * @param {string} exerciseParameters.output.output
    * @param {string[]} content
    *
    * @returns {string}
    */
    get_core(exerciseParameters: ExerciseParameters, content) {
        // let contentToAdd = contentIsEmpty(content) ? '  ' : this.addIdentation(content);
        // return [exerciseParameters.methodDeclaration, ...contentToAdd].join('\n');

        let functionName = exerciseParameters.methodName;
        let methodParameters = exerciseParameters.methodParameters;
        let returnType = exerciseParameters.output.outputType;
        let returnVariable = exerciseParameters.output.output;

        let definition = 'public ' + returnType + ' ' + functionName + '(' + methodParameters + ') {';

        let contentToAdd = contentIsEmpty(content) ? ' '.repeat(this.standardIndent) : this.addIdentation(content);

        let retStatement = ' '.repeat(this.standardIndent) + 'return ' + returnVariable;

        return [definition, ...contentToAdd, retStatement, '}'].join('\n');
    }

    // noinspection JSMethodCanBeStatic
    get_eifthen(econdition, content, deep) {
        return " ".repeat(deep) + "if(" + econdition + "){\n" + content + "\n" + " ".repeat(deep) + "}\n";
    }

    /**
     * @param {string} condition
     * @param {string[]} contentThen
     * @param {string[]} contentElse
     * @returns {string[]}
     */
    get_if(condition, contentThen, contentElse) {
        let contentThenToAdd = contentIsEmpty(contentThen) ? ['  pass'] : this.addIdentation(contentThen);

        // Omit else if content is empty
        let contentElseToAdd = contentIsEmpty(contentElse) ? [] : this.addIdentation(contentElse);

        return ['if (' + condition + ') {', ...contentThenToAdd, '} else {', contentElseToAdd, '}'];
    }

    // noinspection JSMethodCanBeStatic
    get_efor(eelement, collection, content, deep) {
        return " ".repeat(deep) + "for(" + eelement + ":" + collection + "){\n" + content + "\n" + " ".repeat(deep) + "}\n";
    }

    // noinspection JSMethodCanBeStatic
    get_edw(econdition, content, deep) {
        return " ".repeat(deep) + "do{\n" + content + "\n" + " ".repeat(deep) + "}\n" + " ".repeat(deep) + "while(" + econdition + ");\n";
    }

    // noinspection JSMethodCanBeStatic
    get_ewd(econdition, content, deep) {
        return " ".repeat(deep) + "while(" + econdition + "){\n" + content + "\n" + " ".repeat(deep) + "}\n";
    }

    getCore(exerciseParameters: ExerciseParameters, content: string[]): string {
        return "";
    }

    getDoWhile(condition: string, content: string[]): string[] {
        return undefined;
    }

    getFor(variable: string, collection: string, content: string[]): string[] {
        return undefined;
    }

    getIfElse(condition: string, contentThen: string[], contentElse: string[]): string[] {
        return undefined;
    }

    getIfThen(condition: string, content: string[]): string[] {
        return undefined;
    }

    getWhileDo(condition: string, content: string[]): string[] {
        return undefined;
    }

}

const Java = new JavaBuilder();
const Python = new PythonBuilder();