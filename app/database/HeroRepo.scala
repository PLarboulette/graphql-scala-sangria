package database

import java.util.UUID

import models.{Hero, Side}
import org.mongodb.scala.bson.BsonValue
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}

import scala.concurrent.{ExecutionContext, Future}
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._

/**
  * Created by Pierre Larboulette on 24/02/2017.
  */
object HeroRepo  {

  val mongoClient: MongoClient = MongoClient("mongodb://localhost:32768/")
  val database: MongoDatabase = mongoClient.getDatabase("graphql")
  val collection: MongoCollection[Document] = database.getCollection("heroes")

  val heroes : List[(String, Option[Side.Value], List[String])] = List(
    ("Professor X",Some(Side.GOOD), List.empty),
    ("Cyclops", Some(Side.GOOD), List.empty),
    ("Iceman",  Some(Side.GOOD), List.empty),
    ("Archangel",Some(Side.GOOD), List.empty),
    ("BEast", Some(Side.GOOD), List.empty),
    ("Phoenix",  Some(Side.GOOD),List.empty),
    ("Nightcrawler", Some(Side.GOOD), List.empty),
    ("Wolverine", Some(Side.GOOD), List.empty),
    ("Storm", Some(Side.GOOD), List.empty),
    ("33333", Some(Side.GOOD), List("09508a52-508c-4ffb-b89d-fdc30a6af26b", "41c344b8-f6c4-491a-9d66-a0404b497502"))
  )

  def convertHeroToDocument(hero : Hero) : Document = {
    Document("_id" -> hero.id, "name" -> hero.name, "side" -> hero.side.get.id, "friends" -> hero.friends)
  }

  def convertDocumentToHero (document : Document) : Hero = {
    Hero(
      document.get("_id").get.asString().getValue,
      document.get("name").map(_.asString().getValue).getOrElse("No name"),
      document.get("side").map { elem => Side.values.find(_.id == elem.asInt32().getValue).getOrElse(Side.NEUTRAL)},
      document.get("friends").get.asArray().toArray.toList.map(_.asInstanceOf[BsonValue].asString().getValue)
    )
  }

  def genericUpdate (id : String, field : String, value : AnyRef)  (implicit ec : ExecutionContext) : Future[Boolean] = {
    collection.updateOne(equal("_id", id),set(field, value)).toFuture().recoverWith {
      case e : Throwable => Future.failed(e)
    }.map(_.headOption.map(_.getMatchedCount).exists(elem => if (elem == 1) true else false))
  }

  def updateAndFind (id : String, field : String, value : AnyRef)  (implicit ec : ExecutionContext) : Future[Option[Hero]] = {
    for {
      resultUpdate <- genericUpdate(id, field, value)
      heroUpdated <- if (resultUpdate) getHeroByID(id) else Future.successful(None)
    } yield {
      heroUpdated
    }
  }

  def genericDelete (field : String, value : String) (implicit ec : ExecutionContext) : Future[Boolean] = {
    collection.deleteMany(equal(field, value)).toFuture().recoverWith {
      case e : Throwable => Future.failed(e)
    }.map (_.headOption.map(_.getDeletedCount).exists(elem => if (elem == 1) true else false))
  }

  // -------------------------------------------------------------------------------------------------------------- //

  // Read

  def getHeroes ()(implicit ec : ExecutionContext): Future[List[Hero]] = {
    collection.find().toFuture().recoverWith {
      case e: Throwable =>
        println(e.getMessage)
        Future.failed(e)
    }.map(_.map(convertDocumentToHero).toList.sortBy(_.id))
  }

  def getHeroByID(id : String)(implicit ec:ExecutionContext): Future[Option[Hero]] = {
    collection.find(equal("_id",  id)).toFuture().recoverWith {
      case e: Throwable =>
        println(e.getMessage)
        Future.failed(e)
    }.map(_.headOption.map(convertDocumentToHero))
  }

  def getHeroByName(name : String)(implicit ec:ExecutionContext): Future[Option[Hero]] = {
    collection.find(equal("name", name)).toFuture().recoverWith {
      case e: Throwable =>
        println(e.getMessage)
        Future.failed(e)
    }.map(_.headOption.map(convertDocumentToHero))
  }

  def convertListIdToListHero( ids : List[String]) (implicit ec:ExecutionContext) : Future[List[Hero]] = {
    for {
      hero <- Future.sequence(ids.map(getHeroByID))
    } yield {
      hero.filter(_.isDefined).map(_.get)
    }
  }

  // -------------------------------------------------------------------------------------------------------------- //

  def createHeroes (test : Boolean) (implicit ec : ExecutionContext) : Future[List[Hero]] = {
    Future.sequence(heroes map {
      case (name, side, friends) =>
        createHero(name, side.map(_.id).getOrElse(Side.NEUTRAL.id), Some(friends.toVector)).filter(_.isDefined).map(_.get)
    })
  }

  def createHero (name : String, side : Int, friends : Option[Vector[String]]) (implicit ec : ExecutionContext) : Future[Option[Hero]] = {
    val hero = Hero(UUID.randomUUID().toString, name, Hero.convertStringToSide(side), friends.map(_.toList).getOrElse(List.empty))
    collection.insertOne(convertHeroToDocument(hero)).toFuture().recoverWith {
      case e : Throwable =>
        println(e.getMessage)
        Future.failed(e)
    }.map(_.nonEmpty).map{insertResult => if (insertResult) Some(hero) else None}
  }

  def updateHeroName (id : String, name : String) (implicit ec : ExecutionContext) : Future[Option[Hero]] = {
    updateAndFind(id, "name", name)
  }

  def deleteHeroByID (id : String) (implicit ec : ExecutionContext) : Future[Boolean] = {
    genericDelete("_id",id)
  }

  def deleteHeroByName (name : String) (implicit ec : ExecutionContext) : Future[Boolean] = {
    genericDelete("name" ,name)
  }

  def deleteHeroes (test : Boolean) (implicit ec : ExecutionContext) : Future[List[Boolean]] = {
    for {
      heroes <- getHeroes()
      heroesDeleted <- Future.sequence(heroes.map(hero => deleteHeroByID(hero.id)))
    } yield {
      heroesDeleted
    }
  }
}
