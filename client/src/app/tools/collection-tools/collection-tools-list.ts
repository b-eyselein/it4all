import {Tool} from '../../_interfaces/tool';
import {ProgrammingTool} from './programming-tool';
import {XmlTool} from './xml/xml-tool';
import {WebTool} from './web/web-tool';
import {RegexTool} from './regex/regex-tool';
import {RoseTool} from './rose/rose-tool';
import {SqlTool} from './sql/sql-tool';
import {UmlTool} from './uml/uml-tools';

// All Tools

export const collectionTools: Tool[] = [
  ProgrammingTool,
  RegexTool,
  RoseTool,
  SqlTool,
  UmlTool,
  WebTool,
  XmlTool
];

