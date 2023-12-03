ARG IMAGE=intersystemsdc/iris-community
FROM $IMAGE

WORKDIR /opt/irisapp

COPY . .

RUN iris start $ISC_PACKAGE_INSTANCENAME \
    && iris session $ISC_PACKAGE_INSTANCENAME < iris.script \
    && iris stop $ISC_PACKAGE_INSTANCENAME quietly
