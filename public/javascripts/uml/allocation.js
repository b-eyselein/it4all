function prepareFormForSubmitting() {
    let classes = [];

    $('.panel.panel-default').each((index, elem) => {
        let className = elem.dataset.clazz;

        let attrCheckboxes = $('input[data-class="' + className + '"][data-membertype="attribute"]');
        let methodCheckboxes = $('input[data-class="' + className + '"][data-membertype="method"]');

        classes.push({
            name: className,
            attributes: attrCheckboxes.map((index, elem) => {
                return (elem.checked) ? {name: elem.dataset.name, type: elem.dataset.type} : null
            }).get(),
            methods: methodCheckboxes.map((index, elem) => {
                return (elem.checked) ? {name: elem.dataset.name, returnType: elem.dataset.type} : null
            }).get()
        })
    });


    $('#learnerSolution').val(JSON.stringify({
        classes: classes,
        associations: [],
        implementations: []
    }));
}