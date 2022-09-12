package model.graphql

import model.{TableDefs, User}
import sangria.schema._

final case class GraphQLContext(
  loggedInUser: Option[User],
  tableDefs: TableDefs
)

trait GraphQLModel extends RootQuery with RootMutations {

  val schema: Schema[GraphQLContext, Unit] = Schema(queryType, mutation = Some(mutationType))

}
