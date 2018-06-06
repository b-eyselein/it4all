import * as $ from "jquery";

export {BoolSolution,  readBoolSolution}

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

function readBoolSolution(valueTableBody: JQuery, isFillout: boolean): BoolSolution {

    let formula: string;
    if (isFillout) {
        formula = $('#formula').data('formula');
    } else {
        formula = $('#solution').val() as string;
    }

    let tableRows: BoolTableRow[] = [];

    valueTableBody.find('tr').each((index: number, row: HTMLElement) => {
        let assignments: Assignment[] = [];

        $(row).find('[data-variable]').each(function (index, cell: HTMLElement) {
            assignments.push({
                variable: cell.dataset.variable,
                value: cell.dataset.value === 'true'
            });
        });

        if (isFillout) {
            assignments.push({variable: 'y', value: $(row).find('button').text() === '1'});
        }

        tableRows.push({assignments});
    });


    return {formula, tableRows};
}