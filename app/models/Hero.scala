package models

import database.HeroRepo
import play.api.libs.json.{Json, Reads, Writes}
import sangria.schema.{Argument, EnumType, EnumValue, Field, InputField, InputObjectType, InterfaceType, ListInputType, ListType, ObjectType, OptionInputType, OptionType, StringType, fields}
import utils.JsonUtils
import sangria.marshalling.{CoercedScalaResultMarshaller, FromInput}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Pierre Larboulette on 23/02/2017.
  */

object Side extends Enumeration {
  type Side = Value
  val GOOD, NEUTRAL, EVIL  = Value
  implicit val reads:Reads[Side] = JsonUtils.enumReads(Side)
  implicit val writes:Writes[Side] = JsonUtils.enumWrites
}

trait Test {
  def id : String
}

case class Hero (
                  id : String,
                  name : String,
                  side : Option[Side.Value],
                  friends : List[String]) extends Test

object Hero {

  implicit val reads = Json.reads[Hero]
  implicit val writes = Json.writes[Hero]

  val heroRepo = new HeroRepo()

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



  val Character: InterfaceType[Unit, Test] =
    InterfaceType(
      "Character",
      "A character in the Star Wars Trilogy",
      () => fields[Unit, Test](
        Field("id", StringType,
          Some("The id of the character."),
          resolve = _.value.id)
      ))

   val HeroType : ObjectType[Unit, Hero] = ObjectType (
    "Hero",
    "The hero",
    fields[Unit, Hero] (
      Field("id", StringType, resolve = _.value.id),
      Field("name", StringType, resolve = _.value.name),
      Field("side", OptionType(SideEnum), resolve = _.value.side),
      Field("friends", ListType(Character),
        resolve = ctx => heroRepo.convertListIdToListHero(ctx.value.friends)
      )
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