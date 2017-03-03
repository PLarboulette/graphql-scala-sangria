package models
import database.{FactionRepo, GlobalRepo, HeroRepo}
import sangria.schema.{Field, _}
import sangria.macros._
import sangria.relay._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Pierre Larboulette on 23/02/2017.
  */
object SchemaDefinition {





  val globalQuery = ObjectType("Query",
    fields[GlobalRepo, Unit](
      Field ("getHeroes", ListType(Hero.HeroType), description = Some("Return all the heroes"),
        arguments = Nil, resolve = c => c.ctx.getHeroes()
      ),
      Field(
        "getHeroByID", OptionType(Hero.HeroType), description = Some("Return one hero thanks to its id"),
        arguments = Hero.Id :: Nil, resolve = c => c.ctx.getHeroByID(c arg Hero.Id)
      ),
      Field(
        "getHeroByName", OptionType(Hero.HeroType), description = Some("Return one hero thanks to its id"),
        arguments = Hero.name :: Nil, resolve = c => c.ctx.getHeroByName(c arg Hero.name.name)
      )
    )
  )

  val globalMutation = ObjectType("Mutation", fields[GlobalRepo, Unit](
    Field("createHero", OptionType(Hero.HeroType),
      arguments = Hero.name :: Hero.side :: Hero.friends :: Nil,
      resolve = ctx => ctx.ctx.createHero(ctx.arg(Hero.name.name), ctx.arg(Hero.side.name), ctx.arg(Hero.friends.name))
    ),
    Field("updateHero", OptionType(Hero.HeroType),
      arguments = Hero.Id :: Hero.name :: Hero.side :: Hero.friends :: Nil,
      resolve = ctx => ctx.ctx.updateHero(ctx.arg(Hero.Id.name), ctx.arg(Hero.name.name), ctx.arg(Hero.side.name), ctx.arg(Hero.friends.name))
    )

  )
  )



  val schema = Schema(globalQuery, Some(globalMutation))

}



