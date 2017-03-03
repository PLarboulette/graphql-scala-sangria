package models

import models.Side.Side
import play.api.libs.json.{Json, Reads, Writes}
import sangria.schema.{Argument, EnumType, EnumValue, Field, ListInputType, ListType, ObjectType, OptionInputType, OptionType, StringType, fields}
import utils.JsonUtils

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Pierre Larboulette on 23/02/2017.
  */

object Side extends Enumeration {
  type Side = Value
  val EVIL, NEUTRAL, GOOD  = Value
  implicit val reads:Reads[Side] = JsonUtils.enumReads(Side)
  implicit val writes:Writes[Side] = JsonUtils.enumWrites
}

case class Hero (
                  id : String,
                  name : Option[String],
                  side : Option[Side.Value],
                  friends : List[String]
                )

object Hero {

  implicit val reads = Json.reads[Hero]
  implicit val writes = Json.writes[Hero]

  def convertStringToSide (sideString : String) : Side.Value = {
    sideString match {
      case "GOOD" => Side.GOOD
      case "NEUTRAL" => Side.NEUTRAL
      case "BAD" => Side.EVIL
    }
  }

  val SideEnum = EnumType(
    "Side",
    Some("Different sides"),
    List(
      EnumValue("GOOD", value = Side.GOOD, description = Some("Good heroes")),
      EnumValue("NEUTRAL", value = Side.NEUTRAL, description = Some("Neutral heroes")),
      EnumValue("BAD", value = Side.EVIL, description = Some("Bad heroes")))
  )

  val Id = Argument("id", StringType)
  val name = Argument("name", OptionInputType(StringType))
  val side = Argument("side", OptionInputType(StringType))
  val friends = Argument("friends", ListInputType(StringType))

  val HeroType = ObjectType (
    "Hero",
    "The hero",
    fields[Unit, Hero] (
      Field("id", StringType, resolve = _.value.id),
      Field("name", OptionType(StringType), resolve = _.value.name),
      Field("side", OptionType(SideEnum), resolve = _.value.side),
      Field("friends", ListType(StringType), resolve = _.value.friends)
    )
  )

}





