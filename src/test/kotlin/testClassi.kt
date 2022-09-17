import ga.gabboxl.pininparse.PininParse

suspend fun main(){
    PininParse.Classi.init()

    println(PininParse.Classi.list())

    println(PininParse.Classi.listNomiClassi())
}