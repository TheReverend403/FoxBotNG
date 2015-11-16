FoxBotNG
========

[![Build Status](https://img.shields.io/jenkins/s/https/ci.notoriousdev.com/FoxBotNG.svg?style=flat-square)](https://ci.notoriousdev.com/job/FoxBotNG/)
[![Tests](https://img.shields.io/jenkins/t/https/ci.notoriousdev.com/FoxBotNG.svg?style=flat-square)](https://ci.notoriousdev.com/job/FoxBotNG/testngreports/)
[![License](https://img.shields.io/badge/license-GPLv3-blue.svg?style=flat-square)](https://www.gnu.org/licenses/gpl-3.0.en.html)

A complete, ground up rewrite of [FoxBot](https://github.com/FoxDev/FoxBot), now with Gradle and unit tests.

# Usage

* Download or compile the bot. Downloads can be found at https://ci.notoriousdev.com/job/FoxBotNG/
* A default config will be saved in either $XDG_CONFIG_DIR/foxbot/ or ~/.config/foxbot/. Edit this file to configure your bot.
* java -jar FoxBotNG.jar

Note: Your jar may be named differently depending on whether you compiled from source or downloaded a binary. Generally, source builds are found in build/libs/.