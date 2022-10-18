import ga.gabboxl.pininparse.PininParse

suspend fun main(){
    PininParse.Periodi.init("https://orario.itispininfarina.it/")

    println(PininParse.Periodi.list())
}