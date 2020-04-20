import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CollectionTool, ToolPart} from '../../../_interfaces/tool';
import {collectionTools} from '../collection-tools-list';
import {
  ExerciseGQL,
  ExerciseQuery,
  ProgExerciseContentSolveFieldsFragment,
  ProgrammingSampleSolutionFragment,
  RegexSampleSolutionFragment,
  SqlExerciseContentSolveFieldsFragment,
  SqlSampleSolutionFragment,
  UmlExerciseContentSolveFieldsFragment,
  UmlSampleSolutionFragment,
  WebExerciseContentSolveFieldsFragment,
  WebSampleSolutionFragment,
  XmlExerciseContentSolveFieldsFragment,
  XmlSampleSolutionFragment
} from "../../../_services/apollo_services";

type SampleSolutionFragment =
  ProgrammingSampleSolutionFragment
  | RegexSampleSolutionFragment
  | SqlSampleSolutionFragment
  | UmlSampleSolutionFragment
  | WebSampleSolutionFragment
  | XmlSampleSolutionFragment;


function isProgrammingSampleSolutionFragment(x: SampleSolutionFragment): x is ProgrammingSampleSolutionFragment {
  return x.__typename === 'ProgrammingSampleSolution';
}

function isRegexSampleSolutionFragment(x: SampleSolutionFragment): x is RegexSampleSolutionFragment {
  return x.__typename === 'RegexSampleSolution';
}

function isSqlSampleSolutionFragment(x: SampleSolutionFragment): x is SqlSampleSolutionFragment {
  return x.__typename === 'SqlSampleSolution';
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

  // Exercise content

  private get exContent() {
    return this.exerciseQuery.tool.collection.exercise.content;
  }

  get progExerciseContent(): ProgExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === 'ProgrammingExerciseContent') ? this.exContent : undefined;
  }

  isRegexExerciseContent(): boolean {
    return this.exContent.__typename === 'RegexExerciseContent';
  }

  get sqlExerciseContent(): SqlExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === 'SqlExerciseContent') ? this.exContent : undefined;
  }

  get umlExerciseContent(): UmlExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === 'UmlExerciseContent') ? this.exContent : undefined;
  }

  get webExerciseContent(): WebExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === 'WebExerciseContent') ? this.exContent : undefined;
  }


  get xmlExerciseContent(): XmlExerciseContentSolveFieldsFragment | undefined {
    return (this.exContent.__typename === 'XmlExerciseContent') ? this.exContent : undefined;
  }

  // Sample solutions

  private get sampleSolutions(): SampleSolutionFragment[] {
    return this.exerciseQuery.tool.collection.exercise.sampleSolutions;
  }

  get programmingSampleSolutionFragments(): ProgrammingSampleSolutionFragment[] {
    return this.sampleSolutions.filter(isProgrammingSampleSolutionFragment);
  }

  get regexSampleSolutionFragments(): RegexSampleSolutionFragment[] {
    return this.sampleSolutions.filter(isRegexSampleSolutionFragment);
  }

  get sqlSampleSolutionFragments(): SqlSampleSolutionFragment[] {
    return this.sampleSolutions.filter(isSqlSampleSolutionFragment);
  }

  get umlSampleSolutionFragments(): UmlSampleSolutionFragment[] {
    return this.sampleSolutions.filter(isUmlSampleSolutionFragment);
  }

  get webSampleSolutionFragments(): WebSampleSolutionFragment[] {
    return this.sampleSolutions.filter(isWebSampleSolutionFragment);
  }

  get xmlSampleSolutionFragments(): XmlSampleSolutionFragment[] {
    return this.sampleSolutions.filter(isXmlSampleSolutionFragment);
  }

}
