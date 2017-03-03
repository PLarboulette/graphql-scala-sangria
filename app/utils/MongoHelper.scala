package utils


import org.mongodb.scala.{Completed, Observable, Observer}

import scala.concurrent.{Future, Promise}

/**
  * Created by Pierre Larboulette on 28/02/2017.
  */
object MongoHelper {


  def wrapperSubscribe(observable : Observable[Completed]) : Observable[Completed] = {
    observable.subscribe(new Observer[Completed] {
      override def onError(e: Throwable) = println(e.getMessage)
      override def onComplete() = println("Finished")
      override def onNext(result: Completed) = println("Element inserted")
    })
    observable
  }


}
