# Khipu

Khipu is a workflow management tool developed by Timo Erdelt for his _Masters Thesis_ @ Institute for Informatics, 
Ludwig-Maximilian University of Munich. Its backend is written in the Scala Programming Language.

## Requirements
- At least Java 8 (e.g. AdoptOpenJDK8: `brew install adoptopenjdk8`)
- scala (`brew install scala`)
- sbt (`brew install sbt`)
- docker (`brew install docker`)
- python 3.x.x
- alembic (`pip install alembic`)

## Development
Run inside `/masterthesis-khipu`
- Setup db:
  ```
  docker-compose up -d
  ```
- Run db migration:
  ```
  alembic upgrade head
  ```
- Compile and run project:
  ```
  sbt run
  ```
