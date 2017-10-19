package model

trait JsonReadable {

  def getId: Int

  def saveInDB(): Unit

}