function readMemberElement(elem) {
    if (elem.checked) {
        return {
            name: $(elem).data('value'),
            type: $(elem).data('type')
        };
    } else {
        return null;
    }
}

function prepareFormForSubmitting() {
    let classes = [];

    $('.panel.panel-default').each((index, elem) => {
        let className = $(elem).data('clazz');

        let attrCheckboxes = $('input[data-class="' + className + '"][data-membertype="attribute"]');
        let methodCheckboxes = $('input[data-class="' + className + '"][data-membertype="method"]');

        classes.push({
            name: className,
            attributes: attrCheckboxes.map((index, elem) => readMemberElement(elem)).get(),
            methods: methodCheckboxes.map((index, elem) => readMemberElement(elem)).get()
        });
    }).get();

    // classes.forEach((c) => console.warn(JSON.stringify(c)));

    $('#learnerSolution').val(JSON.stringify({
        classes,
        associations: [],
        implementations: []
    }));
}