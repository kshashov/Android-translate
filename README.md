# Android-translate
Клиент API Яндекс.Переводчика под Android для [Школа мобильной разработки](https://academy.yandex.ru/events/mobdev/msk-2017/)
<img src="https://github.com/envoy93/Android-translate/blob/master/img/1.png" width="250">
###Особенности функционала
* При очистке истории, переводы удаляются и из избранного
* Список доступных языков скачивается при каждом запуске приложения. Вдруг поменялся, а чекнуть, были ли обновления, способа не нашел.
* Перевод осуществляется через 0.5 секунд после ввода, чтобы снизить количество запросов пока юзер набирает текст
###Внешние зависимости
* Интернет - Retrofit
* База данных - Realm
* UI  
    - AutoFitTextView - тектсовое поле для отображения перевода с динамическим размером шрифта 
    - RealmSearchview + RealmSearchAdapter - для списка переводов с поиском
    - MaterialFavoriteButton - кнопка-переключатель избранного
* Прочее
    - RxJava - использовался в связке с Retrofit и для самодельного EventBus
    - ButterKnife
    - DI - Dagger 2
    
###Что хорошо
* Базовый функционал реализован
* Адекватно масштабируется под базовые размеры экранов. Есть горизонтальный лайаут для планшетов
* Адекватно отрабатывает поворот экрана
* Отсутствие интернета и прочие ошибки при добычи данных адекватно отображаются с кнопкой "обновить"

###Что плохо
* Dagger использовал первый раз Как итог - куча синглтонов
* Применил MVP (хорошо) в форме своего велосипеда (плохо). Изначально начал делать свою реализацию чтобы ощутить все подводные камни. Потом хотел заюзать готовую библиотеку. Увы, в итоге время пришлось посвятить магистерсокой диссертации
    - Связка презентер-вью для окна перевода вышла слишком "спагетти"
* Не написал юнит тесты :((( 
* Не прикрутил API словаря для различных вариантов перевода

###Скриншоты
<img src="https://github.com/envoy93/Android-translate/blob/master/img/2.png" width="200">
<img src="https://github.com/envoy93/Android-translate/blob/master/img/3.png" width="200">
<img src="https://github.com/envoy93/Android-translate/blob/master/img/4.png" width="200">
<img src="https://github.com/envoy93/Android-translate/blob/master/img/5.png" width="200">

Планшет

<img src="https://github.com/envoy93/Android-translate/blob/master/img/6.png" height="500">
<img src="https://github.com/envoy93/Android-translate/blob/master/img/7.png" height="500">


