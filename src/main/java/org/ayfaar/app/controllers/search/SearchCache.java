package org.ayfaar.app.controllers.search;

public interface SearchCache {
    /**
     * Генерация уникального ключа с зависимостью от заданных аргументов.
     * То есть для одинаковых аргументов генерируется одинаковые ключи
     *
     * @param query
     * @param pageNumber
     * @param fromItemNumber
     * @return уникальный ключ
     */
    Object generateKey(String query, Integer pageNumber, String fromItemNumber);

    /**
     * Сообщает о том есть ли сохранённый данные для указаного ключа
     *
     * @param cacheKey результат generateKey
     * @return true если есть
     */
    boolean has(Object cacheKey);

    /**
     * Возвращает сохранённое ранее значение по ключу
     *
     * @param cacheKey результат generateKey
     * @return страница результата поиска
     */
    SearchResultPage get(Object cacheKey);

    /**
     * Сохранение в кеше результат поиска
     *
     * @param cacheKey результат generateKey
     * @param page страница поиска
     */
    void put(Object cacheKey, SearchResultPage page);
}
