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

$(() => {
    showSampleSolBtn = $('#showSampleSolBtn');
    showSampleSolBtn.on('click', () => {
        $.ajax({
            url: showSampleSolBtn.data('url'),
            method: 'GET',
            success: (data: string) => {
                $('#sampleSolTab').html(`
<div class="card">
    <div class="card-body bg-light">
        <pre>${escapeHtml(data)}</pre>
    </div>
</div>`);
            }
        });
    });
});