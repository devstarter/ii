Интерактивная Ииссиидиология
============================
[ii.ayfaar.org](http://ii.ayfaar.org)

Цели проекта
------------
Основная цель - представление информацци, поиск, структурирование, простраивание взаимосвязей между понятиями [ииссиидиологии](http://ru.science.wikia.com/wiki/%D0%98%D0%B8%D1%81%D1%81%D0%B8%D0%B8%D0%B4%D0%B8%D0%BE%D0%BB%D0%BE%D0%B3%D0%B8%D1%8F)

Косвенная цель - плацдарм для обученя современным техникам и подходам программирования.

О проекте
---------

Это [single-page application](http://en.wikipedia.org/wiki/Single-page_application). Со стороны сервера Java SE, Spring, Hibernate. На клиенте JavaScript, HTML5, CSS3, визуальные компоненты и биндинг - [KendoUI](www.kendoui.com)

Основная задача проекта сформировать связки *термин-описание* из сплошного текста хранящегося по абзацам в базе данных. Плюс поиск по всему содержимому - [SearchController](https://github.com/enginer/ii/blob/master/src/main/java/org/ayfaar/app/controllers/SearchController.java).

Реализован експорт связок *термин-описание* в [mediawiki](http://www.mediawiki.org) (движок википедии) и импорт из [evernote](https://www.evernote.com). 

Проект бесплатно хостится на [OpenShift](https://www.openshift.com/) (Open Hybrid Cloud Application Platform by Red Hat)

С чего начать (Java)
====================

**Видео инструкция https://www.youtube.com/watch?v=mbwN4eaES78**

Устанавливаем:

1.	GIT http://msysgit.github.io
2.	Добавляем git.exe в [переменную окружения Path](http://clip2net.com/s/iuLWXk) и перезагружаем windows
3.	[IntelliJ IDEA](http://www.jetbrains.com/idea/download/) Ultimate Edition
4.	[Java version 1.7](https://www.java.com/en/download)
5.	[Java SE Development Kit 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
6.	[Apache Tomcat](http://tomcat.apache.org/download-70.cgi) или [XAMPP](https://www.apachefriends.org/index.html)
7.	[MySQL](http://dev.mysql.com/downloads/mysql/) или [XAMPP](https://www.apachefriends.org/index.html) [wiki/База данных](https://github.com/devstarter/ii/wiki/%D0%91%D0%B0%D0%B7%D0%B0-%D0%B4%D0%B0%D0%BD%D0%BD%D1%8B%D1%85)

Окрываем проект:

1. Зарегистрируйтесь в [GitHub](https://github.com)
2. Сделайте Fork (копию) [этого кода](https://github.com/devstarter/ii) из своего акаунта
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

