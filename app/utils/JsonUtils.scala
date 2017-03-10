package utils

import play.api.libs.json._

import scala.collection.immutable.ListMap

/**
  * Created by Pierre Larboulette on 23/02/2017.
  */

object JsonUtils {

  val readsNumber = new Reads[JsValue] {
    def reads(json: JsValue): JsResult[JsNumber] = json match {
      case JsString(s) if s.nonEmpty && s.forall(Character.isDigit) =>
        JsSuccess(JsNumber(s.toInt))
      case JsString(s) if s.isEmpty =>
        JsError(s"empty value")
      case JsNumber(n) =>
        JsSuccess(JsNumber(n))
      case _ => JsError(s"'$json' value isn't a number")
    }
  }
  val readsBoolean = new Reads[JsBoolean] {
    def reads(json: JsValue): JsResult[JsBoolean] = json match {
      case JsString(s) if List("0", "1").contains(s) =>
        JsSuccess(JsBoolean(json.as[String] == "1"))
      case JsNumber(n) if n.intValue() == 0 || n.intValue() == 1 =>
        JsSuccess(JsBoolean(n.intValue() == 1))

      case _ => JsError(s"$json value isn't boolean")
    }
  }

  val readsEmail = new Reads[JsValue] {
    def reads(json: JsValue): JsResult[JsValue] = json match {
      case JsString(s) if s.isEmpty =>
        JsSuccess(json)
      case JsString(s) =>
        Reads.email.reads(json) match {
          case JsSuccess(v, p) => JsSuccess(JsString(v))
          case e:JsError => e
        }
      case _ => JsError(s"'$json' value isn't a string")
    }
  }

  implicit val jsErrorWrites = new Writes[JsError] {
    def writes(jsError: JsError): JsValue =
      Json.toJson(jsError.errors.map(entry => Json.obj("path" -> entry._1.toString(), "errors" -> Json.toJson(entry._2.map(error => error.message).toList.distinct))).toList)
  }

  def csvToJsons[A<:JsValue](csv:String, headers:List[String], validator:Reads[A]):List[JsResult[A]] = {
    //    csv.split("\n").map(csvLine => Json.toJson((headers zip csv.split(";")).toMap).validate(validator)).toList
    csv.split("\n").map{csvLine => println(Json.toJson(ListMap[String, String](headers zip csv.split(";") : _*)).validate(validator).toString);Json.toJson(ListMap[String, String](headers zip csv.split(";") : _*)).validate(validator)}.toList
  }

  def readIn(values:String*) = new Reads[JsValue] {
    def reads(json:JsValue):JsResult[JsValue] =  json match {
      case JsString(s) if values.contains(s) =>
        JsSuccess(json)
      case _ => JsError(s"$json value isn't contains in [${values.mkString(",")}]")
    }
  }



  def map(array: JsArray, f: JsValue => JsValue): JsValue = {
    Json.toJson(array.as[List[JsValue]].map(f))
  }

  def enumReads[E <: Enumeration](enum: E): Reads[E#Value] = new Reads[E#Value] {
    def reads(json: JsValue): JsResult[E#Value] = json match {
      case JsString(s) => {
        try {
          JsSuccess(enum.withName(s))
        } catch {
          case _: NoSuchElementException => JsError(s"Enumeration expected of type: '${enum.getClass}', but it does not appear to contain the value: '$s'")
        }
      }
      case _ => JsError("String value expected")
    }
  }

  implicit def enumWrites[E <: Enumeration]: Writes[E#Value] = new Writes[E#Value] {
    def writes(v: E#Value): JsValue = JsString(v.toString)
  }

  def isJson(value:String):Boolean = {
    try {
      Json.parse(value) != null
    } catch {
      case e:Exception => false
    }
  }

}
