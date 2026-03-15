package level2

import scala.xml.XML
import java.nio.file.{Files, Paths}
import scala.collection.mutable.ListBuffer
import scala.io.Source

// --------- Effects ---------
// A Unary Function: logic to fetch data
def fetchData(xmlPath: String): String = {
    println(s"  [Start] Processing $xmlPath")
    val source = Source.fromFile(xmlPath) // Effect
    val xmlContent =
        try
        source.mkString
        finally
        source.close()
    
    return xmlContent
}

// --------- Pure Logic ---------
// A Unary Function: logic to parse XML into a List of Strings
def parseRSS(xmlContent: String): List[(String, String)] = {
    val xml = XML.loadString(xmlContent)
    val itemNodes = xml \\ "item"
    val items = new ListBuffer[(String, String)]
    var i = 0
    while i < itemNodes.size do
        val node = itemNodes(i)
        val title = (node \ "title").text.trim
        val link = (node \ "link").text.trim
        items.append((title, link)) 
        i += 1
    return items.toList
}

// --------- Pure Logic ---------
def formatter(items: List[(String, String)]): String = {
    val sb = new StringBuilder
    var i = 0
    while i < items.size do
        val item = items(i)
        val title = item._1
        val link = item._2
        sb.append(s"Title: $title\nLink: $link\n---\n") 
        i += 1
    return sb.toString
}

// --------- Effects ---------
def saveToFile(path: String, content: String): Unit = {
    Files.write(Paths.get(path), content.getBytes)
}

// --------- Orchestration ---------
// An Extension Method adding Level 2 power to the feeds list
def processFeed(
    fetch: String => String, // Higher-Order Parameter
    parse: String => List[(String, String)], // Higher-Order P,arameter
    formatter: List[(String, String)] => String // Higher-Order Parameter
)(xmlPath: String): String = {
    val content: String = fetch(xmlPath)   
    val items: List[(String, String)] = parse(content)
    val formatted: String = formatter(items)
    return formatted
}

@main def runLevel2(): Unit =
  val bbcFeeds = List(
    "sample-data/level4/tech.xml" -> "output/tech_news.txt",
    "sample-data/level4/business.xml" -> "output/business_news.txt"
  )

  val rssProcessed = processFeed(fetchData, parseRSS, formatter)

  bbcFeeds.foreach:
    (xmlPath, path) =>
      val content: String = rssProcessed(xmlPath)
      saveToFile(path, content)