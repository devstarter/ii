package org.ayfaar.ii.seo;

import org.apache.commons.lang.NotImplementedException;

import java.util.Map;

/**
 * Created by Drorzz on 04.08.2014.
 */
public class SEOViewFactory {
    private final static SEOViewFactory viewFactory = new SEOViewFactory();

    private SEOViewFactory(){}

    public static SEOViewFactory getInstance(){
        return viewFactory;
    }

    public void setViews(Map<String,SEOView> views){
        throw new NotImplementedException();
    }

    // Получает имя страници и ее параметры, возвращает html который должен быть отправлен клиенту
    public String getHtml(String viewName, Map<String, String> viewParameters) {
        // 1. Проверяем есть ли в кеше
        if(isCached(viewName, viewParameters)){
            // 2. Если да, возвращаем из кеша
            return getCachedView(viewName, viewParameters);
        }

        // 3. Получаем класс отвечающий за отрисовку данной страници
        SEOView view = getView(viewName);
        if(view == null) {
            // 4. Если нету отправляем отображение по умолчанию
            view = getDefaultView();
        }

        // 5. Передаем в отображение параметры
        view.setViewParameters(viewParameters);
        // 6. Получаем html страницу из отображения
        String html = view.getHTML();
        // 7. Кешируем полученый результат
        cachingView(viewName,viewParameters,html);
        return html;
    }

    boolean isCached(String viewName, Map<String, String> viewParameters){
        throw new NotImplementedException();
    }

    String getCachedView(String viewName, Map<String, String> viewParameters){
        throw new NotImplementedException();
    }

    void cachingView(String viewName,Map<String, String> viewParameters,String html){
        throw new NotImplementedException();
    }

    SEOView getView(String viewName){
        throw new NotImplementedException();
    }

    SEOView getDefaultView(){
        throw new NotImplementedException();
    }
}
