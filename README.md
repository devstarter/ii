Интерактивная Ииссиидиология
============================
[презетация](http://youtu.be/__ibkaMRHZI), [ii.ayfaar.org](http://ii.ayfaar.org), [канал YouTube](https://www.youtube.com/channel/UCx7OZ2t2mEiaW6kem5lfl9w)

Ключевые слова: OOP, [SOLID](http://ru.wikipedia.org/wiki/SOLID_(%D0%BE%D0%B1%D1%8A%D0%B5%D0%BA%D1%82%D0%BD%D0%BE-%D0%BE%D1%80%D0%B8%D0%B5%D0%BD%D1%82%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%BD%D0%BE%D0%B5_%D0%BF%D1%80%D0%BE%D0%B3%D1%80%D0%B0%D0%BC%D0%BC%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D0%B5)), Java 8, J2SE, Hibernate, [Spring Boot](https://spring.io/) (JPA, IoC, MVC), JUnit, JavaScript, HTML5, CSS3, [KendoUI](www.kendoui.com), [AngularJS](https://angularjs.org), MySQL, Maven, git, TDD, CI, IntelliJ IDEA

Мой скайп: iu3116

Цели проекта
------------
Основная цель - представление информацци, поиск, структурирование, простраивание взаимосвязей между понятиями [ииссиидиологии](http://ru.science.wikia.com/wiki/%D0%98%D0%B8%D1%81%D1%81%D0%B8%D0%B8%D0%B4%D0%B8%D0%BE%D0%BB%D0%BE%D0%B3%D0%B8%D1%8F)

Косвенная цель - плацдарм для обученя современным техникам и подходам программирования.

О проекте
---------

Это [single-page application](http://en.wikipedia.org/wiki/Single-page_application). Со стороны сервера Java SE, Spring, Hibernate. На клиенте JavaScript, HTML5, CSS3, визуальные компоненты и биндинг - [KendoUI](www.kendoui.com) и [AngularJS](https://angularjs.org)

Основная задача проекта сформировать связки *термин-описание* из сплошного текста хранящегося по абзацам в базе данных. Плюс поиск по всему содержимому.

Реализован експорт связок *термин-описание* в [mediawiki](http://www.mediawiki.org) (движок википедии) и импорт из [evernote](https://www.evernote.com). 

Проект бесплатно хостится на [OpenShift](https://www.openshift.com/) (Open Hybrid Cloud Application Platform by Red Hat)

[Видео презентация: Структура проекта](https://www.youtube.com/watch?v=Q7GfXEzswcQ&list=UUx7OZ2t2mEiaW6kem5lfl9w)

С чего начать (Java)
====================

Устанавливаем:

1.	GIT http://msysgit.github.io
2.	Добавляем git.exe в переменную окружения Path и перезагружаем windows
3.	Выполняем тестовую задачу [Тренировка работы с git](https://github.com/devstarter/ii/issues/4)
4.	[IntelliJ IDEA](http://www.jetbrains.com/idea/download/)
5.	[Java](https://www.java.com/en/download)
6.	[Java SE Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
7.	[MySQL](http://dev.mysql.com/downloads/mysql/) или [XAMPP](https://www.apachefriends.org/index.html) [wiki/База данных](https://github.com/devstarter/ii/wiki/%D0%91%D0%B0%D0%B7%D0%B0-%D0%B4%D0%B0%D0%BD%D0%BD%D1%8B%D1%85)

Окрываем проект:

1. Зарегистрируйтесь в [GitHub](https://github.com)
2. Сделайте Fork (копию) [этого кода](https://github.com/devstarter/ii) из своего акаунта
3. Скачайте его на свой компьютер `git clone https://github.com/<ваш акаунт>/ii.git`
4. Устанавите плагин [lombok](http://plugins.jetbrains.com/plugin/6317) для IDEA
5. [Открываем проект в IntelliJ IDEA](https://github.com/devstarter/ii/wiki/%D0%9E%D1%82%D0%BA%D1%80%D1%8B%D1%82%D0%B8%D0%B5-%D0%BF%D1%80%D0%BE%D0%B5%D0%BA%D1%82%D0%B0-%D0%B2-IntelliJ-IDEA)

Настраиваем базу данных (MySQL) [Видео](https://www.youtube.com/watch?v=l-ZGmR98d-4): 

1. Запускаем базу данных
2. Качаем [последний дамп данных](https://github.com/devstarter/ii/tree/master/db)
3. Импортируем дамп

Запускаем проект:

Конфигурация в IDEA [Run/Debug Configuration](https://cloud.githubusercontent.com/assets/1183619/14000544/b469080e-f152-11e5-9a3a-c1acb737b5d8.png)


[![Презетация](http://img.youtube.com/vi/__ibkaMRHZI/0.jpg)](http://youtu.be/__ibkaMRHZI)
