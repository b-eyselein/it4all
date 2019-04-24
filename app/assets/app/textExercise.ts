export function focusOnCorrection(): void {
    document.querySelector<HTMLAnchorElement>('#showCorrectionTabA').click();
}


export function testTextExerciseSolution<SolType, ResType>(testBtn: HTMLButtonElement, solution: SolType, onSuccess: (ResType) => void): void {
    testBtn.disabled = true;

    const headers: Headers = new Headers({
        'Content-Type': 'application/json',
        'Csrf-Token': document.querySelector<HTMLInputElement>('input[name="csrfToken"]').value
    })

    fetch(testBtn.dataset['url'], {method: 'PUT', headers, body: JSON.stringify(solution)})
        .then(response => {
            if (response.status === 200) {
                response.json().then(onSuccess);
            } else {
                response.text().then(errorText => console.error(errorText));
            }
        }).catch(reason => console.error(reason));
}
