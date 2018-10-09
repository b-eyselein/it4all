let userFilter: JQuery<HTMLSelectElement>;

function filterTable(): void {

}

$(() => {
    userFilter = $('#userFilter');
    userFilter.on('change', () => {
        console.error(userFilter.val());
        // FIXME: implement...
    });
});