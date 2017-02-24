package models
import sangria.schema._
import sangria.macros._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Pierre Larboulette on 23/02/2017.
  */
object SchemaDefinition {

  val SideEnum = EnumType(
    "Side",
    Some("Different sides"),
    List(
      EnumValue("GOOD",
        value = Side.GOOD,
        description = Some("Good")),
      EnumValue("NEUTRAL",
        value = Side.NEUTRAL,
        description = Some("Released in 1980.")),
      EnumValue("BAD",
        value = Side.BAD,
        description = Some("Released in 1983."))))


  val heroType = ObjectType (
    "Hero",
    "The hero",
    fields[Unit, Hero] (
      Field("id", StringType, resolve = _.value.id),
      Field("name", OptionType(StringType), resolve = _.value.name),
      Field("side", OptionType(SideEnum), resolve = _.value.side),
      Field("friends", ListType(StringType), resolve = _.value.friends.map(elem => elem.name.getOrElse("")))
    )
  )

  val Id = Argument("id", StringType)

  val query = ObjectType("Query",
    fields[HeroRepo, Unit](
      Field(
        "hero",
        OptionType(heroType),
        description = Some("Return one hero"),
        arguments = Id :: Nil,
        resolve = c => c.ctx.getHeroByID(c arg Id)
      ),
      Field (
        "heroes",
        ListType(heroType),
        description = Some("Return all the heroes"),
        arguments = Nil,
        resolve = c => c.ctx.getHeroes()
      )
    )
  )

  val schema = Schema(query)



}



