package de.tmrdlt.utils

trait OptionExtensions {

  implicit class RichOptionExtensions[A](o: Option[A]) {

    def getOrException(throwable: Throwable): A = o.fold(throw throwable) { x => x }

    def getOrException(errorMsg: String): A = o.fold(throw new Exception(errorMsg)) { x => x }

    def getOrException(): A = getOrException("Could not get value of None.")
  }

}
