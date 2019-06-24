import {domReady} from "../otherHelpers";

const WHITE_SPACES: RegExp = /\s+/g;

domReady(() => {
    let solved: boolean = false;

    const firstSummandBase: number = parseInt(document.querySelector<HTMLElement>('#firstSummandBaseSub').innerText);
    const firstSummandInput: HTMLInputElement = document.querySelector<HTMLInputElement>('#firstSummand');
    const firstSummandValue = parseInt(firstSummandInput.value.replace(WHITE_SPACES, ''), firstSummandBase);

    const secondSummandBase: number = parseInt(document.querySelector<HTMLElement>('#secondSummandBaseSub').innerText);
    const secondSummandInput: HTMLInputElement = document.querySelector<HTMLInputElement>('#secondSummand');
    const secondSummandValue = parseInt(secondSummandInput.value.replace(WHITE_SPACES, ''), secondSummandBase);

    const solutionInput: HTMLInputElement = document.querySelector<HTMLInputElement>('#solution');
    const solutionBase: number = parseInt(document.querySelector<HTMLElement>('#solutionBaseSub').innerText);

    const testBtn: HTMLButtonElement = document.querySelector<HTMLButtonElement>('#testBtn');

    testBtn.onclick = () => {
        const solutionString: string = (solutionInput.value as string).replace(WHITE_SPACES, '').split('').reverse().join('');

        if (solutionString === '') {
            alert('Sie können keine leere Lösung abgeben!');
            return;
        }

        const solutionValue: number = parseInt(solutionString, solutionBase);

        solved = (firstSummandValue + secondSummandValue) === solutionValue;

        const correctnessHookElement = document.querySelector<HTMLElement>('#correctnessHook');

        solutionInput.classList.remove('is-valid', 'is-invalid');

        if (solved) {
            solutionInput.classList.add('is-valid');
            correctnessHookElement.innerHTML = '<h1 class="text-center">&check;</h1>';
        } else {
            solutionInput.classList.add('is-invalid');
            correctnessHookElement.innerHTML = '';
        }
    };

    document.onkeypress = (event) => {
        if (event.key === 'Enter') {
            if (solved) {
                document.querySelector<HTMLAnchorElement>('#nextExerciseAnchor').click();
            } else {
                testBtn.click();
            }
        }
    };
});

