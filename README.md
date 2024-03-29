# Khipu

Khipu is a Kanban board-based task management tool developed by Timo Erdelt for his __Masters Thesis__ at the Institute for Informatics, Ludwig-Maximilian University of Munich. This repository contains the backend written in the Scala Programming Language. 

- The frontend can be found [here](https://github.com/tmrdlt/masterthesis-khipu-frontend).

## API Endpoints
See [Endpoints.md](Endpoints.md)

## Requirements
_Installation instructions for macOS_
- At least Java 11 (e.g. temurin):
    - `brew tap homebrew/cask-versions`
    - `install --cask temurin11`
- scala (`brew install scala`)
- sbt (`brew install sbt`)
- docker (`brew install --cask docker`)
- python 3.x.x
- alembic (`pip3 install alembic`)


## Development
Run inside `/masterthesis-khipu`
- Start PostgreSQL database
  ```
  docker-compose up
  ```
- Run database migration:
  ```
  alembic upgrade head
  ```
- Compile and run project:
  ```
  sbt run
  ```

## Built with
- [Akka HTTP](https://doc.akka.io/docs/akka-http/current/index.html)
- [Akka Actors Classic](https://doc.akka.io/docs/akka/current/index-classic.html)
- [Akka Streams](https://doc.akka.io/docs/akka/current/stream/index.html)
- [Slick](https://scala-slick.org/)
- [slick-pg](https://github.com/tminglei/slick-pg)
- [OptaPlanner](https://www.optaplanner.org/)
- [scala-csv](https://github.com/tototoshi/scala-csv)
- [ScalaTest](https://www.scalatest.org/)
