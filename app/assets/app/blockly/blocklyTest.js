let generateCodeBtn;

$(() => {
    const workspace = Blockly.inject('blocklyDiv', {toolbox: document.getElementById('toolbox')});

    generateCodeBtn = $('#generateCodeBtn');
    generateCodeBtn.on('click', () => {
        const code = Blockly.Python.workspaceToCode(workspace);
        console.warn(code);

        $('#blocklyCode').html(code);
    });
});