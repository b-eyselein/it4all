import * as $ from 'jquery';
import {domReady} from "../otherHelpers";

domReady(() => {

    document.getElementById('addSampleSolutionBtn').onclick = () => {
        const sampleSolDiv = $('#sampleSolDiv');
        const children = sampleSolDiv.children().length + 1;

        sampleSolDiv.append(`
<div class="form-group">
    <label for="samples[${children}]">Musterlösung ${children}:</label>
    <textarea rows="4" class="form-control" name="samples[]" id="samples[${children}]"></textarea>
</div>`);

    }
});
