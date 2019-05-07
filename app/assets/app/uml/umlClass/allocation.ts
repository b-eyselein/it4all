import {UmlClassAttribute, UmlClassMethod, UmlSolution} from "../umlInterfaces";
import {UmlClassDiagCorrectionResult} from "./classDiagCorrection";
import {domReady, testExerciseSolution} from "../../otherHelpers";

let testBtn: HTMLButtonElement;

function readAttributeElement(element: HTMLInputElement): UmlClassAttribute | null {
    if (element.checked) {
        return {
            visibility: element.dataset['visibility'], name: element.dataset['value'], type: element.dataset['type'],
            isDerived: false, isAbstract: false, isStatic: false
        };
    } else {
        return null;
    }
}


function readMethodElement(element: HTMLInputElement): UmlClassMethod | null {
    if (element.checked) {
        return {
            visibility: element.dataset['visibility'], name: element.dataset['value'], type: element.dataset['type'],
            parameters: '', isAbstract: false, isStatic: false
        };
    } else {
        return null;
    }
}

function readAllocation(): UmlSolution {
    let solution: UmlSolution = {
        classes: [], associations: [], implementations: []
    };

    try {
        Array.from(document.querySelectorAll<HTMLDivElement>('.card[data-classname]'))
            .map((element: HTMLDivElement) => {
                let attributes: UmlClassAttribute[] = [];

                element.querySelector<HTMLDivElement>('section.attributeList')
                    .querySelectorAll<HTMLInputElement>('input')
                    .forEach((element: HTMLInputElement) => {
                        const readAttribute: UmlClassAttribute | null = readAttributeElement(element);
                        if (readAttribute != null) {
                            attributes.push(readAttribute);
                        }
                    });

                let methods: UmlClassMethod[] = [];

                element.querySelector<HTMLDivElement>('section.methodList')
                    .querySelectorAll<HTMLInputElement>('input')
                    .forEach((element: HTMLInputElement) => {
                        const readMethod: UmlClassMethod | null = readMethodElement(element);
                        if (readMethod != null) {
                            methods.push(readMethod);
                        }
                    });

                return {
                    classType: element.dataset['classtype'],
                    name: element.dataset['classname'],
                    attributes,
                    methods
                };
            });
    } catch (err) {
        console.error(err);
    }

    return solution;
}

function onMemberAllocationCorrectionSuccess(response: UmlClassDiagCorrectionResult): void {
    // FIXME: implement!
    console.warn(JSON.stringify(response, null, 2));
}

domReady(() => {
    testBtn = document.querySelector<HTMLButtonElement>('#testBtn');
    testBtn.onclick = () => {
        testExerciseSolution<UmlSolution, UmlClassDiagCorrectionResult>(testBtn, readAllocation(), onMemberAllocationCorrectionSuccess)
    };

    let solutionChanged = false; // TODO!

    document.querySelector<HTMLAnchorElement>('#endSolveAnchor').onclick = () => {
        return !solutionChanged || confirm("Ihre Lösung hat sich seit dem letzten Speichern (Korrektur) geändert. Wollen Sie die Bearbeitung beenden?");
    }
});
