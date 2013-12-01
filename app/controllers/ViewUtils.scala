package controllers

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object ViewUtils {
  def formatDateTime(date: Option[DateTime], pattern: String = "yyyy-MM-dd") = {
    date match {
      case Some(x) => DateTimeFormat.forPattern(pattern).print(x)
      case None => ""
    }
  }

  def reverseCase(aString: String) = {
    if (aString.toLowerCase == aString) aString.toUpperCase
    else aString.toLowerCase
  }

  def link(newPage: Int, currentOrderBy: String, currentFilter: String, newOrderBy: Option[String] = None) = {
    routes.Application.list(newPage, newOrderBy.map { orderBy =>
      if (orderBy == currentOrderBy) ViewUtils.reverseCase(orderBy) else orderBy
    }.getOrElse(currentOrderBy), currentFilter)
  }

}