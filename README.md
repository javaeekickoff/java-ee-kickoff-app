[java-ee-kickoff-app](http://javaeekickoff.omnifaces.org/) [![Build Status](https://travis-ci.org/javaeekickoff/java-ee-kickoff-app.svg?branch=master)](https://travis-ci.org/javaeekickoff/java-ee-kickoff-app)
===================

Basic project template to kickoff a new Java EE web application.

## Usage

Clone the branch matching your target environment into a new project. There are currently three branches available.

- [Java EE 8 with JSF 2.3](https://github.com/javaeekickoff/java-ee-kickoff-app/tree/javaee8-jsf23)
- [Java EE 7 with JSF 2.3](https://github.com/javaeekickoff/java-ee-kickoff-app/tree/javaee7-jsf23)
- [Java EE 7 with JSF 2.2](https://github.com/javaeekickoff/java-ee-kickoff-app/tree/javaee7-jsf22)

Note: For usage on JBoss EAP 7 alpha/beta or WildFly 10rc4 and below, JASPIC needs to be activated before the application can be deployed. See also http://arjan-tijms.omnifaces.org/2015/08/activating-jaspic-in-jboss-wildfly.html

## Database

The default app uses [H2](http://www.h2database.com) with `drop-and-create`. In other words, the default app uses an embedded database and is configured to drop and create all tables on startup. If you want to stop dropping all tables on startup, edit `javax.persistence.schema-generation.database.action` property of `/META-INF/persistence.xml` to `create`. If you want to change the database, install the desired JDBC driver in your target server and edit the JDBC driver configuration in `/META-INF/conf/datasource-settings.xml`. It's not necessary to manually create a data source in the target server as that's already done via `<data-source>` in `web.xml`.
