package taller

import scala.annotation.tailrec

class ConjuntosDifusos() {

  type ConjDifuso = Int => Double

  def pertenece(elem: Int, s: ConjDifuso): Double = s(elem)

  def grande(d: Int, e: Int): ConjDifuso = {
    require(d >= 1, "d debe ser >= 1")
    require(e >= 1, "e debe ser >= 1")

    def g(n: Int): Double = {
      if (n <= 0) 0.0
      else {
        val ratio = n.toDouble / (n.toDouble + d.toDouble)
        val r = if (ratio < 0.0) 0.0 else if (ratio > 1.0) 1.0 else ratio
        math.pow(r, e.toDouble)
      }
    }

    g
  }

  def complemento(c: ConjDifuso): ConjDifuso = (x: Int) => {
    val v = c(x)
    1.0 - (if (v < 0.0) 0.0 else if (v > 1.0) 1.0 else v)
  }

  def union(cd1: ConjDifuso, cd2: ConjDifuso): ConjDifuso =
    (x: Int) => math.max(cd1(x), cd2(x))

}