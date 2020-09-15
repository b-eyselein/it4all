export abstract class CorrectionHelpers {

  readonly exerciseTextTabTitle = 'Aufgabenstellung';

  readonly correctionTabTitle = 'Korrektur';

  readonly livePreviewTabTitle = 'Live-Vorschau';

  readonly sampleSolutionsTabTitle = 'Musterlösungen';

}

export interface HasSampleSolutions<SolutionType> {
  displaySampleSolutions: boolean;

  sampleSolutions: SolutionType[];

  toggleSampleSolutions(): void;
}
