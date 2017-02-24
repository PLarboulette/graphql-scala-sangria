package controllers

import javax.inject._

import models.{Hero, HeroRepo, SchemaDefinition}
import play.api._
import play.api.libs.json.Json
import play.api.mvc._
import sangria.execution.Executor
import sangria.macros._

import scala.concurrent.{ExecutionContext, Future}

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

  def ok: Action[AnyContent] = Action.async {


    val query =
      graphql"""
    query Test {
      hero(id: "2") {
        name
      }

    }
  """

    val ok = Executor.execute(SchemaDefinition.schema, query, new HeroRepo)
    ok.map {
      elem => println(elem)
    }

    println("okokokokkoko")

    Future(Ok("ok"))
  /* Hero.getByName("Aragorn").map {
     case Some(hero) => Ok(Json.toJson(hero))
     case None => Ok(Json.obj())
   } recover  {
     case e : Exception =>
       Ok(Json.obj("Exception" -> e.getMessage))
   }*/
  }

}

