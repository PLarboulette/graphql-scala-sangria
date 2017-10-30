package models

import database.HeroRepo
import models.Hero.HeroType
import play.api.libs.json.{Json, Reads, Writes}
import sangria.schema.{EnumType, EnumValue, Field, ListType, ObjectType, OptionType, StringType, fields}
import utils.JsonUtils

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Pierre Larboulette on 23/02/2017.
  */


object Teams extends Enumeration {
  type Teams = Value
  val X_MEN, JUSTICE_LEAGUE, AVENGERS, OTHERS = Value

  implicit val reads:Reads[Teams] = JsonUtils.enumReads(Teams)
  implicit val writes:Writes[Teams] = JsonUtils.enumWrites
}


case class Team (
                  id : String,
                  name : Teams.Value,
                  chief : Option[String],
                  members : List[String],
                  allies : List[String],
                  enemies : List[String]
                )

object Team {
  implicit val reads:Reads[Team] = Json.reads[Team]
  implicit val writes:Writes[Team] = Json.writes[Team]


  val TeamsEnum = EnumType(
    "Teams",
    Some("Different teams"),
    List(
      EnumValue("X_MEN", value = Teams.X_MEN, description = Some("X-Men Team")),
      EnumValue("JUSTICE_LEAGUE", value = Teams.JUSTICE_LEAGUE, description = Some("Justice League Team")),
      EnumValue("AVENGERS", value = Teams.AVENGERS, description = Some("Avengers Team")),
      EnumValue("OTHERS", value = Teams.OTHERS, description = Some("The others")))
  )


  val TeamType = ObjectType (
    "Team",
    "A Team",
    fields[Unit, Team] (
      Field("id", StringType,
        resolve = _.value.id
      ),
      Field("name", OptionType(TeamsEnum),
        resolve = _.value.name
      ),
      Field("chief", OptionType(HeroType),
        resolve =  ctx => HeroRepo.getHeroByID(ctx.value.chief.getOrElse(""))
      ),
      Field("members", ListType(HeroType),
        resolve = ctx => HeroRepo.convertListIdToListHero(ctx.value.members)
      ),
      Field("allies", ListType(HeroType),
        resolve = ctx => HeroRepo.convertListIdToListHero(ctx.value.allies)
      ),
      Field("enemies", ListType(HeroType),
        resolve = ctx => HeroRepo.convertListIdToListHero(ctx.value.enemies)
      )
    )
  )

  /*implicit val HeroFormat = Json.format[Team]

  implicit val manual = new FromInput[Team] {
    val marshaller = CoercedScalaResultMarshaller.default
    def fromResult(node: marshaller.Node) = {
      val ad = node.asInstanceOf[Map[String, Any]]

      val heroRepo = new HeroRepo()

      val id = ad.get("id").asInstanceOf[Option[String]].get
      val name = convertStringToTeams(ad.get("name").asInstanceOf[Option[Int]].get).getOrElse(None)


      val value = for {
        chief <- heroRepo.getHeroByID(id)
        members <- ad.getOrElse("members", List.empty).asInstanceOf[Vector[String]].toList.map(heroRepo.getHeroByID)
      } yield {
        (chief, members)
      }


      ,
      chief = new HeroRepo()). ,
      members = ,
      allies = ad.getOrElse("friends", List.empty).asInstanceOf[Vector[String]].toList,
      enemies = ad.getOrElse("friends", List.empty).asInstanceOf[Vector[String]].toList)

      Team(
    }
  }*/

 /* val HeroInputType = InputObjectType[Hero]("HeroInput", List(
    InputField("id", StringType),
    InputField("name", StringType),
    InputField("side", StringType),
    InputField("friends", ListInputType(StringType))
  ))

  val argHeroInputType = Argument("HeroInput", HeroInputType)
*/


  def convertStringToTeams (sideString : Int) : Option[Teams.Value] = {

    sideString match {
      case 0 => Some(Teams.X_MEN)
      case 1 => Some(Teams.JUSTICE_LEAGUE)
      case 2 => Some(Teams.AVENGERS)
      case 3 => Some(Teams.OTHERS)
      case _ => None
    }
  }



}



