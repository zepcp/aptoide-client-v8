package cm.aptoide.pt.codelabs

import java.math.BigDecimal
import java.util.*

data class KotlinPresentation(val numberOfSlides: Int,
                              val title: String,
                              val javaCreator: JavaCreator,
                              val kotlinCreator: KotlinCreator)


fun getTitle(numberOfSlides: Int, title: String = ""): String {
    return title
}

fun something() {
    getTitle(title = "titulo", numberOfSlides = 4)
}

fun something2() {
    getTitle(numberOfSlides = 4)
}


fun sum(x: Int, y: Int) = x + y


fun getMyPresentation(kotlinPresentation: KotlinPresentation): KotlinPresentation {
    return when (kotlinPresentation.kotlinCreator.name) {
        "myName" -> kotlinPresentation
        else -> throw IllegalArgumentException("Not my presentation")
    }
}


fun getMyPresentation2(kotlinPresentation: KotlinPresentation) = when (kotlinPresentation.kotlinCreator.name) {
    "myName" -> kotlinPresentation
    else -> throw IllegalArgumentException("Not my presentation")
}


infix fun Int.percent(numberOfIntroductionSlides: Int) = numberOfIntroductionSlides / this

fun somethingWithExtensionFunction(numberOfSlides: Int) {
    numberOfSlides.percent(2)
    //OR
    numberOfSlides percent 2
}

val bigDecimal = 100.bd

private val Int.bd: Any
    get() = BigDecimal(this)

fun printJavaCreator(javaCreator: JavaCreator?) {
    println("${javaCreator?.name}")
}

fun printIfMyPresentation(kotlinCreator: KotlinCreator, predicate: (String) -> (Boolean)) {
    when (predicate.invoke(kotlinCreator.name)) {
        true -> println(kotlinCreator.name);
    }
}

fun testPredicate() {
    printIfMyPresentation(KotlinCreator("myName", "id")) { it.equals("MyName") }


    val strings: List<String> = Arrays.asList("a.pt", "a.com")
    val stringsFiltered = strings.filter { it.endsWith(".com") }
}

fun main(args: Array<String>) {

    val myKotlinPresentation = KotlinPresentation(5, "title",
            JavaCreator("name", "id"), KotlinCreator("myName", "id"))

    val hisKotlinPresentation = myKotlinPresentation.copy(kotlinCreator = KotlinCreator("hisName", "id"))

    val myKotlinPresentation2 = myKotlinPresentation.copy()

    if (myKotlinPresentation == hisKotlinPresentation) {
        System.out.print("They're the same")
    }

    if (myKotlinPresentation == myKotlinPresentation2) {
        System.out.print("They're the same")
    }

    //pointer comparision
    if (myKotlinPresentation === myKotlinPresentation2) {
        System.out.print("They're the same ${myKotlinPresentation.title}")
    }

    myKotlinPresentation.javaCreator.id
    Unit
}
