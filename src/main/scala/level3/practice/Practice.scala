package level3
package practice

import scala.xml.XML
import java.nio.file.{Files, Paths}
import scala.io.Source

sealed trait Maybe[+A]:
  def map[B](f: A => B): Maybe[B]
  def flatMap[B](f: A => Maybe[B]): Maybe[B]
  def getOrElse[B >: A](default: => B): B
  def fold[B](ifEmpty: => B)(f: A => B): B

case class Just[+A](value: A) extends Maybe[A]:
  def map[B](f: A => B): Maybe[B] = Just(f(value))
  def flatMap[B](f: A => Maybe[B]): Maybe[B] = f(value)
  def getOrElse[B >: A](default: => B): B = value
  def fold[B](ifEmpty: => B)(f: A => B): B = f(value)

case object Empty extends Maybe[Nothing]:
  def map[B](f: Nothing => B): Maybe[B] = Empty
  def flatMap[B](f: Nothing => Maybe[B]): Maybe[B] = Empty
  def getOrElse[B](default: => B): B = default
  def fold[B](ifEmpty: => B)(f: Nothing => B): B = ifEmpty

// A Unary Function: logic to fetch data
def fetchData(url: String): Maybe[String] =
  try
    val source = Source.fromURL(url)
    try
      Just(source.mkString)
    finally
      source.close()
  catch
    case e: Exception =>
      println(s"Failed to fetch data from $url: ${e.getMessage}")
      Empty

// A Unary Function: logic to parse XML into a List of Strings
def parseRSS(xmlContent: String): List[(String, String)] =
  val xml = XML.loadString(xmlContent)
  val items: List[(String, String)] = (xml \\ "item").toList.map: node =>
    val title = (node \ "title").text.trim
    val link = (node \ "link").text.trim
    (title, link)
  items

def formatter(items: List[(String, String)]): String =
  items
    .map((title, link) => s"Title: $title\nLink: $link\n---")
    .mkString("\n")

def saveToFile(path: String, content: String): Maybe[Unit] =
  try
    Files.write(Paths.get(path), content.getBytes)
    println(s"✅ Saved to $path")
    Just(())
  catch
    case e: Exception =>
      Empty

// An Extension Method adding Level 2 power to the feeds list
def processFeed(
    fetch: String => Maybe[String], // Higher-Order Parameter
    parse: String => List[(String, String)], // Higher-Order P,arameter
    formatter: List[(String, String)] => String // Higher-Order Parameter
)(url: String): Maybe[String] =
  val fetched: Maybe[String] = fetch(url)
  val parsed: Maybe[List[(String, String)]] = fetched.map(content => parse(content))
  val formatted: Maybe[String] = parsed.flatMap((items) => Just(formatter(items)))
  formatted
  
  
@main def runLevel2(): Unit =
  val bbcFeeds = List(
    "https://feeds.bbci.co.uk/news/world/rss.xml" -> "world_news.txt",
    "https://feeds.bbci.co.uk/news/technology/rss.xml" -> "tech_news.txt",
    "https://feeds.bbci.co.uk/news/business/rss.xml" -> "business_news.txt"
  )

  val rssProcessed = processFeed(fetchData, parseRSS, formatter)

  bbcFeeds.foreach: (url, path) =>
    val content = rssProcessed(url) 
    content.fold(println(s"Failed to process feed $url"))(saveToFile(path, _))