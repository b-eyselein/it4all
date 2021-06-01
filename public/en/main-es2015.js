var $localize=Object.assign(void 0===$localize?{}:$localize,{locale:"en"});
"use strict";(function(global){global.ng=global.ng||{};global.ng.common=global.ng.common||{};global.ng.common.locales=global.ng.common.locales||{};const u=undefined;function plural(n){let i=Math.floor(Math.abs(n)),v=n.toString().replace(/^[^.]*\.?/,"").length;if(i===1&&v===0)return 1;return 5}global.ng.common.locales["en"]=["en",[["a","p"],["AM","PM"],u],[["AM","PM"],u,u],[["S","M","T","W","T","F","S"],["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"],["Su","Mo","Tu","We","Th","Fr","Sa"]],u,[["J","F","M","A","M","J","J","A","S","O","N","D"],["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"],["January","February","March","April","May","June","July","August","September","October","November","December"]],u,[["B","A"],["BC","AD"],["Before Christ","Anno Domini"]],0,[6,0],["M/d/yy","MMM d, y","MMMM d, y","EEEE, MMMM d, y"],["h:mm a","h:mm:ss a","h:mm:ss a z","h:mm:ss a zzzz"],["{1}, {0}",u,"{1} 'at' {0}",u],[".",",",";","%","+","-","E","\xD7","\u2030","\u221E","NaN",":"],["#,##0.###","#,##0%","\xA4#,##0.00","#E0"],"USD","$","US Dollar",{},"ltr",plural,[[["mi","n","in the morning","in the afternoon","in the evening","at night"],["midnight","noon","in the morning","in the afternoon","in the evening","at night"],u],[["midnight","noon","morning","afternoon","evening","night"],u,u],["00:00","12:00",["06:00","12:00"],["12:00","18:00"],["18:00","21:00"],["21:00","06:00"]]]]})(typeof globalThis!=="undefined"&&globalThis||typeof global!=="undefined"&&global||typeof window!=="undefined"&&window);;
(window["webpackJsonp"] = window["webpackJsonp"] || []).push([["main"],{

/***/ "+GYJ":
/*!**************************************************************************!*\
  !*** ./src/app/tools/random-tools/bool/_model/boolean-formula-parser.ts ***!
  \**************************************************************************/
/*! exports provided: BooleanFormulaParser, parseBooleanFormula */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BooleanFormulaParser", function() { return BooleanFormulaParser; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "parseBooleanFormula", function() { return parseBooleanFormula; });
/* harmony import */ var chevrotain__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! chevrotain */ "+oOE");
/* harmony import */ var _bool_node__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./bool-node */ "oi/H");


// Tokens
const whiteSpace = Object(chevrotain__WEBPACK_IMPORTED_MODULE_0__["createToken"])({ name: 'WhiteSpace', pattern: /\s+/, group: chevrotain__WEBPACK_IMPORTED_MODULE_0__["Lexer"].SKIPPED });
const variable = Object(chevrotain__WEBPACK_IMPORTED_MODULE_0__["createToken"])({ name: 'Variable', pattern: /[a-z]/ });
const TRUE = Object(chevrotain__WEBPACK_IMPORTED_MODULE_0__["createToken"])({ name: 'TRUE', pattern: /1|TRUE/i });
const FALSE = Object(chevrotain__WEBPACK_IMPORTED_MODULE_0__["createToken"])({ name: 'FALSE', pattern: /0|FALSE/i });
const leftBracket = Object(chevrotain__WEBPACK_IMPORTED_MODULE_0__["createToken"])({ name: 'leftBracket', pattern: /\(/ });
const rightBracket = Object(chevrotain__WEBPACK_IMPORTED_MODULE_0__["createToken"])({ name: 'rightBracket', pattern: /\)/ });
const notOperator = Object(chevrotain__WEBPACK_IMPORTED_MODULE_0__["createToken"])({ name: 'notOperator', pattern: /not/ });
const andOperator = Object(chevrotain__WEBPACK_IMPORTED_MODULE_0__["createToken"])({ name: 'AndOperator', pattern: /and/ });
const orOperator = Object(chevrotain__WEBPACK_IMPORTED_MODULE_0__["createToken"])({ name: 'OrOperator', pattern: /or/ });
const otherOperators = Object(chevrotain__WEBPACK_IMPORTED_MODULE_0__["createToken"])({ name: 'Operators', pattern: /[x|n]or|nand|equiv|impl/ });
const allTokens = [
    //  andOperator, orOperator, xOrOperator, nOrOerator, nAndOperator, equivOperator, implOperator,
    leftBracket, rightBracket,
    notOperator, andOperator, orOperator, otherOperators,
    TRUE, FALSE, variable, whiteSpace
];
const BooleanFormulaLexer = new chevrotain__WEBPACK_IMPORTED_MODULE_0__["Lexer"](allTokens);
class BooleanFormulaParser extends chevrotain__WEBPACK_IMPORTED_MODULE_0__["EmbeddedActionsParser"] {
    constructor() {
        super(allTokens);
        this.trueRule = this.RULE('trueRule', () => {
            this.CONSUME(TRUE);
            return _bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanTrue"];
        });
        this.falseRule = this.RULE('falseRule', () => {
            this.CONSUME(FALSE);
            return _bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanFalse"];
        });
        this.variableRule = this.RULE('variableRule', () => {
            const variableToken = this.CONSUME(variable);
            return new _bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanVariable"](variableToken.image[0]);
        });
        this.factor = this.RULE('factor', () => {
            return this.OR([
                {
                    ALT: () => {
                        this.CONSUME(leftBracket);
                        const child = this.SUBRULE(this.booleanFormula);
                        this.CONSUME(rightBracket);
                        return child;
                    }
                },
                { ALT: () => this.SUBRULE(this.variableRule) },
                { ALT: () => this.SUBRULE(this.trueRule) },
                { ALT: () => this.SUBRULE(this.falseRule) },
            ]);
        });
        this.notFactor = this.RULE('notFactor', () => {
            const negated = this.OPTION(() => {
                return this.CONSUME(notOperator);
            }) !== undefined;
            const child = this.SUBRULE(this.factor);
            return negated ? new _bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanNot"](child) : child;
        });
        this.andTermComponent = this.RULE('otherTerm', () => {
            const left = this.SUBRULE(this.notFactor);
            const otherOpRights = [];
            this.MANY(() => {
                const operator = this.CONSUME(otherOperators);
                const factor = this.SUBRULE2(this.notFactor);
                otherOpRights.push({ operator: operator.image, right: factor });
            });
            if (otherOpRights.length === 0) {
                return left;
            }
            else {
                return otherOpRights.reduce((prev, curr) => Object(_bool_node__WEBPACK_IMPORTED_MODULE_1__["instantiateOperator"])(prev, curr.operator, curr.right), left);
            }
        });
        this.orTermComponent = this.RULE('orTerm', () => {
            const left = this.SUBRULE(this.andTermComponent);
            const rightSides = [];
            this.MANY(() => {
                this.CONSUME(andOperator);
                rightSides.push(this.SUBRULE2(this.andTermComponent));
            });
            return rightSides.length > 0 ? new _bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanAnd"](left, rightSides.reduce((l, r) => new _bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanAnd"](l, r))) : left;
        });
        this.booleanFormula = this.RULE('booleanFormula', () => {
            const left = this.SUBRULE(this.orTermComponent);
            const rightSides = [];
            this.MANY(() => {
                this.CONSUME(orOperator);
                rightSides.push(this.SUBRULE2(this.orTermComponent));
            });
            return rightSides.length > 0 ? new _bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanOr"](left, rightSides.reduce((l, r) => new _bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanOr"](l, r))) : left;
        });
        this.performSelfAnalysis();
    }
}
function parseBooleanFormula(formulaString) {
    const lexResult = BooleanFormulaLexer.tokenize(formulaString);
    const parser = new BooleanFormulaParser();
    parser.input = lexResult.tokens;
    return parser.booleanFormula();
}


/***/ }),

/***/ "+Iia":
/*!***********************************************************************************!*\
  !*** ./src/app/tools/collection-tools/sql/sql-exercise/sql-exercise.component.ts ***!
  \***********************************************************************************/
/*! exports provided: SqlExerciseComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SqlExerciseComponent", function() { return SqlExerciseComponent; });
/* harmony import */ var _collection_tool_helpers__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../collection-tool-helpers */ "VRMR");
/* harmony import */ var _helpers_component_with_exercise_directive__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../_helpers/component-with-exercise.directive */ "TRIe");
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../../../_services/apollo_services */ "o20/");
/* harmony import */ var codemirror_mode_sql_sql__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! codemirror/mode/sql/sql */ "/9rB");
/* harmony import */ var codemirror_mode_sql_sql__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(codemirror_mode_sql_sql__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _services_dexie_service__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../../../_services/dexie.service */ "4di/");
/* harmony import */ var _ctrl_ngx_codemirror__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! @ctrl/ngx-codemirror */ "Xl2X");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! @angular/forms */ "3Pt+");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _shared_tabs_tabs_component__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ../../../../shared/tabs/tabs.component */ "b4kd");
/* harmony import */ var _shared_tab_tab_component__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ../../../../shared/tab/tab.component */ "4YYW");
/* harmony import */ var _components_sql_table_contents_sql_table_contents_component__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ../_components/sql-table-contents/sql-table-contents.component */ "HvLl");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _shared_solution_saved_solution_saved_component__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ../../../../shared/solution-saved/solution-saved.component */ "rqf4");
/* harmony import */ var _results_sql_result_sql_result_component__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! ../_results/sql-result/sql-result.component */ "L0Rw");
/* harmony import */ var _components_string_sample_sol_string_sample_sol_component__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! ../../_components/string-sample-sol/string-sample-sol.component */ "jonz");
/* harmony import */ var _results_sql_execution_result_sql_execution_result_component__WEBPACK_IMPORTED_MODULE_16__ = __webpack_require__(/*! ../_results/sql-execution-result/sql-execution-result.component */ "WY1b");


















function SqlExerciseComponent_div_23_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "div", 16);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](1, "div", 17);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](2, "Es gab einen Fehler bei der Korrektur:");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](3, "div", 18);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](4, "pre");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate"](ctx_r0.queryError.message);
} }
function SqlExerciseComponent_ng_container_24_it4all_sql_result_4_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](0, "it4all-sql-result", 22);
} if (rf & 2) {
    const ctx_r4 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("result", ctx_r4.sqlResult);
} }
function SqlExerciseComponent_ng_container_24_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](1, "div", 19);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](2, "it4all-solution-saved", 20);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](3, "div", 19);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](4, SqlExerciseComponent_ng_container_24_it4all_sql_result_4_Template, 1, 1, "it4all-sql-result", 21);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("solutionSaved", ctx_r1.correctionResult.solutionSaved);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx_r1.sqlResult);
} }
function SqlExerciseComponent_ng_container_29_ng_container_1_br_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](0, "br");
} }
function SqlExerciseComponent_ng_container_29_ng_container_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](1, "it4all-string-sample-sol", 24);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](2, SqlExerciseComponent_ng_container_29_ng_container_1_br_2_Template, 1, 0, "br", 13);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const sample_r6 = ctx.$implicit;
    const last_r7 = ctx.last;
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("sample", sample_r6);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", !last_r7);
} }
function SqlExerciseComponent_ng_container_29_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](1, SqlExerciseComponent_ng_container_29_ng_container_1_Template, 3, 2, "ng-container", 23);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r2 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngForOf", ctx_r2.sampleSolutions);
} }
function SqlExerciseComponent_ng_container_30_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](1, "it4all-sql-execution-result", 22);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("result", ctx_r3.sqlResult.executionResult);
} }
function getIdForSqlExPart(sqlExPart) {
    return 'solve';
}
class SqlExerciseComponent extends _helpers_component_with_exercise_directive__WEBPACK_IMPORTED_MODULE_1__["ComponentWithExerciseDirective"] {
    constructor(sqlCorrectionGQL, dexieService) {
        super(sqlCorrectionGQL, dexieService);
        this.editorOptions = Object(_collection_tool_helpers__WEBPACK_IMPORTED_MODULE_0__["getDefaultEditorOptions"])('sql');
        this.partId = getIdForSqlExPart(_services_apollo_services__WEBPACK_IMPORTED_MODULE_2__["SqlExPart"].SqlSingleExPart);
        this.solution = '';
        // Sample solutions
        this.displaySampleSolutions = false;
    }
    ngOnInit() {
        this.dexieService
            .getSolution(this.exerciseFragment, this.partId)
            .then((solution) => this.solution = solution ? solution.solution : '');
    }
    // Result types
    get correctionResult() {
        var _a, _b;
        return (_b = (_a = this.resultQuery) === null || _a === void 0 ? void 0 : _a.me.sqlExercise) === null || _b === void 0 ? void 0 : _b.correct;
    }
    get sqlResult() {
        var _a;
        return (_a = this.correctionResult) === null || _a === void 0 ? void 0 : _a.result;
    }
    // Correction
    getSolution() {
        return this.solution.length > 0 ? this.solution : undefined;
    }
    getMutationQueryVariables() {
        return {
            exId: this.exerciseFragment.exerciseId,
            collId: this.exerciseFragment.collectionId,
            solution: this.getSolution(),
            part: _services_apollo_services__WEBPACK_IMPORTED_MODULE_2__["SqlExPart"].SqlSingleExPart,
        };
    }
    correct() {
        this.correctAbstract(this.exerciseFragment, this.partId);
    }
    toggleSampleSolutions() {
        this.displaySampleSolutions = !this.displaySampleSolutions;
    }
    get sampleSolutions() {
        return this.contentFragment.sqlSampleSolutions;
    }
}
SqlExerciseComponent.ɵfac = function SqlExerciseComponent_Factory(t) { return new (t || SqlExerciseComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵdirectiveInject"](_services_apollo_services__WEBPACK_IMPORTED_MODULE_2__["SqlCorrectionGQL"]), _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵdirectiveInject"](_services_dexie_service__WEBPACK_IMPORTED_MODULE_5__["DexieService"])); };
SqlExerciseComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵdefineComponent"]({ type: SqlExerciseComponent, selectors: [["it4all-sql-exercise"]], inputs: { exerciseFragment: "exerciseFragment", contentFragment: "contentFragment" }, features: [_angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵInheritDefinitionFeature"]], decls: 31, vars: 14, consts: [[1, "container", "is-fluid"], [1, "columns"], [1, "column", "is-two-fifths-desktop"], [1, "title", "is-3", "has-text-centered"], [1, "notification", "is-light-grey"], [1, "title", "is-4", "has-text-centered"], [3, "ngModel", "options", "ngModelChange"], [1, "column"], [1, "button", "is-link", "is-fullwidth", 3, "click"], ["routerLink", "../..", 1, "button", "is-dark", "is-fullwidth"], [3, "title"], [3, "dbContents"], ["class", "message is-danger my-3", 4, "ngIf"], [4, "ngIf"], [1, "buttons"], [1, "button", "is-primary", "is-fullwidth", 3, "click"], [1, "message", "is-danger", "my-3"], [1, "message-header"], [1, "message-body"], [1, "my-3"], [3, "solutionSaved"], [3, "result", 4, "ngIf"], [3, "result"], [4, "ngFor", "ngForOf"], [3, "sample"]], template: function SqlExerciseComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](1, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](2, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](3, "h1", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](4, "Aufgabenstellung");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](5, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](6);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](7, "h1", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](8, "Anfrage");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](9, "ngx-codemirror", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵlistener"]("ngModelChange", function SqlExerciseComponent_Template_ngx_codemirror_ngModelChange_9_listener($event) { return ctx.solution = $event; });
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](10, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](11, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](12, "div", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](13, "button", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵlistener"]("click", function SqlExerciseComponent_Template_button_click_13_listener() { return ctx.correct(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](14, " L\u00F6sung testen ");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](15, "div", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](16, "a", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](17, "Bearbeiten beenden");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](18, "div", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](19, "it4all-tabs");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](20, "it4all-tab", 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](21, "it4all-sql-table-contents", 11);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](22, "it4all-tab", 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](23, SqlExerciseComponent_div_23_Template, 6, 1, "div", 12);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](24, SqlExerciseComponent_ng_container_24_Template, 5, 2, "ng-container", 13);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](25, "it4all-tab", 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](26, "div", 14);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](27, "button", 15);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵlistener"]("click", function SqlExerciseComponent_Template_button_click_27_listener() { return ctx.toggleSampleSolutions(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](28);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](29, SqlExerciseComponent_ng_container_29_Template, 2, 1, "ng-container", 13);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](30, SqlExerciseComponent_ng_container_30_Template, 2, 1, "ng-container", 13);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](6);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate"](ctx.exerciseFragment.text);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngModel", ctx.solution)("options", ctx.editorOptions);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](4);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵclassProp"]("is-loading", ctx.isCorrecting);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](7);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("title", "Datenbankinhalt");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("dbContents", ctx.contentFragment.sqlDbContents);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("title", ctx.correctionTabTitle);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx.queryError);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx.resultQuery);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("title", ctx.sampleSolutionsTabTitle);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate1"](" Musterl\u00F6sungen ", ctx.displaySampleSolutions ? "anzeigen" : "ausblenden", " ");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx.displaySampleSolutions);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx.sqlResult);
    } }, directives: [_ctrl_ngx_codemirror__WEBPACK_IMPORTED_MODULE_6__["CodemirrorComponent"], _angular_forms__WEBPACK_IMPORTED_MODULE_7__["NgControlStatus"], _angular_forms__WEBPACK_IMPORTED_MODULE_7__["NgModel"], _angular_router__WEBPACK_IMPORTED_MODULE_8__["RouterLinkWithHref"], _shared_tabs_tabs_component__WEBPACK_IMPORTED_MODULE_9__["TabsComponent"], _shared_tab_tab_component__WEBPACK_IMPORTED_MODULE_10__["TabComponent"], _components_sql_table_contents_sql_table_contents_component__WEBPACK_IMPORTED_MODULE_11__["SqlTableContentsComponent"], _angular_common__WEBPACK_IMPORTED_MODULE_12__["NgIf"], _shared_solution_saved_solution_saved_component__WEBPACK_IMPORTED_MODULE_13__["SolutionSavedComponent"], _results_sql_result_sql_result_component__WEBPACK_IMPORTED_MODULE_14__["SqlResultComponent"], _angular_common__WEBPACK_IMPORTED_MODULE_12__["NgForOf"], _components_string_sample_sol_string_sample_sol_component__WEBPACK_IMPORTED_MODULE_15__["StringSampleSolComponent"], _results_sql_execution_result_sql_execution_result_component__WEBPACK_IMPORTED_MODULE_16__["SqlExecutionResultComponent"]], styles: [".CodeMirror {\n  height: 200px;\n  border: 1px solid grey;\n}\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uLy4uLy4uL3NxbC1leGVyY2lzZS5jb21wb25lbnQuc2FzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtFQUNFLGFBQUE7RUFDQSxzQkFBQTtBQUNGIiwiZmlsZSI6InNxbC1leGVyY2lzZS5jb21wb25lbnQuc2FzcyIsInNvdXJjZXNDb250ZW50IjpbIi5Db2RlTWlycm9yXG4gIGhlaWdodDogMjAwcHhcbiAgYm9yZGVyOiAxcHggc29saWQgZ3JleVxuIl19 */"], encapsulation: 2 });


/***/ }),

/***/ "/bl0":
/*!**************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/ebnf/ebnf-exercise/ebnf-exercise.component.ts ***!
  \**************************************************************************************/
/*! exports provided: EbnfExerciseComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "EbnfExerciseComponent", function() { return EbnfExerciseComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");

class EbnfExerciseComponent {
    constructor() {
    }
    ngOnInit() {
        console.info(JSON.stringify(this.contentFragment, null, 2));
    }
}
EbnfExerciseComponent.ɵfac = function EbnfExerciseComponent_Factory(t) { return new (t || EbnfExerciseComponent)(); };
EbnfExerciseComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: EbnfExerciseComponent, selectors: [["it4all-ebnf-exercise"]], inputs: { exerciseFragment: "exerciseFragment", contentFragment: "contentFragment" }, decls: 4, vars: 0, consts: [[1, "container"]], template: function EbnfExerciseComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "p");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2, "ebnf-exercise works!");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](3, " TODO!\n");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } }, encapsulation: 2 });


/***/ }),

/***/ "/fbC":
/*!********************************************************************************!*\
  !*** ./src/app/tools/collection-tools/uml/_model/joint-class-diag-elements.ts ***!
  \********************************************************************************/
/*! exports provided: STD_CLASS_HEIGHT, STD_CLASS_WIDTH, MyJointClass, isMyJointClass, isAssociation, isImplementation */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "STD_CLASS_HEIGHT", function() { return STD_CLASS_HEIGHT; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "STD_CLASS_WIDTH", function() { return STD_CLASS_WIDTH; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "MyJointClass", function() { return MyJointClass; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "isMyJointClass", function() { return isMyJointClass; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "isAssociation", function() { return isAssociation; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "isImplementation", function() { return isImplementation; });
/* harmony import */ var jointjs__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! jointjs */ "iuCI");
/* harmony import */ var lodash__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! lodash */ "LvDl");
/* harmony import */ var lodash__WEBPACK_IMPORTED_MODULE_1___default = /*#__PURE__*/__webpack_require__.n(lodash__WEBPACK_IMPORTED_MODULE_1__);
/* harmony import */ var _uml_consts__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./uml-consts */ "y3Rt");
/* harmony import */ var _my_uml_interfaces__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./my-uml-interfaces */ "LvJS");




const STD_CLASS_HEIGHT = 160;
const STD_CLASS_WIDTH = 200;
const CLASS_MARKUP = `
<g class="rotatable">
  <g class="scalable">
    <rect class="uml-class-name-rect"/><rect class="uml-class-attrs-rect"/><rect class="uml-class-methods-rect"/>
  </g>
  <text class="uml-class-name-text"/><text class="uml-class-attrs-text"/><text class="uml-class-methods-text"/>
</g>`.trim();
class MyJointClass extends jointjs__WEBPACK_IMPORTED_MODULE_0__["shapes"].basic.Generic {
    constructor(attributes, options) {
        super(attributes, options);
        this.set('markup', CLASS_MARKUP);
    }
    initialize() {
        this.on('change:classType change:className change:attributes change:methods', () => {
            console.log('Update...');
            this.updateRectangles();
        });
        this.updateRectangles();
        jointjs__WEBPACK_IMPORTED_MODULE_0__["shapes"].basic.Generic.prototype.initialize.apply(this, arguments);
    }
    updateRectangles() {
        const attrs = this.get('attrs');
        const rects = [
            { type: 'name', text: this.getClassRectText() },
            { type: 'attrs', text: this.getAttributesAsStrings() },
            { type: 'methods', text: this.getMethodsAsStrings() }
        ];
        let offsetY = 0;
        rects.forEach((rect) => {
            const rectHeight = Object(_uml_consts__WEBPACK_IMPORTED_MODULE_2__["calcRectHeight"])(rect.text);
            attrs['.uml-class-' + rect.type + '-text'].text = rect.text.join('\n');
            attrs['.uml-class-' + rect.type + '-rect'].height = rectHeight;
            attrs['.uml-class-' + rect.type + '-rect'].transform = 'translate(0,' + offsetY + ')';
            offsetY += rectHeight;
        });
        this.resize(STD_CLASS_WIDTH, offsetY);
    }
    defaults() {
        return lodash__WEBPACK_IMPORTED_MODULE_1__["defaultsDeep"]({
            type: 'MyJointClass',
            size: { width: STD_CLASS_WIDTH, height: STD_CLASS_HEIGHT },
            attrs: {
                rect: { width: _uml_consts__WEBPACK_IMPORTED_MODULE_2__["STD_ELEMENT_WIDTH"], stroke: _uml_consts__WEBPACK_IMPORTED_MODULE_2__["COLORS"].Black, strokeWidth: 2 },
                text: { fill: _uml_consts__WEBPACK_IMPORTED_MODULE_2__["COLORS"].Black, fontSize: _uml_consts__WEBPACK_IMPORTED_MODULE_2__["fontSize"], fontFamily: 'Times New Roman' },
                // Do not delete, needed for later!
                '.uml-class-name-rect': {},
                '.uml-class-attrs-rect': {},
                '.uml-class-methods-rect': {},
                '.uml-class-name-text': {
                    ref: '.uml-class-name-rect', refY: .5, refX: .5,
                    textAnchor: 'middle', yAlignment: 'middle', fontWeight: 'bold',
                },
                '.uml-class-attrs-text': { ref: '.uml-class-attrs-rect', refY: _uml_consts__WEBPACK_IMPORTED_MODULE_2__["STD_PADDING"], refX: _uml_consts__WEBPACK_IMPORTED_MODULE_2__["STD_PADDING"] },
                '.uml-class-methods-text': { ref: '.uml-class-methods-rect', refY: _uml_consts__WEBPACK_IMPORTED_MODULE_2__["STD_PADDING"], refX: _uml_consts__WEBPACK_IMPORTED_MODULE_2__["STD_PADDING"] }
            },
            className: '',
            classType: '',
            attributes: [],
            methods: []
        }, jointjs__WEBPACK_IMPORTED_MODULE_0__["shapes"].basic.Generic.prototype.defaults);
    }
    getClassType() {
        return this.get('classType') || 'CLASS';
    }
    setClassType(classType) {
        if (_my_uml_interfaces__WEBPACK_IMPORTED_MODULE_3__["CLASS_TYPES"].indexOf(classType) >= 0) {
            this.prop('classType', classType);
        }
        else {
            console.error('>>' + classType + '<< is no legal value for a class type!');
        }
    }
    getClassName() {
        return this.get('className');
    }
    setClassName(className) {
        this.set('className', className);
    }
    getClassRectText() {
        const className = this.getClassName();
        switch (this.getClassType()) {
            case 'ABSTRACT':
                return ['<<abstract>>', className];
            case 'INTERFACE':
                return ['<<interface>>', className];
            case 'CLASS':
            default:
                return [className];
        }
    }
    getAttributesAsStrings() {
        return this.get('attributes').map(_my_uml_interfaces__WEBPACK_IMPORTED_MODULE_3__["buildAttributeString"]);
    }
    getAttributes() {
        return this.get('attributes');
    }
    setAttributes(attributes) {
        this.set('attributes', attributes);
    }
    getMethods() {
        return this.get('methods');
    }
    getMethodsAsStrings() {
        return this.get('methods').map(_my_uml_interfaces__WEBPACK_IMPORTED_MODULE_3__["buildMethodString"]);
    }
    setMethods(methods) {
        this.set('methods', methods);
    }
    getAsUmlClass() {
        return {
            name: this.getClassName(),
            classType: this.getClassType(),
            attributes: this.getAttributes(),
            methods: this.getMethods()
        };
    }
}
function isMyJointClass(cell) {
    return cell instanceof MyJointClass;
}
function isAssociation(link) {
    return link instanceof jointjs__WEBPACK_IMPORTED_MODULE_0__["shapes"].uml.Association;
}
function isImplementation(link) {
    return link instanceof jointjs__WEBPACK_IMPORTED_MODULE_0__["shapes"].uml.Implementation;
}


/***/ }),

/***/ 0:
/*!***************************!*\
  !*** multi ./src/main.ts ***!
  \***************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

module.exports = __webpack_require__(/*! /home/bjorn/workspace/scala/it4all/ui/src/main.ts */"zUnb");


/***/ }),

/***/ "0iet":
/*!*******************************************************************!*\
  !*** ./src/app/tools/collection-tools/collection-tools.module.ts ***!
  \*******************************************************************/
/*! exports provided: CollectionToolsModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CollectionToolsModule", function() { return CollectionToolsModule; });
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _regex_regex_matching_result_regex_matching_result_component__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./regex/regex-matching-result/regex-matching-result.component */ "uEXo");
/* harmony import */ var _regex_regex_extraction_result_regex_extraction_result_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./regex/regex-extraction-result/regex-extraction-result.component */ "kLWW");
/* harmony import */ var _regex_regex_extraction_result_regex_extraction_match_regex_extraction_match_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./regex/regex-extraction-result/regex-extraction-match/regex-extraction-match.component */ "5M+P");
/* harmony import */ var _regex_regex_cheatsheet_regex_cheatsheet_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./regex/regex-cheatsheet/regex-cheatsheet.component */ "9siN");
/* harmony import */ var _sql_results_sql_result_sql_result_component__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./sql/_results/sql-result/sql-result.component */ "L0Rw");
/* harmony import */ var _sql_results_sql_matching_result_sql_matching_result_component__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./sql/_results/sql-matching-result/sql-matching-result.component */ "1GxF");
/* harmony import */ var _collection_tools_routing__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ./collection-tools.routing */ "BeXp");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! @angular/forms */ "3Pt+");
/* harmony import */ var _components_exercise_files_editor_exercise_files_editor_component__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./_components/exercise-files-editor/exercise-files-editor.component */ "346A");
/* harmony import */ var _components_exercise_file_card_exercise_file_card_component__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ./_components/exercise-file-card/exercise-file-card.component */ "c3C5");
/* harmony import */ var _shared_shared_module__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ../../shared/shared.module */ "PCNd");
/* harmony import */ var _ctrl_ngx_codemirror__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! @ctrl/ngx-codemirror */ "Xl2X");
/* harmony import */ var _exercise_overview_exercise_overview_component__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ./exercise-overview/exercise-overview.component */ "SJa0");
/* harmony import */ var _exercise_exercise_component__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! ./exercise/exercise.component */ "t2v4");
/* harmony import */ var _regex_regex_exercise_regex_exercise_component__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! ./regex/regex-exercise/regex-exercise.component */ "Brp2");
/* harmony import */ var _programming_programming_exercise_programming_exercise_component__WEBPACK_IMPORTED_MODULE_16__ = __webpack_require__(/*! ./programming/programming-exercise/programming-exercise.component */ "EK2f");
/* harmony import */ var _sql_sql_exercise_sql_exercise_component__WEBPACK_IMPORTED_MODULE_17__ = __webpack_require__(/*! ./sql/sql-exercise/sql-exercise.component */ "+Iia");
/* harmony import */ var _uml_uml_exercise_uml_exercise_component__WEBPACK_IMPORTED_MODULE_18__ = __webpack_require__(/*! ./uml/uml-exercise/uml-exercise.component */ "ghNm");
/* harmony import */ var _web_web_exercise_web_exercise_component__WEBPACK_IMPORTED_MODULE_19__ = __webpack_require__(/*! ./web/web-exercise/web-exercise.component */ "iKdM");
/* harmony import */ var _uml_uml_class_selection_uml_class_selection_component__WEBPACK_IMPORTED_MODULE_20__ = __webpack_require__(/*! ./uml/uml-class-selection/uml-class-selection.component */ "xvqd");
/* harmony import */ var _all_exercises_overview_all_exercises_overview_component__WEBPACK_IMPORTED_MODULE_21__ = __webpack_require__(/*! ./all-exercises-overview/all-exercises-overview.component */ "Zy2k");
/* harmony import */ var _xml_xml_exercise_xml_exercise_component__WEBPACK_IMPORTED_MODULE_22__ = __webpack_require__(/*! ./xml/xml-exercise/xml-exercise.component */ "C5uP");
/* harmony import */ var _sql_results_sql_execution_result_sql_execution_result_component__WEBPACK_IMPORTED_MODULE_23__ = __webpack_require__(/*! ./sql/_results/sql-execution-result/sql-execution-result.component */ "WY1b");
/* harmony import */ var _uml_uml_diagram_drawing_uml_diagram_drawing_component__WEBPACK_IMPORTED_MODULE_24__ = __webpack_require__(/*! ./uml/uml-diagram-drawing/uml-diagram-drawing.component */ "sArB");
/* harmony import */ var _uml_components_uml_class_edit_uml_class_edit_component__WEBPACK_IMPORTED_MODULE_25__ = __webpack_require__(/*! ./uml/_components/uml-class-edit/uml-class-edit.component */ "V+Il");
/* harmony import */ var _uml_components_uml_assoc_edit_uml_assoc_edit_component__WEBPACK_IMPORTED_MODULE_26__ = __webpack_require__(/*! ./uml/_components/uml-assoc-edit/uml-assoc-edit.component */ "UZ51");
/* harmony import */ var _components_exercise_link_card_exercise_link_card_component__WEBPACK_IMPORTED_MODULE_27__ = __webpack_require__(/*! ./_components/exercise-link-card/exercise-link-card.component */ "xeHz");
/* harmony import */ var _uml_components_uml_diag_drawing_correction_uml_diag_drawing_correction_component__WEBPACK_IMPORTED_MODULE_28__ = __webpack_require__(/*! ./uml/_components/uml-diag-drawing-correction/uml-diag-drawing-correction.component */ "x5V5");
/* harmony import */ var _uml_uml_member_allocation_uml_member_allocation_component__WEBPACK_IMPORTED_MODULE_29__ = __webpack_require__(/*! ./uml/uml-member-allocation/uml-member-allocation.component */ "Qt55");
/* harmony import */ var _xml_components_xml_document_correction_xml_document_correction_component__WEBPACK_IMPORTED_MODULE_30__ = __webpack_require__(/*! ./xml/_components/xml-document-correction/xml-document-correction.component */ "PLyK");
/* harmony import */ var _sql_components_sql_table_contents_sql_table_contents_component__WEBPACK_IMPORTED_MODULE_31__ = __webpack_require__(/*! ./sql/_components/sql-table-contents/sql-table-contents.component */ "HvLl");
/* harmony import */ var _components_string_sample_sol_string_sample_sol_component__WEBPACK_IMPORTED_MODULE_32__ = __webpack_require__(/*! ./_components/string-sample-sol/string-sample-sol.component */ "jonz");
/* harmony import */ var _sql_components_query_result_table_query_result_table_component__WEBPACK_IMPORTED_MODULE_33__ = __webpack_require__(/*! ./sql/_components/query-result-table/query-result-table.component */ "DeMQ");
/* harmony import */ var _programming_results_programming_unit_test_result_programming_unit_test_result_component__WEBPACK_IMPORTED_MODULE_34__ = __webpack_require__(/*! ./programming/_results/programming-unit-test-result/programming-unit-test-result.component */ "lIHn");
/* harmony import */ var _programming_results_programming_normal_result_programming_implementation_correction_result_component__WEBPACK_IMPORTED_MODULE_35__ = __webpack_require__(/*! ./programming/_results/programming-normal-result/programming-implementation-correction-result.component */ "3HtT");
/* harmony import */ var _web_components_html_task_result_html_task_result_component__WEBPACK_IMPORTED_MODULE_36__ = __webpack_require__(/*! ./web/_components/html-task-result/html-task-result.component */ "qv6m");
/* harmony import */ var _web_components_html_attribute_result_html_attribute_result_component__WEBPACK_IMPORTED_MODULE_37__ = __webpack_require__(/*! ./web/_components/html-attribute-result/html-attribute-result.component */ "IASd");
/* harmony import */ var _components_filled_points_filled_points_component__WEBPACK_IMPORTED_MODULE_38__ = __webpack_require__(/*! ./_components/filled-points/filled-points.component */ "L0fW");
/* harmony import */ var _components_proficiency_card_proficiency_card_component__WEBPACK_IMPORTED_MODULE_39__ = __webpack_require__(/*! ./_components/proficiency-card/proficiency-card.component */ "vXFn");
/* harmony import */ var _lessons_lesson_questions_lesson_questions_content_component__WEBPACK_IMPORTED_MODULE_40__ = __webpack_require__(/*! ./lessons/lesson-questions/lesson-questions-content.component */ "CohO");
/* harmony import */ var _flask_flask_exercise_flask_exercise_component__WEBPACK_IMPORTED_MODULE_41__ = __webpack_require__(/*! ./flask/flask-exercise/flask-exercise.component */ "XO5P");
/* harmony import */ var _components_files_exercise_files_exercise_component__WEBPACK_IMPORTED_MODULE_42__ = __webpack_require__(/*! ./_components/files-exercise/files-exercise.component */ "Emuw");
/* harmony import */ var _uml_components_uml_impl_result_uml_impl_result_component__WEBPACK_IMPORTED_MODULE_43__ = __webpack_require__(/*! ./uml/_components/uml-impl-result/uml-impl-result.component */ "qhSi");
/* harmony import */ var _xml_components_xml_element_line_match_result_xml_element_line_match_result_component__WEBPACK_IMPORTED_MODULE_44__ = __webpack_require__(/*! ./xml/_components/xml-element-line-match-result/xml-element-line-match-result.component */ "wDY7");
/* harmony import */ var _uml_components_uml_assoc_match_result_uml_assoc_match_result_component__WEBPACK_IMPORTED_MODULE_45__ = __webpack_require__(/*! ./uml/_components/uml-assoc-match-result/uml-assoc-match-result.component */ "1UiD");
/* harmony import */ var _angular_platform_browser__WEBPACK_IMPORTED_MODULE_46__ = __webpack_require__(/*! @angular/platform-browser */ "jhN1");
/* harmony import */ var _ebnf_ebnf_exercise_ebnf_exercise_component__WEBPACK_IMPORTED_MODULE_47__ = __webpack_require__(/*! ./ebnf/ebnf-exercise/ebnf-exercise.component */ "/bl0");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_48__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _collection_tool_overview_collection_tool_overview_component__WEBPACK_IMPORTED_MODULE_49__ = __webpack_require__(/*! ./collection-tool-overview/collection-tool-overview.component */ "d8br");
/* harmony import */ var _lessons_lessons_for_tool_overview_lessons_for_tool_overview_component__WEBPACK_IMPORTED_MODULE_50__ = __webpack_require__(/*! ./lessons/lessons-for-tool-overview/lessons-for-tool-overview.component */ "dT2p");
/* harmony import */ var _lessons_lesson_overview_lesson_overview_component__WEBPACK_IMPORTED_MODULE_51__ = __webpack_require__(/*! ./lessons/lesson-overview/lesson-overview.component */ "8EYx");
/* harmony import */ var _lessons_lesson_as_text_lesson_as_text_component__WEBPACK_IMPORTED_MODULE_52__ = __webpack_require__(/*! ./lessons/lesson-as-text/lesson-as-text.component */ "tsMx");
/* harmony import */ var _lessons_lesson_as_video_lesson_as_video_component__WEBPACK_IMPORTED_MODULE_53__ = __webpack_require__(/*! ./lessons/lesson-as-video/lesson-as-video.component */ "1kDo");
/* harmony import */ var _collections_list_collections_list_component__WEBPACK_IMPORTED_MODULE_54__ = __webpack_require__(/*! ./collections-list/collections-list.component */ "Xlg+");
/* harmony import */ var _collection_overview_collection_overview_component__WEBPACK_IMPORTED_MODULE_55__ = __webpack_require__(/*! ./collection-overview/collection-overview.component */ "q8z/");



























































class CollectionToolsModule {
}
CollectionToolsModule.ɵfac = function CollectionToolsModule_Factory(t) { return new (t || CollectionToolsModule)(); };
CollectionToolsModule.ɵmod = _angular_core__WEBPACK_IMPORTED_MODULE_48__["ɵɵdefineNgModule"]({ type: CollectionToolsModule });
CollectionToolsModule.ɵinj = _angular_core__WEBPACK_IMPORTED_MODULE_48__["ɵɵdefineInjector"]({ providers: [], imports: [[
            _angular_platform_browser__WEBPACK_IMPORTED_MODULE_46__["BrowserModule"],
            _angular_common__WEBPACK_IMPORTED_MODULE_0__["CommonModule"],
            _angular_forms__WEBPACK_IMPORTED_MODULE_8__["FormsModule"],
            _ctrl_ngx_codemirror__WEBPACK_IMPORTED_MODULE_12__["CodemirrorModule"],
            _shared_shared_module__WEBPACK_IMPORTED_MODULE_11__["SharedModule"],
            _collection_tools_routing__WEBPACK_IMPORTED_MODULE_7__["CollectionToolRoutingModule"]
        ]] });
(function () { (typeof ngJitMode === "undefined" || ngJitMode) && _angular_core__WEBPACK_IMPORTED_MODULE_48__["ɵɵsetNgModuleScope"](CollectionToolsModule, { declarations: [_collection_tool_overview_collection_tool_overview_component__WEBPACK_IMPORTED_MODULE_49__["CollectionToolOverviewComponent"], _lessons_lessons_for_tool_overview_lessons_for_tool_overview_component__WEBPACK_IMPORTED_MODULE_50__["LessonsForToolOverviewComponent"], _lessons_lesson_overview_lesson_overview_component__WEBPACK_IMPORTED_MODULE_51__["LessonOverviewComponent"], _lessons_lesson_as_text_lesson_as_text_component__WEBPACK_IMPORTED_MODULE_52__["LessonAsTextComponent"], _lessons_lesson_as_video_lesson_as_video_component__WEBPACK_IMPORTED_MODULE_53__["LessonAsVideoComponent"], _collections_list_collections_list_component__WEBPACK_IMPORTED_MODULE_54__["CollectionsListComponent"], _collection_overview_collection_overview_component__WEBPACK_IMPORTED_MODULE_55__["CollectionOverviewComponent"], _exercise_overview_exercise_overview_component__WEBPACK_IMPORTED_MODULE_13__["ExerciseOverviewComponent"], _exercise_exercise_component__WEBPACK_IMPORTED_MODULE_14__["ExerciseComponent"], _all_exercises_overview_all_exercises_overview_component__WEBPACK_IMPORTED_MODULE_21__["AllExercisesOverviewComponent"], _components_exercise_files_editor_exercise_files_editor_component__WEBPACK_IMPORTED_MODULE_9__["ExerciseFilesEditorComponent"], _components_exercise_file_card_exercise_file_card_component__WEBPACK_IMPORTED_MODULE_10__["ExerciseFileCardComponent"],
        _lessons_lesson_questions_lesson_questions_content_component__WEBPACK_IMPORTED_MODULE_40__["LessonQuestionsContentComponent"],
        _all_exercises_overview_all_exercises_overview_component__WEBPACK_IMPORTED_MODULE_21__["AllExercisesOverviewComponent"],
        _exercise_overview_exercise_overview_component__WEBPACK_IMPORTED_MODULE_13__["ExerciseOverviewComponent"],
        _components_exercise_link_card_exercise_link_card_component__WEBPACK_IMPORTED_MODULE_27__["ExerciseLinkCardComponent"],
        _components_proficiency_card_proficiency_card_component__WEBPACK_IMPORTED_MODULE_39__["ProficiencyCardComponent"],
        _exercise_exercise_component__WEBPACK_IMPORTED_MODULE_14__["ExerciseComponent"],
        _components_string_sample_sol_string_sample_sol_component__WEBPACK_IMPORTED_MODULE_32__["StringSampleSolComponent"], _components_filled_points_filled_points_component__WEBPACK_IMPORTED_MODULE_38__["FilledPointsComponent"],
        _components_files_exercise_files_exercise_component__WEBPACK_IMPORTED_MODULE_42__["FilesExerciseComponent"],
        _ebnf_ebnf_exercise_ebnf_exercise_component__WEBPACK_IMPORTED_MODULE_47__["EbnfExerciseComponent"],
        _flask_flask_exercise_flask_exercise_component__WEBPACK_IMPORTED_MODULE_41__["FlaskExerciseComponent"],
        _programming_programming_exercise_programming_exercise_component__WEBPACK_IMPORTED_MODULE_16__["ProgrammingExerciseComponent"], _programming_results_programming_unit_test_result_programming_unit_test_result_component__WEBPACK_IMPORTED_MODULE_34__["ProgrammingUnitTestResultComponent"], _programming_results_programming_normal_result_programming_implementation_correction_result_component__WEBPACK_IMPORTED_MODULE_35__["ProgrammingImplementationCorrectionResultComponent"],
        _regex_regex_exercise_regex_exercise_component__WEBPACK_IMPORTED_MODULE_15__["RegexExerciseComponent"], _regex_regex_matching_result_regex_matching_result_component__WEBPACK_IMPORTED_MODULE_1__["RegexMatchingResultComponent"], _regex_regex_extraction_result_regex_extraction_result_component__WEBPACK_IMPORTED_MODULE_2__["RegexExtractionResultComponent"], _regex_regex_extraction_result_regex_extraction_match_regex_extraction_match_component__WEBPACK_IMPORTED_MODULE_3__["RegexExtractionMatchComponent"],
        _regex_regex_cheatsheet_regex_cheatsheet_component__WEBPACK_IMPORTED_MODULE_4__["RegexCheatsheetComponent"],
        _sql_sql_exercise_sql_exercise_component__WEBPACK_IMPORTED_MODULE_17__["SqlExerciseComponent"], _sql_results_sql_result_sql_result_component__WEBPACK_IMPORTED_MODULE_5__["SqlResultComponent"], _sql_results_sql_matching_result_sql_matching_result_component__WEBPACK_IMPORTED_MODULE_6__["SqlMatchingResultComponent"], _sql_results_sql_execution_result_sql_execution_result_component__WEBPACK_IMPORTED_MODULE_23__["SqlExecutionResultComponent"], _sql_components_sql_table_contents_sql_table_contents_component__WEBPACK_IMPORTED_MODULE_31__["SqlTableContentsComponent"], _sql_components_query_result_table_query_result_table_component__WEBPACK_IMPORTED_MODULE_33__["QueryResultTableComponent"],
        _uml_uml_diagram_drawing_uml_diagram_drawing_component__WEBPACK_IMPORTED_MODULE_24__["UmlDiagramDrawingComponent"], _uml_components_uml_class_edit_uml_class_edit_component__WEBPACK_IMPORTED_MODULE_25__["UmlClassEditComponent"], _uml_components_uml_assoc_edit_uml_assoc_edit_component__WEBPACK_IMPORTED_MODULE_26__["UmlAssocEditComponent"], _uml_uml_exercise_uml_exercise_component__WEBPACK_IMPORTED_MODULE_18__["UmlExerciseComponent"], _uml_uml_class_selection_uml_class_selection_component__WEBPACK_IMPORTED_MODULE_20__["UmlClassSelectionComponent"],
        _uml_components_uml_diag_drawing_correction_uml_diag_drawing_correction_component__WEBPACK_IMPORTED_MODULE_28__["UmlDiagDrawingCorrectionComponent"], _uml_uml_member_allocation_uml_member_allocation_component__WEBPACK_IMPORTED_MODULE_29__["UmlMemberAllocationComponent"],
        _web_web_exercise_web_exercise_component__WEBPACK_IMPORTED_MODULE_19__["WebExerciseComponent"], _web_components_html_task_result_html_task_result_component__WEBPACK_IMPORTED_MODULE_36__["HtmlTaskResultComponent"], _web_components_html_attribute_result_html_attribute_result_component__WEBPACK_IMPORTED_MODULE_37__["HtmlAttributeResultComponent"],
        _xml_xml_exercise_xml_exercise_component__WEBPACK_IMPORTED_MODULE_22__["XmlExerciseComponent"], _xml_components_xml_document_correction_xml_document_correction_component__WEBPACK_IMPORTED_MODULE_30__["XmlDocumentCorrectionComponent"], _uml_components_uml_impl_result_uml_impl_result_component__WEBPACK_IMPORTED_MODULE_43__["UmlImplResultComponent"], _uml_components_uml_impl_result_uml_impl_result_component__WEBPACK_IMPORTED_MODULE_43__["UmlImplResultComponent"], _xml_components_xml_element_line_match_result_xml_element_line_match_result_component__WEBPACK_IMPORTED_MODULE_44__["XmlElementLineMatchResultComponent"], _uml_components_uml_assoc_match_result_uml_assoc_match_result_component__WEBPACK_IMPORTED_MODULE_45__["UmlAssocMatchResultComponent"]], imports: [_angular_platform_browser__WEBPACK_IMPORTED_MODULE_46__["BrowserModule"],
        _angular_common__WEBPACK_IMPORTED_MODULE_0__["CommonModule"],
        _angular_forms__WEBPACK_IMPORTED_MODULE_8__["FormsModule"],
        _ctrl_ngx_codemirror__WEBPACK_IMPORTED_MODULE_12__["CodemirrorModule"],
        _shared_shared_module__WEBPACK_IMPORTED_MODULE_11__["SharedModule"],
        _collection_tools_routing__WEBPACK_IMPORTED_MODULE_7__["CollectionToolRoutingModule"]] }); })();


/***/ }),

/***/ "1GxF":
/*!**********************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/sql/_results/sql-matching-result/sql-matching-result.component.ts ***!
  \**********************************************************************************************************/
/*! exports provided: SqlMatchingResultComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SqlMatchingResultComponent", function() { return SqlMatchingResultComponent; });
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common */ "ofXK");



function SqlMatchingResultComponent_div_2_li_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "li");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const match_r4 = ctx.$implicit;
    const ctx_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵclassMap"](ctx_r1.getCssClassForMatchType(match_r4.matchType));
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" Die Angabe ", ctx_r1.matchSingularName, " ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](ctx_r1.getArgDescription(match_r4));
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" ", ctx_r1.getTextForMatchType(match_r4.matchType), ". ");
} }
function SqlMatchingResultComponent_div_2_li_3_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "li", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](4, " ist falsch. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const notMatchedForUser_r5 = ctx.$implicit;
    const ctx_r2 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" Die Angabe ", ctx_r2.matchSingularName, " ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](notMatchedForUser_r5);
} }
function SqlMatchingResultComponent_div_2_li_4_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "li", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](4, " fehlt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const notMatchedForSample_r6 = ctx.$implicit;
    const ctx_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" Die Angabe ", ctx_r3.matchSingularName, " ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](notMatchedForSample_r6);
} }
function SqlMatchingResultComponent_div_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "ul");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](2, SqlMatchingResultComponent_div_2_li_2_Template, 5, 5, "li", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](3, SqlMatchingResultComponent_div_2_li_3_Template, 5, 2, "li", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](4, SqlMatchingResultComponent_div_2_li_4_Template, 5, 2, "li", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx_r0.matchingResult.allMatches);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx_r0.matchingResult.notMatchedForUser);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx_r0.matchingResult.notMatchedForSample);
} }
class SqlMatchingResultComponent {
    ngOnChanges(changes) {
        this.successful = this.matchingResult.allMatches
            .every((m) => m.matchType === _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["MatchType"].SuccessfulMatch);
    }
    getCssClassForMatchType(matchType) {
        return (matchType === _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["MatchType"].SuccessfulMatch) ? 'has-text-success' : 'has-text-danger';
    }
    getArgDescription(match) {
        return match.userArg;
    }
    getTextForMatchType(matchType) {
        return matchType === _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["MatchType"].SuccessfulMatch ? 'ist korrekt' : 'ist nicht komplett richtig.';
    }
}
SqlMatchingResultComponent.ɵfac = function SqlMatchingResultComponent_Factory(t) { return new (t || SqlMatchingResultComponent)(); };
SqlMatchingResultComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineComponent"]({ type: SqlMatchingResultComponent, selectors: [["it4all-sql-matching-result"]], inputs: { matchName: "matchName", matchSingularName: "matchSingularName", matchingResult: "matchingResult" }, features: [_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵNgOnChangesFeature"]], decls: 3, vars: 4, consts: [[3, "ngClass"], ["class", "content", 4, "ngIf"], [1, "content"], [3, "class", 4, "ngFor", "ngForOf"], ["class", "has-text-danger", 4, "ngFor", "ngForOf"], [1, "has-text-danger"]], template: function SqlMatchingResultComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](2, SqlMatchingResultComponent_div_2_Template, 5, 3, "div", 1);
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngClass", ctx.successful ? "has-text-success" : "has-text-danger");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate2"](" Der Vergleich der ", ctx.matchName, " war ", ctx.successful ? "" : "nicht", " erfolgreich. ");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", !ctx.successful);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_2__["NgClass"], _angular_common__WEBPACK_IMPORTED_MODULE_2__["NgIf"], _angular_common__WEBPACK_IMPORTED_MODULE_2__["NgForOf"]], encapsulation: 2 });


/***/ }),

/***/ "1UiD":
/*!*******************************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/uml/_components/uml-assoc-match-result/uml-assoc-match-result.component.ts ***!
  \*******************************************************************************************************************/
/*! exports provided: UmlAssocMatchResultComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlAssocMatchResultComponent", function() { return UmlAssocMatchResultComponent; });
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common */ "ofXK");



function UmlAssocMatchResultComponent_li_1_div_9_ng_container_12_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r5 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"]("Erwartet wurde ", ctx_r5.correctCardinalities, "");
} }
function UmlAssocMatchResultComponent_li_1_div_9_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "ul");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "li", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3, " Der Typ der Assoziation ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](4, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](6);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](7, "li", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](8, " Die Kardinalit\u00E4ten der Assoziation ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](9, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](10);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](11);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](12, UmlAssocMatchResultComponent_li_1_div_9_ng_container_12_Template, 2, 1, "ng-container", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const m_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]().$implicit;
    const ctx_r4 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngClass", m_r3.analysisResult.assocTypeEqual ? "has-text-dark-success" : "has-text-danger");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](m_r3.userArg.assocType);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" war ", m_r3.analysisResult.assocTypeEqual ? "" : "nicht", " korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngClass", m_r3.analysisResult.multiplicitiesEqual ? "has-text-dark-success" : "has-text-danger");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](ctx_r4.gottenMultiplicities(m_r3));
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" waren ", m_r3.analysisResult.multiplicitiesEqual ? "" : "nicht", " korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", !m_r3.analysisResult.multiplicitiesEqual);
} }
function UmlAssocMatchResultComponent_li_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "li");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "div", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](2, " Die Assoziation von ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](3, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](5, " nach ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](6, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](7);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](8);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](9, UmlAssocMatchResultComponent_li_1_div_9_Template, 13, 7, "div", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const m_r3 = ctx.$implicit;
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngClass", ctx_r0.isCorrect(m_r3) ? "has-text-dark-success" : "has-text-danger");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](m_r3.userArg.firstEnd);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](m_r3.userArg.secondEnd);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" ist ", ctx_r0.isCorrect(m_r3) ? "" : "nicht", " korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", !ctx_r0.isCorrect);
} }
function UmlAssocMatchResultComponent_li_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "li", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1, " Die Assoziation von ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](4, " nach ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](5, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](6);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](7, " ist falsch. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const mu_r7 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](mu_r7.firstEnd);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](mu_r7.secondEnd);
} }
function UmlAssocMatchResultComponent_li_3_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "li", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1, " Die Assoziation von ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](4, " nach ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](5, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](6);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](7, " fehlt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ms_r8 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](ms_r8.firstEnd);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](ms_r8.secondEnd);
} }
function printCardinality(c) {
    return c === _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["UmlMultiplicity"].Unbound ? '*' : '1';
}
class UmlAssocMatchResultComponent {
    isCorrect(m) {
        return m.matchType === _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["MatchType"].SuccessfulMatch;
    }
    gottenMultiplicities(m) {
        return `${printCardinality(m.userArg.firstMult)} : ${printCardinality(m.userArg.secondMult)}`;
    }
    correctCardinalities(m) {
        if (m.userArg.firstEnd === m.sampleArg.firstEnd) {
            return printCardinality(m.sampleArg.firstMult) + ' : ' + printCardinality(m.sampleArg.secondMult);
        }
        else {
            return printCardinality(m.sampleArg.secondMult) + ' : ' + printCardinality(m.sampleArg.firstMult);
        }
    }
}
UmlAssocMatchResultComponent.ɵfac = function UmlAssocMatchResultComponent_Factory(t) { return new (t || UmlAssocMatchResultComponent)(); };
UmlAssocMatchResultComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineComponent"]({ type: UmlAssocMatchResultComponent, selectors: [["it4all-uml-assoc-match-result"]], inputs: { assocResult: "assocResult" }, decls: 4, vars: 3, consts: [[4, "ngFor", "ngForOf"], ["class", "has-text-danger", 4, "ngFor", "ngForOf"], [3, "ngClass"], ["class", "content", 4, "ngIf"], [1, "content"], [4, "ngIf"], [1, "has-text-danger"]], template: function UmlAssocMatchResultComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "ul");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](1, UmlAssocMatchResultComponent_li_1_Template, 10, 5, "li", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](2, UmlAssocMatchResultComponent_li_2_Template, 8, 2, "li", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](3, UmlAssocMatchResultComponent_li_3_Template, 8, 2, "li", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx.assocResult.allMatches);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx.assocResult.notMatchedForUser);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx.assocResult.notMatchedForSample);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_2__["NgForOf"], _angular_common__WEBPACK_IMPORTED_MODULE_2__["NgClass"], _angular_common__WEBPACK_IMPORTED_MODULE_2__["NgIf"]], encapsulation: 2 });


/***/ }),

/***/ "1kDo":
/*!*********************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/lessons/lesson-as-video/lesson-as-video.component.ts ***!
  \*********************************************************************************************/
/*! exports provided: LessonAsVideoComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LessonAsVideoComponent", function() { return LessonAsVideoComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_platform_browser__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/platform-browser */ "jhN1");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/common */ "ofXK");





function LessonAsVideoComponent_figure_3_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "figure", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](1, "iframe", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("src", ctx_r0.sanitizeUrl(ctx_r0.lessonAsVideoQuery.me.tool.lesson.video), _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵsanitizeResourceUrl"]);
} }
class LessonAsVideoComponent {
    constructor(route, lessonAsVideoGQL, domSanitizer) {
        this.route = route;
        this.lessonAsVideoGQL = lessonAsVideoGQL;
        this.domSanitizer = domSanitizer;
    }
    ngOnInit() {
        this.sub = this.route.paramMap.subscribe((paramMap) => {
            const toolId = paramMap.get('toolId');
            const lessonId = parseInt(paramMap.get('lessonId'), 10);
            this.lessonAsVideoGQL
                .watch({ toolId, lessonId })
                .valueChanges
                .subscribe(({ data }) => this.lessonAsVideoQuery = data);
        });
    }
    ngOnDestroy() {
        this.sub.unsubscribe();
    }
    sanitizeUrl(videoUrl) {
        return this.domSanitizer.bypassSecurityTrustResourceUrl(videoUrl);
    }
}
LessonAsVideoComponent.ɵfac = function LessonAsVideoComponent_Factory(t) { return new (t || LessonAsVideoComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_angular_router__WEBPACK_IMPORTED_MODULE_1__["ActivatedRoute"]), _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_services_apollo_services__WEBPACK_IMPORTED_MODULE_2__["LessonAsVideoGQL"]), _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_angular_platform_browser__WEBPACK_IMPORTED_MODULE_3__["DomSanitizer"])); };
LessonAsVideoComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: LessonAsVideoComponent, selectors: [["it4all-lesson-as-video"]], decls: 4, vars: 2, consts: [[1, "container"], [1, "title", "is-3", "has-text-centered"], ["class", "image is-16by9", 4, "ngIf"], [1, "image", "is-16by9"], ["frameborder", "0", "width", "560", "height", "315", "allow", "accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture", "allowfullscreen", "", 1, "has-ratio", 3, "src"]], template: function LessonAsVideoComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "h1", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](3, LessonAsVideoComponent_figure_3_Template, 2, 1, "figure", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx.lessonAsVideoQuery.me.tool.lesson.title);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx.lessonAsVideoQuery.me.tool.lesson.video);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_4__["NgIf"]], encapsulation: 2 });


/***/ }),

/***/ "2X8u":
/*!******************************************************************************!*\
  !*** ./src/app/tools/random-tools/bool/bool-create/bool-create.component.ts ***!
  \******************************************************************************/
/*! exports provided: BoolCreateComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BoolCreateComponent", function() { return BoolCreateComponent; });
/* harmony import */ var _model_boolean_formula_parser__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../_model/boolean-formula-parser */ "+GYJ");
/* harmony import */ var _model_bool_component_helper__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../_model/bool-component-helper */ "vWq+");
/* harmony import */ var _model_bool_node__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../_model/bool-node */ "oi/H");
/* harmony import */ var _model_bool_formula__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../_model/bool-formula */ "tTUD");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! @angular/forms */ "3Pt+");
/* harmony import */ var _components_random_solve_buttons_random_solve_buttons_component__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../_components/random-solve-buttons/random-solve-buttons.component */ "5Adw");
/* harmony import */ var _bool_create_instructions_bool_create_instructions_component__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./bool-create-instructions/bool-create-instructions.component */ "ubCX");









function BoolCreateComponent_th_5_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "th", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const variable_r8 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate"](variable_r8.variable);
} }
function BoolCreateComponent_tr_11_td_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "td", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const variable_r13 = ctx.$implicit;
    const assignment_r9 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"]().$implicit;
    const ctx_r10 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate"](ctx_r10.displayAssignmentValue(assignment_r9, variable_r13));
} }
function BoolCreateComponent_tr_11_span_6_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "span", 21);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](1, "\u2718");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} }
function BoolCreateComponent_tr_11_span_7_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "span", 21);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](1, "\u2714");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} }
function BoolCreateComponent_tr_11_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "tr");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](1, BoolCreateComponent_tr_11_td_1_Template, 2, 1, "td", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](2, "td", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](4, "td", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](6, BoolCreateComponent_tr_11_span_6_Template, 2, 0, "span", 20);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](7, BoolCreateComponent_tr_11_span_7_Template, 2, 0, "span", 20);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const assignment_r9 = ctx.$implicit;
    const ctx_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngForOf", ctx_r1.formula.getVariables());
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate"](ctx_r1.displayAssignmentValue(assignment_r9, ctx_r1.sampleVariable));
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵclassProp"]("has-background-danger", ctx_r1.corrected && !ctx_r1.isCorrect(assignment_r9))("has-background-success", ctx_r1.corrected && ctx_r1.isCorrect(assignment_r9))("has-text-white", ctx_r1.corrected);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate1"](" ", ctx_r1.displayAssignmentValue(assignment_r9, ctx_r1.learnerVariable), " \u00A0 ");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx_r1.corrected && !ctx_r1.isCorrect(assignment_r9));
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx_r1.corrected && ctx_r1.isCorrect(assignment_r9));
} }
function BoolCreateComponent_div_19_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "div", 22);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](1, "code", 23);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r2 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("innerHTML", "y = " + ctx_r2.oldSolution.asHtmlString(), _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵsanitizeHtml"]);
} }
function BoolCreateComponent_div_20_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "div", 24);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](1, " \u2757 Ihre Formel konnte nicht geparst werden! ");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} }
function BoolCreateComponent_div_21_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "div", 24);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](1, " \u2718 Die Formel ist nicht korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} }
function BoolCreateComponent_div_22_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "div", 25);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](1, " \u2714 Die Formel ist korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} }
function BoolCreateComponent_div_33_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "div", 26);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](1, "header", 27);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](2, "p", 28);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](3, "Musterl\u00F6sungen");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](4, "div", 29);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](5, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](6);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r6 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](6);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate"](ctx_r6.formula.asString());
} }
function BoolCreateComponent_it4all_bool_create_instructions_34_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](0, "it4all-bool-create-instructions");
} }
class BoolCreateComponent extends _model_bool_component_helper__WEBPACK_IMPORTED_MODULE_1__["BoolComponentHelper"] {
    constructor() {
        super(...arguments);
        this.solution = '';
        this.formulaParsed = false;
        this.showInstructions = false;
        this.showSampleSolutions = false;
    }
    ngOnInit() {
        this.update();
    }
    update() {
        this.showSampleSolutions = false;
        this.showInstructions = false;
        this.completelyCorrect = false;
        this.formulaParsed = false;
        this.corrected = false;
        this.oldSolution = undefined;
        this.solution = '';
        this.formula = Object(_model_bool_formula__WEBPACK_IMPORTED_MODULE_3__["generateBooleanFormula"])(this.sampleVariable);
        this.assignments = Object(_model_bool_node__WEBPACK_IMPORTED_MODULE_2__["calculateAssignments"])(this.formula.getVariables());
        this.assignments.forEach((assignment) => assignment.set(this.sampleVariable.variable, this.formula.evaluate(assignment)));
    }
    correct() {
        this.corrected = true;
        const booleanFormula = Object(_model_boolean_formula_parser__WEBPACK_IMPORTED_MODULE_0__["parseBooleanFormula"])(this.solution);
        if (!booleanFormula) {
            alert('Konnte Formel >>' + this.solution + '<< nicht parsen!');
            return;
        }
        this.oldSolution = booleanFormula;
        this.formulaParsed = true;
        // check contained variables!
        const variablesAllowed = this.formula.getVariables().map((v) => v.variable);
        const variablesUsed = booleanFormula.getVariables().map((v) => v.variable);
        const illegalVariables = variablesUsed.filter((v) => !variablesAllowed.includes(v));
        if (illegalVariables.length > 0) {
            alert('Sie haben die falschen Variablen ' + illegalVariables + ' benutzt!');
            return;
        }
        this.completelyCorrect = this.assignments
            .map((assignment) => {
            const value = booleanFormula.evaluate(assignment);
            assignment.set(this.learnerVariable.variable, value);
            return assignment.get(this.sampleVariable.variable) === value;
        })
            .every((a) => a);
    }
    handleKeyboardEvent(event) {
        if (event.key === 'Enter') {
            if (this.completelyCorrect) {
                this.update();
            }
            else {
                this.correct();
            }
        }
    }
}
BoolCreateComponent.ɵfac = function BoolCreateComponent_Factory(t) { return ɵBoolCreateComponent_BaseFactory(t || BoolCreateComponent); };
BoolCreateComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵdefineComponent"]({ type: BoolCreateComponent, selectors: [["it4all-bool-create"]], hostBindings: function BoolCreateComponent_HostBindings(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵlistener"]("keypress", function BoolCreateComponent_keypress_HostBindingHandler($event) { return ctx.handleKeyboardEvent($event); }, false, _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵresolveDocument"]);
    } }, features: [_angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵInheritDefinitionFeature"]], decls: 35, vars: 12, consts: [[1, "columns"], [1, "column", "is-half-desktop", "is-offset-one-quarter-desktop"], [1, "table", "is-bordered", "is-fullwidth"], ["class", "has-text-centered", 4, "ngFor", "ngForOf"], [1, "has-text-centered"], [4, "ngFor", "ngForOf"], [1, "field", "has-addons"], [1, "control"], ["for", "solution", 1, "button", "is-static"], [1, "control", "is-expanded"], ["type", "text", "id", "solution", "autocomplete", "off", "placeholder", "Boolesche Formel", "autofocus", "", 1, "input", 3, "ngModel", "ngModelChange"], ["class", "notification has-text-centered", 4, "ngIf"], ["class", "notification has-text-centered is-danger", 4, "ngIf"], ["class", "notification has-text-centered is-success", 4, "ngIf"], [3, "correctEmitter", "nextEmitter"], [1, "column", "is-half-desktop"], [1, "button", "is-primary", "is-fullwidth", 3, "click"], [1, "button", "is-info", "is-fullwidth", 3, "click"], ["class", "card", 4, "ngIf"], [4, "ngIf"], ["class", "has-text-white", 4, "ngIf"], [1, "has-text-white"], [1, "notification", "has-text-centered"], [3, "innerHTML"], [1, "notification", "has-text-centered", "is-danger"], [1, "notification", "has-text-centered", "is-success"], [1, "card"], [1, "card-header"], [1, "card-header-title", "is-centered"], [1, "card-content"]], template: function BoolCreateComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](1, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](2, "table", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](3, "thead");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](4, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](5, BoolCreateComponent_th_5_Template, 2, 1, "th", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](6, "th", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](7);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](8, "th", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](9);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](10, "tbody");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](11, BoolCreateComponent_tr_11_Template, 8, 11, "tr", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](12, "hr");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](13, "div", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](14, "div", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](15, "label", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](16);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](17, "div", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](18, "input", 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵlistener"]("ngModelChange", function BoolCreateComponent_Template_input_ngModelChange_18_listener($event) { return ctx.solution = $event; });
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](19, BoolCreateComponent_div_19_Template, 2, 1, "div", 11);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](20, BoolCreateComponent_div_20_Template, 2, 0, "div", 12);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](21, BoolCreateComponent_div_21_Template, 2, 0, "div", 12);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](22, BoolCreateComponent_div_22_Template, 2, 0, "div", 13);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](23, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](24, "it4all-random-solve-buttons", 14);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵlistener"]("correctEmitter", function BoolCreateComponent_Template_it4all_random_solve_buttons_correctEmitter_24_listener() { return ctx.correct(); })("nextEmitter", function BoolCreateComponent_Template_it4all_random_solve_buttons_nextEmitter_24_listener() { return ctx.update(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](25, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](26, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](27, "div", 15);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](28, "button", 16);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵlistener"]("click", function BoolCreateComponent_Template_button_click_28_listener() { return ctx.showSampleSolutions = !ctx.showSampleSolutions; });
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](29, " Musterl\u00F6sungen anzeigen ");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](30, "div", 15);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](31, "button", 17);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵlistener"]("click", function BoolCreateComponent_Template_button_click_31_listener() { return ctx.showInstructions = !ctx.showInstructions; });
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](32, " Hilfe anzeigen ");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](33, BoolCreateComponent_div_33_Template, 7, 1, "div", 18);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](34, BoolCreateComponent_it4all_bool_create_instructions_34_Template, 1, 0, "it4all-bool-create-instructions", 19);
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](5);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngForOf", ctx.formula.getVariables());
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate"](ctx.sampleVariable.variable);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate"](ctx.learnerVariable.variable);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngForOf", ctx.assignments);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](5);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate1"]("", ctx.sampleVariable.variable, " = ");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngModel", ctx.solution);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx.oldSolution);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx.corrected && !ctx.formulaParsed);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx.corrected && ctx.formulaParsed && !ctx.completelyCorrect);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx.corrected && ctx.formulaParsed && ctx.completelyCorrect);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](11);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx.showSampleSolutions);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx.showInstructions);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_5__["NgForOf"], _angular_forms__WEBPACK_IMPORTED_MODULE_6__["DefaultValueAccessor"], _angular_forms__WEBPACK_IMPORTED_MODULE_6__["NgControlStatus"], _angular_forms__WEBPACK_IMPORTED_MODULE_6__["NgModel"], _angular_common__WEBPACK_IMPORTED_MODULE_5__["NgIf"], _components_random_solve_buttons_random_solve_buttons_component__WEBPACK_IMPORTED_MODULE_7__["RandomSolveButtonsComponent"], _bool_create_instructions_bool_create_instructions_component__WEBPACK_IMPORTED_MODULE_8__["BoolCreateInstructionsComponent"]], encapsulation: 2 });
const ɵBoolCreateComponent_BaseFactory = /*@__PURE__*/ _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵgetInheritedFactory"](BoolCreateComponent);


/***/ }),

/***/ "2zuv":
/*!********************************************************************************!*\
  !*** ./src/app/tools/random-tools/bool/bool-drawing/bool-drawing.component.ts ***!
  \********************************************************************************/
/*! exports provided: BoolDrawingComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BoolDrawingComponent", function() { return BoolDrawingComponent; });
/* harmony import */ var _bool_drawing_model_boolDrawing__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./_bool-drawing-model/boolDrawing */ "Bd0U");
/* harmony import */ var _bool_drawing_model_formulaExtractor__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./_bool-drawing-model/formulaExtractor */ "Dja1");
/* harmony import */ var _model_bool_node__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../_model/bool-node */ "oi/H");
/* harmony import */ var jointjs__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! jointjs */ "iuCI");
/* harmony import */ var _model_bool_formula__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../_model/bool-formula */ "tTUD");
/* harmony import */ var _model_bool_component_helper__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../_model/bool-component-helper */ "vWq+");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! @angular/common */ "ofXK");








function BoolDrawingComponent_div_6_Template(rf, ctx) { if (rf & 1) {
    const _r5 = _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵgetCurrentView"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementStart"](0, "div", 11);
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementStart"](1, "button", 12);
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵlistener"]("click", function BoolDrawingComponent_div_6_Template_button_click_1_listener($event) { _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵrestoreView"](_r5); const gateType_r3 = ctx.$implicit; const ctx_r4 = _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵnextContext"](); return ctx_r4.toggleGateButton($event, gateType_r3); })("dragstart", function BoolDrawingComponent_div_6_Template_button_dragstart_1_listener($event) { _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵrestoreView"](_r5); const gateType_r3 = ctx.$implicit; const ctx_r6 = _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵnextContext"](); return ctx_r6.gateButtonOnDragstart($event, gateType_r3); });
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementEnd"]();
} if (rf & 2) {
    const gateType_r3 = ctx.$implicit;
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵclassProp"]("is-primary", ctx_r0.elementToCreate === gateType_r3);
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵtextInterpolate1"](" ", gateType_r3, " ");
} }
function BoolDrawingComponent_th_11_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementStart"](0, "th", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementEnd"]();
} if (rf & 2) {
    const variable_r7 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵtextInterpolate"](variable_r7.variable);
} }
function BoolDrawingComponent_tr_15_td_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementStart"](0, "td", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementEnd"]();
} if (rf & 2) {
    const variable_r10 = ctx.$implicit;
    const assignment_r8 = _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵnextContext"]().$implicit;
    const ctx_r9 = _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵtextInterpolate1"](" ", ctx_r9.displayAssignmentValue(assignment_r8, variable_r10), " ");
} }
function BoolDrawingComponent_tr_15_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementStart"](0, "tr");
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵtemplate"](1, BoolDrawingComponent_tr_15_td_1_Template, 2, 1, "td", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementStart"](2, "td", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementEnd"]();
} if (rf & 2) {
    const assignment_r8 = ctx.$implicit;
    const ctx_r2 = _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵproperty"]("ngForOf", ctx_r2.formula.getVariables());
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵtextInterpolate"](ctx_r2.formula.getValueFor(assignment_r8) ? "1" : "0");
} }
class BoolDrawingComponent extends _model_bool_component_helper__WEBPACK_IMPORTED_MODULE_5__["BoolComponentHelper"] {
    constructor() {
        super(...arguments);
        this.gateTypes = ['not', 'and', 'nand', 'or', 'nor', 'xor', 'xnor' /*, 'equiv', 'impl'*/];
        this.elementToCreate = undefined;
    }
    createElement(elementName, x, y) {
        Object(_bool_drawing_model_boolDrawing__WEBPACK_IMPORTED_MODULE_0__["createElementInGraph"])(elementName, x, y);
        this.elementToCreate = undefined;
    }
    initPaperEvents() {
        _bool_drawing_model_boolDrawing__WEBPACK_IMPORTED_MODULE_0__["paper"].on('blank:pointerclick', (evt, x, y) => {
            if (this.elementToCreate) {
                this.createElement('element' + this.elementToCreate.toUpperCase(), x, y);
            }
        });
        _bool_drawing_model_boolDrawing__WEBPACK_IMPORTED_MODULE_0__["paper"].on('cell:pointerclick', (cellView) => {
            // Left click on cell
            const inputCell = cellView.model;
            if (inputCell instanceof jointjs__WEBPACK_IMPORTED_MODULE_3__["shapes"].logic.Input) {
                const logicSymbol = inputCell.attr('logicSymbol');
                const newSignal = !_bool_drawing_model_boolDrawing__WEBPACK_IMPORTED_MODULE_0__["SIGNALS"].get(logicSymbol);
                _bool_drawing_model_boolDrawing__WEBPACK_IMPORTED_MODULE_0__["SIGNALS"].set(logicSymbol, newSignal);
                Object(_bool_drawing_model_boolDrawing__WEBPACK_IMPORTED_MODULE_0__["toggleLive"])(inputCell, newSignal);
                Object(_bool_drawing_model_boolDrawing__WEBPACK_IMPORTED_MODULE_0__["broadcastSignal"])(inputCell, newSignal);
            }
        });
        _bool_drawing_model_boolDrawing__WEBPACK_IMPORTED_MODULE_0__["paper"].on('cell:contextmenu', (evt) => {
            // Right click on cell
            if (evt.model instanceof jointjs__WEBPACK_IMPORTED_MODULE_3__["shapes"].logic.IO) {
                alert('You cannot delete in Input or an Output!');
            }
            else {
                _bool_drawing_model_boolDrawing__WEBPACK_IMPORTED_MODULE_0__["graph"].getCell(evt.model.id).remove();
            }
        });
    }
    ngOnInit() {
        this.update();
        Object(_bool_drawing_model_boolDrawing__WEBPACK_IMPORTED_MODULE_0__["draw"])(this.formula);
        this.initPaperEvents();
    }
    update() {
        this.formula = Object(_model_bool_formula__WEBPACK_IMPORTED_MODULE_4__["generateBooleanFormula"])(this.sampleVariable);
        this.assignments = Object(_model_bool_node__WEBPACK_IMPORTED_MODULE_2__["calculateAssignments"])(this.formula.getVariables());
    }
    toggleGateButton(event, gateType) {
        this.elementToCreate = this.elementToCreate === gateType ? undefined : gateType;
    }
    gateButtonOnDragstart(event, item) {
        event.dataTransfer.setData('text', item);
    }
    paperOnDragover(event) {
        event.preventDefault();
    }
    paperOnDrop(event) {
        event.preventDefault();
        const elementToCreate = event.dataTransfer.getData('text');
        console.error(elementToCreate);
        // scale: Coordinates offset by graph scale, correct with factor
        const scale = jointjs__WEBPACK_IMPORTED_MODULE_3__["V"](_bool_drawing_model_boolDrawing__WEBPACK_IMPORTED_MODULE_0__["paper"].viewport).scale();
        this.createElement('element' + elementToCreate.toUpperCase(), _bool_drawing_model_boolDrawing__WEBPACK_IMPORTED_MODULE_0__["dragX"] / scale.sx, _bool_drawing_model_boolDrawing__WEBPACK_IMPORTED_MODULE_0__["dragY"] / scale.sy);
    }
    updateCreatedFormula() {
        const formulas = Object(_bool_drawing_model_formulaExtractor__WEBPACK_IMPORTED_MODULE_1__["extractFormulaFromGraph"])(_bool_drawing_model_boolDrawing__WEBPACK_IMPORTED_MODULE_0__["graph"]);
        formulas.forEach((outputFormula) => {
            if (outputFormula[1]) {
                // tslint:disable-next-line:no-console
                console.info(outputFormula[0].variable + ' = ' + outputFormula[1].asString());
            }
            else {
                // tslint:disable-next-line:no-console
                console.info(outputFormula[0].variable + ' = undefined!');
            }
        });
    }
}
BoolDrawingComponent.ɵfac = function BoolDrawingComponent_Factory(t) { return ɵBoolDrawingComponent_BaseFactory(t || BoolDrawingComponent); };
BoolDrawingComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵdefineComponent"]({ type: BoolDrawingComponent, selectors: [["ng-component"]], features: [_angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵInheritDefinitionFeature"]], decls: 18, vars: 4, consts: [[1, "container", "is-fluid"], [1, "columns"], [1, "column", "is-two-thirds-desktop"], ["id", "paper", 3, "dragover", "drop"], ["class", "column", 4, "ngFor", "ngForOf"], [1, "column", "is-one-third-desktop"], [1, "table", "is-bordered", "is-striped", "is-fullwidth"], ["class", "has-text-centered", 4, "ngFor", "ngForOf"], [1, "has-text-centered"], [4, "ngFor", "ngForOf"], [1, "button", "is-link", "is-fullwidth", 3, "click"], [1, "column"], ["draggable", "true", 1, "button", "is-fullwidth", 3, "click", "dragstart"]], template: function BoolDrawingComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementStart"](1, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementStart"](2, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementStart"](3, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵlistener"]("dragover", function BoolDrawingComponent_Template_div_dragover_3_listener($event) { return ctx.paperOnDragover($event); })("drop", function BoolDrawingComponent_Template_div_drop_3_listener($event) { return ctx.paperOnDrop($event); });
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelement"](4, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementStart"](5, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵtemplate"](6, BoolDrawingComponent_div_6_Template, 3, 3, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementStart"](7, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementStart"](8, "table", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementStart"](9, "thead");
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementStart"](10, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵtemplate"](11, BoolDrawingComponent_th_11_Template, 2, 1, "th", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementStart"](12, "th", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵtext"](13);
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementStart"](14, "tbody");
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵtemplate"](15, BoolDrawingComponent_tr_15_Template, 4, 2, "tr", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementStart"](16, "button", 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵlistener"]("click", function BoolDrawingComponent_Template_button_click_16_listener() { return ctx.updateCreatedFormula(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵtext"](17, "Formel aktualisieren");
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵadvance"](6);
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵproperty"]("ngForOf", ctx.gateTypes);
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵadvance"](5);
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵproperty"]("ngForOf", ctx.formula.getVariables());
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵtextInterpolate"](ctx.formula.left.variable);
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵproperty"]("ngForOf", ctx.formula.getAssignments());
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_7__["NgForOf"]], styles: ["\n    #paper {\n      border: 1px solid grey;\n    }\n  "], encapsulation: 2 });
const ɵBoolDrawingComponent_BaseFactory = /*@__PURE__*/ _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵgetInheritedFactory"](BoolDrawingComponent);


/***/ }),

/***/ "346A":
/*!*************************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/_components/exercise-files-editor/exercise-files-editor.component.ts ***!
  \*************************************************************************************************************/
/*! exports provided: ExerciseFilesEditorComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExerciseFilesEditorComponent", function() { return ExerciseFilesEditorComponent; });
/* harmony import */ var _collection_tool_helpers__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../collection-tool-helpers */ "VRMR");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _ctrl_ngx_codemirror__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @ctrl/ngx-codemirror */ "Xl2X");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/forms */ "3Pt+");





function ExerciseFilesEditorComponent_li_2_Template(rf, ctx) { if (rf & 1) {
    const _r3 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵgetCurrentView"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "li", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵlistener"]("click", function ExerciseFilesEditorComponent_li_2_Template_li_click_0_listener($event) { _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵrestoreView"](_r3); const ctx_r2 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](); return ctx_r2.changeFile($event); });
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "a", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const file_r1 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵclassProp"]("is-active", file_r1.active);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵclassProp"]("has-text-grey-light", !file_r1.file.editable);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("title", file_r1.file.editable ? "" : "Nicht editierbar");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](file_r1.file.name);
} }
class ExerciseFilesEditorComponent {
    constructor() {
        this.currentFileName = undefined;
        this.editorOptions = Object(_collection_tool_helpers__WEBPACK_IMPORTED_MODULE_0__["getDefaultEditorOptions"])('');
        this.theContent = '';
    }
    get content() {
        return this.theContent;
    }
    /**
     * called only from CodeMirror editor
     */
    set content(newContent) {
        this.theContent = newContent;
        this.saveEditorContent();
    }
    saveEditorContent() {
        if (this.currentFileName) {
            this.activatableExerciseFiles.find((f) => f.file.name === this.currentFileName).file.content = this.content;
        }
    }
    ngOnChanges(changes) {
        this.activatableExerciseFiles = this.exerciseFileFragments.map((file) => {
            // Delete __typename field that was added by apollo
            delete file.__typename;
            return { file, active: false };
        });
        this.updateFirstFile();
    }
    updateFirstFile() {
        if (this.activatableExerciseFiles && this.activatableExerciseFiles.length > 0) {
            // console.log(this.currentFileName);
            if (!this.currentFileName) {
                const editableExerciseFile = this.activatableExerciseFiles.find((ef) => ef.file.editable);
                if (editableExerciseFile) {
                    this.updateEditor(editableExerciseFile, false);
                }
                else {
                    this.updateEditor(this.activatableExerciseFiles[0], false);
                }
            }
            else {
                const currentFile = this.activatableExerciseFiles.find((f) => f.file.name === this.currentFileName);
                this.updateEditor(currentFile, false);
            }
        }
    }
    updateEditor(exerciseFile, saveContent = true) {
        if (saveContent) {
            this.saveEditorContent();
        }
        exerciseFile.active = true;
        this.theContent = exerciseFile.file.content;
        this.currentFileName = exerciseFile.file.name;
        this.editorOptions.mode = exerciseFile.file.fileType;
        this.editorOptions.readOnly = !exerciseFile.file.editable;
    }
    changeFile($event) {
        const fileName = $event.target.textContent;
        // disable other files...
        this.activatableExerciseFiles.forEach((ef) => {
            ef.active = false;
        });
        const exerciseFile = this.activatableExerciseFiles.find((ef) => ef.file.name === fileName);
        if (exerciseFile) {
            this.updateEditor(exerciseFile);
        }
    }
}
ExerciseFilesEditorComponent.ɵfac = function ExerciseFilesEditorComponent_Factory(t) { return new (t || ExerciseFilesEditorComponent)(); };
ExerciseFilesEditorComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineComponent"]({ type: ExerciseFilesEditorComponent, selectors: [["it4all-exercise-files-editor"]], inputs: { exerciseFileFragments: "exerciseFileFragments", mode: "mode" }, features: [_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵNgOnChangesFeature"]], decls: 4, vars: 3, consts: [[1, "tabs", "is-centered"], [3, "is-active", "click", 4, "ngFor", "ngForOf"], [3, "options", "ngModel", "ngModelChange"], [3, "click"], [3, "title"]], template: function ExerciseFilesEditorComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "ul");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](2, ExerciseFilesEditorComponent_li_2_Template, 3, 6, "li", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](3, "ngx-codemirror", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵlistener"]("ngModelChange", function ExerciseFilesEditorComponent_Template_ngx_codemirror_ngModelChange_3_listener($event) { return ctx.content = $event; });
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx.activatableExerciseFiles);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("options", ctx.editorOptions)("ngModel", ctx.content);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_2__["NgForOf"], _ctrl_ngx_codemirror__WEBPACK_IMPORTED_MODULE_3__["CodemirrorComponent"], _angular_forms__WEBPACK_IMPORTED_MODULE_4__["NgControlStatus"], _angular_forms__WEBPACK_IMPORTED_MODULE_4__["NgModel"]], styles: [".CodeMirror {\n  border: 1px solid grey;\n  height: 700px;\n}\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi4uLy4uLy4uLy4uLy4uLy4uL2V4ZXJjaXNlLWZpbGVzLWVkaXRvci5jb21wb25lbnQuc2FzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtFQUNFLHNCQUFBO0VBQ0EsYUFBQTtBQUNGIiwiZmlsZSI6ImV4ZXJjaXNlLWZpbGVzLWVkaXRvci5jb21wb25lbnQuc2FzcyIsInNvdXJjZXNDb250ZW50IjpbIi5Db2RlTWlycm9yXG4gIGJvcmRlcjogMXB4IHNvbGlkIGdyZXlcbiAgaGVpZ2h0OiA3MDBweFxuIl19 */"], encapsulation: 2 });


/***/ }),

/***/ "3HtT":
/*!*************************************************************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/programming/_results/programming-normal-result/programming-implementation-correction-result.component.ts ***!
  \*************************************************************************************************************************************************/
/*! exports provided: ProgrammingImplementationCorrectionResultComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ProgrammingImplementationCorrectionResultComponent", function() { return ProgrammingImplementationCorrectionResultComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/common */ "ofXK");


class ProgrammingImplementationCorrectionResultComponent {
    get stderr() {
        return this.normalResult.stderr.join('\n');
    }
}
ProgrammingImplementationCorrectionResultComponent.ɵfac = function ProgrammingImplementationCorrectionResultComponent_Factory(t) { return new (t || ProgrammingImplementationCorrectionResultComponent)(); };
ProgrammingImplementationCorrectionResultComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: ProgrammingImplementationCorrectionResultComponent, selectors: [["it4all-programming-normal-result"]], inputs: { normalResult: "normalResult" }, decls: 3, vars: 2, consts: [[1, "notification", 3, "ngClass"]], template: function ProgrammingImplementationCorrectionResultComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "pre");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngClass", ctx.normalResult.successful ? "is-success" : "is-danger");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx.stderr);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_1__["NgClass"]], encapsulation: 2 });


/***/ }),

/***/ "4KHl":
/*!***********************************!*\
  !*** ./src/app/graphql.module.ts ***!
  \***********************************/
/*! exports provided: GraphQLModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "GraphQLModule", function() { return GraphQLModule; });
/* harmony import */ var apollo_angular__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! apollo-angular */ "/IUn");
/* harmony import */ var apollo_angular_http__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! apollo-angular/http */ "E21e");
/* harmony import */ var _apollo_client_core__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @apollo/client/core */ "ALmS");
/* harmony import */ var _angular_common_http__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/common/http */ "tk/3");
/* harmony import */ var _angular_platform_browser__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/platform-browser */ "jhN1");
/* harmony import */ var _apollo_client_link_context__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! @apollo/client/link/context */ "MWEN");
/* harmony import */ var _services_authentication_service__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./_services/authentication.service */ "pW6c");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! @angular/core */ "fXoL");








function createApollo(httpLink) {
    const auth = Object(_apollo_client_link_context__WEBPACK_IMPORTED_MODULE_5__["setContext"])(() => {
        const loggedInUser = Object(_services_authentication_service__WEBPACK_IMPORTED_MODULE_6__["getCurrentUser"])();
        if (loggedInUser === null) {
            return {};
        }
        else {
            return {
                headers: {
                    Authorization: loggedInUser.jwt
                }
            };
        }
    });
    return {
        link: _apollo_client_core__WEBPACK_IMPORTED_MODULE_2__["ApolloLink"].from([auth, httpLink.create({ uri: '/api/graphql' })]),
        cache: new _apollo_client_core__WEBPACK_IMPORTED_MODULE_2__["InMemoryCache"](),
        defaultOptions: {
            watchQuery: { fetchPolicy: 'no-cache' },
            query: { fetchPolicy: 'no-cache' },
            mutate: { fetchPolicy: 'no-cache' }
        }
    };
}
class GraphQLModule {
}
GraphQLModule.ɵfac = function GraphQLModule_Factory(t) { return new (t || GraphQLModule)(); };
GraphQLModule.ɵmod = _angular_core__WEBPACK_IMPORTED_MODULE_7__["ɵɵdefineNgModule"]({ type: GraphQLModule });
GraphQLModule.ɵinj = _angular_core__WEBPACK_IMPORTED_MODULE_7__["ɵɵdefineInjector"]({ providers: [
        {
            provide: apollo_angular__WEBPACK_IMPORTED_MODULE_0__["APOLLO_OPTIONS"],
            useFactory: createApollo,
            deps: [apollo_angular_http__WEBPACK_IMPORTED_MODULE_1__["HttpLink"]],
        },
    ], imports: [[_angular_platform_browser__WEBPACK_IMPORTED_MODULE_4__["BrowserModule"], _angular_common_http__WEBPACK_IMPORTED_MODULE_3__["HttpClientModule"]]] });
(function () { (typeof ngJitMode === "undefined" || ngJitMode) && _angular_core__WEBPACK_IMPORTED_MODULE_7__["ɵɵsetNgModuleScope"](GraphQLModule, { imports: [_angular_platform_browser__WEBPACK_IMPORTED_MODULE_4__["BrowserModule"], _angular_common_http__WEBPACK_IMPORTED_MODULE_3__["HttpClientModule"]] }); })();


/***/ }),

/***/ "4YYW":
/*!*********************************************!*\
  !*** ./src/app/shared/tab/tab.component.ts ***!
  \*********************************************/
/*! exports provided: TabComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "TabComponent", function() { return TabComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");

const _c0 = ["*"];
class TabComponent {
    constructor() {
        this.active = false;
        this.selectable = true;
    }
}
TabComponent.ɵfac = function TabComponent_Factory(t) { return new (t || TabComponent)(); };
TabComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: TabComponent, selectors: [["it4all-tab"]], inputs: { title: "title", active: "active", selectable: "selectable" }, ngContentSelectors: _c0, decls: 2, vars: 1, consts: [[3, "hidden"]], template: function TabComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵprojectionDef"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵprojection"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("hidden", !ctx.active);
    } }, encapsulation: 2 });


/***/ }),

/***/ "4di/":
/*!********************************************!*\
  !*** ./src/app/_services/dexie.service.ts ***!
  \********************************************/
/*! exports provided: DexieService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DexieService", function() { return DexieService; });
/* harmony import */ var dexie__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! dexie */ "Texg");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "fXoL");


class DexieService extends dexie__WEBPACK_IMPORTED_MODULE_0__["default"] {
    constructor() {
        super('it4all-client');
        this.version(1).stores({
            solutions: '[toolId+collId+exId+partId]',
        });
        this.solutions = this.table('solutions');
    }
    getSolution(exerciseFragment, partId) {
        return this.solutions.get([exerciseFragment.toolId, exerciseFragment.collectionId, exerciseFragment.exerciseId, partId]);
    }
    upsertSolution(exerciseFragment, partId, solution) {
        return this.solutions.put({
            exId: exerciseFragment.exerciseId,
            collId: exerciseFragment.collectionId,
            toolId: exerciseFragment.toolId,
            partId,
            solution
        });
    }
}
DexieService.ɵfac = function DexieService_Factory(t) { return new (t || DexieService)(); };
DexieService.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: DexieService, factory: DexieService.ɵfac, providedIn: 'root' });


/***/ }),

/***/ "4u5/":
/*!************************************************************!*\
  !*** ./src/app/tools/random-tools/random-tools.routing.ts ***!
  \************************************************************/
/*! exports provided: RandomToolsRoutingModule, randomToolRoutingComponents */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RandomToolsRoutingModule", function() { return RandomToolsRoutingModule; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "randomToolRoutingComponents", function() { return randomToolRoutingComponents; });
/* harmony import */ var _random_overview_random_overview_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./random-overview/random-overview.component */ "aO+D");
/* harmony import */ var _helpers_auth_guard__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../_helpers/auth-guard */ "T7p5");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _bool_bool_exercise_bool_exercise_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./bool/bool-exercise/bool-exercise.component */ "YH1L");
/* harmony import */ var _nary_nary_exercise_nary_exercise_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./nary/nary-exercise/nary-exercise.component */ "SZTG");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! @angular/core */ "fXoL");







const randomToolRoutes = [
    {
        path: 'randomTools', canActivate: [_helpers_auth_guard__WEBPACK_IMPORTED_MODULE_1__["AuthGuard"]], children: [
            { path: ':toolId', component: _random_overview_random_overview_component__WEBPACK_IMPORTED_MODULE_0__["RandomOverviewComponent"] },
            { path: 'bool/:part', component: _bool_bool_exercise_bool_exercise_component__WEBPACK_IMPORTED_MODULE_3__["BoolExerciseComponent"] },
            { path: 'nary/:part', component: _nary_nary_exercise_nary_exercise_component__WEBPACK_IMPORTED_MODULE_4__["NaryExerciseComponent"] },
        ]
    }
];
class RandomToolsRoutingModule {
}
RandomToolsRoutingModule.ɵfac = function RandomToolsRoutingModule_Factory(t) { return new (t || RandomToolsRoutingModule)(); };
RandomToolsRoutingModule.ɵmod = _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵdefineNgModule"]({ type: RandomToolsRoutingModule });
RandomToolsRoutingModule.ɵinj = _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵdefineInjector"]({ imports: [[_angular_router__WEBPACK_IMPORTED_MODULE_2__["RouterModule"].forChild(randomToolRoutes)], _angular_router__WEBPACK_IMPORTED_MODULE_2__["RouterModule"]] });
(function () { (typeof ngJitMode === "undefined" || ngJitMode) && _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵsetNgModuleScope"](RandomToolsRoutingModule, { imports: [_angular_router__WEBPACK_IMPORTED_MODULE_2__["RouterModule"]], exports: [_angular_router__WEBPACK_IMPORTED_MODULE_2__["RouterModule"]] }); })();
const randomToolRoutingComponents = [
    _random_overview_random_overview_component__WEBPACK_IMPORTED_MODULE_0__["RandomOverviewComponent"],
    _bool_bool_exercise_bool_exercise_component__WEBPACK_IMPORTED_MODULE_3__["BoolExerciseComponent"],
    _nary_nary_exercise_nary_exercise_component__WEBPACK_IMPORTED_MODULE_4__["NaryExerciseComponent"],
];


/***/ }),

/***/ "5Adw":
/*!*******************************************************************************************************!*\
  !*** ./src/app/tools/random-tools/_components/random-solve-buttons/random-solve-buttons.component.ts ***!
  \*******************************************************************************************************/
/*! exports provided: RandomSolveButtonsComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RandomSolveButtonsComponent", function() { return RandomSolveButtonsComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/router */ "tyNb");



class RandomSolveButtonsComponent {
    constructor() {
        this.correctEmitter = new _angular_core__WEBPACK_IMPORTED_MODULE_0__["EventEmitter"]();
        this.nextEmitter = new _angular_core__WEBPACK_IMPORTED_MODULE_0__["EventEmitter"]();
    }
}
RandomSolveButtonsComponent.ɵfac = function RandomSolveButtonsComponent_Factory(t) { return new (t || RandomSolveButtonsComponent)(); };
RandomSolveButtonsComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: RandomSolveButtonsComponent, selectors: [["it4all-random-solve-buttons"]], outputs: { correctEmitter: "correctEmitter", nextEmitter: "nextEmitter" }, decls: 10, vars: 0, consts: [[1, "columns"], [1, "column", "is-one-third-desktop"], [1, "button", "is-link", "is-fullwidth", 3, "click"], [1, "button", "is-primary", "is-fullwidth", 3, "click"], ["routerLink", "..", 1, "button", "is-dark", "is-fullwidth"]], template: function RandomSolveButtonsComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](2, "button", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵlistener"]("click", function RandomSolveButtonsComponent_Template_button_click_2_listener() { return ctx.correctEmitter.emit(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](3, "L\u00F6sung testen");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](4, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](5, "button", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵlistener"]("click", function RandomSolveButtonsComponent_Template_button_click_5_listener() { return ctx.nextEmitter.emit(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](6, "N\u00E4chste Aufgabe");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](7, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](8, "a", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](9, "Bearbeiten beenden");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } }, directives: [_angular_router__WEBPACK_IMPORTED_MODULE_1__["RouterLinkWithHref"]], encapsulation: 2 });


/***/ }),

/***/ "5BB2":
/*!**********************************************************************************!*\
  !*** ./src/app/tools/random-tools/nary/nary-addition/nary-addition.component.ts ***!
  \**********************************************************************************/
/*! exports provided: NaryAdditionComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "NaryAdditionComponent", function() { return NaryAdditionComponent; });
/* harmony import */ var _nary__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../nary */ "mjeW");
/* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../../../helpers */ "Afm0");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/forms */ "3Pt+");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _components_nary_number_read_only_input_nary_number_read_only_input_component__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../_components/nary-number-read-only-input/nary-number-read-only-input.component */ "Ox94");
/* harmony import */ var _components_random_solve_buttons_random_solve_buttons_component__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../../_components/random-solve-buttons/random-solve-buttons.component */ "5Adw");







function NaryAdditionComponent_option_21_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "option", 22);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ns_r3 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngValue", ns_r3);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtextInterpolate2"]("", ns_r3.radix, " - ", ns_r3.name, "");
} }
function NaryAdditionComponent_div_40_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 23);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1, " \u2718 Die L\u00F6sung ist nicht korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} }
function NaryAdditionComponent_div_41_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 24);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1, " \u2714 Die L\u00F6sung ist korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} }
const _c0 = function (a0, a1) { return { "is-success": a0, "is-danger": a1 }; };
class NaryAdditionComponent extends _nary__WEBPACK_IMPORTED_MODULE_0__["NaryComponentBase"] {
    constructor() {
        super();
        // noinspection JSMismatchedCollectionQueryUpdate
        this.numberingSystems = _nary__WEBPACK_IMPORTED_MODULE_0__["NUMBERING_SYSTEMS"];
        this.system = _nary__WEBPACK_IMPORTED_MODULE_0__["BINARY_SYSTEM"];
        this.target = 0;
        this.firstSummandInput = {
            decimalNumber: 0,
            numberingSystem: this.system,
            fieldId: 'firstSummand',
            labelContent: 'Summand 1:',
            maxValueForDigits: this.max
        };
        this.secondSummandInput = {
            decimalNumber: 0,
            numberingSystem: this.system,
            fieldId: 'secondSummand',
            labelContent: 'Summand 2:',
            maxValueForDigits: this.max
        };
        this.checked = false;
        this.correct = false;
        this.solutionString = '';
    }
    ngOnInit() {
        this.update();
    }
    update() {
        this.checked = false;
        this.correct = false;
        this.solutionString = '';
        this.target = Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["randomInt"])(1, this.max);
        const firstSummand = Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["randomInt"])(1, this.target);
        this.firstSummandInput.decimalNumber = firstSummand;
        this.firstSummandInput.numberingSystem = this.system;
        this.firstSummandInput.maxValueForDigits = this.max;
        this.secondSummandInput.decimalNumber = this.target - firstSummand;
        this.secondSummandInput.numberingSystem = this.system;
        this.secondSummandInput.maxValueForDigits = this.max;
    }
    checkSolution() {
        const reversedSolutionString = this.solutionString
            .replace(/\s/g, '')
            .split('').reverse().join('');
        const solution = parseInt(reversedSolutionString, this.system.radix);
        this.checked = true;
        this.correct = solution === this.target;
    }
    handleKeyboardEvent(event) {
        if (event.key === 'Enter') {
            if (this.correct) {
                this.update();
            }
            else {
                this.checkSolution();
            }
        }
    }
}
NaryAdditionComponent.ɵfac = function NaryAdditionComponent_Factory(t) { return new (t || NaryAdditionComponent)(); };
NaryAdditionComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdefineComponent"]({ type: NaryAdditionComponent, selectors: [["it4all-nary-addition"]], hostBindings: function NaryAdditionComponent_HostBindings(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("keypress", function NaryAdditionComponent_keypress_HostBindingHandler($event) { return ctx.handleKeyboardEvent($event); }, false, _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵresolveDocument"]);
    } }, features: [_angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵInheritDefinitionFeature"]], decls: 44, vars: 15, consts: [[1, "container"], [1, "columns"], [1, "column"], ["for", "max", 1, "label", "has-text-centered"], [1, "field", "has-addons"], [1, "control"], [1, "button", 3, "disabled", "click"], [1, "control", "is-expanded"], ["type", "number", "id", "max", 1, "input", "has-text-centered", 3, "value"], [1, "field"], ["for", "numberSystem", 1, "label", "has-text-centered"], [1, "select", "is-fullwidth"], ["id", "numberSystem", 3, "ngModel", "ngModelChange", "change"], [3, "ngValue", 4, "ngFor", "ngForOf"], [1, "my-3"], [3, "naryNumberInput"], [1, "button", "is-static"], ["for", "solution"], ["type", "text", "id", "solution", "placeholder", "L\u00F6sung", "autofocus", "", "autocomplete", "off", 1, "input", 3, "ngModel", "ngClass", "ngModelChange"], ["class", "notification has-text-centered is-danger", 4, "ngIf"], ["class", "notification has-text-centered is-success", 4, "ngIf"], [3, "correctEmitter", "nextEmitter"], [3, "ngValue"], [1, "notification", "has-text-centered", "is-danger"], [1, "notification", "has-text-centered", "is-success"]], template: function NaryAdditionComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](1, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](2, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](3, "label", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](4, "Maximalwert");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](5, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](6, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](7, "button", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("click", function NaryAdditionComponent_Template_button_click_7_listener() { return ctx.doubleMax(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](8, "* 2");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](9, "div", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](10, "input", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](11, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](12, "button", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("click", function NaryAdditionComponent_Template_button_click_12_listener() { return ctx.halveMax(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](13, "/ 2");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](14, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](15, "div", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](16, "label", 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](17, "Zahlensystem");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](18, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](19, "div", 11);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](20, "select", 12);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("ngModelChange", function NaryAdditionComponent_Template_select_ngModelChange_20_listener($event) { return ctx.system = $event; })("change", function NaryAdditionComponent_Template_select_change_20_listener() { return ctx.update(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](21, NaryAdditionComponent_option_21_Template, 2, 3, "option", 13);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](22, "hr");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](23, "div", 14);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](24, "it4all-nary-number-read-only-input", 15);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](25, "div", 14);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](26, "it4all-nary-number-read-only-input", 15);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](27, "hr");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](28, "div", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](29, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](30, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](31, "div", 16);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](32, "label", 17);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](33, "L\u00F6sung:");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](34, "div", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](35, "input", 18);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("ngModelChange", function NaryAdditionComponent_Template_input_ngModelChange_35_listener($event) { return ctx.solutionString = $event; });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](36, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](37, "div", 16);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](38, "sub");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](39);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](40, NaryAdditionComponent_div_40_Template, 2, 0, "div", 19);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](41, NaryAdditionComponent_div_41_Template, 2, 0, "div", 20);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](42, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](43, "it4all-random-solve-buttons", 21);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("correctEmitter", function NaryAdditionComponent_Template_it4all_random_solve_buttons_correctEmitter_43_listener() { return ctx.checkSolution(); })("nextEmitter", function NaryAdditionComponent_Template_it4all_random_solve_buttons_nextEmitter_43_listener() { return ctx.update(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](7);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("disabled", ctx.max === ctx.maximalMax);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵpropertyInterpolate"]("value", ctx.max);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("disabled", ctx.max === ctx.minimalMax);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](8);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngModel", ctx.system);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngForOf", ctx.numberingSystems);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("naryNumberInput", ctx.firstSummandInput);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("naryNumberInput", ctx.secondSummandInput);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](9);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngModel", ctx.solutionString)("ngClass", _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵpureFunction2"](12, _c0, ctx.checked && ctx.correct, ctx.checked && !ctx.correct));
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtextInterpolate"](ctx.system.radix);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx.checked && !ctx.correct);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx.checked && ctx.correct);
    } }, directives: [_angular_forms__WEBPACK_IMPORTED_MODULE_3__["SelectControlValueAccessor"], _angular_forms__WEBPACK_IMPORTED_MODULE_3__["NgControlStatus"], _angular_forms__WEBPACK_IMPORTED_MODULE_3__["NgModel"], _angular_common__WEBPACK_IMPORTED_MODULE_4__["NgForOf"], _components_nary_number_read_only_input_nary_number_read_only_input_component__WEBPACK_IMPORTED_MODULE_5__["NaryNumberReadOnlyInputComponent"], _angular_forms__WEBPACK_IMPORTED_MODULE_3__["DefaultValueAccessor"], _angular_common__WEBPACK_IMPORTED_MODULE_4__["NgClass"], _angular_common__WEBPACK_IMPORTED_MODULE_4__["NgIf"], _components_random_solve_buttons_random_solve_buttons_component__WEBPACK_IMPORTED_MODULE_6__["RandomSolveButtonsComponent"], _angular_forms__WEBPACK_IMPORTED_MODULE_3__["NgSelectOption"], _angular_forms__WEBPACK_IMPORTED_MODULE_3__["ɵangular_packages_forms_forms_z"]], styles: ["#solution[_ngcontent-%COMP%] {\n      direction: rtl;\n      unicode-bidi: bidi-override\n    }"] });


/***/ }),

/***/ "5M+P":
/*!*********************************************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/regex/regex-extraction-result/regex-extraction-match/regex-extraction-match.component.ts ***!
  \*********************************************************************************************************************************/
/*! exports provided: RegexExtractionMatchComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegexExtractionMatchComponent", function() { return RegexExtractionMatchComponent; });
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common */ "ofXK");



function RegexExtractionMatchComponent_span_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "span");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](ctx_r0.isCorrect() ? "\u2714" : "\u2718");
} }
function RegexExtractionMatchComponent_span_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "span");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1, "Erwartet: ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](ctx_r1.match.sampleArg);
} }
function RegexExtractionMatchComponent_span_4_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "span");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1, "Bekommen: ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r2 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](ctx_r2.match.userArg);
} }
class RegexExtractionMatchComponent {
    isCorrect() {
        return this.match.matchType === _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["MatchType"].SuccessfulMatch;
    }
}
RegexExtractionMatchComponent.ɵfac = function RegexExtractionMatchComponent_Factory(t) { return new (t || RegexExtractionMatchComponent)(); };
RegexExtractionMatchComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineComponent"]({ type: RegexExtractionMatchComponent, selectors: [["it4all-regex-extraction-match"]], inputs: { match: "match" }, decls: 5, vars: 4, consts: [[1, "notification", 3, "ngClass"], [4, "ngIf"]], template: function RegexExtractionMatchComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](1, RegexExtractionMatchComponent_span_1_Template, 2, 1, "span", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](2, RegexExtractionMatchComponent_span_2_Template, 4, 1, "span", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3, ", ");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](4, RegexExtractionMatchComponent_span_4_Template, 4, 1, "span", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngClass", ctx.isCorrect() ? "is-success" : "is-danger");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", ctx.isCorrect());
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", ctx.match.sampleArg);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", ctx.match.userArg);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_2__["NgClass"], _angular_common__WEBPACK_IMPORTED_MODULE_2__["NgIf"]], encapsulation: 2 });


/***/ }),

/***/ "8EYx":
/*!*********************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/lessons/lesson-overview/lesson-overview.component.ts ***!
  \*********************************************************************************************/
/*! exports provided: LessonOverviewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LessonOverviewComponent", function() { return LessonOverviewComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/common */ "ofXK");




function LessonOverviewComponent_ng_container_1_a_7_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "a", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, "Als Video");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} }
function LessonOverviewComponent_ng_container_1_a_9_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "a", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, "Als Text");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} }
function LessonOverviewComponent_ng_container_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "h1", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](3, "div", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](5, "div", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](6, "div", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](7, LessonOverviewComponent_ng_container_1_a_7_Template, 2, 0, "a", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](8, "div", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](9, LessonOverviewComponent_ng_container_1_a_9_Template, 2, 0, "a", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx_r0.lessonOverviewFragment.title);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx_r0.lessonOverviewFragment.description);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx_r0.lessonOverviewFragment.video);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx_r0.lessonOverviewFragment.contentCount > 0);
} }
function LessonOverviewComponent_ng_template_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 11);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, "Lade Daten...");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} }
class LessonOverviewComponent {
    constructor(route, lessonOverviewGQL) {
        this.route = route;
        this.lessonOverviewGQL = lessonOverviewGQL;
    }
    ngOnInit() {
        this.sub = this.route.paramMap.subscribe((paramMap) => {
            const toolId = paramMap.get('toolId');
            const lessonId = parseInt(paramMap.get('lessonId'), 10);
            this.lessonOverviewGQL
                .watch({ toolId, lessonId })
                .valueChanges
                .subscribe(({ data }) => this.lessonOverviewQuery = data);
        });
    }
    ngOnDestroy() {
        this.sub.unsubscribe();
    }
    get lessonOverviewFragment() {
        return this.lessonOverviewQuery ? this.lessonOverviewQuery.me.tool.lesson : undefined;
    }
}
LessonOverviewComponent.ɵfac = function LessonOverviewComponent_Factory(t) { return new (t || LessonOverviewComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_angular_router__WEBPACK_IMPORTED_MODULE_1__["ActivatedRoute"]), _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_services_apollo_services__WEBPACK_IMPORTED_MODULE_2__["LessonOverviewGQL"])); };
LessonOverviewComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: LessonOverviewComponent, selectors: [["it4all-lesson-overview"]], decls: 4, vars: 2, consts: [[1, "container"], [4, "ngIf", "ngIfElse"], ["loadingDataBlock", ""], [1, "title", "is-2", "has-text-centered"], [1, "content", "box"], [1, "columns"], [1, "column"], ["class", "button is-fullwidth", "routerLink", "video", 4, "ngIf"], ["class", "button is-fullwidth", "routerLink", "text", 4, "ngIf"], ["routerLink", "video", 1, "button", "is-fullwidth"], ["routerLink", "text", 1, "button", "is-fullwidth"], [1, "notification", "is-primary", "has-text-centered"]], template: function LessonOverviewComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](1, LessonOverviewComponent_ng_container_1_Template, 10, 4, "ng-container", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](2, LessonOverviewComponent_ng_template_2_Template, 2, 0, "ng-template", null, 2, _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplateRefExtractor"]);
    } if (rf & 2) {
        const _r1 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵreference"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx.lessonOverviewQuery)("ngIfElse", _r1);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_3__["NgIf"], _angular_router__WEBPACK_IMPORTED_MODULE_1__["RouterLinkWithHref"]], encapsulation: 2 });


/***/ }),

/***/ "9siN":
/*!*********************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/regex/regex-cheatsheet/regex-cheatsheet.component.ts ***!
  \*********************************************************************************************/
/*! exports provided: RegexCheatsheetComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegexCheatsheetComponent", function() { return RegexCheatsheetComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");

class RegexCheatsheetComponent {
    constructor() {
    }
}
RegexCheatsheetComponent.ɵfac = function RegexCheatsheetComponent_Factory(t) { return new (t || RegexCheatsheetComponent)(); };
RegexCheatsheetComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: RegexCheatsheetComponent, selectors: [["it4all-regex-cheatsheet"]], decls: 89, vars: 0, consts: [[1, "table", "is-bordered", "is-fullwidth"]], template: function RegexCheatsheetComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "table", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "thead");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](2, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](3, "th");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](4, "Symbol");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](5, "th");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](6, "Bedeutung");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](7, "th");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](8, "Symbol");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](9, "th");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](10, "Bedeutung");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](11, "tbody");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](12, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](13, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](14, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](15, "\\d");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](16, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](17, "Eine Ziffer zwischen 0 und 9");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](18, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](19, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](20, "\\D");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](21, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](22, "Zeichen, das keine Ziffer ist");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](23, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](24, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](25, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](26, "\\s");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](27, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](28, "Leerzeichen, Tabulator, ...");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](29, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](30, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](31, "\\w");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](32, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](33, "Buchstabe, Ziffer oder Unterstrich");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](34, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](35, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](36, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](37, ".");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](38, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](39, "Ein beliebiges Zeichen");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](40, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](41, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](42, "\\.");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](43, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](44, "Ein Punkt");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](45, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](46, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](47, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](48, "+");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](49, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](50, "Ein oder mehr Vorkommen");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](51, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](52, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](53, "{x}");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](54, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](55, "Genau x Vorkommen");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](56, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](57, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](58, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](59, "{x,y}");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](60, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](61, "x bis y Vorkommen");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](62, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](63, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](64, "{x,}");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](65, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](66, "x bis unendlich viele Vorkommen");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](67, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](68, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](69, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](70, "*");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](71, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](72, "Beliebig viele (auch 0) Vorkommen");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](73, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](74, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](75, "?");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](76, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](77, "Ein oder kein Vorkommen");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](78, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](79, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](80, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](81, "x | y");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](82, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](83, "x oder y");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](84, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](85, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](86, "[x-y]");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](87, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](88, "Ein Zeichen im Bereich zwischen x und y");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } }, encapsulation: 2 });


/***/ }),

/***/ "Afm0":
/*!****************************!*\
  !*** ./src/app/helpers.ts ***!
  \****************************/
/*! exports provided: randomInt, takeRandom, flatMapArray, distinctStringArray, distinctObjectArray */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "randomInt", function() { return randomInt; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "takeRandom", function() { return takeRandom; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "flatMapArray", function() { return flatMapArray; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "distinctStringArray", function() { return distinctStringArray; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "distinctObjectArray", function() { return distinctObjectArray; });
function randomInt(minInclusive, maxExclusive) {
    const intMax = Math.floor(maxExclusive);
    const intMin = Math.floor(minInclusive);
    return Math.floor(Math.random() * (intMax - intMin)) + intMin;
}
function takeRandom(from) {
    return from[Math.floor(Math.random() * from.length)];
}
function flatMapArray(ts, f) {
    return ts.reduce((acc, t) => acc.concat(f(t)), []);
}
function distinctStringArray(ts) {
    return [...new Set(ts)];
}
function distinctObjectArray(ts, key) {
    const helperMap = new Map();
    for (const t of ts) {
        const tKey = key(t);
        if (!helperMap.has(tKey)) {
            helperMap.set(tKey, t);
        }
    }
    return Array.from(helperMap.values());
}


/***/ }),

/***/ "AgHE":
/*!********************************************************************!*\
  !*** ./src/app/user_management/login-form/login-form.component.ts ***!
  \********************************************************************/
/*! exports provided: LoginFormComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LoginFormComponent", function() { return LoginFormComponent; });
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/forms */ "3Pt+");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! rxjs/operators */ "kU1M");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _services_authentication_service__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../_services/authentication.service */ "pW6c");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! @angular/common */ "ofXK");







function LoginFormComponent_div_18_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 15);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](1, "span");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18n"](2, 16);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](3, "! ");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} }
class LoginFormComponent {
    constructor(route, router, authenticationService) {
        this.route = route;
        this.router = router;
        this.authenticationService = authenticationService;
        this.loading = false;
        this.submitted = false;
        this.inValid = false;
        if (this.authenticationService.currentUserValue) {
            // noinspection JSIgnoredPromiseFromCall
            this.router.navigate(['/']);
        }
    }
    ngOnInit() {
        this.loginForm = new _angular_forms__WEBPACK_IMPORTED_MODULE_0__["FormGroup"]({
            username: new _angular_forms__WEBPACK_IMPORTED_MODULE_0__["FormControl"]('', [_angular_forms__WEBPACK_IMPORTED_MODULE_0__["Validators"].required]),
            password: new _angular_forms__WEBPACK_IMPORTED_MODULE_0__["FormControl"]('', [_angular_forms__WEBPACK_IMPORTED_MODULE_0__["Validators"].required])
        });
        this.returnUrl = this.route.snapshot.queryParams.returnUrl || '/';
    }
    get f() {
        return this.loginForm.controls;
    }
    onSubmit() {
        this.submitted = true;
        const username = this.f.username.value;
        const password = this.f.password.value;
        if (this.loginForm.invalid) {
            alert('Daten sind nicht valide!');
            return;
        }
        this.loading = true;
        this.authenticationService.login(username, password)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_1__["first"])())
            .subscribe((data) => {
            if (data) {
                // noinspection JSIgnoredPromiseFromCall
                this.router.navigate([this.returnUrl]);
            }
            else {
                this.inValid = true;
            }
        }, () => this.loading = false);
    }
}
LoginFormComponent.ɵfac = function LoginFormComponent_Factory(t) { return new (t || LoginFormComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdirectiveInject"](_angular_router__WEBPACK_IMPORTED_MODULE_3__["ActivatedRoute"]), _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdirectiveInject"](_angular_router__WEBPACK_IMPORTED_MODULE_3__["Router"]), _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdirectiveInject"](_services_authentication_service__WEBPACK_IMPORTED_MODULE_4__["AuthenticationService"])); };
LoginFormComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdefineComponent"]({ type: LoginFormComponent, selectors: [["ng-component"]], decls: 24, vars: 2, consts: function () { let i18n_0; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_2454050363478003966$$SRC_APP_USER_MANAGEMENT_LOGIN_FORM_LOGIN_FORM_COMPONENT_TS_1 = goog.getMsg("Login");
        i18n_0 = MSG_EXTERNAL_2454050363478003966$$SRC_APP_USER_MANAGEMENT_LOGIN_FORM_LOGIN_FORM_COMPONENT_TS_1;
    }
    else {
        i18n_0 = "Login";
    } let i18n_2; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_5643948441575626307$$SRC_APP_USER_MANAGEMENT_LOGIN_FORM_LOGIN_FORM_COMPONENT_TS_3 = goog.getMsg("Nutzername");
        i18n_2 = MSG_EXTERNAL_5643948441575626307$$SRC_APP_USER_MANAGEMENT_LOGIN_FORM_LOGIN_FORM_COMPONENT_TS_3;
    }
    else {
        i18n_2 = "Username";
    } let i18n_4; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_5643948441575626307$$SRC_APP_USER_MANAGEMENT_LOGIN_FORM_LOGIN_FORM_COMPONENT_TS_5 = goog.getMsg("Nutzername");
        i18n_4 = MSG_EXTERNAL_5643948441575626307$$SRC_APP_USER_MANAGEMENT_LOGIN_FORM_LOGIN_FORM_COMPONENT_TS_5;
    }
    else {
        i18n_4 = "Username";
    } let i18n_6; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_6057821346666596338$$SRC_APP_USER_MANAGEMENT_LOGIN_FORM_LOGIN_FORM_COMPONENT_TS_7 = goog.getMsg("Passwort");
        i18n_6 = MSG_EXTERNAL_6057821346666596338$$SRC_APP_USER_MANAGEMENT_LOGIN_FORM_LOGIN_FORM_COMPONENT_TS_7;
    }
    else {
        i18n_6 = "Password";
    } let i18n_8; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_6057821346666596338$$SRC_APP_USER_MANAGEMENT_LOGIN_FORM_LOGIN_FORM_COMPONENT_TS_9 = goog.getMsg("Passwort");
        i18n_8 = MSG_EXTERNAL_6057821346666596338$$SRC_APP_USER_MANAGEMENT_LOGIN_FORM_LOGIN_FORM_COMPONENT_TS_9;
    }
    else {
        i18n_8 = "Password";
    } let i18n_10; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_2454050363478003966$$SRC_APP_USER_MANAGEMENT_LOGIN_FORM_LOGIN_FORM_COMPONENT_TS_11 = goog.getMsg("Login");
        i18n_10 = MSG_EXTERNAL_2454050363478003966$$SRC_APP_USER_MANAGEMENT_LOGIN_FORM_LOGIN_FORM_COMPONENT_TS_11;
    }
    else {
        i18n_10 = "Login";
    } let i18n_12; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_2992974827262307628$$SRC_APP_USER_MANAGEMENT_LOGIN_FORM_LOGIN_FORM_COMPONENT_TS__13 = goog.getMsg("Kombination aus Nutzername und Password ist nicht valide");
        i18n_12 = MSG_EXTERNAL_2992974827262307628$$SRC_APP_USER_MANAGEMENT_LOGIN_FORM_LOGIN_FORM_COMPONENT_TS__13;
    }
    else {
        i18n_12 = "Invalid combination of username and password";
    } return [[1, "container"], [1, "title", "is-3", "has-text-centered"], i18n_0, [3, "formGroup", "ngSubmit"], [1, "field"], ["for", "username", 1, "label"], i18n_2, [1, "control"], ["type", "text", "id", "username", "formControlName", "username", "placeholder", i18n_4, "autofocus", "", 1, "input"], ["for", "password", 1, "label"], i18n_6, ["type", "password", "formControlName", "password", "placeholder", i18n_8, "id", "password", 1, "input"], ["class", "notification is-danger", 4, "ngIf"], [1, "button", "is-link"], i18n_10, [1, "notification", "is-danger"], i18n_12]; }, template: function LoginFormComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](1, "h1", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18n"](2, 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](3, "form", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("ngSubmit", function LoginFormComponent_Template_form_ngSubmit_3_listener() { return ctx.onSubmit(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](4, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](5, "label", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](6, "span");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18n"](7, 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](8, ":");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](9, "div", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](10, "input", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](11, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](12, "label", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](13, "span");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18n"](14, 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](15, ":");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](16, "div", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](17, "input", 11);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](18, LoginFormComponent_div_18_Template, 4, 0, "div", 12);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](19, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](20, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](21, "div", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](22, "button", 13);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18n"](23, 14);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("formGroup", ctx.loginForm);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](15);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx.submitted && ctx.inValid);
    } }, directives: [_angular_forms__WEBPACK_IMPORTED_MODULE_0__["ɵangular_packages_forms_forms_ba"], _angular_forms__WEBPACK_IMPORTED_MODULE_0__["NgControlStatusGroup"], _angular_forms__WEBPACK_IMPORTED_MODULE_0__["FormGroupDirective"], _angular_forms__WEBPACK_IMPORTED_MODULE_0__["DefaultValueAccessor"], _angular_forms__WEBPACK_IMPORTED_MODULE_0__["NgControlStatus"], _angular_forms__WEBPACK_IMPORTED_MODULE_0__["FormControlName"], _angular_common__WEBPACK_IMPORTED_MODULE_5__["NgIf"]], encapsulation: 2 });


/***/ }),

/***/ "AytR":
/*!*****************************************!*\
  !*** ./src/environments/environment.ts ***!
  \*****************************************/
/*! exports provided: environment */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "environment", function() { return environment; });
// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.
const environment = {
    production: false
};
/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.


/***/ }),

/***/ "Bd0U":
/*!*****************************************************************************************!*\
  !*** ./src/app/tools/random-tools/bool/bool-drawing/_bool-drawing-model/boolDrawing.ts ***!
  \*****************************************************************************************/
/*! exports provided: graph, paper, dragX, dragY, SIGNALS, createElementInGraph, toggleLive, broadcastSignal, draw */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "graph", function() { return graph; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "paper", function() { return paper; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "dragX", function() { return dragX; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "dragY", function() { return dragY; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SIGNALS", function() { return SIGNALS; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "createElementInGraph", function() { return createElementInGraph; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "toggleLive", function() { return toggleLive; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "broadcastSignal", function() { return broadcastSignal; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "draw", function() { return draw; });
/* harmony import */ var jquery__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! jquery */ "EVdn");
/* harmony import */ var jquery__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(jquery__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var jointjs__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! jointjs */ "iuCI");
/* harmony import */ var underscore__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! underscore */ "xG9w");
/* harmony import */ var _boolDrawingElements__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./boolDrawingElements */ "ErB8");




var Graph = jointjs__WEBPACK_IMPORTED_MODULE_1__["dia"].Graph;
const graph = new Graph();
const elementWidth = 60;
const gridSize = elementWidth / 2;
let paper;
let dragX; // Postion within div : X
let dragY; // Postion within div : Y
const SIGNALS = new Map([
    ['a', true],
    ['b', true],
    ['c', true],
    ['d', true]
]);
function createElementInGraph(elementName, x, y) {
    let element;
    let svg;
    const elementPos = { position: { x, y } };
    switch (elementName) {
        case 'elementAND':
            element = new jointjs__WEBPACK_IMPORTED_MODULE_1__["shapes"].logic.And(elementPos);
            svg = 'https://upload.wikimedia.org/wikipedia/commons/0/0f/AND_IEC.svg';
            break;
        case 'elementNAND':
            element = new jointjs__WEBPACK_IMPORTED_MODULE_1__["shapes"].logic.Nand(elementPos);
            svg = 'https://upload.wikimedia.org/wikipedia/commons/d/d8/NAND_IEC.svg';
            break;
        case 'elementOR':
            element = new jointjs__WEBPACK_IMPORTED_MODULE_1__["shapes"].logic.Or(elementPos);
            svg = 'https://upload.wikimedia.org/wikipedia/commons/4/42/OR_IEC.svg';
            break;
        case 'elementNOR':
            element = new jointjs__WEBPACK_IMPORTED_MODULE_1__["shapes"].logic.Nor(elementPos);
            svg = 'https://upload.wikimedia.org/wikipedia/commons/6/6d/NOR_IEC.svg';
            break;
        case 'elementXOR':
            element = new jointjs__WEBPACK_IMPORTED_MODULE_1__["shapes"].logic.Xor(elementPos);
            svg = 'https://upload.wikimedia.org/wikipedia/commons/4/4e/XOR_IEC.svg';
            break;
        case 'elementXNOR':
            element = new jointjs__WEBPACK_IMPORTED_MODULE_1__["shapes"].logic.Xnor(elementPos);
            svg = 'https://upload.wikimedia.org/wikipedia/commons/5/56/XNOR_IEC.svg';
            break;
        case 'elementNOT':
            element = new jointjs__WEBPACK_IMPORTED_MODULE_1__["shapes"].logic.Not(elementPos);
            svg = 'https://upload.wikimedia.org/wikipedia/commons/e/ef/NOT_IEC.svg';
            break;
    }
    element.attr('image/xlink:href', svg);
    graph.addCell(element);
}
document.addEventListener('dragover', (e) => {
    const offset = jquery__WEBPACK_IMPORTED_MODULE_0__('#paper').offset();
    // console.warn(offset);
    dragX = e.pageX - offset.left;
    dragY = e.pageY - offset.top;
}, false);
function initGraph(inputsToAdd, outputsToAdd) {
    for (const inputToAdd of inputsToAdd) {
        const newGate = new jointjs__WEBPACK_IMPORTED_MODULE_1__["shapes"].logic.Input(inputToAdd);
        newGate.attr('logicSymbol', inputToAdd.symbol);
        newGate.attr('text', { text: inputToAdd.symbol });
        graph.addCell(newGate);
    }
    for (const outputToAdd of outputsToAdd) {
        const newGate = new jointjs__WEBPACK_IMPORTED_MODULE_1__["shapes"].logic.Output(outputToAdd);
        newGate.attr('logicSymbol', outputToAdd.symbol);
        newGate.attr('text', { text: outputToAdd.symbol });
        graph.addCell(newGate);
    }
}
function toggleLive(model, signal) {
    // add 'live' class to the element if there is a positive signal
    // FIXME: cast should not work?
    const x = paper.findViewByModel(model).el;
    jointjs__WEBPACK_IMPORTED_MODULE_1__["V"](x).toggleClass('live', signal);
}
function broadcastSignal(gate, signal) {
    toggleLive(gate, signal);
    // broadcast signal to all output ports
    const outGoingWires = graph.getConnectedLinks(gate, { outbound: true });
    for (const wire of outGoingWires) {
        wire.set('signal', signal);
    }
}
function initializeSignals() {
    // cancel all signals stores in wires
    for (const wire of graph.getLinks()) {
        wire.set('signal', false);
    }
    // remove all 'live' classes
    jquery__WEBPACK_IMPORTED_MODULE_0__('.live').each(() => {
        // TODO:        joint.V(this).removeClass('live');
    });
    for (const element of graph.getElements()) {
        const outGoingLinks = graph.getConnectedLinks(element, { outbound: true });
        if (element instanceof jointjs__WEBPACK_IMPORTED_MODULE_1__["shapes"].logic.Input && outGoingLinks.length > 0) {
            broadcastSignal(element, SIGNALS[element.attr('logicSymbol')]);
        }
    }
}
function initGraphEvents() {
    graph.on('change:source change:target', (model, end) => {
        const e = 'target' in model.changed ? 'target' : 'source';
        if ((model.previous(e).id && !model.get(e).id) || (!model.previous(e).id && model.get(e).id)) {
            // if source/target has been connected to a port or disconnected from a port reinitialize signals
            initializeSignals();
        }
    });
    graph.on('change:signal', (wire, signal) => {
        toggleLive(wire, signal);
        const gate = graph.getCell(wire.get('target').id);
        if (gate && false) {
            // FIXME: implement!
            if (gate instanceof jointjs__WEBPACK_IMPORTED_MODULE_1__["shapes"].logic.Gate11) {
                gate.onSignal(signal, () => {
                    const maybeInput = graph.getConnectedLinks(gate, { inbound: true });
                    const input = (maybeInput.length === 1) ? maybeInput[0].get('signal') : false;
                    // calculate the output signal
                    throw Error('TODO');
                    // const output: boolean = gate.operation(input);
                    // broadcastSignal(gate, output);
                });
            }
            else if (gate instanceof jointjs__WEBPACK_IMPORTED_MODULE_1__["shapes"].logic.Gate21) {
                const maybeInput = graph.getConnectedLinks(gate, { inbound: true });
                const input1 = (maybeInput.length >= 1) ? maybeInput[0].get('signal') : false;
                const input2 = (maybeInput.length === 2) ? maybeInput[1].get('signal') : false;
                // calculate the output signal
                throw Error('TODO');
                // const output: boolean = gate.operation(input1, input2);
                // broadcastSignal(gate, output);
            }
            else {
                // TODO!
            }
        }
    });
    graph.on('change:position', (cell, newPosition, opt) => {
        if (opt.skipParentHandler) {
            return;
        }
        if (cell.get('embeds') && cell.get('embeds').length) {
            // If we're manipulating a parent element, let's store it's original position to a special property so that
            // we can shrink the parent element back while manipulating its children.
            cell.set('originalPosition', cell.get('position'));
        }
        const parentId = cell.get('parent');
        if (!parentId) {
            return;
        }
        const parent = graph.getCell(parentId);
        if (!parent.get('originalPosition')) {
            parent.set('originalPosition', parent.get('position'));
        }
        if (!parent.get('originalSize')) {
            parent.set('originalSize', parent.get('size'));
        }
        const originalPosition = parent.get('originalPosition');
        const originalSize = parent.get('originalSize');
        let newX = originalPosition.x;
        let newY = originalPosition.y;
        let newCornerX = originalPosition.x + originalSize.width;
        let newCornerY = originalPosition.y + originalSize.height;
        underscore__WEBPACK_IMPORTED_MODULE_2__["each"](parent.getEmbeddedCells(), (child) => {
            const childBbox = child.getBBox();
            if (childBbox.x < newX) {
                newX = childBbox.x;
            }
            if (childBbox.y < newY) {
                newY = childBbox.y;
            }
            if (childBbox.corner().x > newCornerX) {
                newCornerX = childBbox.corner().x;
            }
            if (childBbox.corner().y > newCornerY) {
                newCornerY = childBbox.corner().y;
            }
        });
        // Note that we also pass a flag so that we know we shouldn't adjust the `originalPosition` and `originalSize` in our handlers as a
        // reaction on the following `set()` call.
        parent.set({
            position: { x: newX, y: newY },
            size: { width: newCornerX - newX, height: newCornerY - newY }
        } /*, {skipParentHandler: true}*/);
    });
    graph.on('change:size', (cell, newPosition, opt) => {
        if (opt.skipParentHandler) {
            return;
        }
        if (cell.get('embeds') && cell.get('embeds').length) {
            // If we're manipulating a parent element, let's store it's original size to a special property so that
            // we can shrink the parent element back while manipulating its children.
            cell.set('originalSize', cell.get('size'));
        }
    });
}
function draw(formula) {
    const paperSelector = jquery__WEBPACK_IMPORTED_MODULE_0__('#paper');
    paper = new jointjs__WEBPACK_IMPORTED_MODULE_1__["dia"].Paper({
        el: paperSelector,
        model: graph,
        width: paperSelector.width(), height: 600,
        gridSize, drawGrid: { color: 'grey', thickness: 1, name: 'mesh' },
        snapLinks: true, linkPinning: false,
        defaultLink: (cellView, magnet) => new jointjs__WEBPACK_IMPORTED_MODULE_1__["shapes"].logic.Wire(cellView, magnet),
        validateConnection(cellViewSource, magnetSource, cellViewTarget, magnetTarget, end, linkView) {
            if (end === 'target') {
                //        target requires an input port to connect
                if (!magnetTarget || !magnetTarget.getAttribute('class') || magnetTarget.getAttribute('class').indexOf('input') < 0) {
                    return false;
                }
                //  check whether the port is being already used
                const portUsed = underscore__WEBPACK_IMPORTED_MODULE_2__["find"](this.model.getLinks(), (link) => {
                    return (link.id !== linkView.model.id &&
                        link.get('target').id === cellViewTarget.model.id &&
                        link.get('target').port === magnetTarget.getAttribute('port'));
                });
                return !portUsed;
            }
            else { // e === 'source'
                //     source requires an output port to connect
                return magnetSource && magnetSource.getAttribute('class') && magnetSource.getAttribute('class').indexOf('output') >= 0;
            }
        }
    });
    // Graph events
    initGraphEvents();
    const outputVariables = ['z'];
    const inputsToAdd = formula.getVariables().map((variable, index) => {
        return { symbol: variable.variable, position: { x: gridSize, y: gridSize + index * (2 * elementWidth + gridSize) } };
    });
    const xMaxPos = paper.getArea().width - 60 - gridSize;
    // const yPos: number = paper.getArea().height;
    const outputsToAdd = outputVariables.map((variable, index) => {
        // TODO: calculate if more than one input!
        return { symbol: variable, position: { x: xMaxPos, y: 3 * elementWidth } };
    });
    initGraph(inputsToAdd, outputsToAdd);
}


/***/ }),

/***/ "BeXp":
/*!********************************************************************!*\
  !*** ./src/app/tools/collection-tools/collection-tools.routing.ts ***!
  \********************************************************************/
/*! exports provided: CollectionToolRoutingModule, collectionToolRoutingComponents */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CollectionToolRoutingModule", function() { return CollectionToolRoutingModule; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "collectionToolRoutingComponents", function() { return collectionToolRoutingComponents; });
/* harmony import */ var _collection_tool_overview_collection_tool_overview_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./collection-tool-overview/collection-tool-overview.component */ "d8br");
/* harmony import */ var _helpers_auth_guard__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../_helpers/auth-guard */ "T7p5");
/* harmony import */ var _collection_overview_collection_overview_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./collection-overview/collection-overview.component */ "q8z/");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _exercise_overview_exercise_overview_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./exercise-overview/exercise-overview.component */ "SJa0");
/* harmony import */ var _exercise_exercise_component__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./exercise/exercise.component */ "t2v4");
/* harmony import */ var _all_exercises_overview_all_exercises_overview_component__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./all-exercises-overview/all-exercises-overview.component */ "Zy2k");
/* harmony import */ var _collections_list_collections_list_component__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ./collections-list/collections-list.component */ "Xlg+");
/* harmony import */ var _lessons_lessons_for_tool_overview_lessons_for_tool_overview_component__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./lessons/lessons-for-tool-overview/lessons-for-tool-overview.component */ "dT2p");
/* harmony import */ var _lessons_lesson_as_text_lesson_as_text_component__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./lessons/lesson-as-text/lesson-as-text.component */ "tsMx");
/* harmony import */ var _lessons_lesson_as_video_lesson_as_video_component__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ./lessons/lesson-as-video/lesson-as-video.component */ "1kDo");
/* harmony import */ var _lessons_lesson_overview_lesson_overview_component__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ./lessons/lesson-overview/lesson-overview.component */ "8EYx");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! @angular/core */ "fXoL");














const collectionToolRoutes = [
    {
        path: 'tools/:toolId', canActivate: [_helpers_auth_guard__WEBPACK_IMPORTED_MODULE_1__["AuthGuard"]], children: [
            { path: '', component: _collection_tool_overview_collection_tool_overview_component__WEBPACK_IMPORTED_MODULE_0__["CollectionToolOverviewComponent"] },
            {
                path: 'lessons', children: [
                    { path: '', component: _lessons_lessons_for_tool_overview_lessons_for_tool_overview_component__WEBPACK_IMPORTED_MODULE_8__["LessonsForToolOverviewComponent"] },
                    {
                        path: ':lessonId', children: [
                            { path: '', component: _lessons_lesson_overview_lesson_overview_component__WEBPACK_IMPORTED_MODULE_11__["LessonOverviewComponent"] },
                            { path: 'text', component: _lessons_lesson_as_text_lesson_as_text_component__WEBPACK_IMPORTED_MODULE_9__["LessonAsTextComponent"] },
                            { path: 'video', component: _lessons_lesson_as_video_lesson_as_video_component__WEBPACK_IMPORTED_MODULE_10__["LessonAsVideoComponent"] }
                        ]
                    }
                ]
            },
            {
                path: 'collections', children: [
                    { path: '', component: _collections_list_collections_list_component__WEBPACK_IMPORTED_MODULE_7__["CollectionsListComponent"] },
                    {
                        path: ':collId', children: [
                            { path: '', component: _collection_overview_collection_overview_component__WEBPACK_IMPORTED_MODULE_2__["CollectionOverviewComponent"] },
                            { path: 'exercises/:exId', component: _exercise_overview_exercise_overview_component__WEBPACK_IMPORTED_MODULE_4__["ExerciseOverviewComponent"] },
                            { path: 'exercises/:exId/parts/:partId', component: _exercise_exercise_component__WEBPACK_IMPORTED_MODULE_5__["ExerciseComponent"] },
                        ]
                    },
                ]
            },
            { path: 'allExercises', component: _all_exercises_overview_all_exercises_overview_component__WEBPACK_IMPORTED_MODULE_6__["AllExercisesOverviewComponent"] }
        ]
    }
];
class CollectionToolRoutingModule {
}
CollectionToolRoutingModule.ɵfac = function CollectionToolRoutingModule_Factory(t) { return new (t || CollectionToolRoutingModule)(); };
CollectionToolRoutingModule.ɵmod = _angular_core__WEBPACK_IMPORTED_MODULE_12__["ɵɵdefineNgModule"]({ type: CollectionToolRoutingModule });
CollectionToolRoutingModule.ɵinj = _angular_core__WEBPACK_IMPORTED_MODULE_12__["ɵɵdefineInjector"]({ imports: [[_angular_router__WEBPACK_IMPORTED_MODULE_3__["RouterModule"].forChild(collectionToolRoutes)], _angular_router__WEBPACK_IMPORTED_MODULE_3__["RouterModule"]] });
(function () { (typeof ngJitMode === "undefined" || ngJitMode) && _angular_core__WEBPACK_IMPORTED_MODULE_12__["ɵɵsetNgModuleScope"](CollectionToolRoutingModule, { imports: [_angular_router__WEBPACK_IMPORTED_MODULE_3__["RouterModule"]], exports: [_angular_router__WEBPACK_IMPORTED_MODULE_3__["RouterModule"]] }); })();
const collectionToolRoutingComponents = [
    _collection_tool_overview_collection_tool_overview_component__WEBPACK_IMPORTED_MODULE_0__["CollectionToolOverviewComponent"],
    // Lesssons
    _lessons_lessons_for_tool_overview_lessons_for_tool_overview_component__WEBPACK_IMPORTED_MODULE_8__["LessonsForToolOverviewComponent"],
    _lessons_lesson_overview_lesson_overview_component__WEBPACK_IMPORTED_MODULE_11__["LessonOverviewComponent"],
    _lessons_lesson_as_text_lesson_as_text_component__WEBPACK_IMPORTED_MODULE_9__["LessonAsTextComponent"],
    _lessons_lesson_as_video_lesson_as_video_component__WEBPACK_IMPORTED_MODULE_10__["LessonAsVideoComponent"],
    // Collections
    _collections_list_collections_list_component__WEBPACK_IMPORTED_MODULE_7__["CollectionsListComponent"],
    _collection_overview_collection_overview_component__WEBPACK_IMPORTED_MODULE_2__["CollectionOverviewComponent"],
    // Exercises
    _exercise_overview_exercise_overview_component__WEBPACK_IMPORTED_MODULE_4__["ExerciseOverviewComponent"],
    _exercise_exercise_component__WEBPACK_IMPORTED_MODULE_5__["ExerciseComponent"],
    // AllExercises
    _all_exercises_overview_all_exercises_overview_component__WEBPACK_IMPORTED_MODULE_6__["AllExercisesOverviewComponent"],
];


/***/ }),

/***/ "Brp2":
/*!*****************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/regex/regex-exercise/regex-exercise.component.ts ***!
  \*****************************************************************************************/
/*! exports provided: RegexExerciseComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegexExerciseComponent", function() { return RegexExerciseComponent; });
/* harmony import */ var _helpers_component_with_exercise_directive__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../_helpers/component-with-exercise.directive */ "TRIe");
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _services_dexie_service__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../../../_services/dexie.service */ "4di/");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/forms */ "3Pt+");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _shared_tabs_tabs_component__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../../../shared/tabs/tabs.component */ "b4kd");
/* harmony import */ var _shared_tab_tab_component__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../../../../shared/tab/tab.component */ "4YYW");
/* harmony import */ var _regex_cheatsheet_regex_cheatsheet_component__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ../regex-cheatsheet/regex-cheatsheet.component */ "9siN");
/* harmony import */ var _shared_points_notification_points_notification_component__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ../../../../shared/points-notification/points-notification.component */ "ef8k");
/* harmony import */ var _shared_solution_saved_solution_saved_component__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ../../../../shared/solution-saved/solution-saved.component */ "rqf4");
/* harmony import */ var _regex_matching_result_regex_matching_result_component__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ../regex-matching-result/regex-matching-result.component */ "uEXo");
/* harmony import */ var _regex_extraction_result_regex_extraction_result_component__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ../regex-extraction-result/regex-extraction-result.component */ "kLWW");
/* harmony import */ var _components_string_sample_sol_string_sample_sol_component__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! ../../_components/string-sample-sol/string-sample-sol.component */ "jonz");
















function RegexExerciseComponent_span_23_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "span");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1, "anzeigen");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} }
function RegexExerciseComponent_span_24_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "span");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1, "ausblenden");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} }
function RegexExerciseComponent_it4all_regex_cheatsheet_25_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](0, "it4all-regex-cheatsheet");
} }
function RegexExerciseComponent_div_29_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 19);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](1, "div", 20);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](2, "Fehler beim Parsen des regul\u00E4ren Ausdrucks:");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](3, "div", 21);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](4, "pre");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtextInterpolate"](ctx_r3.queryError.message);
} }
function RegexExerciseComponent_ng_container_30_it4all_solution_saved_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](0, "it4all-solution-saved", 26);
} if (rf & 2) {
    const ctx_r6 = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("solutionSaved", ctx_r6.correctionResult.solutionSaved);
} }
function RegexExerciseComponent_ng_container_30_div_5_div_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 29);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](1, "it4all-regex-matching-result", 30);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} if (rf & 2) {
    const matchingResult_r10 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("matchingResult", matchingResult_r10);
} }
function RegexExerciseComponent_ng_container_30_div_5_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 27);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](1, RegexExerciseComponent_ng_container_30_div_5_div_1_Template, 2, 1, "div", 28);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r7 = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngForOf", ctx_r7.regexMatchingResult.matchingResults);
} }
function RegexExerciseComponent_ng_container_30_ng_container_6_div_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 22);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](1, "it4all-regex-extraction-result", 32);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} if (rf & 2) {
    const extractionResult_r12 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("extractionResult", extractionResult_r12);
} }
function RegexExerciseComponent_ng_container_30_ng_container_6_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](1, RegexExerciseComponent_ng_container_30_ng_container_6_div_1_Template, 2, 1, "div", 31);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r8 = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngForOf", ctx_r8.regexExtractionResult.extractionResults);
} }
function RegexExerciseComponent_ng_container_30_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](1, "div", 22);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](2, RegexExerciseComponent_ng_container_30_it4all_solution_saved_2_Template, 1, 1, "it4all-solution-saved", 23);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](3, "div", 22);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](4, "it4all-points-notification", 24);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](5, RegexExerciseComponent_ng_container_30_div_5_Template, 2, 1, "div", 25);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](6, RegexExerciseComponent_ng_container_30_ng_container_6_Template, 2, 1, "ng-container", 15);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r4 = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx_r4.correctionResult);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("points", ctx_r4.abstractResult.points)("maxPoints", ctx_r4.abstractResult.maxPoints);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx_r4.regexMatchingResult);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx_r4.regexExtractionResult);
} }
function RegexExerciseComponent_ng_container_35_ng_container_1_br_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](0, "br");
} }
function RegexExerciseComponent_ng_container_35_ng_container_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](1, "it4all-string-sample-sol", 34);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](2, RegexExerciseComponent_ng_container_35_ng_container_1_br_2_Template, 1, 0, "br", 15);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const sample_r14 = ctx.$implicit;
    const last_r15 = ctx.last;
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("sample", sample_r14);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", !last_r15);
} }
function RegexExerciseComponent_ng_container_35_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](1, RegexExerciseComponent_ng_container_35_ng_container_1_Template, 3, 2, "ng-container", 33);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r5 = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngForOf", ctx_r5.sampleSolutions);
} }
const _c0 = function () { return ["../.."]; };
function getIdForRegexExercisePart(regexExPart) {
    switch (regexExPart) {
        case _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["RegexExPart"].RegexSingleExPart:
            return 'regex';
    }
}
class RegexExerciseComponent extends _helpers_component_with_exercise_directive__WEBPACK_IMPORTED_MODULE_0__["ComponentWithExerciseDirective"] {
    constructor(regexCorrectionGQL, dexieService) {
        super(regexCorrectionGQL, dexieService);
        this.solution = '';
        this.showInfo = false;
        this.partId = getIdForRegexExercisePart(_services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["RegexExPart"].RegexSingleExPart);
        // Sample solutions
        this.displaySampleSolutions = false;
    }
    ngOnInit() {
        this.loadOldSolutionAbstract(this.exerciseFragment, this.partId, (oldSol) => this.solution = oldSol);
    }
    toggleSampleSolutions() {
        this.displaySampleSolutions = !this.displaySampleSolutions;
    }
    get sampleSolutions() {
        return this.contentFragment.regexSampleSolutions;
    }
    // Correction
    getSolution() {
        return this.solution;
    }
    getMutationQueryVariables() {
        return {
            exId: this.exerciseFragment.exerciseId,
            collId: this.exerciseFragment.collectionId,
            solution: this.getSolution(),
            part: _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["RegexExPart"].RegexSingleExPart,
        };
    }
    correct() {
        if (this.solution === undefined || this.solution.length === 0) {
            alert('Sie können keine leere Lösung abgeben!');
            return;
        }
        this.correctAbstract(this.exerciseFragment, this.partId);
    }
    // Results
    get correctionResult() {
        var _a, _b;
        return (_b = (_a = this.resultQuery) === null || _a === void 0 ? void 0 : _a.me.regexExercise) === null || _b === void 0 ? void 0 : _b.correct;
    }
    get abstractResult() {
        var _a;
        return (_a = this.correctionResult) === null || _a === void 0 ? void 0 : _a.result;
    }
    get regexMatchingResult() {
        var _a;
        return ((_a = this.abstractResult) === null || _a === void 0 ? void 0 : _a.__typename) === 'RegexMatchingResult' ? this.abstractResult : null;
    }
    get regexExtractionResult() {
        var _a;
        return ((_a = this.abstractResult) === null || _a === void 0 ? void 0 : _a.__typename) === 'RegexExtractionResult' ? this.abstractResult : undefined;
    }
    // Other
    // FIXME: make directive?
    handleKeyboardEvent(event) {
        if (event.key === 'Enter') {
            // if (this.correct) {
            // this.update();
            // } else {
            this.correct();
            // }
        }
    }
}
RegexExerciseComponent.ɵfac = function RegexExerciseComponent_Factory(t) { return new (t || RegexExerciseComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdirectiveInject"](_services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["RegexCorrectionGQL"]), _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdirectiveInject"](_services_dexie_service__WEBPACK_IMPORTED_MODULE_3__["DexieService"])); };
RegexExerciseComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdefineComponent"]({ type: RegexExerciseComponent, selectors: [["it4all-regex-exercise"]], hostBindings: function RegexExerciseComponent_HostBindings(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("keypress", function RegexExerciseComponent_keypress_HostBindingHandler($event) { return ctx.handleKeyboardEvent($event); }, false, _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵresolveDocument"]);
    } }, inputs: { exerciseFragment: "exerciseFragment", contentFragment: "contentFragment" }, features: [_angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵInheritDefinitionFeature"]], decls: 36, vars: 13, consts: [[1, "container", "is-fluid"], [1, "columns"], [1, "column", "is-two-fifths-desktop"], [1, "title", "is-3", "has-text-centered"], [1, "notification", "is-light-grey"], [1, "field", "has-addons"], [1, "control"], ["for", "solution", 1, "button", "is-static"], [1, "control", "is-expanded"], ["type", "text", "id", "solution", "placeholder", "Ihre L\u00F6sung", "autofocus", "", "autocomplete", "off", 1, "input", 3, "ngModel", "ngModelChange"], [1, "column"], [1, "button", "is-link", "is-fullwidth", 3, "click"], [1, "button", "is-dark", "is-fullwidth", 3, "routerLink"], [1, "buttons"], [1, "button", "is-info", "is-fullwidth", 3, "click"], [4, "ngIf"], [3, "title"], ["class", "message is-danger", 4, "ngIf"], [1, "button", "is-primary", "is-fullwidth", 3, "click"], [1, "message", "is-danger"], [1, "message-header"], [1, "message-body"], [1, "my-3"], [3, "solutionSaved", 4, "ngIf"], [3, "points", "maxPoints"], ["class", "columns is-multiline my-3", 4, "ngIf"], [3, "solutionSaved"], [1, "columns", "is-multiline", "my-3"], ["class", "column is-half-desktop", 4, "ngFor", "ngForOf"], [1, "column", "is-half-desktop"], [3, "matchingResult"], ["class", "my-3", 4, "ngFor", "ngForOf"], [3, "extractionResult"], [4, "ngFor", "ngForOf"], [3, "sample"]], template: function RegexExerciseComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](1, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](2, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](3, "h1", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](4, "Aufgabenstellung");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](5, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](6);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](7, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](8, "div", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](9, "label", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](10, "Ihre L\u00F6sung:");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](11, "div", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](12, "input", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("ngModelChange", function RegexExerciseComponent_Template_input_ngModelChange_12_listener($event) { return ctx.solution = $event; });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](13, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](14, "div", 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](15, "button", 11);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("click", function RegexExerciseComponent_Template_button_click_15_listener() { return ctx.correct(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](16, "L\u00F6sung testen");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](17, "div", 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](18, "a", 12);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](19, "Bearbeiten beenden");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](20, "div", 13);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](21, "button", 14);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("click", function RegexExerciseComponent_Template_button_click_21_listener() { return ctx.showInfo = !ctx.showInfo; });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](22, " Hilfe\u00A0");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](23, RegexExerciseComponent_span_23_Template, 2, 0, "span", 15);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](24, RegexExerciseComponent_span_24_Template, 2, 0, "span", 15);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](25, RegexExerciseComponent_it4all_regex_cheatsheet_25_Template, 1, 0, "it4all-regex-cheatsheet", 15);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](26, "div", 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](27, "it4all-tabs");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](28, "it4all-tab", 16);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](29, RegexExerciseComponent_div_29_Template, 6, 1, "div", 17);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](30, RegexExerciseComponent_ng_container_30_Template, 7, 5, "ng-container", 15);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](31, "it4all-tab", 16);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](32, "div", 13);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](33, "button", 18);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("click", function RegexExerciseComponent_Template_button_click_33_listener() { return ctx.toggleSampleSolutions(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](34);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](35, RegexExerciseComponent_ng_container_35_Template, 2, 1, "ng-container", 15);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](6);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtextInterpolate"](ctx.exerciseFragment.text);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](6);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngModel", ctx.solution);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](6);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("routerLink", _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵpureFunction0"](12, _c0));
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](5);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", !ctx.showInfo);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx.showInfo);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx.showInfo);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("title", ctx.correctionTabTitle);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx.queryError);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx.resultQuery);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("title", ctx.sampleSolutionsTabTitle);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtextInterpolate1"](" Musterl\u00F6sungen ", ctx.displaySampleSolutions ? "ausblenden" : "anzeigen", " ");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx.displaySampleSolutions);
    } }, directives: [_angular_forms__WEBPACK_IMPORTED_MODULE_4__["DefaultValueAccessor"], _angular_forms__WEBPACK_IMPORTED_MODULE_4__["NgControlStatus"], _angular_forms__WEBPACK_IMPORTED_MODULE_4__["NgModel"], _angular_router__WEBPACK_IMPORTED_MODULE_5__["RouterLinkWithHref"], _angular_common__WEBPACK_IMPORTED_MODULE_6__["NgIf"], _shared_tabs_tabs_component__WEBPACK_IMPORTED_MODULE_7__["TabsComponent"], _shared_tab_tab_component__WEBPACK_IMPORTED_MODULE_8__["TabComponent"], _regex_cheatsheet_regex_cheatsheet_component__WEBPACK_IMPORTED_MODULE_9__["RegexCheatsheetComponent"], _shared_points_notification_points_notification_component__WEBPACK_IMPORTED_MODULE_10__["PointsNotificationComponent"], _shared_solution_saved_solution_saved_component__WEBPACK_IMPORTED_MODULE_11__["SolutionSavedComponent"], _angular_common__WEBPACK_IMPORTED_MODULE_6__["NgForOf"], _regex_matching_result_regex_matching_result_component__WEBPACK_IMPORTED_MODULE_12__["RegexMatchingResultComponent"], _regex_extraction_result_regex_extraction_result_component__WEBPACK_IMPORTED_MODULE_13__["RegexExtractionResultComponent"], _components_string_sample_sol_string_sample_sol_component__WEBPACK_IMPORTED_MODULE_14__["StringSampleSolComponent"]], encapsulation: 2 });


/***/ }),

/***/ "C5uP":
/*!***********************************************************************************!*\
  !*** ./src/app/tools/collection-tools/xml/xml-exercise/xml-exercise.component.ts ***!
  \***********************************************************************************/
/*! exports provided: getIdForXmlExPart, getXmlGrammarContent, getXmlDocumentContent, XmlExerciseComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "getIdForXmlExPart", function() { return getIdForXmlExPart; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "getXmlGrammarContent", function() { return getXmlGrammarContent; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "getXmlDocumentContent", function() { return getXmlDocumentContent; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "XmlExerciseComponent", function() { return XmlExerciseComponent; });
/* harmony import */ var _helpers_component_with_exercise_directive__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../_helpers/component-with-exercise.directive */ "TRIe");
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../../../_services/apollo_services */ "o20/");
/* harmony import */ var codemirror_mode_dtd_dtd__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! codemirror/mode/dtd/dtd */ "/YIB");
/* harmony import */ var codemirror_mode_dtd_dtd__WEBPACK_IMPORTED_MODULE_2___default = /*#__PURE__*/__webpack_require__.n(codemirror_mode_dtd_dtd__WEBPACK_IMPORTED_MODULE_2__);
/* harmony import */ var codemirror_mode_xml_xml__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! codemirror/mode/xml/xml */ "1eCo");
/* harmony import */ var codemirror_mode_xml_xml__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(codemirror_mode_xml_xml__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _services_dexie_service__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../../../_services/dexie.service */ "4di/");
/* harmony import */ var _components_exercise_files_editor_exercise_files_editor_component__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../../_components/exercise-files-editor/exercise-files-editor.component */ "346A");
/* harmony import */ var _shared_tabs_tabs_component__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../../../shared/tabs/tabs.component */ "b4kd");
/* harmony import */ var _shared_tab_tab_component__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../../../../shared/tab/tab.component */ "4YYW");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _shared_solution_saved_solution_saved_component__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ../../../../shared/solution-saved/solution-saved.component */ "rqf4");
/* harmony import */ var _shared_points_notification_points_notification_component__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ../../../../shared/points-notification/points-notification.component */ "ef8k");
/* harmony import */ var _components_xml_document_correction_xml_document_correction_component__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ../_components/xml-document-correction/xml-document-correction.component */ "PLyK");
/* harmony import */ var _components_xml_element_line_match_result_xml_element_line_match_result_component__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! ../_components/xml-element-line-match-result/xml-element-line-match-result.component */ "wDY7");
















function XmlExerciseComponent_ng_container_8_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](1, "p", 16);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](2, "Erstellen Sie eine DTD zu folgender Beschreibung:");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](3, "p");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](5, "p", 17);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](6, " Hinweis: Benutzen Sie die in Klammern angegebenen Element- bzw. Attributnamen. Falls nichts anderes angegeben ist, sollen die Elemente nur Text enthalten. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate"](ctx_r0.contentFragment.grammarDescription);
} }
function XmlExerciseComponent_ng_template_9_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](0);
} if (rf & 2) {
    const ctx_r2 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate"](ctx_r2.exerciseFragment.text);
} }
function XmlExerciseComponent_div_19_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "div", 18);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](1, "div", 19);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](2, "Es gab einen Fehler bei der Korrektur:");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](3, "div", 20);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](4, "pre");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate"](ctx_r3.queryError.message);
} }
function XmlExerciseComponent_ng_container_20_it4all_points_notification_4_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](0, "it4all-points-notification", 25);
} if (rf & 2) {
    const ctx_r6 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("points", ctx_r6.xmlResult.points)("maxPoints", ctx_r6.xmlResult.maxPoints);
} }
function XmlExerciseComponent_ng_container_20_div_5_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "div", 21);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](1, "it4all-xml-document-correction", 26);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r7 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("result", ctx_r7.documentResult);
} }
function XmlExerciseComponent_ng_container_20_ng_container_6_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainer"](0);
} }
function XmlExerciseComponent_ng_container_20_div_7_div_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "div", 18);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](1, "div", 19);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](2, " Fehler beim Parse von \"");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](3, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](5, "\": ");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](6, "div", 20);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](7, "pre");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](8, "                    ");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](9, "code", 28);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](10);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](11, "\n                  ");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const parseError_r11 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate"](parseError_r11.parsedLine);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](6);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate"](parseError_r11.msg);
} }
function XmlExerciseComponent_ng_container_20_div_7_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "div", 21);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](1, XmlExerciseComponent_ng_container_20_div_7_div_1_Template, 12, 2, "div", 27);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](2, "it4all-xml-element-line-match-result", 26);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r9 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngForOf", ctx_r9.grammarResult.parseErrors);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("result", ctx_r9.grammarResult.results);
} }
function XmlExerciseComponent_ng_container_20_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](1, "div", 21);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](2, "it4all-solution-saved", 22);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](3, "div", 21);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](4, XmlExerciseComponent_ng_container_20_it4all_points_notification_4_Template, 1, 2, "it4all-points-notification", 23);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](5, XmlExerciseComponent_ng_container_20_div_5_Template, 2, 1, "div", 24);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](6, XmlExerciseComponent_ng_container_20_ng_container_6_Template, 1, 0, "ng-container", 12);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](7, XmlExerciseComponent_ng_container_20_div_7_Template, 3, 2, "div", 24);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r4 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("solutionSaved", ctx_r4.correctionResult.solutionSaved);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx_r4.isGrammarPart && ctx_r4.xmlResult);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx_r4.documentResult);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx_r4.documentResult && ctx_r4.documentResult.errors.length === 0);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx_r4.grammarResult);
} }
function XmlExerciseComponent_div_25_div_1_br_3_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](0, "br");
} }
function XmlExerciseComponent_div_25_div_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "div");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](1, "pre");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](3, XmlExerciseComponent_div_25_div_1_br_3_Template, 1, 0, "br", 12);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const sampleSolution_r13 = ctx.$implicit;
    const last_r14 = ctx.last;
    const ctx_r12 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate"](ctx_r12.isGrammarPart ? sampleSolution_r13.grammar : sampleSolution_r13.document);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", !last_r14);
} }
function XmlExerciseComponent_div_25_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "div", 29);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](1, XmlExerciseComponent_div_25_div_1_Template, 4, 2, "div", 30);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r5 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngForOf", ctx_r5.sampleSolutions);
} }
const _c0 = function () { return ["../.."]; };
function getIdForXmlExPart(xmlExPart) {
    switch (xmlExPart) {
        case _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["XmlExPart"].DocumentCreationXmlPart:
            return 'document';
        case _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["XmlExPart"].GrammarCreationXmlPart:
            return 'grammar';
    }
}
function getXmlGrammarContent(rootNode) {
    return `<!ELEMENT ${rootNode} (EMPTY)>`;
}
function getXmlDocumentContent(rootNode) {
    return `
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE ${rootNode} SYSTEM "${rootNode}.dtd">
<${rootNode}>
</${rootNode}>`.trim();
}
class XmlExerciseComponent extends _helpers_component_with_exercise_directive__WEBPACK_IMPORTED_MODULE_0__["ComponentWithExerciseDirective"] {
    constructor(xmlCorrectionGQL, dexieService) {
        super(xmlCorrectionGQL, dexieService);
        this.exerciseFileFragments = [];
        // Sample solutions
        this.displaySampleSolutions = false;
    }
    ngOnInit() {
        const rootNode = this.contentFragment.rootNode;
        this.partId = getIdForXmlExPart(this.contentFragment.xmlPart);
        this.isGrammarPart = this.partId === 'grammar';
        const grammarFileName = `${rootNode}.dtd`;
        this.grammarFile = {
            name: grammarFileName,
            content: this.isGrammarPart ? getXmlGrammarContent(rootNode) : this.contentFragment.xmlSampleSolutions[0].grammar,
            fileType: 'dtd',
            editable: this.isGrammarPart,
        };
        const documentFileName = `${rootNode}.xml`;
        this.documentFile = {
            name: documentFileName,
            content: getXmlDocumentContent(rootNode),
            fileType: 'xml',
            editable: !this.isGrammarPart,
        };
        this.exerciseFileFragments = [this.grammarFile, this.documentFile];
        this.loadOldSolutionAbstract(this.exerciseFragment, this.partId, (oldSol) => {
            this.grammarFile.content = oldSol.grammar;
            this.documentFile.content = oldSol.document;
            // do not delete or else editor does not get updated...
            this.exerciseFileFragments = [this.grammarFile, this.documentFile];
        });
    }
    // Correction
    getSolution() {
        return {
            grammar: this.grammarFile.content,
            document: this.documentFile.content
        };
    }
    getMutationQueryVariables() {
        return {
            exId: this.exerciseFragment.exerciseId,
            collId: this.exerciseFragment.collectionId,
            solution: this.getSolution(),
            part: this.contentFragment.xmlPart,
        };
    }
    correct() {
        this.correctAbstract(this.exerciseFragment, this.partId);
    }
    // Results
    get correctionResult() {
        var _a, _b;
        return (_b = (_a = this.resultQuery) === null || _a === void 0 ? void 0 : _a.me.xmlExercise) === null || _b === void 0 ? void 0 : _b.correct;
    }
    get xmlResult() {
        var _a;
        return (_a = this.correctionResult) === null || _a === void 0 ? void 0 : _a.result;
    }
    get grammarResult() {
        var _a;
        return (_a = this.xmlResult) === null || _a === void 0 ? void 0 : _a.grammarResult;
    }
    get documentResult() {
        var _a;
        return (_a = this.xmlResult) === null || _a === void 0 ? void 0 : _a.documentResult;
    }
    toggleSampleSolutions() {
        this.displaySampleSolutions = !this.displaySampleSolutions;
    }
    get sampleSolutions() {
        return this.contentFragment.xmlSampleSolutions;
    }
}
XmlExerciseComponent.ɵfac = function XmlExerciseComponent_Factory(t) { return new (t || XmlExerciseComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵdirectiveInject"](_services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["XmlCorrectionGQL"]), _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵdirectiveInject"](_services_dexie_service__WEBPACK_IMPORTED_MODULE_5__["DexieService"])); };
XmlExerciseComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵdefineComponent"]({ type: XmlExerciseComponent, selectors: [["it4all-xml-exercise"]], inputs: { exerciseFragment: "exerciseFragment", contentFragment: "contentFragment" }, features: [_angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵInheritDefinitionFeature"]], decls: 26, vars: 15, consts: [[1, "container", "is-fluid"], [1, "columns"], [1, "column", "is-half-desktop"], [3, "exerciseFileFragments", "mode"], [3, "title"], [1, "notification", "is-light-grey"], [4, "ngIf", "ngIfElse"], ["documentTextBlock", ""], [1, "column"], [1, "button", "is-link", "is-fullwidth", 3, "click"], [1, "button", "is-dark", "is-fullwidth", 3, "routerLink"], ["class", "message is-danger", 4, "ngIf"], [4, "ngIf"], [1, "buttons"], [1, "button", "is-primary", "is-fullwidth", 3, "click"], ["class", "overflowHiddenDiv", 4, "ngIf"], [1, "has-text-weight-bold"], [1, "is-italic", "has-text-info"], [1, "message", "is-danger"], [1, "message-header"], [1, "message-body"], [1, "my-3"], [3, "solutionSaved"], [3, "points", "maxPoints", 4, "ngIf"], ["class", "my-3", 4, "ngIf"], [3, "points", "maxPoints"], [3, "result"], ["class", "message is-danger", 4, "ngFor", "ngForOf"], [1, "has-text-danger"], [1, "overflowHiddenDiv"], [4, "ngFor", "ngForOf"]], template: function XmlExerciseComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](1, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](2, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](3, "it4all-exercise-files-editor", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](4, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](5, "it4all-tabs");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](6, "it4all-tab", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](7, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](8, XmlExerciseComponent_ng_container_8_Template, 7, 1, "ng-container", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](9, XmlExerciseComponent_ng_template_9_Template, 1, 1, "ng-template", null, 7, _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplateRefExtractor"]);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](11, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](12, "div", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](13, "button", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵlistener"]("click", function XmlExerciseComponent_Template_button_click_13_listener() { return ctx.correct(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](14, " Korrektur ");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](15, "div", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](16, "a", 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](17, "Bearbeiten beenden");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](18, "it4all-tab", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](19, XmlExerciseComponent_div_19_Template, 6, 1, "div", 11);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](20, XmlExerciseComponent_ng_container_20_Template, 8, 5, "ng-container", 12);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](21, "it4all-tab", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](22, "div", 13);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](23, "button", 14);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵlistener"]("click", function XmlExerciseComponent_Template_button_click_23_listener() { return ctx.toggleSampleSolutions(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](24);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](25, XmlExerciseComponent_div_25_Template, 2, 1, "div", 15);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    } if (rf & 2) {
        const _r1 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵreference"](10);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("exerciseFileFragments", ctx.exerciseFileFragments)("mode", "xml");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("title", "Aufgabenstellung");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx.isGrammarPart)("ngIfElse", _r1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](5);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵclassProp"]("is-loading", ctx.isCorrecting);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("routerLink", _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵpureFunction0"](14, _c0));
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("title", ctx.correctionTabTitle);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx.queryError);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx.resultQuery);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("title", ctx.sampleSolutionsTabTitle);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate1"](" Musterl\u00F6sung ", ctx.displaySampleSolutions ? "ausblenden" : "anzeigen", " ");
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx.displaySampleSolutions);
    } }, directives: [_components_exercise_files_editor_exercise_files_editor_component__WEBPACK_IMPORTED_MODULE_6__["ExerciseFilesEditorComponent"], _shared_tabs_tabs_component__WEBPACK_IMPORTED_MODULE_7__["TabsComponent"], _shared_tab_tab_component__WEBPACK_IMPORTED_MODULE_8__["TabComponent"], _angular_common__WEBPACK_IMPORTED_MODULE_9__["NgIf"], _angular_router__WEBPACK_IMPORTED_MODULE_10__["RouterLinkWithHref"], _shared_solution_saved_solution_saved_component__WEBPACK_IMPORTED_MODULE_11__["SolutionSavedComponent"], _shared_points_notification_points_notification_component__WEBPACK_IMPORTED_MODULE_12__["PointsNotificationComponent"], _components_xml_document_correction_xml_document_correction_component__WEBPACK_IMPORTED_MODULE_13__["XmlDocumentCorrectionComponent"], _angular_common__WEBPACK_IMPORTED_MODULE_9__["NgForOf"], _components_xml_element_line_match_result_xml_element_line_match_result_component__WEBPACK_IMPORTED_MODULE_14__["XmlElementLineMatchResultComponent"]], encapsulation: 2 });


/***/ }),

/***/ "CohO":
/*!*******************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/lessons/lesson-questions/lesson-questions-content.component.ts ***!
  \*******************************************************************************************************/
/*! exports provided: LessonQuestionsContentComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LessonQuestionsContentComponent", function() { return LessonQuestionsContentComponent; });
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common */ "ofXK");



function LessonQuestionsContentComponent_div_0_div_5_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "label", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "input", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵlistener"]("change", function LessonQuestionsContentComponent_div_0_div_5_Template_input_change_2_listener() { const answer_r3 = ctx.$implicit; return answer_r3.selected = !answer_r3.selected; });
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3, " \u00A0 ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](4, "span", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const answer_r3 = ctx.$implicit;
    const questionWithSelectableAnswer_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]().$implicit;
    const ctx_r2 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngClass", ctx_r2.isCorrectedAndCorrect(questionWithSelectableAnswer_r1, answer_r3));
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("innerHTML", answer_r3.answer, _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵsanitizeHtml"]);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](answer_r3.answer);
} }
function LessonQuestionsContentComponent_div_0_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "header", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "p", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](4, "div", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](5, LessonQuestionsContentComponent_div_0_div_5_Template, 6, 3, "div", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](6, "button", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵlistener"]("click", function LessonQuestionsContentComponent_div_0_Template_button_click_6_listener() { const questionWithSelectableAnswer_r1 = ctx.$implicit; return questionWithSelectableAnswer_r1.corrected = true; });
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](7, " Korrektur ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const questionWithSelectableAnswer_r1 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("innerHTML", questionWithSelectableAnswer_r1.question.questionText, _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵsanitizeHtml"]);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" ", questionWithSelectableAnswer_r1.question.questionText, " ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", questionWithSelectableAnswer_r1.answers);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("disabled", questionWithSelectableAnswer_r1.corrected);
} }
function shuffleArray(array) {
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
}
class LessonQuestionsContentComponent {
    ngOnInit() {
        this.selectableAnswers = this.content.questions.map((question) => {
            const answers = question.answers.map((answer) => {
                return Object.assign({ selected: false }, answer);
            });
            shuffleArray(answers);
            return { question, corrected: false, answers };
        });
    }
    isCorrectedAndCorrect(questionWithSelectableAnswer, answer) {
        // FIXME: implement!
        if (questionWithSelectableAnswer.corrected) {
            return 'TODO!';
        }
        else {
            return '';
        }
    }
}
LessonQuestionsContentComponent.ɵfac = function LessonQuestionsContentComponent_Factory(t) { return new (t || LessonQuestionsContentComponent)(); };
LessonQuestionsContentComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineComponent"]({ type: LessonQuestionsContentComponent, selectors: [["it4all-lesson-questions-content"]], inputs: { content: "content" }, decls: 1, vars: 1, consts: [["class", "card my-3", 4, "ngFor", "ngForOf"], [1, "card", "my-3"], [1, "card-header"], [1, "card-header-title", 3, "innerHTML"], [1, "card-content"], ["class", "field", 4, "ngFor", "ngForOf"], [1, "button", "is-link", 3, "disabled", "click"], [1, "field"], [1, "checkbox", 3, "ngClass"], ["type", "checkbox", 3, "change"], [3, "innerHTML"]], template: function LessonQuestionsContentComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](0, LessonQuestionsContentComponent_div_0_Template, 8, 4, "div", 0);
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx.selectableAnswers);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_2__["NgForOf"], _angular_common__WEBPACK_IMPORTED_MODULE_2__["NgClass"]], encapsulation: 2 });


/***/ }),

/***/ "DeMQ":
/*!***********************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/sql/_components/query-result-table/query-result-table.component.ts ***!
  \***********************************************************************************************************/
/*! exports provided: QueryResultTableComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "QueryResultTableComponent", function() { return QueryResultTableComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/common */ "ofXK");


function QueryResultTableComponent_th_3_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "th");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const colName_r2 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](colName_r2);
} }
function QueryResultTableComponent_tr_5_td_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "td", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const colName_r5 = ctx.$implicit;
    const row_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]().$implicit;
    const ctx_r4 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngClass", ctx_r4.getCell(row_r3, colName_r5).different ? "is-light-danger" : "");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx_r4.getCell(row_r3, colName_r5).content);
} }
function QueryResultTableComponent_tr_5_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "tr");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](1, QueryResultTableComponent_tr_5_td_1_Template, 2, 2, "td", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx_r1.queryResult.columnNames);
} }
class QueryResultTableComponent {
    constructor() {
    }
    ngOnInit() {
    }
    getCell(row, key) {
        return row.cells.find((r) => r.key === key).value;
    }
}
QueryResultTableComponent.ɵfac = function QueryResultTableComponent_Factory(t) { return new (t || QueryResultTableComponent)(); };
QueryResultTableComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: QueryResultTableComponent, selectors: [["it4all-query-result-table"]], inputs: { queryResult: "queryResult" }, decls: 6, vars: 2, consts: [[1, "table", "is-bordered", "is-fullwidth"], [4, "ngFor", "ngForOf"], [3, "ngClass", 4, "ngFor", "ngForOf"], [3, "ngClass"]], template: function QueryResultTableComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "table", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "thead");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](2, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](3, QueryResultTableComponent_th_3_Template, 2, 1, "th", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](4, "tbody");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](5, QueryResultTableComponent_tr_5_Template, 2, 1, "tr", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx.queryResult.columnNames);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx.queryResult.rows);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_1__["NgForOf"], _angular_common__WEBPACK_IMPORTED_MODULE_1__["NgClass"]], encapsulation: 2 });


/***/ }),

/***/ "Dja1":
/*!**********************************************************************************************!*\
  !*** ./src/app/tools/random-tools/bool/bool-drawing/_bool-drawing-model/formulaExtractor.ts ***!
  \**********************************************************************************************/
/*! exports provided: extractFormulaFromGraph */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "extractFormulaFromGraph", function() { return extractFormulaFromGraph; });
/* harmony import */ var jointjs__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! jointjs */ "iuCI");
/* harmony import */ var _model_bool_node__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../_model/bool-node */ "oi/H");
/* harmony import */ var _boolDrawing__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./boolDrawing */ "Bd0U");



function getSubOutputFormula(wire, graph) {
    const inputGate = graph.getCell(wire.prop('source').id);
    return getOutputFormula(inputGate, graph);
}
function getOutputFormulaFromSingleInputGate(gate, ingoingWires, graph) {
    if (gate instanceof jointjs__WEBPACK_IMPORTED_MODULE_0__["shapes"].logic.Not) {
        if (ingoingWires.length !== 1) {
            // Not cannot have more than 1 ingoing wire...
            gate.findView(_boolDrawing__WEBPACK_IMPORTED_MODULE_2__["paper"]).highlight();
            return undefined;
        }
        const sourceInput = getSubOutputFormula(ingoingWires[0], graph);
        return sourceInput ? new _model_bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanNot"](sourceInput) : undefined;
    }
    else if (gate instanceof jointjs__WEBPACK_IMPORTED_MODULE_0__["shapes"].logic.Repeater) {
        // TODO: Repeater!
        return undefined;
    }
    else {
        return undefined;
    }
}
function getOutputFormulaFromDoubleInputGate(gate, ingoingWires, graph) {
    if (ingoingWires.length !== 2) {
        _boolDrawing__WEBPACK_IMPORTED_MODULE_2__["paper"].findViewByModel(gate.id).highlight();
        return undefined;
    }
    const firstInput = getSubOutputFormula(ingoingWires[0], graph);
    const secondInput = getSubOutputFormula(ingoingWires[1], graph);
    if (firstInput && secondInput) {
        const operationString = gate.attributes.type.split('\.')[1].toLocaleLowerCase();
        return Object(_model_bool_node__WEBPACK_IMPORTED_MODULE_1__["instantiateOperator"])(firstInput, operationString, secondInput);
    }
    else {
        return undefined;
    }
}
function getOutputFormula(gate, graph) {
    const ingoingWires = graph.getConnectedLinks(gate, { inbound: true });
    if (gate instanceof jointjs__WEBPACK_IMPORTED_MODULE_0__["shapes"].logic.Gate11) {
        return getOutputFormulaFromSingleInputGate(gate, ingoingWires, graph);
    }
    else if (gate instanceof jointjs__WEBPACK_IMPORTED_MODULE_0__["shapes"].logic.Gate21) {
        // gate is and, nand, or, nor, xor, xnor, equiv or impl
        return getOutputFormulaFromDoubleInputGate(gate, ingoingWires, graph);
    }
    else if (gate instanceof jointjs__WEBPACK_IMPORTED_MODULE_0__["shapes"].logic.IO) {
        if (gate instanceof jointjs__WEBPACK_IMPORTED_MODULE_0__["shapes"].logic.Input) {
            return new _model_bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanVariable"](gate.attr('logicSymbol').toString().charAt(0));
        }
        else if (gate instanceof jointjs__WEBPACK_IMPORTED_MODULE_0__["shapes"].logic.Output) {
            if (ingoingWires.length !== 1) {
                gate.findView(_boolDrawing__WEBPACK_IMPORTED_MODULE_2__["paper"]).highlight();
                return undefined;
            }
            return getSubOutputFormula(ingoingWires[0], graph);
        }
    }
}
function extractFormulaFromGraph(graph) {
    const formulas = [];
    for (const element of graph.getElements()) {
        _boolDrawing__WEBPACK_IMPORTED_MODULE_2__["paper"].findViewByModel(element).unhighlight();
        if (element instanceof jointjs__WEBPACK_IMPORTED_MODULE_0__["shapes"].logic.Output) {
            const variable = new _model_bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanVariable"](element.attr('logicSymbol').toString().charAt(0));
            formulas.push([variable, getOutputFormula(element, graph)]);
        }
    }
    return formulas;
}


/***/ }),

/***/ "EK2f":
/*!***********************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/programming/programming-exercise/programming-exercise.component.ts ***!
  \***********************************************************************************************************/
/*! exports provided: ProgrammingExerciseComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ProgrammingExerciseComponent", function() { return ProgrammingExerciseComponent; });
/* harmony import */ var _helpers_component_with_exercise_directive__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../_helpers/component-with-exercise.directive */ "TRIe");
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../../../_services/apollo_services */ "o20/");
/* harmony import */ var _components_files_exercise_files_exercise_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../_components/files-exercise/files-exercise.component */ "Emuw");
/* harmony import */ var codemirror_mode_python_python__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! codemirror/mode/python/python */ "25Eh");
/* harmony import */ var codemirror_mode_python_python__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(codemirror_mode_python_python__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _services_dexie_service__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../../../_services/dexie.service */ "4di/");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _shared_solution_saved_solution_saved_component__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../../../shared/solution-saved/solution-saved.component */ "rqf4");
/* harmony import */ var _shared_points_notification_points_notification_component__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../../../../shared/points-notification/points-notification.component */ "ef8k");
/* harmony import */ var _results_programming_unit_test_result_programming_unit_test_result_component__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ../_results/programming-unit-test-result/programming-unit-test-result.component */ "lIHn");
/* harmony import */ var _results_programming_normal_result_programming_implementation_correction_result_component__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ../_results/programming-normal-result/programming-implementation-correction-result.component */ "3HtT");













function ProgrammingExerciseComponent_div_4_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "div", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](1, "div", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](2, "Es gab einen internen Fehler bei der Korrektur:");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](3, "div", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](4, "pre");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate"](ctx_r0.queryError.message);
} }
function ProgrammingExerciseComponent_ng_container_5_div_5_ul_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "ul");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](1, "it4all-programming-unit-test-result", 16);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const unitTestResult_r5 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("unitTestResult", unitTestResult_r5);
} }
function ProgrammingExerciseComponent_ng_container_5_div_5_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "div", 14);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](1, ProgrammingExerciseComponent_ng_container_5_div_5_ul_1_Template, 2, 1, "ul", 15);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r2 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngForOf", ctx_r2.unitTestResults);
} }
function ProgrammingExerciseComponent_ng_container_5_it4all_programming_normal_result_6_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](0, "it4all-programming-normal-result", 17);
} if (rf & 2) {
    const ctx_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("normalResult", ctx_r3.implementationCorrectionResult);
} }
function ProgrammingExerciseComponent_ng_container_5_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](1, "div", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](2, "it4all-solution-saved", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](3, "div", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](4, "it4all-points-notification", 11);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](5, ProgrammingExerciseComponent_ng_container_5_div_5_Template, 2, 1, "div", 12);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](6, ProgrammingExerciseComponent_ng_container_5_it4all_programming_normal_result_6_Template, 1, 1, "it4all-programming-normal-result", 13);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("solutionSaved", ctx_r1.correctionResult.solutionSaved);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("points", ctx_r1.abstractResult.points)("maxPoints", ctx_r1.abstractResult.maxPoints);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx_r1.unitTestResults && ctx_r1.unitTestResults.length > 0);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx_r1.implementationCorrectionResult);
} }
function getIdForProgExPart(progExPart) {
    switch (progExPart) {
        case _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["ProgExPart"].ActivityDiagram:
        case _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["ProgExPart"].Implementation:
            return 'implementation';
        case _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["ProgExPart"].TestCreation:
            return 'testCreation';
    }
}
class ProgrammingExerciseComponent extends _helpers_component_with_exercise_directive__WEBPACK_IMPORTED_MODULE_0__["ComponentWithExerciseDirective"] {
    constructor(programmingCorrectionGQL, dexieService) {
        super(programmingCorrectionGQL, dexieService);
        this.exerciseFiles = [];
    }
    ngOnInit() {
        this.partId = getIdForProgExPart(this.contentFragment.programmingPart);
        this.exerciseFiles = (this.contentFragment.programmingPart === _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["ProgExPart"].Implementation)
            ? this.contentFragment.implementationPart.files
            : this.contentFragment.unitTestPart.unitTestFiles;
        this.loadOldSolutionAbstract(this.exerciseFragment, this.partId, (oldSol) => this.exerciseFiles = oldSol.files);
    }
    // Sample solutions
    get sampleSolutions() {
        return this.contentFragment.programmingSampleSolutions;
    }
    // Correction
    getSolution() {
        return { files: this.exerciseFiles };
    }
    getMutationQueryVariables() {
        return {
            exId: this.exerciseFragment.exerciseId,
            collId: this.exerciseFragment.collectionId,
            solution: this.getSolution(),
            part: this.contentFragment.programmingPart,
        };
    }
    correct() {
        this.correctAbstract(this.exerciseFragment, this.partId, () => {
            if (this.filesExerciseComponent) {
                this.filesExerciseComponent.toggleCorrectionTab();
            }
        });
    }
    // Results
    get correctionResult() {
        var _a, _b;
        return (_b = (_a = this.resultQuery) === null || _a === void 0 ? void 0 : _a.me.programmingExercise) === null || _b === void 0 ? void 0 : _b.correct;
    }
    get abstractResult() {
        var _a;
        return (_a = this.correctionResult) === null || _a === void 0 ? void 0 : _a.result;
    }
    get unitTestResults() {
        return this.abstractResult.unitTestResults || [];
    }
    get implementationCorrectionResult() {
        var _a;
        return (_a = this.abstractResult) === null || _a === void 0 ? void 0 : _a.implementationCorrectionResult;
    }
}
ProgrammingExerciseComponent.ɵfac = function ProgrammingExerciseComponent_Factory(t) { return new (t || ProgrammingExerciseComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵdirectiveInject"](_services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["ProgrammingCorrectionGQL"]), _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵdirectiveInject"](_services_dexie_service__WEBPACK_IMPORTED_MODULE_5__["DexieService"])); };
ProgrammingExerciseComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵdefineComponent"]({ type: ProgrammingExerciseComponent, selectors: [["it4all-programming-exercise"]], viewQuery: function ProgrammingExerciseComponent_Query(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵviewQuery"](_components_files_exercise_files_exercise_component__WEBPACK_IMPORTED_MODULE_2__["FilesExerciseComponent"], 1);
    } if (rf & 2) {
        let _t;
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵqueryRefresh"](_t = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵloadQuery"]()) && (ctx.filesExerciseComponent = _t.first);
    } }, inputs: { exerciseFragment: "exerciseFragment", contentFragment: "contentFragment" }, features: [_angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵInheritDefinitionFeature"]], decls: 6, vars: 6, consts: [["defaultMode", "python", 3, "exerciseFiles", "isCorrecting", "sampleSolutions", "correct"], ["exText", ""], [3, "innerHTML"], ["correctionContent", ""], ["class", "message is-danger", 4, "ngIf"], [4, "ngIf"], [1, "message", "is-danger"], [1, "message-header"], [1, "message-body"], [1, "my-3"], [3, "solutionSaved"], [3, "points", "maxPoints"], ["class", "content my-3", 4, "ngIf"], [3, "normalResult", 4, "ngIf"], [1, "content", "my-3"], [4, "ngFor", "ngForOf"], [3, "unitTestResult"], [3, "normalResult"]], template: function ProgrammingExerciseComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "it4all-files-exercise", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵlistener"]("correct", function ProgrammingExerciseComponent_Template_it4all_files_exercise_correct_0_listener() { return ctx.correct(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerStart"](1, 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](2, "span", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerStart"](3, 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](4, ProgrammingExerciseComponent_div_4_Template, 6, 1, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](5, ProgrammingExerciseComponent_ng_container_5_Template, 7, 5, "ng-container", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("exerciseFiles", ctx.exerciseFiles)("isCorrecting", ctx.isCorrecting)("sampleSolutions", ctx.sampleSolutions);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("innerHTML", ctx.exerciseFragment.text, _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵsanitizeHtml"]);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx.queryError);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx.correctionResult);
    } }, directives: [_components_files_exercise_files_exercise_component__WEBPACK_IMPORTED_MODULE_2__["FilesExerciseComponent"], _angular_common__WEBPACK_IMPORTED_MODULE_6__["NgIf"], _shared_solution_saved_solution_saved_component__WEBPACK_IMPORTED_MODULE_7__["SolutionSavedComponent"], _shared_points_notification_points_notification_component__WEBPACK_IMPORTED_MODULE_8__["PointsNotificationComponent"], _angular_common__WEBPACK_IMPORTED_MODULE_6__["NgForOf"], _results_programming_unit_test_result_programming_unit_test_result_component__WEBPACK_IMPORTED_MODULE_9__["ProgrammingUnitTestResultComponent"], _results_programming_normal_result_programming_implementation_correction_result_component__WEBPACK_IMPORTED_MODULE_10__["ProgrammingImplementationCorrectionResultComponent"]], encapsulation: 2 });


/***/ }),

/***/ "Emuw":
/*!***********************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/_components/files-exercise/files-exercise.component.ts ***!
  \***********************************************************************************************/
/*! exports provided: FilesExerciseComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FilesExerciseComponent", function() { return FilesExerciseComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _helpers_correction_helpers__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../_helpers/correction-helpers */ "KhWx");
/* harmony import */ var _shared_tabs_tabs_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../../../shared/tabs/tabs.component */ "b4kd");
/* harmony import */ var _exercise_files_editor_exercise_files_editor_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../exercise-files-editor/exercise-files-editor.component */ "346A");
/* harmony import */ var _shared_tab_tab_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../../shared/tab/tab.component */ "4YYW");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _exercise_file_card_exercise_file_card_component__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../exercise-file-card/exercise-file-card.component */ "c3C5");










function FilesExerciseComponent_div_17_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 15);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, " Die Korrektur l\u00E4uft gerade... ");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} }
function FilesExerciseComponent_it4all_tab_20_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "it4all-tab", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵprojection"](1, 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("title", ctx_r1.livePreviewTabTitle);
} }
function FilesExerciseComponent_ng_container_25_ng_container_2_div_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 18);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](1, "it4all-exercise-file-card", 19);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const exFile_r8 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("exerciseFile", exFile_r8);
} }
function FilesExerciseComponent_ng_container_25_ng_container_2_br_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](0, "br");
} }
function FilesExerciseComponent_ng_container_25_ng_container_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](1, FilesExerciseComponent_ng_container_25_ng_container_2_div_1_Template, 2, 1, "div", 17);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](2, FilesExerciseComponent_ng_container_25_ng_container_2_br_2_Template, 1, 0, "br", 14);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const sample_r4 = ctx.$implicit;
    const lastList_r5 = ctx.last;
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", sample_r4.files);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", !lastList_r5);
} }
function FilesExerciseComponent_ng_container_25_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "div", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](2, FilesExerciseComponent_ng_container_25_ng_container_2_Template, 3, 2, "ng-container", 16);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r2 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx_r2.sampleSolutions);
} }
const _c0 = [[["", "exText", ""]], [["", "correctionContent", ""]], [["", "livePreview", ""]]];
const _c1 = ["[exText]", "[correctionContent]", "[livePreview]"];
class FilesExerciseComponent extends _helpers_correction_helpers__WEBPACK_IMPORTED_MODULE_1__["CorrectionHelpers"] {
    constructor() {
        super(...arguments);
        this.hasLivePreview = false;
        this.isCorrecting = false;
        this.correct = new _angular_core__WEBPACK_IMPORTED_MODULE_0__["EventEmitter"]();
        this.displaySampleSolutions = false;
    }
    toggleSampleSolutions() {
        this.displaySampleSolutions = !this.displaySampleSolutions;
    }
    performCorrection() {
        this.correct.emit();
    }
    toggleCorrectionTab() {
        if (this.tabsComponent) {
            this.tabsComponent.selectTabByTitle(this.correctionTabTitle);
        }
    }
}
FilesExerciseComponent.ɵfac = function FilesExerciseComponent_Factory(t) { return ɵFilesExerciseComponent_BaseFactory(t || FilesExerciseComponent); };
FilesExerciseComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: FilesExerciseComponent, selectors: [["it4all-files-exercise"]], viewQuery: function FilesExerciseComponent_Query(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵviewQuery"](_shared_tabs_tabs_component__WEBPACK_IMPORTED_MODULE_2__["TabsComponent"], 1);
    } if (rf & 2) {
        let _t;
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵqueryRefresh"](_t = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵloadQuery"]()) && (ctx.tabsComponent = _t.first);
    } }, inputs: { exerciseFiles: "exerciseFiles", defaultMode: "defaultMode", sampleSolutions: "sampleSolutions", hasLivePreview: "hasLivePreview", isCorrecting: "isCorrecting" }, outputs: { correct: "correct" }, features: [_angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵInheritDefinitionFeature"]], ngContentSelectors: _c1, decls: 26, vars: 11, consts: [[1, "container", "is-fluid"], [1, "columns"], [1, "column", "is-half-desktop"], [3, "exerciseFileFragments", "mode"], [3, "title"], [1, "notification", "mb-3", "is-light-grey"], [1, "column"], [1, "button", "is-link", "is-fullwidth", 3, "click"], ["routerLink", "../..", 1, "button", "is-dark", "is-fullwidth"], ["class", "notification is-dark-warning", 4, "ngIf"], [1, "overflow-hidden"], [3, "title", 4, "ngIf"], [1, "buttons"], [1, "button", "is-primary", "is-fullwidth", 3, "click"], [4, "ngIf"], [1, "notification", "is-dark-warning"], [4, "ngFor", "ngForOf"], ["class", "mb-3", 4, "ngFor", "ngForOf"], [1, "mb-3"], [3, "exerciseFile"]], template: function FilesExerciseComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵprojectionDef"](_c0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](2, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](3, "it4all-exercise-files-editor", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](4, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](5, "it4all-tabs");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](6, "it4all-tab", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](7, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵprojection"](8);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](9, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](10, "div", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](11, "button", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵlistener"]("click", function FilesExerciseComponent_Template_button_click_11_listener() { return ctx.correct.emit(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](12, " Korrektur ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](13, "div", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](14, "a", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](15, "Bearbeiten beenden");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](16, "it4all-tab", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](17, FilesExerciseComponent_div_17_Template, 2, 0, "div", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](18, "div", 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵprojection"](19, 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](20, FilesExerciseComponent_it4all_tab_20_Template, 2, 1, "it4all-tab", 11);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](21, "it4all-tab", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](22, "div", 12);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](23, "button", 13);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵlistener"]("click", function FilesExerciseComponent_Template_button_click_23_listener() { return ctx.toggleSampleSolutions(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](24);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](25, FilesExerciseComponent_ng_container_25_Template, 3, 1, "ng-container", 14);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("exerciseFileFragments", ctx.exerciseFiles)("mode", ctx.defaultMode);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("title", ctx.exerciseTextTabTitle);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](5);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵclassProp"]("is-loading", ctx.isCorrecting);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](5);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("title", ctx.correctionTabTitle);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx.isCorrecting);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx.hasLivePreview);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("title", ctx.sampleSolutionsTabTitle);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"](" Musterl\u00F6sung ", ctx.displaySampleSolutions ? "ausblenden" : "anzeigen", " ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx.displaySampleSolutions);
    } }, directives: [_exercise_files_editor_exercise_files_editor_component__WEBPACK_IMPORTED_MODULE_3__["ExerciseFilesEditorComponent"], _shared_tabs_tabs_component__WEBPACK_IMPORTED_MODULE_2__["TabsComponent"], _shared_tab_tab_component__WEBPACK_IMPORTED_MODULE_4__["TabComponent"], _angular_router__WEBPACK_IMPORTED_MODULE_5__["RouterLinkWithHref"], _angular_common__WEBPACK_IMPORTED_MODULE_6__["NgIf"], _angular_common__WEBPACK_IMPORTED_MODULE_6__["NgForOf"], _exercise_file_card_exercise_file_card_component__WEBPACK_IMPORTED_MODULE_7__["ExerciseFileCardComponent"]], styles: [".overflow-hidden[_ngcontent-%COMP%] {\n      max-height: 750px;\n      overflow-y: auto;\n    }"] });
const ɵFilesExerciseComponent_BaseFactory = /*@__PURE__*/ _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵgetInheritedFactory"](FilesExerciseComponent);


/***/ }),

/***/ "EoM4":
/*!**********************************************************************************************!*\
  !*** ./src/app/tools/random-tools/nary/nary-two-conversion/nary-two-conversion.component.ts ***!
  \**********************************************************************************************/
/*! exports provided: NaryTwoConversionComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "NaryTwoConversionComponent", function() { return NaryTwoConversionComponent; });
/* harmony import */ var _nary__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../nary */ "mjeW");
/* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../../../helpers */ "Afm0");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/forms */ "3Pt+");
/* harmony import */ var _components_nary_number_read_only_input_nary_number_read_only_input_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../_components/nary-number-read-only-input/nary-number-read-only-input.component */ "Ox94");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _components_random_solve_buttons_random_solve_buttons_component__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../../_components/random-solve-buttons/random-solve-buttons.component */ "5Adw");







function NaryTwoConversionComponent_div_35_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 24);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1, " \u2718 Diese Eingabe ist nicht korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} }
function NaryTwoConversionComponent_div_36_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 25);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1, " \u2714 Diese Eingabe ist korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} }
function NaryTwoConversionComponent_div_50_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 24);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1, " \u2718 Diese Eingabe ist nicht korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} }
function NaryTwoConversionComponent_div_51_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 25);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1, " \u2714 Diese Eingabe ist korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} }
function NaryTwoConversionComponent_div_65_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 24);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1, " \u2718 Die L\u00F6sung ist nicht korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} }
function NaryTwoConversionComponent_div_66_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 25);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1, " \u2714 Die L\u00F6sung ist korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} }
class NaryTwoConversionComponent extends _nary__WEBPACK_IMPORTED_MODULE_0__["NaryComponentBase"] {
    constructor() {
        super(128);
        this.withIntermediateSteps = true;
        this.toConvertInput = {
            decimalNumber: 0,
            numberingSystem: _nary__WEBPACK_IMPORTED_MODULE_0__["DECIMAL_SYSTEM"],
            fieldId: 'startNumber',
            labelContent: 'Startzahl:',
            maxValueForDigits: this.max
        };
        this.binaryAbsoluteString = '';
        this.invertedAbsoluteString = '';
        this.solutionString = '';
        this.checked = false;
        this.binaryAbsoluteCorrect = false;
        this.invertedAbsoluteCorrect = false;
        this.solutionCorrect = false;
        this.completelyCorrect = false;
    }
    static swapOnesAndZeros(str) {
        return str
            .replace(/0/g, 'a')
            .replace(/1/g, '0')
            .replace(/a/g, '1');
    }
    ngOnInit() {
        this.update();
    }
    update() {
        this.toConvertInput.decimalNumber = Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["randomInt"])(0, this.max);
        this.toConvertInput.maxValueForDigits = this.max;
        this.checked = false;
        this.binaryAbsoluteString = '';
        this.invertedAbsoluteString = '';
        this.solutionString = '';
    }
    checkSolution() {
        this.checked = true;
        const absoluteToConvert = Math.abs(this.toConvertInput.decimalNumber);
        const binAbsStr = this.binaryAbsoluteString.replace(/\s+/g, '');
        this.binaryAbsoluteCorrect = binAbsStr.length === 8 && parseInt(binAbsStr, 2) === absoluteToConvert;
        const invAbsStr = this.invertedAbsoluteString.replace(/\s+/g, '');
        const awaitedInvertedAbs = NaryTwoConversionComponent.swapOnesAndZeros(absoluteToConvert.toString(2).padStart(8, '0'));
        this.invertedAbsoluteCorrect = invAbsStr.length === 8 && awaitedInvertedAbs === invAbsStr;
        const solStr = this.solutionString.replace(/\s+/g, '');
        const awaitedSolution = (parseInt(awaitedInvertedAbs, 2) + 1).toString(2).padStart(8, '0');
        this.solutionCorrect = solStr.length === 8 && awaitedSolution === solStr;
    }
    handleKeyboardEvent(event) {
        if (event.key === 'Enter') {
            if (this.completelyCorrect) {
                this.update();
            }
            else {
                this.checkSolution();
            }
        }
    }
}
NaryTwoConversionComponent.ɵfac = function NaryTwoConversionComponent_Factory(t) { return new (t || NaryTwoConversionComponent)(); };
NaryTwoConversionComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdefineComponent"]({ type: NaryTwoConversionComponent, selectors: [["it4all-nary-two-conversion"]], hostBindings: function NaryTwoConversionComponent_HostBindings(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("keypress", function NaryTwoConversionComponent_keypress_HostBindingHandler($event) { return ctx.handleKeyboardEvent($event); }, false, _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵresolveDocument"]);
    } }, features: [_angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵInheritDefinitionFeature"]], decls: 69, vars: 28, consts: [[1, "columns"], [1, "column", "is-half-desktop", "is-offset-one-quarter-desktop"], ["for", "max", 1, "label", "has-text-centered"], [1, "field", "has-addons"], [1, "control"], [1, "button", 3, "disabled", "click"], [1, "control", "is-expanded"], ["type", "number", "id", "max", 1, "input", "has-text-centered", 3, "value"], [1, "field"], [1, "control", "has-text-centered"], [1, "checkbox"], ["type", "checkbox", 3, "ngModel", "ngModelChange"], [1, "is-italic", "has-text-info", "has-text-centered"], [3, "naryNumberInput"], [1, "button", "is-static"], ["for", "binaryAbsolute"], ["type", "text", "id", "binaryAbsolute", "autofocus", "", "autocomplete", "off", 1, "input", "has-text-right", 3, "ngModel", "placeholder", "ngModelChange"], ["class", "notification has-text-centered is-danger", 4, "ngIf"], ["class", "notification has-text-centered is-success", 4, "ngIf"], ["for", "invertedAbsolute"], ["type", "text", "id", "invertedAbsolute", "placeholder", "Invertierung aller Bits", "autofocus", "", "autocomplete", "off", 1, "input", "has-text-right", 3, "ngModel", "ngModelChange"], ["for", "solution"], ["type", "text", "id", "solution", "placeholder", "Zweierkomplement", "autofocus", "", "autocomplete", "off", 1, "input", "has-text-right", 3, "ngModel", "ngModelChange"], [3, "correctEmitter", "nextEmitter"], [1, "notification", "has-text-centered", "is-danger"], [1, "notification", "has-text-centered", "is-success"]], template: function NaryTwoConversionComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](1, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](2, "label", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](3, "Maximalwert");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](4, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](5, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](6, "button", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("click", function NaryTwoConversionComponent_Template_button_click_6_listener() { return ctx.doubleMax(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](7, "* 2");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](8, "div", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](9, "input", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](10, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](11, "button", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("click", function NaryTwoConversionComponent_Template_button_click_11_listener() { return ctx.halveMax(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](12, "/ 2");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](13, "div", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](14, "div", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](15, "label", 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](16, "input", 11);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("ngModelChange", function NaryTwoConversionComponent_Template_input_ngModelChange_16_listener($event) { return ctx.withIntermediateSteps = $event; });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](17, " Mit Zwischenschritten ");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](18, "p", 12);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](19, "Geben Sie alle Bin\u00E4rzahlen mit einer L\u00E4nge von 8 Bit ein!");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](20, "hr");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](21, "it4all-nary-number-read-only-input", 13);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](22, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](23, "div", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](24, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](25, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](26, "div", 14);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](27, "label", 15);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](28);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](29, "div", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](30, "input", 16);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("ngModelChange", function NaryTwoConversionComponent_Template_input_ngModelChange_30_listener($event) { return ctx.binaryAbsoluteString = $event; });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](31, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](32, "div", 14);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](33, "sub");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](34, "2");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](35, NaryTwoConversionComponent_div_35_Template, 2, 0, "div", 17);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](36, NaryTwoConversionComponent_div_36_Template, 2, 0, "div", 18);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](37, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](38, "div", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](39, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](40, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](41, "div", 14);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](42, "label", 19);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](43, "Invertierung aller Bits:");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](44, "div", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](45, "input", 20);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("ngModelChange", function NaryTwoConversionComponent_Template_input_ngModelChange_45_listener($event) { return ctx.invertedAbsoluteString = $event; });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](46, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](47, "div", 14);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](48, "sub");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](49, "2");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](50, NaryTwoConversionComponent_div_50_Template, 2, 0, "div", 17);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](51, NaryTwoConversionComponent_div_51_Template, 2, 0, "div", 18);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](52, "hr");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](53, "div", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](54, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](55, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](56, "div", 14);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](57, "label", 21);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](58, "Zweierkomplement:");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](59, "div", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](60, "input", 22);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("ngModelChange", function NaryTwoConversionComponent_Template_input_ngModelChange_60_listener($event) { return ctx.solutionString = $event; });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](61, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](62, "div", 14);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](63, "sub");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](64, "2");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](65, NaryTwoConversionComponent_div_65_Template, 2, 0, "div", 17);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](66, NaryTwoConversionComponent_div_66_Template, 2, 0, "div", 18);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](67, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](68, "it4all-random-solve-buttons", 23);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("correctEmitter", function NaryTwoConversionComponent_Template_it4all_random_solve_buttons_correctEmitter_68_listener() { return ctx.checkSolution(); })("nextEmitter", function NaryTwoConversionComponent_Template_it4all_random_solve_buttons_nextEmitter_68_listener() { return ctx.update(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](6);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("disabled", ctx.max === ctx.maximalMax);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵpropertyInterpolate"]("value", ctx.max);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("disabled", ctx.max === ctx.minimalMax);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](5);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngModel", ctx.withIntermediateSteps);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](5);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("naryNumberInput", ctx.toConvertInput);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](7);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtextInterpolate1"]("Bin\u00E4rdarstellung von ", ctx.toConvertInput.decimalNumber, ":");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵclassProp"]("is-success", ctx.checked && ctx.withIntermediateSteps && ctx.binaryAbsoluteCorrect)("is-danger", ctx.checked && ctx.withIntermediateSteps && !ctx.binaryAbsoluteCorrect);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵpropertyInterpolate1"]("placeholder", "Bin\u00E4rdarstellung von ", ctx.toConvertInput.decimalNumber, "");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngModel", ctx.binaryAbsoluteString);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](5);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx.checked && ctx.withIntermediateSteps && !ctx.binaryAbsoluteCorrect);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx.checked && ctx.withIntermediateSteps && ctx.binaryAbsoluteCorrect);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](9);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵclassProp"]("is-success", ctx.checked && ctx.withIntermediateSteps && ctx.invertedAbsoluteCorrect)("is-danger", ctx.checked && ctx.withIntermediateSteps && !ctx.invertedAbsoluteCorrect);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngModel", ctx.invertedAbsoluteString);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](5);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx.checked && ctx.withIntermediateSteps && !ctx.invertedAbsoluteCorrect);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx.checked && ctx.withIntermediateSteps && ctx.invertedAbsoluteCorrect);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](9);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵclassProp"]("is-success", ctx.checked && ctx.solutionCorrect)("is-danger", ctx.checked && !ctx.solutionCorrect);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngModel", ctx.solutionString);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](5);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx.checked && !ctx.solutionCorrect);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx.checked && ctx.solutionCorrect);
    } }, directives: [_angular_forms__WEBPACK_IMPORTED_MODULE_3__["CheckboxControlValueAccessor"], _angular_forms__WEBPACK_IMPORTED_MODULE_3__["NgControlStatus"], _angular_forms__WEBPACK_IMPORTED_MODULE_3__["NgModel"], _components_nary_number_read_only_input_nary_number_read_only_input_component__WEBPACK_IMPORTED_MODULE_4__["NaryNumberReadOnlyInputComponent"], _angular_forms__WEBPACK_IMPORTED_MODULE_3__["DefaultValueAccessor"], _angular_common__WEBPACK_IMPORTED_MODULE_5__["NgIf"], _components_random_solve_buttons_random_solve_buttons_component__WEBPACK_IMPORTED_MODULE_6__["RandomSolveButtonsComponent"]], encapsulation: 2 });


/***/ }),

/***/ "ErB8":
/*!*************************************************************************************************!*\
  !*** ./src/app/tools/random-tools/bool/bool-drawing/_bool-drawing-model/boolDrawingElements.ts ***!
  \*************************************************************************************************/
/*! no exports provided */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var jointjs__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! jointjs */ "iuCI");
/* harmony import */ var lodash__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! lodash */ "LvDl");
/* harmony import */ var lodash__WEBPACK_IMPORTED_MODULE_1___default = /*#__PURE__*/__webpack_require__.n(lodash__WEBPACK_IMPORTED_MODULE_1__);


jointjs__WEBPACK_IMPORTED_MODULE_0__["shapes"].logic.Gate.prototype.onSignal = (signal, handler) => {
    handler.call(undefined, signal);
};
// The repeater delays a signal handling by 400ms
jointjs__WEBPACK_IMPORTED_MODULE_0__["shapes"].logic.Repeater.prototype.onSignal = (signal, handler) => {
    lodash__WEBPACK_IMPORTED_MODULE_1__["delay"](handler, 400, signal);
};
// Output element just marks itself as alive.
jointjs__WEBPACK_IMPORTED_MODULE_0__["shapes"].logic.Output.prototype.onSignal = (signal, handler) => {
    // FIXME: toggleLive(this, signal);
};


/***/ }),

/***/ "HKNS":
/*!**********************************************************************************!*\
  !*** ./src/app/tools/random-tools/bool/bool-fill-out/bool-fill-out.component.ts ***!
  \**********************************************************************************/
/*! exports provided: BoolFillOutComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BoolFillOutComponent", function() { return BoolFillOutComponent; });
/* harmony import */ var _model_bool_component_helper__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../_model/bool-component-helper */ "vWq+");
/* harmony import */ var _model_bool_node__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../_model/bool-node */ "oi/H");
/* harmony import */ var _model_bool_formula__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../_model/bool-formula */ "tTUD");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _components_random_solve_buttons_random_solve_buttons_component__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../_components/random-solve-buttons/random-solve-buttons.component */ "5Adw");






function BoolFillOutComponent_th_11_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementStart"](0, "th", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementEnd"]();
} if (rf & 2) {
    const variable_r5 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtextInterpolate"](variable_r5.variable);
} }
function BoolFillOutComponent_ng_container_12_th_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelement"](0, "th", 14);
} if (rf & 2) {
    const subFormula_r7 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵproperty"]("innerHTML", subFormula_r7.asHtmlString(), _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵsanitizeHtml"]);
} }
function BoolFillOutComponent_ng_container_12_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtemplate"](1, BoolFillOutComponent_ng_container_12_th_1_Template, 1, 1, "th", 13);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵproperty"]("ngForOf", ctx_r1.subFormulas);
} }
function BoolFillOutComponent_tr_16_td_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementStart"](0, "td", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementEnd"]();
} if (rf & 2) {
    const variable_r13 = ctx.$implicit;
    const assignment_r8 = _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵnextContext"]().$implicit;
    const ctx_r9 = _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtextInterpolate"](ctx_r9.displayAssignmentValue(assignment_r8, variable_r13));
} }
function BoolFillOutComponent_tr_16_ng_container_2_th_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelement"](0, "th");
} }
function BoolFillOutComponent_tr_16_ng_container_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtemplate"](1, BoolFillOutComponent_tr_16_ng_container_2_th_1_Template, 1, 0, "th", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r10 = _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵproperty"]("ngForOf", ctx_r10.subFormulas);
} }
function BoolFillOutComponent_tr_16_span_7_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementStart"](0, "span");
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtext"](1, "\u2714");
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementEnd"]();
} }
function BoolFillOutComponent_tr_16_span_8_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementStart"](0, "span");
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtext"](1, "\u2718");
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementEnd"]();
} }
function BoolFillOutComponent_tr_16_Template(rf, ctx) { if (rf & 1) {
    const _r18 = _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵgetCurrentView"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementStart"](0, "tr");
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtemplate"](1, BoolFillOutComponent_tr_16_td_1_Template, 2, 1, "td", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtemplate"](2, BoolFillOutComponent_tr_16_ng_container_2_Template, 2, 1, "ng-container", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementStart"](3, "td", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementStart"](4, "button", 15);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵlistener"]("click", function BoolFillOutComponent_tr_16_Template_button_click_4_listener() { _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵrestoreView"](_r18); const assignment_r8 = ctx.$implicit; const ctx_r17 = _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵnextContext"](); return ctx_r17.updateAssignment(assignment_r8); });
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtext"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtext"](6, " \u00A0 ");
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtemplate"](7, BoolFillOutComponent_tr_16_span_7_Template, 2, 0, "span", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtemplate"](8, BoolFillOutComponent_tr_16_span_8_Template, 2, 0, "span", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementEnd"]();
} if (rf & 2) {
    const assignment_r8 = ctx.$implicit;
    const ctx_r2 = _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵproperty"]("ngForOf", ctx_r2.formula.getVariables());
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵproperty"]("ngIf", ctx_r2.withSubFormulas);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵclassProp"]("has-background-danger", ctx_r2.corrected && !ctx_r2.isCorrect(assignment_r8))("has-background-success", ctx_r2.corrected && ctx_r2.isCorrect(assignment_r8));
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵclassProp"]("is-link", assignment_r8.get(ctx_r2.learnerVariable.variable));
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtextInterpolate1"](" ", ctx_r2.displayAssignmentValue(assignment_r8, ctx_r2.learnerVariable), " ");
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵproperty"]("ngIf", ctx_r2.corrected && ctx_r2.isCorrect(assignment_r8));
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵproperty"]("ngIf", ctx_r2.corrected && !ctx_r2.isCorrect(assignment_r8));
} }
function BoolFillOutComponent_div_17_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementStart"](0, "div", 16);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtext"](1, " \u2718 Ihre L\u00F6sung ist nicht (komplett) korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementEnd"]();
} }
function BoolFillOutComponent_div_18_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementStart"](0, "div", 17);
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtext"](1, " \u2714 Ihre L\u00F6sung ist korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementEnd"]();
} }
class BoolFillOutComponent extends _model_bool_component_helper__WEBPACK_IMPORTED_MODULE_0__["BoolComponentHelper"] {
    constructor() {
        super(...arguments);
        this.withSubFormulas = false;
        this.subFormulas = [];
    }
    ngOnInit() {
        this.update();
    }
    update() {
        this.completelyCorrect = false;
        this.corrected = false;
        this.formula = Object(_model_bool_formula__WEBPACK_IMPORTED_MODULE_2__["generateBooleanFormula"])(this.sampleVariable);
        this.assignments = Object(_model_bool_node__WEBPACK_IMPORTED_MODULE_1__["calculateAssignments"])(this.formula.getVariables());
        this.subFormulas = this.formula.getSubFormulas();
        this.assignments.forEach((assignment) => {
            assignment.set(this.sampleVariable.variable, this.formula.evaluate(assignment));
            assignment.set(this.learnerVariable.variable, false);
        });
    }
    correct() {
        this.corrected = true;
        this.completelyCorrect = this.assignments.every((a) => this.isCorrect(a));
    }
    updateAssignment(assignment) {
        for (const as of this.assignments) {
            if (as === assignment) {
                const newValue = !as.get(this.learnerVariable.variable);
                as.set(this.learnerVariable.variable, newValue);
            }
        }
        if (this.corrected) {
            this.correct();
        }
    }
    handleKeyboardEvent(event) {
        if (event.key === 'Enter') {
            if (this.completelyCorrect) {
                this.update();
            }
            else {
                this.corrected = true;
            }
        }
    }
}
BoolFillOutComponent.ɵfac = function BoolFillOutComponent_Factory(t) { return ɵBoolFillOutComponent_BaseFactory(t || BoolFillOutComponent); };
BoolFillOutComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵdefineComponent"]({ type: BoolFillOutComponent, selectors: [["it4all-bool-fillout"]], hostBindings: function BoolFillOutComponent_HostBindings(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵlistener"]("keypress", function BoolFillOutComponent_keypress_HostBindingHandler($event) { return ctx.handleKeyboardEvent($event); }, false, _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵresolveDocument"]);
    } }, features: [_angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵInheritDefinitionFeature"]], decls: 20, vars: 8, consts: [[1, "columns"], [1, "column", "is-half-desktop", "is-offset-one-quarter-desktop"], [1, "subtitle", "is-4", "has-text-centered"], [3, "innerHTML"], [1, "subtitle", "is-5", "has-text-centered", "has-text-grey"], [1, "table", "is-bordered", "is-fullwidth"], ["class", "has-text-centered", 4, "ngFor", "ngForOf"], [4, "ngIf"], [1, "has-text-centered"], [4, "ngFor", "ngForOf"], ["class", "notification has-text-centered is-danger", 4, "ngIf"], ["class", "notification has-text-centered is-success", 4, "ngIf"], [3, "correctEmitter", "nextEmitter"], ["class", "has-text-centered", 3, "innerHTML", 4, "ngFor", "ngForOf"], [1, "has-text-centered", 3, "innerHTML"], [1, "button", 3, "click"], [1, "notification", "has-text-centered", "is-danger"], [1, "notification", "has-text-centered", "is-success"]], template: function BoolFillOutComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementStart"](1, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelement"](2, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementStart"](3, "h2", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelement"](4, "code", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementStart"](5, "h3", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtext"](6);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelement"](7, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementStart"](8, "table", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementStart"](9, "thead");
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementStart"](10, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtemplate"](11, BoolFillOutComponent_th_11_Template, 2, 1, "th", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtemplate"](12, BoolFillOutComponent_ng_container_12_Template, 2, 1, "ng-container", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementStart"](13, "th", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtext"](14);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementStart"](15, "tbody");
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtemplate"](16, BoolFillOutComponent_tr_16_Template, 9, 11, "tr", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtemplate"](17, BoolFillOutComponent_div_17_Template, 2, 0, "div", 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtemplate"](18, BoolFillOutComponent_div_18_Template, 2, 0, "div", 11);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementStart"](19, "it4all-random-solve-buttons", 12);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵlistener"]("correctEmitter", function BoolFillOutComponent_Template_it4all_random_solve_buttons_correctEmitter_19_listener() { return ctx.correct(); })("nextEmitter", function BoolFillOutComponent_Template_it4all_random_solve_buttons_nextEmitter_19_listener() { return ctx.update(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵadvance"](4);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵproperty"]("innerHTML", ctx.formula.asHtmlString(), _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵsanitizeHtml"]);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtextInterpolate"](ctx.formula.asString());
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵadvance"](5);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵproperty"]("ngForOf", ctx.formula.getVariables());
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵproperty"]("ngIf", ctx.withSubFormulas);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵtextInterpolate"](ctx.learnerVariable.variable);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵproperty"]("ngForOf", ctx.assignments);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵproperty"]("ngIf", ctx.corrected && !ctx.completelyCorrect);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵproperty"]("ngIf", ctx.corrected && ctx.completelyCorrect);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_4__["NgForOf"], _angular_common__WEBPACK_IMPORTED_MODULE_4__["NgIf"], _components_random_solve_buttons_random_solve_buttons_component__WEBPACK_IMPORTED_MODULE_5__["RandomSolveButtonsComponent"]], encapsulation: 2 });
const ɵBoolFillOutComponent_BaseFactory = /*@__PURE__*/ _angular_core__WEBPACK_IMPORTED_MODULE_3__["ɵɵgetInheritedFactory"](BoolFillOutComponent);


/***/ }),

/***/ "HvLl":
/*!***********************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/sql/_components/sql-table-contents/sql-table-contents.component.ts ***!
  \***********************************************************************************************************/
/*! exports provided: SqlTableContentsComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SqlTableContentsComponent", function() { return SqlTableContentsComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _query_result_table_query_result_table_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../query-result-table/query-result-table.component */ "DeMQ");



function SqlTableContentsComponent_div_1_Template(rf, ctx) { if (rf & 1) {
    const _r4 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵgetCurrentView"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "button", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵlistener"]("click", function SqlTableContentsComponent_div_1_Template_button_click_1_listener() { _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵrestoreView"](_r4); const dbContent_r2 = ctx.$implicit; const ctx_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"](); return ctx_r3.activateModal(dbContent_r2); });
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const dbContent_r2 = ctx.$implicit;
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵclassProp"]("is-info", dbContent_r2 === ctx_r0.shownDbContent);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"](" ", dbContent_r2.tableName, " ");
} }
function SqlTableContentsComponent_div_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "header", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](2, "p", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](4, "section", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](5, "div", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](6, "it4all-query-result-table", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx_r1.shownDbContent.tableName);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("queryResult", ctx_r1.shownDbContent);
} }
class SqlTableContentsComponent {
    activateModal(sqlQueryResult) {
        if (this.shownDbContent === sqlQueryResult) {
            this.shownDbContent = undefined;
        }
        else {
            this.shownDbContent = sqlQueryResult;
        }
    }
}
SqlTableContentsComponent.ɵfac = function SqlTableContentsComponent_Factory(t) { return new (t || SqlTableContentsComponent)(); };
SqlTableContentsComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: SqlTableContentsComponent, selectors: [["it4all-sql-table-contents"]], inputs: { dbContents: "dbContents" }, decls: 3, vars: 2, consts: [[1, "columns", "is-multiline"], ["class", "column is-one-quarter-desktop", 4, "ngFor", "ngForOf"], ["class", "card", 4, "ngIf"], [1, "column", "is-one-quarter-desktop"], [1, "button", "is-fullwidth", 3, "click"], [1, "card"], [1, "card-header"], [1, "card-header-title"], [1, "card-content"], [1, "table-container"], [3, "queryResult"]], template: function SqlTableContentsComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](1, SqlTableContentsComponent_div_1_Template, 3, 3, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](2, SqlTableContentsComponent_div_2_Template, 7, 2, "div", 2);
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx.dbContents);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx.shownDbContent);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_1__["NgForOf"], _angular_common__WEBPACK_IMPORTED_MODULE_1__["NgIf"], _query_result_table_query_result_table_component__WEBPACK_IMPORTED_MODULE_2__["QueryResultTableComponent"]], encapsulation: 2 });


/***/ }),

/***/ "IASd":
/*!*****************************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/web/_components/html-attribute-result/html-attribute-result.component.ts ***!
  \*****************************************************************************************************************/
/*! exports provided: HtmlAttributeResultComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "HtmlAttributeResultComponent", function() { return HtmlAttributeResultComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/common */ "ofXK");


function HtmlAttributeResultComponent_ng_container_4_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, " sollte den Wert ");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](2, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](4, " haben. Gefunden wurde ");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](5, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](6);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](7, ". ");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx_r0.attributeResult.awaitedContent);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx_r0.attributeResult.maybeFoundContent);
} }
function HtmlAttributeResultComponent_ng_container_5_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, " wurde nicht gefunden! ");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerEnd"]();
} }
class HtmlAttributeResultComponent {
}
HtmlAttributeResultComponent.ɵfac = function HtmlAttributeResultComponent_Factory(t) { return new (t || HtmlAttributeResultComponent)(); };
HtmlAttributeResultComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: HtmlAttributeResultComponent, selectors: [["it4all-html-attribute-result"]], inputs: { attributeResult: "attributeResult" }, decls: 6, vars: 4, consts: [[3, "ngClass"], [4, "ngIf"]], template: function HtmlAttributeResultComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "li", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, " Das Attribut ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](2, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](4, HtmlAttributeResultComponent_ng_container_4_Template, 8, 2, "ng-container", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](5, HtmlAttributeResultComponent_ng_container_5_Template, 2, 0, "ng-container", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngClass", ctx.attributeResult.isSuccessful ? "has-text-success" : "has-text-danger");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx.attributeResult.keyName);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx.attributeResult.maybeFoundContent);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", !ctx.attributeResult.maybeFoundContent);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_1__["NgClass"], _angular_common__WEBPACK_IMPORTED_MODULE_1__["NgIf"]], encapsulation: 2 });


/***/ }),

/***/ "KhWx":
/*!***********************************************************************!*\
  !*** ./src/app/tools/collection-tools/_helpers/correction-helpers.ts ***!
  \***********************************************************************/
/*! exports provided: CorrectionHelpers */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CorrectionHelpers", function() { return CorrectionHelpers; });
class CorrectionHelpers {
    constructor() {
        this.exerciseTextTabTitle = 'Aufgabenstellung';
        this.correctionTabTitle = 'Korrektur';
        this.livePreviewTabTitle = 'Live-Vorschau';
        this.sampleSolutionsTabTitle = 'Musterlösungen';
    }
}


/***/ }),

/***/ "L0Rw":
/*!****************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/sql/_results/sql-result/sql-result.component.ts ***!
  \****************************************************************************************/
/*! exports provided: SqlResultComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SqlResultComponent", function() { return SqlResultComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _sql_matching_result_sql_matching_result_component__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../sql-matching-result/sql-matching-result.component */ "1GxF");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common */ "ofXK");



function SqlResultComponent_ng_container_8_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](1, "it4all-sql-matching-result", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](2, "br");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](3, "it4all-sql-matching-result", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](4, "br");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](5, "it4all-sql-matching-result", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("matchingResult", ctx_r0.selectAdditionalComparisons.orderByComparison);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("matchingResult", ctx_r0.selectAdditionalComparisons.groupByComparison);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("matchingResult", ctx_r0.selectAdditionalComparisons.limitComparison);
} }
function SqlResultComponent_it4all_sql_matching_result_10_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](0, "it4all-sql-matching-result", 9);
} if (rf & 2) {
    const ctx_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("matchingResult", ctx_r1.insertAdditionalComparisons);
} }
class SqlResultComponent {
    get columnComparison() {
        return this.result.staticComparison.columnComparison;
    }
    get tableComparison() {
        return this.result.staticComparison.tableComparison;
    }
    get whereComparison() {
        return this.result.staticComparison.whereComparison;
    }
    get joinExpressionComparison() {
        return this.result.staticComparison.joinExpressionComparison;
    }
    get selectAdditionalComparisons() {
        return this.result.staticComparison.additionalComparisons.selectComparisons;
    }
    get insertAdditionalComparisons() {
        return this.result.staticComparison.additionalComparisons.insertComparison;
    }
}
SqlResultComponent.ɵfac = function SqlResultComponent_Factory(t) { return new (t || SqlResultComponent)(); };
SqlResultComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: SqlResultComponent, selectors: [["it4all-sql-result"]], inputs: { result: "result" }, decls: 11, vars: 6, consts: [["matchName", "Spalten", "matchSingularName", "der Spalte", 3, "matchingResult"], ["matchName", "Tabellen", "matchSingularName", "der Tabelle", 3, "matchingResult"], ["matchName", "Join-Bedingungen", "matchSingularName", "der Join-Bedingung", 3, "matchingResult"], ["matchName", "Bedingungen", "matchSingularName", "der Bedingung", 3, "matchingResult"], [4, "ngIf"], ["matchName", "Inserts", "matchSingularName", "Insert", 3, "matchingResult", 4, "ngIf"], ["matchName", "Order Bys", "matchSingularName", "des Order By-Statements", 3, "matchingResult"], ["matchName", "Group Bys", "matchSingularName", "des Group By-Statements", 3, "matchingResult"], ["matchName", "Limits", "matchSingularName", "des Limit-Statements", 3, "matchingResult"], ["matchName", "Inserts", "matchSingularName", "Insert", 3, "matchingResult"]], template: function SqlResultComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](0, "it4all-sql-matching-result", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](1, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](2, "it4all-sql-matching-result", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](3, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](4, "it4all-sql-matching-result", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](5, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](6, "it4all-sql-matching-result", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](7, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](8, SqlResultComponent_ng_container_8_Template, 6, 3, "ng-container", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](9, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](10, SqlResultComponent_it4all_sql_matching_result_10_Template, 1, 1, "it4all-sql-matching-result", 5);
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("matchingResult", ctx.columnComparison);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("matchingResult", ctx.tableComparison);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("matchingResult", ctx.joinExpressionComparison);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("matchingResult", ctx.whereComparison);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx.selectAdditionalComparisons);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx.insertAdditionalComparisons);
    } }, directives: [_sql_matching_result_sql_matching_result_component__WEBPACK_IMPORTED_MODULE_1__["SqlMatchingResultComponent"], _angular_common__WEBPACK_IMPORTED_MODULE_2__["NgIf"]], encapsulation: 2 });


/***/ }),

/***/ "L0fW":
/*!*********************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/_components/filled-points/filled-points.component.ts ***!
  \*********************************************************************************************/
/*! exports provided: FilledPointsComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FilledPointsComponent", function() { return FilledPointsComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/common */ "ofXK");


function FilledPointsComponent_span_0_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "span");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, "\u26AB");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} }
function FilledPointsComponent_span_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "span");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, "\u26AA");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} }
class FilledPointsComponent {
    get filledDifficultyStars() {
        return Array(this.filledPoints).fill(0);
    }
    get nonFilledDifficultyStars() {
        return Array(this.maxPoints - this.filledPoints).fill(0);
    }
}
FilledPointsComponent.ɵfac = function FilledPointsComponent_Factory(t) { return new (t || FilledPointsComponent)(); };
FilledPointsComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: FilledPointsComponent, selectors: [["it4all-filled-points"]], inputs: { filledPoints: "filledPoints", maxPoints: "maxPoints" }, decls: 2, vars: 2, consts: [[4, "ngFor", "ngForOf"]], template: function FilledPointsComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](0, FilledPointsComponent_span_0_Template, 2, 0, "span", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](1, FilledPointsComponent_span_1_Template, 2, 0, "span", 0);
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx.filledDifficultyStars);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx.nonFilledDifficultyStars);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_1__["NgForOf"]], encapsulation: 2 });


/***/ }),

/***/ "LvJS":
/*!************************************************************************!*\
  !*** ./src/app/tools/collection-tools/uml/_model/my-uml-interfaces.ts ***!
  \************************************************************************/
/*! exports provided: CLASS_TYPES, buildMethodString, buildAttributeString, getMultiplicity, umlImplfromConnection, umlAssocfromConnection */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CLASS_TYPES", function() { return CLASS_TYPES; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "buildMethodString", function() { return buildMethodString; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "buildAttributeString", function() { return buildAttributeString; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "getMultiplicity", function() { return getMultiplicity; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "umlImplfromConnection", function() { return umlImplfromConnection; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "umlAssocfromConnection", function() { return umlAssocfromConnection; });
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../../../_services/apollo_services */ "o20/");

const CLASS_TYPES = ['CLASS', 'ABSTRACT', 'INTERFACE'];
function buildMethodString(cm) {
    const modifier = [];
    if (cm.isAbstract) {
        modifier.push('a');
    }
    if (cm.isStatic) {
        modifier.push('s');
    }
    return cm.visibility + ' ' + (modifier.length === 0 ? '' : '{' + modifier.join(', ') + '} ') + cm.memberName + '(' + cm.parameters + '): ' + cm.memberType;
}
function buildAttributeString(ca) {
    const modifier = [];
    if (ca.isAbstract) {
        modifier.push('a');
    }
    if (ca.isStatic) {
        modifier.push('s');
    }
    if (ca.isDerived) {
        modifier.push('d');
    }
    return ca.visibility + ' ' + (modifier.length === 0 ? '' : '{' + modifier.join(', ') + '} ') + ca.memberName + ': ' + ca.memberType;
}
function getTypeName(type) {
    switch (type) {
        case 'uml.Association':
            return 'ASSOCIATION';
        case 'uml.Aggregation':
            return 'AGGREGATION';
        case 'uml.Composition':
            return 'COMPOSITION';
        case 'uml.Implementation':
            return 'IMPLEMENTATION';
        default:
            return 'ERROR!';
    }
}
function getMultiplicity(label) {
    return label.attrs.text.text === '1' ? _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["UmlMultiplicity"].Single : _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["UmlMultiplicity"].Unbound;
}
function umlImplfromConnection(conn) {
    return {
        subClass: conn.getSourceCell().getClassName(),
        superClass: conn.getTargetCell().getClassName()
    };
}
function umlAssocfromConnection(conn) {
    return {
        assocType: getTypeName(conn.attributes.type),
        assocName: '',
        firstEnd: conn.getSourceCell().getClassName(),
        firstMult: getMultiplicity(conn.attributes.labels[0]),
        secondEnd: conn.getTargetCell().getClassName(),
        secondMult: getMultiplicity(conn.attributes.labels[1])
    };
}


/***/ }),

/***/ "MQgL":
/*!***********************************************************!*\
  !*** ./src/app/tools/random-tools/random-tools.module.ts ***!
  \***********************************************************/
/*! exports provided: RandomToolsModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RandomToolsModule", function() { return RandomToolsModule; });
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _components_random_solve_buttons_random_solve_buttons_component__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./_components/random-solve-buttons/random-solve-buttons.component */ "5Adw");
/* harmony import */ var _nary_components_nary_number_read_only_input_nary_number_read_only_input_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./nary/_components/nary-number-read-only-input/nary-number-read-only-input.component */ "Ox94");
/* harmony import */ var _bool_bool_create_bool_create_instructions_bool_create_instructions_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./bool/bool-create/bool-create-instructions/bool-create-instructions.component */ "ubCX");
/* harmony import */ var _bool_bool_drawing_bool_drawing_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./bool/bool-drawing/bool-drawing.component */ "2zuv");
/* harmony import */ var _random_tools_routing__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./random-tools.routing */ "4u5/");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! @angular/forms */ "3Pt+");
/* harmony import */ var _shared_shared_module__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../shared/shared.module */ "PCNd");
/* harmony import */ var _bool_bool_fill_out_bool_fill_out_component__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./bool/bool-fill-out/bool-fill-out.component */ "HKNS");
/* harmony import */ var _bool_bool_create_bool_create_component__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./bool/bool-create/bool-create.component */ "2X8u");
/* harmony import */ var _nary_nary_addition_nary_addition_component__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ./nary/nary-addition/nary-addition.component */ "5BB2");
/* harmony import */ var _nary_nary_conversion_nary_conversion_component__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ./nary/nary-conversion/nary-conversion.component */ "eNrX");
/* harmony import */ var _nary_nary_two_conversion_nary_two_conversion_component__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ./nary/nary-two-conversion/nary-two-conversion.component */ "EoM4");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _random_overview_random_overview_component__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! ./random-overview/random-overview.component */ "aO+D");
/* harmony import */ var _bool_bool_exercise_bool_exercise_component__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! ./bool/bool-exercise/bool-exercise.component */ "YH1L");
/* harmony import */ var _nary_nary_exercise_nary_exercise_component__WEBPACK_IMPORTED_MODULE_16__ = __webpack_require__(/*! ./nary/nary-exercise/nary-exercise.component */ "SZTG");

















class RandomToolsModule {
}
RandomToolsModule.ɵfac = function RandomToolsModule_Factory(t) { return new (t || RandomToolsModule)(); };
RandomToolsModule.ɵmod = _angular_core__WEBPACK_IMPORTED_MODULE_13__["ɵɵdefineNgModule"]({ type: RandomToolsModule });
RandomToolsModule.ɵinj = _angular_core__WEBPACK_IMPORTED_MODULE_13__["ɵɵdefineInjector"]({ imports: [[
            _angular_common__WEBPACK_IMPORTED_MODULE_0__["CommonModule"],
            _angular_forms__WEBPACK_IMPORTED_MODULE_6__["FormsModule"],
            _shared_shared_module__WEBPACK_IMPORTED_MODULE_7__["SharedModule"],
            _random_tools_routing__WEBPACK_IMPORTED_MODULE_5__["RandomToolsRoutingModule"]
        ]] });
(function () { (typeof ngJitMode === "undefined" || ngJitMode) && _angular_core__WEBPACK_IMPORTED_MODULE_13__["ɵɵsetNgModuleScope"](RandomToolsModule, { declarations: [_random_overview_random_overview_component__WEBPACK_IMPORTED_MODULE_14__["RandomOverviewComponent"], _bool_bool_exercise_bool_exercise_component__WEBPACK_IMPORTED_MODULE_15__["BoolExerciseComponent"], _nary_nary_exercise_nary_exercise_component__WEBPACK_IMPORTED_MODULE_16__["NaryExerciseComponent"], _components_random_solve_buttons_random_solve_buttons_component__WEBPACK_IMPORTED_MODULE_1__["RandomSolveButtonsComponent"],
        _bool_bool_fill_out_bool_fill_out_component__WEBPACK_IMPORTED_MODULE_8__["BoolFillOutComponent"],
        _bool_bool_drawing_bool_drawing_component__WEBPACK_IMPORTED_MODULE_4__["BoolDrawingComponent"],
        _bool_bool_create_bool_create_component__WEBPACK_IMPORTED_MODULE_9__["BoolCreateComponent"],
        _bool_bool_create_bool_create_instructions_bool_create_instructions_component__WEBPACK_IMPORTED_MODULE_3__["BoolCreateInstructionsComponent"],
        _nary_components_nary_number_read_only_input_nary_number_read_only_input_component__WEBPACK_IMPORTED_MODULE_2__["NaryNumberReadOnlyInputComponent"],
        _nary_nary_addition_nary_addition_component__WEBPACK_IMPORTED_MODULE_10__["NaryAdditionComponent"],
        _nary_nary_conversion_nary_conversion_component__WEBPACK_IMPORTED_MODULE_11__["NaryConversionComponent"],
        _nary_nary_two_conversion_nary_two_conversion_component__WEBPACK_IMPORTED_MODULE_12__["NaryTwoConversionComponent"]], imports: [_angular_common__WEBPACK_IMPORTED_MODULE_0__["CommonModule"],
        _angular_forms__WEBPACK_IMPORTED_MODULE_6__["FormsModule"],
        _shared_shared_module__WEBPACK_IMPORTED_MODULE_7__["SharedModule"],
        _random_tools_routing__WEBPACK_IMPORTED_MODULE_5__["RandomToolsRoutingModule"]] }); })();


/***/ }),

/***/ "Ox94":
/*!**************************************************************************************************************************!*\
  !*** ./src/app/tools/random-tools/nary/_components/nary-number-read-only-input/nary-number-read-only-input.component.ts ***!
  \**************************************************************************************************************************/
/*! exports provided: NaryNumberReadOnlyInputComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "NaryNumberReadOnlyInputComponent", function() { return NaryNumberReadOnlyInputComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");

class NaryNumberReadOnlyInputComponent {
    constructor() {
    }
    getSummandNary() {
        const radix = this.naryNumberInput.numberingSystem.radix;
        const blockSize = radix === 2 ? 4 : 2;
        const minimalDigits = Math.log2(this.naryNumberInput.maxValueForDigits) / Math.log2(radix);
        const newDigitCount = Math.ceil(minimalDigits / blockSize) * blockSize;
        return this.naryNumberInput.decimalNumber
            .toString(radix)
            .padStart(newDigitCount, '0')
            .match(new RegExp(`.{1,${blockSize}}`, 'g')) // split in blocks
            .join(' ');
    }
}
NaryNumberReadOnlyInputComponent.ɵfac = function NaryNumberReadOnlyInputComponent_Factory(t) { return new (t || NaryNumberReadOnlyInputComponent)(); };
NaryNumberReadOnlyInputComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: NaryNumberReadOnlyInputComponent, selectors: [["it4all-nary-number-read-only-input"]], inputs: { naryNumberInput: "naryNumberInput" }, decls: 11, vars: 6, consts: [[1, "field", "has-addons"], [1, "control"], [1, "button", "is-static"], [3, "for"], [1, "control", "is-expanded"], ["readonly", "", 1, "input", "has-text-right", 3, "id", "value", "placeholder"]], template: function NaryNumberReadOnlyInputComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](2, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](3, "label", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](4);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](5, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](6, "input", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](7, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](8, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](9, "sub");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](10);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵpropertyInterpolate"]("for", ctx.naryNumberInput.fieldId);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx.naryNumberInput.labelContent);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵpropertyInterpolate"]("id", ctx.naryNumberInput.fieldId);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("value", ctx.getSummandNary())("placeholder", ctx.naryNumberInput.labelContent);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](4);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx.naryNumberInput.numberingSystem.radix);
    } }, encapsulation: 2 });


/***/ }),

/***/ "PCNd":
/*!*****************************************!*\
  !*** ./src/app/shared/shared.module.ts ***!
  \*****************************************/
/*! exports provided: SharedModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SharedModule", function() { return SharedModule; });
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _tab_tab_component__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./tab/tab.component */ "4YYW");
/* harmony import */ var _tabs_tabs_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./tabs/tabs.component */ "b4kd");
/* harmony import */ var _points_notification_points_notification_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./points-notification/points-notification.component */ "ef8k");
/* harmony import */ var _solution_saved_solution_saved_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./solution-saved/solution-saved.component */ "rqf4");
/* harmony import */ var _breadcrumbs_breadcrumbs_component__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./breadcrumbs/breadcrumbs.component */ "lmDL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! @angular/core */ "fXoL");








class SharedModule {
}
SharedModule.ɵfac = function SharedModule_Factory(t) { return new (t || SharedModule)(); };
SharedModule.ɵmod = _angular_core__WEBPACK_IMPORTED_MODULE_7__["ɵɵdefineNgModule"]({ type: SharedModule });
SharedModule.ɵinj = _angular_core__WEBPACK_IMPORTED_MODULE_7__["ɵɵdefineInjector"]({ imports: [[
            _angular_common__WEBPACK_IMPORTED_MODULE_0__["CommonModule"],
            _angular_router__WEBPACK_IMPORTED_MODULE_6__["RouterModule"]
        ]] });
(function () { (typeof ngJitMode === "undefined" || ngJitMode) && _angular_core__WEBPACK_IMPORTED_MODULE_7__["ɵɵsetNgModuleScope"](SharedModule, { declarations: [_tab_tab_component__WEBPACK_IMPORTED_MODULE_1__["TabComponent"],
        _tabs_tabs_component__WEBPACK_IMPORTED_MODULE_2__["TabsComponent"],
        _points_notification_points_notification_component__WEBPACK_IMPORTED_MODULE_3__["PointsNotificationComponent"],
        _breadcrumbs_breadcrumbs_component__WEBPACK_IMPORTED_MODULE_5__["BreadcrumbsComponent"],
        _solution_saved_solution_saved_component__WEBPACK_IMPORTED_MODULE_4__["SolutionSavedComponent"]], imports: [_angular_common__WEBPACK_IMPORTED_MODULE_0__["CommonModule"],
        _angular_router__WEBPACK_IMPORTED_MODULE_6__["RouterModule"]], exports: [_tab_tab_component__WEBPACK_IMPORTED_MODULE_1__["TabComponent"],
        _tabs_tabs_component__WEBPACK_IMPORTED_MODULE_2__["TabsComponent"],
        _points_notification_points_notification_component__WEBPACK_IMPORTED_MODULE_3__["PointsNotificationComponent"],
        _solution_saved_solution_saved_component__WEBPACK_IMPORTED_MODULE_4__["SolutionSavedComponent"],
        _breadcrumbs_breadcrumbs_component__WEBPACK_IMPORTED_MODULE_5__["BreadcrumbsComponent"]] }); })();


/***/ }),

/***/ "PLyK":
/*!*********************************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/xml/_components/xml-document-correction/xml-document-correction.component.ts ***!
  \*********************************************************************************************************************/
/*! exports provided: XmlDocumentCorrectionComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "XmlDocumentCorrectionComponent", function() { return XmlDocumentCorrectionComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/common */ "ofXK");


function XmlDocumentCorrectionComponent_div_2_li_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "li", 0);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "b");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](3, ": ");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](4, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const err_r2 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngClass", err_r2.errorType === "WARNING" ? "has-text-dark-warning" : "has-text-danger");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"]("Fehler in Zeile ", err_r2.line, "");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](err_r2.errorMessage);
} }
function XmlDocumentCorrectionComponent_div_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "ul");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](2, XmlDocumentCorrectionComponent_div_2_li_2_Template, 6, 3, "li", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx_r0.result.errors);
} }
class XmlDocumentCorrectionComponent {
    constructor() {
        this.isCorrect = false;
    }
    ngOnInit() {
        this.isCorrect = this.result.errors.length === 0;
    }
}
XmlDocumentCorrectionComponent.ɵfac = function XmlDocumentCorrectionComponent_Factory(t) { return new (t || XmlDocumentCorrectionComponent)(); };
XmlDocumentCorrectionComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: XmlDocumentCorrectionComponent, selectors: [["it4all-xml-document-correction"]], inputs: { result: "result" }, decls: 3, vars: 5, consts: [[3, "ngClass"], ["class", "content", 4, "ngIf"], [1, "content"], [3, "ngClass", 4, "ngFor", "ngForOf"]], template: function XmlDocumentCorrectionComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "p", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](2, XmlDocumentCorrectionComponent_div_2_Template, 3, 1, "div", 1);
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngClass", ctx.isCorrect ? "has-text-dark-success" : "has-text-danger");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate3"](" Die Korrektur war ", ctx.isCorrect ? "" : "nicht", " erfolgreich. Es wurden ", ctx.isCorrect ? "keine" : ctx.result.errors.length, " Fehler gefunden", ctx.isCorrect ? "." : ":", " ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx.result.errors.length > 0);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_1__["NgClass"], _angular_common__WEBPACK_IMPORTED_MODULE_1__["NgIf"], _angular_common__WEBPACK_IMPORTED_MODULE_1__["NgForOf"]], encapsulation: 2 });


/***/ }),

/***/ "PyDI":
/*!*********************************************************!*\
  !*** ./src/app/tools/collection-tools/uml/uml-tools.ts ***!
  \*********************************************************/
/*! exports provided: getIdForUmlExPart, splitExerciseText, replaceWithMapping, isSelectable, getUmlExerciseTextParts */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "getIdForUmlExPart", function() { return getIdForUmlExPart; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "splitExerciseText", function() { return splitExerciseText; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "replaceWithMapping", function() { return replaceWithMapping; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "isSelectable", function() { return isSelectable; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "getUmlExerciseTextParts", function() { return getUmlExerciseTextParts; });
/* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../../helpers */ "Afm0");
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../../_services/apollo_services */ "o20/");


function getIdForUmlExPart(umlExPart) {
    switch (umlExPart) {
        case _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["UmlExPart"].ClassSelection:
            return 'classSelection';
        case _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["UmlExPart"].DiagramDrawing:
            return 'diagramDrawing';
        case _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["UmlExPart"].DiagramDrawingHelp:
            return 'diagramDrawingHelp';
        case _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["UmlExPart"].MemberAllocation:
            return 'memberAllocation';
    }
}
const capWordTextSplitRegex = /([A-Z][\wäöü?&;]*)/g;
function splitExerciseText(exerciseText) {
    return exerciseText
        .replace('\n', ' ')
        .split(capWordTextSplitRegex)
        .filter((s) => s.length > 0);
}
function replaceWithMapping(mappings, str) {
    const maybeMapping = mappings.find((m) => m.key === str);
    return maybeMapping ? maybeMapping.value : str;
}
function isSelectable(toIgnore, s) {
    return s.match(capWordTextSplitRegex) && !toIgnore.includes(s);
}
function getUmlExerciseTextParts(exercise, exerciseContent) {
    const splitText = splitExerciseText(exercise.text);
    const allBaseForms = Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["distinctStringArray"])(splitText
        .filter((s) => isSelectable(exerciseContent.toIgnore, s))
        .map((s) => replaceWithMapping(exerciseContent.mappings, s)));
    const sampleSolution = exerciseContent.umlSampleSolutions[0];
    const selectableClasses = allBaseForms.map((name) => {
        return {
            name,
            selected: false,
            isCorrect: sampleSolution.classes.find((c) => c.name === name) !== undefined
        };
    });
    const textParts = splitText.map((text) => {
        if (isSelectable(exerciseContent.toIgnore, text)) {
            const selectableClass = selectableClasses.find((c) => c.name === replaceWithMapping(exerciseContent.mappings, text));
            return { text, selectableClass };
        }
        else {
            return { text };
        }
    });
    return { selectableClasses, textParts };
}


/***/ }),

/***/ "Qt55":
/*!*****************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/uml/uml-member-allocation/uml-member-allocation.component.ts ***!
  \*****************************************************************************************************/
/*! exports provided: UmlMemberAllocationComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlMemberAllocationComponent", function() { return UmlMemberAllocationComponent; });
/* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../../../helpers */ "Afm0");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/common */ "ofXK");




function UmlMemberAllocationComponent_div_16_p_6_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "p");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "label", 16);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelement"](2, "input", 17);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const a_r4 = ctx.$implicit;
    const ctx_r2 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngClass", ctx_r2.memberColor(a_r4));
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" ", a_r4.display, " ");
} }
function UmlMemberAllocationComponent_div_16_p_8_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "p");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "label", 16);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelement"](2, "input", 17);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const m_r5 = ctx.$implicit;
    const ctx_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngClass", ctx_r3.memberColor(m_r5));
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" ", m_r5.display, " ");
} }
function UmlMemberAllocationComponent_div_16_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "div", 11);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "header", 12);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](3, "p", 13);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](5, "div", 14);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](6, UmlMemberAllocationComponent_div_16_p_6_Template, 4, 2, "p", 15);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelement"](7, "hr");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](8, UmlMemberAllocationComponent_div_16_p_8_Template, 4, 2, "p", 15);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const clazz_r1 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](clazz_r1.name);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", clazz_r1.attributes);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", clazz_r1.methods);
} }
function printVisibility(v) {
    switch (v) {
        case 'PUBLIC':
            return '+';
        case 'PACKAGE':
            return '~';
        case 'PROTECTED':
            return '#';
        case 'PRIVATE':
            return '-';
    }
}
function printAttribute(a) {
    return `${printVisibility(a.visibility)} ${a.memberName}: ${a.memberType}`;
}
function printMethod(m) {
    return `${printVisibility(m.visibility)} ${m.memberName}(${m.parameters}): ${m.memberType}`;
}
class UmlMemberAllocationComponent {
    constructor() {
        this.corrected = false;
    }
    ngOnInit() {
        this.sample = this.contentFragment.umlSampleSolutions[0];
        this.allAttributes = Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["distinctObjectArray"])(Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["flatMapArray"])(this.sample.classes, (clazz) => clazz.attributes), (a) => a.memberName);
        this.allMethods = Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["distinctObjectArray"])(Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["flatMapArray"])(this.sample.classes, (clazz) => clazz.methods), (m) => m.memberName);
        this.data = this.sample.classes.map((clazz) => {
            return {
                name: clazz.name,
                attributes: this.allAttributes.map((attr) => {
                    return {
                        display: printAttribute(attr),
                        correct: clazz.attributes.includes(attr),
                        selected: false
                    };
                }),
                methods: this.allMethods.map((met) => {
                    return {
                        display: printMethod(met),
                        correct: clazz.methods.includes(met),
                        selected: false
                    };
                })
            };
        });
    }
    memberColor(m) {
        if (this.corrected) {
            return (m.correct === m.selected) ? 'has-text-dark-success' : 'has-text-danger';
        }
        else {
            return '';
        }
    }
    correct() {
        this.corrected = true;
    }
}
UmlMemberAllocationComponent.ɵfac = function UmlMemberAllocationComponent_Factory(t) { return new (t || UmlMemberAllocationComponent)(); };
UmlMemberAllocationComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineComponent"]({ type: UmlMemberAllocationComponent, selectors: [["it4all-uml-member-allocation"]], inputs: { exerciseFragment: "exerciseFragment", contentFragment: "contentFragment" }, decls: 17, vars: 3, consts: [[1, "container", "is-fluid"], [1, "columns"], [1, "column", "is-one-quarter-desktop"], [1, "subtitle", "is-4", "has-text-centered"], [1, "notification", "is-light-grey"], [1, "column"], [1, "button", "is-link", "is-fullwidth", 3, "disabled", "click"], ["routerLink", "../..", 1, "button", "is-dark", "is-fullwidth"], [1, "columns", "is-multiline"], ["class", "column is-one-third-desktop", 4, "ngFor", "ngForOf"], [1, "column", "is-one-third-desktop"], [1, "card"], [1, "card-header"], [1, "card-header-title"], [1, "card-content"], [4, "ngFor", "ngForOf"], [1, "checkbox", 3, "ngClass"], ["type", "checkbox"]], template: function UmlMemberAllocationComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](3, "h2", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](4, "Aufgabentext");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](5, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](6);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](7, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](8, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](9, "button", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵlistener"]("click", function UmlMemberAllocationComponent_Template_button_click_9_listener() { return ctx.correct(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](10, "Korrektur");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](11, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](12, "a", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](13, "Bearbeiten beenden");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](14, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](15, "div", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](16, UmlMemberAllocationComponent_div_16_Template, 9, 3, "div", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](6);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" ", ctx.exerciseFragment.text, " ");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("disabled", ctx.corrected);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](7);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx.data);
    } }, directives: [_angular_router__WEBPACK_IMPORTED_MODULE_2__["RouterLinkWithHref"], _angular_common__WEBPACK_IMPORTED_MODULE_3__["NgForOf"], _angular_common__WEBPACK_IMPORTED_MODULE_3__["NgClass"]], encapsulation: 2 });


/***/ }),

/***/ "SJa0":
/*!*****************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/exercise-overview/exercise-overview.component.ts ***!
  \*****************************************************************************************/
/*! exports provided: ExerciseOverviewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExerciseOverviewComponent", function() { return ExerciseOverviewComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _shared_breadcrumbs_breadcrumbs_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../shared/breadcrumbs/breadcrumbs.component */ "lmDL");





const _c0 = function (a1) { return ["parts", a1]; };
function ExerciseOverviewComponent_ng_container_1_div_7_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "a", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const part_r4 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("routerLink", _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵpureFunction1"](2, _c0, part_r4.id));
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](part_r4.name);
} }
function ExerciseOverviewComponent_ng_container_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "h1", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](3, "div", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](4, "it4all-breadcrumbs", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](5, "div", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](6, "div", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](7, ExerciseOverviewComponent_ng_container_1_div_7_Template, 3, 4, "div", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"]("Aufgabe \"", ctx_r0.exercise.title, "\"");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("parts", ctx_r0.breadCrumbs);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("innerHTML", ctx_r0.exercise.text, _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵsanitizeHtml"]);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx_r0.entryParts);
} }
function ExerciseOverviewComponent_ng_template_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 11);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, "Lade Daten...");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} }
class ExerciseOverviewComponent {
    constructor(route, exerciseOverviewGQL) {
        this.route = route;
        this.exerciseOverviewGQL = exerciseOverviewGQL;
    }
    ngOnInit() {
        this.sub = this.route.paramMap.subscribe((paramMap) => {
            const toolId = paramMap.get('toolId');
            const collId = parseInt(paramMap.get('collId'), 10);
            const exId = parseInt(paramMap.get('exId'), 10);
            this.exerciseOverviewGQL
                .watch({ toolId, collId, exId })
                .valueChanges
                .subscribe(({ data }) => this.exerciseOverviewQuery = data);
        });
    }
    ngOnDestroy() {
        this.sub.unsubscribe();
    }
    getTool() {
        var _a;
        return (_a = this.exerciseOverviewQuery) === null || _a === void 0 ? void 0 : _a.me.tool;
    }
    get exercise() {
        var _a;
        return (_a = this.exerciseOverviewQuery) === null || _a === void 0 ? void 0 : _a.me.tool.collection.exercise;
    }
    get entryParts() {
        return this.exercise.parts.filter((p) => p.isEntryPart);
    }
    get breadCrumbs() {
        return [
            { routerLinkPart: '/', title: 'Tools' },
            { routerLinkPart: `tools/${this.getTool().id}`, title: this.getTool().name },
            { routerLinkPart: 'collections', title: 'Sammlungen' },
            { routerLinkPart: this.getTool().collection.collectionId.toString(), title: this.getTool().collection.title },
            { routerLinkPart: 'exercises', title: 'Aufgaben' },
            { routerLinkPart: this.exercise.exerciseId.toString(), title: this.exercise.exerciseId.toString() }
        ];
    }
}
ExerciseOverviewComponent.ɵfac = function ExerciseOverviewComponent_Factory(t) { return new (t || ExerciseOverviewComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_angular_router__WEBPACK_IMPORTED_MODULE_1__["ActivatedRoute"]), _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_services_apollo_services__WEBPACK_IMPORTED_MODULE_2__["ExerciseOverviewGQL"])); };
ExerciseOverviewComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: ExerciseOverviewComponent, selectors: [["ng-component"]], decls: 4, vars: 2, consts: [[1, "container"], [4, "ngIf", "ngIfElse"], ["loadingDataBlock", ""], [1, "title", "is-3", "has-text-centered"], [1, "my-3"], [3, "parts"], [1, "notification", "is-light-grey", 3, "innerHTML"], [1, "columns"], ["class", "column", 4, "ngFor", "ngForOf"], [1, "column"], [1, "button", "is-link", "is-fullwidth", 3, "routerLink"], [1, "notification", "is-primary", "has-text-centered"]], template: function ExerciseOverviewComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](1, ExerciseOverviewComponent_ng_container_1_Template, 8, 4, "ng-container", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](2, ExerciseOverviewComponent_ng_template_2_Template, 2, 0, "ng-template", null, 2, _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplateRefExtractor"]);
    } if (rf & 2) {
        const _r1 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵreference"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx.exercise)("ngIfElse", _r1);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_3__["NgIf"], _shared_breadcrumbs_breadcrumbs_component__WEBPACK_IMPORTED_MODULE_4__["BreadcrumbsComponent"], _angular_common__WEBPACK_IMPORTED_MODULE_3__["NgForOf"], _angular_router__WEBPACK_IMPORTED_MODULE_1__["RouterLinkWithHref"]], encapsulation: 2 });


/***/ }),

/***/ "SZTG":
/*!**********************************************************************************!*\
  !*** ./src/app/tools/random-tools/nary/nary-exercise/nary-exercise.component.ts ***!
  \**********************************************************************************/
/*! exports provided: NaryExerciseComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "NaryExerciseComponent", function() { return NaryExerciseComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _nary_addition_nary_addition_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../nary-addition/nary-addition.component */ "5BB2");
/* harmony import */ var _nary_conversion_nary_conversion_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../nary-conversion/nary-conversion.component */ "eNrX");
/* harmony import */ var _nary_two_conversion_nary_two_conversion_component__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../nary-two-conversion/nary-two-conversion.component */ "EoM4");






function NaryExerciseComponent_it4all_nary_addition_3_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](0, "it4all-nary-addition");
} }
function NaryExerciseComponent_it4all_nary_conversion_4_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](0, "it4all-nary-conversion");
} }
function NaryExerciseComponent_it4all_nary_two_conversion_5_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](0, "it4all-nary-two-conversion");
} }
function NaryExerciseComponent_div_6_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"](" Part \"", ctx_r3.part, "\" was not found! ");
} }
class NaryExerciseComponent {
    constructor(route) {
        this.route = route;
    }
    ngOnInit() {
        this.sub = this.route.paramMap.subscribe((paramMap) => {
            this.part = paramMap.get('part');
            this.title = this.getTitleForPart();
        });
    }
    getTitleForPart() {
        switch (this.part) {
            case 'addition':
                return 'Addition';
            case 'conversion':
                return 'Zahlenumwandlung';
            case 'twoConversion':
                return 'Zahlenumwandlung im Zweiersystem';
            default:
                return '';
        }
    }
}
NaryExerciseComponent.ɵfac = function NaryExerciseComponent_Factory(t) { return new (t || NaryExerciseComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_angular_router__WEBPACK_IMPORTED_MODULE_1__["ActivatedRoute"])); };
NaryExerciseComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: NaryExerciseComponent, selectors: [["ng-component"]], decls: 7, vars: 5, consts: [[1, "title", "is-3", "has-text-centered"], [3, "ngSwitch"], [4, "ngSwitchCase"], ["class", "notification is-light-danger has-text-centered", 4, "ngSwitchDefault"], [1, "notification", "is-light-danger", "has-text-centered"]], template: function NaryExerciseComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "h1", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerStart"](2, 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](3, NaryExerciseComponent_it4all_nary_addition_3_Template, 1, 0, "it4all-nary-addition", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](4, NaryExerciseComponent_it4all_nary_conversion_4_Template, 1, 0, "it4all-nary-conversion", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](5, NaryExerciseComponent_it4all_nary_two_conversion_5_Template, 1, 0, "it4all-nary-two-conversion", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](6, NaryExerciseComponent_div_6_Template, 2, 1, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx.title);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngSwitch", ctx.part);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngSwitchCase", "addition");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngSwitchCase", "conversion");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngSwitchCase", "twoConversion");
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_2__["NgSwitch"], _angular_common__WEBPACK_IMPORTED_MODULE_2__["NgSwitchCase"], _angular_common__WEBPACK_IMPORTED_MODULE_2__["NgSwitchDefault"], _nary_addition_nary_addition_component__WEBPACK_IMPORTED_MODULE_3__["NaryAdditionComponent"], _nary_conversion_nary_conversion_component__WEBPACK_IMPORTED_MODULE_4__["NaryConversionComponent"], _nary_two_conversion_nary_two_conversion_component__WEBPACK_IMPORTED_MODULE_5__["NaryTwoConversionComponent"]], encapsulation: 2 });


/***/ }),

/***/ "Sy1n":
/*!**********************************!*\
  !*** ./src/app/app.component.ts ***!
  \**********************************/
/*! exports provided: AppComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AppComponent", function() { return AppComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _services_authentication_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./_services/authentication.service */ "pW6c");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/common */ "ofXK");




function AppComponent_a_10_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "a", 17);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const lang_r4 = ctx.$implicit;
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵpropertyInterpolate2"]("href", "/", lang_r4, "", ctx_r0.getCurrentUrl(), "", _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵsanitizeUrl"]);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](lang_r4);
} }
function AppComponent_ng_container_18_Template(rf, ctx) { if (rf & 1) {
    const _r6 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵgetCurrentView"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "button", 18);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵlistener"]("click", function AppComponent_ng_container_18_Template_button_click_1_listener() { _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵrestoreView"](_r6); const ctx_r5 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"](); return ctx_r5.logout(); });
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](2, "span");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵi18n"](3, 19);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"]("\u00A0", ctx_r1.currentUser.loggedInUser.username, "");
} }
function AppComponent_ng_template_19_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 20);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "a", 21);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵi18n"](2, 22);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](3, "a", 23);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵi18n"](4, 24);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} }
const _c12 = function () { return ["/"]; };
class AppComponent {
    constructor(router, authenticationService) {
        this.router = router;
        this.authenticationService = authenticationService;
        this.langs = ["de", "en"];
        this.authenticationService.currentUser.subscribe((u) => this.currentUser = u);
    }
    logout() {
        this.authenticationService.logout();
    }
    getCurrentUrl() {
        return this.router.url;
    }
}
AppComponent.ɵfac = function AppComponent_Factory(t) { return new (t || AppComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_angular_router__WEBPACK_IMPORTED_MODULE_1__["Router"]), _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_services_authentication_service__WEBPACK_IMPORTED_MODULE_2__["AuthenticationService"])); };
AppComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: AppComponent, selectors: [["it4all-root"]], decls: 23, vars: 5, consts: function () { let i18n_0; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_51574765818008149$$SRC_APP_APP_COMPONENT_TS_1 = goog.getMsg("Sprache");
        i18n_0 = MSG_EXTERNAL_51574765818008149$$SRC_APP_APP_COMPONENT_TS_1;
    }
    else {
        i18n_0 = "Language";
    } let i18n_2; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_1547876271716599756$$SRC_APP_APP_COMPONENT_TS_3 = goog.getMsg("Impressum");
        i18n_2 = MSG_EXTERNAL_1547876271716599756$$SRC_APP_APP_COMPONENT_TS_3;
    }
    else {
        i18n_2 = "Imprint";
    } let i18n_4; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_4082114936887396529$$SRC_APP_APP_COMPONENT_TS_5 = goog.getMsg("Datenschutz");
        i18n_4 = MSG_EXTERNAL_4082114936887396529$$SRC_APP_APP_COMPONENT_TS_5;
    }
    else {
        i18n_4 = "Data Protection";
    } let i18n_6; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_3797778920049399855$$SRC_APP_APP_COMPONENT_TS__7 = goog.getMsg("Logout");
        i18n_6 = MSG_EXTERNAL_3797778920049399855$$SRC_APP_APP_COMPONENT_TS__7;
    }
    else {
        i18n_6 = "Logout";
    } let i18n_8; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_2454050363478003966$$SRC_APP_APP_COMPONENT_TS__9 = goog.getMsg("Login");
        i18n_8 = MSG_EXTERNAL_2454050363478003966$$SRC_APP_APP_COMPONENT_TS__9;
    }
    else {
        i18n_8 = "Login";
    } let i18n_10; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_2736731364720267618$$SRC_APP_APP_COMPONENT_TS__11 = goog.getMsg("Registrieren");
        i18n_10 = MSG_EXTERNAL_2736731364720267618$$SRC_APP_APP_COMPONENT_TS__11;
    }
    else {
        i18n_10 = "Register";
    } return [["role", "navigation", "aria-label", "main navigation", 1, "navbar", "is-dark"], [1, "navbar-brand"], [1, "navbar-item", 3, "routerLink"], [1, "navbar-menu"], [1, "navbar-end"], [1, "navbar-item", "has-dropdown", "is-hoverable"], [1, "navbar-link"], i18n_0, [1, "navbar-dropdown"], ["class", "navbar-item", 3, "href", 4, "ngFor", "ngForOf"], ["target", "_blank", "href", "https://www.uni-wuerzburg.de/sonstiges/impressum/", 1, "navbar-item"], i18n_2, ["target", "_blank", "href", "https://www.uni-wuerzburg.de/sonstiges/datenschutz/", 1, "navbar-item"], i18n_4, [1, "navbar-item"], [4, "ngIf", "ngIfElse"], ["noUserBlock", ""], [1, "navbar-item", 3, "href"], [1, "button", "is-light", 3, "click"], i18n_6, [1, "buttons"], ["routerLink", "/loginForm", 1, "button", "is-light"], i18n_8, ["routerLink", "/registerForm", 1, "button", "is-light"], i18n_10]; }, template: function AppComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "nav", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](2, "a", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](3, "it4all - \u03B2");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](4, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](5, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](6, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](7, "a", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵi18n"](8, 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](9, "div", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](10, AppComponent_a_10_Template, 2, 3, "a", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](11, "a", 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](12, "span");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵi18n"](13, 11);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](14, "a", 12);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](15, "span");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵi18n"](16, 13);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](17, "div", 14);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](18, AppComponent_ng_container_18_Template, 5, 1, "ng-container", 15);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](19, AppComponent_ng_template_19_Template, 5, 0, "ng-template", null, 16, _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplateRefExtractor"]);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](21, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](22, "router-outlet");
    } if (rf & 2) {
        const _r2 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵreference"](20);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("routerLink", _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵpureFunction0"](4, _c12));
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](8);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx.langs);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](8);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx.currentUser)("ngIfElse", _r2);
    } }, directives: [_angular_router__WEBPACK_IMPORTED_MODULE_1__["RouterLinkWithHref"], _angular_common__WEBPACK_IMPORTED_MODULE_3__["NgForOf"], _angular_common__WEBPACK_IMPORTED_MODULE_3__["NgIf"], _angular_router__WEBPACK_IMPORTED_MODULE_1__["RouterOutlet"]], encapsulation: 2 });


/***/ }),

/***/ "T7p5":
/*!****************************************!*\
  !*** ./src/app/_helpers/auth-guard.ts ***!
  \****************************************/
/*! exports provided: AuthGuard */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AuthGuard", function() { return AuthGuard; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _services_authentication_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../_services/authentication.service */ "pW6c");



class AuthGuard {
    constructor(router, authenticationService) {
        this.router = router;
        this.authenticationService = authenticationService;
    }
    canActivate(route, state) {
        const currentUser = this.authenticationService.currentUserValue;
        if (currentUser) {
            return true;
        }
        else {
            this.router.navigate(['/loginForm'], { queryParams: { returnUrl: state.url } });
            return false;
        }
    }
}
AuthGuard.ɵfac = function AuthGuard_Factory(t) { return new (t || AuthGuard)(_angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵinject"](_angular_router__WEBPACK_IMPORTED_MODULE_1__["Router"]), _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵinject"](_services_authentication_service__WEBPACK_IMPORTED_MODULE_2__["AuthenticationService"])); };
AuthGuard.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineInjectable"]({ token: AuthGuard, factory: AuthGuard.ɵfac, providedIn: 'root' });


/***/ }),

/***/ "TRIe":
/*!**************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/_helpers/component-with-exercise.directive.ts ***!
  \**************************************************************************************/
/*! exports provided: ComponentWithExerciseDirective */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ComponentWithExerciseDirective", function() { return ComponentWithExerciseDirective; });
/* harmony import */ var _shared_tabs_tabs_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../../shared/tabs/tabs.component */ "b4kd");
/* harmony import */ var _correction_helpers__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./correction-helpers */ "KhWx");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var apollo_angular__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! apollo-angular */ "/IUn");
/* harmony import */ var _services_dexie_service__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../_services/dexie.service */ "4di/");





class ComponentWithExerciseDirective extends _correction_helpers__WEBPACK_IMPORTED_MODULE_1__["CorrectionHelpers"] {
    constructor(mutationGQL, dexieService) {
        super();
        this.mutationGQL = mutationGQL;
        this.dexieService = dexieService;
        this.isCorrecting = false;
    }
    onCorrectionEnd(onComplete = () => {
    }) {
        this.isCorrecting = false;
        if (this.tabsComponent) {
            this.tabsComponent.selectTabByTitle(this.correctionTabTitle);
        }
        onComplete();
    }
    correctAbstract(exerciseFragment, partId, onComplete = () => {
    }) {
        const solution = this.getSolution();
        if (!solution) {
            alert('Ihre Lösung war nicht valide!');
            return;
        }
        this.isCorrecting = true;
        // noinspection JSIgnoredPromiseFromCall
        this.dexieService.upsertSolution(exerciseFragment, partId, solution);
        this.mutationGQL
            .mutate(this.getMutationQueryVariables())
            .subscribe(({ data }) => {
            this.queryError = null;
            this.resultQuery = data;
            this.onCorrectionEnd(onComplete);
        }, (error) => {
            this.queryError = error;
            this.resultQuery = null;
            this.onCorrectionEnd(onComplete);
            console.error('There has been an graphQL error:', this.queryError);
        });
    }
    loadOldSolutionAbstract(exerciseFragment, partId, setOldSolution, onNoSolution = () => void 0) {
        return this.dexieService.getSolution(exerciseFragment, partId)
            .then((dbSol) => {
            if (dbSol) {
                setOldSolution(dbSol.solution);
            }
            else {
                onNoSolution();
            }
        });
    }
}
ComponentWithExerciseDirective.ɵfac = function ComponentWithExerciseDirective_Factory(t) { return new (t || ComponentWithExerciseDirective)(_angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdirectiveInject"](apollo_angular__WEBPACK_IMPORTED_MODULE_3__["Mutation"]), _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdirectiveInject"](_services_dexie_service__WEBPACK_IMPORTED_MODULE_4__["DexieService"])); };
ComponentWithExerciseDirective.ɵdir = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdefineDirective"]({ type: ComponentWithExerciseDirective, viewQuery: function ComponentWithExerciseDirective_Query(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵviewQuery"](_shared_tabs_tabs_component__WEBPACK_IMPORTED_MODULE_0__["TabsComponent"], 1);
    } if (rf & 2) {
        let _t;
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵqueryRefresh"](_t = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵloadQuery"]()) && (ctx.tabsComponent = _t.first);
    } }, features: [_angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵInheritDefinitionFeature"]] });


/***/ }),

/***/ "UE/p":
/*!*********************************************************!*\
  !*** ./src/app/tools/random-tools/random-tools-list.ts ***!
  \*********************************************************/
/*! exports provided: randomTools */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "randomTools", function() { return randomTools; });
const randomTools = [
    {
        id: 'bool',
        name: 'Boolesche Algebra',
        parts: [
            { id: 'fillOut', name: 'Wahrheitstabellen ausfüllen' },
            { id: 'create', name: 'Boolesche Formel erstellen' },
            { id: 'drawing', name: 'Schaltkreis zeichnen', disabled: true }
        ]
    },
    {
        id: 'nary',
        name: 'Zahlensysteme',
        parts: [
            { id: 'addition', name: 'Addition' },
            { id: 'conversion', name: 'Zahlenumwandlung' },
            { id: 'twoConversion', name: 'Zahlenumwandlung im Zweiersystem' }
        ]
    }
];


/***/ }),

/***/ "UZ51":
/*!***************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/uml/_components/uml-assoc-edit/uml-assoc-edit.component.ts ***!
  \***************************************************************************************************/
/*! exports provided: UmlAssocEditComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlAssocEditComponent", function() { return UmlAssocEditComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/forms */ "3Pt+");



class UmlAssocEditComponent {
    constructor() {
        this.cancel = new _angular_core__WEBPACK_IMPORTED_MODULE_0__["EventEmitter"]();
    }
    ngOnChanges(changes) {
        const labels = this.editedAssociation.labels();
        this.firstEnd = this.editedAssociation.getSourceCell();
        this.firstMult = labels[0].attrs.text.text;
        this.secondEnd = this.editedAssociation.getTargetCell();
        this.secondMult = labels[1].attrs.text.text;
    }
    updateAssoc(firstMultValue, secondMultValue) {
        this.editedAssociation.label(0, { position: 25, attrs: { text: { text: firstMultValue } } });
        this.editedAssociation.label(1, { position: -25, attrs: { text: { text: secondMultValue } } });
    }
}
UmlAssocEditComponent.ɵfac = function UmlAssocEditComponent_Factory(t) { return new (t || UmlAssocEditComponent)(); };
UmlAssocEditComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: UmlAssocEditComponent, selectors: [["it4all-uml-assoc-edit"]], inputs: { editedAssociation: "editedAssociation" }, outputs: { cancel: "cancel" }, features: [_angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵNgOnChangesFeature"]], decls: 31, vars: 6, consts: [[1, "card"], [1, "card-header"], [1, "card-header-title"], [1, "card-content"], [1, "columns"], [1, "column"], [1, "select"], ["title", "Multiplizit\u00E4t"], ["firstMultSelect", ""], [3, "selected"], ["secondMultSelect", ""], [1, "card-footer"], [1, "card-footer-item", 3, "click"]], template: function UmlAssocEditComponent_Template(rf, ctx) { if (rf & 1) {
        const _r2 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵgetCurrentView"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "header", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](2, "p", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](3, "Assoziation bearbeiten");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](4, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](5, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](6, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](7);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](8, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](9, "div", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](10, "select", 7, 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](12, "option", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](13, "1");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](14, "option", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](15, "*");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](16, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](17, "div", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](18, "select", 7, 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](20, "option", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](21, "1");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](22, "option", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](23, "*");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](24, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](25);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](26, "footer", 11);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](27, "a", 12);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵlistener"]("click", function UmlAssocEditComponent_Template_a_click_27_listener() { return ctx.cancel.emit(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](28, "Verwerfen");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](29, "a", 12);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵlistener"]("click", function UmlAssocEditComponent_Template_a_click_29_listener() { _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵrestoreView"](_r2); const _r0 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵreference"](11); const _r1 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵreference"](19); return ctx.updateAssoc(_r0.value, _r1.value); });
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](30, "Anwenden");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](7);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"](" ", ctx.firstEnd.getClassName(), " ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](5);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("selected", ctx.firstMult === "1");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("selected", ctx.firstMult === "*");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](6);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("selected", ctx.secondMult === "1");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("selected", ctx.secondMult === "*");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"](" ", ctx.secondEnd.getClassName(), " ");
    } }, directives: [_angular_forms__WEBPACK_IMPORTED_MODULE_1__["NgSelectOption"], _angular_forms__WEBPACK_IMPORTED_MODULE_1__["ɵangular_packages_forms_forms_z"]], encapsulation: 2 });


/***/ }),

/***/ "V+Il":
/*!***************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/uml/_components/uml-class-edit/uml-class-edit.component.ts ***!
  \***************************************************************************************************/
/*! exports provided: UmlClassEditComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlClassEditComponent", function() { return UmlClassEditComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/forms */ "3Pt+");




function UmlClassEditComponent_ng_container_5_option_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "option", 16);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const visibility_r7 = ctx.$implicit;
    const attr_r4 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]().$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("selected", visibility_r7 === attr_r4.visibility);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"](" ", visibility_r7, " ");
} }
function UmlClassEditComponent_ng_container_5_option_7_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "option", 16);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const umlType_r9 = ctx.$implicit;
    const attr_r4 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]().$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("selected", umlType_r9 === attr_r4.type);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](umlType_r9);
} }
function UmlClassEditComponent_ng_container_5_Template(rf, ctx) { if (rf & 1) {
    const _r12 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵgetCurrentView"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "select", 11);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](2, UmlClassEditComponent_ng_container_5_option_2_Template, 2, 2, "option", 12);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](3, " \u00A0 ");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](4, "input", 13);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](5, " : ");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](6, "select", 14);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](7, UmlClassEditComponent_ng_container_5_option_7_Template, 2, 2, "option", 12);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](8, " \u00A0 ");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](9, "button", 15);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵlistener"]("click", function UmlClassEditComponent_ng_container_5_Template_button_click_9_listener() { _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵrestoreView"](_r12); const attr_r4 = ctx.$implicit; const ctx_r11 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"](); return ctx_r11.removeAttribute(attr_r4); });
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](10, "-");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const attr_r4 = ctx.$implicit;
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("value", attr_r4.visibility);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx_r0.visibilities);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("value", attr_r4.name);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("value", attr_r4.type);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx_r0.umlTypes);
} }
function UmlClassEditComponent_option_9_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "option");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const visibility_r13 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](visibility_r13);
} }
function UmlClassEditComponent_option_14_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "option");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const umlType_r14 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](umlType_r14);
} }
function UmlClassEditComponent_ng_container_16_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵpipe"](2, "json");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const method_r15 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"](" ", _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵpipeBind1"](2, 1, method_r15), " ");
} }
class UmlClassEditComponent {
    constructor() {
        this.visibilities = ['+', '-', '#', '~'];
        this.umlTypes = ['String', 'int', 'double', 'char', 'boolean', 'void'];
        this.cancel = new _angular_core__WEBPACK_IMPORTED_MODULE_0__["EventEmitter"]();
    }
    removeAttribute(attr) {
        const newAttributes = this.editedClass.getAttributes().filter((a) => a !== attr);
        this.editedClass.setAttributes(newAttributes);
    }
}
UmlClassEditComponent.ɵfac = function UmlClassEditComponent_Factory(t) { return new (t || UmlClassEditComponent)(); };
UmlClassEditComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: UmlClassEditComponent, selectors: [["it4all-uml-class-edit"]], inputs: { editedClass: "editedClass" }, outputs: { cancel: "cancel" }, decls: 22, vars: 5, consts: [[1, "card"], [1, "card-header"], [1, "card-header-title"], [1, "card-content"], [4, "ngFor", "ngForOf"], ["title", "Sichtbarkeit"], ["type", "text", "title", "Attributname", "placeholder", "Attributname"], ["title", "Datentyp"], [1, "card-footer"], [1, "card-footer-item", 3, "click"], [1, "card-footer-item"], ["title", "Sichtbarkeit", 3, "value"], [3, "selected", 4, "ngFor", "ngForOf"], ["type", "text", "title", "Attributname", "placeholder", "Attributname", 3, "value"], ["title", "Datentyp", 3, "value"], ["title", "Attribut l\u00F6schen", 3, "click"], [3, "selected"]], template: function UmlClassEditComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "header", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](2, "p", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](4, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](5, UmlClassEditComponent_ng_container_5_Template, 11, 5, "ng-container", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](6, "hr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](7, "div");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](8, "select", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](9, UmlClassEditComponent_option_9_Template, 2, 1, "option", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](10, " \u00A0 ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](11, "input", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](12, " : ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](13, "select", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](14, UmlClassEditComponent_option_14_Template, 2, 1, "option", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](15, "hr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](16, UmlClassEditComponent_ng_container_16_Template, 3, 3, "ng-container", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](17, "footer", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](18, "a", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵlistener"]("click", function UmlClassEditComponent_Template_a_click_18_listener() { return ctx.cancel.emit(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](19, "Verwerfen");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](20, "a", 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](21, "Anwenden");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx.editedClass.getClassName());
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx.editedClass.getAttributes());
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](4);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx.visibilities);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](5);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx.umlTypes);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx.editedClass.getMethods());
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_1__["NgForOf"], _angular_forms__WEBPACK_IMPORTED_MODULE_2__["NgSelectOption"], _angular_forms__WEBPACK_IMPORTED_MODULE_2__["ɵangular_packages_forms_forms_z"]], pipes: [_angular_common__WEBPACK_IMPORTED_MODULE_1__["JsonPipe"]], encapsulation: 2 });


/***/ }),

/***/ "VRMR":
/*!*******************************************************************!*\
  !*** ./src/app/tools/collection-tools/collection-tool-helpers.ts ***!
  \*******************************************************************/
/*! exports provided: getDefaultEditorOptions */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "getDefaultEditorOptions", function() { return getDefaultEditorOptions; });
function getDefaultEditorOptions(mode) {
    // noinspection JSUnusedGlobalSymbols
    return {
        mode,
        lineNumbers: true,
        theme: 'eclipse',
        indentUnit: 4,
        readOnly: false,
        extraKeys: {
            Tab: (cm) => cm.execCommand('indentMore'),
            'Shift-Tab': (cm) => cm.execCommand('indentLess'),
        }
    };
}


/***/ }),

/***/ "WY1b":
/*!************************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/sql/_results/sql-execution-result/sql-execution-result.component.ts ***!
  \************************************************************************************************************/
/*! exports provided: SqlExecutionResultComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SqlExecutionResultComponent", function() { return SqlExecutionResultComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _components_query_result_table_query_result_table_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../_components/query-result-table/query-result-table.component */ "DeMQ");



function SqlExecutionResultComponent_div_6_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](1, "it4all-query-result-table", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("queryResult", ctx_r0.result.userResult);
} }
function SqlExecutionResultComponent_ng_template_12_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, "Es gab einen Fehler beim Ausf\u00FChren ihrer Query!");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} }
class SqlExecutionResultComponent {
}
SqlExecutionResultComponent.ɵfac = function SqlExecutionResultComponent_Factory(t) { return new (t || SqlExecutionResultComponent)(); };
SqlExecutionResultComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: SqlExecutionResultComponent, selectors: [["it4all-sql-execution-result"]], inputs: { result: "result" }, decls: 14, vars: 3, consts: [[1, "subtitle", "is-4", "has-text-centered"], [1, "columns"], [1, "column"], ["class", "table-container", 4, "ngIf", "ngIfElse"], [1, "table-container"], [3, "queryResult"], ["noUserResultBlock", ""], [1, "notification", "is-danger"]], template: function SqlExecutionResultComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "h3", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, "Vergleich der Ergebnistabellen");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](2, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](3, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](4, "h4", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](5, "Nutzer");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](6, SqlExecutionResultComponent_div_6_Template, 2, 1, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](7, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](8, "h4", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](9, "Muster");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](10, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](11, "it4all-query-result-table", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](12, SqlExecutionResultComponent_ng_template_12_Template, 2, 0, "ng-template", null, 6, _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplateRefExtractor"]);
    } if (rf & 2) {
        const _r1 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵreference"](13);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](6);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx.result.userResult)("ngIfElse", _r1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](5);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("queryResult", ctx.result.sampleResult);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_1__["NgIf"], _components_query_result_table_query_result_table_component__WEBPACK_IMPORTED_MODULE_2__["QueryResultTableComponent"]], encapsulation: 2 });


/***/ }),

/***/ "XO5P":
/*!*****************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/flask/flask-exercise/flask-exercise.component.ts ***!
  \*****************************************************************************************/
/*! exports provided: FlaskExerciseComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FlaskExerciseComponent", function() { return FlaskExerciseComponent; });
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../../../_services/apollo_services */ "o20/");
/* harmony import */ var _helpers_component_with_exercise_directive__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../_helpers/component-with-exercise.directive */ "TRIe");
/* harmony import */ var _components_files_exercise_files_exercise_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../_components/files-exercise/files-exercise.component */ "Emuw");
/* harmony import */ var codemirror_mode_jinja2_jinja2__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! codemirror/mode/jinja2/jinja2 */ "ToA7");
/* harmony import */ var codemirror_mode_jinja2_jinja2__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(codemirror_mode_jinja2_jinja2__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var codemirror_mode_python_python__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! codemirror/mode/python/python */ "25Eh");
/* harmony import */ var codemirror_mode_python_python__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(codemirror_mode_python_python__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _services_dexie_service__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../../../../_services/dexie.service */ "4di/");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _shared_solution_saved_solution_saved_component__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../../../../shared/solution-saved/solution-saved.component */ "rqf4");
/* harmony import */ var _shared_points_notification_points_notification_component__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ../../../../shared/points-notification/points-notification.component */ "ef8k");












function FlaskExerciseComponent_div_5_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementStart"](0, "div");
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementStart"](2, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵtext"](4, ": ");
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelement"](5, "span", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementEnd"]();
} if (rf & 2) {
    const singleTestConfig_r3 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵtextInterpolate1"](" ", singleTestConfig_r3.id, ". ");
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵtextInterpolate"](singleTestConfig_r3.testName);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵproperty"]("innerHTML", singleTestConfig_r3.description, _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵsanitizeHtml"]);
} }
function FlaskExerciseComponent_div_7_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementStart"](0, "div", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementStart"](1, "div", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵtext"](2, "Es gab einen internen Fehler bei der Korrektur:");
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementStart"](3, "div", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementStart"](4, "pre");
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵtext"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵadvance"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵtextInterpolate"](ctx_r1.queryError.message);
} }
function FlaskExerciseComponent_ng_container_8_div_5_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementStart"](0, "div", 11);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementStart"](1, "div", 15);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementStart"](2, "header", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementStart"](4, "div", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementStart"](5, "pre");
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵtext"](6);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementEnd"]();
} if (rf & 2) {
    const testResult_r5 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵproperty"]("ngClass", testResult_r5.successful ? "is-success" : "is-danger");
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵtextInterpolate"](testResult_r5.testName);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵtextInterpolate"](testResult_r5.stderr.join("\n"));
} }
function FlaskExerciseComponent_ng_container_8_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementStart"](1, "div", 11);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelement"](2, "it4all-solution-saved", 12);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementStart"](3, "div", 11);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelement"](4, "it4all-points-notification", 13);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵtemplate"](5, FlaskExerciseComponent_ng_container_8_div_5_Template, 7, 3, "div", 14);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r2 = _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵproperty"]("solutionSaved", ctx_r2.correctionResult.solutionSaved);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵproperty"]("points", ctx_r2.result.points)("maxPoints", ctx_r2.result.maxPoints);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵproperty"]("ngForOf", ctx_r2.result.testResults);
} }
function getIdForFlaskExPart(flaskExPart) {
    switch (flaskExPart) {
        case _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["FlaskExercisePart"].FlaskSingleExPart:
            return 'solve';
    }
}
class FlaskExerciseComponent extends _helpers_component_with_exercise_directive__WEBPACK_IMPORTED_MODULE_1__["ComponentWithExerciseDirective"] {
    constructor(flaskCorrectionGQL, dexieService) {
        super(flaskCorrectionGQL, dexieService);
        this.partId = getIdForFlaskExPart(_services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["FlaskExercisePart"].FlaskSingleExPart);
        this.exerciseFileFragments = [];
    }
    ngOnInit() {
        this.exerciseFileFragments = this.contentFragment.files;
        this.loadOldSolutionAbstract(this.exerciseFragment, this.partId, (oldSol) => this.exerciseFileFragments = oldSol.files);
    }
    // Sample solutions
    get sampleSolutions() {
        return this.contentFragment.flaskSampleSolutions;
    }
    // Correction
    getSolution() {
        const files = this.exerciseFileFragments
            .map(({ name, fileType, content, editable }) => {
            return { name, fileType, content, editable };
        });
        return { files };
    }
    getMutationQueryVariables() {
        return {
            exId: this.exerciseFragment.exerciseId,
            collId: this.exerciseFragment.collectionId,
            part: _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["FlaskExercisePart"].FlaskSingleExPart,
            solution: this.getSolution()
        };
    }
    correct() {
        this.correctAbstract(this.exerciseFragment, this.partId, () => {
            if (this.filesExerciseComponent) {
                this.filesExerciseComponent.toggleCorrectionTab();
            }
        });
    }
    // Result
    get correctionResult() {
        var _a, _b;
        return (_b = (_a = this.resultQuery) === null || _a === void 0 ? void 0 : _a.me.flaskExercise) === null || _b === void 0 ? void 0 : _b.correct;
    }
    get result() {
        var _a;
        return (_a = this.correctionResult) === null || _a === void 0 ? void 0 : _a.result;
    }
}
FlaskExerciseComponent.ɵfac = function FlaskExerciseComponent_Factory(t) { return new (t || FlaskExerciseComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵdirectiveInject"](_services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["FlaskCorrectionGQL"]), _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵdirectiveInject"](_services_dexie_service__WEBPACK_IMPORTED_MODULE_6__["DexieService"])); };
FlaskExerciseComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵdefineComponent"]({ type: FlaskExerciseComponent, selectors: [["it4all-flask-exercise"]], viewQuery: function FlaskExerciseComponent_Query(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵviewQuery"](_components_files_exercise_files_exercise_component__WEBPACK_IMPORTED_MODULE_2__["FilesExerciseComponent"], 1);
    } if (rf & 2) {
        let _t;
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵqueryRefresh"](_t = _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵloadQuery"]()) && (ctx.filesExerciseComponent = _t.first);
    } }, inputs: { exerciseFragment: "exerciseFragment", contentFragment: "contentFragment" }, features: [_angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵInheritDefinitionFeature"]], decls: 9, vars: 7, consts: [["defaultMode", "'python'", 3, "exerciseFiles", "isCorrecting", "sampleSolutions", "correct"], ["exText", "", 1, "content"], [1, "mb-3", 3, "innerHTML"], [4, "ngFor", "ngForOf"], ["correctionContent", ""], ["class", "message is-danger", 4, "ngIf"], [4, "ngIf"], [3, "innerHTML"], [1, "message", "is-danger"], [1, "message-header"], [1, "message-body"], [1, "my-3"], [3, "solutionSaved"], [3, "points", "maxPoints"], ["class", "my-3", 4, "ngFor", "ngForOf"], [1, "message", 3, "ngClass"]], template: function FlaskExerciseComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementStart"](0, "it4all-files-exercise", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵlistener"]("correct", function FlaskExerciseComponent_Template_it4all_files_exercise_correct_0_listener() { return ctx.correct(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementStart"](1, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelement"](2, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementStart"](3, "p");
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵtext"](4, "Es werden folgende Testf\u00E4lle ausgef\u00FChrt:");
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵtemplate"](5, FlaskExerciseComponent_div_5_Template, 6, 3, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementContainerStart"](6, 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵtemplate"](7, FlaskExerciseComponent_div_7_Template, 6, 1, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵtemplate"](8, FlaskExerciseComponent_ng_container_8_Template, 6, 4, "ng-container", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementContainerEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵproperty"]("exerciseFiles", ctx.exerciseFileFragments)("isCorrecting", ctx.isCorrecting)("sampleSolutions", ctx.sampleSolutions);
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵproperty"]("innerHTML", ctx.exerciseFragment.text, _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵsanitizeHtml"]);
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵproperty"]("ngForOf", ctx.contentFragment.testConfig.tests);
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵproperty"]("ngIf", ctx.queryError);
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_5__["ɵɵproperty"]("ngIf", ctx.resultQuery);
    } }, directives: [_components_files_exercise_files_exercise_component__WEBPACK_IMPORTED_MODULE_2__["FilesExerciseComponent"], _angular_common__WEBPACK_IMPORTED_MODULE_7__["NgForOf"], _angular_common__WEBPACK_IMPORTED_MODULE_7__["NgIf"], _shared_solution_saved_solution_saved_component__WEBPACK_IMPORTED_MODULE_8__["SolutionSavedComponent"], _shared_points_notification_points_notification_component__WEBPACK_IMPORTED_MODULE_9__["PointsNotificationComponent"], _angular_common__WEBPACK_IMPORTED_MODULE_7__["NgClass"]], encapsulation: 2 });


/***/ }),

/***/ "Xlg+":
/*!***************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/collections-list/collections-list.component.ts ***!
  \***************************************************************************************/
/*! exports provided: CollectionsListComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CollectionsListComponent", function() { return CollectionsListComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _shared_breadcrumbs_breadcrumbs_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../shared/breadcrumbs/breadcrumbs.component */ "lmDL");





const _c0 = function (a0) { return [a0]; };
function CollectionsListComponent_ng_container_1_div_6_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "div", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](2, "header", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](3, "p", 11);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](5, "div", 12);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](6);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](7, "footer", 13);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](8, "a", 14);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](9, "Zur Sammlung");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const coll_r4 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate2"]("", coll_r4.collectionId, ". ", coll_r4.title, "");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"]("", coll_r4.exerciseCount, " Aufgaben");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("routerLink", _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵpureFunction1"](4, _c0, coll_r4.collectionId));
} }
function CollectionsListComponent_ng_container_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "h1", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](3, "div", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](4, "it4all-breadcrumbs", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](5, "div", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](6, CollectionsListComponent_ng_container_1_div_6_Template, 10, 6, "div", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"]("Tool ", ctx_r0.tool.name, ": Sammlungen");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("parts", ctx_r0.breadCrumbs);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx_r0.collections);
} }
function CollectionsListComponent_ng_template_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 15);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, "Lade Daten...");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} }
// url => /tools/:toolId/collections
class CollectionsListComponent {
    constructor(route, collectionsGQL) {
        this.route = route;
        this.collectionsGQL = collectionsGQL;
    }
    ngOnInit() {
        this.sub = this.route.paramMap.subscribe((paramMap) => {
            const toolId = paramMap.get('toolId');
            this.collectionsGQL
                .watch({ toolId })
                .valueChanges
                .subscribe(({ data }) => this.collectionListQuery = data);
        });
    }
    ngOnDestroy() {
        this.sub.unsubscribe();
    }
    get tool() {
        return this.collectionListQuery.me.tool;
    }
    get collections() {
        return this.tool.collections;
    }
    get breadCrumbs() {
        return [
            { routerLinkPart: '/', title: 'Tools' },
            { routerLinkPart: `tools/${this.tool.id}`, title: this.tool.name },
            { routerLinkPart: 'collections', title: 'Sammlungen' },
        ];
    }
}
CollectionsListComponent.ɵfac = function CollectionsListComponent_Factory(t) { return new (t || CollectionsListComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_angular_router__WEBPACK_IMPORTED_MODULE_1__["ActivatedRoute"]), _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_services_apollo_services__WEBPACK_IMPORTED_MODULE_2__["CollectionListGQL"])); };
CollectionsListComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: CollectionsListComponent, selectors: [["ng-component"]], decls: 4, vars: 2, consts: [[1, "container"], [4, "ngIf", "ngIfElse"], ["loadingDataBlock", ""], [1, "title", "is-3", "has-text-centered"], [1, "mb-3"], [3, "parts"], [1, "columns", "is-multiline"], ["class", "column is-one-quarter", 4, "ngFor", "ngForOf"], [1, "column", "is-one-quarter"], [1, "card"], [1, "card-header"], [1, "card-header-title", "is-centered"], [1, "card-content"], [1, "card-footer"], [1, "card-footer-item", 3, "routerLink"], [1, "notification", "is-primary", "has-text-centered"]], template: function CollectionsListComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](1, CollectionsListComponent_ng_container_1_Template, 7, 3, "ng-container", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](2, CollectionsListComponent_ng_template_2_Template, 2, 0, "ng-template", null, 2, _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplateRefExtractor"]);
    } if (rf & 2) {
        const _r1 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵreference"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx.collectionListQuery)("ngIfElse", _r1);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_3__["NgIf"], _shared_breadcrumbs_breadcrumbs_component__WEBPACK_IMPORTED_MODULE_4__["BreadcrumbsComponent"], _angular_common__WEBPACK_IMPORTED_MODULE_3__["NgForOf"], _angular_router__WEBPACK_IMPORTED_MODULE_1__["RouterLinkWithHref"]], encapsulation: 2 });


/***/ }),

/***/ "YH1L":
/*!**********************************************************************************!*\
  !*** ./src/app/tools/random-tools/bool/bool-exercise/bool-exercise.component.ts ***!
  \**********************************************************************************/
/*! exports provided: BoolExerciseComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BoolExerciseComponent", function() { return BoolExerciseComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _bool_fill_out_bool_fill_out_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../bool-fill-out/bool-fill-out.component */ "HKNS");
/* harmony import */ var _bool_create_bool_create_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../bool-create/bool-create.component */ "2X8u");





function BoolExerciseComponent_it4all_bool_fillout_4_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](0, "it4all-bool-fillout");
} }
function BoolExerciseComponent_it4all_bool_create_5_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](0, "it4all-bool-create");
} }
function BoolExerciseComponent_div_6_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r2 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"](" Part \"", ctx_r2.part, "\" was not found! ");
} }
class BoolExerciseComponent {
    constructor(route) {
        this.route = route;
    }
    ngOnInit() {
        this.sub = this.route.paramMap.subscribe((paramMap) => {
            this.part = paramMap.get('part');
            this.title = this.getTitleForPart();
        });
    }
    getTitleForPart() {
        switch (this.part) {
            case 'fillOut':
                return 'Wahrheitstabellen ausfüllen';
            case 'create':
                return 'Boolesche Formel erstellen';
            case 'drawing':
                return '';
            default:
                return 'Fehler!';
        }
    }
}
BoolExerciseComponent.ɵfac = function BoolExerciseComponent_Factory(t) { return new (t || BoolExerciseComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_angular_router__WEBPACK_IMPORTED_MODULE_1__["ActivatedRoute"])); };
BoolExerciseComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: BoolExerciseComponent, selectors: [["ng-component"]], decls: 7, vars: 4, consts: [[1, "container"], [1, "title", "is-3", "has-text-centered"], [3, "ngSwitch"], [4, "ngSwitchCase"], ["class", "notification is-light-danger has-text-centered", 4, "ngSwitchDefault"], [1, "notification", "is-light-danger", "has-text-centered"]], template: function BoolExerciseComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "h1", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerStart"](3, 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](4, BoolExerciseComponent_it4all_bool_fillout_4_Template, 1, 0, "it4all-bool-fillout", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](5, BoolExerciseComponent_it4all_bool_create_5_Template, 1, 0, "it4all-bool-create", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](6, BoolExerciseComponent_div_6_Template, 2, 1, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx.title);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngSwitch", ctx.part);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngSwitchCase", "fillOut");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngSwitchCase", "create");
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_2__["NgSwitch"], _angular_common__WEBPACK_IMPORTED_MODULE_2__["NgSwitchCase"], _angular_common__WEBPACK_IMPORTED_MODULE_2__["NgSwitchDefault"], _bool_fill_out_bool_fill_out_component__WEBPACK_IMPORTED_MODULE_3__["BoolFillOutComponent"], _bool_create_bool_create_component__WEBPACK_IMPORTED_MODULE_4__["BoolCreateComponent"]], encapsulation: 2 });


/***/ }),

/***/ "ZAI4":
/*!*******************************!*\
  !*** ./src/app/app.module.ts ***!
  \*******************************/
/*! exports provided: AppModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AppModule", function() { return AppModule; });
/* harmony import */ var _angular_platform_browser__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/platform-browser */ "jhN1");
/* harmony import */ var _app_component__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./app.component */ "Sy1n");
/* harmony import */ var _app_routing_module__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./app-routing.module */ "vY5A");
/* harmony import */ var _angular_common_http__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/common/http */ "tk/3");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/forms */ "3Pt+");
/* harmony import */ var _helpers_error_interceptor__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./_helpers/error.interceptor */ "nSnL");
/* harmony import */ var _ctrl_ngx_codemirror__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! @ctrl/ngx-codemirror */ "Xl2X");
/* harmony import */ var _tools_random_tools_random_tools_module__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ./tools/random-tools/random-tools.module */ "MQgL");
/* harmony import */ var _shared_shared_module__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./shared/shared.module */ "PCNd");
/* harmony import */ var _tools_collection_tools_collection_tools_module__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./tools/collection-tools/collection-tools.module */ "0iet");
/* harmony import */ var _graphql_module__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ./graphql.module */ "4KHl");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _tool_overview_tool_overview_component__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ./tool-overview/tool-overview.component */ "u6rX");
/* harmony import */ var _user_management_login_form_login_form_component__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ./user_management/login-form/login-form.component */ "AgHE");
/* harmony import */ var _user_management_register_form_register_form_component__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! ./user_management/register-form/register-form.component */ "oCnN");
/* harmony import */ var _lti_lti_component__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! ./lti/lti.component */ "fxtn");
















class AppModule {
}
AppModule.ɵfac = function AppModule_Factory(t) { return new (t || AppModule)(); };
AppModule.ɵmod = _angular_core__WEBPACK_IMPORTED_MODULE_11__["ɵɵdefineNgModule"]({ type: AppModule, bootstrap: [_app_component__WEBPACK_IMPORTED_MODULE_1__["AppComponent"]] });
AppModule.ɵinj = _angular_core__WEBPACK_IMPORTED_MODULE_11__["ɵɵdefineInjector"]({ providers: [
        _angular_platform_browser__WEBPACK_IMPORTED_MODULE_0__["Title"],
        { provide: _angular_common_http__WEBPACK_IMPORTED_MODULE_3__["HTTP_INTERCEPTORS"], useClass: _helpers_error_interceptor__WEBPACK_IMPORTED_MODULE_5__["ErrorInterceptor"], multi: true }
    ], imports: [[
            _angular_platform_browser__WEBPACK_IMPORTED_MODULE_0__["BrowserModule"], _angular_common_http__WEBPACK_IMPORTED_MODULE_3__["HttpClientModule"], _app_routing_module__WEBPACK_IMPORTED_MODULE_2__["AppRoutingModule"], _angular_forms__WEBPACK_IMPORTED_MODULE_4__["FormsModule"], _angular_forms__WEBPACK_IMPORTED_MODULE_4__["ReactiveFormsModule"],
            _ctrl_ngx_codemirror__WEBPACK_IMPORTED_MODULE_6__["CodemirrorModule"],
            // Own Modules
            _shared_shared_module__WEBPACK_IMPORTED_MODULE_8__["SharedModule"],
            _tools_collection_tools_collection_tools_module__WEBPACK_IMPORTED_MODULE_9__["CollectionToolsModule"],
            _tools_random_tools_random_tools_module__WEBPACK_IMPORTED_MODULE_7__["RandomToolsModule"],
            _graphql_module__WEBPACK_IMPORTED_MODULE_10__["GraphQLModule"]
        ]] });
(function () { (typeof ngJitMode === "undefined" || ngJitMode) && _angular_core__WEBPACK_IMPORTED_MODULE_11__["ɵɵsetNgModuleScope"](AppModule, { declarations: [_app_component__WEBPACK_IMPORTED_MODULE_1__["AppComponent"], _tool_overview_tool_overview_component__WEBPACK_IMPORTED_MODULE_12__["ToolOverviewComponent"], _user_management_login_form_login_form_component__WEBPACK_IMPORTED_MODULE_13__["LoginFormComponent"], _user_management_register_form_register_form_component__WEBPACK_IMPORTED_MODULE_14__["RegisterFormComponent"], _lti_lti_component__WEBPACK_IMPORTED_MODULE_15__["LtiComponent"]], imports: [_angular_platform_browser__WEBPACK_IMPORTED_MODULE_0__["BrowserModule"], _angular_common_http__WEBPACK_IMPORTED_MODULE_3__["HttpClientModule"], _app_routing_module__WEBPACK_IMPORTED_MODULE_2__["AppRoutingModule"], _angular_forms__WEBPACK_IMPORTED_MODULE_4__["FormsModule"], _angular_forms__WEBPACK_IMPORTED_MODULE_4__["ReactiveFormsModule"],
        _ctrl_ngx_codemirror__WEBPACK_IMPORTED_MODULE_6__["CodemirrorModule"],
        // Own Modules
        _shared_shared_module__WEBPACK_IMPORTED_MODULE_8__["SharedModule"],
        _tools_collection_tools_collection_tools_module__WEBPACK_IMPORTED_MODULE_9__["CollectionToolsModule"],
        _tools_random_tools_random_tools_module__WEBPACK_IMPORTED_MODULE_7__["RandomToolsModule"],
        _graphql_module__WEBPACK_IMPORTED_MODULE_10__["GraphQLModule"]] }); })();


/***/ }),

/***/ "Zy2k":
/*!***************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/all-exercises-overview/all-exercises-overview.component.ts ***!
  \***************************************************************************************************/
/*! exports provided: AllExercisesOverviewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AllExercisesOverviewComponent", function() { return AllExercisesOverviewComponent; });
/* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../../helpers */ "Afm0");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _components_exercise_link_card_exercise_link_card_component__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../_components/exercise-link-card/exercise-link-card.component */ "xeHz");






function AllExercisesOverviewComponent_ng_container_1_div_5_Template(rf, ctx) { if (rf & 1) {
    const _r7 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵgetCurrentView"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "button", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵlistener"]("click", function AllExercisesOverviewComponent_ng_container_1_div_5_Template_button_click_1_listener() { _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵrestoreView"](_r7); const topicWithLevel_r5 = ctx.$implicit; const ctx_r6 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](2); return ctx_r6.toggleFilter(topicWithLevel_r5); });
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const topicWithLevel_r5 = ctx.$implicit;
    const ctx_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵclassProp"]("is-link", ctx_r3.filtersActivated.get(topicWithLevel_r5.topic));
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" ", topicWithLevel_r5.topic.title, " ");
} }
function AllExercisesOverviewComponent_ng_container_1_div_10_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelement"](1, "it4all-exercise-link-card", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const metaData_r8 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("exercise", metaData_r8);
} }
function AllExercisesOverviewComponent_ng_container_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "section");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "h2", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3, "Filter");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](4, "div", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](5, AllExercisesOverviewComponent_ng_container_1_div_5_Template, 3, 3, "div", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelement"](6, "hr");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](7, "h1", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](8, "Alle Aufgaben");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](9, "div", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](10, AllExercisesOverviewComponent_ng_container_1_div_10_Template, 2, 1, "div", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx_r0.distinctTopicWithLevels);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx_r0.filteredExercises);
} }
function AllExercisesOverviewComponent_ng_template_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 11);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1, "Lade Aufgaben...");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} }
class AllExercisesOverviewComponent {
    constructor(route, allExercisesOverviewGQL) {
        this.route = route;
        this.allExercisesOverviewGQL = allExercisesOverviewGQL;
        this.filtersActivated = new Map();
    }
    ngOnInit() {
        this.sub = this.route.paramMap.subscribe((paramMap) => {
            const toolId = paramMap.get('toolId');
            this.allExercisesOverviewGQL
                .watch({ toolId })
                .valueChanges
                .subscribe(({ data }) => {
                this.allExercisesOverviewQuery = data;
                this.filteredExercises = this.allExercisesOverviewQuery.me.tool.allExercises;
                this.distinctTopicWithLevels = Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["distinctObjectArray"])(Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["flatMapArray"])(this.allExercisesOverviewQuery.me.tool.allExercises, (exercises) => exercises.topicsWithLevels), (t) => t.topic.abbreviation);
            });
        });
    }
    ngOnDestroy() {
        this.sub.unsubscribe();
    }
    toggleFilter(topicWithLevel) {
        this.filtersActivated.set(topicWithLevel.topic, !this.filtersActivated.get(topicWithLevel.topic));
        const activatedFilters = Array.from(this.filtersActivated.entries())
            .filter(([_, activated]) => activated)
            .map(([t, _]) => t);
        if (activatedFilters.length > 0) {
            this.filteredExercises = this.allExercisesOverviewQuery.me.tool.allExercises
                .filter((metaData) => activatedFilters.every((t) => this.exerciseHasTag(metaData, t)));
        }
        else {
            this.filteredExercises = this.allExercisesOverviewQuery.me.tool.allExercises;
        }
    }
    exerciseHasTag(exercise, tag) {
        return exercise.topicsWithLevels
            .map((t) => t.topic.abbreviation)
            .includes(tag.abbreviation);
    }
}
AllExercisesOverviewComponent.ɵfac = function AllExercisesOverviewComponent_Factory(t) { return new (t || AllExercisesOverviewComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdirectiveInject"](_angular_router__WEBPACK_IMPORTED_MODULE_2__["ActivatedRoute"]), _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdirectiveInject"](_services_apollo_services__WEBPACK_IMPORTED_MODULE_3__["AllExercisesOverviewGQL"])); };
AllExercisesOverviewComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineComponent"]({ type: AllExercisesOverviewComponent, selectors: [["ng-component"]], decls: 4, vars: 2, consts: [[1, "container"], [4, "ngIf", "ngIfElse"], ["loadingExercisesBlock", ""], [1, "title", "is-3", "has-text-centered"], [1, "columns", "is-multiline"], ["class", "column is-one-fifth", 4, "ngFor", "ngForOf"], ["class", "column is-one-third-desktop", 4, "ngFor", "ngForOf"], [1, "column", "is-one-fifth"], [1, "button", "is-fullwidth", 3, "click"], [1, "column", "is-one-third-desktop"], [3, "exercise"], [1, "notification", "is-primary", "has-text-centered"]], template: function AllExercisesOverviewComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](1, AllExercisesOverviewComponent_ng_container_1_Template, 11, 2, "ng-container", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](2, AllExercisesOverviewComponent_ng_template_2_Template, 2, 0, "ng-template", null, 2, _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplateRefExtractor"]);
    } if (rf & 2) {
        const _r1 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵreference"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", ctx.allExercisesOverviewQuery)("ngIfElse", _r1);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_4__["NgIf"], _angular_common__WEBPACK_IMPORTED_MODULE_4__["NgForOf"], _components_exercise_link_card_exercise_link_card_component__WEBPACK_IMPORTED_MODULE_5__["ExerciseLinkCardComponent"]], encapsulation: 2 });


/***/ }),

/***/ "aO+D":
/*!*********************************************************************************!*\
  !*** ./src/app/tools/random-tools/random-overview/random-overview.component.ts ***!
  \*********************************************************************************/
/*! exports provided: RandomOverviewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RandomOverviewComponent", function() { return RandomOverviewComponent; });
/* harmony import */ var _random_tools_list__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../random-tools-list */ "UE/p");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/common */ "ofXK");




const _c0 = function (a0) { return [a0]; };
function RandomOverviewComponent_ng_container_10_a_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "a", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const part_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]().$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("routerLink", _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵpureFunction1"](2, _c0, part_r1.id));
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](part_r1.name);
} }
function RandomOverviewComponent_ng_container_10_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](1, RandomOverviewComponent_ng_container_10_a_1_Template, 2, 4, "a", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const part_r1 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", !part_r1.disabled);
} }
class RandomOverviewComponent {
    constructor(route) {
        this.route = route;
    }
    ngOnInit() {
        this.sub = this.route.paramMap
            .subscribe((paramMap) => this.tool = _random_tools_list__WEBPACK_IMPORTED_MODULE_0__["randomTools"].find((t) => t.id === paramMap.get('toolId')));
    }
}
RandomOverviewComponent.ɵfac = function RandomOverviewComponent_Factory(t) { return new (t || RandomOverviewComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdirectiveInject"](_angular_router__WEBPACK_IMPORTED_MODULE_2__["ActivatedRoute"])); };
RandomOverviewComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineComponent"]({ type: RandomOverviewComponent, selectors: [["ng-component"]], decls: 11, vars: 2, consts: [[1, "container"], [1, "title", "is-3", "has-text-centered"], [1, "buttons"], ["routerLink", "../..", 1, "button", "is-primary", "is-fullwidth"], [1, "subtitle", "is-3", "has-text-centered"], [4, "ngFor", "ngForOf"], ["class", "button is-link is-fullwidth", 3, "routerLink", 4, "ngIf"], [1, "button", "is-link", "is-fullwidth", 3, "routerLink"]], template: function RandomOverviewComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "h1", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](3, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](4, "a", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](5, "Zur\u00FCck zur Tool\u00FCbersicht");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelement"](6, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](7, "h2", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](8, "\u00DCbungsaufgaben");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](9, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](10, RandomOverviewComponent_ng_container_10_Template, 2, 1, "ng-container", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"]("Tool ", ctx.tool.name, "");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](8);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx.tool.parts);
    } }, directives: [_angular_router__WEBPACK_IMPORTED_MODULE_2__["RouterLinkWithHref"], _angular_common__WEBPACK_IMPORTED_MODULE_3__["NgForOf"], _angular_common__WEBPACK_IMPORTED_MODULE_3__["NgIf"]], encapsulation: 2 });


/***/ }),

/***/ "b4kd":
/*!***********************************************!*\
  !*** ./src/app/shared/tabs/tabs.component.ts ***!
  \***********************************************/
/*! exports provided: TabsComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "TabsComponent", function() { return TabsComponent; });
/* harmony import */ var _tab_tab_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../tab/tab.component */ "4YYW");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common */ "ofXK");



function TabsComponent_li_2_Template(rf, ctx) { if (rf & 1) {
    const _r3 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵgetCurrentView"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "li", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵlistener"]("click", function TabsComponent_li_2_Template_li_click_0_listener() { _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵrestoreView"](_r3); const tab_r1 = ctx.$implicit; const ctx_r2 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](); return ctx_r2.selectTab(tab_r1); });
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "a");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const tab_r1 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵclassProp"]("is-active", tab_r1.active);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](tab_r1.title);
} }
const _c0 = ["*"];
class TabsComponent {
    ngAfterContentInit() {
        const activeTabs = this.tabs.filter((t) => t.active);
        if (this.tabs.length > 0 && activeTabs.length === 0) {
            this.selectTab(this.tabs.first);
        }
    }
    selectTab(tab) {
        if (tab.selectable) {
            this.tabs.toArray().forEach((t) => t.active = false);
            tab.active = true;
        }
        else {
            alert('Sie können diesen Tab momentan nicht aktivieren!');
        }
    }
    selectTabByTitle(title) {
        const maybeTab = this.tabs.toArray().find((tabComponent) => tabComponent.title === title);
        if (maybeTab) {
            this.selectTab(maybeTab);
        }
    }
}
TabsComponent.ɵfac = function TabsComponent_Factory(t) { return new (t || TabsComponent)(); };
TabsComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineComponent"]({ type: TabsComponent, selectors: [["it4all-tabs"]], contentQueries: function TabsComponent_ContentQueries(rf, ctx, dirIndex) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵcontentQuery"](dirIndex, _tab_tab_component__WEBPACK_IMPORTED_MODULE_0__["TabComponent"], 0);
    } if (rf & 2) {
        let _t;
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵqueryRefresh"](_t = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵloadQuery"]()) && (ctx.tabs = _t);
    } }, ngContentSelectors: _c0, decls: 4, vars: 1, consts: [[1, "tabs", "is-centered", "is-boxed", "is-fullwidth"], [3, "is-active", "click", 4, "ngFor", "ngForOf"], [3, "click"]], template: function TabsComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵprojectionDef"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "ul");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](2, TabsComponent_li_2_Template, 3, 3, "li", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵprojection"](3);
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx.tabs);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_2__["NgForOf"]], encapsulation: 2 });


/***/ }),

/***/ "c3C5":
/*!*******************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/_components/exercise-file-card/exercise-file-card.component.ts ***!
  \*******************************************************************************************************/
/*! exports provided: ExerciseFileCardComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExerciseFileCardComponent", function() { return ExerciseFileCardComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");

class ExerciseFileCardComponent {
}
ExerciseFileCardComponent.ɵfac = function ExerciseFileCardComponent_Factory(t) { return new (t || ExerciseFileCardComponent)(); };
ExerciseFileCardComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: ExerciseFileCardComponent, selectors: [["it4all-exercise-file-card"]], inputs: { exerciseFile: "exerciseFile" }, decls: 7, vars: 2, consts: [[1, "card"], [1, "card-header"], [1, "card-header-title"], [1, "card-content"]], template: function ExerciseFileCardComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "header", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](2, "p", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](4, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](5, "pre");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](6);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx.exerciseFile.name);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx.exerciseFile.content);
    } }, encapsulation: 2 });


/***/ }),

/***/ "d8br":
/*!*******************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/collection-tool-overview/collection-tool-overview.component.ts ***!
  \*******************************************************************************************************/
/*! exports provided: CollectionToolOverviewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CollectionToolOverviewComponent", function() { return CollectionToolOverviewComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _shared_breadcrumbs_breadcrumbs_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../shared/breadcrumbs/breadcrumbs.component */ "lmDL");
/* harmony import */ var _components_proficiency_card_proficiency_card_component__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../_components/proficiency-card/proficiency-card.component */ "vXFn");






function CollectionToolOverviewComponent_ng_container_1_div_39_div_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 22);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](1, "it4all-proficiency-card", 23);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const proficiency_r7 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("proficiency", proficiency_r7);
} }
function CollectionToolOverviewComponent_ng_container_1_div_39_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 20);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](1, CollectionToolOverviewComponent_ng_container_1_div_39_div_1_Template, 2, 1, "div", 21);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r5 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx_r5.proficiencies);
} }
const _c0 = function () { return ["lessons"]; };
function CollectionToolOverviewComponent_ng_container_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "h1", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](3, "div", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](4, "it4all-breadcrumbs", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](5, "div", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](6, "div", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](7, "div", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](8, "header", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](9, "p", 11);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](10, "Sammlungen");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](11, "div", 12);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](12);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](13, "footer", 13);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](14, "a", 14);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](15, "Zu den Sammlungen");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](16, "div", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](17, "div", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](18, "header", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](19, "p", 11);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](20, "Alle Aufgaben");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](21, "div", 12);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](22);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](23, "footer", 13);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](24, "a", 15);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](25, "Zur \u00DCbersicht");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](26, "div", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](27, "div", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](28, "header", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](29, "p", 16);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](30, "Lektionen");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](31, "div", 12);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](32);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](33, "footer", 13);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](34, "a", 17);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](35, "Zu den Lektionen");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](36, "br");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](37, "h2", 18);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](38, "Meine Fertigkeiten");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](39, CollectionToolOverviewComponent_ng_container_1_div_39_Template, 2, 1, "div", 19);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    const _r3 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵreference"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"]("Tool ", ctx_r0.tool.name, "");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("parts", ctx_r0.breadCrumbs);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](8);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate2"](" ", ctx_r0.tool.collectionCount, " Sammlungen mit ", ctx_r0.tool.exerciseCount, " Aufgaben ");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](10);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"]("", ctx_r0.tool.exerciseCount, " Aufgaben");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](10);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"]("", ctx_r0.tool.lessonCount, " Lektionen");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("routerLink", _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵpureFunction0"](9, _c0));
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx_r0.proficiencies.length > 0)("ngIfElse", _r3);
} }
function CollectionToolOverviewComponent_ng_template_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 24);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, "Lade Daten...");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} }
function CollectionToolOverviewComponent_ng_template_4_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 24);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, "Sie haben bisher noch keine F\u00E4higkeiten erworben.");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} }
class CollectionToolOverviewComponent {
    constructor(route, collectionToolOverviewGQL) {
        this.route = route;
        this.collectionToolOverviewGQL = collectionToolOverviewGQL;
    }
    ngOnInit() {
        this.sub = this.route.paramMap.subscribe((paramMap) => {
            const toolId = paramMap.get('toolId');
            this.collectionToolOverviewGQL
                .watch({ toolId })
                .valueChanges
                .subscribe(({ data }) => this.collectionToolOverviewQuery = data);
        });
    }
    get proficiencies() {
        return this.collectionToolOverviewQuery.me.tool.proficiencies;
    }
    ngOnDestroy() {
        this.sub.unsubscribe();
    }
    get tool() {
        return this.collectionToolOverviewQuery.me.tool;
    }
    get breadCrumbs() {
        return [
            { routerLinkPart: '/', title: 'Tools' },
            { routerLinkPart: `tools/${this.tool.id}`, title: this.tool.name }
        ];
    }
}
CollectionToolOverviewComponent.ɵfac = function CollectionToolOverviewComponent_Factory(t) { return new (t || CollectionToolOverviewComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_angular_router__WEBPACK_IMPORTED_MODULE_1__["ActivatedRoute"]), _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_services_apollo_services__WEBPACK_IMPORTED_MODULE_2__["CollectionToolOverviewGQL"])); };
CollectionToolOverviewComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: CollectionToolOverviewComponent, selectors: [["ng-component"]], decls: 6, vars: 2, consts: [[1, "container"], [4, "ngIf", "ngIfElse"], ["loadingDataBlock", ""], ["noProficienciesYetBlock", ""], [1, "title", "is-3", "has-text-centered"], [1, "mb-3"], [3, "parts"], [1, "columns"], [1, "column"], [1, "card"], [1, "card-header"], [1, "card-header-title"], [1, "card-content"], [1, "card-footer"], ["routerLink", "./collections", 1, "card-footer-item"], ["routerLink", "allExercises", 1, "card-footer-item"], [1, "card-header-title", "is-centered"], [1, "card-footer-item", 3, "routerLink"], [1, "subtitle", "is-3", "has-text-centered"], ["class", "columns is-multiline", 4, "ngIf", "ngIfElse"], [1, "columns", "is-multiline"], ["class", "column is-one-quarter-desktop", 4, "ngFor", "ngForOf"], [1, "column", "is-one-quarter-desktop"], [3, "proficiency"], [1, "notification", "is-primary", "has-text-centered"]], template: function CollectionToolOverviewComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](1, CollectionToolOverviewComponent_ng_container_1_Template, 40, 10, "ng-container", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](2, CollectionToolOverviewComponent_ng_template_2_Template, 2, 0, "ng-template", null, 2, _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplateRefExtractor"]);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](4, CollectionToolOverviewComponent_ng_template_4_Template, 2, 0, "ng-template", null, 3, _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplateRefExtractor"]);
    } if (rf & 2) {
        const _r1 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵreference"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx.collectionToolOverviewQuery)("ngIfElse", _r1);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_3__["NgIf"], _shared_breadcrumbs_breadcrumbs_component__WEBPACK_IMPORTED_MODULE_4__["BreadcrumbsComponent"], _angular_router__WEBPACK_IMPORTED_MODULE_1__["RouterLinkWithHref"], _angular_common__WEBPACK_IMPORTED_MODULE_3__["NgForOf"], _components_proficiency_card_proficiency_card_component__WEBPACK_IMPORTED_MODULE_5__["ProficiencyCardComponent"]], encapsulation: 2 });


/***/ }),

/***/ "dT2p":
/*!*****************************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/lessons/lessons-for-tool-overview/lessons-for-tool-overview.component.ts ***!
  \*****************************************************************************************************************/
/*! exports provided: LessonsForToolOverviewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LessonsForToolOverviewComponent", function() { return LessonsForToolOverviewComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/common */ "ofXK");




const _c0 = function (a0) { return [a0]; };
function LessonsForToolOverviewComponent_ng_container_1_div_3_div_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "div", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](2, "header", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](3, "p", 11);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](5, "div", 12);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](6);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](7, "div", 13);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](8, "a", 14);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](9, "Zur Lektion");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const lesson_r7 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate2"]("", lesson_r7.lessonId, ". ", lesson_r7.title, "");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](lesson_r7.description);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("routerLink", _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵpureFunction1"](4, _c0, lesson_r7.lessonId));
} }
function LessonsForToolOverviewComponent_ng_container_1_div_3_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](1, LessonsForToolOverviewComponent_ng_container_1_div_3_div_1_Template, 10, 6, "div", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r5 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx_r5.lessons);
} }
function LessonsForToolOverviewComponent_ng_container_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "h1", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](3, LessonsForToolOverviewComponent_ng_container_1_div_3_Template, 2, 1, "div", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    const _r3 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵreference"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"]("Lektionen f\u00FCr ", ctx_r0.lessonsForToolQuery.me.tool.name, "");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx_r0.lessons.length > 0)("ngIfElse", _r3);
} }
function LessonsForToolOverviewComponent_ng_template_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 15);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, "Lade Lektionen...");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} }
function LessonsForToolOverviewComponent_ng_template_4_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 16);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, "Es konnten keine Lektionen gefunden werden!");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} }
class LessonsForToolOverviewComponent {
    constructor(route, lessonsForToolGQL) {
        this.route = route;
        this.lessonsForToolGQL = lessonsForToolGQL;
    }
    ngOnInit() {
        this.sub = this.route.paramMap.subscribe((paramMap) => {
            const toolId = paramMap.get('toolId');
            this.lessonsForToolGQL
                .watch({ toolId })
                .valueChanges
                .subscribe(({ data }) => this.lessonsForToolQuery = data);
        });
    }
    get lessons() {
        return this.lessonsForToolQuery ? this.lessonsForToolQuery.me.tool.lessons : [];
    }
    ngOnDestroy() {
        this.sub.unsubscribe();
    }
}
LessonsForToolOverviewComponent.ɵfac = function LessonsForToolOverviewComponent_Factory(t) { return new (t || LessonsForToolOverviewComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_angular_router__WEBPACK_IMPORTED_MODULE_1__["ActivatedRoute"]), _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_services_apollo_services__WEBPACK_IMPORTED_MODULE_2__["LessonsForToolGQL"])); };
LessonsForToolOverviewComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: LessonsForToolOverviewComponent, selectors: [["ng-component"]], decls: 6, vars: 2, consts: [[1, "container"], [4, "ngIf", "ngIfElse"], ["loadingLessonsBlock", ""], ["noLessonsFoundBlock", ""], [1, "title", "is-3", "has-text-centered"], ["class", "columns is-multiline", 4, "ngIf", "ngIfElse"], [1, "columns", "is-multiline"], ["class", "column is-half-desktop", 4, "ngFor", "ngForOf"], [1, "column", "is-half-desktop"], [1, "card"], [1, "card-header"], [1, "card-header-title"], [1, "card-content"], [1, "card-footer"], [1, "card-footer-item", 3, "routerLink"], [1, "notification", "is-primary", "has-text-centered"], [1, "notification", "is-danger", "has-text-centered"]], template: function LessonsForToolOverviewComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](1, LessonsForToolOverviewComponent_ng_container_1_Template, 4, 3, "ng-container", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](2, LessonsForToolOverviewComponent_ng_template_2_Template, 2, 0, "ng-template", null, 2, _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplateRefExtractor"]);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](4, LessonsForToolOverviewComponent_ng_template_4_Template, 2, 0, "ng-template", null, 3, _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplateRefExtractor"]);
    } if (rf & 2) {
        const _r1 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵreference"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx.lessonsForToolQuery)("ngIfElse", _r1);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_3__["NgIf"], _angular_common__WEBPACK_IMPORTED_MODULE_3__["NgForOf"], _angular_router__WEBPACK_IMPORTED_MODULE_1__["RouterLinkWithHref"]], encapsulation: 2 });


/***/ }),

/***/ "eNrX":
/*!**************************************************************************************!*\
  !*** ./src/app/tools/random-tools/nary/nary-conversion/nary-conversion.component.ts ***!
  \**************************************************************************************/
/*! exports provided: NaryConversionComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "NaryConversionComponent", function() { return NaryConversionComponent; });
/* harmony import */ var _nary__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../nary */ "mjeW");
/* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../../../helpers */ "Afm0");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/forms */ "3Pt+");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _components_nary_number_read_only_input_nary_number_read_only_input_component__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../_components/nary-number-read-only-input/nary-number-read-only-input.component */ "Ox94");
/* harmony import */ var _components_random_solve_buttons_random_solve_buttons_component__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../../_components/random-solve-buttons/random-solve-buttons.component */ "5Adw");







function NaryConversionComponent_option_21_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "option", 23);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ns_r4 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngValue", ns_r4);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtextInterpolate2"]("", ns_r4.radix, " - ", ns_r4.name, "");
} }
function NaryConversionComponent_option_29_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "option", 23);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ns_r5 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngValue", ns_r5);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtextInterpolate2"]("", ns_r5.radix, " - ", ns_r5.name, "");
} }
function NaryConversionComponent_div_45_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 24);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1, " \u2718 Die L\u00F6sung ist nicht korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} }
function NaryConversionComponent_div_46_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 25);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1, " \u2714 Die L\u00F6sung ist korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} }
const _c0 = function (a0, a1) { return { "is-success": a0, "is-danger": a1 }; };
class NaryConversionComponent extends _nary__WEBPACK_IMPORTED_MODULE_0__["NaryComponentBase"] {
    constructor() {
        super();
        // noinspection JSMismatchedCollectionQueryUpdate
        this.numberingSystems = _nary__WEBPACK_IMPORTED_MODULE_0__["NUMBERING_SYSTEMS"];
        this.startSystem = _nary__WEBPACK_IMPORTED_MODULE_0__["BINARY_SYSTEM"];
        this.targetSystem = _nary__WEBPACK_IMPORTED_MODULE_0__["HEXADECIMAL_SYSTEM"];
        this.toConvertInput = {
            decimalNumber: 0,
            numberingSystem: this.startSystem,
            fieldId: 'toConvert',
            labelContent: 'Startzahl:',
            maxValueForDigits: this.max
        };
        this.checked = false;
        this.correct = false;
    }
    ngOnInit() {
        this.update();
    }
    update() {
        this.toConvertInput.decimalNumber = Object(_helpers__WEBPACK_IMPORTED_MODULE_1__["randomInt"])(1, this.max);
        this.toConvertInput.numberingSystem = this.startSystem;
        this.toConvertInput.maxValueForDigits = this.max;
        this.solutionString = '';
        this.checked = false;
        this.correct = false;
    }
    checkSolution() {
        this.checked = true;
        const processedSolutionString = this.solutionString.replace(/\s+/g, '');
        const solution = parseInt(processedSolutionString, this.targetSystem.radix);
        this.correct = solution === this.toConvertInput.decimalNumber;
    }
    handleKeyboardEvent(event) {
        if (event.key === 'Enter') {
            if (this.correct) {
                this.update();
            }
            else {
                this.checkSolution();
            }
        }
    }
}
NaryConversionComponent.ɵfac = function NaryConversionComponent_Factory(t) { return new (t || NaryConversionComponent)(); };
NaryConversionComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdefineComponent"]({ type: NaryConversionComponent, selectors: [["it4all-nary-conversion"]], hostBindings: function NaryConversionComponent_HostBindings(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("keypress", function NaryConversionComponent_keypress_HostBindingHandler($event) { return ctx.handleKeyboardEvent($event); }, false, _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵresolveDocument"]);
    } }, features: [_angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵInheritDefinitionFeature"]], decls: 49, vars: 16, consts: [[1, "columns"], [1, "column", "is-half-desktop", "is-offset-one-quarter-desktop"], ["for", "max", 1, "label", "has-text-centered"], [1, "field", "has-addons"], [1, "control"], [1, "button", 3, "disabled", "click"], [1, "control", "is-expanded"], ["type", "number", "id", "max", 1, "input", "has-text-centered", 3, "value"], [1, "column", "is-half-desktop"], [1, "field"], ["for", "startSystem", 1, "label", "has-text-centered"], [1, "select", "is-fullwidth"], ["id", "startSystem", 3, "ngModel", "ngModelChange", "change"], [3, "ngValue", 4, "ngFor", "ngForOf"], ["for", "targetSystem", 1, "label", "has-text-centered"], ["id", "targetSystem", 3, "ngModel", "ngModelChange", "change"], [3, "naryNumberInput"], [1, "button", "is-static"], ["for", "solution"], ["type", "text", "id", "solution", "placeholder", "L\u00F6sung", "autofocus", "", 1, "input", "has-text-right", 3, "ngModel", "ngClass", "ngModelChange"], ["class", "notification has-text-centered is-danger", 4, "ngIf"], ["class", "notification has-text-centered is-success", 4, "ngIf"], [3, "correctEmitter", "nextEmitter"], [3, "ngValue"], [1, "notification", "has-text-centered", "is-danger"], [1, "notification", "has-text-centered", "is-success"]], template: function NaryConversionComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](1, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](2, "label", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](3, "Maximalwert");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](4, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](5, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](6, "button", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("click", function NaryConversionComponent_Template_button_click_6_listener() { return ctx.doubleMax(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](7, "* 2");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](8, "div", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](9, "input", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](10, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](11, "button", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("click", function NaryConversionComponent_Template_button_click_11_listener() { return ctx.halveMax(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](12, "/ 2");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](13, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](14, "div", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](15, "div", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](16, "label", 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](17, "Startsystem");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](18, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](19, "div", 11);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](20, "select", 12);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("ngModelChange", function NaryConversionComponent_Template_select_ngModelChange_20_listener($event) { return ctx.startSystem = $event; })("change", function NaryConversionComponent_Template_select_change_20_listener() { return ctx.update(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](21, NaryConversionComponent_option_21_Template, 2, 3, "option", 13);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](22, "div", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](23, "div", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](24, "label", 14);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](25, "Zielsystem");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](26, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](27, "div", 11);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](28, "select", 15);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("ngModelChange", function NaryConversionComponent_Template_select_ngModelChange_28_listener($event) { return ctx.targetSystem = $event; })("change", function NaryConversionComponent_Template_select_change_28_listener() { return ctx.update(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](29, NaryConversionComponent_option_29_Template, 2, 3, "option", 13);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](30, "hr");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](31, "it4all-nary-number-read-only-input", 16);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](32, "hr");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](33, "div", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](34, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](35, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](36, "div", 17);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](37, "label", 18);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](38, "L\u00F6sung:");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](39, "div", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](40, "input", 19);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("ngModelChange", function NaryConversionComponent_Template_input_ngModelChange_40_listener($event) { return ctx.solutionString = $event; });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](41, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](42, "div", 17);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](43, "sub");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](44);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](45, NaryConversionComponent_div_45_Template, 2, 0, "div", 20);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](46, NaryConversionComponent_div_46_Template, 2, 0, "div", 21);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](47, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](48, "it4all-random-solve-buttons", 22);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("correctEmitter", function NaryConversionComponent_Template_it4all_random_solve_buttons_correctEmitter_48_listener() { return ctx.checkSolution(); })("nextEmitter", function NaryConversionComponent_Template_it4all_random_solve_buttons_nextEmitter_48_listener() { return ctx.update(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](6);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("disabled", ctx.max === ctx.maximalMax);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵpropertyInterpolate"]("value", ctx.max);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("disabled", ctx.max === ctx.minimalMax);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](9);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngModel", ctx.startSystem);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngForOf", ctx.numberingSystems);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](7);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngModel", ctx.targetSystem);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngForOf", ctx.numberingSystems);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("naryNumberInput", ctx.toConvertInput);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](9);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngModel", ctx.solutionString)("ngClass", _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵpureFunction2"](13, _c0, ctx.checked && ctx.correct, ctx.checked && !ctx.correct));
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtextInterpolate"](ctx.targetSystem.radix);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx.checked && !ctx.correct);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx.checked && ctx.correct);
    } }, directives: [_angular_forms__WEBPACK_IMPORTED_MODULE_3__["SelectControlValueAccessor"], _angular_forms__WEBPACK_IMPORTED_MODULE_3__["NgControlStatus"], _angular_forms__WEBPACK_IMPORTED_MODULE_3__["NgModel"], _angular_common__WEBPACK_IMPORTED_MODULE_4__["NgForOf"], _components_nary_number_read_only_input_nary_number_read_only_input_component__WEBPACK_IMPORTED_MODULE_5__["NaryNumberReadOnlyInputComponent"], _angular_forms__WEBPACK_IMPORTED_MODULE_3__["DefaultValueAccessor"], _angular_common__WEBPACK_IMPORTED_MODULE_4__["NgClass"], _angular_common__WEBPACK_IMPORTED_MODULE_4__["NgIf"], _components_random_solve_buttons_random_solve_buttons_component__WEBPACK_IMPORTED_MODULE_6__["RandomSolveButtonsComponent"], _angular_forms__WEBPACK_IMPORTED_MODULE_3__["NgSelectOption"], _angular_forms__WEBPACK_IMPORTED_MODULE_3__["ɵangular_packages_forms_forms_z"]], encapsulation: 2 });


/***/ }),

/***/ "ef8k":
/*!*****************************************************************************!*\
  !*** ./src/app/shared/points-notification/points-notification.component.ts ***!
  \*****************************************************************************/
/*! exports provided: PointsNotificationComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "PointsNotificationComponent", function() { return PointsNotificationComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");

class PointsNotificationComponent {
    constructor() {
        this.textColor = 'has-text-danger';
        this.backgroundColor = 'is-danger';
    }
    ngOnInit() {
        this.updatePercentage();
    }
    ngOnChanges(changes) {
        this.updatePercentage();
    }
    updatePercentage() {
        if (isNaN(this.points) || isNaN(this.maxPoints)) {
            this.percentage = 0;
        }
        else {
            this.percentage = this.points / this.maxPoints * 100;
            if (this.percentage >= 90) {
                this.textColor = 'has-text-dark-success';
            }
            else if (this.percentage >= 75) {
                this.textColor = 'has-text-warning';
            }
            else {
                this.textColor = 'has-text-danger';
            }
        }
    }
}
PointsNotificationComponent.ɵfac = function PointsNotificationComponent_Factory(t) { return new (t || PointsNotificationComponent)(); };
PointsNotificationComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: PointsNotificationComponent, selectors: [["it4all-points-notification"]], inputs: { points: "points", maxPoints: "maxPoints" }, features: [_angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵNgOnChangesFeature"]], decls: 8, vars: 7, consts: function () { let i18n_0; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_4543501436409009599$$SRC_APP_SHARED_POINTS_NOTIFICATION_POINTS_NOTIFICATION_COMPONENT_TS_1 = goog.getMsg("Sie haben {$interpolation} von maximal {$interpolation_1} Punkten erreicht", { "interpolation": "\uFFFD0\uFFFD", "interpolation_1": "\uFFFD1\uFFFD" });
        i18n_0 = MSG_EXTERNAL_4543501436409009599$$SRC_APP_SHARED_POINTS_NOTIFICATION_POINTS_NOTIFICATION_COMPONENT_TS_1;
    }
    else {
        i18n_0 = "You have reached " + "\uFFFD0\uFFFD" + " of " + "\uFFFD1\uFFFD" + " points";
    } return [[1, "has-text-centered"], i18n_0, ["max", "100", 1, "progress", 3, "value"]]; }, template: function PointsNotificationComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "p", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](2, "span");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵi18n"](3, 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](4, ". ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](5, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](6, "progress", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](7);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵclassMapInterpolate1"]("notification is-light-grey ", ctx.textColor, "");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵi18nExp"](ctx.points)(ctx.maxPoints);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵi18nApply"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵpropertyInterpolate"]("value", ctx.percentage);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"]("", ctx.percentage, "%");
    } }, encapsulation: 2 });


/***/ }),

/***/ "fxtn":
/*!**************************************!*\
  !*** ./src/app/lti/lti.component.ts ***!
  \**************************************/
/*! exports provided: LtiComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LtiComponent", function() { return LtiComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _services_authentication_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../_services/authentication.service */ "pW6c");



class LtiComponent {
    // TODO: eventually render template?
    constructor(route, router, authenticationService) {
        this.route = route;
        this.router = router;
        this.authenticationService = authenticationService;
        const uuid = this.route.snapshot.paramMap.get('uuid');
        this.authenticationService.claimJsonWebToken(uuid)
            .subscribe(() => this.router.navigate(['/']));
    }
}
LtiComponent.ɵfac = function LtiComponent_Factory(t) { return new (t || LtiComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_angular_router__WEBPACK_IMPORTED_MODULE_1__["ActivatedRoute"]), _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_angular_router__WEBPACK_IMPORTED_MODULE_1__["Router"]), _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_services_authentication_service__WEBPACK_IMPORTED_MODULE_2__["AuthenticationService"])); };
LtiComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: LtiComponent, selectors: [["ng-component"]], decls: 0, vars: 0, template: function LtiComponent_Template(rf, ctx) { }, encapsulation: 2 });


/***/ }),

/***/ "ghNm":
/*!***********************************************************************************!*\
  !*** ./src/app/tools/collection-tools/uml/uml-exercise/uml-exercise.component.ts ***!
  \***********************************************************************************/
/*! exports provided: UmlExerciseComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlExerciseComponent", function() { return UmlExerciseComponent; });
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _uml_class_selection_uml_class_selection_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../uml-class-selection/uml-class-selection.component */ "xvqd");
/* harmony import */ var _uml_diagram_drawing_uml_diagram_drawing_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../uml-diagram-drawing/uml-diagram-drawing.component */ "sArB");
/* harmony import */ var _uml_member_allocation_uml_member_allocation_component__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../uml-member-allocation/uml-member-allocation.component */ "Qt55");






function UmlExerciseComponent_it4all_uml_class_selection_0_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelement"](0, "it4all-uml-class-selection", 2);
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("exerciseFragment", ctx_r0.exerciseFragment)("exerciseContent", ctx_r0.contentFragment);
} }
function UmlExerciseComponent_it4all_uml_diagram_drawing_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelement"](0, "it4all-uml-diagram-drawing", 3);
} if (rf & 2) {
    const ctx_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("exerciseFragment", ctx_r1.exerciseFragment)("contentFragment", ctx_r1.contentFragment);
} }
function UmlExerciseComponent_it4all_uml_member_allocation_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelement"](0, "it4all-uml-member-allocation", 3);
} if (rf & 2) {
    const ctx_r2 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("exerciseFragment", ctx_r2.exerciseFragment)("contentFragment", ctx_r2.contentFragment);
} }
class UmlExerciseComponent {
    isClassSelection() {
        return this.contentFragment.umlPart === _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["UmlExPart"].ClassSelection;
    }
    isDiagramDrawing() {
        return [_services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["UmlExPart"].DiagramDrawingHelp, _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["UmlExPart"].DiagramDrawing].includes(this.contentFragment.umlPart);
    }
    isMemberAllocation() {
        return this.contentFragment.umlPart === _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["UmlExPart"].MemberAllocation;
    }
}
UmlExerciseComponent.ɵfac = function UmlExerciseComponent_Factory(t) { return new (t || UmlExerciseComponent)(); };
UmlExerciseComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineComponent"]({ type: UmlExerciseComponent, selectors: [["it4all-uml-exercise"]], inputs: { exerciseFragment: "exerciseFragment", contentFragment: "contentFragment" }, decls: 3, vars: 3, consts: [[3, "exerciseFragment", "exerciseContent", 4, "ngIf"], [3, "exerciseFragment", "contentFragment", 4, "ngIf"], [3, "exerciseFragment", "exerciseContent"], [3, "exerciseFragment", "contentFragment"]], template: function UmlExerciseComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](0, UmlExerciseComponent_it4all_uml_class_selection_0_Template, 1, 2, "it4all-uml-class-selection", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](1, UmlExerciseComponent_it4all_uml_diagram_drawing_1_Template, 1, 2, "it4all-uml-diagram-drawing", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](2, UmlExerciseComponent_it4all_uml_member_allocation_2_Template, 1, 2, "it4all-uml-member-allocation", 1);
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", ctx.isClassSelection());
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", ctx.isDiagramDrawing());
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", ctx.isMemberAllocation());
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_2__["NgIf"], _uml_class_selection_uml_class_selection_component__WEBPACK_IMPORTED_MODULE_3__["UmlClassSelectionComponent"], _uml_diagram_drawing_uml_diagram_drawing_component__WEBPACK_IMPORTED_MODULE_4__["UmlDiagramDrawingComponent"], _uml_member_allocation_uml_member_allocation_component__WEBPACK_IMPORTED_MODULE_5__["UmlMemberAllocationComponent"]], encapsulation: 2 });


/***/ }),

/***/ "iKdM":
/*!***********************************************************************************!*\
  !*** ./src/app/tools/collection-tools/web/web-exercise/web-exercise.component.ts ***!
  \***********************************************************************************/
/*! exports provided: getIdForWebExPart, WebExerciseComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "getIdForWebExPart", function() { return getIdForWebExPart; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "WebExerciseComponent", function() { return WebExerciseComponent; });
/* harmony import */ var _helpers_component_with_exercise_directive__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../_helpers/component-with-exercise.directive */ "TRIe");
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../../../_services/apollo_services */ "o20/");
/* harmony import */ var _components_files_exercise_files_exercise_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../_components/files-exercise/files-exercise.component */ "Emuw");
/* harmony import */ var codemirror_mode_htmlmixed_htmlmixed__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! codemirror/mode/htmlmixed/htmlmixed */ "1p+/");
/* harmony import */ var codemirror_mode_htmlmixed_htmlmixed__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(codemirror_mode_htmlmixed_htmlmixed__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _services_dexie_service__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../../../_services/dexie.service */ "4di/");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _shared_solution_saved_solution_saved_component__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../../../shared/solution-saved/solution-saved.component */ "rqf4");
/* harmony import */ var _shared_points_notification_points_notification_component__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../../../../shared/points-notification/points-notification.component */ "ef8k");
/* harmony import */ var _components_html_task_result_html_task_result_component__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ../_components/html-task-result/html-task-result.component */ "qv6m");












function WebExerciseComponent_it4all_files_exercise_0_ol_4_li_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "li");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const task_r6 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate1"](" ", task_r6.text, " ");
} }
function WebExerciseComponent_it4all_files_exercise_0_ol_4_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "ol");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](1, WebExerciseComponent_it4all_files_exercise_0_ol_4_li_1_Template, 2, 1, "li", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngForOf", ctx_r1.contentFragment.siteSpec.htmlTasks);
} }
function WebExerciseComponent_it4all_files_exercise_0_p_5_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "p");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r2 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate1"](" Es gibt insgesamt ", ctx_r2.contentFragment.siteSpec.jsTaskCount, " Testf\u00E4lle. ");
} }
function WebExerciseComponent_it4all_files_exercise_0_div_7_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "div", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](1, "div", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](2, "Es gab einen Fehler bei der Korrektur:");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](3, "div", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](4, "pre");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate"](ctx_r3.queryError.message);
} }
function WebExerciseComponent_it4all_files_exercise_0_ng_container_8_ul_6_li_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "li");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](1, "it4all-html-task-result", 14);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const htmlResult_r10 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("htmlResult", htmlResult_r10);
} }
function WebExerciseComponent_it4all_files_exercise_0_ng_container_8_ul_6_li_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "li");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](1, "span", 15);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵpipe"](4, "json");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const jsResult_r11 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngClass", jsResult_r11.success === "COMPLETE" ? "has-text-success" : "has-text-danger");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate4"](" (", jsResult_r11.points, " / ", jsResult_r11.maxPoints, ") Test ", jsResult_r11.id, " ist ", jsResult_r11.success === "COMPLETE" ? "" : "nicht", " korrekt: ");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate1"](" ", _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵpipeBind1"](4, 6, jsResult_r11), " ");
} }
function WebExerciseComponent_it4all_files_exercise_0_ng_container_8_ul_6_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "ul");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](1, WebExerciseComponent_it4all_files_exercise_0_ng_container_8_ul_6_li_1_Template, 2, 1, "li", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](2, WebExerciseComponent_it4all_files_exercise_0_ng_container_8_ul_6_li_2_Template, 5, 8, "li", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r7 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngForOf", ctx_r7.result.gradedHtmlTaskResults);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngForOf", ctx_r7.result.gradedJsTaskResults);
} }
function WebExerciseComponent_it4all_files_exercise_0_ng_container_8_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](1, "div", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](2, "it4all-solution-saved", 11);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](3, "div", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelement"](4, "it4all-points-notification", 12);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](5, "div", 13);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](6, WebExerciseComponent_it4all_files_exercise_0_ng_container_8_ul_6_Template, 3, 2, "ul", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r4 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("solutionSaved", ctx_r4.correctionResult.solutionSaved);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("points", ctx_r4.result.points)("maxPoints", ctx_r4.result.maxPoints);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx_r4.result.gradedHtmlTaskResults.length > 0);
} }
function WebExerciseComponent_it4all_files_exercise_0_Template(rf, ctx) { if (rf & 1) {
    const _r13 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵgetCurrentView"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](0, "it4all-files-exercise", 1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵlistener"]("correct", function WebExerciseComponent_it4all_files_exercise_0_Template_it4all_files_exercise_correct_0_listener() { _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵrestoreView"](_r13); const ctx_r12 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"](); return ctx_r12.correct(); });
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](1, "div", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementStart"](2, "p");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](4, WebExerciseComponent_it4all_files_exercise_0_ol_4_Template, 2, 1, "ol", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](5, WebExerciseComponent_it4all_files_exercise_0_p_5_Template, 2, 1, "p", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerStart"](6, 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](7, WebExerciseComponent_it4all_files_exercise_0_div_7_Template, 6, 1, "div", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](8, WebExerciseComponent_it4all_files_exercise_0_ng_container_8_Template, 7, 4, "ng-container", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementContainerEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("exerciseFiles", ctx_r0.exerciseFiles)("isCorrecting", ctx_r0.isCorrecting)("sampleSolutions", ctx_r0.sampleSolutions);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtextInterpolate"](ctx_r0.exerciseFragment.text);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx_r0.partId === "html");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx_r0.partId === "js");
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx_r0.queryError);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx_r0.correctionResult);
} }
function getIdForWebExPart(webExPart) {
    switch (webExPart) {
        case _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["WebExPart"].HtmlPart:
            return 'html';
        case _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["WebExPart"].JsPart:
            return 'js';
        default:
            throw Error('TODO!');
    }
}
class WebExerciseComponent extends _helpers_component_with_exercise_directive__WEBPACK_IMPORTED_MODULE_0__["ComponentWithExerciseDirective"] {
    constructor(webCorrectionGQL, dexieService) {
        super(webCorrectionGQL, dexieService);
        this.exerciseFiles = [];
    }
    ngOnInit() {
        this.partId = getIdForWebExPart(this.contentFragment.webPart);
        this.exerciseFiles = this.contentFragment.files;
        this.loadOldSolutionAbstract(this.exerciseFragment, this.partId, (oldSolution) => this.exerciseFiles = oldSolution.files);
    }
    // Sample solutions
    get sampleSolutions() {
        return this.contentFragment.webSampleSolutions;
    }
    // Correction
    getSolution() {
        return { files: this.exerciseFiles };
    }
    getMutationQueryVariables() {
        return {
            exId: this.exerciseFragment.exerciseId,
            collId: this.exerciseFragment.collectionId,
            solution: this.getSolution(),
            part: this.contentFragment.webPart,
        };
    }
    correct() {
        this.correctAbstract(this.exerciseFragment, this.partId, () => {
            if (this.filesExerciseComponent) {
                this.filesExerciseComponent.toggleCorrectionTab();
            }
        });
    }
    ;
    // Results
    get correctionResult() {
        var _a, _b;
        return (_b = (_a = this.resultQuery) === null || _a === void 0 ? void 0 : _a.me.webExercise) === null || _b === void 0 ? void 0 : _b.correct;
    }
    get result() {
        var _a;
        return (_a = this.correctionResult) === null || _a === void 0 ? void 0 : _a.result;
    }
}
WebExerciseComponent.ɵfac = function WebExerciseComponent_Factory(t) { return new (t || WebExerciseComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵdirectiveInject"](_services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["WebCorrectionGQL"]), _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵdirectiveInject"](_services_dexie_service__WEBPACK_IMPORTED_MODULE_5__["DexieService"])); };
WebExerciseComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵdefineComponent"]({ type: WebExerciseComponent, selectors: [["it4all-web-exercise"]], viewQuery: function WebExerciseComponent_Query(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵviewQuery"](_components_files_exercise_files_exercise_component__WEBPACK_IMPORTED_MODULE_2__["FilesExerciseComponent"], 1);
    } if (rf & 2) {
        let _t;
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵqueryRefresh"](_t = _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵloadQuery"]()) && (ctx.filesExerciseComponent = _t.first);
    } }, inputs: { exerciseFragment: "exerciseFragment", contentFragment: "contentFragment" }, features: [_angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵInheritDefinitionFeature"]], decls: 1, vars: 1, consts: [["defaultMode", "htmlmixed", 3, "exerciseFiles", "isCorrecting", "sampleSolutions", "correct", 4, "ngIf"], ["defaultMode", "htmlmixed", 3, "exerciseFiles", "isCorrecting", "sampleSolutions", "correct"], ["exText", "", 1, "content"], [4, "ngIf"], ["correctionContent", ""], ["class", "message is-danger", 4, "ngIf"], [4, "ngFor", "ngForOf"], [1, "message", "is-danger"], [1, "message-header"], [1, "message-body"], [1, "my-3"], [3, "solutionSaved"], [3, "points", "maxPoints"], [1, "content", "my-3"], [3, "htmlResult"], [3, "ngClass"]], template: function WebExerciseComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵtemplate"](0, WebExerciseComponent_it4all_files_exercise_0_Template, 9, 8, "it4all-files-exercise", 0);
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_4__["ɵɵproperty"]("ngIf", ctx.exerciseFragment && ctx.contentFragment);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_6__["NgIf"], _components_files_exercise_files_exercise_component__WEBPACK_IMPORTED_MODULE_2__["FilesExerciseComponent"], _angular_common__WEBPACK_IMPORTED_MODULE_6__["NgForOf"], _shared_solution_saved_solution_saved_component__WEBPACK_IMPORTED_MODULE_7__["SolutionSavedComponent"], _shared_points_notification_points_notification_component__WEBPACK_IMPORTED_MODULE_8__["PointsNotificationComponent"], _components_html_task_result_html_task_result_component__WEBPACK_IMPORTED_MODULE_9__["HtmlTaskResultComponent"], _angular_common__WEBPACK_IMPORTED_MODULE_6__["NgClass"]], pipes: [_angular_common__WEBPACK_IMPORTED_MODULE_6__["JsonPipe"]], encapsulation: 2 });


/***/ }),

/***/ "jonz":
/*!*****************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/_components/string-sample-sol/string-sample-sol.component.ts ***!
  \*****************************************************************************************************/
/*! exports provided: StringSampleSolComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "StringSampleSolComponent", function() { return StringSampleSolComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");

class StringSampleSolComponent {
}
StringSampleSolComponent.ɵfac = function StringSampleSolComponent_Factory(t) { return new (t || StringSampleSolComponent)(); };
StringSampleSolComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: StringSampleSolComponent, selectors: [["it4all-string-sample-sol"]], inputs: { sample: "sample" }, decls: 3, vars: 1, consts: [[1, "notification", "is-light-grey"]], template: function StringSampleSolComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "pre");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx.sample);
    } }, encapsulation: 2 });


/***/ }),

/***/ "k/IA":
/*!***************************************************************************!*\
  !*** ./src/app/tools/collection-tools/lessons/solvable-lesson-content.ts ***!
  \***************************************************************************/
/*! exports provided: isSolvableLessonTextContentFragment, isSolvableLessonMultipleChoiceQuestionContentFragment */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "isSolvableLessonTextContentFragment", function() { return isSolvableLessonTextContentFragment; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "isSolvableLessonMultipleChoiceQuestionContentFragment", function() { return isSolvableLessonMultipleChoiceQuestionContentFragment; });
function isSolvableLessonTextContentFragment(lessonContentFragment) {
    return lessonContentFragment.__typename === 'LessonTextContent';
}
function isSolvableLessonMultipleChoiceQuestionContentFragment(lessonContentFragment) {
    return lessonContentFragment.__typename === 'LessonMultipleChoiceQuestionsContent';
}


/***/ }),

/***/ "kLWW":
/*!***********************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/regex/regex-extraction-result/regex-extraction-result.component.ts ***!
  \***********************************************************************************************************/
/*! exports provided: RegexExtractionResultComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegexExtractionResultComponent", function() { return RegexExtractionResultComponent; });
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common */ "ofXK");



function RegexExtractionResultComponent_div_6_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "div", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](3, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](5, ", bekommen: ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](6, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](7);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const m_r3 = ctx.$implicit;
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngClass", ctx_r0.isCorrect(m_r3) ? "is-success" : "is-danger");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" ", ctx_r0.isCorrect(m_r3) ? "\u2714" : "\u2718", " Erwartet: ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](m_r3.sampleArg);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](m_r3.userArg);
} }
function RegexExtractionResultComponent_div_7_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "div", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](4, " sollte nicht gefunden werden! ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const mu_r4 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](mu_r4);
} }
function RegexExtractionResultComponent_div_8_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "div", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](4, " sollte gefunden werden! ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ms_r5 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](ms_r5);
} }
class RegexExtractionResultComponent {
    isCorrect(m) {
        return m.matchType === _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["MatchType"].SuccessfulMatch;
    }
    ngOnInit() {
        console.info(JSON.stringify(this.extractionResult));
    }
}
RegexExtractionResultComponent.ɵfac = function RegexExtractionResultComponent_Factory(t) { return new (t || RegexExtractionResultComponent)(); };
RegexExtractionResultComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineComponent"]({ type: RegexExtractionResultComponent, selectors: [["it4all-regex-extraction-result"]], inputs: { extractionResult: "extractionResult" }, decls: 9, vars: 4, consts: [[1, "notification"], [1, "has-text-centered"], [1, "columns", "is-multiline", "my-3"], ["class", "column is-half-desktop", 4, "ngFor", "ngForOf"], [1, "column", "is-half-desktop"], [1, "notification", 3, "ngClass"], [1, "notification", "is-danger"]], template: function RegexExtractionResultComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "p", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](2, "Suche in: ");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](3, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](4);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](5, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](6, RegexExtractionResultComponent_div_6_Template, 8, 4, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](7, RegexExtractionResultComponent_div_7_Template, 5, 1, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](8, RegexExtractionResultComponent_div_8_Template, 5, 1, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](4);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](ctx.extractionResult.base);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx.extractionResult.extractionMatchingResult.allMatches);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx.extractionResult.extractionMatchingResult.notMatchedForUser);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx.extractionResult.extractionMatchingResult.notMatchedForSample);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_2__["NgForOf"], _angular_common__WEBPACK_IMPORTED_MODULE_2__["NgClass"]], encapsulation: 2 });


/***/ }),

/***/ "kRoC":
/*!*************************************************************************!*\
  !*** ./src/app/tools/collection-tools/uml/_model/class-diag-helpers.ts ***!
  \*************************************************************************/
/*! exports provided: addClassToGraph, addImplementationToGraph, addAssociationToGraph */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "addClassToGraph", function() { return addClassToGraph; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "addImplementationToGraph", function() { return addImplementationToGraph; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "addAssociationToGraph", function() { return addAssociationToGraph; });
/* harmony import */ var jointjs__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! jointjs */ "iuCI");
/* harmony import */ var _uml_consts__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./uml-consts */ "y3Rt");
/* harmony import */ var _joint_class_diag_elements__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./joint-class-diag-elements */ "/fbC");



function findFreePositionForNextClass(paper) {
    const maxRows = Math.floor((paper.getArea().height - _uml_consts__WEBPACK_IMPORTED_MODULE_1__["GRID_SIZE"]) / (_joint_class_diag_elements__WEBPACK_IMPORTED_MODULE_2__["STD_CLASS_HEIGHT"] + _uml_consts__WEBPACK_IMPORTED_MODULE_1__["GRID_SIZE"]));
    const maxCols = Math.floor((paper.getArea().width - _uml_consts__WEBPACK_IMPORTED_MODULE_1__["GRID_SIZE"]) / (_joint_class_diag_elements__WEBPACK_IMPORTED_MODULE_2__["STD_CLASS_WIDTH"] + _uml_consts__WEBPACK_IMPORTED_MODULE_1__["GRID_SIZE"]));
    for (let row = 0; row < maxRows; row++) {
        for (let col = 0; col < maxCols; col++) {
            const x = _uml_consts__WEBPACK_IMPORTED_MODULE_1__["GRID_SIZE"] + col * (_joint_class_diag_elements__WEBPACK_IMPORTED_MODULE_2__["STD_CLASS_WIDTH"] + _uml_consts__WEBPACK_IMPORTED_MODULE_1__["GRID_SIZE"]);
            const y = _uml_consts__WEBPACK_IMPORTED_MODULE_1__["GRID_SIZE"] + row * (_joint_class_diag_elements__WEBPACK_IMPORTED_MODULE_2__["STD_CLASS_HEIGHT"] + _uml_consts__WEBPACK_IMPORTED_MODULE_1__["GRID_SIZE"]);
            const viewIsBlocked = paper.findViewsInArea({ x, y, width: _joint_class_diag_elements__WEBPACK_IMPORTED_MODULE_2__["STD_CLASS_WIDTH"], height: _joint_class_diag_elements__WEBPACK_IMPORTED_MODULE_2__["STD_CLASS_HEIGHT"] }).length > 0;
            if (!viewIsBlocked) {
                return { x, y };
            }
        }
    }
    return { x: _uml_consts__WEBPACK_IMPORTED_MODULE_1__["GRID_SIZE"], y: _uml_consts__WEBPACK_IMPORTED_MODULE_1__["GRID_SIZE"] };
}
function addClassToGraph(name, paper, attributes = [], methods = [], maybePosition) {
    if (paper.model.getCells().find((c) => c instanceof _joint_class_diag_elements__WEBPACK_IMPORTED_MODULE_2__["MyJointClass"] && c.getClassName() === name)) {
        // graph already contains class with that name!
        return;
    }
    paper.model.addCell(new _joint_class_diag_elements__WEBPACK_IMPORTED_MODULE_2__["MyJointClass"]({
        className: name,
        size: { width: _joint_class_diag_elements__WEBPACK_IMPORTED_MODULE_2__["STD_CLASS_WIDTH"], height: _joint_class_diag_elements__WEBPACK_IMPORTED_MODULE_2__["STD_CLASS_HEIGHT"] },
        position: maybePosition || findFreePositionForNextClass(paper),
        attributes, methods
    }));
}
function addImplementationToGraph(subClass, superClass, graph) {
    graph.addCell(new jointjs__WEBPACK_IMPORTED_MODULE_0__["shapes"].uml.Implementation({
        source: { id: subClass.id },
        target: { id: superClass.id }
    }));
}
function addAssociationToGraph(firstEnd, firstMult, secondEnd, secondMult, graph) {
    const config = {
        source: { id: firstEnd.id },
        target: { id: secondEnd.id },
        labels: [
            { position: 25, attrs: { text: { text: firstMult } } },
            { position: -25, attrs: { text: { text: secondMult } } }
        ]
    };
    graph.addCell(new jointjs__WEBPACK_IMPORTED_MODULE_0__["shapes"].uml.Association(config));
}


/***/ }),

/***/ "lIHn":
/*!************************************************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/programming/_results/programming-unit-test-result/programming-unit-test-result.component.ts ***!
  \************************************************************************************************************************************/
/*! exports provided: ProgrammingUnitTestResultComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ProgrammingUnitTestResultComponent", function() { return ProgrammingUnitTestResultComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/common */ "ofXK");


function ProgrammingUnitTestResultComponent_ng_container_3_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "p");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](3, "pre");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"]("Beschreibung: ", ctx_r0.unitTestResult.description, "");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx_r0.stderr);
} }
class ProgrammingUnitTestResultComponent {
    get stderr() {
        return this.unitTestResult.stderr.join('\n');
    }
}
ProgrammingUnitTestResultComponent.ɵfac = function ProgrammingUnitTestResultComponent_Factory(t) { return new (t || ProgrammingUnitTestResultComponent)(); };
ProgrammingUnitTestResultComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: ProgrammingUnitTestResultComponent, selectors: [["it4all-programming-unit-test-result"]], inputs: { unitTestResult: "unitTestResult" }, decls: 4, vars: 5, consts: [[3, "ngClass"], [4, "ngIf"]], template: function ProgrammingUnitTestResultComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "li");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "p", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](3, ProgrammingUnitTestResultComponent_ng_container_3_Template, 5, 2, "ng-container", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngClass", ctx.unitTestResult.successful ? "has-text-success" : "has-text-danger");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate3"](" Der ", ctx.unitTestResult.testId, ". Test war ", ctx.unitTestResult.successful ? "" : "nicht", " erfolgreich. Der Test sollte ", ctx.unitTestResult.shouldFail ? "" : "nicht", " fehlschlagen. ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", !ctx.unitTestResult.successful);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_1__["NgClass"], _angular_common__WEBPACK_IMPORTED_MODULE_1__["NgIf"]], encapsulation: 2 });


/***/ }),

/***/ "lmDL":
/*!*************************************************************!*\
  !*** ./src/app/shared/breadcrumbs/breadcrumbs.component.ts ***!
  \*************************************************************/
/*! exports provided: BreadcrumbsComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BreadcrumbsComponent", function() { return BreadcrumbsComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "tyNb");



function BreadcrumbsComponent_li_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "li");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "a", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const part_r1 = ctx.$implicit;
    const last_r2 = ctx.last;
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵclassProp"]("is-active", last_r2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵpropertyInterpolate"]("routerLink", part_r1.routerLink);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](part_r1.title);
} }
class BreadcrumbsComponent {
    get breadCrumbs() {
        let partAggregator = [];
        return this.parts.map(({ routerLinkPart, title }) => {
            partAggregator.push(routerLinkPart);
            return { routerLink: partAggregator.join('/'), title };
        });
    }
}
BreadcrumbsComponent.ɵfac = function BreadcrumbsComponent_Factory(t) { return new (t || BreadcrumbsComponent)(); };
BreadcrumbsComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: BreadcrumbsComponent, selectors: [["it4all-breadcrumbs"]], inputs: { parts: "parts" }, decls: 3, vars: 1, consts: [["aria-label", "breadcrumbs", 1, "breadcrumb"], [3, "is-active", 4, "ngFor", "ngForOf"], [3, "routerLink"]], template: function BreadcrumbsComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "nav", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "ul");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](2, BreadcrumbsComponent_li_2_Template, 3, 4, "li", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx.breadCrumbs);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_1__["NgForOf"], _angular_router__WEBPACK_IMPORTED_MODULE_2__["RouterLinkWithHref"]], encapsulation: 2 });


/***/ }),

/***/ "mjeW":
/*!*************************************************!*\
  !*** ./src/app/tools/random-tools/nary/nary.ts ***!
  \*************************************************/
/*! exports provided: BINARY_SYSTEM, DECIMAL_SYSTEM, HEXADECIMAL_SYSTEM, NUMBERING_SYSTEMS, NaryComponentBase */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BINARY_SYSTEM", function() { return BINARY_SYSTEM; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DECIMAL_SYSTEM", function() { return DECIMAL_SYSTEM; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "HEXADECIMAL_SYSTEM", function() { return HEXADECIMAL_SYSTEM; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "NUMBERING_SYSTEMS", function() { return NUMBERING_SYSTEMS; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "NaryComponentBase", function() { return NaryComponentBase; });
const BINARY_SYSTEM = { radix: 2, name: 'Binärsystem', allowedDigits: '01' };
const DECIMAL_SYSTEM = { radix: 10, name: 'Dezimalsystem', allowedDigits: '0123456789' };
const HEXADECIMAL_SYSTEM = {
    radix: 16,
    name: 'Hexadezimalsystem',
    allowedDigits: '0123456789ABCDEF'
};
const NUMBERING_SYSTEMS = [
    BINARY_SYSTEM,
    { radix: 4, name: 'Quaternärsystem', allowedDigits: '0123' },
    { radix: 8, name: 'Oktalsystem', allowedDigits: '01234567' },
    HEXADECIMAL_SYSTEM
];
class NaryComponentBase {
    constructor(defaultMax = 256) {
        this.minimalMax = 16;
        this.maximalMax = Math.pow(2, 32);
        this.max = defaultMax;
    }
    halveMax() {
        this.max = Math.max(this.minimalMax, this.max / 2);
        this.update();
    }
    doubleMax() {
        this.max = Math.min(this.maximalMax, this.max * 2);
        this.update();
    }
}


/***/ }),

/***/ "nSnL":
/*!***********************************************!*\
  !*** ./src/app/_helpers/error.interceptor.ts ***!
  \***********************************************/
/*! exports provided: ErrorInterceptor */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ErrorInterceptor", function() { return ErrorInterceptor; });
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! rxjs */ "qCKp");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! rxjs/operators */ "kU1M");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _services_authentication_service__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../_services/authentication.service */ "pW6c");




class ErrorInterceptor {
    constructor(authenticationService) {
        this.authenticationService = authenticationService;
    }
    intercept(request, next) {
        return next.handle(request).pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_1__["catchError"])((err) => {
            if (err.status === 401) {
                this.authenticationService.logout();
            }
            const error = err.error.message || err.statusTest;
            return Object(rxjs__WEBPACK_IMPORTED_MODULE_0__["throwError"])(error);
        }));
    }
}
ErrorInterceptor.ɵfac = function ErrorInterceptor_Factory(t) { return new (t || ErrorInterceptor)(_angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵinject"](_services_authentication_service__WEBPACK_IMPORTED_MODULE_3__["AuthenticationService"])); };
ErrorInterceptor.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdefineInjectable"]({ token: ErrorInterceptor, factory: ErrorInterceptor.ɵfac });


/***/ }),

/***/ "o20/":
/*!**********************************************!*\
  !*** ./src/app/_services/apollo_services.ts ***!
  \**********************************************/
/*! exports provided: BinaryClassificationResultType, EbnfExPart, FlaskExercisePart, JsActionType, MatchType, SuccessType, XmlErrorType, ProgExPart, RegexCorrectionType, RegexExPart, SqlExPart, SqlExerciseType, ToolState, UmlAssociationType, UmlClassType, UmlExPart, UmlMultiplicity, UmlVisibility, WebExPart, XmlExPart, FlaskTestResultFragmentDoc, FlaskResultFragmentDoc, FlaskCorrectionResultFragmentDoc, ImplementationCorrectionResultFragmentDoc, UnitTestCorrectionResultFragmentDoc, ProgrammingResultFragmentDoc, ProgrammingCorrectionResultFragmentDoc, RegexAbstractResultFragmentDoc, RegexMatchingSingleResultFragmentDoc, RegexMatchingResultFragmentDoc, RegexExtractionMatchFragmentDoc, ExtractionMatchingResultFragmentDoc, RegexExtractionSingleResultFragmentDoc, RegexExtractionResultFragmentDoc, RegexCorrectionResultFragmentDoc, NewMatchFragmentDoc, SqlMatchingResultFragmentDoc, ColumnComparisonFragmentDoc, StringMatchFragmentDoc, StringMatchingResultFragmentDoc, BinaryExpressionComparisonFragmentDoc, LimitComparisonFragmentDoc, SelectAdditionalComparisonFragmentDoc, SqlCellFragmentDoc, SqlRowFragmentDoc, SqlQueryResultFragmentDoc, SqlExecutionResultFragmentDoc, SqlResultFragmentDoc, SqlCorrectionResultFragmentDoc, UmlSolutionClassFragmentDoc, UmlClassMatchFragmentDoc, UmlClassMatchingResultFragmentDoc, UmlAssociationFragmentDoc, UmlAssociationMatchFragmentDoc, UmlAssociationMatchingResultFragmentDoc, UmlImplementationFragmentDoc, UmlImplementationMatchFragmentDoc, UmlImplementationMatchingResultFragmentDoc, UmlResultFragmentDoc, UmlCorrectionResultFragmentDoc, GradedTextContentResultFragmentDoc, GradedHtmlTaskResultFragmentDoc, GradedJsHtmlElementSpecResultFragmentDoc, GradedJsActionResultFragmentDoc, GradedJsTaskResultFragmentDoc, WebResultFragmentDoc, WebCorrectionResultFragmentDoc, ElementLineFragmentDoc, XmlElementLineAnalysisResultFragmentDoc, XmlElementLineMatchFragmentDoc, XmlElementLineMatchingResultFragmentDoc, XmlGrammarResultFragmentDoc, XmlErrorFragmentDoc, XmlDocumentResultFragmentDoc, XmlResultFragmentDoc, XmlCorrectionResultFragmentDoc, LessonIdentifierFragmentDoc, LessonOverviewFragmentDoc, LessonTextContentFragmentDoc, LessonMultipleChoiceQuestionAnswerFragmentDoc, LessonMultipleChoiceQuestionFragmentDoc, LessonMultipleChoiceQuestionContentFragmentDoc, LessonAsTextFragmentDoc, CollectionToolFragmentDoc, LevelFragmentDoc, TopicFragmentDoc, UserProficiencyFragmentDoc, CollectionValuesFragmentDoc, TopicWithLevelFragmentDoc, FieldsPartFragmentDoc, FieldsForLinkFragmentDoc, CollOverviewToolFragmentDoc, PartFragmentDoc, ExerciseOverviewFragmentDoc, EbnfExerciseContentFragmentDoc, ExerciseFileFragmentDoc, FilesSolutionFragmentDoc, FlaskExerciseContentFragmentDoc, UnitTestPartFragmentDoc, ProgrammingExerciseContentFragmentDoc, RegexExerciseContentFragmentDoc, SqlExerciseContentFragmentDoc, UmlAttributeFragmentDoc, UmlMethodFragmentDoc, UmlClassFragmentDoc, UmlClassDiagramFragmentDoc, UmlExerciseContentFragmentDoc, WebExerciseContentFragmentDoc, XmlSolutionFragmentDoc, XmlExerciseContentFragmentDoc, ExerciseSolveFieldsFragmentDoc, LoggedInUserWithTokenFragmentDoc, FlaskCorrectionDocument, FlaskCorrectionGQL, ProgrammingCorrectionDocument, ProgrammingCorrectionGQL, RegexCorrectionDocument, RegexCorrectionGQL, SqlCorrectionDocument, SqlCorrectionGQL, UmlCorrectionDocument, UmlCorrectionGQL, WebCorrectionDocument, WebCorrectionGQL, XmlCorrectionDocument, XmlCorrectionGQL, LessonsForToolDocument, LessonsForToolGQL, LessonOverviewDocument, LessonOverviewGQL, LessonAsTextDocument, LessonAsTextGQL, LessonAsVideoDocument, LessonAsVideoGQL, ToolOverviewDocument, ToolOverviewGQL, CollectionToolOverviewDocument, CollectionToolOverviewGQL, AllExercisesOverviewDocument, AllExercisesOverviewGQL, CollectionListDocument, CollectionListGQL, CollectionOverviewDocument, CollectionOverviewGQL, ExerciseOverviewDocument, ExerciseOverviewGQL, ExerciseDocument, ExerciseGQL, RegisterDocument, RegisterGQL, LoginDocument, LoginGQL */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BinaryClassificationResultType", function() { return BinaryClassificationResultType; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "EbnfExPart", function() { return EbnfExPart; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FlaskExercisePart", function() { return FlaskExercisePart; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "JsActionType", function() { return JsActionType; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "MatchType", function() { return MatchType; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SuccessType", function() { return SuccessType; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "XmlErrorType", function() { return XmlErrorType; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ProgExPart", function() { return ProgExPart; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegexCorrectionType", function() { return RegexCorrectionType; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegexExPart", function() { return RegexExPart; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SqlExPart", function() { return SqlExPart; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SqlExerciseType", function() { return SqlExerciseType; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ToolState", function() { return ToolState; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlAssociationType", function() { return UmlAssociationType; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlClassType", function() { return UmlClassType; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlExPart", function() { return UmlExPart; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlMultiplicity", function() { return UmlMultiplicity; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlVisibility", function() { return UmlVisibility; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "WebExPart", function() { return WebExPart; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "XmlExPart", function() { return XmlExPart; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FlaskTestResultFragmentDoc", function() { return FlaskTestResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FlaskResultFragmentDoc", function() { return FlaskResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FlaskCorrectionResultFragmentDoc", function() { return FlaskCorrectionResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ImplementationCorrectionResultFragmentDoc", function() { return ImplementationCorrectionResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UnitTestCorrectionResultFragmentDoc", function() { return UnitTestCorrectionResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ProgrammingResultFragmentDoc", function() { return ProgrammingResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ProgrammingCorrectionResultFragmentDoc", function() { return ProgrammingCorrectionResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegexAbstractResultFragmentDoc", function() { return RegexAbstractResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegexMatchingSingleResultFragmentDoc", function() { return RegexMatchingSingleResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegexMatchingResultFragmentDoc", function() { return RegexMatchingResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegexExtractionMatchFragmentDoc", function() { return RegexExtractionMatchFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExtractionMatchingResultFragmentDoc", function() { return ExtractionMatchingResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegexExtractionSingleResultFragmentDoc", function() { return RegexExtractionSingleResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegexExtractionResultFragmentDoc", function() { return RegexExtractionResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegexCorrectionResultFragmentDoc", function() { return RegexCorrectionResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "NewMatchFragmentDoc", function() { return NewMatchFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SqlMatchingResultFragmentDoc", function() { return SqlMatchingResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ColumnComparisonFragmentDoc", function() { return ColumnComparisonFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "StringMatchFragmentDoc", function() { return StringMatchFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "StringMatchingResultFragmentDoc", function() { return StringMatchingResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BinaryExpressionComparisonFragmentDoc", function() { return BinaryExpressionComparisonFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LimitComparisonFragmentDoc", function() { return LimitComparisonFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SelectAdditionalComparisonFragmentDoc", function() { return SelectAdditionalComparisonFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SqlCellFragmentDoc", function() { return SqlCellFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SqlRowFragmentDoc", function() { return SqlRowFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SqlQueryResultFragmentDoc", function() { return SqlQueryResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SqlExecutionResultFragmentDoc", function() { return SqlExecutionResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SqlResultFragmentDoc", function() { return SqlResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SqlCorrectionResultFragmentDoc", function() { return SqlCorrectionResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlSolutionClassFragmentDoc", function() { return UmlSolutionClassFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlClassMatchFragmentDoc", function() { return UmlClassMatchFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlClassMatchingResultFragmentDoc", function() { return UmlClassMatchingResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlAssociationFragmentDoc", function() { return UmlAssociationFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlAssociationMatchFragmentDoc", function() { return UmlAssociationMatchFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlAssociationMatchingResultFragmentDoc", function() { return UmlAssociationMatchingResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlImplementationFragmentDoc", function() { return UmlImplementationFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlImplementationMatchFragmentDoc", function() { return UmlImplementationMatchFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlImplementationMatchingResultFragmentDoc", function() { return UmlImplementationMatchingResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlResultFragmentDoc", function() { return UmlResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlCorrectionResultFragmentDoc", function() { return UmlCorrectionResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "GradedTextContentResultFragmentDoc", function() { return GradedTextContentResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "GradedHtmlTaskResultFragmentDoc", function() { return GradedHtmlTaskResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "GradedJsHtmlElementSpecResultFragmentDoc", function() { return GradedJsHtmlElementSpecResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "GradedJsActionResultFragmentDoc", function() { return GradedJsActionResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "GradedJsTaskResultFragmentDoc", function() { return GradedJsTaskResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "WebResultFragmentDoc", function() { return WebResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "WebCorrectionResultFragmentDoc", function() { return WebCorrectionResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ElementLineFragmentDoc", function() { return ElementLineFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "XmlElementLineAnalysisResultFragmentDoc", function() { return XmlElementLineAnalysisResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "XmlElementLineMatchFragmentDoc", function() { return XmlElementLineMatchFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "XmlElementLineMatchingResultFragmentDoc", function() { return XmlElementLineMatchingResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "XmlGrammarResultFragmentDoc", function() { return XmlGrammarResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "XmlErrorFragmentDoc", function() { return XmlErrorFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "XmlDocumentResultFragmentDoc", function() { return XmlDocumentResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "XmlResultFragmentDoc", function() { return XmlResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "XmlCorrectionResultFragmentDoc", function() { return XmlCorrectionResultFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LessonIdentifierFragmentDoc", function() { return LessonIdentifierFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LessonOverviewFragmentDoc", function() { return LessonOverviewFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LessonTextContentFragmentDoc", function() { return LessonTextContentFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LessonMultipleChoiceQuestionAnswerFragmentDoc", function() { return LessonMultipleChoiceQuestionAnswerFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LessonMultipleChoiceQuestionFragmentDoc", function() { return LessonMultipleChoiceQuestionFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LessonMultipleChoiceQuestionContentFragmentDoc", function() { return LessonMultipleChoiceQuestionContentFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LessonAsTextFragmentDoc", function() { return LessonAsTextFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CollectionToolFragmentDoc", function() { return CollectionToolFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LevelFragmentDoc", function() { return LevelFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "TopicFragmentDoc", function() { return TopicFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UserProficiencyFragmentDoc", function() { return UserProficiencyFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CollectionValuesFragmentDoc", function() { return CollectionValuesFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "TopicWithLevelFragmentDoc", function() { return TopicWithLevelFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FieldsPartFragmentDoc", function() { return FieldsPartFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FieldsForLinkFragmentDoc", function() { return FieldsForLinkFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CollOverviewToolFragmentDoc", function() { return CollOverviewToolFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "PartFragmentDoc", function() { return PartFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExerciseOverviewFragmentDoc", function() { return ExerciseOverviewFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "EbnfExerciseContentFragmentDoc", function() { return EbnfExerciseContentFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExerciseFileFragmentDoc", function() { return ExerciseFileFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FilesSolutionFragmentDoc", function() { return FilesSolutionFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FlaskExerciseContentFragmentDoc", function() { return FlaskExerciseContentFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UnitTestPartFragmentDoc", function() { return UnitTestPartFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ProgrammingExerciseContentFragmentDoc", function() { return ProgrammingExerciseContentFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegexExerciseContentFragmentDoc", function() { return RegexExerciseContentFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SqlExerciseContentFragmentDoc", function() { return SqlExerciseContentFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlAttributeFragmentDoc", function() { return UmlAttributeFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlMethodFragmentDoc", function() { return UmlMethodFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlClassFragmentDoc", function() { return UmlClassFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlClassDiagramFragmentDoc", function() { return UmlClassDiagramFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlExerciseContentFragmentDoc", function() { return UmlExerciseContentFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "WebExerciseContentFragmentDoc", function() { return WebExerciseContentFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "XmlSolutionFragmentDoc", function() { return XmlSolutionFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "XmlExerciseContentFragmentDoc", function() { return XmlExerciseContentFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExerciseSolveFieldsFragmentDoc", function() { return ExerciseSolveFieldsFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LoggedInUserWithTokenFragmentDoc", function() { return LoggedInUserWithTokenFragmentDoc; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FlaskCorrectionDocument", function() { return FlaskCorrectionDocument; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FlaskCorrectionGQL", function() { return FlaskCorrectionGQL; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ProgrammingCorrectionDocument", function() { return ProgrammingCorrectionDocument; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ProgrammingCorrectionGQL", function() { return ProgrammingCorrectionGQL; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegexCorrectionDocument", function() { return RegexCorrectionDocument; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegexCorrectionGQL", function() { return RegexCorrectionGQL; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SqlCorrectionDocument", function() { return SqlCorrectionDocument; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SqlCorrectionGQL", function() { return SqlCorrectionGQL; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlCorrectionDocument", function() { return UmlCorrectionDocument; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlCorrectionGQL", function() { return UmlCorrectionGQL; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "WebCorrectionDocument", function() { return WebCorrectionDocument; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "WebCorrectionGQL", function() { return WebCorrectionGQL; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "XmlCorrectionDocument", function() { return XmlCorrectionDocument; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "XmlCorrectionGQL", function() { return XmlCorrectionGQL; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LessonsForToolDocument", function() { return LessonsForToolDocument; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LessonsForToolGQL", function() { return LessonsForToolGQL; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LessonOverviewDocument", function() { return LessonOverviewDocument; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LessonOverviewGQL", function() { return LessonOverviewGQL; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LessonAsTextDocument", function() { return LessonAsTextDocument; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LessonAsTextGQL", function() { return LessonAsTextGQL; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LessonAsVideoDocument", function() { return LessonAsVideoDocument; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LessonAsVideoGQL", function() { return LessonAsVideoGQL; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ToolOverviewDocument", function() { return ToolOverviewDocument; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ToolOverviewGQL", function() { return ToolOverviewGQL; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CollectionToolOverviewDocument", function() { return CollectionToolOverviewDocument; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CollectionToolOverviewGQL", function() { return CollectionToolOverviewGQL; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AllExercisesOverviewDocument", function() { return AllExercisesOverviewDocument; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AllExercisesOverviewGQL", function() { return AllExercisesOverviewGQL; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CollectionListDocument", function() { return CollectionListDocument; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CollectionListGQL", function() { return CollectionListGQL; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CollectionOverviewDocument", function() { return CollectionOverviewDocument; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CollectionOverviewGQL", function() { return CollectionOverviewGQL; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExerciseOverviewDocument", function() { return ExerciseOverviewDocument; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExerciseOverviewGQL", function() { return ExerciseOverviewGQL; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExerciseDocument", function() { return ExerciseDocument; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExerciseGQL", function() { return ExerciseGQL; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegisterDocument", function() { return RegisterDocument; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegisterGQL", function() { return RegisterGQL; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LoginDocument", function() { return LoginDocument; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LoginGQL", function() { return LoginGQL; });
/* harmony import */ var apollo_angular__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! apollo-angular */ "/IUn");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "fXoL");




var BinaryClassificationResultType;
(function (BinaryClassificationResultType) {
    BinaryClassificationResultType["TruePositive"] = "TruePositive";
    BinaryClassificationResultType["FalsePositive"] = "FalsePositive";
    BinaryClassificationResultType["FalseNegative"] = "FalseNegative";
    BinaryClassificationResultType["TrueNegative"] = "TrueNegative";
})(BinaryClassificationResultType || (BinaryClassificationResultType = {}));
var EbnfExPart;
(function (EbnfExPart) {
    EbnfExPart["GrammarCreation"] = "GrammarCreation";
})(EbnfExPart || (EbnfExPart = {}));
var FlaskExercisePart;
(function (FlaskExercisePart) {
    FlaskExercisePart["FlaskSingleExPart"] = "FlaskSingleExPart";
})(FlaskExercisePart || (FlaskExercisePart = {}));
var JsActionType;
(function (JsActionType) {
    JsActionType["Click"] = "Click";
    JsActionType["FillOut"] = "FillOut";
})(JsActionType || (JsActionType = {}));
var MatchType;
(function (MatchType) {
    MatchType["SuccessfulMatch"] = "SUCCESSFUL_MATCH";
    MatchType["PartialMatch"] = "PARTIAL_MATCH";
    MatchType["UnsuccessfulMatch"] = "UNSUCCESSFUL_MATCH";
})(MatchType || (MatchType = {}));
var SuccessType;
(function (SuccessType) {
    SuccessType["Error"] = "ERROR";
    SuccessType["None"] = "NONE";
    SuccessType["Partially"] = "PARTIALLY";
    SuccessType["Complete"] = "COMPLETE";
})(SuccessType || (SuccessType = {}));
var XmlErrorType;
(function (XmlErrorType) {
    XmlErrorType["Error"] = "ERROR";
    XmlErrorType["Fatal"] = "FATAL";
    XmlErrorType["Warning"] = "WARNING";
})(XmlErrorType || (XmlErrorType = {}));
var ProgExPart;
(function (ProgExPart) {
    ProgExPart["TestCreation"] = "TestCreation";
    ProgExPart["Implementation"] = "Implementation";
    ProgExPart["ActivityDiagram"] = "ActivityDiagram";
})(ProgExPart || (ProgExPart = {}));
var RegexCorrectionType;
(function (RegexCorrectionType) {
    RegexCorrectionType["Matching"] = "MATCHING";
    RegexCorrectionType["Extraction"] = "EXTRACTION";
})(RegexCorrectionType || (RegexCorrectionType = {}));
var RegexExPart;
(function (RegexExPart) {
    RegexExPart["RegexSingleExPart"] = "RegexSingleExPart";
})(RegexExPart || (RegexExPart = {}));
var SqlExPart;
(function (SqlExPart) {
    SqlExPart["SqlSingleExPart"] = "SqlSingleExPart";
})(SqlExPart || (SqlExPart = {}));
var SqlExerciseType;
(function (SqlExerciseType) {
    SqlExerciseType["Insert"] = "INSERT";
    SqlExerciseType["Update"] = "UPDATE";
    SqlExerciseType["Create"] = "CREATE";
    SqlExerciseType["Select"] = "SELECT";
    SqlExerciseType["Delete"] = "DELETE";
})(SqlExerciseType || (SqlExerciseType = {}));
var ToolState;
(function (ToolState) {
    ToolState["PreAlpha"] = "PRE_ALPHA";
    ToolState["Alpha"] = "ALPHA";
    ToolState["Beta"] = "BETA";
    ToolState["Live"] = "LIVE";
})(ToolState || (ToolState = {}));
var UmlAssociationType;
(function (UmlAssociationType) {
    UmlAssociationType["Association"] = "ASSOCIATION";
    UmlAssociationType["Aggregation"] = "AGGREGATION";
    UmlAssociationType["Composition"] = "COMPOSITION";
})(UmlAssociationType || (UmlAssociationType = {}));
var UmlClassType;
(function (UmlClassType) {
    UmlClassType["Abstract"] = "ABSTRACT";
    UmlClassType["Class"] = "CLASS";
    UmlClassType["Interface"] = "INTERFACE";
})(UmlClassType || (UmlClassType = {}));
var UmlExPart;
(function (UmlExPart) {
    UmlExPart["ClassSelection"] = "ClassSelection";
    UmlExPart["DiagramDrawingHelp"] = "DiagramDrawingHelp";
    UmlExPart["DiagramDrawing"] = "DiagramDrawing";
    UmlExPart["MemberAllocation"] = "MemberAllocation";
})(UmlExPart || (UmlExPart = {}));
var UmlMultiplicity;
(function (UmlMultiplicity) {
    UmlMultiplicity["Single"] = "SINGLE";
    UmlMultiplicity["Unbound"] = "UNBOUND";
})(UmlMultiplicity || (UmlMultiplicity = {}));
var UmlVisibility;
(function (UmlVisibility) {
    UmlVisibility["Public"] = "PUBLIC";
    UmlVisibility["Package"] = "PACKAGE";
    UmlVisibility["Protected"] = "PROTECTED";
    UmlVisibility["Private"] = "PRIVATE";
})(UmlVisibility || (UmlVisibility = {}));
var WebExPart;
(function (WebExPart) {
    WebExPart["HtmlPart"] = "HtmlPart";
    WebExPart["JsPart"] = "JsPart";
})(WebExPart || (WebExPart = {}));
var XmlExPart;
(function (XmlExPart) {
    XmlExPart["GrammarCreationXmlPart"] = "GrammarCreationXmlPart";
    XmlExPart["DocumentCreationXmlPart"] = "DocumentCreationXmlPart";
})(XmlExPart || (XmlExPart = {}));
const FlaskTestResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment FlaskTestResult on FlaskTestResult {
  testName
  successful
  stdout
  stderr
}
    `;
const FlaskResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment FlaskResult on FlaskResult {
  points
  maxPoints
  testResults {
    ...FlaskTestResult
  }
}
    ${FlaskTestResultFragmentDoc}`;
const FlaskCorrectionResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment FlaskCorrectionResult on FlaskCorrectionResult {
  solutionSaved
  resultSaved
  proficienciesUpdated
  result {
    ...FlaskResult
  }
}
    ${FlaskResultFragmentDoc}`;
const ImplementationCorrectionResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment ImplementationCorrectionResult on ImplementationCorrectionResult {
  successful
  stdout
  stderr
}
    `;
const UnitTestCorrectionResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment UnitTestCorrectionResult on UnitTestCorrectionResult {
  testId
  successful
  shouldFail
  description
  stderr
}
    `;
const ProgrammingResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment ProgrammingResult on ProgrammingResult {
  points
  maxPoints
  implementationCorrectionResult {
    ...ImplementationCorrectionResult
  }
  unitTestResults {
    ...UnitTestCorrectionResult
  }
}
    ${ImplementationCorrectionResultFragmentDoc}
${UnitTestCorrectionResultFragmentDoc}`;
const ProgrammingCorrectionResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment ProgrammingCorrectionResult on ProgrammingCorrectionResult {
  __typename
  solutionSaved
  resultSaved
  proficienciesUpdated
  result {
    ...ProgrammingResult
  }
}
    ${ProgrammingResultFragmentDoc}`;
const RegexAbstractResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment RegexAbstractResult on RegexAbstractResult {
  __typename
  points
  maxPoints
}
    `;
const RegexMatchingSingleResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment RegexMatchingSingleResult on RegexMatchingSingleResult {
  resultType
  matchData
}
    `;
const RegexMatchingResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment RegexMatchingResult on RegexMatchingResult {
  matchingResults {
    ...RegexMatchingSingleResult
  }
}
    ${RegexMatchingSingleResultFragmentDoc}`;
const RegexExtractionMatchFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment RegexExtractionMatch on RegexMatchMatch {
  matchType
  userArg
  sampleArg
}
    `;
const ExtractionMatchingResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment ExtractionMatchingResult on RegexExtractedValuesComparisonMatchingResult {
  allMatches {
    ...RegexExtractionMatch
  }
  notMatchedForUser
  notMatchedForSample
  points
  maxPoints
}
    ${RegexExtractionMatchFragmentDoc}`;
const RegexExtractionSingleResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment RegexExtractionSingleResult on RegexExtractionSingleResult {
  base
  extractionMatchingResult {
    ...ExtractionMatchingResult
  }
}
    ${ExtractionMatchingResultFragmentDoc}`;
const RegexExtractionResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment RegexExtractionResult on RegexExtractionResult {
  extractionResults {
    ...RegexExtractionSingleResult
  }
}
    ${RegexExtractionSingleResultFragmentDoc}`;
const RegexCorrectionResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment RegexCorrectionResult on RegexCorrectionResult {
  solutionSaved
  resultSaved
  proficienciesUpdated
  result {
    ...RegexAbstractResult
    ...RegexMatchingResult
    ...RegexExtractionResult
  }
}
    ${RegexAbstractResultFragmentDoc}
${RegexMatchingResultFragmentDoc}
${RegexExtractionResultFragmentDoc}`;
const NewMatchFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment NewMatch on NewMatch {
  matchType
  sampleArgDescription
  userArgDescription
}
    `;
const SqlMatchingResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment SqlMatchingResult on MatchingResult {
  points
  maxPoints
  allMatches {
    ...NewMatch
  }
  notMatchedForUserString
  notMatchedForSampleString
}
    ${NewMatchFragmentDoc}`;
const ColumnComparisonFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment ColumnComparison on SqlColumnComparisonMatchingResult {
  ...SqlMatchingResult
}
    ${SqlMatchingResultFragmentDoc}`;
const StringMatchFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment StringMatch on StringMatch {
  matchType
  sampleArg
  userArg
}
    `;
const StringMatchingResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment StringMatchingResult on StringMatchingResult {
  points
  maxPoints
  allMatches {
    ...StringMatch
  }
  notMatchedForUser
  notMatchedForSample
}
    ${StringMatchFragmentDoc}`;
const BinaryExpressionComparisonFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment BinaryExpressionComparison on SqlBinaryExpressionComparisonMatchingResult {
  ...SqlMatchingResult
}
    ${SqlMatchingResultFragmentDoc}`;
const LimitComparisonFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment LimitComparison on SqlLimitComparisonMatchingResult {
  ...SqlMatchingResult
}
    ${SqlMatchingResultFragmentDoc}`;
const SelectAdditionalComparisonFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment SelectAdditionalComparison on SelectAdditionalComparisons {
  groupByComparison {
    ...StringMatchingResult
  }
  orderByComparison {
    ...StringMatchingResult
  }
  limitComparison {
    ...LimitComparison
  }
}
    ${StringMatchingResultFragmentDoc}
${LimitComparisonFragmentDoc}`;
const SqlCellFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment SqlCell on SqlCell {
  colName
  content
  different
}
    `;
const SqlRowFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment SqlRow on SqlRow {
  cells {
    key
    value {
      ...SqlCell
    }
  }
}
    ${SqlCellFragmentDoc}`;
const SqlQueryResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment SqlQueryResult on SqlQueryResult {
  tableName
  columnNames
  rows {
    ...SqlRow
  }
}
    ${SqlRowFragmentDoc}`;
const SqlExecutionResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment SqlExecutionResult on SqlExecutionResult {
  userResult {
    ...SqlQueryResult
  }
  sampleResult {
    ...SqlQueryResult
  }
}
    ${SqlQueryResultFragmentDoc}`;
const SqlResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment SqlResult on SqlResult {
  points
  maxPoints
  staticComparison {
    columnComparison {
      ...ColumnComparison
    }
    tableComparison {
      ...StringMatchingResult
    }
    joinExpressionComparison {
      ...BinaryExpressionComparison
    }
    whereComparison {
      ...BinaryExpressionComparison
    }
    additionalComparisons {
      selectComparisons {
        ...SelectAdditionalComparison
      }
      insertComparison {
        ...StringMatchingResult
      }
    }
  }
  executionResult {
    ...SqlExecutionResult
  }
}
    ${ColumnComparisonFragmentDoc}
${StringMatchingResultFragmentDoc}
${BinaryExpressionComparisonFragmentDoc}
${SelectAdditionalComparisonFragmentDoc}
${SqlExecutionResultFragmentDoc}`;
const SqlCorrectionResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment SqlCorrectionResult on SqlCorrectionResult {
  solutionSaved
  resultSaved
  proficienciesUpdated
  result {
    ...SqlResult
  }
}
    ${SqlResultFragmentDoc}`;
const UmlSolutionClassFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment UmlSolutionClass on UmlClass {
  classType
  name
  attributes {
    __typename
  }
  methods {
    __typename
  }
}
    `;
const UmlClassMatchFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment UmlClassMatch on UmlClassMatch {
  matchType
  userArg {
    ...UmlSolutionClass
  }
  sampleArg {
    ...UmlSolutionClass
  }
  analysisResult {
    __typename
  }
}
    ${UmlSolutionClassFragmentDoc}`;
const UmlClassMatchingResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment UmlClassMatchingResult on UmlClassMatchingResult {
  allMatches {
    ...UmlClassMatch
  }
  notMatchedForUser {
    ...UmlSolutionClass
  }
  notMatchedForSample {
    ...UmlSolutionClass
  }
  points
  maxPoints
}
    ${UmlClassMatchFragmentDoc}
${UmlSolutionClassFragmentDoc}`;
const UmlAssociationFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment UmlAssociation on UmlAssociation {
  assocType
  assocName
  firstEnd
  firstMult
  secondEnd
  secondMult
}
    `;
const UmlAssociationMatchFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment UmlAssociationMatch on UmlAssociationMatch {
  matchType
  userArg {
    ...UmlAssociation
  }
  sampleArg {
    ...UmlAssociation
  }
  analysisResult {
    assocTypeEqual
    correctAssocType
    multiplicitiesEqual
  }
}
    ${UmlAssociationFragmentDoc}`;
const UmlAssociationMatchingResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment UmlAssociationMatchingResult on UmlAssociationMatchingResult {
  allMatches {
    ...UmlAssociationMatch
  }
  notMatchedForUser {
    ...UmlAssociation
  }
  notMatchedForSample {
    ...UmlAssociation
  }
  points
  maxPoints
}
    ${UmlAssociationMatchFragmentDoc}
${UmlAssociationFragmentDoc}`;
const UmlImplementationFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment UmlImplementation on UmlImplementation {
  subClass
  superClass
}
    `;
const UmlImplementationMatchFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment UmlImplementationMatch on UmlImplementationMatch {
  matchType
  userArg {
    ...UmlImplementation
  }
  sampleArg {
    ...UmlImplementation
  }
}
    ${UmlImplementationFragmentDoc}`;
const UmlImplementationMatchingResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment UmlImplementationMatchingResult on UmlImplementationMatchingResult {
  allMatches {
    ...UmlImplementationMatch
  }
  notMatchedForUser {
    ...UmlImplementation
  }
  notMatchedForSample {
    ...UmlImplementation
  }
  points
  maxPoints
}
    ${UmlImplementationMatchFragmentDoc}
${UmlImplementationFragmentDoc}`;
const UmlResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment UmlResult on UmlResult {
  points
  maxPoints
  classResult {
    ...UmlClassMatchingResult
  }
  assocResult {
    ...UmlAssociationMatchingResult
  }
  implResult {
    ...UmlImplementationMatchingResult
  }
}
    ${UmlClassMatchingResultFragmentDoc}
${UmlAssociationMatchingResultFragmentDoc}
${UmlImplementationMatchingResultFragmentDoc}`;
const UmlCorrectionResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment UmlCorrectionResult on UmlCorrectionResult {
  solutionSaved
  resultSaved
  proficienciesUpdated
  result {
    ...UmlResult
  }
}
    ${UmlResultFragmentDoc}`;
const GradedTextContentResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment GradedTextContentResult on GradedTextResult {
  keyName
  awaitedContent
  maybeFoundContent
  isSuccessful
  points
  maxPoints
}
    `;
const GradedHtmlTaskResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment GradedHtmlTaskResult on GradedHtmlTaskResult {
  id
  success
  elementFound
  textContentResult {
    ...GradedTextContentResult
  }
  attributeResults {
    ...GradedTextContentResult
  }
  isSuccessful
  points
  maxPoints
}
    ${GradedTextContentResultFragmentDoc}`;
const GradedJsHtmlElementSpecResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment GradedJsHtmlElementSpecResult on GradedJsHtmlElementSpecResult {
  id
}
    `;
const GradedJsActionResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment GradedJsActionResult on GradedJsActionResult {
  jsAction {
    __typename
  }
  actionPerformed
  points
  maxPoints
}
    `;
const GradedJsTaskResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment GradedJsTaskResult on GradedJsTaskResult {
  id
  gradedPreResults {
    ...GradedJsHtmlElementSpecResult
  }
  gradedJsActionResult {
    ...GradedJsActionResult
  }
  gradedPostResults {
    ...GradedJsHtmlElementSpecResult
  }
  success
  points
  maxPoints
}
    ${GradedJsHtmlElementSpecResultFragmentDoc}
${GradedJsActionResultFragmentDoc}`;
const WebResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment WebResult on WebResult {
  points
  maxPoints
  gradedHtmlTaskResults {
    ...GradedHtmlTaskResult
  }
  gradedJsTaskResults {
    ...GradedJsTaskResult
  }
}
    ${GradedHtmlTaskResultFragmentDoc}
${GradedJsTaskResultFragmentDoc}`;
const WebCorrectionResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment WebCorrectionResult on WebCorrectionResult {
  solutionSaved
  resultSaved
  proficienciesUpdated
  result {
    ...WebResult
  }
}
    ${WebResultFragmentDoc}`;
const ElementLineFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment ElementLine on ElementLine {
  elementName
  elementDefinition {
    elementName
    content
  }
  attributeLists {
    elementName
    attributeDefinitions
  }
}
    `;
const XmlElementLineAnalysisResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment XmlElementLineAnalysisResult on ElementLineAnalysisResult {
  attributesCorrect
  correctAttributes
  contentCorrect
  correctContent
}
    `;
const XmlElementLineMatchFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment XmlElementLineMatch on ElementLineMatch {
  matchType
  userArg {
    ...ElementLine
  }
  sampleArg {
    ...ElementLine
  }
  analysisResult {
    ...XmlElementLineAnalysisResult
  }
}
    ${ElementLineFragmentDoc}
${XmlElementLineAnalysisResultFragmentDoc}`;
const XmlElementLineMatchingResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment XmlElementLineMatchingResult on XmlElementLineComparisonMatchingResult {
  allMatches {
    ...XmlElementLineMatch
  }
  notMatchedForUser {
    ...ElementLine
  }
  notMatchedForSample {
    ...ElementLine
  }
}
    ${XmlElementLineMatchFragmentDoc}
${ElementLineFragmentDoc}`;
const XmlGrammarResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment XmlGrammarResult on XmlGrammarResult {
  parseErrors {
    msg
    parsedLine
  }
  results {
    points
    maxPoints
    ...XmlElementLineMatchingResult
  }
}
    ${XmlElementLineMatchingResultFragmentDoc}`;
const XmlErrorFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment XmlError on XmlError {
  success
  line
  errorType
  errorMessage
}
    `;
const XmlDocumentResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment XmlDocumentResult on XmlDocumentResult {
  errors {
    ...XmlError
  }
}
    ${XmlErrorFragmentDoc}`;
const XmlResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment XmlResult on XmlResult {
  points
  maxPoints
  successType
  grammarResult {
    ...XmlGrammarResult
  }
  documentResult {
    ...XmlDocumentResult
  }
}
    ${XmlGrammarResultFragmentDoc}
${XmlDocumentResultFragmentDoc}`;
const XmlCorrectionResultFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment XmlCorrectionResult on XmlCorrectionResult {
  solutionSaved
  resultSaved
  proficienciesUpdated
  result {
    ...XmlResult
  }
}
    ${XmlResultFragmentDoc}`;
const LessonIdentifierFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment LessonIdentifier on Lesson {
  lessonId
  title
  description
  video
}
    `;
const LessonOverviewFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment LessonOverview on Lesson {
  title
  description
  video
  contentCount
}
    `;
const LessonTextContentFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment LessonTextContent on LessonTextContent {
  __typename
  contentId
  content
}
    `;
const LessonMultipleChoiceQuestionAnswerFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment LessonMultipleChoiceQuestionAnswer on LessonMultipleChoiceQuestionAnswer {
  id
  answer
  isCorrect
}
    `;
const LessonMultipleChoiceQuestionFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment LessonMultipleChoiceQuestion on LessonMultipleChoiceQuestion {
  id
  questionText
  answers {
    ...LessonMultipleChoiceQuestionAnswer
  }
}
    ${LessonMultipleChoiceQuestionAnswerFragmentDoc}`;
const LessonMultipleChoiceQuestionContentFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment LessonMultipleChoiceQuestionContent on LessonMultipleChoiceQuestionsContent {
  __typename
  contentId
  questions {
    ...LessonMultipleChoiceQuestion
  }
}
    ${LessonMultipleChoiceQuestionFragmentDoc}`;
const LessonAsTextFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment LessonAsText on Lesson {
  lessonId
  title
  description
  contents {
    ... on LessonTextContent {
      ...LessonTextContent
    }
    ... on LessonMultipleChoiceQuestionsContent {
      ...LessonMultipleChoiceQuestionContent
    }
  }
}
    ${LessonTextContentFragmentDoc}
${LessonMultipleChoiceQuestionContentFragmentDoc}`;
const CollectionToolFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment CollectionTool on CollectionTool {
  id
  name
  state
  collectionCount
  lessonCount
  exerciseCount
}
    `;
const LevelFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment Level on Level {
  title
  levelIndex
}
    `;
const TopicFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment Topic on Topic {
  abbreviation
  title
  maxLevel {
    ...Level
  }
}
    ${LevelFragmentDoc}`;
const UserProficiencyFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment UserProficiency on UserProficiency {
  topic {
    ...Topic
  }
  points
  pointsForNextLevel
  level {
    ...Level
  }
}
    ${TopicFragmentDoc}
${LevelFragmentDoc}`;
const CollectionValuesFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment CollectionValues on ExerciseCollection {
  collectionId
  title
  exerciseCount
}
    `;
const TopicWithLevelFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment TopicWithLevel on TopicWithLevel {
  topic {
    ...Topic
  }
  level {
    ...Level
  }
}
    ${TopicFragmentDoc}
${LevelFragmentDoc}`;
const FieldsPartFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment FieldsPart on ExPart {
  id
  name
  solved
}
    `;
const FieldsForLinkFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment FieldsForLink on Exercise {
  exerciseId
  collectionId
  toolId
  title
  difficulty
  topicsWithLevels {
    ...TopicWithLevel
  }
  parts {
    ...FieldsPart
  }
}
    ${TopicWithLevelFragmentDoc}
${FieldsPartFragmentDoc}`;
const CollOverviewToolFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment CollOverviewTool on CollectionTool {
  id
  name
  collection(collId: $collId) {
    collectionId
    title
    exercises {
      ...FieldsForLink
    }
  }
}
    ${FieldsForLinkFragmentDoc}`;
const PartFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment Part on ExPart {
  id
  name
  isEntryPart
  solved
}
    `;
const ExerciseOverviewFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment ExerciseOverview on Exercise {
  exerciseId
  title
  text
  parts {
    ...Part
  }
}
    ${PartFragmentDoc}`;
const EbnfExerciseContentFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment EbnfExerciseContent on EbnfExerciseContent {
  sampleSolutions
}
    `;
const ExerciseFileFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment ExerciseFile on ExerciseFile {
  name
  fileType
  content
  editable
}
    `;
const FilesSolutionFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment FilesSolution on FilesSolution {
  __typename
  files {
    ...ExerciseFile
  }
}
    ${ExerciseFileFragmentDoc}`;
const FlaskExerciseContentFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment FlaskExerciseContent on FlaskExerciseContent {
  __typename
  testConfig {
    tests {
      id
      testName
      description
    }
  }
  files {
    ...ExerciseFile
  }
  flaskSampleSolutions: sampleSolutions {
    ...FilesSolution
  }
}
    ${ExerciseFileFragmentDoc}
${FilesSolutionFragmentDoc}`;
const UnitTestPartFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment UnitTestPart on UnitTestPart {
  unitTestFiles {
    ...ExerciseFile
  }
}
    ${ExerciseFileFragmentDoc}`;
const ProgrammingExerciseContentFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment ProgrammingExerciseContent on ProgrammingExerciseContent {
  unitTestPart {
    ...UnitTestPart
  }
  implementationPart {
    files {
      ...ExerciseFile
    }
  }
  programmingSampleSolutions: sampleSolutions {
    ...FilesSolution
  }
  programmingPart: part(partId: $partId)
}
    ${UnitTestPartFragmentDoc}
${ExerciseFileFragmentDoc}
${FilesSolutionFragmentDoc}`;
const RegexExerciseContentFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment RegexExerciseContent on RegexExerciseContent {
  regexSampleSolutions: sampleSolutions
  regexPart: part(partId: $partId)
}
    `;
const SqlExerciseContentFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment SqlExerciseContent on SqlExerciseContent {
  hint
  sqlSampleSolutions: sampleSolutions
  sqlPart: part(partId: $partId)
  sqlDbContents {
    ...SqlQueryResult
  }
}
    ${SqlQueryResultFragmentDoc}`;
const UmlAttributeFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment UmlAttribute on UmlAttribute {
  isAbstract
  isDerived
  isStatic
  visibility
  memberName
  memberType
}
    `;
const UmlMethodFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment UmlMethod on UmlMethod {
  isAbstract
  isStatic
  visibility
  memberName
  parameters
  memberType
}
    `;
const UmlClassFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment UmlClass on UmlClass {
  classType
  name
  attributes {
    ...UmlAttribute
  }
  methods {
    ...UmlMethod
  }
}
    ${UmlAttributeFragmentDoc}
${UmlMethodFragmentDoc}`;
const UmlClassDiagramFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment UmlClassDiagram on UmlClassDiagram {
  classes {
    ...UmlClass
  }
  associations {
    ...UmlAssociation
  }
  implementations {
    ...UmlImplementation
  }
}
    ${UmlClassFragmentDoc}
${UmlAssociationFragmentDoc}
${UmlImplementationFragmentDoc}`;
const UmlExerciseContentFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment UmlExerciseContent on UmlExerciseContent {
  toIgnore
  mappings {
    key
    value
  }
  umlSampleSolutions: sampleSolutions {
    ...UmlClassDiagram
  }
  umlPart: part(partId: $partId)
}
    ${UmlClassDiagramFragmentDoc}`;
const WebExerciseContentFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment WebExerciseContent on WebExerciseContent {
  files {
    ...ExerciseFile
  }
  siteSpec {
    fileName
    htmlTasks {
      text
    }
    jsTaskCount
  }
  webSampleSolutions: sampleSolutions {
    ...FilesSolution
  }
  webPart: part(partId: $partId)
}
    ${ExerciseFileFragmentDoc}
${FilesSolutionFragmentDoc}`;
const XmlSolutionFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment XmlSolution on XmlSolution {
  __typename
  document
  grammar
}
    `;
const XmlExerciseContentFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment XmlExerciseContent on XmlExerciseContent {
  rootNode
  grammarDescription
  xmlSampleSolutions: sampleSolutions {
    ...XmlSolution
  }
  xmlPart: part(partId: $partId)
}
    ${XmlSolutionFragmentDoc}`;
const ExerciseSolveFieldsFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment ExerciseSolveFields on Exercise {
  exerciseId
  collectionId
  toolId
  title
  text
  content {
    __typename
    ...EbnfExerciseContent
    ...FlaskExerciseContent
    ...ProgrammingExerciseContent
    ...ProgrammingExerciseContent
    ...RegexExerciseContent
    ...SqlExerciseContent
    ...UmlExerciseContent
    ...WebExerciseContent
    ...XmlExerciseContent
  }
}
    ${EbnfExerciseContentFragmentDoc}
${FlaskExerciseContentFragmentDoc}
${ProgrammingExerciseContentFragmentDoc}
${RegexExerciseContentFragmentDoc}
${SqlExerciseContentFragmentDoc}
${UmlExerciseContentFragmentDoc}
${WebExerciseContentFragmentDoc}
${XmlExerciseContentFragmentDoc}`;
const LoggedInUserWithTokenFragmentDoc = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    fragment LoggedInUserWithToken on LoggedInUserWithToken {
  loggedInUser {
    username
    isAdmin
  }
  jwt
}
    `;
const FlaskCorrectionDocument = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    mutation FlaskCorrection($collId: Int!, $exId: Int!, $part: FlaskExercisePart!, $solution: FilesSolutionInput!) {
  me {
    flaskExercise(collId: $collId, exId: $exId) {
      correct(part: $part, solution: $solution) {
        ...FlaskCorrectionResult
      }
    }
  }
}
    ${FlaskCorrectionResultFragmentDoc}`;
class FlaskCorrectionGQL extends apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Mutation"] {
    constructor(apollo) {
        super(apollo);
        this.document = FlaskCorrectionDocument;
    }
}
FlaskCorrectionGQL.ɵfac = function FlaskCorrectionGQL_Factory(t) { return new (t || FlaskCorrectionGQL)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵinject"](apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Apollo"])); };
FlaskCorrectionGQL.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: FlaskCorrectionGQL, factory: FlaskCorrectionGQL.ɵfac, providedIn: 'root' });
const ProgrammingCorrectionDocument = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    mutation ProgrammingCorrection($collId: Int!, $exId: Int!, $part: ProgExPart!, $solution: FilesSolutionInput!) {
  me {
    programmingExercise(collId: $collId, exId: $exId) {
      correct(part: $part, solution: $solution) {
        ...ProgrammingCorrectionResult
      }
    }
  }
}
    ${ProgrammingCorrectionResultFragmentDoc}`;
class ProgrammingCorrectionGQL extends apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Mutation"] {
    constructor(apollo) {
        super(apollo);
        this.document = ProgrammingCorrectionDocument;
    }
}
ProgrammingCorrectionGQL.ɵfac = function ProgrammingCorrectionGQL_Factory(t) { return new (t || ProgrammingCorrectionGQL)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵinject"](apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Apollo"])); };
ProgrammingCorrectionGQL.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: ProgrammingCorrectionGQL, factory: ProgrammingCorrectionGQL.ɵfac, providedIn: 'root' });
const RegexCorrectionDocument = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    mutation RegexCorrection($collId: Int!, $exId: Int!, $part: RegexExPart!, $solution: String!) {
  me {
    regexExercise(collId: $collId, exId: $exId) {
      correct(part: $part, solution: $solution) {
        ...RegexCorrectionResult
      }
    }
  }
}
    ${RegexCorrectionResultFragmentDoc}`;
class RegexCorrectionGQL extends apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Mutation"] {
    constructor(apollo) {
        super(apollo);
        this.document = RegexCorrectionDocument;
    }
}
RegexCorrectionGQL.ɵfac = function RegexCorrectionGQL_Factory(t) { return new (t || RegexCorrectionGQL)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵinject"](apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Apollo"])); };
RegexCorrectionGQL.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: RegexCorrectionGQL, factory: RegexCorrectionGQL.ɵfac, providedIn: 'root' });
const SqlCorrectionDocument = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    mutation SqlCorrection($collId: Int!, $exId: Int!, $part: SqlExPart!, $solution: String!) {
  me {
    sqlExercise(collId: $collId, exId: $exId) {
      correct(part: $part, solution: $solution) {
        ...SqlCorrectionResult
      }
    }
  }
}
    ${SqlCorrectionResultFragmentDoc}`;
class SqlCorrectionGQL extends apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Mutation"] {
    constructor(apollo) {
        super(apollo);
        this.document = SqlCorrectionDocument;
    }
}
SqlCorrectionGQL.ɵfac = function SqlCorrectionGQL_Factory(t) { return new (t || SqlCorrectionGQL)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵinject"](apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Apollo"])); };
SqlCorrectionGQL.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: SqlCorrectionGQL, factory: SqlCorrectionGQL.ɵfac, providedIn: 'root' });
const UmlCorrectionDocument = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    mutation UmlCorrection($collId: Int!, $exId: Int!, $part: UmlExPart!, $solution: UmlClassDiagramInput!) {
  me {
    umlExercise(collId: $collId, exId: $exId) {
      correct(part: $part, solution: $solution) {
        ...UmlCorrectionResult
      }
    }
  }
}
    ${UmlCorrectionResultFragmentDoc}`;
class UmlCorrectionGQL extends apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Mutation"] {
    constructor(apollo) {
        super(apollo);
        this.document = UmlCorrectionDocument;
    }
}
UmlCorrectionGQL.ɵfac = function UmlCorrectionGQL_Factory(t) { return new (t || UmlCorrectionGQL)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵinject"](apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Apollo"])); };
UmlCorrectionGQL.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: UmlCorrectionGQL, factory: UmlCorrectionGQL.ɵfac, providedIn: 'root' });
const WebCorrectionDocument = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    mutation WebCorrection($collId: Int!, $exId: Int!, $part: WebExPart!, $solution: FilesSolutionInput!) {
  me {
    webExercise(collId: $collId, exId: $exId) {
      correct(part: $part, solution: $solution) {
        ...WebCorrectionResult
      }
    }
  }
}
    ${WebCorrectionResultFragmentDoc}`;
class WebCorrectionGQL extends apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Mutation"] {
    constructor(apollo) {
        super(apollo);
        this.document = WebCorrectionDocument;
    }
}
WebCorrectionGQL.ɵfac = function WebCorrectionGQL_Factory(t) { return new (t || WebCorrectionGQL)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵinject"](apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Apollo"])); };
WebCorrectionGQL.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: WebCorrectionGQL, factory: WebCorrectionGQL.ɵfac, providedIn: 'root' });
const XmlCorrectionDocument = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    mutation XmlCorrection($collId: Int!, $exId: Int!, $part: XmlExPart!, $solution: XmlSolutionInput!) {
  me {
    xmlExercise(collId: $collId, exId: $exId) {
      correct(part: $part, solution: $solution) {
        ...XmlCorrectionResult
      }
    }
  }
}
    ${XmlCorrectionResultFragmentDoc}`;
class XmlCorrectionGQL extends apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Mutation"] {
    constructor(apollo) {
        super(apollo);
        this.document = XmlCorrectionDocument;
    }
}
XmlCorrectionGQL.ɵfac = function XmlCorrectionGQL_Factory(t) { return new (t || XmlCorrectionGQL)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵinject"](apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Apollo"])); };
XmlCorrectionGQL.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: XmlCorrectionGQL, factory: XmlCorrectionGQL.ɵfac, providedIn: 'root' });
const LessonsForToolDocument = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    query LessonsForTool($toolId: String!) {
  me {
    tool(toolId: $toolId) {
      name
      lessons {
        ...LessonIdentifier
      }
    }
  }
}
    ${LessonIdentifierFragmentDoc}`;
class LessonsForToolGQL extends apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Query"] {
    constructor(apollo) {
        super(apollo);
        this.document = LessonsForToolDocument;
    }
}
LessonsForToolGQL.ɵfac = function LessonsForToolGQL_Factory(t) { return new (t || LessonsForToolGQL)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵinject"](apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Apollo"])); };
LessonsForToolGQL.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: LessonsForToolGQL, factory: LessonsForToolGQL.ɵfac, providedIn: 'root' });
const LessonOverviewDocument = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    query LessonOverview($toolId: String!, $lessonId: Int!) {
  me {
    tool(toolId: $toolId) {
      lesson(lessonId: $lessonId) {
        ...LessonOverview
      }
    }
  }
}
    ${LessonOverviewFragmentDoc}`;
class LessonOverviewGQL extends apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Query"] {
    constructor(apollo) {
        super(apollo);
        this.document = LessonOverviewDocument;
    }
}
LessonOverviewGQL.ɵfac = function LessonOverviewGQL_Factory(t) { return new (t || LessonOverviewGQL)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵinject"](apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Apollo"])); };
LessonOverviewGQL.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: LessonOverviewGQL, factory: LessonOverviewGQL.ɵfac, providedIn: 'root' });
const LessonAsTextDocument = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    query LessonAsText($toolId: String!, $lessonId: Int!) {
  me {
    tool(toolId: $toolId) {
      lesson(lessonId: $lessonId) {
        ...LessonAsText
      }
    }
  }
}
    ${LessonAsTextFragmentDoc}`;
class LessonAsTextGQL extends apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Query"] {
    constructor(apollo) {
        super(apollo);
        this.document = LessonAsTextDocument;
    }
}
LessonAsTextGQL.ɵfac = function LessonAsTextGQL_Factory(t) { return new (t || LessonAsTextGQL)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵinject"](apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Apollo"])); };
LessonAsTextGQL.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: LessonAsTextGQL, factory: LessonAsTextGQL.ɵfac, providedIn: 'root' });
const LessonAsVideoDocument = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    query LessonAsVideo($toolId: String!, $lessonId: Int!) {
  me {
    tool(toolId: $toolId) {
      lesson(lessonId: $lessonId) {
        title
        video
      }
    }
  }
}
    `;
class LessonAsVideoGQL extends apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Query"] {
    constructor(apollo) {
        super(apollo);
        this.document = LessonAsVideoDocument;
    }
}
LessonAsVideoGQL.ɵfac = function LessonAsVideoGQL_Factory(t) { return new (t || LessonAsVideoGQL)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵinject"](apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Apollo"])); };
LessonAsVideoGQL.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: LessonAsVideoGQL, factory: LessonAsVideoGQL.ɵfac, providedIn: 'root' });
const ToolOverviewDocument = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    query ToolOverview {
  me {
    tools {
      ...CollectionTool
    }
  }
}
    ${CollectionToolFragmentDoc}`;
class ToolOverviewGQL extends apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Query"] {
    constructor(apollo) {
        super(apollo);
        this.document = ToolOverviewDocument;
    }
}
ToolOverviewGQL.ɵfac = function ToolOverviewGQL_Factory(t) { return new (t || ToolOverviewGQL)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵinject"](apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Apollo"])); };
ToolOverviewGQL.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: ToolOverviewGQL, factory: ToolOverviewGQL.ɵfac, providedIn: 'root' });
const CollectionToolOverviewDocument = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    query CollectionToolOverview($toolId: String!) {
  me {
    tool(toolId: $toolId) {
      id
      name
      collectionCount
      exerciseCount
      lessonCount
      proficiencies {
        ...UserProficiency
      }
    }
  }
}
    ${UserProficiencyFragmentDoc}`;
class CollectionToolOverviewGQL extends apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Query"] {
    constructor(apollo) {
        super(apollo);
        this.document = CollectionToolOverviewDocument;
    }
}
CollectionToolOverviewGQL.ɵfac = function CollectionToolOverviewGQL_Factory(t) { return new (t || CollectionToolOverviewGQL)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵinject"](apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Apollo"])); };
CollectionToolOverviewGQL.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: CollectionToolOverviewGQL, factory: CollectionToolOverviewGQL.ɵfac, providedIn: 'root' });
const AllExercisesOverviewDocument = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    query AllExercisesOverview($toolId: String!) {
  me {
    tool(toolId: $toolId) {
      allExercises {
        topicsWithLevels {
          ...TopicWithLevel
        }
        ...FieldsForLink
      }
    }
  }
}
    ${TopicWithLevelFragmentDoc}
${FieldsForLinkFragmentDoc}`;
class AllExercisesOverviewGQL extends apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Query"] {
    constructor(apollo) {
        super(apollo);
        this.document = AllExercisesOverviewDocument;
    }
}
AllExercisesOverviewGQL.ɵfac = function AllExercisesOverviewGQL_Factory(t) { return new (t || AllExercisesOverviewGQL)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵinject"](apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Apollo"])); };
AllExercisesOverviewGQL.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: AllExercisesOverviewGQL, factory: AllExercisesOverviewGQL.ɵfac, providedIn: 'root' });
const CollectionListDocument = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    query CollectionList($toolId: String!) {
  me {
    tool(toolId: $toolId) {
      id
      name
      collections {
        ...CollectionValues
      }
    }
  }
}
    ${CollectionValuesFragmentDoc}`;
class CollectionListGQL extends apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Query"] {
    constructor(apollo) {
        super(apollo);
        this.document = CollectionListDocument;
    }
}
CollectionListGQL.ɵfac = function CollectionListGQL_Factory(t) { return new (t || CollectionListGQL)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵinject"](apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Apollo"])); };
CollectionListGQL.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: CollectionListGQL, factory: CollectionListGQL.ɵfac, providedIn: 'root' });
const CollectionOverviewDocument = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    query CollectionOverview($toolId: String!, $collId: Int!) {
  me {
    tool(toolId: $toolId) {
      ...CollOverviewTool
    }
  }
}
    ${CollOverviewToolFragmentDoc}`;
class CollectionOverviewGQL extends apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Query"] {
    constructor(apollo) {
        super(apollo);
        this.document = CollectionOverviewDocument;
    }
}
CollectionOverviewGQL.ɵfac = function CollectionOverviewGQL_Factory(t) { return new (t || CollectionOverviewGQL)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵinject"](apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Apollo"])); };
CollectionOverviewGQL.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: CollectionOverviewGQL, factory: CollectionOverviewGQL.ɵfac, providedIn: 'root' });
const ExerciseOverviewDocument = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    query ExerciseOverview($toolId: String!, $collId: Int!, $exId: Int!) {
  me {
    tool(toolId: $toolId) {
      id
      name
      collection(collId: $collId) {
        collectionId
        title
        exercise(exId: $exId) {
          ...ExerciseOverview
        }
      }
    }
  }
}
    ${ExerciseOverviewFragmentDoc}`;
class ExerciseOverviewGQL extends apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Query"] {
    constructor(apollo) {
        super(apollo);
        this.document = ExerciseOverviewDocument;
    }
}
ExerciseOverviewGQL.ɵfac = function ExerciseOverviewGQL_Factory(t) { return new (t || ExerciseOverviewGQL)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵinject"](apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Apollo"])); };
ExerciseOverviewGQL.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: ExerciseOverviewGQL, factory: ExerciseOverviewGQL.ɵfac, providedIn: 'root' });
const ExerciseDocument = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    query Exercise($toolId: String!, $collId: Int!, $exId: Int!, $partId: String!) {
  me {
    tool(toolId: $toolId) {
      collection(collId: $collId) {
        exercise(exId: $exId) {
          ...ExerciseSolveFields
        }
      }
    }
  }
}
    ${ExerciseSolveFieldsFragmentDoc}`;
class ExerciseGQL extends apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Query"] {
    constructor(apollo) {
        super(apollo);
        this.document = ExerciseDocument;
    }
}
ExerciseGQL.ɵfac = function ExerciseGQL_Factory(t) { return new (t || ExerciseGQL)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵinject"](apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Apollo"])); };
ExerciseGQL.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: ExerciseGQL, factory: ExerciseGQL.ɵfac, providedIn: 'root' });
const RegisterDocument = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    mutation Register($username: String!, $firstPassword: String!, $secondPassword: String!) {
  register(
    registerValues: {username: $username, firstPassword: $firstPassword, secondPassword: $secondPassword}
  )
}
    `;
class RegisterGQL extends apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Mutation"] {
    constructor(apollo) {
        super(apollo);
        this.document = RegisterDocument;
    }
}
RegisterGQL.ɵfac = function RegisterGQL_Factory(t) { return new (t || RegisterGQL)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵinject"](apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Apollo"])); };
RegisterGQL.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: RegisterGQL, factory: RegisterGQL.ɵfac, providedIn: 'root' });
const LoginDocument = apollo_angular__WEBPACK_IMPORTED_MODULE_0__["gql"] `
    mutation Login($username: String!, $password: String!) {
  login(credentials: {username: $username, password: $password}) {
    ...LoggedInUserWithToken
  }
}
    ${LoggedInUserWithTokenFragmentDoc}`;
class LoginGQL extends apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Mutation"] {
    constructor(apollo) {
        super(apollo);
        this.document = LoginDocument;
    }
}
LoginGQL.ɵfac = function LoginGQL_Factory(t) { return new (t || LoginGQL)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵinject"](apollo_angular__WEBPACK_IMPORTED_MODULE_0__["Apollo"])); };
LoginGQL.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineInjectable"]({ token: LoginGQL, factory: LoginGQL.ɵfac, providedIn: 'root' });


/***/ }),

/***/ "oCnN":
/*!**************************************************************************!*\
  !*** ./src/app/user_management/register-form/register-form.component.ts ***!
  \**************************************************************************/
/*! exports provided: RegisterFormComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegisterFormComponent", function() { return RegisterFormComponent; });
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/forms */ "3Pt+");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! rxjs/operators */ "kU1M");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../_services/apollo_services */ "o20/");
/* harmony import */ var _services_authentication_service__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../_services/authentication.service */ "pW6c");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! @angular/common */ "ofXK");








function RegisterFormComponent_div_25_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 18);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](1, "span");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18n"](2, 19);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](3, "\u00A0");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](4, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](6, "\u00A0");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](7, "span");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18n"](8, 20);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](9, ". ");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtextInterpolate"](ctx_r0.registeredUsername);
} }
class RegisterFormComponent {
    constructor(router, registerGQL, authenticationService) {
        this.router = router;
        this.registerGQL = registerGQL;
        this.authenticationService = authenticationService;
        this.loading = false;
        this.submitted = false;
        if (this.authenticationService.currentUserValue) {
            // noinspection JSIgnoredPromiseFromCall
            this.router.navigate(['/']);
        }
    }
    ngOnInit() {
        this.registerForm = new _angular_forms__WEBPACK_IMPORTED_MODULE_0__["FormGroup"]({
            username: new _angular_forms__WEBPACK_IMPORTED_MODULE_0__["FormControl"]('', [_angular_forms__WEBPACK_IMPORTED_MODULE_0__["Validators"].required]),
            firstPassword: new _angular_forms__WEBPACK_IMPORTED_MODULE_0__["FormControl"]('', [_angular_forms__WEBPACK_IMPORTED_MODULE_0__["Validators"].required]),
            secondPassword: new _angular_forms__WEBPACK_IMPORTED_MODULE_0__["FormControl"]('', [_angular_forms__WEBPACK_IMPORTED_MODULE_0__["Validators"].required]),
        });
    }
    get f() {
        return this.registerForm.controls;
    }
    onSubmit() {
        this.submitted = true;
        const username = this.f.username.value;
        const firstPassword = this.f.firstPassword.value;
        const secondPassword = this.f.secondPassword.value;
        if (this.registerForm.invalid || firstPassword !== secondPassword) {
            alert('Daten sind nicht valide!');
            return;
        }
        this.loading = true;
        this.registerGQL
            .mutate({ username, firstPassword, secondPassword })
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_1__["first"])())
            .subscribe(({ data }) => {
            this.loading = false;
            this.registeredUsername = data.register;
        }, (err) => this.loading = false);
    }
}
RegisterFormComponent.ɵfac = function RegisterFormComponent_Factory(t) { return new (t || RegisterFormComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdirectiveInject"](_angular_router__WEBPACK_IMPORTED_MODULE_3__["Router"]), _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdirectiveInject"](_services_apollo_services__WEBPACK_IMPORTED_MODULE_4__["RegisterGQL"]), _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdirectiveInject"](_services_authentication_service__WEBPACK_IMPORTED_MODULE_5__["AuthenticationService"])); };
RegisterFormComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdefineComponent"]({ type: RegisterFormComponent, selectors: [["ng-component"]], decls: 31, vars: 2, consts: function () { let i18n_0; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_2736731364720267618$$SRC_APP_USER_MANAGEMENT_REGISTER_FORM_REGISTER_FORM_COMPONENT_TS_1 = goog.getMsg("Registrieren");
        i18n_0 = MSG_EXTERNAL_2736731364720267618$$SRC_APP_USER_MANAGEMENT_REGISTER_FORM_REGISTER_FORM_COMPONENT_TS_1;
    }
    else {
        i18n_0 = "Register";
    } let i18n_2; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_5643948441575626307$$SRC_APP_USER_MANAGEMENT_REGISTER_FORM_REGISTER_FORM_COMPONENT_TS_3 = goog.getMsg("Nutzername");
        i18n_2 = MSG_EXTERNAL_5643948441575626307$$SRC_APP_USER_MANAGEMENT_REGISTER_FORM_REGISTER_FORM_COMPONENT_TS_3;
    }
    else {
        i18n_2 = "Username";
    } let i18n_4; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_5643948441575626307$$SRC_APP_USER_MANAGEMENT_REGISTER_FORM_REGISTER_FORM_COMPONENT_TS_5 = goog.getMsg("Nutzername");
        i18n_4 = MSG_EXTERNAL_5643948441575626307$$SRC_APP_USER_MANAGEMENT_REGISTER_FORM_REGISTER_FORM_COMPONENT_TS_5;
    }
    else {
        i18n_4 = "Username";
    } let i18n_6; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_6057821346666596338$$SRC_APP_USER_MANAGEMENT_REGISTER_FORM_REGISTER_FORM_COMPONENT_TS_7 = goog.getMsg("Passwort");
        i18n_6 = MSG_EXTERNAL_6057821346666596338$$SRC_APP_USER_MANAGEMENT_REGISTER_FORM_REGISTER_FORM_COMPONENT_TS_7;
    }
    else {
        i18n_6 = "Password";
    } let i18n_8; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_6057821346666596338$$SRC_APP_USER_MANAGEMENT_REGISTER_FORM_REGISTER_FORM_COMPONENT_TS_9 = goog.getMsg("Passwort");
        i18n_8 = MSG_EXTERNAL_6057821346666596338$$SRC_APP_USER_MANAGEMENT_REGISTER_FORM_REGISTER_FORM_COMPONENT_TS_9;
    }
    else {
        i18n_8 = "Password";
    } let i18n_10; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_5242658385575909219$$SRC_APP_USER_MANAGEMENT_REGISTER_FORM_REGISTER_FORM_COMPONENT_TS_11 = goog.getMsg("Passwort wiederholen");
        i18n_10 = MSG_EXTERNAL_5242658385575909219$$SRC_APP_USER_MANAGEMENT_REGISTER_FORM_REGISTER_FORM_COMPONENT_TS_11;
    }
    else {
        i18n_10 = "Repeat password";
    } let i18n_12; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_5242658385575909219$$SRC_APP_USER_MANAGEMENT_REGISTER_FORM_REGISTER_FORM_COMPONENT_TS_13 = goog.getMsg("Passwort wiederholen");
        i18n_12 = MSG_EXTERNAL_5242658385575909219$$SRC_APP_USER_MANAGEMENT_REGISTER_FORM_REGISTER_FORM_COMPONENT_TS_13;
    }
    else {
        i18n_12 = "Repeat password";
    } let i18n_14; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_2736731364720267618$$SRC_APP_USER_MANAGEMENT_REGISTER_FORM_REGISTER_FORM_COMPONENT_TS_15 = goog.getMsg("Registrieren");
        i18n_14 = MSG_EXTERNAL_2736731364720267618$$SRC_APP_USER_MANAGEMENT_REGISTER_FORM_REGISTER_FORM_COMPONENT_TS_15;
    }
    else {
        i18n_14 = "Register";
    } let i18n_16; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_5138289382926356237$$SRC_APP_USER_MANAGEMENT_REGISTER_FORM_REGISTER_FORM_COMPONENT_TS__17 = goog.getMsg("Nutzer");
        i18n_16 = MSG_EXTERNAL_5138289382926356237$$SRC_APP_USER_MANAGEMENT_REGISTER_FORM_REGISTER_FORM_COMPONENT_TS__17;
    }
    else {
        i18n_16 = "User";
    } let i18n_18; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_7688846404428192340$$SRC_APP_USER_MANAGEMENT_REGISTER_FORM_REGISTER_FORM_COMPONENT_TS__19 = goog.getMsg("wurde erfolgreich registriert");
        i18n_18 = MSG_EXTERNAL_7688846404428192340$$SRC_APP_USER_MANAGEMENT_REGISTER_FORM_REGISTER_FORM_COMPONENT_TS__19;
    }
    else {
        i18n_18 = "was successfully registered";
    } return [[1, "container"], [1, "title", "is-3", "has-text-centered"], i18n_0, [3, "formGroup", "ngSubmit"], [1, "field"], ["for", "username", 1, "label"], i18n_2, [1, "control"], ["type", "text", "id", "username", "formControlName", "username", "required", "", "placeholder", i18n_4, "autofocus", "", 1, "input"], ["for", "firstPassword", 1, "label"], i18n_6, ["type", "password", "id", "firstPassword", "formControlName", "firstPassword", "placeholder", i18n_8, "required", "", 1, "input"], ["for", "secondPassword", 1, "label"], i18n_10, ["type", "password", "id", "secondPassword", "formControlName", "secondPassword", "placeholder", i18n_12, "required", "", 1, "input"], ["class", "notification", 4, "ngIf"], [1, "button", "is-link"], i18n_14, [1, "notification"], i18n_16, i18n_18]; }, template: function RegisterFormComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](1, "h1", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18n"](2, 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](3, "form", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("ngSubmit", function RegisterFormComponent_Template_form_ngSubmit_3_listener() { return ctx.onSubmit(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](4, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](5, "label", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](6, "span");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18n"](7, 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](8, ":");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](9, "div", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](10, "input", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](11, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](12, "label", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](13, "span");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18n"](14, 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](15, ":");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](16, "div", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](17, "input", 11);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](18, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](19, "label", 12);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](20, "span");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18n"](21, 13);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](22, ":");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](23, "div", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](24, "input", 14);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](25, RegisterFormComponent_div_25_Template, 10, 1, "div", 15);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](26, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](27, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](28, "div", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](29, "button", 16);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18n"](30, 17);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("formGroup", ctx.registerForm);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](22);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx.registeredUsername);
    } }, directives: [_angular_forms__WEBPACK_IMPORTED_MODULE_0__["ɵangular_packages_forms_forms_ba"], _angular_forms__WEBPACK_IMPORTED_MODULE_0__["NgControlStatusGroup"], _angular_forms__WEBPACK_IMPORTED_MODULE_0__["FormGroupDirective"], _angular_forms__WEBPACK_IMPORTED_MODULE_0__["DefaultValueAccessor"], _angular_forms__WEBPACK_IMPORTED_MODULE_0__["NgControlStatus"], _angular_forms__WEBPACK_IMPORTED_MODULE_0__["FormControlName"], _angular_forms__WEBPACK_IMPORTED_MODULE_0__["RequiredValidator"], _angular_common__WEBPACK_IMPORTED_MODULE_6__["NgIf"]], encapsulation: 2 });


/***/ }),

/***/ "oi/H":
/*!*************************************************************!*\
  !*** ./src/app/tools/random-tools/bool/_model/bool-node.ts ***!
  \*************************************************************/
/*! exports provided: calculateAssignments, BooleanNode, BooleanVariable, BooleanConstant, BooleanTrue, BooleanFalse, BooleanNot, BooleanBinaryNode, BooleanAnd, BooleanOr, BooleanNAnd, BooleanNOr, BooleanXOr, BooleanXNor, BooleanEquivalency, BooleanImplication, instantiateOperator */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "calculateAssignments", function() { return calculateAssignments; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BooleanNode", function() { return BooleanNode; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BooleanVariable", function() { return BooleanVariable; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BooleanConstant", function() { return BooleanConstant; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BooleanTrue", function() { return BooleanTrue; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BooleanFalse", function() { return BooleanFalse; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BooleanNot", function() { return BooleanNot; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BooleanBinaryNode", function() { return BooleanBinaryNode; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BooleanAnd", function() { return BooleanAnd; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BooleanOr", function() { return BooleanOr; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BooleanNAnd", function() { return BooleanNAnd; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BooleanNOr", function() { return BooleanNOr; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BooleanXOr", function() { return BooleanXOr; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BooleanXNor", function() { return BooleanXNor; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BooleanEquivalency", function() { return BooleanEquivalency; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BooleanImplication", function() { return BooleanImplication; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "instantiateOperator", function() { return instantiateOperator; });
/* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../../../helpers */ "Afm0");

const HTML_REPLACERS = new Map([
    [/nand/g, '&#x22bc;'], [/nor/g, '&#x22bd;'],
    [/xor/g, '&oplus;'], [/not /g, '&not;'],
    [/and/g, '&and;'], [/or/g, '&or;'],
    [/impl/g, '&rArr;'], [/equiv/g, '&hArr;']
]);
function calculateAssignments(variables) {
    let assignments = [];
    for (const variable of variables) {
        if (assignments.length === 0) {
            assignments = [
                new Map([[variable.variable, false]]),
                new Map([[variable.variable, true]])
            ];
        }
        else {
            assignments = Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["flatMapArray"])(assignments, (assignment) => [
                new Map([...assignment, [variable.variable, false]]),
                new Map([...assignment, [variable.variable, true]])
            ]);
        }
    }
    return assignments;
}
// Boolean nodes
class BooleanNode {
    getVariables() {
        if (!this.variables) {
            this.variables = this.calculateVariables()
                .sort((v1, v2) => v1.variable.charCodeAt(0) - v2.variable.charCodeAt(0));
        }
        return this.variables;
    }
    asHtmlString() {
        let base = this.asString();
        HTML_REPLACERS.forEach((replacer, replaced) => base = base.replace(replaced, replacer));
        return base;
    }
}
class BooleanVariable extends BooleanNode {
    constructor(variable) {
        super();
        this.variable = variable;
    }
    evaluate(assignments) {
        if (assignments.has(this.variable)) {
            return assignments.get(this.variable);
        }
        else {
            return undefined;
        }
    }
    calculateVariables() {
        return [this];
    }
    asString() {
        return this.variable;
    }
    getSubFormulas() {
        return [];
    }
}
class BooleanConstant extends BooleanNode {
    constructor(value) {
        super();
        this.value = value;
    }
    evaluate(assignments) {
        return this.value;
    }
    calculateVariables() {
        return [];
    }
    asString() {
        return this.value ? '1' : '0';
    }
    getSubFormulas() {
        return [];
    }
}
const BooleanTrue = new BooleanConstant(true);
const BooleanFalse = new BooleanConstant(false);
class BooleanNot extends BooleanNode {
    constructor(child) {
        super();
        this.child = child;
    }
    evaluate(assignments) {
        return !(this.child.evaluate(assignments));
    }
    calculateVariables() {
        return this.child.getVariables();
    }
    asString() {
        const childString = this.child instanceof BooleanBinaryNode ? '(' + this.child.asString() + ')' : this.child.asString();
        return 'not ' + childString;
    }
    getSubFormulas() {
        return [this.child];
    }
}
// Boolean binary nodes
class BooleanBinaryNode extends BooleanNode {
    constructor(left, right) {
        super();
        this.left = left;
        this.right = right;
    }
    calculateVariables() {
        const leftVars = this.left.getVariables();
        const rightVars = this.right.getVariables();
        return Array.from(new Set(leftVars.concat(rightVars)));
    }
    asString() {
        // FIXME: test parentheses!
        const leftChildString = this.left instanceof BooleanBinaryNode ? `(${this.left.asString()})` : this.left.asString();
        const rightChildString = this.right instanceof BooleanBinaryNode ? `(${this.right.asString()})` : this.right.asString();
        return leftChildString + ' ' + this.operator + ' ' + rightChildString;
    }
    evaluate(assignments) {
        return this.evalFunc(this.left.evaluate(assignments), this.right.evaluate(assignments));
    }
    getSubFormulas() {
        const maybeLeftSubFormula = (this.left instanceof BooleanVariable) ? [] : [this.left];
        const maybeRightSubFormula = (this.right instanceof BooleanVariable) ? [] : [this.right];
        return maybeLeftSubFormula.concat(maybeRightSubFormula);
    }
}
class BooleanAnd extends BooleanBinaryNode {
    constructor() {
        super(...arguments);
        this.operator = 'and';
    }
    evalFunc(a, b) {
        return a && b;
    }
}
class BooleanOr extends BooleanBinaryNode {
    constructor() {
        super(...arguments);
        this.operator = 'or';
    }
    evalFunc(a, b) {
        return a || b;
    }
}
class BooleanNAnd extends BooleanBinaryNode {
    constructor() {
        super(...arguments);
        this.operator = 'nand';
    }
    evalFunc(a, b) {
        return !(a && b);
    }
}
class BooleanNOr extends BooleanBinaryNode {
    constructor() {
        super(...arguments);
        this.operator = 'nor';
    }
    evalFunc(a, b) {
        return !(a || b);
    }
}
class BooleanXOr extends BooleanBinaryNode {
    constructor() {
        super(...arguments);
        this.operator = 'xor';
    }
    evalFunc(a, b) {
        return (a && !b) || (!a && b);
    }
}
class BooleanXNor extends BooleanBinaryNode {
    constructor() {
        super(...arguments);
        this.operator = 'xnor';
    }
    evalFunc(a, b) {
        return (!a || b) && (a || !b);
    }
}
class BooleanEquivalency extends BooleanBinaryNode {
    constructor() {
        super(...arguments);
        this.operator = 'equiv';
    }
    evalFunc(a, b) {
        return a === b;
    }
}
class BooleanImplication extends BooleanBinaryNode {
    constructor() {
        super(...arguments);
        this.operator = 'impl';
    }
    evalFunc(a, b) {
        return !a || b;
    }
}
function instantiateOperator(leftOp, opString, rightOp) {
    switch (opString) {
        case 'and':
            return new BooleanAnd(leftOp, rightOp);
        case 'or':
            return new BooleanOr(leftOp, rightOp);
        case 'xor':
            return new BooleanXOr(leftOp, rightOp);
        case 'nor':
            return new BooleanNOr(leftOp, rightOp);
        case 'xnor':
            return new BooleanXNor(leftOp, rightOp);
        case 'nand':
            return new BooleanNAnd(leftOp, rightOp);
        case 'equiv':
            return new BooleanEquivalency(leftOp, rightOp);
        case 'impl':
            return new BooleanImplication(leftOp, rightOp);
    }
}


/***/ }),

/***/ "pW6c":
/*!*****************************************************!*\
  !*** ./src/app/_services/authentication.service.ts ***!
  \*****************************************************/
/*! exports provided: getCurrentUser, AuthenticationService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "getCurrentUser", function() { return getCurrentUser; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AuthenticationService", function() { return AuthenticationService; });
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! rxjs */ "qCKp");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! rxjs/operators */ "kU1M");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common_http__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/common/http */ "tk/3");
/* harmony import */ var _apollo_services__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./apollo_services */ "o20/");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! @angular/router */ "tyNb");






const currentUserField = 'currentUser';
function getCurrentUser() {
    const json = localStorage.getItem(currentUserField);
    return json ? JSON.parse(json) : null;
}
class AuthenticationService {
    constructor(http, loginGQL, router) {
        this.http = http;
        this.loginGQL = loginGQL;
        this.router = router;
        this.currentUserSubject = new rxjs__WEBPACK_IMPORTED_MODULE_0__["BehaviorSubject"](getCurrentUser());
        this.currentUser = this.currentUserSubject.asObservable();
    }
    get currentUserValue() {
        return this.currentUserSubject.value;
    }
    activateLogin(user) {
        localStorage.setItem(currentUserField, JSON.stringify(user));
        this.currentUserSubject.next(user);
    }
    login(username, password) {
        return this.loginGQL
            .mutate({ username, password })
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_1__["map"])(({ data }) => {
            if (data.login) {
                this.activateLogin(data.login);
            }
            return data.login;
        }));
    }
    claimJsonWebToken(uuid) {
        return this.http.get(`/api/claimWebToken/${uuid}`)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_1__["tap"])((user) => this.activateLogin(user)));
    }
    logout() {
        localStorage.removeItem(currentUserField);
        this.currentUserSubject.next(null);
        // noinspection JSIgnoredPromiseFromCall
        this.router.navigate(['/loginForm']);
    }
}
AuthenticationService.ɵfac = function AuthenticationService_Factory(t) { return new (t || AuthenticationService)(_angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵinject"](_angular_common_http__WEBPACK_IMPORTED_MODULE_3__["HttpClient"]), _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵinject"](_apollo_services__WEBPACK_IMPORTED_MODULE_4__["LoginGQL"]), _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵinject"](_angular_router__WEBPACK_IMPORTED_MODULE_5__["Router"])); };
AuthenticationService.ɵprov = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdefineInjectable"]({ token: AuthenticationService, factory: AuthenticationService.ɵfac, providedIn: 'root' });


/***/ }),

/***/ "q8z/":
/*!*********************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/collection-overview/collection-overview.component.ts ***!
  \*********************************************************************************************/
/*! exports provided: CollectionOverviewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CollectionOverviewComponent", function() { return CollectionOverviewComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _shared_breadcrumbs_breadcrumbs_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../shared/breadcrumbs/breadcrumbs.component */ "lmDL");
/* harmony import */ var _components_exercise_link_card_exercise_link_card_component__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../_components/exercise-link-card/exercise-link-card.component */ "xeHz");






function CollectionOverviewComponent_ng_container_1_nav_5_li_4_Template(rf, ctx) { if (rf & 1) {
    const _r10 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵgetCurrentView"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "li");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "button", 14);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵlistener"]("click", function CollectionOverviewComponent_ng_container_1_nav_5_li_4_Template_button_click_1_listener() { _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵrestoreView"](_r10); const page_r8 = ctx.$implicit; const ctx_r9 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"](3); return ctx_r9.currentPage = page_r8; });
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const page_r8 = ctx.$implicit;
    const ctx_r7 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵclassProp"]("is-current", page_r8 === ctx_r7.currentPage);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"](" ", page_r8, " ");
} }
function CollectionOverviewComponent_ng_container_1_nav_5_Template(rf, ctx) { if (rf & 1) {
    const _r12 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵgetCurrentView"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "nav", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "button", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵlistener"]("click", function CollectionOverviewComponent_ng_container_1_nav_5_Template_button_click_1_listener() { _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵrestoreView"](_r12); const ctx_r11 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"](2); return ctx_r11.currentPage = ctx_r11.currentPage - 1; });
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2, " Vorherige ");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](3, "ul", 11);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](4, CollectionOverviewComponent_ng_container_1_nav_5_li_4_Template, 3, 3, "li", 12);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](5, "button", 13);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵlistener"]("click", function CollectionOverviewComponent_ng_container_1_nav_5_Template_button_click_5_listener() { _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵrestoreView"](_r12); const ctx_r13 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"](2); return ctx_r13.currentPage = ctx_r13.currentPage + 1; });
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](6, " N\u00E4chste ");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r5 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("disabled", ctx_r5.currentPage < 1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx_r5.pages);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("disabled", ctx_r5.currentPage >= ctx_r5.maxPage - 1);
} }
function CollectionOverviewComponent_ng_container_1_div_6_div_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 17);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](1, "it4all-exercise-link-card", 18);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const exercise_r15 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("exercise", exercise_r15);
} }
function CollectionOverviewComponent_ng_container_1_div_6_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 15);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](1, CollectionOverviewComponent_ng_container_1_div_6_div_1_Template, 2, 1, "div", 16);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r6 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx_r6.getExercisesPaginated());
} }
function CollectionOverviewComponent_ng_container_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "h1", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](3, "div", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](4, "it4all-breadcrumbs", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](5, CollectionOverviewComponent_ng_container_1_nav_5_Template, 7, 3, "nav", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](6, CollectionOverviewComponent_ng_container_1_div_6_Template, 2, 1, "div", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    const _r3 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵreference"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"]("Sammlung \"", ctx_r0.tool.collection.title, "\" - Aufgaben");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("parts", ctx_r0.breadCrumbs);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx_r0.paginationNeeded);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx_r0.exercises.length > 0)("ngIfElse", _r3);
} }
function CollectionOverviewComponent_ng_template_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 19);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, "Lade Daten...");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} }
function CollectionOverviewComponent_ng_template_4_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 20);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, " Es konnten keine Aufgaben f\u00FCr diese Sammlung gefunden werden! ");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} }
const SLICE_COUNT = 12;
// url => /tools/:toolId/collections/:collId
class CollectionOverviewComponent {
    constructor(route, collectionOverviewGQL) {
        this.route = route;
        this.collectionOverviewGQL = collectionOverviewGQL;
        this.paginationNeeded = false;
        this.maxPage = 0;
        this.currentPage = 0;
        this.pages = [];
    }
    ngOnInit() {
        this.sub = this.route.paramMap.subscribe((paramMap) => {
            const toolId = paramMap.get('toolId');
            const collId = parseInt(paramMap.get('collId'), 10);
            this.collectionOverviewGQL
                .watch({ toolId, collId })
                .valueChanges
                .subscribe(({ data }) => {
                this.collectionOverviewQuery = data;
                this.paginationNeeded = this.exercises.length > SLICE_COUNT;
                this.maxPage = Math.ceil(this.collectionOverviewQuery.me.tool.collection.exercises.length / SLICE_COUNT);
                this.pages = Array(this.maxPage).fill(0).map((value, index) => index);
            });
        });
    }
    ngOnDestroy() {
        this.sub.unsubscribe();
    }
    get tool() {
        return this.collectionOverviewQuery.me.tool;
    }
    get exercises() {
        return this.tool.collection.exercises;
    }
    getExercisesPaginated() {
        return this.exercises.slice(this.currentPage * SLICE_COUNT, (this.currentPage + 1) * SLICE_COUNT);
    }
    get breadCrumbs() {
        return [
            { routerLinkPart: '/', title: 'Tools' },
            { routerLinkPart: `tools/${this.tool.id}`, title: this.tool.name },
            { routerLinkPart: 'collections', title: 'Sammlungen' },
            { routerLinkPart: this.tool.collection.collectionId.toString(), title: this.tool.collection.title }
        ];
    }
}
CollectionOverviewComponent.ɵfac = function CollectionOverviewComponent_Factory(t) { return new (t || CollectionOverviewComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_angular_router__WEBPACK_IMPORTED_MODULE_1__["ActivatedRoute"]), _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_services_apollo_services__WEBPACK_IMPORTED_MODULE_2__["CollectionOverviewGQL"])); };
CollectionOverviewComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: CollectionOverviewComponent, selectors: [["ng-component"]], decls: 6, vars: 2, consts: [[1, "container"], [4, "ngIf", "ngIfElse"], ["loadingDataBlock", ""], ["noExercisesFoundBlock", ""], [1, "title", "is-3", "has-text-centered"], [1, "mb-3"], [3, "parts"], ["class", "pagination is-centered", "role", "navigation", "aria-label", "pagination", 4, "ngIf"], ["class", "columns is-multiline", 4, "ngIf", "ngIfElse"], ["role", "navigation", "aria-label", "pagination", 1, "pagination", "is-centered"], [1, "button", "pagination-previous", 3, "disabled", "click"], [1, "pagination-list"], [4, "ngFor", "ngForOf"], [1, "button", "pagination-next", 3, "disabled", "click"], [1, "button", "pagination-link", 3, "click"], [1, "columns", "is-multiline"], ["class", "column is-one-third-desktop is-half", 4, "ngFor", "ngForOf"], [1, "column", "is-one-third-desktop", "is-half"], [3, "exercise"], [1, "notification", "is-primary", "has-text-centered"], [1, "notification", "is-danger", "has-text-centered"]], template: function CollectionOverviewComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](1, CollectionOverviewComponent_ng_container_1_Template, 7, 5, "ng-container", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](2, CollectionOverviewComponent_ng_template_2_Template, 2, 0, "ng-template", null, 2, _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplateRefExtractor"]);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](4, CollectionOverviewComponent_ng_template_4_Template, 2, 0, "ng-template", null, 3, _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplateRefExtractor"]);
    } if (rf & 2) {
        const _r1 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵreference"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx.collectionOverviewQuery)("ngIfElse", _r1);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_3__["NgIf"], _shared_breadcrumbs_breadcrumbs_component__WEBPACK_IMPORTED_MODULE_4__["BreadcrumbsComponent"], _angular_common__WEBPACK_IMPORTED_MODULE_3__["NgForOf"], _components_exercise_link_card_exercise_link_card_component__WEBPACK_IMPORTED_MODULE_5__["ExerciseLinkCardComponent"]], encapsulation: 2 });


/***/ }),

/***/ "qhSi":
/*!*****************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/uml/_components/uml-impl-result/uml-impl-result.component.ts ***!
  \*****************************************************************************************************/
/*! exports provided: UmlImplResultComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlImplResultComponent", function() { return UmlImplResultComponent; });
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common */ "ofXK");



function UmlImplResultComponent_div_2_li_2_div_9_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "ul");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "li", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const m_r4 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]().$implicit;
    const ctx_r5 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngClass", ctx_r5.directionCorrect(m_r4) ? "has-text-dark-success" : "has-text-danger");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" Die Vererbungsrichtung war ", ctx_r5.directionCorrect(m_r4) ? "" : "nicht", " korrekt. ");
} }
function UmlImplResultComponent_div_2_li_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "li");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "div", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](2, " Die Vererbung von ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](3, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](5, " nach ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](6, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](7);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](8);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](9, UmlImplResultComponent_div_2_li_2_div_9_Template, 4, 2, "div", 1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const m_r4 = ctx.$implicit;
    const ctx_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngClass", ctx_r1.isCorrect(m_r4) ? "has-text-dark-success" : "has-text-danger");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](m_r4.userArg.subClass);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](m_r4.userArg.superClass);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" ist ", ctx_r1.isCorrect(m_r4) ? "" : "nicht", " korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", !ctx_r1.isCorrect(m_r4));
} }
function UmlImplResultComponent_div_2_li_3_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "li", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1, " Die Vererbung von ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](4, " nach ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](5, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](6);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](7, " ist falsch. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const mu_r7 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](mu_r7.subClass);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](mu_r7.superClass);
} }
function UmlImplResultComponent_div_2_li_4_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "li", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1, " Die Vererbung von ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](4, " nach ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](5, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](6);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](7, " fehlt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ms_r8 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](ms_r8.subClass);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](ms_r8.superClass);
} }
function UmlImplResultComponent_div_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "ul");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](2, UmlImplResultComponent_div_2_li_2_Template, 10, 5, "li", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](3, UmlImplResultComponent_div_2_li_3_Template, 8, 2, "li", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](4, UmlImplResultComponent_div_2_li_4_Template, 8, 2, "li", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx_r0.implResult.allMatches);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx_r0.implResult.notMatchedForUser);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx_r0.implResult.notMatchedForSample);
} }
class UmlImplResultComponent {
    constructor() {
        this.implResultSuccessful = false;
    }
    ngOnChanges(changes) {
        this.implResultSuccessful = this.implResult.allMatches.every(this.isCorrect);
    }
    isCorrect(m) {
        return m.matchType === _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["MatchType"].SuccessfulMatch;
    }
    directionCorrect(m) {
        return m.userArg.subClass === m.sampleArg.subClass;
    }
}
UmlImplResultComponent.ɵfac = function UmlImplResultComponent_Factory(t) { return new (t || UmlImplResultComponent)(); };
UmlImplResultComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineComponent"]({ type: UmlImplResultComponent, selectors: [["it4all-uml-impl-result"]], inputs: { implResult: "implResult" }, features: [_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵNgOnChangesFeature"]], decls: 3, vars: 3, consts: [[1, "subtitle", "is-5", 3, "ngClass"], ["class", "content", 4, "ngIf"], [1, "content"], [4, "ngFor", "ngForOf"], ["class", "has-text-danger", 4, "ngFor", "ngForOf"], [3, "ngClass"], [1, "has-text-danger"]], template: function UmlImplResultComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "h3", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](2, UmlImplResultComponent_div_2_Template, 5, 3, "div", 1);
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngClass", ctx.implResultSuccessful ? "has-text-dark-success" : "has-text-danger");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" Der Vergleich der Vererbungen war ", ctx.implResultSuccessful ? "" : "nicht", " erfolgreich. ");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", !ctx.implResultSuccessful);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_2__["NgClass"], _angular_common__WEBPACK_IMPORTED_MODULE_2__["NgIf"], _angular_common__WEBPACK_IMPORTED_MODULE_2__["NgForOf"]], encapsulation: 2 });


/***/ }),

/***/ "qv6m":
/*!*******************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/web/_components/html-task-result/html-task-result.component.ts ***!
  \*******************************************************************************************************/
/*! exports provided: HtmlTaskResultComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "HtmlTaskResultComponent", function() { return HtmlTaskResultComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _html_attribute_result_html_attribute_result_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../html-attribute-result/html-attribute-result.component */ "IASd");



function HtmlTaskResultComponent_ng_container_5_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](1, "it4all-html-attribute-result", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const attributeResult_r2 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("attributeResult", attributeResult_r2);
} }
function HtmlTaskResultComponent_ng_container_6_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "li", 0);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2, " Das Element sollte den Textinhalt ");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](3, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](5, " haben. Gefunden wurde ");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](6, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](7);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](8, ". ");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngClass", ctx_r1.htmlResult.textContentResult.isSuccessful ? "has-text-success" : "has-text-danger");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx_r1.htmlResult.textContentResult.awaitedContent);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx_r1.htmlResult.textContentResult.maybeFoundContent);
} }
class HtmlTaskResultComponent {
}
HtmlTaskResultComponent.ɵfac = function HtmlTaskResultComponent_Factory(t) { return new (t || HtmlTaskResultComponent)(); };
HtmlTaskResultComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: HtmlTaskResultComponent, selectors: [["it4all-html-task-result"]], inputs: { htmlResult: "htmlResult" }, decls: 7, vars: 9, consts: [[3, "ngClass"], [4, "ngFor", "ngForOf"], [4, "ngIf"], [3, "attributeResult"]], template: function HtmlTaskResultComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "span", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](2, "ul");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](3, "li", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](4);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](5, HtmlTaskResultComponent_ng_container_5_Template, 2, 1, "ng-container", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](6, HtmlTaskResultComponent_ng_container_6_Template, 9, 3, "ng-container", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngClass", ctx.htmlResult.isSuccessful ? "has-text-success" : "has-text-danger");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate4"](" (", ctx.htmlResult.points, " / ", ctx.htmlResult.maxPoints, ") Teilaufgabe ", ctx.htmlResult.id, " ist ", ctx.htmlResult.isSuccessful ? "" : "nicht", " korrekt: ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngClass", ctx.htmlResult.elementFound ? "has-text-success" : "has-text-danger");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"](" Das Element konnte ", ctx.htmlResult.elementFound ? "" : "nicht", " gefunden werden! ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx.htmlResult.attributeResults);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx.htmlResult.textContentResult);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_1__["NgClass"], _angular_common__WEBPACK_IMPORTED_MODULE_1__["NgForOf"], _angular_common__WEBPACK_IMPORTED_MODULE_1__["NgIf"], _html_attribute_result_html_attribute_result_component__WEBPACK_IMPORTED_MODULE_2__["HtmlAttributeResultComponent"]], encapsulation: 2 });


/***/ }),

/***/ "rqf4":
/*!*******************************************************************!*\
  !*** ./src/app/shared/solution-saved/solution-saved.component.ts ***!
  \*******************************************************************/
/*! exports provided: SolutionSavedComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SolutionSavedComponent", function() { return SolutionSavedComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/common */ "ofXK");


class SolutionSavedComponent {
}
SolutionSavedComponent.ɵfac = function SolutionSavedComponent_Factory(t) { return new (t || SolutionSavedComponent)(); };
SolutionSavedComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: SolutionSavedComponent, selectors: [["it4all-solution-saved"]], inputs: { solutionSaved: "solutionSaved" }, decls: 4, vars: 2, consts: [[1, "notification", "is-light-grey", 3, "ngClass"], [3, "innerHTML"]], template: function SolutionSavedComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, " \u2714 Ihre L\u00F6sung wurde ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](2, "span", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](3, " gespeichert. ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngClass", ctx.solutionSaved ? "has-text-dark-success" : "has-text-danger");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("innerHTML", ctx.solutionSaved ? "" : "<b>nicht</b>", _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵsanitizeHtml"]);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_1__["NgClass"]], encapsulation: 2 });


/***/ }),

/***/ "sArB":
/*!*************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/uml/uml-diagram-drawing/uml-diagram-drawing.component.ts ***!
  \*************************************************************************************************/
/*! exports provided: UmlDiagramDrawingComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlDiagramDrawingComponent", function() { return UmlDiagramDrawingComponent; });
/* harmony import */ var _model_joint_class_diag_elements__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../_model/joint-class-diag-elements */ "/fbC");
/* harmony import */ var _uml_tools__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../uml-tools */ "PyDI");
/* harmony import */ var _model_uml_consts__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../_model/uml-consts */ "y3Rt");
/* harmony import */ var _model_class_diag_helpers__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../_model/class-diag-helpers */ "kRoC");
/* harmony import */ var _model_my_uml_interfaces__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../_model/my-uml-interfaces */ "LvJS");
/* harmony import */ var _helpers_component_with_exercise_directive__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../_helpers/component-with-exercise.directive */ "TRIe");
/* harmony import */ var _environments_environment__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../../../../../environments/environment */ "AytR");
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../../../_services/apollo_services */ "o20/");
/* harmony import */ var jointjs__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! jointjs */ "iuCI");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _services_dexie_service__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ../../../../_services/dexie.service */ "4di/");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _shared_tabs_tabs_component__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ../../../../shared/tabs/tabs.component */ "b4kd");
/* harmony import */ var _shared_tab_tab_component__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ../../../../shared/tab/tab.component */ "4YYW");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _shared_solution_saved_solution_saved_component__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! ../../../../shared/solution-saved/solution-saved.component */ "rqf4");
/* harmony import */ var _components_uml_diag_drawing_correction_uml_diag_drawing_correction_component__WEBPACK_IMPORTED_MODULE_16__ = __webpack_require__(/*! ../_components/uml-diag-drawing-correction/uml-diag-drawing-correction.component */ "x5V5");
/* harmony import */ var _components_uml_class_edit_uml_class_edit_component__WEBPACK_IMPORTED_MODULE_17__ = __webpack_require__(/*! ../_components/uml-class-edit/uml-class-edit.component */ "V+Il");
/* harmony import */ var _components_uml_assoc_edit_uml_assoc_edit_component__WEBPACK_IMPORTED_MODULE_18__ = __webpack_require__(/*! ../_components/uml-assoc-edit/uml-assoc-edit.component */ "UZ51");




















function UmlDiagramDrawingComponent_ng_container_4_Template(rf, ctx) { if (rf & 1) {
    const _r9 = _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵgetCurrentView"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelement"](1, "br");
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](2, "div", 1);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](3, "div", 16);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](4, "label", 17);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtext"](5, "Alte L\u00F6sung importieren:");
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](6, "div", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](7, "input", 18);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵlistener"]("change", function UmlDiagramDrawingComponent_ng_container_4_Template_input_change_7_listener($event) { _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵrestoreView"](_r9); const ctx_r8 = _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵnextContext"](); return ctx_r8.readFile($event.target.files); });
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementContainerEnd"]();
} }
function UmlDiagramDrawingComponent_ng_container_9_span_1_Template(rf, ctx) { if (rf & 1) {
    const _r15 = _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵgetCurrentView"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](0, "span", 20);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵlistener"]("click", function UmlDiagramDrawingComponent_ng_container_9_span_1_Template_span_click_0_listener() { _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵrestoreView"](_r15); const tp_r10 = _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵnextContext"]().$implicit; const ctx_r13 = _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵnextContext"](); return ctx_r13.createClass(tp_r10.selectableClass); });
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
} if (rf & 2) {
    const tp_r10 = _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵnextContext"]().$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtextInterpolate"](tp_r10.text);
} }
function UmlDiagramDrawingComponent_ng_container_9_span_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](0, "span");
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
} if (rf & 2) {
    const tp_r10 = _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵnextContext"]().$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtextInterpolate"](tp_r10.text);
} }
function UmlDiagramDrawingComponent_ng_container_9_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtemplate"](1, UmlDiagramDrawingComponent_ng_container_9_span_1_Template, 2, 1, "span", 19);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtemplate"](2, UmlDiagramDrawingComponent_ng_container_9_span_2_Template, 2, 1, "span", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const tp_r10 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵproperty"]("ngIf", tp_r10.selectableClass);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵproperty"]("ngIf", !tp_r10.selectableClass);
} }
function UmlDiagramDrawingComponent_div_11_Template(rf, ctx) { if (rf & 1) {
    const _r20 = _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵgetCurrentView"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](0, "div", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](1, "button", 21);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵlistener"]("click", function UmlDiagramDrawingComponent_div_11_Template_button_click_1_listener() { _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵrestoreView"](_r20); const x_r18 = ctx.$implicit; const ctx_r19 = _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵnextContext"](); return ctx_r19.toggle(x_r18); });
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
} if (rf & 2) {
    const x_r18 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵproperty"]("ngClass", x_r18.selected ? "is-link" : "")("disabled", x_r18.disabled);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtextInterpolate1"](" ", x_r18.name, " ");
} }
function UmlDiagramDrawingComponent_it4all_solution_saved_23_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelement"](0, "it4all-solution-saved", 22);
} if (rf & 2) {
    const ctx_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵproperty"]("solutionSaved", ctx_r3.correctionResult.solutionSaved);
} }
function UmlDiagramDrawingComponent_div_24_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](0, "div", 23);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](1, "div", 24);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtext"](2, "Es gab einen Fehler bei der Korrektur:");
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](3, "div", 25);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](4, "pre");
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtext"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r4 = _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtextInterpolate"](ctx_r4.queryError.message);
} }
function UmlDiagramDrawingComponent_it4all_uml_diag_drawing_correction_25_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelement"](0, "it4all-uml-diag-drawing-correction", 26);
} if (rf & 2) {
    const ctx_r5 = _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵproperty"]("result", ctx_r5.result);
} }
function UmlDiagramDrawingComponent_ng_container_26_Template(rf, ctx) { if (rf & 1) {
    const _r22 = _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵgetCurrentView"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelement"](1, "hr");
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](2, "it4all-uml-class-edit", 27);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵlistener"]("cancel", function UmlDiagramDrawingComponent_ng_container_26_Template_it4all_uml_class_edit_cancel_2_listener() { _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵrestoreView"](_r22); const ctx_r21 = _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵnextContext"](); return ctx_r21.editedClass = undefined; });
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r6 = _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵproperty"]("editedClass", ctx_r6.editedClass);
} }
function UmlDiagramDrawingComponent_ng_container_27_Template(rf, ctx) { if (rf & 1) {
    const _r24 = _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵgetCurrentView"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelement"](1, "hr");
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](2, "it4all-uml-assoc-edit", 28);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵlistener"]("cancel", function UmlDiagramDrawingComponent_ng_container_27_Template_it4all_uml_assoc_edit_cancel_2_listener() { _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵrestoreView"](_r24); const ctx_r23 = _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵnextContext"](); return ctx_r23.editedAssociation = undefined; });
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r7 = _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵproperty"]("editedAssociation", ctx_r7.editedAssociation);
} }
const _c0 = function (a1) { return ["..", a1]; };
const _c1 = function () { return ["../.."]; };
var CreatableClassDiagramObject;
(function (CreatableClassDiagramObject) {
    CreatableClassDiagramObject[CreatableClassDiagramObject["Class"] = 0] = "Class";
    CreatableClassDiagramObject[CreatableClassDiagramObject["Association"] = 1] = "Association";
    CreatableClassDiagramObject[CreatableClassDiagramObject["Implementation"] = 2] = "Implementation";
})(CreatableClassDiagramObject || (CreatableClassDiagramObject = {}));
class UmlDiagramDrawingComponent extends _helpers_component_with_exercise_directive__WEBPACK_IMPORTED_MODULE_5__["ComponentWithExerciseDirective"] {
    constructor(umlCorrectionGQL, dexieService) {
        super(umlCorrectionGQL, dexieService);
        this.nextPartId = Object(_uml_tools__WEBPACK_IMPORTED_MODULE_1__["getIdForUmlExPart"])(_services_apollo_services__WEBPACK_IMPORTED_MODULE_7__["UmlExPart"].MemberAllocation);
        this.graph = new jointjs__WEBPACK_IMPORTED_MODULE_8__["dia"].Graph();
        this.corrected = false;
        this.debug = !_environments_environment__WEBPACK_IMPORTED_MODULE_6__["environment"].production;
    }
    ngOnInit() {
        this.partId = Object(_uml_tools__WEBPACK_IMPORTED_MODULE_1__["getIdForUmlExPart"])(this.contentFragment.umlPart);
        this.withHelp = this.contentFragment.umlPart === _services_apollo_services__WEBPACK_IMPORTED_MODULE_7__["UmlExPart"].DiagramDrawingHelp;
        this.creatableClassDiagramObjects = [
            { name: 'Klasse', key: CreatableClassDiagramObject.Class, selected: false, disabled: this.withHelp },
            { name: 'Assoziation', key: CreatableClassDiagramObject.Association, selected: false },
            { name: 'Vererbung', key: CreatableClassDiagramObject.Implementation, selected: false }
        ];
        const { selectableClasses, textParts } = Object(_uml_tools__WEBPACK_IMPORTED_MODULE_1__["getUmlExerciseTextParts"])(this.exerciseFragment, this.contentFragment);
        this.selectableClasses = selectableClasses;
        this.umlExerciseTextParts = textParts;
        const paperJQueryElement = $('#myPaper');
        this.paper = new jointjs__WEBPACK_IMPORTED_MODULE_8__["dia"].Paper({
            el: paperJQueryElement, model: this.graph,
            width: Math.floor(paperJQueryElement.width()), height: _model_uml_consts__WEBPACK_IMPORTED_MODULE_2__["PAPER_HEIGHT"],
            gridSize: _model_uml_consts__WEBPACK_IMPORTED_MODULE_2__["GRID_SIZE"], drawGrid: { name: 'dot' }
        });
        this.createPaperEvents(this.paper);
        // load classes
        const declaration = this.contentFragment.umlSampleSolutions[0];
        this.loadOldSolutionAbstract(this.exerciseFragment, this.partId, (oldSol) => this.loadClassDiagram(oldSol), () => this.loadClassDiagram(declaration, false));
    }
    loadClassDiagram(cd, loadAttributesAndMethods = true) {
        for (const clazz of cd.classes) {
            const attributes = loadAttributesAndMethods ? clazz.attributes : [];
            const methods = loadAttributesAndMethods ? clazz.methods : [];
            Object(_model_class_diag_helpers__WEBPACK_IMPORTED_MODULE_3__["addClassToGraph"])(clazz.name, this.paper, attributes, methods);
        }
        const allCells = this.graph.getCells().filter(_model_joint_class_diag_elements__WEBPACK_IMPORTED_MODULE_0__["isMyJointClass"]);
        if (loadAttributesAndMethods) {
            for (const assoc of cd.associations) {
                Object(_model_class_diag_helpers__WEBPACK_IMPORTED_MODULE_3__["addAssociationToGraph"])(allCells.find((c) => c.getClassName() === assoc.firstEnd), assoc.firstMult === 'UNBOUND' ? '*' : '1', allCells.find((c) => c.getClassName() === assoc.secondEnd), assoc.secondMult === 'UNBOUND' ? '*' : '1', this.graph);
            }
            for (const impl of cd.implementations) {
                Object(_model_class_diag_helpers__WEBPACK_IMPORTED_MODULE_3__["addImplementationToGraph"])(allCells.find((c) => c.getClassName() === impl.subClass), allCells.find((c) => c.getClassName() === impl.superClass), this.graph);
            }
        }
    }
    createPaperEvents(paper) {
        paper.on('blank:pointerclick', (evt, x, y) => {
            const selectedObjectToCreate = this.creatableClassDiagramObjects.find((scdo) => scdo.selected);
            if (selectedObjectToCreate && CreatableClassDiagramObject.Class === selectedObjectToCreate.key) {
                Object(_model_class_diag_helpers__WEBPACK_IMPORTED_MODULE_3__["addClassToGraph"])('Klasse 1', paper, [], [], { x, y });
            }
        });
        paper.on('cell:pointerclick', (cellView, event /*, x: number, y: number*/) => {
            if (cellView.model instanceof _model_joint_class_diag_elements__WEBPACK_IMPORTED_MODULE_0__["MyJointClass"]) {
                const selectedObjectToCreate = this.creatableClassDiagramObjects.find((scdo) => scdo.selected);
                if (!this.markedClass) {
                    this.markedClass = cellView.model;
                    cellView.highlight();
                }
                else if (this.markedClass === cellView.model) {
                    this.markedClass = undefined;
                    cellView.unhighlight();
                }
                else if (selectedObjectToCreate) {
                    if (selectedObjectToCreate.key === CreatableClassDiagramObject.Association) {
                        Object(_model_class_diag_helpers__WEBPACK_IMPORTED_MODULE_3__["addAssociationToGraph"])(this.markedClass, '*', cellView.model, '*', this.graph);
                    }
                    else if (selectedObjectToCreate.key === CreatableClassDiagramObject.Implementation) {
                        Object(_model_class_diag_helpers__WEBPACK_IMPORTED_MODULE_3__["addImplementationToGraph"])(this.markedClass, cellView.model, this.graph);
                    }
                    this.markedClass.findView(paper).unhighlight();
                    this.markedClass = undefined;
                }
            }
            else {
                // console.info(cellView.model);
                event.preventDefault();
                event.stopImmediatePropagation();
                event.stopPropagation();
            }
        });
        paper.on('cell:contextmenu', (cellView /*, event: joint.dia.Event, x: number, y: number*/) => {
            if (this.withHelp) {
                if (cellView.model instanceof _model_joint_class_diag_elements__WEBPACK_IMPORTED_MODULE_0__["MyJointClass"]) {
                    // Cannot change classes
                    return;
                }
                else if (cellView.model instanceof jointjs__WEBPACK_IMPORTED_MODULE_8__["shapes"].uml.Association) {
                    // Change association...
                    this.editedAssociation = cellView.model;
                }
            }
            else if (cellView.model instanceof _model_joint_class_diag_elements__WEBPACK_IMPORTED_MODULE_0__["MyJointClass"]) {
                if (!this.editedClass) {
                    this.editedClass = cellView.model;
                }
                else {
                    this.editedClass = undefined;
                }
            }
            else {
                console.log(cellView.model);
            }
        });
    }
    createClass(selectableClass) {
        if (this.withHelp || selectableClass.selected) {
            // Class is already in graph!
            return;
        }
        selectableClass.selected = true;
        Object(_model_class_diag_helpers__WEBPACK_IMPORTED_MODULE_3__["addClassToGraph"])(selectableClass.name, this.paper);
    }
    toggle(toCreate) {
        this.creatableClassDiagramObjects.forEach((scdo) => scdo.selected = (scdo.key === toCreate.key) ? !scdo.selected : false);
    }
    readFile(files) {
        const fileReader = new FileReader();
        fileReader.onload = ((pe) => {
            const read = pe.target.result;
            const loaded = JSON.parse(read);
            for (const clazz of loaded.classes) {
                Object(_model_class_diag_helpers__WEBPACK_IMPORTED_MODULE_3__["addClassToGraph"])(clazz.name, this.paper, clazz.attributes || [], clazz.methods || []);
            }
            const allCells = this.graph.getCells().filter(_model_joint_class_diag_elements__WEBPACK_IMPORTED_MODULE_0__["isMyJointClass"]);
            for (const impl of loaded.implementations) {
                Object(_model_class_diag_helpers__WEBPACK_IMPORTED_MODULE_3__["addImplementationToGraph"])(allCells.find((c) => c.getClassName() === impl.subClass), allCells.find((c) => c.getClassName() === impl.superClass), this.graph);
            }
        });
        fileReader.readAsText(files.item(0));
    }
    // Results
    get correctionResult() {
        var _a, _b;
        return (_b = (_a = this.resultQuery) === null || _a === void 0 ? void 0 : _a.me.umlExercise) === null || _b === void 0 ? void 0 : _b.correct;
    }
    get result() {
        var _a;
        return (_a = this.correctionResult) === null || _a === void 0 ? void 0 : _a.result;
    }
    // Correction
    getSolution() {
        return {
            classes: this.graph.getCells().filter(_model_joint_class_diag_elements__WEBPACK_IMPORTED_MODULE_0__["isMyJointClass"]).map((cell) => cell.getAsUmlClass()),
            associations: this.graph.getLinks().filter(_model_joint_class_diag_elements__WEBPACK_IMPORTED_MODULE_0__["isAssociation"]).map(_model_my_uml_interfaces__WEBPACK_IMPORTED_MODULE_4__["umlAssocfromConnection"]),
            implementations: this.graph.getLinks().filter(_model_joint_class_diag_elements__WEBPACK_IMPORTED_MODULE_0__["isImplementation"]).map(_model_my_uml_interfaces__WEBPACK_IMPORTED_MODULE_4__["umlImplfromConnection"])
        };
    }
    getMutationQueryVariables() {
        return {
            exId: this.exerciseFragment.exerciseId,
            collId: this.exerciseFragment.collectionId,
            solution: this.getSolution(),
            part: this.contentFragment.umlPart,
        };
    }
    correct() {
        super.correctAbstract(this.exerciseFragment, this.partId);
        this.corrected = true;
    }
}
UmlDiagramDrawingComponent.ɵfac = function UmlDiagramDrawingComponent_Factory(t) { return new (t || UmlDiagramDrawingComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵdirectiveInject"](_services_apollo_services__WEBPACK_IMPORTED_MODULE_7__["UmlCorrectionGQL"]), _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵdirectiveInject"](_services_dexie_service__WEBPACK_IMPORTED_MODULE_10__["DexieService"])); };
UmlDiagramDrawingComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵdefineComponent"]({ type: UmlDiagramDrawingComponent, selectors: [["it4all-uml-diagram-drawing"]], inputs: { exerciseFragment: "exerciseFragment", contentFragment: "contentFragment" }, features: [_angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵInheritDefinitionFeature"]], decls: 28, vars: 19, consts: [[1, "container", "is-fluid"], [1, "columns"], [1, "column", "is-three-fifths"], ["id", "myPaper"], [4, "ngIf"], [1, "column"], [3, "title"], [1, "notification", "is-light-grey"], [4, "ngFor", "ngForOf"], ["class", "column", 4, "ngFor", "ngForOf"], [1, "button", "is-link", "is-fullwidth", 3, "click"], [1, "button", "is-fullwidth", 3, "routerLink", "disabled", "ngClass"], [1, "button", "is-dark", "is-fullwidth", 3, "routerLink"], [3, "solutionSaved", 4, "ngIf"], ["class", "message", 4, "ngIf"], [3, "result", 4, "ngIf"], [1, "column", "is-one-quarter-desktop"], ["for", "importFile", 1, "label"], ["type", "file", "id", "importFile", 1, "input", "is-fullwidth", 3, "change"], [3, "click", 4, "ngIf"], [3, "click"], [1, "button", "is-fullwidth", 3, "ngClass", "disabled", "click"], [3, "solutionSaved"], [1, "message"], [1, "message-header"], [1, "message-body"], [3, "result"], [3, "editedClass", "cancel"], [3, "editedAssociation", "cancel"]], template: function UmlDiagramDrawingComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](1, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](2, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelement"](3, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtemplate"](4, UmlDiagramDrawingComponent_ng_container_4_Template, 8, 0, "ng-container", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](5, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](6, "it4all-tabs");
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](7, "it4all-tab", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](8, "div", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtemplate"](9, UmlDiagramDrawingComponent_ng_container_9_Template, 3, 2, "ng-container", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](10, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtemplate"](11, UmlDiagramDrawingComponent_div_11_Template, 3, 3, "div", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](12, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](13, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](14, "button", 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵlistener"]("click", function UmlDiagramDrawingComponent_Template_button_click_14_listener() { return ctx.correct(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtext"](15, " Korrektur ");
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](16, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](17, "button", 11);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtext"](18, " Zum n\u00E4chsten Aufgabenteil ");
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](19, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](20, "a", 12);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtext"](21, "Bearbeiten beenden");
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementStart"](22, "it4all-tab", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtemplate"](23, UmlDiagramDrawingComponent_it4all_solution_saved_23_Template, 1, 1, "it4all-solution-saved", 13);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtemplate"](24, UmlDiagramDrawingComponent_div_24_Template, 6, 1, "div", 14);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtemplate"](25, UmlDiagramDrawingComponent_it4all_uml_diag_drawing_correction_25_Template, 1, 1, "it4all-uml-diag-drawing-correction", 15);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtemplate"](26, UmlDiagramDrawingComponent_ng_container_26_Template, 3, 1, "ng-container", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵtemplate"](27, UmlDiagramDrawingComponent_ng_container_27_Template, 3, 1, "ng-container", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](4);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵproperty"]("ngIf", ctx.debug);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵproperty"]("title", ctx.exerciseTextTabTitle);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵproperty"]("ngForOf", ctx.umlExerciseTextParts);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵproperty"]("ngForOf", ctx.creatableClassDiagramObjects);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵclassProp"]("is-loading", ctx.isCorrecting);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵproperty"]("routerLink", _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵpureFunction1"](16, _c0, ctx.nextPartId))("disabled", !ctx.corrected)("ngClass", ctx.corrected ? "is-link" : "is-dark");
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵproperty"]("routerLink", _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵpureFunction0"](18, _c1));
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵproperty"]("title", ctx.correctionTabTitle);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵproperty"]("ngIf", ctx.result);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵproperty"]("ngIf", ctx.queryError);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵproperty"]("ngIf", ctx.resultQuery);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵproperty"]("ngIf", ctx.editedClass);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_9__["ɵɵproperty"]("ngIf", ctx.editedAssociation);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_11__["NgIf"], _shared_tabs_tabs_component__WEBPACK_IMPORTED_MODULE_12__["TabsComponent"], _shared_tab_tab_component__WEBPACK_IMPORTED_MODULE_13__["TabComponent"], _angular_common__WEBPACK_IMPORTED_MODULE_11__["NgForOf"], _angular_router__WEBPACK_IMPORTED_MODULE_14__["RouterLink"], _angular_common__WEBPACK_IMPORTED_MODULE_11__["NgClass"], _angular_router__WEBPACK_IMPORTED_MODULE_14__["RouterLinkWithHref"], _shared_solution_saved_solution_saved_component__WEBPACK_IMPORTED_MODULE_15__["SolutionSavedComponent"], _components_uml_diag_drawing_correction_uml_diag_drawing_correction_component__WEBPACK_IMPORTED_MODULE_16__["UmlDiagDrawingCorrectionComponent"], _components_uml_class_edit_uml_class_edit_component__WEBPACK_IMPORTED_MODULE_17__["UmlClassEditComponent"], _components_uml_assoc_edit_uml_assoc_edit_component__WEBPACK_IMPORTED_MODULE_18__["UmlAssocEditComponent"]], styles: ["#myPaper[_ngcontent-%COMP%] {\n      border: 1px solid slategrey;\n      border-radius: 5px;\n    }"] });


/***/ }),

/***/ "t2v4":
/*!***********************************************************************!*\
  !*** ./src/app/tools/collection-tools/exercise/exercise.component.ts ***!
  \***********************************************************************/
/*! exports provided: ExerciseComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExerciseComponent", function() { return ExerciseComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _ebnf_ebnf_exercise_ebnf_exercise_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../ebnf/ebnf-exercise/ebnf-exercise.component */ "/bl0");
/* harmony import */ var _flask_flask_exercise_flask_exercise_component__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../flask/flask-exercise/flask-exercise.component */ "XO5P");
/* harmony import */ var _programming_programming_exercise_programming_exercise_component__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../programming/programming-exercise/programming-exercise.component */ "EK2f");
/* harmony import */ var _regex_regex_exercise_regex_exercise_component__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../regex/regex-exercise/regex-exercise.component */ "Brp2");
/* harmony import */ var _sql_sql_exercise_sql_exercise_component__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../sql/sql-exercise/sql-exercise.component */ "+Iia");
/* harmony import */ var _uml_uml_exercise_uml_exercise_component__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ../uml/uml-exercise/uml-exercise.component */ "ghNm");
/* harmony import */ var _web_web_exercise_web_exercise_component__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ../web/web-exercise/web-exercise.component */ "iKdM");
/* harmony import */ var _xml_xml_exercise_xml_exercise_component__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ../xml/xml-exercise/xml-exercise.component */ "C5uP");












function ExerciseComponent_ng_container_0_it4all_ebnf_exercise_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](0, "it4all-ebnf-exercise", 3);
} if (rf & 2) {
    const ctx_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("exerciseFragment", ctx_r3.exercise)("contentFragment", ctx_r3.ebnfExerciseContent);
} }
function ExerciseComponent_ng_container_0_it4all_flask_exercise_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](0, "it4all-flask-exercise", 3);
} if (rf & 2) {
    const ctx_r4 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("exerciseFragment", ctx_r4.exercise)("contentFragment", ctx_r4.flaskExerciseContent);
} }
function ExerciseComponent_ng_container_0_it4all_programming_exercise_3_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](0, "it4all-programming-exercise", 3);
} if (rf & 2) {
    const ctx_r5 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("exerciseFragment", ctx_r5.exercise)("contentFragment", ctx_r5.programmingExerciseContent);
} }
function ExerciseComponent_ng_container_0_it4all_regex_exercise_4_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](0, "it4all-regex-exercise", 3);
} if (rf & 2) {
    const ctx_r6 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("exerciseFragment", ctx_r6.exercise)("contentFragment", ctx_r6.regexExerciseContent);
} }
function ExerciseComponent_ng_container_0_it4all_sql_exercise_5_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](0, "it4all-sql-exercise", 3);
} if (rf & 2) {
    const ctx_r7 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("exerciseFragment", ctx_r7.exercise)("contentFragment", ctx_r7.sqlExerciseContent);
} }
function ExerciseComponent_ng_container_0_it4all_uml_exercise_6_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](0, "it4all-uml-exercise", 3);
} if (rf & 2) {
    const ctx_r8 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("exerciseFragment", ctx_r8.exercise)("contentFragment", ctx_r8.umlExerciseContent);
} }
function ExerciseComponent_ng_container_0_it4all_web_exercise_7_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](0, "it4all-web-exercise", 3);
} if (rf & 2) {
    const ctx_r9 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("exerciseFragment", ctx_r9.exercise)("contentFragment", ctx_r9.webExerciseContent);
} }
function ExerciseComponent_ng_container_0_it4all_xml_exercise_8_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](0, "it4all-xml-exercise", 3);
} if (rf & 2) {
    const ctx_r10 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("exerciseFragment", ctx_r10.exercise)("contentFragment", ctx_r10.xmlExerciseContent);
} }
function ExerciseComponent_ng_container_0_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](1, ExerciseComponent_ng_container_0_it4all_ebnf_exercise_1_Template, 1, 2, "it4all-ebnf-exercise", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](2, ExerciseComponent_ng_container_0_it4all_flask_exercise_2_Template, 1, 2, "it4all-flask-exercise", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](3, ExerciseComponent_ng_container_0_it4all_programming_exercise_3_Template, 1, 2, "it4all-programming-exercise", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](4, ExerciseComponent_ng_container_0_it4all_regex_exercise_4_Template, 1, 2, "it4all-regex-exercise", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](5, ExerciseComponent_ng_container_0_it4all_sql_exercise_5_Template, 1, 2, "it4all-sql-exercise", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](6, ExerciseComponent_ng_container_0_it4all_uml_exercise_6_Template, 1, 2, "it4all-uml-exercise", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](7, ExerciseComponent_ng_container_0_it4all_web_exercise_7_Template, 1, 2, "it4all-web-exercise", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](8, ExerciseComponent_ng_container_0_it4all_xml_exercise_8_Template, 1, 2, "it4all-xml-exercise", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx_r0.ebnfExerciseContent);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx_r0.flaskExerciseContent);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx_r0.programmingExerciseContent);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx_r0.regexExerciseContent);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx_r0.sqlExerciseContent);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx_r0.umlExerciseContent);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx_r0.webExerciseContent);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx_r0.xmlExerciseContent);
} }
function ExerciseComponent_ng_template_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, "Lade Aufgabe...");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} }
class ExerciseComponent {
    constructor(route, exerciseGQL) {
        this.route = route;
        this.exerciseGQL = exerciseGQL;
    }
    ngOnInit() {
        this.sub = this.route.paramMap.subscribe((paramMap) => {
            const toolId = paramMap.get('toolId');
            const collId = parseInt(paramMap.get('collId'), 10);
            const exId = parseInt(paramMap.get('exId'), 10);
            const partId = paramMap.get('partId');
            this.apolloSub = this.exerciseGQL
                .watch({ toolId, collId, exId, partId })
                .valueChanges
                .subscribe(({ data }) => this.exerciseQuery = data);
        });
    }
    ngOnDestroy() {
        this.apolloSub.unsubscribe();
        this.sub.unsubscribe();
    }
    // Exercise content
    get exercise() {
        return this.exerciseQuery.me.tool.collection.exercise;
    }
    get ebnfExerciseContent() {
        return this.exercise.content.__typename === 'EbnfExerciseContent' ? this.exercise.content : null;
    }
    get flaskExerciseContent() {
        return this.exercise.content.__typename === 'FlaskExerciseContent' ? this.exercise.content : null;
    }
    get programmingExerciseContent() {
        return this.exercise.content.__typename === 'ProgrammingExerciseContent' ? this.exercise.content : null;
    }
    get regexExerciseContent() {
        return this.exercise.content.__typename === 'RegexExerciseContent' ? this.exercise.content : null;
    }
    get sqlExerciseContent() {
        return this.exercise.content.__typename === 'SqlExerciseContent' ? this.exercise.content : null;
    }
    get umlExerciseContent() {
        return this.exercise.content.__typename === 'UmlExerciseContent' ? this.exercise.content : null;
    }
    get webExerciseContent() {
        return this.exercise.content.__typename === 'WebExerciseContent' ? this.exercise.content : null;
    }
    get xmlExerciseContent() {
        return this.exercise.content.__typename === 'XmlExerciseContent' ? this.exercise.content : null;
    }
}
ExerciseComponent.ɵfac = function ExerciseComponent_Factory(t) { return new (t || ExerciseComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_angular_router__WEBPACK_IMPORTED_MODULE_1__["ActivatedRoute"]), _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdirectiveInject"](_services_apollo_services__WEBPACK_IMPORTED_MODULE_2__["ExerciseGQL"])); };
ExerciseComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: ExerciseComponent, selectors: [["ng-component"]], decls: 3, vars: 2, consts: [[4, "ngIf", "ngIfElse"], ["loadingDataBlock", ""], [3, "exerciseFragment", "contentFragment", 4, "ngIf"], [3, "exerciseFragment", "contentFragment"], [1, "notification", "is-primary", "has-text-centered"]], template: function ExerciseComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](0, ExerciseComponent_ng_container_0_Template, 9, 8, "ng-container", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](1, ExerciseComponent_ng_template_1_Template, 2, 0, "ng-template", null, 1, _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplateRefExtractor"]);
    } if (rf & 2) {
        const _r1 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵreference"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx.exerciseQuery)("ngIfElse", _r1);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_3__["NgIf"], _ebnf_ebnf_exercise_ebnf_exercise_component__WEBPACK_IMPORTED_MODULE_4__["EbnfExerciseComponent"], _flask_flask_exercise_flask_exercise_component__WEBPACK_IMPORTED_MODULE_5__["FlaskExerciseComponent"], _programming_programming_exercise_programming_exercise_component__WEBPACK_IMPORTED_MODULE_6__["ProgrammingExerciseComponent"], _regex_regex_exercise_regex_exercise_component__WEBPACK_IMPORTED_MODULE_7__["RegexExerciseComponent"], _sql_sql_exercise_sql_exercise_component__WEBPACK_IMPORTED_MODULE_8__["SqlExerciseComponent"], _uml_uml_exercise_uml_exercise_component__WEBPACK_IMPORTED_MODULE_9__["UmlExerciseComponent"], _web_web_exercise_web_exercise_component__WEBPACK_IMPORTED_MODULE_10__["WebExerciseComponent"], _xml_xml_exercise_xml_exercise_component__WEBPACK_IMPORTED_MODULE_11__["XmlExerciseComponent"]], encapsulation: 2 });


/***/ }),

/***/ "tTUD":
/*!****************************************************************!*\
  !*** ./src/app/tools/random-tools/bool/_model/bool-formula.ts ***!
  \****************************************************************/
/*! exports provided: BooleanFormula, generateBooleanFormula */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BooleanFormula", function() { return BooleanFormula; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "generateBooleanFormula", function() { return generateBooleanFormula; });
/* harmony import */ var _helpers__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../../../helpers */ "Afm0");
/* harmony import */ var _bool_node__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./bool-node */ "oi/H");


const varA = new _bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanVariable"]('a');
const varB = new _bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanVariable"]('b');
const varC = new _bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanVariable"]('c');
function generateRandomOperator(left, right) {
    const leftNegated = Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["randomInt"])(0, 3) === 2;
    const rightNegated = Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["randomInt"])(0, 3) === 2;
    const leftChild = leftNegated ? new _bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanNot"](left) : left;
    const rightChild = rightNegated ? new _bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanNot"](right) : right;
    const operatorInt = Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["randomInt"])(0, 20);
    if (0 <= operatorInt && operatorInt < 8) {
        return new _bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanAnd"](leftChild, rightChild);
    }
    else if (8 <= operatorInt && operatorInt < 16) {
        return new _bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanOr"](leftChild, rightChild);
    }
    else if (operatorInt === 16) {
        return new _bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanXOr"](leftChild, rightChild);
    }
    else if (operatorInt === 17) {
        return new _bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanNAnd"](leftChild, rightChild);
    }
    else if (operatorInt === 18) {
        return new _bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanNOr"](leftChild, rightChild);
    }
    else if (operatorInt === 19) {
        return new _bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanEquivalency"](leftChild, rightChild);
    }
    else {
        return new _bool_node__WEBPACK_IMPORTED_MODULE_1__["BooleanImplication"](leftChild, rightChild);
    }
}
class BooleanFormula {
    constructor(left, right) {
        this.left = left;
        this.right = right;
        this.assignments = Object(_bool_node__WEBPACK_IMPORTED_MODULE_1__["calculateAssignments"])(right.getVariables());
    }
    getVariables() {
        return this.right.getVariables();
    }
    getSubFormulas() {
        return this.right.getSubFormulas();
    }
    getAssignments() {
        return this.assignments;
    }
    getValueFor(assignment) {
        return this.right.evaluate(assignment);
    }
    asString() {
        return this.left.variable + ' = ' + this.right.asString();
    }
    asHtmlString() {
        return this.left.variable + ' = ' + this.right.asHtmlString();
    }
    evaluate(assignments) {
        return this.right.evaluate(assignments);
    }
}
function generateBooleanFormula(left) {
    const depth = Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["randomInt"])(1, 3);
    if (depth === 1) {
        return new BooleanFormula(left, generateRandomOperator(varA, varB));
    }
    else {
        const vars = [varA, varB, varC];
        const leftChild = generateRandomOperator(Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["takeRandom"])(vars), Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["takeRandom"])(vars));
        const rightChild = generateRandomOperator(Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["takeRandom"])(vars), Object(_helpers__WEBPACK_IMPORTED_MODULE_0__["takeRandom"])(vars));
        return new BooleanFormula(left, generateRandomOperator(leftChild, rightChild));
    }
}


/***/ }),

/***/ "tsMx":
/*!*******************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/lessons/lesson-as-text/lesson-as-text.component.ts ***!
  \*******************************************************************************************/
/*! exports provided: LessonAsTextComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LessonAsTextComponent", function() { return LessonAsTextComponent; });
/* harmony import */ var _solvable_lesson_content__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../solvable-lesson-content */ "k/IA");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _lesson_questions_lesson_questions_content_component__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../lesson-questions/lesson-questions-content.component */ "CohO");






function LessonAsTextComponent_ng_container_1_ng_container_3_li_5_Template(rf, ctx) { if (rf & 1) {
    const _r11 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵgetCurrentView"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "li");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "button", 12);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵlistener"]("click", function LessonAsTextComponent_ng_container_1_ng_container_3_li_5_Template_button_click_1_listener() { _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵrestoreView"](_r11); const page_r9 = ctx.$implicit; const ctx_r10 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](3); return ctx_r10.currentIndex = page_r9; });
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const page_r9 = ctx.$implicit;
    const ctx_r6 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵclassProp"]("is-current", page_r9 === ctx_r6.currentIndex);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" ", page_r9, " ");
} }
function LessonAsTextComponent_ng_container_1_ng_container_3_div_8_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 13);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelement"](1, "div", 14);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r7 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("innerHTML", ctx_r7.currentTextContentFragment.content, _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵsanitizeHtml"]);
} }
function LessonAsTextComponent_ng_container_1_ng_container_3_div_9_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelement"](1, "it4all-lesson-questions-content", 15);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r8 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("content", ctx_r8.currentMultipleChoiceFragment);
} }
function LessonAsTextComponent_ng_container_1_ng_container_3_Template(rf, ctx) { if (rf & 1) {
    const _r13 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵgetCurrentView"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "nav", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "button", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵlistener"]("click", function LessonAsTextComponent_ng_container_1_ng_container_3_Template_button_click_2_listener() { _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵrestoreView"](_r13); const ctx_r12 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](2); return ctx_r12.currentIndex = ctx_r12.currentIndex - 1; });
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3, " Vorheriger Inhalt ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](4, "ul", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](5, LessonAsTextComponent_ng_container_1_ng_container_3_li_5_Template, 3, 3, "li", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](6, "button", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵlistener"]("click", function LessonAsTextComponent_ng_container_1_ng_container_3_Template_button_click_6_listener() { _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵrestoreView"](_r13); const ctx_r14 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](2); return ctx_r14.currentIndex = ctx_r14.currentIndex + 1; });
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](7, " N\u00E4chster Inhalt ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](8, LessonAsTextComponent_ng_container_1_ng_container_3_div_8_Template, 2, 1, "div", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](9, LessonAsTextComponent_ng_container_1_ng_container_3_div_9_Template, 2, 1, "div", 11);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r5 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("disabled", ctx_r5.currentIndex < 1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx_r5.contentIndexes);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("disabled", ctx_r5.currentIndex >= ctx_r5.contents.length - 1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", ctx_r5.currentTextContentFragment);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", ctx_r5.currentMultipleChoiceFragment);
} }
function LessonAsTextComponent_ng_container_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "h1", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](3, LessonAsTextComponent_ng_container_1_ng_container_3_Template, 10, 5, "ng-container", 1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]();
    const _r3 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵreference"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"]("Lektion: ", ctx_r0.lessonQuery.me.tool.lesson.title, "");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", ctx_r0.contents.length > 0)("ngIfElse", _r3);
} }
function LessonAsTextComponent_ng_template_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 16);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1, "Lade Daten...");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} }
function LessonAsTextComponent_ng_template_4_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 17);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1, "Kein Inhalt gefunden!");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} }
class LessonAsTextComponent {
    constructor(route, lessonGQL) {
        this.route = route;
        this.lessonGQL = lessonGQL;
        this.currentIndex = 0;
    }
    ngOnInit() {
        this.sub = this.route.paramMap.subscribe((paramMap) => {
            const toolId = paramMap.get('toolId');
            const lessonId = parseInt(paramMap.get('lessonId'), 10);
            this.lessonGQL
                .watch({ toolId, lessonId })
                .valueChanges
                .subscribe(({ data }) => this.lessonQuery = data);
        });
    }
    ngOnDestroy() {
        this.sub.unsubscribe();
    }
    get contents() {
        var _a;
        return (_a = this.lessonQuery) === null || _a === void 0 ? void 0 : _a.me.tool.lesson.contents;
    }
    get contentIndexes() {
        var _a;
        return (_a = this.contents) === null || _a === void 0 ? void 0 : _a.map((value, index) => index);
    }
    get currentContent() {
        return this.contents ? this.contents[this.currentIndex] : null;
    }
    get currentTextContentFragment() {
        return this.currentContent && Object(_solvable_lesson_content__WEBPACK_IMPORTED_MODULE_0__["isSolvableLessonTextContentFragment"])(this.currentContent) ? this.currentContent : undefined;
    }
    get currentMultipleChoiceFragment() {
        return this.currentContent && Object(_solvable_lesson_content__WEBPACK_IMPORTED_MODULE_0__["isSolvableLessonMultipleChoiceQuestionContentFragment"])(this.currentContent) ? this.currentContent : undefined;
    }
    hasMoreContent() {
        return this.lessonQuery && this.currentIndex < (this.lessonQuery.me.tool.lesson.contents.length - 1);
    }
    previousContent() {
        if (this.currentIndex > 0) {
            this.currentIndex--;
        }
    }
    nextContent() {
        if (this.currentIndex <= this.lessonQuery.me.tool.lesson.contents.length) {
            this.currentIndex++;
        }
    }
}
LessonAsTextComponent.ɵfac = function LessonAsTextComponent_Factory(t) { return new (t || LessonAsTextComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdirectiveInject"](_angular_router__WEBPACK_IMPORTED_MODULE_2__["ActivatedRoute"]), _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdirectiveInject"](_services_apollo_services__WEBPACK_IMPORTED_MODULE_3__["LessonAsTextGQL"])); };
LessonAsTextComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineComponent"]({ type: LessonAsTextComponent, selectors: [["ng-component"]], decls: 6, vars: 2, consts: [[1, "container"], [4, "ngIf", "ngIfElse"], ["loadingDataBlock", ""], ["noContentBlock", ""], [1, "title", "is-3", "has-text-centered"], ["role", "navigation", "aria-label", "pagination", 1, "pagination", "is-centered"], [1, "button", "pagination-previous", 3, "disabled", "click"], [1, "pagination-list"], [4, "ngFor", "ngForOf"], [1, "button", "pagination-next", 3, "disabled", "click"], ["class", "content box", 4, "ngIf"], [4, "ngIf"], [1, "button", "pagination-link", 3, "click"], [1, "content", "box"], [3, "innerHTML"], [3, "content"], [1, "notification", "is-primary", "has-text-centered"], [1, "notification", "is-warning", "has-text-centered"]], template: function LessonAsTextComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](1, LessonAsTextComponent_ng_container_1_Template, 4, 3, "ng-container", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](2, LessonAsTextComponent_ng_template_2_Template, 2, 0, "ng-template", null, 2, _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplateRefExtractor"]);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](4, LessonAsTextComponent_ng_template_4_Template, 2, 0, "ng-template", null, 3, _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplateRefExtractor"]);
    } if (rf & 2) {
        const _r1 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵreference"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", ctx.lessonQuery)("ngIfElse", _r1);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_4__["NgIf"], _angular_common__WEBPACK_IMPORTED_MODULE_4__["NgForOf"], _lesson_questions_lesson_questions_content_component__WEBPACK_IMPORTED_MODULE_5__["LessonQuestionsContentComponent"]], encapsulation: 2 });


/***/ }),

/***/ "u6rX":
/*!**********************************************************!*\
  !*** ./src/app/tool-overview/tool-overview.component.ts ***!
  \**********************************************************/
/*! exports provided: ToolOverviewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ToolOverviewComponent", function() { return ToolOverviewComponent; });
/* harmony import */ var _tools_random_tools_random_tools_list__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../tools/random-tools/random-tools-list */ "UE/p");
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/router */ "tyNb");






const _c4 = function (a1) { return ["/lessons", a1]; };
function ToolOverviewComponent_div_8_a_13_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "a", 11);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementContainerStart"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18n"](2, 13);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementContainerEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ranTool_r2 = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵnextContext"]().$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("routerLink", _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵpureFunction1"](1, _c4, ranTool_r2.id));
} }
const _c5 = function (a1) { return ["/randomTools", a1]; };
function ToolOverviewComponent_div_8_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](1, "div", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](2, "header", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](3, "p", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](5, "div", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](6, "p");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](7, "\u00A0");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](8, "p");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](9, "\u00A0");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](10, "footer", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](11, "a", 11);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](12, "Zum Tool");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](13, ToolOverviewComponent_div_8_a_13_Template, 3, 3, "a", 12);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ranTool_r2 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtextInterpolate"](ranTool_r2.name);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](7);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("routerLink", _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵpureFunction1"](3, _c5, ranTool_r2.id));
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ranTool_r2.hasLessons);
} }
function ToolOverviewComponent_div_9_sup_5_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](0, "sup", 18);
} if (rf & 2) {
    const colTool_r5 = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵnextContext"]().$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("innerHTML", "\u00A0&" + colTool_r5.state.toLowerCase() + ";", _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵsanitizeHtml"]);
} }
const _c12 = function (a1) { return ["/tools", a1]; };
function ToolOverviewComponent_div_9_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](1, "div", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](2, "header", 7);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](3, "p", 8);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](5, ToolOverviewComponent_div_9_sup_5_Template, 1, 1, "sup", 14);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](6, "div", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](7, "p");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18n"](8, 15);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](9, "p");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18n"](10, 16);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](11, "footer", 10);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](12, "a", 11);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18n"](13, 17);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} if (rf & 2) {
    const colTool_r5 = ctx.$implicit;
    const ctx_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtextInterpolate1"](" ", colTool_r5.name, " ");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", !ctx_r1.isLive(colTool_r5.state));
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18nExp"](colTool_r5.collectionCount)(colTool_r5.exerciseCount);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18nApply"](8);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18nExp"](colTool_r5.lessonCount === 0 ? "Keine" : colTool_r5.lessonCount);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18nApply"](10);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("routerLink", _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵpureFunction1"](6, _c12, colTool_r5.id));
} }
class ToolOverviewComponent {
    constructor(toolOverviewGQL) {
        this.toolOverviewGQL = toolOverviewGQL;
        this.randomTools = _tools_random_tools_random_tools_list__WEBPACK_IMPORTED_MODULE_0__["randomTools"];
    }
    ngOnInit() {
        this.toolOverviewGQL
            .watch()
            .valueChanges
            .subscribe(({ data }) => this.toolOverviewQuery = data);
    }
    isLive(toolState) {
        return toolState === _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["ToolState"].Live;
    }
    get collectionTools() {
        if (this.toolOverviewQuery) {
            return this.toolOverviewQuery.me.tools;
        }
        else {
            return [];
        }
    }
}
ToolOverviewComponent.ɵfac = function ToolOverviewComponent_Factory(t) { return new (t || ToolOverviewComponent)(_angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdirectiveInject"](_services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["ToolOverviewGQL"])); };
ToolOverviewComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdefineComponent"]({ type: ToolOverviewComponent, selectors: [["ng-component"]], decls: 10, vars: 2, consts: function () { let i18n_0; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_1684734235599362204$$SRC_APP_TOOL_OVERVIEW_TOOL_OVERVIEW_COMPONENT_TS_1 = goog.getMsg("Tools");
        i18n_0 = MSG_EXTERNAL_1684734235599362204$$SRC_APP_TOOL_OVERVIEW_TOOL_OVERVIEW_COMPONENT_TS_1;
    }
    else {
        i18n_0 = "Tools";
    } let i18n_2; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_8144064759296290968$$SRC_APP_TOOL_OVERVIEW_TOOL_OVERVIEW_COMPONENT_TS___3 = goog.getMsg("Zu den Lektionen");
        i18n_2 = MSG_EXTERNAL_8144064759296290968$$SRC_APP_TOOL_OVERVIEW_TOOL_OVERVIEW_COMPONENT_TS___3;
    }
    else {
        i18n_2 = "Go to lessons";
    } let i18n_6; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_2864721186201046098$$SRC_APP_TOOL_OVERVIEW_TOOL_OVERVIEW_COMPONENT_TS__7 = goog.getMsg("{$interpolation} Sammlungen mit {$interpolation_1} Aufgaben", { "interpolation": "\uFFFD0\uFFFD", "interpolation_1": "\uFFFD1\uFFFD" });
        i18n_6 = MSG_EXTERNAL_2864721186201046098$$SRC_APP_TOOL_OVERVIEW_TOOL_OVERVIEW_COMPONENT_TS__7;
    }
    else {
        i18n_6 = "" + "\uFFFD0\uFFFD" + " collections with " + "\uFFFD1\uFFFD" + " exercises";
    } let i18n_8; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_5011333921574496958$$SRC_APP_TOOL_OVERVIEW_TOOL_OVERVIEW_COMPONENT_TS__9 = goog.getMsg("{$interpolation} Lektionen", { "interpolation": "\uFFFD0\uFFFD" });
        i18n_8 = MSG_EXTERNAL_5011333921574496958$$SRC_APP_TOOL_OVERVIEW_TOOL_OVERVIEW_COMPONENT_TS__9;
    }
    else {
        i18n_8 = "" + "\uFFFD0\uFFFD" + " Lektionen";
    } let i18n_10; if (typeof ngI18nClosureMode !== "undefined" && ngI18nClosureMode) {
        const MSG_EXTERNAL_1932363691402414050$$SRC_APP_TOOL_OVERVIEW_TOOL_OVERVIEW_COMPONENT_TS__11 = goog.getMsg("Zum Tool");
        i18n_10 = MSG_EXTERNAL_1932363691402414050$$SRC_APP_TOOL_OVERVIEW_TOOL_OVERVIEW_COMPONENT_TS__11;
    }
    else {
        i18n_10 = "Go to tool";
    } return [[1, "container"], [1, "title", "is-3", "has-text-centered"], i18n_0, [1, "columns", "is-multiline"], ["class", "column is-one-quarter-desktop is-half-tablet", 4, "ngFor", "ngForOf"], [1, "column", "is-one-quarter-desktop", "is-half-tablet"], [1, "card"], [1, "card-header"], [1, "card-header-title"], [1, "card-content"], [1, "card-footer"], [1, "card-footer-item", 3, "routerLink"], ["class", "card-footer-item", 3, "routerLink", 4, "ngIf"], i18n_2, [3, "innerHTML", 4, "ngIf"], i18n_6, i18n_8, i18n_10, [3, "innerHTML"]]; }, template: function ToolOverviewComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](1, "h1", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](2, "span");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵi18n"](3, 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](4, "\u00A0");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](5, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](6, "it4all");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](7, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](8, ToolOverviewComponent_div_8_Template, 14, 5, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](9, ToolOverviewComponent_div_9_Template, 14, 8, "div", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](8);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngForOf", ctx.randomTools);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngForOf", ctx.collectionTools);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_3__["NgForOf"], _angular_router__WEBPACK_IMPORTED_MODULE_4__["RouterLinkWithHref"], _angular_common__WEBPACK_IMPORTED_MODULE_3__["NgIf"]], encapsulation: 2 });


/***/ }),

/***/ "uEXo":
/*!*******************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/regex/regex-matching-result/regex-matching-result.component.ts ***!
  \*******************************************************************************************************/
/*! exports provided: RegexMatchingResultComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RegexMatchingResultComponent", function() { return RegexMatchingResultComponent; });
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common */ "ofXK");



class RegexMatchingResultComponent {
    ngOnInit() {
        this.correct = [_services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["BinaryClassificationResultType"].TruePositive, _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["BinaryClassificationResultType"].TrueNegative]
            .includes(this.matchingResult.resultType);
        this.wasMatched = [_services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["BinaryClassificationResultType"].FalsePositive, _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["BinaryClassificationResultType"].TruePositive]
            .includes(this.matchingResult.resultType);
    }
}
RegexMatchingResultComponent.ɵfac = function RegexMatchingResultComponent_Factory(t) { return new (t || RegexMatchingResultComponent)(); };
RegexMatchingResultComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineComponent"]({ type: RegexMatchingResultComponent, selectors: [["it4all-regex-matching-result"]], inputs: { matchingResult: "matchingResult" }, decls: 7, vars: 5, consts: [[1, "notification", "is-light-grey", 3, "ngClass"], [3, "innerHTML"]], template: function RegexMatchingResultComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](4);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelement"](5, "span", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](6, " erkannt. ");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngClass", ctx.correct ? "has-text-dark-success" : "has-text-danger");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" ", ctx.correct ? "\u2714" : "\u2718", " \u00A0 ");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](ctx.matchingResult.matchData);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" wurde ", ctx.correct ? "korrekt" : "f\u00E4lschlicherweise", " ");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("innerHTML", ctx.wasMatched ? "" : "<b>nicht</b>", _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵsanitizeHtml"]);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_2__["NgClass"]], encapsulation: 2 });


/***/ }),

/***/ "ubCX":
/*!********************************************************************************************************************!*\
  !*** ./src/app/tools/random-tools/bool/bool-create/bool-create-instructions/bool-create-instructions.component.ts ***!
  \********************************************************************************************************************/
/*! exports provided: BoolCreateInstructionsComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BoolCreateInstructionsComponent", function() { return BoolCreateInstructionsComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");

class BoolCreateInstructionsComponent {
    constructor() {
    }
}
BoolCreateInstructionsComponent.ɵfac = function BoolCreateInstructionsComponent_Factory(t) { return new (t || BoolCreateInstructionsComponent)(); };
BoolCreateInstructionsComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: BoolCreateInstructionsComponent, selectors: [["it4all-bool-create-instructions"]], decls: 161, vars: 0, consts: [[1, "subtitle", "is-4", "has-text-info", "has-text-centered"], [1, "table", "is-fullwidth"], [1, "has-background-grey-light"], [1, "content"]], template: function BoolCreateInstructionsComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](0, "hr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "h3", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](2, "Folgende Operatoren sind definiert und d\u00FCrfen verwendet werden:");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](3, "table", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](4, "thead");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](5, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](6, "th");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](7, "Werte");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](8, "th");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](9, "th");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](10, "Darstellung");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](11, "tbody");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](12, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](13, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](14, "Wahr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](15, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](16, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](17, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](18, "true");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](19, " oder ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](20, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](21, "1");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](22, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](23, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](24, "Falsch");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](25, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](26, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](27, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](28, "false");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](29, " oder ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](30, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](31, "0");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](32, "thead");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](33, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](34, "th");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](35, "Standardoperatoren");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](36, "th");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](37, "Mathematisches Symbol");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](38, "th");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](39, "Darstellung");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](40, "tbody");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](41, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](42, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](43, "Nicht");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](44, "td", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](45, "\u00AC a");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](46, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](47, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](48, "not a");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](49, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](50, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](51, "Und");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](52, "td", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](53, "a \u2227 b");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](54, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](55, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](56, "a and b");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](57, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](58, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](59, "Oder");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](60, "td", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](61, "a \u2228 b");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](62, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](63, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](64, "a or b");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](65, "thead");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](66, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](67, "th");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](68, "Spezialoperatoren");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](69, "th");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](70, "th");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](71, "tbody");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](72, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](73, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](74, "Exklusives Oder");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](75, "td", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](76, "a \u2295 b");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](77, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](78, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](79, "a xor b");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](80, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](81, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](82, "Negiertes Und");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](83, "td", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](84, "a \u22BC b");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](85, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](86, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](87, "a nand b");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](88, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](89, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](90, "Negiertes Oder");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](91, "td", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](92, "a \u22BD b");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](93, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](94, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](95, "a nor b");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](96, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](97, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](98, "\u00C4quivalenz");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](99, "td", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](100, "a \u21D4 b");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](101, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](102, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](103, "a equiv b");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](104, "tr");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](105, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](106, "Implikation");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](107, "td", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](108, "a \u21D2 b");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](109, "td");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](110, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](111, "a impl b");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](112, "h3", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](113, "Weitere Regeln");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](114, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](115, "ul");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](116, "li");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](117, "Gro\u00DF- und Kleinschreibung spielt keine Rolle.");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](118, "li");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](119, " Reihenfolge der Auswertung: ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](120, "ul");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](121, "li");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](122, "Die Operatorrangfolge ist folgenderma\u00DFen festgelegt: ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](123, "ol");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](124, "li");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](125, "Nicht");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](126, "li");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](127, "Und");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](128, "li");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](129, "Oder");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](130, "li");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](131, "Implikation");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](132, "li");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](133, "Restliche Operatoren: exklusives Oder, Nicht Und, Nicht Oder, \u00E4quivalenz");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](134, "li");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](135, "Beeinflussung der Auswertung: ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](136, "ul");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](137, "li");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](138, "Klammere, um die Reihenfolge festzulegen, in der Ausdr\u00FCcke ausgewertet werden. Beispiel: ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](139, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](140, "not((a or b) and c)");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](141, "li");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](142, "Ansonsten wird von links nach rechts ausgewertet (bei gleicher Pr\u00E4zedenz der zweite Operator Vorrang) ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](143, "li");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](144, "Beispiele: ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](145, "ul");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](146, "li");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](147, "Nach Rangfolge: ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](148, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](149, "a xor b impl c or d and not e");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](150, " \u21D4 ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](151, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](152, "a xor (b impl (c or (d and (not e))))))");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](153, "li");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](154, "Gleichwertiger Rang (links nach rechts): ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](155, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](156, "a xor b nor c nand d equiv e");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](157, " \u21D4 ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](158, "code");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](159, "((((a xor b) nor c) nand d) equiv e)");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](160, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } }, encapsulation: 2 });


/***/ }),

/***/ "vWq+":
/*!*************************************************************************!*\
  !*** ./src/app/tools/random-tools/bool/_model/bool-component-helper.ts ***!
  \*************************************************************************/
/*! exports provided: BoolComponentHelper */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "BoolComponentHelper", function() { return BoolComponentHelper; });
/* harmony import */ var _bool_node__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./bool-node */ "oi/H");

class BoolComponentHelper {
    constructor() {
        this.sampleVariable = new _bool_node__WEBPACK_IMPORTED_MODULE_0__["BooleanVariable"]('z');
        this.learnerVariable = new _bool_node__WEBPACK_IMPORTED_MODULE_0__["BooleanVariable"]('y');
        // FIXME: remove assignments, get from formula!
        this.assignments = [];
        this.corrected = false;
        this.completelyCorrect = false;
    }
    displayAssignmentValue(assignment, variable) {
        if (assignment.has(variable.variable)) {
            return assignment.get(variable.variable) ? '1' : '0';
        }
        else {
            return '';
        }
    }
    isCorrect(assignment) {
        return assignment.get(this.learnerVariable.variable) === assignment.get(this.sampleVariable.variable);
    }
}


/***/ }),

/***/ "vXFn":
/*!***************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/_components/proficiency-card/proficiency-card.component.ts ***!
  \***************************************************************************************************/
/*! exports provided: ProficiencyCardComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ProficiencyCardComponent", function() { return ProficiencyCardComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _filled_points_filled_points_component__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../filled-points/filled-points.component */ "L0fW");


class ProficiencyCardComponent {
}
ProficiencyCardComponent.ɵfac = function ProficiencyCardComponent_Factory(t) { return new (t || ProficiencyCardComponent)(); };
ProficiencyCardComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: ProficiencyCardComponent, selectors: [["it4all-proficiency-card"]], inputs: { proficiency: "proficiency" }, decls: 11, vars: 6, consts: [[1, "card"], [1, "card-header"], [1, "card-header-title"], [1, "card-content"], [3, "filledPoints", "maxPoints"]], template: function ProficiencyCardComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "header", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](2, "p", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](4, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](5, "p");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](6);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](7, "p");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](8);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](9, "p");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](10, "it4all-filled-points", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](ctx.proficiency.topic.title);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"]("Level: ", ctx.proficiency.level.title, "");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate2"]("", ctx.proficiency.points, " von ", ctx.proficiency.pointsForNextLevel, " Punkten f\u00FCr n\u00E4chstes Level");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("filledPoints", ctx.proficiency.level.levelIndex)("maxPoints", ctx.proficiency.topic.maxLevel.levelIndex);
    } }, directives: [_filled_points_filled_points_component__WEBPACK_IMPORTED_MODULE_1__["FilledPointsComponent"]], encapsulation: 2 });


/***/ }),

/***/ "vY5A":
/*!***************************************!*\
  !*** ./src/app/app-routing.module.ts ***!
  \***************************************/
/*! exports provided: AppRoutingModule, routingComponents */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AppRoutingModule", function() { return AppRoutingModule; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "routingComponents", function() { return routingComponents; });
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/router */ "tyNb");
/* harmony import */ var _tool_overview_tool_overview_component__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./tool-overview/tool-overview.component */ "u6rX");
/* harmony import */ var _user_management_login_form_login_form_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./user_management/login-form/login-form.component */ "AgHE");
/* harmony import */ var _helpers_auth_guard__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./_helpers/auth-guard */ "T7p5");
/* harmony import */ var _lti_lti_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./lti/lti.component */ "fxtn");
/* harmony import */ var _user_management_register_form_register_form_component__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./user_management/register-form/register-form.component */ "oCnN");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! @angular/core */ "fXoL");








const routes = [
    { path: '', component: _tool_overview_tool_overview_component__WEBPACK_IMPORTED_MODULE_1__["ToolOverviewComponent"], canActivate: [_helpers_auth_guard__WEBPACK_IMPORTED_MODULE_3__["AuthGuard"]] },
    { path: 'registerForm', component: _user_management_register_form_register_form_component__WEBPACK_IMPORTED_MODULE_5__["RegisterFormComponent"] },
    { path: 'loginForm', component: _user_management_login_form_login_form_component__WEBPACK_IMPORTED_MODULE_2__["LoginFormComponent"] },
    { path: 'lti/:uuid', component: _lti_lti_component__WEBPACK_IMPORTED_MODULE_4__["LtiComponent"] }
];
class AppRoutingModule {
}
AppRoutingModule.ɵfac = function AppRoutingModule_Factory(t) { return new (t || AppRoutingModule)(); };
AppRoutingModule.ɵmod = _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵdefineNgModule"]({ type: AppRoutingModule });
AppRoutingModule.ɵinj = _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵdefineInjector"]({ imports: [[_angular_router__WEBPACK_IMPORTED_MODULE_0__["RouterModule"].forRoot(routes, { relativeLinkResolution: 'legacy' })], _angular_router__WEBPACK_IMPORTED_MODULE_0__["RouterModule"]] });
(function () { (typeof ngJitMode === "undefined" || ngJitMode) && _angular_core__WEBPACK_IMPORTED_MODULE_6__["ɵɵsetNgModuleScope"](AppRoutingModule, { imports: [_angular_router__WEBPACK_IMPORTED_MODULE_0__["RouterModule"]], exports: [_angular_router__WEBPACK_IMPORTED_MODULE_0__["RouterModule"]] }); })();
const routingComponents = [
    _tool_overview_tool_overview_component__WEBPACK_IMPORTED_MODULE_1__["ToolOverviewComponent"],
    _user_management_login_form_login_form_component__WEBPACK_IMPORTED_MODULE_2__["LoginFormComponent"],
    _user_management_register_form_register_form_component__WEBPACK_IMPORTED_MODULE_5__["RegisterFormComponent"],
    _lti_lti_component__WEBPACK_IMPORTED_MODULE_4__["LtiComponent"],
];


/***/ }),

/***/ "wDY7":
/*!*********************************************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/xml/_components/xml-element-line-match-result/xml-element-line-match-result.component.ts ***!
  \*********************************************************************************************************************************/
/*! exports provided: XmlElementLineMatchResultComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "XmlElementLineMatchResultComponent", function() { return XmlElementLineMatchResultComponent; });
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common */ "ofXK");



function XmlElementLineMatchResultComponent_div_0_ul_6_ng_container_3_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1, " Erwartet wurde ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const m_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](2).$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](m_r3.analysisResult.correctContent);
} }
function XmlElementLineMatchResultComponent_div_0_ul_6_ng_container_6_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1, " Erwartet wurde ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](4, ". ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const m_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](2).$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](m_r3.analysisResult.correctAttributes);
} }
function XmlElementLineMatchResultComponent_div_0_ul_6_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "ul");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "li", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](3, XmlElementLineMatchResultComponent_div_0_ul_6_ng_container_3_Template, 4, 1, "ng-container", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](4, "li", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](6, XmlElementLineMatchResultComponent_div_0_ul_6_ng_container_6_Template, 5, 1, "ng-container", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const m_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]().$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngClass", m_r3.analysisResult.contentCorrect ? "has-text-dark-success" : "has-text-danger");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" Der Inhalt des Elements war ", m_r3.analysisResult.contentCorrect ? "" : "nicht", " korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", !m_r3.analysisResult.contentCorrect);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngClass", m_r3.analysisResult.attributesCorrect ? "has-text-dark-success" : "has-text-danger");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" Die Attribute des Elements waren ", m_r3.analysisResult.attributesCorrect ? "" : "nicht", " korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", !m_r3.analysisResult.attributesCorrect);
} }
function XmlElementLineMatchResultComponent_div_0_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "div", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](2, " Die Definition des Element ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](3, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](6, XmlElementLineMatchResultComponent_div_0_ul_6_Template, 7, 6, "ul", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const m_r3 = ctx.$implicit;
    const ctx_r0 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngClass", ctx_r0.isCorrect(m_r3) ? "has-text-dark-success" : "has-text-danger");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](m_r3.userArg.elementName);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" ist ", ctx_r0.isCorrect(m_r3) ? "" : "nicht", " korrekt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", m_r3.analysisResult && !ctx_r0.isCorrect(m_r3));
} }
function XmlElementLineMatchResultComponent_div_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1, " Die Definition des Elements ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](4, " ist falsch. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const mu_r10 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](mu_r10.elementName);
} }
function XmlElementLineMatchResultComponent_div_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](1, " Die Definition des Elements ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](2, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](4, " fehlt. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ms_r11 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate"](ms_r11.elementName);
} }
class XmlElementLineMatchResultComponent {
    isCorrect(m) {
        return m.matchType === _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["MatchType"].SuccessfulMatch;
    }
}
XmlElementLineMatchResultComponent.ɵfac = function XmlElementLineMatchResultComponent_Factory(t) { return new (t || XmlElementLineMatchResultComponent)(); };
XmlElementLineMatchResultComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineComponent"]({ type: XmlElementLineMatchResultComponent, selectors: [["it4all-xml-element-line-match-result"]], inputs: { result: "result" }, decls: 3, vars: 3, consts: [["class", "content", 4, "ngFor", "ngForOf"], ["class", "has-text-danger", 4, "ngFor", "ngForOf"], [1, "content"], [3, "ngClass"], [4, "ngIf"], [1, "has-text-danger"]], template: function XmlElementLineMatchResultComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](0, XmlElementLineMatchResultComponent_div_0_Template, 7, 4, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](1, XmlElementLineMatchResultComponent_div_1_Template, 5, 1, "div", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](2, XmlElementLineMatchResultComponent_div_2_Template, 5, 1, "div", 1);
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx.result.allMatches);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx.result.notMatchedForUser);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngForOf", ctx.result.notMatchedForSample);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_2__["NgForOf"], _angular_common__WEBPACK_IMPORTED_MODULE_2__["NgClass"], _angular_common__WEBPACK_IMPORTED_MODULE_2__["NgIf"]], encapsulation: 2 });


/***/ }),

/***/ "x5V5":
/*!*****************************************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/uml/_components/uml-diag-drawing-correction/uml-diag-drawing-correction.component.ts ***!
  \*****************************************************************************************************************************/
/*! exports provided: UmlDiagDrawingCorrectionComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlDiagDrawingCorrectionComponent", function() { return UmlDiagDrawingCorrectionComponent; });
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _uml_assoc_match_result_uml_assoc_match_result_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../uml-assoc-match-result/uml-assoc-match-result.component */ "1UiD");
/* harmony import */ var _uml_impl_result_uml_impl_result_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../uml-impl-result/uml-impl-result.component */ "qhSi");





function UmlDiagDrawingCorrectionComponent_ng_container_0_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementContainer"](0);
} }
function UmlDiagDrawingCorrectionComponent_ng_container_2_div_3_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](0, "div", 3);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelement"](1, "it4all-uml-assoc-match-result", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("assocResult", ctx_r3.result.assocResult);
} }
function UmlDiagDrawingCorrectionComponent_ng_container_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementStart"](1, "h3", 1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtext"](2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](3, UmlDiagDrawingCorrectionComponent_ng_container_2_div_3_Template, 2, 1, "div", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngClass", ctx_r1.assocResultSuccessful ? "has-text-dark-success" : "has-text-danger");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtextInterpolate1"](" Der Vergleich der Assoziationen war ", ctx_r1.assocResultSuccessful ? "" : "nicht", " erfolgreich. ");
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", !ctx_r1.assocResultSuccessful);
} }
function UmlDiagDrawingCorrectionComponent_ng_container_3_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelement"](1, "it4all-uml-impl-result", 5);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r2 = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("implResult", ctx_r2.result.implResult);
} }
class UmlDiagDrawingCorrectionComponent {
    constructor() {
        this.assocResultSuccessful = false;
    }
    ngOnChanges(changes) {
        if (this.result.assocResult) {
            this.assocResultSuccessful = this.associationResultSuccessful(this.result.assocResult);
        }
    }
    assocMatchIsCorrect(m) {
        return m.matchType === _services_apollo_services__WEBPACK_IMPORTED_MODULE_0__["MatchType"].SuccessfulMatch;
    }
    associationResultSuccessful(assocResult) {
        return assocResult.allMatches.every((x) => this.assocMatchIsCorrect(x));
    }
}
UmlDiagDrawingCorrectionComponent.ɵfac = function UmlDiagDrawingCorrectionComponent_Factory(t) { return new (t || UmlDiagDrawingCorrectionComponent)(); };
UmlDiagDrawingCorrectionComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵdefineComponent"]({ type: UmlDiagDrawingCorrectionComponent, selectors: [["it4all-uml-diag-drawing-correction"]], inputs: { result: "result" }, features: [_angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵNgOnChangesFeature"]], decls: 4, vars: 3, consts: [[4, "ngIf"], [1, "subtitle", "is-5", 3, "ngClass"], ["class", "content", 4, "ngIf"], [1, "content"], [3, "assocResult"], [3, "implResult"]], template: function UmlDiagDrawingCorrectionComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](0, UmlDiagDrawingCorrectionComponent_ng_container_0_Template, 1, 0, "ng-container", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵelement"](1, "br");
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](2, UmlDiagDrawingCorrectionComponent_ng_container_2_Template, 4, 3, "ng-container", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵtemplate"](3, UmlDiagDrawingCorrectionComponent_ng_container_3_Template, 2, 1, "ng-container", 0);
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", ctx.result.classResult);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", ctx.result.assocResult);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_1__["ɵɵproperty"]("ngIf", ctx.result.implResult);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_2__["NgIf"], _angular_common__WEBPACK_IMPORTED_MODULE_2__["NgClass"], _uml_assoc_match_result_uml_assoc_match_result_component__WEBPACK_IMPORTED_MODULE_3__["UmlAssocMatchResultComponent"], _uml_impl_result_uml_impl_result_component__WEBPACK_IMPORTED_MODULE_4__["UmlImplResultComponent"]], encapsulation: 2 });


/***/ }),

/***/ "xeHz":
/*!*******************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/_components/exercise-link-card/exercise-link-card.component.ts ***!
  \*******************************************************************************************************/
/*! exports provided: ExerciseLinkCardComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExerciseLinkCardComponent", function() { return ExerciseLinkCardComponent; });
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _filled_points_filled_points_component__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../filled-points/filled-points.component */ "L0fW");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/router */ "tyNb");




function ExerciseLinkCardComponent_div_8_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 12);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const part_r4 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵclassProp"]("is-success", part_r4.solved);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate"](part_r4.name);
} }
function ExerciseLinkCardComponent_div_9_div_1_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 14);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](2, "it4all-filled-points", 15);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const topicWithLevel_r6 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("title", topicWithLevel_r6.topic);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate1"](" ", topicWithLevel_r6.topic.abbreviation, "\u00A0 - \u00A0 ");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("filledPoints", topicWithLevel_r6.level.levelIndex)("maxPoints", topicWithLevel_r6.topic.maxLevel.levelIndex);
} }
function ExerciseLinkCardComponent_div_9_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](1, ExerciseLinkCardComponent_div_9_div_1_Template, 3, 4, "div", 13);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} if (rf & 2) {
    const ctx_r1 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx_r1.exercise.topicsWithLevels);
} }
function ExerciseLinkCardComponent_ng_template_13_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 16);
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](1, "Keine Tags vorhanden");
    _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
} }
const _c0 = function (a1, a3, a5) { return ["/tools", a1, "collections", a3, "exercises", a5]; };
class ExerciseLinkCardComponent {
}
ExerciseLinkCardComponent.ɵfac = function ExerciseLinkCardComponent_Factory(t) { return new (t || ExerciseLinkCardComponent)(); };
ExerciseLinkCardComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵdefineComponent"]({ type: ExerciseLinkCardComponent, selectors: [["it4all-exercise-link-card"]], inputs: { exercise: "exercise" }, decls: 15, vars: 11, consts: [[1, "card"], [1, "card-header"], [1, "card-header-title"], ["title", "Schwierigkeit", 1, "tag"], ["maxPoints", "5", 3, "filledPoints"], [1, "card-content"], [1, "tags"], ["class", "tag", 3, "is-success", 4, "ngFor", "ngForOf"], ["class", "tags", 4, "ngIf", "ngIfElse"], [1, "card-footer"], [1, "card-footer-item", 3, "routerLink"], ["noTagsBlock", ""], [1, "tag"], ["class", "tag", 3, "title", 4, "ngFor", "ngForOf"], [1, "tag", 3, "title"], [3, "filledPoints", "maxPoints"], [1, "tag", "is-warning"]], template: function ExerciseLinkCardComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](1, "header", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](2, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](4, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelement"](5, "it4all-filled-points", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](6, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](7, "div", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](8, ExerciseLinkCardComponent_div_8_Template, 2, 3, "div", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](9, ExerciseLinkCardComponent_div_9_Template, 2, 1, "div", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](10, "footer", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementStart"](11, "a", 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtext"](12, " Zur Aufgabe ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplate"](13, ExerciseLinkCardComponent_ng_template_13_Template, 2, 0, "ng-template", null, 11, _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtemplateRefExtractor"]);
    } if (rf & 2) {
        const _r2 = _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵreference"](14);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵtextInterpolate2"](" ", ctx.exercise.exerciseId, ". ", ctx.exercise.title, " \u00A0 ");
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("filledPoints", ctx.exercise.difficulty);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngForOf", ctx.exercise.parts);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](1);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("ngIf", ctx.exercise.topicsWithLevels.length > 0)("ngIfElse", _r2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵproperty"]("routerLink", _angular_core__WEBPACK_IMPORTED_MODULE_0__["ɵɵpureFunction3"](7, _c0, ctx.exercise.toolId, ctx.exercise.collectionId, ctx.exercise.exerciseId));
    } }, directives: [_filled_points_filled_points_component__WEBPACK_IMPORTED_MODULE_1__["FilledPointsComponent"], _angular_common__WEBPACK_IMPORTED_MODULE_2__["NgForOf"], _angular_common__WEBPACK_IMPORTED_MODULE_2__["NgIf"], _angular_router__WEBPACK_IMPORTED_MODULE_3__["RouterLinkWithHref"]], encapsulation: 2 });


/***/ }),

/***/ "xvqd":
/*!*************************************************************************************************!*\
  !*** ./src/app/tools/collection-tools/uml/uml-class-selection/uml-class-selection.component.ts ***!
  \*************************************************************************************************/
/*! exports provided: UmlClassSelectionComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UmlClassSelectionComponent", function() { return UmlClassSelectionComponent; });
/* harmony import */ var _uml_tools__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../uml-tools */ "PyDI");
/* harmony import */ var _services_apollo_services__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ../../../../_services/apollo_services */ "o20/");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/common */ "ofXK");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/router */ "tyNb");





function UmlClassSelectionComponent_ng_container_8_span_1_Template(rf, ctx) { if (rf & 1) {
    const _r8 = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵgetCurrentView"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "span", 15);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("click", function UmlClassSelectionComponent_ng_container_8_span_1_Template_span_click_0_listener() { _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵrestoreView"](_r8); const textPart_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵnextContext"]().$implicit; const ctx_r6 = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵnextContext"](); return ctx_r6.toggleClassSelected(textPart_r3.selectableClass); });
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} if (rf & 2) {
    const textPart_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵnextContext"]().$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngClass", textPart_r3.selectableClass.selected ? "has-text-link" : "has-text-black");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtextInterpolate"](textPart_r3.text);
} }
function UmlClassSelectionComponent_ng_container_8_span_2_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "span");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} if (rf & 2) {
    const textPart_r3 = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵnextContext"]().$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtextInterpolate"](textPart_r3.text);
} }
function UmlClassSelectionComponent_ng_container_8_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](1, UmlClassSelectionComponent_ng_container_8_span_1_Template, 2, 2, "span", 14);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](2, UmlClassSelectionComponent_ng_container_8_span_2_Template, 2, 1, "span", 13);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const textPart_r3 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", textPart_r3.selectableClass);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", !textPart_r3.selectableClass);
} }
function UmlClassSelectionComponent_li_14_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "li");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} if (rf & 2) {
    const selectableClass_r11 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](1);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtextInterpolate"](selectableClass_r11.name);
} }
function UmlClassSelectionComponent_ng_container_25_li_9_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "li");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1, "\u2714\u00A0");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](2, "code", 17);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} if (rf & 2) {
    const correctClass_r15 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtextInterpolate"](correctClass_r15.name);
} }
function UmlClassSelectionComponent_ng_container_25_li_14_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "li");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1, "\u2718\u00A0");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](2, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} if (rf & 2) {
    const missingClass_r16 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtextInterpolate"](missingClass_r16.name);
} }
function UmlClassSelectionComponent_ng_container_25_li_19_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "li");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](1, "\u2753\u00A0");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](2, "code");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
} if (rf & 2) {
    const wrongClass_r17 = ctx.$implicit;
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](3);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtextInterpolate"](wrongClass_r17.name);
} }
function UmlClassSelectionComponent_ng_container_25_Template(rf, ctx) { if (rf & 1) {
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementContainerStart"](0);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelement"](1, "hr");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](2, "h2", 4);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](3, "Korrektur");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](4, "div", 2);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](5, "div", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](6, "h3", 16);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](7, "Korrekte Klassen:");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](8, "ul");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](9, UmlClassSelectionComponent_ng_container_25_li_9_Template, 4, 1, "li", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](10, "div", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](11, "h3", 16);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](12, "Fehlende Klassen:");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](13, "ul");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](14, UmlClassSelectionComponent_ng_container_25_li_14_Template, 4, 1, "li", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](15, "div", 9);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](16, "h3", 16);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](17, "Falsche Klassen");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](18, "ul");
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](19, UmlClassSelectionComponent_ng_container_25_li_19_Template, 4, 1, "li", 6);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementContainerEnd"]();
} if (rf & 2) {
    const ctx_r2 = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵnextContext"]();
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](9);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngForOf", ctx_r2.getCorrectClasses());
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngForOf", ctx_r2.getMissingClasses());
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](5);
    _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngForOf", ctx_r2.getWrongClasses());
} }
const _c0 = function (a1) { return ["..", a1]; };
const _c1 = function () { return ["../.."]; };
class UmlClassSelectionComponent {
    constructor() {
        this.nextPartId = Object(_uml_tools__WEBPACK_IMPORTED_MODULE_0__["getIdForUmlExPart"])(_services_apollo_services__WEBPACK_IMPORTED_MODULE_1__["UmlExPart"].DiagramDrawingHelp);
        this.corrected = false;
    }
    ngOnInit() {
        const { selectableClasses, textParts } = Object(_uml_tools__WEBPACK_IMPORTED_MODULE_0__["getUmlExerciseTextParts"])(this.exerciseFragment, this.exerciseContent);
        this.selectableClasses = selectableClasses;
        this.umlExerciseTextParts = textParts;
    }
    getSelectedClasses() {
        return this.selectableClasses.filter((sc) => sc.selected);
    }
    toggleClassSelected(selectableClass) {
        selectableClass.selected = !selectableClass.selected;
    }
    correct() {
        this.corrected = true;
    }
    getCorrectClasses() {
        return this.selectableClasses.filter((sc) => sc.selected && sc.isCorrect);
    }
    getMissingClasses() {
        return this.selectableClasses.filter((sc) => !sc.selected && sc.isCorrect);
    }
    getWrongClasses() {
        return this.selectableClasses.filter((sc) => sc.selected && !sc.isCorrect);
    }
}
UmlClassSelectionComponent.ɵfac = function UmlClassSelectionComponent_Factory(t) { return new (t || UmlClassSelectionComponent)(); };
UmlClassSelectionComponent.ɵcmp = _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵdefineComponent"]({ type: UmlClassSelectionComponent, selectors: [["it4all-uml-class-selection"]], inputs: { exerciseFragment: "exerciseFragment", exerciseContent: "exerciseContent" }, decls: 26, vars: 12, consts: [[1, "container"], [1, "title", "is-3", "has-text-centered"], [1, "columns"], [1, "column", "is-two-thirds-desktop"], [1, "subtitle", "is-3", "has-text-centered"], [1, "notification", "is-light-grey"], [4, "ngFor", "ngForOf"], [1, "column", "is-one-third-desktop"], [1, "content"], [1, "column"], [1, "button", "is-fullwidth", 3, "disabled", "ngClass", "click"], ["title", "F\u00FChren Sie zuerst die Korrektur aus!", 1, "button", "is-fullwidth", 3, "routerLink", "disabled", "ngClass"], [1, "button", "is-dark-warning", "is-fullwidth", 3, "routerLink"], [4, "ngIf"], [3, "ngClass", "click", 4, "ngIf"], [3, "ngClass", "click"], [1, "subtitle", "is-4", "has-text-centered"], [1, "has-text-dark-success"]], template: function UmlClassSelectionComponent_Template(rf, ctx) { if (rf & 1) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](0, "div", 0);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](1, "h1", 1);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](2, "Klassenselektion");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](3, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](4, "div", 3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](5, "h2", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](6, "Aufgabentext");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](7, "div", 5);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](8, UmlClassSelectionComponent_ng_container_8_Template, 3, 2, "ng-container", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](9, "div", 7);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](10, "h2", 4);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](11, "Gew\u00E4hlte Klassen");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](12, "div", 8);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](13, "ul");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](14, UmlClassSelectionComponent_li_14_Template, 2, 1, "li", 6);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](15, "div", 2);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](16, "div", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](17, "button", 10);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵlistener"]("click", function UmlClassSelectionComponent_Template_button_click_17_listener() { return ctx.correct(); });
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](18, " Korrektur ");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](19, "div", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](20, "button", 11);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](21, " Zum n\u00E4chsten Aufgabenteil ");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](22, "div", 9);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementStart"](23, "a", 12);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtext"](24, "Bearbeiten beenden");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵtemplate"](25, UmlClassSelectionComponent_ng_container_25_Template, 20, 3, "ng-container", 13);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵelementEnd"]();
    } if (rf & 2) {
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](8);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngForOf", ctx.umlExerciseTextParts);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](6);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngForOf", ctx.getSelectedClasses());
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("disabled", ctx.corrected)("ngClass", ctx.corrected ? "is-dark" : "is-link");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("routerLink", _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵpureFunction1"](9, _c0, ctx.nextPartId))("disabled", !ctx.corrected)("ngClass", ctx.corrected ? "is-link" : "is-dark");
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](3);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("routerLink", _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵpureFunction0"](11, _c1));
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵadvance"](2);
        _angular_core__WEBPACK_IMPORTED_MODULE_2__["ɵɵproperty"]("ngIf", ctx.corrected);
    } }, directives: [_angular_common__WEBPACK_IMPORTED_MODULE_3__["NgForOf"], _angular_common__WEBPACK_IMPORTED_MODULE_3__["NgClass"], _angular_router__WEBPACK_IMPORTED_MODULE_4__["RouterLink"], _angular_router__WEBPACK_IMPORTED_MODULE_4__["RouterLinkWithHref"], _angular_common__WEBPACK_IMPORTED_MODULE_3__["NgIf"]], encapsulation: 2 });


/***/ }),

/***/ "y3Rt":
/*!*****************************************************************!*\
  !*** ./src/app/tools/collection-tools/uml/_model/uml-consts.ts ***!
  \*****************************************************************/
/*! exports provided: START_END_SIZE, COLORS, calcRectHeight, MIN_HEIGHT, fontSize, STD_ACTIVITY_ELEMENT_WIDTH, STD_TEXT_ELEMENT_EVENTS, strokeWidth, PAPER_HEIGHT, GRID_SIZE, STD_PADDING, STD_ELEMENT_WIDTH */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "START_END_SIZE", function() { return START_END_SIZE; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "COLORS", function() { return COLORS; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "calcRectHeight", function() { return calcRectHeight; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "MIN_HEIGHT", function() { return MIN_HEIGHT; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "fontSize", function() { return fontSize; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "STD_ACTIVITY_ELEMENT_WIDTH", function() { return STD_ACTIVITY_ELEMENT_WIDTH; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "STD_TEXT_ELEMENT_EVENTS", function() { return STD_TEXT_ELEMENT_EVENTS; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "strokeWidth", function() { return strokeWidth; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "PAPER_HEIGHT", function() { return PAPER_HEIGHT; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "GRID_SIZE", function() { return GRID_SIZE; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "STD_PADDING", function() { return STD_PADDING; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "STD_ELEMENT_WIDTH", function() { return STD_ELEMENT_WIDTH; });

const PAPER_HEIGHT = 700;
const GRID_SIZE = 20;
const STD_PADDING = 10;
const STD_ELEMENT_WIDTH = 200;
const STD_ACTIVITY_ELEMENT_WIDTH = 200;
const START_END_SIZE = 40;
const fontSize = 15;
const strokeWidth = 2;
const MIN_HEIGHT = fontSize + 2 * STD_PADDING;
const STD_TEXT_ELEMENT_EVENTS = {
    click: 'onLeftClick',
    contextmenu: 'onRightClick'
};
function calcRectHeight(lines) {
    return Math.max((lines.length * fontSize) + (2 * STD_PADDING), MIN_HEIGHT);
}
var COLORS;
(function (COLORS) {
    // noinspection JSUnusedGlobalSymbols
    COLORS["Black"] = "#000000";
    COLORS["Navy"] = "#000080";
    COLORS["DarkBlue"] = "#00008B";
    COLORS["MediumBlue"] = "#0000CD";
    COLORS["Blue"] = "#0000FF";
    COLORS["DarkGreen"] = "#006400";
    COLORS["Green"] = "#008000";
    COLORS["Teal"] = "#008080";
    COLORS["DarkCyan"] = "#008B8B";
    COLORS["DeepSkyBlue"] = "#00BFFF";
    COLORS["DarkTurquoise"] = "#00CED1";
    COLORS["MediumSpringGreen"] = "#00FA9A";
    COLORS["Lime"] = "#00FF00";
    COLORS["SpringGreen"] = "#00FF7F";
    COLORS["Aqua"] = "#00FFFF";
    COLORS["Cyan"] = "#00FFFF";
    COLORS["MidnightBlue"] = "#191970";
    COLORS["DodgerBlue"] = "#1E90FF";
    COLORS["LightSeaGreen"] = "#20B2AA";
    COLORS["ForestGreen"] = "#228B22";
    COLORS["SeaGreen"] = "#2E8B57";
    COLORS["DarkSlateGray"] = "#2F4F4F";
    COLORS["DarkSlateGrey"] = "#2F4F4F";
    COLORS["LimeGreen"] = "#32CD32";
    COLORS["MediumSeaGreen"] = "#3CB371";
    COLORS["Turquoise"] = "#40E0D0";
    COLORS["RoyalBlue"] = "#4169E1";
    COLORS["SteelBlue"] = "#4682B4";
    COLORS["DarkSlateBlue"] = "#483D8B";
    COLORS["MediumTurquoise"] = "#48D1CC";
    COLORS["Indigo"] = "#4B0082";
    COLORS["DarkOliveGreen"] = "#556B2F";
    COLORS["CadetBlue"] = "#5F9EA0";
    COLORS["CornflowerBlue"] = "#6495ED";
    COLORS["RebeccaPurple"] = "#663399";
    COLORS["MediumAquaMarine"] = "#66CDAA";
    COLORS["DimGray"] = "#696969";
    COLORS["DimGrey"] = "#696969";
    COLORS["SlateBlue"] = "#6A5ACD";
    COLORS["OliveDrab"] = "#6B8E23";
    COLORS["SlateGray"] = "#708090";
    COLORS["SlateGrey"] = "#708090";
    COLORS["LightSlateGray"] = "#778899";
    COLORS["LightSlateGrey"] = "#778899";
    COLORS["MediumSlateBlue"] = "#7B68EE";
    COLORS["LawnGreen"] = "#7CFC00";
    COLORS["Chartreuse"] = "#7FFF00";
    COLORS["Aquamarine"] = "#7FFFD4";
    COLORS["Maroon"] = "#800000";
    COLORS["Purple"] = "#800080";
    COLORS["Olive"] = "#808000";
    COLORS["Gray"] = "#808080";
    COLORS["Grey"] = "#808080";
    COLORS["SkyBlue"] = "#87CEEB";
    COLORS["LightSkyBlue"] = "#87CEFA";
    COLORS["BlueViolet"] = "#8A2BE2";
    COLORS["DarkRed"] = "#8B0000";
    COLORS["DarkMagenta"] = "#8B008B";
    COLORS["SaddleBrown"] = "#8B4513";
    COLORS["DarkSeaGreen"] = "#8FBC8F";
    COLORS["LightGreen"] = "#90EE90";
    COLORS["MediumPurple"] = "#9370DB";
    COLORS["DarkViolet"] = "#9400D3";
    COLORS["PaleGreen"] = "#98FB98";
    COLORS["DarkOrchid"] = "#9932CC";
    COLORS["YellowGreen"] = "#9ACD32";
    COLORS["Sienna"] = "#A0522D";
    COLORS["Brown"] = "#A52A2A";
    COLORS["DarkGray"] = "#A9A9A9";
    COLORS["DarkGrey"] = "#A9A9A9";
    COLORS["LightBlue"] = "#ADD8E6";
    COLORS["GreenYellow"] = "#ADFF2F";
    COLORS["PaleTurquoise"] = "#AFEEEE";
    COLORS["LightSteelBlue"] = "#B0C4DE";
    COLORS["PowderBlue"] = "#B0E0E6";
    COLORS["FireBrick"] = "#B22222";
    COLORS["DarkGoldenRod"] = "#B8860B";
    COLORS["MediumOrchid"] = "#BA55D3";
    COLORS["RosyBrown"] = "#BC8F8F";
    COLORS["DarkKhaki"] = "#BDB76B";
    COLORS["Silver"] = "#C0C0C0";
    COLORS["MediumVioletRed"] = "#C71585";
    COLORS["IndianRed"] = "#CD5C5C";
    COLORS["Peru"] = "#CD853F";
    COLORS["Chocolate"] = "#D2691E";
    COLORS["Tan"] = "#D2B48C";
    COLORS["LightGray"] = "#D3D3D3";
    COLORS["LightGrey"] = "#D3D3D3";
    COLORS["Thistle"] = "#D8BFD8";
    COLORS["Orchid"] = "#DA70D6";
    COLORS["GoldenRod"] = "#DAA520";
    COLORS["PaleVioletRed"] = "#DB7093";
    COLORS["Crimson"] = "#DC143C";
    COLORS["Gainsboro"] = "#DCDCDC";
    COLORS["Plum"] = "#DDA0DD";
    COLORS["BurlyWood"] = "#DEB887";
    COLORS["LightCyan"] = "#E0FFFF";
    COLORS["Lavender"] = "#E6E6FA";
    COLORS["DarkSalmon"] = "#E9967A";
    COLORS["Violet"] = "#EE82EE";
    COLORS["PaleGoldenRod"] = "#EEE8AA";
    COLORS["LightCoral"] = "#F08080";
    COLORS["Khaki"] = "#F0E68C";
    COLORS["AliceBlue"] = "#F0F8FF";
    COLORS["HoneyDew"] = "#F0FFF0";
    COLORS["Azure"] = "#F0FFFF";
    COLORS["SandyBrown"] = "#F4A460";
    COLORS["Wheat"] = "#F5DEB3";
    COLORS["Beige"] = "#F5F5DC";
    COLORS["WhiteSmoke"] = "#F5F5F5";
    COLORS["MintCream"] = "#F5FFFA";
    COLORS["GhostWhite"] = "#F8F8FF";
    COLORS["Salmon"] = "#FA8072";
    COLORS["AntiqueWhite"] = "#FAEBD7";
    COLORS["Linen"] = "#FAF0E6";
    COLORS["LightGoldenRodYellow"] = "#FAFAD2";
    COLORS["OldLace"] = "#FDF5E6";
    COLORS["Red"] = "#FF0000";
    COLORS["Fuchsia"] = "#FF00FF";
    COLORS["Magenta"] = "#FF00FF";
    COLORS["DeepPink"] = "#FF1493";
    COLORS["OrangeRed"] = "#FF4500";
    COLORS["Tomato"] = "#FF6347";
    COLORS["HotPink"] = "#FF69B4";
    COLORS["Coral"] = "#FF7F50";
    COLORS["DarkOrange"] = "#FF8C00";
    COLORS["LightSalmon"] = "#FFA07A";
    COLORS["Orange"] = "#FFA500";
    COLORS["LightPink"] = "#FFB6C1";
    COLORS["Pink"] = "#FFC0CB";
    COLORS["Gold"] = "#FFD700";
    COLORS["PeachPuff"] = "#FFDAB9";
    COLORS["NavajoWhite"] = "#FFDEAD";
    COLORS["Moccasin"] = "#FFE4B5";
    COLORS["Bisque"] = "#FFE4C4";
    COLORS["MistyRose"] = "#FFE4E1";
    COLORS["BlanchedAlmond"] = "#FFEBCD";
    COLORS["PapayaWhip"] = "#FFEFD5";
    COLORS["LavenderBlush"] = "#FFF0F5";
    COLORS["SeaShell"] = "#FFF5EE";
    COLORS["Cornsilk"] = "#FFF8DC";
    COLORS["LemonChiffon"] = "#FFFACD";
    COLORS["FloralWhite"] = "#FFFAF0";
    COLORS["Snow"] = "#FFFAFA";
    COLORS["Yellow"] = "#FFFF00";
    COLORS["LightYellow"] = "#FFFFE0";
    COLORS["Ivory"] = "#FFFFF0";
    COLORS["White"] = "#FFFFFF";
})(COLORS || (COLORS = {}));


/***/ }),

/***/ "zUnb":
/*!*********************!*\
  !*** ./src/main.ts ***!
  \*********************/
/*! no exports provided */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _angular_platform_browser__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! @angular/platform-browser */ "jhN1");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "fXoL");
/* harmony import */ var _app_app_module__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./app/app.module */ "ZAI4");
/* harmony import */ var _environments_environment__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./environments/environment */ "AytR");




if (_environments_environment__WEBPACK_IMPORTED_MODULE_3__["environment"].production) {
    Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["enableProdMode"])();
}
_angular_platform_browser__WEBPACK_IMPORTED_MODULE_0__["platformBrowser"]().bootstrapModule(_app_app_module__WEBPACK_IMPORTED_MODULE_2__["AppModule"])
    .catch(err => console.error(err));


/***/ }),

/***/ "zn8P":
/*!******************************************************!*\
  !*** ./$$_lazy_route_resource lazy namespace object ***!
  \******************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

function webpackEmptyAsyncContext(req) {
	// Here Promise.resolve().then() is used instead of new Promise() to prevent
	// uncaught exception popping up in devtools
	return Promise.resolve().then(function() {
		var e = new Error("Cannot find module '" + req + "'");
		e.code = 'MODULE_NOT_FOUND';
		throw e;
	});
}
webpackEmptyAsyncContext.keys = function() { return []; };
webpackEmptyAsyncContext.resolve = webpackEmptyAsyncContext;
module.exports = webpackEmptyAsyncContext;
webpackEmptyAsyncContext.id = "zn8P";

/***/ })

},[[0,"runtime","vendor"]]]);
//# sourceMappingURL=main-es2015.js.map