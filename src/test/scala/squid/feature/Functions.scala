package squid
package feature

import utils._

class Functions extends MyFunSuite {
  import TestDSL.Predef._
  import Functions._
  
  test("Thunks") {
    
    val tbn = ir"takeByName(42)"
    
    tbn matches {
      case ir"takeByName(42)" =>
    } and {
      case ir"takeByName($n)" =>
        eqt(n, ir"42")
    }
    
    same(tbn.run, 43)
    same(ir"takeFunction(_ => 42)".run, 43)
    
  }
  
  test("Functions of Arity 0") {
    
    val f = ir"() => 42"
    same(f.run apply (), 42)
    same(ir"$f()".run, 42)
    
  }
  
  test("Functions of Arity 1") {
    
    val f = ir"(x: Int) => x + 1"
    same(f.run apply 1, 2)
    same(ir"$f(1)".run, 2)
    eqt(ir"${f}${ir"1"}", ir"$f(1)")
    
  }
  
  test("Functions of Arity 2") {
    
    val f = ir"(x: Int, y: Symbol) => x+y.toString"
    same(f.run apply (1,'ok), "1'ok")
    same(ir"$f(1,'ok)".run, "1'ok")
    
    f match {
      case ir"(x: Int, y: Symbol) => $body" =>
        body subs 'x -> ir"42" eqt ir"42 + ($$y:Symbol).toString"
    }
    f.erase match {
      case ir"$f: ($t => String)" => fail
      case _ =>
    }
    ir"$f(1,'ok)" match {
      case ir"$$f($a,$b)" =>
        eqt(a, ir"1")
        eqt(b, ir"'ok")
    }
    
    ir"$f(1,'ok)".erase match {
        
      case ir"((px: $tx, py: $ty) => $body: $t)($ax, $ay)" =>
        eqt(body subs 'px -> ax subs 'py -> ay, ir"1 + 'ok.toString")
        
    }
    
  }
  
  test("Functions of Arity 3") {
    
    val f = ir"(a: Int, b: Double, c: Float) => a * b + c"
    same(f.run apply (3,.5,13f), 14.5)
    same(ir"$f(3,.5,13f)".run, 14.5)
    
  }
  
  test("Functions of Arity 5") {
    
    val f = ir"(a: Int, b: Double, c: Float, x: Int, y: Int) => a * b + c - x * y"
    same(f.run apply (3,.5,13f,2,4), 6.5)
    same(ir"$f(3,.5,13f,2,4)".run, 6.5)
    
  }
  
  test("Functions of Arity 22") { // Not currently supported -- throws: java.lang.UnsupportedOperationException: Function type of arity 22 > 5
  intercept[java.lang.UnsupportedOperationException] {
    val f = ir"""
      (x1:Int, x2:Int, x3:Int, x4:Int, x5:Int, x6:Int, x7:Int, x8:Int, x9:Int, x10:Int, x11:Int, x12:Int, x13:Int, x14:Int, x15:Int, x16:Int, x17:Int, x18:Int, x19:Int, x20:Int, x21:Int, x22:Int) =>
       x1  +   x2  +   x3  +   x4  +   x5  +   x6  +   x7  +   x8  +   x9  +   x10  +   x11  +   x12  +   x13  +   x14  +   x15  +   x16  +   x17  +   x18  +   x19  +   x20  +   x21  +   x22
    """
    same(f.run apply (2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2), 44)
  }}
  
}
object Functions {
  def takeByName(x: => Int) = x+1
  def takeFunction(f: Unit => Int) = f(())+1
}


