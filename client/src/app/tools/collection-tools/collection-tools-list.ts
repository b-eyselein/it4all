import {Tool, ToolPart} from '../../_interfaces/tool';

export const ProgrammingTool: Tool = {
  id: 'programming',
  name: 'Programmierung',
  parts: [
    {id: 'testCreation', name: 'Erstellen der Unittests'},
    {id: 'implementation', name: 'Implementierung', disabled: true}
  ],
  status: 'beta'
};

export const RegexTool: Tool = {
  id: 'regex',
  name: 'Reguläre Ausdrücke',
  parts: [
    {id: 'regex', name: 'Regulären Ausdruck erstellen'}
  ],
};

// SQL

export const SqlCreateQueryPart: ToolPart = {id: 'createQuery', name: 'Abfrage erstellen'};

export const SqlTool: Tool = {id: 'sql', name: 'SQL', parts: [SqlCreateQueryPart]};

// Web

export const WebTool: Tool = {
  id: 'web',
  name: 'Web',
  parts: [],
  hasLivePreview: true
};

// XML

export const XmlTool: Tool = {id: 'xml', name: 'XML', parts: []};

// All Tools

export const collectionTools: Tool[] = [
  ProgrammingTool,
  RegexTool,
  {id: 'rose', name: 'ROSE', status: 'alpha', parts: [], hasPlayground: false},
  SqlTool,
  {id: 'uml', name: 'UML-Klassendiagramme', status: 'beta', parts: [], hasPlayground: false},
  WebTool,
  XmlTool
];

