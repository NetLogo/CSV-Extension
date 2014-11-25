package org.nlogo.extensions.csv

import scala.collection.JavaConverters._

import org.nlogo.api._
import org.nlogo.api.Syntax._

import org.apache.commons.csv._
import java.io.StringReader

class CSVExtension extends DefaultClassManager {
  val format = CSVFormat.DEFAULT

  object ParseRow extends DefaultReporter {
    override def getSyntax = reporterSyntax(Array(StringType), ListType)
    override def report(args: Array[Argument], context: Context) = {
      val row = args(0).getString
      val record = format.parse(new StringReader(row)).iterator.next
      LogoList.fromIterator(record.iterator.asScala)
    }
  }
  override def load(primManager: PrimitiveManager) = {
    val add = primManager.addPrimitive _
    add("parse-row", ParseRow)
  }
}
