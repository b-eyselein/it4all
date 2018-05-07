function readMemberElement(elem) {
    if (elem.checked) {
        return {
            visibility: 'public',
            name: elem.dataset['value'],
            type: elem.dataset['type']
        };
    }
    else {
        return null;
    }
}
function readAllocation() {
    try {
        let classPanels = Array.from(document.getElementsByClassName('panel panel-default'));
        let classes = classPanels.map((elem) => {
            let name = elem.dataset['clazz'];
            let attrCheckBoxes = Array.from(elem.querySelector('section.attributeList').getElementsByTagName('input'));
            let attributes = attrCheckBoxes.map((elem) => readMemberElement(elem)).filter((c) => c != null);
            let methodCheckBoxes = Array.from(elem.querySelector('section.methodList').getElementsByTagName('input'));
            let methods = methodCheckBoxes.map((elem) => readMemberElement(elem)).filter((c) => c != null);
            return { name, attributes, methods };
        });
        document.getElementById('learnerSolution').value = JSON.stringify({
            classes,
            associations: [],
            implementations: []
        });
        return true;
    }
    catch (err) {
        console.error(err);
        return false;
    }
}
//# sourceMappingURL=allocation.js.map