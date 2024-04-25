# it4all

it4all is a web correction framework for

- Python
- Web Programming (Html, CSS, JavaScript, Flask)
- XML
- SQL
- Uml Class Diagrams and Activity Diagrams
- Nary Numbers, Boolean Algebra
- Regular Expressions

[![pipeline status](https://gitlab2.informatik.uni-wuerzburg.de/bje40dc/it4all/badges/main/pipeline.svg)](https://gitlab2.informatik.uni-wuerzburg.de/bje40dc/it4all/-/commits/main)

## Development

### Prerequisites

To develop `it4all`, you need to have installed the following programs (names of packages in Ubuntu in brackets):

1. Git (`git`)
2. (Open-)JDK 11 (`default-jdk` or `openjdk-11-jdk`)
3. NodeJS with NPM (`nodejs`, `npm`)
5. Docker (`docker.io`) (Do not forget to add yourself to group "docker" on Linux with `sudo usermod -aG docker $(whoami)` and log yourself out and in again)
6. Docker-compose (`docker-compose`)
8. [sbt](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html)

```bash
sudo apt install git openjdk-11-jdk nodejs npm docker.io docker-compose
```

### First start

```bash
# Clone the repository on the [GitLab](https://gitlab2.informatik.uni-wuerzburg.de/bje40dc/it4all.git) of the institute of computer science of the university of Wuerzburg
git clone https://gitlab2.informatik.uni-wuerzburg.de/bje40dc/it4all.git

cd it4all

# Create a folder for solutions needed by docker
mkdir -p data/web/solutions

# Set up all containers with docker-compose
docker-compose up -d

# Start the server with
sbt run

# install frontend dependencies
cd react-ui
npm i

# start frontent
npm run start
```

### Following starts

Since the containers are all built and started, you can just restart the server with

`sbt run`

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
* Docker with docker-compose (`docker.io`, `docker-compose`)

### Running

* Create docker containers:

  `docker-compose up -d`

* Create solution dir or reset ownership (recursively) on `data/web`

* Perform first start with db init

  `bin/it4all -Dplay.http.secret.key="<random string>" -Dplay.evolutions.db.default.autoApply=true`

* Subsequent starts without db update:

  `bin/it4all -Dplay.http.secret.key="<random string>"`
