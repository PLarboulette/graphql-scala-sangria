package database

import models.{Faction, Hero, Nation}
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Pierre Larboulette on 24/02/2017.
  */
class FactionRepo {

  val mongoClient: MongoClient = MongoClient("mongodb://localhost:32772/")
  val database: MongoDatabase = mongoClient.getDatabase("graphql")
  val collection: MongoCollection[Document] = database.getCollection("factions")

  val data : List[Faction] = List(
    Faction("1", "Gondor", None , List.empty, List.empty, Some(Nation.HUMANS)),
    Faction("2", "Rohan", Some("Theoden"), List.empty, List.empty, Some(Nation.HUMANS))
  )
  def getFactions () (implicit ec : ExecutionContext) : Future[List[Faction]] = {

    val data : List[Faction] = List(


    )
    Future(data)
  }

  /* def convertFactionToDocument(faction : Faction) : Document = {
    Document("_id" -> faction.id, "name" -> hero.name.get, "side" -> hero.side.get.id, "friends" -> hero.friends)
  }

  def convertDocumentToHero (document : Document) : Hero = {
    Hero(
      document.get("_id").get.asString().getValue,
      document.get("name").map(_.asString().getValue),
      document.get("side").map { elem =>Side.values.filter(_.id == elem.asInt32().getValue).head},
      document.get("friends").get.asArray().toArray.toList.asInstanceOf[List[String]]
    )
  }*/



}
