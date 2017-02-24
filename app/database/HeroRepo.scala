package database

import models.Hero

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Pierre Larboulette on 24/02/2017.
  */
class HeroRepo {

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

  def getHeroes () (implicit ec : ExecutionContext): Future[List[Hero]] = {
    Future(heroes)
  }

  def getHeroByID (id : String) (implicit ec : ExecutionContext) : Future[Option[Hero]] = {
    Future(heroes.find(hero => hero.id equals id))
  }

  def getHeroByName (name : String) (implicit ec : ExecutionContext) : Future[Option[Hero]] = {
    Future(heroes.find(hero => hero.name.getOrElse("") equals name))
  }

}
