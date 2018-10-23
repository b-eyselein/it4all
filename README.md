# it4all

it4all is a web correction framework for

- Web Programming (Html, CSS, JavaScript)
- XML
- SQL
- Different Programming Languages (Python3, Java, ...)
- Uml Class Diagrams and Activity Diagrams
- Spreadsheets [MS Excel, Libre-/Openoffice Calc]
- Nary Numbers, Boolean Algebra

[![build status](https://gitlab2.informatik.uni-wuerzburg.de/bje40dc/it4all/badges/master/build.svg)](https://gitlab2.informatik.uni-wuerzburg.de/bje40dc/it4all/commits/master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/2941021ee993484db0cab405aa03b209)](https://www.codacy.com/app/it4all/it4all?utm_source=gitlab2.informatik.uni-wuerzburg.de&amp;utm_medium=referral&amp;utm_content=bje40dc/it4all&amp;utm_campaign=Badge_Grade)

## Development

### Prerequisites
To develop it4all, you need to have installed the following programs (names of packages in Ubuntu in brackets):

1. Git (`git`)
2. Docker (`docker.io`) (Do not forget to add yourself to group "docker" on Linux with
   `sudo usermod -aG docker $(whoami)` and log yourself out and in again)
3. Docker-compose (`docker-compose`)

### First start

* Clone the repository on the [GitLab](https://gitlab2.informatik.uni-wuerzburg.de/bje40dc/it4all.git) of the institute of computer science of the university of Wuerzburg and change folders:

  `$ git clone --recurse-submodules https://gitlab2.informatik.uni-wuerzburg.de/bje40dc/it4all.git`

  `$ cd it4all`

* Set up all containers with docker-compose:

  `$ docker-compose up -d`

* Change ownership of all folders in `data/` (it gets created with ownership `root:root`):

  `$ sudo chown -R $(whoami):$(whoami) data/`

* Start the server with

  `$ sbt run`


### Following starts

Since the containers are all built and started, you can just restart the server with

`$ sbt run`

### Common Commands for the sbt/Play Framework - Console

`reload` - Reload the complete Configuration of the Project (needed for changes in `conf/application.conf` or `build.sbt`)

`compile` - Compile all sources

`clean` - Delete generated files (e. g. `.class`)

`test` - Run JUnit-Tests of the Project

`run` - Start Application in **_Development_** mode

### IntelliJ Idea IDE

Install `Scala Tools` Plugin for IntelliJ Idea and import the `build.sbt`-file