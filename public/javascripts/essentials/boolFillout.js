function readValues() {
    let assignments = [];

    $('#valueTableBody').find('tr').each(function (index, row) {
        let partAssignments = {};
        $(row).find('.text-center').each(function (index, cell) {
            partAssignments[cell.dataset.variable] = cell.dataset.value === "true"
        });
        partAssignments[lerVar] = $(row).find('td > input').val() !== "0";
        assignments.push(partAssignments)
    });

    return {
        formula: formula,
        assignments: assignments
    };
}

function onAjaxSuccess(response) {
    for (const assignment of response) {
        let elem = $('#' + assignment.id);
        if (assignment[lerVar] === assignment[solVar]) {
            elem.removeClass("danger").addClass("success");
        } else {
            elem.removeClass("success").addClass("danger");
        }
    }
}

function testSol() {
    $.ajax({
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        url: url,
        data: JSON.stringify(readValues()),
        async: true,
        success: onAjaxSuccess
    });
}