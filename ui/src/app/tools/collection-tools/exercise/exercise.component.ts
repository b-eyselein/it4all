import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CollectionTool, ToolPart} from '../../../_interfaces/tool';
import {collectionTools} from '../collection-tools-list';
import {
  ExerciseGQL,
  ExerciseQuery,
  ProgExerciseContentSolveFieldsFragment,
  ProgrammingSampleSolutionFragment,
  SqlExerciseContentSolveFieldsFragment,
  StringSampleSolutionFragment,
  UmlExerciseContentSolveFieldsFragment,
  UmlSampleSolutionFragment,
  WebExerciseContentSolveFieldsFragment,
  WebSampleSolutionFragment,
  XmlExerciseContentSolveFieldsFragment,
  XmlSampleSolutionFragment
} from "../../../_services/apollo_services";

type SampleSolutionFragment =
  ProgrammingSampleSolutionFragment
  | StringSampleSolutionFragment
  | UmlSampleSolutionFragment
  | WebSampleSolutionFragment
  | XmlSampleSolutionFragment;

function isStringSampleSolution(x: SampleSolutionFragment): x is StringSampleSolutionFragment {
  return x.__typename === 'StringSampleSolution';
}

function isProgrammingSampleSolutionFragment(x: SampleSolutionFragment): x is ProgrammingSampleSolutionFragment {
  return x.__typename === 'ProgrammingSampleSolution';
}

function isUmlSampleSolutionFragment(x: SampleSolutionFragment): x is UmlSampleSolutionFragment {
  return x.__typename === 'UmlSampleSolution';
}

function isWebSampleSolutionFragment(x: WebSampleSolutionFragment): x is WebSampleSolutionFragment {
  return x.__typename === 'WebSampleSolution';
}

function isXmlSampleSolutionFragment(x: XmlSampleSolutionFragment): x is XmlSampleSolutionFragment {
  return x.__typename === 'XmlSampleSolution';
}

@Component({templateUrl: './exercise.component.html'})
export class ExerciseComponent implements OnInit {

  tool: CollectionTool;
  collectionId: number;
  exerciseId: number;

  exerciseQuery: ExerciseQuery;

  oldPart: ToolPart;

  constructor(private route: ActivatedRoute, private exerciseGQL: ExerciseGQL) {
    this.route.paramMap.subscribe((paramMap) => {
      this.tool = collectionTools.find((t) => t.id === paramMap.get('toolId'));
      this.oldPart = this.tool.parts.find((p) => p.id === paramMap.get('partId'));

      this.collectionId = parseInt(paramMap.get('collId'), 10);
      this.exerciseId = parseInt(paramMap.get('exId'), 10);
    });
  }

  ngOnInit() {
    this.exerciseGQL
      .watch({toolId: this.tool.id, collId: this.collectionId, exId: this.exerciseId})
      .valueChanges
      .subscribe(({data}) => this.exerciseQuery = data);
  }

  private get exContent() {
    return this.exerciseQuery.tool.collection.exerciseContent;
  }

  get progExerciseContent(): ProgExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === 'ProgrammingExerciseContent') ? this.exContent : undefined;
  }

  get programmingSampleSolutionFragments(): ProgrammingSampleSolutionFragment[] {
    return this.exerciseQuery.tool.collection.sampleSolutions.filter(isProgrammingSampleSolutionFragment);
  }

  isRegexExerciseContent(): boolean {
    return this.exContent.__typename === 'RegexExerciseContent';
  }

  get sqlExerciseContent(): SqlExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === 'SqlExerciseContent') ? this.exContent : undefined;
  }

  get stringSampleSolutionFragments(): StringSampleSolutionFragment[] {
    return this.exerciseQuery.tool.collection.sampleSolutions.filter(isStringSampleSolution);
  }

  get umlExerciseContent(): UmlExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === 'UmlExerciseContent') ? this.exContent : undefined;
  }

  get umlSampleSolutionFragments(): UmlSampleSolutionFragment[] {
    return this.exerciseQuery.tool.collection.sampleSolutions.filter(isUmlSampleSolutionFragment);
  }

  get webExerciseContent(): WebExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === 'WebExerciseContent') ? this.exContent : undefined;
  }

  get webSampleSolutionFragments(): WebSampleSolutionFragment[] {
    return this.exerciseQuery.tool.collection.sampleSolutions.filter(isWebSampleSolutionFragment);
  }

  get xmlExerciseContent(): XmlExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === 'XmlExerciseContent') ? this.exContent : undefined;
  }

  get xmlSampleSolutionFragments(): XmlSampleSolutionFragment[] {
    return this.exerciseQuery.tool.collection.sampleSolutions.filter(isXmlSampleSolutionFragment);
  }

}
