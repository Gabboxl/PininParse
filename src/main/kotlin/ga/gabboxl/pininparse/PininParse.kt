package ga.gabboxl.pininparse

import java.net.URL

class PininParse {
    companion object {
        fun classi(): MutableList<ArrayList<Any>> {
            var pages = mutableListOf<ArrayList<Any>>()

            val apiResponsePeriodi =
                URL("https://intranet.itispininfarina.it/orario/_ressource.js").readText().lines()

            for (line in apiResponsePeriodi) {
                if (line.contains("new Ressource")) {
                    val pattern = Regex("""listeRessources \[\d+]\s?=\s?new Ressource \(("(.*)",?\s?\n?)+\);""")
                    parseEDTjs(pattern, line, pages)
                }
            }

            return pages
        }

        fun periodi(): MutableList<ArrayList<Any>> {
            var pages = mutableListOf<ArrayList<Any>>()

            val apiResponsePeriodi =
                URL("https://intranet.itispininfarina.it/orario/_periode.js").readText().lines()

            for (line in apiResponsePeriodi) {
                if (line.contains("new Periode")) {
                    val pattern = Regex("""listePeriodes \[\d+]\s?=\s?new Periode \(("(.*)",?\s?\n?)+\);""")
                    parseEDTjs(pattern, line, pages)
                }
            }

            return pages
        }

        private fun parseEDTjs(
            pattern: Regex,
            page: String,
            pages: MutableList<ArrayList<Any>>
        ) {
            var fatt = pattern.find(page)!!.groupValues.get(1)

            var patternSplit = Regex(""",(?=(?:[^"]*"[^"]*")*(?![^"]*"))""")
            var values = patternSplit.split(fatt)

            var currNo = pages.count()
            pages.plusAssign(arrayListOf<Any>())

            var i = 0
            while (i < values.count()) {

                pages[currNo].plusAssign(values[i])

                i++
            }
        }

    }
}