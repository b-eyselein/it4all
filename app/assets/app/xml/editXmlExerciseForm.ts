import * as $ from 'jquery';


let addSampleSolBtn: JQuery<HTMLButtonElement>;
let sampleSolsDiv: JQuery<HTMLDivElement>;

/**
 * Sets the click event for all delete buttons for sample solutions.
 * We need to 'delete' (unbind...) the existing events so they do not
 * get triggered multiple times.
 */
function instantiateDeleteSampleSolBtnClickEvents(): void {
    $('.deleteSampleSolBtn').unbind('click').on('click', deleteSampleSolution);
}

function deleteSampleSolution(event: Event): void {
    if (sampleSolsDiv.children().length <= 1) {
        alert("There has got to be at least one sample solution!");
        return;
    }

    const solutionID: number = $(event.target).data('solutionid');
    if (confirm("Wollen Sie diese Musterlösung mit der ID " + solutionID + " wirklich löschen?")) {
        $('#sample_row_' + solutionID).remove();

        // FIXME: count down all greater solution ids?
    }
}


$(() => {
    sampleSolsDiv = $('#sampleSolsDiv');
    addSampleSolBtn = $('#addSampleSolBtn');
    addSampleSolBtn.on('click', () => {
        const newId: number = sampleSolsDiv.children().length;

        sampleSolsDiv.append(`
<div class="form-group row" id="sample_row_${newId}">
    <div class="col-md-2">
        <div class="form-group">
            <input type="number" class="form-control" name="samples[${newId}].id" id="samples_${newId}_id" value="${newId}" readonly="">
        </div>
        <div class="form-group">
            <button type="button" class="btn btn-danger btn-block deleteSampleSolBtn" title="Musterlösung löschen" data-solutionid="${newId}">
                <span class="octicon octicon-x"></span>
            </button>
        </div>
    </div>
    <div class="col-md-5">
        <textarea class="form-control" name="samples[${newId}].grammar" id="samples_${newId}_grammar" rows="10" required="" placeholder="<!ELEMENT root ...>"></textarea>
    </div>
    <div class="col-md-5">
        <textarea class="form-control" name="samples[${newId}].document" id="samples_${newId}_document" rows="10" required="" placeholder="<root></root>"></textarea>
    </div>
</div>`.trim());

        instantiateDeleteSampleSolBtnClickEvents();
    });

    instantiateDeleteSampleSolBtnClickEvents();
});
