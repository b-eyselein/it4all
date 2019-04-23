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

export function initShowSampleSolBtn<T>(renderSampleSolResponse: (T) => string): void {
    const showSampleSolBtn = document.querySelector<HTMLButtonElement>('#showSampleSolBtn');

    showSampleSolBtn.onclick = () => {
        showSampleSolBtn.disabled = true;
        fetch(showSampleSolBtn.dataset['url'])
            .then(response => {
                if (response.status === 200) {
                    response.json().then(response => {
                        document.querySelector<HTMLDivElement>('#sampleSolDiv').innerHTML = renderSampleSolResponse(response);
                    });
                    showSampleSolBtn.remove();
                } else {
                    response.text().then(error => console.error(error));
                }
            })
            .catch(reason => console.error(reason))
    }
}
