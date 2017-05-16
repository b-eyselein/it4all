# it4all

it4all is a correction framework for

- Html
- CSS
- JavaScript
- SQL (TODO)
- PHP (TODO)

- Spreadsheet

  - MS Excel
  - Libre-, Openoffice Calc

- Mindmap (MindManager)

## Development and Testing

Clone the repository on the [GitLab](https://gitlab2.informatik.uni-wuerzburg.de/bje40dc/it4all.git) of the institute of computer science of the university of Wuerzburg

You can start the Play Framework Console with the command `activator`

### Common Commands for Play Framework Console

`reload` - Reload the complete Configuration of the Project

`compile` - Compile all sources

`test` - Run JUnit-Tests of the Project

`projects` - Show all (Sub-)Projects

`project <proj>` - Change current Project to `<proj>`

`run` - Start Application in **_Development_** mode

`eclipse with-source=true skip-parents=false` - Generate the files needed for Eclipse IDE (with sources respectively with respect to parent Projects)

### Prerequisites

it4all needs a MySQL-Database called `it4all`. Username and Password have to be stored in the `conf/application.conf` - file. This project uses the **_evolutions_** of the Play Framework which generates SQL-Scripts after reloading the application in the browser.

The correction for Html also saves files to your local hard drive. The paths to the folders can be found in the `conf/application.conf` Configuration file under `rootDirLinux` for linux- and unix-like systems respectively rootDirWin for Windows systems. Under Linux the folder has to be owned by the user starting the program (in development mode too).

### Eclipse IDE

Start the Play Framework Console with `activator`. You can generate the files needed for the Eclipse IDE with the command `eclipse with-source=true skip-parents=false`. This also loads the sources and compiles all projects (including subprojects).

You can import the Eclipse Project with `Import` `-->` `Existing Projects into Workspace` (or `Import` `-->` `Git` `-->` `Projects from Git` `-->` `Existing local repository`). Make sure to select `Search for nested projects` to include all subprojects.
