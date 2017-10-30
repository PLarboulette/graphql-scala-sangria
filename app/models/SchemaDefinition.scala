package models
import database.GlobalRepo
import sangria.schema.{BooleanType, Field, _}
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
        arguments = Hero.id :: Nil, resolve = c => c.ctx.getHeroByID(c arg Hero.id)
      ),
      Field(
        "getHeroByName", OptionType(Hero.HeroType), description = Some("Return one hero thanks to its name"),
        arguments = Hero.name :: Nil, resolve = c => c.ctx.getHeroByName(c arg Hero.name.name)
      )
    )
  )

  val globalMutation = ObjectType("Mutation",
    fields[GlobalRepo, Unit](
      Field("createHeroes", ListType(Hero.HeroType), arguments = Nil,
        resolve = ctx => ctx.ctx.createHeroes(test = false)
      ),
      Field("createHero", OptionType(Hero.HeroType), arguments = Hero.name :: Hero.side :: Hero.friends :: Nil,
        resolve = ctx => ctx.ctx.createHero(ctx.arg(Hero.name.name), ctx.arg(Hero.side.name), ctx.arg(Hero.friends.name))
      ),
      Field("updateHeroName", OptionType(Hero.HeroType), arguments = Hero.id :: Hero.name :: Nil,
        resolve = ctx => ctx.ctx.updateHeroName(ctx.arg(Hero.id.name), ctx.arg(Hero.name.name))
      ),
      Field("deleteHeroByID", BooleanType, arguments = Hero.id :: Nil,
        resolve = ctx => ctx.ctx.deleteHeroByID(ctx.arg(Hero.id.name))
      ),
      Field("deleteHeroes", BooleanType, arguments = Nil,
        resolve = ctx => ctx.ctx.deleteHeroes(test = false)
      )
    )
  )

   val schema = Schema(globalQuery, Some(globalMutation))
}



