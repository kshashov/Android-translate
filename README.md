[![Build Status](https://travis-ci.org/kshashov/Android-translate.svg?branch=master)](https://travis-ci.org/kshashov/Android-translate)
[![codecov](https://codecov.io/gh/kshashov/Android-translate/branch/master/graph/badge.svg)](https://codecov.io/gh/kshashov/Android-translate)
[![MIT licensed](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/envoy93/Android-translate/master/LICENSE)

# Android-translate
Примитивный клиент API Яндекс.Переводчика под Android.

### Внешние зависимости
* Интернет - Retrofit
* База данных - Realm
* Архитектура
    - `Moxy` - реализация MVP. Используется на всех экранах 
    - `Dagger 2` - DI инъекции вспомогательных сущностей (`Realm`, `Otto`), а также моделей в презентерах 
    - `Otto` - взаимодействие между презентерами
    - `Cicerone` - навигация между фрагментами
* UI  
    - `AutoFitTextView` - тектсовое поле для отображения перевода с динамическим размером шрифта 
    - `RealmSearchview` + `RealmSearchAdapter` - для списка переводов с поиском
    - `MaterialFavoriteButton` - кнопка-переключатель избранного
    - `SpinFit` - индикатор загрузки для сплеш-экрана
    - `FadingTextView` - меняющийся текст для отображения загрузки перевода
* Прочее
    - `RxJava` - все асинхронные операции, `Retrofit` вызовы
    - `ButterKnife`
    - `Retrolambda`
* Тесты
    - `Mockito`
* Управление проектом
    - `Travis CI`
    - `Codecov`

### Скриншоты
<img src="https://github.com/envoy93/Android-translate/blob/master/img/1.png" width="200"><img src="https://github.com/envoy93/Android-translate/blob/master/img/2.png" width="200"><img src="https://github.com/envoy93/Android-translate/blob/master/img/3.png" width="200"><img src="https://github.com/envoy93/Android-translate/blob/master/img/4.png" width="200">

Планшет

<img src="https://github.com/envoy93/Android-translate/blob/master/img/5.png" height="500">


