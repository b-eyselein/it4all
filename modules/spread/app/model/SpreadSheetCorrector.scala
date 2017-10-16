package model

import java.nio.file.Path
import com.google.common.io.Files

object SpreadSheetCorrector {

  val correctors = Map(
    "ods" -> ODFCorrector,
    "xlsx" -> XLSXCorrector,
    "xlsm" -> XLSXCorrector)


  def correct(musterPath: Path, testPath: Path, conditionalFormating: Boolean, charts: Boolean): SpreadSheetCorrectionResult /* throws CorrectionException*/ = {
    val fileExtension: String = Files.getFileExtension(testPath.toString)

    correctors.get(fileExtension) match {
      case None => SpreadSheetCorrectionFailure(s"""The filetype "$fileExtension" is not supported. Could not start correction.""")
      case Some(corrector) => corrector.correct(musterPath, testPath, conditionalFormating, charts)
    }
  }

}
