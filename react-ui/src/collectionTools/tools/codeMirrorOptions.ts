import CodeMirror from 'codemirror';

export function getDefaultCodeMirrorEditorOptions(mode: string): CodeMirror.EditorConfiguration {
  return {
    mode,
    lineNumbers: true,
    theme: 'eclipse',
    indentUnit: 4,
    readOnly: false,
    extraKeys: {
      'Tab': (cm) => cm.execCommand('indentMore'),
      'Shift-Tab': (cm) => cm.execCommand('indentLess'),
    }
  };
}

