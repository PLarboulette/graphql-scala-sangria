package utils

import database.GlobalRepo
import models.SchemaDefinition
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc.Request
import sangria.ast.Document
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import sangria.marshalling.playJson._
import sangria.parser.{QueryParser, SyntaxError}

/**
  * Created by Pierre Larboulette on 27/02/2017.
  */

object GraphQLHelper {

  def executeGraphQLQuery(query: Document, op: Option[String], vars: JsObject)(implicit ec: ExecutionContext): Future[Try[JsValue]] = {

    Executor.execute(SchemaDefinition.schema, query, new GlobalRepo, operationName = op, variables = vars)
      .map { elem =>Success(elem)
      }.recover {
      case error: QueryAnalysisError ⇒ Failure(new Exception(error))
      case error: ErrorWithResolver ⇒ Failure(new Exception(error))
    }
  }

  def parseAndLaunchQuery(request : Request[JsValue]) (implicit ec : ExecutionContext) : Future[Try[JsValue]] = {
    val query = (request.body \ "query").as[String]
    val operation = (request.body \ "operationName").asOpt[String]
    val variables = (request.body \ "variables").toOption.map {
      case obj: JsObject ⇒ obj
      case _ ⇒ Json.obj()
    }
    QueryParser.parse(query) match {
      case Success(queryAst) ⇒
        GraphQLHelper.executeGraphQLQuery(queryAst, operation, variables.getOrElse(Json.obj()))
      case Failure(error: SyntaxError) ⇒
        Future.successful(Failure(new Exception(error.getMessage)))
    }
  }
}