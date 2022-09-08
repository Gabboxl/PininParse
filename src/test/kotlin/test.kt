import ga.gabboxl.pininparse.PininParse

suspend fun main(){
    //PininParse().Classi().init()
    //PininParse().Periodi().init()
    PininParse.Update.init()

    //println(PininParse().Classi().list())
    //println(PininParse().Periodi().list())

    //println(PininParse().Classi().listNomiClassi())

    println(PininParse.Update.list())
}