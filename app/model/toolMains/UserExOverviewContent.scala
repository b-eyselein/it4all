package model.toolMains

case class UserExOverviewContent(numOfExes: Int, exesAndRoutes: Seq[ExAndRoute]) {

  def pages: Int = numOfExes / ToolList.STEP + 1

}
