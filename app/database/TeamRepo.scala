package database

import models.Team
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Pierre Larboulette on 24/02/2017.
  */
class TeamRepo {

  val mongoClient: MongoClient = MongoClient("mongodb://localhost:32772/")
  val database: MongoDatabase = mongoClient.getDatabase("graphql")
  val collection: MongoCollection[Document] = database.getCollection("factions")

  val data : List[Team] = List(

  )
  def getTeams () (implicit ec : ExecutionContext) : Future[List[Team]] = {
    val data : List[Team] = List()
    Future(data)
  }

}
