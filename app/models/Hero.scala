package models

import play.api.libs.json.{Json, Reads, Writes}
import sangria.schema.{EnumType, EnumValue, Field, ListType, ObjectType, OptionType, StringType, fields}
import utils.JsonUtils

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Pierre Larboulette on 23/02/2017.
  */

object Side extends Enumeration {
  type Side = Value
  val BAD, NEUTRAL, GOOD  = Value

  implicit val reads:Reads[Side] = JsonUtils.enumReads(Side)
  implicit val writes:Writes[Side] = JsonUtils.enumWrites
}


case class Hero (
                  id : String,
                  name : Option[String],
                  side : Option[Side.Value],
                  friends : List[Hero]
                )

object Hero {
  implicit val reads = Json.reads[Hero]
  implicit val writes = Json.writes[Hero]

  val SideEnum = EnumType(
    "Side",
    Some("Different sides"),
    List(
      EnumValue("GOOD", value = Side.GOOD, description = Some("Good heroes")),
      EnumValue("NEUTRAL", value = Side.NEUTRAL, description = Some("Neutral heroes")),
      EnumValue("BAD", value = Side.BAD, description = Some("Bad heroes")))
  )


  val HeroType = ObjectType (
    "Hero",
    "The hero",
    fields[Unit, Hero] (
      Field("id", StringType, resolve = _.value.id),
      Field("name", OptionType(StringType), resolve = _.value.name),
      Field("side", OptionType(SideEnum), resolve = _.value.side),
      Field("friends", ListType(StringType), resolve = _.value.friends.map(elem => elem.name.getOrElse("")))
    )
  )

}





