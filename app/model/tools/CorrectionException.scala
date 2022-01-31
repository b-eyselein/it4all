package model.tools

import sangria.execution.UserFacingError

class CorrectionException(msg: String) extends Exception(msg) with UserFacingError
