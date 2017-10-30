package controllers

import javax.inject._

import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import utils.GraphQLHelper

import scala.concurrent.ExecutionContext
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

  def ok: Action[JsValue] = Action.async (parse.json) {

     /*{
    "query" : "query Query($id : String!) t{hero (id : $id) {id, name, side, friends}}",
    "variables" : {"id" : "2"}
    }*/

    request ⇒
      GraphQLHelper.parseAndLaunchQuery(request).map {
        case Success(result) => Ok(result)
        case Failure(error) => BadRequest(Json.obj("error" -> error.getMessage ))
      }
  }
}

