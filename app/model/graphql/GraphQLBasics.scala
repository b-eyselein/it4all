package model.graphql

import sangria.schema.{Action, Context}

import scala.concurrent.Future

trait GraphQLBasics {

  type Resolver[S, T] = Context[GraphQLContext, S] => Action[GraphQLContext, T]

  protected def futureFromOption[S](opt: Option[S], onError: => Exception): Future[S] = opt match {
    case None        => Future.failed(onError)
    case Some(value) => Future.successful(value)
  }

}
