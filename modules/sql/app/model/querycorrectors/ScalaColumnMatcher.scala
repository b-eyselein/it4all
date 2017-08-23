package model.querycorrectors;

import model.StringConsts
import model.matching.ScalaMatcher

case class ColumnMatcher() extends ScalaMatcher[ScalaColumnWrapper, ColumnMatch](model.StringConsts.COLUMNS_NAME, _.canMatch(_), new ColumnMatch(_, _))