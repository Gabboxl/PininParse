package ga.gabboxl.pininparse

import java.net.URL

class PininParse {
    companion object {



        private fun parseEDTjs(pattern: Regex, line: String, pages: MutableList<ArrayList<Any>>) {
            val fatt = pattern.find(line)!!.groupValues[1]

            val patternSplit = Regex(""",(?=(?:[^"]*"[^"]*")*(?![^"]*"))""")
            val values = patternSplit.split(fatt)

            val currNo = pages.count()
            pages.plusAssign(arrayListOf<Any>())

            var i = 0
            while (i < values.count()) {

                pages[currNo].plusAssign(values[i])

                i++
            }
        }

    }

    class periodi {
        companion object {
            private val pages = mutableListOf<ArrayList<Any>>()

            init {

                val apiResponsePeriodi =
                    URL("https://intranet.itispininfarina.it/orario/_periode.js").readText().lines()

                for (line in apiResponsePeriodi) {
                    if (line.contains("new Periode")) {
                        val pattern = Regex("""listePeriodes \[\d+]\s?=\s?new Periode \(("(.*)",?\s?\n?)+\);""")
                        parseEDTjs(pattern, line, pages)
                    }
                }
            }

            fun list(): MutableList<ArrayList<Any>> {
                return pages
            }
        }
    }

    class classi {

        companion object {
            private val pages = mutableListOf<ArrayList<Any>>()

            init {

                val apiResponsePeriodi =
                    URL("https://intranet.itispininfarina.it/orario/_ressource.js").readText().lines()

                for (line in apiResponsePeriodi) {
                    if (line.contains("new Ressource")) {
                        val pattern = Regex("""listeRessources \[\d+]\s?=\s?new Ressource \(("(.*)",?\s?\n?)+\);""")
                        parseEDTjs(pattern, line, pages)
                    }
                }
            }

            fun list(): MutableList<ArrayList<Any>> {
                return pages
            }
        }

    }
}