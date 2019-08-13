# it4all

it4all is a web correction framework for

- Web Programming (Html, CSS, JavaScript)
- XML
- SQL
- Different Programming Languages (Python3, Java, ...)
- Uml Class Diagrams and Activity Diagrams
- Nary Numbers, Boolean Algebra
- Regular Expressions

[![build status](https://gitlab2.informatik.uni-wuerzburg.de/bje40dc/it4all/badges/master/build.svg)](https://gitlab2.informatik.uni-wuerzburg.de/bje40dc/it4all/commits/master)

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/2941021ee993484db0cab405aa03b209)](https://www.codacy.com/app/it4all/it4all?utm_source=gitlab2.informatik.uni-wuerzburg.de&amp;utm_medium=referral&amp;utm_content=bje40dc/it4all&amp;utm_campaign=Badge_Grade)

## Development

### Prerequisites
To develop `it4all`, you need to have installed the following programs (names of packages in Ubuntu in brackets):

1. Git (`git`)
2. (Open-)JDK 11 (`default-jdk` or `openjdk-11-jdk`)
3. NodeJS (`nodejs` for compiling Typescript)
4. Docker (`docker.io`) (Do not forget to add yourself to group "docker" on Linux with
   `sudo usermod -aG docker $(whoami)` and log yourself out and in again)
5. Docker-compose (`docker-compose`)
6. MariaDB-Server (`mariadb-server`)
7. sbt (https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html)

```bash
sudo apt install git openjdk-11-jdk nodejs docker.io docker-compose mariadb-server
```

### MariaDB-Server

`it4all` expects a `MariaDB`-Server running on port 3306 with users given in `conf/application.conf`

### First start

* Clone the repository on the [GitLab](https://gitlab2.informatik.uni-wuerzburg.de/bje40dc/it4all.git) of the institute of computer science of the university of Wuerzburg and change folders:

  `$ git clone --recurse-submodules https://gitlab2.informatik.uni-wuerzburg.de/bje40dc/it4all.git`

  `$ cd it4all`

* Set up all containers with docker-compose:

  `$ docker-compose up -d`

* Change ownership of all folders in `data/` (it gets created with ownership `root:root` by docker-compose):

  `$ sudo chown -R $(whoami):$(whoami) data/`

* Start the server with

  `$ sbt run`

If you forgot to clone the submodules, you can do it with

`$ git submodule update --recursive`

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

## Deployment to production (Ubuntu Server)

### Prerequisites

* JRE (`default-jre`)
* MariaDB Server (`mariadb-server`)
* Docker with docker-compose (`docker.io`, `docker-compose`)

### Running

* Create docker containers:

  `docker-compose up -d`
  
* Create solution dir or reset ownership (recursively) on `data/web`

* Perform first start with db init
  
  `bin/it4all -Dplay.http.secret.key="<random string>" -Dplay.evolutions.db.default.autoApply=true`
  
* Subsequent starts without db update:

  `bin/it4all -Dplay.http.secret.key="<random string>"`
