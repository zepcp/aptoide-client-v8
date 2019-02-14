package cm.aptoide.pt.codelabs

import java.util.*

sealed class KotlinCreatorResult
data class Success(val creators: List<KotlinCreator>): KotlinCreatorResult()
data class Failure(val message: String): KotlinCreatorResult()

fun retrieveCreators(): KotlinCreatorResult {
    val kotlinCreator = KotlinCreator("name","id")
    return Success(Arrays.asList(kotlinCreator, kotlinCreator.copy(), kotlinCreator.copy()))
}

fun main(){
    val result = retrieveCreators()

    when(result){
        is Success -> result.creators.forEach { println(it.name)}
        is Failure -> println(result.message)
    }
}