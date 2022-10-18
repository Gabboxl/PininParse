import ga.gabboxl.pininparse.PininParse

suspend fun main(){
    PininParse.Update.init("https://orario.itispininfarina.it/")

    println(PininParse.Update.list())

    println(PininParse.Update.getDate())
}