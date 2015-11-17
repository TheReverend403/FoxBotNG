FoxBotNG
========

[![Build Status](https://img.shields.io/jenkins/s/https/ci.notoriousdev.com/FoxBotNG.svg?style=flat-square)](https://ci.notoriousdev.com/job/FoxBotNG/)
[![Tests](https://img.shields.io/jenkins/t/https/ci.notoriousdev.com/FoxBotNG.svg?style=flat-square)](https://ci.notoriousdev.com/job/FoxBotNG/lastSuccessfulBuild/testReport/)
[![License](https://img.shields.io/badge/license-GPLv3-blue.svg?style=flat-square)](https://www.gnu.org/licenses/gpl-3.0.en.html)

A complete, ground up rewrite of [FoxBot](https://github.com/FoxDev/FoxBot), now with Gradle and unit tests.

# Usage

* Download or compile the bot. Downloads can be found at https://ci.notoriousdev.com/job/FoxBotNG/
* A default config will be saved in either $XDG_CONFIG_HOME/foxbot/ or ~/.config/foxbot/. Edit this file to configure your bot.
* java -jar FoxBotNG.jar

Note: Your jar may be named differently depending on whether you compiled from source or downloaded a binary. Generally, source builds are found in build/libs/.

# Untrusted SSL Certificates

To use untrusted or self-signed certificates with this bot, you must add the certificate to Java's keystore. There is no config option that will allow you to accept any certificate as this is incredibly insecure.

To retrieve a certificate and add it to your keystore, use the following commands.

```bash
HOST=irc.example.com # change this
PORT=6697 # also maybe change this, but usually not
openssl s_client -connect $HOST:$PORT -showcerts 2>&1 < /dev/null | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' | sed -ne '1,/-END CERTIFICATE-/p' > irc-cert.pem
sudo keytool -import -alias dev-server -keystore $JAVA_HOME/jre/lib/security/cacerts -file irc-cert.pem
# You will be asked for a keystore password here. It is 'changeit' by default.
```