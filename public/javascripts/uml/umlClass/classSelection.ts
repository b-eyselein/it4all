// Class selection

let chosenClasses: string[] = [];

function prepareFormForSubmitting(): void {
    let solutionToSend = {
        associations: [],
        implementations: [],
        classes: chosenClasses.map(function (clazz) {
            return {
                name: clazz,
                classType: 'CLASS',
                methods: [],
                attributes: []
            };
        })
    };

    $('#learnerSolution').val(JSON.stringify(solutionToSend))
}

function asList(array: string[]): string {
    return array.length === 0 ? '<li>--</li>' : '<li>' + array.join('</li><li>') + '</li>';
}

function select(span): void {
    let baseform = span.dataset.baseform;

    if (chosenClasses.indexOf(baseform) < 0) {
        chosenClasses.push(baseform);
    } else {
        chosenClasses.splice(chosenClasses.indexOf(baseform), 1);
    }

    $('#classesList').html(asList(chosenClasses));

    for (let otherSpan of $('#exercisetext').find('span').get()) {
        if (chosenClasses.indexOf(otherSpan.dataset.baseform) < 0) {
            otherSpan.className = 'non-marked';
        } else {
            otherSpan.className = 'marked bg-info';
        }
    }
}

