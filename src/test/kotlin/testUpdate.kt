import ga.gabboxl.pininparse.PininParse

suspend fun main(){
    PininParse.Update.init()

    println(PininParse.Update.list())
}