# spring-eureka-broadcast
Позволяет синхронизировать отправку websocket-сообщений между запущеными экземплярами
сервиса используя [spring-cloud-netflix-eureka-client](https://github.com/spring-cloud/spring-cloud-netflix/tree/master/spring-cloud-netflix-eureka-client).

### Использование
1. Добавляем репозиторий [jitpack.io](https://jitpack.io) в свой проект:
    ```groovy
    repositories {
        maven { url "https://jitpack.io" }
    }
    ```

2. Добавляем зависимость (обязательно убедитесь, что используете последнюю версию)
    ```groovy
    compile 'com.github.vastik:spring-boot-starter-eureka-broadcast:1.0.+'
    ```

3. Включаем через свойство **eureka.broadcast.enabled**

### Принцип работы
- Отправитель запрашивает у сервера информацию об экзеплярах и выполняет рассылку на каждый из них.
- Получатель проверяет наличие адреса отправителя в списке экзепляров и выполняет отправку сообщения используя SimpMessagingTemplate 

### Требования
Для инициализации следующие параметры должны быть определены:
- eureka.client.enabled - включает клиента eureka
- eureka.broadcast.enabled - включает поддержку eureka broadcast
- spring.application.name - имя сервиса, используется для получения информации об экземплярах. 
 
Поскольку получатель сам производит валидацию адреса отправителя, адрес по которому будут прозводится запросы
не должен запрашивать авторизацию.  

### Конфигурация
|Параметр|Дескрипшен|Значение|
|--------|----------|--------|
|eureka.broadcast.enabled|Флаг контроллирующий создание бинов авто-конфигурации|false|
|eureka.broadcast.endpoint|Адрес по которому экземпляры будут выполнять рассылку сообщений|/api/eureka/broadcast|    
 