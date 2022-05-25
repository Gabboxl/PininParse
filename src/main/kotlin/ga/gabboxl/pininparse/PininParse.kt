package ga.gabboxl.pininparse

import java.net.URL

class PininParse {
    companion object {

        private fun parseEDTjs(pattern: Regex, line: String, pages: ArrayList<ArrayList<String>>) {
            val fatt = pattern.find(line)!!.groupValues[2]

            val patternSplit = Regex("""",(?=([^"]*"[^"]*")*)"""")
            val values = patternSplit.split(fatt)

            val currNo = pages.count()
            pages.plusAssign(arrayListOf<String>())

            var i = 0
            while (i < values.count()) {

                pages[currNo].plusAssign(values[i])

                i++
            }
        }

    }

    class Periodi {
        companion object {
            private val pages = arrayListOf<ArrayList<String>>()

            suspend fun init(){
                val apiResponsePeriodi =
                    URL("https://orario.itispininfarina.it/_periode.js").readText().lines()

                for (line in apiResponsePeriodi) {
                    if (line.contains("new Periode")) {
                        val pattern = Regex("""listePeriodes \[\d+]\s?=\s?new Periode \(("(.*)",?\s?\n?)+\);""")
                        parseEDTjs(pattern, line, pages)
                    }
                }
            }

            fun list(): MutableList<ArrayList<String>> {
                return pages
            }
        }
    }

    class Classi {

        companion object {
            private val pages = arrayListOf<ArrayList<String>>()

            suspend fun init(){
                val apiResponsePeriodi =
                    URL("https://orario.itispininfarina.it/_ressource.js").readText().lines()


                for (line in apiResponsePeriodi) {
                    if (line.contains("new Ressource")) {
                        val pattern = Regex("""listeRessources \[\d+]\s?=\s?new Ressource \(("(.*)",?\s?\n?)+\);""")
                        parseEDTjs(pattern, line, pages)
                    }
                }

                pages.removeAt(0) //levo il primo elemento che contiene selezionate...
            }

            fun list(): ArrayList<ArrayList<String>> {
                return pages
            }

            fun listNomiClassi(): ArrayList<String>{
                val nomiclassi = arrayListOf<String>()

                for (pagina in pages){
                    nomiclassi.add(pagina[1])
                }
                return nomiclassi
            }
        }

    }
}