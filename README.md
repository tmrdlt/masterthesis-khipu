# Khipu

Khipu is a workflow management tool developed by Timo Erdelt for his _Masters Thesis_ @ Institute for Informatics, 
Ludwig-Maximilian University of Munich. Its backend is written in the Scala Programming Language. Its frontend can be found [here](https://github.com/tmrdlt/masterthesis-khipu-frontend).

## Requirements
- At least Java 8 (e.g. AdoptOpenJDK8: `brew install adoptopenjdk8`)
- scala (`brew install scala`)
- sbt (`brew install sbt`)
- docker (`brew install docker`)
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
