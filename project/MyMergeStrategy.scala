import sbtassembly.MergeStrategy

import java.io.File

class MyMergeStrategy extends MergeStrategy{
  override def name: String = "Rename to application.conf"

  override def apply(tempDir: File, path: String, files: Seq[File]): Either[String, Seq[(File, String)]] = {
    Right(files.map(_ -> "application.conf"))
  }
}