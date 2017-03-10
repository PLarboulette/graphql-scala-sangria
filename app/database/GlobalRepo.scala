package database


import models.Hero

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Pierre Larboulette on 02/03/2017.
  */
class GlobalRepo {

  val heroRepo = new HeroRepo()

  def getHeroes ()(implicit ec : ExecutionContext): Future[List[Hero]] = {
    heroRepo.getHeroes()
  }

  def getHeroByID(id : String)(implicit ec:ExecutionContext): Future[Option[Hero]] = {
    heroRepo.getHeroByID(id)
  }

  def getHeroByName(name : String)(implicit ec:ExecutionContext): Future[Option[Hero]] = {
    heroRepo.getHeroByName(name)
  }

  def createHero (name : String, side : Int, friends : Option[Vector[String]]) (implicit ec:ExecutionContext): Future[Option[Hero]] = {
    heroRepo.createHero(name, side, friends)
  }

  def createHeroes (test : Boolean) (implicit ec:ExecutionContext): Future[List[Hero]] = {
    heroRepo.createHeroes(test)
  }

  def updateHeroName (id : String, name : String) (implicit ec:ExecutionContext): Future[Option[Hero]] = {
    heroRepo.updateHeroName(id, name)
  }

  def deleteHeroByID (id : String)  (implicit ec:ExecutionContext): Future[Boolean] = {
    heroRepo.deleteHeroByID(id)
  }

  def deleteHeroes (test : Boolean)(implicit ec:ExecutionContext): Future[Boolean] = {
    heroRepo.deleteHeroes(test)
    Future(false)
  }

}
