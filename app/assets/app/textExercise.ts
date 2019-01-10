import * as $ from 'jquery';

let showSampleSolBtn: JQuery;

function escapeHtml(unescapedHtml: string): string {
    return unescapedHtml
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
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