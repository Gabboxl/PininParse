import ga.gabboxl.pininparse.PininParse

suspend fun main(){
    PininParse.Update.init(null)

    println(PininParse.Update.list())

    println(PininParse.Update.getDate())
}