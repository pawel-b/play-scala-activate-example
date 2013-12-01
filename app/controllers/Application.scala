package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import views._
import models._
import org.joda.time._
import net.fwbrasil.activate.ActivateContext
import net.fwbrasil.activate.play.EntityForm
import net.fwbrasil.activate.play.EntityForm._
import models.tasksContext._
import play.i18n.Messages

/**
 * Manage a database
 */
object Application extends Controller {

    /**
     * This result directly redirect to the application home.
     */
    val Home = Redirect(routes.Application.list(0, "", ""))

    /**
     * Describe the object form (used in both edit and create screens).
     */
    val taskForm =
        EntityForm[Task](
            _.name -> nonEmptyText,
            _.taskGroup -> entity[TaskGroup],
            _.estimatedFinishDate -> optional(jodaDate),
            _.actualFinishDate -> optional(jodaDate),
            _.employee -> optional(entity[Employee]))

    // -- Actions

    /**
     * Handle default path requests, redirect to tasks list
     */
    def index = Action { Home }

    /**
     * Display the paginated list of objects.
     *
     * @param page Current page number (starts from 0)
     * @param orderBy Column to be sorted
     * @param filter Filter applied on task names
     */
    def list(page: Int, orderByString: String, filter: String) = Action { implicit request =>
        transactional {
            Ok(html.list(
                Task.list(page = page, orderByString = orderByString, filter = ("*" + filter + "*")),
                orderByString, filter))
        }
    }
    //
    /**
     * Display the 'edit form' of a existing object.
     *
     * @param id Id of the task to edit
     */
    def edit(id: String) = Action {
        transactional {
            byId[Task](id).map { task =>
                Ok(html.editForm(id, taskForm.fillWith(task)))
            }
        }.getOrElse(NotFound)
    }

    /**
     * Handle the 'edit form' submission
     *
     * @param id Id of the task to edit
     */
    def update(id: String) = Action { implicit request =>
        transactional {
            taskForm.bindFromRequest.fold(
                formWithErrors => BadRequest(html.editForm(id, formWithErrors)),
                taskData => {
                    val task = taskData.updateEntity(id)
                    Home.flashing("success" -> Messages.get("tasks.updated", task.name))
                })
        }
    }

    /**
     * Display the 'new task form'.
     */
    def create = Action {
        transactional {
            Ok(html.createForm(taskForm))
        }
    }

    /**
     * Handle the 'new task form' submission.
     */
    def save = Action { implicit request =>
        transactional {
            taskForm.bindFromRequest.fold(
                formWithErrors => BadRequest(html.createForm(formWithErrors)),
                taskData => {
                    val task = taskData.createEntity
                    Home.flashing("success" -> Messages.get("tasks.created", task.name) )
                })
        }
    }

    /**
     * Handle task deletion.
     */
    def delete(id: String) = Action {
        transactional {
            byId[Task](id).get.delete
            Home.flashing("success" ->  Messages.get("tasks.deleted"))
        }
    }

}
