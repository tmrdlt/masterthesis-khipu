package de.tmrdlt.utils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object FutureUtil {

  def mergeFutureSeqs[X](fl1: Future[Seq[X]],
                          fl2: Future[Seq[X]]): Future[Seq[X]] =
    fl1 zip fl2 map Function.tupled(_ ++ _)

  // Runs List[Future[U]] sequentially (https://stackoverflow.com/a/20415056)
  def linearize[T, U](items: IterableOnce[T])(yourfunction: T => Future[U]): Future[List[U]] = {
    items.iterator.foldLeft(Future.successful[List[U]](Nil)) {
      (f, item) => f.flatMap {
        x => yourfunction(item).map(_ :: x)
      }
    } map (_.reverse)
  }

}
