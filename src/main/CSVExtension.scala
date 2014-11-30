package org.nlogo.extensions.csv

import scala.collection.JavaConverters._

import org.nlogo.api._
import org.nlogo.api.Syntax._

import org.apache.commons.csv._
import java.io.StringReader

class CSVExtension extends DefaultClassManager {
  def format(delimiter: Option[String]) =
    (delimiter foldLeft CSVFormat.DEFAULT)(_ withDelimiter _(0))

  def parse(str: String, format: CSVFormat) =
    format.parse(new StringReader(str)).iterator.asScala map (_.iterator.asScala)

  def write(row: Iterator[String], format: CSVFormat) = format.format(row.toSeq:_*)

  def numberOrString(entry: String): AnyRef = NumberParser.parse(entry).right getOrElse entry

  def liftParser[T](parseItem: T => AnyRef)(row: Iterator[T]): LogoList =
    LogoList.fromIterator(row map parseItem)

  case class ParserPrimitive(process: Iterator[Iterator[String]] => LogoList) extends DefaultReporter {
    override def getSyntax = reporterSyntax(Array(StringType | RepeatableType), ListType, 1)
    override def report(args: Array[Argument], context: Context) =
      process(parse(args(0).getString, format(args.lift(1) map (_.getString))))
  }

  def rowParser(parseItem: String => AnyRef) = ParserPrimitive(rows => liftParser(parseItem)(rows.next))
  def fullParser(parseItem: String => AnyRef) = ParserPrimitive(liftParser(liftParser(parseItem)))

  case class WriterPrimitive(dump: AnyRef => String) extends DefaultReporter {
    override def getSyntax = reporterSyntax(Array(ListType, StringType | RepeatableType), StringType, 1)
    override def report(args: Array[Argument], context: Context) =
      write(args(0).getList.scalaIterator map dump, format(args.lift(1) map (_.getString)))
  }

  override def load(primManager: PrimitiveManager) = {
    val add = primManager.addPrimitive _
    add("csv-row-to-strings", rowParser(identity))
    add("csv-row-to-strings-and-numbers", rowParser(numberOrString))
    add("csv-to-strings", fullParser(identity))
    add("csv-to-strings-and-numbers", fullParser(numberOrString))
    add("list-to-csv-row", WriterPrimitive(Dump.logoObject _))
    add("list-to-readable-csv-row", WriterPrimitive(Dump.logoObject(_, readable = true, exporting = false)))
  }
}
