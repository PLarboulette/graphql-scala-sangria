package controllers

import javax.inject._

import database.HeroRepo
import models.SchemaDefinition
import org.mongodb.scala.{Completed, Observer}
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.mvc._
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.macros._

import scala.concurrent.{ExecutionContext, Future}
import sangria.marshalling.playJson._
import sangria.ast.Document
import sangria.parser.{QueryParser, SyntaxError}
import sangria.schema.Schema
import utils.{GraphQLHelper}

import scala.util.{Failure, Success}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (implicit ec : ExecutionContext) extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def ok = Action.async (parse.json) {

     /*{
    "query" : "query Query($id : String!) {hero (id : $id) {id, name, side, friends}}",
    "variables" : {"id" : "2"}
    }*/

    request ⇒
      GraphQLHelper.parseAndLaunchQuery(request).map {
        case Success(result) => Ok(result)
        case Failure(error) => BadRequest(Json.obj("error" -> error.getMessage ))
      }
  }
}

