package ga.gabboxl.pininparse

import java.net.URL

class PininParse {
    companion object {

        private fun parseEDTjs(pattern: Regex, line: String, pages: MutableList<ArrayList<String>>) {
            val fatt = pattern.find(line)!!.groupValues[1]

            val patternSplit = Regex(""",(?=(?:[^"]*"[^"]*")*(?![^"]*"))""")
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
            private val pages = mutableListOf<ArrayList<String>>()

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
            private val pages = mutableListOf<ArrayList<String>>()

            suspend fun init(){
                val apiResponsePeriodi =
                    URL("https://orario.itispininfarina.it/_ressource.js").readText().lines()

                for (line in apiResponsePeriodi) {
                    if (line.contains("new Ressource")) {
                        val pattern = Regex("""listeRessources \[\d+]\s?=\s?new Ressource \(("(.*)",?\s?\n?)+\);""")
                        parseEDTjs(pattern, line, pages)
                    }
                }
            }

            fun list(): MutableList<ArrayList<String>> {
                return pages
            }

            fun listNomiClassi(withFirstElement: Boolean): ArrayList<String>{
                val nomiclassi = arrayListOf<String>()

                for (pagina in pages){
                    nomiclassi.add(pagina[1])
                }

                if (!withFirstElement){
                    nomiclassi.removeAt(0)
                }

                return nomiclassi
            }
        }

    }
}