import {Component, Input, OnInit} from '@angular/core';
import {ComponentWithExerciseDirective} from '../../_helpers/component-with-exercise.directive';
import {DexieService} from '../../../../_services/dexie.service';
import {
  ExerciseFileFragment,
  ExerciseSolveFieldsFragment,
  XmlExerciseContentFragment
} from '../../../../_services/apollo_services';
import {
  XmlAbstractResultFragment,
  XmlCorrectionGQL,
  XmlCorrectionMutation,
  XmlCorrectionMutationVariables,
  XmlCorrectionResultFragment,
  XmlDocumentResultFragment,
  XmlGrammarResultFragment,
  XmlInternalErrorResultFragment,
  XmlResultFragment
} from '../xml-apollo-mutations.service';
import {XmlExPart, XmlSolution, XmlSolutionInput} from '../../../../_interfaces/graphql-types';
import {AuthenticationService} from '../../../../_services/authentication.service';

import 'codemirror/mode/dtd/dtd';
import 'codemirror/mode/xml/xml';
import {HasSampleSolutions} from "../../_helpers/correction-helpers";


export function getIdForXmlExPart(xmlExPart: XmlExPart): string {
  switch (xmlExPart) {
    case XmlExPart.DocumentCreationXmlPart:
      return 'document';
    case XmlExPart.GrammarCreationXmlPart:
      return 'grammar';
  }
}

export function getXmlGrammarContent(rootNode: string): string {
  return `<!ELEMENT ${rootNode} (EMPTY)>`;
}

export function getXmlDocumentContent(rootNode: string): string {
  return `
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE ${rootNode} SYSTEM "${rootNode}.dtd">
<${rootNode}>
</${rootNode}>`.trim();
}


@Component({
  selector: 'it4all-xml-exercise',
  templateUrl: './xml-exercise.component.html'
})
export class XmlExerciseComponent
  extends ComponentWithExerciseDirective<XmlSolutionInput, XmlCorrectionMutation, XmlCorrectionMutationVariables>
  implements OnInit, HasSampleSolutions<XmlSolution> {

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: XmlExerciseContentFragment;

  partId: string;

  isGrammarPart: boolean;

  grammarFile: ExerciseFileFragment;
  documentFile: ExerciseFileFragment;

  exerciseFileFragments: ExerciseFileFragment[] = [];

  constructor(private authenticationService: AuthenticationService, xmlCorrectionGQL: XmlCorrectionGQL, dexieService: DexieService) {
    super(xmlCorrectionGQL, dexieService);
  }

  ngOnInit() {
    const rootNode = this.contentFragment.rootNode;

    this.partId = getIdForXmlExPart(this.contentFragment.xmlPart);
    this.isGrammarPart = this.partId === 'grammar';

    const grammarFileName = `${rootNode}.dtd`;
    this.grammarFile = {
      name: grammarFileName,
      content: this.isGrammarPart ? getXmlGrammarContent(rootNode) : this.contentFragment.xmlSampleSolutions[0].grammar,
      fileType: 'dtd',
      editable: this.isGrammarPart,
    };

    const documentFileName = `${rootNode}.xml`;
    this.documentFile = {
      name: documentFileName,
      content: getXmlDocumentContent(rootNode),
      fileType: 'xml',
      editable: !this.isGrammarPart,
    };

    this.exerciseFileFragments = [this.grammarFile, this.documentFile];

    this.loadOldSolutionAbstract(this.exerciseFragment, this.partId, (oldSol) => {
      this.grammarFile.content = oldSol.grammar;
      this.documentFile.content = oldSol.document;

      // do not delete or else editor does not get updated...
      this.exerciseFileFragments = [this.grammarFile, this.documentFile];
    });
  }

  // Correction

  protected getSolution(): XmlSolutionInput {
    return {
      grammar: this.grammarFile.content,
      document: this.documentFile.content
    };
  }

  protected getMutationQueryVariables(): XmlCorrectionMutationVariables {
    return {
      exId: this.exerciseFragment.exerciseId,
      collId: this.exerciseFragment.collectionId,
      solution: this.getSolution(),
      part: this.contentFragment.xmlPart,
      userJwt: this.authenticationService.currentUserValue.jwt
    };
  }

  correct(): void {
    this.correctAbstract(this.exerciseFragment, this.partId);
  }

  // Results

  get correctionResult(): XmlCorrectionResultFragment | null {
    return this.resultQuery?.me.xmlExercise?.correct;
  }

  get xmlAbstractResult(): XmlAbstractResultFragment & (XmlResultFragment | XmlInternalErrorResultFragment) | null {
    return this.correctionResult?.result;
  }

  private get xmlResult(): XmlResultFragment | null {
    return this.xmlAbstractResult?.__typename === 'XmlResult' ? this.xmlAbstractResult : null;
  }

  get grammarResult(): XmlGrammarResultFragment | null {
    return this.xmlResult?.grammarResult;
  }

  get documentResult(): XmlDocumentResultFragment | null {
    return this.xmlResult?.documentResult;
  }

  // Sample solutions

  displaySampleSolutions = false;

  toggleSampleSolutions() {
    this.displaySampleSolutions = !this.displaySampleSolutions;
  }

  get sampleSolutions(): XmlSolution[] {
    return this.contentFragment.xmlSampleSolutions;
  }

}
