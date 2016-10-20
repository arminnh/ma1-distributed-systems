# HOW TO RUN:

[Server]

ant init
ant compile
ant registry.start
ant run.serveronly 
(...)
ant registry.stop

[Client]

ant init
ant compile
ant -Dargs='server-ip-here' run.clientonly
