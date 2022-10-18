package ga.gabboxl.pininparse

import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory


class PininParse {
    companion object {
        const val basePininLink = "https://orario.itispininfarina.it/"

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

        private fun fixServerLink(serverLink: String): String {
            var serverLinkOk = serverLink.trim()

            //controllo se la sintassi del link è corretta, altrimenti sta funzione lancia un'eccezione / forse un metodo migliore esiste comunque
                URL(serverLinkOk).toURI()

            if(!serverLinkOk.endsWith("/") && !serverLinkOk.endsWith("\\")){
                serverLinkOk = serverLink.plus("/")
            }

            return serverLinkOk
        }
    }


    object Update {
        private var match: String? = null

        suspend fun init(serverLink: String = basePininLink) {
            val apiResponsePeriodi =
                URL(fixServerLink(serverLink) + "_bandeau.js").readText().lines()


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
            return SimpleDateFormat("dd/MM/yyyy").parse(match)
        }
    }

    //struttura dati periodo: [codiceclasse, nomeperiodo, semilinkimg, titoloperiodo] / a volte i tizi che fanno l'orario mettono le date dell'orario nel nomeperiodo e a volte nel titoloperiodo, quindi cio' è da tenere in conto
    object Periodi {
        private val pages = arrayListOf<ArrayList<String>>()

        suspend fun init(serverLink: String = basePininLink) {
            val serverLinkFin = fixServerLink(serverLink)

            val apiResponsePeriodi =
                URL(serverLinkFin + "_periode.js").readText().lines()

            pages.clear()

            for (line in apiResponsePeriodi) {
                if (line.contains("new Periode") && !line.contains("Selezionate") && line.contains("c0")) { //skippo i periodi falsi
                    val pattern = Regex("""listePeriodes \[\d+]\s?=\s?new Periode \(("(.*)",?\s?\n?)+\);""")
                    parseEDTjs(pattern, line, pages)


                    val patternxmlnomefile = Regex("""^(.*?)_""")

                    val nomefilexml =
                        patternxmlnomefile.find(pages[pages.count() - 1][2])!!.groupValues[1] //gruppo 1 per questo pattern: https://regex101.com/r/7PBxGL/1

                    val contenutofilexml = URL(serverLinkFin + "classi/" + nomefilexml + ".xml").readText().byteInputStream()


                    val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
                    val builder = factory.newDocumentBuilder()


                    val documento = builder.parse(contenutofilexml)

                    //è importante a quanto pare
                    documento.documentElement.normalize()

                    val xPath: XPath = XPathFactory.newInstance().newXPath()
                    val xpathStr = "//Document//ParametrePublication//Grille//String[2]//text()"

                    val titoloPeriodo = xPath.evaluate(xpathStr, documento, XPathConstants.STRING)

                    //aggiungo il titolo dell'orario dall'xml all'indice - 1 perche' con la funzione parseEDTjs ho gia' aggiunto un indice
                    pages[pages.count() - 1].plusAssign(titoloPeriodo.toString()) //aggiungo il titolo del periodo all'array corrente del periodo
                }
            }
        }

        fun list(): MutableList<ArrayList<String>> {
            return pages
        }
    }

    object Classi {
        private val pages = arrayListOf<ArrayList<String>>()

        suspend fun init(serverLink: String = basePininLink) {
            val apiResponsePeriodi =
                URL(fixServerLink(serverLink) + "_ressource.js").readText().lines()

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