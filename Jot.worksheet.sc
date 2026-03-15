def add(a: Int, b: Int): Int = {
    a + b
}

val result = add(1, 2)


def multiply(a: Int, b: Int): Int = {
    a * b
}

val result2 = multiply(2, 3)

def subtract(a: Int, b: Int): Int = {
    a - b
}

val result3 = subtract(5, 2)

val square = (x: Int) => x * x

def doThing(f: Int => Int, a: Int): Int = f.apply(a)

val result8 = doThing(square, 1)



////

def factorial(n: Int): Int = {
    if (n == 0) 1
    else n * factorial(n - 1)
}

val result9 = factorial(5)

val students1 = List(
    "Alice",
    "Bob",
    "Charlie",
    "David",
    "Eve",
)
val scores1 = List(100, 90, 80, 70, 60)

def transform(students: List[String], prefix: String): List[String] = {
    if (students.isEmpty) return List.empty
    val first = students.head
    val rest = students.tail
    return List(prefix + first) ++ transform(rest, prefix)
    
}

def transform2(scores: List[Int], adder: Int): List[Int] = {
    if (scores.isEmpty) return List.empty
    val first = scores.head
    val rest = scores.tail
    return List(first + adder) ++ transform2(rest, adder)
}

val result10 = transform(students1, "Mr. ")
println(result10)

val result11 = transform2(scores1, 10)
println(result11)


extension (data: List[String]) 
    def addPrefix(prefix: String): List[String] = {
    if (data.isEmpty) List.empty
    else List(prefix + data.head) ++ data.tail.addPrefix(prefix)
}

extension (data: List[Int]) 
    def transform(operation: (Int, Int) => Int, param: Int): List[Int] = {
    if (data.isEmpty) List.empty
    else List(operation(data.head, param)) ++ data.tail.transform(operation, param)
}


val result12 = students1.addPrefix("Mr. ")
println(result12)

val result13 = scores1.transform(add, 10)
println(result13)

val result14 = scores1.transform(multiply, 10)
println(result14)

val result15 = scores1.transform(subtract, 10)
println(result15)