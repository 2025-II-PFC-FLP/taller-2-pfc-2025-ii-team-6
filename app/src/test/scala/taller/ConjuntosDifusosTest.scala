package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ConjuntosDifusosTest extends AnyFunSuite {
  val cd = new ConjuntosDifusos
  import cd._
  val EPS = 1e-6

  // ---- Tests para grande ----
  test("grande: valores n <= 0 devuelven 0.0") {
    val g = grande(1, 2)
    assert(math.abs(g(0) - 0.0) < EPS)
    assert(math.abs(g(-5) - 0.0) < EPS)
  }

  test("grande: valores pequeños dan pertenencia baja y creciente") {
    val g = grande(1, 2)
    assert(g(1) >= 0.0 && g(1) < 0.5)
    assert(g(2) > g(1))
  }

  test("grande: valores grandes tienden a 1.0") {
    val g = grande(1, 2)
    assert(g(100) > 0.95)
    assert(g(1000) > 0.99)
  }

  // ---- Tests para complemento ----
  test("complemento: siempre entre 0 y 1") {
    val g = grande(1, 2)
    val cg = complemento(g)
    val x = 5
    val value = cg(x)
    assert(value >= 0.0 && value <= 1.0)
  }

  test("complemento: f(x) + f_complemento(x) ≈ 1") {
    val g = grande(1, 2)
    val cg = complemento(g)
    val x = 10
    assert(math.abs(g(x) + cg(x) - 1.0) < EPS)
  }

  // ---- Tests para union ----
  test("union: devuelve el máximo entre dos conjuntos") {
    val g1 = grande(1, 2)
    val g2 = grande(2, 3)
    val u = union(g1, g2)
    val x = 5
    assert(math.abs(u(x) - math.max(g1(x), g2(x))) < EPS)
  }
}

