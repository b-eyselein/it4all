class AbstractLanguageBuilder {

    funcsToTest() {
        return [this.get_core, this.get_if, this.get_loop, this.get_efor, this.get_edw, this.get_ewd, this.get_manualIf, this.get_manualLoop];
    }

    constructor() {
        for (let func of this.funcsToTest()) {
            if (func === undefined || typeof func !== 'function') {
                throw new TypeError('Must override method ' + func)
            }
        }
    }

}

//python contents
class PythonBuilder extends AbstractLanguageBuilder {

    constructor(deep) {
        super();
        this.deep = parseInt(deep);// deep is the amount of spaces before the text starts
    }

    // noinspection JSMethodCanBeStatic
    get_core(startnode_inputtype, startnode_input, endnode_outputtype, endnode_output, methodname, content) {
        if ($('#editDiagramModal').hasClass('in')) {
            return content;
        } else {
            return `def ${methodname}(${startnode_input}):` + '\n' + content + '\n' + ' '.repeat(this.deep) + `return ${endnode_output}`;
        }
    }

    // noinspection JSMethodCanBeStatic
    get_if(econdition, ethen, eelse, deep) {
        return ' '.repeat(deep) + `if ${econdition}:` + '\n' + ethen + '\n' + ' '.repeat(deep) + 'else:\n' + eelse + '\n';
    }

    // noinspection JSMethodCanBeStatic
    get_loop(econdition, path, content, deep) {
        return path + '\n' + ' '.repeat(deep) + 'while' + econdition + ':\n' + content;
    }

    // noinspection JSMethodCanBeStatic
    get_efor(eelement, collection, content, deep) {
        return ' '.repeat(deep) + `for ${eelement} in ${collection}:` + '\n' + content;
    }

    // noinspection JSMethodCanBeStatic
    get_edw(econdition, content, deep) {
        return ' '.repeat(deep) + 'while True:\n' + content + '\n' + ' '.repeat(deep) + 'if ' + econdition + ':\n' + ' '.repeat(deep) + 'break\n';
    }

    // noinspection JSMethodCanBeStatic
    get_ewd(econdition, content, deep) {
        return ' '.repeat(deep) + 'while ' + econdition + ':\n' + content + '\n';
    }

    // noinspection JSMethodCanBeStatic
    get_manualIf(econdition, left, right, deep) {
        return ' '.repeat(deep - 1) + 'if ' + econdition + ':\n' + ' '.repeat(deep - 2) + left + ' '.repeat(deep - 1) + 'else:\n' + ' '.repeat(deep - 2) + right;
    }

    // noinspection JSMethodCanBeStatic
    get_manualLoop(econdition, path2, comb, deep) {
        return path2 + ' '.repeat(deep - 1) + 'while ' + econdition + ':\n' + comb;
    }

    set_increaseDeep(value) {
        this.deep = this.deep + parseInt(value);
    }
}


//java contents
class JavaBuilder extends AbstractLanguageBuilder {

    constructor(deep) {
        super();
        this.deep = parseInt(deep);
    }

    // noinspection JSMethodCanBeStatic
    get_core(startnode_inputtype, startnode_input, endnode_outputtype, endnode_output, methodname, content) {
        if ($('#editDiagramModal').hasClass('in')) {
            return content;
        } else {
            return "public " + endnode_outputtype + " " + methodname + "(" + startnode_inputtype + " " + startnode_input + ")\n{\n" + content + "return " + endnode_output + ";\n}";
        }
    }

    // noinspection JSMethodCanBeStatic
    get_if(econdition, ethen, eelse, deep) {
        return " ".repeat(deep) + "if(" + econdition + "){\n" + ethen + "\n" + " ".repeat(deep) + "}else{\n" + eelse + "\n" + " ".repeat(deep) + "}\n";
    }

    // noinspection JSMethodCanBeStatic
    get_loop(econdition, path, content, deep) {
        return path + " ".repeat(deep) + "while(!(" + econdition + ")){\n" + content + "}";
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

    // noinspection JSMethodCanBeStatic
    get_manualIf(econdition, left, right, deep) {
        return " ".repeat(deep - 1) + "if(" + econdition + "){\n" + left + " ".repeat(deep - 1) + "}else{\n" + right + " ".repeat(deep - 1) + "}\n";
    }

    // noinspection JSMethodCanBeStatic
    get_manualLoop(econdition, path2, comb, deep) {
        return path2 + " ".repeat(deep - 1) + "while(!(" + econdition + ")){\n" + comb + "}\n";
    }

    set_increaseDeep(value) {
        this.deep = this.deep + parseInt(value);
    }
}

const Java = new JavaBuilder(2);
const Python = new PythonBuilder(2);