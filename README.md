# Whitelist Synchronizer

The purpose of this plugin is to provide the possibility of synchronizing 
the Whitelist of a Minecraft server with an external repository, without 
having to open extra ports or use RCON.

Tested on **1.16.4**.

Setting up
----------

The configuration of this plugin is very simple. You only have to set the 
address of the server where the requests will be made and the synchronization 
period.

``` YAML
#Sync period in seconds
sync-period: 120

# Remote server address
server-address: 'http://localhost'

# Notifies all server members that someone has been added to the whitelist
broadcast-added-entries: false

# Notifies all server members that someone has been removed from the whitelist
broadcast-removed-entries: false
```

Expected server response
------------------------

The response from the server must be a standard Minecraft whitelist, in JSON format, as follows.

``` JSON
[{
    "uuid": "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
    "name": "User1"
}, {
    "uuid": "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX",
    "name": "User2"
}]
```

Compiling
---------

The project is written for Java 8 and our build process makes use of
[Gradle](http://gradle.org).

Dependencies are automatically handled by Gradle.