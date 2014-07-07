Интерактивная Ииссиидиология
============================


Цель проекта - поиск, структурирование информации, простраивание взаимосвязей между понятиями, вот ссылка на сам проект [ii.ayfaar.org](http://ii.ayfaar.org)

Задача состоит в том? чтобы из текста хронящегося в [Item](https://github.com/enginer/ii/blob/master/src/main/java/org/ayfaar/app/model/Item.java) (абзаци) сформировать систему термин - описание.

Также нужно искать по всему содержимому - [SearchController](https://github.com/enginer/ii/blob/master/src/main/java/org/ayfaar/app/controllers/SearchController.java). 

Ещё експорт в [mediawiki](http://www.mediawiki.org) (движок википедии) и импорт из [evernote](https://www.evernote.com). 

Уникальность обеспечивается таблицей (сущностью) [UID](https://github.com/enginer/ii/blob/master/src/main/java/org/ayfaar/app/model/UID.java)
То есть любая сущность в системе имеет свой уникальный глобальный идентификатор url. И мы можем линковать эти url как угодно...

 общем [модель хранения данных](https://github.com/enginer/ii/tree/master/src/main/java/org/ayfaar/app/model) не тривиальная, гляньте внимательно...
