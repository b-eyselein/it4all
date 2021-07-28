import React from 'react';
import {ConcreteExerciseIProps} from '../../Exercise';
import {ExerciseFileFragment, useXmlCorrectionMutation, XmlCorrectionMutation, XmlExerciseContentFragment, XmlExPart, XmlSolutionInput} from '../../../graphql';
import {FilesExercise} from '../FilesExercise';
import {WithQuery} from '../../../WithQuery';
import {useTranslation} from 'react-i18next';
import {SolutionSaved} from '../../../helpers/SolutionSaved';
import {PointsNotification} from '../../../helpers/PointsNotification';
import {XmlDocumentResultDisplay} from './XmlDocumentResultDisplay';
import {XmlGrammarResultDisplay} from './XmlGrammarResultDisplay';

type IProps = ConcreteExerciseIProps<XmlExerciseContentFragment>;


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


export function XmlExercise({exercise, content}: IProps): JSX.Element {

  const {t} = useTranslation('common');
  const [correctExercise, correctionMutationResult] = useXmlCorrectionMutation();

  if (!content.xmlPart) {
    throw new Error('TODO!');
  }

  const part = content.xmlPart;

  const isGrammarPart = part === XmlExPart.GrammarCreationXmlPart;

  const grammarFile = {
    name: `${content.rootNode}.dtd`,
    content: isGrammarPart ? getXmlGrammarContent(content.rootNode) : content.xmlSampleSolutions[0].grammar,
    fileType: 'dtd',
    editable: isGrammarPart,
  };

  const documentFile = {
    name: `${content.rootNode}.xml`,
    content: getXmlDocumentContent(content.rootNode),
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

  function correct(files: ExerciseFileFragment[]): void {
    const solution: XmlSolutionInput = {grammar: files[0].content, document: files[1].content};

    correctExercise({variables: {collId: exercise.collectionId, exId: exercise.exerciseId, solution, part}})
      .catch((err) => console.error(err));
  }

  function renderCorrection({me}: XmlCorrectionMutation): JSX.Element {
    if (!me?.xmlExercise?.correct) {
      return <div className="notification is-danger has-text-centered">{t('error while correction...')}</div>;
    }

    const {solutionSaved, /*proficienciesUpdated, resultSaved,*/ result} = me.xmlExercise.correct;

    return (
      <>
        <SolutionSaved solutionSaved={solutionSaved}/>

        {isGrammarPart && <PointsNotification points={result.points} maxPoints={result.maxPoints}/>}

        {result.documentResult && <XmlDocumentResultDisplay result={result.documentResult}/>}

        {result.grammarResult && <XmlGrammarResultDisplay result={result.grammarResult}/>}

      </>
    );
  }

  function renderSampleSolutions(): JSX.Element [] {
    return content.xmlSampleSolutions.map((sample, index) => <pre key={index}>{isGrammarPart ? sample.grammar : sample.document}</pre>);
  }

  // FIXME: sample solutions!

  return <FilesExercise exerciseId={exercise.exerciseId} exerciseDescription={exerciseDescription} initialFiles={[grammarFile, documentFile]}
                        sampleSolutions={[] /*content.xmlSampleSolutions*/} correct={correct} isCorrecting={correctionMutationResult.loading}
                        correctionTabRender={() => <WithQuery query={correctionMutationResult} render={renderCorrection}/>}/>;
}
