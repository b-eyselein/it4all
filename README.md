# it4allit4all is a correction framework for* Html* CSS* JavaScript* SQL (TODO)* PHP (TODO)* Spreadsheet	* MS Excel 	* Libre-, Openoffice Calc* Mindmap (MindManager)## Development and TestingClone the repository on the [GitLab-Site of the institute of computer science of the university of Wuerzburg](https://gitlab.informatik.uni-wuerzburg.de/bje40dc/it4all.git)You can start the Play Framework Console with the command `activator`### Common Commands for Play Framework Console`reload` - Reload the complete Configuration of the Project`compile` - Compile all sources`test` - Run JUnit-Tests of the Project`projects` - Show all (Sub-)Projects`project <proj>` - Change current Project to `<proj>``run` - Start Application in ***Development*** mode`eclipse (with-source=true) (skip-parents=false)` - Generate the files needed for Eclipse IDE (with sources respectively with respect to parent Projects)### Prerequisitesit4all needs a MySQL-Database called `it4all`. Username and Password have to be stored in the `app/conf/application.conf` - file. Skripts for generating the tables and inserting examples can be found in the `sql_skripts` - Folder.