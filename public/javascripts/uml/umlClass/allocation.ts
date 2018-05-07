interface Class {
    name: string,
    attributes: ClassMember[],
    methods: ClassMember[]
}

interface ClassMember {
    visibility: string,
    name: string
    type: string
}

function readMemberElement(elem: HTMLInputElement): ClassMember {
    if (elem.checked) {
        return {
            visibility: 'public',
            name: elem.dataset['value'],
            type: elem.dataset['type']
        };
    } else {
        return null;
    }
}

function readAllocation(): boolean {
    try {
        let classPanels: Element[] = Array.from(document.getElementsByClassName('panel panel-default'));

        let classes: Class[] = classPanels.map((elem: HTMLElement) => {
            let name = elem.dataset['clazz'];

            let attrCheckBoxes: HTMLInputElement[] = Array.from(elem.querySelector('section.attributeList').getElementsByTagName('input'));
            let attributes: ClassMember[] = attrCheckBoxes.map((elem) => readMemberElement(elem)).filter((c) => c != null);

            let methodCheckBoxes: HTMLInputElement[] = Array.from(elem.querySelector('section.methodList').getElementsByTagName('input'));
            let methods: ClassMember[] = methodCheckBoxes.map((elem) => readMemberElement(elem)).filter((c) => c != null);

            return {name, attributes, methods};
        });

        (document.getElementById('learnerSolution') as HTMLInputElement).value = JSON.stringify({
            classes,
            associations: [],
            implementations: []
        });
        return true;
    } catch (err) {
        console.error(err);
        return false;
    }
}