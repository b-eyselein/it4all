class AbstractLanguageBuilder {

    funcsToTest() {
        return [this.get_core, this.get_if, this.get_efor, this.get_edw, this.get_ewd, this.get_eifthen];
    }

    constructor(standardIndent) {
        this.standardIndent = standardIndent;

        for (let func of this.funcsToTest()) {
            if (func === undefined || typeof func !== 'function') {
                throw new TypeError('Must override method ' + func)
            }
        }
    }

    /**
     * @param {string[]} stringList
     */
    addIdentation(stringList) {
        if (Array.isArray(stringList)) {
            return stringList.map((str) => ' '.repeat(this.standardIndent) + str)
        } else {
            console.error("No array: " + typeof stringList);
            console.error(stringList);
        }
    }

}

/**
 * @param {string[]} content
 */
function contentIsEmpty(content) {
    return content.length === 0 || (content.length === 1 && content[0].trim().length === 0);
}

class PythonBuilder extends AbstractLanguageBuilder {

    constructor() {
        super(2);
    }

    /**
     * @param {string} functionName
     * @param {string} methodParameters
     * @param {string} returnType
     * @param {string} returnVariable
     * @param {string[]} content
     *
     * @returns {string}
     */
    get_core(functionName, methodParameters, returnType, returnVariable, content) {
        let definition = 'def ' + functionName + '(' + methodParameters + '):';
        let contentToAdd = contentIsEmpty(content) ? '  ' : this.addIdentation(content);
        let retStatement = ' '.repeat(this.standardIndent) + 'return ' + returnVariable;

        return [definition, ...contentToAdd, retStatement].join('\n');
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
        let contentElseToAdd = contentIsEmpty(contentElse) ? [] : ['else:', ...this.addIdentation(contentElse)];

        return ['if ' + condition + ':', ...contentThenToAdd, ...contentElseToAdd];
    }

    /**
     * @param {string} variable
     * @param {string} collection
     * @param {string[]} content
     *
     * @returns {string[]}
     */
    get_efor(variable, collection, content) {
        let contentToAdd = contentIsEmpty(content) ? ['  pass'] : this.addIdentation(content);
        return ['for ' + variable + ' in ' + collection + ':', ...contentToAdd];
    }

    /**
     *
     * @param {string} condition
     * @param {string[]} content
     *
     * @returns {string[]}
     */
    get_edw(condition, content) {
        let contentToAdd = contentIsEmpty(content) ? [] : this.addIdentation(content);
        return ['while True:', ...contentToAdd, '  if ' + condition + ':', '    break'];
    }

    /**
     * @param {string} condition
     * @param {string[]} content
     *
     * @returns {string[]}
     */
    get_eifthen(condition, content) {
        let contentToAdd = contentIsEmpty(content) ? ['  pass'] : this.addIdentation(content);
        return ['if ' + condition + ':', ...contentToAdd];
    }

    /**
     * @param {string} condition
     * @param {string[]} content
     *
     * @returns {string[]}
     */
    get_ewd(condition, content) {
        let contentToAdd = contentIsEmpty(content) ? ['  pass'] : this.addIdentation(content);
        return ['while ' + condition + ':', ...contentToAdd];
    }

}


//java contents
class JavaBuilder extends AbstractLanguageBuilder {

    constructor() {
        super(4);
    }

    /**
     * @param {string} functionName
     * @param {string} methodParameters
     * @param {string} returnType
     * @param {string} returnVariable
     * @param {string[]} content
     *
     * @returns {string}
     */
    get_core(functionName, methodParameters, returnType, returnVariable, content) {
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

}

const Java = new JavaBuilder();
const Python = new PythonBuilder();