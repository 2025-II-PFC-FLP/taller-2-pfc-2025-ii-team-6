# Conjuntos Difusos

## Introducción

Este documento presenta la implementación y análisis del **módulo de Conjuntos Difusos** desarrollado en Scala. Un conjunto difuso permite representar grados de pertenencia de los elementos, en contraste con los conjuntos clásicos (crisp), donde un elemento solo puede pertenecer (1) o no pertenecer (0) al conjunto.

La teoría de conjuntos difusos fue introducida por Lotfi Zadeh en 1965 y es ampliamente utilizada en sistemas de control, inteligencia artificial y toma de decisiones, donde la imprecisión o gradualidad es necesaria.

## Definiciones Matemáticas

### Función de Pertenencia

Un conjunto difuso ( S ) sobre un universo ( U ) está definido por su **función de pertenencia**:

$$f_S: U \rightarrow [0,1]$$

Para cada elemento ( x \in U ):

* ( f_S(x) = 0 ) indica que no pertenece al conjunto.
* ( f_S(x) = 1 ) indica pertenencia total.
* ( 0 < f_S(x) < 1 ) indica pertenencia parcial.

### Operaciones Fundamentales

* **Complemento:**
  $$f_{\neg S}(x) = 1 - f_S(x)$$

* **Unión:**
  $$f_{S \cup T}(x) = \max(f_S(x), f_T(x))$$

* **Intersección:**
  $$f_{S \cap T}(x) = \min(f_S(x), f_T(x))$$

* **Inclusión:**
  $$S \subseteq T \iff \forall x, f_S(x) \le f_T(x)$$

* **Igualdad:**
  $$S = T \iff f_S(x) = f_T(x), \forall x$$

## Descripción del Código

En el código, un **Conjunto Difuso** se representa como una función:

```scala
 type ConjDifuso = Int => Double
```

### Función `grande(d, e)`

Define el conjunto difuso de elementos "grandes":

$$f_{grande}(n) = \left(\frac{n}{n + d}\right)^e$$

* **Parámetros:**

    * `d ≥ 1`: controla el punto de transición.
    * `e ≥ 1`: controla la pendiente de la curva.
* Si ( n \le 0 ), entonces ( f(n) = 0 ).
* A medida que ( n \to \infty ), ( f(n) \to 1 ).

### Función `complemento(c)`

Genera el complemento de un conjunto difuso:

```scala
(x: Int) => 1.0 - c(x)
```

Asegura que los valores estén en el rango [0, 1].

### Función `union(cd1, cd2)`

Implementa la unión difusa usando el máximo:

```scala
(x: Int) => math.max(cd1(x), cd2(x))
```

### Función `interseccion(cd1, cd2)`

Implementa la intersección difusa usando el mínimo:

```scala
(x: Int) => math.min(cd1(x), cd2(x))
```

### Función `inclusion(cd1, cd2)`

Verifica si ( cd1 \subseteq cd2 ) evaluando los valores en el rango ([0,1000]) con tolerancia (\varepsilon = 10^{-9}).
Utiliza **recursión de cola**:

```scala
@tailrec
def aux(i: Int): Boolean = {
  if (i > 1000) true
  else if (cd1(i) <= cd2(i) + EPS) aux(i + 1)
  else false
}
```

### Función `igualdad(cd1, cd2)`

Define igualdad como inclusión mutua:

```scala
inclusion(cd1, cd2) && inclusion(cd2, cd1)
```

### Función `pertenece(elem, s)`

Devuelve el grado de pertenencia de `elem` al conjunto `s`:

```scala
s(elem)
```

## Ejemplo Práctico

Consideremos:

```scala
val g1 = grande(2, 2)
val g2 = grande(5, 3)
```

* ( g1(1) = (1/(1+2))^2 = (1/3)^2 = 0.1111 )
* ( g2(1) = (1/(1+5))^3 = (1/6)^3 = 0.0046 )

**Unión:**
$$f_{g1 \cup g2}(1) = \max(0.1111, 0.0046) = 0.1111$$

**Intersección:**
$$f_{g1 \cap g2}(1) = \min(0.1111, 0.0046) = 0.0046$$

**Complemento de g1:**
$$f_{\neg g1}(1) = 1 - 0.1111 = 0.8889$$

## Diagrama del Proceso Recursivo

### Inclusión

```mermaid
flowchart TD
    A[Inicio: inclusion(cd1, cd2, i=0)] --> B{i > 1000?}
    B -- Sí --> C[return true]
    B -- No --> D{cd1(i) <= cd2(i) + EPS?}
    D -- Sí --> E[aux(i+1)]
    D -- No --> F[return false]
```

## Estado de la Pila de Llamados en `inclusion`

A continuación se muestra el estado de la **pila de llamados** cuando se evalúa `inclusion(cd1, cd2)` para ( i = 0, 1, 2, 3 ) suponiendo que ninguna de las condiciones de corte se cumple hasta ( i = 3 ).

```mermaid
graph TB
    subgraph Paso_0
        A0[aux(0)]
    end

    subgraph Paso_1
        A1[aux(1)] --> A0[retorna a aux(0)]
    end

    subgraph Paso_2
        A2[aux(2)] --> A1[retorna a aux(1)]
    end

    subgraph Paso_3
        A3[aux(3)] --> A2[retorna a aux(2)]
    end
```

* **Paso 0:** Se llama `aux(0)`.
* **Paso 1:** Como la condición no se cumple, se llama `aux(1)` que queda en el tope de la pila.
* **Paso 2:** Se llama `aux(2)`, desplazando las llamadas previas hacia abajo.
* **Paso 3:** Se llama `aux(3)` y así sucesivamente hasta que se alcanza la condición de corte.

Este diagrama muestra cómo la pila crece a medida que se incrementa `i`. Cuando una condición devuelve `true` o `false`, las llamadas pendientes se resuelven en orden inverso (última en entrar, primera en salir — LIFO).

## Análisis de Complejidad

| Operación      | Complejidad Temporal |
| -------------- | -------------------- |
| `grande`       | O(1)                 |
| `complemento`  | O(1)                 |
| `union`        | O(1)                 |
| `interseccion` | O(1)                 |
| `inclusion`    | O(N) con N=1000      |
| `igualdad`     | O(2N)                |

* **Espacial:** Todas las funciones son **inmutables** y usan espacio O(1), salvo `inclusion` que consume espacio O(N) por la recursión.

## Ventajas de la Representación Funcional

1. **Inmutabilidad:** evita efectos secundarios.
2. **Elegancia matemática:** los conjuntos se expresan como funciones.
3. **Extensibilidad:** permite definir nuevos conjuntos difusos fácilmente.
4. **Compatibilidad con operaciones funcionales:** composición, mapeo, etc.

## Conclusiones

El módulo implementado satisface los requisitos de la teoría de conjuntos difusos y aprovecha las características de Scala como funciones de orden superior, inmutabilidad y recursión de cola.

Esta solución es eficiente, correcta y fácilmente extensible a otros dominios como control difuso y sistemas de inferencia.
ConjuntosDifusosDocs.md
6 KB