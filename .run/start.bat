@ECHO OFF
java -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -Xms1024M -Xmx1G -jar "../.spigot/spigot-1.11.2.jar"
PAUSE