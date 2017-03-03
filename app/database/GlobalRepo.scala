package database

import java.util.UUID

import models.{Hero, Side}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by Pierre Larboulette on 02/03/2017.
  */
class GlobalRepo {

  def getHeroes ()(implicit ec : ExecutionContext): Future[List[Hero]] = {
    new HeroRepo().getHeroes()
  }

  def getHeroByID(id : String)(implicit ec:ExecutionContext): Future[Option[Hero]] = {
    new HeroRepo().getHeroByID(id)
  }

  def getHeroByName(name : String)(implicit ec:ExecutionContext): Future[Option[Hero]] = {
    new HeroRepo().getHeroByName(name)
  }

  def createHero (name : String, side : String, friends : Vector[String]) (implicit ec:ExecutionContext): Future[Option[Hero]] = {
    new HeroRepo().addHero(name, side, friends)
  }

  def updateHero (id : String, name : String, side : String, friends : Vector[String])(implicit ec:ExecutionContext): Future[Option[Hero]] = {
    new HeroRepo().updateHero(id, name, side, friends)
  }

}
