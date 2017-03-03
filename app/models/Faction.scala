package models

import models.Side.Value
import play.api.libs.json.{Json, Reads, Writes}
import utils.JsonUtils

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Pierre Larboulette on 23/02/2017.
  */


object Nation extends Enumeration {
  type Nation = Value
  val HUMANS, ELVES, DWARFS, MORDOR, GOBLIN, ISENGARD, MAGICIANS = Value

  implicit val reads:Reads[Nation] = JsonUtils.enumReads(Nation)
  implicit val writes:Writes[Nation] = JsonUtils.enumWrites
}


case class Faction (
                   id : String,
                  name : String,
                  chief : Option[String],
                  allies : List[String],
                  enemies : List[String],
                  nation : Option[Nation.Value]
                )
object Faction {
  implicit val reads:Reads[Faction] = Json.reads[Faction]
  implicit val writes:Writes[Faction] = Json.writes[Faction]
}



