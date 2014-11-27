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
    format.parse(new StringReader(str)).iterator.next.iterator.asScala

  def write(row: Iterator[String], format: CSVFormat) = format.format(row.toSeq:_*)

  case class ParserPrimitive(parseItem: String => AnyRef) extends DefaultReporter {
    override def getSyntax = reporterSyntax(Array(StringType | RepeatableType), ListType, 1)
    override def report(args: Array[Argument], context: Context) =
      LogoList.fromIterator(parse(args(0).getString, format(args.lift(1) map (_.getString))) map parseItem)
  }

  case class WriterPrimitive(dump: AnyRef => String) extends DefaultReporter {
    override def getSyntax = reporterSyntax(Array(ListType, StringType | RepeatableType), StringType, 1)
    override def report(args: Array[Argument], context: Context) =
      write(args(0).getList.scalaIterator map dump, format(args.lift(1) map (_.getString)))
  }

  override def load(primManager: PrimitiveManager) = {
    val add = primManager.addPrimitive _
    add("to-strings", ParserPrimitive(identity))
    add("to-strings-and-numbers", ParserPrimitive { entry => NumberParser.parse(entry).right getOrElse entry })
    add("from-list-to-string", WriterPrimitive(Dump.logoObject _))
    add("from-list-to-readable-string", WriterPrimitive(Dump.logoObject(_, readable = true, exporting = false)))
  }
}
