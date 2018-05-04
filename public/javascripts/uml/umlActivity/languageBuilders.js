function contentIsEmpty(content) {
    return content.length === 0 || (content.length === 1 && content[0].trim().length === 0);
}
class AbstractLanguageBuilder {
    constructor(standardIndent) {
        this.standardIndent = standardIndent;
    }
    addIdentation(stringList) {
        if (Array.isArray(stringList)) {
            return stringList.map((str) => ' '.repeat(this.standardIndent) + str);
        }
        else {
            console.error("No array: " + typeof stringList);
            console.error(stringList);
        }
    }
}
class PythonBuilder extends AbstractLanguageBuilder {
    constructor() {
        super(4);
    }
    getCore(exerciseParameters, content) {
        let contentToAdd = contentIsEmpty(content) ? '  ' : this.addIdentation(content);
        return [exerciseParameters.methodDeclaration, ...contentToAdd].join('\n');
    }
    getDoWhile(condition, content) {
        let contentToAdd = contentIsEmpty(content) ? [] : this.addIdentation(content);
        return ['while True:', ...contentToAdd, '  if ' + condition + ':', '    break'];
    }
    getFor(variable, collection, content) {
        let contentToAdd = contentIsEmpty(content) ? ['  pass'] : this.addIdentation(content);
        return ['for ' + variable + ' in ' + collection + ':', ...contentToAdd];
    }
    getIfElse(condition, contentThen, contentElse) {
        let contentThenToAdd = contentIsEmpty(contentThen) ? ['  pass'] : this.addIdentation(contentThen);
        let contentElseToAdd = contentIsEmpty(contentElse) ? [] : ['else:', ...this.addIdentation(contentElse)];
        return ['if ' + condition + ':', ...contentThenToAdd, ...contentElseToAdd];
    }
    getIfThen(condition, content) {
        let contentToAdd = contentIsEmpty(content) ? ['  pass'] : this.addIdentation(content);
        return ['if ' + condition + ':', ...contentToAdd];
    }
    getWhileDo(condition, content) {
        let contentToAdd = contentIsEmpty(content) ? ['  pass'] : this.addIdentation(content);
        return ['while ' + condition + ':', ...contentToAdd];
    }
}
class JavaBuilder extends AbstractLanguageBuilder {
    constructor() {
        super(4);
    }
    get_core(exerciseParameters, content) {
        let functionName = exerciseParameters.methodName;
        let methodParameters = exerciseParameters.methodParameters;
        let returnType = exerciseParameters.output.outputType;
        let returnVariable = exerciseParameters.output.output;
        let definition = 'public ' + returnType + ' ' + functionName + '(' + methodParameters + ') {';
        let contentToAdd = contentIsEmpty(content) ? ' '.repeat(this.standardIndent) : this.addIdentation(content);
        let retStatement = ' '.repeat(this.standardIndent) + 'return ' + returnVariable;
        return [definition, ...contentToAdd, retStatement, '}'].join('\n');
    }
    get_eifthen(econdition, content, deep) {
        return " ".repeat(deep) + "if(" + econdition + "){\n" + content + "\n" + " ".repeat(deep) + "}\n";
    }
    get_if(condition, contentThen, contentElse) {
        let contentThenToAdd = contentIsEmpty(contentThen) ? ['  pass'] : this.addIdentation(contentThen);
        let contentElseToAdd = contentIsEmpty(contentElse) ? [] : this.addIdentation(contentElse);
        return ['if (' + condition + ') {', ...contentThenToAdd, '} else {', contentElseToAdd, '}'];
    }
    get_efor(eelement, collection, content, deep) {
        return " ".repeat(deep) + "for(" + eelement + ":" + collection + "){\n" + content + "\n" + " ".repeat(deep) + "}\n";
    }
    get_edw(econdition, content, deep) {
        return " ".repeat(deep) + "do{\n" + content + "\n" + " ".repeat(deep) + "}\n" + " ".repeat(deep) + "while(" + econdition + ");\n";
    }
    get_ewd(econdition, content, deep) {
        return " ".repeat(deep) + "while(" + econdition + "){\n" + content + "\n" + " ".repeat(deep) + "}\n";
    }
    getCore(exerciseParameters, content) {
        return "";
    }
    getDoWhile(condition, content) {
        return undefined;
    }
    getFor(variable, collection, content) {
        return undefined;
    }
    getIfElse(condition, contentThen, contentElse) {
        return undefined;
    }
    getIfThen(condition, content) {
        return undefined;
    }
    getWhileDo(condition, content) {
        return undefined;
    }
}
const Java = new JavaBuilder();
const Python = new PythonBuilder();
//# sourceMappingURL=languageBuilders.js.map