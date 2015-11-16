FoxBotNG
========

[![Build Status](https://img.shields.io/jenkins/s/https/ci.notoriousdev.com/FoxBotNG.svg?style=flat-square)](https://ci.notoriousdev.com/job/FoxBotNG/)
[![Tests](https://img.shields.io/jenkins/t/https/ci.notoriousdev.com/FoxBotNG.svg?style=flat-square)](https://ci.notoriousdev.com/job/FoxBotNG/testngreports/)
[![License](https://img.shields.io/badge/license-GPLv3-blue.svg?style=flat-square)](https://www.gnu.org/licenses/gpl-3.0.en.html)

A complete, ground up rewrite of [FoxBot](https://github.com/FoxDev/FoxBot), now with Gradle and unit tests.

# Usage

* Download or compile the bot. Downloads can be found at https://ci.notoriousdev.com/job/FoxBotNG/
* Copy foxbot.conf.dist to foxbot.conf
* Edit foxbot.conf as you see fit.
* java -jar FoxBotNG.jar

Note: Your jar may be named differently depending on whether you compiled from source or downloaded a binary. Generally, source builds are found in build/libs/.

If you have no foxbot.conf.dist (almost definitely the case if you downloaded a binary), save a copy from https://raw.githubusercontent.com/FoxDev/FoxBotNG/master/foxbot.conf.dist to foxbot.conf.

The bot looks for it's config in the same folder as the jar, so make sure they're together.

Automatic generation of a default config may be worked on in future.