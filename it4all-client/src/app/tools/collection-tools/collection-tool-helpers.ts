export function getDefaultEditorOptions(mode: string) {
  // noinspection JSUnusedGlobalSymbols
  return {
    mode,
    lineNumbers: true,
    theme: 'eclipse',
    indentUnit: 4,
    readOnly: false,
    extraKeys: {
      Tab: (cm) => cm.execCommand('indentMore'),
      'Shift-Tab': (cm) => cm.execCommand('indentLess'),
    }
  };
}

