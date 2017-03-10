package models

import play.api.libs.json.{Json, Reads, Writes}
import sangria.schema.{Argument, EnumType, EnumValue, Field, InputField, InputObjectType, ListInputType, ListType, ObjectType, OptionInputType, OptionType, StringType, fields}
import utils.JsonUtils
import sangria.marshalling.{CoercedScalaResultMarshaller, FromInput}


/**
  * Created by Pierre Larboulette on 23/02/2017.
  */

object Side extends Enumeration {
  type Side = Value
  val GOOD, NEUTRAL, EVIL  = Value
  implicit val reads:Reads[Side] = JsonUtils.enumReads(Side)
  implicit val writes:Writes[Side] = JsonUtils.enumWrites
}

case class Hero (id : String, name : String, side : Option[Side.Value], friends : List[String])

object Hero {

  implicit val reads = Json.reads[Hero]
  implicit val writes = Json.writes[Hero]

  val SideEnum = EnumType(
    "Side",
    Some("Different sides"),
    List(
      EnumValue("GOOD", value = Side.GOOD, description = Some("Good heroes")),
      EnumValue("NEUTRAL", value = Side.NEUTRAL, description = Some("Neutral heroes")),
      EnumValue("EVIL", value = Side.EVIL, description = Some("Bad heroes")))
  )

  val id = Argument("id", StringType)
  val name = Argument("name", StringType)
  val side = Argument("side", StringType)
  val friends = Argument("friends", ListInputType(StringType))

  val key = Argument("key", StringType)

  val HeroType = ObjectType (
    "Hero",
    "The hero",
    fields[Unit, Hero] (
      Field("id", StringType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name),
      Field("side", OptionType(SideEnum), resolve = _.value.side),
      Field("friends", ListType(StringType), resolve = _.value.friends)
    )
  )

  implicit val HeroFormat = Json.format[Hero]

  implicit val manual = new FromInput[Hero] {
    val marshaller = CoercedScalaResultMarshaller.default
    def fromResult(node: marshaller.Node) = {
      val ad = node.asInstanceOf[Map[String, Any]]

      Hero(
        id = ad.get("id").asInstanceOf[Option[String]].get,
        name = ad.get("name").asInstanceOf[Option[String]].get,
        side = convertStringToSide(ad.get("side").asInstanceOf[Option[Int]].get),
        friends = ad.getOrElse("friends", List.empty).asInstanceOf[Vector[String]].toList)
    }
  }

  val HeroInputType = InputObjectType[Hero]("HeroInput", List(
    InputField("id", StringType),
    InputField("name", StringType),
    InputField("side", StringType),
    InputField("friends", ListInputType(StringType))
  ))




  val argHeroInputType = Argument("HeroInput", HeroInputType)



  def convertStringToSide (sideString : Int) : Option[Side.Value] = {
    sideString match {
      case 0 => Some(Side.GOOD)
      case 1 => Some(Side.NEUTRAL)
      case 2 => Some(Side.EVIL)
      case _ => None
    }
  }
}