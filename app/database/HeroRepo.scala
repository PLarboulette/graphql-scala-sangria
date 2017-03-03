package database

import java.util.UUID

import models.{Hero, Side}
import org.bson.BsonValue
import org.mongodb.scala.bson.BsonValue
import org.mongodb.scala.model.Filters
import org.mongodb.scala.{Completed, Document, MongoClient, MongoCollection, MongoDatabase, Observable, Observer}

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

  val heroes : List[Hero] = List(
    Hero("1", Some("Aragorn"),Some(Side.GOOD), List.empty),
    Hero("2", Some("Legolas"), Some(Side.GOOD), List.empty),
    Hero("3", Some("Gimli"),  Some(Side.GOOD), List.empty),
    Hero("4", Some("Gandalf"),Some(Side.GOOD), List.empty),
    Hero("5", Some("Frodon"), Some(Side.GOOD), List.empty),
    Hero("6", Some("Sam"),  Some(Side.GOOD), List.empty),
    Hero("7", Some("Merry"), Some(Side.GOOD), List.empty),
    Hero("8", Some("Pippin"), Some(Side.GOOD), List.empty),
    Hero("9", Some("Boromir"), Some(Side.GOOD), List.empty)
  )

  //  Utils

  def convertHeroToDocument(hero : Hero) : Document = {
    Document("_id" -> hero.id, "name" -> hero.name.get, "side" -> hero.side.get.id, "friends" -> hero.friends)
  }

  def convertDocumentToHero (document : Document) : Option[Hero] = {
    try {
      Some(Hero(
        document.get("_id").get.asString().getValue,
        document.get("name").map(_.asString().getValue),
        document.get("side").map { elem => Side.values.filter(_.id == elem.asInt32().getValue).head},
        document.get("friends").get.asArray().toArray.toList.map(_.asInstanceOf[BsonValue].asString().getValue)
      ))
    } catch {
      case e : Exception =>
        println(e.getMessage)
        None
    }
  }

  // Data
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
    collection.find(equal("name",  name)).toFuture().recoverWith {
      case e: Throwable => Future.failed(e)
    }.map(_.headOption.flatMap(convertDocumentToHero))
  }

  def addHeroes (test : Boolean) (implicit ec : ExecutionContext) : Future[List[Option[Hero]]] = {
    Future.sequence(heroes map {
      hero =>
        addHero(hero.name.getOrElse(""), hero.side.get.id.asInstanceOf[String], hero.friends.toVector)
    })
  }

  def addHero (name : String, side : String, friends : Vector[String]) (implicit ec : ExecutionContext) : Future[Option[Hero]] = {
    val sideHero = Hero.convertStringToSide(side)
    val hero = Hero(UUID.randomUUID().toString, Some(name), Some(sideHero), friends.toList)

    collection.insertOne(convertHeroToDocument(hero)).toFuture().recoverWith {
      case e : Throwable => Future.failed(e)
    }.map(_.nonEmpty).map{insertResult => if (insertResult) Some(hero) else None}
  }

  def updateName (id : String, name : String) (implicit ec : ExecutionContext) : Future[Boolean] = {
    collection.updateOne(equal("_id", id), set("name", name)).toFuture().recoverWith {
      case e : Throwable => Future.failed(e)
    }.map (_.nonEmpty)
  }

  def updateHero (id : String, name : String, side : String, friends : Vector[String])(implicit ec : ExecutionContext) : Future[Option[Hero]] = {
    val hero = Hero(id, Some(name), Some(Hero.convertStringToSide(side)), friends.toList)
    val docHero = convertHeroToDocument(hero)

    collection.replaceOne(Filters.eq("_id", id),docHero).toFuture().recoverWith {
      case e : Throwable => Future.failed(e)
    }.map (_.nonEmpty).map{insertResult => if (insertResult) Some(hero) else None}
  }





}
