import {Component, Input, OnInit} from '@angular/core';
import {ToolPart} from '../../../../_interfaces/tool';
import {ComponentWithExerciseDirective} from '../../_helpers/component-with-exercise.directive';
import {DexieService} from '../../../../_services/dexie.service';
import {
  ExerciseFileFragment,
  ExerciseSolveFieldsFragment,
  XmlExerciseContentSolveFieldsFragment
} from '../../../../_services/apollo_services';
import {
  XmlAbstractResultFragment,
  XmlCorrectionGQL,
  XmlCorrectionMutation,
  XmlCorrectionMutationVariables,
  XmlDocumentResultFragment,
  XmlGrammarResultFragment,
  XmlInternalErrorResultFragment,
  XmlResultFragment
} from '../xml-apollo-mutations.service';
import {XmlExPart, XmlSolution, XmlSolutionInput} from '../../../../_interfaces/graphql-types';

import 'codemirror/mode/dtd/dtd';
import 'codemirror/mode/xml/xml';
import {getXmlDocumentContent, getXmlGrammarContent, XmlDocumentCreation, XmlGrammarCreation} from '../xml-tool';
import {AuthenticationService} from '../../../../_services/authentication.service';

@Component({
  selector: 'it4all-xml-exercise',
  templateUrl: './xml-exercise.component.html'
})
export class XmlExerciseComponent
  extends ComponentWithExerciseDirective<XmlSolution, XmlSolutionInput, XmlCorrectionMutation, XmlExPart, XmlCorrectionMutationVariables, XmlCorrectionGQL>
  implements OnInit {

  @Input() exerciseFragment: ExerciseSolveFieldsFragment;
  @Input() contentFragment: XmlExerciseContentSolveFieldsFragment;

  oldPart: ToolPart;

  isGrammarPart: boolean;

  grammarFile: ExerciseFileFragment;
  documentFile: ExerciseFileFragment;

  exerciseFileFragments: ExerciseFileFragment[] = [];

  constructor(private authenticationService: AuthenticationService, xmlCorrectionGQL: XmlCorrectionGQL, dexieService: DexieService) {
    super(xmlCorrectionGQL, dexieService);
  }

  ngOnInit() {
    switch (this.contentFragment.part) {
      case XmlExPart.DocumentCreationXmlPart:
        this.oldPart = XmlDocumentCreation;
        break;
      case XmlExPart.GrammarCreationXmlPart:
        this.oldPart = XmlGrammarCreation;
        break;
    }

    const rootNode = this.contentFragment.rootNode;

    this.isGrammarPart = this.oldPart.id === 'grammar';

    const grammarFileName = `${rootNode}.dtd`;
    this.grammarFile = {
      name: grammarFileName,
      content: this.isGrammarPart ? getXmlGrammarContent(rootNode) : this.contentFragment.sampleSolutions[0].sample.grammar,
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

    this.loadOldSolutionAbstract(this.exerciseFragment, this.oldPart.id, (oldSol) => {
      this.grammarFile.content = oldSol.grammar;
      this.documentFile.content = oldSol.document;

      // do not delete or else editor does not get updated...
      this.exerciseFileFragments = [this.grammarFile, this.documentFile];
    });
  }

  correct(): void {
    this.correctAbstract(this.exerciseFragment, this.contentFragment.part, this.oldPart.id);
  }

  protected getSolution(): XmlSolutionInput {
    return {
      grammar: this.grammarFile.content,
      document: this.documentFile.content
    };
  }

  get sampleSolutions(): XmlSolution[] {
    return this.contentFragment.sampleSolutions.map((s) => s.sample);
  }

  protected getMutationQueryVariables(part: XmlExPart): XmlCorrectionMutationVariables {
    return {
      exId: this.exerciseFragment.exerciseId,
      collId: this.exerciseFragment.collectionId,
      solution: this.getSolution(),
      part,
      userJwt: this.authenticationService.currentUserValue.jwt
    };
  }

  get xmlAbstractResult(): XmlAbstractResultFragment & (XmlResultFragment | XmlInternalErrorResultFragment) | null {
    return this.resultQuery?.me.xmlExercise?.correct;
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

}
