package de.tmrdlt.utils

import com.github.tototoshi.csv.{CSVWriter, DefaultCSVFormat}
import de.tmrdlt.database.event.Event
import de.tmrdlt.database.workflowlist.WorkflowList

import java.io.File
import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object CsvUtil {

  def writeWorkflowListsToCsv(boardName: String, workflowLists: Seq[WorkflowList]): Future[File] = {
    val header = Seq(classOf[WorkflowList].getDeclaredFields.map(_.getName).toSeq)
    val data = workflowLists.map(wl =>
      Seq(
        wl.id.toString,
        wl.apiId,
        wl.title,
        wl.description.getOrElse(""),
        wl.parentId.getOrElse("").toString,
        wl.position.toString,
        wl.listType.toString,
        wl.state.getOrElse("").toString,
        wl.dataSource.toString,
        wl.useCase.getOrElse("").toString,
        wl.createdAt.toString,
        wl.updatedAt.toString
      )
    )
    writeCSV("workflow_lists_" ++ boardName, header ++ data)
  }


  def writeEventsToCsv(boardName: String, actions: Seq[Event]): Future[File] = {
    val header = Seq(classOf[Event].getDeclaredFields.map(_.getName).toSeq)
    val data = actions.map(a =>
      Seq(
        a.id.toString,
        a.apiId,
        a.eventType,
        a.workflowListApiId,
        a.parentApiId.getOrElse(""),
        a.oldParentApiId.getOrElse(""),
        a.newParentApiId.getOrElse(""),
        a.userApiId,
        a.createdAt.toString,
        a.dataSource.toString,
      )
    )
    writeCSV("events_" ++ boardName, header ++ data)
  }

  def writeCSV(filePrefix: String = "data",
               data: Seq[Seq[String]],
               delimiterChar: Char = ';',
               encoding: String = "UTF-8"): Future[File] = {

    val filename = filePrefix + "_" + LocalDateTime.now().toString + ".csv"
    val file: File = new File("notebooks/data/" + filename)
    file.createNewFile()

    implicit object MyFormat extends DefaultCSVFormat {
      override val delimiter: Char = delimiterChar
    }

    Future {
      val writer = CSVWriter.open(file, append = false, encoding)
      writer.writeAll(data)
      writer.close()
      file
    }
  }

}
