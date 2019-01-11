import * as $ from 'jquery';

export {focusOnCorrection, testTextExerciseSolution};

let showSampleSolBtn: JQuery;

function focusOnCorrection(): void {
    $('#showCorrectionTabA').get()[0].click();
}

function escapeHtml(unescapedHtml: string): string {
    return unescapedHtml
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function testTextExerciseSolution(testBtn: JQuery, solution: any, success, error): void {
    testBtn.prop('disabled', true);

    const isDocumentPart: boolean = $('#exercisePart').val() === 'document';

    // const solution: string = editor.getValue();

    $.ajax({
        type: 'PUT',
        dataType: 'json', // return type
        contentType: 'application/json', // type of message to server
        url: testBtn.data('url'),
        data: JSON.stringify(solution),
        async: true,
        beforeSend: (xhr) => {
            const token = $('input[name="csrfToken"]').val() as string;
            xhr.setRequestHeader("Csrf-Token", token)
        },
        success, //: isDocumentPart ? onXmlDocumentCorrectionSuccess : onXmlGrammarCorrectionSuccess,
        error, //: onXmlCorrectionError
    });
}


function onShowSampleSolutionSuccess(solutions: string[]): void {
    let solutionRenders: string[] = [];

    for (const solution of solutions) {
        solutionRenders.push(`<pre>${escapeHtml(solution)}</pre>`);
    }

    $('#sampleSolTab').html(`
<div class="card">
    <div class="card-body bg-light">
        ${solutionRenders.join("\n")}
    </div>
</div>`);
}

function onShowSampleSolutionError(jqXHR): void {
    console.error(jqXHR.responseText);
}

$(() => {
    showSampleSolBtn = $('#showSampleSolBtn');
    showSampleSolBtn.on('click', () => {
        showSampleSolBtn.prop('disabled', true);
        $.ajax({
            url: showSampleSolBtn.data('url'),
            method: 'GET',
            success: onShowSampleSolutionSuccess,
            error: onShowSampleSolutionError
        });
    });
});