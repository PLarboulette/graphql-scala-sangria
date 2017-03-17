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
class HeroRepo  {

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

  //  Utils

  def convertHeroToDocument(hero : Hero) : Document = {
    Document("_id" -> hero.id, "name" -> hero.name, "side" -> hero.side.get.id, "friends" -> hero.friends)
  }

  def convertDocumentToHero (document : Document) : Option[Hero] = {
    try {
      Some(Hero(
        document.get("_id").get.asString().getValue,
        document.get("name").map(_.asString().getValue).head,
        document.get("side").map { elem => Side.values.filter(_.id == elem.asInt32().getValue).head},
        document.get("friends").get.asArray().toArray.toList.map(_.asInstanceOf[BsonValue].asString().getValue)
      ))
    } catch {
      case e : Exception =>
        None
    }
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
      case e: Throwable => Future.failed(e)
    }.map(_.map(convertDocumentToHero).toList.filter(_.nonEmpty).map(_.get).sortBy(_.id))
  }

  def getHeroByID(id : String)(implicit ec:ExecutionContext): Future[Option[Hero]] = {
    collection.find(equal("_id",  id)).toFuture().recoverWith {
        case e: Throwable => Future.failed(e)
    }.map(_.headOption.flatMap(convertDocumentToHero))
  }

  def getHeroByName(name : String)(implicit ec:ExecutionContext): Future[Option[Hero]] = {
    collection.find(equal("name", name)).toFuture().recoverWith {
      case e: Throwable => Future.failed(e)
    }.map(_.headOption.flatMap(convertDocumentToHero))
  }

  def convertListIdToListHero( ids : List[String]) (implicit ec:ExecutionContext) : Future[List[Hero]] = {

    if (ids nonEmpty) {

    } else {

    }

    for {
        hero <- if (ids.nonEmpty) Future.sequence(ids.map(getHeroByID)).filter(_.head.nonEmpty).map(_.map(_.get)) else Future.successful(List.empty)
      } yield {
        hero
      }
  }

  // -------------------------------------------------------------------------------------------------------------- //

  // Create

  def createHeroes (test : Boolean) (implicit ec : ExecutionContext) : Future[List[Hero]] = {
    Future.sequence(heroes map {
      hero =>
        createHero(hero._1, hero._2.get.id, Some(hero._3.toVector)).filter(_.nonEmpty).map(_.get)
    })
  }

  def createHero (name : String, side : Int, friends : Option[Vector[String]]) (implicit ec : ExecutionContext) : Future[Option[Hero]] = {
    val sideHero = Hero.convertStringToSide(side)
    val hero = Hero(UUID.randomUUID().toString, name, sideHero, friends.map(_.toList).getOrElse(List.empty))
    collection.insertOne(convertHeroToDocument(hero)).toFuture().recoverWith {
      case e : Throwable => Future.failed(e)
    }.map(_.nonEmpty).map{insertResult => if (insertResult) Some(hero) else None}
  }

  // -------------------------------------------------------------------------------------------------------------- //

  // Update

  def updateHeroName (id : String, name : String) (implicit ec : ExecutionContext) : Future[Option[Hero]] = {
    updateAndFind(id, "name", name)
  }

  // -------------------------------------------------------------------------------------------------------------- //

  // Delete

  def deleteHeroByID (id : String) (implicit ec : ExecutionContext) : Future[Boolean] = {
    genericDelete("_id",id)
  }

  def deleteHeroByName (name : String) (implicit ec : ExecutionContext) : Future[Boolean] = {
    genericDelete("name" ,name)
  }

  def deleteHeroes (test : Boolean) (implicit ec : ExecutionContext) : Future[Boolean] = {
    getHeroes.map {
      list =>
        list.map {
          hero => deleteHeroByID(hero.id)
        }
    }
    Future(false)
  }






}
