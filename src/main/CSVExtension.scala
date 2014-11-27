package org.nlogo.extensions.csv

import scala.collection.JavaConverters._

import org.nlogo.api._
import org.nlogo.api.Syntax._

import org.apache.commons.csv._
import java.io.StringReader

class CSVExtension extends DefaultClassManager {
  val format = CSVFormat.DEFAULT

  trait ParserPrimitive extends DefaultReporter {
    override def getSyntax = reporterSyntax(Array(StringType | RepeatableType), ListType, 1)
    def format(args: Array[Argument]) =
      (args.lift(1) foldLeft CSVFormat.DEFAULT)(_ withDelimiter _.getString(0))

    def parse(args: Array[Argument]) = {
      val parsed = format(args).parse(new StringReader(args(0).getString))
      parsed.iterator.next.iterator.asScala
    }
  }

  object ToStrings extends ParserPrimitive {
    override def report(args: Array[Argument], context: Context) =
      LogoList.fromIterator(parse(args))
  }

  object ToStringsAndNumbers extends ParserPrimitive {
    override def report(args: Array[Argument], context: Context) = {
      val parsedRecord: Iterator[AnyRef] = parse(args) map { entry =>
        NumberParser.parse(entry).right getOrElse entry
      }
      LogoList.fromIterator(parsedRecord)
    }
  }

  override def load(primManager: PrimitiveManager) = {
    val add = primManager.addPrimitive _
    add("to-strings", ToStrings)
    add("to-strings-and-numbers", ToStringsAndNumbers)
  }
}
