# Khipu

Khipu is a Kanban board-based task management tool developed by Timo Erdelt for his __Masters Thesis__ at the Institute for Informatics, Ludwig-Maximilian University of Munich. This repository contains the backend, which is written in the Scala Programming Language. 

- The frontend can be found [here](https://github.com/tmrdlt/masterthesis-khipu-frontend).
- The tool was evaluated with a user study. The evaluation can be found [here](https://github.com/tmrdlt/masterthesis-evaluation).

## Requirements
- At least Java 11 (e.g. temurin):
    - `brew tap homebrew/cask-versions`
    - `install --cask temurin11`)
- scala (`brew install scala`)
- sbt (`brew install sbt`)
- docker (`brew install --cask docker`)
- python 3.x.x
- alembic (`pip3 install alembic`)

## Development
Run inside `/masterthesis-khipu`
- Run PostgreSQL DB & Jupyter Notebook:
  ```
  docker-compose up
  ```
- Run db migration:
  ```
  alembic upgrade head
  ```
- Compile and run project:
  ```
  sbt run
  ```
- Create a fat `.jar` to use inside Jupyter Notebook:
  ```
  sbt assembly
  ```
- Access Jupyter Notebook at http://localhost:8888. The link with token is also printed by `docker-compose up`.

## Libraries used
- [Akka HTTP](https://doc.akka.io/docs/akka-http/current/index.html)
- [Akka Actors Classic](https://doc.akka.io/docs/akka/current/index-classic.html)
- [Akka Streams](https://doc.akka.io/docs/akka/current/stream/index.html)
- [Slick](https://scala-slick.org/)
- [slick-pg](https://github.com/tminglei/slick-pg)
- [OptaPlanner](https://www.optaplanner.org/)
- [scala-csv](https://github.com/tototoshi/scala-csv)
- [ScalaTest](https://www.scalatest.org/)

## API Endpoints
See [Endpoints.md](Endpoints.md)
