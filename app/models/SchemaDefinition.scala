package models
import database.HeroRepo
import sangria.schema._
import sangria.macros._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Pierre Larboulette on 23/02/2017.
  */
object SchemaDefinition {


  val Id = Argument("id", StringType)

  val query = ObjectType("Query",
    fields[HeroRepo, Unit](
      Field(
        "hero",
        OptionType(Hero.HeroType),
        description = Some("Return one hero"),
        arguments = Id :: Nil,
        resolve = c => c.ctx.getHeroByID(c arg Id)
      ),
      Field (
        "heroes",
        ListType(Hero.HeroType),
        description = Some("Return all the heroes"),
        arguments = Nil,
        resolve = c => c.ctx.getHeroes()
      )
    )
  )

  val schema = Schema(query)


}



