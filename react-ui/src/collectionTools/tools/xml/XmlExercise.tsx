import {ConcreteExerciseIProps} from '../../Exercise';
import {ExerciseFileFragment, FilesSolution, useXmlCorrectionMutation, XmlExerciseContentFragment, XmlExPart, XmlSolutionInput} from '../../../graphql';
import {FilesExercise} from '../FilesExercise';
import {WithQuery} from '../../../WithQuery';
import {SolutionSaved} from '../../../helpers/SolutionSaved';
import {PointsNotification} from '../../../helpers/PointsNotification';
import {XmlDocumentResultDisplay} from './XmlDocumentResultDisplay';
import {XmlGrammarResultDisplay} from './XmlGrammarResultDisplay';
import {database} from '../../DexieTable';
import {WithNullableNavigate} from '../../../WithNullableNavigate';

type IProps = ConcreteExerciseIProps<XmlExerciseContentFragment, XmlSolutionInput>;

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


export function XmlExercise({exercise, content, partId, oldSolution}: IProps): JSX.Element {

  const [correctExercise, correctionMutationResult] = useXmlCorrectionMutation();

  const part = partId === 'grammar' ? XmlExPart.GrammarCreationXmlPart : XmlExPart.DocumentCreationXmlPart;

  const isGrammarPart = part === XmlExPart.GrammarCreationXmlPart;

  const grammarFile = {
    name: `${content.rootNode}.dtd`,
    content: oldSolution
      ? oldSolution.grammar
      : (isGrammarPart ? getXmlGrammarContent(content.rootNode) : content.xmlSampleSolutions[0].grammar),
    fileType: 'dtd',
    editable: isGrammarPart,
  };

  const documentFile = {
    name: `${content.rootNode}.xml`,
    content: oldSolution?.document || getXmlDocumentContent(content.rootNode),
    fileType: 'xml',
    editable: !isGrammarPart,
  };

  const exerciseDescription = isGrammarPart
    ? <>
      <p className="has-text-weight-bold">Erstellen Sie eine DTD zu folgender Beschreibung:</p>
      <p>{content.grammarDescription}</p>
      <p className="is-italic has-text-info">
        Hinweis: Benutzen Sie die in Klammern angegebenen Element- bzw. Attributnamen. Falls nichts anderes
        angegeben ist, sollen die Elemente nur Text enthalten.
      </p>
    </>
    : <span>{exercise.text}</span>;

  function correct(files: ExerciseFileFragment[], onCorrect: () => void): void {
    const solution: XmlSolutionInput = {grammar: files[0].content, document: files[1].content};

    database.upsertSolution(exercise.toolId, exercise.collectionId, exercise.exerciseId, partId, solution);

    correctExercise({variables: {collId: exercise.collectionId, exId: exercise.exerciseId, solution, part}})
      .then(onCorrect)
      .catch((err) => console.error(err));
  }

  // FIXME: sample solutions!

  const sampleSolutions: FilesSolution[] = content.xmlSampleSolutions.map(({document, grammar}) => {
    return {
      files: [
        isGrammarPart
          ? {name: `${content.rootNode}.dtd`, content: grammar, fileType: 'dtd', editable: false}
          : {name: `${content.rootNode}.xml`, content: document, fileType: 'xml', editable: false}
      ]
    };
  });

  const correctionTabRender = (
    <WithQuery query={correctionMutationResult}>
      {({xmlExercise}) => <WithNullableNavigate t={xmlExercise}>
        {({correct: {solutionSaved, /*proficienciesUpdated, resultSaved,*/ result}}) => <>
          <SolutionSaved solutionSaved={solutionSaved}/>

          {isGrammarPart && <PointsNotification points={result.points} maxPoints={result.maxPoints}/>}

          {result.documentResult && <XmlDocumentResultDisplay result={result.documentResult}/>}

          {result.grammarResult && <XmlGrammarResultDisplay result={result.grammarResult}/>}

        </>}
      </WithNullableNavigate>}
    </WithQuery>
  );

  return (
    <FilesExercise
      exerciseDescription={exerciseDescription}
      defaultFiles={[grammarFile, documentFile]}
      oldSolution={undefined}
      sampleSolutions={sampleSolutions}
      correct={correct}
      isCorrecting={correctionMutationResult.loading}
      correctionTabRender={correctionTabRender}/>
  );
}
