Интерактивная Ииссиидиология
============================
[ii.ayfaar.org](http://ii.ayfaar.org)

Цели проекта
------------
Основная цель - представление информацци, поиск, структурирование, простраивание взаимосвязей между понятиями [ииссиидиологии](http://ru.science.wikia.com/wiki/%D0%98%D0%B8%D1%81%D1%81%D0%B8%D0%B8%D0%B4%D0%B8%D0%BE%D0%BB%D0%BE%D0%B3%D0%B8%D1%8F)

Косвенная цель - плацдарм для обученя современным техникам и подходам.

О проекте
---------

Это [single-page application](http://en.wikipedia.org/wiki/Single-page_application). Со стороны сервера Java SE, Spring, Hibernate. На клиенте JavaScript, HTML5, CSS3, визуальные компоненты и биндинг - [KendoUI](www.kendoui.com)

Основная задача проекта сформировать модель термин - описание из сплошного текста хранящегося по абзацам в [Item](https://github.com/enginer/ii/blob/master/src/main/java/org/ayfaar/app/model/Item.java). Также нужно искать по всему содержимому - [SearchController](https://github.com/enginer/ii/blob/master/src/main/java/org/ayfaar/app/controllers/SearchController.java).

Реализован експорт связок термин-описание в [mediawiki](http://www.mediawiki.org) (движок википедии) и импорт из [evernote](https://www.evernote.com). 

Проект бесплатно хостится на [OpenShift](https://www.openshift.com/) (Open Hybrid Cloud Application Platform by Red Hat)

С чего начать (Java)
====================

**Видео инструкция https://www.youtube.com/watch?v=mbwN4eaES78**

Устанавливаем:

1.	GIT http://msysgit.github.io
2.	IntelliJ IDEA http://www.jetbrains.com/idea/download/ Ultimate Edition
3.	Java version 1.7 (На сервере 1.7.0_55, OpenJDK Runtime Environment (rhel-2.4.7.1.el6_5-i386 u55-b13); OpenJDK Server VM (build 24.51-b03, mixed mode)
4.	Java SE Development Kit 7 http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html
5.	Apache Tomcat http://tomcat.apache.org/download-70.cgi
6.	MySQL [wiki/База данных](https://github.com/devstarter/ii/wiki/%D0%91%D0%B0%D0%B7%D0%B0-%D0%B4%D0%B0%D0%BD%D0%BD%D1%8B%D1%85)

Окрываем проект:

1. Зарегистрируйтесь в GitHub
2. Сделайте Fork (копию) этого кода из своего акаунта
3. Скачайте его на свой компьютер `git clone https://github.com/<ваш акаунт>/ii.git`
4. Откройте проект с помощью IDEA
5. Устанавите плагин [lombok](http://plugins.jetbrains.com/plugin/6317) для IDEA
6. Запустите тест [RunTest.java](https://github.com/devstarter/ii/blob/master/src/test/java/RunTest.java)

Настраиваем базу данных (MySQL) [Видео](https://www.youtube.com/watch?v=l-ZGmR98d-4): 

1. Запускаем базу данных
2. Качаем [последний дамп данных](https://github.com/devstarter/ii/tree/master/db)
3. Импортируем дамп

Запускаем проект:

1. Добавляем Run Configuration для Tomcat в IDEA
2. Запускаем эту конфигурацию

Подробнее в видео https://www.youtube.com/watch?v=mbwN4eaES78
