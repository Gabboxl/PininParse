import ga.gabboxl.pininparse.PininParse

suspend fun main(){
    PininParse.Classi.init("https://orario.itispininfarina.it/")

    println(PininParse.Classi.list())

    println(PininParse.Classi.listNomiClassi())
}