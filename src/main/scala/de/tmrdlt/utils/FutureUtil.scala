package de.tmrdlt.utils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object FutureUtil {

  def mergeFutureSeqs[X](fl1: Future[Seq[X]],
                          fl2: Future[Seq[X]]): Future[Seq[X]] =
    fl1 zip fl2 map Function.tupled(_ ++ _)

}
