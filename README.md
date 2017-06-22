# it4all

it4all is a correction framework for

- Html, CSS, JavaScript
- SQL
- Different Programming Languages (Python3, JavaScript, ...)
- Uml Class Diagrams
- Spreadsheets
  - MS Excel
  - Libre-, Openoffice Calc
- Mindmap (MindManager)
- Nary Numbers, Boolean Algebra

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/1d60243593e347fc9ec6026bf8e7e465)](https://www.codacy.com/app/b.eyselein/it4all?utm_source=bje40dc@gitlab2.informatik.uni-wuerzburg.de&amp;utm_medium=referral&amp;utm_content=bje40dc/it4all&amp;utm_campaign=Badge_Grade)

## Versions

There are two versions of it4all on [GitLab](https:/gitlab2.informatik.uni-wuerzburg.de):
* [The development version](https://gitlab2.informatik.uni-wuerzburg.de/bje40dc/it4all.git)
* [The distribution version](https://gitlab2.informatik.uni-wuerzburg.de/it4all/dist.git)

This is the development version.

## Development

### Prerequisites
To develop it4all, you need to have installed the following programs (names of packages in Ubuntu in brackets):

1. Git (git)
2. Docker (docker.io)
3. Docker-compose (docker-compose)

### First start

Clone the repository on the [GitLab](https://gitlab2.informatik.uni-wuerzburg.de/bje40dc/it4all.git) of the institute of computer science of the university of Wuerzburg and change folders:

`$ git clone https://gitlab2.informatik.uni-wuerzburg.de/bje40dc/it4all.git`

`$ cd it4all`

Build the image defined in the file 'Dockerfile':

`$ docker build -t play --build-arg USERNAME=$(whoami) --build-arg DOCKER_GID=$(stat -c "%g" /var/run/docker.sock) .`

Set up all containers with docker-compose:

`$ docker-compose up -d`

Attach to the main it4all-Container:

`$ docker attach it4all_it4all_1`

Start the sbt/Play Framework - Console with

`$ sbt`

### Following starts

Since the containers are all built and started, you can just restart the main container with (the options `-ai` stand for attach and interactive)

`$ docker start -ai it4all_it4all_1`

### Common Commands for the sbt/Play Framework - Console

`reload` - Reload the complete Configuration of the Project

`compile` - Compile all sources

`test` - Run JUnit-Tests of the Project

`projects` - Show all (Sub-)Projects

`project <proj>` - Change current Project to `<proj>`

`run` - Start Application in **_Development_** mode

`eclipse` - Generate the files needed for Eclipse IDE (with sources respectively with respect to parent Projects)

### Eclipse IDE

You can generate the files needed for the Eclipse IDE in the sbt-Console with the command `eclipse`. This also loads the sources and compiles all projects (including subprojects).

You can import the Eclipse Project with `Import` `-->` `Existing Projects into Workspace`. Make sure to select `Search for nested projects` to include all subprojects.
