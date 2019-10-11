import {domReady} from "../otherHelpers";

const WHITE_SPACES: RegExp = /\s+/g;

domReady(() => {
    let solved = false;

    const startNumberBase: number = parseInt(document.querySelector<HTMLElement>('#startNumBaseSub').innerText);
    const startNumberInput: HTMLInputElement = document.querySelector<HTMLInputElement>('#startValue');
    const startValue: number = parseInt(startNumberInput.value.replace(WHITE_SPACES, ''), startNumberBase);

    const targetNumberBase: number = parseInt(document.querySelector<HTMLElement>('#targetNumBaseSub').innerText);
    const targetNumberInput = document.querySelector<HTMLInputElement>('#targetValue');

    const correctnessHookElement = document.querySelector<HTMLElement>('#correctnessHook');

    const testBtn = document.querySelector<HTMLButtonElement>('#testBtn');
    testBtn.onclick = () => {
        const solutionString = targetNumberInput.value.replace(WHITE_SPACES, '');

        if (solutionString === '') {
            alert('Sie können keine leere Lösung abgeben!');
            return;
        }

        const targetValue: number = parseInt(solutionString, targetNumberBase);

        solved = startValue === targetValue;

        targetNumberInput.classList.remove('is-valid', 'is-invalid');

        if (solved) {
            targetNumberInput.classList.add('is-valid');
            correctnessHookElement.innerHTML = '<h1 class="text-center">&check;</h1>';
        } else {
            targetNumberInput.classList.add('is-invalid');
            correctnessHookElement.innerHTML = '';
        }
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
