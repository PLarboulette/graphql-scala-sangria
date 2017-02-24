package database

import models.Faction

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Pierre Larboulette on 24/02/2017.
  */
class FactionRepo {


  def getFactions () (implicit ec : ExecutionContext) : Future[List[Faction]] = {
    val data : List[Faction] = List(
      Faction("1", "Gondor", None, List.empty, List.empty, Some(Nation.HUMANS))

    )
    Future(data)
  }

}
