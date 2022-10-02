package ga.gabboxl.pininparse

import com.google.gson.JsonParser
import org.json.XML
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class PininParse {
    companion object {
        const val baseLink = "https://orario.itispininfarina.it/"

        private fun parseEDTjs(pattern: Regex, line: String, pages: ArrayList<ArrayList<String>>) {
            val fatt =
                pattern.find(line)!!.groupValues[2] //forse da sostituire con il gruppo 1 perche mette anche gli apici allinizio e alla fine

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


    object Update {
            var match: String? = null

        suspend fun init() {
            val apiResponsePeriodi =
                URL(baseLink + "_bandeau.js").readText().lines()



            for (line in apiResponsePeriodi) {
                if (line.contains("Aggiornamento")) {
                    val pattern =
                        Regex("""var dateDerniereMaj = "Aggiornamento: (\d\d\/\d\d\/\d\d\d\d)\";""") //thx to https://regex101.com/
                    match = pattern.find(line)!!.groupValues[1]
                }
            }
        }

        fun list(): String? {
            return match
        }

        fun getDate(): Date {
            return SimpleDateFormat("dd/mm/yyyy").parse(match)
        }
    }

    //struttura dati periodo: [codiceclasse, nomeperiodo, semilinkimg, titoloperiodo] / a volte i tizi che fanno l'orario mettono le date dell'orario nel nomeperiodo e a volte nel titoloperiodo, quindi cio' e' da tenere in conto
    object Periodi {
            private val pages = arrayListOf<ArrayList<String>>()

        suspend fun init() {
            val apiResponsePeriodi =
                URL(baseLink + "_periode.js").readText().lines()

            pages.clear()

            for (line in apiResponsePeriodi) {
                if (line.contains("new Periode") && !line.contains("Selezionate") && line.contains("c0")) { //skippo i periodi falsi
                    val pattern = Regex("""listePeriodes \[\d+]\s?=\s?new Periode \(("(.*)",?\s?\n?)+\);""")
                    parseEDTjs(pattern, line, pages)


                    val patternxmlnomefile = Regex("""^(.*?)_""")

                    val nomefilexml =
                        patternxmlnomefile.find(pages[pages.count() - 1][2])!!.groupValues[1] //gruppo 1 per questo pattern: https://regex101.com/r/7PBxGL/1

                    val contenutofilexml = URL(baseLink + "classi/" + nomefilexml + ".xml").readText()

                    val jsonObjstring = XML.toJSONObject(contenutofilexml).toString()

                    val jsonparserjsonobject = JsonParser.parseString(jsonObjstring).asJsonObject //non so perche serva questo pero' l'ho messo e funziona per il parsing

                    val titoloPeriodo: String = jsonparserjsonobject.get("Document").asJsonObject.get("ParametrePublication").asJsonObject.get("Grille").asJsonObject.get("String").asJsonArray.get(1).asJsonObject.get("content").asString


                    //aggiungo il titolo dell'orario dall'xml all'indice - 1 perche' con la funzione parseEDTjs ho gia' aggiunto un indice
                    pages[pages.count() - 1].plusAssign(titoloPeriodo) //aggiungo il titolo del periodo all'array corrente del periodo
                }
            }
        }

        fun list(): MutableList<ArrayList<String>> {
            return pages
        }
    }

    object Classi {
            private val pages = arrayListOf<ArrayList<String>>()

        suspend fun init() {
            val apiResponsePeriodi =
                URL(baseLink + "_ressource.js").readText().lines()

            pages.clear()

            for (line in apiResponsePeriodi) {
                if (line.contains("new Ressource") && line.contains("grClasse")) {
                    val pattern = Regex("""listeRessources \[\d+]\s?=\s?new Ressource \(("(.*)",?\s?\n?)+\);""")
                    parseEDTjs(pattern, line, pages)
                }
            }

            pages.removeAt(0) //levo il primo elemento che contiene selezionate...
        }

        fun list(): ArrayList<ArrayList<String>> {
            return pages
        }

        fun listNomiClassi(): ArrayList<String> {
            val nomiclassi = arrayListOf<String>()

            for (pagina in pages) {
                nomiclassi.add(pagina[1])
            }
            return nomiclassi
        }
    }
}