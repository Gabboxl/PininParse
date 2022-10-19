import ga.gabboxl.pininparse.PininParse

suspend fun main(){
    PininParse.Periodi.init("    ")

    println(PininParse.Periodi.list())
}