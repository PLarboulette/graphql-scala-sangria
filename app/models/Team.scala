package models

import play.api.libs.json.{Json, Reads, Writes}
import utils.JsonUtils

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
                  chief : Option[Hero],
                  members : List[Hero],
                  allies : List[Hero],
                  enemies : List[Hero]
                )

object Team {
  implicit val reads:Reads[Team] = Json.reads[Team]
  implicit val writes:Writes[Team] = Json.writes[Team]
}



