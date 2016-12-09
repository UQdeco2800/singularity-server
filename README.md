#DECO2800 2016 Singularity Server
## Quick Background
This server was developed during the course of Semster 2, 2016 at UQ in the course DECO2800 (Design Computing Studio II). This course puts students into projects with 50-60 other people. Each project should interface with a central server. In this year, that server is this one. Students were allowed to vote to opensource the project, and if students didn't want to publish their work, then their commits were removed. As a result, the server may not currently compile. 

## Original Contributors
The students who worked on this version that has been published are:
- timmyhadwen (tutor)
- dion-loetscher (tutor)
- Nguyen Dang Khoi Truong
- email4nickp
- liamdm
- jnfry
- MaxwellBo
- gregarious96
- 1Jamster1 
- lutzenburg
- RyanCarrier
- tbric123
- charliegrc
- dgormly
- pan1501
- jhess-osum
- alex-mclean
- slamon34
- Sang Ik Park (wqdoqw)
- tomquirk

# The Server
The **Singularity Server** is the point where all the projects and common utilities and tools come together. The central point of communication for the games, this server will handle any and all requests that come its way (whether the response is what you expect is another question). 

The Singularity Server is built upon the [Dropwizard Framework v0.9.3](http://www.dropwizard.io/). Dropwizard compiles a few different tools to allow for the simple and rapid development of a RESTful API. Dropwizard also provides JDBI for easy integration with a database. The database used in this project is [Apache Derby](https://db.apache.org/derby/). 

Finally, the build tool used in this project is [Gradle](http://gradle.org/). It allows for the easy dependency management, building, testing and running of the system with additional helper tools to manage inter-IDE operability or database management. In addition to this, a database migration tool has also been added to the build script to manage database schema migrations. This is managed by [FlywayDB](https://flywaydb.org/) which is used through Gradle.

There has recently been an addition to the server to allow real time services to be used. In this course, the library that has been used to make this happen is called [KryoNet](https://github.com/EsotericSoftware/kryonet). It allows users to to start a server which receives connections through which it can send and receive Objects (Java objects). It can then process this object and respond, or forward the object or send new objects to other users. This can then be used to implement a push notification system. Currently it is being used to implement instant messaging. Have a look at the classes to try and understand how it is being done.

## Setting Up and Running ##
In order to have the project prepared for running the following is required:

### Database Setup ###
In the root project directory, run `./gradlew flywayMigrate`. This will cause flyway to start and attempt to go through the SQL statements to build up the Derby Database. If, you ever encounter issues with the database, Delete the `singularityDB` folder in the `server` directory. Then rerun `./gradlew flywayMigrate`. 

#### Running the Server ####
Running the server is extremely simple. In the root directory, simply run `./gradlew run`. This will start the server up on port 8080 and the messaging service on port 8888.

You can then configure the clients used in your game to then use `localhost` for the clients which is defined in the `ServerConstants` class in the `common` sub-project.

*If you have followed the steps to install Singularity above and are still getting dependency errors, check that the version number is updated as per the Singularity Versioning section below *

## Project Structure and Making Changes ##
The Singularity Server is currently split into 3 projects: Common, Clients, Server. 

Common is used to store any common Data Structures or Utilities for any and all the projects to use. It is important to note that both the Server and the Clients projects depend on Common. 

The server has what are known as resources and DAOs. The DAOs, known as Data Access Objects, are used to interface with the database. Through the DAOs, resources can make changes to the stored data and make decisions. Resources are endpoints which can be connected to through HTTP. As an example, if the server is running on `http://localhost:8080/`, one resource could be `http://localhost:8080/user/{id-num-here}` and would respond to the HTTP `GET` request to retrieve information about the user. 

If a Database schema change needs to be made, then the appropriate way to do this would be to: 
1. Add (not change) an SQL file of the format `VX__Title_of_change.sql` where `X` is replaced with the latest version number. This SQL file should describe any changes to the _lastest_ schema. These changes include, deletion of tables, addition of columns, etc. 
2. Run `./gradlew flywayMigrate` again from the root project directory.
3. Update all the affected DAOs in the server project.

Finally, the client is used to standardise the clients interaction with the server. All clients will inherit from one main generic client. And will build additional required functionality from this. This means that each external project will have its own client to interact with the server with. If there are changes to the response the server gives, the clients will have to be updated respectively. 

## Singularity Versioning ##
Singularity uses Gradle versioning to stay up to date with your projects. Your projects depend on Singularity, and as such, if you update the "dependency" which is Singularity, it will also update in your project. This is not good if you are making changes to Singularity that break your existing projects code. To solve these issues, Singularity has a version number defined in the root `build.gradle` file.

```
allprojects {
    group = "uq.deco2800.singularity"
    version = "0.1-SNAPSHOT"
}
```

In the *dependencies* section of your project's root `build.gradle` file, you will also see the Singularity version. Note, this is *not* the dependency in the "allprojects" section!

```
`   dependencies {
        // DECO2800 dependencies
        compile (group: 'uq.deco2800.singularity', name: 'common', version: '0.1-SNAPSHOT', changing: true)
        compile (group: 'uq.deco2800.singularity', name: 'clients', version: '0.1-SNAPSHOT', changing: true)
```

If your local project targets version `0.1`, then it will connect to artifactory, and automatically download the latest copy of `0.1` before building on Jenkins. This means if you want to develop or change features that have not been implemented in your project, you can push the changed Singularity as version `0.2`. Then if the project is still `0.1`, it will not update to the new version of Singularity, and Jenkins will still pass. Then you can change your client version number up as you update.

This allows you to change Singularity extensively, and only update your when needed - allowing you to push often. None the less, this can be a hassle for other projects, so only change the version number if neccescary, and let others know about this change in a ticket or on Slack!


## Final Note ##
Finally, before pushing any changes, Please run _**ALL tests**_ before pushing. 


