package ga.gabboxl.pininparse

import java.io.File
import java.net.URL

class PininParse {
    companion object {
        fun classi(): MutableList<ArrayList<Any>> {
            var pages = mutableListOf<ArrayList<Any>>()

            val apiResponsePeriodi =
                URL("https://intranet.itispininfarina.it/orario/_ressource.js").readText().lines()

            for (page in apiResponsePeriodi) {
                if (page.contains("new Ressource")) {
                    var pattern = Regex("""listeRessources \[\d+]\s?=\s?new Ressource \(("(.*)",?\s?\n?)+\);""")
                    var fatt = pattern.find(page)!!.groupValues.get(1)
                    //println(fatt)

                    var patternSplit = Regex(""",(?=(?:[^"]*"[^"]*")*(?![^"]*"))""")
                    var values = patternSplit.split(fatt)
                    //println(fatto2)

                    var currNo = pages.count()
                    pages.plusAssign(arrayListOf<Any>())

                    var i = 0
                    while (i < values.count()) {

                        pages[currNo].plusAssign(values[i])

                        i++
                    }
                }
            }

            return pages
        }
    }
}