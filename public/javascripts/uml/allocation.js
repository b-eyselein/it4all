function readMemberElement(elem) {
    if (elem.checked) {
        return {
            name: $(elem).data('name'),
            type: $(elem).data('type')
        };
    } else {
        return null;
    }
}

function prepareFormForSubmitting() {
    let classes = [];

    $('.panel.panel-default').each((index, elem) => {
        let className = elem.dataset.clazz;

        let attrCheckboxes = $('input[data-class="' + className + '"][data-membertype="attribute"]');
        let methodCheckboxes = $('input[data-class="' + className + '"][data-membertype="method"]');

        classes.push({
            name: className,
            attributes: attrCheckboxes.map((index, elem) => readMemberElement(elem)).get(),
            methods: methodCheckboxes.map((index, elem) => readMemberElement(elem)).get()
        });
    });

    $('#learnerSolution').val(JSON.stringify({
        classes,
        associations: [],
        implementations: []
    }));
}