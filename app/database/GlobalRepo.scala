package database

import models.Hero

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Pierre Larboulette on 02/03/2017.
  */

class GlobalRepo {

  def getHeroes ()(implicit ec : ExecutionContext): Future[List[Hero]] = {
    HeroRepo.getHeroes()
  }

  def getHeroByID(id : String)(implicit ec:ExecutionContext): Future[Option[Hero]] = {
    HeroRepo.getHeroByID(id)
  }

  def getHeroByName(name : String)(implicit ec:ExecutionContext): Future[Option[Hero]] = {
    HeroRepo.getHeroByName(name)
  }

  def createHero (name : String, side : Int, friends : Option[Vector[String]]) (implicit ec:ExecutionContext): Future[Option[Hero]] = {
    HeroRepo.createHero(name, side, friends)
  }

  def createHeroes (test : Boolean) (implicit ec:ExecutionContext): Future[List[Hero]] = {
    HeroRepo.createHeroes(test)
  }

  def updateHeroName (id : String, name : String) (implicit ec:ExecutionContext): Future[Option[Hero]] = {
    HeroRepo.updateHeroName(id, name)
  }

  def deleteHeroByID (id : String)  (implicit ec:ExecutionContext): Future[Boolean] = {
    HeroRepo.deleteHeroByID(id)
  }

  def deleteHeroes (test : Boolean)(implicit ec:ExecutionContext): Future[Boolean] = {
    HeroRepo.deleteHeroes(test)
  }

}
