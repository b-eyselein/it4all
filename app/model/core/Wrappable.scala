package model.core

trait MyWrapper {

  def wrappedObj: Wrappable

}

trait Wrappable {

  def wrapped: MyWrapper

}