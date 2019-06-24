import {domReady} from "../otherHelpers";

const WHITE_SPACES: RegExp = /\s+/g;

function invertBits(num: string): string {
    return num
        .replace(/1/g, 'a')
        .replace(/0/g, '1')
        .replace(/a/g, '0')
}

domReady(() => {
    let solved: boolean = false;

    const startValueInput: HTMLInputElement = document.querySelector<HTMLInputElement>('#value');
    const startValue: number = parseInt(startValueInput.value);

    const binaryAbsInput = document.querySelector<HTMLInputElement>('#binaryAbs');
    const invertedAbsInput = document.querySelector<HTMLInputElement>('#invertedAbs');
    const solutionInput = document.querySelector<HTMLInputElement>('#solution');

    const testBtn = document.querySelector<HTMLButtonElement>('#testBtn');

    testBtn.onclick = () => {
        binaryAbsInput.classList.remove('is-valid', 'is-invalid');
        invertedAbsInput.classList.remove('is-valid', 'is-invalid');
        solutionInput.classList.remove('is-valid', 'is-invalid');

        // Correct binaryAbs
        const binaryAbsStr: string = binaryAbsInput.value.replace(WHITE_SPACES, '');
        const binaryAbsValue: number = parseInt(binaryAbsStr, 2);

        if (binaryAbsStr.length !== 8) {
            alert('Die Binärstellung des positiven Wertes muss in 8 Bit sein!');
            binaryAbsInput.classList.add(`is-invalid`);
            return;
        }

        const binaryAbsCorrect: boolean = startValue === -binaryAbsValue;

        binaryAbsInput.classList.add(`is-${binaryAbsCorrect ? '' : 'in'}valid`);

        // Correct invertedAbs
        const invertedAbsStr: string = invertedAbsInput.value.replace(WHITE_SPACES, '');
        const invertedAbsPosValue: number = parseInt(invertedAbsStr, 2);
        const invertedAbsCorrect: boolean = binaryAbsCorrect && invertBits(binaryAbsStr) === invertedAbsStr;

        invertedAbsInput.classList.add(`is-${invertedAbsCorrect ? '' : 'in'}valid`);

        // Correct twoComp
        const solutionStr: string = solutionInput.value.replace(WHITE_SPACES, '');
        const solution: number = parseInt(solutionStr, 2);
        solved = solution === (invertedAbsPosValue + 1 % 256);

        solutionInput.classList.add(`is-${solved ? '' : 'in'}valid`);
    };

    document.onkeypress = (event) => {
        if (event.key === 'Enter' && document.activeElement.id !== 'noteTextArea') {
            if (solved) {
                document.querySelector<HTMLAnchorElement>('#nextExerciseAnchor').click();
            } else {
                testBtn.click();
            }
        }
    };
});

