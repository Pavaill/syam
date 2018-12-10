FROM jboss/wildfly
 ADD target/webapp-tweet.war /opt/jboss/wildfly/standalone/deployments/
RUN /opt/jboss/wildfly/bin/add-user.sh admin Admin#70365 --silent

ADD standalone.xml /opt/jboss/wildfly/standalone/configuration/
#install mysql in wildfly
RUN mkdir -p /opt/jboss/wildfly/modules/system/layers/base/com/mysql/driver/main
RUN cd /opt/jboss/wildfly/modules/system/layers/base/com/mysql/driver/main
ADD mysql-connector-java-5.1.39.jar /opt/jboss/wildfly/modules/system/layers/base/com/mysql/driver/main/
ADD module.xml /opt/jboss/wildfly/modules/system/layers/base/com/mysql/driver/main/


 USER root

 # downloaded form https://downloads.jboss.org/keycloak/4.6.0.Final/adapters/keycloak-oidc/keycloak-wildfly-adapter-dist-4.6.0.Final.zip

 ADD keycloak-wildfly-adapter-dist-4.6.0.Final.zip /opt/jboss/wildfly

 RUN cd /opt/jboss/wildfly \

     && chown jboss:root keycloak-wildfly-adapter-dist-4.6.0.Final.zip \

     && chmod 600 keycloak-wildfly-adapter-dist-4.6.0.Final.zip \

     && unzip keycloak-wildfly-adapter-dist-4.6.0.Final.zip

 #WORKDIR /opt/jboss/wildfly/bin

 #RUN ./jboss-cli.sh --file=adapter-install-offline.cli


CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]

