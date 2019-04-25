export {BoolSolution, readBoolSolution}

interface BoolSolution {
    formula: string
    tableRows: BoolTableRow[]
}

interface BoolTableRow {
    assignments: Assignment[]
}

interface Assignment {
    variable: string
    value: boolean
}

function readBoolSolution(valueTableBody: HTMLElement, isFillout: boolean): BoolSolution {

    let formula: string;
    if (isFillout) {
        formula = document.querySelector<HTMLSpanElement>('#formula').dataset['formula'];
    } else {
        formula = document.querySelector<HTMLInputElement>('#solution').value;
    }

    let tableRows: BoolTableRow[] =
        Array.from(valueTableBody.querySelectorAll<HTMLTableRowElement>('tr'))
            .map((row: HTMLElement) => {

                let assignments: Assignment[] = [];

                row.querySelectorAll('[data-variable]').forEach((cell: HTMLElement) =>
                    assignments.push({variable: cell.dataset.variable, value: cell.dataset.value === 'true'})
                );

                if (isFillout) {
                    const value = row.querySelector<HTMLButtonElement>('button').innerText === '1';
                    assignments.push({variable: 'y', value});
                }

                return {assignments};
            });

    return {formula, tableRows};
}
