package model.graphql

import model.{TableDefs, User}
import sangria.execution.UserFacingError
import sangria.schema._

import scala.concurrent.ExecutionContext

final case class GraphQLContext(
  loggedInUser: Option[User],
  tableDefs: TableDefs,
  executionContext: ExecutionContext
)

final case class MyUserFacingGraphQLError(msg: String) extends Exception(msg) with UserFacingError

trait GraphQLModel extends RootQuery with RootMutations {

  val schema: Schema[GraphQLContext, Unit] = Schema(queryType, mutation = Some(mutationType))

}
