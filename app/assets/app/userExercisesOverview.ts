function onUpdateVersionSelect(event: Event): void {

    const targetSelect: JQuery<HTMLSelectElement> = $(event.target as HTMLSelectElement);

    const id: number = targetSelect.data('id');
    const newVersion: string = targetSelect.val() as string;

    $('.ex_link_' + id).each((_, link: HTMLElement) => {
        if (link instanceof HTMLAnchorElement && link.href !== "") {

            if (link.href.includes("?version=")) {
                // Remove old version parameter
                link.href = link.href.split("?")[0];
            }

            link.href = link.href + "?version=" + newVersion;
        }
    });
}

$(() => {
    const versionSelects = $('.version_select');
    versionSelects.on('change', onUpdateVersionSelect);
});