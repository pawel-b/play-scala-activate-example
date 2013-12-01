package models

import net.fwbrasil.activate.play.EntityForm
import scala.reflect.runtime.universe._
import tasksContext._
import net.fwbrasil.activate.statement.query._
import org.joda.time._
import org.joda.time.format._

class Employee(
  var name: String,
  var email: String)
  extends Entity

class TaskGroup(
  var name: String)
  extends Entity

class Task(
  var taskGroup: TaskGroup,
  var name: String,
  var estimatedFinishDate: Option[DateTime],
  var actualFinishDate: Option[DateTime],
  var employee: Option[Employee])
  extends Entity

/**
 * Helper for pagination.
 */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

object Task {

  def list(page: Int = 0, pageSize: Int = 10, orderByString: String = "n", filter: String = "*"): Page[(Task, TaskGroup, Option[Employee])] = transactional {
    val isAsc = orderByString.toLowerCase == orderByString
    println("Order by: " + orderByString + ", isAsc: " + isAsc)
    val q = { (c: Task) =>
      where(toUpperCase(c.name) like filter.toUpperCase) select (c) orderBy {
        orderByString match {
          case "n" => c.name
          case "e" => c.estimatedFinishDate
          case "a" => c.actualFinishDate
          case "m" => c.employee.map(_.name)
          case "t" => c.taskGroup.name
          case "N" => c.name desc
          case "E" => c.estimatedFinishDate desc
          case "A" => c.actualFinishDate desc
          case "M" => c.employee.map(_.name) desc
          case "T" => c.taskGroup.name desc
          case _ => c.name
        }

      }
    }
    println(q)
    val pagination = paginatedQuery(q)
    val navigator = pagination.navigator(pageSize)
    if (navigator.numberOfResults > 0) {
      val p = navigator.page(page)
      Page(p.map(c => (c, c.taskGroup, c.employee)), page, page * pageSize, navigator.numberOfResults)
    } else
      Page(Nil, 0, 0, 0)
  }
}

object Employee {
  def options: Seq[(String, String)] = transactional {
    (
      query {
        (
          (employee: Employee) =>
            where(employee isNotNull) select (employee.id, employee.name) orderBy (employee.name))
      })
  }

}

object TaskGroup {
  def options: Seq[(String, String)] = transactional {
    (
      query {
        (
          (taskGroup: TaskGroup) =>
            where(taskGroup isNotNull) select (taskGroup.id, taskGroup.name) orderBy (taskGroup.name))
      })
  }

}

